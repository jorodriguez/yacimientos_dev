package com.ihsa.sia.api;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import org.json.JSONException;
import org.json.JSONObject;
import sia.excepciones.SIAException;
import sia.inventarios.service.TransaccionImpl;

/**
 *
 * @author Aplimovil SA de CV
 */
@Path("transaccion")
@RequestScoped
public class TransaccionApi {
    @Inject
    protected TransaccionImpl servicio;
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public JSONObject validarFolioOrdenDeCompra(@QueryParam("folio") String folio) throws JSONException {
        boolean valido;
        
        try{
            valido = servicio.validarFolioOrdenDeCompra(folio);
        }
        catch(SIAException ex){
            throw new JSONException(ex.getMessage());
        }
        
        JSONObject respuesta = new JSONObject();
        
        respuesta.put("valido", valido);
        
        return respuesta;
    }
}
