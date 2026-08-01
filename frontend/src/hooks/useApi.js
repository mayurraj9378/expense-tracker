import { useState, useEffect } from 'react';
import { toast } from 'react-toastify';

export const useApi = (apiFunction, options = {}) => {
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const { showSuccessToast = false, showErrorToast = true } = options;

  useEffect(() => {
    const fetchData = async () => {
      try {
        setLoading(true);
        const response = await apiFunction();
        setData(response.data);
        setError(null);
        if (showSuccessToast) {
          toast.success('Data loaded successfully');
        }
      } catch (err) {
        setError(err);
        if (showErrorToast) {
          toast.error(err.response?.data?.message || 'An error occurred');
        }
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, [apiFunction, showSuccessToast, showErrorToast]);

  return { data, loading, error, refetch: () => fetchData() };
};

export const useMutation = (apiFunction, options = {}) => {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const { showSuccessToast = true, showErrorToast = true } = options;

  const mutate = async (...args) => {
    try {
      setLoading(true);
      setError(null);
      const response = await apiFunction(...args);
      if (showSuccessToast) {
        toast.success(response.data?.message || 'Operation successful');
      }
      return { success: true, data: response.data };
    } catch (err) {
      setError(err);
      if (showErrorToast) {
        toast.error(err.response?.data?.message || 'Operation failed');
      }
      return { success: false, error: err.response?.data?.message };
    } finally {
      setLoading(false);
    }
  };

  return { mutate, loading, error };
};