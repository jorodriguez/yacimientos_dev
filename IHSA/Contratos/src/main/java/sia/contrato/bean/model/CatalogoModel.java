/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.contrato.bean.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedProperty;
import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.PrimeFaces;
import sia.constantes.Constantes;
import sia.contrato.bean.soporte.FacesUtils;
import sia.ihsa.contratos.Sesion;
import sia.modelo.contrato.vo.ClasificacionVo;
import sia.modelo.contrato.vo.EvaluacionTemplateVo;
import sia.modelo.contrato.vo.PreguntaVo;
import sia.modelo.contrato.vo.SeccionVo;
import sia.modelo.documento.vo.DocumentoVO;
import sia.modelo.sgl.vo.Vo;
import sia.servicios.convenio.impl.CvClasificacionImpl;
import sia.servicios.convenio.impl.CvCondicionPagoImpl;
import sia.servicios.convenio.impl.CvHitoImpl;
import sia.servicios.convenio.impl.CvTipoImpl;
import sia.servicios.evaluacion.impl.CvEvaluacionSeccionImpl;
import sia.servicios.evaluacion.impl.CvEvaluacionTemplateDetImpl;
import sia.servicios.evaluacion.impl.CvEvaluacionTemplateImpl;
import sia.servicios.proveedor.impl.PvDocumentoImpl;
import sia.servicios.sistema.impl.SiListaElementoImpl;
import sia.servicios.sistema.vo.CatalogoContratoVo;
import sia.util.UtilLog4j;

/**
 *
 * @author ihsa
 */
@Named(value = "catalogoBean")
@ViewScoped
public class CatalogoModel implements Serializable {

    static final long serialVersionUID = 1;

    /**
     * Creates a new instance of CatalogoModel
     */
    public CatalogoModel() {
    }
    @Inject
    private Sesion sesion;

    @Inject
    private CvTipoImpl cvTipoImpl;
    @Inject
    private PvDocumentoImpl pvDocumentoImpl;
    @Inject
    private CvClasificacionImpl cvClasificacionImpl;
    @Inject
    private CvCondicionPagoImpl cvCondicionPagoImpl;
    @Inject
    private CvHitoImpl cvHitoImpl;
    @Inject
    private SiListaElementoImpl siListaElementoImpl;
    @Inject
    private CvEvaluacionTemplateImpl cvEvaluacionTemplateImpl;
    @Inject
    private CvEvaluacionSeccionImpl cvEvaluacionSeccionImpl;
    @Inject
    private CvEvaluacionTemplateDetImpl cvEvaluacionTemplateDetImpl;

    private List<DocumentoVO> documentos;
    private ClasificacionVo clasificaion;
    private List<ClasificacionVo> listaClasificaion = new ArrayList<ClasificacionVo>();
    private List<CatalogoContratoVo> hitos;
    private List<CatalogoContratoVo> condiciones;
    private List<CatalogoContratoVo> tipos;
    //
    private int indice;
    private int idVo;
    private String nombre;
    private String descripcion;
    private int tipoDoc;
    private List<SelectItem> lstTipoDoc;
    private boolean modificar;
    private int totalHijos = 0;

    private EvaluacionTemplateVo nuevoFormato;
    private PreguntaVo nuevaPregunta;
    private SeccionVo nuevaSeccion;
    private List<EvaluacionTemplateVo> formatos;
    private List<PreguntaVo> preguntas;
    private List<SeccionVo> secciones;
    private int idFormato;
    private int idSeccion;
    private int idPregunta;
    private List<ClasificacionVo> clasificaciones;
    private String tituloFormato;
    private String tituloSeccion;

