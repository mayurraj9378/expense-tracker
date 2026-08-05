import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { Container, Form, Button, Card, Alert, Spinner } from 'react-bootstrap';
import { toast } from 'react-toastify';
import { authAPI } from '../api/axiosConfig';

const Register = () => {
  const [userData, setUserData] = useState({
    username: '',
    email: '',
    password: '',
    fullName: ''
  });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');

    try {
      console.log('📤 Register attempt for:', userData.username);

      const response = await authAPI.register(userData);
      console.log('📊 Response:', response.data);

      toast.success('Registration successful! Please login.');
      navigate('/login');

    } catch (error) {
      const message = error.response?.data?.message || error.message || 'Registration failed';
      console.error('❌ Register error:', message);
      setError(message);
      toast.error(message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <Container className="d-flex justify-content-center align-items-center" style={{ minHeight: '100vh' }}>
      <Card style={{ width: '450px', boxShadow: '0 4px 6px rgba(0,0,0,0.1)' }}>
        <Card.Body>
          <h3 className="text-center mb-4">Create Account</h3>
          
          {error && <Alert variant="danger">{error}</Alert>}
          
          <Form onSubmit={handleSubmit}>
            <Form.Group className="mb-3">
              <Form.Label>Full Name</Form.Label>
              <Form.Control
                type="text"
                name="fullName"
                placeholder="Enter full name"
                value={userData.fullName}
                onChange={(e) => setUserData({ ...userData, fullName: e.target.value })}
                required
                disabled={loading}
              />
            </Form.Group>

            <Form.Group className="mb-3">
              <Form.Label>Username</Form.Label>
              <Form.Control
                type="text"
                name="username"
                placeholder="Enter username"
                value={userData.username}
                onChange={(e) => setUserData({ ...userData, username: e.target.value })}
                required
                disabled={loading}
              />
            </Form.Group>

            <Form.Group className="mb-3">
              <Form.Label>Email</Form.Label>
              <Form.Control
                type="email"
                name="email"
                placeholder="Enter email"
                value={userData.email}
                onChange={(e) => setUserData({ ...userData, email: e.target.value })}
                required
                disabled={loading}
              />
            </Form.Group>

            <Form.Group className="mb-3">
              <Form.Label>Password</Form.Label>
              <Form.Control
                type="password"
                name="password"
                placeholder="Enter password (min 6 characters)"
                value={userData.password}
                onChange={(e) => setUserData({ ...userData, password: e.target.value })}
                minLength="6"
                required
                disabled={loading}
              />
            </Form.Group>

            <Button variant="primary" type="submit" className="w-100" disabled={loading}>
              {loading ? <><Spinner animation="border" size="sm" className="me-2" /> Registering...</> : 'Register'}
            </Button>
          </Form>

          <div className="text-center mt-3">
            <Link to="/login">Already have an account? Login</Link>
          </div>
        </Card.Body>
      </Card>
    </Container>
  );
};

export default Register;