/*
 * NotaOrdenFacade.java
 * Creada el 30/11/2009, 04:55:50 PM
 * Clase Java desarrollada por: Héctor Acosta Sierra para: MPG-IHSA
 *
 * Para información sobre el uso de esta clase, asi como bugs, actualizaciones o mejoras
 * enviar un mail a: hacosta@mpg-ihsa.com.mx o a: new_nick_name@hotmail.com
 */
package sia.servicios.orden.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.LocalBean;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import sia.modelo.NotaOrden;
import sia.modelo.requisicion.vo.NotaVO;
import sia.modelo.usuario.vo.UsuarioVO;
import sia.util.UtilLog4j;

/**
 *
 * @author Héctor Acosta Sierra
 * @version 1.0
 * @author-mail new_nick_name@hotmail.com @date 30/11/2009
 */
@LocalBean 
public class NotaOrdenImpl{

    @Inject
    private InvitadosNotaOrdenImpl invitadosNotaOrdenServicioImpl;
    @Inject
    private InvitadosNotaOrdenImpl invitadosNotaOrdenRemote;
    //
    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    public void create(NotaOrden notaOrden) {
        //notaOrden.setId(this.folioServicioImpl.getFolio("NotaOrden"));
        em.persist(notaOrden);
    }

    
    public NotaOrden save(NotaOrden notaOrden) {
        //notaOrden.setId(this.folioServicioImpl.getFolio("NotaOrden"));
        em.persist(notaOrden);
        return notaOrden;
    }

    
    public void crearRespuesta(NotaOrden notaOrden) {
//        //notaOrden.setId(this.folioServicioImpl.getFolio("NotaOrden"));
//        notaOrden.setFecha(new Date());
//        notaOrden.setHora(new Date());
//        notaOrden.setRespuestas(0);
//        notaOrden.setFinalizada("No");
//        List<ContactosOrden> listaContactosOrden = this.ordenServicioImpl.getContactos(notaOrden.getOrden().getId());
//        boolean enviado = false;
//        if (notaOrden.getAutor().equals("PRUEBA")) {
//            enviado = this.notificacionOrdenRemote.enviarNotificacionNotaOrden(
//                    getCorreoInvitados(notaOrden),
//                    "",
//                    "",
//                    notaOrden.getOrden(),
//                    notaOrden.getTitulo(),
//                    notaOrden,
//                    listaContactosOrden);
//        } else {
//            UsuarioVO uvo = usuarioRemote.traerResponsableGerencia(notaOrden.getOrden().getApCampo().getId(), Constantes.GERENCIA_ID_COMPRAS, notaOrden.getOrden().getCompania().getRfc());
//            enviado = this.notificacionOrdenRemote.enviarNotificacionNotaOrden(
//                    getCorreoInvitados(notaOrden),
//                    "",
//                    uvo.getMail(),
//                    notaOrden.getOrden(),
//                    notaOrden.getTitulo(),
//                    notaOrden,                    
//                    listaContactosOrden);
//        }
//        if (enviado) {
//            em.persist(notaOrden);
//            //-- edit los campos Respuestas y ultRespuesta de la Nota principal
//            notaOrden = this.find(notaOrden.getIdentificador());
//            notaOrden.setRespuestas(notaOrden.getRespuestas() + 1);
//            notaOrden.setUltRespuesta(new Date());
//            this.edit(notaOrden);
//        }
    }