    @PostConstruct
    public void iniciar() {
        tipos = cvTipoImpl.traerTodo();
        documentos = pvDocumentoImpl.traerDocumento(Constantes.LISTA_TIPO_DOCUMENTO + ", " + Constantes.LISTA_TIPO_IDENTIFICACION);
        lstTipoDoc = siListaElementoImpl.getListaElementos("Tipo de Documento", 0);
        //List<ClasificacionVo> l = cvClasificacionImpl.traerClasificacionPrincipal();
        llenarClasificacion();
        condiciones = cvCondicionPagoImpl.traerTodo();
        hitos = cvHitoImpl.traerTodo();
        this.setFormatos(cvEvaluacionTemplateImpl.traerTemplatePorTipo(0, sesion.getRfcEmpresa()));
    }

    private void llenarClasificacion() {
        listaClasificaion = new ArrayList<>();
        List<ClasificacionVo> c = cvClasificacionImpl.traerClasificaciones();
        for (ClasificacionVo clasificaion1 : c) {
            clasificaion = new ClasificacionVo();
            String cadena = "";
            String cadenaDescr = "";

            String[] cd = clasificaion1.getNombre().split("->");
            // String principal = "";
            //
            for (String cd1 : cd) {
                String[] n = cd1.split("-");
                //principal = n[0];
                //
                if (cadenaDescr.isEmpty()) {
                    cadenaDescr = n[0];
                } else {
                    cadenaDescr += "--" + n[0];
                }
                if (cadena.isEmpty()) {
                    cadena = n[1];
                } else {
                    cadena += " --> " + n[1];
                }
            }
            clasificaion.setNombre(cadena);
            clasificaion.setDescripcion(cadenaDescr);
            listaClasificaion.add(clasificaion);
        }
    }

    public void limpiar() {
        setIdVo(0);
        setNombre("");
        setDescripcion("");
        //cerrarDialogo("dialogoDocumento);
    }
////////////////////////////////////////////

    public boolean guardarDocto() {
        boolean v = true;
        try {
            pvDocumentoImpl.guardar(sesion.getUsuarioSesion().getId(), getNombre(), getDescripcion(), getTipoDoc());
            documentos = pvDocumentoImpl.traerDocumento(Constantes.LISTA_TIPO_DOCUMENTO + ", " + Constantes.LISTA_TIPO_IDENTIFICACION);
            PrimeFaces.current().executeScript(";$(dialogoDocumento).modal('hide');;");
        } catch (Exception e) {
            v = false;
            UtilLog4j.log.fatal(e);
        }
        return v;
    }

    public Vo buscarDoctoPorId(int idVo) {
        Vo vo = pvDocumentoImpl.buscarPorId(idVo);
        if (vo != null) {
            setIdVo(vo.getId());
            setNombre(vo.getNombre());
            setDescripcion(vo.getDescripcion());
            vo.setModificar(true);
        }

        return vo;
    }

    public void modificarDocto(int param, int idTipoDoc, String nombre, String descr) {
        setIdVo(param);
        setNombre(nombre);
        setDescripcion(descr);
        setTipoDoc(idTipoDoc);

    }

    public void completarModDocto() {
        boolean v = pvDocumentoImpl.isUsado(getIdVo());
        if (!v) {
            try {
                pvDocumentoImpl.modificar(sesion.getUsuarioSesion().getId(), getIdVo(), getNombre(), getDescripcion(), getTipoDoc());
                documentos = pvDocumentoImpl.traerDocumento(Constantes.LISTA_TIPO_DOCUMENTO + ", " + Constantes.LISTA_TIPO_IDENTIFICACION);
                PrimeFaces.current().executeScript(";$(dialogoDocumentoMod).modal('hide');");
            } catch (Exception e) {
                v = false;
                UtilLog4j.log.fatal(e);
            }
        }
        setNombre("");
        setDescripcion("");
    }

    public void eliminarDocto(int id) {
        idVo = id;
        boolean v = pvDocumentoImpl.isUsado(id);
        if (!v) {
            try {
                pvDocumentoImpl.eliminar(sesion.getUsuarioSesion().getId(), getIdVo());
                documentos = pvDocumentoImpl.traerDocumento(Constantes.LISTA_TIPO_DOCUMENTO + ", " + Constantes.LISTA_TIPO_IDENTIFICACION);
            } catch (Exception e) {
                v = false;
                UtilLog4j.log.fatal(e);
            }
            documentos = pvDocumentoImpl.traerDocumento(Constantes.LISTA_TIPO_DOCUMENTO + ", " + Constantes.LISTA_TIPO_IDENTIFICACION);
        } else {
            FacesUtils.addInfoMessage("No se puede eliminar el documento, porque ya se uso en el sistema");
        }
    }
/////////////////////////////////////////////////////////

