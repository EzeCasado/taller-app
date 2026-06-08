package ezecasado.tallerapp.controller;


import ezecasado.tallerapp.models.Empleado;
import ezecasado.tallerapp.models.Modificacion;
import ezecasado.tallerapp.models.Vehiculo;
import ezecasado.tallerapp.service.ModificacionService;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ModificacionController.class)
@AutoConfigureRestDocs(outputDir = "target/generated-snippets")
@DisplayName("Tests de controlador - ModificacionController")
public class ModificacionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ModificacionService modificacionService;

    // ─── POST /api/modificaciones/crear ──────────────────────────────────────

    @Test
    @DisplayName("POST /crear: responde 201 CREATED al registrar una modificación válida")
    public void debeCrearModificacionYDocumentar() throws Exception {

        // GIVEN
        Vehiculo vehiculo = new Vehiculo();
        vehiculo.setId(1L);
        vehiculo.setPatente("ABC123");

        Empleado empleado = new Empleado("Técnico", "tecnico1", "hash123");
        empleado.setId(5L);

        Modificacion modNueva = new Modificacion();
        modNueva.setNombre("Kit turbo");
        modNueva.setCosto(new BigDecimal("3500.00"));
        modNueva.setFecha(LocalDate.of(2026, 5, 15));
        modNueva.setSigueInstalada(true);
        modNueva.setVehiculo(vehiculo);
        modNueva.setEmpleado(empleado);

        Modificacion modGuardada = new Modificacion();
        modGuardada.setId(20L);
        modGuardada.setNombre("Kit turbo");
        modGuardada.setCosto(new BigDecimal("3500.00"));
        modGuardada.setFecha(LocalDate.of(2026, 5, 15));
        modGuardada.setSigueInstalada(true);
        modGuardada.setActiva(true);
        modGuardada.setVehiculo(vehiculo);
        modGuardada.setEmpleado(empleado);

        when(modificacionService.crearModificacion(any(Modificacion.class))).thenReturn(modGuardada);

        // WHEN & THEN
        mockMvc.perform(post("/api/modificaciones/crear")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(modNueva)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(20))
                .andExpect(jsonPath("$.nombre").value("Kit turbo"))
                .andExpect(jsonPath("$.activa").value(true))
                .andExpect(jsonPath("$.sigueInstalada").value(true))
                .andDo(document("crear-modificacion"));
    }

    @Test
    @DisplayName("POST /crear: lanza ServletException cuando el vehículo asociado no existe")
    public void debeRechazarModificacionConVehiculoInexistente() {

        // GIVEN
        Modificacion modSinVehiculo = new Modificacion();
        modSinVehiculo.setNombre("Spoiler");
        modSinVehiculo.setCosto(new BigDecimal("1500.00"));
        modSinVehiculo.setFecha(LocalDate.of(2026, 6, 1));

        when(modificacionService.crearModificacion(any(Modificacion.class)))
                .thenThrow(new IllegalArgumentException("Vehiculo no encontrado en el sistema"));

        // WHEN & THEN
        jakarta.servlet.ServletException exception = Assertions.assertThrows(
                jakarta.servlet.ServletException.class, () -> {
                    mockMvc.perform(post("/api/modificaciones/crear")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(modSinVehiculo)));
                }
        );
        Assertions.assertTrue(exception.getCause().getMessage().contains("Vehiculo no encontrado en el sistema"));
    }

    // ─── GET /api/modificaciones/vehiculo/{id} ────────────────────────────────

    @Test
    @DisplayName("GET /vehiculo/{id}: responde 200 OK con el historial de modificaciones del vehículo")
    public void debeObtenerModificacionesPorVehiculoYDocumentar() throws Exception {

        // GIVEN
        Long idVehiculo = 1L;

        Modificacion modFalsa = new Modificacion();
        modFalsa.setId(20L);
        modFalsa.setNombre("Kit turbo");
        modFalsa.setCosto(new BigDecimal("3500.00"));
        modFalsa.setFecha(LocalDate.of(2026, 5, 15));
        modFalsa.setActiva(true);
        modFalsa.setSigueInstalada(true);

        when(modificacionService.listarModificacionesPorVehiculo(idVehiculo))
                .thenReturn(List.of(modFalsa));

        // WHEN & THEN
        mockMvc.perform(get("/api/modificaciones/vehiculo/{id}", idVehiculo))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(20))
                .andExpect(jsonPath("$[0].nombre").value("Kit turbo"))
                .andExpect(jsonPath("$[0].costo").value(3500.00))
                .andExpect(jsonPath("$[0].activa").value(true))
                .andDo(document("listar-modificaciones-por-vehiculo"));
    }

    @Test
    @DisplayName("GET /vehiculo/{id}: responde 200 OK con lista vacía si el vehículo no tiene modificaciones")
    public void debeRetornarListaVaciaCuandoVehiculoSinModificaciones() throws Exception {

        // GIVEN
        when(modificacionService.listarModificacionesPorVehiculo(99L)).thenReturn(List.of());

        // WHEN & THEN
        mockMvc.perform(get("/api/modificaciones/vehiculo/{id}", 99L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty())
                .andDo(document("listar-modificaciones-vacio"));
    }

    // ─── DELETE /api/modificaciones/eliminar/{id} ────────────────────────────

    @Test
    @DisplayName("DELETE /eliminar/{id}: responde 200 OK al dar de baja una modificación existente")
    public void debeEliminarModificacionYDocumentar() throws Exception {

        // GIVEN
        doNothing().when(modificacionService).eliminarModificacion(20L);

        // WHEN & THEN
        mockMvc.perform(delete("/api/modificaciones/eliminar/{id}", 20L))
                .andExpect(status().isOk())
                .andExpect(content().string("Modificacion con el id: 20 fue eliminada correctamente del sistema"))
                .andDo(document("eliminar-modificacion"));
    }

    @Test
    @DisplayName("DELETE /eliminar/{id}: lanza ServletException cuando la modificación no existe")
    public void debeLanzarExcepcionAlEliminarModificacionInexistente() {

        // GIVEN
        doThrow(new RuntimeException("Modificación no encontrada con el ID: 99"))
                .when(modificacionService).eliminarModificacion(99L);

        // WHEN & THEN
        jakarta.servlet.ServletException exception = Assertions.assertThrows(
                jakarta.servlet.ServletException.class, () -> {
                    mockMvc.perform(delete("/api/modificaciones/eliminar/{id}", 99L));
                }
        );
        Assertions.assertTrue(exception.getCause().getMessage().contains("Modificación no encontrada con el ID: 99"));
    }
}
