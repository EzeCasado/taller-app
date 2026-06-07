package ezecasado.tallerapp.controller;


import ezecasado.tallerapp.DTO.GastoVehiculoDTO;
import ezecasado.tallerapp.models.Vehiculo;
import ezecasado.tallerapp.service.VehiculoService;
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
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(VehiculoController.class)
@AutoConfigureRestDocs(outputDir = "target/generated-snippets")
@DisplayName("Tests de controlador - VehiculoController")
public class VehiculoControllerTest {

    @Autowired
    private MockMvc mockMvc;


    @MockitoBean
    private VehiculoService vehiculoService;
    @Autowired
    private ObjectMapper objectMapper;

    // ─── GET /api/vehiculos/listar ────────────────────────────────────────────

    @Test
    @DisplayName("GET /listar: responde 200 OK con la lista de vehículos activos")
    public void debeListarVehiculosActivosYDocumentar() throws Exception {

        // GIVEN
        Vehiculo autoFalso = new Vehiculo();
        autoFalso.setId(1L);
        autoFalso.setPatente("AA123BB");
        autoFalso.setMarca("Volkswagen");
        autoFalso.setModelo("Gol");
        autoFalso.setAnio(2020);
        autoFalso.setKilometraje(45000);
        autoFalso.setActivo(true);

        when(vehiculoService.listarVehiculosActivos()).thenReturn(List.of(autoFalso));

        // WHEN & THEN
        mockMvc.perform(get("/api/vehiculos/listar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].patente").value("AA123BB"))
                .andExpect(jsonPath("$[0].activo").value(true))
                .andDo(document("listar-vehiculos"));
    }

