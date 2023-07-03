/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package mx.ihsa.procesador.bean;

import com.google.common.base.Preconditions;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import lombok.Getter;
import lombok.Setter;
import mx.ihsa.dominio.vo.AdjuntoVO;
import mx.ihsa.dominio.vo.CategoriaAdjuntoVo;
import mx.ihsa.dominio.vo.CategoriaVo;
import mx.ihsa.servicios.sistema.impl.RepAdjuntoCategoriaImpl;
import mx.ihsa.servicios.sistema.impl.SiCategoriaImpl;
import mx.ihsa.servicios.sistema.impl.SiTagImpl;
import mx.ihsa.sistema.bean.backing.Sesion;
import org.primefaces.PrimeFaces;
import org.primefaces.model.file.UploadedFile;

/**
 *
 * @author marin
 */
@Named(value = "cargaArchivoBean")
@ViewScoped
public class CargaArchivoBean implements Serializable {

    /**
     * Creates a new instance of CargaArchivoBean
     */
    public CargaArchivoBean() {
    }

    @Inject
    Sesion sesion;
    @Inject
    RepAdjuntoCategoriaImpl adjuntoCategoriaImpl;
    @Inject
    SiCategoriaImpl categoriaImpl;
    @Inject
    SiTagImpl tagImpl;
    //
    @Setter
    List<CategoriaAdjuntoVo> archivos;
    @Getter
    @Setter
    CategoriaAdjuntoVo categoriaAdjuntoVo;
    @Getter
    @Setter
    AdjuntoVO AdjuntoVo;
    @Getter
    @Setter
    List<CategoriaVo> categorias;
    @Getter
    @Setter
    CategoriaVo categoriaVo;
    @Getter
    @Setter
    UploadedFile uploadedFile;

    @PostConstruct
    public void iniciar() {
        categoriaVo = new CategoriaVo();
        categoriaAdjuntoVo = new CategoriaAdjuntoVo();
        categorias = new ArrayList<>();
        setArchivos(new ArrayList<>());
        llenarDatos();
    }

    private void llenarDatos() {
        setArchivos(adjuntoCategoriaImpl.traerPorArchiCategoria());
    }

    public void registrar() {
        categoriaVo = new CategoriaVo();
        PrimeFaces.current().executeScript("$(dialogoRegistrar).modal(show);");
    }
    
    public void agregarCategoria(){
        PrimeFaces.current().executeScript("$(dialogoRegistrarCategoria).modal(show);");
    }

    public void eliminarRegistro() {
        Preconditions.checkArgument(archivos.stream().anyMatch(CategoriaAdjuntoVo::isSelected), "Seleccione al menos un registro");
        //
        archivos.stream().forEach(cs -> {
            adjuntoCategoriaImpl.eliminiar(sesion.getUsuarioSesion().getId(), cs);
        });
    }

    /**
     * @return the archivos
     */
    public List<CategoriaAdjuntoVo> getArchivos() {
        return archivos;
    }

    /**
     * @param archivos the archivos to set
     */
    public void setArchivos(List<CategoriaAdjuntoVo> archivos) {
        this.archivos = archivos;
    }

}
