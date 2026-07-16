// import { NavLink } from 'react-router-dom';
// import { useAuth } from '../../hooks/useAuth';
// // import {
// //   IconDashboard,
// //   IconSend,
// //   IconHistory,
// //   IconProfile,
// //   IconLogout,
// // } from './Icons'; // we'll define icons in the same file

// // Define simple SVG icons inline or import from a separate file.
// // For brevity, I'll keep them as simple components.

// export const IconDashboard = ({ className }) => (
//   <svg className={className} fill="none" stroke="currentColor" viewBox="0 0 24 24">
//     <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 6a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2H6a2 2 0 01-2-2V6zm0 10a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2H6a2 2 0 01-2-2v-2zm10-10a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2h-2a2 2 0 01-2-2V6zm0 10a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2h-2a2 2 0 01-2-2v-2z" />
//   </svg>
// );
// export const IconSend = ({ className }) => (
//   <svg className={className} fill="none" stroke="currentColor" viewBox="0 0 24 24">
//     <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 19l9 2-9-18-9 18 9-2zm0 0v-8" />
//   </svg>
// );
// export const IconHistory = ({ className }) => (
//   <svg className={className} fill="none" stroke="currentColor" viewBox="0 0 24 24">
//     <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
//   </svg>
// );
// export const IconProfile = ({ className }) => (
//   <svg className={className} fill="none" stroke="currentColor" viewBox="0 0 24 24">
//     <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
//   </svg>
// );
// export const IconLogout = ({ className }) => (
//   <svg className={className} fill="none" stroke="currentColor" viewBox="0 0 24 24">
//     <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1" />
//   </svg>
// );

// const Sidebar = () => {
//   const { logout } = useAuth();

//   const menuItems = [
//   { id: 'dashboard', label: 'Dashboard', icon: IconDashboard, path: '/dashboard' },
//   { id: 'send', label: 'Send Money', icon: IconSend, path: '/dashboard/send' },
//   { id: 'deposit', label: 'Deposit', icon: IconDeposit, path: '/dashboard/deposit' },
//   { id: 'withdraw', label: 'Withdraw', icon: IconWithdraw, path: '/dashboard/withdraw' },
//   { id: 'qr', label: 'QR Pay', icon: IconQr, path: '/dashboard/qr' },
//   { id: 'history', label: 'History', icon: IconHistory, path: '/dashboard/history' },
//   { id: 'profile', label: 'Profile', icon: IconProfile, path: '/dashboard/profile' },
// ];

//   return (
//     <aside className="w-20 md:w-64 bg-white/5 backdrop-blur-sm border-r border-white/10 p-4 flex flex-col gap-1 overflow-y-auto h-full">
//       <div className="flex items-center justify-center md:justify-start gap-2 mb-6">
//         <span className="text-3xl">💳</span>
//         <span className="hidden md:inline text-white font-bold text-xl">
//           <span className="text-cyan-400">Digital</span> Wallet
//         </span>
//       </div>
//       {menuItems.map((item) => {
//         const Icon = item.icon;
//         return (
//           <NavLink
//             key={item.id}
//             to={item.path}
//             className={({ isActive }) =>
//               `flex items-center gap-3 px-4 py-3 rounded-xl transition-all duration-200 ${
//                 isActive
//                   ? 'bg-gradient-to-r from-cyan-500/30 to-blue-500/30 border border-cyan-400/20 text-white shadow-lg'
//                   : 'text-gray-400 hover:text-white hover:bg-white/5'
//               }`
//             }
//           >
//             <Icon className="w-5 h-5 flex-shrink-0" />
//             <span className="hidden md:inline text-sm font-medium">{item.label}</span>
//           </NavLink>
//         );
//       })}
//       <button
//         onClick={logout}
//         className="flex items-center gap-3 px-4 py-3 rounded-xl text-gray-400 hover:text-white hover:bg-white/5 transition-colors mt-auto"
//       >
//         <IconLogout className="w-5 h-5 flex-shrink-0" />
//         <span className="hidden md:inline text-sm font-medium">Logout</span>
//       </button>
//     </aside>
//   );
// };

// export default Sidebar;