    @Test
    @DisplayName("GET /listar: responde 200 OK con lista vacía cuando no hay vehículos activos")
    public void debeListarVehiculosActivos_listaVacia() throws Exception {

        // GIVEN
        when(vehiculoService.listarVehiculosActivos()).thenReturn(List.of());

        // WHEN & THEN
        mockMvc.perform(get("/api/vehiculos/listar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty())
                .andDo(document("listar-vehiculos-vacio"));
    }

    // ─── POST /api/vehiculos/crear ────────────────────────────────────────────

    @Test
    @DisplayName("POST /crear: responde 201 CREATED al registrar un vehículo válido")
    public void debeCrearVehiculoYDocumentar() throws Exception {

        // GIVEN
        Vehiculo nuevoVehiculo = new Vehiculo();
        nuevoVehiculo.setPatente("ZZ999YY");
        nuevoVehiculo.setMarca("Ford");
        nuevoVehiculo.setModelo("Ka");
        nuevoVehiculo.setAnio(2022);
        nuevoVehiculo.setKilometraje(10000);
        nuevoVehiculo.setMotor("1.0L");

        Vehiculo vehiculoGuardado = new Vehiculo();
        vehiculoGuardado.setId(2L);
        vehiculoGuardado.setPatente("ZZ999YY");
        vehiculoGuardado.setMarca("Ford");
        vehiculoGuardado.setModelo("Ka");
        vehiculoGuardado.setAnio(2022);
        vehiculoGuardado.setKilometraje(10000);
        vehiculoGuardado.setMotor("1.0L");
        vehiculoGuardado.setActivo(true);

        when(vehiculoService.crearVehiculo(any(Vehiculo.class))).thenReturn(vehiculoGuardado);

        // WHEN & THEN
        mockMvc.perform(post("/api/vehiculos/crear")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nuevoVehiculo)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.patente").value("ZZ999YY"))
                .andExpect(jsonPath("$.activo").value(true))
                .andDo(document("crear-vehiculo"));
    }

    @Test
    @DisplayName("POST /crear: responde 500 cuando el service lanza excepción por patente duplicada")
    public void debeRechazarVehiculoConPatenteDuplicada() throws Exception {

        // GIVEN
        Vehiculo vehiculoConPatenteExistente = new Vehiculo();
        vehiculoConPatenteExistente.setPatente("AA123BB");
        vehiculoConPatenteExistente.setMarca("Fiat");
        vehiculoConPatenteExistente.setModelo("Uno");

        when(vehiculoService.crearVehiculo(any(Vehiculo.class)))
                .thenThrow(new IllegalArgumentException("Patente ya existe en el sistema"));

       // WHEN & THEN (Esperamos que explote el ServletException debido al entorno aislado)
        jakarta.servlet.ServletException exception = org.junit.jupiter.api.Assertions.assertThrows(
                jakarta.servlet.ServletException.class, () -> {
                    mockMvc.perform(post("/api/vehiculos/crear")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(vehiculoConPatenteExistente)));
                }
        );

        // Verificamos que la causa real sea el mensaje que mockeamos
        org.junit.jupiter.api.Assertions.assertTrue(exception.getCause().getMessage().contains("Patente ya existe"));
    }

    // ─── DELETE /api/vehiculos/eliminar/{id} ──────────────────────────────────

    @Test
    @DisplayName("DELETE /eliminar/{id}: responde 200 OK al dar de baja un vehículo existente")
    public void debeEliminarVehiculoYDocumentar() throws Exception {

        // GIVEN
        doNothing().when(vehiculoService).eliminarVehiculo(1L);

        // WHEN & THEN
        mockMvc.perform(delete("/api/vehiculos/eliminar/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().string("El vehiculo con ID 1 fue dado de baja correctamente"))
                .andDo(document("eliminar-vehiculo"));
    }

    @Test
    @DisplayName("DELETE /eliminar/{id}: responde 500 cuando el vehículo no existe")
    public void debeRechazarEliminarVehiculoInexistente() throws Exception {

        // GIVEN
        doThrow(new RuntimeException("Vehiculo no encontrado con el id99"))
                .when(vehiculoService).eliminarVehiculo(99L);

        // WHEN & THEN
        jakarta.servlet.ServletException exception = org.junit.jupiter.api.Assertions.assertThrows(
                jakarta.servlet.ServletException.class, () -> {
                    mockMvc.perform(delete("/api/vehiculos/eliminar/{id}", 99L));
                }
        );

        org.junit.jupiter.api.Assertions.assertTrue(exception.getCause().getMessage().contains("Vehiculo no encontrado"));
    }

    // ─── GET /api/vehiculos/{id}/gastos ───────────────────────────────────────

    @Test
    @DisplayName("GET /{id}/gastos: responde 200 OK con el resumen de gastos del vehículo")
    public void debeObtenerGastosVehiculoYDocumentar() throws Exception {

        // GIVEN
        GastoVehiculoDTO gastoDTO = new GastoVehiculoDTO(
                new BigDecimal("4000.00"),
                new BigDecimal("1500.00"),
                new BigDecimal("2500.00")
        );

        when(vehiculoService.getGastoVehiculoDTO(1L)).thenReturn(gastoDTO);

        // WHEN & THEN
        mockMvc.perform(get("/api/vehiculos/{id}/gastos", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.costoTotal").value(4000.00))
                .andExpect(jsonPath("$.costoTotalMantenimiento").value(1500.00))
                .andExpect(jsonPath("$.costoTotalModificacion").value(2500.00))
                .andDo(document("gastos-vehiculo"));
    }

    @Test
    @DisplayName("GET /{id}/gastos: responde 200 OK con costos en cero cuando no hay registros")
    public void debeObtenerGastosVehiculoConTodoEnCero() throws Exception {

        // GIVEN
        GastoVehiculoDTO gastosVacios = new GastoVehiculoDTO(
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO
        );

        when(vehiculoService.getGastoVehiculoDTO(2L)).thenReturn(gastosVacios);

        // WHEN & THEN
        mockMvc.perform(get("/api/vehiculos/{id}/gastos", 2L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.costoTotal").value(0))
                .andDo(document("gastos-vehiculo-sin-registros"));
    }
}
