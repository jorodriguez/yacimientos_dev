/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.sgl.combustible.bean;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.faces.bean.ManagedProperty;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.file.UploadedFile;
import sia.archivador.ProveedorAlmacenDocumentos;
import sia.constantes.Constantes;
import sia.modelo.SgTarjetaOperacion;
import sia.modelo.SiAdjunto;
import sia.servicios.sgl.combustible.impl.SgTarjetaOperacionImpl;
import sia.servicios.sistema.impl.SiAdjuntoImpl;
import sia.servicios.sistema.impl.SiManejoFechaImpl;
import sia.sgl.sistema.bean.backing.Sesion;
import sia.sgl.sistema.bean.support.FacesUtils;
import sia.util.UtilLog4j;
import sia.util.ValidadorNombreArchivo;

/**
 *
 * @author ihsa
 */
@Named(value = "consumoBean")
@ViewScoped
public class ConsumoBean implements Serializable {

    @Inject
    private ProveedorAlmacenDocumentos proveedorAlmacenDocumentos;
    @Inject
    private SiAdjuntoImpl siAdjuntoImpl;
    @Inject
    private SgTarjetaOperacionImpl sgTarjetaOperacionImpl;
    @Inject
    private SiManejoFechaImpl siManejoFechaImpl;

    @Inject
    private Sesion sesion;
    //
    SiAdjunto siAdjunto;
    private boolean cargarArchivo = true;
    private String fechaCargaArchivo = "";
    private List<SgTarjetaOperacion> lista;
    private final static UtilLog4j LOGGER = UtilLog4j.log;
    private List<SelectItem> listaFechaCarga;
    @Getter
    @Setter
    private UploadedFile fileInfo;

    /**
     * Creates a new instance of ConsumoBean
     */
    public ConsumoBean() {
    }

    @PostConstruct
    public void iniciar() {
        listaFechaCarga = new ArrayList<SelectItem>();
        List<Date> lcArchivo = sgTarjetaOperacionImpl.traerFechaCargaArchivo(Constantes.TOTAL_FILAS_RECUPERADAS);
        for (Date fechaCarga : lcArchivo) {
            listaFechaCarga.add(new SelectItem(siManejoFechaImpl.convertirFechaStringddMMyyyy(fechaCarga)));
        }
        llenarDatos();
    }

    private void llenarDatos() {
        Calendar cInicioSem = siManejoFechaImpl.getInicioSemana();
        Calendar cFinSem = siManejoFechaImpl.getFinSemana();
        lista = sgTarjetaOperacionImpl.traerRegistrosPorSemana(cInicioSem, cFinSem);
        if (lista != null && !lista.isEmpty()) {
            cargarArchivo = false;
            fechaCargaArchivo = Constantes.FMT_ddMMyyy.format(lista.get(lista.size() - 1).getFechaGenero());
        }
    }

    public void subirArchivo(FileUploadEvent fileEvent) throws Exception {
        boolean valid = false;
        fileInfo = fileEvent.getFile();

        ValidadorNombreArchivo validadorNombreArchivo = new ValidadorNombreArchivo();
        if (fileInfo.getFileName().endsWith(".xls")) {
            boolean addArchivo = validadorNombreArchivo.isNombreValido(fileInfo.getFileName());
            try {

                if (addArchivo) {
                    try {
                        lista = new ArrayList<>();
                        File file = new File(fileInfo.getFileName());
                        lista = sgTarjetaOperacionImpl.guardar(sesion.getUsuario().getId(), file);
                        //
                        if (lista != null && lista.isEmpty()) {
                            cargarArchivo = false;
                            fechaCargaArchivo = Constantes.FMT_ddMMyyy.format(lista.get(lista.size() - 1).getFechaGenero());
                        }
                        List<Date> lcArchivo = sgTarjetaOperacionImpl.traerFechaCargaArchivo(Constantes.TOTAL_FILAS_RECUPERADAS);
                        for (Date fechaCarga : lcArchivo) {
                            listaFechaCarga.add(new SelectItem(siManejoFechaImpl.convertirFechaStringddMMyyyy(fechaCarga)));
                        }
                    } catch (Exception ex) {
                        UtilLog4j.log.fatal(this, "Ocurrio un error al guardar el archivo . . . . . " + ex.getMessage());
                        valid = false;
                    }
                    //  return v;
                } else {
                    FacesUtils.addWarnMessage(new StringBuilder()
                            .append("No se permiten los siguientes caracteres especiales en el nombre del Archivo: ")
                            .append(validadorNombreArchivo.getCaracteresNoValidos())
                            .toString());
                }

            } catch (Exception e) {
                LOGGER.fatal(e);
                valid = false;
            }

            if (!valid) {
                FacesUtils.addErrorMessage("Ocurrió un error al subir el archivo del Pago. Porfavor contacte al Equipo del SIA al correo soportesia@ihsa.mx");
            }
        } else {
            FacesUtils.addWarnMessage("El archivo no es del tipo esperado (*.xls).");
        }
    }

