package ezecasado.tallerapp.DTO;

import java.math.BigDecimal;

public class GastoVehiculoDTO {

    private BigDecimal costoTotalMantenimiento;
    private BigDecimal costoTotalModificacion;
    private BigDecimal costoTotal;


    public GastoVehiculoDTO(BigDecimal costoTotal, BigDecimal costoTotalMantenimiento, BigDecimal costoTotalModificacion) {
        this.costoTotal = costoTotal;
        this.costoTotalMantenimiento = costoTotalMantenimiento;
        this.costoTotalModificacion = costoTotalModificacion;
    }


    public BigDecimal getCostoTotal() {
        return costoTotal;
    }

    public void setCostoTotal(BigDecimal costoTotal) {
        this.costoTotal = costoTotal;
    }

    public BigDecimal getCostoTotalMantenimiento() {
        return costoTotalMantenimiento;
    }

    public void setCostoTotalMantenimiento(BigDecimal costoTotalMantenimiento) {
        this.costoTotalMantenimiento = costoTotalMantenimiento;
    }

    public BigDecimal getCostoTotalModificacion() {
        return costoTotalModificacion;
    }

    public void setCostoTotalModificacion(BigDecimal costoTotalModificacion) {
        this.costoTotalModificacion = costoTotalModificacion;
    }
}
