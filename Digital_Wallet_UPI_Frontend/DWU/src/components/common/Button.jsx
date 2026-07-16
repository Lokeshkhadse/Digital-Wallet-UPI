import Loader from './Loader';

const Button = ({
  children,
  onClick,
  type = 'button',
  variant = 'primary', // primary, secondary, danger, success
  loading = false,
  disabled = false,
  className = '',
  ...props
}) => {
  const variantClasses = {
    primary: 'bg-gradient-to-r from-cyan-500 to-blue-500 hover:shadow-lg shadow-cyan-500/30 hover:shadow-cyan-500/50',
    secondary: 'bg-white/10 hover:bg-white/20 border border-white/20',
    danger: 'bg-gradient-to-r from-red-500 to-orange-500 hover:shadow-lg shadow-red-500/30 hover:shadow-red-500/50',
    success: 'bg-gradient-to-r from-emerald-500 to-green-500 hover:shadow-lg shadow-emerald-500/30 hover:shadow-emerald-500/50',
  };

  return (
    <button
      type={type}
      onClick={onClick}
      disabled={disabled || loading}
      className={`w-full py-3.5 rounded-xl text-white font-semibold transition-all duration-300 ${variantClasses[variant]} disabled:opacity-70 disabled:cursor-not-allowed flex items-center justify-center gap-2 ${className}`}
      {...props}
    >
      {loading ? <Loader size="w-5 h-5" /> : children}
    </button>
  );
};

export default Button;