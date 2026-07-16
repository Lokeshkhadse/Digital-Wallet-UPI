import { useState, useEffect } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { useNavigate, useLocation } from 'react-router-dom';
import { loginUser, registerUser, fetchUserProfile } from '../features/auth/authThunks';
import { showToast } from '../features/toast/toastSlice';
import Loader from '../components/common/Loader';

// Helper to decode JWT and extract payload
const decodeToken = (token) => {
  try {
    const payload = token.split('.')[1];
    return JSON.parse(atob(payload));
  } catch (e) {
    return null;
  }
};

const Login = () => {
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const location = useLocation();
  const { isLoading, isAuthenticated, user } = useSelector((state) => state.auth);

  // Read query param to set active tab
  const queryParams = new URLSearchParams(location.search);
  const tab = queryParams.get('tab');
  const [isLogin, setIsLogin] = useState(tab !== 'register');

  const [loginData, setLoginData] = useState({ email: '', password: '' });
  const [registerData, setRegisterData] = useState({
    name: '',
    email: '',
    password: '',
    dob: '',
    roles: [],
  });
  const [showLoginPassword, setShowLoginPassword] = useState(false);
  const [showRegisterPassword, setShowRegisterPassword] = useState(false);
  const [loginErrors, setLoginErrors] = useState({ email: '', password: '' });

  // Redirect if already authenticated
  useEffect(() => {
    if (isAuthenticated && user) {
      navigate('/dashboard');
    }
  }, [isAuthenticated, user, navigate]);

  // ---------- Login Handlers ----------
  const handleLoginChange = (e) => {
    const { name, value } = e.target;
    setLoginData({ ...loginData, [name]: value });
    setLoginErrors({ ...loginErrors, [name]: '' });
  };

  const validateLogin = () => {
    const errors = { email: '', password: '' };
    let valid = true;
    if (!loginData.email) {
      errors.email = 'Email is required';
      valid = false;
    } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(loginData.email)) {
      errors.email = 'Invalid email format';
      valid = false;
    }
    if (!loginData.password) {
      errors.password = 'Password is required';
      valid = false;
    } else if (loginData.password.length < 6) {
      errors.password = 'Password must be at least 6 characters';
      valid = false;
    }
    setLoginErrors(errors);
    return valid;
  };

  const handleLoginSubmit = async (e) => {
    e.preventDefault();
    if (!validateLogin()) return;

    try {
      // 1. Login to get token
      const result = await dispatch(loginUser({ email: loginData.email, password: loginData.password })).unwrap();
      // result: { accessToken, refreshToken, tokenType, email }

      // 2. Decode access token to extract userId
      const decoded = decodeToken(result.accessToken);
      let userId = null;
      if (decoded && decoded.id) {
        userId = decoded.id;
      } else {
        // If token doesn't have 'id' claim, fallback: use email to fetch user ID
        // For now, we'll log a warning and continue without userId (dashboard will fail)
        console.warn('Token does not contain "id" claim. Dashboard may not work correctly.');
      }

      // 3. Store tokens and user info in localStorage
      localStorage.setItem('accessToken', result.accessToken);
      localStorage.setItem('refreshToken', result.refreshToken);
      localStorage.setItem('userEmail', result.email);
      if (userId) {
        localStorage.setItem('userId', userId);
      }

      // 4. Optionally fetch full user profile using the userId
      if (userId) {
        try {
          await dispatch(fetchUserProfile(userId)).unwrap();
        } catch (profileErr) {
          // If profile fetch fails, we still have the token, so navigate anyway
          console.warn('Profile fetch failed:', profileErr);
        }
      }

      // 5. Navigate to dashboard
      navigate('/dashboard');
    } catch (err) {
      dispatch(showToast({ message: err || 'Login failed', type: 'error' }));
    }
  };

  // ---------- Register Handlers ----------
  const handleRegisterChange = (e) => {
    const { name, value } = e.target;
    setRegisterData({ ...registerData, [name]: value });
  };

  const handleDobChange = (e) => {
    setRegisterData({ ...registerData, dob: e.target.value });
  };

  const handleRoleChange = (role) => {
    setRegisterData((prev) => {
      const updatedRoles = prev.roles.includes(role)
        ? prev.roles.filter((r) => r !== role)
        : [...prev.roles, role];
      return { ...prev, roles: updatedRoles };
    });
  };

  const validateRegister = () => {
    if (!registerData.name) return false;
    if (!registerData.email) return false;
    if (registerData.password.length < 6) return false;
    if (!registerData.dob) return false;
    if (registerData.roles.length === 0) return false;
    return true;
  };

  const handleRegisterSubmit = async (e) => {
    e.preventDefault();
    if (!validateRegister()) {
      dispatch(showToast({ message: 'Please fill all fields correctly', type: 'error' }));
      return;
    }
    // Convert dob from YYYY-MM-DD to DD-MM-YYYY
    const parts = registerData.dob.split('-');
    const formattedDob = `${parts[2]}-${parts[1]}-${parts[0]}`;
    const payload = {
      ...registerData,
      dob: formattedDob,
    };
    try {
      await dispatch(registerUser(payload)).unwrap();
      dispatch(showToast({ message: 'Registration successful! Please login.', type: 'success' }));
      setIsLogin(true);
      setRegisterData({ name: '', email: '', password: '', dob: '', roles: [] });
      navigate('/login');
    } catch (err) {
      dispatch(showToast({ message: err || 'Registration failed', type: 'error' }));
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center p-4 bg-gradient-to-br from-blue-900 via-indigo-900 to-purple-900 relative overflow-hidden">
      <div className="absolute inset-0 overflow-hidden pointer-events-none">
        <div className="absolute -top-24 -right-24 w-96 h-96 bg-blue-400/20 rounded-full blur-3xl animate-pulse" />
        <div className="absolute top-1/3 -left-32 w-80 h-80 bg-cyan-400/20 rounded-full blur-3xl animate-pulse delay-1000" />
        <div className="absolute bottom-20 right-20 w-72 h-72 bg-purple-400/20 rounded-full blur-3xl animate-pulse delay-2000" />
      </div>

      <div className="w-full max-w-md relative z-10 bg-white/10 backdrop-blur-xl border border-white/20 rounded-3xl shadow-2xl shadow-black/30 p-6 md:p-8 animate-scaleIn">
        <div className="flex bg-white/10 backdrop-blur-sm rounded-2xl p-1.5 mb-8 border border-white/20">
          <button
            className={`flex-1 py-3 rounded-xl text-sm font-semibold transition-all duration-300 ${
              isLogin
                ? 'bg-gradient-to-r from-cyan-500 to-blue-500 text-white shadow-lg shadow-cyan-500/30'
                : 'text-gray-300 hover:text-white hover:bg-white/5'
            }`}
            onClick={() => setIsLogin(true)}
          >
            Login
          </button>
          <button
            className={`flex-1 py-3 rounded-xl text-sm font-semibold transition-all duration-300 ${
              !isLogin
                ? 'bg-gradient-to-r from-emerald-500 to-green-500 text-white shadow-lg shadow-emerald-500/30'
                : 'text-gray-300 hover:text-white hover:bg-white/5'
            }`}
            onClick={() => setIsLogin(false)}
          >
            Sign Up
          </button>
        </div>

        {isLogin ? (
          <form onSubmit={handleLoginSubmit} className="space-y-4">
            <h2 className="text-2xl font-bold text-white text-center">Welcome Back</h2>
            <p className="text-center text-gray-400 text-sm">Sign in to your account</p>
            <div>
              <input
                type="email"
                name="email"
                placeholder="Email address"
                value={loginData.email}
                onChange={handleLoginChange}
                className="w-full bg-white/10 border border-white/20 rounded-xl px-4 py-3 text-white placeholder-gray-400 focus:ring-2 focus:ring-cyan-400 outline-none"
                required
              />
              {loginErrors.email && <p className="text-red-400 text-sm mt-1">{loginErrors.email}</p>}
            </div>
            <div className="relative">
              <input
                type={showLoginPassword ? 'text' : 'password'}
                name="password"
                placeholder="Password"
                value={loginData.password}
                onChange={handleLoginChange}
                className="w-full bg-white/10 border border-white/20 rounded-xl px-4 py-3 text-white placeholder-gray-400 focus:ring-2 focus:ring-cyan-400 outline-none"
                required
              />
              <button
                type="button"
                onClick={() => setShowLoginPassword(!showLoginPassword)}
                className="absolute right-3 top-3 text-gray-400 hover:text-white"
              >
                {showLoginPassword ? '🙈' : '👁️'}
              </button>
              {loginErrors.password && <p className="text-red-400 text-sm mt-1">{loginErrors.password}</p>}
            </div>
            <button
              type="submit"
              disabled={isLoading}
              className="w-full bg-gradient-to-r from-cyan-500 to-blue-500 py-3.5 rounded-xl text-white font-semibold shadow-lg shadow-cyan-500/30 hover:shadow-cyan-500/50 transition-all duration-300 disabled:opacity-70 flex items-center justify-center gap-2"
            >
              {isLoading ? <Loader size="w-5 h-5" /> : 'Login'}
            </button>
          </form>
        ) : (
          <form onSubmit={handleRegisterSubmit} className="space-y-4">
            <h2 className="text-2xl font-bold text-white text-center">Create Account</h2>
            <p className="text-center text-gray-400 text-sm">Join the digital wallet</p>
            <input
              type="text"
              name="name"
              placeholder="Full name"
              value={registerData.name}
              onChange={handleRegisterChange}
              className="w-full bg-white/10 border border-white/20 rounded-xl px-4 py-3 text-white placeholder-gray-400 focus:ring-2 focus:ring-cyan-400 outline-none"
              required
            />
            <input
              type="email"
              name="email"
              placeholder="Email address"
              value={registerData.email}
              onChange={handleRegisterChange}
              className="w-full bg-white/10 border border-white/20 rounded-xl px-4 py-3 text-white placeholder-gray-400 focus:ring-2 focus:ring-cyan-400 outline-none"
              required
            />
            <div className="relative">
              <input
                type={showRegisterPassword ? 'text' : 'password'}
                name="password"
                placeholder="Password (min 6 chars)"
                value={registerData.password}
                onChange={handleRegisterChange}
                className="w-full bg-white/10 border border-white/20 rounded-xl px-4 py-3 text-white placeholder-gray-400 focus:ring-2 focus:ring-cyan-400 outline-none"
                required
              />
              <button
                type="button"
                onClick={() => setShowRegisterPassword(!showRegisterPassword)}
                className="absolute right-3 top-3 text-gray-400 hover:text-white"
              >
                {showRegisterPassword ? '🙈' : '👁️'}
              </button>
            </div>
            <div>
              <input
                type="date"
                name="dob"
                value={registerData.dob}
                onChange={handleDobChange}
                className="w-full bg-white/10 border border-white/20 rounded-xl px-4 py-3 text-white placeholder-gray-400 focus:ring-2 focus:ring-cyan-400 outline-none"
                required
              />
            </div>
            <div className="flex flex-wrap gap-4">
              {['USER', 'ADMIN', 'MANAGER'].map((role) => (
                <label key={role} className="flex items-center gap-2 text-white">
                  <input
                    type="checkbox"
                    checked={registerData.roles.includes(role)}
                    onChange={() => handleRoleChange(role)}
                    className="w-4 h-4 accent-cyan-500"
                  />
                  {role}
                </label>
              ))}
            </div>
            <button
              type="submit"
              disabled={isLoading}
              className="w-full bg-gradient-to-r from-emerald-500 to-green-500 py-3.5 rounded-xl text-white font-semibold shadow-lg shadow-emerald-500/30 hover:shadow-emerald-500/50 transition-all duration-300 disabled:opacity-70 flex items-center justify-center gap-2"
            >
              {isLoading ? <Loader size="w-5 h-5" /> : 'Sign Up'}
            </button>
          </form>
        )}
      </div>
    </div>
  );
};

export default Login;