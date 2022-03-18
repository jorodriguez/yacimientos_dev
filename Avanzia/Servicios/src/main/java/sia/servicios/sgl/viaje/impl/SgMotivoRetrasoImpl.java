/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.sgl.viaje.impl;

import java.util.Date;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.constantes.Constantes;
import sia.excepciones.SIAException;
import sia.modelo.SgInvitado;
import sia.modelo.SgLugar;
import sia.modelo.SgMotivoRetraso;
import sia.modelo.Usuario;
import sia.modelo.sgl.viaje.vo.MotivoRetrasoVO;
import sia.modelo.sistema.AbstractFacade;
import sia.servicios.catalogos.impl.UsuarioImpl;
import sia.servicios.sgl.impl.SgInvitadoImpl;
import sia.servicios.sgl.impl.SgTipoEspecificoImpl;
import sia.util.UtilLog4j;

/**
 *
 * @author b75ckd35th
 */
@Stateless 
public class SgMotivoRetrasoImpl extends AbstractFacade<SgMotivoRetraso>{

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;
    @Inject
    private UsuarioImpl usuarioService;
    @Inject
    private SgTipoEspecificoImpl sgTipoEspecificoRemote;
    @Inject
    private SgInvitadoImpl sgInvitadoRemote;
    @Inject
    private SgLugarImpl sgLugarRemote;
    
    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SgMotivoRetrasoImpl() {
        super(SgMotivoRetraso.class);
    }

    /**
     * Fecha : 15/oct/2013 Modifico: Joel Rodriguez Rojas Descripción :
     * Anteriormente entraba un paramatro de objeto JPA, ahora solo entran tipos
     * nativos
     *
     * @param idSgInvitado
     * @param idSgLugar
     * @param idUsuario
     * @param empleado
     */
    
