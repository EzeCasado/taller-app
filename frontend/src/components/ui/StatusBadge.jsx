/**
 * Componente React: StatusBadge.
 * 
 * Este componente es responsable de renderizar y gestionar la vista de StatusBadge
 * dentro de la aplicación. Maneja su propio estado local y propiedades.
 * 
 * @param {Object} props - Propiedades pasadas al componente.
 * @returns {JSX.Element} El elemento renderizado del componente StatusBadge.
 */
export default function StatusBadge({ activo, labels = ['Activo', 'Inactivo'] }) {
  return (
    <span className={`badge ${activo ? 'badge-success' : 'badge-danger'}`} aria-label={activo ? labels[0] : labels[1]}>
      <span
        style={{
          width: 6,
          height: 6,
          borderRadius: '50%',
          background: activo ? 'var(--success)' : 'var(--danger)',
          display: 'inline-block',
        }}
      />
      {activo ? labels[0] : labels[1]}
    </span>
  );
}
