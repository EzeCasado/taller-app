import Sidebar from './Sidebar';
import Header from './Header';

/**
 * Componente React: Layout.
 * 
 * Este componente es responsable de renderizar y gestionar la vista de Layout
 * dentro de la aplicación. Maneja su propio estado local y propiedades.
 * 
 * @param {Object} props - Propiedades pasadas al componente.
 * @returns {JSX.Element} El elemento renderizado del componente Layout.
 */
export default function Layout({ children, onRefresh }) {
  return (
    <div className="app-layout">
      <Sidebar />
      <div className="main-content">
        <Header onRefresh={onRefresh} />
        <main className="page-content" id="main-content" tabIndex={-1}>
          {children}
        </main>
      </div>
    </div>
  );
}
