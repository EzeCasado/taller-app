package ezecasado.tallerapp.service;

import ezecasado.tallerapp.DTO.GastoVehiculoDTO;
import ezecasado.tallerapp.models.Cliente;
import ezecasado.tallerapp.models.Vehiculo;
import ezecasado.tallerapp.repository.ClienteRepository;
import ezecasado.tallerapp.repository.MantenimientoRepository;
import ezecasado.tallerapp.repository.ModificacionRepository;
import ezecasado.tallerapp.repository.vehiculoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests unitarios - VehiculoService")
class VehiculoServiceTest {

    @Mock
    private vehiculoRepository vehiculoRepo;

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private MantenimientoRepository mantenimientoRepository;

    @Mock
    private ModificacionRepository modificacionRepository;

    @InjectMocks
    private VehiculoService vehiculoService;

    private Cliente clienteExistente;
    private Vehiculo vehiculoValido;

    @BeforeEach
    void setUp() {
        clienteExistente = new Cliente(true, "Pérez", "perez@mail.com", "Mario", "Ok", "113456789");
        clienteExistente.setId(10L);

        vehiculoValido = new Vehiculo(true, 2020, clienteExistente, "Sin daños", 50000, "Toyota", "Corolla", "1.8L", "ABC123");
        vehiculoValido.setId(1L);
    }

    // ─── crearVehiculo ────────────────────────────────────────────────────────

    @Test
    @DisplayName("crearVehiculo: guarda el vehículo cuando el cliente existe y la patente es nueva")
    void crearVehiculo_clienteExistePatenteNueva_guardaVehiculo() {
        // GIVEN
        when(clienteRepository.findById(10L)).thenReturn(Optional.of(clienteExistente));
        when(vehiculoRepo.findByPatente("ABC123")).thenReturn(Optional.empty());
        when(vehiculoRepo.save(vehiculoValido)).thenReturn(vehiculoValido);

        // WHEN
        Vehiculo resultado = vehiculoService.crearVehiculo(vehiculoValido);

        // THEN
        assertThat(resultado).isNotNull();
        assertThat(resultado.getPatente()).isEqualTo("ABC123");
        verify(vehiculoRepo, times(1)).save(vehiculoValido);
    }

    @Test
    @DisplayName("crearVehiculo: lanza IllegalArgumentException cuando el cliente no existe")
    void crearVehiculo_clienteNoExiste_lanzaExcepcion() {
        // GIVEN
        when(clienteRepository.findById(10L)).thenReturn(Optional.empty());

        // WHEN / THEN
        assertThatThrownBy(() -> vehiculoService.crearVehiculo(vehiculoValido))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Cliente no encontrado en el sistema");

        verify(vehiculoRepo, never()).save(any());
    }

    @Test
    @DisplayName("crearVehiculo: lanza IllegalArgumentException cuando la patente ya existe")
    void crearVehiculo_patenteExistente_lanzaExcepcion() {
        // GIVEN
        when(clienteRepository.findById(10L)).thenReturn(Optional.of(clienteExistente));
        when(vehiculoRepo.findByPatente("ABC123")).thenReturn(Optional.of(vehiculoValido));

        // WHEN / THEN
        assertThatThrownBy(() -> vehiculoService.crearVehiculo(vehiculoValido))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Patente ya existe en el sistema");

        verify(vehiculoRepo, never()).save(any());
    }

    // ─── eliminarVehiculo ─────────────────────────────────────────────────────

    @Test
    @DisplayName("eliminarVehiculo: marca activo=false (soft delete) cuando el vehículo existe")
    void eliminarVehiculo_vehiculoExiste_realizaBorradoLogico() {
        // GIVEN
        vehiculoValido.setActivo(true);
        when(vehiculoRepo.findById(1L)).thenReturn(Optional.of(vehiculoValido));

        // WHEN
        vehiculoService.eliminarVehiculo(1L);

        // THEN
        assertThat(vehiculoValido
                .getActivo()).isFalse();
        verify(vehiculoRepo, times(1)).save(vehiculoValido);
    }

