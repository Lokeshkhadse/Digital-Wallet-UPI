import { useEffect, useMemo } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { Link } from 'react-router-dom';
import { fetchDashboard } from '../../features/dashboard/dashboardThunks';
import { fetchMiniStatement } from '../../features/transactions/transactionsThunks';
import { useAuth } from '../../hooks/useAuth';
import Loader from '../../components/common/Loader';
import { formatCurrency, formatDate, formatShortDate, getLast7Days } from '../../utils/formatters';

// ---------- Reusable Chart Components ----------
const WeeklyBarChart = ({ data, max, days }) => {
  if (data.every((v) => v === 0)) {
    return <p className="text-gray-400 text-sm text-center py-4">No spending in the last 7 days.</p>;
  }
  return (
    <div className="flex items-end justify-between h-28 gap-1">
      {days.map((day, idx) => (
        <div key={idx} className="flex flex-col items-center flex-1">
          <div
            className="w-full bg-gradient-to-t from-cyan-400 to-blue-400 rounded-t transition-all hover:opacity-80"
            style={{ height: `${(data[idx] / max) * 100}%` }}
          />
          <span className="text-[10px] text-gray-400 mt-1">{day}</span>
        </div>
      ))}
    </div>
  );
};

const DonutChart = ({ sentPercent, receivedPercent, totalTx, totalSent, totalReceived }) => {
  const circumference = 2 * Math.PI * 40;
  const sentOffset = circumference - (sentPercent / 100) * circumference;
  const receivedOffset =
    circumference - (receivedPercent / 100) * circumference - (sentPercent / 100) * circumference;

  return (
    <div className="relative inline-block">
      <svg viewBox="0 0 100 100" className="w-32 h-32 -rotate-90">
        <circle cx="50" cy="50" r="40" fill="none" stroke="#1e293b" strokeWidth="12" />
        {sentPercent > 0 && (
          <circle
            cx="50"
            cy="50"
            r="40"
            fill="none"
            stroke="#f97316"
            strokeWidth="12"
            strokeDasharray={circumference}
            strokeDashoffset={sentOffset}
            strokeLinecap="round"
          />
        )}
        {receivedPercent > 0 && (
          <circle
            cx="50"
            cy="50"
            r="40"
            fill="none"
            stroke="#22d3ee"
            strokeWidth="12"
            strokeDasharray={circumference}
            strokeDashoffset={receivedOffset}
            strokeLinecap="round"
          />
        )}
      </svg>
      <div className="absolute inset-0 flex items-center justify-center text-white font-bold text-sm">
        {totalTx > 0 ? 'Flow' : '0'}
      </div>
    </div>
  );
};

const StatsCard = ({ label, value, subValue }) => (
  <div className="bg-white/5 backdrop-blur-sm rounded-xl p-4 border border-white/10">
    <p className="text-gray-400 text-xs">{label}</p>
    <p className="text-white font-bold text-xl">{value}</p>
    {subValue && <p className="text-gray-500 text-xs mt-1">{subValue}</p>}
  </div>
);

