package ezecasado.tallerapp.service;

import ezecasado.tallerapp.models.Empleado;
import ezecasado.tallerapp.repository.EmpleadoRepository;
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
@DisplayName("Tests unitarios - EmpleadoService")
class EmpleadoServiceTest {

    @Mock
    private EmpleadoRepository empleadoRepository;

    @InjectMocks
    private EmpleadoService empleadoService;

    private Empleado empleadoValido;

    @BeforeEach
    void setUp() {
        empleadoValido = new Empleado("Carlos Méndez", "carlitos", "hash_seguro_123");
        empleadoValido.setId(1L);
    }

    // ─── crearEmpleado ────────────────────────────────────────────────────────

    @Test
    @DisplayName("crearEmpleado: guarda y retorna el empleado cuando el usuario no existe")
    void crearEmpleado_usuarioNuevo_guardaYRetornaEmpleado() {
        // GIVEN
        when(empleadoRepository.findByUsuario("carlitos")).thenReturn(Optional.empty());
        when(empleadoRepository.save(empleadoValido)).thenReturn(empleadoValido);

        // WHEN
        Empleado resultado = empleadoService.crearEmpleado(empleadoValido);

        // THEN
        assertThat(resultado).isNotNull();
        assertThat(resultado.getUsuario()).isEqualTo("carlitos");
        assertThat(resultado.getActivo()).isTrue();
        verify(empleadoRepository, times(1)).save(empleadoValido);
    }

    @Test
    @DisplayName("crearEmpleado: lanza IllegalArgumentException cuando el usuario ya existe")
    void crearEmpleado_usuarioDuplicado_lanzaExcepcion() {
        // GIVEN
        when(empleadoRepository.findByUsuario("carlitos")).thenReturn(Optional.of(empleadoValido));

        // WHEN / THEN
        assertThatThrownBy(() -> empleadoService.crearEmpleado(empleadoValido))
                .isInstanceOf(org.springframework.dao.DataIntegrityViolationException.class)
                .hasMessage("El usuario carlitos ya existe en el sistema");

        // Nunca debe intentar guardar
        verify(empleadoRepository, never()).save(any());
    }

    @Test
    @DisplayName("crearEmpleado: el empleado nuevo tiene activo = true por defecto")
    void crearEmpleado_nuevoEmpleado_activoEsTrue() {
        // GIVEN
        when(empleadoRepository.findByUsuario("carlitos")).thenReturn(Optional.empty());
        when(empleadoRepository.save(any())).thenReturn(empleadoValido);

        // WHEN
        Empleado resultado = empleadoService.crearEmpleado(empleadoValido);

        // THEN
        assertThat(resultado.getActivo()).isTrue();
    }

    // ─── listarEmpleadosActivos ───────────────────────────────────────────────

    @Test
    @DisplayName("listarEmpleadosActivos: retorna lista de empleados activos")
    void listarEmpleadosActivos_retornaListaCorrecta() {
        // GIVEN
        List<Empleado> empleadosActivos = List.of(empleadoValido);
        when(empleadoRepository.findByActivo(true)).thenReturn(empleadosActivos);

        // WHEN
        List<Empleado> resultado = empleadoService.listarEmpleadosActivos();

        // THEN
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getUsuario()).isEqualTo("carlitos");
        verify(empleadoRepository, times(1)).findByActivo(true);
    }

    @Test
    @DisplayName("listarEmpleadosActivos: retorna lista vacía cuando no hay empleados activos")
    void listarEmpleadosActivos_sinEmpleados_retornaListaVacia() {
        // GIVEN
        when(empleadoRepository.findByActivo(true)).thenReturn(List.of());

        // WHEN
        List<Empleado> resultado = empleadoService.listarEmpleadosActivos();

        // THEN
        assertThat(resultado).isEmpty();
    }
}
