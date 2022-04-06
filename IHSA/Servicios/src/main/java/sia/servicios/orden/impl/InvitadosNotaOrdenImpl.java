/*
 * InvitadosNotaOrdenFacade.java
 * Creada el 30/11/2009, 04:55:49 PM
 * Clase Java desarrollada por: Héctor Acosta Sierra para: MPG-IHSA
 *
 * Para información sobre el uso de esta clase, asi como bugs, actualizaciones o mejoras
 * enviar un mail a: hacosta@mpg-ihsa.com.mx o a: new_nick_name@hotmail.com
 */
package sia.servicios.orden.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import sia.modelo.InvitadosNotaOrden;
import sia.modelo.requisicion.vo.NotaVO;
import sia.modelo.usuario.vo.UsuarioVO;
import sia.util.UtilLog4j;

/**
 *
 * @author Héctor Acosta Sierra
 * @version 1.0
 * @author-mail new_nick_name@hotmail.com @date 30/11/2009
 */
@Stateless 
public class InvitadosNotaOrdenImpl {

    @PersistenceContext
    private EntityManager em;

    
    public void create(InvitadosNotaOrden invitadosNotaOrden) {
        em.persist(invitadosNotaOrden);
    }

    
    public void edit(InvitadosNotaOrden invitadosNotaOrden) {
        em.merge(invitadosNotaOrden);
    }

    
    public void remove(InvitadosNotaOrden invitadosNotaOrden) {
        em.remove(em.merge(invitadosNotaOrden));
    }

    
    public InvitadosNotaOrden find(Object id) {
        return em.find(InvitadosNotaOrden.class, id);
    }

    
    public List<InvitadosNotaOrden> findAll() {
        return em.createQuery("select object(o) from InvitadosNotaOrden as o").getResultList();
    }

    
    public List<NotaVO> getNotasPorInvitado(Object idUsuario, Integer idApCampo) {

        String qry = "";
        Query q;
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT nor.ID, nor.FECHA, nor.FINALIZADA, nor.HORA, nor.IDENTIFICADOR,nor.MENSAJE,nor.RESPUESTAS,  nor.TITULO, ");
        sb.append(" nor.ULT_RESPUESTA,u.nombre, o.CONSECUTIVO,");
        sb.append(" nor.AUTOR,  u.NOMBRE");
        sb.append(" FROM NOTA_ORDEN nor");
        sb.append(" inner join  INVITADOS_NOTA_ORDEN ino on nor.id = ino.NOTA_ORDEN AND ino.INVITADO = '").append(idUsuario).append("'");
        sb.append(" inner join USUARIO u on nor.AUTOR = u.id");
        sb.append(" inner join ORDEN o on nor.ORDEN = o.id");
        sb.append(" WHERE  nor.IDENTIFICADOR = 0 ");
        sb.append(" AND nor.FINALIZADA = 'No'");
        sb.append(" AND nor.AP_CAMPO = ").append(idApCampo);
        
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
        notaVo.setInvitado((String) obj[9]); // -->>> se guarda el nombre del usuario que realizo la nota.
        notaVo.setConsecutivo((String) obj[10]);
        notaVo.setIdAutor((String) obj[11]);
        notaVo.setAutor((String) obj[12]);
        return notaVo;
    }

    
    public int getTotalNotasPorInvitado(Object idUsuario, Integer idApCampo) {
        return ((Long) em.createQuery("select count(i.notaOrden) FROM InvitadosNotaOrden i WHERE i.notaOrden.identificador = 0 AND i.notaOrden.finalizada = :finalizada AND i.invitado.id = :usuario AND i.notaOrden.apCampo.id = :idApCampo ").setParameter("usuario", idUsuario).setParameter("finalizada", "No").setParameter("idApCampo", idApCampo).getSingleResult()).intValue();
    }

    
    public List<UsuarioVO> getInvitadosPorNota(Object idNota, Object idAutor) {
        String qry = "";
        Query q;
        qry = "Select u.id,"
                +" u.EMAIL,"
                +" u.DESTINATARIOS "
                +" From INVITADOS_NOTA_ORDEN ino,usuario u"
                +" Where ino.NOTA_ORDEN = "+idNota+" AND ino.INVITADO <> '"+idAutor+"'"
                +" AND ino.INVITADO = u.id ";
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
    }
}
