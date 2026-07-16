import { useState, useEffect } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { useAuth } from '../../hooks/useAuth';
import { createScheduledTransfer, getScheduledTransfer, cancelScheduledTransfer } from '../../api/actionApi';
import { showToast } from '../../features/toast/toastSlice';
import Input from '../../components/common/Input';
import Button from '../../components/common/Button';
import Loader from '../../components/common/Loader';

const ScheduledTransfer = () => {
  const dispatch = useDispatch();
  const { user } = useAuth();
  const [loading, setLoading] = useState(false);
  const [schedules, setSchedules] = useState([]);
  const [form, setForm] = useState({
    senderUserId: user?.id || '',
    receiverUserId: '',
    senderBankId: '',
    receiverBankId: '',
    amount: '',
    frequency: 'ONE_TIME',
    nextExecutionTime: '',
    upiPin: '',
    remarks: '',
  });

  // Fetch schedules (since we don't have a list endpoint, we'll simulate by using get for each ID; we'll use a simple list placeholder)
  // For now, we'll just show a placeholder list. In real scenario, you'd have a GET /scheduled-transfer/all endpoint.
  // We'll just show a create form and a list placeholder.

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    // Validate
    if (!form.receiverUserId || !form.senderBankId || !form.receiverBankId || parseFloat(form.amount) <= 0 || !form.nextExecutionTime || !form.upiPin) {
      dispatch(showToast({ message: 'Please fill all required fields', type: 'error' }));
      setLoading(false);
      return;
    }
    try {
      const payload = {
        ...form,
        senderUserId: parseInt(form.senderUserId),
        receiverUserId: parseInt(form.receiverUserId),
        senderBankId: parseInt(form.senderBankId),
        receiverBankId: parseInt(form.receiverBankId),
        amount: parseFloat(form.amount),
      };
      const response = await createScheduledTransfer(payload);
      if (response.data.statuscode === 200) {
        dispatch(showToast({ message: 'Scheduled transfer created!', type: 'success' }));
        setForm({
          ...form,
          receiverUserId: '',
          receiverBankId: '',
          amount: '',
          nextExecutionTime: '',
          upiPin: '',
          remarks: '',
        });
        // Refresh list (we'll just push to local state)
        setSchedules([...schedules, response.data.data]);
      } else {
        dispatch(showToast({ message: response.data.message || 'Creation failed', type: 'error' }));
      }
    } catch (err) {
      dispatch(showToast({ message: err.response?.data?.message || 'Creation failed', type: 'error' }));
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="max-w-4xl mx-auto">
      <h2 className="text-2xl font-bold text-white mb-6">Scheduled Transfers</h2>

      <div className="bg-white/5 backdrop-blur-sm rounded-xl p-6 border border-white/10 mb-8">
        <h3 className="text-xl font-semibold text-white mb-4">Create New Schedule</h3>
        <form onSubmit={handleSubmit} className="space-y-4">
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <Input
              label="Sender User ID"
              name="senderUserId"
              type="number"
              value={form.senderUserId}
              onChange={handleChange}
              required
            />
            <Input
              label="Receiver User ID"
              name="receiverUserId"
              type="number"
              value={form.receiverUserId}
              onChange={handleChange}
              required
            />
            <Input
              label="Sender Bank ID"
              name="senderBankId"
              type="number"
              value={form.senderBankId}
              onChange={handleChange}
              required
            />
            <Input
              label="Receiver Bank ID"
              name="receiverBankId"
              type="number"
              value={form.receiverBankId}
              onChange={handleChange}
              required
            />
            <Input
              label="Amount (₹)"
              name="amount"
              type="number"
              step="0.01"
              min="1"
              value={form.amount}
              onChange={handleChange}
              required
            />
            <div>
              <label className="block text-gray-300 text-sm mb-1">Frequency</label>
              <select
                name="frequency"
                value={form.frequency}
                onChange={handleChange}
                className="w-full bg-white/10 border border-white/20 rounded-xl px-4 py-3 text-white focus:ring-2 focus:ring-cyan-400 outline-none"
              >
                <option value="ONE_TIME">One Time</option>
                <option value="WEEKLY">Weekly</option>
                <option value="MONTHLY">Monthly</option>
              </select>
            </div>
            <Input
              label="Next Execution Time"
              name="nextExecutionTime"
              type="datetime-local"
              value={form.nextExecutionTime}
              onChange={handleChange}
              required
            />
            <Input
              label="UPI PIN"
              name="upiPin"
              type="password"
              maxLength="6"
              pattern="\d{6}"
              value={form.upiPin}
              onChange={handleChange}
              required
            />
            <Input
              label="Remarks (optional)"
              name="remarks"
              type="text"
              value={form.remarks}
              onChange={handleChange}
            />
          </div>
          <Button type="submit" loading={loading}>Create Schedule</Button>
        </form>
      </div>

      {/* List of schedules - placeholder */}
      <div>
        <h3 className="text-xl font-semibold text-white mb-4">Your Schedules</h3>
        {schedules.length === 0 ? (
          <p className="text-gray-400">No schedules created yet.</p>
        ) : (
          <div className="space-y-3">
            {schedules.map((sched, idx) => (
              <div key={idx} className="bg-white/5 rounded-xl p-4 border border-white/10 flex justify-between items-center">
                <div>
                  <p className="text-white font-semibold">Amount: ₹{sched.amount}</p>
                  <p className="text-gray-400 text-sm">Frequency: {sched.frequency} | Next: {sched.nextExecutionTime}</p>
                  <p className="text-gray-400 text-sm">Status: {sched.status}</p>
                </div>
                <button
                  onClick={async () => {
                    try {
                      await cancelScheduledTransfer(sched.id);
                      dispatch(showToast({ message: 'Cancelled', type: 'success' }));
                      setSchedules(schedules.filter(s => s.id !== sched.id));
                    } catch (err) {
                      dispatch(showToast({ message: 'Cancel failed', type: 'error' }));
                    }
                  }}
                  className="px-4 py-2 bg-red-500/20 text-red-300 rounded-lg hover:bg-red-500/30 transition"
                >
                  Cancel
                </button>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
};

export default ScheduledTransfer;