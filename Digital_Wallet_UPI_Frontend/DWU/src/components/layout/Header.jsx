import { useSelector } from 'react-redux';

const Header = () => {
  const { user } = useSelector((state) => state.auth);

  return (
    <header className="flex justify-between items-center px-4 md:px-8 py-4 bg-white/10 backdrop-blur-md border-b border-white/10">
      <div className="flex items-center gap-2">
        <span className="text-3xl">💳</span>
        <h1 className="text-xl md:text-2xl font-bold text-white">
          <span className="text-cyan-400">Digital</span> Wallet
        </h1>
      </div>
      <div className="flex items-center gap-4">
        <span className="hidden md:inline text-white/80 text-sm">
          {user?.name || 'User'}
        </span>
        <span className="text-white font-semibold bg-white/10 px-3 py-1 rounded-full text-sm">
          ₹{user?.totalBalance?.toFixed(2) || '0.00'}
        </span>
      </div>
    </header>
  );
};

export default Header;