import { useState } from 'react';
import { useDispatch } from 'react-redux';
import { useNavigate, useLocation } from 'react-router-dom';
import { createBalance } from '../../api/actionApi';
import { showToast } from '../../features/toast/toastSlice';
import { useAuth } from '../../hooks/useAuth';
import Loader from '../../components/common/Loader';

const CreateBalance = () => {
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const location = useLocation();
  const { user } = useAuth();

  // Get bankId and bankName from navigation state
  const { bankId, bankName } = location.state || {};

  const [amount, setAmount] = useState(0);
  const [submitting, setSubmitting] = useState(false);

  // If no bankId, redirect back to accounts page
  if (!bankId) {
    return (
      <div className="text-center text-gray-300 py-16">
        <p className="text-6xl mb-4">⚠️</p>
        <h2 className="text-2xl font-bold text-white">No bank selected</h2>
        <p className="mt-2 text-gray-400">Please create a bank account first.</p>
        <button
          onClick={() => navigate('/dashboard/accounts')}
          className="mt-4 px-6 py-2 bg-cyan-500 text-white rounded-xl hover:bg-cyan-600 transition"
        >
          Go to Bank Accounts
        </button>
      </div>
    );
  }

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (amount < 0) {
      dispatch(showToast({ message: 'Balance cannot be negative', type: 'error' }));
      return;
    }
    setSubmitting(true);
    const userId = user?.id || parseInt(localStorage.getItem('userId'), 10);
    try {
      // Payload matches backend DTO exactly
      await createBalance({
        userId,
        userBankId: bankId,
        availableBalance: parseFloat(amount) || 0,
      });
      dispatch(showToast({ message: 'Balance set successfully! Now create UPI PIN.', type: 'success' }));
      // Navigate to UPI PIN page with bankId
      navigate('/dashboard/upi-pin', { state: { bankId } });
    } catch (err) {
      const msg = err.response?.data?.message || 'Failed to set balance';
      dispatch(showToast({ message: msg, type: 'error' }));
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="max-w-lg mx-auto">
      <h2 className="text-2xl font-bold text-white mb-6">Set Initial Balance</h2>
      <form onSubmit={handleSubmit} className="bg-white/5 backdrop-blur-sm rounded-xl p-6 border border-white/10 space-y-4">
        <div>
          <label className="block text-gray-300 text-sm mb-1">Bank</label>
          <div className="w-full bg-white/10 border border-white/20 rounded-xl px-4 py-3 text-white cursor-not-allowed">
            {bankName || 'Unknown Bank'} (ID: {bankId})
          </div>
        </div>
        <div>
          <label className="block text-gray-300 text-sm mb-1">Initial Balance (₹)</label>
          <input
            type="number"
            step="0.01"
            min="0"
            value={amount}
            onChange={(e) => setAmount(e.target.value)}
            className="w-full bg-white/10 border border-white/20 rounded-xl px-4 py-3 text-white placeholder-gray-400 focus:ring-2 focus:ring-cyan-400 outline-none"
            required
          />
        </div>
        <button
          type="submit"
          disabled={submitting}
          className="w-full bg-gradient-to-r from-cyan-500 to-blue-500 py-3 rounded-xl text-white font-semibold shadow-lg shadow-cyan-500/30 hover:shadow-cyan-500/50 transition disabled:opacity-70 flex items-center justify-center gap-2"
        >
          {submitting ? <Loader size="w-5 h-5" /> : 'Set Balance'}
        </button>
      </form>
    </div>
  );
};

export default CreateBalance;