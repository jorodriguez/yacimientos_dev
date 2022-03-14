/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.sistema.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.LocalBean;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.constantes.Constantes;
import sia.modelo.SiCatalogoHidrocarburo;
import sia.modelo.SiFactura;
import sia.modelo.SiFacturaContenidoNacional;
import sia.modelo.Usuario;
import sia.modelo.sistema.AbstractFacade;
import sia.modelo.sistema.vo.FacturaContenidoNacionalVo;
import sia.modelo.sistema.vo.FacturaVo;
import sia.util.FacturaEstadoEnum;
import sia.util.UtilLog4j;

/**
 *
 * @author mluis
 */
@LocalBean 
public class SiFacturaContenidoNacionalImpl extends AbstractFacade<SiFacturaContenidoNacional> {

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SiFacturaContenidoNacionalImpl() {
        super(SiFacturaContenidoNacional.class);
    }

    
    public void guardar(String sesion, int idFactura, FacturaContenidoNacionalVo contenido) {
        SiFacturaContenidoNacional fcn = new SiFacturaContenidoNacional();
        fcn.setSiFactura(new SiFactura(idFactura));
        fcn.setSiCatalogoHidrocarburo(new SiCatalogoHidrocarburo(contenido.getIdCatHidro()));
        fcn.setContenidoNacional(contenido.getProporcionContenido().doubleValue());
        fcn.setMontoFacturado(contenido.getMontoFacturado());
        fcn.setGenero(new Usuario(sesion));
        fcn.setFechaGenero(new Date());
        fcn.setHoraGenero(new Date());
        fcn.setEliminado(Boolean.FALSE);
        //
        create(fcn);
    }

    
    public List<FacturaContenidoNacionalVo> contedinoNacionaPorFactura(int idFactura) {
        String s = " select fcn.id, fcn.si_factura, ch.id, ch.codigo, ch.nombre, fcn.contenido_nacional, fcn.monto_facturado"
                + "  from si_factura_contenido_nacional fcn "
                + "     inner join si_catalogo_hidrocarburo ch on fcn.si_catalogo_hidrocarburo = ch.id "
                + "  where fcn.si_factura = ? and fcn.eliminado = false ";
        List<Object[]> objects = em.createNativeQuery(s).setParameter(1, idFactura).getResultList();
        //
        List<FacturaContenidoNacionalVo> conts = new ArrayList<>();
        double total = 0;
        for (Object[] object : objects) {
            FacturaContenidoNacionalVo cn = new FacturaContenidoNacionalVo();
            cn.setId((Integer) object[0]);
            cn.setIdFactura((Integer) object[1]);
            cn.setIdCatHidro((Integer) object[2]);
            cn.setCodigoCatalogoHidro((String) object[3]);
            cn.setNombreCatalogoHidro((String) object[4]);
            cn.setProporcionContenido((BigDecimal) object[5]);
            cn.setMontoFacturado((BigDecimal) object[6]);
            //
            total = cn.getMontoFacturado().add(new BigDecimal(total)).doubleValue();
            conts.add(cn);
        }
        FacturaContenidoNacionalVo cn = new FacturaContenidoNacionalVo();
        cn.setId(Constantes.CERO);
        cn.setNombreCatalogoHidro("Total: ");
        cn.setTotalMontoFacturado(new BigDecimal(total));
        conts.add(cn);
        //

        return conts;
    }

    
    public void eliminar(String sesion, int idFacContenido) {
        SiFacturaContenidoNacional fcn = find(idFacContenido);
        //
        fcn.setEliminado(Boolean.TRUE);
        fcn.setModifico(new Usuario(sesion));
        fcn.setFechaModifico(new Date());
        fcn.setHoraModifico(new Date());
        //
        edit(fcn);
    }

    
    public void modificar(String sesion, FacturaContenidoNacionalVo facturaContenidoNacionalVo) {
        SiFacturaContenidoNacional fcn = find(facturaContenidoNacionalVo.getId());
        fcn.setSiCatalogoHidrocarburo(new SiCatalogoHidrocarburo(facturaContenidoNacionalVo.getIdCatHidro()));
        fcn.setContenidoNacional(facturaContenidoNacionalVo.getProporcionContenido().doubleValue());
        fcn.setMontoFacturado(facturaContenidoNacionalVo.getMontoFacturado());
        //
        fcn.setModifico(new Usuario(sesion));
        fcn.setFechaModifico(new Date());
        fcn.setHoraModifico(new Date());
        //
        edit(fcn);
    }

    
    public List<FacturaVo> traerTotalContNacPorCompania(String compania, int anio) {
        List<FacturaVo> lista = new ArrayList<>();
        try {
            String c = "SELECT a.codeproy, round(sum(fcn.monto_facturado::numeric),2) from si_factura_contenido_nacional fcn \n"
                    + "		inner join si_factura f on fcn.si_factura = f.id \n"
                    + "         inner join oc_factura_status fe on fe.si_factura = f.id \n"
                    + "		inner join orden o on f.orden = o.id \n"
                    + "         inner join ap_campo a on o.ap_campo = a.id \n"
                    + "  where o.compania =  ? \n"
                    + "  and extract(year from fcn.fecha_genero) = ? \n"
                    + "  and fe.estatus >=  " + FacturaEstadoEnum.PROCESO_INTERNO_CLIENTE.getId()
                    + "  and o.eliminado = false \n"
                    + "  and fe.actual = true \n"
                    + "  and fcn.eliminado = false \n"
                    + "    and f.eliminado = false \n"
                    + "  GROUP by a.codeproy \n"
                    + "  order by a.codeproy";
            List<Object[]> lo = em.createNativeQuery(c).setParameter(1, compania).setParameter(2, anio).getResultList();
            for (Object[] objects : lo) {
                FacturaVo o = new FacturaVo();
                o.setCampo((String) objects[0]);
                o.setMonto(((BigDecimal) objects[1]));
                //
                lista.add(o);
            }
        } catch (Exception e) {
            UtilLog4j.log.error(e);
        }
        return lista;
    }

    /**
     *
     * @param idFactura
     * @return
     */
    
    public BigDecimal totalPorFactura(int idFactura) {
        String sq = " select coalesce(sum(fcn.monto_facturado), 0) "
                + "from si_factura_contenido_nacional fcn  where fcn.si_factura = ?  and fcn.eliminado = false ";
        return (BigDecimal) em.createNativeQuery(sq).setParameter(1, idFactura).getSingleResult();
    }

}
