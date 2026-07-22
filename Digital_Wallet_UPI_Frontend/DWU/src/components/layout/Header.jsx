import { useSelector } from 'react-redux';

const Header = () => {
  const { user } = useSelector((state) => state.auth);

  return (
    <header className="flex justify-between items-center px-4 md:px-8 py-4 bg-white/10 backdrop-blur-md border-b border-white/10">
      {/* Icon only – app name is in sidebar */}
      <div className="flex items-center">
        <span className="text-3xl">💳</span>
      </div>

      {/* User Name */}
      <div className="flex items-center gap-4">
        <span className="hidden md:inline text-white/80 text-sm font-medium">
          {user?.name || 'User'}
        </span>
      </div>
    </header>
  );
};

export default Header;