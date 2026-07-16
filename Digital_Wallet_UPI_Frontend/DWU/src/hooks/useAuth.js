import { useSelector, useDispatch } from 'react-redux';
import { logout, setToken } from '../features/auth/authSlice';
import { showToast } from '../features/toast/toastSlice';

export const useAuth = () => {
  const dispatch = useDispatch();
  const { user, token, isAuthenticated, isLoading } = useSelector((state) => state.auth);

  const handleLogout = () => {
    dispatch(logout());
    dispatch(showToast({ message: 'Logged out successfully', type: 'info' }));
  };

  return {
    user,
    token,
    isAuthenticated,
    isLoading,
    logout: handleLogout,
    setToken: (token) => dispatch(setToken(token)),
  };
};