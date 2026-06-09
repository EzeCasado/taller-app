import { useState, useEffect, useCallback } from 'react';
import { Plus, Edit2, Trash2, AlertTriangle } from 'lucide-react';
import DataTable from '../components/ui/DataTable';
import Modal from '../components/ui/Modal';
import StatusBadge from '../components/ui/StatusBadge';
import Spinner from '../components/ui/Spinner';
import { listarEmpleados, crearEmpleado, actualizarEmpleado, eliminarEmpleado } from '../api/empleados';
import { useAuth } from '../context/AuthContext';
import { saveCredentials } from '../api/axiosConfig';

const initialState = { nombre: '', usuario: '', contrasenia: '' };

export default function Empleados({ toast }) {
  const { user, login } = useAuth();
  const [empleados, setEmpleados] = useState([]);
  const [loading, setLoading] = useState(true);
  const [modalOpen, setModalOpen] = useState(false);
  const [editTarget, setEditTarget] = useState(null);
  const [confirmDelete, setConfirmDelete] = useState(null);
  const [form, setForm] = useState(initialState);
  const [errors, setErrors] = useState({});
  const [saving, setSaving] = useState(false);

  const fetchEmpleados = useCallback(async () => {
    setLoading(true);
    try {
      const res = await listarEmpleados();
      setEmpleados(res.data);
    } catch (err) {
      toast?.({ type: 'error', title: 'Error', message: err.message });
    } finally {
      setLoading(false);
    }
  }, [toast]);

  useEffect(() => { fetchEmpleados(); }, [fetchEmpleados]);

  const openCreate = () => { setEditTarget(null); setForm(initialState); setErrors({}); setModalOpen(true); };
  const openEdit = (emp) => {
    setEditTarget(emp);
    setForm({ nombre: emp.nombre, usuario: emp.usuario, contrasenia: '' });
    setErrors({});
    setModalOpen(true);
  };
  const closeModal = () => { setModalOpen(false); setEditTarget(null); };

  const validate = () => {
    const errs = {};
    if (!form.nombre.trim()) errs.nombre = 'El nombre es requerido';
    if (!form.usuario.trim()) errs.usuario = 'El usuario es requerido';
    if (!editTarget && !form.contrasenia.trim()) errs.contrasenia = 'La contraseña es requerida';
    return errs;
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setForm((f) => ({ ...f, [name]: value }));
    if (errors[name]) setErrors((e) => ({ ...e, [name]: undefined }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    const errs = validate();
    if (Object.keys(errs).length > 0) { setErrors(errs); return; }

    setSaving(true);
    try {
      if (editTarget) {
        await actualizarEmpleado(editTarget.id, { ...form, activo: true });
        toast?.({ type: 'success', title: 'Empleado actualizado', message: form.nombre });
        
        // Si el usuario actualizó sus PROPIOS datos, actualizamos el sessionStorage
        // para que las próximas peticiones a la API no fallen con 401
        if (user && user.usuario === editTarget.usuario) {
          const encoded = sessionStorage.getItem('taller_credentials');
          if (encoded) {
            const decoded = atob(encoded);
            const oldPass = decoded.substring(decoded.indexOf(':') + 1);
            const newPass = form.contrasenia.trim() ? form.contrasenia : oldPass;
            saveCredentials(form.usuario, newPass);
          }
          login(form.usuario, form.nombre);
        }
      } else {
        await crearEmpleado({ ...form, activo: true });
        toast?.({ type: 'success', title: 'Empleado registrado', message: form.nombre });
      }
      closeModal();
      fetchEmpleados();
    } catch (err) {
      toast?.({ type: 'error', title: 'Error', message: err.message });
    } finally {
      setSaving(false);
    }
  };

  const handleDelete = async () => {
    if (!confirmDelete) return;
    try {
      await eliminarEmpleado(confirmDelete.id);
      toast?.({ type: 'success', title: 'Empleado eliminado', message: confirmDelete.nombre });
      setConfirmDelete(null);
      fetchEmpleados();
    } catch (err) {
      toast?.({ type: 'error', title: 'Error', message: err.message });
    }
  };

  const columns = [
    {
      key: 'nombre',
      label: 'Nombre',
      sortable: true,
      render: (val) => (
        <div style={{ display: 'flex', alignItems: 'center', gap: 10 }}>
          <div
            style={{
              width: 32,
              height: 32,
              borderRadius: '50%',
              background: 'linear-gradient(135deg, var(--accent), #a855f7)',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              fontSize: 12,
              fontWeight: 700,
              color: 'white',
              flexShrink: 0,
            }}
          >
            {val?.[0]?.toUpperCase()}
          </div>
          <span style={{ fontWeight: 600 }}>{val}</span>
        </div>
      ),
    },
    {
      key: 'usuario',
      label: 'Usuario',
      render: (val) => (
        <span className="font-mono" style={{ color: 'var(--text-secondary)' }}>@{val}</span>
      ),
    },
    {
      key: 'activo',
      label: 'Estado',
      render: (val) => <StatusBadge activo={val} />,
    },
  ];

  return (
    <div>
      <div className="page-header">
        <div>
          <h1>Empleados</h1>
          <p>Gestioná el equipo del taller</p>
        </div>
        <div className="page-header-actions">
          <button id="btn-nuevo-empleado" className="btn btn-primary" onClick={openCreate}>
            <Plus size={16} /> Nuevo Empleado
          </button>
        </div>
      </div>

      {loading ? (
        <Spinner label="Cargando empleados..." />
      ) : (
        <DataTable
          title="Empleados"
          columns={columns}
          data={empleados}
          searchKeys={['nombre', 'usuario']}
          emptyText="No hay empleados registrados"
          actions={(row) => (
            <>
              <button
                id={`btn-editar-empleado-${row.id}`}
                className="btn btn-secondary btn-sm btn-icon"
                onClick={() => openEdit(row)}
                aria-label={`Editar ${row.nombre}`}
              >
                <Edit2 size={14} />
              </button>
              <button
                id={`btn-eliminar-empleado-${row.id}`}
                className="btn btn-danger btn-sm btn-icon"
                onClick={() => setConfirmDelete(row)}
                aria-label={`Eliminar ${row.nombre}`}
              >
                <Trash2 size={14} />
              </button>
            </>
          )}
        />
      )}

      {/* Create / Edit Modal */}
      <Modal
        open={modalOpen}
        onClose={closeModal}
        title={editTarget ? 'Editar Empleado' : 'Nuevo Empleado'}
        size="sm"
        footer={
          <>
            <button className="btn btn-secondary" onClick={closeModal} disabled={saving}>Cancelar</button>
            <button
              id="btn-guardar-empleado"
              className="btn btn-primary"
              disabled={saving}
              onClick={() => document.getElementById('empleado-form')?.requestSubmit()}
            >
              {saving && <span className="spinner" style={{ width: 14, height: 14 }} />}
              {editTarget ? 'Guardar cambios' : 'Registrar'}
            </button>
          </>
        }
      >
        <form id="empleado-form" onSubmit={handleSubmit} noValidate>
          <div className="form-group">
            <label className="form-label" htmlFor="emp-nombre">
              Nombre completo <span className="form-required">*</span>
            </label>
            <input
              id="emp-nombre"
              name="nombre"
              className={`form-input${errors.nombre ? ' error' : ''}`}
              placeholder="Ej: Carlos López"
              value={form.nombre}
              onChange={handleChange}
              autoFocus
            />
            {errors.nombre && <div className="form-error-msg">{errors.nombre}</div>}
          </div>

          <div className="form-group">
            <label className="form-label" htmlFor="emp-usuario">
              Usuario <span className="form-required">*</span>
            </label>
            <input
              id="emp-usuario"
              name="usuario"
              className={`form-input${errors.usuario ? ' error' : ''}`}
              placeholder="Ej: carlos.lopez"
              value={form.usuario}
              onChange={handleChange}
            />
            {errors.usuario && <div className="form-error-msg">{errors.usuario}</div>}
          </div>

          <div className="form-group">
            <label className="form-label" htmlFor="emp-pass">
              Contraseña {!editTarget && <span className="form-required">*</span>}
              {editTarget && <span style={{ color: 'var(--text-muted)', fontSize: 11 }}> (dejar vacío para no cambiar)</span>}
            </label>
            <input
              id="emp-pass"
              name="contrasenia"
              type="password"
              className={`form-input${errors.contrasenia ? ' error' : ''}`}
              placeholder={editTarget ? 'Nueva contraseña (opcional)' : 'Contraseña'}
              value={form.contrasenia}
              onChange={handleChange}
              autoComplete="new-password"
            />
            {errors.contrasenia && <div className="form-error-msg">{errors.contrasenia}</div>}
          </div>
        </form>
      </Modal>

      {/* Confirm Delete */}
      <Modal
        open={!!confirmDelete}
        onClose={() => setConfirmDelete(null)}
        title="Eliminar empleado"
        size="sm"
        footer={
          <>
            <button className="btn btn-secondary" onClick={() => setConfirmDelete(null)}>Cancelar</button>
            <button id="btn-confirmar-eliminar-empleado" className="btn btn-danger" onClick={handleDelete}>Eliminar</button>
          </>
        }
      >
        <div className="confirm-dialog">
          <AlertTriangle size={40} />
          <h3>¿Eliminar al empleado?</h3>
          <p>Se eliminará a <strong>{confirmDelete?.nombre}</strong> del sistema.</p>
        </div>
      </Modal>
    </div>
  );
}
