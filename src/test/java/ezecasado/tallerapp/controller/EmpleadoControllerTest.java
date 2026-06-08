package ezecasado.tallerapp.controller;


import ezecasado.tallerapp.models.Empleado;
import ezecasado.tallerapp.service.EmpleadoService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.restdocs.test.autoconfigure.AutoConfigureRestDocs;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EmpleadoController.class)
@AutoConfigureRestDocs(outputDir = "target/generated-snippets")
@DisplayName("Tests de controlador - EmpleadoController")
public class EmpleadoControllerTest {

    @Autowired
    private MockMvc mockMvc;



    @MockitoBean
    private EmpleadoService empleadoService;
    @Autowired
    private ObjectMapper objectMapper;

    // ─── POST /api/empleados/crear ────────────────────────────────────────────

    @Test
    @DisplayName("POST /crear: responde 201 CREATED al registrar un empleado válido")
    public void debeCrearEmpleadoYDocumentar() throws Exception {

        // GIVEN
        Empleado empleadoNuevo = new Empleado("Carlos Méndez", "carlitos", "hash_seguro_123");

        Empleado empleadoGuardado = new Empleado("Carlos Méndez", "carlitos", "hash_seguro_123");
        empleadoGuardado.setId(1L);

        when(empleadoService.crearEmpleado(any(Empleado.class))).thenReturn(empleadoGuardado);

        // WHEN & THEN
        mockMvc.perform(post("/api/empleados/crear")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(empleadoNuevo)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Carlos Méndez"))
                .andExpect(jsonPath("$.usuario").value("carlitos"))
                .andExpect(jsonPath("$.activo").value(true))
                .andDo(document("crear-empleado"));
    }

    @Test
    @DisplayName("POST /crear: lanza ServletException cuando el nombre de usuario ya existe")
    public void debeRechazarEmpleadoConUsuarioDuplicado() {

        // GIVEN
        Empleado empleadoDuplicado = new Empleado("Otro nombre", "carlitos", "otrapass");

        when(empleadoService.crearEmpleado(any(Empleado.class)))
                .thenThrow(new IllegalArgumentException("El usuario ya existe en el sistema"));

        // WHEN & THEN
        jakarta.servlet.ServletException exception = Assertions.assertThrows(
                jakarta.servlet.ServletException.class, () -> {
                    mockMvc.perform(post("/api/empleados/crear")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(empleadoDuplicado)));
                }
        );
        Assertions.assertTrue(exception.getCause().getMessage().contains("El usuario ya existe en el sistema"));
    }

    // ─── GET /api/empleados (listar todos los activos) ────────────────────────

