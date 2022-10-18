/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo.sgl.vo;

/**
 *
 * @author mluis
 */
public class HospedadoVO {

    private int id;
    private String oficina;
    private String lugar;
    private int total;

    public HospedadoVO(String lugar, int total) {
        this.lugar = lugar;
        this.total = total;
    }

    public HospedadoVO(int id, String lugar, int total) {
        this.id = id;
        this.lugar = lugar;
        this.total = total;
    }

    public HospedadoVO() {
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the lugar
     */
    public String getLugar() {
        return lugar;
    }

    /**
     * @param lugar the lugar to set
     */
    public void setLugar(String lugar) {
        this.lugar = lugar;
    }

    /**
     * @return the total
     */
    public int getTotal() {
        return total;
    }

    /**
     * @param total the total to set
     */
    public void setTotal(int total) {
        this.total = total;
    }

    /**
     * @return the oficina
     */
    public String getOficina() {
        return oficina;
    }

    /**
     * @param oficina the oficina to set
     */
    public void setOficina(String oficina) {
        this.oficina = oficina;
    }
}
