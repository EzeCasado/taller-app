import { useState } from 'react';

const initialState = {
  nombre: '',
  apellido: '',
  telefono: '',
  email: '',
  observaciones: '',
};

export default function ClienteForm({ onSubmit, loading, initial = {} }) {
  const [form, setForm] = useState({ ...initialState, ...initial });
  const [errors, setErrors] = useState({});

  const validate = () => {
    const errs = {};
    if (!form.nombre.trim()) errs.nombre = 'El nombre es requerido';
    if (!form.apellido.trim()) errs.apellido = 'El apellido es requerido';
    if (form.email && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(form.email)) {
      errs.email = 'Email inválido';
    }
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
    onSubmit({ ...form, activo: true });
  };

  return (
    <form onSubmit={handleSubmit} id="cliente-form" noValidate>
      <div className="form-row">
        <div className="form-group">
          <label className="form-label" htmlFor="nombre">
            Nombre <span className="form-required">*</span>
          </label>
          <input
            id="nombre"
            name="nombre"
            className={`form-input${errors.nombre ? ' error' : ''}`}
            placeholder="Ej: Juan"
            value={form.nombre}
            onChange={handleChange}
            autoFocus
          />
          {errors.nombre && <div className="form-error-msg">{errors.nombre}</div>}
        </div>

        <div className="form-group">
          <label className="form-label" htmlFor="apellido">
            Apellido <span className="form-required">*</span>
          </label>
          <input
            id="apellido"
            name="apellido"
            className={`form-input${errors.apellido ? ' error' : ''}`}
            placeholder="Ej: García"
            value={form.apellido}
            onChange={handleChange}
          />
          {errors.apellido && <div className="form-error-msg">{errors.apellido}</div>}
        </div>
      </div>

      <div className="form-row">
        <div className="form-group">
          <label className="form-label" htmlFor="telefono">Teléfono</label>
          <input
            id="telefono"
            name="telefono"
            className="form-input"
            placeholder="Ej: 011-4567-8901"
            value={form.telefono}
            onChange={handleChange}
          />
        </div>

        <div className="form-group">
          <label className="form-label" htmlFor="email">Email</label>
          <input
            id="email"
            name="email"
            type="email"
            className={`form-input${errors.email ? ' error' : ''}`}
            placeholder="Ej: juan@ejemplo.com"
            value={form.email}
            onChange={handleChange}
          />
          {errors.email && <div className="form-error-msg">{errors.email}</div>}
        </div>
      </div>

      <div className="form-group">
        <label className="form-label" htmlFor="observaciones">Observaciones</label>
        <textarea
          id="observaciones"
          name="observaciones"
          className="form-textarea"
          placeholder="Notas adicionales sobre el cliente..."
          value={form.observaciones}
          onChange={handleChange}
          rows={3}
        />
      </div>
    </form>
  );
}
