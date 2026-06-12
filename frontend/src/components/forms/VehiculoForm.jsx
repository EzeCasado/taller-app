import { useState } from 'react';

const initialState = {
  patente: '',
  marca: '',
  modelo: '',
  anio: new Date().getFullYear(),
  kilometraje: 0,
  motor: '',
  comentarios: '',
  clienteId: '',
};

/**
 * Componente React: VehiculoForm.
 * 
 * Este componente es responsable de renderizar y gestionar la vista de VehiculoForm
 * dentro de la aplicación. Maneja su propio estado local y propiedades.
 * 
 * @param {Object} props - Propiedades pasadas al componente.
 * @returns {JSX.Element} El elemento renderizado del componente VehiculoForm.
 */
export default function VehiculoForm({ onSubmit, loading, clientes = [] }) {
  const [form, setForm] = useState(initialState);
  const [errors, setErrors] = useState({});

  const validate = () => {
    const errs = {};
    if (!form.patente.trim()) errs.patente = 'La patente es requerida';
    if (!form.marca.trim()) errs.marca = 'La marca es requerida';
    if (!form.modelo.trim()) errs.modelo = 'El modelo es requerido';
    if (!form.clienteId) errs.clienteId = 'Debe seleccionar un cliente';
    if (form.anio < 1900 || form.anio > new Date().getFullYear() + 1) {
      errs.anio = 'Año inválido';
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

    onSubmit({
      patente: form.patente.toUpperCase().trim(),
      marca: form.marca,
      modelo: form.modelo,
      anio: Number(form.anio),
      kilometraje: Number(form.kilometraje),
      motor: form.motor,
      comentarios: form.comentarios,
      activo: true,
      cliente: { id: Number(form.clienteId) },
    });
  };

  return (
    <form onSubmit={handleSubmit} id="vehiculo-form" noValidate>
      <div className="form-row">
        <div className="form-group">
          <label className="form-label" htmlFor="veh-patente">
            Patente <span className="form-required">*</span>
          </label>
          <input
            id="veh-patente"
            name="patente"
            className={`form-input${errors.patente ? ' error' : ''}`}
            placeholder="Ej: ABC 123"
            value={form.patente}
            onChange={handleChange}
            style={{ textTransform: 'uppercase' }}
            autoFocus
          />
          {errors.patente && <div className="form-error-msg">{errors.patente}</div>}
        </div>

        <div className="form-group">
          <label className="form-label" htmlFor="veh-cliente">
            Cliente propietario <span className="form-required">*</span>
          </label>
          <select
            id="veh-cliente"
            name="clienteId"
            className={`form-select${errors.clienteId ? ' error' : ''}`}
            value={form.clienteId}
            onChange={handleChange}
          >
            <option value="">Seleccionar cliente...</option>
            {clientes.map((c) => (
              <option key={c.id} value={c.id}>
                {c.nombre} {c.apellido}
              </option>
            ))}
          </select>
          {errors.clienteId && <div className="form-error-msg">{errors.clienteId}</div>}
        </div>
      </div>

      <div className="form-row">
        <div className="form-group">
          <label className="form-label" htmlFor="veh-marca">
            Marca <span className="form-required">*</span>
          </label>
          <input
            id="veh-marca"
            name="marca"
            className={`form-input${errors.marca ? ' error' : ''}`}
            placeholder="Ej: Toyota"
            value={form.marca}
            onChange={handleChange}
          />
          {errors.marca && <div className="form-error-msg">{errors.marca}</div>}
        </div>

        <div className="form-group">
          <label className="form-label" htmlFor="veh-modelo">
            Modelo <span className="form-required">*</span>
          </label>
          <input
            id="veh-modelo"
            name="modelo"
            className={`form-input${errors.modelo ? ' error' : ''}`}
            placeholder="Ej: Corolla"
            value={form.modelo}
            onChange={handleChange}
          />
          {errors.modelo && <div className="form-error-msg">{errors.modelo}</div>}
        </div>
      </div>

      <div className="form-row">
        <div className="form-group">
          <label className="form-label" htmlFor="veh-anio">Año</label>
          <input
            id="veh-anio"
            name="anio"
            type="number"
            className={`form-input${errors.anio ? ' error' : ''}`}
            value={form.anio}
            onChange={handleChange}
            min="1900"
            max={new Date().getFullYear() + 1}
          />
          {errors.anio && <div className="form-error-msg">{errors.anio}</div>}
        </div>

        <div className="form-group">
          <label className="form-label" htmlFor="veh-km">Kilometraje</label>
          <input
            id="veh-km"
            name="kilometraje"
            type="number"
            className="form-input"
            value={form.kilometraje}
            onChange={handleChange}
            min="0"
          />
        </div>
      </div>

      <div className="form-group">
        <label className="form-label" htmlFor="veh-motor">Motor</label>
        <input
          id="veh-motor"
          name="motor"
          className="form-input"
          placeholder="Ej: 2.0 TSI, 1.6 Diesel"
          value={form.motor}
          onChange={handleChange}
        />
      </div>

      <div className="form-group">
        <label className="form-label" htmlFor="veh-comentarios">Comentarios</label>
        <textarea
          id="veh-comentarios"
          name="comentarios"
          className="form-textarea"
          placeholder="Observaciones sobre el vehículo..."
          value={form.comentarios}
          onChange={handleChange}
          rows={3}
        />
      </div>
    </form>
  );
}