import { NavLink } from "react-router-dom";
import { useAuth } from "../../hooks/useAuth";

// Dashboard Icon
const IconDashboard = ({ className }) => (
  <svg className={className} fill="none" stroke="currentColor" viewBox="0 0 24 24">
    <path
      strokeLinecap="round"
      strokeLinejoin="round"
      strokeWidth={2}
      d="M4 6a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2H6a2 2 0 01-2-2V6zm0 10a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2H6a2 2 0 01-2-2v-2zm10-10a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2h-2a2 2 0 01-2-2V6zm0 10a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2h-2a2 2 0 01-2-2v-2"
    />
  </svg>
);

// Send Money Icon
const IconSend = ({ className }) => (
  <svg className={className} fill="none" stroke="currentColor" viewBox="0 0 24 24">
    <path
      strokeLinecap="round"
      strokeLinejoin="round"
      strokeWidth={2}
      d="M12 19l9 2-9-18-9 18 9-2zm0 0v-8"
    />
  </svg>
);

// Deposit Icon
const IconDeposit = ({ className }) => (
  <svg className={className} fill="none" stroke="currentColor" viewBox="0 0 24 24">
    <path
      strokeLinecap="round"
      strokeLinejoin="round"
      strokeWidth={2}
      d="M12 5v14m-7-7h14"
    />
  </svg>
);

// Withdraw Icon
const IconWithdraw = ({ className }) => (
  <svg className={className} fill="none" stroke="currentColor" viewBox="0 0 24 24">
    <path
      strokeLinecap="round"
      strokeLinejoin="round"
      strokeWidth={2}
      d="M19 12H5"
    />
  </svg>
);

// QR Icon
const IconQr = ({ className }) => (
  <svg className={className} fill="none" stroke="currentColor" viewBox="0 0 24 24">
    <path
      strokeLinecap="round"
      strokeLinejoin="round"
      strokeWidth={2}
      d="M4 4h6v6H4V4zm10 0h6v6h-6V4zM4 14h6v6H4v-6zm10 4h2v2h-2zm4-4h2v2h-2zm0 4h2v2h-2zm-4-4h2v2h-2z"
    />
  </svg>
);

// History Icon
const IconHistory = ({ className }) => (
  <svg className={className} fill="none" stroke="currentColor" viewBox="0 0 24 24">
    <path
      strokeLinecap="round"
      strokeLinejoin="round"
      strokeWidth={2}
      d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z"
    />
  </svg>
);

// Profile Icon
const IconProfile = ({ className }) => (
  <svg className={className} fill="none" stroke="currentColor" viewBox="0 0 24 24">
    <path
      strokeLinecap="round"
      strokeLinejoin="round"
      strokeWidth={2}
      d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"
    />
  </svg>
);

// Logout Icon
const IconLogout = ({ className }) => (
  <svg className={className} fill="none" stroke="currentColor" viewBox="0 0 24 24">
    <path
      strokeLinecap="round"
      strokeLinejoin="round"
      strokeWidth={2}
      d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1"
    />
  </svg>
);

  // Scheduled Transfer Icon
const IconSchedule = ({ className }) => (
  <svg className={className} fill="none" stroke="currentColor" viewBox="0 0 24 24">
    <path
      strokeLinecap="round"
      strokeLinejoin="round"
      strokeWidth={2}
      d="M8 7V3m8 4V3M5 11h14M6 5h12a1 1 0 011 1v13a1 1 0 01-1 1H6a1 1 0 01-1-1V6a1 1 0 011-1z"
    />
  </svg>
);

// Split Bill Create Icon
const IconSplit = ({ className }) => (
  <svg className={className} fill="none" stroke="currentColor" viewBox="0 0 24 24">
    <path
      strokeLinecap="round"
      strokeLinejoin="round"
      strokeWidth={2}
      d="M12 4v16m8-8H4"
    />
  </svg>
);

// Split Bill View Icon
const IconSplitView = ({ className }) => (
  <svg className={className} fill="none" stroke="currentColor" viewBox="0 0 24 24">
    <path
      strokeLinecap="round"
      strokeLinejoin="round"
      strokeWidth={2}
      d="M4 6h16M4 12h16M4 18h16"
    />
  </svg>
);

