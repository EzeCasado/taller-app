import { Navigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';

/**
 * Protege las rutas del panel.
 * Si el usuario no está autenticado, redirige a /login.
 */
/**
 * Componente React: PrivateRoute.
 * 
 * Este componente es responsable de renderizar y gestionar la vista de PrivateRoute
 * dentro de la aplicación. Maneja su propio estado local y propiedades.
 * 
 * @param {Object} props - Propiedades pasadas al componente.
 * @returns {JSX.Element} El elemento renderizado del componente PrivateRoute.
 */
export default function PrivateRoute({ children }) {
  const { isAuthenticated } = useAuth();
  return isAuthenticated ? children : <Navigate to="/login" replace />;
}
