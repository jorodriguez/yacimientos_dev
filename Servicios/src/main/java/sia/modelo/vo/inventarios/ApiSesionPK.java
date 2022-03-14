package sia.modelo.vo.inventarios;

import java.io.Serializable;

/**
 * Representa la clave primaria de la entidad {@link ApiSesion}
 *
 * @author Aplimovil SA de CV
 */

public class ApiSesionPK implements Serializable {

    private String apiKey;
    private String usuario;

    public ApiSesionPK(String usuarioId) {
        this.usuario = usuarioId;
    }

    public ApiSesionPK(String apiKey, String usuarioId) {
        this.apiKey = apiKey;
        this.usuario = usuarioId;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ApiSesionPK that = (ApiSesionPK) o;

        if (apiKey != null ? !apiKey.equals(that.apiKey) : that.apiKey != null) return false;
        return usuario != null ? usuario.equals(that.usuario) : that.usuario == null;

    }

    
    public int hashCode() {
        int result = apiKey != null ? apiKey.hashCode() : 0;
        result = 31 * result + (usuario != null ? usuario.hashCode() : 0);
        return result;
    }
}
