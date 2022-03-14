/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.rh.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.LocalBean;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.modelo.Convenio;
import sia.modelo.RhConvenioDocumentos;
import sia.modelo.RhDocumentos;
import sia.modelo.SiAdjunto;
import sia.modelo.Usuario;
import sia.modelo.contrato.vo.RhConvenioDocumentoVo;
import sia.modelo.proveedor.Vo.ProveedorVo;
import sia.modelo.sistema.AbstractFacade;
import sia.notificaciones.convenio.impl.NotificacionConvenioImpl;
import sia.servicios.convenio.impl.ConvenioImpl;
import sia.servicios.sistema.impl.SiAdjuntoImpl;
import sia.util.UtilLog4j;

/**
 *
 * @author mluis
 */
@LocalBean 
public class RhConvenioDocumentosImpl extends AbstractFacade<RhConvenioDocumentos>{

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public RhConvenioDocumentosImpl() {
        super(RhConvenioDocumentos.class);
    }
    @Inject
    SiAdjuntoImpl adjuntoRemote;
    @Inject
    NotificacionConvenioImpl notificacionConvenioLocal;
    @Inject
    ConvenioImpl convenioRemote;

    
    public void guardar(String sesion, RhConvenioDocumentoVo convenioDocumentoVo) {
        RhConvenioDocumentos rcd = new RhConvenioDocumentos();
        rcd.setConvenio(new Convenio(convenioDocumentoVo.getIdConvenio()));
        rcd.setRhDocumentos(new RhDocumentos(convenioDocumentoVo.getIdDocumento()));
        if (convenioDocumentoVo.getIdAdjunto() > 0) {
            rcd.setSiAdjunto(new SiAdjunto(convenioDocumentoVo.getIdAdjunto()));
        }
        rcd.setObservacion(convenioDocumentoVo.getObservacion());
        rcd.setValido(Boolean.FALSE);
        rcd.setGenero(new Usuario(sesion));
        rcd.setFechaGenero(new Date());
        rcd.setHoraGenero(new Date());
        rcd.setEliminado(Boolean.FALSE);
        //
        create(rcd);

    }

    
    public void agregarArchivo(String sesion, RhConvenioDocumentoVo convenioDocumentoVo, int idAdjunto) {
        //        
        RhConvenioDocumentos rcd = find(convenioDocumentoVo.getId());
        rcd.setObservacion(convenioDocumentoVo.getObservacion());
        rcd.setSiAdjunto(new SiAdjunto(idAdjunto));
        rcd.setValido(Boolean.TRUE);
        rcd.setModifico(new Usuario(sesion));
        rcd.setFechaModifico(new Date());
        rcd.setHoraModifico(new Date());
        //
        edit(rcd);
    }

    
    public void quitarArchivo(String sesion, int idConvDocto) {
        RhConvenioDocumentos rcd = find(idConvDocto);
        //
        adjuntoRemote.eliminarArchivo(rcd.getSiAdjunto().getId(), sesion);
        //
        rcd.setSiAdjunto(null);
        rcd.setValido(Boolean.FALSE);
        rcd.setModifico(new Usuario(sesion));
        rcd.setFechaModifico(new Date());
        rcd.setHoraModifico(new Date());
        //
        edit(rcd);
    }

    
    public List<RhConvenioDocumentoVo> traerPorConvenio(int convenioId) {
        String c = consulta()
                + " where rcd.convenio  = " + convenioId
                + " \n and rcd.eliminado  = false ";
        List<Object[]> objs = em.createNativeQuery(c).getResultList();
        List<RhConvenioDocumentoVo> cds = new ArrayList<RhConvenioDocumentoVo>();
        for (Object[] obj : objs) {
            cds.add(cast(obj));
        }
        return cds;
    }

    
    public List<RhConvenioDocumentoVo> traerDoctosNoPeriodicosPorConvenio(int convenioId) {
        String c = consulta()
                + " where rcd.convenio  = " + convenioId
                + " and rd.sg_periodicidad is null"
                + " \n and rcd.eliminado  = false ";
        List<Object[]> objs = em.createNativeQuery(c).getResultList();
        List<RhConvenioDocumentoVo> cds = new ArrayList<RhConvenioDocumentoVo>();
        for (Object[] obj : objs) {
            cds.add(cast(obj));
        }
        return cds;
    }

    
    public List<RhConvenioDocumentoVo> traerDoctosPeriodicosPorConvenio(int convenioId) {
        String c = consulta()
                + " where rcd.convenio  = " + convenioId
                + " and rd.sg_periodicidad is not null"
                + " \n and rcd.eliminado  = false ";
        List<Object[]> objs = em.createNativeQuery(c).getResultList();
        List<RhConvenioDocumentoVo> cds = new ArrayList<RhConvenioDocumentoVo>();
        for (Object[] obj : objs) {
            cds.add(cast(obj));
        }
        return cds;
    }

    
    public List<RhConvenioDocumentoVo> traerDoctosPeriodicosPorConvenioPorDocumento(int convenioId, int doctoId) {
        String c = consulta()
                + " where rcd.convenio  = " + convenioId
                + " and rd.id = " + doctoId
                + " \n and rcd.eliminado  = false ";
        List<Object[]> objs = em.createNativeQuery(c).getResultList();
        List<RhConvenioDocumentoVo> cds = new ArrayList<RhConvenioDocumentoVo>();
        for (Object[] obj : objs) {
            cds.add(cast(obj));
        }
        return cds;
    }

