import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { Container, Card, Form, Button, Row, Col, Alert, Spinner } from 'react-bootstrap';
import { expenseAPI, categoryAPI } from '../api/axiosConfig';
import { toast } from 'react-toastify';

const AddExpense = () => {
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);
  const [categoriesLoading, setCategoriesLoading] = useState(true);
  const [error, setError] = useState(null);
  const [categories, setCategories] = useState([]);
  const [formData, setFormData] = useState({
    amount: '',
    description: '',
    date: new Date().toISOString().split('T')[0],
    paymentMethod: 'CASH',
    categoryId: ''
  });

  useEffect(() => {
    fetchCategories();
  }, []);

  const fetchCategories = async () => {
    try {
      setCategoriesLoading(true);
      setError(null);
      const response = await categoryAPI.getAll();
      setCategories(response.data || []);
      if (response.data && response.data.length > 0) {
        setFormData(prev => ({ ...prev, categoryId: response.data[0].id }));
      }
    } catch (error) {
      console.error('❌ Failed to fetch categories:', error);
      setError('Failed to load categories. Please refresh the page.');
      toast.error('Failed to fetch categories');
    } finally {
      setCategoriesLoading(false);
    }
  };

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError(null);

    try {
      const expenseData = {
        ...formData,
        amount: parseFloat(formData.amount)
      };
      console.log('📤 Adding expense:', expenseData);
      
      await expenseAPI.create(expenseData);
      toast.success('Expense added successfully!');
      navigate('/expenses');
    } catch (error) {
      console.error('❌ Failed to add expense:', error);
      setError('Failed to add expense. Please try again.');
      toast.error('Failed to add expense');
    } finally {
      setLoading(false);
    }
  };

  if (categoriesLoading) {
    return (
      <Container className="d-flex justify-content-center align-items-center" style={{ minHeight: '60vh' }}>
        <Spinner animation="border" variant="primary" />
      </Container>
    );
  }

  return (
    <Container>
      <Card>
        <Card.Body>
          <h4 className="mb-4">💰 Add New Expense</h4>
          
          {error && (
            <Alert variant="danger" className="mb-3">
              {error}
            </Alert>
          )}

          {categories.length === 0 && !error && (
            <Alert variant="warning" className="mb-3">
              No categories found. Please go to Categories page and create default categories.
            </Alert>
          )}
          
          <Form onSubmit={handleSubmit}>
            <Row>
              <Col md={6}>
                <Form.Group className="mb-3">
                  <Form.Label>Amount (₹)</Form.Label>
                  <Form.Control
                    type="number"
                    name="amount"
                    placeholder="Enter amount"
                    value={formData.amount}
                    onChange={handleChange}
                    required
                    min="0.01"
                    step="0.01"
                    disabled={loading || categories.length === 0}
                  />
                </Form.Group>
              </Col>
              <Col md={6}>
                <Form.Group className="mb-3">
                  <Form.Label>Date</Form.Label>
                  <Form.Control
                    type="date"
                    name="date"
                    value={formData.date}
                    onChange={handleChange}
                    required
                    disabled={loading || categories.length === 0}
                  />
                </Form.Group>
              </Col>
            </Row>

            <Form.Group className="mb-3">
              <Form.Label>Description</Form.Label>
              <Form.Control
                type="text"
                name="description"
                placeholder="Enter description"
                value={formData.description}
                onChange={handleChange}
                disabled={loading || categories.length === 0}
              />
            </Form.Group>

            <Row>
              <Col md={6}>
                <Form.Group className="mb-3">
                  <Form.Label>Category</Form.Label>
                  <Form.Select
                    name="categoryId"
                    value={formData.categoryId}
                    onChange={handleChange}
                    required
                    disabled={loading || categories.length === 0}
                  >
                    {categories.map(category => (
                      <option key={category.id} value={category.id}>
                        {category.icon || '📌'} {category.name}
                      </option>
                    ))}
                  </Form.Select>
                </Form.Group>
              </Col>
              <Col md={6}>
                <Form.Group className="mb-3">
                  <Form.Label>Payment Method</Form.Label>
                  <Form.Select
                    name="paymentMethod"
                    value={formData.paymentMethod}
                    onChange={handleChange}
                    disabled={loading || categories.length === 0}
                  >
                    <option value="CASH">💵 Cash</option>
                    <option value="CARD">💳 Card</option>
                    <option value="UPI">📱 UPI</option>
                    <option value="BANK_TRANSFER">🏦 Bank Transfer</option>
                  </Form.Select>
                </Form.Group>
              </Col>
            </Row>

            <Button 
              variant="primary" 
              type="submit" 
              className="w-100"
              disabled={loading || categories.length === 0}
            >
              {loading ? <><Spinner animation="border" size="sm" className="me-2" /> Adding...</> : 'Add Expense'}
            </Button>
          </Form>
        </Card.Body>
      </Card>
    </Container>
  );
};

export default AddExpense;