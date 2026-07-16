import { useDispatch, useSelector } from 'react-redux';
import { showToast, hideToast } from '../features/toast/toastSlice';

export const useToast = () => {
  const dispatch = useDispatch();
  const toast = useSelector((state) => state.toast);

  const show = (message, type = 'info') => {
    dispatch(showToast({ message, type }));
  };

  const hide = () => {
    dispatch(hideToast());
  };

  return { toast, show, hide };
};