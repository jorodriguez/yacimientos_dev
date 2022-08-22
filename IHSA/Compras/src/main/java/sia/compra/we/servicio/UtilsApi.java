/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.compra.we.servicio;

import com.google.gson.JsonObject;
import javax.ws.rs.core.Response;
import static sia.compra.we.servicio.UtilsApi.*;
/**
 *Level of package
 * @author jorodriguez
 */
public final class  UtilsApi { 
    
    
    static final Response buildBadResponse(String error) {

        final JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("error", true);
        jsonObject.addProperty("message", error);

        return Response
                .status(Response.Status.BAD_REQUEST)
                .entity(jsonObject.toString())
                .build();
    }

    static final Response buildAcceptedResponse(JsonObject jsonObject) {
        return Response.status(Response.Status.ACCEPTED).entity(jsonObject.toString()).build();
    }

    static final Response buildAcceptedResponse(String json) {
        return Response.status(Response.Status.ACCEPTED).entity(json).build();
    }
    
 
}
