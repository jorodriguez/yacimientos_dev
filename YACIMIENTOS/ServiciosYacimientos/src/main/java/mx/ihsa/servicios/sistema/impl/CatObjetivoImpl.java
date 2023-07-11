/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mx.ihsa.servicios.sistema.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import mx.ihsa.dominio.vo.ObjetivoVo;
import mx.ihsa.modelo.CatObjetivo;
import mx.ihsa.modelo.Usuario;
import mx.ihsa.sistema.AbstractImpl;

/**
 *
 * @author marin
 */
@Stateless
public class CatObjetivoImpl extends AbstractImpl<CatObjetivo> {


    public CatObjetivoImpl() {
        super(CatObjetivo.class);
    }

    public CatObjetivo guardar(int sesionId, ObjetivoVo objVo) {
        CatObjetivo objetivo = buscarPorNombre(objVo.getNombre());
        if (objetivo == null) {
            objetivo = new CatObjetivo();
            objetivo.setNombre(objVo.getNombre());
            objetivo.setGenero(new Usuario(sesionId));
            objetivo.setFechaGenero(new Date());
            objetivo.setEliminado(Boolean.FALSE);
            create(objetivo);
        }
        //
        return objetivo;
    }

    public void modificar(int sesionId, ObjetivoVo objVo) {
        CatObjetivo objetivo = find(objVo.getId());
        objetivo.setNombre(objVo.getNombre());
        objetivo.setModifico(new Usuario(sesionId));
        objetivo.setFechaModifico(new Date());
        //
        edit(objetivo);
    }

    public CatObjetivo buscarPorNombre(String nombre) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(" select ca.id, ca.nombre from cat_objetivo ca ")
                    .append(" where ca.nombre = '").append(nombre).append("'")
                    .append(" and ca.eliminado = false");
            return (CatObjetivo) em.createNativeQuery(sb.toString(), CatObjetivo.class).getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }
    
     public List<CatObjetivo> traerTodos() {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(" select ca.id, ca.nombre from cat_objetivo ca ")
                    .append(" where ca.eliminado = false");
            return  em.createNativeQuery(sb.toString(), CatObjetivo.class).getResultList();
        } catch (Exception e) {
            return null;
        }
    }

}
