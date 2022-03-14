/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.comunicacion.impl;

import java.util.ArrayList;
import java.util.List;
import javax.ejb.LocalBean;
import javax.inject.Inject;
import sia.modelo.CoGrupo;
import sia.modelo.CoPrivacidad;
import sia.modelo.Usuario;
import sia.modelo.comunicacion.ComparteCon;
import sia.servicios.campo.nuevo.impl.ApCampoUsuarioRhPuestoImpl;
import sia.servicios.catalogos.impl.UsuarioImpl;
import sia.util.UtilLog4j;

/**
 *
 * @author hacosta
 */
@LocalBean 
public class ComparteConImpl {

    @Inject
    private CoPrivacidadImpl servicioCoPrivacidad;
    @Inject
    private UsuarioImpl usuarioServicioImpl;
    @Inject
    private CoGrupoImpl servicioCoGrupoRemote;
    @Inject
    private ApCampoUsuarioRhPuestoImpl apCampoUsuarioRhPuestoRemote;

    
    public List<ComparteCon> getComparteCon(String idUsuario) {
        List<ComparteCon> lista = new ArrayList<ComparteCon>();
        try {
            for (Usuario usuario : this.usuarioServicioImpl.getActivos()) {
                lista.add(new ComparteCon(usuario.getId(), usuario.getNombre(), this.apCampoUsuarioRhPuestoRemote.getPuestoPorUsurioCampo(usuario.getId(), usuario.getApCampo().getId().intValue()), "Usuario"));
            }
            for (CoPrivacidad Privacidad : this.servicioCoPrivacidad.getListaPrivacidad()) {
                lista.add(new ComparteCon(Privacidad.getId().toString(), Privacidad.getNombre(), Privacidad.getDescripcion(), "Privacidad"));
            }
            for (CoGrupo grupo : this.servicioCoGrupoRemote.getGrupos(idUsuario)) {
                lista.add(new ComparteCon(grupo.getId().toString(), grupo.getNombre(), grupo.getDescripcion(), "Grupo"));
            }
            return lista;
        } catch (Exception e) {
            UtilLog4j.log.error(e);
            return null;
        }
    }

    
    public List<ComparteCon> getGrupos(String idUsuario) {
        List<ComparteCon> lista = new ArrayList<ComparteCon>();
        try {
            for (CoPrivacidad Privacidad : this.servicioCoPrivacidad.getListaPrivacidad()) {
                lista.add(new ComparteCon(Privacidad.getId().toString(), Privacidad.getNombre(), Privacidad.getDescripcion(), "Privacidad"));
            }
            for (CoGrupo grupo : this.servicioCoGrupoRemote.getGrupos(idUsuario)) {
                lista.add(new ComparteCon(grupo.getId().toString(), grupo.getNombre(), grupo.getDescripcion(), "Grupo"));
            }
            return lista;
        } catch (Exception e) {
            UtilLog4j.log.error(e);
            return null;
        }
    }

}
