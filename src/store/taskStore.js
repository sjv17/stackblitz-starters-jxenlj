import create from 'zustand';
import { taskService } from '../services/api';

const useTaskStore = create((set) => ({
  tasks: [],
  loading: false,
  error: null,
  
  fetchTasks: async () => {
    set({ loading: true });
    try {
      const response = await taskService.getAllTasks();
      set({ tasks: response.data, loading: false });
    } catch (error) {
      set({ error: error.message, loading: false });
    }
  },

  createTask: async (taskData) => {
    set({ loading: true });
    try {
      await taskService.createTask(taskData);
      const response = await taskService.getAllTasks();
      set({ tasks: response.data, loading: false });
    } catch (error) {
      set({ error: error.message, loading: false });
    }
  },

  updateTask: async (id, taskData) => {
    set({ loading: true });
    try {
      await taskService.updateTask(id, taskData);
      const response = await taskService.getAllTasks();
      set({ tasks: response.data, loading: false });
    } catch (error) {
      set({ error: error.message, loading: false });
    }
  },

  deleteTask: async (id) => {
    set({ loading: true });
    try {
      await taskService.deleteTask(id);
      const response = await taskService.getAllTasks();
      set({ tasks: response.data, loading: false });
    } catch (error) {
      set({ error: error.message, loading: false });
    }
  },

  completeTask: async (id) => {
    set({ loading: true });
    try {
      await taskService.completeTask(id);
      const response = await taskService.getAllTasks();
      set({ tasks: response.data, loading: false });
    } catch (error) {
      set({ error: error.message, loading: false });
    }
  },
}));

export default useTaskStore;