import { useEffect, useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { fetchTransactions } from '../../features/transactions/transactionsThunks';
import { useAuth } from '../../hooks/useAuth';
import Loader from '../../components/common/Loader';

const TransactionHistory = () => {
  const dispatch = useDispatch();
  const { user } = useAuth();
  const { transactions, pagination, isLoading } = useSelector((state) => state.transactions);
  const [page, setPage] = useState(0);
  const size = 10;

  useEffect(() => {
    if (user?.id) {
      dispatch(fetchTransactions({ userId: user.id, page, size }));
    }
  }, [dispatch, user, page]);

  const handlePrevPage = () => {
    if (page > 0) setPage(page - 1);
  };
  const handleNextPage = () => {
    if (page < pagination.totalPages - 1) setPage(page + 1);
  };

  return (
    <div>
      <h2 className="text-2xl font-bold text-white mb-6">Transaction History</h2>
      {isLoading ? (
        <Loader />
      ) : (
        <div className="bg-white/5 backdrop-blur-sm rounded-xl border border-white/10 overflow-hidden">
          <div className="overflow-x-auto">
            <table className="w-full text-left text-sm">
              <thead className="bg-white/10 text-gray-300">
                <tr>
                  <th className="px-5 py-3 font-medium">Ref No</th>
                  <th className="px-5 py-3 font-medium">Type</th>
                  <th className="px-5 py-3 font-medium">Amount</th>
                  <th className="px-5 py-3 font-medium">Status</th>
                  <th className="px-5 py-3 font-medium">Date</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-white/5">
                {transactions.map((tx) => (
                  <tr key={tx.transactionId} className="hover:bg-white/5 transition-colors">
                    <td className="px-5 py-3 text-white text-xs">{tx.transactionRefNo}</td>
                    <td className="px-5 py-3 text-gray-300">{tx.transactionType}</td>
                    <td className={`px-5 py-3 font-semibold ${tx.transactionType === 'TRANSFER' ? 'text-cyan-400' : 'text-green-400'}`}>
                      ₹{tx.amount}
                    </td>
                    <td className="px-5 py-3">
                      <span className={`px-2 py-1 rounded-full text-xs font-medium ${
                        tx.transactionStatus === 'SUCCESS' ? 'bg-green-500/20 text-green-300' :
                        tx.transactionStatus === 'PENDING' ? 'bg-yellow-500/20 text-yellow-300' :
                        'bg-red-500/20 text-red-300'
                      }`}>
                        {tx.transactionStatus}
                      </span>
                    </td>
                    <td className="px-5 py-3 text-gray-300">{new Date(tx.createdAt).toLocaleString()}</td>
                  </tr>
                ))}
                {transactions.length === 0 && (
                  <tr><td colSpan="5" className="px-5 py-10 text-center text-gray-400">No transactions found.</td></tr>
                )}
              </tbody>
            </table>
          </div>
          {/* Pagination */}
          {pagination.totalPages > 1 && (
            <div className="flex justify-between items-center px-5 py-3 border-t border-white/10">
              <button
                onClick={handlePrevPage}
                disabled={page === 0}
                className="text-white/70 hover:text-white disabled:opacity-30"
              >
                Previous
              </button>
              <span className="text-gray-400 text-sm">
                Page {page + 1} of {pagination.totalPages}
              </span>
              <button
                onClick={handleNextPage}
                disabled={page >= pagination.totalPages - 1}
                className="text-white/70 hover:text-white disabled:opacity-30"
              >
                Next
              </button>
            </div>
          )}
        </div>
      )}
    </div>
  );
};

export default TransactionHistory;