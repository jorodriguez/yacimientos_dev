/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.sgl.incidencia.vehiculo.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.LocalBean;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.constantes.Constantes;
import sia.modelo.SiFactura;
import sia.modelo.SiIncidencia;
import sia.modelo.SiIncidenciaFactura;
import sia.modelo.Usuario;
import sia.modelo.sgl.vo.OrdenDetalleVO;
import sia.modelo.sistema.AbstractFacade;
import sia.modelo.sistema.vo.FacturaVo;
import sia.servicios.sistema.impl.SiFacturaImpl;
import sia.util.UtilLog4j;

/**
 *
 * @author ihsa
 */
@LocalBean 
public class SiIncidenciaFacturaImpl extends AbstractFacade<SiIncidenciaFactura>{

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SiIncidenciaFacturaImpl() {
        super(SiIncidenciaFactura.class);
    }
    @Inject
    private SiFacturaImpl siFacturaRemote;

    
    public List<FacturaVo> traerFacturaPorIncidencia(int idIncidencia) {
        clearQuery();
        query.append("select p.nombre, f.concepto, f.folio, f.monto, m.siglas, f.observacion, f.fecha_emision, a.id, a.nombre, a.uuid, f.id, ifact.id  from si_incidencia_factura ifact");
        query.append("      inner join si_factura f on ifact.si_factura = f.id ");
        query.append("      inner join proveedor p on f.proveedor = p.id ");
        query.append("      inner join moneda m on f.moneda = m.id ");
        query.append("      left join si_adjunto a on f.si_adjunto = a.id ");
        query.append("  where ifact.si_incidencia = ").append(idIncidencia);
        query.append("  and ifact.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");
        query.append("  and f.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");
        List<Object[]> objects = em.createNativeQuery(query.toString()).getResultList();
        List<FacturaVo> lfac = new ArrayList<>();
        for (Object[] obj : objects) {
            lfac.add(castFacturaVo(obj));
        }
        return lfac;
    }

    private FacturaVo castFacturaVo(Object[] objects) {
        FacturaVo facturaVo = new FacturaVo();
        facturaVo.setProveedor((String) objects[0]);
        facturaVo.setConcepto((String) objects[1]);
        facturaVo.setFolio((String) objects[2]);
        facturaVo.setMonto((BigDecimal) objects[3]);
        facturaVo.setMoneda((String) objects[4]);
        facturaVo.setObservacion((String) objects[5]);
        facturaVo.setFechaEmision((Date) objects[6]);
        facturaVo.setIdAdjunto(objects[7] != null ? (Integer) objects[7] : 0);
        facturaVo.setAdjunto((String) objects[8]);
        facturaVo.setUuId((String) objects[9]);
        facturaVo.setId((Integer) objects[10]);
        facturaVo.setIdRelacion((Integer) objects[11]);
        return facturaVo;
    }

    
    public boolean guardar(int incidencia, FacturaVo facturaVo, String sesion) {
        boolean v = true;
        try {
            SiFactura siFactura = siFacturaRemote.guardarFactura(facturaVo, sesion, new ArrayList<OrdenDetalleVO>(), Constantes.VACIO);
            //
            SiIncidenciaFactura siIncidenciaFactura = new SiIncidenciaFactura();
            //
            siIncidenciaFactura.setSiIncidencia(new SiIncidencia(incidencia));
            siIncidenciaFactura.setSiFactura(new SiFactura(siFactura.getId()));
            siIncidenciaFactura.setGenero(new Usuario(sesion));
            siIncidenciaFactura.setFechaGenero(new Date());
            siIncidenciaFactura.setHoraGenero(new Date());
            siIncidenciaFactura.setEliminado(Constantes.NO_ELIMINADO);
            //
            create(siIncidenciaFactura);
            //
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Error al guardar la factura -- incidencia  + + + + + + + +" + e.getMessage());
            v = false;
        }
        return v;
    }

    
    public void eliminarFactura(int idRelacion,  String sesion) {
        //
        SiIncidenciaFactura siIncidenciaFactura = find(idRelacion);
        siIncidenciaFactura.setModifico(new Usuario(sesion));
        siIncidenciaFactura.setFechaModifico(new Date());
        siIncidenciaFactura.setHoraModifico(new Date());
        siIncidenciaFactura.setEliminado(Constantes.ELIMINADO);
        //
        edit(siIncidenciaFactura);
    //eliminar factura
        siFacturaRemote.eliminarFactura(siIncidenciaFactura.getSiFactura().getId(), Constantes.CERO, sesion);
        
    }
}
