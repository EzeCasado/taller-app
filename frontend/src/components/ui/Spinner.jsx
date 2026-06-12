/**
 * Componente React: Spinner.
 * 
 * Este componente es responsable de renderizar y gestionar la vista de Spinner
 * dentro de la aplicación. Maneja su propio estado local y propiedades.
 * 
 * @param {Object} props - Propiedades pasadas al componente.
 * @returns {JSX.Element} El elemento renderizado del componente Spinner.
 */
export default function Spinner({ size = 'md', label = 'Cargando...' }) {
  const cls = size === 'lg' ? 'spinner spinner-lg' : 'spinner';
  return (
    <div className="loading-state" role="status" aria-label={label}>
      <div className={cls} />
      <span>{label}</span>
    </div>
  );
}
