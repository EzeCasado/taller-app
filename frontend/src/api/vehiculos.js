import api from './axiosConfig';

export const listarVehiculos = () => api.get('/vehiculos/listar');
export const crearVehiculo = (vehiculo) => api.post('/vehiculos/crear', vehiculo);
export const eliminarVehiculo = (id) => api.delete(`/vehiculos/eliminar/${id}`);
export const obtenerGastosVehiculo = (id) => api.get(`/vehiculos/${id}/gastos`);
