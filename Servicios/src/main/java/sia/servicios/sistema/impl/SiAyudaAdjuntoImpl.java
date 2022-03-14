/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.sistema.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import javax.ejb.LocalBean;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import sia.constantes.Constantes;
import sia.excepciones.SIAException;
import sia.modelo.SiAdjunto;
import sia.modelo.SiAyuda;
import sia.modelo.SiAyudaAdjunto;
import sia.modelo.Usuario;
import sia.modelo.sistema.AbstractFacade;
import sia.servicios.sistema.vo.SiAyudaAdjuntoVo;
import sia.util.UtilLog4j;

/**
 *
 * @author b75ckd35th
 */
@LocalBean 
public class SiAyudaAdjuntoImpl extends AbstractFacade<SiAyudaAdjunto> {

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
    
    public SiAyudaAdjuntoImpl() {
        super(SiAyudaAdjunto.class);
    }

    
    public List<SiAyudaAdjunto> getAllAdjuntosByAyuda(SiAyuda ayuda) throws SIAException, Exception {
        UtilLog4j.log.info(this, "SiAyudaAdjuntoImpl.getAllAdjuntosByAyuda()");
        List<SiAyudaAdjunto> ayudaAdjuntos = null;

        if (ayuda != null) {

            ayudaAdjuntos = em.createQuery("SELECT aya FROM SiAyudaAdjunto aya WHERE aya.siAyuda.id = :idAyuda AND aya.eliminado = :eliminado").setParameter("eliminado", Constantes.BOOLEAN_FALSE).setParameter("idAyuda", ayuda.getId()).getResultList();

            UtilLog4j.log.info(this, "Se encontraron " + (ayudaAdjuntos != null ? ayudaAdjuntos.size() : 0) + " ayudasAdjuntas");

            return ayudaAdjuntos;
        } else {
            throw new SIAException(SiAyudaAdjuntoImpl.class.getName(), "getAllAdjuntosByAyuda()",
                    "Faltan la ayuda para poder realizar la consulta");
        }
    }

    
    public void save(SiAyuda ayuda, SiAdjunto adjunto, String idUsuario) throws SIAException, Exception {
        UtilLog4j.log.info(this, "SiAyudaAdjunto.save()");

        SiAyudaAdjunto ayudaAdjunto = null;

        if (ayuda != null && adjunto != null) {
            ayudaAdjunto = new SiAyudaAdjunto();
            ayudaAdjunto.setSiAdjunto(adjunto);
            ayudaAdjunto.setSiAyuda(ayuda);
            ayudaAdjunto.setFechaGenero(new Date());
            ayudaAdjunto.setHoraGenero(new Date());
            ayudaAdjunto.setGenero(new Usuario(idUsuario));
            ayudaAdjunto.setEliminado(Constantes.NO_ELIMINADO);
            super.create(ayudaAdjunto);

        } else {
            throw new SIAException(SiAyudaAdjuntoImpl.class.getName(), "save()",
                    "Faltan parámetros para poder guardar la relación entre la Ayuda y el Adjunto",
                    ("Parámetros: adjunto: " + (adjunto != null ? adjunto.getId() : null)
                    + " ayuda: " + (ayuda != null ? ayuda.getId() : null)
                    + "idUsuario" + idUsuario));
        }
    }

    
    public void delete(SiAyudaAdjunto ayudaAdjunto, String idUsuario) throws SIAException, Exception {
        UtilLog4j.log.info(this, "SiAyudaAdjunto.delete()");

        if (ayudaAdjunto != null && idUsuario != null && !idUsuario.equals("")) {
            ayudaAdjunto.setFechaGenero(new Date());
            ayudaAdjunto.setHoraGenero(new Date());
            ayudaAdjunto.setEliminado(Constantes.ELIMINADO);
            ayudaAdjunto.setGenero(new Usuario(idUsuario));

            super.edit(ayudaAdjunto);
        } else {
            throw new SIAException(SiAyudaAdjuntoImpl.class.getName(), "delete()",
                    "Faltan parámetros para poder eliminar la relación entre la ayuda y el adjunto",
                    ("Parámetros: ayudaAdjunto: " + (ayudaAdjunto != null ? ayudaAdjunto : null)));
        }

        UtilLog4j.log.info(this, "AyudaAdjunto ELIMINADO SATISFACTORIAMENTE");
    }

    
    public List<SiAyudaAdjuntoVo> getAllSiAdjuntoBySiAyuda(int idSiAyuda) {
        UtilLog4j.log.info(this, "SiAyudaAdjuntoImpl.getAllSiAdjuntoBySiAyuda()");

        String q = "SELECT "
                + "aya.ID, " //0
                + "ayu.ID AS ID_SI_AYUDA, " //1
                + "adj.ID AS ID_SI_ADJUNTO, " //2
                + "adj.URL, " //3
                + "adj.TIPO_ARCHIVO, " //4
                + "adj.UUID " //5
                + "FROM "
                + "SI_AYUDA_ADJUNTO aya, SI_ADJUNTO adj, SI_AYUDA ayu "
                + "WHERE "
                + "aya.ELIMINADO ='" + (Constantes.NO_ELIMINADO) + "' "
                + "AND aya.SI_AYUDA =" + idSiAyuda
                + "AND aya.SI_ADJUNTO = adj.ID "
                + "AND aya.SI_AYUDA = ayu.ID "
                + "ORDER BY aya.ID ";

        Query consulta = em.createNativeQuery(q);

        UtilLog4j.log.info(this, "query: " + consulta.toString());

        List<Object[]> result = consulta.getResultList();
        List<SiAyudaAdjuntoVo> list = new ArrayList<SiAyudaAdjuntoVo>();

        SiAyudaAdjuntoVo vo = null;

        for (Object[] objects : result) {
            vo = new SiAyudaAdjuntoVo();
            vo.setId((Integer) objects[0]);
            vo.setIdSiAyuda((Integer) objects[1]);
            vo.setIdSiAjunto((Integer) objects[2]);
            vo.setUrl((String) objects[3]);
            vo.setTipoArchivo((String) objects[4]);
            vo.setUuidSiAjunto((String) objects[5]);
            list.add(vo);
        }

        UtilLog4j.log.info(this, "Se encontraron " + (list != null ? list.size() : 0) + " SiAyudaAdjunto");

        return (list != null ? list : Collections.EMPTY_LIST);
    }
}
