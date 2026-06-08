🛠️ Sistema de Gestión para Taller Mecánico (taller-app)
Este proyecto consiste en el desarrollo de una API REST para el sistema de gestión del taller mecánico, con la documentación oficial a cargo de Ezequiel & Máximo. El catálogo de endpoints se genera de forma automatizada garantizando la consistencia del negocio.

📂 Módulos del Sistema y Catálogo de Endpoints
La API organiza el negocio del taller en 5 grandes bloques funcionales:

1. 👥 Módulo de Clientes
Muestra la lista de todos los clientes registrados en el sistema de gestión.

Endpoint: GET /api/clientes/listar

Atributos del JSON: id, nombre, apellido, telefono, email, activo, observaciones.

2. 🪪 Módulo de Empleados
Registra un nuevo mecánico o administrativo en la plataforma del taller.

Endpoint: POST /api/empleados/crear

Atributos del JSON: id, nombre, usuario, contrasenia, activo.

3. 🚗 Módulo de Vehículos
Devuelve el listado de todos los autos ingresados en el taller con sus respectivos motores y clientes asociados.

Endpoint: GET /api/vehiculos/listar

Atributos del JSON: id, patente, marca, modelo, anio, kilometraje, motor, activo, cliente, comentarios.

4. 🔧 Módulo de Mantenimientos
Carga una nueva orden de servicio (cambios de aceite, frenos, distribución) asignándole un mecánico y un auto.

Endpoint: POST /api/mantenimientos/crear

Atributos del JSON: id, fecha, descripcion, costo, kilometraje, comentario, activo, empleado, vehiculo.

5. 🏎️ Módulo de Modificaciones
Muestra las mejoras o agregados estéticos/mecánicos realizados en los vehículos (escapes, suspensiones, pantallas).

Endpoint: GET /api/modificaciones/vehiculo/1

Atributos del JSON: id, nombre, fecha, costo, activa, sigueInstalada, vehiculo, empleado.

🌐 Documentación Automatizada (Spring REST Docs)
Los ejemplos de peticiones y respuestas HTTP presentados en este catálogo se generan automáticamente a partir de los tests de integración de la aplicación.

¿Cómo visualizar el manual unificado?
Una vez completado el empaquetado del proyecto, el archivo web estático final se genera en la siguiente ruta local:
📂 target/generated-docs/index.html
