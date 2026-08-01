import React, { useState, useEffect } from 'react';
import { Container, Row, Col, Card, Spinner, Badge, Button, Alert } from 'react-bootstrap';
import { expenseAPI } from '../api/axiosConfig';
import { FaMoneyBillWave, FaChartLine, FaCalendar } from 'react-icons/fa';
import { toast } from 'react-toastify';

const Dashboard = () => {
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [stats, setStats] = useState({
    total: 0,
    monthlyTotal: 0,
  });
  const [recentExpenses, setRecentExpenses] = useState([]);

  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = async () => {
    try {
      setLoading(true);
      setError(null);
      
      const today = new Date();
      const monthStart = new Date(today.getFullYear(), today.getMonth(), 1);
      
      console.log('📊 Fetching dashboard data...');
      
      let total = 0;
      let monthlyTotal = 0;
      let recentData = [];

      try {
        const totalRes = await expenseAPI.getTotal(
          '2024-01-01',
          today.toISOString().split('T')[0]
        );
        total = totalRes.data?.total || 0;
        console.log('✅ Total expenses loaded:', total);
      } catch (err) {
        console.warn('⚠️ Could not load total expenses:', err.message);
      }

      try {
        const monthlyRes = await expenseAPI.getTotal(
          monthStart.toISOString().split('T')[0],
          today.toISOString().split('T')[0]
        );
        monthlyTotal = monthlyRes.data?.total || 0;
        console.log('✅ Monthly expenses loaded:', monthlyTotal);
      } catch (err) {
        console.warn('⚠️ Could not load monthly expenses:', err.message);
      }

      try {
        const recentRes = await expenseAPI.getAll();
        recentData = recentRes.data || [];
        console.log('✅ Recent expenses loaded:', recentData.length);
      } catch (err) {
        console.warn('⚠️ Could not load recent expenses:', err.message);
      }

      setStats({
        total: total,
        monthlyTotal: monthlyTotal,
      });
      setRecentExpenses(recentData.slice(0, 5));

    } catch (error) {
      console.error('❌ Dashboard error:', error);
      setError('Failed to load dashboard data. Please try again.');
      
      if (error.response?.status === 401 || error.response?.status === 403) {
        toast.error('Session expired. Please login again.');
        localStorage.removeItem('token');
        localStorage.removeItem('user');
        window.location.href = '/login';
      }
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <Container className="d-flex justify-content-center align-items-center" style={{ minHeight: '60vh' }}>
        <Spinner animation="border" variant="primary" />
      </Container>
    );
  }

  if (error) {
    return (
      <Container className="mt-4">
        <Alert variant="danger">
          <Alert.Heading>Error Loading Dashboard</Alert.Heading>
          <p>{error}</p>
          <hr />
          <div className="d-flex gap-2">
            <Button variant="outline-danger" onClick={fetchData}>
              Retry
            </Button>
          </div>
        </Alert>
      </Container>
    );
  }

  return (
    <Container>
      <h4 className="mb-4">📊 Dashboard</h4>
      
      <Row className="mb-4">
        <Col md={4}>
          <Card className="text-center">
            <Card.Body>
              <FaMoneyBillWave size={30} className="text-primary mb-2" />
              <h6>Total Expenses</h6>
              <h4 className="text-primary">₹{stats.total.toFixed(2)}</h4>
            </Card.Body>
          </Card>
        </Col>
        <Col md={4}>
          <Card className="text-center">
            <Card.Body>
              <FaCalendar size={30} className="text-success mb-2" />
              <h6>This Month</h6>
              <h4 className="text-success">₹{stats.monthlyTotal.toFixed(2)}</h4>
            </Card.Body>
          </Card>
        </Col>
        <Col md={4}>
          <Card className="text-center">
            <Card.Body>
              <FaChartLine size={30} className="text-warning mb-2" />
              <h6>Daily Average</h6>
              <h4 className="text-warning">₹{(stats.monthlyTotal / 30).toFixed(2)}</h4>
            </Card.Body>
          </Card>
        </Col>
      </Row>

      <Card>
        <Card.Body>
          <h6>Recent Expenses</h6>
          <div className="table-responsive">
            <table className="table table-sm">
              <thead>
                <tr>
                  <th>Date</th>
                  <th>Description</th>
                  <th>Category</th>
                  <th>Amount</th>
                </tr>
              </thead>
              <tbody>
                {recentExpenses.length === 0 ? (
                  <tr>
                    <td colSpan="4" className="text-center text-muted">
                      No expenses yet. Click "Add Expense" to get started!
                    </td>
                  </tr>
                ) : (
                  recentExpenses.map((expense) => (
                    <tr key={expense.id}>
                      <td>{new Date(expense.date).toLocaleDateString()}</td>
                      <td>{expense.description || '-'}</td>
                      <td>
                        <Badge style={{ backgroundColor: expense.categoryColor || '#6c757d' }}>
                          {expense.categoryIcon} {expense.categoryName}
                        </Badge>
                      </td>
                      <td className="text-danger">₹{expense.amount}</td>
                    </tr>
                  ))
                )}
              </tbody>
            </table>
          </div>
        </Card.Body>
      </Card>
    </Container>
  );
};

export default Dashboard;