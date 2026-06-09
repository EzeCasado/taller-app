import { useState, useEffect, useCallback } from 'react';
import { Plus, Trash2, Eye, Car, AlertTriangle } from 'lucide-react';
import { useNavigate } from 'react-router-dom';
import DataTable from '../components/ui/DataTable';
import Modal from '../components/ui/Modal';
import StatusBadge from '../components/ui/StatusBadge';
import VehiculoForm from '../components/forms/VehiculoForm';
import Spinner from '../components/ui/Spinner';
import { listarVehiculos, crearVehiculo, eliminarVehiculo } from '../api/vehiculos';
import { listarClientes } from '../api/clientes';

export default function Vehiculos({ toast }) {
  const navigate = useNavigate();
  const [vehiculos, setVehiculos] = useState([]);
  const [clientes, setClientes] = useState([]);
  const [loading, setLoading] = useState(true);
  const [modalOpen, setModalOpen] = useState(false);
  const [confirmDelete, setConfirmDelete] = useState(null);
  const [saving, setSaving] = useState(false);

  const fetchData = useCallback(async () => {
    setLoading(true);
    try {
      const [vRes, cRes] = await Promise.all([listarVehiculos(), listarClientes()]);
      setVehiculos(vRes.data);
      setClientes(cRes.data);
    } catch (err) {
      toast?.({ type: 'error', title: 'Error', message: err.message });
    } finally {
      setLoading(false);
    }
  }, [toast]);

  useEffect(() => { fetchData(); }, [fetchData]);

  const handleSubmit = async (data) => {
    setSaving(true);
    try {
      await crearVehiculo(data);
      toast?.({ type: 'success', title: 'Vehículo registrado', message: `${data.patente} — ${data.marca} ${data.modelo}` });
      setModalOpen(false);
      fetchData();
    } catch (err) {
      toast?.({ type: 'error', title: 'Error al registrar', message: err.message });
    } finally {
      setSaving(false);
    }
  };

  const handleDelete = async () => {
    if (!confirmDelete) return;
    try {
      await eliminarVehiculo(confirmDelete.id);
      toast?.({ type: 'success', title: 'Vehículo dado de baja', message: confirmDelete.patente });
      setConfirmDelete(null);
      fetchData();
    } catch (err) {
      toast?.({ type: 'error', title: 'Error', message: err.message });
    }
  };

  const columns = [
    {
      key: 'patente',
      label: 'Patente',
      sortable: true,
      render: (val) => (
        <span
          className="font-mono"
          style={{
            letterSpacing: 2,
            fontSize: 13,
            color: 'var(--accent)',
            background: 'var(--accent-light)',
            padding: '2px 8px',
            borderRadius: 'var(--radius-sm)',
          }}
        >
          {val}
        </span>
      ),
    },
    { key: 'marca', label: 'Marca', sortable: true },
    { key: 'modelo', label: 'Modelo', sortable: true },
    { key: 'anio', label: 'Año', sortable: true },
    {
      key: 'kilometraje',
      label: 'Kilometraje',
      render: (val) => val ? `${val.toLocaleString('es-AR')} km` : '—',
    },
    {
      key: 'cliente',
      label: 'Propietario',
      render: (val) => val ? `${val.nombre} ${val.apellido}` : '—',
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
          <h1>Vehículos</h1>
          <p>Administrá la flota de vehículos del taller</p>
        </div>
        <div className="page-header-actions">
          <button id="btn-nuevo-vehiculo" className="btn btn-primary" onClick={() => setModalOpen(true)}>
            <Plus size={16} /> Nuevo Vehículo
          </button>
        </div>
      </div>

      {loading ? (
        <Spinner label="Cargando vehículos..." />
      ) : (
        <DataTable
          title="Vehículos"
          columns={columns}
          data={vehiculos}
          searchKeys={['patente', 'marca', 'modelo']}
          emptyText="No hay vehículos registrados"
          actions={(row) => (
            <>
              <button
                id={`btn-detalle-vehiculo-${row.id}`}
                className="btn btn-secondary btn-sm btn-icon"
                onClick={() => navigate(`/vehiculos/${row.id}`)}
                title="Ver detalle"
                aria-label={`Ver detalle de ${row.patente}`}
              >
                <Eye size={14} />
              </button>
              <button
                id={`btn-eliminar-vehiculo-${row.id}`}
                className="btn btn-danger btn-sm btn-icon"
                onClick={() => setConfirmDelete(row)}
                title="Dar de baja"
                aria-label={`Eliminar ${row.patente}`}
              >
                <Trash2 size={14} />
              </button>
            </>
          )}
        />
      )}

      {/* Create Modal */}
      <Modal
        open={modalOpen}
        onClose={() => setModalOpen(false)}
        title="Registrar Vehículo"
        size="lg"
        footer={
          <>
            <button className="btn btn-secondary" onClick={() => setModalOpen(false)} disabled={saving}>
              Cancelar
            </button>
            <button
              id="btn-guardar-vehiculo"
              className="btn btn-primary"
              disabled={saving}
              onClick={() => document.getElementById('vehiculo-form')?.requestSubmit()}
            >
              {saving && <span className="spinner" style={{ width: 14, height: 14 }} />}
              Registrar vehículo
            </button>
          </>
        }
      >
        <VehiculoForm onSubmit={handleSubmit} loading={saving} clientes={clientes} />
      </Modal>

      {/* Confirm Delete */}
      <Modal
        open={!!confirmDelete}
        onClose={() => setConfirmDelete(null)}
        title="Confirmar baja"
        size="sm"
        footer={
          <>
            <button className="btn btn-secondary" onClick={() => setConfirmDelete(null)}>Cancelar</button>
            <button id="btn-confirmar-eliminar-vehiculo" className="btn btn-danger" onClick={handleDelete}>
              Dar de baja
            </button>
          </>
        }
      >
        <div className="confirm-dialog">
          <AlertTriangle size={40} />
          <h3>¿Dar de baja el vehículo?</h3>
          <p>
            Se desactivará el vehículo <strong>{confirmDelete?.patente}</strong> — {confirmDelete?.marca} {confirmDelete?.modelo}.
          </p>
        </div>
      </Modal>
    </div>
  );
}
