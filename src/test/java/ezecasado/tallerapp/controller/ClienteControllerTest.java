package ezecasado.tallerapp.controller;


import ezecasado.tallerapp.models.Cliente;
import ezecasado.tallerapp.service.ClienteService;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ClienteController.class)
@AutoConfigureRestDocs(outputDir = "target/generated-snippets")
@DisplayName("Tests de controlador - ClienteController")
public class ClienteControllerTest {

    @Autowired
    private MockMvc mockMvc;


    @MockitoBean
    private ClienteService clienteService;
    @Autowired
    private ObjectMapper objectMapper;

    // ─── POST /api/clientes/crear ─────────────────────────────────────────────

    @Test
    @DisplayName("POST /crear: responde 201 CREATED al registrar un cliente válido")
    public void debeCrearClienteYDocumentar() throws Exception {

        // GIVEN
        Cliente clienteNuevo = new Cliente();
        clienteNuevo.setNombre("Juan");
        clienteNuevo.setApellido("González");
        clienteNuevo.setEmail("juan@mail.com");
        clienteNuevo.setTelefono("1123456789");

        Cliente clienteGuardado = new Cliente();
        clienteGuardado.setId(1L);
        clienteGuardado.setNombre("Juan");
        clienteGuardado.setApellido("González");
        clienteGuardado.setEmail("juan@mail.com");
        clienteGuardado.setTelefono("1123456789");
        clienteGuardado.setActivo(true);

        when(clienteService.registrarCliente(any(Cliente.class))).thenReturn(clienteGuardado);

        // WHEN & THEN
        mockMvc.perform(post("/api/clientes/crear")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(clienteNuevo)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Juan"))
                .andExpect(jsonPath("$.email").value("juan@mail.com"))
                .andExpect(jsonPath("$.activo").value(true))
                .andDo(document("crear-cliente"));
    }

    @Test
    @DisplayName("POST /crear: lanza ServletException cuando el email ya existe en el sistema")
    public void debeRechazarClienteConEmailDuplicado() {

        // GIVEN
        Cliente clienteConEmailExistente = new Cliente();
        clienteConEmailExistente.setNombre("Pedro");
        clienteConEmailExistente.setEmail("juan@mail.com");

        when(clienteService.registrarCliente(any(Cliente.class)))
                .thenThrow(new IllegalArgumentException("El email ya existe en el sistema"));

        // WHEN & THEN
        jakarta.servlet.ServletException exception = Assertions.assertThrows(
                jakarta.servlet.ServletException.class, () -> {
                    mockMvc.perform(post("/api/clientes/crear")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(clienteConEmailExistente)));
                }
        );
        Assertions.assertTrue(exception.getCause().getMessage().contains("El email ya existe en el sistema"));
    }

    // ─── GET /api/clientes/listar ─────────────────────────────────────────────

    @Test
    @DisplayName("GET /listar: responde 200 OK con la lista de clientes activos")
    public void debeListarClientesActivosYDocumentar() throws Exception {

        // GIVEN
        Cliente clienteFalso = new Cliente();
        clienteFalso.setId(1L);
        clienteFalso.setNombre("Juan");
        clienteFalso.setApellido("González");
        clienteFalso.setEmail("juan@mail.com");
        clienteFalso.setActivo(true);

        when(clienteService.listarClientesActivos()).thenReturn(List.of(clienteFalso));

        // WHEN & THEN
        mockMvc.perform(get("/api/clientes/listar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].nombre").value("Juan"))
                .andExpect(jsonPath("$[0].activo").value(true))
                .andDo(document("listar-clientes"));
    }

