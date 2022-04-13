/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.orden.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.constantes.Constantes;
import sia.modelo.ApCampo;
import sia.modelo.OcCampoProveedor;
import sia.modelo.Proveedor;
import sia.modelo.Usuario;
import sia.modelo.proveedor.Vo.ProveedorVo;
import sia.modelo.sistema.AbstractFacade;
import sia.util.UtilLog4j;

/**
 *
 * @author ihsa
 */
@Stateless 
public class OcCampoProveedorImpl extends AbstractFacade<OcCampoProveedor>{

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public OcCampoProveedorImpl() {
        super(OcCampoProveedor.class);
    }

    
    public boolean estaProveedor(int idProveedor, int campo) {
        boolean v = true;
        clearQuery();
        try {
            query.append("select ap.id, ap.ap_campo, ap.proveedor from oc_campo_proveedor ap");
            query.append(" where ap.proveedor = ").append(idProveedor);
            query.append(" and ap.ap_campo = ").append(campo);
            query.append(" and ap.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");
            Object[] objects = (Object[]) em.createNativeQuery(query.toString()).getSingleResult();

            return v;
        } catch (Exception e) {
            UtilLog4j.log.error(e);
            return false;
        }
    }

    
    public void agregarProveedor(int idCampo, int idProveedor, String id) {
        OcCampoProveedor ocCampoProveedor = new OcCampoProveedor();
        ocCampoProveedor.setApCampo(new ApCampo(idCampo));
        ocCampoProveedor.setProveedor(new Proveedor(idProveedor));
        ocCampoProveedor.setGenero(new Usuario(id));
        ocCampoProveedor.setFechaGenero(new Date());
        ocCampoProveedor.setHoraGenero(new Date());
        ocCampoProveedor.setEliminado(Constantes.NO_ELIMINADO);
        create(ocCampoProveedor);
        //
    }

    
    public List<ProveedorVo> traerProveedor(int campo) {
        clearQuery();
        List<ProveedorVo> lp = null;
        try {
            query.append("select ap.id, ap.proveedor, p.nombre from oc_campo_proveedor ap");
            query.append("      inner join proveedor p on ap.proveedor = p.id");
            query.append(" where ap.ap_campo = ").append(campo);
            query.append(" and ap.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");
            List<Object[]> objects = em.createNativeQuery(query.toString()).getResultList();
            if (objects != null) {
                lp = new ArrayList<ProveedorVo>();
                for (Object[] obj : objects) {
                    lp.add(castProveedor(obj));
                }
            }
            return lp;
        } catch (Exception e) {
            UtilLog4j.log.error(e);
        }
        return lp;
    }

    private ProveedorVo castProveedor(Object[] obj) {
        ProveedorVo p = new ProveedorVo();
        p.setIdRelacion((Integer) obj[0]);
        p.setIdProveedor((Integer) obj[1]);
        p.setNombre((String) obj[2]);
        return p;
    }

    
    public void eliminarProveedorCampo(int idRelacion, String sesion) {
        OcCampoProveedor ocCampoProveedor = find(idRelacion);
        ocCampoProveedor.setModifico(new Usuario(sesion));
        ocCampoProveedor.setFechaModifico(new Date());
        ocCampoProveedor.setHoraModifico(new Date());
        ocCampoProveedor.setEliminado(Constantes.ELIMINADO);
        edit(ocCampoProveedor);
        //
    }

    
    public boolean buscarProveedorCampo(int idCampo, int idProveedor) {
        clearQuery();
        boolean v = false;
        try {
            query.append("select ap.id, ap.proveedor from oc_campo_proveedor ap");
            query.append(" where ap.ap_campo = ").append(idCampo);
            query.append(" and ap.proveedor = ").append(idProveedor);
            query.append(" and ap.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");
            Object[] objects = (Object[]) em.createNativeQuery(query.toString()).getSingleResult();
            if (objects != null) {
                v = true;
            }
        } catch (Exception e) {
            UtilLog4j.log.error(e);
        }
        return v;
    }
}
