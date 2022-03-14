/*
 * ContactosOrdenFacade.java
 * Creada el 13/10/2009, 06:06:28 PM
 * Clase Java desarrollada por: Héctor Acosta Sierra para: MPG-IHSA
 *
 * Para información sobre el uso de esta clase, asi como bugs, actualizaciones o mejoras
 * enviar un mail a: hacosta@mpg-ihsa.com.mx o a: new_nick_name@hotmail.com
 */
package sia.servicios.orden.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.LocalBean;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import sia.constantes.Constantes;
import sia.modelo.ContactosOrden;
import sia.modelo.Usuario;
import sia.modelo.orden.vo.ContactoOrdenVo;
import sia.modelo.proveedor.Vo.ContactoProveedorVO;
import sia.servicios.proveedor.impl.ContactoProveedorImpl;
import sia.util.UtilLog4j;

/**
 *
 * @author Héctor Acosta Sierra
 * @version 1.0
 * @author-mail new_nick_name@hotmail.com @date 13/10/2009
 */
@LocalBean 
public class ContactosOrdenImpl{

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;    
    @Inject
    private OrdenImpl ordenRemote;
    @Inject
    private ContactoProveedorImpl contactoProveedorRemote;

    
    public void create(ContactosOrden contactosOrden) {
        em.persist(contactosOrden);
    }

    
    public void edit(ContactosOrden contactosOrden) {
        em.merge(contactosOrden);
    }

    
    public void remove(ContactosOrden contactosOrden) {
        em.remove(em.merge(contactosOrden));
    }

    
    public ContactosOrden find(Object id) {
        return em.find(ContactosOrden.class, id);
    }

    
    public ContactosOrden buscarPorNombre(Object idOrden, Object nombre) {
        ContactosOrden contactoOrden;
        List<ContactosOrden> contactosOrden = em.createQuery("SELECT c FROM ContactosOrden c WHERE c.orden.id = :idOrden AND c.contactoProveedor.nombre = :nombre").setParameter("idOrden", idOrden).setParameter("nombre", nombre).getResultList();
        if (contactosOrden.isEmpty()) {
            contactoOrden = null;
        } else {
            contactoOrden = contactosOrden.get(0);
        }
        return contactoOrden;
    }

    
    public List<ContactosOrden> findAll() {
        return em.createQuery("select object(o) from ContactosOrden as o").getResultList();
    }

    
    public List<ContactosOrden> getContactosPorOrden(Object idOrden) {
        return em.createQuery("SELECT c FROM ContactosOrden c WHERE c.orden.id = :idOrden and c.eliminado = :eli").setParameter("eli", Constantes.NO_ELIMINADO).setParameter("idOrden", idOrden).getResultList();
    }

    
    public List<ContactoOrdenVo> traerContactoPorOrden(int idOrden) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT c.id, o.ID as orden,c.CONTACTO_PROVEEDOR,  cp.NOMBRE, cp.CORREO FROM Contactos_Orden c ");
        sb.append(" inner join orden o on c.ORDEN = o.id and o.id = ").append(idOrden);
        sb.append(" inner join contacto_proveedor cp on c.CONTACTO_PROVEEDOR = cp.id and cp.ELIMINADO = '").append(Constantes.BOOLEAN_FALSE).append("'");
        sb.append(" and c.eliminado = '").append(Constantes.BOOLEAN_FALSE).append("'");
        sb.append(" order by cp.nombre  asc");
        UtilLog4j.log.info(this, "Q contac Odc : :: " + sb.toString());
        List<Object[]> lo = em.createNativeQuery(sb.toString()).getResultList();
        List<ContactoOrdenVo> lc = null;
        if (lo != null) {
            lc = new ArrayList<ContactoOrdenVo>();
            for (Object[] objects : lo) {
                lc.add(castContacto(objects));
            }
        }
        return lc;
    }

    private ContactoOrdenVo castContacto(Object[] object) {
        ContactoOrdenVo cov = new ContactoOrdenVo();
        cov.setId((Integer) object[0]);
        cov.setIdOrden((Integer) object[1]);
        cov.setIdContactoProveedor((Integer) object[2]);
        cov.setNombre((String) object[3]);
        cov.setCorreo((String) object[4]);
        cov.setSelected(false);
        return cov;
    }

    
    public void guardarContacto(String idSesion, List<ContactoProveedorVO> lcp, int idOrden) {
        List<Integer> li = new ArrayList<Integer>();
        for (ContactoProveedorVO contactoProveedorVO : lcp) {
            if (!existeContactoOrden(idOrden, contactoProveedorVO.getIdContactoProveedor())) { // si no esta se agrega
                UtilLog4j.log.info(this, "COntancto: " + contactoProveedorVO.getIdContactoProveedor());
                UtilLog4j.log.info(this, "id orden: " + idOrden);
                ContactosOrden co = new ContactosOrden();
                co.setOrden(ordenRemote.find(idOrden));
                co.setContactoProveedor(contactoProveedorRemote.find(contactoProveedorVO.getIdContactoProveedor()));
                co.setGenero(new Usuario(idSesion));
                co.setFechaGenero(new Date());
                co.setHoraGenero(new Date());
                co.setEliminado(Constantes.NO_ELIMINADO);
                create(co);
                li.add(contactoProveedorVO.getIdContactoProveedor());
            }
        }
        //Quitar a los contactos que no estan en la lista
        if (li.size() > 0) {
            buscarContacto(idOrden, li, idSesion);
        }
    }

    private boolean existeContactoOrden(int idOrden, int idContacto) {
        boolean v = false;
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("select * from contactos_orden oc where oc.orden = ").append(idOrden);
            sb.append(" and oc.contacto_proveedor = ").append(idContacto);
            sb.append(" and oc.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");

            Object[] objects = (Object[]) em.createNativeQuery(sb.toString()).getSingleResult();
            if (objects != null) {
                v = true;
            }
            return v;
        } catch (NoResultException e) {
            UtilLog4j.log.fatal(this, "E : al buscar contactos " + e.getMessage());
            v = false;
        }
        return v;
    }

    private void buscarContacto(int idOrden, List<Integer> lc, String idSesion) {

        String cad = lc.toString();
        String cadenaLista = cad.substring(1, cad.length() - 1);
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("select oc.id, oc.orden from contactos_orden oc where oc.orden = ").append(idOrden);
            sb.append(" and oc.contacto_proveedor not in (").append(cadenaLista).append(")");
            UtilLog4j.log.info(this, "Con: " + sb.toString());
            List<Object[]> lo = em.createNativeQuery(sb.toString()).getResultList();
            if (lo != null) {
                for (Object[] objects : lo) {
                    int id = (Integer) objects[0];
                    UtilLog4j.log.info(this, "Id contacto: " + id);
                    eliminarContactoOrden(id, idSesion);
                }
            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "E : " + e.getMessage(), e);
        }
    }

    
    public void eliminarContactoOrden(int idContactoOrden, String idSesion) {
        ContactosOrden orden = find(idContactoOrden);
        orden.setEliminado(Constantes.ELIMINADO);
        orden.setModifico(new Usuario(idSesion));
        orden.setFechaModifico(new Date());
        orden.setHoraModifico(new Date());
        edit(orden);
    }

    
    public String correoContactoOrden(int idOrden) {
        String c = "select  COALESCE(array_to_string(array_agg(DISTINCT cp.correo), ', '), '')  FROM Contactos_Orden c \n"
                + "         inner join orden o on c.ORDEN = o.id and o.id = " + idOrden
                + "         inner join contacto_proveedor cp on c.CONTACTO_PROVEEDOR = cp.id and cp.ELIMINADO = false\n"
                + "         and c.eliminado = false";
        return (String) em.createNativeQuery(c).getSingleResult();
    }
}