    public SgMotivoRetraso save(String justificacion, Date horaReunion, int idSgInvitado, int idSgLugar, String idUsuario, int idTipoEspecifico,String empleado) {
        UtilLog4j.log.info(this, "SgMotivoRetrasoImpl.save()");

        SgMotivoRetraso sgMotivoRetraso = new SgMotivoRetraso();
        sgMotivoRetraso.setHoraReunion(horaReunion);
        sgMotivoRetraso.setJustificacionRetraso(justificacion);
        if(empleado != null && !empleado.equals("")){
            sgMotivoRetraso.setUsuario(usuarioService.find(empleado));
        } else {
            sgMotivoRetraso.setSgInvitado(this.sgInvitadoRemote.find(idSgInvitado));
        }               
        sgMotivoRetraso.setSgLugar(this.sgLugarRemote.find(idSgLugar));
        sgMotivoRetraso.setGenero(new Usuario(idUsuario));
        sgMotivoRetraso.setFechaGenero(new Date());
        sgMotivoRetraso.setHoraGenero(new Date());
        sgMotivoRetraso.setEliminado(Constantes.NO_ELIMINADO);
        sgMotivoRetraso.setSgTipoEspecifico(sgTipoEspecificoRemote.find(idTipoEspecifico));
        create(sgMotivoRetraso);

        UtilLog4j.log.info(this, "SgMotivoRetraso CREATED SUCCESSFULLY");
        return sgMotivoRetraso;
    }

    
    public void save(String justificacion, int idSgSolicitudViaje, String idUsuario) {
        UtilLog4j.log.info(this, "SgMotivoRetrasoImpl.save()");

        SgMotivoRetraso sgMotivoRetraso = new SgMotivoRetraso();
        sgMotivoRetraso.setJustificacionRetraso(justificacion);
//        sgMotivoRetraso.setSgSolicitudViaje(this.sgSolicitudViajeRemote.find(idSgSolicitudViaje));
        sgMotivoRetraso.setGenero(new Usuario(idUsuario));
        sgMotivoRetraso.setFechaGenero(new Date());
        sgMotivoRetraso.setHoraGenero(new Date());
        sgMotivoRetraso.setEliminado(Constantes.NO_ELIMINADO);

        create(sgMotivoRetraso);
        UtilLog4j.log.info(this, "SgMotivoRetraso CREATED SUCCESSFULLY");
    }

    
    public void update(int idSgMotivoRetraso, int idSgInvitado, int idSgLugar, String justificacion, String idUsuario) {
        UtilLog4j.log.info(this, "SgMotivoRetrasoImpl.update()");

        SgMotivoRetraso sgMotivoRetraso = find(idSgMotivoRetraso);
        sgMotivoRetraso.setJustificacionRetraso(justificacion);
        sgMotivoRetraso.setSgInvitado(this.sgInvitadoRemote.find(idSgInvitado));
        sgMotivoRetraso.setSgLugar(this.sgLugarRemote.find(idSgLugar));
        sgMotivoRetraso.setModifico(new Usuario(idUsuario));
        sgMotivoRetraso.setFechaModifico(new Date());
        sgMotivoRetraso.setHoraModifico(new Date());

        edit(sgMotivoRetraso);
        UtilLog4j.log.info(this, "SgMotivoRetraso UPDATED SUCCESSFULLY");
    }

    
    public void update(int idSgMotivoRetraso, String justificacion, String idUsuario) {
        UtilLog4j.log.info(this, "SgMotivoRetrasoImpl.update()");

        SgMotivoRetraso sgMotivoRetraso = find(idSgMotivoRetraso);
        sgMotivoRetraso.setJustificacionRetraso(justificacion);
        sgMotivoRetraso.setModifico(new Usuario(idUsuario));
        sgMotivoRetraso.setFechaModifico(new Date());
        sgMotivoRetraso.setHoraModifico(new Date());

        edit(sgMotivoRetraso);
        UtilLog4j.log.info(this, "SgMotivoRetraso UPDATED SUCCESSFULLY");

    }

    
    public void delete(int idSgMotivoRetraso, String idUsuario) {
        UtilLog4j.log.info(this, "SgMotivoRetrasoImpl.delete()");

        SgMotivoRetraso sgMotivoRetraso = find(idSgMotivoRetraso);
        sgMotivoRetraso.setModifico(new Usuario(idUsuario));
        sgMotivoRetraso.setFechaModifico(new Date());
        sgMotivoRetraso.setHoraModifico(new Date());
        sgMotivoRetraso.setEliminado(Constantes.ELIMINADO);

        edit(sgMotivoRetraso);
        UtilLog4j.log.info(this, "SgMotivoRetraso DELETED SUCCESSFULLY");
    }

    
    public SgMotivoRetraso save(SgMotivoRetraso motivoRetraso, SgLugar sgLugar, SgInvitado sgInvitado, String idUsuario) throws SIAException, Exception {
        UtilLog4j.log.info(this, "SgMotivoRetrasoImpl.save()");

        motivoRetraso.setSgLugar(sgLugar);
        motivoRetraso.setSgInvitado(sgInvitado);
        motivoRetraso.setGenero(new Usuario(idUsuario));
        motivoRetraso.setFechaGenero(new Date());
        motivoRetraso.setHoraGenero(new Date());
        motivoRetraso.setEliminado(Constantes.NO_ELIMINADO);

        super.create(motivoRetraso);
        UtilLog4j.log.info(this, "SgMotivoRetraso CREATED SUCCESSFULLY");

        return motivoRetraso;
    }

    
    public MotivoRetrasoVO findById(Integer idMotivoRetraso, int tipoViaje) {
        UtilLog4j.log.info(this, "findBySolicitud");
        clearQuery();
        SgMotivoRetraso mr = find(idMotivoRetraso);

        try {
            boolean tipo = true;
            if (tipoViaje == Constantes.TIPO_ESPECIFICO_SOLICITUD_TERRESTRE) {
                if (mr != null) {
                    if (mr.getSgInvitado() != null) {
                        appendQuery("select mr.id,");
                        appendQuery(" mr.hora_reunion,");
                        appendQuery(" mr.justificacion_retraso, ");
                        //validar si es usuario o invitado
                        appendQuery(" inv.id,");//este o otro
                        appendQuery(" inv.nombre,");//este o otro

                        appendQuery(" l.id, l.nombre, ");
                        appendQuery(" case when inv.email is null then '' when inv.email is not null then inv.email end,");
                        appendQuery(" em.NOMBRE");//este o otro
                        appendQuery(" from sg_motivo_retraso mr, ");
                        appendQuery(" sg_invitado inv,");
                        appendQuery(" sg_lugar l,");
                        appendQuery(" SG_EMPRESA em");//este o otro

                        appendQuery(" where mr.sg_invitado = inv.id and mr.sg_lugar = l.id");//este o otro
                        appendQuery(" and inv.SG_EMPRESA = em.ID");
                        appendQuery(" and mr.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");
                        appendQuery(" and mr.id = ").append(idMotivoRetraso);

                    } else {
                        tipo = false;
                        appendQuery("select mr.id,");
                        appendQuery(" mr.hora_reunion,");
                        appendQuery(" mr.justificacion_retraso, ");
                        appendQuery(" u.id,");
                        appendQuery(" u.nombre,");
                        appendQuery(" l.id, l.nombre, ");
                        appendQuery(" case when u.email is null then '' when u.email is not null then u.email end,");
                        appendQuery(" 'IHSA'");//este o otro
                        appendQuery(" from sg_motivo_retraso mr, ");
                        appendQuery(" USUARIO U,");
                        appendQuery(" sg_lugar l");
                        appendQuery(" where mr.USUARIO=u.ID and mr.SG_LUGAR= l.ID");
                        appendQuery(" and mr.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");
                        appendQuery(" and mr.id = ").append(idMotivoRetraso);
                    }
                }
            } else {
                // es justificacion aerea
                appendQuery(" select mr.id,");
                appendQuery(" mr.justificacion_retraso ");
                appendQuery(" from sg_motivo_retraso mr");
                appendQuery(" where  mr.id =  ");
                appendQuery(idMotivoRetraso);
                appendQuery(" and mr.eliminado = 'False'");
            }

            Object[] obj = (Object[]) em.createNativeQuery(query.toString()).getSingleResult();
            return castMotivoRetrasoVO(obj, tipoViaje, tipo);
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Exception " + e.getMessage());
            return null;
        }
    }

