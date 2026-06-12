package ezecasado.tallerapp.controller;


import ezecasado.tallerapp.DTO.GastoVehiculoDTO;
import ezecasado.tallerapp.models.Cliente;
import ezecasado.tallerapp.models.Vehiculo;
import ezecasado.tallerapp.service.VehiculoService;
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

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private VehiculoService vehiculoService;

    // ─── GET /api/vehiculos/listar ────────────────────────────────────────────

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("GET /listar: responde 200 OK con la lista de vehículos activos")
    public void debeListarVehiculosActivosYDocumentar() throws Exception {

        // GIVEN
        Cliente cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNombre("Mario");
        cliente.setApellido("Pérez");

        Vehiculo vehiculoFalso = new Vehiculo();
        vehiculoFalso.setId(1L);
        vehiculoFalso.setPatente("AA123BB");
        vehiculoFalso.setMarca("Volkswagen");
        vehiculoFalso.setModelo("Gol");
        vehiculoFalso.setAnio(2020);
        vehiculoFalso.setKilometraje(45000);
        vehiculoFalso.setMotor("1.6L");
        vehiculoFalso.setActivo(true);
        vehiculoFalso.setCliente(cliente);

        when(vehiculoService.listarVehiculosActivos()).thenReturn(List.of(vehiculoFalso));

        // WHEN & THEN
        mockMvc.perform(get("/api/vehiculos/listar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].patente").value("AA123BB"))
                .andExpect(jsonPath("$[0].motor").value("1.6L"))
                .andExpect(jsonPath("$[0].activo").value(true))
                .andDo(document("listar-vehiculos"));
    }

    // ─── POST /api/vehiculos/crear ────────────────────────────────────────────

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("POST /crear: responde 201 CREATED al registrar un vehículo válido")
    public void debeCrearVehiculoYDocumentar() throws Exception {

        // GIVEN
        Cliente cliente = new Cliente();
        cliente.setId(1L);

        Vehiculo vehiculoNuevo = new Vehiculo();
        vehiculoNuevo.setPatente("ZZ999YY");
        vehiculoNuevo.setMarca("Ford");
        vehiculoNuevo.setModelo("Ka");
        vehiculoNuevo.setAnio(2022);
        vehiculoNuevo.setKilometraje(10000);
        vehiculoNuevo.setMotor("1.0L");
        vehiculoNuevo.setCliente(cliente);

        Vehiculo vehiculoGuardado = new Vehiculo();
        vehiculoGuardado.setId(2L);
        vehiculoGuardado.setPatente("ZZ999YY");
        vehiculoGuardado.setMarca("Ford");
        vehiculoGuardado.setModelo("Ka");
        vehiculoGuardado.setAnio(2022);
        vehiculoGuardado.setKilometraje(10000);
        vehiculoGuardado.setMotor("1.0L");
        vehiculoGuardado.setActivo(true);
        vehiculoGuardado.setCliente(cliente);

        when(vehiculoService.crearVehiculo(any(Vehiculo.class))).thenReturn(vehiculoGuardado);

        // WHEN & THEN
        mockMvc.perform(post("/api/vehiculos/crear")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(vehiculoNuevo)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.patente").value("ZZ999YY"))
                .andExpect(jsonPath("$.motor").value("1.0L"))
                .andExpect(jsonPath("$.activo").value(true))
                .andDo(document("crear-vehiculo"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("POST /crear: lanza ServletException cuando la patente ya existe")
    public void debeRechazarVehiculoConPatenteDuplicada() throws Exception {

        // GIVEN
        Vehiculo vehiculoDuplicado = new Vehiculo();
        vehiculoDuplicado.setPatente("AA123BB");
        vehiculoDuplicado.setMarca("Fiat");
        vehiculoDuplicado.setModelo("Uno");
        vehiculoDuplicado.setMotor("1.4L");

        when(vehiculoService.crearVehiculo(any(Vehiculo.class)))
                .thenThrow(new org.springframework.dao.DataIntegrityViolationException("Patente ya existe en el sistema"));

        // WHEN & THEN
        mockMvc.perform(post("/api/vehiculos/crear")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(vehiculoDuplicado)))
                .andExpect(status().is4xxClientError());
    }

    // ─── GET /api/vehiculos/{id}/gastos ───────────────────────────────────────

    @Test
    @WithMockUser(roles = "ADMIN")
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

    // ─── DELETE /api/vehiculos/eliminar/{id} ──────────────────────────────────

    @Test
    @WithMockUser(roles = "ADMIN")
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
    @WithMockUser(roles = "ADMIN")
    @DisplayName("DELETE /eliminar/{id}: lanza ServletException cuando el vehículo no existe")
    public void debeLanzarExcepcionAlEliminarVehiculoInexistente() throws Exception {

        // GIVEN
        doThrow(new ezecasado.tallerapp.exception.ResourceNotFoundException("Vehiculo no encontrado con el id99"))
                .when(vehiculoService).eliminarVehiculo(99L);

        // WHEN & THEN
        mockMvc.perform(delete("/api/vehiculos/eliminar/{id}", 99L))
                .andExpect(status().is4xxClientError());
    }
}
