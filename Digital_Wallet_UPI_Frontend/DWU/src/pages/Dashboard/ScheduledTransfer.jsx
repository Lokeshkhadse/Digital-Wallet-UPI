import { useState, useEffect } from 'react';
import { useDispatch } from 'react-redux';
import { useAuth } from '../../hooks/useAuth';
import {
  createScheduledTransfer,
  cancelScheduledTransfer,
} from '../../api/actionApi';
import { getUserBankDetails } from '../../api/queryApi';
import { getAllUsers } from '../../api/authApi';
import { showToast } from '../../features/toast/toastSlice';
import Loader from '../../components/common/Loader';
import Input from '../../components/common/Input';
import Button from '../../components/common/Button';

const ScheduledTransfer = () => {
  const dispatch = useDispatch();
  const { user } = useAuth();

  // ---------- State ----------
  const [loading, setLoading] = useState(false);
  const [loadingUsers, setLoadingUsers] = useState(false);
  const [loadingSenderBanks, setLoadingSenderBanks] = useState(false);
  const [loadingReceiverBanks, setLoadingReceiverBanks] = useState(false);
  const [schedules, setSchedules] = useState([]);
  const [allUsers, setAllUsers] = useState([]);
  const [senderBanks, setSenderBanks] = useState([]);
  const [receiverBanks, setReceiverBanks] = useState([]);

  const [form, setForm] = useState({
    senderUserId: user?.id || '', // kept internally, not displayed
    senderBankId: '',
    receiverUserId: '',
    receiverBankId: '',
    amount: '',
    frequency: 'ONE_TIME',
    nextExecutionTime: '',
    upiPin: '',
    remarks: '',
  });

  // ---------- Fetch sender's banks ----------
  useEffect(() => {
    const fetchSenderBanks = async () => {
      if (!user?.id) return;
      setLoadingSenderBanks(true);
      try {
        const response = await getUserBankDetails(user.id);
        const banks = response.data.data || [];
        setSenderBanks(banks);
        if (banks.length > 0 && !form.senderBankId) {
          setForm((prev) => ({ ...prev, senderBankId: banks[0].id }));
        }
      } catch (error) {
        dispatch(showToast({ message: 'Failed to load your bank accounts', type: 'error' }));
      } finally {
        setLoadingSenderBanks(false);
      }
    };
    fetchSenderBanks();
  }, [user, dispatch, form.senderBankId]);

  // ---------- Fetch all users ----------
  useEffect(() => {
    const fetchUsers = async () => {
      setLoadingUsers(true);
      try {
        const response = await getAllUsers();
        const users = response.data.users || response.data || [];
        setAllUsers(users);
      } catch (error) {
        dispatch(showToast({ message: 'Failed to load users list', type: 'error' }));
      } finally {
        setLoadingUsers(false);
      }
    };
    fetchUsers();
  }, [dispatch]);

  // ---------- Fetch receiver's banks ----------
  useEffect(() => {
    const fetchReceiverBanks = async () => {
      if (!form.receiverUserId) {
        setReceiverBanks([]);
        setForm((prev) => ({ ...prev, receiverBankId: '' }));
        return;
      }
      setLoadingReceiverBanks(true);
      try {
        const response = await getUserBankDetails(form.receiverUserId);
        const banks = response.data.data || [];
        setReceiverBanks(banks);
        if (banks.length > 0) {
          setForm((prev) => ({ ...prev, receiverBankId: banks[0].id }));
        } else {
          setForm((prev) => ({ ...prev, receiverBankId: '' }));
        }
      } catch (error) {
        dispatch(showToast({ message: 'Failed to load receiver banks', type: 'error' }));
        setReceiverBanks([]);
      } finally {
        setLoadingReceiverBanks(false);
      }
    };
    fetchReceiverBanks();
  }, [form.receiverUserId, dispatch]);

  // ---------- Fetch existing schedules (dummy - replace with API if available) ----------
  useEffect(() => {
    // In a real app, you'd have a GET /scheduled-transfer/list endpoint.
    // For now, we keep a placeholder list.
    // You can later replace this with an API call.
    const dummySchedules = [];
    setSchedules(dummySchedules);
  }, []);

  // ---------- Handlers ----------
  const handleChange = (e) => {
    const { name, value } = e.target;
    setForm((prev) => ({ ...prev, [name]: value }));
  };

  const handleReceiverUserChange = (e) => {
    const userId = e.target.value;
    setForm((prev) => ({
      ...prev,
      receiverUserId: userId,
      receiverBankId: '',
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);

    // Validation
    if (!form.senderBankId) {
      dispatch(showToast({ message: 'Please select a sender account', type: 'error' }));
      setLoading(false);
      return;
    }
    if (!form.receiverUserId) {
      dispatch(showToast({ message: 'Please select a receiver', type: 'error' }));
      setLoading(false);
      return;
    }
    if (!form.receiverBankId) {
      dispatch(showToast({ message: 'Receiver has no bank account', type: 'error' }));
      setLoading(false);
      return;
    }
    if (parseFloat(form.amount) <= 0) {
      dispatch(showToast({ message: 'Amount must be greater than zero', type: 'error' }));
      setLoading(false);
      return;
    }
    if (!form.nextExecutionTime) {
      dispatch(showToast({ message: 'Please select execution time', type: 'error' }));
      setLoading(false);
      return;
    }
    if (!form.upiPin || form.upiPin.length !== 6 || !/^\d{6}$/.test(form.upiPin)) {
      dispatch(showToast({ message: 'Please enter a valid 6-digit UPI PIN', type: 'error' }));
      setLoading(false);
      return;
    }

    try {
      const payload = {
        senderUserId: parseInt(form.senderUserId),
        receiverUserId: parseInt(form.receiverUserId),
        senderBankId: parseInt(form.senderBankId),
        receiverBankId: parseInt(form.receiverBankId),
        amount: parseFloat(form.amount),
        frequency: form.frequency,
        nextExecutionTime: form.nextExecutionTime,
        upiPin: form.upiPin.trim(),
        remarks: form.remarks || '',
      };

      const response = await createScheduledTransfer(payload);
      if (response.data.statuscode === 200) {
        dispatch(showToast({ message: 'Scheduled transfer created!', type: 'success' }));
        setForm((prev) => ({
          ...prev,
          receiverUserId: '',
          receiverBankId: '',
          amount: '',
          nextExecutionTime: '',
          upiPin: '',
          remarks: '',
        }));
        setReceiverBanks([]);
        // Refresh schedule list (if API exists, call it)
        // For now, we push to local state
        setSchedules((prev) => [...prev, response.data.data]);
      } else {
        dispatch(showToast({ message: response.data.message || 'Creation failed', type: 'error' }));
      }
    } catch (err) {
      const msg = err.response?.data?.message || 'Creation failed';
      dispatch(showToast({ message: msg, type: 'error' }));
    } finally {
      setLoading(false);
    }
  };

  // ---------- Cancel handler ----------
  const handleCancel = async (scheduleId) => {
    try {
      await cancelScheduledTransfer(scheduleId);
      dispatch(showToast({ message: 'Cancelled successfully', type: 'success' }));
      setSchedules((prev) => prev.filter((s) => s.id !== scheduleId));
    } catch (err) {
      dispatch(showToast({ message: 'Cancel failed', type: 'error' }));
    }
  };

  return (
    <div className="max-w-4xl mx-auto">
      <h2 className="text-2xl font-bold text-white mb-6">Scheduled Transfers</h2>

      {/* Create Form */}
      <div className="bg-white/5 backdrop-blur-sm rounded-xl p-6 border border-white/10 mb-8">
        <h3 className="text-xl font-semibold text-white mb-4">Create New Schedule</h3>
        <form onSubmit={handleSubmit} className="space-y-4">
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            {/* Sender Bank Dropdown */}
            <div>
              <label className="block text-gray-300 text-sm mb-1">From Account</label>
              <select
                name="senderBankId"
                value={form.senderBankId}
                onChange={handleChange}
                className="w-full bg-white/10 border border-white/20 rounded-xl px-4 py-3 text-white focus:ring-2 focus:ring-cyan-400 outline-none"
                required
                disabled={loadingSenderBanks}
              >
                <option value="" className="bg-gray-800 text-white">Select account</option>
                {senderBanks.map((bank) => (
                  <option key={bank.id} value={bank.id} className="bg-gray-800 text-white">
                    {bank.bankName}
                  </option>
                ))}
              </select>
              {loadingSenderBanks && <p className="text-gray-400 text-xs mt-1">Loading accounts...</p>}
            </div>

            {/* Receiver User Dropdown */}
            <div>
              <label className="block text-gray-300 text-sm mb-1">Receiver User</label>
              <select
                name="receiverUserId"
                value={form.receiverUserId}
                onChange={handleReceiverUserChange}
                className="w-full bg-white/10 border border-white/20 rounded-xl px-4 py-3 text-white focus:ring-2 focus:ring-cyan-400 outline-none"
                required
                disabled={loadingUsers}
              >
                <option value="" className="bg-gray-800 text-white">Select user</option>
                {allUsers.map((u) => (
                  <option key={u.id} value={u.id} className="bg-gray-800 text-white">
                    {u.name} ({u.email})
                  </option>
                ))}
              </select>
              {loadingUsers && <p className="text-gray-400 text-xs mt-1">Loading users...</p>}
            </div>

            {/* Receiver Bank Dropdown */}
            <div>
              <label className="block text-gray-300 text-sm mb-1">Receiver Bank Account</label>
              <select
                name="receiverBankId"
                value={form.receiverBankId}
                onChange={handleChange}
                className="w-full bg-white/10 border border-white/20 rounded-xl px-4 py-3 text-white focus:ring-2 focus:ring-cyan-400 outline-none"
                required
                disabled={loadingReceiverBanks || !form.receiverUserId}
              >
                <option value="" className="bg-gray-800 text-white">Select bank</option>
                {receiverBanks.map((bank) => (
                  <option key={bank.id} value={bank.id} className="bg-gray-800 text-white">
                    {bank.bankName} - {bank.accountNumber}
                  </option>
                ))}
              </select>
              {loadingReceiverBanks && <p className="text-gray-400 text-xs mt-1">Loading banks...</p>}
              {!form.receiverUserId && <p className="text-gray-400 text-xs mt-1">Select a receiver first</p>}
              {form.receiverUserId && receiverBanks.length === 0 && !loadingReceiverBanks && (
                <p className="text-yellow-400 text-xs mt-1">This user has no bank accounts</p>
              )}
            </div>

            {/* Amount */}
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

            {/* Frequency */}
            <div>
              <label className="block text-gray-300 text-sm mb-1">Frequency</label>
              <select
                name="frequency"
                value={form.frequency}
                onChange={handleChange}
                className="w-full bg-white/10 border border-white/20 rounded-xl px-4 py-3 text-white focus:ring-2 focus:ring-cyan-400 outline-none"
              >
                <option value="ONE_TIME" className="bg-gray-800 text-white">One Time</option>
                <option value="WEEKLY" className="bg-gray-800 text-white">Weekly</option>
                <option value="MONTHLY" className="bg-gray-800 text-white">Monthly</option>
              </select>
            </div>

            {/* Next Execution Time */}
            <Input
              label="Next Execution Time"
              name="nextExecutionTime"
              type="datetime-local"
              value={form.nextExecutionTime}
              onChange={handleChange}
              required
            />

            {/* UPI PIN */}
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

            {/* Remarks */}
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

      {/* List of schedules */}
      <div>
        <h3 className="text-xl font-semibold text-white mb-4">Your Schedules</h3>
        {schedules.length === 0 ? (
          <p className="text-gray-400">No schedules created yet.</p>
        ) : (
          <div className="space-y-3">
            {schedules.map((sched) => (
              <div
                key={sched.id}
                className="bg-white/5 rounded-xl p-4 border border-white/10 flex justify-between items-center"
              >
                <div>
                  <p className="text-white font-semibold">Amount: ₹{sched.amount}</p>
                  <p className="text-gray-400 text-sm">
                    Frequency: {sched.frequency} | Next: {sched.nextExecutionTime}
                  </p>
                  <p className="text-gray-400 text-sm">Status: {sched.status}</p>
                </div>
                <button
                  onClick={() => handleCancel(sched.id)}
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