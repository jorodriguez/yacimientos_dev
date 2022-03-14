package sia.modelo.combustible.vo;

import java.math.BigDecimal;
import java.sql.Timestamp;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Consumo {

    private Timestamp fecha;
    private int vale;
    private String estacion;
    private String tarjeta;
    private String tipoComb;
    private BigDecimal importe;
    private String usuario;
    private String depto;
    private String concepto;
    private String placa;
    private String idMovimiento;
    private String tipo;
    private BigDecimal precio;
    private int kmIni;
    private int kmFin;
    private BigDecimal litros;
    private BigDecimal rendimiento;
    private String iva;
    private String rfcEstacion;
    private String estado;
    private String ciudad;
    private String colonia;
    private String domicilio;
    private int year;
    private int month;
    private int day;
    private String time;
    private int sgVehiculo;
    private String codOficina;
    //

    
    public String toString() {
	return "Consumo [fecha=" + fecha + ", vale=" + vale + ", estacion=" + estacion + ", tarjeta=" + tarjeta
		+ ", tipoComb=" + tipoComb + ", importe=" + importe + ", usuario=" + usuario + ", depto=" + depto
		+ ", concepto=" + concepto + ", placa=" + placa + ", idMovimiento=" + idMovimiento + ", tipo=" + tipo
		+ ", precio=" + precio + ", kmIni=" + kmIni + ", kmFin=" + kmFin + ", litros=" + litros
		+ ", rendimiento=" + rendimiento + ", iva=" + iva + ", rfcEstacion=" + rfcEstacion + ", estado="
		+ estado + ", ciudad=" + ciudad + ", colonia=" + colonia + ", domicilio=" + domicilio + ", year=" + year
		+ ", month=" + month + ", day=" + day + ", time=" + time + "]";
    }

}
