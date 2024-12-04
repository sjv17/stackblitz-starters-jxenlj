import create from 'zustand';
import { payrollService } from '../services/api';

const usePayrollStore = create((set) => ({
  payrolls: [],
  pendingPayrolls: [],
  loading: false,
  error: null,

  generatePayroll: async (data) => {
    set({ loading: true });
    try {
      await payrollService.generatePayroll(data);
      const response = await payrollService.getPendingPayrolls();
      set({ pendingPayrolls: response.data, loading: false });
    } catch (error) {
      set({ error: error.message, loading: false });
    }
  },

  processPayment: async (id) => {
    set({ loading: true });
    try {
      await payrollService.processPayment(id);
      const response = await payrollService.getPendingPayrolls();
      set({ pendingPayrolls: response.data, loading: false });
    } catch (error) {
      set({ error: error.message, loading: false });
    }
  },

  fetchPayrolls: async () => {
    set({ loading: true });
    try {
      const response = await payrollService.getMyPayroll();
      set({ payrolls: response.data, loading: false });
    } catch (error) {
      set({ error: error.message, loading: false });
    }
  },

  fetchPendingPayrolls: async () => {
    set({ loading: true });
    try {
      const response = await payrollService.getPendingPayrolls();
      set({ pendingPayrolls: response.data, loading: false });
    } catch (error) {
      set({ error: error.message, loading: false });
    }
  },
}));

export default usePayrollStore;