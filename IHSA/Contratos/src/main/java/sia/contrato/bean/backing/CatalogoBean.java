/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.contrato.bean.backing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;

import javax.faces.model.SelectItem;
import javax.inject.Named;
import org.primefaces.PrimeFaces;
import sia.contrato.bean.model.CatalogoModel;
import sia.contrato.bean.soporte.FacesUtils;
import sia.modelo.contrato.vo.ClasificacionVo;
import sia.modelo.contrato.vo.EvaluacionTemplateVo;
import sia.modelo.contrato.vo.PreguntaVo;
import sia.modelo.contrato.vo.SeccionVo;
import sia.modelo.documento.vo.DocumentoVO;
import sia.servicios.sistema.vo.CatalogoContratoVo;

/**
 *
 * @author ihsa
 */
@Named(value  = "deprecadoCatalogoBean")
public class CatalogoBean implements Serializable {

    static final long serialVersionUID = 1;

    /**
     * Creates a new instance of CatalogoBean
     */
    public CatalogoBean() {
    }

    @ManagedProperty(value = "#{catalogoModel}")
    private CatalogoModel catalogoModel;
    //

    public void limpiar() {
	catalogoModel.setIdVo(0);
	catalogoModel.setNombre("");
	catalogoModel.setDescripcion("");
	//cerrarDialogo("dialogoDocumento);
    }

    public void agregarCatalogo() {
	catalogoModel.setModificar(false);
    }

//////////////////////////////////////////////////////////////
    public void guardarDocto() {
	if (catalogoModel.guardarDocto()) {
	    FacesUtils.addInfoMessage("Se agregó el documento de contrato");
	    limpiar();
	}
	PrimeFaces.current().executeScript(";$(dialogoDocumento).modal('hide');;");
    }

    public void modificarDocto() {
	int param = Integer.parseInt(FacesUtils.getRequestParam("idDocto"));
	int idTipoDoc = Integer.parseInt(FacesUtils.getRequestParam("tipoDoc"));
	String nombre = FacesUtils.getRequestParam("nombre");
	String descr = FacesUtils.getRequestParam("descripcion");
	catalogoModel.setIdVo(param);
	catalogoModel.setNombre(nombre);
	catalogoModel.setDescripcion(descr);
	catalogoModel.setTipoDoc(idTipoDoc);
	PrimeFaces.current().executeScript(";mostrarDialogo(dialogoDocumentoMod);;");
    }

    public void completarModDocto() {
//	if (catalogoModel.modificarDocto()) {
//	    FacesUtils.addInfoMessage("No se puede modificar el documento de contrato, ya se usó para registrar un contrato");
//	}
//	PrimeFaces.current().executeScript(";$(dialogoDocumentoMod).modal('hide');;");
    }

//    public void eliminarDocto(int id) {
//	catalogoModel.setIdVo(id);
//	if (eliminarDocto()) {
//	    FacesUtils.addInfoMessage("No se puede eliminar el documento de contrato, ya se usó para registrar un contrato");
//	    catalogoModel.setIdVo(0);
//	}
//    }

    //
    public void guardarTipo() {
	if (catalogoModel.guardarTipo()) {
	    FacesUtils.addInfoMessage("Se agregó el tipo de contrato");
	    limpiar();
	    PrimeFaces.current().executeScript(";$(dialogoTipo).modal('hide');;");
	}
    }

