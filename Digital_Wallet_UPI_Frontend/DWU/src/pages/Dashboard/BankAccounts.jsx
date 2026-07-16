import { useEffect, useState } from "react";
import { useDispatch } from "react-redux";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../../hooks/useAuth";
import { getUserBankDetails } from "../../api/queryApi";
import { getUserBankBalances } from "../../api/queryApi";
import { addBankDetails, validateUpiPin } from "../../api/actionApi";
import { showToast } from "../../features/toast/toastSlice";
import Loader from "../../components/common/Loader";

// Predefined list of Indian banks (capitals)
const BANK_LIST = [
  "SBI", "HDFC", "ICICI", "AXIS", "KOTAK", "INDUSIND", "YES", "IDFC",
  "BANDHAN", "FEDERAL", "RBL", "CITI", "HSBC", "STANDARD CHARTERED", "DBS",
  "JANATA SAHAKARI", "COSMOS", "SARASWAT", "ABHYUDAYA", "TJSB", "NKGSB",
  "BHARAT CO-OP", "PUNJAB NATIONAL", "CANARA", "UNION", "INDIAN", "IDBI",
  "BANK OF BARODA", "BANK OF INDIA", "CENTRAL BANK", "UCO", "VIJAYA",
  "DENA", "ORIENTAL", "ALLAHABAD", "SYNDICATE", "CORPORATION", "ANDHRA",
  "INDIAN OVERSEAS",
];

// Mapping for IFSC prefix (first 4 chars)
const IFSC_PREFIX_MAP = {
  "SBI": "SBIN", "HDFC": "HDFC", "ICICI": "ICIC", "AXIS": "UTIB",
  "KOTAK": "KKBK", "INDUSIND": "INDB", "YES": "YESB", "IDFC": "IDFB",
  "BANDHAN": "BDBL", "FEDERAL": "FDRL", "RBL": "RBLB", "CITI": "CITI",
  "HSBC": "HSBC", "STANDARD CHARTERED": "SCBL", "DBS": "DBSS",
  "JANATA SAHAKARI": "JSBL", "COSMOS": "COSB", "SARASWAT": "SRSW",
  "ABHYUDAYA": "ABHY", "TJSB": "TJSB", "NKGSB": "NKGS",
  "BHARAT CO-OP": "BCBM", "PUNJAB NATIONAL": "PUNB", "CANARA": "CNRB",
  "UNION": "UBIN", "INDIAN": "IDIB", "IDBI": "IBKL",
  "BANK OF BARODA": "BARB", "BANK OF INDIA": "BKID", "CENTRAL BANK": "CBIN",
  "UCO": "UCBA", "VIJAYA": "VIJB", "DENA": "BKDN", "ORIENTAL": "ORBC",
  "ALLAHABAD": "ALLA", "SYNDICATE": "SYNB", "CORPORATION": "CORP",
  "ANDHRA": "ANDB", "INDIAN OVERSEAS": "IOBA",
};

// ---- Helper functions ----
const generateAccountNumber = (existing) => {
  let num;
  let attempts = 0;
  do {
    num = "";
    for (let i = 0; i < 12; i++) num += Math.floor(Math.random() * 10);
    attempts++;
  } while (existing.some((a) => a.accountNumber === num) && attempts < 50);
  return num;
};

const generateIfsc = (bank) => {
  const prefix = IFSC_PREFIX_MAP[bank] || "XXXX";
  return prefix + "0" + Math.random().toString(36).substring(2, 8).toUpperCase();
};

const generateUpiId = (userName, bank, existing) => {
  const base = userName.toLowerCase().replace(/\s/g, "");
  const bp = bank.substring(0, 3).toLowerCase();
  const existingForBank = existing.filter(
    (a) => a.bankName === bank && a.upiId?.startsWith(`${base}${bp}`)
  );
  const count = existingForBank.length;
  return count === 0 ? `${base}${bp}@upi` : `${base}${bp}${count + 1}@upi`;
};

