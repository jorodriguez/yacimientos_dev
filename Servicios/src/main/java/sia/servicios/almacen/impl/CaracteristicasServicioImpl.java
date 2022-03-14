/*
 * ServicioProveedorServicioImpl.java
 * Creada el 26/08/2009, 12:25:55 PM
 * Clase Java desarrollada por: Héctor Acosta Sierra para: MPG-IHSA
 *
 * Para información sobre el uso de esta clase, asi como bugs, actualizaciones o mejoras
 * enviar un mail a: hacosta@mpg-ihsa.com.mx o a: new_nick_name@hotmail.com
 */
package sia.servicios.almacen.impl;

import java.util.ArrayList;
import java.util.List;
import javax.ejb.LocalBean;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import sia.modelo.CaracteristicasServicio;
import sia.modelo.sgl.vo.CaracteristicaServicioVO;
import sia.util.UtilLog4j;

/**
 *
 * @author Héctor Acosta Sierra
 * @version 1.0
 * @author-mail new_nick_name@hotmail.com @date 26/08/2009
 */
@LocalBean 
public class CaracteristicasServicioImpl {

    StringBuilder qry = new StringBuilder();
   
    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    public void create(CaracteristicasServicio caracteristicasServicio) {
        em.persist(caracteristicasServicio);
    }

    
    public void edit(CaracteristicasServicio caracteristicasServicio) {
        em.merge(caracteristicasServicio);
    }

    
    public void remove(CaracteristicasServicio caracteristicasServicio) {
        em.remove(em.merge(caracteristicasServicio));
    }

    public CaracteristicasServicio find(Object id) {
        return em.find(CaracteristicasServicio.class, id);
    }

    
    public List<CaracteristicasServicio> traerTodo(int posicionInicio, int tamañoFragmento) {
        return em.createQuery("select object(o) from ServicioProveedor as o").setFirstResult(posicionInicio).setMaxResults(tamañoFragmento).getResultList();
    }

    public List<CaracteristicasServicio> getPorProveedor(Object nombreProveedor, int posicionInicio, int tamañoFragmento) {
        return em.createQuery("SELECT s FROM ServicioProveedor s WHERE s.proveedor.nombre = :nombreProveedor").setParameter("nombreProveedor", nombreProveedor).setFirstResult(posicionInicio).setMaxResults(tamañoFragmento).getResultList();
    }

    public List<CaracteristicasServicio> getPorConvenio(Object nombreConvenio, int posicionInicio, int tamañoFragmento) {
        return em.createQuery("SELECT s FROM ServicioProveedor s WHERE s.convenio.nombre = :nombreConvenio").setParameter("nombreConvenio", nombreConvenio).setFirstResult(posicionInicio).setMaxResults(tamañoFragmento).getResultList();
    }

    public int getTotalServicios() {
        Query q = em.createQuery("select count(s) from ServicioProveedor as s");
        int count = ((Long) q.getSingleResult()).intValue();
        return count;
    }

    public int getTotalServiciosPorProveedor(Object nombreProveedor) {
        Query q = em.createQuery("select count(s) from ServicioProveedor as s WHERE s.proveedor.nombre = :nombreProveedor").setParameter("nombreProveedor", nombreProveedor);
        int count = ((Long) q.getSingleResult()).intValue();
        return count;
    }

    public int getTotalServiciosPorConvenio(Object nombreConvenio) {
        Query q = em.createQuery("select count(s) from ServicioProveedor as s WHERE s.convenio.nombre = :nombreConvenio").setParameter("nombreConvenio", nombreConvenio);
        int count = ((Long) q.getSingleResult()).intValue();
        return count;
    }

    //ZONA de Filtros por letra
    public List<CaracteristicasServicio> getPorProveedorLetra(Object nombreProveedor, Object letra, int posicionInicio, int tamañoFragmento) {
        return em.createQuery("SELECT s FROM ServicioProveedor s WHERE s.proveedor.nombre = :nombreProveedor AND s.servicio.nombre LIKE :letra ORDER BY s.servicio.nombre ASC").setParameter("nombreProveedor", nombreProveedor).
                setParameter("letra", letra.toString().toUpperCase() + "%") //                .setFirstResult(posicionInicio)
                //                .setMaxResults(tamañoFragmento)
                .getResultList();
    }

    public List<CaracteristicasServicio> getPorConvenioLetra(Object nombreConvenio, Object letra, int posicionInicio, int tamañoFragmento) {
        return em.createQuery("SELECT s FROM ServicioProveedor s WHERE s.convenio.nombre = :nombreConvenio AND s.servicio.nombre LIKE :letra ORDER BY s.servicio.nombre ASC").setParameter("nombreConvenio", nombreConvenio).setParameter("letra", letra.toString().toUpperCase() + "%") //                .setFirstResult(posicionInicio)
                //                .setMaxResults(tamañoFragmento)
                .getResultList();
    }

    public List<CaracteristicasServicio> getPorLetra(Object letra, int posicionInicio, int tamañoFragmento) {
        return em.createQuery("select object(o) from ServicioProveedor as o WHERE o.servicio.nombre LIKE :letra ORDER BY o.servicio.nombre ASC").setParameter("letra", letra.toString().toUpperCase() + "%") //                .setFirstResult(posicionInicio)
                //                .setMaxResults(tamañoFragmento)
                .getResultList();
    }

    public int getTotalServiciosPorLetra(Object letra) {
        return ((Long) em.createQuery("select count(s) from ServicioProveedor as s WHERE s.servicio.nombre LIKE :letra").setParameter("letra", letra.toString().toUpperCase() + "%").getSingleResult()).intValue();
    }

