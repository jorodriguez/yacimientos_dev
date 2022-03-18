/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.convenio.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.constantes.Constantes;
import sia.modelo.ApCampo;
import sia.modelo.Compania;
import sia.modelo.Convenio;
import sia.modelo.CvConvenioExhorto;
import sia.modelo.CvConvenioGerencia;
import sia.modelo.CvFormas;
import sia.modelo.Estatus;
import sia.modelo.Usuario;
import sia.modelo.contrato.vo.ContratoVO;
import sia.modelo.contrato.vo.ExhortoVo;
import sia.modelo.proveedor.Vo.ProveedorVo;
import sia.modelo.sgl.vo.AdjuntoVO;
import sia.modelo.sistema.AbstractFacade;
import sia.modelo.usuario.vo.UsuarioVO;
import sia.notificaciones.convenio.impl.NotificacionConvenioImpl;
import sia.servicios.catalogos.impl.CompaniaImpl;
import sia.servicios.catalogos.impl.GerenciaImpl;
import sia.servicios.proveedor.impl.ProveedorServicioImpl;
import sia.servicios.sistema.impl.FolioImpl;
import sia.servicios.sistema.impl.SiAdjuntoImpl;
import sia.servicios.sistema.impl.SiUsuarioRolImpl;
import sia.util.UtilLog4j;

/**
 *
 * @author ihsa
 */
@Stateless 
public class CvConvenioExhortoImpl extends AbstractFacade<CvConvenioExhorto> {

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
    @Inject
    private NotificacionConvenioImpl notificacionConvenioLocal;
    @Inject
    private ConvenioImpl convenioRemote;
    @Inject
    private CompaniaImpl companiaRemote;
    @Inject
    ProveedorServicioImpl proveedorRemote;
    @Inject
    FolioImpl folioRemote;
    @Inject
    SiAdjuntoImpl adjuntoRemote;
    @Inject
    CvConvenioFormasImpl  convenioFormasLocal;
    @Inject
    CvFormasImpl formasLocal;
    @Inject
    SiUsuarioRolImpl usuarioRolRemote;
    @Inject
    CvConvenioGerenciaImpl convenioGerenciaLocal;
    @Inject
    GerenciaImpl gerenciaRemote;

