import React from 'react'
import ReactDOM from 'react-dom/client'
import App from './App' // ✅ This is fine in Vite, you don't need .jsx here
import './index.css'

ReactDOM.createRoot(document.getElementById('root')).render(
  <React.StrictMode>
    <App />
  </React.StrictMode>,
)