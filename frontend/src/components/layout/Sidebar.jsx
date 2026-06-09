import { NavLink, useNavigate } from 'react-router-dom';
import {
  LayoutDashboard,
  Users,
  Car,
  Wrench,
  UserCog,
  Gauge,
  LogOut,
} from 'lucide-react';
import { useAuth } from '../../context/AuthContext';

const navItems = [
  { to: '/', label: 'Dashboard', icon: LayoutDashboard, end: true },
  { to: '/clientes', label: 'Clientes', icon: Users },
  { to: '/vehiculos', label: 'Vehículos', icon: Car },
  { to: '/mantenimientos', label: 'Mantenimientos', icon: Wrench },
  { to: '/empleados', label: 'Empleados', icon: UserCog },
];

export default function Sidebar() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/login', { replace: true });
  };

  // Iniciales del nombre del usuario (máx 2 caracteres)
  const initials = user?.nombre
    ? user.nombre.split(' ').map((w) => w[0]).join('').slice(0, 2).toUpperCase()
    : (user?.usuario?.[0]?.toUpperCase() ?? 'A');

  return (
    <nav className="sidebar" aria-label="Navegación principal">
      {/* Logo */}
      <div className="sidebar-logo">
        <div className="sidebar-logo-icon" aria-hidden="true">
          <Gauge size={22} color="white" />
        </div>
        <div className="sidebar-logo-text">
          <h1>TallerApp</h1>
          <span>Panel de Gestión</span>
        </div>
      </div>

      {/* Navigation */}
      <div className="sidebar-nav">
        <span className="sidebar-section-label">Menú</span>

        {navItems.map(({ to, label, icon: Icon, end }) => (
          <NavLink
            key={to}
            to={to}
            end={end}
            id={`nav-${label.toLowerCase().replace(/\s/g, '-')}`}
            className={({ isActive }) =>
              `sidebar-item${isActive ? ' active' : ''}`
            }
            aria-label={label}
          >
            <Icon size={18} />
            <span>{label}</span>
          </NavLink>
        ))}
      </div>

      {/* Footer — usuario + logout */}
      <div className="sidebar-footer">
        <div className="sidebar-footer-user">
          <div className="user-avatar" aria-hidden="true">{initials}</div>
          <div className="user-info" style={{ flex: 1, minWidth: 0 }}>
            <h4 style={{ overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>
              {user?.nombre ?? user?.usuario}
            </h4>
            <span>@{user?.usuario}</span>
          </div>
          <button
            id="btn-logout"
            onClick={handleLogout}
            aria-label="Cerrar sesión"
            title="Cerrar sesión"
            style={{
              background: 'transparent',
              border: 'none',
              cursor: 'pointer',
              color: 'var(--text-muted)',
              display: 'flex',
              alignItems: 'center',
              padding: 4,
              borderRadius: 'var(--radius-sm)',
              transition: 'color var(--transition-fast)',
              flexShrink: 0,
            }}
            onMouseEnter={(e) => (e.currentTarget.style.color = 'var(--danger)')}
            onMouseLeave={(e) => (e.currentTarget.style.color = 'var(--text-muted)')}
          >
            <LogOut size={15} />
          </button>
        </div>
      </div>
    </nav>
  );
}
