import { Routes, Route } from 'react-router-dom';
import Sidebar from '../../components/layout/Sidebar';
import Header from '../../components/layout/Header';
import Overview from './Overview';
import SendMoney from './SendMoney';
import TransactionHistory from './TransactionHistory';
import Profile from './Profile';
import Deposit from './Deposit';
import Withdraw from './Withdraw';
import QrPayment from './QrPayment';
import ScheduledTransfer from './ScheduledTransfer';
import SplitBillCreate from './SplitBillCreate';
import SplitBillView from './SplitBillView';
import UpiPinManagement from './UpiPinManagement';
import BankAccounts from './BankAccounts';
import CreateBalance from './CreateBalance';

const Dashboard = () => {
  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-900 via-indigo-900 to-purple-900 flex overflow-hidden relative">
      <div className="absolute inset-0 overflow-hidden pointer-events-none">
        <div className="absolute -top-24 -right-24 w-96 h-96 bg-blue-400/20 rounded-full blur-3xl animate-pulse" />
        <div className="absolute top-1/3 -left-32 w-80 h-80 bg-cyan-400/20 rounded-full blur-3xl animate-pulse delay-1000" />
        <div className="absolute bottom-20 right-20 w-72 h-72 bg-purple-400/20 rounded-full blur-3xl animate-pulse delay-2000" />
      </div>

      <Sidebar />
      <div className="flex-1 flex flex-col overflow-hidden relative z-10">
        <Header />
        <main className="flex-1 p-4 md:p-8 overflow-y-auto">
          <Routes>
            <Route index element={<Overview />} />
            <Route path="send" element={<SendMoney />} />
            <Route path="history" element={<TransactionHistory />} />
            <Route path="profile" element={<Profile />} />
            <Route path="deposit" element={<Deposit />} />
            <Route path="withdraw" element={<Withdraw />} />
            <Route path="qr" element={<QrPayment />} />
            <Route path="scheduled" element={<ScheduledTransfer />} />
            <Route path="split-create" element={<SplitBillCreate />} />
            <Route path="split-view" element={<SplitBillView />} />
            <Route path="upi-pin" element={<UpiPinManagement />} />
            <Route path="accounts" element={<BankAccounts />} />
            <Route path="create-balance" element={<CreateBalance />} />
            <Route path="upi-pin" element={<UpiPinManagement />} />
          </Routes>
        </main>
      </div>
    </div>
  );
};

export default Dashboard;