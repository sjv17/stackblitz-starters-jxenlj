import axios from 'axios';

const api = axios.create({
  baseURL: '/api',
  headers: {
    'Content-Type': 'application/json',
  },
});

// Add request interceptor to include CSRF token
api.interceptors.request.use((config) => {
  const token = document.querySelector('meta[name="_csrf"]')?.getAttribute('content');
  const header = document.querySelector('meta[name="_csrf_header"]')?.getAttribute('content');
  
  if (token && header) {
    config.headers[header] = token;
  }
  return config;
});

export const taskService = {
  getAllTasks: () => api.get('/tasks'),
  getTaskById: (id) => api.get(`/tasks/${id}`),
  createTask: (data) => api.post('/tasks/create', data),
  updateTask: (id, data) => api.post(`/tasks/edit/${id}`, data),
  deleteTask: (id) => api.get(`/tasks/delete/${id}`),
  completeTask: (id) => api.post(`/tasks/${id}/complete`),
};

export const attendanceService = {
  logAttendance: (data) => api.post('/attendance/log', data),
  logTodayAttendance: (status) => api.post('/attendance/log-today', { status }),
  getMyAttendance: () => api.get('/attendance/my-history'),
  getAttendanceCalendar: (month) => api.get('/attendance/calendar', { params: { month } }),
};

export const payrollService = {
  generatePayroll: (data) => api.post('/payroll/generate', data),
  processPayment: (id) => api.post(`/payroll/process/${id}`),
  getMyPayroll: () => api.get('/payroll/my-payroll'),
  getPendingPayrolls: () => api.get('/payroll/manage'),
};

export const userService = {
  getCurrentUser: () => api.get('/profile'),
  updateProfile: (data) => api.post('/profile/update', data),
};

export default api;