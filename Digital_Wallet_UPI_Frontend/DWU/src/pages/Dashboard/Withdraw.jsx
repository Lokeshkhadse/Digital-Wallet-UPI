import { useState, useEffect } from 'react';
import { useDispatch } from 'react-redux';
import { useAuth } from '../../hooks/useAuth';
import { withdrawAmount } from '../../api/actionApi';
import { getUserBankDetails } from '../../api/queryApi';
import { showToast } from '../../features/toast/toastSlice';
import Loader from '../../components/common/Loader';

const Withdraw = () => {
  const dispatch = useDispatch();
  const { user } = useAuth();
  const [loading, setLoading] = useState(false);
  const [loadingBanks, setLoadingBanks] = useState(false);
  const [banks, setBanks] = useState([]);
  const [form, setForm] = useState({
    bankId: '',
    amount: '',
    upiPin: '',
    remark: '',
  });

  // Fetch user's bank accounts
  useEffect(() => {
    const fetchBanks = async () => {
      if (!user?.id) return;
      setLoadingBanks(true);
      try {
        const response = await getUserBankDetails(user.id);
        const bankList = response.data.data || [];
        setBanks(bankList);
        if (bankList.length > 0 && !form.bankId) {
          setForm((prev) => ({ ...prev, bankId: bankList[0].id }));
        }
      } catch (error) {
        dispatch(showToast({ message: 'Failed to load bank accounts', type: 'error' }));
      } finally {
        setLoadingBanks(false);
      }
    };
    fetchBanks();
  }, [user, dispatch, form.bankId]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setForm((prev) => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);

    // Validation
    if (!form.bankId) {
      dispatch(showToast({ message: 'Please select a bank account', type: 'error' }));
      setLoading(false);
      return;
    }
    if (parseFloat(form.amount) <= 0) {
      dispatch(showToast({ message: 'Amount must be greater than zero', type: 'error' }));
      setLoading(false);
      return;
    }
    if (!form.upiPin || form.upiPin.length !== 6 || !/^\d{6}$/.test(form.upiPin)) {
      dispatch(showToast({ message: 'Please enter a valid 6-digit UPI PIN', type: 'error' }));
      setLoading(false);
      return;
    }

    const userId = user?.id || parseInt(localStorage.getItem('userId'), 10);
    if (!userId) {
      dispatch(showToast({ message: 'User not authenticated', type: 'error' }));
      setLoading(false);
      return;
    }

    const payload = {
      userId,
      userBankId: parseInt(form.bankId),
      upiPin: form.upiPin.trim(),
      amount: parseFloat(form.amount),
      remarks: form.remark || '',
    };

    try {
      const response = await withdrawAmount(payload);
      if (response.data.statuscode === 200) {
        dispatch(showToast({ message: 'Withdrawal successful!', type: 'success' }));
        setForm({ bankId: form.bankId, amount: '', upiPin: '', remark: '' });
      } else {
        dispatch(showToast({ message: response.data.message || 'Withdrawal failed', type: 'error' }));
      }
    } catch (err) {
      const msg = err.response?.data?.message || 'Withdrawal failed';
      dispatch(showToast({ message: msg, type: 'error' }));
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="max-w-lg mx-auto">
      <h2 className="text-2xl font-bold text-white mb-6">Withdraw Money</h2>
      <form onSubmit={handleSubmit} className="space-y-4">
        {/* Bank Dropdown */}
        <div>
          <label className="block text-gray-300 text-sm mb-1">Select Account</label>
          <select
            name="bankId"
            value={form.bankId}
            onChange={handleChange}
            className="w-full bg-white/10 border border-white/20 rounded-xl px-4 py-3 text-white focus:ring-2 focus:ring-cyan-400 outline-none"
            required
            disabled={loadingBanks}
          >
            <option value="" className="bg-gray-800 text-white">Select bank</option>
            {banks.map((bank) => (
              <option key={bank.id} value={bank.id} className="bg-gray-800 text-white">
                {bank.bankName} 
              </option>
            ))}
          </select>
          {loadingBanks && <p className="text-gray-400 text-xs mt-1">Loading accounts...</p>}
          {!loadingBanks && banks.length === 0 && (
            <p className="text-yellow-400 text-xs mt-1">No bank accounts found. Please add one first.</p>
          )}
        </div>

        {/* Amount */}
        <div>
          <label className="block text-gray-300 text-sm mb-1">Amount (₹)</label>
          <input
            type="number"
            name="amount"
            value={form.amount}
            onChange={handleChange}
            placeholder="0.00"
            className="w-full bg-white/10 border border-white/20 rounded-xl px-4 py-3 text-white placeholder-gray-400 focus:ring-2 focus:ring-cyan-400 outline-none"
            required
            min="1"
            step="0.01"
          />
        </div>

        {/* UPI PIN */}
        <div>
          <label className="block text-gray-300 text-sm mb-1">UPI PIN</label>
          <input
            type="password"
            name="upiPin"
            value={form.upiPin}
            onChange={handleChange}
            placeholder="Enter 6-digit PIN"
            className="w-full bg-white/10 border border-white/20 rounded-xl px-4 py-3 text-white placeholder-gray-400 focus:ring-2 focus:ring-cyan-400 outline-none"
            required
            maxLength="6"
            pattern="\d{6}"
          />
        </div>

        {/* Remark */}
        <div>
          <label className="block text-gray-300 text-sm mb-1">Remark (optional)</label>
          <input
            type="text"
            name="remark"
            value={form.remark}
            onChange={handleChange}
            placeholder="e.g., ATM withdrawal"
            className="w-full bg-white/10 border border-white/20 rounded-xl px-4 py-3 text-white placeholder-gray-400 focus:ring-2 focus:ring-cyan-400 outline-none"
          />
        </div>

        <button
          type="submit"
          disabled={loading}
          className="w-full bg-gradient-to-r from-orange-500 to-red-500 py-3.5 rounded-xl text-white font-semibold shadow-lg shadow-orange-500/30 hover:shadow-orange-500/50 transition-all duration-300 disabled:opacity-70 flex items-center justify-center gap-2"
        >
          {loading ? <Loader size="w-5 h-5" /> : 'Withdraw'}
        </button>
      </form>
    </div>
  );
};

export default Withdraw;