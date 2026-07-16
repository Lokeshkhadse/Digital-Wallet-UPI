import { useState, useEffect } from 'react';
import { useDispatch } from 'react-redux';
import { useNavigate, useLocation } from 'react-router-dom';
import { createUpiPin, changeUpiPin } from '../../api/actionApi';
import { getUserBankDetails } from '../../api/queryApi';
import { showToast } from '../../features/toast/toastSlice';
import { useAuth } from '../../hooks/useAuth';
import Input from '../../components/common/Input';
import Button from '../../components/common/Button';
import Loader from '../../components/common/Loader';

const UpiPinManagement = () => {
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const location = useLocation();
  const { user } = useAuth();

  // Get userId from auth or localStorage fallback
  const userId = user?.id || parseInt(localStorage.getItem('userId'), 10);

  // If bankId is passed via state (from CreateBalance), pre-select it
  const preSelectedBankId = location.state?.bankId;

  const [mode, setMode] = useState('create'); // 'create' or 'change'
  const [loading, setLoading] = useState(false);
  const [loadingBanks, setLoadingBanks] = useState(false);
  const [banks, setBanks] = useState([]);
  const [form, setForm] = useState({
    userId: userId || '',
    userBankId: preSelectedBankId || '',
    pin: '',
    oldPin: '',
    newPin: '',
  });

  // Fetch user's bank accounts
  useEffect(() => {
    if (userId) {
      fetchBanks();
    }
  }, [userId]);

  const fetchBanks = async () => {
    setLoadingBanks(true);
    try {
      const response = await getUserBankDetails(userId);
      const bankList = response.data.data || [];
      setBanks(bankList);
      // If no bankId is pre-selected and we have banks, select the first one
      if (!form.userBankId && bankList.length > 0) {
        setForm(prev => ({ ...prev, userBankId: bankList[0].id }));
      }
    } catch (err) {
      dispatch(showToast({ message: 'Failed to load bank accounts', type: 'error' }));
    } finally {
      setLoadingBanks(false);
    }
  };

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleBankChange = (e) => {
    setForm({ ...form, userBankId: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!form.userId || !form.userBankId) {
      dispatch(showToast({ message: 'Please select a bank account', type: 'error' }));
      return;
    }
    setLoading(true);
    try {
      let response;
      if (mode === 'create') {
        response = await createUpiPin({
          userId: parseInt(form.userId),
          userBankId: parseInt(form.userBankId),
          pin: form.pin,
        });
      } else {
        response = await changeUpiPin({
          userId: parseInt(form.userId),
          userBankId: parseInt(form.userBankId),
          oldPin: form.oldPin,
          newPin: form.newPin,
        });
      }
      if (response.status === 200 || response.status === 201) {
        dispatch(showToast({ 
          message: `UPI PIN ${mode === 'create' ? 'created' : 'changed'} successfully!`, 
          type: 'success' 
        }));
        // Reset form
        setForm({ ...form, pin: '', oldPin: '', newPin: '' });
        // Navigate to dashboard after success
        navigate('/dashboard');
      } else {
        dispatch(showToast({ message: response.data || 'Operation failed', type: 'error' }));
      }
    } catch (err) {
      dispatch(showToast({ message: err.response?.data?.message || 'Operation failed', type: 'error' }));
    } finally {
      setLoading(false);
    }
  };

  if (loadingBanks) {
    return <Loader />;
  }

  if (banks.length === 0) {
    return (
      <div className="text-center text-gray-300 py-16">
        <p className="text-6xl mb-4">🏦</p>
        <h2 className="text-2xl font-bold text-white">No bank accounts linked</h2>
        <p className="mt-2 text-gray-400">Please add a bank account first.</p>
        <button
          onClick={() => navigate('/dashboard/accounts')}
          className="mt-4 px-6 py-2 bg-cyan-500 text-white rounded-xl hover:bg-cyan-600 transition"
        >
          Add Bank Account
        </button>
      </div>
    );
  }

  return (
    <div className="max-w-lg mx-auto">
      <h2 className="text-2xl font-bold text-white mb-6">UPI PIN Management</h2>
      <div className="flex gap-4 mb-6">
        <button
          onClick={() => setMode('create')}
          className={`px-4 py-2 rounded-xl ${
            mode === 'create' ? 'bg-cyan-500 text-white' : 'bg-white/10 text-gray-300'
          }`}
        >
          Create PIN
        </button>
        <button
          onClick={() => setMode('change')}
          className={`px-4 py-2 rounded-xl ${
            mode === 'change' ? 'bg-cyan-500 text-white' : 'bg-white/10 text-gray-300'
          }`}
        >
          Change PIN
        </button>
      </div>

      <form onSubmit={handleSubmit} className="bg-white/5 backdrop-blur-sm rounded-xl p-6 border border-white/10 space-y-4">
        {/* User ID - read-only */}
        <div>
          <label className="block text-gray-300 text-sm mb-1">User ID</label>
          <input
            type="number"
            name="userId"
            value={form.userId}
            readOnly
            className="w-full bg-white/10 border border-white/20 rounded-xl px-4 py-3 text-white/70 cursor-not-allowed"
          />
        </div>

        {/* Bank dropdown */}
        <div>
          <label className="block text-gray-300 text-sm mb-1">Select Bank</label>
          <select
            name="userBankId"
            value={form.userBankId}
            onChange={handleBankChange}
            className="w-full bg-white/10 border border-white/20 rounded-xl px-4 py-3 text-white focus:ring-2 focus:ring-cyan-400 outline-none"
            required
          >
            <option value="" className="bg-gray-800 text-white">Select Bank</option>
            {banks.map((bank) => (
              <option key={bank.id} value={bank.id} className="bg-gray-800 text-white">
                {bank.bankName} - {bank.upiId || bank.accountNumber}
              </option>
            ))}
          </select>
        </div>

        {mode === 'create' ? (
          <Input
            label="New PIN (6 digits)"
            name="pin"
            type="password"
            maxLength="6"
            pattern="\d{6}"
            value={form.pin}
            onChange={handleChange}
            required
          />
        ) : (
          <>
            <Input
              label="Old PIN"
              name="oldPin"
              type="password"
              maxLength="6"
              pattern="\d{6}"
              value={form.oldPin}
              onChange={handleChange}
              required
            />
            <Input
              label="New PIN"
              name="newPin"
              type="password"
              maxLength="6"
              pattern="\d{6}"
              value={form.newPin}
              onChange={handleChange}
              required
            />
          </>
        )}

        <Button type="submit" loading={loading}>
          {mode === 'create' ? 'Create PIN' : 'Change PIN'}
        </Button>
      </form>
    </div>
  );
};

export default UpiPinManagement;