const Overview = () => {
  const dispatch = useDispatch();
  const { user } = useAuth();
  const { userDashboard, isLoading } = useSelector((state) => state.dashboard);
  const { miniStatement } = useSelector((state) => state.transactions);

  const userId = useMemo(() => user?.id || parseInt(localStorage.getItem('userId'), 10) || null, [user]);

  useEffect(() => {
    if (userId) {
      dispatch(fetchDashboard(userId));
      dispatch(fetchMiniStatement(userId));
    }
  }, [dispatch, userId]);

  // Stats from real transactions
  const stats = useMemo(() => {
    if (!miniStatement?.length) {
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

  // Weekly chart
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

  // Donut chart
  const donutData = useMemo(() => {
    const { totalSent, totalReceived, totalTx } = stats;
    const total = totalSent + totalReceived || 1;
    return {
      sentPercent: (totalSent / total) * 100,
      receivedPercent: (totalReceived / total) * 100,
      totalTx,
      totalSent,
      totalReceived,
    };
  }, [stats]);

  // ---------- Loading & Empty States ----------
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

  if (!userDashboard?.linkedAccounts?.length) {
    return (
      <div className="text-center text-gray-300 py-16">
        <p className="text-6xl mb-4">🏦</p>
        <h2 className="text-2xl font-bold text-white">Welcome to your Dashboard</h2>
        <p className="mt-2 text-gray-400">You don't have any bank accounts linked yet.</p>
        <div className="mt-6 flex flex-col sm:flex-row gap-4 justify-center">
          <Link
            to="/dashboard/accounts"
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
      {/* ---------- Top: Balance + Quick Stats ---------- */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-4">
        {/* Balance Card */}
        <div className="lg:col-span-1 bg-gradient-to-r from-cyan-500 to-blue-600 rounded-2xl p-6 text-white shadow-xl relative overflow-hidden">
          <div className="absolute top-0 right-0 text-8xl opacity-10">💳</div>
          <div className="relative z-10">
            <p className="text-sm opacity-80">Total Balance</p>
            <p className="text-3xl font-bold">{formatCurrency(totalBalance)}</p>
            <p className="text-xs mt-1 opacity-70">UPI: {primaryAccount?.upiId || 'Not set'}</p>
            <p className="text-xs mt-2 opacity-60">{linkedAccounts.length} linked accounts</p>
          </div>
        </div>

        {/* Stats */}
        <div className="lg:col-span-2 grid grid-cols-2 gap-4">
          <StatsCard label="Total Sent" value={formatCurrency(stats.totalSent)} subValue={`Avg: ${formatCurrency(stats.avgSent)}`} />
          <StatsCard label="Total Received" value={formatCurrency(stats.totalReceived)} subValue={`Avg: ${formatCurrency(stats.avgReceived)}`} />
          <StatsCard label="Transactions" value={stats.totalTx} />
          <StatsCard
            label="Net Flow"
            value={formatCurrency(stats.totalReceived - stats.totalSent)}
            subValue={stats.totalReceived - stats.totalSent >= 0 ? 'Positive' : 'Negative'}
            className={stats.totalReceived - stats.totalSent >= 0 ? 'text-green-400' : 'text-red-400'}
          />
        </div>
      </div>

      {/* ---------- Charts Row ---------- */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-4">
        <div className="lg:col-span-2 bg-white/5 backdrop-blur-sm rounded-xl p-5 border border-white/10">
          <div className="flex justify-between items-center mb-4">
            <h3 className="text-white font-semibold">Weekly Spending</h3>
            <span className="text-xs text-gray-400">Last 7 days</span>
          </div>
          <WeeklyBarChart data={chartData.values} max={chartData.max} days={chartData.days} />
        </div>

        <div className="bg-white/5 backdrop-blur-sm rounded-xl p-5 border border-white/10 flex flex-col items-center justify-center">
          <h3 className="text-white font-semibold mb-2">Income vs Expense</h3>
          <DonutChart
            sentPercent={donutData.sentPercent}
            receivedPercent={donutData.receivedPercent}
            totalTx={donutData.totalTx}
            totalSent={donutData.totalSent}
            totalReceived={donutData.totalReceived}
          />
          <div className="flex gap-4 mt-2 text-xs">
            <span className="flex items-center gap-1">
              <span className="inline-block w-3 h-3 rounded-full bg-orange-500" /> Sent
            </span>
            <span className="flex items-center gap-1">
              <span className="inline-block w-3 h-3 rounded-full bg-cyan-400" /> Received
            </span>
          </div>
          <div className="mt-2 text-gray-400 text-xs">
            Sent: {formatCurrency(donutData.totalSent)} | Received: {formatCurrency(donutData.totalReceived)}
          </div>
        </div>
      </div>

      {/* ---------- Recent Transactions ---------- */}
      <div>
        <div className="flex justify-between items-center mb-3">
          <h3 className="text-white font-semibold">Recent Transactions</h3>
          <Link to="/dashboard/history" className="text-xs text-cyan-400 hover:underline">
            See all
          </Link>
        </div>
        {!miniStatement?.length ? (
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