    private String consulta() {
        String c = "select rcd.id , c.id as conv_id, c.codigo as codigo_conv, c.nombre as convenio, rd.id as docto_id"
                + " , rd.nombre as documento\n"
                + " ,sa.id as adj_id, sa.nombre as adj, sa.uuid\n"
                + " ,rcd.observacion, rcd.valido "
                + " , sp.id as id_periodicidad, sp.nombre as periodicidad, sp.mes "
                + " , sa.fecha_genero "
                + " from rh_convenio_documentos rcd \n"
                + "	inner join convenio c on rcd.convenio  = c.id \n"
                + "	inner join rh_documentos rd on rcd.rh_documentos  = rd.id \n"
                + "     left  join sg_periodicidad sp on rd.sg_periodicidad  = sp.id \n"
                + "	left  join si_adjunto sa on rcd.si_adjunto = sa.id  and sa.eliminado = false \n";
        return c;
    }

    private RhConvenioDocumentoVo cast(Object[] obj) {
        RhConvenioDocumentoVo cdVo = new RhConvenioDocumentoVo();
        cdVo.setId((Integer) obj[0]);
        cdVo.setIdConvenio((Integer) obj[1]);
        cdVo.setCodigoConvenio((String) obj[2]);
        cdVo.setConvenio((String) obj[3]);
        cdVo.setIdDocumento((Integer) obj[4]);
        cdVo.setDocumento((String) obj[5]);
        cdVo.setIdAdjunto(obj[6] != null ? (Integer) obj[6] : 0);
        cdVo.setAdjunto((String) obj[7]);
        cdVo.setUuId((String) obj[8]);
        cdVo.setObservacion((String) obj[9]);
        cdVo.setValido((Boolean) obj[10]);
        cdVo.setIdPeriodicidad(obj[11] != null ? (Integer) obj[11] : 0);
        cdVo.setPeriodicidad((String) obj[12]);
        cdVo.setMes(obj[13] != null ? (Integer) obj[13] : 0);
        cdVo.setFechaGenero((Date) obj[14]);
        //
        return cdVo;
    }

    
    public List<RhConvenioDocumentoVo> traerDoctosNoPeriodicosPorProveedor(int proveedorId) {
        String c = consulta()
                + " where c.proveedor  = " + proveedorId
                + " and rd.sg_periodicidad is null"
                + " \n and rcd.eliminado  = false ";
        List<Object[]> objs = em.createNativeQuery(c).getResultList();
        List<RhConvenioDocumentoVo> cds = new ArrayList<RhConvenioDocumentoVo>();
        for (Object[] obj : objs) {
            cds.add(cast(obj));
        }
        return cds;
    }

    
    public List<ProveedorVo> proveedoresConDocumentación() {
        String c = "select distinct p.id,  p.rfc, p.nombre as proveedor  from rh_convenio_documentos rcd \n"
                + "	inner join convenio c on rcd.convenio  = c.id \n"
                + "	inner join proveedor p  on c.proveedor  = p.id \n"
                + "	inner join rh_documentos rd on rcd.rh_documentos  = rd.id \n"
                + "where  rcd.eliminado  = false ";
        //
        List<Object[]> objs = em.createNativeQuery(c).getResultList();
        List<ProveedorVo> lista = new ArrayList<ProveedorVo>();
        for (Object[] obj : objs) {
            ProveedorVo pvo = new ProveedorVo();
            pvo.setIdProveedor((Integer) obj[0]);
            pvo.setRfc((String) obj[1]);
            pvo.setNombre((String) obj[2]);
            lista.add(pvo);
        }
        return lista;
    }

    
    public List<RhConvenioDocumentoVo> traerDocumentacionDistintaPorConvenio(int convenioId) {
        String c = "select distinct rd.id, rd.nombre, c.id, sp.mes from rh_convenio_documentos rcd \n"
                + "	inner join convenio c on rcd.convenio  = c.id \n"
                + "	inner join proveedor p  on c.proveedor  = p.id \n"
                + "	inner join rh_documentos rd on rcd.rh_documentos  = rd.id\n"
                + "	left join sg_periodicidad sp on rd.sg_periodicidad  = sp.id \n"
                + " where rcd.convenio  = " + convenioId + "\n"
                + " and rcd.eliminado  = false\n"
                + " order by rd.nombre ";

        List<RhConvenioDocumentoVo> lista = new ArrayList<RhConvenioDocumentoVo>();
        List<Object[]> objs = em.createNativeQuery(c).getResultList();
        for (Object[] obj : objs) {
            RhConvenioDocumentoVo rcdVo = new RhConvenioDocumentoVo();
            rcdVo.setIdDocumento((Integer) obj[0]);
            rcdVo.setDocumento((String) obj[1]);
            rcdVo.setIdConvenio((Integer) obj[2]);
            rcdVo.setPeriodicidad(obj[3] != null ? ((Integer) obj[3]) + " Mes " : "");
            //
            lista.add(rcdVo);
        }
        return lista;
    }

    
    public void eliminarConvenioDocumento(String sesion, int doctoId, int convenioId) {
        List<RhConvenioDocumentos> lista = doctosPorIdConvenio(doctoId, convenioId);
        if (lista != null) {
            for (RhConvenioDocumentos rhConvenioDocumentos : lista) {
                rhConvenioDocumentos.setModifico(new Usuario(sesion));
                rhConvenioDocumentos.setFechaModifico(new Date());
                rhConvenioDocumentos.setHoraModifico(new Date());
                rhConvenioDocumentos.setEliminado(Boolean.TRUE);
                //
                edit(rhConvenioDocumentos);
            }
        }
    }