    public boolean guardarTipo() {
        boolean v = true;
        try {
            cvTipoImpl.guardar(sesion.getUsuarioSesion().getId(), getNombre(), getDescripcion());
            tipos = cvTipoImpl.traerTodo();
            PrimeFaces.current().executeScript(";$(dialogoTipo).modal('hide');;");
        } catch (Exception e) {
            v = false;
            UtilLog4j.log.fatal(e);
        }
        return v;
    }

    public Vo buscarTipoPorId(int idVo) {
        Vo vo = cvTipoImpl.buscarPorId(idVo);
        if (vo != null) {
            setIdVo(vo.getId());
            setNombre(vo.getNombre());
            setDescripcion(vo.getDescripcion());
            vo.setModificar(true);
        }

        return vo;
    }

    public void modificarTipo(int param, String nombre, String descr) {
        setIdVo(param);
        setNombre(nombre);
        setDescripcion(descr);
        setModificar(true);
        PrimeFaces.current().executeScript(";mostrarDialogo(dialogoTipoMod);;");
    }

    public void completarModificaTipo() {
        boolean v = cvTipoImpl.isUsado(getIdVo());
        if (!v) {
            try {
                cvTipoImpl.modificar(sesion.getUsuarioSesion().getId(), getIdVo(), getNombre(), getDescripcion());
                tipos = cvTipoImpl.traerTodo();
                PrimeFaces.current().executeScript("$(dialogoTipoMod).modal('hide');");
            } catch (Exception e) {
                v = false;
                UtilLog4j.log.fatal(e);
            }
        }
    }

    public void eliminarTipo(int id) {
        idVo = id;
        boolean v = cvTipoImpl.isUsado(getIdVo());
        if (!v) {
            try {
                cvTipoImpl.eliminar(sesion.getUsuarioSesion().getId(), getIdVo());
                tipos = cvTipoImpl.traerTodo();
            } catch (Exception e) {
                v = false;
                UtilLog4j.log.fatal(e);
            }
        } else {
            FacesUtils.addInfoMessage("No se puede eliminar el tipo de contrato, porque ya se uso en el sistema.");
        }
    }
////////////////////////////////////////////

    public boolean guardarHito() {
        boolean v = true;
        try {
            cvHitoImpl.guardar(sesion.getUsuarioSesion().getId(), getNombre(), getDescripcion());
            hitos = cvHitoImpl.traerTodo();
	    PrimeFaces.current().executeScript(";$(dialogoHito).modal('hide');;");
        } catch (Exception e) {
            v = false;
            UtilLog4j.log.fatal(e);
        }
        return v;
    }

    public Vo buscarHitoPorId(int idVo) {
        Vo vo = cvHitoImpl.buscarPorId(idVo);
        if (vo != null) {
            setIdVo(vo.getId());
            setNombre(vo.getNombre());
            setDescripcion(vo.getDescripcion());
            vo.setModificar(true);
        }

        return vo;
    }

    public void modificarHito(int id, String nombre, String descr) {
        setIdVo(id);
        setNombre(nombre);
        setDescripcion(descr);
        setModificar(true);
        PrimeFaces.current().executeScript(";mostrarDialogo(dialogoHitoMod);");
    }

    public void completarModHito() {

        boolean v = cvHitoImpl.isUsado(getIdVo());
        if (!v) {
            try {
                cvHitoImpl.modificar(sesion.getUsuarioSesion().getId(), getIdVo(), getNombre(), getDescripcion());
                hitos = cvHitoImpl.traerTodo();
            } catch (Exception e) {
                v = false;
                UtilLog4j.log.fatal(e);
            }
        }
        PrimeFaces.current().executeScript(";$(dialogoHitoMod).modal('hide');;");
    }

