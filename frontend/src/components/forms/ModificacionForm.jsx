import { useState } from 'react';

const today = new Date().toISOString().split('T')[0];

const initialState = {
  nombre: '',
  fecha: today,
  costo: '',
  sigueInstalada: true,
  vehiculoId: '',
  empleadoId: '',
};

export default function ModificacionForm({ onSubmit, loading, vehiculos = [], empleados = [], defaultVehiculoId = '' }) {
  const [form, setForm] = useState({ ...initialState, vehiculoId: defaultVehiculoId });
  const [errors, setErrors] = useState({});

  const validate = () => {
    const errs = {};
    if (!form.nombre.trim()) errs.nombre = 'El nombre es requerido';
    if (!form.costo || isNaN(form.costo) || Number(form.costo) < 0) errs.costo = 'Costo inválido';
    if (!form.vehiculoId) errs.vehiculoId = 'Seleccione un vehículo';
    if (!form.empleadoId) errs.empleadoId = 'Seleccione un empleado';
    return errs;
  };

  const handleChange = (e) => {
    const { name, value, type, checked } = e.target;
    setForm((f) => ({ ...f, [name]: type === 'checkbox' ? checked : value }));
    if (errors[name]) setErrors((e) => ({ ...e, [name]: undefined }));
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    const errs = validate();
    if (Object.keys(errs).length > 0) { setErrors(errs); return; }

    onSubmit({
      nombre: form.nombre,
      fecha: form.fecha,
      costo: Number(form.costo),
      sigueInstalada: form.sigueInstalada,
      activa: true,
      vehiculo: { id: Number(form.vehiculoId) },
      empleado: { id: Number(form.empleadoId) },
    });
  };

  return (
    <form onSubmit={handleSubmit} id="modificacion-form" noValidate>
      <div className="form-group">
        <label className="form-label" htmlFor="mod-nombre">
          Nombre de la modificación <span className="form-required">*</span>
        </label>
        <input
          id="mod-nombre"
          name="nombre"
          className={`form-input${errors.nombre ? ' error' : ''}`}
          placeholder="Ej: Escape deportivo, Llantas 17'"
          value={form.nombre}
          onChange={handleChange}
          autoFocus
        />
        {errors.nombre && <div className="form-error-msg">{errors.nombre}</div>}
      </div>

      <div className="form-row">
        <div className="form-group">
          <label className="form-label" htmlFor="mod-vehiculo">
            Vehículo <span className="form-required">*</span>
          </label>
          <select
            id="mod-vehiculo"
            name="vehiculoId"
            className={`form-select${errors.vehiculoId ? ' error' : ''}`}
            value={form.vehiculoId}
            onChange={handleChange}
          >
            <option value="">Seleccionar vehículo...</option>
            {vehiculos.map((v) => (
              <option key={v.id} value={v.id}>
                {v.patente} — {v.marca} {v.modelo}
              </option>
            ))}
          </select>
          {errors.vehiculoId && <div className="form-error-msg">{errors.vehiculoId}</div>}
        </div>

        <div className="form-group">
          <label className="form-label" htmlFor="mod-empleado">
            Mecánico <span className="form-required">*</span>
          </label>
          <select
            id="mod-empleado"
            name="empleadoId"
            className={`form-select${errors.empleadoId ? ' error' : ''}`}
            value={form.empleadoId}
            onChange={handleChange}
          >
            <option value="">Seleccionar empleado...</option>
            {empleados.map((e) => (
              <option key={e.id} value={e.id}>
                {e.nombre}
              </option>
            ))}
          </select>
          {errors.empleadoId && <div className="form-error-msg">{errors.empleadoId}</div>}
        </div>
      </div>

      <div className="form-row">
        <div className="form-group">
          <label className="form-label" htmlFor="mod-costo">
            Costo (ARS) <span className="form-required">*</span>
          </label>
          <input
            id="mod-costo"
            name="costo"
            type="number"
            className={`form-input${errors.costo ? ' error' : ''}`}
            placeholder="0.00"
            value={form.costo}
            onChange={handleChange}
            min="0"
            step="0.01"
          />
          {errors.costo && <div className="form-error-msg">{errors.costo}</div>}
        </div>

        <div className="form-group">
          <label className="form-label" htmlFor="mod-fecha">Fecha</label>
          <input
            id="mod-fecha"
            name="fecha"
            type="date"
            className="form-input"
            value={form.fecha}
            onChange={handleChange}
          />
        </div>
      </div>

      <div className="form-group">
        <label
          style={{ display: 'flex', alignItems: 'center', gap: 10, cursor: 'pointer' }}
          htmlFor="mod-instalada"
        >
          <input
            id="mod-instalada"
            name="sigueInstalada"
            type="checkbox"
            checked={form.sigueInstalada}
            onChange={handleChange}
            style={{ width: 16, height: 16, accentColor: 'var(--accent)', cursor: 'pointer' }}
          />
          <span className="form-label" style={{ margin: 0 }}>
            Modificación sigue instalada actualmente
          </span>
        </label>
      </div>
    </form>
  );
}
