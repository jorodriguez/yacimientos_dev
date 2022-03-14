/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.gr.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.LocalBean;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.constantes.Constantes;
import sia.correo.impl.EnviarCorreoImpl;
import sia.modelo.GrArchivo;
import sia.modelo.SiAdjunto;
import sia.modelo.gr.vo.GrArchivoVO;
import sia.modelo.sistema.AbstractFacade;
import sia.modelo.usuario.vo.UsuarioVO;
import sia.servicios.catalogos.impl.UsuarioImpl;
import sia.servicios.sgl.semaforo.impl.SgSemaforoImpl;
import sia.servicios.sistema.impl.SiAdjuntoImpl;
import sia.util.UtilLog4j;

/**
 *
 * @author ihsa
 */
@LocalBean 
public class GrArchivoImpl extends AbstractFacade<GrArchivo>{

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Inject
    SiAdjuntoImpl siAdjuntoRemote;
    @Inject
    SgSemaforoImpl sgSemaforoRemote;
    @Inject
    GrTipoArchivoImpl grTipoArchivoRemote;
    @Inject
    GrMapaImpl grMapaRemote;
    @Inject
    UsuarioImpl usuarioRemote;
    @Inject
    private EnviarCorreoImpl enviarCorreo;

    public GrArchivoImpl() {
        super(GrArchivo.class);
    }

    
    public List<GrArchivoVO> getArchivos(int tipo, boolean todos) {
        List<GrArchivoVO> archivos = null;
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(" SELECT a.ID,a.SI_ADJUNTO,a.SG_SEMAFORO,a.GR_TIPO_ARCHIVO,a.GR_MAPA,a.FECHA_GENERO,a.HORA_GENERO,a.TITULO,a.ELIMINADO, null  ");
            sb.append(" FROM GR_ARCHIVO a");
            if (tipo == 1) {
                sb.append(" INNER JOIN GR_MAPA m on m.id = a.GR_MAPA and m.eliminado = 'False' ");
            }
            sb.append(" where ");
            if (!todos) {
                sb.append(" a.ELIMINADO = 'False' ");
                if (tipo > 0) {
                    sb.append(" and a.GR_TIPO_ARCHIVO = ").append(tipo);
                }
            } else {
                if (tipo > 0) {
                    sb.append(" a.GR_TIPO_ARCHIVO = ").append(tipo);
                }
            }
            sb.append(" ORDER BY a.ID DESC  limit 30 ");
            UtilLog4j.log.info(this, "Q: : : : : : : : : : " + sb.toString());
            List<Object[]> lo = em.createNativeQuery(sb.toString()).getResultList();
            if (lo != null) {
                archivos = new ArrayList<GrArchivoVO>();
                for (Object[] objects : lo) {
                    archivos.add(castArchivo(objects));
                }
            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
            archivos = null;
        }
        return archivos;
    }

    
    public GrArchivoVO getArchivo(int tipo) {
        GrArchivoVO archivo = null;
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(" SELECT a.ID,a.SI_ADJUNTO,a.SG_SEMAFORO,a.GR_TIPO_ARCHIVO,a.GR_MAPA,a.FECHA_GENERO,a.HORA_GENERO,a.TITULO,a.ELIMINADO, null  ");
            sb.append(" FROM GR_ARCHIVO a");
            if (tipo == Constantes.GR_TIPO_ARCHIVO_Mapas) {
                sb.append(" inner join GR_MAPA m on m.id = a.GR_MAPA and m.eliminado = 'False' ");
            }
            sb.append(" where a.ELIMINADO = 'False' ");
            sb.append(" and a.GR_MAPA = ").append(tipo);
            sb.append(" ORDER BY a.ID DESC limit 1 ");
            UtilLog4j.log.info(this, "Q: : : : : : : : : : " + sb.toString());
            Object[] obj = (Object[]) em.createNativeQuery(sb.toString()).getSingleResult();
            if (obj != null) {
                archivo = castArchivo(obj);
            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
            archivo = null;
        }
        return archivo;
    }

    
    public GrArchivoVO getArchivoById(int id) {
        GrArchivoVO archivo = null;
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(" SELECT ID,SI_ADJUNTO,SG_SEMAFORO,GR_TIPO_ARCHIVO,GR_MAPA, FECHA_GENERO, HORA_GENERO, TITULO, ELIMINADO, null  ");
            sb.append(" FROM GR_ARCHIVO ");
            sb.append(" where ");
            sb.append(" ID = ").append(id);
            UtilLog4j.log.info(this, "Q: : : : : : : : : : " + sb.toString());
            Object[] obj = (Object[]) em.createNativeQuery(sb.toString()).getSingleResult();
            if (obj != null) {
                archivo = castArchivo(obj);
            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
            archivo = null;
        }
        return archivo;
    }

