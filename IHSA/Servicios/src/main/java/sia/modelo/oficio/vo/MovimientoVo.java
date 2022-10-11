
package sia.modelo.oficio.vo;

import sia.constantes.Constantes;
import sia.modelo.sgl.vo.Vo;

/**
 * Contiene la información de un movimiento del oficio.
 * 
 * @author esapien
 */
public class MovimientoVo extends Vo {
    
    private int oficioMovimientoId;
    
    private int oficioId;
    private String oficioNumero;
    
    private int operacionId;
    private String operacion;
    
    private String motivo;
    
    private AdjuntoOficioVo adjunto;
    
    public AdjuntoOficioVo getAdjunto() {
        return adjunto;
    }
    
    
    
    /**
     * 
     * @return 
     */
    public boolean tieneAdjunto() {
        
        boolean result = adjunto != null 
                && adjunto.getNombre() != null 
                && !adjunto.getNombre().trim().equalsIgnoreCase(Constantes.NULL)
                && adjunto.getNombre().trim().length() > 0;
        
        return result;
        
    }
    
    public void setAdjunto(AdjuntoOficioVo adjunto) {
        this.adjunto = adjunto;
    }
    
    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public String getOperacion() {
        return operacion;
    }

    public void setOperacion(String operacion) {
        this.operacion = operacion;
    }

    public int getOperacionId() {
        return operacionId;
    }

    public void setOperacionId(int operacionId) {
        this.operacionId = operacionId;
    }

    public int getOficioId() {
        return oficioId;
    }

    public void setOficioId(int oficioId) {
        this.oficioId = oficioId;
    }

    public int getOficioMovimientoId() {
        return oficioMovimientoId;
    }

    public void setOficioMovimientoId(int oficioMovimientoId) {
        this.oficioMovimientoId = oficioMovimientoId;
    }

    public String getOficioNumero() {
        return oficioNumero;
    }

    public void setOficioNumero(String oficioNumero) {
        this.oficioNumero = oficioNumero;
    }
    
    public boolean isAdjuntoEliminado() {
        
        return tieneAdjunto() && getAdjunto().isEliminado();
        
    }
    
    
    
    
    
    /**
     * Regresa el valor de fecha y hora del movimiento en formato para
     * su despliegue en interfaz de usuario.
     * 
     * @return 
     */
    public String getFechaMovimiento() {
        
        String fecha = Constantes.FMT_ddMMyyy.format(this.getFechaGenero());
        String hora = Constantes.FMT_hmm_a.format(this.getHoraGenero());
        
        StringBuilder sb = new StringBuilder();
        sb.append(fecha).append(Constantes.BLANCO).append(hora);
        
        return sb.toString();
        
    }
    
    
    /**
     * 
     * @return 
     */
    
    public String toString() {
        
        StringBuilder sb = new StringBuilder();
        
        sb.append("{operacionId = ").append(this.operacionId);
        sb.append(", operacion = ").append(this.operacion);
        sb.append(", motivo = ").append(this.motivo);
        sb.append(", adjunto = ").append(this.adjunto);
        sb.append("}");
        
        return sb.toString();
        
    }
    
}