    public void eliminarHito(int id) {
        boolean v = cvHitoImpl.isUsado(id);
        if (!v) {
            setIdVo(id);
            cvHitoImpl.eliminar(sesion.getUsuarioSesion().getId(), getIdVo());
            try {
                hitos = cvHitoImpl.traerTodo();
            } catch (Exception e) {
                v = false;
                UtilLog4j.log.fatal(e);
            }
        } else {
            FacesUtils.addInfoMessage("No se puede eliminar el hito de contrato, ya se usó para registrar un contrato");
        }
    }

    ////////////////////////////////////////////
    public void agregarHijoClasificacion(String desc) {
        setDescripcion(desc);
        setModificar(false);
        PrimeFaces.current().executeScript(";$(dialogoClasi).modal('show');");
    }

    public void traerClasificacion() {
        //adasd;
        //clasificaion.get(getIndice()).setListaClasificacion(cvClasificacionImpl.traerPorClasificacion(getIdVo()));
    }

    public void cargarClasificacionPrincipal() {
        this.setClasificaciones(this.cvClasificacionImpl.traerClasificacionPrincipal());
    }

    public void guardarClasificacion() {
        try {
            String[] d = descripcion.split("--");
            //
            int t = d.length;
            setIdVo(Integer.parseInt(d[t - 1]));
            cvClasificacionImpl.guardar(sesion.getUsuarioSesion().getId(), getNombre(), getDescripcion(), getIdVo());
            //
            llenarClasificacion();
        } catch (Exception e) {
            UtilLog4j.log.fatal(e);
        }
    }

    public ClasificacionVo buscarClasificaPorId(int idVo) {
        ClasificacionVo vo = cvClasificacionImpl.buscarPorId(idVo);
        if (vo != null) {
            setIdVo(vo.getId());
            setNombre(vo.getNombre());
            setDescripcion(vo.getDescripcion());
            vo.setModificar(true);
        }
        return vo;
    }

    public void modificarClasificacion(int id, String nombre, String descr) {
        setIdVo(id);
        setNombre(nombre);
        setDescripcion(descr);
        setModificar(true);
        PrimeFaces.current().executeScript(";mostrarDialogo(dialogoClasiMod);");
    }

    public void completarModClasifica() {

        boolean v = cvClasificacionImpl.isUsado(getIdVo());
        if (!v) {
            try {
                cvClasificacionImpl.modificar(sesion.getUsuarioSesion().getId(), getIdVo(), getNombre(), getDescripcion());
                //	clasificaion = cvClasificacionImpl.traerClasificacionPrincipal();
            } catch (Exception e) {
                v = false;
                UtilLog4j.log.fatal(e);
            }
        }
        PrimeFaces.current().executeScript(";$(dialogoClasiMod).modal('hide');;");
    }

    public boolean eliminarClasificacion() {
        boolean v = cvClasificacionImpl.isUsado(getIdVo());
        if (!v) {
            try {
                cvClasificacionImpl.eliminar(sesion.getUsuarioSesion().getId(), getIdVo());
                //	clasificaion = cvClasificacionImpl.traerClasificacionPrincipal();
            } catch (Exception e) {
                v = false;
                UtilLog4j.log.fatal(e);
            }
        }
        return v;
    }

    ////////////////////////////////////////////
    public boolean guardarCondicion() {
        boolean v = true;
        try {
            cvCondicionPagoImpl.guardar(sesion.getUsuarioSesion().getId(), getNombre(), getDescripcion());
            condiciones = cvCondicionPagoImpl.traerTodo();
	    PrimeFaces.current().executeScript(";$(dialogoCondicion).modal('hide');;");
        } catch (Exception e) {
            v = false;
            UtilLog4j.log.fatal(e);
        }
        return v;
    }