    private MotivoRetrasoVO castMotivoRetrasoVO(Object[] obj, int tipoViaje, boolean tipo) {
        MotivoRetrasoVO motivoRetrasoVO = new MotivoRetrasoVO();
        if (tipoViaje == Constantes.TIPO_ESPECIFICO_SOLICITUD_TERRESTRE) {            
            motivoRetrasoVO.setIdMotivoRetraso((Integer) obj[0]);
            motivoRetrasoVO.setHoraReunion((Date) obj[1]);
            motivoRetrasoVO.setJustificacion((String) obj[2]);
            if(tipo){
                motivoRetrasoVO.setIdInvitado((Integer) obj[3]);
            motivoRetrasoVO.setInvitado((String) obj[4]);
            } else {
                motivoRetrasoVO.setIdUsuario((String) obj[3]);
                motivoRetrasoVO.setUsuario((String) obj[4]);
            }            
            motivoRetrasoVO.setIdLugar((Integer) obj[5]);
            motivoRetrasoVO.setLugar((String) obj[6]);
            motivoRetrasoVO.setMail((String) obj[7]);
            motivoRetrasoVO.setNombreEmpresa((String) obj[8]);            
            return motivoRetrasoVO;
        }else{
            motivoRetrasoVO.setIdMotivoRetraso((Integer) obj[0]);
            motivoRetrasoVO.setJustificacion((String) obj[2]);
        }
        return motivoRetrasoVO;
    }    
}