/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lector.servicios.sistema.impl;

import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NonUniqueResultException;
import lector.archivador.AlmacenDocumentos;
import lector.archivador.ProveedorAlmacenDocumentos;
import lector.constantes.Constantes;
import lector.dominio.modelo.usuario.vo.UsuarioVO;
import lector.dominio.vo.UsuarioRolVo;
import lector.excepciones.LectorException;
import lector.modelo.Usuario;
import lector.servicios.catalogos.impl.UsuarioImpl;
import lector.sistema.AbstractImpl;
import static lector.util.UsuarioIHelp.buildUsuarioDto;
import lector.util.UtilLog4j;
import lector.util.ValidadorNombreArchivo;
import lector.vision.InformacionCredencialDto;

/**
 *
 * @author jorodriguez
 */
@Stateless
public class ContactoImpl extends AbstractImpl<Usuario> {

    @Inject
    private UsuarioImpl usuarioService;
    
    public ContactoImpl() {
        super(Usuario.class);
    }

    public void guardarContacto(InformacionCredencialDto informacionCredencial) throws LectorException {

        final ValidadorNombreArchivo validador = new ValidadorNombreArchivo();
        
        //  datos del contacto
        final UsuarioVO usuario = informacionCredencial.getUsuarioDto();

        final Usuario usuarioBuild = buildUsuarioDto(usuario);

        usuarioService.create(usuarioBuild);
        
        
           
    } 

    public UsuarioRolVo findNombreUsuarioRolVO(int rolId, String nombreUsuario, int idCampo) {
        clearQuery();
        try {
            query.append("select ur.id,ur.usuario,ur.si_rol  from SI_USUARIO_ROL ur where ur.USUARIO =");
            query.append(" (select u.id from USUARIO u where u.NOMBRE = '").append(nombreUsuario).append("'").append(" and u.activo = '").append(Constantes.BOOLEAN_TRUE).append("')");
            query.append(" and ur.si_rol= ").append(rolId);
            query.append(" and ur.ap_campo = ").append(idCampo);
            query.append(" and ur.eliminado = '").append(Constantes.NO_ELIMINADO).append("' ");
            UsuarioRolVo usuarioRol = new UsuarioRolVo();
            UtilLog4j.log.info(this, "Q: " + query.toString());
            Object[] lo = (Object[]) em.createNativeQuery(query.toString()).getSingleResult();
            if (lo != null) {
                usuarioRol.setIdUsuarioRol((Integer) lo[0]);
                usuarioRol.setIdUsuario((String) lo[1]);
                usuarioRol.setIdRol((Integer) lo[2]);

            }
            return usuarioRol;
        } catch (NonUniqueResultException u) {
            UtilLog4j.log.info(this, "hay mas de un resultado");
            return new UsuarioRolVo();

        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "No recupero nada nombre usuario: " + e.getMessage());
            return null;
        }
    }

    public List<UsuarioRolVo> traerUsuarioByRol(List<Integer> listaRoles, int idCampo) {
        try {
            String cad = listaRoles.toString();
            String cadenaLista = cad.substring(1, cad.length() - 1);
            clearQuery();
            query.append("select ur.ID, u.ID, u.NOMBRE, r.ID, r.NOMBRE, ur.is_principal, u.EMAIL, u.TELEFONO ");
            query.append(" from SI_USUARIO_ROL ur, USUARIO u, SI_ROL r ");
            query.append(" where ur.SI_ROL in ( ").append(cadenaLista);
            query.append(" ) and ur.USUARIO = u.id and ur.SI_ROL = r.id ");
            query.append(" and ur.ap_campo = ").append(idCampo);
            query.append(" and u.ELIMINADO = 'False' and r.ELIMINADO = 'False' and ur.ELIMINADO = 'False' ");
            // 
            UtilLog4j.log.info(this, " query roles ");
            List<Object[]> lo = em.createNativeQuery(query.toString()).getResultList();
            List<UsuarioRolVo> lu = new ArrayList<>();
            for (Object[] objects : lo) {
            }

            return lu;
        } catch (Exception e) {
            e.getStackTrace();
            return null;
        }
    }

}
