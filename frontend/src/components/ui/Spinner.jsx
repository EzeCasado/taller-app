export default function Spinner({ size = 'md', label = 'Cargando...' }) {
  const cls = size === 'lg' ? 'spinner spinner-lg' : 'spinner';
  return (
    <div className="loading-state" role="status" aria-label={label}>
      <div className={cls} />
      <span>{label}</span>
    </div>
  );
}
