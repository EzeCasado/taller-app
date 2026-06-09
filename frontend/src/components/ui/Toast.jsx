import { CheckCircle, AlertCircle, AlertTriangle, X } from 'lucide-react';

const icons = {
  success: <CheckCircle size={18} color="var(--success)" />,
  error: <AlertCircle size={18} color="var(--danger)" />,
  warning: <AlertTriangle size={18} color="var(--warning)" />,
};

export default function Toast({ toasts, onRemove }) {
  return (
    <div className="toast-container" aria-live="polite" aria-label="Notificaciones">
      {toasts.map((toast) => (
        <div key={toast.id} className={`toast toast-${toast.type}`} role="alert">
          <div className="toast-icon">{icons[toast.type]}</div>
          <div className="toast-content">
            {toast.title && <div className="toast-title">{toast.title}</div>}
            {toast.message && <div className="toast-msg">{toast.message}</div>}
          </div>
          <button
            className="btn btn-icon"
            style={{ padding: '4px', background: 'transparent', color: 'var(--text-muted)' }}
            onClick={() => onRemove(toast.id)}
            aria-label="Cerrar notificación"
          >
            <X size={14} />
          </button>
        </div>
      ))}
    </div>
  );
}
