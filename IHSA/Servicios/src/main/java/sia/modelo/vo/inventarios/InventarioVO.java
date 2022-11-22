package sia.modelo.vo.inventarios;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import sia.modelo.sgl.vo.Vo;

/**
 *
 * @author Aplimovil SA de CV Modifico : Joel Rodriguez Fecha: 29-junio-2020
 * Motivo: se agregaron los campos: precio y moneda al reporte de existencias en
 * el modulo de inventarios
 */
@Getter
@Setter
public class InventarioVO extends Vo {

    private static final long serialVersionUID = 1L;

//    private Integer id;
    private Integer almacenId;
    private String almacenNombre;
    private Integer articuloId;
    private Integer unidadId;
    private String articuloNombre;
    private String articuloUnidad;
    private double numeroUnidades;
    private double minimoUnidades;
    private Date fechaUltimaRevision;
    private double maximoDeInventario;
    private double puntoDeReorden;
    private String ubicacion;
    private double totalUnidades;
    private String codigoInt;
    //anexos para el reporte de existencias
    private double precio;
    private double importe;
    private String moneda;

    private String folio;
    private String proveedor;
    private String tipoMov;
    private double unidadesMov;
    private Date FechaMov;
    private String gerenciaMov;
    private String usuarioMov;

    //
    private List<CeldaVo> celdas = new ArrayList<>();
    
    private int diferenciaDias;

    public InventarioVO() {
    }

    public InventarioVO(Integer id, Integer almacenId, String almacenNombre,
            Integer articuloId, String articuloNombre, double numeroUnidades,
            double minimoUnidades, Date fechaUltimaRevision) {
        this.id = id;
        this.almacenId = almacenId;
        this.almacenNombre = almacenNombre;
        this.articuloId = articuloId;
        this.articuloNombre = articuloNombre;
        this.numeroUnidades = numeroUnidades;
        this.minimoUnidades = minimoUnidades;
        this.fechaUltimaRevision = fechaUltimaRevision;
    }

    public InventarioVO(Integer id, Integer almacenId, String almacenNombre, Integer articuloId, String articuloNombre,
            double numeroUnidades, double minimoUnidades, double maximoDeInventario, double puntoDeReorden,
            Date fechaUltimaRevision) {
        this.id = id;
        this.almacenId = almacenId;
        this.almacenNombre = almacenNombre;
        this.articuloId = articuloId;
        this.articuloNombre = articuloNombre;
        this.numeroUnidades = numeroUnidades;
        this.minimoUnidades = minimoUnidades;
        this.maximoDeInventario = maximoDeInventario;
        this.puntoDeReorden = puntoDeReorden;
        this.fechaUltimaRevision = fechaUltimaRevision;
    }
}
