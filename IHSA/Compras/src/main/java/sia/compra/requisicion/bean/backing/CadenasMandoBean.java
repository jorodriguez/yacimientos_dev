/*
 * CadenasMando.java
 * Creado el 6/07/2009, 12:27:44 PM
 * Managed Bean desarrollado por: Héctor Acosta Sierra para: MPG-IHSA
 *
 * Para información sobre el uso de este Managed Bean, asi como bugs, actualizaciones o mejoras
 * enviar un mail a: hacosta@ihsa.mx o a: hacosta.0505@gmail.com
 */
package sia.compra.requisicion.bean.backing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.CustomScoped;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.inject.Named;
import sia.constantes.Constantes;
import sia.modelo.CadenasMando;
import sia.modelo.requisicion.vo.CadenasMandoVo;
import sia.servicios.requisicion.impl.CadenasMandoImpl;
import sia.util.UtilLog4j;

/**
 *
 * @author Héctor Acosta Sierra
 * @version 1.0
 * @author-mail hacosta.0505@gmail.com @date 6/07/2009
 */
@Named (value = CadenasMandoBean.BEAN_NAME)
@CustomScoped(value = "#{window}")
public class CadenasMandoBean implements Serializable {

    //------------------------------------------------------
    public static final String BEAN_NAME = "cadenasMandoBean";
    //------------------------------------------------------

    @Inject
    private CadenasMandoImpl cadenasMandoServicioRemoto;

    /**
     * Creates a new instance of CadenasMando
     */
    public CadenasMandoBean() {
    }

    /**
     * @return Lista de las cadenas de mando
     */
    public CadenasMando[] getCadenasMando() {
        try {
            List<CadenasMando> tempList = cadenasMandoServicioRemoto.findAll();
            return tempList.toArray(new CadenasMando[tempList.size()]);
        } catch (RuntimeException ex) {
            UtilLog4j.log.fatal(this, ex.getMessage());
        }
        return new CadenasMando[0];
    }

    
    /**
     * @param idUsuario
     * @param idCampo
     * @return Lista de usuarios Que Aprueban Requisiciones
     */
    public List<SelectItem> traerRevisan(String idUsuario, int idCampo) {
        List<SelectItem> resultList = new ArrayList<>();
        try {
            List<CadenasMandoVo> lcad = cadenasMandoServicioRemoto.traerUsuarioRevisa(idUsuario, idCampo, Constantes.BOOLEAN_TRUE);
            //List<Usuario> tempList = cadenasMandoServicioRemoto.getAprueban(idUsuario, idCampo, false);
            for (CadenasMandoVo lista : lcad) {
                if (lista.getRevisa() != null) {
                    SelectItem item = new SelectItem(lista.getIdRevisa(), lista.getRevisa());
                    // esta linea es por si quiero agregar mas de un valoritem.setValue(Lista.getId());
                    resultList.add(item);
                }
            }
            return resultList;
        } catch (RuntimeException ex) {
            UtilLog4j.log.fatal(this, ex.getMessage());
        }
        return resultList;
    }

    /**
     * @param idUsuario
     * @param revisa
     * @param idCampo
     * @return Lista de usuarios Que Aprueban Requisiciones
     */
    public List<SelectItem> getListaAprueban(String idUsuario, String revisa, int idCampo) {
        List<SelectItem> resultList = new ArrayList<>();
        try {
            List<CadenasMandoVo> lcad = cadenasMandoServicioRemoto.traerUsuarioAprueba(idUsuario, revisa, idCampo, Constantes.BOOLEAN_TRUE);
            //List<Usuario> tempList = cadenasMandoServicioRemoto.getAprueban(idUsuario, idCampo, false);
            for (CadenasMandoVo lista : lcad) {
                if (lista != null) {
                    SelectItem item = new SelectItem(lista.getIdAprueba(), lista.getAprueba());
                    // esta linea es por si quiero agregar mas de un valoritem.setValue(Lista.getId());
                    resultList.add(item);
                }
            }            
        } catch (RuntimeException ex) {
            resultList = new ArrayList<>();
            UtilLog4j.log.fatal(this, ex.getMessage());
        }
        return resultList;
    }

    /**
     * @return the usuarioBean
     */
    public UsuarioBean getUsuarioBean() {
        return (UsuarioBean) FacesUtilsBean.getManagedBean("usuarioBean");
    }

}