    private List<RhConvenioDocumentos> doctosPorIdConvenio(int doctoId, int convenioId) {
        try {
            String c = "select  * from rh_convenio_documentos rcd  "
                    + " where rcd.convenio  = " + convenioId
                    + "and rcd.rh_documentos = " + doctoId
                    + "and rcd.eliminado  = false ";

            return em.createNativeQuery(c, RhConvenioDocumentos.class).getResultList();
        } catch (Exception e) {
            UtilLog4j.log.error(e);
            return null;
        }
    }

    
    public List<RhConvenioDocumentoVo> traerDoctosPeriodicosPorProveedor(int proveedorId) {
        String c = consulta()
                + " where c.proveedor  = " + proveedorId
                + " and rd.sg_periodicidad is not null"
                + " \n and rcd.eliminado  = false ";
        List<Object[]> objs = em.createNativeQuery(c).getResultList();
        List<RhConvenioDocumentoVo> cds = new ArrayList<RhConvenioDocumentoVo>();
        for (Object[] obj : objs) {
            cds.add(cast(obj));
        }
        return cds;
    }

    
    public RhConvenioDocumentoVo buscarPorId(int doctoRhId) {
        String c = consulta()
                + " where rcd.id  = " + doctoRhId
                + " \n and rcd.eliminado  = false ";
        Object[] objs = (Object[]) em.createNativeQuery(c).getSingleResult();
        RhConvenioDocumentoVo cdsVo = new RhConvenioDocumentoVo();
        cdsVo = cast(objs);
        return cdsVo;
    }

    
    public void enviarObservacion(RhConvenioDocumentoVo doctosRhVo, String correoPara) {
        //ContratoVO contratoVO = convenioRemote.traerConveniosPorCodigo(doctosRhVo.getCodigoConvenio());
        //
        notificacionConvenioLocal.notificacionObservacionRh(correoPara, "Observación de Recursos Humanos", doctosRhVo);
    }
}
