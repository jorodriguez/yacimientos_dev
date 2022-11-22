/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.convenio.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.constantes.Constantes;
import sia.modelo.Convenio;
import sia.modelo.CvConvenioFormas;
import sia.modelo.CvFormas;
import sia.modelo.Gerencia;
import sia.modelo.SiAdjunto;
import sia.modelo.SiRol;
import sia.modelo.Usuario;
import sia.modelo.contrato.vo.ContratoFormasNotificacionesVo;
import sia.modelo.contrato.vo.ContratoFormasVo;
import sia.modelo.contrato.vo.ContratoVO;
import sia.modelo.sgl.vo.AdjuntoVO;
import sia.modelo.sistema.AbstractFacade;
import sia.modelo.usuario.vo.UsuarioResponsableGerenciaVo;
import sia.modelo.usuario.vo.UsuarioVO;
import sia.notificaciones.convenio.impl.NotificacionConvenioImpl;
import sia.servicios.campo.nuevo.impl.ApCampoGerenciaImpl;
import sia.servicios.sistema.impl.SiAdjuntoImpl;
import sia.servicios.sistema.impl.SiUsuarioRolImpl;
import sia.util.UtilLog4j;

/**
 *
 * @author mluis
 */
@Stateless 
public class CvConvenioFormasImpl extends AbstractFacade<CvConvenioFormas> {

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public CvConvenioFormasImpl() {
        super(CvConvenioFormas.class);
    }

    @Inject
    SiAdjuntoImpl adjuntoRemote;
    @Inject
    SiUsuarioRolImpl usuarioRolRemote;
    @Inject
    ConvenioImpl convenioRemote;
    @Inject
    NotificacionConvenioImpl notificacionConvenioLocal;
    @Inject
    ApCampoGerenciaImpl campoGerenciaRemote;
    @Inject
    CvConvenioFormasNotificacionesImpl  convenioFormasNotificacionesLocal;

    
    public void guardar(String sesion, int contratoId, int formaId, int adjuntoId,
            int gerenciaId, int rolId, boolean validado, String codigo, int campoId) {
        CvConvenioFormas cf = new CvConvenioFormas();
        try {
            cf.setConvenio(new Convenio(contratoId));
            cf.setCvFormas(new CvFormas(formaId));
            cf.setSiAdjunto(adjuntoId > 0 ? new SiAdjunto(adjuntoId) : null);
            cf.setSiRol(rolId > 0 ? new SiRol(rolId) : null);
            cf.setGerencia(gerenciaId > 0 ? new Gerencia(gerenciaId) : null);
            cf.setValidado(validado);
            if (validado) {
                cf.setFechaValido(new Date());
                cf.setHoraValido(new Date());
                cf.setUsuarioValido(new Usuario(sesion));
            }
            //
            cf.setGenero(new Usuario(sesion));
            cf.setFechaGenero(new Date());
            cf.setHoraGenero(new Date());
            cf.setEliminado(Constantes.NO_ELIMINADO);
            //
            create(cf);

            if (codigo.equals(Constantes.FORMA_SOL_FIN)) {
                String usuarios = usuarioRolRemote.traerUsuarioPorCodigoRolList(Constantes.COD_CONVENIO, campoId);
                convenioFormasNotificacionesLocal.guardar(sesion, usuarios, cf.getId());
            }
        } catch (Exception e) {
            UtilLog4j.log.error(e);
        }
    }

    
    public List<ContratoFormasVo> traerFormasPorConvenio(int idConvenio) {
        String c = consulta()
                + " where cf.convenio = " + idConvenio
                + " and cf.eliminado = false "
                + " order by f.codigo asc ";
        List<Object[]> objs = em.createNativeQuery(c).getResultList();
        List<ContratoFormasVo> formas = new ArrayList<>();
        for (Object[] obj : objs) {
            formas.add(cast(obj));
        }
        return formas;
    }

