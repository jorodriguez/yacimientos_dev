

package sia.util.ui.bootstrap;

/**
 * 
 * Clase auxiliar para la configuración de un elemento <div> para mostrar una 
 * alerta de Bootstrap en pantalla.
 * 
 * Ejemplo de uso en archivo XHTML:
 * 
    <div class="alert alert-danger alert-dismissible fade in" role="alert">
        <button type="button" class="close" data-dismiss="alert"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>
        <strong>Warning!</strong> Better check yourself, you're not looking too good.
    </div>
 * 
 * 
 * @author esapien
 */
public final class Alert {
    
    /**
     * Clase de CSS de Bootstrap para mensajes de confirmacion de una acción 
     * correcta. Muestra en color verde.
     */
    public static final String CSS_CLASS_SUCCESS = "alert-success";
    
    /**
     * Clase de CSS de Bootstrap para un mensaje informativo.
     * Muestra en color azul.
     */
    public static final String CSS_CLASS_INFO = "alert-info";
    
    /**
     * Clase de CSS de Bootstrap para un mensaje de advertencia. 
     * Muestra en color amarillo.
     */
    public static final String CSS_CLASS_WARNING = "alert-warning";
    
    /**
     * Clase de CSS de Bootstrap para mensajes de error. 
     * Muestra en color rojo.
     */
    public static final String CSS_CLASS_DANGER = "alert-danger";
    
    /**
     * Clase de CSS de Bootstrap para que la caja de alerta 
     * se pueda remover con click.
     */
    private static final String CSS_CLASS_DISMISSIBLE = "alert-dismissible";
    
    /**
     * Clases de CSS de Bootstrap para agregar efecto de desvanecimiento 
     * al <div>.
     */
    private static final String CSS_CLASS_FADE_IN = "fade in";
    
    /**
     * Clase de CSS de Bootstrap base para una caja de alerta. Se incluye 
     * siempre como primera clase de CSS.
     * 
     */
    private static final String CSS_CLASS_ALERT = "alert";
    
    private static final String EMPTY_STRING = "";
    
    private static final String BLANK_STRING = " ";
    
    
    
    private String message;
    
    private String cssClass;
    
    private boolean dismissible;
    
    private String cssClassDismissible;
    
    private String cssClassFadeIn;
    
    private boolean rendered;
    

    /**
     * Constructor
     */
    public Alert() {
        
        // valores por defecto
        setCssClass(CSS_CLASS_INFO);
        setDismissible(true);
        setCssClassFadeIn(true);
        setRendered(false);
    }
    

    /**
     * Para establecer la clase de Bootstrap deseada para el elemento de alerta:
     * Success, Info, Warning o Danger.
     * 
     * @param cssClass 
     */
    public void setCssClass(String cssClass) {
        this.cssClass = cssClass;
    }
    

    public boolean isDismissible() {
        return dismissible;
    }
    
    /**
     * 
     * @param dismissible 
     */
    public void setDismissible(boolean dismissible) {
        
        this.dismissible = dismissible;
        this.cssClassDismissible = dismissible ? CSS_CLASS_DISMISSIBLE : EMPTY_STRING;
        
    }
    
    /**
     * 
     * @param fadeIn 
     */
    public void setCssClassFadeIn(boolean fadeIn) {
        
        this.cssClassFadeIn = fadeIn ? CSS_CLASS_FADE_IN : EMPTY_STRING;
        
    }
    
    

    
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    
    

    public boolean isRendered() {
        return rendered;
    }

    public void setRendered(boolean rendered) {
        this.rendered = rendered;
    }
    
    
    
    
    /**
     * 
     * Regresa la cadena de clases CSS de Bootstrap configurada
     * para el despliegue deseado del elemento de Alert.
     * 
     * Ejemplo de resultado: "alert alert-danger alert-dismissible fade in"
     * 
     * Ejemplo de uso:
     * 
        <div class="alert alert-danger alert-dismissible fade in" role="alert">
                ...
        </div>
     * 
     * @return 
     */
    public String getCssClass() {
        
        StringBuilder sb = new StringBuilder();
        
        sb.append(CSS_CLASS_ALERT);
        sb.append(BLANK_STRING);
        sb.append(cssClass);
        sb.append(BLANK_STRING);
        sb.append(cssClassDismissible);
        sb.append(BLANK_STRING);
        sb.append(cssClassFadeIn);
        
        return sb.toString();
        
    }
    
    
    
}
