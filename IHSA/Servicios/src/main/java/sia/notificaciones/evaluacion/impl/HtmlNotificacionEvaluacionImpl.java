/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.notificaciones.evaluacion.impl;

import javax.ejb.Stateless;
import javax.inject.Inject;
import sia.correo.impl.CodigoHtml;
import sia.modelo.CvConvenioEvaluacion;
import sia.modelo.CvEvaluacion;
import sia.modelo.SiPlantillaHtml;
import sia.servicios.campo.nuevo.impl.ApCampoGerenciaImpl;
import sia.servicios.campo.nuevo.impl.ApCampoUsuarioRhPuestoImpl;
import sia.servicios.sistema.impl.SiPlantillaHtmlImpl;

/**
 *
 * @author jcarranza
 */
@Stateless 
public class HtmlNotificacionEvaluacionImpl  extends CodigoHtml {
    @Inject
    private SiPlantillaHtmlImpl plantillaHtml;
    @Inject
    private ApCampoGerenciaImpl apCampoGerenciaRemote;
    @Inject
    private ApCampoUsuarioRhPuestoImpl apCampoUsuarioRhPuestoRemote;
    
    
    public StringBuilder nuevaEvaluacion(String asunto, CvConvenioEvaluacion conEva) {
        SiPlantillaHtml plantilla = this.plantillaHtml.find(1);

        limpiarCuerpoCorreo();
        cuerpoCorreo.append(plantilla.getInicio());
        cuerpoCorreo.append(getTitulo(asunto));
        cuerpoCorreo.append("<br/>");
        //Aquí va todo el contenido del cuerpo,
        cuerpoCorreo.append("<p>Se solicita una evaluación para el proveedor ")
                .append(conEva.getConvenio().getProveedor().getNombre())                
                .append(" para la gerencia ")
                .append(conEva.getGerencia().getNombre())
                .append(". En su desempeño en el contrato ")
                .append(conEva.getConvenio().getCodigo())
                .append(".</p>");
        //Lista de convenios
        
        // Fin
        cuerpoCorreo.append(plantilla.getFin());
        return cuerpoCorreo;
    }
    
    
    public StringBuilder respuestaEvaluacion(String asunto, CvEvaluacion evaluacion) {
    SiPlantillaHtml plantilla = this.plantillaHtml.find(1);

        limpiarCuerpoCorreo();
        cuerpoCorreo.append(plantilla.getInicio());
        cuerpoCorreo.append(getTitulo(asunto));
        cuerpoCorreo.append("<br/>");
        //Aquí va todo el contenido del cuerpo,
        cuerpoCorreo.append("<p>Se genero correctamente la evaluación del proveedor para el contrato ")
                .append(evaluacion.getConvenio().getCodigo())
                .append(" de la gerencia ")
                .append(evaluacion.getNombreGerencia())
                .append(".</p>");
        //Lista de convenios
        
        // Fin
        cuerpoCorreo.append(plantilla.getFin());
        return cuerpoCorreo;
    }   
}
