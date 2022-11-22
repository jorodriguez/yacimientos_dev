/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo.sgl.vo;

import java.io.Serializable;

/**
 *
 * @author mluis
 */
public class SeguridadVO{
    private int id;
    private String ciudad;
    private  int total;
    private long totalLong;

    public SeguridadVO() {
    }

    public SeguridadVO(String ciudad, int total) {
        this.ciudad = ciudad;
        this.total = total;
    }

    public SeguridadVO(int id, String ciudad, int total) {
        this.id = id;
        this.ciudad = ciudad;
        this.total = total;
    }

    

    /**
     * @return the ciudad
     */
    public String getCiudad() {
        return ciudad;
    }

    /**
     * @param ciudad the ciudad to set
     */
    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
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
     * @return the totalLong
     */
    public long getTotalLong() {
        return totalLong;
    }

    /**
     * @param totalLong the totalLong to set
     */
    public void setTotalLong(long totalLong) {
        this.totalLong = totalLong;
    }
    
}
