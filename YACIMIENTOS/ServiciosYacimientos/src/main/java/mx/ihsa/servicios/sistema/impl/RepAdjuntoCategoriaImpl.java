/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mx.ihsa.servicios.sistema.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import mx.ihsa.dominio.vo.AdjuntoVO;
import mx.ihsa.dominio.vo.CategoriaAdjuntoVo;
import mx.ihsa.dominio.vo.CategoriaVo;
import mx.ihsa.dominio.vo.TagVo;
import mx.ihsa.modelo.RepAdjuntoCategoria;
import mx.ihsa.modelo.RepAdjuntoTag;
import mx.ihsa.modelo.SiAdjunto;
import mx.ihsa.modelo.SiCategoria;
import mx.ihsa.modelo.SiTag;
import mx.ihsa.modelo.Usuario;
import mx.ihsa.sistema.AbstractImpl;

/**
 *
 * @author marin
 */
@Stateless
public class RepAdjuntoCategoriaImpl extends AbstractImpl<RepAdjuntoCategoria> {

    @PersistenceContext(unitName = "Yacimientos-ServiciosPU")
    private EntityManager em;

    protected EntityManager getEntityManager() {
        return em;
    }

    public RepAdjuntoCategoriaImpl() {
        super(RepAdjuntoCategoria.class);
    }
    @Inject
    SiAdjuntoImpl siAdjuntoImpl;
    @Inject
    RepAdjuntoTagImpl adjuntoTagImpl;
    @Inject
    SiTagImpl tagImpl;
    String catsel;

    public void guardar(int sesionId, CategoriaAdjuntoVo categoriaAdjuntoVo, AdjuntoVO adjuntoVo, List<CategoriaVo> categoriaVos, List<TagVo> tags) {
        // guardar archivo
        SiAdjunto adj = siAdjuntoImpl.saveSiAdjunto(adjuntoVo.getNombre(), adjuntoVo.getTipoArchivo(), "", adjuntoVo.getTamanio() + "", sesionId);
        //
        categoriaVos.stream().forEach(cs -> {
            catsel += cs.getNombre();
        });
        RepAdjuntoCategoria categiriaAdj = new RepAdjuntoCategoria();
        categiriaAdj.setSiAdjuntoId(adj);
        categiriaAdj.setCategorias(catsel);
        categiriaAdj.setFecha(categiriaAdj.getFecha());
        categiriaAdj.setFase(categiriaAdj.getFase());
        categiriaAdj.setSiCategoriaId(new SiCategoria(categoriaAdjuntoVo.getIdCategoria()));
        categiriaAdj.setGenero(new Usuario(sesionId));
        categiriaAdj.setFechaGenero(new Date());
        categiriaAdj.setEliminado(Boolean.FALSE);
        create(categiriaAdj);
        // registro archivo tags
        tags.stream().forEach(tg -> {
            RepAdjuntoTag repAdjuntoTag = new RepAdjuntoTag();
            repAdjuntoTag.setSiAdjuntoId(adj);
            repAdjuntoTag.setSiTagId(new SiTag(tg.getId()));
            repAdjuntoTag.setGenero(new Usuario(sesionId));
            repAdjuntoTag.setFechaGenero(new Date());
            repAdjuntoTag.setEliminado(Boolean.FALSE);
            //
            adjuntoTagImpl.create(repAdjuntoTag);
        });
    }

    public void modificar(int sesionId, CategoriaAdjuntoVo categoriaAdjuntoVo) {
        RepAdjuntoCategoria adjuntoCat = find(categoriaAdjuntoVo.getId());
        adjuntoCat.setSiAdjuntoId(new SiAdjunto(categoriaAdjuntoVo.getIdAdjunto()));
        adjuntoCat.setSiCategoriaId(new SiCategoria(categoriaAdjuntoVo.getIdCategoria()));
        adjuntoCat.setModifico(new Usuario(sesionId));
        adjuntoCat.setFechaModifico(new Date());
        edit(adjuntoCat);
    }

    public void eliminiar(int sesionId, CategoriaAdjuntoVo categoriaAdjuntoVo) {
        RepAdjuntoCategoria adjuntoCat = find(categoriaAdjuntoVo.getId());
        adjuntoCat.setSiAdjuntoId(new SiAdjunto(categoriaAdjuntoVo.getIdAdjunto()));
        adjuntoCat.setSiCategoriaId(new SiCategoria(categoriaAdjuntoVo.getIdCategoria()));
        adjuntoCat.setModifico(new Usuario(sesionId));
        adjuntoCat.setFechaModifico(new Date());
        edit(adjuntoCat);
    }

    public List<CategoriaAdjuntoVo> traerPorCategoria(int sesionId, String tag) {
        StringBuilder sb = new StringBuilder();
        sb.append(" select rpt.id, ad.id, ad.nombre, ca.id, ca.nombre, ca.categorias, rpt.categorias, rpt.fecha, rpt.fase  from rep_adjunto_categoria rpt ")
                .append("   inner join si_adjunto ad on rpt.si_adjunto_id = ad.id")
                .append("   inner join si_categoria ca on rpt.si_categoria_id = ca.id")
                .append(" where t.nombre = '").append(tag).append("'")
                .append(" and rpt.eliminado =  false");
        //
        List<Object[]> lo = em.createNativeQuery(sb.toString()).getResultList();
        List<CategoriaAdjuntoVo> adjTags = new ArrayList<>();
        for (Object[] objects : lo) {
            CategoriaAdjuntoVo adjuntoTagVo = new CategoriaAdjuntoVo();
            adjuntoTagVo.setId((Integer) objects[0]);
            adjuntoTagVo.setIdAdjunto((Integer) objects[1]);
            adjuntoTagVo.setAdjunto((String) objects[2]);
            adjuntoTagVo.setIdCategoria((Integer) objects[3]);
            adjuntoTagVo.setNombreCategoria((String) objects[4]);
            adjuntoTagVo.setCategorias((String) objects[5]);
            adjuntoTagVo.setFecha((LocalDate) objects[6]);
            adjuntoTagVo.setFace((String) objects[7]);
            adjTags.add(adjuntoTagVo);
        }
        return adjTags;
    }

