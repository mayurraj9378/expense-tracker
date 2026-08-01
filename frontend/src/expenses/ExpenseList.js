import React, { useState, useEffect } from 'react';
import { Table, Card, Button, Form, Row, Col, Badge, Alert, Spinner } from 'react-bootstrap';
import { FaTrash, FaSync } from 'react-icons/fa';
import { expenseAPI } from '../api/axiosConfig';
import { toast } from 'react-toastify';

const ExpenseList = () => {
  const [expenses, setExpenses] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [searchTerm, setSearchTerm] = useState('');

  useEffect(() => {
    fetchExpenses();
  }, []);

  const fetchExpenses = async () => {
    try {
      setLoading(true);
      setError(null);
      const response = await expenseAPI.getAll();
      setExpenses(response.data || []);
    } catch (error) {
      console.error('❌ Failed to fetch expenses:', error);
      setError('Failed to load expenses');
      toast.error('Failed to fetch expenses');
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async (id) => {
    if (window.confirm('Are you sure you want to delete this expense?')) {
      try {
        await expenseAPI.delete(id);
        setExpenses(expenses.filter(exp => exp.id !== id));
        toast.success('Expense deleted successfully');
      } catch (error) {
        toast.error('Failed to delete expense');
      }
    }
  };

  const filteredExpenses = expenses.filter(expense =>
    expense.description?.toLowerCase().includes(searchTerm.toLowerCase()) ||
    expense.categoryName?.toLowerCase().includes(searchTerm.toLowerCase())
  );

  if (loading) {
    return (
      <Card>
        <Card.Body className="text-center" style={{ minHeight: '300px' }}>
          <Spinner animation="border" variant="primary" className="mt-5" />
          <p className="mt-3 text-muted">Loading expenses...</p>
        </Card.Body>
      </Card>
    );
  }

  return (
    <Card>
      <Card.Body>
        <div className="d-flex justify-content-between align-items-center mb-4">
          <h4 className="mb-0">📊 Expense List</h4>
          <Button variant="outline-secondary" size="sm" onClick={fetchExpenses}>
            <FaSync /> Refresh
          </Button>
        </div>
        
        <Row className="mb-4">
          <Col md={6}>
            <Form.Group>
              <Form.Label>Search</Form.Label>
              <Form.Control
                type="text"
                placeholder="Search expenses..."
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
              />
            </Form.Group>
          </Col>
        </Row>

        {error && (
          <Alert variant="danger">
            <Alert.Heading>Error Loading Expenses</Alert.Heading>
            <p>{error}</p>
            <Button variant="outline-danger" size="sm" onClick={fetchExpenses}>Retry</Button>
          </Alert>
        )}

        {!error && filteredExpenses.length === 0 ? (
          <Alert variant="info">
            <Alert.Heading>No expenses found</Alert.Heading>
            <p>Start tracking your expenses by clicking "Add Expense" in the navigation bar.</p>
          </Alert>
        ) : (
          <div className="table-responsive">
            <Table striped bordered hover>
              <thead>
                <tr>
                  <th>Date</th>
                  <th>Description</th>
                  <th>Category</th>
                  <th>Amount</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                {filteredExpenses.map((expense) => (
                  <tr key={expense.id}>
                    <td>{new Date(expense.date).toLocaleDateString()}</td>
                    <td>{expense.description || '-'}</td>
                    <td>
                      <Badge style={{ backgroundColor: expense.categoryColor || '#6c757d' }}>
                        {expense.categoryIcon || '📌'} {expense.categoryName || 'Uncategorized'}
                      </Badge>
                    </td>
                    <td className="fw-bold text-danger">₹{expense.amount}</td>
                    <td>
                      <Button variant="outline-danger" size="sm" onClick={() => handleDelete(expense.id)}>
                        <FaTrash />
                      </Button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </Table>
          </div>
        )}
      </Card.Body>
    </Card>
  );
};

export default ExpenseList;