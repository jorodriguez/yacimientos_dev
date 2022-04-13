/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.inventarios.authentication;

import sia.excepciones.SIAException;

import javax.ejb.*;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import sia.modelo.Usuario;
import sia.modelo.usuario.vo.UsuarioVO;
import sia.servicios.catalogos.impl.UsuarioImpl;

/**
 *
 * @author Eduardo
 */
//Stateless 
@Stateless
public class SiaAuthService {

    UsuarioVO userVO;
    @Inject
    private UsuarioImpl  usuarioRemote;

    
    public UsuarioVO login(String username, String passwordHash) throws SIAException {
        try {
            Usuario authenticatedUser = usuarioRemote.login(username, passwordHash);
            //(Usuario) em.createNativeQuery("SELECT u.* FROM Usuario u WHERE u.ID = '" +username + "' AND u.clave = '" +passwordHash+ "'", Usuario.class).getSingleResult();

            // Generar un VO ligero del usuario
            if (authenticatedUser != null) {
                userVO = new UsuarioVO(authenticatedUser.getId(), authenticatedUser.getNombre(), authenticatedUser.getEmail());
            }

        } catch (NoResultException ex) {
            userVO = null; // Login fallido, no se encontro el usuario porque el usuario o la contraseña son incorrectos
        } catch (Exception e) {
            throw new SIAException("AuthService", "login", e.getMessage());
        }

        return userVO;
    }

}