    public List<CategoriaAdjuntoVo> traerPorArchivo(int adjuntoId) {
        StringBuilder sb = new StringBuilder();
        sb.append(" select rpt.id, ad.id, ad.nombre, ca.id, ca.nombre, rpt.categorias, rpt.fecha, rpt.fase  from rep_adjunto_categoria rpt ")
                .append("   inner join si_adjunto ad on rpt.si_adjunto_id = ad.id")
                .append("   inner join si_categoria ca on rpt.si_categoria_id = ca.id")
                .append(" where ad.id = ").append(adjuntoId)
                .append(" and rpt.eliminado =  false");
        //
        List<Object[]> lo = em.createNativeQuery(sb.toString()).getResultList();
        List<CategoriaAdjuntoVo> adjTags = new ArrayList<>();
        for (Object[] objects : lo) {
            CategoriaAdjuntoVo adjuntoTagVo = new CategoriaAdjuntoVo();
            adjuntoTagVo.setId((Integer) objects[0]);
            adjuntoTagVo.setIdAdjunto((Integer) objects[1]);
            adjuntoTagVo.setAdjunto((String) objects[2]);
            adjuntoTagVo.setIdCategoria((Integer) objects[3]);
            adjuntoTagVo.setNombreCategoria((String) objects[4]);
            adjuntoTagVo.setCategorias((String) objects[5]);
            adjuntoTagVo.setFecha((LocalDate) objects[6]);
            adjuntoTagVo.setFace((String) objects[7]);
            adjTags.add(adjuntoTagVo);
        }
        return adjTags;
    }

    public List<CategoriaAdjuntoVo> buscarPorArchiCategoriaId(int adjuntoId, int categoriaId) {
        StringBuilder sb = new StringBuilder();
        sb.append(" select rpt.id, ad.id, ad.nombre, ca.id, ca.nombre, rpt.categorias, rpt.fecha, rpt.fase  from rep_adjunto_categoria rpt ")
                .append("   inner join si_adjunto ad on rpt.si_adjunto_id = ad.id")
                .append("   inner join si_categoria ca on rpt.si_categoria_id = ca.id")
                .append(" where ad.id = ").append(adjuntoId)
                .append(" and ca.id = ").append(categoriaId)
                .append(" and rpt.eliminado =  false");
        //
        List<Object[]> lo = em.createNativeQuery(sb.toString()).getResultList();
        List<CategoriaAdjuntoVo> adjTags = new ArrayList<>();
        for (Object[] objects : lo) {
            CategoriaAdjuntoVo adjuntoTagVo = new CategoriaAdjuntoVo();
            adjuntoTagVo.setId((Integer) objects[0]);
            adjuntoTagVo.setIdAdjunto((Integer) objects[1]);
            adjuntoTagVo.setAdjunto((String) objects[2]);
            adjuntoTagVo.setIdCategoria((Integer) objects[3]);
            adjuntoTagVo.setNombreCategoria((String) objects[4]);
            adjuntoTagVo.setCategorias((String) objects[5]);
            adjuntoTagVo.setFecha((LocalDate) objects[6]);
            adjuntoTagVo.setFace((String) objects[7]);
            adjTags.add(adjuntoTagVo);
        }
        return adjTags;
    }

    public List<CategoriaAdjuntoVo> traerPorArchiCategoria() {
        StringBuilder sb = new StringBuilder();
        sb.append(" select rpt.id, ad.id, ad.nombre, ca.id, ca.nombre, rpt.categorias, rpt.fecha, rpt.fase  from rep_adjunto_categoria rpt ")
                .append("   inner join si_adjunto ad on rpt.si_adjunto_id = ad.id")
                .append("   inner join si_categoria ca on rpt.si_categoria_id = ca.id")
                .append(" where rpt.eliminado =  false");
        //
        List<Object[]> lo = em.createNativeQuery(sb.toString()).getResultList();
        List<CategoriaAdjuntoVo> adjTags = new ArrayList<>();
        for (Object[] objects : lo) {
            CategoriaAdjuntoVo adjuntoTagVo = new CategoriaAdjuntoVo();
            adjuntoTagVo.setId((Integer) objects[0]);
            adjuntoTagVo.setIdAdjunto((Integer) objects[1]);
            adjuntoTagVo.setAdjunto((String) objects[2]);
            adjuntoTagVo.setIdCategoria((Integer) objects[3]);
            adjuntoTagVo.setNombreCategoria((String) objects[4]);
            adjuntoTagVo.setCategorias((String) objects[5]);
            adjuntoTagVo.setFecha((LocalDate) objects[6]);
            adjuntoTagVo.setFace((String) objects[7]);
            adjTags.add(adjuntoTagVo);
        }
        return adjTags;
    }

}