    public void modificarTipo() {
	int param = Integer.parseInt(FacesUtils.getRequestParam("idTipo"));
	String nombre = FacesUtils.getRequestParam("nombre");
	String descr = FacesUtils.getRequestParam("descripcion");
	catalogoModel.setIdVo(param);
	catalogoModel.setNombre(nombre);
	catalogoModel.setDescripcion(descr);
	catalogoModel.setModificar(true);
	PrimeFaces.current().executeScript(";mostrarDialogo(dialogoTipoMod);;");
    }

//    public void completarModificaTipo() {
//	if (catalogoModel.modificarTipo()) {
//	    FacesUtils.addInfoMessage("No se puede modificar el tipo de contrato, ya se usó para registrar un contrato");
//	    limpiar();
//	}
//	PrimeFaces.current().executeScript(";mostrarDialogo(dialogoTipoMod);;");
//    }

//    public void eliminarTipo() {
//	int id = Integer.parseInt(FacesUtils.getRequestParam("idTipo"));
//	catalogoModel.setIdVo(id);
//	if (catalogoModel.eliminarTipo()) {
//	    FacesUtils.addInfoMessage("No se puede eliminar el tipo de contrato, ya se usó para registrar un contrato");
//	    limpiar();
//	}
//    }
////
//////////////////////////////////////////////////////////////

    public void guardarHito() {
	if (catalogoModel.guardarHito()) {
	    FacesUtils.addInfoMessage("Se agregó el hito de contrato");
	    limpiar();
	    PrimeFaces.current().executeScript(";$(dialogoHito).modal('hide');;");
	}
    }

    public void modificarHito() {
	int id = Integer.parseInt(FacesUtils.getRequestParam("idHito"));
	String nombre = FacesUtils.getRequestParam("nombre");
	String descr = FacesUtils.getRequestParam("descripcion");
	catalogoModel.setIdVo(id);
	catalogoModel.setNombre(nombre);
	catalogoModel.setDescripcion(descr);
	catalogoModel.setModificar(true);
	PrimeFaces.current().executeScript(";mostrarDialogo(dialogoHitoMod);");
    }

//    public void completarModHito() {
//	if (catalogoModel.modificarHito()) {
//	    FacesUtils.addInfoMessage("No se puede modificar el hito de contrato, ya se usó para registrar un contrato");
//	    limpiar();
//	}
//	PrimeFaces.current().executeScript(";$(dialogoHitoMod).modal('hide');;");
//    }
//
//    public void eliminarHito() {
//	int id = Integer.parseInt(FacesUtils.getRequestParam("idHito"));
//	catalogoModel.setIdVo(id);
//	if (catalogoModel.eliminarHito()) {
//	    FacesUtils.addInfoMessage("No se puede eliminar el hito de contrato, ya se usó para registrar un contrato");
//	    limpiar();
//	}
//    }

////////////////////////////////////////////////////////////// clasificacion
    public void agregarHijoClasificacion() {
	catalogoModel.setDescripcion(FacesUtils.getRequestParam("desc"));
	catalogoModel.setModificar(false);
	PrimeFaces.current().executeScript(";$(dialogoClasi).modal('show');;");
    }

    public void buscarCategoriaPorId() {
	catalogoModel.setIndice(Integer.parseInt(FacesUtils.getRequestParam("indice")));
	catalogoModel.setIdVo(Integer.parseInt(FacesUtils.getRequestParam("idClas")));
	catalogoModel.traerClasificacion();
    }

    public void guardarClasificacion() {
//	if (catalogoModel.guardarClasificacion()) {
//	    FacesUtils.addInfoMessage("Se agregó la calsificación de contrato");
//	    limpiar();
//
//	    PrimeFaces.current().executeScript(";$(dialogoClasi).modal('hide');;");
//	}
    }

    public void modificarClasificacion() {
	int id = Integer.parseInt(FacesUtils.getRequestParam("idClasificacion"));
	String nombre = FacesUtils.getRequestParam("nombre");
	String descr = FacesUtils.getRequestParam("descripcion");
	catalogoModel.setIdVo(id);
	catalogoModel.setNombre(nombre);
	catalogoModel.setDescripcion(descr);
	catalogoModel.setModificar(true);
	PrimeFaces.current().executeScript(";mostrarDialogo(dialogoClasiMod);");
    }

//    public void completarModClasifica() {
//	if (catalogoModel.modificarClasificacion()) {
//	    FacesUtils.addInfoMessage("No se puede modificar la clasificación de contrato, ya se usó para registrar un contrato");
//	    limpiar();
//	}
//	PrimeFaces.current().executeScript(";$(dialogoClasiMod).modal('hide');;");
//    }

