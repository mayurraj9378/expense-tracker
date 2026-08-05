import React, { createContext, useState, useContext, useEffect } from 'react';
import { authAPI } from '../api/axiosConfig';

const AuthContext = createContext();

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);
  const [isAuthenticated, setIsAuthenticated] = useState(false);

  const checkAuth = () => {
    const token = localStorage.getItem('token');
    const userData = localStorage.getItem('user');
    
    if (token && userData) {
      try {
        setUser(JSON.parse(userData));
        setIsAuthenticated(true);
        return true;
      } catch (e) {
        console.error('Auth error:', e);
        localStorage.removeItem('token');
        localStorage.removeItem('user');
      }
    }
    
    setIsAuthenticated(false);
    return false;
  };

  useEffect(() => {
    checkAuth();
    setLoading(false);
  }, []);

  const login = async (credentials) => {
    try {
      const response = await authAPI.login(credentials);
      const data = response.data;

      if (!data.token) {
        throw new Error('No token received');
      }

      const cleanToken = data.token.replace(/\s/g, '');
      localStorage.setItem('token', cleanToken);
      
      const userData = {
        username: data.username,
        fullName: data.fullName,
        email: data.email,
        userId: data.userId
      };
      localStorage.setItem('user', JSON.stringify(userData));
      
      setUser(userData);
      setIsAuthenticated(true);
      
      return { success: true };
      
    } catch (error) {
      const message = error.response?.data?.message || error.message;
      return { success: false, error: message };
    }
  };

  const logout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    setUser(null);
    setIsAuthenticated(false);
  };

  return (
    <AuthContext.Provider value={{ user, loading, isAuthenticated, login, logout, checkAuth }}>
      {children}
    </AuthContext.Provider>
  );
};