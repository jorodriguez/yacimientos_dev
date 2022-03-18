/*
 * NotaRequisicionFacade.java
 * Creada el 20/01/2010, 04:43:41 PM
 * Clase Java desarrollada por: Héctor Acosta Sierra para: MPG-IHSA
 *
 * Para información sobre el uso de esta clase, asi como bugs, actualizaciones o mejoras
 * enviar un mail a: hacosta@mpg-ihsa.com.mx o a: new_nick_name@hotmail.com
 */
package sia.servicios.requisicion.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import sia.modelo.NotaRequisicion;
import sia.modelo.requisicion.vo.NotaVO;
import sia.modelo.usuario.vo.UsuarioVO;
import sia.util.UtilLog4j;

/**
 *
 * @author Héctor Acosta Sierra
 * @version 1.0
 * @author-mail new_nick_name@hotmail.com @date 20/01/2010
 */
@Stateless 
public class NotaRequisicionImpl {
        
    
    @PersistenceContext
    private EntityManager em;
    //
    @Inject
    private InvitadosNotaRequisicionImpl invitadosNotaRequisicionServicioImpl;
//
    
    public void create(NotaRequisicion notaRequisicion) {        
        em.persist(notaRequisicion);
    }

   
    
    private String getCorreoInvitadosNotaRequisicion(NotaRequisicion notaRequisicion) {
        StringBuilder destinatarios = new StringBuilder();
        //USUARIO VO por que ahi tiene el email y los destinatarios
        List<UsuarioVO> listaInvitados = this.invitadosNotaRequisicionServicioImpl.getInvitadosPorNota(notaRequisicion.getIdentificador(), notaRequisicion.getAutor().getId());
        for (UsuarioVO lista : listaInvitados) {
            if (destinatarios.toString().isEmpty()) {
                destinatarios.append(lista.getMail()); //--Originalmente el atributo se llama EMail
            } else {
                destinatarios.append(",").append(lista.getMail());
            }
        }
        return destinatarios.toString();
    }


    
    public void edit(NotaRequisicion notaRequisicion) {
        em.merge(notaRequisicion);
    }

    
    public void remove(NotaRequisicion notaRequisicion) {
        em.remove(em.merge(notaRequisicion));
    }

    
    public NotaRequisicion find(Object id) {
        return em.find(NotaRequisicion.class, id);
    }

    
    public List<NotaRequisicion> findAll() {
        return em.createQuery("select object(o) from NotaRequisicion as o").getResultList();
    }

    
    public boolean finalizarNotas(Integer idRequisicion) {
        try {
            List<NotaRequisicion> listaNotas = this.getNotasPorRequisicion(idRequisicion);
            for (NotaRequisicion nota : listaNotas) {
                nota.setFinalizada("Si");
                this.edit(nota);
            }
            return true;
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Excepción al Finalizar las notas de la reuquisicion " + e.getMessage());
            return false;
        }
    }
    
    //Ocupado para traer una lista de notas por requisicion en forma JPA, para editarlas
    private List<NotaRequisicion> getNotasPorRequisicion(Integer idRequisicion) {
        return em.createQuery("SELECT n FROM NotaRequisicion n WHERE n.requisicion.id = :requisicion AND n.identificador = :identificador AND n.finalizada = :finalizada").setParameter("requisicion", idRequisicion).setParameter("identificador", 0).setParameter("finalizada", "No").getResultList();
    }
    //-----------

    //ApCampo
    
    public List<NotaVO> getNotasPorInvitado(Object idUsuario, Integer idApCampo) {
        return this.invitadosNotaRequisicionServicioImpl.getNotasPorInvitado(idUsuario, idApCampo);
    }

    ////ApCampo
    
    public int getTotalNotasPorInvitado(Object idUsuario, Integer idApCampo) {
        return this.invitadosNotaRequisicionServicioImpl.getTotalNotasPorInvitado(idUsuario, idApCampo);
    }

    //public List<NotaRequisicion> getNotasPorRequisicion(Object idRequisicion) {
    