    public Vo buscarCondicionPorId(int idVo) {
        Vo vo = cvCondicionPagoImpl.buscarPorId(idVo);
        if (vo != null) {
            setIdVo(vo.getId());
            setNombre(vo.getNombre());
            setDescripcion(vo.getDescripcion());
            vo.setModificar(true);
        }
        return vo;
    }

    public void modificarCondicion() {
        int id = Integer.parseInt(FacesUtils.getRequestParam("idCondicion"));
        String nombre = FacesUtils.getRequestParam("nombre");
        String descr = FacesUtils.getRequestParam("descripcion");
        setIdVo(id);
        setNombre(nombre);
        setDescripcion(descr);
        setModificar(true);
        PrimeFaces.current().executeScript(";mostrarDialogo(dialogoCondicionMod);");
    }

    public void completarModCondicion() {
        boolean v = cvCondicionPagoImpl.isUsado(getIdVo());
        if (!v) {
            try {
                cvCondicionPagoImpl.modificar(sesion.getUsuarioSesion().getId(), getIdVo(), getNombre(), getDescripcion());
                condiciones = cvCondicionPagoImpl.traerTodo();
            } catch (Exception e) {
                v = false;
                UtilLog4j.log.fatal(e);
            }
        }
        PrimeFaces.current().executeScript(";$(dialogoCondicionMod).modal('hide');;");
    }

    public boolean eliminarCondicion() {
        boolean v = cvCondicionPagoImpl.isUsado(getIdVo());
        if (!v) {
            try {
                cvCondicionPagoImpl.eliminar(sesion.getUsuarioSesion().getId(), getIdVo());
                condiciones = cvCondicionPagoImpl.traerTodo();
            } catch (Exception e) {
                v = false;
                UtilLog4j.log.fatal(e);
            }
        }
        return v;
    }

    public void seleccionarFormato(int id, int indice) {
        setIdFormato(id);
        cargarSecciones();
        if (indice > -1 && this.getFormatos().size() > 0) {
            this.setTituloFormato(this.getFormatos().get(indice).getNombre());
        }
        PrimeFaces.current().executeScript(";ocultarDiv('panelFormatos');mostrarDiv('panelSecciones');");
    }

    public void modificarFormato(int id, int indice) {
        setIdFormato(id);
        cargarClasificacionPrincipal();
        if (indice > -1) {
            setNuevoFormato(getFormatos().get(indice));
        }
        PrimeFaces.current().executeScript(";mostrarDialogo(dialogoFormato);");
    }

    public void regresarSeccion() {
        setIdFormato(0);
        setSecciones(new ArrayList<SeccionVo>());
        setIdSeccion(0);
        setNuevaSeccion(new SeccionVo());
        this.setTituloFormato(null);
        PrimeFaces.current().executeScript(";ocultarDiv('panelSecciones');mostrarDiv('panelFormatos');");
    }

    public void seleccionarSeccion(int id, int indice) {
        setIdSeccion(id);
        cargarPreguntas();
        if (indice > -1 && this.getSecciones().size() > 0) {
            this.setTituloSeccion(this.getSecciones().get(indice).getSeccionNombre());
        }
        PrimeFaces.current().executeScript(";ocultarDiv('panelSecciones');mostrarDiv('panelPregunta');");
    }

    public void regresarPregunta() {
        setIdSeccion(0);
        setIdPregunta(0);
        setNuevaPregunta(new PreguntaVo());
        setPreguntas(new ArrayList<PreguntaVo>());
        this.setTituloSeccion(null);
        PrimeFaces.current().executeScript(";ocultarDiv('panelPregunta');mostrarDiv('panelSecciones');");
    }

    public void completarModFormato() {
        if (guardarFormato()) {
            FacesUtils.addInfoMessage("No se puede modificar el formato de evaluación.");
        }
        PrimeFaces.current().executeScript(";$(dialogoFormato).modal('hide');;");
    }

    public void eliminarFormato(int id) {
        setIdFormato(id);
        if (eliminarFormato()) {
            FacesUtils.addInfoMessage("No se puede eliminar el formato de evaluación.");
        }
    }