    private ContratoFormasVo cast(Object[] objects) {
        ContratoFormasVo cfVo = new ContratoFormasVo();
        cfVo.setId((Integer) objects[0]);
        cfVo.setIdConvenio((Integer) objects[1]);
        cfVo.setCodigoConvenio((String) objects[2]);
        cfVo.setConvenio((String) objects[3]);
        cfVo.setIdForma((Integer) objects[4]);
        cfVo.setForma((String) objects[5]);
        cfVo.setIdGerencia(objects[6] != null ? (Integer) objects[6] : 0);
        cfVo.setGerencia((String) objects[7]);
        cfVo.setIdRol(objects[8] != null ? (Integer) objects[8] : 0);
        cfVo.setRol((String) objects[9]);
        cfVo.setIdUsuarioValido((String) objects[10]);
        cfVo.setUsuarioValido((String) objects[11]);
        cfVo.setValidado((Boolean) objects[12]);
        cfVo.setFechaValido((Date) objects[13]);
        cfVo.setHoraValido((Date) objects[14]);
        cfVo.setIdAdjuntoPlantilla(objects[15] != null ? (Integer) objects[15] : 0);
        cfVo.setAdjuntoPlantilla((String) objects[16]);
        cfVo.setUuIdPlantilla((String) objects[17]);
        cfVo.setIdAdjunto(objects[18] != null ? (Integer) objects[18] : 0);
        cfVo.setAdjunto((String) objects[19]);
        cfVo.setUuIdAdjunto((String) objects[20]);
        cfVo.setIdProveedor((Integer) objects[21]);
        cfVo.setRfcProveedor((String) objects[22]);
        cfVo.setProveedor((String) objects[23]);
        cfVo.setTotalNotas((Long) objects[24]);
        cfVo.setResponsableGerencia((String) objects[25]);
        cfVo.setFechaPrimerNotificacion((String) objects[26]);
        cfVo.setFormaCodigo((String) objects[27]);
        //notificaciones
        cfVo.setNotificaciones(new ArrayList<ContratoFormasNotificacionesVo>());
        cfVo.setNotificaciones(convenioFormasNotificacionesLocal.traerPorForma(cfVo.getId()));
        
        
        return cfVo;
    }

