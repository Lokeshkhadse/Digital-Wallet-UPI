import { useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { useAuth } from '../../hooks/useAuth';
import { updatePassword } from '../../api/authApi'; // we'll add this to authApi
import { showToast } from '../../features/toast/toastSlice';
import Input from '../../components/common/Input';
import Button from '../../components/common/Button';
import Loader from '../../components/common/Loader';

const Profile = () => {
  const dispatch = useDispatch();
  const { user, logout } = useAuth();
  const { isLoading } = useSelector((state) => state.auth);
  const [passwordForm, setPasswordForm] = useState({
    newPassword: '',
    confirmPassword: '',
  });
  const [changingPassword, setChangingPassword] = useState(false);

  const handlePasswordChange = (e) => {
    setPasswordForm({ ...passwordForm, [e.target.name]: e.target.value });
  };

  const handleUpdatePassword = async (e) => {
    e.preventDefault();
    if (passwordForm.newPassword !== passwordForm.confirmPassword) {
      dispatch(showToast({ message: 'Passwords do not match', type: 'error' }));
      return;
    }
    if (passwordForm.newPassword.length < 6) {
      dispatch(showToast({ message: 'Password must be at least 6 characters', type: 'error' }));
      return;
    }
    setChangingPassword(true);
    try {
      await updatePassword(user.email, passwordForm.newPassword);
      dispatch(showToast({ message: 'Password updated successfully. Please login again.', type: 'success' }));
      setPasswordForm({ newPassword: '', confirmPassword: '' });
      // Optional: logout or redirect to login
      logout();
    } catch (err) {
      dispatch(showToast({ message: err.response?.data?.message || 'Password update failed', type: 'error' }));
    } finally {
      setChangingPassword(false);
    }
  };

  if (isLoading) return <Loader />;

  return (
    <div className="max-w-lg mx-auto">
      <h2 className="text-2xl font-bold text-white mb-6">Profile</h2>
      <div className="bg-white/10 backdrop-blur-sm rounded-xl p-6 border border-white/10 mb-6">
        <div className="flex items-center gap-4 mb-4">
          <div className="w-16 h-16 rounded-full bg-gradient-to-r from-cyan-500 to-blue-500 flex items-center justify-center text-2xl font-bold text-white">
            {user?.name?.charAt(0) || 'U'}
          </div>
          <div>
            <p className="text-white font-semibold text-xl">{user?.name}</p>
            <p className="text-gray-400">{user?.email}</p>
            <p className="text-gray-400 text-sm">Roles: {user?.roles}</p>
          </div>
        </div>
        <div className="space-y-2 text-gray-300">
          <div className="flex justify-between py-2 border-b border-white/5">
            <span>User ID</span>
            <span className="font-semibold text-white">{user?.id}</span>
          </div>
          <div className="flex justify-between py-2">
            <span>DOB</span>
            <span className="font-semibold text-white">{user?.dob || '-'}</span>
          </div>
        </div>
      </div>

      <div className="bg-white/5 backdrop-blur-sm rounded-xl p-6 border border-white/10">
        <h3 className="text-xl font-semibold text-white mb-4">Change Password</h3>
        <form onSubmit={handleUpdatePassword} className="space-y-4">
          <Input
            label="New Password"
            name="newPassword"
            type="password"
            value={passwordForm.newPassword}
            onChange={handlePasswordChange}
            required
          />
          <Input
            label="Confirm Password"
            name="confirmPassword"
            type="password"
            value={passwordForm.confirmPassword}
            onChange={handlePasswordChange}
            required
          />
          <Button type="submit" loading={changingPassword}>Update Password</Button>
        </form>
      </div>
    </div>
  );
};

export default Profile;