    public void agregarSeccion() {
        setNuevaSeccion(new SeccionVo());
    }

    public void modificarSeccion(int id, int indice) {
        setIdSeccion(id);
        if (indice > -1) {
            setNuevaSeccion(getSecciones().get(indice));
        }
        PrimeFaces.current().executeScript(";mostrarDialogo(dialogoSeccion);");
    }

    public void completarModSeccion() {
        if (guardarSeccion()) {
            FacesUtils.addInfoMessage("No se puede modificar la sección del formato de evaluación.");
        }
        PrimeFaces.current().executeScript(";$(dialogoSeccion).modal('hide');;");
    }

    public void eliminarSeccion(int id) {
        setIdSeccion(id);
        this.cvEvaluacionSeccionImpl.eliminar(sesion.getUsuarioSesion().getId(), this.getIdSeccion());
        this.setSecciones(cvEvaluacionSeccionImpl.traerSecciones(this.getIdFormato()));
    }

    public void agregarPregunta() {
        setNuevaPregunta(new PreguntaVo());
    }

    public void modificarPregunta(int id, int indice) {
        setIdPregunta(id);
        if (indice > -1) {
            setNuevaPregunta(getPreguntas().get(indice));
        }
        PrimeFaces.current().executeScript(";mostrarDialogo(dialogoPregunta);");
    }

    public void completarModPregunta() {
        if (guardarPregunta()) {
            FacesUtils.addInfoMessage("No se puede modificar la pregunta del formato de evaluación.");
        }
        PrimeFaces.current().executeScript(";$(dialogoPregunta).modal('hide');;");
    }

    public void eliminarPregunta(int id) {
        setIdPregunta(id);
        if (eliminarPregunta()) {
            FacesUtils.addInfoMessage("No se puede eliminar la pregunta del formato de evaluación");
        }
    }

    /**
     * @return the documentos
     */
    public List<DocumentoVO> getDocumentos() {
        return documentos;
    }

    /**
     * @return the clasificaion
     */
    public ClasificacionVo getClasificaion() {
        return clasificaion;
    }

    /**
     * @return the hitos
     */
    public List<CatalogoContratoVo> getHitos() {
        return hitos;
    }

    /**
     * @return the condiciones
     */
    public List<CatalogoContratoVo> getCondiciones() {
        return condiciones;
    }

    /**
     * @return the tipos
     */
    public List<CatalogoContratoVo> getTipos() {
        return tipos;
    }

    /**
     * @param sesion the sesion to set
     */
    public void setSesion(Sesion sesion) {
        this.sesion = sesion;
    }

    /**
     * @return the nombre
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * @param nombre the nombre to set
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * @return the descripcion
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * @param descripcion the descripcion to set
     */
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    /**
     * @return the idVo
     */
    public int getIdVo() {
        return idVo;
    }

    /**
     * @param idVo the idVo to set
     */
    public void setIdVo(int idVo) {
        this.idVo = idVo;
    }

    /**
     * @return the modificar
     */
    public boolean isModificar() {
        return modificar;
    }

    /**
     * @param modificar the modificar to set
     */
    public void setModificar(boolean modificar) {
        this.modificar = modificar;
    }

    /**
     * @return the indice
     */
    public int getIndice() {
        return indice;
    }

    /**
     * @param indice the indice to set
     */
    public void setIndice(int indice) {
        this.indice = indice;
    }

    /**
     * @return the totalHijos
     */
    public int getTotalHijos() {
        return totalHijos;
    }

    /**
     * @param totalHijos the totalHijos to set
     */
    public void setTotalHijos(int totalHijos) {
        this.totalHijos = totalHijos;
    }

    /**
     * @return the listaClasificaion
     */
    public List<ClasificacionVo> getListaClasificaion() {
        return listaClasificaion;
    }

    /**
     * @return the tipoDoc
     */
    public int getTipoDoc() {
        return tipoDoc;
    }

