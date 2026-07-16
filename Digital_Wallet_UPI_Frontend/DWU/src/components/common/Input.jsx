const Input = ({
  label,
  name,
  type = 'text',
  value,
  onChange,
  placeholder,
  required = false,
  error,
  className = '',
  ...props
}) => {
  return (
    <div className="mb-4">
      {label && (
        <label htmlFor={name} className="block text-gray-300 text-sm mb-1">
          {label}
        </label>
      )}
      <input
        id={name}
        name={name}
        type={type}
        value={value}
        onChange={onChange}
        placeholder={placeholder}
        required={required}
        className={`w-full bg-white/10 border ${error ? 'border-red-400' : 'border-white/20'} rounded-xl px-4 py-3 text-white placeholder-gray-400 focus:ring-2 ${error ? 'focus:ring-red-400' : 'focus:ring-cyan-400'} outline-none transition-all ${className}`}
        {...props}
      />
      {error && <p className="text-red-400 text-sm mt-1">{error}</p>}
    </div>
  );
};

export default Input;