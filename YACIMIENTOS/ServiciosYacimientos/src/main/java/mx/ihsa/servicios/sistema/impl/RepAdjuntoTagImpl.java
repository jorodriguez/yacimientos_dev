/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mx.ihsa.servicios.sistema.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import mx.ihsa.dominio.vo.AdjuntoTagVo;
import mx.ihsa.modelo.RepAdjuntoTag;
import mx.ihsa.modelo.SiAdjunto;
import mx.ihsa.modelo.SiTag;
import mx.ihsa.modelo.Usuario;
import mx.ihsa.sistema.AbstractImpl;

/**
 *
 * @author marin
 */
@Stateless
public class RepAdjuntoTagImpl extends AbstractImpl<RepAdjuntoTag> {

    @PersistenceContext(unitName = "Yacimientos-ServiciosPU")
    private EntityManager em;

    protected EntityManager getEntityManager() {
        return em;
    }

    public RepAdjuntoTagImpl() {
        super(RepAdjuntoTag.class);
    }

    public void guardar(int sesionId, AdjuntoTagVo adjuntoTagVo) {
        RepAdjuntoTag adjuntoTag = new RepAdjuntoTag();
        adjuntoTag.setSiAdjuntoId(new SiAdjunto(adjuntoTagVo.getIdAdjunto()));
        adjuntoTag.setSiTagId(new SiTag(adjuntoTagVo.getIdTag()));
        adjuntoTag.setGenero(new Usuario(sesionId));
        adjuntoTag.setFechaGenero(new Date());
        adjuntoTag.setEliminado(Boolean.FALSE);
        create(adjuntoTag);
    }

    public void modificar(int sesionId, AdjuntoTagVo adjuntoTagVo) {
        RepAdjuntoTag adjuntoTag = find(adjuntoTagVo.getId());
        adjuntoTag.setSiAdjuntoId(new SiAdjunto(adjuntoTagVo.getIdAdjunto()));
        adjuntoTag.setSiTagId(new SiTag(adjuntoTagVo.getIdTag()));
        adjuntoTag.setModifico(new Usuario(sesionId));
        adjuntoTag.setFechaModifico(new Date());
        edit(adjuntoTag);
    }

    public List<AdjuntoTagVo> traerPorTag(int sesionId, String tag) {
        StringBuilder sb = new StringBuilder();
        sb.append(" select rpt.id, ad.id, ad.nombre, t.id, t.nombre  from rep_adjunto_tag rpt ")
                .append("   inner join si_adjunto ad on rpt.si_adjunto_id = ad.id")
                .append("   inner join si_tag t on rpt.si_tag_id = t.id")
                .append(" where t.nombre = '").append(tag).append("'")
                .append(" and rpt.eliminado =  false");
        //
        List<Object[]> lo = em.createNativeQuery(sb.toString()).getResultList();
        List<AdjuntoTagVo> adjTags = new ArrayList<>();
        for (Object[] objects : lo) {
            AdjuntoTagVo adjuntoTagVo = new AdjuntoTagVo();
            adjuntoTagVo.setId((Integer) objects[0]);
            adjuntoTagVo.setIdAdjunto((Integer) objects[1]);
            adjuntoTagVo.setIdTag((Integer) objects[2]);
            adjuntoTagVo.setNombreArchivo((String) objects[3]);
            adjuntoTagVo.setTag((String) objects[4]);
            adjuntoTagVo.setRuta((String) objects[5]);
            adjTags.add(adjuntoTagVo);
        }
        return adjTags;
    }

    public List<AdjuntoTagVo> traerPorArchivo(int adjuntoId) {
        StringBuilder sb = new StringBuilder();
        sb.append(" select rpt.id, ad.id, ad.nombre, t.id, t.nombre  from rep_adjunto_tag rpt ")
                .append("   inner join si_adjunto ad on rpt.si_adjunto_id = ad.id")
                .append("   inner join si_tag t on rpt.si_tag_id = t.id")
                .append(" where ad.id = ").append(adjuntoId)
                .append(" and rpt.eliminado =  false");
        //
        List<Object[]> lo = em.createNativeQuery(sb.toString()).getResultList();
        List<AdjuntoTagVo> adjTags = new ArrayList<>();
        for (Object[] objects : lo) {
            AdjuntoTagVo adjuntoTagVo = new AdjuntoTagVo();
            adjuntoTagVo.setId((Integer) objects[0]);
            adjuntoTagVo.setIdAdjunto((Integer) objects[1]);
            adjuntoTagVo.setIdTag((Integer) objects[2]);
            adjuntoTagVo.setNombreArchivo((String) objects[3]);
            adjuntoTagVo.setTag((String) objects[4]);
            adjuntoTagVo.setRuta((String) objects[5]);
            adjTags.add(adjuntoTagVo);
        }
        return adjTags;
    }
    
    public List<AdjuntoTagVo> buscarPorArchiTagId(int adjuntoId, int tagId) {
        StringBuilder sb = new StringBuilder();
        sb.append(" select rpt.id, ad.id, ad.nombre, t.id, t.nombre  from rep_adjunto_tag rpt ")
                .append("   inner join si_adjunto ad on rpt.si_adjunto_id = ad.id")
                .append("   inner join si_tag t on rpt.si_tag_id = t.id")
                .append(" where ad.id = ").append(adjuntoId)
                .append(" and t.id = ").append(tagId)
                .append(" and rpt.eliminado =  false");
        //
        List<Object[]> lo = em.createNativeQuery(sb.toString()).getResultList();
        List<AdjuntoTagVo> adjTags = new ArrayList<>();
        for (Object[] objects : lo) {
            AdjuntoTagVo adjuntoTagVo = new AdjuntoTagVo();
            adjuntoTagVo.setId((Integer) objects[0]);
            adjuntoTagVo.setIdAdjunto((Integer) objects[1]);
            adjuntoTagVo.setIdTag((Integer) objects[2]);
            adjuntoTagVo.setNombreArchivo((String) objects[3]);
            adjuntoTagVo.setTag((String) objects[4]);
            adjuntoTagVo.setRuta((String) objects[5]);
            adjTags.add(adjuntoTagVo);
        }
        return adjTags;
    }
}
