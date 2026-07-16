import { useState } from 'react';
import { useDispatch } from 'react-redux';
import { createSplitBill } from '../../api/actionApi';
import { showToast } from '../../features/toast/toastSlice';
import { useAuth } from '../../hooks/useAuth';
import Input from '../../components/common/Input';
import Button from '../../components/common/Button';

const SplitBillCreate = () => {
  const dispatch = useDispatch();
  const { user } = useAuth();
  const [loading, setLoading] = useState(false);
  const [form, setForm] = useState({
    createdByUserId: user?.id || '',
    receiverBankId: '',
    title: '',
    description: '',
    totalAmount: '',
    splitType: 'EQUAL',
    dueDate: '',
    expiryDate: '',
    participants: [{ userId: '', userBankId: '', shareAmount: '' }],
  });

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleParticipantChange = (index, field, value) => {
    const updated = [...form.participants];
    updated[index][field] = value;
    setForm({ ...form, participants: updated });
  };

  const addParticipant = () => {
    setForm({
      ...form,
      participants: [...form.participants, { userId: '', userBankId: '', shareAmount: '' }],
    });
  };

  const removeParticipant = (index) => {
    if (form.participants.length <= 1) return;
    const updated = form.participants.filter((_, i) => i !== index);
    setForm({ ...form, participants: updated });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    // Validate
    if (!form.title || parseFloat(form.totalAmount) <= 0 || form.participants.some(p => !p.userId || !p.shareAmount)) {
      dispatch(showToast({ message: 'Please fill all fields', type: 'error' }));
      setLoading(false);
      return;
    }
    const payload = {
      ...form,
      createdByUserId: parseInt(form.createdByUserId),
      receiverBankId: parseInt(form.receiverBankId),
      totalAmount: parseFloat(form.totalAmount),
      participants: form.participants.map(p => ({
        userId: parseInt(p.userId),
        userBankId: parseInt(p.userBankId || 0),
        shareAmount: parseFloat(p.shareAmount),
      })),
    };
    try {
      const response = await createSplitBill(payload);
      if (response.data.statuscode === 200) {
        dispatch(showToast({ message: 'Split bill created!', type: 'success' }));
        setForm({
          ...form,
          receiverBankId: '',
          title: '',
          description: '',
          totalAmount: '',
          dueDate: '',
          expiryDate: '',
          participants: [{ userId: '', userBankId: '', shareAmount: '' }],
        });
      } else {
        dispatch(showToast({ message: response.data.message || 'Creation failed', type: 'error' }));
      }
    } catch (err) {
      dispatch(showToast({ message: err.response?.data?.message || 'Creation failed', type: 'error' }));
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="max-w-2xl mx-auto">
      <h2 className="text-2xl font-bold text-white mb-6">Create Split Bill</h2>
      <form onSubmit={handleSubmit} className="space-y-4 bg-white/5 backdrop-blur-sm rounded-xl p-6 border border-white/10">
        <Input
          label="Receiver Bank ID (who receives the total)"
          name="receiverBankId"
          type="number"
          value={form.receiverBankId}
          onChange={handleChange}
          required
        />
        <Input
          label="Title"
          name="title"
          value={form.title}
          onChange={handleChange}
          required
        />
        <Input
          label="Description"
          name="description"
          value={form.description}
          onChange={handleChange}
        />
        <Input
          label="Total Amount (₹)"
          name="totalAmount"
          type="number"
          step="0.01"
          min="1"
          value={form.totalAmount}
          onChange={handleChange}
          required
        />
        <div>
          <label className="block text-gray-300 text-sm mb-1">Split Type</label>
          <select
            name="splitType"
            value={form.splitType}
            onChange={handleChange}
            className="w-full bg-white/10 border border-white/20 rounded-xl px-4 py-3 text-white focus:ring-2 focus:ring-cyan-400 outline-none"
          >
            <option value="EQUAL">Equal</option>
            <option value="CUSTOM">Custom</option>
          </select>
        </div>
        <Input
          label="Due Date"
          name="dueDate"
          type="datetime-local"
          value={form.dueDate}
          onChange={handleChange}
        />
        <Input
          label="Expiry Date"
          name="expiryDate"
          type="datetime-local"
          value={form.expiryDate}
          onChange={handleChange}
        />

        <div>
          <h4 className="text-white font-semibold mb-2">Participants</h4>
          {form.participants.map((p, idx) => (
            <div key={idx} className="grid grid-cols-3 gap-2 mb-2">
              <Input
                label="User ID"
                type="number"
                value={p.userId}
                onChange={(e) => handleParticipantChange(idx, 'userId', e.target.value)}
                required
              />
              <Input
                label="Bank ID"
                type="number"
                value={p.userBankId}
                onChange={(e) => handleParticipantChange(idx, 'userBankId', e.target.value)}
              />
              <Input
                label="Share Amount"
                type="number"
                step="0.01"
                min="0"
                value={p.shareAmount}
                onChange={(e) => handleParticipantChange(idx, 'shareAmount', e.target.value)}
                required
              />
              <button
                type="button"
                onClick={() => removeParticipant(idx)}
                className="col-span-3 text-red-400 text-sm hover:underline"
              >
                Remove
              </button>
            </div>
          ))}
          <button
            type="button"
            onClick={addParticipant}
            className="text-cyan-400 text-sm hover:underline"
          >
            + Add Participant
          </button>
        </div>

        <Button type="submit" loading={loading}>Create Split Bill</Button>
      </form>
    </div>
  );
};

export default SplitBillCreate;