    public List<NotaVO> getNotasPorRequisicion(Object idRequisicion) {
        String qry = "";
        /*
         * return em.createQuery("SELECT n FROM NotaRequisicion n WHERE
         * n.requisicion.id = :requisicion AND n.identificador = :identificador
         * AND n.finalizada = :finalizada") .setParameter("requisicion",
         * idRequisicion) .setParameter("identificador", 0)
         * .setParameter("finalizada", "No").getResultList();
         */
        Query q;
        qry = "SELECT  nr.ID," //0
                + " nr.FECHA, " //1
                + " nr.FINALIZADA, " //2
                + " nr.HORA, "//3
                + " nr.IDENTIFICADOR, "//4
                + " nr.MENSAJE, "//5
                + " nr.RESPUESTAS, "//6
                + " nr.TITULO, "//7
                + " nr.ULT_RESPUESTA, "//8                        
                + " u.nombre"//9
                + " FROM NOTA_REQUISICION nr ,usuario u "
                + " WHERE (((REQUISICION = " + idRequisicion + ") AND (IDENTIFICADOR = 0)) AND (FINALIZADA = 'No')) and u.ID = nr.AUTOR ";
        try {
            List<NotaVO> le = new ArrayList<NotaVO>();
            List<Object[]> lo = em.createNativeQuery(qry).getResultList();
            for (Object[] objects : lo) {
                le.add(castNotaVO(objects));
            }
            return le;
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Error al realizar la consulta de Notas por requisición.."+e.getMessage() );
            return null;
           // throw new SIAException(this.toString(), "Error al realizar la consulta de Notas por requisición..", e.getMessage());
        }
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

    
    public List<NotaVO> getNotasParaDetalleRequisicion(Object idRequisicion) {
        UtilLog4j.log.info(this, "#getNotasParaDetalleRequisicion " + em.createQuery("SELECT n FROM NotaRequisicion n WHERE n.requisicion.id = :requisicion AND n.identificador = :identificador").setParameter("requisicion", idRequisicion).setParameter("identificador", 0).toString());
        //return em.createQuery("SELECT n FROM NotaRequisicion n WHERE n.requisicion.id = :requisicion AND n.identificador = :identificador").setParameter("requisicion", idRequisicion).setParameter("identificador", 0).getResultList();        
        String qry = "";
        Query q;
        qry = "SELECT  nr.ID," //0
                + " nr.FECHA, " //1
                + " nr.FINALIZADA, " //2
                + " nr.HORA, "//3
                + " nr.IDENTIFICADOR, "//4
                + " nr.MENSAJE, "//5
                + " nr.RESPUESTAS, "//6
                + " nr.TITULO, "//7
                + " nr.ULT_RESPUESTA, "//8                        
                + " u.nombre"//9
                + " FROM NOTA_REQUISICION nr ,usuario u "
                + " WHERE (((REQUISICION = " + idRequisicion + ") AND (IDENTIFICADOR = 0))) and u.ID = nr.AUTOR ";
        try {
            List<NotaVO> le = new ArrayList<NotaVO>();
            List<Object[]> lo = em.createNativeQuery(qry).getResultList();
            for (Object[] objects : lo) {
                le.add(castNotaVO(objects));
            }
            return le;
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Error al realizar la consulta de Notas a detalle por requisición.."+e.getMessage() );
            return null;
           // throw new SIAException(this.toString(), "Error al realizar la consulta de Notas por requisición..", e.getMessage());
        }
    }

    //ap campo
    
    public List<NotaVO> getRespuestas(Object idNota) {        
        //return em.createQuery("SELECT n FROM NotaRequisicion n WHERE n.identificador > 0 AND n.identificador = :nota").setParameter("nota", idNota).getResultList();
        String qry = "";
        Query q;
        qry = "SELECT  nr.ID," //0
                + " nr.FECHA, " //1
                + " nr.FINALIZADA, " //2
                + " nr.HORA, "//3
                + " nr.IDENTIFICADOR, "//4
                + " nr.MENSAJE, "//5
                + " nr.RESPUESTAS, "//6
                + " nr.TITULO, "//7
                + " nr.ULT_RESPUESTA, "//8                        
                + " u.nombre"//9
                + " FROM NOTA_REQUISICION nr ,usuario u "
                + " WHERE ((IDENTIFICADOR > 0) AND (IDENTIFICADOR = "+idNota+")) and u.ID = nr.AUTOR ";
        try {
            List<NotaVO> le = new ArrayList<NotaVO>();
            List<Object[]> lo = em.createNativeQuery(qry).getResultList();
            for (Object[] objects : lo) {
                le.add(castNotaVO(objects));
            }
            return le;
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Error al realizar la consulta de respuestas de requiscion.."+e.getMessage() );
            return null;
        }
    }
    
    
    public NotaRequisicion save (NotaRequisicion notaOrden){
        em.persist(notaOrden);
        return notaOrden;
    }

}
