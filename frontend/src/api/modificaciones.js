import api from './axiosConfig';

export const listarModificacionesPorVehiculo = (vehiculoId) =>
  api.get(`/modificaciones/vehiculo/${vehiculoId}`);
export const crearModificacion = (modificacion) =>
  api.post('/modificaciones/crear', modificacion);
export const eliminarModificacion = (id) =>
  api.delete(`/modificaciones/eliminar/${id}`);
