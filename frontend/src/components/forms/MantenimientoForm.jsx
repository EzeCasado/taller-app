import { useState } from 'react';

const today = new Date().toISOString().split('T')[0];

const initialState = {
  fecha: today,
  descripcion: '',
  costo: '',
  kilometraje: '',
  comentario: '',
  vehiculoId: '',
  empleadoId: '',
};

export default function MantenimientoForm({ onSubmit, loading, vehiculos = [], empleados = [], defaultVehiculoId = '' }) {
  const [form, setForm] = useState({ ...initialState, vehiculoId: defaultVehiculoId });
  const [errors, setErrors] = useState({});

  const validate = () => {
    const errs = {};
    if (!form.descripcion.trim()) errs.descripcion = 'La descripción es requerida';
    if (!form.costo || isNaN(form.costo) || Number(form.costo) < 0) errs.costo = 'Costo inválido';
    if (!form.vehiculoId) errs.vehiculoId = 'Seleccione un vehículo';
    if (!form.empleadoId) errs.empleadoId = 'Seleccione un empleado';
    return errs;
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setForm((f) => ({ ...f, [name]: value }));
    if (errors[name]) setErrors((e) => ({ ...e, [name]: undefined }));
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    const errs = validate();
    if (Object.keys(errs).length > 0) { setErrors(errs); return; }

    onSubmit({
      fecha: form.fecha,
      descripcion: form.descripcion,
      costo: Number(form.costo),
      kilometraje: Number(form.kilometraje) || 0,
      comentario: form.comentario,
      activo: true,
      vehiculo: { id: Number(form.vehiculoId) },
      empleado: { id: Number(form.empleadoId) },
    });
  };

  return (
    <form onSubmit={handleSubmit} id="mantenimiento-form" noValidate>
      <div className="form-row">
        <div className="form-group">
          <label className="form-label" htmlFor="mant-vehiculo">
            Vehículo <span className="form-required">*</span>
          </label>
          <select
            id="mant-vehiculo"
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
          <label className="form-label" htmlFor="mant-empleado">
            Mecánico <span className="form-required">*</span>
          </label>
          <select
            id="mant-empleado"
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

      <div className="form-group">
        <label className="form-label" htmlFor="mant-descripcion">
          Descripción <span className="form-required">*</span>
        </label>
        <textarea
          id="mant-descripcion"
          name="descripcion"
          className={`form-textarea${errors.descripcion ? ' error' : ''}`}
          placeholder="Describí el trabajo realizado..."
          value={form.descripcion}
          onChange={handleChange}
          rows={3}
          autoFocus
        />
        {errors.descripcion && <div className="form-error-msg">{errors.descripcion}</div>}
      </div>

      <div className="form-row">
        <div className="form-group">
          <label className="form-label" htmlFor="mant-costo">
            Costo (ARS) <span className="form-required">*</span>
          </label>
          <input
            id="mant-costo"
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
          <label className="form-label" htmlFor="mant-km">Kilometraje actual</label>
          <input
            id="mant-km"
            name="kilometraje"
            type="number"
            className="form-input"
            placeholder="0"
            value={form.kilometraje}
            onChange={handleChange}
            min="0"
          />
        </div>
      </div>

      <div className="form-row">
        <div className="form-group">
          <label className="form-label" htmlFor="mant-fecha">Fecha</label>
          <input
            id="mant-fecha"
            name="fecha"
            type="date"
            className="form-input"
            value={form.fecha}
            onChange={handleChange}
          />
        </div>
      </div>

      <div className="form-group">
        <label className="form-label" htmlFor="mant-comentario">Comentario adicional</label>
        <textarea
          id="mant-comentario"
          name="comentario"
          className="form-textarea"
          placeholder="Observaciones, repuestos utilizados..."
          value={form.comentario}
          onChange={handleChange}
          rows={2}
        />
      </div>
    </form>
  );
}