// UPI PIN Icon
const IconPin = ({ className }) => (
  <svg className={className} fill="none" stroke="currentColor" viewBox="0 0 24 24">
    <path
      strokeLinecap="round"
      strokeLinejoin="round"
      strokeWidth={2}
      d="M12 15v2m-4-6V9a4 4 0 118 0v2m-9 0h10a1 1 0 011 1v7a1 1 0 01-1 1H7a1 1 0 01-1-1v-7a1 1 0 011-1z"
    />
  </svg>
);

// Bank Account Icon
const IconBank = ({ className }) => (
  <svg className={className} fill="none" stroke="currentColor" viewBox="0 0 24 24">
    <path
      strokeLinecap="round"
      strokeLinejoin="round"
      strokeWidth={2}
      d="M3 10l9-6 9 6M5 10v8m4-8v8m4-8v8m4-8v8M3 20h18"
    />
  </svg>

);

const Sidebar = () => {
  const { logout } = useAuth();
const menuItems = [
  {
    id: "dashboard",
    label: "Dashboard",
    icon: IconDashboard,
    path: "/dashboard",
  },
  {
    id: "send",
    label: "Send Money",
    icon: IconSend,
    path: "/dashboard/send",
  },
  {
    id: "deposit",
    label: "Deposit",
    icon: IconDeposit,
    path: "/dashboard/deposit",
  },
  {
    id: "withdraw",
    label: "Withdraw",
    icon: IconWithdraw,
    path: "/dashboard/withdraw",
  },
  {
    id: "qr",
    label: "QR Pay",
    icon: IconQr,
    path: "/dashboard/qr",
  },
  {
    id: "scheduled",
    label: "Scheduled Transfers",
    icon: IconSchedule,
    path: "/dashboard/scheduled",
  },
  {
    id: "split-create",
    label: "Create Split Bill",
    icon: IconSplit,
    path: "/dashboard/split-create",
  },
  {
    id: "split-view",
    label: "Split Bills",
    icon: IconSplitView,
    path: "/dashboard/split-view",
  },
  {
    id: "upi-pin",
    label: "UPI PIN",
    icon: IconPin,
    path: "/dashboard/upi-pin",
  },
  {
    id: "accounts",
    label: "Bank Accounts",
    icon: IconBank,
    path: "/dashboard/accounts",
  },
  {
    id: "history",
    label: "History",
    icon: IconHistory,
    path: "/dashboard/history",
  },
  {
    id: "profile",
    label: "Profile",
    icon: IconProfile,
    path: "/dashboard/profile",
  },
];

  return (
    <aside className="w-20 md:w-64 bg-white/5 backdrop-blur-sm border-r border-white/10 p-4 flex flex-col gap-1 overflow-y-auto h-full">
      <div className="flex items-center justify-center md:justify-start gap-2 mb-6">
        <span className="text-3xl">💳</span>
        <span className="hidden md:inline text-white font-bold text-xl">
          <span className="text-cyan-400">Digital</span> Wallet
        </span>
      </div>

      {menuItems.map((item) => {
        const Icon = item.icon;

        return (
          <NavLink
            key={item.id}
            to={item.path}
            className={({ isActive }) =>
              `flex items-center gap-3 px-4 py-3 rounded-xl transition-all duration-200 ${
                isActive
                  ? "bg-gradient-to-r from-cyan-500/30 to-blue-500/30 border border-cyan-400/20 text-white shadow-lg"
                  : "text-gray-400 hover:text-white hover:bg-white/5"
              }`
            }
          >
            <Icon className="w-5 h-5 flex-shrink-0" />
            <span className="hidden md:inline text-sm font-medium">
              {item.label}
            </span>
          </NavLink>
        );
      })}

      <button
        onClick={logout}
        className="flex items-center gap-3 px-4 py-3 rounded-xl text-gray-400 hover:text-white hover:bg-white/5 transition-colors mt-auto"
      >
        <IconLogout className="w-5 h-5 flex-shrink-0" />
        <span className="hidden md:inline text-sm font-medium">Logout</span>
      </button>
    </aside>
  );
};

export default Sidebar;