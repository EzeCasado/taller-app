package ezecasado.tallerapp.service;

import ezecasado.tallerapp.models.Empleado;
import ezecasado.tallerapp.models.Mantenimiento;
import ezecasado.tallerapp.models.Vehiculo;
import ezecasado.tallerapp.repository.MantenimientoRepository;
import ezecasado.tallerapp.repository.vehiculoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests unitarios - MantenimientoService")
class MantenimientoServiceTest {

    @Mock
    private MantenimientoRepository mantenimientoRepository;

    @Mock
    private vehiculoRepository vehiculoRepo;

    @InjectMocks
    private MantenimientoService mantenimientoService;

    private Vehiculo vehiculoExistente;
    private Mantenimiento mantenimientoValido;

    @BeforeEach
    void setUp() {
        vehiculoExistente = new Vehiculo();
        vehiculoExistente.setId(1L);
        vehiculoExistente.setPatente("ABC123");
        vehiculoExistente.setActivo(true);

        Empleado empleado = new Empleado("Técnico", "tecnico1", "hash123");
        empleado.setId(5L);

        mantenimientoValido = new Mantenimiento(
                true,
                "Sin novedad",
                new BigDecimal("1200.00"),
                "Cambio de aceite y filtros",
                empleado,
                LocalDate.of(2026, 6, 7),
                55000,
                vehiculoExistente
        );
        mantenimientoValido.setId(10L);
    }

    // ─── agregarMantenimiento ─────────────────────────────────────────────────

    @Test
    @DisplayName("agregarMantenimiento: guarda y retorna cuando el vehículo existe")
    void agregarMantenimiento_vehiculoExiste_guardaYRetorna() {
        // GIVEN
        when(vehiculoRepo.findById(1L)).thenReturn(Optional.of(vehiculoExistente));
        when(mantenimientoRepository.save(mantenimientoValido)).thenReturn(mantenimientoValido);

        // WHEN
        Mantenimiento resultado = mantenimientoService.agregarMantenimiento(mantenimientoValido);

        // THEN
        assertThat(resultado).isNotNull();
        assertThat(resultado.getDescripcion()).isEqualTo("Cambio de aceite y filtros");
        assertThat(resultado.getCosto()).isEqualByComparingTo("1200.00");
        verify(mantenimientoRepository, times(1)).save(mantenimientoValido);
    }

    @Test
    @DisplayName("agregarMantenimiento: lanza IllegalArgumentException cuando el vehículo no existe")
    void agregarMantenimiento_vehiculoNoExiste_lanzaExcepcion() {
        // GIVEN
        when(vehiculoRepo.findById(1L)).thenReturn(Optional.empty());

        // WHEN / THEN
        assertThatThrownBy(() -> mantenimientoService.agregarMantenimiento(mantenimientoValido))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Vehiculo no encontrado en el sistema");

        verify(mantenimientoRepository, never()).save(any());
    }

    // ─── obtenerMantenimientosPorVehiculo ─────────────────────────────────────

    @Test
    @DisplayName("obtenerMantenimientosPorVehiculo: retorna el historial del vehículo")
    void obtenerMantenimientosPorVehiculo_retornaHistorial() {
        // GIVEN
        List<Mantenimiento> historial = List.of(mantenimientoValido);
        when(mantenimientoRepository.findByVehiculoId(1L)).thenReturn(historial);

        // WHEN
        List<Mantenimiento> resultado = mantenimientoService.obtenerMantenimientosPorVehiculo(1L);

        // THEN
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getId()).isEqualTo(10L);
        verify(mantenimientoRepository, times(1)).findByVehiculoId(1L);
    }

    @Test
    @DisplayName("obtenerMantenimientosPorVehiculo: retorna lista vacía si no hay historial")
    void obtenerMantenimientosPorVehiculo_sinHistorial_retornaListaVacia() {
        // GIVEN
        when(mantenimientoRepository.findByVehiculoId(1L)).thenReturn(List.of());

        // WHEN
        List<Mantenimiento> resultado = mantenimientoService.obtenerMantenimientosPorVehiculo(1L);

        // THEN
        assertThat(resultado).isEmpty();
    }

    // ─── eliminarMantenimiento ────────────────────────────────────────────────

    @Test
    @DisplayName("eliminarMantenimiento: marca activo=false (soft delete) cuando existe")
    void eliminarMantenimiento_existe_realizaBorradoLogico() {
        // GIVEN
        mantenimientoValido.setActivo(true);
        when(mantenimientoRepository.findById(10L)).thenReturn(Optional.of(mantenimientoValido));

        // WHEN
        mantenimientoService.eliminarMantenimiento(10L);

        // THEN
        assertThat(mantenimientoValido.isActivo()).isFalse();
        verify(mantenimientoRepository, times(1)).save(mantenimientoValido);
    }

    @Test
    @DisplayName("eliminarMantenimiento: lanza IllegalArgumentException cuando no existe")
    void eliminarMantenimiento_noExiste_lanzaExcepcion() {
        // GIVEN
        when(mantenimientoRepository.findById(99L)).thenReturn(Optional.empty());

        // WHEN / THEN
        assertThatThrownBy(() -> mantenimientoService.eliminarMantenimiento(99L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("No existe el mantenimiento con el id: 99");

        verify(mantenimientoRepository, never()).save(any());
    }
}