    @Test
    @DisplayName("GET /: responde 200 OK con la lista de empleados activos")
    public void debeListarEmpleadosActivosYDocumentar() throws Exception {

        // GIVEN
        Empleado empleadoFalso = new Empleado("Carlos Méndez", "carlitos", "hash123");
        empleadoFalso.setId(1L);

        when(empleadoService.listarEmpleadosActivos()).thenReturn(List.of(empleadoFalso));

        // WHEN & THEN
        mockMvc.perform(get("/api/empleados"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].usuario").value("carlitos"))
                .andExpect(jsonPath("$[0].activo").value(true))
                .andDo(document("listar-empleados"));
    }

    @Test
    @DisplayName("GET /: responde 200 OK con lista vacía cuando no hay empleados activos")
    public void debeRetornarListaVaciaCuandoNoHayEmpleadosActivos() throws Exception {

        // GIVEN
        when(empleadoService.listarEmpleadosActivos()).thenReturn(List.of());

        // WHEN & THEN
        mockMvc.perform(get("/api/empleados"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty())
                .andDo(document("listar-empleados-vacio"));
    }

    // ─── GET /api/empleados/{id} ──────────────────────────────────────────────

    @Test
    @DisplayName("GET /{id}: responde 200 OK con el empleado encontrado")
    public void debeBuscarEmpleadoPorIdYDocumentar() throws Exception {

        // GIVEN
        Empleado empleadoFalso = new Empleado("Carlos Méndez", "carlitos", "hash123");
        empleadoFalso.setId(3L);

        when(empleadoService.buscarEmpleadoPorId(3L)).thenReturn(empleadoFalso);

        // WHEN & THEN
        mockMvc.perform(get("/api/empleados/{id}", 3L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.nombre").value("Carlos Méndez"))
                .andExpect(jsonPath("$.usuario").value("carlitos"))
                .andDo(document("buscar-empleado-por-id"));
    }

    @Test
    @DisplayName("GET /{id}: lanza ServletException cuando el empleado no existe")
    public void debeLanzarExcepcionCuandoEmpleadoNoExiste() {

        // GIVEN
        when(empleadoService.buscarEmpleadoPorId(99L))
                .thenThrow(new RuntimeException("Empleado no encontrado con id: 99"));

        // WHEN & THEN
        jakarta.servlet.ServletException exception = Assertions.assertThrows(
                jakarta.servlet.ServletException.class, () -> {
                    mockMvc.perform(get("/api/empleados/{id}", 99L));
                }
        );
        Assertions.assertTrue(exception.getCause().getMessage().contains("Empleado no encontrado con id: 99"));
    }

    // ─── PUT /api/empleados/actualizar/{id} ───────────────────────────────────

    @Test
    @DisplayName("PUT /actualizar/{id}: responde 200 OK al actualizar un empleado existente")
    public void debeActualizarEmpleadoYDocumentar() throws Exception {

        // GIVEN
        Empleado datosNuevos = new Empleado("Carlos Actualizado", "carlitos_v2", "nuevo_hash");

        Empleado empleadoExistente = new Empleado("Carlos Méndez", "carlitos", "hash_viejo");
        empleadoExistente.setId(1L);

        Empleado empleadoActualizado = new Empleado("Carlos Actualizado", "carlitos_v2", "nuevo_hash");
        empleadoActualizado.setId(1L);

        when(empleadoService.buscarEmpleadoPorId(1L)).thenReturn(empleadoExistente);
        when(empleadoService.actualizarEmpleado(any(Empleado.class))).thenReturn(empleadoActualizado);

        // WHEN & THEN
        mockMvc.perform(put("/api/empleados/actualizar/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(datosNuevos)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Carlos Actualizado"))
                .andExpect(jsonPath("$.usuario").value("carlitos_v2"))
                .andDo(document("actualizar-empleado"));
    }

    @Test
    @DisplayName("PUT /actualizar/{id}: lanza ServletException cuando el empleado a actualizar no existe")
    public void debeLanzarExcepcionAlActualizarEmpleadoInexistente() {

        // GIVEN
        Empleado datosNuevos = new Empleado("Fantasma", "ghost", "pass");

        when(empleadoService.buscarEmpleadoPorId(99L))
                .thenThrow(new RuntimeException("Empleado no encontrado con id: 99"));

        // WHEN & THEN
        jakarta.servlet.ServletException exception = Assertions.assertThrows(
                jakarta.servlet.ServletException.class, () -> {
                    mockMvc.perform(put("/api/empleados/actualizar/{id}", 99L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(datosNuevos)));
                }
        );
        Assertions.assertTrue(exception.getCause().getMessage().contains("Empleado no encontrado con id: 99"));
    }

    // ─── DELETE /api/empleados/eliminar/{id} ──────────────────────────────────

    @Test
    @DisplayName("DELETE /eliminar/{id}: responde 200 OK al dar de baja un empleado existente")
    public void debeEliminarEmpleadoYDocumentar() throws Exception {

        // GIVEN
        doNothing().when(empleadoService).eliminarEmpleado(1L);

        // WHEN & THEN
        mockMvc.perform(delete("/api/empleados/eliminar/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().string("empleado con el id 1 fue eliminado correctamente"))
                .andDo(document("eliminar-empleado"));
    }

    @Test
    @DisplayName("DELETE /eliminar/{id}: lanza ServletException cuando el empleado no existe")
    public void debeLanzarExcepcionAlEliminarEmpleadoInexistente() {

        // GIVEN
        doThrow(new RuntimeException("Empleado no encontrado con id: 99"))
                .when(empleadoService).eliminarEmpleado(99L);

        // WHEN & THEN
        jakarta.servlet.ServletException exception = Assertions.assertThrows(
                jakarta.servlet.ServletException.class, () -> {
                    mockMvc.perform(delete("/api/empleados/eliminar/{id}", 99L));
                }
        );
        Assertions.assertTrue(exception.getCause().getMessage().contains("Empleado no encontrado con id: 99"));
    }
}
