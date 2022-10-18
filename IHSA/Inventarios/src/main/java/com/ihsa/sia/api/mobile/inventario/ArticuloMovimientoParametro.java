package com.ihsa.sia.api.mobile.inventario;

/**
 * Clase que represanta un elemento de la lista de artículos a ser procesado y
 * es utilizado como parámetro de un endpoint Rest del api para aplicaciones
 * moviles
 *
 * @author Aplimovil SA de CV
 */
public class ArticuloMovimientoParametro {

    private int id;
    private int unidades;
    private String identificador;

    public ArticuloMovimientoParametro(int id, int unidades, String identificador) {
	this.id = id;
	this.unidades = unidades;
	this.identificador = identificador;
    }
    
    public int getId() {
	return id;
    }

    public void setId(int id) {
	this.id = id;
    }

    public int getUnidades() {
	return unidades;
    }

    public void setUnidades(int unidades) {
	this.unidades = unidades;
    }

    public String getIdentificador() {
	return identificador;
    }

    public void setIdentificador(String identificador) {
	this.identificador = identificador;
    }
}
