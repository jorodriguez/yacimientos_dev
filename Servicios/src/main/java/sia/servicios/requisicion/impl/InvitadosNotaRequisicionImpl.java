/*
 * InvitadosNotaRequisicionFacade.java
 * Creada el 20/01/2010, 04:43:37 PM
 * Clase Java desarrollada por: Héctor Acosta Sierra para: MPG-IHSA
 *
 * Para información sobre el uso de esta clase, asi como bugs, actualizaciones o mejoras
 * enviar un mail a: hacosta@mpg-ihsa.com.mx o a: new_nick_name@hotmail.com
 */
package sia.servicios.requisicion.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.LocalBean;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import sia.modelo.InvitadosNotaRequisicion;
import sia.modelo.requisicion.vo.NotaVO;
import sia.modelo.usuario.vo.UsuarioVO;
import sia.util.UtilLog4j;

/**
 *
 * @author Héctor Acosta Sierra
 * @version 1.0
 * @author-mail new_nick_name@hotmail.com @date 20/01/2010
 */
@LocalBean 
public class InvitadosNotaRequisicionImpl {

    @PersistenceContext
    private EntityManager em;

    
    public void create(InvitadosNotaRequisicion invitadosNotaRequisicion) {
        em.persist(invitadosNotaRequisicion);
    }

    
    public void edit(InvitadosNotaRequisicion invitadosNotaRequisicion) {
        em.merge(invitadosNotaRequisicion);
    }

    
    public void remove(InvitadosNotaRequisicion invitadosNotaRequisicion) {
        em.remove(em.merge(invitadosNotaRequisicion));
    }

    
    public InvitadosNotaRequisicion find(Object id) {
        return em.find(InvitadosNotaRequisicion.class, id);
    }

    
    public List<InvitadosNotaRequisicion> findAll() {
        return em.createQuery("select object(o) from InvitadosNotaRequisicion as o").getResultList();
    }

    //apCampo
    
    public List<NotaVO> getNotasPorInvitado(Object idUsuario, Integer idApCampo) {
        
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT  nt.ID,  nt.FECHA, nt.FINALIZADA,  nt.HORA,  nt.IDENTIFICADOR,  nt.MENSAJE, nt.RESPUESTAS,");
        sb.append(" nt.TITULO, nt.ULT_RESPUESTA,  nt.AP_CAMPO,  ir.INVITADO,  r.CONSECUTIVO , u.NOMBRE ");
        sb.append(" FROM NOTA_REQUISICION nt");
        sb.append(" inner join INVITADOS_NOTA_REQUISICION ir ");
        sb.append(" on nt.ID = ir.NOTA_REQUISICION AND ir.INVITADO = '").append(idUsuario).append("'");
        sb.append(" inner join USUARIO u on nt.AUTOR = u.id  ");
        sb.append(" inner join REQUISICION r on nt.REQUISICION = r.id ").append(" and r.AP_CAMPO = ").append(idApCampo);
        sb.append(" where nt.IDENTIFICADOR = 0 ");
        sb.append(" AND nt.FINALIZADA = 'No'");
        try {
            List<NotaVO> le = new ArrayList<NotaVO>();
            List<Object[]> lo = em.createNativeQuery(sb.toString()).getResultList();
            for (Object[] objects : lo) {
                le.add(castNotaVO(objects));
            }
            return le;
        } catch (Exception e) {
            UtilLog4j.log.error(e);
            return null;
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
        notaVo.setInvitado((String) obj[10]); // -->>> se guarda el nombre del usuario que realizo la nota.
        notaVo.setConsecutivo((String) obj[11]);
        notaVo.setAutor((String) obj[12]);
        return notaVo;
    }

    //apCampo
    
    public int getTotalNotasPorInvitado(Object idUsuario, Integer idApCampo) {
        return ((Long) em.createQuery("select count(i.notaRequisicion) "
                + " FROM InvitadosNotaRequisicion i "
                + " WHERE i.notaRequisicion.identificador = 0 AND i.notaRequisicion.finalizada = :finalizada AND i.invitado.id = :usuario AND i.notaRequisicion.apCampo.id = :idApCampo ").setParameter("usuario", idUsuario).setParameter("finalizada", "No").setParameter("idApCampo", idApCampo).getSingleResult()).intValue();
    }

    
    public List<UsuarioVO> getInvitadosPorNota(Object idNota, Object idAutor) {
        String qry = "";
        Query q;
        qry = "Select u.id,u.EMAIL,u.DESTINATARIOS "
                + " From INVITADOS_NOTA_REQUISICION inr,usuario u"
                + " Where inr.NOTA_REQUISICION = " + idNota + " AND inr.INVITADO <> '" + idAutor + "'"
                + " AND inr.INVITADO = u.id ";
        try {
            List<UsuarioVO> le = new ArrayList<UsuarioVO>();
            List<Object[]> lo = em.createNativeQuery(qry).getResultList();
            for (Object[] objects : lo) {
                UsuarioVO invitadoVo = new UsuarioVO();
                invitadoVo.setId((String) objects[0]);
                invitadoVo.setMail((String) objects[1]);
                invitadoVo.setDestinatarios((String) objects[2]);
                le.add(invitadoVo);
            }
            return le;
        } catch (Exception e) {
            UtilLog4j.log.error(e);
            return null;
        }


        //return em.createQuery("SELECT  i FROM InvitadosNotaRequisicion i WHERE i.notaRequisicion.id = :nota AND i.invitado.id <> :autor").setParameter("nota", idNota).setParameter("autor", idAutor).getResultList();
    }
}
