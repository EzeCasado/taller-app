import { useState, useEffect, useCallback } from 'react';
import { Plus, Wrench, Zap, Trash2, AlertTriangle } from 'lucide-react';
import Timeline from '../components/ui/Timeline';
import Modal from '../components/ui/Modal';
import DataTable from '../components/ui/DataTable';
import StatusBadge from '../components/ui/StatusBadge';
import MantenimientoForm from '../components/forms/MantenimientoForm';
import ModificacionForm from '../components/forms/ModificacionForm';
import Spinner from '../components/ui/Spinner';
import { listarVehiculos } from '../api/vehiculos';
import { listarMantenimientosPorVehiculo, crearMantenimiento, eliminarMantenimiento } from '../api/mantenimientos';
import { listarModificacionesPorVehiculo, crearModificacion, eliminarModificacion } from '../api/modificaciones';
import { listarEmpleados } from '../api/empleados';

const formatCurrency = (val) =>
  new Intl.NumberFormat('es-AR', { style: 'currency', currency: 'ARS' }).format(val ?? 0);

const formatDate = (d) =>
  d ? new Date(d).toLocaleDateString('es-AR', { day: '2-digit', month: 'short', year: 'numeric' }) : '—';

export default function Mantenimientos({ toast }) {
  const [vehiculos, setVehiculos] = useState([]);
  const [empleados, setEmpleados] = useState([]);
  const [selectedVehiculo, setSelectedVehiculo] = useState('');
  const [mantenimientos, setMantenimientos] = useState([]);
  const [modificaciones, setModificaciones] = useState([]);
  const [loading, setLoading] = useState(false);
  const [baseLoading, setBaseLoading] = useState(true);
  const [mantModal, setMantModal] = useState(false);
  const [modModal, setModModal] = useState(false);
  const [confirmDelete, setConfirmDelete] = useState(null);
  const [saving, setSaving] = useState(false);
  const [viewMode, setViewMode] = useState('timeline'); // 'timeline' | 'table'

  const fetchBase = useCallback(async () => {
    setBaseLoading(true);
    try {
      const [vRes, eRes] = await Promise.all([listarVehiculos(), listarEmpleados()]);
      setVehiculos(vRes.data);
      setEmpleados(eRes.data);
    } catch (err) {
      toast?.({ type: 'error', title: 'Error', message: err.message });
    } finally {
      setBaseLoading(false);
    }
  }, [toast]);

  useEffect(() => { fetchBase(); }, [fetchBase]);

  const fetchHistorial = useCallback(async (vehiculoId) => {
    if (!vehiculoId) { setMantenimientos([]); setModificaciones([]); return; }
    setLoading(true);
    try {
      const [mRes, modRes] = await Promise.all([
        listarMantenimientosPorVehiculo(vehiculoId),
        listarModificacionesPorVehiculo(vehiculoId),
      ]);
      setMantenimientos(mRes.data);
      setModificaciones(modRes.data);
    } catch (err) {
      toast?.({ type: 'error', title: 'Error', message: err.message });
    } finally {
      setLoading(false);
    }
  }, [toast]);

  useEffect(() => { fetchHistorial(selectedVehiculo); }, [selectedVehiculo, fetchHistorial]);

  const handleCrearMantenimiento = async (data) => {
    setSaving(true);
    try {
      await crearMantenimiento(data);
      toast?.({ type: 'success', title: 'Mantenimiento registrado' });
      setMantModal(false);
      fetchHistorial(selectedVehiculo || data.vehiculo.id);
      setSelectedVehiculo(String(data.vehiculo.id));
    } catch (err) {
      toast?.({ type: 'error', title: 'Error', message: err.message });
    } finally {
      setSaving(false);
    }
  };

  const handleCrearModificacion = async (data) => {
    setSaving(true);
    try {
      await crearModificacion(data);
      toast?.({ type: 'success', title: 'Modificación registrada' });
      setModModal(false);
      fetchHistorial(selectedVehiculo || data.vehiculo.id);
      setSelectedVehiculo(String(data.vehiculo.id));
    } catch (err) {
      toast?.({ type: 'error', title: 'Error', message: err.message });
    } finally {
      setSaving(false);
    }
  };

  const handleDelete = async () => {
    if (!confirmDelete) return;
    try {
      if (confirmDelete.type === 'mantenimiento') {
        await eliminarMantenimiento(confirmDelete.id);
      } else {
        await eliminarModificacion(confirmDelete.id);
      }
      toast?.({ type: 'success', title: 'Registro eliminado' });
      setConfirmDelete(null);
      fetchHistorial(selectedVehiculo);
    } catch (err) {
      toast?.({ type: 'error', title: 'Error', message: err.message });
    }
  };

  const tableItems = [
    ...mantenimientos.map((m) => ({ ...m, _type: 'Mantenimiento', _nombre: m.descripcion })),
    ...modificaciones.map((m) => ({ ...m, _type: 'Modificación', _nombre: m.nombre })),
  ].sort((a, b) => new Date(b.fecha) - new Date(a.fecha));

  const tableColumns = [
    { key: 'fecha', label: 'Fecha', sortable: true, render: (v) => formatDate(v) },
    {
      key: '_type',
      label: 'Tipo',
      render: (v) => (
        <span className={`badge ${v === 'Mantenimiento' ? 'badge-info' : 'badge-warning'}`}>
          {v === 'Mantenimiento' ? <Wrench size={10} /> : <Zap size={10} />} {v}
        </span>
      ),
    },
    { key: '_nombre', label: 'Descripción', sortable: true },
    { key: 'costo', label: 'Costo', sortable: true, render: (v) => formatCurrency(v) },
    {
      key: 'empleado',
      label: 'Mecánico',
      render: (v) => v?.nombre ?? '—',
    },
  ];

  if (baseLoading) return <Spinner label="Cargando..." />;

  const selectedVehicleObj = vehiculos.find((v) => String(v.id) === selectedVehiculo);

  return (
    <div>
      <div className="page-header">
        <div>
          <h1>Mantenimientos y Modificaciones</h1>
          <p>Historial de trabajos por vehículo</p>
        </div>
        <div className="page-header-actions">
          <button id="btn-nuevo-mantenimiento-page" className="btn btn-secondary" onClick={() => setMantModal(true)}>
            <Wrench size={15} /> Mantenimiento
          </button>
          <button id="btn-nueva-modificacion-page" className="btn btn-primary" onClick={() => setModModal(true)}>
            <Zap size={15} /> Modificación
          </button>
        </div>
      </div>

      {/* Vehicle Filter */}
      <div className="card" style={{ marginBottom: 20 }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: 14, flexWrap: 'wrap' }}>
          <label className="form-label" htmlFor="filter-vehiculo" style={{ margin: 0, whiteSpace: 'nowrap' }}>
            Filtrar por vehículo:
          </label>
          <select
            id="filter-vehiculo"
            className="form-select"
            style={{ maxWidth: 320 }}
            value={selectedVehiculo}
            onChange={(e) => setSelectedVehiculo(e.target.value)}
          >
            <option value="">— Seleccioná un vehículo —</option>
            {vehiculos.map((v) => (
              <option key={v.id} value={v.id}>
                {v.patente} — {v.marca} {v.modelo}
              </option>
            ))}
          </select>

          {selectedVehicleObj && (
            <div style={{ display: 'flex', gap: 8, marginLeft: 'auto' }}>
              <button
                className={`btn btn-sm ${viewMode === 'timeline' ? 'btn-primary' : 'btn-secondary'}`}
                onClick={() => setViewMode('timeline')}
                id="btn-vista-timeline"
              >
                Timeline
              </button>
              <button
                className={`btn btn-sm ${viewMode === 'table' ? 'btn-primary' : 'btn-secondary'}`}
                onClick={() => setViewMode('table')}
                id="btn-vista-tabla"
              >
                Tabla
              </button>
            </div>
          )}
        </div>
      </div>

      {/* Content */}
      {!selectedVehiculo ? (
        <div className="card">
          <div className="table-empty">
            <Wrench size={40} />
            <p>Seleccioná un vehículo para ver su historial</p>
          </div>
        </div>
      ) : loading ? (
        <Spinner label="Cargando historial..." />
      ) : viewMode === 'timeline' ? (
        <div className="card">
          <div className="section-header" style={{ marginBottom: 20 }}>
            <span className="section-title">
              <Wrench size={16} /> Historial — {selectedVehicleObj?.patente}
            </span>
            <span className="badge badge-neutral">
              {mantenimientos.length + modificaciones.length} registros
            </span>
          </div>
          <Timeline mantenimientos={mantenimientos} modificaciones={modificaciones} />
        </div>
      ) : (
        <DataTable
          title={`Historial — ${selectedVehicleObj?.patente}`}
          columns={tableColumns}
          data={tableItems}
          searchKeys={['_nombre', '_type']}
          emptyText="Sin registros para este vehículo"
          actions={(row) => (
            <button
              id={`btn-eliminar-registro-${row._type}-${row.id}`}
              className="btn btn-danger btn-sm btn-icon"
              onClick={() => setConfirmDelete({ id: row.id, type: row._type === 'Mantenimiento' ? 'mantenimiento' : 'modificacion' })}
              aria-label="Eliminar registro"
            >
              <Trash2 size={14} />
            </button>
          )}
        />
      )}

      {/* Mantenimiento Modal */}
      <Modal
        open={mantModal}
        onClose={() => setMantModal(false)}
        title="Nuevo Mantenimiento"
        size="lg"
        footer={
          <>
            <button className="btn btn-secondary" onClick={() => setMantModal(false)} disabled={saving}>Cancelar</button>
            <button
              id="btn-guardar-mant-modal"
              className="btn btn-primary"
              disabled={saving}
              onClick={() => document.getElementById('mantenimiento-form')?.requestSubmit()}
            >
              {saving && <span className="spinner" style={{ width: 14, height: 14 }} />}
              Guardar
            </button>
          </>
        }
      >
        <MantenimientoForm
          onSubmit={handleCrearMantenimiento}
          loading={saving}
          vehiculos={vehiculos}
          empleados={empleados}
          defaultVehiculoId={selectedVehiculo}
        />
      </Modal>

      {/* Modificacion Modal */}
      <Modal
        open={modModal}
        onClose={() => setModModal(false)}
        title="Nueva Modificación"
        size="lg"
        footer={
          <>
            <button className="btn btn-secondary" onClick={() => setModModal(false)} disabled={saving}>Cancelar</button>
            <button
              id="btn-guardar-mod-modal"
              className="btn btn-primary"
              disabled={saving}
              onClick={() => document.getElementById('modificacion-form')?.requestSubmit()}
            >
              {saving && <span className="spinner" style={{ width: 14, height: 14 }} />}
              Guardar
            </button>
          </>
        }
      >
        <ModificacionForm
          onSubmit={handleCrearModificacion}
          loading={saving}
          vehiculos={vehiculos}
          empleados={empleados}
          defaultVehiculoId={selectedVehiculo}
        />
      </Modal>

      {/* Confirm Delete */}
      <Modal
        open={!!confirmDelete}
        onClose={() => setConfirmDelete(null)}
        title="Eliminar registro"
        size="sm"
        footer={
          <>
            <button className="btn btn-secondary" onClick={() => setConfirmDelete(null)}>Cancelar</button>
            <button id="btn-confirmar-eliminar-registro" className="btn btn-danger" onClick={handleDelete}>Eliminar</button>
          </>
        }
      >
        <div className="confirm-dialog">
          <AlertTriangle size={40} />
          <h3>¿Eliminar este registro?</h3>
          <p>Esta acción no se puede deshacer.</p>
        </div>
      </Modal>
    </div>
  );
}
