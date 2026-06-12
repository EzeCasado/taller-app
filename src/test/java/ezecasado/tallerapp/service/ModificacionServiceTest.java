package ezecasado.tallerapp.service;

import ezecasado.tallerapp.models.Empleado;
import ezecasado.tallerapp.models.Modificacion;
import ezecasado.tallerapp.models.Vehiculo;
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
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests unitarios - ModificacionService")
class ModificacionServiceTest {

    @Mock
    private ModificacionRepository modificacionRepository;

    @Mock
    private vehiculoRepository vehiculoRepository;

    @InjectMocks
    private ModificacionService modificacionService;

    private Modificacion modificacionValida;

    @BeforeEach
    void setUp() {
        Vehiculo vehiculo = new Vehiculo();
        vehiculo.setId(1L);
        vehiculo.setPatente("XYZ999");

        Empleado empleado = new Empleado("Instalador", "instal1", "hash456");
        empleado.setId(3L);

        modificacionValida = new Modificacion(
                true,
                new BigDecimal("3500.00"),
                empleado,
                LocalDate.of(2026, 5, 15),
                "Kit turbo",
                true,
                vehiculo
        );
        modificacionValida.setId(20L);
    }

    // ─── crearModificacion ────────────────────────────────────────────────────

    @Test
    @DisplayName("crearModificacion: guarda y retorna la modificación")
    void crearModificacion_datosValidos_guardaYRetorna() {
        // GIVEN
        when(vehiculoRepository.existsById(any())).thenReturn(false);
        when(modificacionRepository.save(modificacionValida)).thenReturn(modificacionValida);

        // WHEN
        Modificacion resultado = modificacionService.crearModificacion(modificacionValida);

        // THEN
        assertThat(resultado).isNotNull();
        assertThat(resultado.getNombre()).isEqualTo("Kit turbo");
        assertThat(resultado.getCosto()).isEqualByComparingTo("3500.00");
        assertThat(resultado.getActiva()).isTrue();
        verify(modificacionRepository, times(1)).save(modificacionValida);
    }

    @Test
    @DisplayName("crearModificacion: la modificación nueva tiene sigueInstalada = true")
    void crearModificacion_nueva_sigueInstaladaEsTrue() {
        // GIVEN
        when(vehiculoRepository.existsById(any())).thenReturn(false);
        when(modificacionRepository.save(any())).thenReturn(modificacionValida);

        // WHEN
        Modificacion resultado = modificacionService.crearModificacion(modificacionValida);

        // THEN
        assertThat(resultado.getSigueInstalada()).isTrue();
    }

    // ─── eliminarModificacion ─────────────────────────────────────────────────

    @Test
    @DisplayName("eliminarModificacion: marca activa=false (soft delete) cuando existe")
    void eliminarModificacion_existe_realizaBorradoLogico() {
        // GIVEN
        modificacionValida.setActiva(true);
        when(modificacionRepository.findById(20L)).thenReturn(Optional.of(modificacionValida));

        // WHEN
        modificacionService.eliminarModificacion(20L);

        // THEN
        assertThat(modificacionValida.getActiva()).isFalse();
        verify(modificacionRepository, times(1)).save(modificacionValida);
    }

    @Test
    @DisplayName("eliminarModificacion: lanza RuntimeException cuando la modificación no existe")
    void eliminarModificacion_noExiste_lanzaExcepcion() {
        // GIVEN
        when(modificacionRepository.findById(99L)).thenReturn(Optional.empty());

        // WHEN / THEN
        assertThatThrownBy(() -> modificacionService.eliminarModificacion(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Modificación no encontrada con el ID: 99");

        verify(modificacionRepository, never()).save(any());
    }

    // ─── listarModificacionesActivas ──────────────────────────────────────────

    @Test
    @DisplayName("listarModificacionesActivas: retorna lista de modificaciones activas")
    void listarModificacionesActivas_retornaListaCorrecta() {
        // GIVEN
        List<Modificacion> activas = List.of(modificacionValida);
        when(modificacionRepository.findByActiva(true)).thenReturn(activas);

        // WHEN
        List<Modificacion> resultado = modificacionService.listarModificacionesActivas();

        // THEN
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getNombre()).isEqualTo("Kit turbo");
        verify(modificacionRepository, times(1)).findByActiva(true);
    }

    @Test
    @DisplayName("listarModificacionesActivas: retorna lista vacía si no hay modificaciones activas")
    void listarModificacionesActivas_sinModificaciones_retornaListaVacia() {
        // GIVEN
        when(modificacionRepository.findByActiva(true)).thenReturn(List.of());

        // WHEN
        List<Modificacion> resultado = modificacionService.listarModificacionesActivas();

        // THEN
        assertThat(resultado).isEmpty();
    }
}
