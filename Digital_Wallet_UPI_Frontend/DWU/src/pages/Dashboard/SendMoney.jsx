import { useState, useEffect } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { useAuth } from '../../hooks/useAuth';
import { transferAmount } from '../../api/actionApi';
import { getUserBankDetails } from '../../api/queryApi';
import { getAllUsers } from '../../api/authApi';
import { showToast } from '../../features/toast/toastSlice';
import Loader from '../../components/common/Loader';

const SendMoney = () => {
  const dispatch = useDispatch();
  const { user } = useAuth();
  const { userDashboard } = useSelector((state) => state.dashboard);

  // State
  const [loading, setLoading] = useState(false);
  const [loadingUsers, setLoadingUsers] = useState(false);
  const [loadingReceiverBanks, setLoadingReceiverBanks] = useState(false);
  const [loadingSenderBanks, setLoadingSenderBanks] = useState(false);
  const [allUsers, setAllUsers] = useState([]);
  const [receiverBanks, setReceiverBanks] = useState([]);
  const [senderBanks, setSenderBanks] = useState([]);
  const [isUserReady, setIsUserReady] = useState(false);

  const [form, setForm] = useState({
    senderUserId: user?.id || '',
    senderBankId: '',
    receiverUserId: '',
    receiverBankId: '',
    amount: '',
    upiPin: '',
    remark: '',
  });

  // Wait for user to be loaded
  useEffect(() => {
    if (user?.id) {
      setIsUserReady(true);
      setForm((prev) => ({ ...prev, senderUserId: user.id }));
    }
  }, [user]);

  // 1. Fetch sender's banks
  useEffect(() => {
    if (!isUserReady) return;

    const fetchSenderBanks = async () => {
      if (userDashboard?.linkedAccounts?.length > 0) {
        setSenderBanks(userDashboard.linkedAccounts);
        if (!form.senderBankId) {
          setForm((prev) => ({ ...prev, senderBankId: userDashboard.linkedAccounts[0].bankId }));
        }
        return;
      }

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
  }, [isUserReady, user, userDashboard, dispatch, form.senderBankId]);

  // 2. Fetch all users
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

  // 3. Fetch receiver banks
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

  // Handlers
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

    // Validate
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
    if (!form.upiPin || form.upiPin.length !== 6 || !/^\d{6}$/.test(form.upiPin)) {
      dispatch(showToast({ message: 'Please enter a valid 6-digit UPI PIN', type: 'error' }));
      setLoading(false);
      return;
    }

    try {
      // Build payload with explicit number conversion
      const payload = {
        senderUserId: Number(form.senderUserId),
        senderBankId: Number(form.senderBankId),
        receiverUserId: Number(form.receiverUserId),
        receiverBankId: Number(form.receiverBankId),
        upiPin: form.upiPin.trim(),
        amount: Number(parseFloat(form.amount).toFixed(2)),
        transactionType: 'TRANSFER',
        remarks: form.remark?.trim() || '',
      };

      // 🔍 Debug: log the payload
      console.log('📤 Transfer Payload:', JSON.stringify(payload, null, 2));

      const response = await transferAmount(payload);
      if (response.data.statuscode === 200) {
        dispatch(showToast({ message: 'Transfer successful!', type: 'success' }));
        setForm({
          ...form,
          receiverUserId: '',
          receiverBankId: '',
          amount: '',
          upiPin: '',
          remark: '',
        });
        setReceiverBanks([]);
      } else {
        dispatch(showToast({ message: response.data.message || 'Transfer failed', type: 'error' }));
      }
    } catch (error) {
      console.error('❌ Transfer Error:', error);
      const msg = error.response?.data?.message || error.message || 'Transfer failed';
      dispatch(showToast({ message: msg, type: 'error' }));
    } finally {
      setLoading(false);
    }
  };

  // Show loader while user is not ready
  if (!isUserReady) {
    return (
      <div className="flex justify-center items-center h-64">
        <Loader size="w-8 h-8" />
      </div>
    );
  }

  return (
    <div className="max-w-lg mx-auto">
      <h2 className="text-2xl font-bold text-white mb-6">Send Money</h2>
      <form onSubmit={handleSubmit} className="space-y-4">
        {/* From Account */}
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
            {senderBanks.map((acc) => (
              <option key={acc.bankId || acc.id} value={acc.bankId || acc.id} className="bg-gray-800 text-white">
                {acc.bankName}
              </option>
            ))}
          </select>
          {loadingSenderBanks && <p className="text-gray-400 text-xs mt-1">Loading your accounts...</p>}
        </div>

        {/* Receiver User */}
        <div>
          <label className="block text-gray-300 text-sm mb-1">Select Receiver</label>
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

        {/* Receiver Bank */}
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
          {!form.receiverUserId && <p className="text-gray-400 text-xs mt-1">Please select a receiver first</p>}
          {form.receiverUserId && receiverBanks.length === 0 && !loadingReceiverBanks && (
            <p className="text-yellow-400 text-xs mt-1">This user has no bank accounts</p>
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
            placeholder="For: Lunch"
            className="w-full bg-white/10 border border-white/20 rounded-xl px-4 py-3 text-white placeholder-gray-400 focus:ring-2 focus:ring-cyan-400 outline-none"
          />
        </div>

        <button
          type="submit"
          disabled={loading}
          className="w-full bg-gradient-to-r from-cyan-500 to-blue-500 py-3.5 rounded-xl text-white font-semibold shadow-lg shadow-cyan-500/30 hover:shadow-cyan-500/50 transition-all duration-300 disabled:opacity-70 flex items-center justify-center gap-2"
        >
          {loading ? <Loader size="w-5 h-5" /> : 'Send Money'}
        </button>
      </form>
    </div>
  );
};

export default SendMoney;