// ---- Component ----
const BankAccounts = () => {
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const { user } = useAuth();
  const [accounts, setAccounts] = useState([]);
  const [balances, setBalances] = useState([]);
  const [loading, setLoading] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const [showForm, setShowForm] = useState(false);
  const [form, setForm] = useState({
    bankName: "",
    accountNumber: "",
    ifscCode: "",
    upiId: "",
    accountType: "SAVINGS",
  });

  // Modal state for PIN verification
  const [modalOpen, setModalOpen] = useState(false);
  const [selectedAccount, setSelectedAccount] = useState(null);
  const [pinInput, setPinInput] = useState("");
  const [verifying, setVerifying] = useState(false);
  const [verified, setVerified] = useState(false);
  const [viewBalance, setViewBalance] = useState(null);

  // Fetch accounts and balances
  useEffect(() => {
    if (user?.id) fetchData();
  }, [user]);

  const fetchData = async () => {
    setLoading(true);
    try {
      const accRes = await getUserBankDetails(user.id);
      setAccounts(accRes.data.data || []);
      const balRes = await getUserBankBalances(user.id);
      setBalances(balRes.data.data || []);
    } catch {
      dispatch(showToast({ message: "Failed to load accounts or balances", type: "error" }));
    } finally {
      setLoading(false);
    }
  };

  // Auto-generate fields when bank is selected
  useEffect(() => {
    if (showForm && form.bankName) {
      const accNum = generateAccountNumber(accounts);
      const ifsc = generateIfsc(form.bankName);
      const upi = generateUpiId(user?.name || "user", form.bankName, accounts);
      setForm((prev) => ({
        ...prev,
        accountNumber: accNum,
        ifscCode: ifsc,
        upiId: upi,
      }));
    }
  }, [form.bankName, showForm, user?.name, accounts]);

  const handleChange = (e) => setForm({ ...form, [e.target.name]: e.target.value });
  const handleBankChange = (e) => setForm({ ...form, bankName: e.target.value });

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!form.bankName || !form.accountNumber || !form.ifscCode || !form.upiId || !form.accountType) {
      dispatch(showToast({ message: "All fields are required", type: "error" }));
      return;
    }
    setSubmitting(true);
    const userId = user?.id || parseInt(localStorage.getItem("userId"), 10);
    if (!userId) {
      dispatch(showToast({ message: "User not authenticated", type: "error" }));
      setSubmitting(false);
      return;
    }

    try {
      const bankRes = await addBankDetails({
        userId,
        bankName: form.bankName,
        accountNumber: form.accountNumber,
        ifscCode: form.ifscCode,
        upiId: form.upiId,
        accountType: form.accountType,
      });
      if (bankRes.data.statuscode !== 200) throw new Error(bankRes.data.message || "Bank creation failed");
      const bankId = bankRes.data.data?.id;
      if (!bankId) throw new Error("Bank ID not returned");

      dispatch(showToast({ message: "Bank account created! Now set initial balance.", type: "success" }));
      navigate("/dashboard/create-balance", {
        state: { bankId, bankName: form.bankName },
      });
    } catch (err) {
      const msg = err.response?.data?.message || err.message || "Failed to create bank account";
      dispatch(showToast({ message: msg, type: "error" }));
    } finally {
      setSubmitting(false);
    }
  };

  const getBalance = (bankId) => {
    const b = balances.find((bal) => bal.userBankId === bankId);
    return b ? b.availableBalance : 0;
  };

  // ---------- Card Click & PIN Verification ----------
  const handleCardClick = (account) => {
    setSelectedAccount(account);
    setPinInput("");
    setVerified(false);
    setViewBalance(null);
    setModalOpen(true);
  };

  const handlePinSubmit = async (e) => {
    e.preventDefault();
    if (!pinInput || pinInput.length !== 6) {
      dispatch(showToast({ message: "Enter a valid 6-digit PIN", type: "error" }));
      return;
    }
    setVerifying(true);
    const userId = user?.id || parseInt(localStorage.getItem("userId"), 10);
    try {
      await validateUpiPin({
        userId,
        userBankId: selectedAccount.id,
        upiPin: pinInput, // must match backend field name
      });
      setVerified(true);
      setViewBalance(getBalance(selectedAccount.id));
      dispatch(showToast({ message: "PIN verified! Access granted.", type: "success" }));
    } catch (err) {
      const msg = err.response?.data?.message || "Invalid PIN. Please try again.";
      dispatch(showToast({ message: msg, type: "error" }));
    } finally {
      setVerifying(false);
    }
  };

  const closeModal = () => {
    setModalOpen(false);
    setSelectedAccount(null);
    setPinInput("");
    setVerified(false);
    setViewBalance(null);
  };

  if (loading) return <Loader />;

  return (
    <div className="max-w-4xl mx-auto">
      <div className="flex justify-between items-center mb-4">
        <div>
          <h2 className="text-2xl font-bold text-white">My Bank Accounts</h2>
          <p className="text-gray-400 text-sm mt-1">
            {accounts.length} {accounts.length === 1 ? "account" : "accounts"} linked
          </p>
        </div>
        <button
          onClick={() => setShowForm(!showForm)}
          className="px-4 py-2 bg-cyan-500 text-white rounded-xl hover:bg-cyan-600 transition"
        >
          {showForm ? "Cancel" : "Add Bank Account"}
        </button>
      </div>

      {showForm && (
        <div className="bg-white/5 backdrop-blur-sm rounded-xl p-6 border border-white/10 mb-6">
          <h3 className="text-xl font-semibold text-white mb-4">Add New Bank Account</h3>
          <form onSubmit={handleSubmit} className="space-y-4">
            <div>
              <label className="block text-gray-300 text-sm mb-1">Bank Name</label>
              <select
                name="bankName"
                value={form.bankName}
                onChange={handleBankChange}
                className="w-full bg-white/10 border border-white/20 rounded-xl px-4 py-3 text-white focus:ring-2 focus:ring-cyan-400 outline-none"
                required
              >
                <option value="" className="bg-gray-800 text-white">Select Bank</option>
                {BANK_LIST.map((bank) => (
                  <option key={bank} value={bank} className="bg-gray-800 text-white">
                    {bank}
                  </option>
                ))}
              </select>
            </div>

            <div>
              <label className="block text-gray-300 text-sm mb-1">Account Number (Auto)</label>
              <input
                type="text"
                name="accountNumber"
                value={form.accountNumber}
                readOnly
                className="w-full bg-white/10 border border-white/20 rounded-xl px-4 py-3 text-white/70 cursor-not-allowed"
              />
            </div>

            <div>
              <label className="block text-gray-300 text-sm mb-1">IFSC (Auto)</label>
              <input
                type="text"
                name="ifscCode"
                value={form.ifscCode}
                readOnly
                className="w-full bg-white/10 border border-white/20 rounded-xl px-4 py-3 text-white/70 cursor-not-allowed"
              />
            </div>

            <div>
              <label className="block text-gray-300 text-sm mb-1">UPI ID (Auto)</label>
              <input
                type="text"
                name="upiId"
                value={form.upiId}
                readOnly
                className="w-full bg-white/10 border border-white/20 rounded-xl px-4 py-3 text-white/70 cursor-not-allowed"
              />
            </div>

            <div>
              <label className="block text-gray-300 text-sm mb-1">Account Type</label>
              <select
                name="accountType"
                value={form.accountType}
                onChange={handleChange}
                className="w-full bg-white/10 border border-white/20 rounded-xl px-4 py-3 text-white focus:ring-2 focus:ring-cyan-400 outline-none"
                required
              >
                <option value="SAVINGS" className="bg-gray-800 text-white">SAVINGS</option>
                <option value="CURRENT" className="bg-gray-800 text-white">CURRENT</option>
                <option value="SALARY" className="bg-gray-800 text-white">SALARY</option>
                <option value="FIXED_DEPOSIT" className="bg-gray-800 text-white">FIXED DEPOSIT</option>
              </select>
            </div>

            <button
              type="submit"
              disabled={submitting}
              className="w-full bg-gradient-to-r from-cyan-500 to-blue-500 py-3 rounded-xl text-white font-semibold shadow-lg shadow-cyan-500/30 hover:shadow-cyan-500/50 transition disabled:opacity-70 flex items-center justify-center gap-2"
            >
              {submitting ? <Loader size="w-5 h-5" /> : "Add Bank Account"}
            </button>
          </form>
        </div>
      )}

      {/* Existing Accounts */}
      {accounts.length === 0 ? (
        <p className="text-gray-400">No bank accounts linked.</p>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          {accounts.map((acc) => (
            <div
              key={acc.id}
              onClick={() => handleCardClick(acc)}
              className="bg-white/5 backdrop-blur-sm rounded-xl p-4 border border-white/10 hover:bg-white/10 cursor-pointer transition"
            >
              <p className="text-white font-semibold">{acc.bankName}</p>
              <p className="text-gray-400 text-sm">Account: {acc.accountNumber}</p>
              <p className="text-gray-400 text-sm">IFSC: {acc.ifscCode}</p>
              <p className="text-gray-400 text-sm">UPI: {acc.upiId}</p>
              <p className="text-gray-400 text-sm">Type: {acc.accountType}</p>
              <p className="text-cyan-400 text-sm font-medium mt-1">
                Balance: ₹****
              </p>
              <p className="text-xs text-gray-500 mt-1">Click to view details</p>
            </div>
          ))}
        </div>
      )}

      {/* ---------- PIN Verification Modal ---------- */}
      {modalOpen && selectedAccount && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/60 backdrop-blur-sm p-4">
          <div className="bg-white/10 backdrop-blur-xl border border-white/20 rounded-2xl max-w-md w-full p-6 shadow-2xl">
            <div className="flex justify-between items-center mb-4">
              <h3 className="text-xl font-bold text-white">🔒 Verify PIN</h3>
              <button
                onClick={closeModal}
                className="text-gray-400 hover:text-white text-2xl"
              >
                ×
              </button>
            </div>

            {!verified ? (
              <form onSubmit={handlePinSubmit} className="space-y-4">
                <div>
                  <p className="text-gray-300 text-sm">
                    Enter your 6‑digit UPI PIN for <strong>{selectedAccount.bankName}</strong>
                  </p>
                  <p className="text-gray-400 text-xs mt-1">
                    Account: {selectedAccount.accountNumber}
                  </p>
                </div>
                <input
                  type="password"
                  maxLength="6"
                  pattern="\d{6}"
                  placeholder="Enter PIN"
                  value={pinInput}
                  onChange={(e) => setPinInput(e.target.value)}
                  className="w-full bg-white/10 border border-white/20 rounded-xl px-4 py-3 text-white placeholder-gray-400 focus:ring-2 focus:ring-cyan-400 outline-none"
                  required
                />
                <button
                  type="submit"
                  disabled={verifying}
                  className="w-full bg-gradient-to-r from-cyan-500 to-blue-500 py-3 rounded-xl text-white font-semibold shadow-lg shadow-cyan-500/30 hover:shadow-cyan-500/50 transition disabled:opacity-70 flex items-center justify-center gap-2"
                >
                  {verifying ? <Loader size="w-5 h-5" /> : "Verify PIN"}
                </button>
              </form>
            ) : (
              <div className="space-y-4">
                <div className="bg-green-500/20 border border-green-400/30 rounded-xl p-4 text-center">
                  <p className="text-green-300 font-semibold">✅ Verified</p>
                </div>
                <div className="bg-white/5 rounded-xl p-4 border border-white/10">
                  <p className="text-gray-300">Bank: <span className="text-white font-semibold">{selectedAccount.bankName}</span></p>
                  <p className="text-gray-300">Account: <span className="text-white">{selectedAccount.accountNumber}</span></p>
                  <p className="text-gray-300">IFSC: <span className="text-white">{selectedAccount.ifscCode}</span></p>
                  <p className="text-gray-300">UPI: <span className="text-white">{selectedAccount.upiId}</span></p>
                  <p className="text-gray-300">Type: <span className="text-white">{selectedAccount.accountType}</span></p>
                  <p className="text-cyan-400 text-lg font-bold mt-2">
                    Balance: ₹{viewBalance?.toFixed(2) || "0.00"}
                  </p>
                </div>
                <button
                  onClick={closeModal}
                  className="w-full bg-white/10 hover:bg-white/20 rounded-xl py-3 text-white font-semibold transition"
                >
                  Close
                </button>
              </div>
            )}
          </div>
        </div>
      )}
    </div>
  );
};

export default BankAccounts;