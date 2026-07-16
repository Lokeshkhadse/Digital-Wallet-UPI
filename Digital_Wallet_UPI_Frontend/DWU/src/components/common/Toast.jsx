import { useEffect } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { hideToast } from '../../features/toast/toastSlice';

const Toast = () => {
  const dispatch = useDispatch();
  const { message, type, visible } = useSelector((state) => state.toast);

  useEffect(() => {
    if (visible) {
      const timer = setTimeout(() => {
        dispatch(hideToast());
      }, 5000);
      return () => clearTimeout(timer);
    }
  }, [visible, dispatch]);

  if (!visible || !message) return null;

  const bgColor =
    type === 'success' ? 'bg-green-500/90' :
    type === 'error' ? 'bg-red-500/90' :
    'bg-blue-500/90';

  return (
    <div className={`fixed top-6 left-1/2 -translate-x-1/2 z-50 px-5 py-3.5 rounded-xl text-white font-medium shadow-2xl backdrop-blur-sm ${bgColor} animate-slideDown max-w-[90%] md:max-w-md`}>
      <span className="text-sm">{message}</span>
    </div>
  );
};

export default Toast;