    public int getTotalServiciosPorProveedorLetra(Object nombreProveedor, Object letra) {
        return ((Long) em.createQuery("select count(s) from ServicioProveedor as s WHERE s.proveedor.nombre = :nombreProveedor AND s.servicio.nombre LIKE :letra").setParameter("nombreProveedor", nombreProveedor).setParameter("letra", letra.toString().toUpperCase() + "%").getSingleResult()).intValue();
    }

    public int getTotalServiciosPorConvenioLetra(Object nombreConvenio, Object letra) {
        return ((Long) em.createQuery("select count(s) from ServicioProveedor as s WHERE s.convenio.nombre = :nombreConvenio AND s.servicio.nombre LIKE :letra").setParameter("nombreConvenio", nombreConvenio).setParameter("letra", letra.toString().toUpperCase() + "%").getSingleResult()).intValue();
    }

    public List<CaracteristicasServicio> getPorId(Object id) {
        return em.createQuery("SELECT s FROM ServicioProveedor s WHERE s.servicio.id = :id ORDER BY s.proveedor.nombre ASC").setParameter("id", id).getResultList();
    }

    public CaracteristicasServicio getServicioProveedor(Object nombreProveedor, Object idServicio) {
        return (CaracteristicasServicio) em.createQuery("SELECT s FROM ServicioProveedor s WHERE s.proveedor.nombre = :nombre AND s.servicio.id =:id").setParameter("nombre", nombreProveedor).setParameter("id", idServicio).getSingleResult();
    }

    public List<CaracteristicaServicioVO> getPorClasificacionServicio(Object clasificacionServicio, Object nombreProveedor) {
        List<Object[]> lo = null;
        List<CaracteristicaServicioVO> listaCaracteristicasServicio=null;
         Query q;//cambiar por nativo la consulta
         //
        limpiarCuerpoQuery();
        qry.append(" SELECT cser.ID,");
        qry.append(" s.NOMBRE,");
        qry.append(" cser.EN_CONVENIO, ");
        qry.append(" cser.NUMERO_PARTE, ");
        qry.append(" case when cser.PRECIO is null then 0");
         qry.append(" when cser.PRECIO is not null then cser.PRECIO");
         qry.append(" end as precio,  ");
        qry.append(" cser.PRINCIPAL, ");
        //qry.append(" cser.CLASIFICACION_SERVICIO, ");
        //qry.append(" cser.CONDICION_PAGO, ");
        //qry.append(" cser.CONVENIO, ");
        qry.append(" mo.nombre,");
        qry.append(" uni.NOMBRE ");

        qry.append(" FROM PROVEEDOR_ACTIVIDAD pa,");
        qry.append(" PROVEEDOR p,");
        qry.append(" CLASIFICACION_SERVICIO cs, ");
        qry.append(" CARACTERISTICAS_SERVICIO cser, ");
        qry.append(" SERVICIO s,");
        qry.append(" UNIDAD uni,");
        qry.append(" moneda mo");
        qry.append(" WHERE ((((cs.NOMBRE = '").append(clasificacionServicio).append("') ");
        qry.append(" AND (p.NOMBRE = '").append(nombreProveedor).append("')) ");
        qry.append(" AND (cser.PRINCIPAL = 'Si'))");
        qry.append(" AND (((cs.ID = cser.CLASIFICACION_SERVICIO");
        qry.append(" AND pa.ID = cs.PROVEEDOR_ACTIVIDAD) ");
        qry.append(" AND p.ID = pa.PROVEEDOR");
        qry.append(" AND cser.UNIDAD = uni.id");
        qry.append(" ANd cser.MONEDA = mo.id) ");
        qry.append(" AND (s.ID = cser.SERVICIO))) ");
        qry.append(" ORDER BY s.NOMBRE ASC");


        lo = em.createNativeQuery(qry.toString()).getResultList();
            if (!lo.isEmpty()) {
                listaCaracteristicasServicio = new ArrayList<CaracteristicaServicioVO>();
                for (Object[] objects : lo) {                    
                    listaCaracteristicasServicio.add(castReturnObjectServicio(objects));
                }
            }
            
            return listaCaracteristicasServicio;
        //return em.createQuery("SELECT c FROM CaracteristicasServicio c WHERE c.clasificacionServicio.nombre = :clasificacion AND c.clasificacionServicio.proveedorActividad.proveedor.nombre = :proveedor AND c.principal = :principal ORDER BY c.servicio.nombre ASC").setParameter("clasificacion", clasificacionServicio).setParameter("proveedor", nombreProveedor).setParameter("principal", "Si").getResultList();
    }
    
     private CaracteristicaServicioVO castReturnObjectServicio(Object[] obj) {
        try {
            CaracteristicaServicioVO  c = new CaracteristicaServicioVO();
            c.setId((Integer) obj[0]);            
            c.setNombre((String) obj[1]);
            c.setEnConvenio((String) obj[2]);
            c.setNumeroParte((String) obj[3]);
            c.setPrecio((Double) obj[4]);
            c.setPrincipal((String) obj[5]);
            c.setNombreMoneda((String) obj[6]);
            c.setNombreUnidad((String) obj[7]);
            return c;
        } catch (Exception e) {
            UtilLog4j.log.error(e);
            return null;
        }
    }
    

    private void limpiarCuerpoQuery() {
        qry.delete(0, qry.length());
    }
}
