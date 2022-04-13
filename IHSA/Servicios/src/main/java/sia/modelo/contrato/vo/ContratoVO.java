/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo.contrato.vo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import sia.modelo.gerencia.vo.GerenciaVo;
import sia.modelo.proveedor.Vo.ProveedorVo;
import sia.modelo.sgl.vo.AdjuntoVO;
import sia.modelo.sgl.vo.OrdenVO;
import sia.modelo.sgl.vo.Vo;
import sia.servicios.sistema.vo.CatalogoContratoVo;

/**
 *
 * @author mluis
 */
@Getter
@Setter
public class ContratoVO extends Vo {

    private int proveedor;
    private String nombreProveedor;
    private String numero;
    private Date fechaFirma;
    private Date fechaInicio;
    private Date fechaVencimiento;
    private double monto;
    private int idTipo;
    private int idMoneda;
    private String moneda;
    private String tipo;
    private String vigencia;
    private Double porcentajeDeduccion;
    private int idClasificacion;
    private String clasificacion;
    private int idGerencia;
    private String gerencia;
    private int idEstado;
    private String estado;
    private int idServicio;
    private String servicio;
    private int idCampo;
    private String campo;
    private int idContratoRelacionado;
    private boolean editar;
    private boolean formalizado;
    private double subTotalOcs;
    private boolean selected;
    //
    private AdjuntoVO adjuntoVO = new AdjuntoVO();
    private ProveedorVo proveedorVo = new ProveedorVo();
    //
    private List<ContratoDocumentoVo> listaConvenioDocumento = new ArrayList<>();
    private List<CatalogoContratoVo> listaConvenioCondicion = new ArrayList<>();
    private List<CatalogoContratoVo> listaConvenioHito = new ArrayList<>();
    private List<AdjuntoVO> listaArchivoConvenio = new ArrayList<>();
    private List<GerenciaVo> listaGerencia = new ArrayList<>();
    //
    private List<OrdenVO> listaOrdenConvenio = new ArrayList<>();
    private List<ContratoVO> listaContratoRelacionado = new ArrayList<>();
    private List<ConvenioArticuloVo> listaArticulo = new ArrayList<>();
    private List<ExhortoVo> exhortos = new ArrayList<>();
    private List<RhConvenioDocumentoVo> doctosRh = new ArrayList<>();
// Código que añadimos a la clase Persona. Sobreescritura del método equals ejemplo aprenderaprogramar.com
//
    private long numeroContratosRelacionados;
    private long totalOCS;
    private long mes;
    private long anio;
    private double totalMes;
    //
    private String nombreTab;
    //
    private int diasRestantes;
    private double remanente;
    private double acumulado;
    private double totalContratoConModificatorios;    
    private List<ContratoEvaluacionVo> listaConvenioEvals = new ArrayList<>();
    private List<EvaluacionVo> listaEvalsPendientes = new ArrayList<>();
    
    private String compania;
    
    private long exhortosEnviados;
    private String codigoContratoRelacionado;
    private String nombreContratoRelacionado;
    private String totalFormas;
    //
    private String codigoExhorto;
    private String analista;
    private int solFinAvance; 
    private int actaEntAvance; 
    private int edoCuentaAvance; 
    private int validContAvance; 
    private int validRhAvance; 

    
    public boolean equals(Object obj) {
	boolean v = false;
	if (obj instanceof ContratoVO) {
	    ContratoVO contr = (ContratoVO) obj;
	    if (contr.getId().intValue() == super.getId()) {
		v = true;
	    }
	}
	return v;

    }

    
    public int hashCode() {
	int hash = 7;
	return hash;
    }
}