    /**
     * @param tipoDoc the tipoDoc to set
     */
    public void setTipoDoc(int tipoDoc) {
        this.tipoDoc = tipoDoc;
    }

    /**
     * @return the lstTipoDoc
     */
    public List<SelectItem> getLstTipoDoc() {
        return lstTipoDoc;
    }

    /**
     * @param lstTipoDoc the lstTipoDoc to set
     */
    public void setLstTipoDoc(List<SelectItem> lstTipoDoc) {
        this.lstTipoDoc = lstTipoDoc;
    }

    /**
     * @return the formatos
     */
    public List<EvaluacionTemplateVo> getFormatos() {
        return formatos;
    }

    /**
     * @param formatos the formatos to set
     */
    public void setFormatos(List<EvaluacionTemplateVo> formatos) {
        this.formatos = formatos;
    }

    /**
     * @return the idFormato
     */
    public int getIdFormato() {
        return idFormato;
    }

    /**
     * @param idFormato the idFormato to set
     */
    public void setIdFormato(int idFormato) {
        this.idFormato = idFormato;
    }

    /**
     * @return the idSeccion
     */
    public int getIdSeccion() {
        return idSeccion;
    }

    /**
     * @param idSeccion the idSeccion to set
     */
    public void setIdSeccion(int idSeccion) {
        this.idSeccion = idSeccion;
    }

    /**
     * @return the idPregunta
     */
    public int getIdPregunta() {
        return idPregunta;
    }

    /**
     * @param idPregunta the idPregunta to set
     */
    public void setIdPregunta(int idPregunta) {
        this.idPregunta = idPregunta;
    }

    /**
     * @return the preguntas
     */
    public List<PreguntaVo> getPreguntas() {
        return preguntas;
    }

    /**
     * @param preguntas the preguntas to set
     */
    public void setPreguntas(List<PreguntaVo> preguntas) {
        this.preguntas = preguntas;
    }

    /**
     * @return the secciones
     */
    public List<SeccionVo> getSecciones() {
        return secciones;
    }

    /**
     * @param secciones the secciones to set
     */
    public void setSecciones(List<SeccionVo> secciones) {
        this.secciones = secciones;
    }

    public void cargarSecciones() {
        this.setSecciones(cvEvaluacionSeccionImpl.traerSecciones(this.getIdFormato()));
    }

    public void cargarPreguntas() {
        this.setPreguntas(cvEvaluacionTemplateDetImpl.traerPreguntas(this.getIdSeccion()));
    }

    /**
     * @return the nuevoFormato
     */
    public EvaluacionTemplateVo getNuevoFormato() {
        return nuevoFormato;
    }

    /**
     * @param nuevoFormato the nuevoFormato to set
     */
    public void setNuevoFormato(EvaluacionTemplateVo nuevoFormato) {
        this.nuevoFormato = nuevoFormato;
    }

    /**
     * @return the nuevaPregunta
     */
    public PreguntaVo getNuevaPregunta() {
        return nuevaPregunta;
    }

    /**
     * @param nuevaPregunta the nuevaPregunta to set
     */
    public void setNuevaPregunta(PreguntaVo nuevaPregunta) {
        this.nuevaPregunta = nuevaPregunta;
    }

    /**
     * @return the nuevaSeccion
     */
    public SeccionVo getNuevaSeccion() {
        return nuevaSeccion;
    }

    /**
     * @param nuevaSeccion the nuevaSeccion to set
     */
    public void setNuevaSeccion(SeccionVo nuevaSeccion) {
        this.nuevaSeccion = nuevaSeccion;
    }

    /**
     * @return the clasificaciones
     */
    public List<ClasificacionVo> getClasificaciones() {
        return clasificaciones;
    }

    /**
     * @param clasificaciones the clasificaciones to set
     */
    public void setClasificaciones(List<ClasificacionVo> clasificaciones) {
        this.clasificaciones = clasificaciones;
    }

    /**
     * @return the tituloFormato
     */
    public String getTituloFormato() {
        return tituloFormato;
    }

