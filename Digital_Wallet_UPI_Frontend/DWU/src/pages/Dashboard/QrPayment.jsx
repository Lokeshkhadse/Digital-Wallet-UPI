import { useState } from 'react';
import { useDispatch } from 'react-redux';
import { qrPay } from '../../api/actionApi';
import { showToast } from '../../features/toast/toastSlice';
import { useAuth } from '../../hooks/useAuth';
import Loader from '../../components/common/Loader';

const QrPayment = () => {
  const dispatch = useDispatch();
  const { user } = useAuth();
  const [loading, setLoading] = useState(false);
  const [form, setForm] = useState({
    senderBankId: '',
    upiId: '',
    amount: '',
    upiPin: '',
    remarks: '',
  });

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    if (parseFloat(form.amount) <= 0) {
      dispatch(showToast({ message: 'Amount must be > 0', type: 'error' }));
      setLoading(false);
      return;
    }
    const payload = {
      senderUserId: user.id,
      senderBankId: parseInt(form.senderBankId),
      upiId: form.upiId,
      amount: parseFloat(form.amount),
      upiPin: form.upiPin,
      remarks: form.remarks,
    };
    try {
      const response = await qrPay(payload);
      if (response.data.statuscode === 200) {
        dispatch(showToast({ message: 'QR Payment successful!', type: 'success' }));
        setForm({ senderBankId: '', upiId: '', amount: '', upiPin: '', remarks: '' });
      } else {
        dispatch(showToast({ message: response.data.message || 'Payment failed', type: 'error' }));
      }
    } catch (err) {
      dispatch(showToast({ message: err.response?.data?.message || 'Payment failed', type: 'error' }));
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="max-w-lg mx-auto">
      <h2 className="text-2xl font-bold text-white mb-6">QR / UPI Payment</h2>
      <form onSubmit={handleSubmit} className="space-y-4">
        <div>
          <label className="block text-gray-300 text-sm mb-1">Sender Account (Bank ID)</label>
          <input
            type="number"
            name="senderBankId"
            value={form.senderBankId}
            onChange={handleChange}
            placeholder="Enter Bank ID"
            className="w-full bg-white/10 border border-white/20 rounded-xl px-4 py-3 text-white placeholder-gray-400 focus:ring-2 focus:ring-cyan-400 outline-none"
            required
          />
        </div>
        <div>
          <label className="block text-gray-300 text-sm mb-1">Recipient UPI ID</label>
          <input
            type="text"
            name="upiId"
            value={form.upiId}
            onChange={handleChange}
            placeholder="e.g. merchant@pay"
            className="w-full bg-white/10 border border-white/20 rounded-xl px-4 py-3 text-white placeholder-gray-400 focus:ring-2 focus:ring-cyan-400 outline-none"
            required
          />
        </div>
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
        <div>
          <label className="block text-gray-300 text-sm mb-1">UPI PIN</label>
          <input
            type="password"
            name="upiPin"
            value={form.upiPin}
            onChange={handleChange}
            placeholder="6-digit PIN"
            className="w-full bg-white/10 border border-white/20 rounded-xl px-4 py-3 text-white placeholder-gray-400 focus:ring-2 focus:ring-cyan-400 outline-none"
            required
            maxLength="6"
            pattern="\d{6}"
          />
        </div>
        <div>
          <label className="block text-gray-300 text-sm mb-1">Remarks</label>
          <input
            type="text"
            name="remarks"
            value={form.remarks}
            onChange={handleChange}
            placeholder="Optional"
            className="w-full bg-white/10 border border-white/20 rounded-xl px-4 py-3 text-white placeholder-gray-400 focus:ring-2 focus:ring-cyan-400 outline-none"
          />
        </div>
        <button
          type="submit"
          disabled={loading}
          className="w-full bg-gradient-to-r from-purple-500 to-pink-500 py-3.5 rounded-xl text-white font-semibold shadow-lg shadow-purple-500/30 hover:shadow-purple-500/50 transition-all duration-300 disabled:opacity-70 flex items-center justify-center gap-2"
        >
          {loading ? <Loader size="w-5 h-5" /> : 'Pay via QR'}
        </button>
      </form>
    </div>
  );
};

export default QrPayment;