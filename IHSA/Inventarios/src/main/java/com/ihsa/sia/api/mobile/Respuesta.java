package com.ihsa.sia.api.mobile;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase que representa la respuesta de la invocación de los servicios para
 * moviles
 *
 * @author Aplimovil SA de CV
 */
public class Respuesta<T> {

    private Estado estado;
    private String mensaje;
    private List<T> resultados;

    public Respuesta(Estado estado, String mensaje) {
	this.estado = estado;
	this.mensaje = mensaje;
	//Para casos que la ejecución fue satisfactoria pero el contenido de resultados es un arreglo vacio
	if (estado == Estado.ok) {
	    resultados = new ArrayList<T>();
	}
    }

    /**
     * Crea un objeto de respuesta con el estado indicado y mensaje vacio
     *
     * @param estado
     */
    public Respuesta(Estado estado) {
	this(estado, "");
    }

    /**
     * Crea un objecto de respuesto con estado ok y mensaje vacio pero con el
     * resultado indicado en el parametro de este constructor
     *
     * @param contenido objecto de resultado
     */
    public Respuesta(T contenido) {
	this(Estado.ok, "");
	addResultado(contenido);
    }

    public Estado getEstado() {
	return estado;
    }

    public String getMensaje() {
	return mensaje;
    }

    public void setResultados(List<T> resultados) {
	this.resultados = resultados;
    }

    public void addResultado(T contenido) {
	if (this.resultados == null) {
	    this.resultados = new ArrayList<T>();
	}
	resultados.add(contenido);
    }

    public List<T> getResultados() {
	return resultados;
    }
}
