import Sidebar from './Sidebar';
import Header from './Header';

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