    public void eliminarClasificacion() {
	int id = Integer.parseInt(FacesUtils.getRequestParam("idClasificacion"));
	catalogoModel.setIdVo(id);
	if (catalogoModel.eliminarClasificacion()) {
	    FacesUtils.addInfoMessage("No se puede eliminar la clasificicación de contrato, ya se usó para registrar un contrato");
	    limpiar();
	}
	PrimeFaces.current().executeScript(";$(dialogoClasi).modal('hide');;");
    }

//////////////////////////////////////////////////////////////
    public void guardarCondicion() {
	if (catalogoModel.guardarCondicion()) {
	    FacesUtils.addInfoMessage("Se agregó la condición de pago de contrato");
	    limpiar();
	    PrimeFaces.current().executeScript(";$(dialogoCondicion).modal('hide');;");
	}
    }

    public void modificarCondicion() {
	int id = Integer.parseInt(FacesUtils.getRequestParam("idCondicion"));
	String nombre = FacesUtils.getRequestParam("nombre");
	String descr = FacesUtils.getRequestParam("descripcion");
	catalogoModel.setIdVo(id);
	catalogoModel.setNombre(nombre);
	catalogoModel.setDescripcion(descr);
	catalogoModel.setModificar(true);
	PrimeFaces.current().executeScript(";mostrarDialogo(dialogoCondicionMod);");
    }

//    public void completarModCondicion() {
//	if (catalogoModel.modificarCondicion()) {
//	    FacesUtils.addInfoMessage("No se puede modificar la condición de pago de contrato, ya se usó para registrar un contrato");
//	    limpiar();
//	}
//	PrimeFaces.current().executeScript(";$(dialogoCondicionMod).modal('hide');;");
//    }

    public void eliminarCondicion() {
	int id = Integer.parseInt(FacesUtils.getRequestParam("idCondicion"));
	catalogoModel.setIdVo(id);
	if (catalogoModel.eliminarCondicion()) {
	    FacesUtils.addInfoMessage("No se puede eliminar la condición de pago de contrato, ya se usó para registrar un contrato");
	    limpiar();
	}
    }////////////////////////////////////////

    /**
     * @return the documentos
     */
    public List<DocumentoVO> getDocumentos() {
	return catalogoModel.getDocumentos();
    }

    /**
     * @return the clasificaion
     */
    public ClasificacionVo getClasificaion() {
	return catalogoModel.getClasificaion();
    }

    /**
     * @return the hitos
     */
    public List<CatalogoContratoVo> getHitos() {
	return catalogoModel.getHitos();
    }

    /**
     * @return the condiciones
     */
    public List<CatalogoContratoVo> getCondiciones() {
	return catalogoModel.getCondiciones();
    }

    /**
     * @return the tipos
     */
    public List<CatalogoContratoVo> getTipos() {
	return catalogoModel.getTipos();
    }

    /**
     * @param catalogoModel the catalogoModel to set
     */
    public void setCatalogoModel(CatalogoModel catalogoModel) {
	this.catalogoModel = catalogoModel;
    }

    /**
     * @return the nombre
     */
    public String getNombre() {
	return catalogoModel.getNombre();
    }

    /**
     * @param nombre the nombre to set
     */
    public void setNombre(String nombre) {
	catalogoModel.setNombre(nombre);
    }

    /**
     * @return the descripcion
     */
    public String getDescripcion() {
	return catalogoModel.getDescripcion();
    }

    /**
     * @param descripcion the descripcion to set
     */
    public void setDescripcion(String descripcion) {
	catalogoModel.setDescripcion(descripcion);
    }

    /**
     * @return the idVo
     */
    public int getIdVo() {
	return catalogoModel.getIdVo();
    }

    /**
     * @param idVo the idVo to set
     */
    public void setIdVo(int idVo) {
	catalogoModel.setIdVo(idVo);
    }

    /**
     * @return the modificar
     */
    public boolean isModificar() {
	return catalogoModel.isModificar();
    }

