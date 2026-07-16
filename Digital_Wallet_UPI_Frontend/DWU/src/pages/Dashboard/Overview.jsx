import { useEffect, useMemo } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { Link } from 'react-router-dom';
import { fetchDashboard } from '../../features/dashboard/dashboardThunks';
import { fetchMiniStatement } from '../../features/transactions/transactionsThunks';
import { useAuth } from '../../hooks/useAuth';
import Loader from '../../components/common/Loader';
import { formatCurrency, formatDate, formatShortDate, getLast7Days } from '../../utils/formatters';

const Overview = () => {
  const dispatch = useDispatch();
  const { user } = useAuth();
  const { userDashboard, isLoading } = useSelector((state) => state.dashboard);
  const { miniStatement } = useSelector((state) => state.transactions);

  // Get userId: from Redux user if available, otherwise from localStorage
  const userId = useMemo(() => {
    if (user?.id) return user.id;
    const storedId = localStorage.getItem('userId');
    if (storedId) return parseInt(storedId, 10);
    return null;
  }, [user]);

  useEffect(() => {
    if (userId) {
      dispatch(fetchDashboard(userId));
      dispatch(fetchMiniStatement(userId));
    }
  }, [dispatch, userId]);

  // ---------- Real stats ----------
  const stats = useMemo(() => {
    if (!miniStatement || miniStatement.length === 0) {
      return { totalSent: 0, totalReceived: 0, totalTx: 0, avgSent: 0, avgReceived: 0 };
    }
    let sent = 0,
      received = 0,
      sentCount = 0,
      receivedCount = 0;
    miniStatement.forEach((tx) => {
      if (tx.transactionType === 'TRANSFER' || tx.transactionType === 'SENT') {
        sent += tx.amount;
        sentCount++;
      } else {
        received += tx.amount;
        receivedCount++;
      }
    });
    return {
      totalSent: sent,
      totalReceived: received,
      totalTx: miniStatement.length,
      avgSent: sentCount > 0 ? sent / sentCount : 0,
      avgReceived: receivedCount > 0 ? received / receivedCount : 0,
    };
  }, [miniStatement]);

  // ---------- Weekly chart data (real) ----------
  const chartData = useMemo(() => {
    const last7Days = getLast7Days();
    const dayTotals = last7Days.map((day) => {
      const dayStr = day.toISOString().split('T')[0];
      let total = 0;
      miniStatement?.forEach((tx) => {
        const txDate = new Date(tx.createdAt).toISOString().split('T')[0];
        if (txDate === dayStr && (tx.transactionType === 'TRANSFER' || tx.transactionType === 'SENT')) {
          total += tx.amount;
        }
      });
      return total;
    });
    const max = Math.max(...dayTotals, 1);
    return { days: last7Days.map((d) => formatShortDate(d.toISOString())), values: dayTotals, max };
  }, [miniStatement]);

  // ---------- Donut chart data (real) ----------
  const donutData = useMemo(() => {
    const sent = stats.totalSent;
    const received = stats.totalReceived;
    const total = sent + received || 1;
    return {
      sent,
      received,
      sentPercent: (sent / total) * 100,
      receivedPercent: (received / total) * 100,
    };
  }, [stats]);

  // ---------- Loading & auth checks ----------
  if (isLoading) return <Loader />;

  if (!userId) {
    return (
      <div className="text-center text-gray-300 py-16">
        <p className="text-6xl mb-4">🔑</p>
        <h2 className="text-2xl font-bold text-white">Session expired</h2>
        <p className="mt-2 text-gray-400">Please log in again to access your dashboard.</p>
        <Link to="/login" className="mt-6 inline-block px-6 py-2 bg-cyan-500 text-white rounded-xl hover:bg-cyan-600 transition">
          Go to Login
        </Link>
      </div>
    );
  }

  if (!userDashboard || !userDashboard.linkedAccounts?.length) {
    return (
      <div className="text-center text-gray-300 py-16">
        <p className="text-6xl mb-4">🏦</p>
        <h2 className="text-2xl font-bold text-white">Welcome to your Dashboard</h2>
        <p className="mt-2 text-gray-400">You don't have any bank accounts linked yet.</p>
        <div className="mt-6 flex flex-col sm:flex-row gap-4 justify-center">
          <Link
            to="/login"
            className="px-6 py-2 bg-cyan-500 text-white rounded-xl hover:bg-cyan-600 transition"
          >
            Add Bank Account
          </Link>
          <Link
            to="/dashboard/deposit"
            className="px-6 py-2 bg-white/10 text-white rounded-xl hover:bg-white/20 transition border border-white/20"
          >
            Deposit Money
          </Link>
        </div>
      </div>
    );
  }

  const { totalBalance, linkedAccounts } = userDashboard;
  const primaryAccount = linkedAccounts[0] || {};

  return (
    <div className="space-y-6">
      {/* ---------- TOP: BALANCE + QUICK STATS ---------- */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-4">
        <div className="lg:col-span-1 bg-gradient-to-r from-cyan-500 to-blue-600 rounded-2xl p-6 text-white shadow-xl relative overflow-hidden">
          <div className="absolute top-0 right-0 text-8xl opacity-10">💳</div>
          <div className="relative z-10">
            <p className="text-sm opacity-80">Total Balance</p>
            <p className="text-3xl font-bold">{formatCurrency(totalBalance)}</p>
            <p className="text-xs mt-1 opacity-70">UPI: {primaryAccount?.upiId || 'Not set'}</p>
            <p className="text-xs mt-2 opacity-60">
              {linkedAccounts?.length || 0} linked accounts
            </p>
          </div>
        </div>

        <div className="lg:col-span-2 grid grid-cols-2 gap-4">
          <div className="bg-white/5 backdrop-blur-sm rounded-xl p-4 border border-white/10">
            <p className="text-gray-400 text-xs">Total Sent</p>
            <p className="text-white font-bold text-xl">{formatCurrency(stats.totalSent)}</p>
            <p className="text-gray-500 text-xs mt-1">Avg: {formatCurrency(stats.avgSent)}</p>
          </div>
          <div className="bg-white/5 backdrop-blur-sm rounded-xl p-4 border border-white/10">
            <p className="text-gray-400 text-xs">Total Received</p>
            <p className="text-white font-bold text-xl">{formatCurrency(stats.totalReceived)}</p>
            <p className="text-gray-500 text-xs mt-1">Avg: {formatCurrency(stats.avgReceived)}</p>
          </div>
          <div className="bg-white/5 backdrop-blur-sm rounded-xl p-4 border border-white/10">
            <p className="text-gray-400 text-xs">Transactions</p>
            <p className="text-white font-bold text-xl">{stats.totalTx}</p>
          </div>
          <div className="bg-white/5 backdrop-blur-sm rounded-xl p-4 border border-white/10">
            <p className="text-gray-400 text-xs">Net Flow</p>
            <p className={`font-bold text-xl ${stats.totalReceived - stats.totalSent >= 0 ? 'text-green-400' : 'text-red-400'}`}>
              {formatCurrency(stats.totalReceived - stats.totalSent)}
            </p>
          </div>
        </div>
      </div>

      

      {/* ---------- CHARTS ROW ---------- */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-4">
        {/* Weekly Spending Bar */}
        <div className="lg:col-span-2 bg-white/5 backdrop-blur-sm rounded-xl p-5 border border-white/10">
          <div className="flex justify-between items-center mb-4">
            <h3 className="text-white font-semibold">Weekly Spending</h3>
            <span className="text-xs text-gray-400">Last 7 days</span>
          </div>
          {chartData.values.every((v) => v === 0) ? (
            <p className="text-gray-400 text-sm text-center py-4">No spending in the last 7 days.</p>
          ) : (
            <div className="flex items-end justify-between h-28 gap-1">
              {chartData.days.map((day, idx) => (
                <div key={idx} className="flex flex-col items-center flex-1">
                  <div
                    className="w-full bg-gradient-to-t from-cyan-400 to-blue-400 rounded-t transition-all hover:opacity-80"
                    style={{ height: `${(chartData.values[idx] / chartData.max) * 100}%` }}
                  />
                  <span className="text-[10px] text-gray-400 mt-1">{day}</span>
                </div>
              ))}
            </div>
          )}
        </div>

        {/* Income vs Expense Donut */}
        <div className="bg-white/5 backdrop-blur-sm rounded-xl p-5 border border-white/10 flex flex-col items-center justify-center">
          <h3 className="text-white font-semibold mb-2">Income vs Expense</h3>
          <div className="relative inline-block">
            <svg viewBox="0 0 100 100" className="w-32 h-32 -rotate-90">
              <circle cx="50" cy="50" r="40" fill="none" stroke="#1e293b" strokeWidth="12" />
              {donutData.sentPercent > 0 && (
                <circle
                  cx="50"
                  cy="50"
                  r="40"
                  fill="none"
                  stroke="#f97316"
                  strokeWidth="12"
                  strokeDasharray={2 * Math.PI * 40}
                  strokeDashoffset={2 * Math.PI * 40 - (donutData.sentPercent / 100) * 2 * Math.PI * 40}
                  strokeLinecap="round"
                />
              )}
              {donutData.receivedPercent > 0 && (
                <circle
                  cx="50"
                  cy="50"
                  r="40"
                  fill="none"
                  stroke="#22d3ee"
                  strokeWidth="12"
                  strokeDasharray={2 * Math.PI * 40}
                  strokeDashoffset={2 * Math.PI * 40 - (donutData.receivedPercent / 100) * 2 * Math.PI * 40 - (donutData.sentPercent / 100) * 2 * Math.PI * 40}
                  strokeLinecap="round"
                />
              )}
            </svg>
            <div className="absolute inset-0 flex items-center justify-center text-white font-bold text-sm">
              {stats.totalTx > 0 ? 'Flow' : '0'}
            </div>
          </div>
          <div className="flex gap-4 mt-2 text-xs">
            <span className="flex items-center gap-1">
              <span className="inline-block w-3 h-3 rounded-full bg-orange-500"></span> Sent
            </span>
            <span className="flex items-center gap-1">
              <span className="inline-block w-3 h-3 rounded-full bg-cyan-400"></span> Received
            </span>
          </div>
          <div className="mt-2 text-gray-400 text-xs">
            Sent: {formatCurrency(stats.totalSent)} | Received: {formatCurrency(stats.totalReceived)}
          </div>
        </div>
      </div>

      {/* ---------- RECENT TRANSACTIONS ---------- */}
      <div>
        <div className="flex justify-between items-center mb-3">
          <h3 className="text-white font-semibold">Recent Transactions</h3>
          <Link to="/dashboard/history" className="text-xs text-cyan-400 hover:underline">
            See all
          </Link>
        </div>
        {miniStatement?.length === 0 ? (
          <p className="text-gray-400 text-sm">No transactions yet.</p>
        ) : (
          <div className="space-y-2">
            {miniStatement.slice(0, 5).map((tx) => (
              <div
                key={tx.transactionId}
                className="flex items-center justify-between bg-white/5 rounded-xl px-4 py-3 border border-white/5 hover:bg-white/10 transition"
              >
                <div className="flex items-center gap-3">
                  <span className="text-2xl">
                    {tx.transactionType === 'TRANSFER' || tx.transactionType === 'SENT' ? '⬆️' : '⬇️'}
                  </span>
                  <div>
                    <p className="text-white text-sm font-medium">
                      {tx.transactionType === 'TRANSFER' || tx.transactionType === 'SENT'
                        ? `Sent to ${tx.receiverUserId || 'User'}`
                        : `Received from ${tx.senderUserId || 'User'}`}
                    </p>
                    <p className="text-gray-400 text-xs">{formatDate(tx.createdAt)}</p>
                  </div>
                </div>
                <div className="text-right">
                  <p
                    className={`font-semibold ${
                      tx.transactionType === 'TRANSFER' || tx.transactionType === 'SENT'
                        ? 'text-red-400'
                        : 'text-green-400'
                    }`}
                  >
                    {tx.transactionType === 'TRANSFER' || tx.transactionType === 'SENT' ? '-' : '+'}
                    {formatCurrency(tx.amount)}
                  </p>
                  <span
                    className={`text-xs px-2 py-0.5 rounded-full ${
                      tx.transactionStatus === 'SUCCESS'
                        ? 'bg-green-500/20 text-green-300'
                        : tx.transactionStatus === 'PENDING'
                        ? 'bg-yellow-500/20 text-yellow-300'
                        : 'bg-red-500/20 text-red-300'
                    }`}
                  >
                    {tx.transactionStatus}
                  </span>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
};

export default Overview;