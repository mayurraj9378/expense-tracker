import React from 'react';
import { Navbar as BootstrapNavbar, Nav, Container, Button } from 'react-bootstrap';
import { Link, useNavigate } from 'react-router-dom';
import { FaHome, FaList, FaPlus, FaTags, FaSignOutAlt, FaUser } from 'react-icons/fa';

const Navbar = () => {
  const navigate = useNavigate();
  
  const userData = localStorage.getItem('user');
  const user = userData ? JSON.parse(userData) : null;
  const token = localStorage.getItem('token');

  const handleLogout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    navigate('/login');
  };

  if (!token) {
    return null;
  }

  return (
    <BootstrapNavbar bg="dark" variant="dark" expand="lg" className="mb-4">
      <Container>
        <BootstrapNavbar.Brand as={Link} to="/" className="fw-bold">
          <span className="me-2">💰</span> Expense Tracker
        </BootstrapNavbar.Brand>
        <BootstrapNavbar.Toggle />
        <BootstrapNavbar.Collapse>
          <Nav className="me-auto">
            <Nav.Link as={Link} to="/"><FaHome /> Dashboard</Nav.Link>
            <Nav.Link as={Link} to="/expenses"><FaList /> Expenses</Nav.Link>
            <Nav.Link as={Link} to="/add-expense"><FaPlus /> Add Expense</Nav.Link>
            <Nav.Link as={Link} to="/categories"><FaTags /> Categories</Nav.Link>
          </Nav>
          <Nav>
            <span className="text-light me-3"><FaUser /> {user?.fullName || user?.username}</span>
            <Button variant="outline-light" size="sm" onClick={handleLogout}>
              <FaSignOutAlt /> Logout
            </Button>
          </Nav>
        </BootstrapNavbar.Collapse>
      </Container>
    </BootstrapNavbar>
  );
};

export default Navbar;