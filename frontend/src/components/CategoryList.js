import React, { useState, useEffect } from 'react';
import { Card, Button, Row, Col, Badge, Alert, Spinner, Form } from 'react-bootstrap';
import { FaTrash, FaPlus } from 'react-icons/fa';
import { categoryAPI } from '../api/axiosConfig';
import { toast } from 'react-toastify';

const CategoryList = () => {
  const [categories, setCategories] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showForm, setShowForm] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const [newCategory, setNewCategory] = useState({ name: '', icon: '📌', color: '#6c757d' });

  useEffect(() => {
    fetchCategories();
  }, []);

  const fetchCategories = async () => {
    try {
      setLoading(true);
      const response = await categoryAPI.getAll();
      setCategories(response.data);
    } catch (error) {
      toast.error('Failed to fetch categories');
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async (id) => {
    if (window.confirm('Are you sure you want to delete this category?')) {
      try {
        await categoryAPI.delete(id);
        setCategories(categories.filter(cat => cat.id !== id));
        toast.success('Category deleted successfully');
      } catch (error) {
        toast.error('Failed to delete category');
      }
    }
  };

  const handleAddCategory = async (e) => {
    e.preventDefault();
    if (!newCategory.name.trim()) {
      toast.error('Category name is required');
      return;
    }
    try {
      setSubmitting(true);
      await categoryAPI.create(newCategory);
      toast.success('Category added successfully');
      setNewCategory({ name: '', icon: '📌', color: '#6c757d' });
      setShowForm(false);
      fetchCategories();
    } catch (error) {
      toast.error(error.response?.data?.message || 'Failed to add category');
    } finally {
      setSubmitting(false);
    }
  };

  const handleCreateDefaults = async () => {
    if (window.confirm('This will create default categories. Continue?')) {
      try {
        await categoryAPI.createDefaults();
        toast.success('Default categories created!');
        fetchCategories();
      } catch (error) {
        toast.error('Failed to create default categories');
      }
    }
  };

  if (loading) {
    return (
      <div className="d-flex justify-content-center align-items-center" style={{ minHeight: '60vh' }}>
        <Spinner animation="border" variant="primary" />
      </div>
    );
  }

  return (
    <Card>
      <Card.Body>
        <div className="d-flex justify-content-between align-items-center mb-4">
          <h4 className="mb-0">🏷️ Categories</h4>
          <div>
            <Button variant="primary" className="me-2" onClick={() => setShowForm(!showForm)}>
              <FaPlus className="me-1" /> Add Category
            </Button>
            <Button variant="outline-secondary" onClick={handleCreateDefaults}>
              Create Defaults
            </Button>
          </div>
        </div>

        {showForm && (
          <Card className="mb-4">
            <Card.Body>
              <Form onSubmit={handleAddCategory}>
                <Row className="align-items-end">
                  <Col md={5}>
                    <Form.Group>
                      <Form.Label>Name</Form.Label>
                      <Form.Control
                        type="text"
                        placeholder="e.g. Pet Care"
                        value={newCategory.name}
                        onChange={(e) => setNewCategory({ ...newCategory, name: e.target.value })}
                        required
                        disabled={submitting}
                      />
                    </Form.Group>
                  </Col>
                  <Col md={3}>
                    <Form.Group>
                      <Form.Label>Icon (emoji)</Form.Label>
                      <Form.Control
                        type="text"
                        value={newCategory.icon}
                        onChange={(e) => setNewCategory({ ...newCategory, icon: e.target.value })}
                        disabled={submitting}
                      />
                    </Form.Group>
                  </Col>
                  <Col md={2}>
                    <Form.Group>
                      <Form.Label>Color</Form.Label>
                      <Form.Control
                        type="color"
                        value={newCategory.color}
                        onChange={(e) => setNewCategory({ ...newCategory, color: e.target.value })}
                        disabled={submitting}
                      />
                    </Form.Group>
                  </Col>
                  <Col md={2}>
                    <Button type="submit" variant="success" className="w-100" disabled={submitting}>
                      {submitting ? <Spinner animation="border" size="sm" /> : 'Save'}
                    </Button>
                  </Col>
                </Row>
              </Form>
            </Card.Body>
          </Card>
        )}

        {categories.length === 0 ? (
          <Alert variant="info">
            No categories found. Click "Create Defaults" to add default categories.
          </Alert>
        ) : (
          <Row>
            {categories.map(category => (
              <Col md={4} lg={3} key={category.id} className="mb-3">
                <Card className="h-100">
                  <Card.Body>
                    <div className="d-flex justify-content-between align-items-start">
                      <div>
                        <div style={{ fontSize: '2rem' }}>{category.icon || '📌'}</div>
                        <h6 className="mt-2">{category.name}</h6>
                        {category.isDefault && (
                          <Badge bg="info" className="mt-1">Default</Badge>
                        )}
                      </div>
                      <Button 
                        variant="outline-danger" 
                        size="sm"
                        onClick={() => handleDelete(category.id)}
                        disabled={category.isDefault}
                      >
                        <FaTrash />
                      </Button>
                    </div>
                    <div className="mt-2">
                      <Badge style={{ backgroundColor: category.color || '#6c757d' }}>
                        Color
                      </Badge>
                    </div>
                  </Card.Body>
                </Card>
              </Col>
            ))}
          </Row>
        )}
      </Card.Body>
    </Card>
  );
};

export default CategoryList;