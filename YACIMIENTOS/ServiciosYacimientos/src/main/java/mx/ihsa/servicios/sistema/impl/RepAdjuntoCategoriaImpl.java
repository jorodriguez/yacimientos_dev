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
import mx.ihsa.modelo.CatObjetivo;
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

    public void guardar(int sesionId, CategoriaAdjuntoVo categoriaAdjuntoVo, int adjId, List<CategoriaVo> categoriaVos, List<TagVo> tags) {
        try {
            RepAdjuntoCategoria categiriaAdj = new RepAdjuntoCategoria();
            categiriaAdj.setSiAdjuntoId(new SiAdjunto(adjId));
            categiriaAdj.setSiCategoriaId(new SiCategoria(categoriaAdjuntoVo.getIdCategoria()));
            categiriaAdj.setCatObjetivoId(new CatObjetivo(categoriaAdjuntoVo.getIdObjetivo()));
            categiriaAdj.setFecha(LocalDate.now());
            categiriaAdj.setNombre(categoriaAdjuntoVo.getNombre());
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
                if (tg.getId() == 0) {
                    TagVo tag = tagImpl.buscarPorNombre(tg.getNombre());
                    tg.setId(tag.getId());
                }
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

    private String consulta() {
        StringBuilder sb = new StringBuilder();
        sb.append(" select rpt.id, ad.id, ad.nombre, ca.id, ca.nombre, rpt.fecha, rpt.nombre, cob.id, cob.nombre from rep_adjunto_categoria rpt ")
                .append("   inner join si_adjunto ad on rpt.si_adjunto_id = ad.id")
                .append("   inner join si_categoria ca on rpt.si_categoria_id = ca.id")
                .append("   inner join cat_objetivo cob on rpt.cat_objetivo_id = cob.id");
        return sb.toString();
    }

    public List<CategoriaAdjuntoVo> traerPorCategoria(int sesionId, String tag) {
        StringBuilder sb = new StringBuilder();
        sb.append(consulta())
                .append(" where t.nombre = '").append(tag).append("'")
                .append(" and rpt.eliminado =  false");
        //
        List<Object[]> lo = em.createNativeQuery(sb.toString()).getResultList();
        List<CategoriaAdjuntoVo> adjTags = new ArrayList<>();
        for (Object[] objects : lo) {
            adjTags.add(cast(objects));
        }
        return adjTags;
    }

    public List<CategoriaAdjuntoVo> traerPorArchivo(int adjuntoId) {
        StringBuilder sb = new StringBuilder();
        sb.append(consulta())
                .append(" where ad.id = ").append(adjuntoId)
                .append(" and rpt.eliminado =  false");
        //
        List<Object[]> lo = em.createNativeQuery(sb.toString()).getResultList();
        List<CategoriaAdjuntoVo> adjTags = new ArrayList<>();
        for (Object[] objects : lo) {
            adjTags.add(cast(objects));
        }
        return adjTags;
    }

    public List<CategoriaAdjuntoVo> buscarPorArchiCategoriaId(int adjuntoId, int categoriaId) {
        StringBuilder sb = new StringBuilder();
        sb.append(consulta())
                .append(" where ad.id = ").append(adjuntoId)
                .append(" and ca.id = ").append(categoriaId)
                .append(" and rpt.eliminado =  false");
        //
        List<Object[]> lo = em.createNativeQuery(sb.toString()).getResultList();
        List<CategoriaAdjuntoVo> adjTags = new ArrayList<>();
        for (Object[] objects : lo) {
            adjTags.add(cast(objects));
        }
        return adjTags;
    }

    public List<CategoriaAdjuntoVo> traerPorArchiCategoria() {
        StringBuilder sb = new StringBuilder();
        sb.append(consulta())
                .append(" where rpt.eliminado =  false");
        //
        List<Object[]> lo = em.createNativeQuery(sb.toString()).getResultList();
        List<CategoriaAdjuntoVo> adjTags = new ArrayList<>();
        for (Object[] objects : lo) {
            adjTags.add(cast(objects));
        }
        return adjTags;
    }

    private LocalDate castDate(Date fecha) {
        return Instant.ofEpochMilli(fecha.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
    }

    private CategoriaAdjuntoVo cast(Object[] objects) {
        CategoriaAdjuntoVo adjuntoTagVo = new CategoriaAdjuntoVo();
        adjuntoTagVo.setId((Integer) objects[0]);
        adjuntoTagVo.setIdAdjunto((Integer) objects[1]);
        adjuntoTagVo.setAdjunto((String) objects[2]);
        adjuntoTagVo.setIdCategoria((Integer) objects[3]);
        adjuntoTagVo.setNombreCategoria((String) objects[4]);
        adjuntoTagVo.setFecha(objects[5] != null ? castDate((Date) objects[5]) : null);
        adjuntoTagVo.setNombre((String) objects[6]);
        adjuntoTagVo.setIdObjetivo((Integer) objects[7]);
        adjuntoTagVo.setObjetivo((String) objects[8]);
        return adjuntoTagVo;
    }
}