    public CvConvenioExhortoImpl() {
        super(CvConvenioExhorto.class);
    }

    
    public List<ContratoVO> traerContratosVencidos(int campoId) {
        List<ContratoVO> lista = null;
        String sb = consultaCompania()
                + " where c.ap_campo = " + campoId
                + " and c.estatus = " + Constantes.ESTADO_CONVENIO_ACTIVO
                + " and c.convenio is null "
                //+ " and position('FIN' in c.codigo ) = 0 "
                + " and c.fecha_vencimiento < current_date "
                + " and c.ELIMINADO = false "
                + " order by c.fecha_vencimiento asc";
        List<Object[]> lo = em.createNativeQuery(sb).getResultList();
        if (lo != null) {
            lista = new ArrayList<>();
            for (Object[] obj : lo) {
                lista.add(castContratos(obj));
            }
        }
        return lista;
    }

    
    public List<ContratoVO> traerContratosPorVencer(Date fecha, int dias, int campoId) {
        List<ContratoVO> lista = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        String sb = consultaCompania()
                + " where c.ap_campo = " + campoId
                + " and c.fecha_vencimiento between cast('" + sdf.format(fecha) + "' as date)" + " and current_date + " + dias
                //+ " and position('FIN' in c.codigo ) = 0"
                + " and c.codigo is null "
                + " and c.ELIMINADO = false "
                + " order by c.fecha_vencimiento asc";
        List<Object[]> lo = em.createNativeQuery(sb).getResultList();
        if (lo != null) {
            lista = new ArrayList<>();
            for (Object[] obj : lo) {
                lista.add(castContratos(obj));
            }
        }
        return lista;
    }

    
    public List<ContratoVO> traerContratosConExhortos(int campoId) {
        List<ContratoVO> lista = null;
        String sb = consultaCompania()
                + " where c.ap_campo = " + campoId
                + " and c.estatus in ( " + Constantes.ESTADO_CONVENIO_ACTIVO  + ", " + Constantes.ESTADO_CONVENIO_VENCIDO + ")"
                + " and c.convenio is null "
                + " and c.ELIMINADO = false "
                + " group  by c.id, c.CODIGO, cr.codigo, cr.nombre, c.NOMBRE, p.NOMBRE , c.FECHA_FIRMA, c.FECHA_INICIO, c.FECHA_VENCIMIENTO, c.VIGENCIA, \n"
                + " c.MONTO, m.SIGLAS, ct.NOMBRE , cc.NOMBRE ,  e.NOMBRE , c.convenio, \n"
                + " proveedor_id, c.ap_campo, ap.compania\n"
                + " having (select count(cce.id) from cv_convenio_exhorto cce where cce.convenio =  c.id and cce.eliminado = false)  > 0\n"
                + " order  by c.fecha_vencimiento asc";
        List<Object[]> lo = em.createNativeQuery(sb).getResultList();
        if (lo != null) {
            lista = new ArrayList<>();
            for (Object[] obj : lo) {
                lista.add(castContratos(obj));
            }
        }
        return lista;
    }

    
    public List<ContratoVO> traerExhortosPorProveedor(int proveedorId) {
        List<ContratoVO> lista = null;
        String sb = "select c.id, c.CODIGO, c.NOMBRE, p.NOMBRE as proveedor, c.FECHA_FIRMA, c.FECHA_INICIO, c.FECHA_VENCIMIENTO, c.VIGENCIA, \n"
                + "  c.MONTO, m.SIGLAS, ct.NOMBRE as tipo, cc.NOMBRE as clasificacion,  e.NOMBRE as status, c.convenio, \n"
                + "  p.id as proveedor_id, c.ap_campo, ap.compania\n"
                + " from CONVENIO c \n"
                + "	inner join PROVEEDOR p on c.PROVEEDOR = p.id		\n"
                + "	inner join AP_CAMPO ap on ap.id = c.ap_campo\n"
                + "	left join  MONEDA m on c.MONEDA= m.ID\n"
                + "	inner join CV_TIPO ct on c.CV_TIPO = ct.ID\n"
                + "	left join  CV_CLASIFICACION cc on c.CV_CLASIFICACION = cc.id \n"
                + "	inner join ESTATUS e on c.ESTATUS = e.ID    \n"
                + "where c.proveedor = " + proveedorId
                + "and c.estatus  in ( " + Constantes.ESTADO_CONVENIO_ACTIVO  + ", " + Constantes.ESTADO_CONVENIO_VENCIDO + ")"
                + "and c.id in (select cce.convenio  from cv_convenio_exhorto cce where cce.eliminado = false)\n"
                + "and c.ELIMINADO = false \n"
                + "order by c.fecha_vencimiento";
        List<Object[]> lo = em.createNativeQuery(sb).getResultList();
        if (lo != null) {
            lista = new ArrayList<>();
            for (Object[] obj : lo) {
                ContratoVO contratoVO = new ContratoVO();
                contratoVO.setId((Integer) obj[0]);
                contratoVO.setNumero(((String) obj[1]));
                contratoVO.setNombre((String) obj[2]);
                contratoVO.setNombreProveedor((String) obj[3]);
                contratoVO.setFechaFirma((Date) obj[4]);
                contratoVO.setFechaInicio((Date) obj[5]);
                contratoVO.setFechaVencimiento((Date) obj[6]);
                contratoVO.setVigencia((String) obj[7]);
                contratoVO.setMonto(obj[8] != null ? (Double) obj[8] : 0.0);
                contratoVO.setMoneda((String) obj[9]);
                contratoVO.setTipo((String) obj[10]);
                contratoVO.setClasificacion((String) obj[11]);
                contratoVO.setEstado((String) obj[12]);
                contratoVO.setIdContratoRelacionado(obj[13] != null ? (Integer) obj[13] : 0);
                contratoVO.setProveedor(obj[14] != null ? (Integer) obj[14] : 0);
                contratoVO.setIdCampo(obj[15] != null ? (Integer) obj[15] : 0);
                contratoVO.setCompania((String) obj[16]);
                //
                lista.add(contratoVO);
            }
        }
        return lista;
    }

    
    public void eliminar(String sesion, int id) {
        try {
            CvConvenioExhorto cvConvenioExhorto = find(id);
            cvConvenioExhorto.setModifico(new Usuario(sesion));
            cvConvenioExhorto.setFechaModifico(new Date());
            cvConvenioExhorto.setHoraModifico(new Date());
            cvConvenioExhorto.setEliminado(Constantes.ELIMINADO);
            edit(cvConvenioExhorto);
            //
        } catch (Exception ex) {
            Logger.getLogger(CvConvenioExhortoImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    
    public void guardar(UsuarioVO sesion, ContratoVO contratoVo, ExhortoVo exhortoVo) {
        //
        CvConvenioExhorto cvConvenioExhorto = new CvConvenioExhorto();
        cvConvenioExhorto.setConvenio(new Convenio(contratoVo.getId()));
        cvConvenioExhorto.setApCampo(new ApCampo(contratoVo.getIdCampo()));
        cvConvenioExhorto.setCompania(new Compania(contratoVo.getCompania()));
        ExhortoVo exVo = generarCodigo(contratoVo.getId());
        cvConvenioExhorto.setCodigo(exVo.getCodigo());
        cvConvenioExhorto.setNumeroExhorto(exVo.getNumero());
        cvConvenioExhorto.setFechaExhorto(new Date());
        cvConvenioExhorto.setRepresentanteLegal(exhortoVo.getRepresentanteLegal());
        cvConvenioExhorto.setPuestoRepresentante(exhortoVo.getPuestoRepresentante());
        cvConvenioExhorto.setCorreoPara(exhortoVo.getCorreoPara());
        cvConvenioExhorto.setCorreoCopia(exhortoVo.getCorreoCopia());
        //
        cvConvenioExhorto.setGenero(new Usuario(sesion.getId()));
        cvConvenioExhorto.setFechaGenero(new Date());
        cvConvenioExhorto.setHoraGenero(new Date());
        cvConvenioExhorto.setEliminado(Constantes.NO_ELIMINADO);
        create(cvConvenioExhorto);
        //
        if (exVo.getCodigo().endsWith("-0")) {
            // cambiar el status del contrato
            Convenio convenio = convenioRemote.find(contratoVo.getId());
            //convenio.setEstatus(new Estatus(Constantes.ESTADO_CONVENIO_EXHORTO));
            convenio.setModifico(new Usuario(sesion.getId()));
            convenio.setFechaModifico(new Date());
            convenio.setHoraModifico(new Date());
            //
            convenioRemote.edit(convenio);
            if (convenio.getConvenio() != null) {
                //convenio.getConvenio().setEstatus(new Estatus(Constantes.ESTADO_CONVENIO_EXHORTO));
                convenio.getConvenio().setModifico(new Usuario(sesion.getId()));
                convenio.getConvenio().setFechaModifico(new Date());
                convenio.getConvenio().setHoraModifico(new Date());
                //
                convenioRemote.edit(convenio.getConvenio());
            }
        }
        // enviar la notificación 
        Compania empresa = companiaRemote.buscarPorRFC(contratoVo.getCompania());
        //
        try {
            contratoVo.setCampo(sesion.getCampo());
            ProveedorVo proveedorVo = proveedorRemote.traerProveedor(contratoVo.getProveedor(), contratoVo.getCompania());
            contratoVo.setProveedorVo(new ProveedorVo());
            contratoVo.setProveedorVo(proveedorVo);
            exhortoVo.setCodigo(exVo.getCodigo());
            notificacionConvenioLocal.notificacionExhortoFiniquito(exhortoVo.getCorreoPara(), exhortoVo.getCorreoCopia(), "Exhorto de Finiquito de Contrato",
                    contratoVo, exhortoVo, empresa);
        } catch (Exception e) {
            UtilLog4j.log.error(e);
        }
    }

    private ExhortoVo generarCodigo(int contrato) {
        List<ExhortoVo> exhs = traerPorConvenio(contrato);
        String cod;
        ExhortoVo exVo = new ExhortoVo();
        if (exhs == null || exhs.isEmpty()) {
            cod = folioRemote.getFolioSinCampo("EXHORTO", 3) + "-0";
            exVo.setNumero(0);
        } else {
            exVo = exhs.get(0);
            cod = exVo.getCodigo().substring(0, (exVo.getCodigo().length() - 1));
            cod += (exhs.size());
            exVo.setNumero(exhs.size());
        }
        exVo.setCodigo(cod);
        return exVo;
    }

    
    public List<ExhortoVo> traerPorConvenio(int idConvenio) {
        List<ExhortoVo> lista = null;
        String sb = "select ce.id, ce.fecha_exhorto, ce.codigo_exhorto, ce.numero_exhorto, p.nombre, c.codigo, c.nombre,"
                + "  ce.representante_legal, ce.puesto_representante, ce.correo_para, ce.correo_copia  "
                + ", (ce.fecha_exhorto -\n"
                + "  (select cce.fecha_exhorto from cv_convenio_exhorto cce where cce.convenio = " + idConvenio
                + "    and cce.eliminado = false order by cce.id  asc limit  1) \n"
                + "   ) as dias"
                + " from CV_CONVENIO_EXHORTO ce "
                + "	    inner join convenio c on ce.convenio = c.ID"
                + "	    inner join proveedor p on c.proveedor = p.ID"
                + "  where ce.CONVENIO = " + idConvenio
                + "  and ce.ELIMINADO = false "
                + "  order by ce.id  ";
        List<Object[]> lo = em.createNativeQuery(sb).getResultList();
        if (lo != null) {
            lista = new ArrayList<>();
            for (Object[] obj : lo) {
                ExhortoVo exhortoVo = new ExhortoVo();
                exhortoVo.setId((Integer) obj[0]);
                exhortoVo.setFechaExhorto((Date) obj[1]);
                exhortoVo.setCodigo((String) obj[2]);
                exhortoVo.setNumero((Integer) obj[3]);
                exhortoVo.setProveedor((String) obj[4]);
                exhortoVo.setContrato((String) obj[5]);
                exhortoVo.setDescripcionContrato((String) obj[6]);
                exhortoVo.setRepresentanteLegal((String) obj[7]);
                exhortoVo.setPuestoRepresentante((String) obj[8]);
                exhortoVo.setCorreoPara((String) obj[9]);
                exhortoVo.setCorreoCopia((String) obj[10]);
                exhortoVo.setDiasTranscurridos((Integer) obj[11]);

                lista.add(exhortoVo);
            }
        }
        return lista;
    }

    private String consultaCompania() {
        String sb = "select c.id, c.CODIGO, c.NOMBRE, p.NOMBRE as proveedor, c.FECHA_FIRMA, c.FECHA_INICIO, c.FECHA_VENCIMIENTO, c.VIGENCIA, \n"
                + "  c.MONTO, m.SIGLAS, ct.NOMBRE as tipo, cc.NOMBRE as clasificacion,  e.NOMBRE as status, c.convenio, \n"
                + "  p.id as proveedor_id, c.ap_campo, ap.compania \n"
                + "  , (select count(cce.id) from cv_convenio_exhorto cce where cce.convenio =  c.id and cce.eliminado = false) as numero_exhorto   \n"
                + "  , cr.codigo, cr.nombre "
                + "  , (select count(cc.id) from convenio cc where cc.convenio = c.id and cc.eliminado = false and cc.codigo is not null)"
                + " from CONVENIO c\n"
                + "	inner join PROVEEDOR p on c.PROVEEDOR = p.id\n"
                + "	inner join AP_CAMPO ap on ap.id = c.ap_campo\n"
                + "	left join MONEDA m on c.MONEDA= m.ID\n"
                + "	inner join CV_TIPO ct on c.CV_TIPO = ct.ID\n"
                + "	left join CV_CLASIFICACION cc on c.CV_CLASIFICACION = cc.id \n"
                + "	inner join ESTATUS e on c.ESTATUS = e.ID   \n"
                + "     left join convenio cr on c.convenio = cr.id "
                + "";
        return sb;
    }

    private ContratoVO castContratos(Object[] obj) {
        ContratoVO contratoVO = new ContratoVO();
        contratoVO.setId((Integer) obj[0]);
        contratoVO.setNumero(((String) obj[1]));
        contratoVO.setNombre((String) obj[2]);
        contratoVO.setNombreProveedor((String) obj[3]);
        contratoVO.setFechaFirma((Date) obj[4]);
        contratoVO.setFechaInicio((Date) obj[5]);
        contratoVO.setFechaVencimiento((Date) obj[6]);
        contratoVO.setVigencia((String) obj[7]);
        contratoVO.setMonto(obj[8] != null ? (Double) obj[8] : 0.0);
        contratoVO.setMoneda((String) obj[9]);
        contratoVO.setTipo((String) obj[10]);
        contratoVO.setClasificacion((String) obj[11]);
        contratoVO.setEstado((String) obj[12]);
        contratoVO.setIdContratoRelacionado(obj[13] != null ? (Integer) obj[13] : 0);
        contratoVO.setProveedor(obj[14] != null ? (Integer) obj[14] : 0);
        contratoVO.setIdCampo(obj[15] != null ? (Integer) obj[15] : 0);
        contratoVO.setCompania((String) obj[16]);
        contratoVO.setExhortosEnviados((Long) obj[17]);
        contratoVO.setCodigoContratoRelacionado((String) obj[18]);
        contratoVO.setNombreContratoRelacionado((String) obj[19]);
        contratoVO.setNumeroContratosRelacionados((Long) obj[20]);
        //
        contratoVO.setExhortos(new ArrayList<ExhortoVo>());
        contratoVO.setExhortos(traerPorConvenio(contratoVO.getId()));
        return contratoVO;
    }

    
    public void enviarSolicitudFiniquito(ProveedorVo proveedorSesion, AdjuntoVO buildAdjuntoVO,
            ContratoVO contratoVo) {
        //cambiar el exhorto de estatus
        Convenio c = convenioRemote.find(contratoVo.getId());
        c.setEstatus(new Estatus(Constantes.ESTADO_CONVENIO_PROCESO_FINIQUITO));
        c.setModifico(new Usuario(proveedorSesion.getRfc()));
        c.setFechaModifico(new Date());
        c.setHoraModifico(new Date());
        convenioRemote.edit(c);
        //
        contratoVo.setCompania(c.getApCampo().getCompania().getNombre());
        //Guardar la relacion entre el convenio y las formas        
        List<CvFormas> formas = formasLocal.traerTodo();
        int adjunto, gerenciaId;
        for (CvFormas forma : formas) {
            adjunto = 0;
            gerenciaId = 0;
            if (forma.getCodigo().equals(Constantes.FORMA_SOL_FIN)) {
                adjunto = adjuntoRemote.saveSiAdjunto(buildAdjuntoVO, proveedorSesion.getRfc());
                //
            }
            if (forma.getCodigo().equals(Constantes.FORMA_ACT_ENTREGA)) {
                List<CvConvenioGerencia> gers = convenioGerenciaLocal.gerenciasPorConvenio(contratoVo.getId());
                if (gers != null) {
                    gerenciaId = gers.get(0).getGerencia().getId();
                }
            }
            if (forma.getGerencia() != null) {
                gerenciaId = forma.getGerencia().getId();
            }

            convenioFormasLocal.guardar(proveedorSesion.getRfc(), contratoVo.getId(), forma.getId(),
                    adjunto,
                    gerenciaId,
                    0, adjunto > 0, forma.getCodigo(), c.getApCampo().getId());
        }
        try {
            CvConvenioExhorto cex = buscarUltimoExhorto(c.getId());
            ExhortoVo exhortoVo = new ExhortoVo();
            exhortoVo.setRepresentanteLegal(cex.getRepresentanteLegal());
            exhortoVo.setPuestoRepresentante(cex.getPuestoRepresentante());
            exhortoVo.setCodigo(cex.getCodigo());
            //
            notificacionConvenioLocal.notificacionSolicitudFiniquito(correos(contratoVo.getIdCampo()), "", "Confirmación de Inicio Proceso de Finiquito de Contrato",
                    contratoVo, exhortoVo);
        } catch (Exception e) {
            UtilLog4j.log.error(e);
        }
    }

    private String correos(int campoId) {
        return usuarioRolRemote.traerCorreosPorCodigoRolList(Constantes.COD_CONVENIO, campoId);
    }

    
    public CvConvenioExhorto buscarUltimoExhorto(int convenioId) {
        String c = "select  cce.* from cv_convenio_exhorto cce \n"
                + "	inner join convenio c  on cce.convenio  = c.id \n"
                + " where cce.convenio  =  " + convenioId
                + " and cce.eliminado  = false \n"
                + " order by cce.id desc \n"
                + " limit 1";

        return (CvConvenioExhorto) em.createNativeQuery(c, CvConvenioExhorto.class).getSingleResult();
    }

}