    public void quitarCargaArchivo(ActionEvent event) {
        sgTarjetaOperacionImpl.quitarRegistrosCargados(sesion.getUsuario().getId(), lista);
        lista = new ArrayList<SgTarjetaOperacion>();
        cargarArchivo = true;
    }

    public String getDirectorio() {
        Calendar c = Calendar.getInstance();
        int mes = c.get(Calendar.MONTH) + 1;
        return "SGyL/Combustible/" + c.get(Calendar.YEAR) + (mes < 10 ? "0" + mes : mes) + c.get(Calendar.DAY_OF_MONTH) + "/";
    }

    public void seleccionarFechaCarga(ValueChangeEvent event) {
        try {
            fechaCargaArchivo = (String) event.getNewValue();
            if (fechaCargaArchivo != null) {
                System.out.println("Fecha seleccionada  . . . " + fechaCargaArchivo);
                String[] fe = fechaCargaArchivo.split("/");
                Calendar c = Calendar.getInstance();
                c.set(Integer.parseInt(fe[2]), (Integer.parseInt(fe[1]) - 1), Integer.parseInt(fe[0]));
                lista = sgTarjetaOperacionImpl.traerRegistrosPorFechaCarga(c.getTime());
            } else {
                FacesUtils.addErrorMessage("No hay fecha seleccionada");
            }

        } catch (Exception ex) {
            Logger.getLogger(ConsumoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * @param sesion the sesion to set
     */
    public void setSesion(Sesion sesion) {
        this.sesion = sesion;
    }

    /**
     * @return the lista
     */
    public List<SgTarjetaOperacion> getLista() {
        return lista;
    }

    /**
     * @param lista the lista to set
     */
    public void setLista(List<SgTarjetaOperacion> lista) {
        this.lista = lista;
    }

    /**
     * @return the cargarArchivo
     */
    public boolean isCargarArchivo() {
        return cargarArchivo;
    }

    /**
     * @param cargarArchivo the cargarArchivo to set
     */
    public void setCargarArchivo(boolean cargarArchivo) {
        this.cargarArchivo = cargarArchivo;
    }

    /**
     * @return the fechaCargaArchivo
     */
    public String getFechaCargaArchivo() {
        return fechaCargaArchivo;
    }

    /**
     * @param fechaCargaArchivo the fechaCargaArchivo to set
     */
    public void setFechaCargaArchivo(String fechaCargaArchivo) {
        this.fechaCargaArchivo = fechaCargaArchivo;
    }

    /**
     * @return the listaFechaCarga
     */
    public List<SelectItem> getListaFechaCarga() {
        return listaFechaCarga;
    }

    /**
     * @param listaFechaCarga the listaFechaCarga to set
     */
    public void setListaFechaCarga(List<SelectItem> listaFechaCarga) {
        this.listaFechaCarga = listaFechaCarga;
    }

}