    /**
     * @param tituloFormato the tituloFormato to set
     */
    public void setTituloFormato(String tituloFormato) {
        this.tituloFormato = tituloFormato;
    }

    /**
     * @return the tituloSeccion
     */
    public String getTituloSeccion() {
        return tituloSeccion;
    }

    /**
     * @param tituloSeccion the tituloSeccion to set
     */
    public void setTituloSeccion(String tituloSeccion) {
        this.tituloSeccion = tituloSeccion;
    }

    public boolean guardarFormato() {
        boolean v = true;
        try {
            if (this.getNuevoFormato().getId() > 0) {
                this.cvEvaluacionTemplateImpl.modificar(sesion.getUsuarioSesion().getId(), this.getNuevoFormato());
            } else {
                this.cvEvaluacionTemplateImpl.guardar(sesion.getUsuarioSesion().getId(), sesion.getRfcEmpresa(), this.getNuevoFormato());
            }
            this.setFormatos(cvEvaluacionTemplateImpl.traerTemplatePorTipo(0, sesion.getRfcEmpresa()));
        } catch (Exception e) {
            v = false;
            UtilLog4j.log.fatal(e);
        }
        return v;
    }

    public boolean eliminarFormato() {
        boolean v = true;
        try {
            this.cvEvaluacionTemplateImpl.eliminar(sesion.getUsuarioSesion().getId(), this.getIdFormato());
            this.setFormatos(cvEvaluacionTemplateImpl.traerTemplatePorTipo(0, sesion.getRfcEmpresa()));
        } catch (Exception e) {
            v = false;
            UtilLog4j.log.fatal(e);
        }
        return v;
    }

//    public boolean eliminarSeccion() {
//        boolean v = true;
//        try {
//            this.cvEvaluacionSeccionImpl.eliminar(sesion.getUsuarioSesion().getId(), this.getIdSeccion());
//            this.setSecciones(cvEvaluacionSeccionImpl.traerSecciones(this.getIdFormato()));
//        } catch (Exception e) {
//            v = false;
//            UtilLog4j.log.fatal(e);
//        }
//        return v;
//    }
    public boolean eliminarPregunta() {
        boolean v = true;
        try {
            this.cvEvaluacionTemplateDetImpl.eliminar(sesion.getUsuarioSesion().getId(), this.getIdPregunta());
            this.setPreguntas(cvEvaluacionTemplateDetImpl.traerPreguntas(this.getIdSeccion()));
        } catch (Exception e) {
            v = false;
            UtilLog4j.log.fatal(e);
        }
        return v;
    }

    public boolean guardarSeccion() {
        boolean v = true;
        try {
            if (this.getNuevaSeccion().getSeccionId() > 0) {
                this.cvEvaluacionSeccionImpl.modificar(sesion.getUsuarioSesion().getId(), this.getNuevaSeccion());
            } else {
                this.cvEvaluacionSeccionImpl.guardar(sesion.getUsuarioSesion().getId(), this.getIdFormato(), this.getNuevaSeccion());
            }

            this.setSecciones(cvEvaluacionSeccionImpl.traerSecciones(this.getIdFormato()));
            PrimeFaces.current().executeScript(";$(dialogoSeccion).modal('hide');;");
        } catch (Exception e) {
            v = false;
            UtilLog4j.log.fatal(e);
        }
        return v;
    }

    public boolean guardarPregunta() {
        boolean v = true;
        try {
            if (this.getNuevaPregunta().getPreguntaId() > 0) {
                this.cvEvaluacionTemplateDetImpl.modificar(sesion.getUsuarioSesion().getId(), this.getNuevaPregunta());
            } else {
                this.cvEvaluacionTemplateDetImpl.guardar(sesion.getUsuarioSesion().getId(), this.getIdSeccion(), this.getNuevaPregunta());
            }
            this.setPreguntas(cvEvaluacionTemplateDetImpl.traerPreguntas(this.getIdSeccion()));
        } catch (Exception e) {
            v = false;
            UtilLog4j.log.fatal(e);
        }
        return v;
    }

}
