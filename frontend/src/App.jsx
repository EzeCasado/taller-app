import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { useCallback } from 'react';
import { AuthProvider, useAuth } from './context/AuthContext';
import Layout from './components/layout/Layout';
import PrivateRoute from './components/layout/PrivateRoute';
import Login from './pages/Login';
import Dashboard from './pages/Dashboard';
import Clientes from './pages/Clientes';
import Vehiculos from './pages/Vehiculos';
import VehiculoDetalle from './pages/VehiculoDetalle';
import Mantenimientos from './pages/Mantenimientos';
import Empleados from './pages/Empleados';
import Toast from './components/ui/Toast';
import { useToast } from './hooks/useToast';

// Componente interno que ya tiene acceso al AuthContext
function AppRoutes() {
  const { isAuthenticated } = useAuth();
  const { toasts, addToast, removeToast } = useToast();
  const toast = useCallback(addToast, [addToast]);

  return (
    <>
      <Routes>
        {/* Rutas públicas */}
        <Route
          path="/login"
          element={isAuthenticated ? <Navigate to="/" replace /> : <Login />}
        />

        {/* Rutas protegidas: requieren autenticación */}
        <Route
          path="/*"
          element={
            <PrivateRoute>
              <Layout>
                <Routes>
                  <Route path="/" element={<Dashboard />} />
                  <Route path="/clientes" element={<Clientes toast={toast} />} />
                  <Route path="/vehiculos" element={<Vehiculos toast={toast} />} />
                  <Route path="/vehiculos/:id" element={<VehiculoDetalle toast={toast} />} />
                  <Route path="/mantenimientos" element={<Mantenimientos toast={toast} />} />
                  <Route path="/empleados" element={<Empleados toast={toast} />} />
                  {/* Catch-all → dashboard */}
                  <Route path="*" element={<Navigate to="/" replace />} />
                </Routes>
              </Layout>
            </PrivateRoute>
          }
        />
      </Routes>

      <Toast toasts={toasts} onRemove={removeToast} />
    </>
  );
}

export default function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <AppRoutes />
      </AuthProvider>
    </BrowserRouter>
  );
}
