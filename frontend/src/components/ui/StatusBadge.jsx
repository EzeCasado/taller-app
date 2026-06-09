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
