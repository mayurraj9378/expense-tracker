import React from 'react';
import ReactDOM from 'react-dom/client';
import 'bootstrap/dist/css/bootstrap.min.css';   // 👈 add this line — it was missing
import './index.css';
import App from './App';

// Clean token on app startup (in case of stray whitespace)
const token = localStorage.getItem('token');
if (token) {
  const cleanToken = token.replace(/\s/g, '');
  if (cleanToken !== token) {
    localStorage.setItem('token', cleanToken);
  }
}

const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(
  <React.StrictMode>
    <App />
  </React.StrictMode>
);