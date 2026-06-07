package ezecasado.tallerapp.controller;


import ezecasado.tallerapp.models.Mantenimiento;
import ezecasado.tallerapp.service.MantenimientoService;
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

@WebMvcTest(MantenimientoController.class)
@AutoConfigureRestDocs(outputDir = "target/generated-snippets")
@DisplayName("Tests de controlador - MantenimientoController")
public class MantenimientoControllerTest {

    @Autowired
    private MockMvc mockMvc;


    @MockitoBean
    private MantenimientoService mantenimientoService;
    @Autowired
    private ObjectMapper objectMapper;

    // ─── GET /api/mantenimientos/vehiculo/{id} ────────────────────────────────

    @Test
    @DisplayName("GET /vehiculo/{id}: responde 200 OK con el historial del vehículo")
    public void debeObtenerMantenimientosPorVehiculoYDocumentar() throws Exception {

        // GIVEN
        Long idVehiculo = 1L;

        Mantenimiento manFalso = new Mantenimiento();
        manFalso.setId(10L);
        manFalso.setDescripcion("Cambio de Aceite");
        manFalso.setCosto(BigDecimal.valueOf(1200));
        manFalso.setFecha(LocalDate.parse("2026-06-07"));
        manFalso.setKilometraje(55000);
        manFalso.setActivo(true);

        when(mantenimientoService.obtenerMantenimientosPorVehiculo(idVehiculo))
                .thenReturn(List.of(manFalso));

        // WHEN & THEN
        mockMvc.perform(get("/api/mantenimientos/vehiculo/{id}", idVehiculo))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(10))
                .andExpect(jsonPath("$[0].descripcion").value("Cambio de Aceite"))
                .andExpect(jsonPath("$[0].costo").value(1200))
                .andDo(document("listar-mantenimientos-por-vehiculo"));
    }

    @Test
    @DisplayName("GET /vehiculo/{id}: responde 200 OK con lista vacía si el vehículo no tiene historial")
    public void debeRetornarListaVaciaCuandoVehiculoSinHistorial() throws Exception {

        // GIVEN
        when(mantenimientoService.obtenerMantenimientosPorVehiculo(99L)).thenReturn(List.of());

        // WHEN & THEN
        mockMvc.perform(get("/api/mantenimientos/vehiculo/{id}", 99L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty())
                .andDo(document("listar-mantenimientos-vacio"));
    }

    // ─── POST /api/mantenimientos/crear ───────────────────────────────────────

    @Test
    @DisplayName("POST /crear: responde 201 CREATED al registrar un mantenimiento válido")
    public void debeCrearMantenimientoYDocumentar() throws Exception {

        // GIVEN
        // El JSON que llega en el body del POST
        Mantenimiento nuevoMan = new Mantenimiento();
        nuevoMan.setDescripcion("Cambio de correa de distribución");
        nuevoMan.setCosto(new BigDecimal("8500.00"));
        nuevoMan.setFecha(LocalDate.of(2026, 6, 7));
        nuevoMan.setKilometraje(60000);
        nuevoMan.setComentario("Se recomienda revisión en 20.000 km");

        // Lo que el service devuelve una vez guardado (tiene id asignado)
        Mantenimiento manGuardado = new Mantenimiento();
        manGuardado.setId(11L);
        manGuardado.setDescripcion("Cambio de correa de distribución");
        manGuardado.setCosto(new BigDecimal("8500.00"));
        manGuardado.setFecha(LocalDate.of(2026, 6, 7));
        manGuardado.setKilometraje(60000);
        manGuardado.setComentario("Se recomienda revisión en 20.000 km");
        manGuardado.setActivo(true);

        // Registramos el JavaTimeModule para que ObjectMapper serialice LocalDate


        when(mantenimientoService.agregarMantenimiento(any(Mantenimiento.class)))
                .thenReturn(manGuardado);

        // WHEN & THEN
        mockMvc.perform(post("/api/mantenimientos/crear")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nuevoMan)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(11))
                .andExpect(jsonPath("$.descripcion").value("Cambio de correa de distribución"))
                .andExpect(jsonPath("$.costo").value(8500.00))
                .andExpect(jsonPath("$.activo").value(true))
                .andDo(document("crear-mantenimiento"));
    }

    @Test
    @DisplayName("POST /crear: responde 500 cuando el vehículo asociado no existe")
    public void debeRechazarMantenimientoConVehiculoInexistente() throws Exception {

        // GIVEN
        Mantenimiento manSinVehiculo = new Mantenimiento();
        manSinVehiculo.setDescripcion("Cambio de aceite");
        manSinVehiculo.setCosto(new BigDecimal("500.00"));
        manSinVehiculo.setFecha(LocalDate.of(2026, 6, 7));


        when(mantenimientoService.agregarMantenimiento(any(Mantenimiento.class)))
                .thenThrow(new IllegalArgumentException("Vehiculo no encontrado en el sistema"));

        // WHEN & THEN: Atrapamos la excepción del entorno aislado
        jakarta.servlet.ServletException exception = org.junit.jupiter.api.Assertions.assertThrows(
                jakarta.servlet.ServletException.class, () -> {
                    mockMvc.perform(post("/api/mantenimientos/crear")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(manSinVehiculo)));
                }
        );

        // Verificamos que la causa real sea el mensaje del Service
        org.junit.jupiter.api.Assertions.assertTrue(exception.getCause().getMessage().contains("Vehiculo no encontrado"));
    }

    // ─── DELETE /api/mantenimientos/eliminar/{id} ─────────────────────────────

    @Test
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
    @DisplayName("DELETE /eliminar/{id}: responde 500 cuando el mantenimiento no existe")
    public void debeRechazarEliminarMantenimientoInexistente() throws Exception {

        // GIVEN
        doThrow(new IllegalArgumentException("No existe el mantenimiento con el id: 99"))
                .when(mantenimientoService).eliminarMantenimiento(99L);

        // WHEN & THEN: Atrapamos el ServletException
        jakarta.servlet.ServletException exception = org.junit.jupiter.api.Assertions.assertThrows(
                jakarta.servlet.ServletException.class, () -> {
                    mockMvc.perform(delete("/api/mantenimientos/eliminar/{id}", 99L));
                }
        );

        // Verificamos el mensaje de error interno
        org.junit.jupiter.api.Assertions.assertTrue(exception.getCause().getMessage().contains("No existe el mantenimiento"));

    }
}