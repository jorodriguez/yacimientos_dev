package sia.inventarios.authentication;

import javax.ejb.ApplicationException;

/**
 * Excepción lanzadad cuando un token (API key) esta vencido.
 *
 * @author Aplimovil SA de CV
 */
@ApplicationException
public class ApiKeyExpiradoException extends RuntimeException {

    public ApiKeyExpiradoException() {
    }
}
