import api from './axiosConfig';

export const listarEmpleados = () => api.get('/empleados');
export const crearEmpleado = (empleado) => api.post('/empleados/crear', empleado);
export const actualizarEmpleado = (id, empleado) =>
  api.put(`/empleados/actualizar/${id}`, empleado);
export const eliminarEmpleado = (id) => api.delete(`/empleados/eliminar/${id}`);
export const obtenerEmpleadoPorId = (id) => api.get(`/empleados/${id}`);
