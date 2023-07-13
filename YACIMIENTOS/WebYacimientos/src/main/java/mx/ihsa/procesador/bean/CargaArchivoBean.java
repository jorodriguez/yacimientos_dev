/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package mx.ihsa.procesador.bean;

import com.google.common.base.Preconditions;
import java.io.IOException;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import lombok.Getter;
import lombok.Setter;
import mx.ihsa.dominio.vo.AdjuntoVO;
import mx.ihsa.dominio.vo.CategoriaAdjuntoVo;
import mx.ihsa.dominio.vo.CategoriaVo;
import mx.ihsa.dominio.vo.ObjetivoVo;
import mx.ihsa.dominio.vo.TagVo;
import mx.ihsa.excepciones.GeneralException;
import mx.ihsa.modelo.CatObjetivo;
import mx.ihsa.modelo.SiAdjunto;
import mx.ihsa.modelo.SiCategoria;
import mx.ihsa.servicios.sistema.impl.CatObjetivoImpl;
import mx.ihsa.servicios.sistema.impl.RepAdjuntoCategoriaImpl;
import mx.ihsa.servicios.sistema.impl.SiAdjuntoImpl;
import mx.ihsa.servicios.sistema.impl.SiCategoriaImpl;
import mx.ihsa.servicios.sistema.impl.SiTagImpl;
import mx.ihsa.sistema.bean.backing.Sesion;
import mx.ihsa.sistema.bean.support.FacesUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
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
    @Inject
    SiAdjuntoImpl adjuntoImpl;
    @Inject
    CatObjetivoImpl objetivoImpl;
    //
    @Setter
    List<CategoriaAdjuntoVo> archivos;
    @Getter
    @Setter
    ObjetivoVo objetivoVo;
    @Getter
    @Setter
    String objetivo;
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
    List<ObjetivoVo> objetivos;
    private UploadedFile uploadedFile;
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
        objetivos = new ArrayList<>();
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
        //
        llenarObjetivos();
    }

    private void llenarDatos() {
        setArchivos(adjuntoCategoriaImpl.traerPorArchiCategoria());
    }

    private void llenarObjetivos() {
        objetivos = objetivoImpl.traerTodos();
    }

    private void llenarCategorias() {
        categorias = categoriaImpl.traerCategoriasIniciales();
    }

    private void llenarTag() {
        tags = tagImpl.traerTodo();
    }

    public void cargarArchivo() {
        categoriaAdjuntoVo = new CategoriaAdjuntoVo();
        objetivoVo = new ObjetivoVo();
        tagsAcumulados = new ArrayList<>();
        PrimeFaces.current().executeScript("$(dialogoRegistrar).modal('show');");
    }

    public void guardarArchivo() {
        try {
            Preconditions.checkArgument((uploadedFile != null), "Seleccione el archivo");
            Preconditions.checkArgument(objetivo != null && !objetivo.isEmpty(), "Agregue el objetivo");
            Preconditions.checkArgument(!categoriaAdjuntoVo.getNombre().trim().isEmpty(), "Agregue un nombre");
            Preconditions.checkArgument((categoriaVo.getId() != null && categoriaVo.getId() > 0), "Seleccione una categoría");
//
            adjuntoVo = new AdjuntoVO();
            adjuntoVo.setNombre(uploadedFile.getFileName());
            adjuntoVo.setTipoArchivo(uploadedFile.getContentType());
            adjuntoVo.setTamanio(uploadedFile.getSize());
            adjuntoVo.setContenido(uploadedFile.getContent());
            categoriaAdjuntoVo.setIdCategoria(categoriaVo.getId());
            //
            objetivoVo.setNombre(objetivo);
            CatObjetivo cob = objetivoImpl.guardar(sesion.getUsuarioSesion().getId(), objetivoVo);
            if (cob != null) {
                categoriaAdjuntoVo.setIdObjetivo(cob.getId());
            }

//            Path pathFile = Files.write(Path.of("/files/yac/" + adjuntoVo.getNombre()), adjuntoVo.getContenido());
//            if (adjuntoVo.getNombre().endsWith("pdf")) {
//                categoriaAdjuntoVo.setArchivoTexto(contenidoPdf(adjuntoVo.getContenido()));
//            } else if ((adjuntoVo.getNombre().endsWith("docx"))) {
//                String contenido = new String(Files.readAllBytes(pathFile));
//                categoriaAdjuntoVo.setArchivoTexto(contenido);
//            }
            // guardar archivo
            SiAdjunto adj;
            adj = adjuntoImpl.save(adjuntoVo.getNombre(), "/files/yac/" + adjuntoVo.getNombre(), adjuntoVo.getTipoArchivo(), adjuntoVo.getTamanio(), sesion.getUsuarioSesion().getId());
            //
            // guardar tags
            tagsAcumulados.stream().filter(tg -> tg.getId() == 0).forEach(t -> {
                tagImpl.guardar(sesion.getUsuarioSesion().getId(), t);
            });
            //
            adjuntoCategoriaImpl.guardar(sesion.getUsuarioSesion().getId(), categoriaAdjuntoVo, adj.getId(),
                    categoriasSeleccionadas, tagsAcumulados);
            //
            llenarDatos();
            //
            objetivo = "";
            PrimeFaces.current().executeScript("$(dialogoRegistrar).modal('hide');");
        } catch (GeneralException | IllegalArgumentException ex) {
            FacesUtils.addErrorMessage(ex.getMessage());
            Logger.getLogger(CargaArchivoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
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

    public void eliminarArchivo(CategoriaAdjuntoVo cav) {
        try {
            adjuntoCategoriaImpl.eliminiar(sesion.getUsuarioSesion().getId(), cav);
            llenarDatos();
        } catch (IllegalArgumentException e) {
            FacesUtils.addErrorMessage(e.getMessage());
        }
    }

    public void eliminarRegistro() {
        try {
            Preconditions.checkArgument(archivos.stream().anyMatch(CategoriaAdjuntoVo::isSelected), "Seleccione al menos un registro");
            //
            archivos.stream().filter(CategoriaAdjuntoVo::isSelected).forEach(cs -> {
                adjuntoCategoriaImpl.eliminiar(sesion.getUsuarioSesion().getId(), cs);
            });
            llenarDatos();
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

    public List<String> completeObjetivo(String cad) {
        String queryLowerCase = cad.toLowerCase();
        List<String> objs = new ArrayList<>();
        for (ObjetivoVo tg : objetivos) {
            objs.add(tg.getNombre());
        }
        return objs.stream().filter(t -> t.toLowerCase()
                .startsWith(queryLowerCase)).collect(Collectors.toList());
    }

    public List<String> autocompletarTags(String cad) {
        String queryLowerCase = cad.toLowerCase();
        List<String> tgs = new ArrayList<>();
        for (TagVo tg : tags) {
            tgs.add(tg.getNombre());
        }
        return tgs.stream().filter(t -> t.toLowerCase().startsWith(queryLowerCase)).collect(Collectors.toList());
    }

    public void selecionarTag() {
        TagVo tVo = tagImpl.buscarPorNombre(texto);
        if (tVo.getId() == 0) {
            tVo = new TagVo();
            tVo.setId(0);
            tVo.setNombre(texto);
        }
        tagsAcumulados.add(tVo);
        texto = "";
    }

    public void agregarTag() {
        tagVo = new TagVo();
        PrimeFaces.current().executeScript("$(dialogoRegistrarTag).modal('show');");
    }

    public void registrarTag() {
        tagImpl.guardar(sesion.getUsuarioSesion().getId(), tagVo);
        //
        tagVo = tagImpl.buscarPorNombre(tagVo.getNombre());
        tags.add(tagVo);
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

    /**
     * @return the uploadedFile
     */
    public UploadedFile getUploadedFile() {
        return uploadedFile;
    }

    /**
     * @param uploadedFile the uploadedFile to set
     */
    public void setUploadedFile(UploadedFile uploadedFile) {
        this.uploadedFile = uploadedFile;
    }

    private String contenidoPdf(byte[] contenidoFile) {
        PDDocument document = null;
        try {
            // Load the PDF document
            document = PDDocument.load(contenidoFile);

            // Create an instance of PDFTextStripper
            PDFTextStripper textStripper = new PDFTextStripper();

            // Get the text content from the PDF
            String text = textStripper.getText(document);

            // Print the extracted text
            System.out.println(text);
            return text;
        } catch (IOException e) {
            System.out.println("ex: " + e);
        } finally {
            if (document != null) {
                try {
                    document.close();
                } catch (IOException e) {
                }
            }
        }
        return "";
    }
}
