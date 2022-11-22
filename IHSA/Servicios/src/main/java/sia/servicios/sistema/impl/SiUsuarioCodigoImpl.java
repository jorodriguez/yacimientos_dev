/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.sistema.impl;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import sia.constantes.Constantes;
import sia.modelo.SiUsuarioCodigo;
import sia.modelo.Usuario;
import sia.modelo.sistema.AbstractFacade;
import sia.util.UtilLog4j;

/**
 *
 * @author ihsa
 */
@Stateless 
public class SiUsuarioCodigoImpl extends AbstractFacade<SiUsuarioCodigo> {

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SiUsuarioCodigoImpl() {
        super(SiUsuarioCodigo.class);
    }

    @Inject
    private SiManejoFechaImpl siManejoFechaLocal;

    /*
     * Guardar el usuario en sesion, genera el codigo y regresa el codigo generado.
     */
    
    public String guardar(String sesion) {
        boolean v = false;
        SiUsuarioCodigo siUsuarioCodigo = new SiUsuarioCodigo();
        siUsuarioCodigo.setUsuario(new Usuario(sesion));
        siUsuarioCodigo.setCodigo(generaCodigo(sesion));
        siUsuarioCodigo.setToken(generaCodigo(sesion));
        siUsuarioCodigo.setGenero(new Usuario(sesion));
        siUsuarioCodigo.setFechaGenero(new Date());
        siUsuarioCodigo.setHoraGenero(new Date());
        siUsuarioCodigo.setEliminado(Constantes.NO_ELIMINADO);
        create(siUsuarioCodigo);
        // gurda el log
        return siUsuarioCodigo.getCodigo();
    }

    
    public String buscarUsuario(String codigo) {
        String usuarioEncontrado = null;
        try {
            StringBuilder usuario = new StringBuilder();
            usuario.append("select uc.id, uc.usuario from si_usuario_codigo uc where uc.codigo = '").append(codigo).append("'");
            usuario.append("    and uc.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");
            Object[] objects = (Object[]) em.createNativeQuery(usuario.toString()).getSingleResult();
            usuarioEncontrado = (String) objects[1];
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "No encontró el usuario . . . " + e.getMessage());
            usuarioEncontrado = "";
        }
        return usuarioEncontrado;
    }

    
    public String generaCodigo(String sesion) {
        return hmacDigest(Constantes.SIA, sesion);
    }

    
    public boolean validaToken(String codigo) {
        boolean encontroUsuario = false;
        String idUsuario = buscarUsuario(codigo);
        if (idUsuario.isEmpty()) {
            //if (codigo.equals(generaCodigo(idUsuario))) {
            encontroUsuario = true;
            //}
        }
        return encontroUsuario;
    }

    private String hmacDigest(String keyTexto, String textoEncriptar) {
        String digest = null;
        //
        String fecha = siManejoFechaLocal.convertirFechaStringddMMyyyy(new Date());
        //
        String cadenaConvertir = textoEncriptar + fecha;
        try {
            SecretKeySpec key = new SecretKeySpec((cadenaConvertir).getBytes("UTF-8"), keyTexto);
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(key);

            byte[] bytes = mac.doFinal(cadenaConvertir.getBytes("ASCII"));

            StringBuilder hash = new StringBuilder();
            for (int i = 0; i < bytes.length; i++) {
                String hex = Integer.toHexString(0xFF & bytes[i]);
                if (hex.length() == 1) {
                    hash.append('0');
                }
                hash.append(hex);
            }
            digest = hash.toString();
        } catch (UnsupportedEncodingException e) {
        } catch (InvalidKeyException e) {
        } catch (NoSuchAlgorithmException e) {
        }
        return digest;
    }

    //fecha: 4 junio se agrega la modificacion para poder crear o actualizar el token del  usuario por la concepcion de la tecnologia FMC
    //autor: Joel Rodriguez
    
    public String guardarToken(String sesion, String telefnoId) {
        boolean v = false;
        SiUsuarioCodigo siUsuarioCodigo = buscarTokenPorUsuario(sesion, telefnoId);
        if (siUsuarioCodigo == null) {
            siUsuarioCodigo = new SiUsuarioCodigo();
            siUsuarioCodigo.setUsuario(new Usuario(sesion));
            siUsuarioCodigo.setCodigo(generaCodigo(sesion));
            siUsuarioCodigo.setToken(telefnoId);
            siUsuarioCodigo.setGenero(new Usuario(sesion));
            siUsuarioCodigo.setFechaGenero(new Date());
            siUsuarioCodigo.setHoraGenero(new Date());
            siUsuarioCodigo.setEliminado(Constantes.NO_ELIMINADO);
            create(siUsuarioCodigo);
            // gurda el log
        } else {
            //
            siUsuarioCodigo.setCodigo(generaCodigo(sesion));
            siUsuarioCodigo.setToken(telefnoId);
            siUsuarioCodigo.setModifico(new Usuario(sesion));
            siUsuarioCodigo.setFechaModifico(new Date());
            siUsuarioCodigo.setHoraModifico(new Date());
            edit(siUsuarioCodigo);
        }
        return siUsuarioCodigo.getCodigo();
    }

    
    public boolean eliminarToken(String sesion, String tokenMsg) {

        boolean response = false;
        
        SiUsuarioCodigo siUsuarioCodigo = buscarTokenPorUsuario(sesion, tokenMsg);        
        
        if (siUsuarioCodigo != null) {
           // siUsuarioCodigo.setCodigo("Sesión Cerrada");
            siUsuarioCodigo.setEliminado(Constantes.ELIMINADO);
            siUsuarioCodigo.setModifico(new Usuario(sesion));
            siUsuarioCodigo.setFechaModifico(new Date());
            siUsuarioCodigo.setHoraModifico(new Date());

            edit(siUsuarioCodigo);
            response = true;            
        }
        
        return response;

    }

    
    public List<SiUsuarioCodigo> buscarPorUsuario(String sesion) {
        try {
            Query q = em.createNamedQuery("SiUsuarioCodigo.findByUsuario");
            q.setParameter(1, sesion);
            List<SiUsuarioCodigo> lusuario = q.getResultList();

            return lusuario;
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "No encontró el usuario . . . " + e.getMessage());
        }
        return null;
    }

    
    public SiUsuarioCodigo buscarTokenPorUsuario(String sesion, String token) {
        try {
            StringBuilder usuario = new StringBuilder();
            usuario.append("select uc.* from si_usuario_codigo uc ");
            usuario.append(" where uc.usuario = '").append(sesion).append("'");
            usuario.append(" and uc.token = '").append(token).append("'");
            usuario.append(" and uc.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");
            SiUsuarioCodigo objects = (SiUsuarioCodigo) em.createNativeQuery(usuario.toString(), SiUsuarioCodigo.class).getSingleResult();
            return objects;
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "No encontró el usuario . . . " + e.getMessage());
            return null;
        }
    }
}
