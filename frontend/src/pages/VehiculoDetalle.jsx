import { useState, useEffect, useCallback } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import {
  ArrowLeft, Car, Gauge, Calendar, Wrench, Zap, Plus,
  DollarSign, Trash2, AlertTriangle,
} from 'lucide-react';
import Timeline from '../components/ui/Timeline';
import Modal from '../components/ui/Modal';
import StatusBadge from '../components/ui/StatusBadge';
import MantenimientoForm from '../components/forms/MantenimientoForm';
import ModificacionForm from '../components/forms/ModificacionForm';
import Spinner from '../components/ui/Spinner';
import { listarVehiculos, obtenerGastosVehiculo } from '../api/vehiculos';
import { listarMantenimientosPorVehiculo, crearMantenimiento } from '../api/mantenimientos';
import { listarModificacionesPorVehiculo, crearModificacion } from '../api/modificaciones';
import { listarVehiculos as fetchVehiculos } from '../api/vehiculos';
import { listarEmpleados } from '../api/empleados';

const formatCurrency = (val) =>
  new Intl.NumberFormat('es-AR', { style: 'currency', currency: 'ARS' }).format(val ?? 0);

/**
 * Componente React: VehiculoDetalle.
 * 
 * Este componente es responsable de renderizar y gestionar la vista de VehiculoDetalle
 * dentro de la aplicación. Maneja su propio estado local y propiedades.
 * 
 * @param {Object} props - Propiedades pasadas al componente.
 * @returns {JSX.Element} El elemento renderizado del componente VehiculoDetalle.
 */
