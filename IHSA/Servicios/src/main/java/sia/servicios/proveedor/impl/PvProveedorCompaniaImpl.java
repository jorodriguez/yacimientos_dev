/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.proveedor.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.constantes.Constantes;
import sia.modelo.Compania;
import sia.modelo.Proveedor;
import sia.modelo.PvProveedorCompania;
import sia.modelo.Usuario;
import sia.modelo.proveedor.Vo.ProveedorVo;
import sia.modelo.sistema.AbstractFacade;
import sia.modelo.vo.CompaniaVo;
import sia.util.UtilLog4j;

/**
 *
 * @author ihsa
 */
@Stateless 
public class PvProveedorCompaniaImpl extends AbstractFacade<PvProveedorCompania>{

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public PvProveedorCompaniaImpl() {
        super(PvProveedorCompania.class);
    }

    
    public void guardarRelacionProveedor(int idProveedor, String rfcCompania, String numeroReferencia, String sesion) {
        try {
            if (!buscarRelacionProveedorCompania(idProveedor, rfcCompania)) {
                PvProveedorCompania proveedorCompania = new PvProveedorCompania();
                proveedorCompania.setProveedor(new Proveedor(idProveedor));
                proveedorCompania.setCompania(new Compania(rfcCompania));
                proveedorCompania.setReferencia(numeroReferencia);
                proveedorCompania.setGenero(new Usuario(sesion));
                proveedorCompania.setFechaGenero(new Date());
                proveedorCompania.setHoraGenero(new Date());
                proveedorCompania.setEliminado(Constantes.NO_ELIMINADO);
                create(proveedorCompania);
            }
        } catch (Exception e) {
            UtilLog4j.log.error(e);
        }
    }

    
    public List<CompaniaVo> traerCompaniaPorProveedor(int idProveedor) {
        clearQuery();
        try {
            query.append("select pc.id, c.rfc, c.nombre, pc.referencia, pc.proveedor ");
            query.append(" from pv_PROVEEDOR_compania pc, compania c");
            query.append(" where pc.proveedor = ").append(idProveedor).append(" and pc.eliminado ='False'");
            query.append(" and pc.compania = c.rfc");
            List<Object[]> lo = em.createNativeQuery(query.toString()).getResultList();
            List<CompaniaVo> lpc = null;
            if (lo != null) {
                lpc = new ArrayList<>();
                for (Object[] objects : lo) {
                    lpc.add(castProveedorCompania(objects));
                }
            }
            return lpc;
        } catch (Exception e) {
            return null;
        }
    }

    private CompaniaVo castProveedorCompania(Object[] objects) {
        CompaniaVo c = new CompaniaVo();
        c.setIdProveedorCompania((Integer) objects[0]);
        c.setRfcCompania((String) objects[1]);
        c.setNombre((String) objects[2]);
        c.setNumeroReferencia((String) objects[3] != null ? (String) objects[3] : "-");
        c.setIdProveedor((Integer) objects[4] != null ? (Integer) objects[4] : 0);
        c.setEditar(false);
        c.setSelected(false);
        return c;
    }

    
    public boolean buscarRelacionProveedorCompania(int idProveedor, String rfcCompania) {
        clearQuery();
        boolean v = false;
        try {
            query.append("select pc.id, pc.referencia ");
            query.append(" from pv_PROVEEDOR_compania pc");
            query.append(" where pc.proveedor = ").append(idProveedor).append(" and pc.eliminado ='False'");
            query.append(" and pc.compania = '").append(rfcCompania).append("'");
//
            List<Object[]> lo = em.createNativeQuery(query.toString()).getResultList();
            if (lo != null && lo.size() > 0) {
                v = true;
            }
            return v;
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Error al buscar el proveedor y compania " + e);
            return v;
        }
    }

    
    public void modificarRel(String idSesion, CompaniaVo companiaVo) {
        PvProveedorCompania proveedorCompania = find(companiaVo.getIdProveedorCompania());
        String ae = proveedorCompania.toString();
        proveedorCompania.setReferencia(companiaVo.getNumeroReferencia());
        proveedorCompania.setModifico(new Usuario(idSesion));
        proveedorCompania.setFechaModifico(new Date());
        proveedorCompania.setHoraModifico(new Date());

        edit(proveedorCompania);

    }

    
    public void eliminarRel(String idSesion, CompaniaVo cvo) {
        PvProveedorCompania proveedorCompania = find(cvo.getIdProveedorCompania());
        String ae = proveedorCompania.toString();
        proveedorCompania.setModifico(new Usuario(idSesion));
        proveedorCompania.setFechaModifico(new Date());
        proveedorCompania.setHoraModifico(new Date());
        proveedorCompania.setEliminado(Constantes.ELIMINADO);
        edit(proveedorCompania);

    }

    
    public List<ProveedorVo> traerProveedorPorCompania(String compania, int status) {
        clearQuery();
        try {
            query.append("select p.id , p.nombre ")
                    .append(" from pv_PROVEEDOR_compania pc")
                    .append("   inner join proveedor p on pc.proveedor = p.id")
                    .append(" where pc.compania = '").append(compania).append("' and pc.eliminado = false")
                    .append(" and p.status  = ").append(status)
                    .append(" order by p.nombre asc");

            List<Object[]> lo = em.createNativeQuery(query.toString()).getResultList();
            List<ProveedorVo> lpc = null;
            if (lo != null) {
                lpc = new ArrayList<>();
                for (Object[] objects : lo) {
                    ProveedorVo prov = new ProveedorVo();
                    prov.setIdProveedor((Integer) objects[0]);
                    prov.setNombre((String) objects[1]);
                    lpc.add(prov);
                }
            }
            return lpc;
        } catch (Exception e) {
            return null;
        }
    }
}
