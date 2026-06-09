const axios = require('axios');

async function test() {
  const api = axios.create({
    baseURL: 'http://localhost:8080/api',
    headers: {
      'Authorization': 'Basic ' + Buffer.from('EzeCasado:24aer512').toString('base64'),
      'Content-Type': 'application/json'
    }
  });

  try {
    console.log("Testing GET vehiculos...");
    const vRes = await api.get('/vehiculos/listar');
    const vehiculos = vRes.data;
    console.log("Vehiculos:", vehiculos.map(v => v.id + ' - ' + v.patente));

    if (vehiculos.length === 0) {
      console.log("No vehiculos found. Test stops.");
      return;
    }

    const vId = vehiculos[0].id;
    console.log("Testing POST modificacion for vehiculo ID:", vId);

    // Payload exactly as sent from ModificacionForm.jsx
    const payload = {
      nombre: "Escape de prueba",
      fecha: new Date().toISOString().split('T')[0],
      costo: 15000,
      sigueInstalada: true,
      activa: true,
      vehiculo: { id: vId, activo: true },
      empleado: { id: 1, activo: true }
    };

    const mRes = await api.post('/modificaciones/crear', payload);
    console.log("Success:", mRes.status);
  } catch (err) {
    console.error("Error status:", err.response ? err.response.status : err.message);
    if (err.response) {
      console.error("Error data:", err.response.data);
    }
  }
}

test();
