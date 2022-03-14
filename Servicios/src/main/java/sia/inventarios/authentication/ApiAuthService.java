package sia.inventarios.authentication;

import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.util.Calendar;
import java.util.Date;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.jose4j.keys.HmacKey;
import org.jose4j.lang.JoseException;
import sia.constantes.Constantes;
import sia.modelo.Usuario;
import sia.modelo.usuario.vo.ApiSessionVO;
import sia.modelo.usuario.vo.UsuarioVO;
import sia.modelo.vo.inventarios.ApiSesion;
import sia.modelo.vo.inventarios.ApiSesionPK;
import sia.servicios.catalogos.impl.UsuarioImpl;

/**
 * Contiene logica de autenticación para los servicios REST
 *
 * @author Aplimovil SA de CV
 */
@Stateless (name = "Inventarios_ApiAuthService")
@LocalBean
public class ApiAuthService {

    private static final String API_KEY_DATA = "86dfe8a400a1bb78ee3f7c2a0a101e8bb2f6598391d6ce213fc338e8224895b2";

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    @Inject
    SiaAuthService authService;
    @Inject
    UsuarioImpl usuarioRemote;

    
    public ApiSessionVO login(String username, String passwordHash) throws Exception {
        UsuarioVO usuarioVO = authService.login(username, passwordHash);
        if (usuarioVO == null) {
            return null;
        }
        //Generar jwt token
        String jwt;
        do {
            jwt = crearToken(usuarioVO.getId());
        } while (buscarSesionPorPK(jwt, usuarioVO.getId()) != null);//se crea un nuevo token si ya existe uno ya creado para el usuario
        //Si el usuario tiene una sesión activa, se la inactiva
        ApiSesion sesion = buscarSesionActivaPorUsuario(usuarioVO.getId());
        if (sesion != null) {
            inactivarSesion(sesion, usuarioVO.getId());
        }
        //Guardar la sesion
        guardarSesion(jwt, usuarioVO.getId());
        return new ApiSessionVO(usuarioVO.getId(), usuarioVO.getNombre(), jwt);
    }

    
    public boolean esApiKeyValido(String apiKey) {
        try {
            //se busca la sesion y se valida que la sesion este activa
            ApiSesion sesion = buscarSesionPorApiKey(apiKey);
            if (sesion != null && !sesion.isActivo()) {
                throw new ApiKeyExpiradoException();
            }
            return sesion.isActivo();
        } catch (InvalidJwtException ex) {
            return false;
        } catch (UnsupportedEncodingException ex) {
            return false;
        }
    }

    
    public void finalizarSesion(String apiKey) throws Exception {
        //se obtiene el id del usuario del token
        String usuarioId = validarTokenYObtenerUsuarioId(apiKey);
        //se busca la sesion
        ApiSesion sesion = buscarSesionPorPK(apiKey, usuarioId);
        //se verifica que exista la sesion
        if (sesion != null && sesion.isActivo()) {
            inactivarSesion(sesion, usuarioId);
        }
    }

    
    public UsuarioVO obtenerUsuario(String apiKey) throws Exception {
        //se valida el token y se obtiene el usuarioId
        ApiSesion sesion = buscarSesionPorApiKey(apiKey);
        UsuarioVO usuario = new UsuarioVO();
        usuario.setId(sesion.getUsuario().getId());
        usuario.setNombre(sesion.getUsuario().getNombre());
        return usuario;
    }

    
    public void limpiarAPIKeysAntiguos(String username) throws Exception {
        if (deberiaRemoverApiKey()) {
            em.createNamedQuery("ApiSesion.EliminarKeysMenoresATresMeses")
                    .setParameter("tresMesesAtras", obtenerFechaTresMesesAtras())
                    .setParameter("usuarioId", username)
                    .executeUpdate();
        }
    }

    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    private ApiSesion buscarSesionPorApiKey(String apiKey) throws UnsupportedEncodingException, InvalidJwtException {
        //se valida el token y se obtiene el usuarioId
        String usuarioId = validarTokenYObtenerUsuarioId(apiKey);
        //se busca la sesion y se valida que la sesion este activa
        return buscarSesionPorPK(apiKey, usuarioId);
    }

    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    private ApiSesion buscarSesionPorPK(String apiKey, String usuarioId) {
        return em.find(ApiSesion.class, new ApiSesionPK(apiKey, usuarioId));
    }

    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    private ApiSesion buscarSesionActivaPorUsuario(String usuarioId) {
        try {
            return em.createNamedQuery("ApiSesion.BuscarSesionActivaPorUsuarioId", ApiSesion.class)
                    .setParameter("usuarioId", usuarioId)
                    .setParameter("activo", Constantes.BOOLEAN_TRUE)
                    .getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
    }

    private void guardarSesion(String apiKey, String usuarioId) {
        ApiSesion sesion = new ApiSesion();
        Usuario usuario = new Usuario(usuarioId);
        sesion.setUsuario(usuario);
        sesion.setApiKey(apiKey);
        sesion.setActivo(Constantes.BOOLEAN_TRUE);
        sesion.setFechaGenero(new Date());
        sesion.setHoraGenero(new Date());
        sesion.setGenero(usuario);
        em.persist(sesion);
    }

    private void inactivarSesion(ApiSesion sesion, String usuarioModificoId) {
        //se actualiza la estado de la sesion a inactiva
        sesion.setActivo(Constantes.BOOLEAN_FALSE);
        sesion.setModifico(new Usuario(usuarioModificoId));
        //sesion.setFechaModifico(new Date());
        //sesion.setHoraModifico(new Date());
        em.merge(sesion);
    }

    private String crearToken(String usuarioId) throws UnsupportedEncodingException, JoseException {
        JwtClaims claims = new JwtClaims();
        claims.setSubject("ApiKey");
        claims.setIssuer("IHSA");
        claims.setAudience("movil");
        claims.setClaim("usuarioId", usuarioId);
        claims.setGeneratedJwtId();
        claims.setIssuedAtToNow();

        Key key = new HmacKey(API_KEY_DATA.getBytes("UTF-8"));

        JsonWebSignature jws = new JsonWebSignature();
        jws.setPayload(claims.toJson());

        jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.HMAC_SHA256);
        jws.setKey(key);
        jws.setDoKeyValidation(false);

        return jws.getCompactSerialization();
    }

    private String validarTokenYObtenerUsuarioId(String jwt) throws InvalidJwtException, UnsupportedEncodingException {
        Key key = new HmacKey(API_KEY_DATA.getBytes("UTF-8"));
        JwtConsumer jwtConsumer = new JwtConsumerBuilder()
                .setAllowedClockSkewInSeconds(30)
                .setRequireSubject()
                .setExpectedIssuer("IHSA")
                .setExpectedAudience("movil")
                .setVerificationKey(key)
                .setRelaxVerificationKeyValidation() // relaxes key length requirement
                .build();

        JwtClaims processedClaims = jwtConsumer.processToClaims(jwt);
        return (String) processedClaims.getClaimValue("usuarioId");
    }

    private boolean deberiaRemoverApiKey() {
        int randomNum = (int) (Math.random() * 10) + 1;
        return randomNum < 2;
    }

    private Date obtenerFechaTresMesesAtras() {
        Date referenceDate = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(referenceDate);
        c.add(Calendar.MONTH, -3);
        return c.getTime();
    }

    public UsuarioVO usuarioVo(String nombre) throws Exception {
        //se valida el token y se obtiene el usuarioId
        UsuarioVO sesion = usuarioRemote.findByName(nombre);
        return sesion;
    }

}
