package sia.modelo.usuario.vo;

/**
 * VO que representa el resultado de autenticación para Rest endpoints del api de aplicaciones
 * moviles
 *
 * @author Aplimovil SA de CV
 */
public class ApiSessionVO {
    private String id;
    private String nombre;
    private String apiKey;

    public ApiSessionVO() {
    }

    public ApiSessionVO(String id, String nombre, String apiKey) {
        this.id = id;
        this.nombre = nombre;
        this.apiKey = apiKey;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }
}
