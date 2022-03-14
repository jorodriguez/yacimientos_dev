/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.proveedor.impl;

import java.util.Date;
import javax.ejb.LocalBean;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import sia.constantes.Constantes;
import sia.modelo.Proveedor;
import sia.modelo.PvRegistroFiscal;
import sia.modelo.Usuario;
import sia.modelo.proveedor.Vo.ProveedorVo;
import sia.modelo.sistema.AbstractFacade;
import sia.util.UtilLog4j;

/**
 *
 * @author ihsa
 */
@LocalBean 

public class PvRegistroFiscalImpl extends AbstractFacade<PvRegistroFiscal> {

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public PvRegistroFiscalImpl() {
        super(PvRegistroFiscal.class);
    }

    
    public void guardar(int proveedor, ProveedorVo proveedorVo, String sesion) {
        try {
            PvRegistroFiscal registroFiscal = buscarPorProveedor(proveedor);
            if (registroFiscal == null) {
                registroFiscal = new PvRegistroFiscal();
                registroFiscal.setProveedor(new Proveedor(proveedor));
                registroFiscal.setGenero(new Usuario(sesion));
                registroFiscal.setFechaGenero(new Date());
                registroFiscal.setHoraGenero(new Date());
            } else {
                registroFiscal.setModifico(new Usuario(sesion));
                registroFiscal.setFechaModifico(new Date());
                registroFiscal.setHoraModifico(new Date());
            }
            registroFiscal.setSede(proveedorVo.getSede());
            registroFiscal.setNoNotaria(proveedorVo.getNoNotaria());
            registroFiscal.setNoBoleta(proveedorVo.getNoBoleta());
            registroFiscal.setNoActa(proveedorVo.getNoActa());
            registroFiscal.setNombreNot(proveedorVo.getNombreNot());
            registroFiscal.setEmision(proveedorVo.getEmision());
            registroFiscal.setInscripcion(proveedorVo.getInscripcion());
            //
            registroFiscal.setEliminado(Constantes.NO_ELIMINADO);
            edit(registroFiscal);
        } catch (Exception e) {
            UtilLog4j.log.error(e);
        }
    }

    private PvRegistroFiscal buscarPorProveedor(int proveedor) {
        try {
            String c = "select * from PV_REGISTROFISCAL  where proveedor = ?1";
            Query  q = em.createNativeQuery(c, PvRegistroFiscal.class);
            q.setParameter(1, proveedor);
            return (PvRegistroFiscal) q.getSingleResult();
        } catch (Exception e) {
            UtilLog4j.log.error(e);
            return null;
        }
    }
}