    @Test
    @DisplayName("GET /listar: responde 200 OK con lista vacía cuando no hay clientes activos")
    public void debeRetornarListaVaciaCuandoNoHayClientesActivos() throws Exception {

        // GIVEN
        when(clienteService.listarClientesActivos()).thenReturn(List.of());

        // WHEN & THEN
        mockMvc.perform(get("/api/clientes/listar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty())
                .andDo(document("listar-clientes-vacio"));
    }

    // ─── GET /api/clientes/cliente/{id} ──────────────────────────────────────

    @Test
    @DisplayName("GET /cliente/{id}: responde 200 OK con el cliente encontrado")
    public void debeBuscarClientePorIdYDocumentar() throws Exception {

        // GIVEN
        Cliente clienteFalso = new Cliente();
        clienteFalso.setId(5L);
        clienteFalso.setNombre("María");
        clienteFalso.setApellido("López");
        clienteFalso.setEmail("maria@mail.com");
        clienteFalso.setActivo(true);

        when(clienteService.buscarCliente(5L)).thenReturn(clienteFalso);

        // WHEN & THEN
        mockMvc.perform(get("/api/clientes/cliente/{id}", 5L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.nombre").value("María"))
                .andExpect(jsonPath("$.email").value("maria@mail.com"))
                .andDo(document("buscar-cliente-por-id"));
    }

    @Test
    @DisplayName("GET /cliente/{id}: lanza ServletException cuando el cliente no existe")
    public void debeLanzarExcepcionCuandoClienteNoExiste() {

        // GIVEN
        when(clienteService.buscarCliente(99L))
                .thenThrow(new RuntimeException("Cliente no encontrado con id: 99"));

        // WHEN & THEN
        jakarta.servlet.ServletException exception = Assertions.assertThrows(
                jakarta.servlet.ServletException.class, () -> {
                    mockMvc.perform(get("/api/clientes/cliente/{id}", 99L));
                }
        );
        Assertions.assertTrue(exception.getCause().getMessage().contains("Cliente no encontrado con id: 99"));
    }

    // ─── GET /api/clientes/eliminar/{id} ─────────────────────────────────────

    @Test
    @DisplayName("GET /eliminar/{id}: responde 200 OK al dar de baja un cliente existente")
    public void debeEliminarClienteYDocumentar() throws Exception {

        // GIVEN
        doNothing().when(clienteService).eliminarCliente(1L);

        // WHEN & THEN
        mockMvc.perform(get("/api/clientes/eliminar/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().string("Cliente con el id: 1 fue borrado dado de baja correctamente del sistema"))
                .andDo(document("eliminar-cliente"));
    }

    @Test
    @DisplayName("GET /eliminar/{id}: lanza ServletException cuando el cliente no existe")
    public void debeLanzarExcepcionAlEliminarClienteInexistente() {

        // GIVEN
        doThrow(new RuntimeException("Cliente no encontrado con id: 99"))
                .when(clienteService).eliminarCliente(99L);

        // WHEN & THEN
        jakarta.servlet.ServletException exception = Assertions.assertThrows(
                jakarta.servlet.ServletException.class, () -> {
                    mockMvc.perform(get("/api/clientes/eliminar/{id}", 99L));
                }
        );
        Assertions.assertTrue(exception.getCause().getMessage().contains("Cliente no encontrado con id: 99"));
    }

    // ─── PUT /api/clientes/actualizar/{id} ───────────────────────────────────

    @Test
    @DisplayName("PUT /actualizar/{id}: responde 200 OK al actualizar un cliente existente")
    public void debeActualizarClienteYDocumentar() throws Exception {

        // GIVEN
        Cliente datosNuevos = new Cliente();
        datosNuevos.setNombre("Juan Actualizado");
        datosNuevos.setApellido("González");
        datosNuevos.setEmail("juanupdate@mail.com");
        datosNuevos.setTelefono("1199999999");

        Cliente clienteExistente = new Cliente();
        clienteExistente.setId(1L);
        clienteExistente.setNombre("Juan");
        clienteExistente.setApellido("González");
        clienteExistente.setEmail("juan@mail.com");
        clienteExistente.setTelefono("1123456789");
        clienteExistente.setActivo(true);

        Cliente clienteActualizado = new Cliente();
        clienteActualizado.setId(1L);
        clienteActualizado.setNombre("Juan Actualizado");
        clienteActualizado.setApellido("González");
        clienteActualizado.setEmail("juanupdate@mail.com");
        clienteActualizado.setTelefono("1199999999");
        clienteActualizado.setActivo(true);

        when(clienteService.buscarCliente(1L)).thenReturn(clienteExistente);
        when(clienteService.actualizarCliente(any(Cliente.class))).thenReturn(clienteActualizado);

        // WHEN & THEN
        mockMvc.perform(put("/api/clientes/actualizar/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(datosNuevos)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Juan Actualizado"))
                .andExpect(jsonPath("$.email").value("juanupdate@mail.com"))
                .andDo(document("actualizar-cliente"));
    }

    @Test
    @DisplayName("PUT /actualizar/{id}: lanza ServletException cuando el cliente a actualizar no existe")
    public void debeLanzarExcepcionAlActualizarClienteInexistente() {

        // GIVEN
        Cliente datosNuevos = new Cliente();
        datosNuevos.setNombre("Fantasma");

        when(clienteService.buscarCliente(99L))
                .thenThrow(new RuntimeException("Cliente no encontrado con id: 99"));

        // WHEN & THEN
        jakarta.servlet.ServletException exception = Assertions.assertThrows(
                jakarta.servlet.ServletException.class, () -> {
                    mockMvc.perform(put("/api/clientes/actualizar/{id}", 99L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(datosNuevos)));
                }
        );
        Assertions.assertTrue(exception.getCause().getMessage().contains("Cliente no encontrado con id: 99"));
    }
}
