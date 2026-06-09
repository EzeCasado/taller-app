import { useState, useEffect, useCallback, useRef } from 'react';
import { Plus, Edit2, Trash2, Phone, Mail, AlertTriangle } from 'lucide-react';
import DataTable from '../components/ui/DataTable';
import Modal from '../components/ui/Modal';
import StatusBadge from '../components/ui/StatusBadge';
import ClienteForm from '../components/forms/ClienteForm';
import Spinner from '../components/ui/Spinner';
import { listarClientes, crearCliente, actualizarCliente, eliminarCliente } from '../api/clientes';

export default function Clientes({ toast }) {
  const [clientes, setClientes] = useState([]);
  const [loading, setLoading] = useState(true);
  const [modalOpen, setModalOpen] = useState(false);
  const [editTarget, setEditTarget] = useState(null);
  const [confirmDelete, setConfirmDelete] = useState(null);
  const [saving, setSaving] = useState(false);
  const formRef = useRef(null);

  const fetchClientes = useCallback(async () => {
    setLoading(true);
    try {
      const res = await listarClientes();
      setClientes(res.data);
    } catch (err) {
      toast?.({ type: 'error', title: 'Error', message: err.message });
    } finally {
      setLoading(false);
    }
  }, [toast]);

  useEffect(() => { fetchClientes(); }, [fetchClientes]);

  const openCreate = () => { setEditTarget(null); setModalOpen(true); };
  const openEdit = (cliente) => { setEditTarget(cliente); setModalOpen(true); };
  const closeModal = () => { setModalOpen(false); setEditTarget(null); };

  const handleSubmit = async (data) => {
    setSaving(true);
    try {
      if (editTarget) {
        await actualizarCliente(editTarget.id, data);
        toast?.({ type: 'success', title: 'Cliente actualizado', message: `${data.nombre} ${data.apellido}` });
      } else {
        await crearCliente(data);
        toast?.({ type: 'success', title: 'Cliente registrado', message: `${data.nombre} ${data.apellido}` });
      }
      closeModal();
      fetchClientes();
    } catch (err) {
      toast?.({ type: 'error', title: 'Error al guardar', message: err.message });
    } finally {
      setSaving(false);
    }
  };

  const handleDelete = async () => {
    if (!confirmDelete) return;
    try {
      await eliminarCliente(confirmDelete.id);
      toast?.({ type: 'success', title: 'Cliente dado de baja', message: `${confirmDelete.nombre} ${confirmDelete.apellido}` });
      setConfirmDelete(null);
      fetchClientes();
    } catch (err) {
      toast?.({ type: 'error', title: 'Error', message: err.message });
    }
  };

  const columns = [
    {
      key: 'nombre',
      label: 'Nombre',
      sortable: true,
      render: (val, row) => (
        <div>
          <div style={{ fontWeight: 600 }}>{val} {row.apellido}</div>
        </div>
      ),
    },
    {
      key: 'telefono',
      label: 'Teléfono',
      render: (val) => val ? (
        <span style={{ display: 'flex', alignItems: 'center', gap: 5, color: 'var(--text-secondary)' }}>
          <Phone size={12} /> {val}
        </span>
      ) : '—',
    },
    {
      key: 'email',
      label: 'Email',
      render: (val) => val ? (
        <span style={{ display: 'flex', alignItems: 'center', gap: 5, color: 'var(--text-secondary)' }}>
          <Mail size={12} /> {val}
        </span>
      ) : '—',
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
          <h1>Clientes</h1>
          <p>Gestioná los clientes registrados en el taller</p>
        </div>
        <div className="page-header-actions">
          <button id="btn-nuevo-cliente" className="btn btn-primary" onClick={openCreate}>
            <Plus size={16} /> Nuevo Cliente
          </button>
        </div>
      </div>

      {loading ? (
        <Spinner label="Cargando clientes..." />
      ) : (
        <DataTable
          title="Clientes"
          columns={columns}
          data={clientes}
          searchKeys={['nombre', 'apellido', 'email', 'telefono']}
          emptyText="No hay clientes registrados"
          actions={(row) => (
            <>
              <button
                id={`btn-editar-cliente-${row.id}`}
                className="btn btn-secondary btn-sm btn-icon"
                onClick={() => openEdit(row)}
                title="Editar cliente"
                aria-label={`Editar ${row.nombre}`}
              >
                <Edit2 size={14} />
              </button>
              <button
                id={`btn-eliminar-cliente-${row.id}`}
                className="btn btn-danger btn-sm btn-icon"
                onClick={() => setConfirmDelete(row)}
                title="Dar de baja"
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
        title={editTarget ? 'Editar Cliente' : 'Nuevo Cliente'}
        footer={
          <>
            <button className="btn btn-secondary" onClick={closeModal} disabled={saving}>
              Cancelar
            </button>
            <button
              id="btn-guardar-cliente"
              className="btn btn-primary"
              disabled={saving}
              onClick={() => {
                document.getElementById('cliente-form')?.requestSubmit();
              }}
            >
              {saving ? <span className="spinner" style={{ width: 16, height: 16 }} /> : null}
              {editTarget ? 'Guardar cambios' : 'Registrar cliente'}
            </button>
          </>
        }
      >
        <ClienteForm
          onSubmit={handleSubmit}
          loading={saving}
          initial={editTarget ?? {}}
        />
      </Modal>

      {/* Confirm Delete Modal */}
      <Modal
        open={!!confirmDelete}
        onClose={() => setConfirmDelete(null)}
        title="Confirmar baja"
        size="sm"
        footer={
          <>
            <button className="btn btn-secondary" onClick={() => setConfirmDelete(null)}>
              Cancelar
            </button>
            <button id="btn-confirmar-eliminar-cliente" className="btn btn-danger" onClick={handleDelete}>
              Dar de baja
            </button>
          </>
        }
      >
        <div className="confirm-dialog">
          <AlertTriangle size={40} />
          <h3>¿Dar de baja al cliente?</h3>
          <p>
            Se desactivará a <strong>{confirmDelete?.nombre} {confirmDelete?.apellido}</strong>.
            Esta acción puede revertirse.
          </p>
        </div>
      </Modal>
    </div>
  );
}