export default function VehiculoDetalle({ toast }) {
  const { id } = useParams();
  const navigate = useNavigate();

  const [vehiculo, setVehiculo] = useState(null);
  const [mantenimientos, setMantenimientos] = useState([]);
  const [modificaciones, setModificaciones] = useState([]);
  const [gastos, setGastos] = useState(null);
  const [empleados, setEmpleados] = useState([]);
  const [vehiculos, setVehiculos] = useState([]);
  const [loading, setLoading] = useState(true);

  const [mantModal, setMantModal] = useState(false);
  const [modModal, setModModal] = useState(false);
  const [saving, setSaving] = useState(false);

  const fetchAll = useCallback(async () => {
    setLoading(true);
    try {
      const [allVehiculos, mantRes, modRes, gastosRes, empRes] = await Promise.all([
        fetchVehiculos(),
        listarMantenimientosPorVehiculo(id),
        listarModificacionesPorVehiculo(id),
        obtenerGastosVehiculo(id),
        listarEmpleados(),
      ]);
      const veh = allVehiculos.data.find((v) => String(v.id) === String(id));
      setVehiculo(veh ?? null);
      setVehiculos(allVehiculos.data);
      setMantenimientos(mantRes.data);
      setModificaciones(modRes.data);
      setGastos(gastosRes.data);
      setEmpleados(empRes.data);
    } catch (err) {
      toast?.({ type: 'error', title: 'Error', message: err.message });
    } finally {
      setLoading(false);
    }
  }, [id, toast]);

  useEffect(() => { fetchAll(); }, [fetchAll]);

  const handleCrearMantenimiento = async (data) => {
    setSaving(true);
    try {
      await crearMantenimiento(data);
      toast?.({ type: 'success', title: 'Mantenimiento registrado' });
      setMantModal(false);
      fetchAll();
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
      fetchAll();
    } catch (err) {
      toast?.({ type: 'error', title: 'Error', message: err.message });
    } finally {
      setSaving(false);
    }
  };

  if (loading) return <Spinner size="lg" label="Cargando vehículo..." />;

  if (!vehiculo) {
    return (
      <div>
        <button className="btn btn-secondary" onClick={() => navigate('/vehiculos')}>
          <ArrowLeft size={16} /> Volver
        </button>
        <p style={{ marginTop: 24, color: 'var(--text-secondary)' }}>Vehículo no encontrado.</p>
      </div>
    );
  }

  return (
    <div>
      {/* Back button + page header */}
      <div className="page-header">
        <div style={{ display: 'flex', alignItems: 'center', gap: 14 }}>
          <button
            id="btn-volver-vehiculos"
            className="btn btn-secondary btn-sm btn-icon"
            onClick={() => navigate('/vehiculos')}
            aria-label="Volver a vehículos"
          >
            <ArrowLeft size={16} />
          </button>
          <div>
            <h1>Detalle del Vehículo</h1>
            <p>Historial completo y gastos</p>
          </div>
        </div>
        <div className="page-header-actions">
          <button id="btn-nuevo-mantenimiento" className="btn btn-secondary" onClick={() => setMantModal(true)}>
            <Wrench size={15} /> Mantenimiento
          </button>
          <button id="btn-nueva-modificacion" className="btn btn-primary" onClick={() => setModModal(true)}>
            <Zap size={15} /> Modificación
          </button>
        </div>
      </div>

      {/* Vehicle Hero Card */}
      <div className="vehicle-hero">
        <div className="vehicle-hero-icon" aria-hidden="true">
          <Car size={36} color="var(--accent)" />
        </div>
        <div style={{ flex: 1 }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: 12, flexWrap: 'wrap' }}>
            <h2 className="vehicle-hero-info" style={{ fontSize: 24, fontWeight: 800 }}>
              {vehiculo.marca} {vehiculo.modelo}
            </h2>
            <StatusBadge activo={vehiculo.activo} />
          </div>
          <div className="vehicle-hero-patente">{vehiculo.patente}</div>
          <div className="vehicle-stats">
            <span className="vehicle-stat">
              <Calendar size={14} /> {vehiculo.anio}
            </span>
            <span className="vehicle-stat">
              <Gauge size={14} /> {vehiculo.kilometraje?.toLocaleString('es-AR')} km
            </span>
            {vehiculo.motor && (
              <span className="vehicle-stat">
                <Wrench size={14} /> {vehiculo.motor}
              </span>
            )}
            {vehiculo.cliente && (
              <span className="vehicle-stat">
                <Car size={14} /> {vehiculo.cliente.nombre} {vehiculo.cliente.apellido}
              </span>
            )}
          </div>
          {vehiculo.comentarios && (
            <p style={{ marginTop: 10, fontSize: 13, color: 'var(--text-secondary)', maxWidth: 600 }}>
              {vehiculo.comentarios}
            </p>
          )}
        </div>
      </div>

      {/* Gastos Summary */}
      {gastos && (
        <div className="gasto-card">
          <div className="section-header">
            <span className="section-title">
              <DollarSign size={16} /> Resumen de Gastos
            </span>
          </div>
          <div className="gasto-grid">
            <div className="gasto-item">
              <div className="gasto-item-value text-info">{formatCurrency(gastos.costoTotalMantenimiento)}</div>
              <div className="gasto-item-label">Mantenimientos</div>
            </div>
            <div className="gasto-item">
              <div className="gasto-item-value text-warning">{formatCurrency(gastos.costoTotalModificacion)}</div>
              <div className="gasto-item-label">Modificaciones</div>
            </div>
            <div className="gasto-item">
              <div className="gasto-item-value text-accent">{formatCurrency(gastos.costoTotal)}</div>
              <div className="gasto-item-label" style={{ fontWeight: 700 }}>Total Invertido</div>
            </div>
          </div>
        </div>
      )}

      {/* Timeline */}
      <div className="card">
        <div className="section-header" style={{ marginBottom: 20 }}>
          <span className="section-title">
            <Calendar size={16} /> Historial de Trabajos
          </span>
          <span className="badge badge-neutral">
            {mantenimientos.length + modificaciones.length} registros
          </span>
        </div>
        <Timeline mantenimientos={mantenimientos} modificaciones={modificaciones} />
      </div>

      {/* Mantenimiento Modal */}
      <Modal
        open={mantModal}
        onClose={() => setMantModal(false)}
        title="Registrar Mantenimiento"
        size="lg"
        footer={
          <>
            <button className="btn btn-secondary" onClick={() => setMantModal(false)} disabled={saving}>Cancelar</button>
            <button
              id="btn-guardar-mantenimiento"
              className="btn btn-primary"
              disabled={saving}
              onClick={() => document.getElementById('mantenimiento-form')?.requestSubmit()}
            >
              {saving && <span className="spinner" style={{ width: 14, height: 14 }} />}
              Guardar mantenimiento
            </button>
          </>
        }
      >
        <MantenimientoForm
          onSubmit={handleCrearMantenimiento}
          loading={saving}
          vehiculos={vehiculos}
          empleados={empleados}
          defaultVehiculoId={id}
        />
      </Modal>

      {/* Modificacion Modal */}
      <Modal
        open={modModal}
        onClose={() => setModModal(false)}
        title="Registrar Modificación"
        size="lg"
        footer={
          <>
            <button className="btn btn-secondary" onClick={() => setModModal(false)} disabled={saving}>Cancelar</button>
            <button
              id="btn-guardar-modificacion"
              className="btn btn-primary"
              disabled={saving}
              onClick={() => document.getElementById('modificacion-form')?.requestSubmit()}
            >
              {saving && <span className="spinner" style={{ width: 14, height: 14 }} />}
              Guardar modificación
            </button>
          </>
        }
      >
        <ModificacionForm
          onSubmit={handleCrearModificacion}
          loading={saving}
          vehiculos={vehiculos}
          empleados={empleados}
          defaultVehiculoId={id}
        />
      </Modal>
    </div>
  );
}