    private String consulta() {
        String c = " select distinct cf.id, cf.convenio, c.codigo, c.nombre, cf.cv_formas, f.nombre,    cf.gerencia, g.nombre, cf.si_rol"
                + " , r.nombre, cf.usuario_valido, uv.nombre   \n"
                + " , cf.validado, cf.fecha_valido, cf.hora_valido      "
                + " , adj.id, adj.nombre, adj.uuid     , adjArc.id, adjArc.nombre, adjArc.uuid \n"
                + " , p.id, p.rfc, p.nombre \n "
                + " , (select count(cfn.id) from cv_convenio_formas_notas cfn where cfn.cv_convenio_formas  = cf.id and cfn.eliminado = false ) \n"
                + " , res.nombre "
                + " , (select to_char(ccfn.fecha_genero, 'dd/MM/yyyy') || '  ' || TO_char( ccfn.hora_genero,'HH24:MI:SS')  from cv_convenio_formas_notificaciones ccfn where ccfn.cv_convenio_formas = cf.id and ccfn.eliminado= false order by ccfn.id desc limit 1)"
                + " , f.codigo "
                + " from cv_convenio_formas cf    \n"
                + " 	inner join convenio c on cf.convenio = c.id     \n"
                + "     inner join proveedor p on c.proveedor = p.id \n"
                + " 	inner join cv_formas f on cf.cv_formas = f.id    \n"
                + " 	inner join si_adjunto adj on f.si_adjunto = adj.id     \n"
                + " 	left  join gerencia g on cf.gerencia = g.id    \n"
                + "  	left join ap_campo_gerencia acg on acg.gerencia = g.id  and acg.ap_campo  = c.ap_campo \n"
                + " 	left join usuario res on acg.responsable  = res.id "
                + " 	left  join si_rol r on cf.si_rol = r.id     \n"
                + " 	left  join usuario uv on cf.usuario_valido = uv.id     \n"
                + " 	left  join si_adjunto adjArc on cf.si_adjunto = adjArc.id";
        return c;
    }

    
    public void eliminarArchivo(String rfc, ContratoFormasVo contratoFormasVo) {
        CvConvenioFormas cf = find(contratoFormasVo.getId());
        // eliminar el archivo
        adjuntoRemote.eliminarArchivo(cf.getSiAdjunto().getId(), rfc);
        cf.setSiAdjunto(null);
        cf.setModifico(new Usuario(rfc));
        cf.setFechaModifico(new Date());
        cf.setHoraModifico(new Date());
        //
        edit(cf);
    }

    
    public void agregarArchivo(String rfc, AdjuntoVO adjuntoVo, ContratoFormasVo contratoFormasVo) {
        CvConvenioFormas cf = find(contratoFormasVo.getId());
        // eliminar el archivo
        int idAdj = adjuntoRemote.saveSiAdjunto(adjuntoVo, rfc);
        cf.setSiAdjunto(new SiAdjunto(idAdj));
        cf.setModifico(new Usuario(rfc));
        cf.setFechaModifico(new Date());
        cf.setHoraModifico(new Date());
        //
        edit(cf);
    }

    
    public List<ContratoFormasVo> traerFormasPorGerenciaSinValidar(int gerenciaId, int statusId) {
        String c = consulta()
                + " where cf.gerencia = " + gerenciaId
                + " and cf.si_adjunto  is not null"
                + " and cf.validado = " + Boolean.FALSE
                + " and c.estatus = " + statusId
                + " and cf.eliminado = false ";
        List<Object[]> objs = em.createNativeQuery(c).getResultList();
        List<ContratoFormasVo> formas = new ArrayList<>();
        for (Object[] obj : objs) {
            formas.add(cast(obj));
        }
        return formas;
    }

    
    public void validarDocumentacion(UsuarioVO usuarioSesion, ContratoFormasVo conFormaVo) {
        CvConvenioFormas cf = find(conFormaVo.getId());
        cf.setValidado(Constantes.BOOLEAN_TRUE);
        cf.setUsuarioValido(new Usuario(usuarioSesion.getId()));
        cf.setFechaValido(new Date());
        cf.setHoraValido(new Date());
        //
        cf.setModifico(new Usuario(usuarioSesion.getId()));
        cf.setFechaModifico(new Date());
        cf.setHoraModifico(new Date());
        edit(cf);
        //Notificar la validación
        ContratoVO coVo = convenioRemote.traerConveniosPorCodigo(conFormaVo.getCodigoConvenio());
        //
        notificacionConvenioLocal.notificacionValidacionFormaFiniquito(correoPorRol(Constantes.COD_CONVENIO, coVo.getIdCampo()),
                "Validación de forma en proceso de finiquito", coVo, conFormaVo);
    }

    private String correoPorRol(String rol, int campoId) {
        return usuarioRolRemote.traerCorreosPorCodigoRolList(rol, campoId);
    }

    
    public void notificarForma(String rfc, ContratoFormasVo conFormaVo) {
        //
        UsuarioResponsableGerenciaVo urgVo = campoGerenciaRemote.buscarResponsablePorGerencia(conFormaVo.getIdGerencia(), conFormaVo.getIdCampo());
        if (urgVo != null) {
            convenioFormasNotificacionesLocal.guardar(rfc, urgVo.getNombreUsuario(), conFormaVo.getId());
            //
            notificacionConvenioLocal.notificacionValidaForma(urgVo.getEmailUsuario(),
                    "Proceso de finiquito", conFormaVo);
        }
    }

    
    public long totalFormasSinValidar(int proveedorId) {
        String c = "select  count(ccf.id) from cv_convenio_formas ccf \n"
                + "	inner join convenio c on ccf.convenio  = c.id \n"
                + "	inner join proveedor p on c.proveedor  =p.id \n"
                + " where p.id = " + proveedorId
                + " and ccf.validado = false\n"
                + " and c.estatus = " + Constantes.ESTADO_CONVENIO_PROCESO_FINIQUITO;
        return (long) em.createNativeQuery(c).getSingleResult();
    }

}
