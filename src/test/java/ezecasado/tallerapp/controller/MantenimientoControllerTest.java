package ezecasado.tallerapp.controller;


import ezecasado.tallerapp.models.Empleado;
import ezecasado.tallerapp.models.Mantenimiento;
import ezecasado.tallerapp.models.Vehiculo;
import ezecasado.tallerapp.service.MantenimientoService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.restdocs.test.autoconfigure.AutoConfigureRestDocs;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MantenimientoController.class)
@AutoConfigureRestDocs(outputDir = "target/generated-snippets")
@DisplayName("Tests de controlador - MantenimientoController")
public class MantenimientoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private MantenimientoService mantenimientoService;

    // ─── POST /api/mantenimientos/crear → snippet: "registrar-mantenimiento" ─

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("POST /crear: responde 201 CREATED al registrar un mantenimiento válido")
    public void debeRegistrarMantenimientoYDocumentar() throws Exception {

        // GIVEN — armamos vehículo y empleado de referencia
        Vehiculo vehiculo = new Vehiculo();
        vehiculo.setId(1L);
        vehiculo.setPatente("AA123BB");
        vehiculo.setMotor("1.6L");

        Empleado empleado = new Empleado("Técnico", "tecnico1", "hash123");
        empleado.setId(5L);

        // JSON que llega en el body del POST
        Mantenimiento manNuevo = new Mantenimiento();
        manNuevo.setDescripcion("Cambio de aceite y filtros");
        manNuevo.setCosto(new BigDecimal("1200.00"));
        manNuevo.setFecha(LocalDate.of(2026, 6, 7));
        manNuevo.setKilometraje(55000);
        manNuevo.setComentario("Sin novedad. Aceite 5W30.");
        manNuevo.setVehiculo(vehiculo);
        manNuevo.setEmpleado(empleado);

        // Lo que el service devuelve una vez persistido (tiene id asignado)
        Mantenimiento manGuardado = new Mantenimiento();
        manGuardado.setId(10L);
        manGuardado.setDescripcion("Cambio de aceite y filtros");
        manGuardado.setCosto(new BigDecimal("1200.00"));
        manGuardado.setFecha(LocalDate.of(2026, 6, 7));
        manGuardado.setKilometraje(55000);
        manGuardado.setComentario("Sin novedad. Aceite 5W30.");
        manGuardado.setActivo(true);
        manGuardado.setVehiculo(vehiculo);
        manGuardado.setEmpleado(empleado);

        when(mantenimientoService.agregarMantenimiento(any(Mantenimiento.class)))
                .thenReturn(manGuardado);

        // WHEN & THEN
        mockMvc.perform(post("/api/mantenimientos/crear")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(manNuevo)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.descripcion").value("Cambio de aceite y filtros"))
                .andExpect(jsonPath("$.costo").value(1200.00))
                .andExpect(jsonPath("$.comentario").value("Sin novedad. Aceite 5W30."))
                .andExpect(jsonPath("$.activo").value(true))
                .andDo(document("registrar-mantenimiento"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("POST /crear: lanza ServletException cuando el vehículo asociado no existe")
    public void debeRechazarMantenimientoConVehiculoInexistente() throws Exception {

        // GIVEN
        Mantenimiento manSinVehiculo = new Mantenimiento();
        manSinVehiculo.setDescripcion("Cambio de correa");
        manSinVehiculo.setCosto(new BigDecimal("5000.00"));
        manSinVehiculo.setFecha(LocalDate.of(2026, 6, 7));
        manSinVehiculo.setComentario("Revisión completa");

        when(mantenimientoService.agregarMantenimiento(any(Mantenimiento.class)))
                .thenThrow(new org.springframework.dao.DataIntegrityViolationException("Vehiculo no encontrado en el sistema"));

        // WHEN & THEN
        mockMvc.perform(post("/api/mantenimientos/crear")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(manSinVehiculo)))
                .andExpect(status().is4xxClientError());
    }

    // ─── GET /api/mantenimientos/vehiculo/{id} ────────────────────────────────

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("GET /vehiculo/{id}: responde 200 OK con el historial del vehículo")
    public void debeObtenerHistorialMantenimientoYDocumentar() throws Exception {

        // GIVEN
        Long idVehiculo = 1L;

        Mantenimiento manFalso = new Mantenimiento();
        manFalso.setId(10L);
        manFalso.setDescripcion("Cambio de aceite y filtros");
        manFalso.setCosto(new BigDecimal("1200.00"));
        manFalso.setFecha(LocalDate.of(2026, 6, 7));
        manFalso.setKilometraje(55000);
        manFalso.setComentario("Sin novedad. Aceite 5W30.");
        manFalso.setActivo(true);

        when(mantenimientoService.obtenerMantenimientosPorVehiculo(idVehiculo))
                .thenReturn(List.of(manFalso));

        // WHEN & THEN
        mockMvc.perform(get("/api/mantenimientos/vehiculo/{id}", idVehiculo))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(10))
                .andExpect(jsonPath("$[0].descripcion").value("Cambio de aceite y filtros"))
                .andExpect(jsonPath("$[0].comentario").value("Sin novedad. Aceite 5W30."))
                .andExpect(jsonPath("$[0].costo").value(1200.00))
                .andDo(document("historial-mantenimiento-por-vehiculo"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("GET /vehiculo/{id}: responde 200 OK con lista vacía si el vehículo no tiene historial")
    public void debeRetornarListaVaciaCuandoVehiculoSinHistorial() throws Exception {

        // GIVEN
        when(mantenimientoService.obtenerMantenimientosPorVehiculo(99L)).thenReturn(List.of());

        // WHEN & THEN
        mockMvc.perform(get("/api/mantenimientos/vehiculo/{id}", 99L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty())
                .andDo(document("historial-mantenimiento-vacio"));
    }

    // ─── DELETE /api/mantenimientos/eliminar/{id} ─────────────────────────────

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("DELETE /eliminar/{id}: responde 200 OK al dar de baja un mantenimiento existente")
    public void debeEliminarMantenimientoYDocumentar() throws Exception {

        // GIVEN
        doNothing().when(mantenimientoService).eliminarMantenimiento(10L);

        // WHEN & THEN
        mockMvc.perform(delete("/api/mantenimientos/eliminar/{id}", 10L))
                .andExpect(status().isOk())
                .andExpect(content().string("Matenimiento id 10 fue eliminado correctamente"))
                .andDo(document("eliminar-mantenimiento"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("DELETE /eliminar/{id}: lanza ServletException cuando el mantenimiento no existe")
    public void debeLanzarExcepcionAlEliminarMantenimientoInexistente() throws Exception {

        // GIVEN
        doThrow(new org.springframework.dao.DataIntegrityViolationException("No existe el mantenimiento con el id: 99"))
                .when(mantenimientoService).eliminarMantenimiento(99L);

        // WHEN & THEN
        mockMvc.perform(delete("/api/mantenimientos/eliminar/{id}", 99L))
                .andExpect(status().is4xxClientError());
    }
}