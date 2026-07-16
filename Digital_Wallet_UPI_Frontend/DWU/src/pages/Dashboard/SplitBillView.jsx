import { useEffect, useState } from 'react';
import { useDispatch } from 'react-redux';
import { useAuth } from '../../hooks/useAuth';
import { getCreatedSplitBills, getPendingSplitBills, getSplitBill } from '../../api/queryApi';
import { showToast } from '../../features/toast/toastSlice';
import Loader from '../../components/common/Loader';
import { formatCurrency, formatDateTime } from '../../utils/formatters';

const SplitBillView = () => {
  const dispatch = useDispatch();
  const { user } = useAuth();
  const [loading, setLoading] = useState(false);
  const [createdBills, setCreatedBills] = useState([]);
  const [pendingBills, setPendingBills] = useState([]);

  useEffect(() => {
    if (user?.id) {
      fetchBills();
    }
  }, [user]);

  const fetchBills = async () => {
    setLoading(true);
    try {
      const [createdRes, pendingRes] = await Promise.all([
        getCreatedSplitBills(user.id),
        getPendingSplitBills(user.id),
      ]);
      setCreatedBills(createdRes.data.data || []);
      setPendingBills(pendingRes.data.data || []);
    } catch (err) {
      dispatch(showToast({ message: 'Failed to load bills', type: 'error' }));
    } finally {
      setLoading(false);
    }
  };

  const BillCard = ({ bill }) => (
    <div className="bg-white/5 rounded-xl p-4 border border-white/10">
      <h4 className="text-white font-semibold">{bill.title}</h4>
      <p className="text-gray-400 text-sm">{bill.description}</p>
      <p className="text-cyan-400 font-bold mt-1">{formatCurrency(bill.totalAmount)}</p>
      <p className="text-gray-400 text-sm">Status: {bill.status}</p>
      <p className="text-gray-400 text-sm">Due: {bill.dueDate ? formatDateTime(bill.dueDate) : 'N/A'}</p>
      <p className="text-gray-400 text-sm">Created: {formatDateTime(bill.createdAt)}</p>
      {bill.participants && (
        <div className="mt-2">
          <p className="text-gray-300 text-sm font-semibold">Participants:</p>
          {bill.participants.map((p, idx) => (
            <div key={idx} className="flex justify-between text-sm text-gray-300">
              <span>User {p.userId}</span>
              <span>{formatCurrency(p.shareAmount)} - {p.paymentStatus}</span>
            </div>
          ))}
        </div>
      )}
    </div>
  );

  if (loading) return <Loader />;

  return (
    <div className="max-w-4xl mx-auto">
      <h2 className="text-2xl font-bold text-white mb-6">Split Bills</h2>

      <div className="mb-8">
        <h3 className="text-xl font-semibold text-white mb-4">Created by You</h3>
        {createdBills.length === 0 ? (
          <p className="text-gray-400">No bills created.</p>
        ) : (
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            {createdBills.map((bill) => (
              <BillCard key={bill.splitBillId} bill={bill} />
            ))}
          </div>
        )}
      </div>

      <div>
        <h3 className="text-xl font-semibold text-white mb-4">Pending for You</h3>
        {pendingBills.length === 0 ? (
          <p className="text-gray-400">No pending bills.</p>
        ) : (
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            {pendingBills.map((bill) => (
              <BillCard key={bill.splitBillId} bill={bill} />
            ))}
          </div>
        )}
      </div>
    </div>
  );
};

export default SplitBillView;