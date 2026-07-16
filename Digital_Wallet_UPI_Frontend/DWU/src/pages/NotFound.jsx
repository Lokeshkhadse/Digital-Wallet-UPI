import { Link } from 'react-router-dom';

const NotFound = () => {
  return (
    <div className="min-h-screen flex flex-col items-center justify-center bg-gradient-to-br from-blue-900 via-indigo-900 to-purple-900 text-white">
      <h1 className="text-6xl font-bold mb-4">404</h1>
      <p className="text-2xl mb-6">Page not found</p>
      <Link to="/dashboard" className="px-6 py-3 bg-cyan-500 rounded-xl hover:bg-cyan-600 transition">
        Go to Dashboard
      </Link>
    </div>
  );
};

export default NotFound;