    /**
     * @param modificar the modificar to set
     */
    public void setModificar(boolean modificar) {
	catalogoModel.setModificar(modificar);
    }

    /**
     * @return the indice
     */
    public int getIndice() {
	return catalogoModel.getIndice();
    }

    /**
     * @param indice the indice to set
     */
    public void setIndice(int indice) {
	catalogoModel.setIndice(indice);
    }

    /**
     * @return the totalHijos
     */
    public int getTotalHijos() {
	return catalogoModel.getTotalHijos();
    }

    /**
     * @param totalHijos the totalHijos to set
     */
    public void setTotalHijos(int totalHijos) {
	catalogoModel.setTotalHijos(totalHijos);
    }

    /**
     * @return the listaClasificaion
     */
    public List<ClasificacionVo> getListaClasificaion() {
	return catalogoModel.getListaClasificaion();
    }

    /**
     * @return the tipoDoc
     */
    public int getTipoDoc() {
	return catalogoModel.getTipoDoc();
    }

    /**
     * @param tipoDoc the tipoDoc to set
     */
    public void setTipoDoc(int tipoDoc) {
	this.catalogoModel.setTipoDoc(tipoDoc);
    }

    /**
     * @return the lstTipoDoc
     */
    public List<SelectItem> getLstTipoDoc() {
	return catalogoModel.getLstTipoDoc();
    }

    /**
     * @param lstTipoDoc the lstTipoDoc to set
     */
    public void setLstTipoDoc(List<SelectItem> lstTipoDoc) {
	this.catalogoModel.setLstTipoDoc(lstTipoDoc);
    }
    
    /**
     * @return the formatos
     */
    public List<EvaluacionTemplateVo> getFormatos() {
        return this.catalogoModel.getFormatos();
    }

    /**
     * @param formatos the formatos to set
     */
    public void setFormatos(List<EvaluacionTemplateVo> formatos) {
        this.catalogoModel.setFormatos(formatos);
    }

    ///////////////////////Formatos
    
    public void agregarFormato() {
	this.catalogoModel.setNuevoFormato(new EvaluacionTemplateVo());
        catalogoModel.cargarClasificacionPrincipal();
        PrimeFaces.current().executeScript(";$(dialogoFormato).modal('show');;");
    }
    
    public void agregarSeccion() {
	this.catalogoModel.setNuevaSeccion(new SeccionVo());
    }
    
    public void agregarPregunta() {
	this.catalogoModel.setNuevaPregunta(new PreguntaVo());
    }
    
    public void guardarFormato() {
	if (catalogoModel.guardarFormato()) {
	    FacesUtils.addInfoMessage("Se agregó el nuevo formato de evaluación correctamente. ");	    
	    PrimeFaces.current().executeScript(";$(dialogoFormato).modal('hide');;");
	}
    }

    public void modificarFormato() {
	int id = Integer.parseInt(FacesUtils.getRequestParam("idFormato"));        
        int indice = Integer.parseInt(FacesUtils.getRequestParam("idIndice"));
	catalogoModel.setIdFormato(id);	
        catalogoModel.cargarClasificacionPrincipal();
        if(indice > -1){
            catalogoModel.setNuevoFormato(catalogoModel.getFormatos().get(indice));
        }	
	PrimeFaces.current().executeScript(";mostrarDialogo(dialogoFormato);");
    }
    
    public void guardarSeccion() {
	if (catalogoModel.guardarSeccion()) {
	    FacesUtils.addInfoMessage("Se agregó correctamente la sección al formato de evaluación.");	    
	    PrimeFaces.current().executeScript(";$(dialogoSeccion).modal('hide');;");
	}
    }

    public void modificarSeccion() {
	int id = Integer.parseInt(FacesUtils.getRequestParam("idSeccion"));        
        int indice = Integer.parseInt(FacesUtils.getRequestParam("idIndice"));
	catalogoModel.setIdSeccion(id);	
	if(indice > -1){
            catalogoModel.setNuevaSeccion(catalogoModel.getSecciones().get(indice));
        }
	PrimeFaces.current().executeScript(";mostrarDialogo(dialogoSeccion);");
    }
    
