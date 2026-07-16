const Skeleton = ({ className = '' }) => {
  return (
    <div className={`animate-pulse bg-white/10 rounded-xl ${className}`}>
      <div className="h-8 bg-white/5 rounded mb-2"></div>
      <div className="h-4 bg-white/5 rounded w-3/4"></div>
    </div>
  );
};

export default Skeleton;