    private String getCorreoInvitados(NotaOrden notaOrden) {
        StringBuilder destinatarios = new StringBuilder();
        List<UsuarioVO> listaInvitados = this.invitadosNotaOrdenRemote.getInvitadosPorNota(notaOrden.getIdentificador(), notaOrden.getAutor().getId());
        for (UsuarioVO lista : listaInvitados) {
            if (destinatarios.toString().isEmpty()) {
                destinatarios.append(lista.getMail());
            } else {
                destinatarios.append(",").append(lista.getMail());
            }
        }
        return destinatarios.toString();
    }

    
    public void edit(NotaOrden notaOrden) {
        em.merge(notaOrden);
    }

    
    public void remove(NotaOrden notaOrden) {
        em.remove(em.merge(notaOrden));
    }

    
    public NotaOrden find(Object id) {
        return em.find(NotaOrden.class, id);
    }

    
    public List<NotaOrden> findAll() {
        return em.createQuery("select object(o) from NotaOrden as o").getResultList();
    }

    
    public List<NotaVO> getNotasPorInvitado(Object idUsuario, Integer idApCampo) {
        return this.invitadosNotaOrdenServicioImpl.getNotasPorInvitado(idUsuario, idApCampo);
    }

    
    public int getTotalNotasPorInvitado(Object idUsuario, Integer idApCampo) {
        return this.invitadosNotaOrdenServicioImpl.getTotalNotasPorInvitado(idUsuario, idApCampo);
    }

    
    public List<NotaVO> getNotasPorOrden(Object idOrden) {
        String qry = "";
        Query q;
        qry = "SELECT nor.ID,"//0
                + "nor.FECHA,"//1
                + "nor.FINALIZADA,"//2
                + "nor.HORA,"//3
                + "nor.IDENTIFICADOR,"//4
                + "nor.MENSAJE,"//5
                + "nor.RESPUESTAS,"//6
                + "nor.TITULO,"//7
                + "nor.ULT_RESPUESTA,"//8                                
                + "u.nombre"//9                
                + " FROM NOTA_ORDEN nor, Usuario u "
                + " WHERE (((nor.ORDEN = " + idOrden + ") AND (nor.IDENTIFICADOR = 0)) AND (nor.FINALIZADA = 'No')) "
                + " AND u.id = nor.AUTOR";
        try {
            List<NotaVO> le = new ArrayList<NotaVO>();
            List<Object[]> lo = em.createNativeQuery(qry).getResultList();
            for (Object[] objects : lo) {
                le.add(castNotaVO(objects));
            }
            return le;
        } catch (Exception e) {
            UtilLog4j.log.error(e);
            return null;
            // throw new SIAException(this.toString(), "Error al realizar la consulta de Notas por requisición..", e.getMessage());
        }


        //return em.createQuery("SELECT n FROM NotaOrden n WHERE n.orden.id = :orden AND n.identificador = :identificador AND n.finalizada = :finalizada").setParameter("orden", idOrden).setParameter("identificador", 0).setParameter("finalizada", "No").getResultList();
    }

    private NotaVO castNotaVO(Object[] obj) {
        NotaVO notaVo = new NotaVO();
        notaVo.setId((Integer) obj[0]);
        notaVo.setFechaGenero((Date) obj[1]);
        notaVo.setFinalizada((String) obj[2]);
        notaVo.setHoraGenero((Date) obj[3]);
        notaVo.setIdentificador((Integer) obj[4]);
        notaVo.setMensaje((String) obj[5]);
        notaVo.setRespuestas((Integer) obj[6]);
        notaVo.setTitulo((String) obj[7]);
        notaVo.setUltRespuesta((Date) obj[8]);
        notaVo.setAutor((String) obj[9]); // -->>> se guarda el nombre del usuario que realizo la nota.
        return notaVo;
    }

    
    public List<NotaVO> getNotasParaDetalleOrden(Object idOrden) {
        String qry = "";
        Query q;
        qry = "SELECT nor.ID,"//0
                + "nor.FECHA,"//1
                + "nor.FINALIZADA,"//2
                + "nor.HORA,"//3
                + "nor.IDENTIFICADOR,"//4
                + "nor.MENSAJE,"//5
                + "nor.RESPUESTAS,"//6
                + "nor.TITULO,"//7
                + "nor.ULT_RESPUESTA,"//8                                
                + "u.nombre"//9                
                + " FROM NOTA_ORDEN nor,Usuario u "
                + " WHERE ((nor.ORDEN = " + idOrden + ") AND (nor.IDENTIFICADOR = 0)) AND U.id = nor.AUTOR ";
        try {
            List<NotaVO> le = new ArrayList<NotaVO>();
            List<Object[]> lo = em.createNativeQuery(qry).getResultList();
            for (Object[] objects : lo) {
                le.add(castNotaVO(objects));
            }
            return le;
        } catch (Exception e) {
            UtilLog4j.log.error(e);
            return null;
        }
    }

    
    public List<NotaVO> getRespuestas(Object idNota) {
        String qry = "";
        Query q;
        qry = "SELECT nor.ID,"//0
                + "nor.FECHA,"//1
                + "nor.FINALIZADA,"//2
                + "nor.HORA,"//3
                + "nor.IDENTIFICADOR,"//4
                + "nor.MENSAJE,"//5
                + "nor.RESPUESTAS,"//6
                + "nor.TITULO,"//7
                + "nor.ULT_RESPUESTA,"//8                                
                + "u.nombre"//9                
                + " FROM NOTA_ORDEN nor,Usuario u"
                + " WHERE ((IDENTIFICADOR > 0) AND (IDENTIFICADOR = " + idNota + ")) AND nor.AUTOR = U.ID";
        try {
            List<NotaVO> le = new ArrayList<NotaVO>();
            List<Object[]> lo = em.createNativeQuery(qry).getResultList();
            for (Object[] objects : lo) {
                le.add(castNotaVO(objects));
            }
            return le;
        } catch (Exception e) {
            UtilLog4j.log.error(e);
            return null;
        }
        //return em.createQuery("SELECT n FROM NotaOrden n WHERE n.identificador > 0 AND n.identificador = :nota").setParameter("nota", idNota).getResultList();
    }
}
