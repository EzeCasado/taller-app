package ezecasado.tallerapp.service;

import ezecasado.tallerapp.models.Cliente;
import ezecasado.tallerapp.repository.ClienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests unitarios - ClienteService")
class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @InjectMocks
    private ClienteService clienteService;

    private Cliente clienteValido;

    @BeforeEach
    void setUp() {
        clienteValido = new Cliente(true, "González", "juan@mail.com", "Juan", "Sin observaciones", "1123456789");
        clienteValido.setId(1L);
    }

    // ─── registrarCliente ─────────────────────────────────────────────────────

    @Test
    @DisplayName("registrarCliente: guarda y retorna el cliente cuando el email no existe")
    void registrarCliente_emailNuevo_guardaYRetornaCliente() {
        // GIVEN
        when(clienteRepository.findByEmail("juan@mail.com")).thenReturn(Optional.empty());
        when(clienteRepository.save(clienteValido)).thenReturn(clienteValido);

        // WHEN
        Cliente resultado = clienteService.registrarCliente(clienteValido);

        // THEN
        assertThat(resultado).isNotNull();
        assertThat(resultado.getEmail()).isEqualTo("juan@mail.com");
        verify(clienteRepository, times(1)).save(clienteValido);
    }

    @Test
    @DisplayName("registrarCliente: lanza IllegalArgumentException cuando el email ya existe")
    void registrarCliente_emailDuplicado_lanzaExcepcion() {
        // GIVEN
        when(clienteRepository.findByEmail("juan@mail.com")).thenReturn(Optional.of(clienteValido));

        // WHEN / THEN
        assertThatThrownBy(() -> clienteService.registrarCliente(clienteValido))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("El email ya existe en el sistema");

        // Verificamos que NUNCA intentó guardar
        verify(clienteRepository, never()).save(any());
    }

    // ─── listarClientesActivos ────────────────────────────────────────────────

    @Test
    @DisplayName("listarClientesActivos: retorna lista de clientes activos")
    void listarClientesActivos_retornaListaCorrecta() {
        // GIVEN
        List<Cliente> clientesActivos = List.of(clienteValido);
        when(clienteRepository.findByActivoTrue()).thenReturn(clientesActivos);

        // WHEN
        List<Cliente> resultado = clienteService.listarClientesActivos();

        // THEN
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).isActivo()).isTrue();
        verify(clienteRepository, times(1)).findByActivoTrue();
    }

    @Test
    @DisplayName("listarClientesActivos: retorna lista vacía cuando no hay clientes activos")
    void listarClientesActivos_sinClientes_retornaListaVacia() {
        // GIVEN
        when(clienteRepository.findByActivoTrue()).thenReturn(List.of());

        // WHEN
        List<Cliente> resultado = clienteService.listarClientesActivos();

        // THEN
        assertThat(resultado).isEmpty();
    }
}