    private GrArchivoVO castArchivo(Object[] obj) {
        GrArchivoVO o = new GrArchivoVO();
        o.setId(obj[0] != null ? (Integer) obj[0] : 0);
        o.setSiAdjunto(obj[1] != null ? (Integer) obj[1] : 0);
        if (o.getSiAdjunto() > 0) {
            o.setSiAdjuntoVO(siAdjuntoRemote.buscarArchivo(o.getSiAdjunto()));
        }
        o.setSgSemaforo(obj[2] != null ? (Integer) obj[2] : 0);
        if (o.getSgSemaforo() > 0) {
            o.setSgSemaforoVO(sgSemaforoRemote.traerSemaforo(o.getSgSemaforo()));
        }
        o.setGrTipoArchivo(obj[3] != null ? (Integer) obj[3] : 0);
        if (o.getGrTipoArchivo() > 0) {
            o.setGrTipoArchivoVO(grTipoArchivoRemote.getGrTipoArch(o.getGrTipoArchivo()));
        }
        o.setGrMapa(obj[4] != null ? (Integer) obj[4] : 0);
        if (o.getGrMapa() > 0) {
            o.setGrMapaVO(grMapaRemote.getMapa(o.getGrMapa()));
        }
        o.setFechaGenero((Date) obj[5]);
        o.setHoraGenero((Date) obj[6]);
        o.setTitulo((String) obj[7]);
        o.setActivo(!(Boolean) obj[8]);
        o.setVisible( obj[9] != null ? (Boolean) obj[9] : Constantes.FALSE);
        return o;
    }

    
    public GrArchivo crear(SiAdjunto siAdjunto, int sgSemaforoID, int grTipo, int grMapa, String usuarioID) {
        return this.crear(siAdjunto, sgSemaforoID, grTipo, grMapa, usuarioID, null);
    }

    
    public GrArchivo crear(SiAdjunto siAdjunto, int sgSemaforoID, int grTipo, int grMapa, String usuarioID, String titulo) {
        GrArchivo nuevo = null;
        try {
            nuevo = new GrArchivo();
            nuevo.setSiAdjunto(siAdjunto);
            nuevo.setGrTipoArchivo(grTipoArchivoRemote.find(grTipo));
            nuevo.setGenero(usuarioRemote.find(usuarioID));
            nuevo.setFechaGenero(new Date());
            nuevo.setHoraGenero(new Date());
            nuevo.setEliminado(Constantes.BOOLEAN_FALSE);
            if (titulo != null) {
                nuevo.setTitulo(titulo);
            }

            if (sgSemaforoID > 0) {
                nuevo.setSgSemaforo(sgSemaforoRemote.find(sgSemaforoID));
            }

            if (grMapa > 0) {
                nuevo.setGrMapa(grMapaRemote.find(grMapa));
            }

            this.create(nuevo);

        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
            nuevo = null;
        }
        return nuevo;
    }

    
    public boolean enviarCentops(String mensaje, byte[] logoGR, byte[] logoEsr, File adjunto) {
        boolean enviado = false;
        try {
            StringBuilder msg = new StringBuilder();
            msg.append(this.getInicioPlantilla());
            msg.append(mensaje);
            msg.append(this.getFinPlantilla());
            UsuarioVO usrVO = usuarioRemote.findById("CENTOPS");
            if (usrVO != null && usrVO.getMail() != null && !usrVO.getMail().isEmpty()) {
                enviado = enviarCorreo.enviarCorreoIhsa(
                        usrVO.getMail(), 
                        "", 
                        "", 
                        "Mensaje para CENTOPS", 
                        msg, 
                        logoGR, 
                        logoEsr, 
                        adjunto, 
                        null, 
                        null
                );
            } else {
                UtilLog4j.log.fatal(this, "Error al enviar correo a CENTOPS modulo GR, no se encontro usuario o no tiene registrado un correo electronico # # # # # # ");
            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
        }
        return enviado;
    }

    private String getInicioPlantilla() {
        StringBuilder inicioPlantilla = new StringBuilder();
        inicioPlantilla.append("<html><head><meta charset=\"UTF-8\"> </head><body><table align=\"center\" width=\"95%\" cellspacing=\"0\" cellpadding=\"0\"><tbody><tr>")
                .append("<td colspan=\"3\"><table align=\"center\" width=\"95%\" border=\"0\" style=\"background-color:#fefefe; border:1px solid #A8CEF0; padding:0px 5px 5px 5px; word-spacing:2px\">")
                .append("<tbody><tr><td colspan=\"3\"><table align=\"center\" width=\"95%\" style=\"border:1px solid #A8CEF0\"><tbody><tr><td valign=\"middle\" style=\"background-color:#A8CEF0; border:1px solid #A8CEF0\">")
                .append("<table align=\"center\" style=\"background-color:#A8CEF0\"><tbody><tr><td><font color=\"black\" face=\"font-family: Gill, Helvetica, sans-serif; font-size:11px;\"><center><b>GESTIÓN DE RIESGOS</b>")
                .append("</center></font></td></tr></tbody></table></td></tr></tbody></table></td></tr><tr><td colspan=\"3\">");
        return inicioPlantilla.toString();
    }

    private String getFinPlantilla() {
        StringBuilder finPlantilla = new StringBuilder();
        finPlantilla.append("</td></tr></tbody></table></td></tr><tr><td style=\"width:2.5%\"></td><td style=\"width:95%; text-align:center; padding:3px; background-color:#A8CEF0; color:#004181; font-size:9px\">")
                .append("Notificación generada automáticamente, por el Sistema de Gestión de Riesgos.<br></td><td style=\"width:2.5%\"></td></tr></tbody></table></body></html>");
        return finPlantilla.toString();
    }

    
    public boolean editarArchivo(GrArchivoVO archivo) {
        boolean ret = false;
        if (archivo != null && archivo.getId() > 0) {
            GrArchivo cambios = this.find(archivo.getId());
            if (cambios.getGrTipoArchivo().getId() == 1) {
                if (archivo.getSgSemaforo() > 0) {
                    cambios.setSgSemaforo(sgSemaforoRemote.find(archivo.getSgSemaforo()));
                }
                if (archivo.getGrMapa() > 0) {
                    cambios.setGrMapa(grMapaRemote.find(archivo.getGrMapa()));
                }
            } else if (cambios.getGrTipoArchivo().getId() > 1) {
                if (archivo.getTitulo() != null && !archivo.getTitulo().isEmpty()) {
                    cambios.setTitulo(archivo.getTitulo());
                    if (!cambios.isEliminado() && !archivo.isActivo()) {
                        cambios.setEliminado(Constantes.BOOLEAN_TRUE);
                    } else if (cambios.isEliminado() && archivo.isActivo()) {
                        cambios.setEliminado(Constantes.BOOLEAN_FALSE);
                    }
                }
            }
            this.edit(cambios);
            ret = true;
        }
        return ret;
    }

    
    public List<GrArchivoVO> getAlertas() {
        List<GrArchivoVO> archivos = null;
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(" select ar.ID,ar.SI_ADJUNTO, ")
            .append(" (select a.SG_SEMAFORO ")
            .append(" from SG_ESTADO_SEMAFORO a ")
            .append(" where a.GR_MAPA = ar.GR_MAPA ")
            .append(" and a.SG_SEMAFORO in (3, 4) ")
            .append(" and a.FECHA_FIN is null ")
            .append(" and a.ELIMINADO = 'False' ")
            .append(" order by a.id desc limit 1 ) as SG_SEMAFORO, ")
            .append("ar.GR_TIPO_ARCHIVO,ar.GR_MAPA,ar.FECHA_GENERO, ar.HORA_GENERO, ar.TITULO, ar.ELIMINADO, m.VISIBLE")
            .append(" from GR_ARCHIVO ar ")
            .append(" inner join GR_MAPA m on m.id = ar.GR_MAPA ")
            .append(" where ar.ELIMINADO = 'False' ")
            .append(" and ar.GR_TIPO_ARCHIVO = ").append(Constantes.GR_TIPO_ARCHIVO_Mapas)
            .append(" and ar.ID in (select max(arr.id) from GR_ARCHIVO arr where arr.GR_MAPA = ar.GR_MAPA) ")
            .append(" and ar.GR_MAPA in (select a.GR_MAPA ")
            .append(" 			   from SG_ESTADO_SEMAFORO a ")
            .append(" 			   where a.GR_MAPA is not null ")
            .append(" 			   and a.SG_SEMAFORO in (").append(Constantes.ID_COLOR_SEMAFORO_ROJO)
            .append(", ").append(Constantes.ID_COLOR_SEMAFORO_NEGRO).append(") ")
            .append(" 			   and a.FECHA_FIN is null  ")
            .append(" 			   and a.ELIMINADO = 'False' ")
            .append(" 			   group by a.GR_MAPA) ")
            .append(" order by ar.FECHA_GENERO desc, ar.HORA_GENERO desc, ar.SG_SEMAFORO ");

            UtilLog4j.log.info(this, "Q: : : : : : : : : : " + sb.toString());
            List<Object[]> lo = em.createNativeQuery(sb.toString()).getResultList();
            if (lo != null) {
                archivos = new ArrayList<GrArchivoVO>();
                for (Object[] objects : lo) {
                    archivos.add(castArchivo(objects));
                }
            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
            archivos = null;
        }
        return archivos;
    }
}
