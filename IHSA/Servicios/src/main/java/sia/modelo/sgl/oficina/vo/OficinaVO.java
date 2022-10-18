/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo.sgl.oficina.vo;

import lombok.Getter;
import lombok.Setter;
import sia.modelo.sgl.vo.Vo;

/**
 *
 * @author mluis
 */
@Getter
@Setter
public class OficinaVO extends Vo {

    private String numeroTelefono;
    private String colonia;
    private String calle;
    private String numeroExterior;
    private String numeroInterior;
    private String numeroPiso;
    private String codigoPostal;
    private String estado;
    private String municipio;
    private String ciudad;
    private String nombreSiPais;
    private String nombreSiEstado;
    private String nombreSiCiudad;
    private int idSiPais;
    private int idSiEstado;
    private int idSiCiudad;
    private int idSgDireccion;
    private boolean vistoBueno;
    private boolean destino;
    private String label;
    private Object value;
    private String longitud;
    private String latitud;
    private int idMoneda;
    private int idStaff;
    private String piso;
    private SgOficinaAnalistaVo sgOficinaAnalistaVo;

    

    public String toString() {
	return "OficinaVO{" + "numeroTelefono=" + numeroTelefono + ", colonia=" + colonia + ", calle=" + calle + ", numeroExterior=" + numeroExterior + ", numeroInterior=" + numeroInterior + ", numeroPiso=" + numeroPiso + ", codigoPostal=" + codigoPostal + ", estado=" + estado + ", municipio=" + municipio + ", ciudad=" + ciudad + ", nombreSiPais=" + nombreSiPais + ", nombreSiEstado=" + nombreSiEstado + ", nombreSiCiudad=" + nombreSiCiudad + ", idSiPais=" + idSiPais + ", idSiEstado=" + idSiEstado + ", idSiCiudad=" + idSiCiudad + ", idSgDireccion=" + idSgDireccion + ", vistoBueno=" + vistoBueno + ", destino=" + destino + '}';
    }

    /**
     * @return the label
     */
    public String getLabel() {
	label = "";
	if (this.getNombre() != null && !this.getNombre().isEmpty()) {
	    label = this.getNombre();
	}
	return label;
    }

    /**
     * @return the value
     */
    public Object getValue() {
	value = null;
	if (this.getId() != null) {
	    value = (Object) this.getId();
	}

	return value;
    }

}
