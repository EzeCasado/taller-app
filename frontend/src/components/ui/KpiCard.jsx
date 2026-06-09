export default function KpiCard({ label, value, icon, color = 'var(--accent)', change }) {
  return (
    <div
      className="kpi-card"
      style={{ '--card-color': color }}
      role="region"
      aria-label={`Métrica: ${label}`}
    >
      <div className="kpi-info">
        <div className="kpi-label">{label}</div>
        <div className="kpi-value">{value ?? '—'}</div>
        {change && <div className="kpi-change">{change}</div>}
      </div>
      <div className="kpi-icon-wrap" style={{ background: `${color}18` }}>
        {icon}
      </div>
    </div>
  );
}