    public void guardarPregunta() {
	if (catalogoModel.guardarPregunta()) {
	    FacesUtils.addInfoMessage("Se agregó correctamente la pregunta al formato de evaluación.");	    
	    PrimeFaces.current().executeScript(";$(dialogoPregunta).modal('hide');;");
	}
    }

    public void modificarPregunta() {
	int id = Integer.parseInt(FacesUtils.getRequestParam("idPregunta")); 
        int indice = Integer.parseInt(FacesUtils.getRequestParam("idIndice"));
	catalogoModel.setIdPregunta(id);	
	if(indice > -1){
            catalogoModel.setNuevaPregunta(catalogoModel.getPreguntas().get(indice));
        }
	PrimeFaces.current().executeScript(";mostrarDialogo(dialogoPregunta);");
    }
    
    public void seleccionarFormato() {
	int id = Integer.parseInt(FacesUtils.getRequestParam("idFormato")); 
        int indice = Integer.parseInt(FacesUtils.getRequestParam("idIndice"));
	catalogoModel.setIdFormato(id);	
	catalogoModel.cargarSecciones();
        if(indice > -1 && this.catalogoModel.getFormatos().size() > 0){
            this.setTituloFormato(this.catalogoModel.getFormatos().get(indice).getNombre());
        }
        PrimeFaces.current().executeScript(";ocultarDiv('panelFormatos');mostrarDiv('panelSecciones');");
    }
    
    public void regresarSeccion() {	
        catalogoModel.setIdFormato(0);	
	catalogoModel.setSecciones(new ArrayList<SeccionVo>());
	catalogoModel.setIdSeccion(0);
        catalogoModel.setNuevaSeccion(new SeccionVo());	
        this.setTituloFormato(null);
        PrimeFaces.current().executeScript(";ocultarDiv('panelSecciones');mostrarDiv('panelFormatos');");
    }
    
    public void seleccionarSeccion() {
	int id = Integer.parseInt(FacesUtils.getRequestParam("idSeccion"));
        int indice = Integer.parseInt(FacesUtils.getRequestParam("idIndice"));        
	catalogoModel.setIdSeccion(id);	
	catalogoModel.cargarPreguntas();
        if(indice > -1 && this.catalogoModel.getSecciones().size() > 0){
            this.setTituloSeccion(this.catalogoModel.getSecciones().get(indice).getSeccionNombre());
        }
        PrimeFaces.current().executeScript(";ocultarDiv('panelSecciones');mostrarDiv('panelPregunta');");
    }
    
    public void regresarPregunta() {	
	catalogoModel.setIdSeccion(0);	
        catalogoModel.setIdPregunta(0);
        catalogoModel.setNuevaPregunta(new PreguntaVo());
	catalogoModel.setPreguntas(new ArrayList<PreguntaVo>());
        this.setTituloSeccion(null);
        PrimeFaces.current().executeScript(";ocultarDiv('panelPregunta');mostrarDiv('panelSecciones');");
    }

    public void completarModFormato() {
	if (catalogoModel.guardarFormato()) {
	    FacesUtils.addInfoMessage("No se puede modificar el formato de evaluación.");	    
	}
	PrimeFaces.current().executeScript(";$(dialogoFormato).modal('hide');;");
    }

    public void eliminarFormato() {
	int id = Integer.parseInt(FacesUtils.getRequestParam("idFormato"));
	catalogoModel.setIdFormato(id);
	if (catalogoModel.eliminarFormato()) {
	    FacesUtils.addInfoMessage("No se puede eliminar el formato de evaluación.");	    
	}
    }
    
