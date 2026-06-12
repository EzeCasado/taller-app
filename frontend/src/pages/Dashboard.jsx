import { useState, useEffect, useCallback } from 'react';
import { Users, Car, Wrench, UserCog, TrendingUp, Clock, Calendar } from 'lucide-react';
import KpiCard from '../components/ui/KpiCard';
import DataTable from '../components/ui/DataTable';
import StatusBadge from '../components/ui/StatusBadge';
import Spinner from '../components/ui/Spinner';
import { listarClientes } from '../api/clientes';
import { listarVehiculos } from '../api/vehiculos';
import { listarEmpleados } from '../api/empleados';

const formatDate = (d) => {
  if (!d) return '—';
  return new Date(d).toLocaleDateString('es-AR', { day: '2-digit', month: 'short', year: 'numeric' });
};

/**
 * Componente React: Dashboard.
 * 
 * Este componente es responsable de renderizar y gestionar la vista de Dashboard
 * dentro de la aplicación. Maneja su propio estado local y propiedades.
 * 
 * @param {Object} props - Propiedades pasadas al componente.
 * @returns {JSX.Element} El elemento renderizado del componente Dashboard.
 */
export default function Dashboard() {
  const [clientes, setClientes] = useState([]);
  const [vehiculos, setVehiculos] = useState([]);
  const [empleados, setEmpleados] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const fetchAll = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const [cRes, vRes, eRes] = await Promise.all([
        listarClientes(),
        listarVehiculos(),
        listarEmpleados(),
      ]);
      setClientes(cRes.data);
      setVehiculos(vRes.data);
      setEmpleados(eRes.data);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => { fetchAll(); }, [fetchAll]);

  const vehiculosActivos = vehiculos.filter((v) => v.activo).length;
  const clientesActivos = clientes.filter((c) => c.activo).length;
  const empleadosActivos = empleados.filter((e) => e.activo).length;

  const recentVehiculos = [...vehiculos].reverse().slice(0, 8);

  const vehiculoColumns = [
    {
      key: 'patente',
      label: 'Patente',
      sortable: true,
      render: (val) => (
        <span className="font-mono" style={{ letterSpacing: 1 }}>{val}</span>
      ),
    },
    { key: 'marca', label: 'Marca', sortable: true },
    { key: 'modelo', label: 'Modelo', sortable: true },
    {
      key: 'anio',
      label: 'Año',
      sortable: true,
    },
    {
      key: 'activo',
      label: 'Estado',
      render: (val) => <StatusBadge activo={val} />,
    },
    {
      key: 'cliente',
      label: 'Propietario',
      render: (val) => val ? `${val.nombre} ${val.apellido}` : '—',
    },
  ];

  if (loading) return <Spinner size="lg" label="Cargando dashboard..." />;

  if (error) {
    return (
      <div
        style={{
          background: 'var(--danger-light)',
          border: '1px solid rgba(239,68,68,0.3)',
          borderRadius: 'var(--radius-md)',
          padding: '20px 24px',
          color: 'var(--danger)',
        }}
        role="alert"
      >
        <strong>Error al conectar con la API:</strong> {error}
        <br />
        <small style={{ color: 'var(--text-secondary)' }}>
          Verificá que tu servidor Spring Boot esté corriendo en el puerto 8080.
        </small>
      </div>
    );
  }

  return (
    <div>
      {/* Page header */}
      <div className="page-header">
        <div>
          <h1>Panel de Control</h1>
          <p>Resumen general del taller · Todo en un vistazo</p>
        </div>
      </div>

      {/* KPI Cards */}
      <div className="kpi-grid">
        <KpiCard
          label="Vehículos activos"
          value={vehiculosActivos}
          color="var(--accent)"
          icon={<Car size={24} color="var(--accent)" />}
          change={`${vehiculos.length} total registrados`}
        />
        <KpiCard
          label="Clientes registrados"
          value={clientesActivos}
          color="var(--success)"
          icon={<Users size={24} color="var(--success)" />}
          change={`${clientes.length} total`}
        />
        <KpiCard
          label="Empleados activos"
          value={empleadosActivos}
          color="var(--info)"
          icon={<UserCog size={24} color="var(--info)" />}
          change={`${empleados.length} total`}
        />
        <KpiCard
          label="Total vehículos"
          value={vehiculos.length}
          color="var(--warning)"
          icon={<TrendingUp size={24} color="var(--warning)" />}
          change="En el sistema"
        />
      </div>

      {/* Recent vehicles table */}
      <DataTable
        title="Vehículos registrados"
        columns={vehiculoColumns}
        data={recentVehiculos}
        searchKeys={['patente', 'marca', 'modelo']}
        emptyText="No hay vehículos registrados aún"
      />
    </div>
  );
}
