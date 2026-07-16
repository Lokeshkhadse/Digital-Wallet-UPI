// import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
// import { useSelector } from 'react-redux';
// import Home from './pages/Home';
// import Login from './pages/Login';
// import Dashboard from './pages/Dashboard/Dashboard';
// import Toast from './components/common/Toast';
// import NotFound from './pages/NotFound';

// // Protected route wrapper
// const ProtectedRoute = ({ children }) => {
//   const isAuthenticated = useSelector((state) => state.auth.isAuthenticated);
//   return isAuthenticated ? children : <Navigate to="/login" />;
// };

// function App() {
  
//   return (
//     <BrowserRouter>
//       <Toast /> {/* global toast */}
//       <Routes>
//         <Route path="/" element={<Home />} />
//         <Route path="/login" element={<Login />} />
//         <Route
//           path="/dashboard/*"
//           element={
//             <ProtectedRoute>
//               <Dashboard />
//             </ProtectedRoute>
//           }
//         />
//         <Route path="*" element={<NotFound />} />
//       </Routes>
//     </BrowserRouter>
//   );
// }

// export default App;


import { useEffect } from 'react';   // 👈 import useEffect
import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import { useDispatch, useSelector } from "react-redux";
import Home from "./pages/Home";
import Login from "./pages/Login";
import Dashboard from "./pages/Dashboard/Dashboard";
import Toast from "./components/common/Toast";
import NotFound from "./pages/NotFound";
import { restoreUser } from './features/auth/authThunks';

// Protected route wrapper
const ProtectedRoute = ({ children }) => {
  const isAuthenticated = useSelector((state) => state.auth.isAuthenticated);
  return isAuthenticated ? children : <Navigate to="/login" />;
};

function App() {
  const dispatch = useDispatch();
  const { isAuthenticated, user, token } = useSelector((state) => state.auth);

  // Restore user on refresh if token exists but user is null
  useEffect(() => {
    if (isAuthenticated && !user && token) {
      dispatch(restoreUser());
    }
  }, [isAuthenticated, user, token, dispatch]);

  return (
    <BrowserRouter>
      <Toast />
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/login" element={<Login />} />
        <Route
          path="/dashboard/*"
          element={
            <ProtectedRoute>
              <Dashboard />
            </ProtectedRoute>
          }
        />
        <Route path="*" element={<NotFound />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;