import { useEffect, useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { fetchTransactions } from '../../features/transactions/transactionsThunks';
import { useAuth } from '../../hooks/useAuth';
import Loader from '../../components/common/Loader';
import { formatCurrency, formatDateTime } from '../../utils/formatters';

const TransactionHistory = () => {
  const dispatch = useDispatch();
  const { user } = useAuth();
  const { transactions, pagination, isLoading } = useSelector((state) => state.transactions);
  const [page, setPage] = useState(0);
  const [size, setSize] = useState(10);

  useEffect(() => {
    if (user?.id) {
      dispatch(fetchTransactions({ userId: user.id, page, size }));
    }
  }, [dispatch, user, page, size]);

  const handlePrevPage = () => {
    if (page > 0) setPage(page - 1);
  };
  const handleNextPage = () => {
    if (page < pagination.totalPages - 1) setPage(page + 1);
  };
  const handlePageSizeChange = (e) => {
    setSize(Number(e.target.value));
    setPage(0);
  };

  return (
    <div>
      <div className="flex flex-wrap justify-between items-center mb-6">
        <h2 className="text-2xl font-bold text-white">Transaction History</h2>
        <div className="flex items-center gap-3">
          <span className="text-gray-400 text-sm">
            {pagination.totalElements || 0} transactions
          </span>
        </div>
      </div>

      {isLoading ? (
        <Loader />
      ) : (
        <>
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
                      <td className="px-5 py-3 text-white text-xs font-mono">
                        {tx.transactionRefNo}
                      </td>
                      <td className="px-5 py-3 text-gray-300">
                        {tx.transactionType}
                      </td>
                      <td className={`px-5 py-3 font-semibold ${
                        tx.transactionType === 'TRANSFER' ? 'text-cyan-400' :
                        tx.transactionType === 'DEPOSIT' ? 'text-green-400' :
                        tx.transactionType === 'WITHDRAW' ? 'text-orange-400' :
                        'text-white'
                      }`}>
                        {formatCurrency(tx.amount)}
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
                      <td className="px-5 py-3 text-gray-300 text-xs">
                        {formatDateTime(tx.createdAt)}
                      </td>
                    </tr>
                  ))}
                  {transactions.length === 0 && (
                    <tr>
                      <td colSpan="5" className="px-5 py-10 text-center text-gray-400">
                        No transactions found.
                      </td>
                    </tr>
                  )}
                </tbody>
              </table>
            </div>
          </div>

          {/* Pagination */}
          <div className="flex flex-wrap items-center justify-between gap-4 mt-4">
            <div className="flex items-center gap-2">
              <span className="text-gray-400 text-sm">Rows per page:</span>
              <select
                value={size}
                onChange={handlePageSizeChange}
                className="bg-white/10 border border-white/20 rounded-lg px-2 py-1 text-white text-sm focus:ring-2 focus:ring-cyan-400 outline-none"
              >
                <option value="5" className="bg-gray-800 text-white">5</option>
                <option value="10" className="bg-gray-800 text-white">10</option>
                <option value="20" className="bg-gray-800 text-white">20</option>
                <option value="50" className="bg-gray-800 text-white">50</option>
              </select>
            </div>
            <div className="flex items-center gap-3">
              <span className="text-gray-400 text-sm">
                {pagination.totalPages > 0
                  ? `Page ${page + 1} of ${pagination.totalPages}`
                  : 'No pages'}
              </span>
              <button
                onClick={handlePrevPage}
                disabled={page === 0}
                className="text-white/70 hover:text-white disabled:opacity-30 disabled:cursor-not-allowed p-1"
              >
                ◀
              </button>
              <button
                onClick={handleNextPage}
                disabled={page >= pagination.totalPages - 1}
                className="text-white/70 hover:text-white disabled:opacity-30 disabled:cursor-not-allowed p-1"
              >
                ▶
              </button>
            </div>
          </div>
        </>
      )}
    </div>
  );
};

export default TransactionHistory;