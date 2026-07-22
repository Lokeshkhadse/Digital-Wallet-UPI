import { useState, useEffect, useRef } from 'react';
import { useDispatch } from 'react-redux';
import { useAuth } from '../../hooks/useAuth';
import { qrPay } from '../../api/actionApi';
import { getUserBankDetails } from '../../api/queryApi';
import { showToast } from '../../features/toast/toastSlice';
import Loader from '../../components/common/Loader';
import QRCode from 'react-qr-code'; // ✅ modern, Vite-friendly
import { Html5Qrcode } from 'html5-qrcode'; // ✅ named export

const QrPayment = () => {
  const dispatch = useDispatch();
  const { user } = useAuth();

  // ---------- State ----------
  const [loading, setLoading] = useState(false);
  const [loadingBanks, setLoadingBanks] = useState(false);
  const [banks, setBanks] = useState([]);
  const [activeTab, setActiveTab] = useState('myqr');
  const [scannerActive, setScannerActive] = useState(false);
  const scannerRef = useRef(null);
  const fileInputRef = useRef(null);

  const [form, setForm] = useState({
    senderBankId: '',
    upiId: '',
    amount: '',
    upiPin: '',
    remarks: '',
  });

  // Fetch user's bank accounts
  useEffect(() => {
    const fetchBanks = async () => {
      if (!user?.id) return;
      setLoadingBanks(true);
      try {
        const response = await getUserBankDetails(user.id);
        const bankList = response.data.data || [];
        setBanks(bankList);
        if (bankList.length > 0 && !form.senderBankId) {
          setForm((prev) => ({ ...prev, senderBankId: bankList[0].id }));
        }
      } catch (error) {
        dispatch(showToast({ message: 'Failed to load bank accounts', type: 'error' }));
      } finally {
        setLoadingBanks(false);
      }
    };
    fetchBanks();
  }, [user, dispatch, form.senderBankId]);

  // ---------- QR Scanner Functions ----------
  const startScanner = () => {
    if (scannerActive) return;
    const scannerContainer = document.getElementById('qr-reader');
    if (!scannerContainer) return;

    const config = {
      fps: 10,
      qrbox: { width: 250, height: 250 },
      aspectRatio: 1.0,
    };

    Html5Qrcode.getCameras()
      .then((cameras) => {
        if (cameras.length === 0) {
          dispatch(showToast({ message: 'No camera found', type: 'error' }));
          return;
        }
        const cameraId = cameras[0].id;
        if (scannerRef.current) {
          scannerRef.current.stop().catch(() => {});
        }
        const scanner = new Html5Qrcode('qr-reader');
        scannerRef.current = scanner;
        scanner
          .start(
            cameraId,
            config,
            (decodedText) => {
              setForm((prev) => ({ ...prev, upiId: decodedText }));
              dispatch(showToast({ message: 'QR scanned successfully!', type: 'success' }));
              stopScanner();
            },
            (error) => {}
          )
          .then(() => setScannerActive(true))
          .catch((err) => {
            dispatch(showToast({ message: 'Failed to start camera: ' + err.message, type: 'error' }));
          });
      })
      .catch((err) => {
        dispatch(showToast({ message: 'Camera access denied: ' + err.message, type: 'error' }));
      });
  };

  const stopScanner = () => {
    if (scannerRef.current) {
      scannerRef.current
        .stop()
        .then(() => setScannerActive(false))
        .catch(() => {});
    }
  };

  // Handle file upload (gallery)
  const handleFileUpload = async (event) => {
    const file = event.target.files[0];
    if (!file) return;

    const scanner = new Html5Qrcode('qr-reader');
    try {
      const decodedText = await scanner.scanFile(file, true);
      setForm((prev) => ({ ...prev, upiId: decodedText }));
      dispatch(showToast({ message: 'QR decoded from image!', type: 'success' }));
    } catch (err) {
      dispatch(showToast({ message: 'Failed to decode QR: ' + err.message, type: 'error' }));
    } finally {
      scanner.clear();
    }
    if (fileInputRef.current) fileInputRef.current.value = '';
  };

  // Cleanup scanner on unmount
  useEffect(() => {
    return () => {
      if (scannerRef.current) {
        scannerRef.current.stop().catch(() => {});
      }
    };
  }, []);

  // ---------- Payment Handlers ----------
  const handleChange = (e) => {
    const { name, value } = e.target;
    setForm((prev) => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);

    if (!form.senderBankId) {
      dispatch(showToast({ message: 'Please select a sender account', type: 'error' }));
      setLoading(false);
      return;
    }
    if (!form.upiId) {
      dispatch(showToast({ message: 'Please enter or scan a UPI ID', type: 'error' }));
      setLoading(false);
      return;
    }
    if (parseFloat(form.amount) <= 0) {
      dispatch(showToast({ message: 'Amount must be greater than zero', type: 'error' }));
      setLoading(false);
      return;
    }
    if (!form.upiPin || form.upiPin.length !== 6 || !/^\d{6}$/.test(form.upiPin)) {
      dispatch(showToast({ message: 'Please enter a valid 6-digit UPI PIN', type: 'error' }));
      setLoading(false);
      return;
    }

    const payload = {
      senderUserId: user?.id || parseInt(localStorage.getItem('userId'), 10),
      senderBankId: parseInt(form.senderBankId),
      upiId: form.upiId.trim(),
      amount: parseFloat(form.amount),
      upiPin: form.upiPin.trim(),
      remarks: form.remarks || '',
    };

    try {
      const response = await qrPay(payload);
      if (response.data.statuscode === 200) {
        dispatch(showToast({ message: 'QR Payment successful!', type: 'success' }));
        setForm((prev) => ({ ...prev, upiId: '', amount: '', upiPin: '', remarks: '' }));
        if (scannerActive) stopScanner();
      } else {
        dispatch(showToast({ message: response.data.message || 'Payment failed', type: 'error' }));
      }
    } catch (err) {
      const msg = err.response?.data?.message || 'Payment failed';
      dispatch(showToast({ message: msg, type: 'error' }));
    } finally {
      setLoading(false);
    }
  };

  // ---------- Render ----------
  const userUpiIds = banks.map((bank) => bank.upiId).filter((id) => id);

  return (
    <div className="max-w-4xl mx-auto">
      <h2 className="text-2xl font-bold text-white mb-6">QR Payments</h2>

      {/* Tabs */}
      <div className="flex gap-4 mb-6">
        <button
          onClick={() => setActiveTab('myqr')}
          className={`px-6 py-2 rounded-xl transition ${
            activeTab === 'myqr' ? 'bg-cyan-500 text-white' : 'bg-white/10 text-gray-300 hover:text-white'
          }`}
        >
          My QR Codes
        </button>
        <button
          onClick={() => {
            setActiveTab('scan');
            if (scannerActive) stopScanner();
          }}
          className={`px-6 py-2 rounded-xl transition ${
            activeTab === 'scan' ? 'bg-cyan-500 text-white' : 'bg-white/10 text-gray-300 hover:text-white'
          }`}
        >
          Scan & Pay
        </button>
      </div>

      {/* ---------- My QR Codes Tab ---------- */}
      {activeTab === 'myqr' && (
        <div className="bg-white/5 backdrop-blur-sm rounded-xl p-6 border border-white/10">
          {loadingBanks ? (
            <Loader />
          ) : userUpiIds.length === 0 ? (
            <p className="text-gray-400 text-center">No UPI IDs found. Please add a bank account.</p>
          ) : (
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              {banks.map((bank) => (
                <div key={bank.id} className="bg-white/10 rounded-xl p-4 text-center border border-white/10">
                  <p className="text-gray-300 text-sm mb-1">{bank.bankName}</p>
                  <p className="text-cyan-400 font-bold text-lg mb-3">{bank.upiId}</p>
                  <div className="flex justify-center">
                    <QRCode value={bank.upiId} size={160} />
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      )}

      {/* ---------- Scan & Pay Tab ---------- */}
      {activeTab === 'scan' && (
        <div className="bg-white/5 backdrop-blur-sm rounded-xl p-6 border border-white/10">
          <h3 className="text-white font-semibold text-lg mb-4">Scan QR Code</h3>

          {/* Scanner / Upload area */}
          <div className="mb-6">
            <div id="qr-reader" className="w-full max-w-md mx-auto"></div>

            <div className="flex flex-col sm:flex-row gap-3 mt-4 justify-center">
              <button
                type="button"
                onClick={scannerActive ? stopScanner : startScanner}
                className={`px-6 py-2 rounded-xl transition ${
                  scannerActive
                    ? 'bg-red-500/80 text-white hover:bg-red-600'
                    : 'bg-cyan-500 text-white hover:bg-cyan-600'
                }`}
              >
                {scannerActive ? 'Stop Camera' : 'Start Camera'}
              </button>
              <button
                type="button"
                onClick={() => fileInputRef.current.click()}
                className="px-6 py-2 rounded-xl bg-white/10 text-white hover:bg-white/20 transition"
              >
                Upload QR Image
              </button>
              <input
                type="file"
                accept="image/*"
                ref={fileInputRef}
                onChange={handleFileUpload}
                className="hidden"
              />
            </div>
            {form.upiId && (
              <p className="text-center text-green-400 mt-2">
                Scanned UPI: <span className="font-bold">{form.upiId}</span>
              </p>
            )}
          </div>

          {/* Payment form */}
          <form onSubmit={handleSubmit} className="space-y-4 max-w-lg mx-auto">
            <div>
              <label className="block text-gray-300 text-sm mb-1">From Account</label>
              <select
                name="senderBankId"
                value={form.senderBankId}
                onChange={handleChange}
                className="w-full bg-white/10 border border-white/20 rounded-xl px-4 py-3 text-white focus:ring-2 focus:ring-cyan-400 outline-none"
                required
                disabled={loadingBanks}
              >
                <option value="" className="bg-gray-800 text-white">Select bank</option>
                {banks.map((bank) => (
                  <option key={bank.id} value={bank.id} className="bg-gray-800 text-white">
                    {bank.bankName} - {bank.accountNumber}
                  </option>
                ))}
              </select>
              {loadingBanks && <p className="text-gray-400 text-xs mt-1">Loading accounts...</p>}
              {!loadingBanks && banks.length === 0 && (
                <p className="text-yellow-400 text-xs mt-1">No bank accounts found. Please add one first.</p>
              )}
            </div>

            <div>
              <label className="block text-gray-300 text-sm mb-1">Recipient UPI ID</label>
              <input
                type="text"
                name="upiId"
                value={form.upiId}
                onChange={handleChange}
                placeholder="e.g. merchant@pay"
                className="w-full bg-white/10 border border-white/20 rounded-xl px-4 py-3 text-white placeholder-gray-400 focus:ring-2 focus:ring-cyan-400 outline-none"
                required
              />
            </div>

            <div>
              <label className="block text-gray-300 text-sm mb-1">Amount (₹)</label>
              <input
                type="number"
                name="amount"
                value={form.amount}
                onChange={handleChange}
                placeholder="0.00"
                className="w-full bg-white/10 border border-white/20 rounded-xl px-4 py-3 text-white placeholder-gray-400 focus:ring-2 focus:ring-cyan-400 outline-none"
                required
                min="1"
                step="0.01"
              />
            </div>

            <div>
              <label className="block text-gray-300 text-sm mb-1">UPI PIN</label>
              <input
                type="password"
                name="upiPin"
                value={form.upiPin}
                onChange={handleChange}
                placeholder="Enter 6-digit PIN"
                className="w-full bg-white/10 border border-white/20 rounded-xl px-4 py-3 text-white placeholder-gray-400 focus:ring-2 focus:ring-cyan-400 outline-none"
                required
                maxLength="6"
                pattern="\d{6}"
              />
            </div>

            <div>
              <label className="block text-gray-300 text-sm mb-1">Remarks (optional)</label>
              <input
                type="text"
                name="remarks"
                value={form.remarks}
                onChange={handleChange}
                placeholder="e.g., Payment for lunch"
                className="w-full bg-white/10 border border-white/20 rounded-xl px-4 py-3 text-white placeholder-gray-400 focus:ring-2 focus:ring-cyan-400 outline-none"
              />
            </div>

            <button
              type="submit"
              disabled={loading}
              className="w-full bg-gradient-to-r from-purple-500 to-pink-500 py-3.5 rounded-xl text-white font-semibold shadow-lg shadow-purple-500/30 hover:shadow-purple-500/50 transition-all duration-300 disabled:opacity-70 flex items-center justify-center gap-2"
            >
              {loading ? <Loader size="w-5 h-5" /> : 'Pay via QR'}
            </button>
          </form>
        </div>
      )}
    </div>
  );
};

export default QrPayment;