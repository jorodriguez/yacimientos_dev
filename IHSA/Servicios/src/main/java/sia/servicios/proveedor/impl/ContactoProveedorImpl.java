/*
 * ContactoProveedorFacade.java
 * Creada el 13/10/2009, 06:06:27 PM
 * Clase Java desarrollada por: Héctor Acosta Sierra para: MPG-IHSA
 *
 * Para información sobre el uso de esta clase, asi como bugs, actualizaciones o mejoras
 * enviar un mail a: hacosta@mpg-ihsa.com.mx o a: new_nick_name@hotmail.com
 */
package sia.servicios.proveedor.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import sia.constantes.Constantes;
import sia.modelo.ContactoProveedor;
import sia.modelo.Proveedor;
import sia.modelo.PvArea;
import sia.modelo.Usuario;
import sia.modelo.proveedor.Vo.ContactoProveedorVO;
import sia.modelo.sistema.AbstractFacade;
import sia.util.UtilLog4j;

/**
 *
 * @author MLUIS
 * @version 1.0
 */
@Stateless 
public class ContactoProveedorImpl extends AbstractFacade<ContactoProveedor> {

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ContactoProveedorImpl() {
        super(ContactoProveedor.class);
    }

    
    public void create(ContactoProveedor contactoProveedor) {
        em.persist(contactoProveedor);
    }

    
    public void edit(ContactoProveedor contactoProveedor) {
        em.merge(contactoProveedor);
    }

    
    public void remove(ContactoProveedor contactoProveedor) {
        em.remove(em.merge(contactoProveedor));
    }

    
    public ContactoProveedor find(Object id) {
        return em.find(ContactoProveedor.class, id);
    }

    
    public List<ContactoProveedor> findAll() {
        return em.createQuery("select object(o) from ContactoProveedor as o").getResultList();
    }

    
    public List<ContactoProveedor> getPorProveedor(Object nombreProveedor) {
        return em.createQuery("SELECT c FROM ContactoProveedor c WHERE c.proveedor.nombre = :nombreProveedor AND c.activo = :true").setParameter("nombreProveedor", nombreProveedor).setParameter("true", true).getResultList();
    }

    
    public ContactoProveedor buscarPorNombre(Object nombreContacto, Object nombreProveedor) {
        return (ContactoProveedor) em.createQuery("SELECT c FROM ContactoProveedor c WHERE c.nombre = :nombre AND c.proveedor.nombre = :proveedor"
                + "  AND c.activo = :true").setParameter("true", Constantes.BOOLEAN_TRUE).setParameter("nombre", nombreContacto).setParameter("proveedor", nombreProveedor).getSingleResult();
    }
    //NUEVOS

    
    public List<ContactoProveedor> getProveedorPorRFC(String rfc) {
        return em.createQuery("SELECT c FROM ContactoProveedor c WHERE c.proveedor.rfc = :rfc").setParameter("rfc", rfc).getResultList();
    }

    
    public boolean guardarContacto(int idPro, ContactoProveedor contactoProveedor, int area) {
        boolean v = false;
        if (idPro > 0) {
            contactoProveedor.setPvArea(new PvArea(area));
            contactoProveedor.setProveedor(new Proveedor(idPro));
            contactoProveedor.setActivo(Constantes.BOOLEAN_TRUE);
            contactoProveedor.setFechaGenero(new Date());
            contactoProveedor.setHoraGenero(new Date());
            contactoProveedor.setEliminado(Constantes.NO_ELIMINADO);
            this.create(contactoProveedor);
            v = true;
        }
        return v;
    }

    
    public void actualizarContacto(ContactoProveedor contactoProveedor, int area) {
        contactoProveedor.setPvArea(new PvArea(area));
        this.edit(contactoProveedor);
    }

    
    public List<ContactoProveedor> traerPorPuesto(int id) {
        return em.createQuery("SELECT f FROM ContactoProveedor f WHERE f.pvArea.id = :id AND f.proveedor.visible = :t").setParameter("id", id).setParameter("t", true).getResultList();
    }

    
    public List<ContactoProveedor> traerContactoPorProveedor(String rfc) {
        return em.createQuery("SELECT f FROM ContactoProveedor f WHERE f.proveedor.rfc = :rfc ORDER BY f.id ASC").setParameter("rfc", rfc).getResultList();
    }

    
    public void eliminarContacto(ContactoProveedor contactoProveedor) {
        contactoProveedor.setActivo(Constantes.BOOLEAN_FALSE);
        contactoProveedor.setFechaGenero(new Date());
        contactoProveedor.setHoraGenero(new Date());
        contactoProveedor.setEliminado(Constantes.ELIMINADO);
        this.edit(contactoProveedor);
    }

    
    public List<ContactoProveedor> traerContactoPorRFC(String rfc) {
        return em.createQuery("SELECT c FROM ContactoProveedor c WHERE c.proveedor.rfc = :rfc AND c.activo = :true").setParameter("true", true).setParameter("rfc", rfc).getResultList();
    }

    
    public List<ContactoProveedor> traerContactoActivoPorProveedor(String p) {
        try {
            return em.createQuery("SELECT c FROM ContactoProveedor c WHERE c.proveedor.nombre = :pro AND c.activo = :t ORDER BY c.nombre ASC").setParameter("pro", p).setParameter("t", true).getResultList();
        } catch (Exception e) {
            return null;
        }
    }

    
    public List<ContactoProveedorVO> traerContactoPorProveedor(int idProveedor, int tipo) {
        List<ContactoProveedorVO> lcp = new ArrayList<>();
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(" select cp.id, cp.proveedor, cp.nombre, cp.telefono,  cp.correo, ");
            //                    0         1           2            3           4
            sb.append(" cp.PUESTO,cp.celular, cp.RFC, cp.CURP, cp.PODERNOTARIAL, cp.NONOTARIA, cp.EMISION, cp.IDTIPO, cp.IDVIGENCIA, cp.NOTARIO, cp.REFERENCIA, ");
            //              5          6          7        8         9                 10           11         12           13        14              15
            sb.append(" cp.SI_LISTA_ELEMENTO, le.NOMBRE, cp.PV_AREA, ar.NOMBRE, cp.notifica ");
            //                  16                17         18            19
            sb.append(" from contacto_proveedor cp ");
            sb.append(" left join SI_LISTA_ELEMENTO le on le.id = cp.SI_LISTA_ELEMENTO and le.ELIMINADO = 'False' ");
            sb.append(" left join PV_AREA ar on ar.id = cp.PV_AREA and ar.ELIMINADO = 'False'   ");
            sb.append(" where cp.proveedor = ");
            sb.append(idProveedor);
            sb.append(" and cp.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");
            if (tipo > 0) {
                sb.append(" and cp.PV_AREA =  ").append(tipo);
            }
            sb.append(" order by cp.pv_area");
            //
            //
            List<Object[]> lo = em.createNativeQuery(sb.toString()).getResultList();
            if (lo != null) {
                for (Object[] objects : lo) {
                    lcp.add(castContactoProveedor(objects));
                }
            }
        } catch (Exception e) {
            UtilLog4j.log.error(e);
            UtilLog4j.log.info(this, "Ocurrio un errro al recuperar los contacto del  proveedor : : : " + idProveedor);
        }
        return lcp;
    }

    
    public ContactoProveedorVO traerContacto(int idContacto) {
        ContactoProveedorVO vo = null;
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(" select cp.id, cp.proveedor, cp.nombre, cp.telefono,  cp.correo, ");
            //                    0         1           2            3           4
            sb.append(" cp.PUESTO,cp.celular, cp.RFC, cp.CURP, cp.PODERNOTARIAL, cp.NONOTARIA, cp.EMISION, cp.IDTIPO, cp.IDVIGENCIA, cp.NOTARIO, cp.REFERENCIA, ");
            //              5          6          7        8         9                 10           11         12           13        14              15
            sb.append(" cp.SI_LISTA_ELEMENTO, le.NOMBRE, cp.PV_AREA, ar.NOMBRE, cp.notifica       ");
            //                  16                17         18            19
            sb.append(" from contacto_proveedor cp ");
            sb.append(" left join SI_LISTA_ELEMENTO le on le.id = cp.SI_LISTA_ELEMENTO and le.ELIMINADO = 'False' ");
            sb.append(" left join PV_AREA ar on ar.id = cp.PV_AREA and ar.ELIMINADO = 'False'   ");
            sb.append(" where cp.id = ");
            sb.append(idContacto);
            sb.append(" and cp.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");
            //sb.append(" order by ar.nombre desc");
            //
            Object[] lo = (Object[]) em.createNativeQuery(sb.toString()).getSingleResult();
            if (lo != null) {
                vo = castContactoProveedor(lo);
            }
        } catch (Exception e) {
            UtilLog4j.log.error(e);
            UtilLog4j.log.info(this, "Ocurrio un errro al recuperar los contacto  : : : " + idContacto);
        }
        return vo;
    }

    private ContactoProveedorVO castContactoProveedor(Object[] objects) {
        ContactoProveedorVO cpvo = new ContactoProveedorVO();
        cpvo.setIdContactoProveedor((Integer) objects[0]);
        cpvo.setIdProveedor((Integer) objects[1]);
        cpvo.setNombre((String) objects[2]);
        cpvo.setTelefono((String) objects[3]);
        cpvo.setCorreo((String) objects[4]);
        cpvo.setPuesto((String) objects[5]);
        cpvo.setCelular((String) objects[6]);
        cpvo.setRfc((String) objects[7]);
        cpvo.setCurp((String) objects[8]);
        cpvo.setPoder((String) objects[9]);
        cpvo.setNotaria((String) objects[10]);
        cpvo.setEmision((Date) objects[11]);
        cpvo.setTipoID((Integer) objects[16] != null ? (Integer) objects[16] : 0);
        cpvo.setTipoTxt((String) objects[17]);
        cpvo.setIdVigencia((Date) objects[13]);
        cpvo.setNombreNotario((String) objects[14]);
        cpvo.setReferencia((String) objects[15]);
        cpvo.setTipoContacto((Integer) objects[18] != null ? (Integer) objects[18] : 0);
        cpvo.setTipoContactoTxt((String) objects[19]);
        cpvo.setNotifica((Boolean) objects[20]);
        //
        cpvo.setSelected(false);
        cpvo.setEditar(false);
        return cpvo;

    }

    
    public void eliminarContacto(int idContacto, String sesion) {
        ContactoProveedor contactoProveedor = find(idContacto);
        contactoProveedor.setActivo(Constantes.BOOLEAN_FALSE);
        contactoProveedor.setModifico(new Usuario(sesion));
        contactoProveedor.setFechaModifico(new Date());
        contactoProveedor.setHoraModifico(new Date());
        contactoProveedor.setEliminado(Constantes.ELIMINADO);
        edit(contactoProveedor);
    }

    
    public void actualizarContacto(int idContactoProveedor, String nombre, String correo, String telefono, boolean notifica, String sesion) {
        ContactoProveedor contactoProveedor = find(idContactoProveedor);
        contactoProveedor.setNombre(nombre);
        contactoProveedor.setCorreo(correo);
        contactoProveedor.setTelefono(telefono);
        contactoProveedor.setNotifica(notifica);
        contactoProveedor.setFechaModifico(new Date());
        contactoProveedor.setHoraModifico(new Date());
        edit(contactoProveedor);

    }

    
    public void guardarContacto(int idProveedor, String contacto, String telefono, String correo, int area, String sesion) {

        ContactoProveedor contactoProveedor = traerContactoPorProveedor(idProveedor, contacto, area);
        if (contactoProveedor == null) {
            contactoProveedor = new ContactoProveedor();
            contactoProveedor.setPvArea(new PvArea(area));
            contactoProveedor.setProveedor(new Proveedor(idProveedor));
            contactoProveedor.setNombre(contacto);
            contactoProveedor.setTelefono(telefono);
            contactoProveedor.setCorreo(correo);
            contactoProveedor.setActivo(Constantes.BOOLEAN_TRUE);
            contactoProveedor.setGenero(new Usuario(sesion));
            contactoProveedor.setFechaGenero(new Date());
            contactoProveedor.setHoraGenero(new Date());
            contactoProveedor.setEliminado(Constantes.NO_ELIMINADO);
            //
        } else {
            contactoProveedor.setNombre(contacto);
            contactoProveedor.setTelefono(telefono);
            contactoProveedor.setCorreo(correo);
            contactoProveedor.setActivo(Constantes.BOOLEAN_TRUE);
            contactoProveedor.setModifico(new Usuario(sesion));
            contactoProveedor.setFechaModifico(new Date());
            contactoProveedor.setHoraModifico(new Date());
        }
        //
        edit(contactoProveedor);
    }

    
    public List<ContactoProveedorVO> traerTodosContactoPorProveedor(int idProveedor) {
        List<ContactoProveedorVO> lcp = new ArrayList<>();
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(" select cp.id, cp.proveedor, cp.nombre, cp.telefono,  cp.correo, ");
            //                    0         1           2            3           4
            sb.append(" cp.PUESTO,cp.celular, cp.RFC, cp.CURP, cp.PODERNOTARIAL, cp.NONOTARIA, cp.EMISION, cp.IDTIPO, cp.IDVIGENCIA, cp.NOTARIO, cp.REFERENCIA, ");
            //              5          6          7        8         9                 10           11         12           13        14              15
            sb.append(" cp.SI_LISTA_ELEMENTO, le.NOMBRE, cp.PV_AREA, ar.NOMBRE, cp.notifica       ");
            //                  16                17         18            19
            sb.append(" from contacto_proveedor cp ");
            sb.append(" left join SI_LISTA_ELEMENTO le on le.id = cp.SI_LISTA_ELEMENTO and le.ELIMINADO = 'False' ");
            sb.append(" left join PV_AREA ar on ar.id = cp.PV_AREA and ar.ELIMINADO = 'False'   ");
            sb.append(" where cp.proveedor = ").append(idProveedor);
            sb.append(" and cp.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");
            //
            List<Object[]> lo = em.createNativeQuery(sb.toString()).getResultList();
            if (lo != null) {
                for (Object[] objects : lo) {
                    lcp.add(castContactoProveedor(objects));
                }
            }
        } catch (Exception e) {
            UtilLog4j.log.error(e);
            UtilLog4j.log.info(this, "Ocurrio un errro al recuperar los contacto del  proveedor : : : " + idProveedor);
        }
        return lcp;
    }

    
    public void guardar(int idProveedor, List<ContactoProveedorVO> contactos, String sesion) {
        try {
            for (ContactoProveedorVO contacto : contactos) {
                guardarContacto(idProveedor, contacto, sesion);
            }
        } catch (Exception e) {
            UtilLog4j.log.error(e);
        }

    }

    private void guardarContacto(int idProveedor, ContactoProveedorVO contactoProveedorVO, String sesion) {
        try {
            if (!contactoProveedorVO.getNombre().isEmpty()) {
                ContactoProveedor contactoProveedor = traerContactoPorProveedor(idProveedor, contactoProveedorVO.getNombre(), contactoProveedorVO.getTipoID());
                if (contactoProveedor == null) {
                    contactoProveedor = new ContactoProveedor();
                    contactoProveedor.setProveedor(new Proveedor(idProveedor));
                    contactoProveedor.setGenero(new Usuario(sesion));
                    contactoProveedor.setFechaGenero(new Date());
                    contactoProveedor.setHoraGenero(new Date());
                } else {
                    contactoProveedor.setModifico(new Usuario(sesion));
                    contactoProveedor.setFechaModifico(new Date());
                    contactoProveedor.setHoraModifico(new Date());
                }
                contactoProveedor.setPvArea(new PvArea(contactoProveedorVO.getTipoID()));
                contactoProveedor.setNombre(contactoProveedorVO.getNombre());
                contactoProveedor.setPuesto(contactoProveedorVO.getPuesto());
                contactoProveedor.setCelular(contactoProveedorVO.getCelular());
                contactoProveedor.setTelefono(contactoProveedorVO.getTelefono());
                contactoProveedor.setCorreo(contactoProveedorVO.getCorreo());
                contactoProveedor.setRfc(contactoProveedorVO.getRfc());
                contactoProveedor.setCurp(contactoProveedorVO.getCurp());
                contactoProveedor.setPoderNotarial(contactoProveedorVO.getPoder());
                contactoProveedor.setNoNotaria(contactoProveedorVO.getNotaria());
                contactoProveedor.setEmision(contactoProveedorVO.getEmision());
                contactoProveedor.setNotario(contactoProveedorVO.getNombreNotario());
                contactoProveedor.setIdTipo(contactoProveedorVO.getTipoTxt());
                contactoProveedor.setReferencia(contactoProveedorVO.getReferencia());
                contactoProveedor.setIdVigencia(contactoProveedorVO.getIdVigencia());
                //            
                contactoProveedor.setActivo(Constantes.BOOLEAN_TRUE);
                contactoProveedor.setEliminado(Constantes.NO_ELIMINADO);
                //
                contactoProveedor.setNotifica(contactoProveedorVO.isNotifica());
                edit(contactoProveedor);
            }
        } catch (Exception e) {
            UtilLog4j.log.error(e);
        }
    }

    private ContactoProveedor traerContactoPorProveedor(int idProveedor, String nombre, int tipo) {
        try {
            String sb = " select * from contacto_proveedor cp "
                    + "      where cp.proveedor = ?1"
                    + "      and cp.nombre = ?2  "
                    + "      and cp.PV_AREA = ?3  "
                    + "      and cp.eliminado = 'False'";
            Query q = em.createNativeQuery(sb, ContactoProveedor.class);
            q.setParameter(1, idProveedor);
            q.setParameter(2, nombre);
            q.setParameter(3, tipo);
            return (ContactoProveedor) q.getSingleResult();
        } catch (Exception e) {
            UtilLog4j.log.error(e);
            UtilLog4j.log.error(this, "Ocurrio un errro al recuperar los contacto del  proveedor : : : " + idProveedor);
            return null;
        }
    }

    
    public String correoNotifica(int idProveedor) {
        try {
            String s = "select  COALESCE(array_to_string(array_agg(DISTINCT cp.correo), ', '), '') from contacto_proveedor cp \n"
                    + "                     where cp.proveedor = ?1 \n"
                    + "                     and cp.notifica = true \n"
                    + "                     and cp.eliminado = false";
            return (String) em.createNativeQuery(s).setParameter(1, idProveedor).getSingleResult();
        } catch (Exception e) {
            UtilLog4j.log.error(e);
            return Constantes.VACIO;
        }
    }
}
