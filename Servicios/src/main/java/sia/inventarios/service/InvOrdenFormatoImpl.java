/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.inventarios.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.LocalBean;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.constantes.Constantes;
import sia.modelo.InvOrdenFormato;
import sia.modelo.InvtipoMovmiento;
import sia.modelo.SiAdjunto;
import sia.modelo.Usuario;
import sia.modelo.sistema.AbstractFacade;
import sia.modelo.vo.inventarios.OrdenFormatoVo;
import sia.servicios.orden.impl.OrdenImpl;
import sia.servicios.sistema.impl.SiAdjuntoImpl;

/**
 *
 * @author mluis
 */
@LocalBean 
public class InvOrdenFormatoImpl extends AbstractFacade<InvOrdenFormato>{

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public InvOrdenFormatoImpl() {
        super(InvOrdenFormato.class);
    }

    @Inject
    OrdenImpl ordenRemote;
    @Inject
    SiAdjuntoImpl siAdjuntoRemote;

    
    public void guardar(String sesion, String codigoOrden, int idAdjunto, int idMovmiento) {
        InvOrdenFormato iof = new InvOrdenFormato();
        iof.setOrden(ordenRemote.buscarPorConsecutivo(codigoOrden));
        iof.setSiAdjunto(new SiAdjunto(idAdjunto));
        iof.setInvtipoMovmiento(new InvtipoMovmiento(idMovmiento));
        iof.setGenero(new Usuario(sesion));
        iof.setFechaGenero(new Date());
        iof.setHoraGenero(new Date());
        iof.setEliminado(Constantes.BOOLEAN_FALSE);
        //
        create(iof);
    }

    
    public List<OrdenFormatoVo> traerPorMovimiento(String compra, int idMov) {
        String c = consulta()
                + " where ofo.inv_tipo_movimiento = " + idMov
                + " and o.consecutivo = '" + compra + "'"
                + " and ofo.eliminado  = false"
                + " and a.eliminado  = false ";
        //
        List<Object[]> lista = em.createNativeQuery(c).getResultList();
        List<OrdenFormatoVo> formatos = new ArrayList<OrdenFormatoVo>();
        for (Object[] objects : lista) {
            formatos.add(cast(objects));
        }
        return formatos;
    }

    
    public void eliminar(String sesion, int idOrdenFormato) {
        InvOrdenFormato iof = find(idOrdenFormato);
        iof.setEliminado(Constantes.BOOLEAN_TRUE);
        iof.setModifico(new Usuario(sesion));
        iof.setFechaModifico(new Date());
        iof.setHoraModifico(new Date());
        //
        edit(iof);
        //
        siAdjuntoRemote.eliminarArchivo(iof.getSiAdjunto().getId(), sesion);
    }

    
    public List<OrdenFormatoVo> traerPorCampo(int idCampo) {
        String c = consulta()
                + " where o.ap_campo = " + idCampo
                + " and ofo.eliminado  = false"
                + " and a.eliminado  = false";
        //
        List<Object[]> lista = em.createNativeQuery(c).getResultList();
        List<OrdenFormatoVo> formatos = new ArrayList<OrdenFormatoVo>();
        for (Object[] objects : lista) {
            formatos.add(cast(objects));
        }
        return formatos;
    }

    
    public List<OrdenFormatoVo> traerPorFecha(Date inicio, Date fin, int idCampo) {
        String c = consulta()
                + " where o.ap_campo = " + idCampo
                + " and ofo.fecha_genero between   ?  and ? "
                + "and ofo.eliminado  = false"
                + " and a.eliminado  = false";
        //
        List<Object[]> lista = em.createNativeQuery(c).setParameter(1, inicio).setParameter(2, fin).getResultList();
        List<OrdenFormatoVo> formatos = new ArrayList<OrdenFormatoVo>();
        for (Object[] objects : lista) {
            formatos.add(cast(objects));
        }
        return formatos;
    }

    
    public List<OrdenFormatoVo> traerPorCompra(String compra) {
        String c = consulta()
                + " where o.consecutivo = '" + compra + "'"
                + "and ofo.eliminado  = false"
                + " and a.eliminado  = false";
        //
        List<Object[]> lista = em.createNativeQuery(c).getResultList();
        List<OrdenFormatoVo> formatos = new ArrayList<OrdenFormatoVo>();
        for (Object[] objects : lista) {
            formatos.add(cast(objects));
        }
        return formatos;
    }

    private OrdenFormatoVo cast(Object[] objects) {

        OrdenFormatoVo ofo = new OrdenFormatoVo();
        ofo.setId((Integer) objects[0]);
        ofo.setOrden((String) objects[1]);
        ofo.setReferencia((String) objects[2]);
        ofo.setIdProveedor((Integer) objects[3]);
        ofo.setProveedor((String) objects[4]);
        ofo.setIdAdjunto((Integer) objects[5]);
        ofo.setArchivo((String) objects[6]);
        ofo.setUuId((String) objects[7]);
        ofo.setFechaGenero((Date) objects[8]);
        ofo.setPedido((String) objects[9]);
        return ofo;
    }

    private String consulta() {
        String s = " select ofo.id, o.consecutivo , o.referencia, p.id , p.nombre , a.id , a.nombre, a.uuid, ofo.fecha_genero, o.navcode from inv_orden_formato ofo\n"
                + "  	 inner join orden o on ofo.orden = o.id \n"
                + "	 inner join proveedor  p on o.proveedor = p.id \n"
                + "	 inner join si_adjunto a on ofo.si_adjunto = a.id\n";
        return s;
    }
}
