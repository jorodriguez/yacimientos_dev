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
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import lombok.Getter;
import lombok.Setter;
import mx.ihsa.dominio.vo.AdjuntoVO;
import mx.ihsa.dominio.vo.CategoriaAdjuntoVo;
import mx.ihsa.dominio.vo.CategoriaVo;
import mx.ihsa.dominio.vo.TagVo;
import mx.ihsa.modelo.SiCategoria;
import mx.ihsa.servicios.sistema.impl.RepAdjuntoCategoriaImpl;
import mx.ihsa.servicios.sistema.impl.SiCategoriaImpl;
import mx.ihsa.servicios.sistema.impl.SiTagImpl;
import mx.ihsa.sistema.bean.backing.Sesion;
import mx.ihsa.sistema.bean.support.FacesUtils;
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
    AdjuntoVO adjuntoVo;
    @Getter
    @Setter
    List<CategoriaVo> categorias;
    @Getter
    @Setter
    List<CategoriaVo> categoriasSeleccionadas;
    @Getter
    @Setter
    CategoriaVo categoriaSeleccionadaVo;
    @Getter
    @Setter
    CategoriaVo categoriaVo;
    @Getter
    @Setter
    TagVo tagVo;
    @Getter
    @Setter
    List<TagVo> tags;
    @Getter
    @Setter
    UploadedFile uploadedFile;
    @Getter
    @Setter
    String notas;
    @Getter
    @Setter
    String texto;
    @Getter
    @Setter
    List<TagVo> tagsAcumulados;

    @PostConstruct
    public void iniciar() {
        tagVo = new TagVo();
        categoriaVo = new CategoriaVo();
        categoriaSeleccionadaVo = new CategoriaVo();
        categoriaAdjuntoVo = new CategoriaAdjuntoVo();
        tags = new ArrayList<>();
        tagsAcumulados = new ArrayList<>();
        categorias = new ArrayList<>();
        categoriasSeleccionadas = new ArrayList<>();
        setArchivos(new ArrayList<>());
        llenarDatos();
        //
        llenarCategorias();
        //
        iniciarCatSel();
        //
        llenarTag();
    }

    private void llenarDatos() {
        setArchivos(adjuntoCategoriaImpl.traerPorArchiCategoria());
    }

    private void llenarCategorias() {
        categorias = categoriaImpl.traerCategoriasIniciales();
    }

    private void llenarTag() {
        tags = tagImpl.buscarPorNombre("");
    }

    public void cargarArchivo() {
        categoriaAdjuntoVo = new CategoriaAdjuntoVo();
        PrimeFaces.current().executeScript("$(dialogoRegistrar).modal('show');");
    }

    public void guardarArchivo() {
        adjuntoVo = new AdjuntoVO();
        adjuntoVo.setNombre(uploadedFile.getFileName());
        adjuntoVo.setTipoArchivo(uploadedFile.getContentType());
        adjuntoVo.setTamanio(uploadedFile.getSize());
        adjuntoVo.setContenido(uploadedFile.getContent());
        categoriaAdjuntoVo.setIdCategoria(categoriaSeleccionadaVo.getId());
        adjuntoCategoriaImpl.guardar(sesion.getUsuarioSesion().getId(), categoriaAdjuntoVo, adjuntoVo, categoriasSeleccionadas, tagsAcumulados);
        PrimeFaces.current().executeScript("$(dialogoRegistrar).modal('show');");
    }

    public void agregarCategoria() {
        categoriaVo = new CategoriaVo();
        PrimeFaces.current().executeScript("$(dialogoRegistrarCategoria).modal('show');");

    }

    private void iniciarCatSel() {
        CategoriaVo c = new CategoriaVo();
        c.setNombre("Pricipales");
        c.setId(0);
        categoriasSeleccionadas.add(c);
        //
        categoriaSeleccionadaVo.setNombre("Iniciales");
    }

    public void registrarCategoria() {
        categoriaImpl.guardar(sesion.getUsuarioSesion().getId(), categoriaVo);
        SiCategoria sc = categoriaImpl.buscarPorNombre(categoriaVo.getNombre());
        categoriaVo.setId(sc.getId());
        categoriaVo.setNombre(sc.getNombre());
        categorias.add(categoriaVo);
        categoriaVo = new CategoriaVo();
        PrimeFaces.current().executeScript("$(dialogoRegistrarCategoria).modal('hide');");
    }

    public void eliminarRegistro() {
        try {
            Preconditions.checkArgument(archivos.stream().anyMatch(CategoriaAdjuntoVo::isSelected), "Seleccione al menos un registro");
            //
            archivos.stream().forEach(cs -> {
                adjuntoCategoriaImpl.eliminiar(sesion.getUsuarioSesion().getId(), cs);
            });
        } catch (IllegalArgumentException e) {
            FacesUtils.addErrorMessage(e.getMessage());
        }
    }

    public void seleccionarCategoria(CategoriaVo catVo) {
        categorias = categoriaImpl.traerCategoriaPorCategoriaId(catVo.getId());
        //
        categoriaVo = catVo;
        categoriaSeleccionadaVo = catVo;
        categoriasSeleccionadas.add(categoriaVo);

    }

    public void seleccionarCategoriaEnSeleccionadas(int indice) {
        CategoriaVo c = categoriasSeleccionadas.get(indice);
        if (indice == 0) {
            categoriaVo = new CategoriaVo();
            categoriasSeleccionadas = new ArrayList<>();
            iniciarCatSel();
            //
            llenarCategorias();
        } else {
            categoriaSeleccionadaVo = c;
            setCategorias(categoriaImpl.traerCategoriaPorCategoriaId(c.getId()));
            if (c.getId() != categoriaVo.getId().intValue()) {
                categoriasSeleccionadas.add(categoriaVo);// limpiar lista seleccionadas
            }
            if ((indice + 1) < categoriasSeleccionadas.size()) {
                for (int i = (categoriasSeleccionadas.size() - 1); i > indice; i--) {
                    categoriasSeleccionadas.remove(i);
                }
            }
        }
    }

    public List<String> autocompletarTags(String cad) {
        String queryLowerCase = cad.toLowerCase();
        List<String> countryList = new ArrayList<>();
        for (TagVo tg : tags) {
            countryList.add(tg.getNombre());
        }
        return countryList.stream().filter(t -> t.toLowerCase().startsWith(queryLowerCase)).collect(Collectors.toList());
    }

    public void selecionarTag() {
        tagsAcumulados.add(tagVo);
    }

    public void agregarTag() {
        tagVo = new TagVo();
        PrimeFaces.current().executeScript("$(dialogoRegistrarTag).modal('show');");
    }

    public void registrarTag() {
        tagImpl.guardar(sesion.getUsuarioSesion().getId(), tagVo);
        tagsAcumulados.add(tagVo);
        tagVo = new TagVo();
        PrimeFaces.current().executeScript("$(dialogoRegistrarTag).modal('hide');");
    }

    public void onClose(TagVo tVo) {
        tagsAcumulados.remove(tVo);
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
