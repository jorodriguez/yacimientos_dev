/*
 *  Document   : RhEmpleadoMaterialFacade.java 
 *  Create on  : May 28, 2013, 2:28:17 PM
 *  Author     : Héctor Acosta
 *  Information: Para información sobre el uso de esta clase, asi como bugs, actualizaciones o mejoras
 *               enviar un correo a: hacost@hotmail.com
 *  Description: 
 *  Purpose of the class follows.
 */
package sia.servicios.catalogos.impl;

import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.constantes.Constantes;
import sia.modelo.RhEmpleadoMaterial;
import sia.modelo.sistema.AbstractFacade;
import sia.modelo.usuario.vo.EmpleadoMaterialVO;
import sia.util.UtilLog4j;

/**
 * @empresa IHSA
 *
 * @author Héctor Acosta @correo hacost@hotmail.com
 */
@Stateless 
public class RhEmpleadoMaterialImpl extends AbstractFacade<RhEmpleadoMaterial>{

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public RhEmpleadoMaterialImpl() {
        super(RhEmpleadoMaterial.class);
    }

    //
    
    public List<EmpleadoMaterialVO> getListEmpleadoMaterial() {
        try {
            String q = "select e.id, g.id, g.nombre, e.nombre, e.descripcion from rh_empleado_material e, gerencia g where e.gerencia = g.id AND e.eliminado = '" + Constantes.BOOLEAN_FALSE +"'"
                    + " order by g.id asc";
            List<EmpleadoMaterialVO> le = new ArrayList<>();

            List<Object[]> lo = em.createNativeQuery(q).getResultList();
            for (Object[] objects : lo) {
                le.add(castEmpleadoMaterialVO(objects));
            }
            return le;
        } catch (Exception e) {
            UtilLog4j.log.error(e);
            return null;
        }
    }

    private EmpleadoMaterialVO castEmpleadoMaterialVO(Object[] objects) {
        EmpleadoMaterialVO emvo = new EmpleadoMaterialVO();
        emvo.setId((Integer) objects[0]);
        emvo.setIdGerencia((Integer) objects[1]);
        emvo.setGerencia((String) objects[2]);
        emvo.setNombre((String) objects[3]);
        emvo.setDescripcion((String) objects[4]);
        return emvo;
    }
}
