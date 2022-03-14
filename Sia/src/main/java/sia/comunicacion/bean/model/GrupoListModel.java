/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.comunicacion.bean.model;

import java.io.Serializable;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import sia.modelo.CoGrupo;
import sia.modelo.CoMiembro;
import sia.servicios.comunicacion.impl.CoGrupoImpl;

/**
 *
 * @author hacosta
 */
@ManagedBean(name = "grupoListModel")
@SessionScoped
public class GrupoListModel implements Serializable{

    @EJB
    private CoGrupoImpl servicioCoGrupo;

    /** Creates a new instance of GrupoListModel */
    public GrupoListModel() {
    }
    
    public List<CoGrupo> getGrupos(String administrador){
        return this.servicioCoGrupo.getGrupos(administrador);
    }
     
    public CoGrupo getGrupoPorNombre(String nombreGrupo, String administrador){
      return  this.servicioCoGrupo.buscarPorNombre(nombreGrupo, administrador);
    }
    public void crearGrupo(CoGrupo grupo){
        this.servicioCoGrupo.create(grupo);
    }
    
    public void actualizarGrupo(CoGrupo grupo){
        this.servicioCoGrupo.edit(grupo);
    }
    
    public void eliminarGrupo(CoGrupo grupo){
        this.servicioCoGrupo.eliminarGrupo(grupo);
    }
    
    public List<CoMiembro> getMiembros(Integer idGrupo){
        return this.servicioCoGrupo.getMiembros(idGrupo);
    }
    
    public int getTotalMiembros(Integer idGrupo){
       return this.servicioCoGrupo.getTotalMiembros(idGrupo);
    }
    
    public void agregarMiembro(CoMiembro miembro){
        this.servicioCoGrupo.agregarMiembro(miembro);
    }
    
    public CoMiembro getMiembroPorNombre(String nombreMiembro, Integer idGrupo){
        return this.servicioCoGrupo.getMiembroPorNombre(nombreMiembro, idGrupo);
    }
    
    public void actualizarMiembro(CoMiembro miembro){
        this.servicioCoGrupo.actualizarMiembro(miembro);
    }
    
}
