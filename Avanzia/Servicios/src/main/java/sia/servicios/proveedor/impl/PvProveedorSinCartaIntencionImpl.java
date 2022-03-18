/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.proveedor.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.modelo.ApCampo;
import sia.modelo.Proveedor;
import sia.modelo.PvProveedorSinCartaIntencion;
import sia.modelo.Usuario;
import sia.modelo.proveedor.Vo.ProveedorSinCartaIntencionVo;
import sia.modelo.sistema.AbstractFacade;
import sia.modelo.vo.ApCampoVo;
import sia.util.UtilLog4j;

/**
 *
 * @author mluis
 */
@Stateless 
public class PvProveedorSinCartaIntencionImpl extends AbstractFacade<PvProveedorSinCartaIntencion> {

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public PvProveedorSinCartaIntencionImpl() {
        super(PvProveedorSinCartaIntencion.class);
    }

    
    public void guardar(String sesion, int proveedorId, int campoId) {
        PvProveedorSinCartaIntencion ppsci;
        if (buscarProveedorCampo(campoId, proveedorId) == null) {
            ppsci = new PvProveedorSinCartaIntencion();
            ppsci.setProveedor(new Proveedor(proveedorId));
            ppsci.setApCampo(new ApCampo(campoId));
            ppsci.setGenero(new Usuario(sesion));
            ppsci.setFechaGenero(new Date());
            ppsci.setHoraGenero(new Date());
            ppsci.setEliminado(Boolean.FALSE);
            //
            create(ppsci);
        }
    }

    
    public void eliminar(String sesion, int id) {
        PvProveedorSinCartaIntencion ppsci = find(id);
        ppsci.setModifico(new Usuario(sesion));
        ppsci.setFechaModifico(new Date());
        ppsci.setFechaModifico(new Date());
        ppsci.setEliminado(Boolean.TRUE);
        //
        edit(ppsci);
    }

    
    public List<ProveedorSinCartaIntencionVo> traerTodos() {
        String c = consulta();
        c += " where ppc.eliminado = false";
        //
        List<Object[]> lista = em.createNativeQuery(c).getResultList();
        List<ProveedorSinCartaIntencionVo> provs = new ArrayList<ProveedorSinCartaIntencionVo>();
        for (Object[] objects : lista) {
            provs.add(cast(objects));
        }
        return provs;
    }

    
    public List<ProveedorSinCartaIntencionVo> traerPorCampo(int campoId) {
        String c = consulta();
        c += " where ppc.ap_campo = " + campoId
                + " and ppc.eliminado = false";
        //
        List<Object[]> lista = em.createNativeQuery(c).getResultList();
        List<ProveedorSinCartaIntencionVo> provs = new ArrayList<ProveedorSinCartaIntencionVo>();
        for (Object[] objects : lista) {
            provs.add(cast(objects));
        }
        return provs;
    }

    
    public ProveedorSinCartaIntencionVo buscarProveedorCampo(int campoId, int proveedorId) {
        try {
            String c = consulta();
            c += " where ppc.proveedor = " + proveedorId
                    + " and ppc.ap_campo = " + campoId
                    + " and ppc.eliminado = false";
            //
            Object[] lista = (Object[]) em.createNativeQuery(c).getSingleResult();
            return cast(lista);
        } catch (Exception e) {
            UtilLog4j.log.error(e);
            return null;
        }
    }
    
    
    public boolean existeProveedorCI(int campoId, int proveedorId) {
        boolean ret = false;
        try {                        
            ProveedorSinCartaIntencionVo voProvCampo = this.buscarProveedorCampo(campoId, proveedorId);            
            ret = (voProvCampo != null && voProvCampo.getId() > 0);                    
        } catch (Exception e) {
            UtilLog4j.log.error(e);
            ret = false;
        }
        return ret;
    }

    private String consulta() {
        String c = "select ppc.id, p.id, p.rfc, p.nombre, c.id, c.nombre from pv_proveedor_sin_carta_intencion ppc"
                + "     inner join proveedor p on ppc.proveedor = p.id "
                + "     inner join ap_campo c on ppc.ap_campo = c.id ";
        return c;
    }

    private ProveedorSinCartaIntencionVo cast(Object[] obj) {
        ProveedorSinCartaIntencionVo ppVo = new ProveedorSinCartaIntencionVo();
        ppVo.setId((Integer) obj[0]);
        ppVo.setIdProveedor((Integer) obj[1]);
        ppVo.setRfcProveedor((String) obj[2]);
        ppVo.setProveedor((String) obj[3]);
        ppVo.setIdCampo((Integer) obj[4]);
        ppVo.setCampo((String) obj[5]);
        //
        return ppVo;
    }

    
    public List<ApCampoVo> traerDistintosCampos() {
        String c = "select  ac.id , ac.nombre  from pv_proveedor_sin_carta_intencion ppsci \n"
                + "	inner join ap_campo ac on ppsci.ap_campo  = ac.id \n"
                + " where ppsci.eliminado  = false\n"
                + " and ac.eliminado  = false\n"
                + " group  by ac.id , ac.nombre\n"
                + " order by ac.id";
        List<Object[]> objs = em.createNativeQuery(c).getResultList();
        List<ApCampoVo> campos = new ArrayList<ApCampoVo>();
        for (Object[] obj : objs) {
            ApCampoVo cVo = new ApCampoVo();
            cVo.setId((Integer) obj[0]);
            cVo.setNombre((String) obj[1]);
            campos.add(cVo);
        }
        return campos;
    }

}
