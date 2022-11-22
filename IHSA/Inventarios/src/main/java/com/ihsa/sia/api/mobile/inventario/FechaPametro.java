package com.ihsa.sia.api.mobile.inventario;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Clase que represanta una fecha recibida como parámetro de un endpoint Rest del api para aplicaciones moviles
 * @author Aplimovil SA de CV
 */
public class FechaPametro {

    private String fecha;

    public FechaPametro(String fecha) {
        this.fecha = fecha;
    }

    public static FechaPametro valueOf(String value) {
        return new FechaPametro(value);
    }

    public Date getFecha() throws ParseException {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        return df.parse(fecha);
    }
}
