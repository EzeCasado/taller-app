import { Wrench, Zap } from 'lucide-react';

const formatDate = (dateStr) => {
  if (!dateStr) return '—';
  return new Date(dateStr).toLocaleDateString('es-AR', {
    day: '2-digit',
    month: 'short',
    year: 'numeric',
  });
};

const formatCurrency = (val) =>
  new Intl.NumberFormat('es-AR', { style: 'currency', currency: 'ARS' }).format(val ?? 0);

/**
 * Componente React: Timeline.
 * 
 * Este componente es responsable de renderizar y gestionar la vista de Timeline
 * dentro de la aplicación. Maneja su propio estado local y propiedades.
 * 
 * @param {Object} props - Propiedades pasadas al componente.
 * @returns {JSX.Element} El elemento renderizado del componente Timeline.
 */
export default function Timeline({ mantenimientos = [], modificaciones = [] }) {
  const items = [
    ...mantenimientos.map((m) => ({ ...m, _type: 'mantenimiento', _date: new Date(m.fecha) })),
    ...modificaciones.map((m) => ({ ...m, _type: 'modificacion', _date: new Date(m.fecha) })),
  ].sort((a, b) => b._date - a._date);

  if (items.length === 0) {
    return (
      <div className="table-empty">
        <Wrench size={36} />
        <p>No hay registros en el historial</p>
      </div>
    );
  }

  return (
    <div className="timeline" aria-label="Historial del vehículo">
      {items.map((item) => {
        const isMant = item._type === 'mantenimiento';
        return (
          <div key={`${item._type}-${item.id}`} className="timeline-item">
            <div
              className={`timeline-dot ${
                isMant ? 'timeline-dot-maintenance' : 'timeline-dot-modification'
              }`}
              aria-hidden="true"
            />
            <div className="timeline-card">
              <div className="timeline-meta">
                <span className="timeline-date">{formatDate(item.fecha)}</span>
                <span className={`badge ${isMant ? 'badge-info' : 'badge-warning'}`}>
                  {isMant ? (
                    <>
                      <Wrench size={10} /> Mantenimiento
                    </>
                  ) : (
                    <>
                      <Zap size={10} /> Modificación
                    </>
                  )}
                </span>
              </div>

              <div className="timeline-title">
                {isMant ? item.descripcion : item.nombre}
              </div>

              {isMant && item.comentario && (
                <div className="timeline-desc">{item.comentario}</div>
              )}

              <div className="timeline-footer">
                <span className="timeline-cost">{formatCurrency(item.costo)}</span>
                {isMant && item.kilometraje > 0 && (
                  <span className="timeline-km">
                    <svg
                      width="12"
                      height="12"
                      viewBox="0 0 24 24"
                      fill="none"
                      stroke="currentColor"
                      strokeWidth="2"
                      aria-hidden="true"
                    >
                      <circle cx="12" cy="12" r="10" />
                      <path d="M12 6v6l4 2" />
                    </svg>
                    {item.kilometraje.toLocaleString('es-AR')} km
                  </span>
                )}
                {!isMant && (
                  <span
                    className={`badge ${item.sigueInstalada ? 'badge-success' : 'badge-neutral'}`}
                  >
                    {item.sigueInstalada ? 'Instalada' : 'Removida'}
                  </span>
                )}
              </div>

              {item.empleado && (
                <div
                  style={{
                    marginTop: 8,
                    fontSize: 12,
                    color: 'var(--text-muted)',
                  }}
                >
                  Mecánico: {item.empleado.nombre}
                </div>
              )}
            </div>
          </div>
        );
      })}
    </div>
  );
}
