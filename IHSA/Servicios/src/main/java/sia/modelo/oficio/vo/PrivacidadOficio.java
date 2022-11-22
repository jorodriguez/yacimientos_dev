
package sia.modelo.oficio.vo;

import javax.faces.event.ValueChangeEvent;
/**
 * Define los valores válidos de privacidad de un oficio. Los valores numéricos
 * corresponden a los ID existentes aplicables en la tabla CO_privacidad (sic).
 *
 * @author esapien
 */
public enum PrivacidadOficio {
    
    GERENCIA(4), PUBLICO(1), RESTRINGIDO(5);
    
    public final static int ID_GERENCIA=4;
    public final static int ID_PUBLICO = 1;
    public final static int ID_RESTRINGIDO = 5;
    private int id;

    private PrivacidadOficio(final int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
    
    /**
     * 
     * @param id
     * @return 
     */
    public static PrivacidadOficio getPrivacidadOficio(final int id) {
        
        PrivacidadOficio resultado;
        
        switch(id) {
            
            case ID_GERENCIA:
                resultado = GERENCIA;
                break;
            case ID_PUBLICO: 
                resultado = PUBLICO;
                break;
            case ID_RESTRINGIDO: 
                resultado = RESTRINGIDO;
                break;
               
            default:
                throw new IllegalArgumentException();
            
        }
        
        return resultado;
        
    }
    
    
    public boolean isRestringido() {
        return id == ID_RESTRINGIDO;
    }
       
      public void validarRestringido (ValueChangeEvent v){
        if(v != null){
            id=Integer.parseInt(v.getNewValue().toString());
        }
    }
      public boolean isPublico() {
        return id == ID_PUBLICO;
    }
}
