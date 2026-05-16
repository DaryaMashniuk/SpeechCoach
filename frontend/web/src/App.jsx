import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { ToastContainer } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import { AuthProvider, useAuth } from './context/AuthContext';
import { LangProvider } from './context/LangContext';
import Login from './pages/Login';
import Register from './pages/Register';
import CreatePresentation from './pages/CreatePresentation';
import Report from './pages/TrainingReport';
import MeetingReport from './pages/MeetingReport';
import Profile from './pages/Profile';
import NotFound from './pages/NotFound';
import PrivateRoute from './routes/PrivateRoute';

const AppRoutes = () => {
  const { isAuthenticated } = useAuth();
  return (
    <Routes>
      <Route path="/login" element={<Login />} />
      <Route path="/register" element={<Register />} />
      <Route path="/" element={isAuthenticated ? <Navigate to="/create" replace /> : <Navigate to="/login" replace />} />
      <Route path="/create" element={<PrivateRoute><CreatePresentation /></PrivateRoute>} />
      <Route path="/report/:jobId" element={<PrivateRoute><Report /></PrivateRoute>} />
      <Route path="/meeting/:jobId" element={<PrivateRoute><MeetingReport /></PrivateRoute>} />
      <Route path="/profile" element={<PrivateRoute><Profile /></PrivateRoute>} />
      <Route path="*" element={<NotFound />} />
    </Routes>
  );
};

function App() {
  return (
    <LangProvider>
      <AuthProvider>
        <BrowserRouter>
          <AppRoutes />
          <ToastContainer
            position="top-right" autoClose={3000}
            toastStyle={{ background: '#1e1b2e', border: '1px solid rgba(167,139,250,0.3)', color: '#e2e8f0' }}
          />
        </BrowserRouter>
      </AuthProvider>
    </LangProvider>
  );
}

export default App;