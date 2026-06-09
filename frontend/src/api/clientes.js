import api from './axiosConfig';

export const listarClientes = () => api.get('/clientes/listar');
export const crearCliente = (cliente) => api.post('/clientes/crear', cliente);
export const actualizarCliente = (id, cliente) => api.put(`/clientes/actualizar/${id}`, cliente);
export const eliminarCliente = (id) => api.get(`/clientes/eliminar/${id}`);
export const obtenerClientePorId = (id) => api.get(`/clientes/cliente/${id}`);
