import create from 'zustand';
import { attendanceService } from '../services/api';

const useAttendanceStore = create((set) => ({
  attendance: [],
  todayAttendance: null,
  loading: false,
  error: null,

  logAttendance: async (data) => {
    set({ loading: true });
    try {
      await attendanceService.logAttendance(data);
      const response = await attendanceService.getMyAttendance();
      set({ attendance: response.data, loading: false });
    } catch (error) {
      set({ error: error.message, loading: false });
    }
  },

  logTodayAttendance: async (status) => {
    set({ loading: true });
    try {
      await attendanceService.logTodayAttendance(status);
      const response = await attendanceService.getMyAttendance();
      set({ attendance: response.data, loading: false });
    } catch (error) {
      set({ error: error.message, loading: false });
    }
  },

  fetchAttendance: async () => {
    set({ loading: true });
    try {
      const response = await attendanceService.getMyAttendance();
      set({ attendance: response.data, loading: false });
    } catch (error) {
      set({ error: error.message, loading: false });
    }
  },
}));

export default useAttendanceStore;