    @Test
    @DisplayName("eliminarVehiculo: lanza RuntimeException cuando el vehículo no existe")
    void eliminarVehiculo_vehiculoNoExiste_lanzaExcepcion() {
        // GIVEN
        when(vehiculoRepo.findById(99L)).thenReturn(Optional.empty());

        // WHEN / THEN
        assertThatThrownBy(() -> vehiculoService.eliminarVehiculo(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Vehiculo no encontrado con el id");

        verify(vehiculoRepo, never()).save(any());
    }

    // ─── listarVehiculosActivos ───────────────────────────────────────────────

    @Test
    @DisplayName("listarVehiculosActivos: retorna lista de vehículos activos")
    void listarVehiculosActivos_retornaListaCorrecta() {
        // GIVEN
        when(vehiculoRepo.findByActivo(true)).thenReturn(List.of(vehiculoValido));

        // WHEN
        List<Vehiculo> resultado = vehiculoService.listarVehiculosActivos();

        // THEN
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getActivo()).isTrue();
    }

    // ─── getGastoVehiculoDTO ──────────────────────────────────────────────────

    @Test
    @DisplayName("getGastoVehiculoDTO: suma correctamente mantenimiento + modificaciones")
    void getGastoVehiculoDTO_conCostos_sumaCorrecto() {
        // GIVEN
        when(mantenimientoRepository.sumarCostoTotalMantenimiento(1L)).thenReturn(new BigDecimal("1500.00"));
        when(modificacionRepository.sumarCostoTotalModificacion(1L)).thenReturn(new BigDecimal("2500.00"));

        // WHEN
        GastoVehiculoDTO dto = vehiculoService.getGastoVehiculoDTO(1L);

        // THEN
        assertThat(dto.getCostoTotalMantenimiento()).isEqualByComparingTo("1500.00");
        assertThat(dto.getCostoTotalModificacion()).isEqualByComparingTo("2500.00");
        assertThat(dto.getCostoTotal()).isEqualByComparingTo("4000.00");
    }

    @Test
    @DisplayName("getGastoVehiculoDTO: trata null de mantenimiento como ZERO")
    void getGastoVehiculoDTO_mantenimientoNull_trataCeroMantenimiento() {
        // GIVEN
        when(mantenimientoRepository.sumarCostoTotalMantenimiento(1L)).thenReturn(null);
        when(modificacionRepository.sumarCostoTotalModificacion(1L)).thenReturn(new BigDecimal("500.00"));

        // WHEN
        GastoVehiculoDTO dto = vehiculoService.getGastoVehiculoDTO(1L);

        // THEN
        assertThat(dto.getCostoTotalMantenimiento()).isEqualByComparingTo("0.00");
        assertThat(dto.getCostoTotal()).isEqualByComparingTo("500.00");
    }

    @Test
    @DisplayName("getGastoVehiculoDTO: trata null de modificaciones como ZERO")
    void getGastoVehiculoDTO_modificacionesNull_trataCeroModificaciones() {
        // GIVEN
        when(mantenimientoRepository.sumarCostoTotalMantenimiento(1L)).thenReturn(new BigDecimal("800.00"));
        when(modificacionRepository.sumarCostoTotalModificacion(1L)).thenReturn(null);

        // WHEN
        GastoVehiculoDTO dto = vehiculoService.getGastoVehiculoDTO(1L);

        // THEN
        assertThat(dto.getCostoTotalModificacion()).isEqualByComparingTo("0.00");
        assertThat(dto.getCostoTotal()).isEqualByComparingTo("800.00");
    }

    @Test
    @DisplayName("getGastoVehiculoDTO: retorna total ZERO cuando ambos costos son null")
    void getGastoVehiculoDTO_ambosCostosNull_retornaTotalCero() {
        // GIVEN
        when(mantenimientoRepository.sumarCostoTotalMantenimiento(1L)).thenReturn(null);
        when(modificacionRepository.sumarCostoTotalModificacion(1L)).thenReturn(null);

        // WHEN
        GastoVehiculoDTO dto = vehiculoService.getGastoVehiculoDTO(1L);

        // THEN
        assertThat(dto.getCostoTotal()).isEqualByComparingTo("0.00");
        assertThat(dto.getCostoTotalMantenimiento()).isEqualByComparingTo("0.00");
        assertThat(dto.getCostoTotalModificacion()).isEqualByComparingTo("0.00");
    }
}
