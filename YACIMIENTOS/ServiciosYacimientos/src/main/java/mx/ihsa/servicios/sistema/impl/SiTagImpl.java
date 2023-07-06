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
import mx.ihsa.dominio.vo.TagVo;
import mx.ihsa.modelo.SiTag;
import mx.ihsa.modelo.Usuario;
import mx.ihsa.sistema.AbstractImpl;

/**
 *
 * @author marin
 */
@Stateless
public class SiTagImpl extends AbstractImpl<SiTag> {

    public SiTagImpl() {
        super(SiTag.class);
    }

    public void guardar(int sesionId, TagVo tagVo) {
        SiTag siTag = new SiTag();
        siTag.setNombre(tagVo.getNombre());
        siTag.setGenero(new Usuario(sesionId));
        siTag.setFechaGenero(new Date());
        siTag.setEliminado(Boolean.FALSE);
        create(siTag);
    }

    public void modificar(int sesionId, TagVo tagVo) {
        SiTag siTag = find(tagVo.getId());
        siTag.setNombre(tagVo.getNombre());
        siTag.setModifico(new Usuario(sesionId));
        siTag.setFechaModifico(new Date());
        edit(siTag);
    }

    public TagVo buscarPorNombre(String nombre) {
        try {

            StringBuilder sb = new StringBuilder();
            sb.append(" select t.id, t.nombre from si_tag t  where t.nombre = '").append(nombre).append("'")
                    .append(" and t.eliminado = false");
            List<Object[]> lo = em.createNativeQuery(sb.toString()).getResultList();
            TagVo tagVo = new TagVo();
            for (Object[] objects : lo) {
                tagVo.setId((Integer) objects[0]);
                tagVo.setNombre((String) objects[1]);
            }
            return tagVo;
        } catch (Exception e) {
            System.out.println("E: " + e.getMessage());
            return null;
        }
    }
    
    public List<TagVo> traerTodo() {
        try {

            StringBuilder sb = new StringBuilder();
            sb.append(" select t.id, t.nombre from si_tag t  where  t.eliminado = false");
            List<Object[]> lo = em.createNativeQuery(sb.toString()).getResultList();
            List<TagVo> tags = new ArrayList<>();
            for (Object[] objects : lo) {
                TagVo tagVo = new TagVo();
                tagVo.setId((Integer) objects[0]);
                tagVo.setNombre((String) objects[1]);
                tags.add(tagVo);
            }
            return tags;
        } catch (Exception e) {
            System.out.println("E: " + e.getMessage());
            return null;
        }
    }

}