    public void completarModSeccion() {
	if (catalogoModel.guardarSeccion()) {
	    FacesUtils.addInfoMessage("No se puede modificar la sección del formato de evaluación.");	    
	}
	PrimeFaces.current().executeScript(";$(dialogoSeccion).modal('hide');;");
    }

//    public void eliminarSeccion() {
//	int id = Integer.parseInt(FacesUtils.getRequestParam("idSeccion"));
//	catalogoModel.setIdSeccion(id);
//	if (catalogoModel.eliminarSeccion()) {
//	    FacesUtils.addInfoMessage("No se puede eliminar la sección del formato de evaluación.");	    
//	}
//    }
    
    public void completarModPregunta() {
	if (catalogoModel.guardarPregunta()) {
	    FacesUtils.addInfoMessage("No se puede modificar la pregunta del formato de evaluación.");	    
	}
	PrimeFaces.current().executeScript(";$(dialogoPregunta).modal('hide');;");
    }

    public void eliminarPregunta() {
	int id = Integer.parseInt(FacesUtils.getRequestParam("idPregunta"));
	catalogoModel.setIdPregunta(id);
	if (catalogoModel.eliminarPregunta()) {
	    FacesUtils.addInfoMessage("No se puede eliminar la pregunta del formato de evaluación");	    
	}
    }
    
    /**
     * @return the preguntas
     */
    public List<PreguntaVo> getPreguntas() {
        return this.catalogoModel.getPreguntas();
    }

    /**
     * @param preguntas the preguntas to set
     */
    public void setPreguntas(List<PreguntaVo> preguntas) {
        this.catalogoModel.setPreguntas(preguntas);
    }

    /**
     * @return the secciones
     */
    public List<SeccionVo> getSecciones() {
        return this.catalogoModel.getSecciones();
    }

    /**
     * @param secciones the secciones to set
     */
    public void setSecciones(List<SeccionVo> secciones) {
        this.catalogoModel.setSecciones(secciones);
    }
    
    /**
     * @return the nuevoFormato
     */
    public EvaluacionTemplateVo getNuevoFormato() {
        return this.catalogoModel.getNuevoFormato();
    }

    /**
     * @param nuevoFormato the nuevoFormato to set
     */
    public void setNuevoFormato(EvaluacionTemplateVo nuevoFormato) {
        this.catalogoModel.setNuevoFormato(nuevoFormato);
    }

    /**
     * @return the nuevaPregunta
     */
    public PreguntaVo getNuevaPregunta() {
        return this.catalogoModel.getNuevaPregunta();
    }

    /**
     * @param nuevaPregunta the nuevaPregunta to set
     */
    public void setNuevaPregunta(PreguntaVo nuevaPregunta) {
        this.catalogoModel.setNuevaPregunta(nuevaPregunta);
    }

    /**
     * @return the nuevaSeccion
     */
    public SeccionVo getNuevaSeccion() {
        return this.catalogoModel.getNuevaSeccion();
    }

    /**
     * @param nuevaSeccion the nuevaSeccion to set
     */
    public void setNuevaSeccion(SeccionVo nuevaSeccion) {
        this.catalogoModel.setNuevaSeccion(nuevaSeccion);
    }
    
    /**
     * @return the clasificaciones
     */
    public List<ClasificacionVo> getClasificaciones() {
        return this.catalogoModel.getClasificaciones();
    }

    /**
     * @param clasificaciones the clasificaciones to set
     */
    public void setClasificaciones(List<ClasificacionVo> clasificaciones) {
        this.catalogoModel.setClasificaciones(clasificaciones);
    }
    
    /**
     * @return the tituloFormato
     */
    public String getTituloFormato() {
        return this.catalogoModel.getTituloFormato();
    }

    /**
     * @param tituloFormato the tituloFormato to set
     */
    public void setTituloFormato(String tituloFormato) {
        this.catalogoModel.setTituloFormato(tituloFormato);
    }

    /**
     * @return the tituloSeccion
     */
    public String getTituloSeccion() {
        return this.catalogoModel.getTituloSeccion();
    }

    /**
     * @param tituloSeccion the tituloSeccion to set
     */
    public void setTituloSeccion(String tituloSeccion) {
        this.catalogoModel.setTituloSeccion(tituloSeccion);
    }
}
