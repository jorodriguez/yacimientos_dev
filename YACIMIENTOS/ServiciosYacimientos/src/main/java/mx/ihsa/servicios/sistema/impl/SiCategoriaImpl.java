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
import mx.ihsa.dominio.vo.CategoriaAdjuntoVo;
import mx.ihsa.dominio.vo.CategoriaVo;
import mx.ihsa.modelo.SiCategoria;
import mx.ihsa.modelo.Usuario;
import mx.ihsa.sistema.AbstractImpl;

/**
 *
 * @author marin
 */
@Stateless
public class SiCategoriaImpl extends AbstractImpl<SiCategoria> {

    @PersistenceContext(unitName = "Yacimientos-ServiciosPU")
    private EntityManager em;

    protected EntityManager getEntityManager() {
        return em;
    }

    public SiCategoriaImpl() {
        super(SiCategoria.class);
    }

    public int guardar(int sesionId, CategoriaVo categoriaVo) {
        SiCategoria categoria = new SiCategoria();
        categoria.setNombre(categoriaVo.getNombre());
        categoria.setSiCategoriaId(categoriaVo.getIdCategoria() > 0 ? new SiCategoria(categoriaVo.getIdCategoria()) : null);
        categoria.setGenero(new Usuario(sesionId));
        categoria.setFechaGenero(new Date());
        categoria.setEliminado(Boolean.FALSE);
        create(categoria);
        //
        return categoria.getId();
    }

    public void modificar(int sesionId, CategoriaVo categoriaVo) {
        SiCategoria categoria = find(categoriaVo.getId());
        categoria.setNombre(categoriaVo.getNombre());
        categoria.setSiCategoriaId(categoriaVo.getIdCategoria() > 0 ? new SiCategoria(categoriaVo.getIdCategoria()) : null);
        categoria.setModifico(new Usuario(sesionId));
        categoria.setFechaModifico(new Date());
        //
        edit(categoria);
    }

    public List<CategoriaVo> traerCategoriaPorCategoriaId(int categoriaId) {
        StringBuilder sb = new StringBuilder();
        sb.append(" select ca.id, ca.nombre, ca.codigo, ca.si_categoria_id, c2.nombre from si_categoria ca ")
                .append("   left join si_categoria c2 on ca.si_categoria_id = c2.id")
                .append(" where ca.si_categoria_id = ").append(categoriaId)
                .append(" and ca.eliminado = false");
        List<Object[]> lo = em.createNativeQuery(sb.toString()).getResultList();
        List<CategoriaVo> catAdjs = new ArrayList<>();
        for (Object[] objects : lo) {
            CategoriaVo cav = new CategoriaVo();
            cav.setId((Integer) objects[0]);
            cav.setNombre((String) objects[1]);
            cav.setCodigo((String) objects[2]);
            cav.setIdCategoria((Integer) objects[3]);
            cav.setCategoriaSuperior((String) objects[4]);
            catAdjs.add(cav);
        }
        return catAdjs;
    }

}
