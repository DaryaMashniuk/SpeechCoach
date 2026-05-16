import { createContext, useContext, useEffect, useState, useCallback } from 'react';
import { login as apiLogin, register as apiRegister } from '../api/auth';

const AuthContext = createContext();

const decodeJwt = (token) => {
  try {
    const payload = JSON.parse(atob(token.split('.')[1].replace(/-/g, '+').replace(/_/g, '/')));
    return { id: payload.id, username: payload.sub, role: payload.role, exp: payload.exp };
  } catch { return null; }
};

const isExpired = (token) => {
  try {
    const { exp } = JSON.parse(atob(token.split('.')[1].replace(/-/g, '+').replace(/_/g, '/')));
    return exp * 1000 < Date.now();
  } catch { return true; }
};

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);
  const [authError, setAuthError] = useState(null);

  useEffect(() => {
    const token = localStorage.getItem('auth_token');
    if (token && !isExpired(token)) {
      const userData = decodeJwt(token);
      if (userData) setUser({ ...userData, token });
    } else {
      localStorage.removeItem('auth_token');
      localStorage.removeItem('refresh_token');
    }
    setLoading(false);
  }, []);

  const login = useCallback(async (credentials) => {
    setAuthError(null);
    try {
      const { token, refreshToken, expiresAt } = await apiLogin(credentials);
      if (!token) throw new Error('No token received');
      console.log('Access Token:', token);
      console.log('Refresh Token:', refreshToken);
      const userData = decodeJwt(token);
      if (!userData) throw new Error('Invalid token');
      localStorage.setItem('auth_token', token);
      
      if (refreshToken) localStorage.setItem('refresh_token', refreshToken);
      if (expiresAt) localStorage.setItem('token_expires_at', expiresAt);
      setUser({ ...userData, token });
      return { success: true };
    } catch (error) {
      const msg = error.response?.data?.message || error.response?.data?.error || error.message || 'Login failed';
      setAuthError(msg);
      return { success: false, error: msg };
    }
  }, []);

  const register = useCallback(async (userData) => {
    try {
      const response = await apiRegister(userData);
      const msg = typeof response === 'string' ? response : response?.message || 'Registered successfully';
      return { success: true, message: msg };
    } catch (error) {
      const msg = error.response?.data?.message || error.response?.data?.error || error.message || 'Registration failed';
      return { success: false, error: msg };
    }
  }, []);

  const logout = useCallback(() => {
    localStorage.removeItem('auth_token');
    localStorage.removeItem('refresh_token');
    localStorage.removeItem('token_expires_at');
    setUser(null);
  }, []);

  const clearError = useCallback(() => setAuthError(null), []);

  return (
    <AuthContext.Provider value={{
      user, loading, authError, clearError, login, register, logout,
      userId: user?.id,
      isAuthenticated: !!user,
    }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error('useAuth must be within AuthProvider');
  return ctx;
};