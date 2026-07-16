import { Link } from 'react-router-dom';
import { useSelector } from 'react-redux';

const Home = () => {
  const { isAuthenticated } = useSelector((state) => state.auth);

  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-900 via-indigo-900 to-purple-900 overflow-hidden relative">
      {/* background blobs */}
      <div className="absolute inset-0 overflow-hidden pointer-events-none">
        <div className="absolute -top-24 -right-24 w-96 h-96 bg-blue-400/20 rounded-full blur-3xl animate-pulse" />
        <div className="absolute top-1/3 -left-32 w-80 h-80 bg-cyan-400/20 rounded-full blur-3xl animate-pulse delay-1000" />
        <div className="absolute bottom-20 right-20 w-72 h-72 bg-purple-400/20 rounded-full blur-3xl animate-pulse delay-2000" />
      </div>

      {/* Navbar */}
      <nav className="relative z-10 flex justify-between items-center px-4 md:px-10 py-4 md:py-6 bg-white/10 backdrop-blur-md border-b border-white/10">
        <div className="flex items-center space-x-2">
          <span className="text-3xl md:text-4xl">💳</span>
          <h1 className="text-xl md:text-3xl font-bold text-white tracking-tight">
            <span className="text-cyan-400">Digital</span> Wallet
          </h1>
        </div>
        <div className="flex items-center space-x-3 md:space-x-4">
          <Link to="/login">
            <button className="px-4 md:px-6 py-2 text-sm md:text-base border border-white/30 text-white rounded-xl hover:bg-white/10 hover:border-white/50 transition-all duration-300 backdrop-blur-sm">
              Login
            </button>
          </Link>
          <Link to="/login?tab=register">
            <button className="px-4 md:px-6 py-2 text-sm md:text-base bg-gradient-to-r from-cyan-500 to-blue-500 text-white rounded-xl hover:from-cyan-600 hover:to-blue-600 shadow-lg shadow-cyan-500/30 hover:shadow-cyan-500/50 transition-all duration-300 transform hover:scale-105">
              Sign Up
            </button>
          </Link>
        </div>
      </nav>

      {/* Hero */}
      <section className="relative z-10 flex flex-col items-center justify-center text-center px-4 pt-16 md:pt-24 pb-12">
        <div className="max-w-4xl mx-auto animate-fadeInUp">
          <span className="inline-block px-4 py-1.5 mb-6 text-xs font-semibold text-cyan-300 bg-cyan-500/20 rounded-full border border-cyan-500/30 backdrop-blur-sm">
            🔥 Next‑Gen UPI Payments
          </span>
          <h1 className="text-4xl md:text-6xl lg:text-7xl font-extrabold text-white leading-tight">
            Welcome to the <br />
            <span className="bg-clip-text text-transparent bg-gradient-to-r from-cyan-400 to-blue-400">
              Future of Money
            </span>
          </h1>
          <p className="mt-6 text-lg md:text-xl text-gray-300 max-w-2xl mx-auto leading-relaxed">
            Fast, secure and seamless digital wallet experience. Transfer money instantly, manage your finances, and pay bills with just a few taps – all in one place.
          </p>
          <div className="mt-10 flex flex-col sm:flex-row items-center justify-center gap-4">
            <Link to={isAuthenticated ? '/dashboard' : '/login'}>
          
            </Link>
          </div>
        </div>
      </section>

      {/* Features */}
      <section className="relative z-10 px-4 md:px-10 py-16 max-w-6xl mx-auto">
        <div className="text-center mb-12">
          <h2 className="text-3xl md:text-4xl font-bold text-white">
            Why Choose <span className="text-cyan-400">Digital Wallet</span>
          </h2>
          <p className="mt-3 text-gray-300 max-w-xl mx-auto">
            All the tools you need to manage your money securely and efficiently.
          </p>
        </div>
        <div className="grid md:grid-cols-3 gap-6 md:gap-8">
          <div className="group bg-white/5 backdrop-blur-md border border-white/10 rounded-2xl p-6 md:p-8 text-center hover:bg-white/10 hover:border-white/20 transition-all duration-300 hover:scale-105 shadow-lg">
            <div className="text-5xl mb-4">💸</div>
            <h3 className="text-xl font-bold text-white">Instant Transfer</h3>
            <p className="mt-2 text-gray-300 text-sm leading-relaxed">Send and receive money in real‑time with zero latency using UPI.</p>
          </div>
          <div className="group bg-white/5 backdrop-blur-md border border-white/10 rounded-2xl p-6 md:p-8 text-center hover:bg-white/10 hover:border-white/20 transition-all duration-300 hover:scale-105 shadow-lg">
            <div className="text-5xl mb-4">🔒</div>
            <h3 className="text-xl font-bold text-white">Secure Wallet</h3>
            <p className="mt-2 text-gray-300 text-sm leading-relaxed">Military‑grade encryption and JWT authentication keep your funds safe.</p>
          </div>
          <div className="group bg-white/5 backdrop-blur-md border border-white/10 rounded-2xl p-6 md:p-8 text-center hover:bg-white/10 hover:border-white/20 transition-all duration-300 hover:scale-105 shadow-lg">
            <div className="text-5xl mb-4">📊</div>
            <h3 className="text-xl font-bold text-white">Transaction History</h3>
            <p className="mt-2 text-gray-300 text-sm leading-relaxed">View detailed logs of every transaction with filters and search.</p>
          </div>
        </div>
      </section>

      {/* Footer */}
      <footer className="relative z-10 mt-12 border-t border-white/10 bg-black/20 backdrop-blur-sm">
        <div className="max-w-6xl mx-auto px-4 md:px-10 py-8 flex flex-col md:flex-row justify-between items-center gap-4 text-sm text-gray-400">
          <div className="flex items-center gap-2">
            <span className="text-xl">💳</span>
            <span className="font-semibold text-white">Digital Wallet UPI</span>
            <span className="hidden md:inline">© 2026 All rights reserved.</span>
          </div>
          <div className="flex gap-6">
            <Link to="#" className="hover:text-cyan-400 transition-colors">Privacy</Link>
            <Link to="#" className="hover:text-cyan-400 transition-colors">Terms</Link>
            <Link to="#" className="hover:text-cyan-400 transition-colors">Support</Link>
          </div>
        </div>
      </footer>

      <style>{`
        @keyframes fadeInUp {
          from { opacity: 0; transform: translateY(30px); }
          to { opacity: 1; transform: translateY(0); }
        }
        .animate-fadeInUp {
          animation: fadeInUp 0.8s ease-out forwards;
        }
        @keyframes pulse {
          0%, 100% { opacity: 0.3; }
          50% { opacity: 0.6; }
        }
        .animate-pulse {
          animation: pulse 4s ease-in-out infinite;
        }
        .delay-1000 { animation-delay: 1000ms; }
        .delay-2000 { animation-delay: 2000ms; }
      `}</style>
    </div>
  );
};

export default Home;