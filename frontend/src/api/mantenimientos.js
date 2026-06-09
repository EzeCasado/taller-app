import api from './axiosConfig';

export const listarMantenimientosPorVehiculo = (vehiculoId) =>
  api.get(`/mantenimientos/vehiculo/${vehiculoId}`);
export const crearMantenimiento = (mantenimiento) =>
  api.post('/mantenimientos/crear', mantenimiento);
export const eliminarMantenimiento = (id) =>
  api.delete(`/mantenimientos/eliminar/${id}`);
