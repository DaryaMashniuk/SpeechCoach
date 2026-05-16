import React from 'react';
import { Navigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { Loader } from '../components/ui/Loader';

const PrivateRoute = ({ children }) => {
  const { isAuthenticated, loading } = useAuth();
  if (loading) return <div className="page-loader"><Loader size="lg" /></div>;
  return isAuthenticated ? children : <Navigate to="/login" replace />;
};

export default PrivateRoute;