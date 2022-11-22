/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo.sgl.vo;

/**
 *
 * @author jorodriguez
 */
public class CaracteristicaServicioVO extends Vo{
     private String enConvenio;
     private String numeroParte;
     private double precio;
     private String principal;     
     private String nombreCondicionPago;
     private String nombreConvenio;
     private String nombreMoneda;
     private String nombreUnidad;


    /**
     * @return the enConvenio
     */
    public String getEnConvenio() {
        return enConvenio;
    }

    /**
     * @param enConvenio the enConvenio to set
     */
    public void setEnConvenio(String enConvenio) {
        this.enConvenio = enConvenio;
    }

    /**
     * @return the numeroParte
     */
    public String getNumeroParte() {
        return numeroParte;
    }

    /**
     * @param numeroParte the numeroParte to set
     */
    public void setNumeroParte(String numeroParte) {
        this.numeroParte = numeroParte;
    }

    /**
     * @return the precio
     */
    public double getPrecio() {
        return precio;
    }

    /**
     * @param precio the precio to set
     */
    public void setPrecio(double precio) {
        this.precio = precio;
    }

    /**
     * @return the principal
     */
    public String getPrincipal() {
        return principal;
    }

    /**
     * @param principal the principal to set
     */
    public void setPrincipal(String principal) {
        this.principal = principal;
    }

    /**
     * @return the nombreCondicionPago
     */
    public String getNombreCondicionPago() {
        return nombreCondicionPago;
    }

    /**
     * @param nombreCondicionPago the nombreCondicionPago to set
     */
    public void setNombreCondicionPago(String nombreCondicionPago) {
        this.nombreCondicionPago = nombreCondicionPago;
    }

    /**
     * @return the nombreConvenio
     */
    public String getNombreConvenio() {
        return nombreConvenio;
    }

    /**
     * @param nombreConvenio the nombreConvenio to set
     */
    public void setNombreConvenio(String nombreConvenio) {
        this.nombreConvenio = nombreConvenio;
    }

    /**
     * @return the nombreMoneda
     */
    public String getNombreMoneda() {
        return nombreMoneda;
    }

    /**
     * @param nombreMoneda the nombreMoneda to set
     */
    public void setNombreMoneda(String nombreMoneda) {
        this.nombreMoneda = nombreMoneda;
    }

    /**
     * @return the nombreUnidad
     */
    public String getNombreUnidad() {
        return nombreUnidad;
    }

    /**
     * @param nombreUnidad the nombreUnidad to set
     */
    public void setNombreUnidad(String nombreUnidad) {
        this.nombreUnidad = nombreUnidad;
    }
    
}
