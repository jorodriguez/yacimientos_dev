/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mx.ihsa.servicios.sistema.impl;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.inject.Inject;
import mx.ihsa.dominio.vo.AdjuntoTagVo;
import mx.ihsa.dominio.vo.CategoriaAdjuntoVo;
import mx.ihsa.dominio.vo.CategoriaVo;
import mx.ihsa.dominio.vo.TagVo;
import mx.ihsa.modelo.RepAdjuntoCategoria;
import mx.ihsa.modelo.SiAdjunto;
import mx.ihsa.modelo.SiCategoria;
import mx.ihsa.modelo.Usuario;
import mx.ihsa.sistema.AbstractImpl;

/**
 *
 * @author marin
 */
@Stateless
public class RepAdjuntoCategoriaImpl extends AbstractImpl<RepAdjuntoCategoria> {

    public RepAdjuntoCategoriaImpl() {
        super(RepAdjuntoCategoria.class);
    }
    @Inject
    SiAdjuntoImpl siAdjuntoImpl;
    @Inject
    RepAdjuntoTagImpl adjuntoTagImpl;
    @Inject
    SiTagImpl tagImpl;
    StringBuilder catsel;

    public void guardar(int sesionId, CategoriaAdjuntoVo categoriaAdjuntoVo, int adjId, List<CategoriaVo> categoriaVos, List<TagVo> tags) {
        try {
            catsel = new StringBuilder();
            categoriaVos.stream().forEach(cs -> {
                catsel.append(", ").append(cs.getId());
            });
            RepAdjuntoCategoria categiriaAdj = new RepAdjuntoCategoria();
            categiriaAdj.setSiAdjuntoId(new SiAdjunto(adjId));
            categiriaAdj.setSiCategoriaId(new SiCategoria(categoriaAdjuntoVo.getIdCategoria()));
            categiriaAdj.setCategorias(catsel.substring(4, catsel.length()));
            categiriaAdj.setFecha(categoriaAdjuntoVo.getFecha());
            categiriaAdj.setFase(categoriaAdjuntoVo.getFace());
            categiriaAdj.setNotas(categoriaAdjuntoVo.getNotas());
            categiriaAdj.setArchivoTexto(categoriaAdjuntoVo.getArchivoTexto());

            categiriaAdj.setGenero(new Usuario(sesionId));
            categiriaAdj.setFechaGenero(new Date());
            categiriaAdj.setEliminado(Boolean.FALSE);
            create(categiriaAdj);
//            // registro archivo tags
            tags.stream().forEach(tg -> {
                AdjuntoTagVo adjuntoTagVo = new AdjuntoTagVo();
                adjuntoTagVo.setIdAdjunto(adjId);
                adjuntoTagVo.setIdTag(tg.getId());
                adjuntoTagImpl.guardar(sesionId, adjuntoTagVo);
            });
        } catch (Exception ex) {
            System.out.println("Error " + ex);
            Logger.getLogger(RepAdjuntoCategoriaImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
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
        adjuntoCat.setModifico(new Usuario(sesionId));
        adjuntoCat.setFechaModifico(new Date());
        adjuntoCat.setEliminado(Boolean.TRUE);
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
            adjuntoTagVo.setFecha(objects[6] != null ? castDate((Date) objects[6]) : null);
            adjuntoTagVo.setFace((String) objects[7]);
            adjTags.add(adjuntoTagVo);
        }
        return adjTags;
    }

    private LocalDate castDate(Date fecha) {
        return Instant.ofEpochMilli(fecha.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
    }

}
