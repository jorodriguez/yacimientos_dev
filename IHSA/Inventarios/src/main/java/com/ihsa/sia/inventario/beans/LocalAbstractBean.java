package com.ihsa.sia.inventario.beans;

import com.ihsa.sia.commons.AbstractBean;
import com.ihsa.sia.commons.SaveObservable;
import com.ihsa.sia.commons.SaveObserver;
import com.ihsa.sia.commons.SessionBean;
import java.io.Serializable;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;

import javax.faces.model.DataModel;
import javax.inject.Inject;
import org.primefaces.model.LazyDataModel;
import sia.excepciones.SIAException;
import sia.inventarios.service.LocalServiceInterface;

/**
 *
 * @author Aplimovil SA de CV
 * @param <ClaseVO>
 * @param <TipoID>
 *
 * Esta clase es necesaria para la reutilizacion de codigo de los views tipo
 * catalogo.
 */
public abstract class LocalAbstractBean<ClaseVO, TipoID> extends AbstractBean implements SaveObservable, Serializable {

    //filtros variables
    private ClaseVO filtro;
    //Objeto para crear y eliminar
    private ClaseVO elemento;
    //Lista de elementos que permite lazy loading
    private DataModel<ClaseVO> lista;
    //clase
    private final Class<ClaseVO> claseVO;
    //bandera para detectar si es nuevo elemento
    private boolean esNuevoElemento;
    //almacena el numero total de filas en la tabla
    private int filasTotales;
    //almacena el numero actual de filas mostradas en la tabla
    private int filasActuales;
    private final List<SaveObserver> observadores;
    //clave del mensaje para mostrar informacion sobre paginacion
    private static final String MENSAJE_PAGINACION = "sia.inventarios.comun.mensajePaginacion";

    public LocalAbstractBean(Class<ClaseVO> claseVO) {
        this.claseVO = claseVO;
        observadores = new ArrayList<>();
    }
    @Inject
    protected SessionBean principal;

    @PostConstruct
    protected void init() {
        try {
            setFiltro(claseVO.newInstance());
            setElemento(claseVO.newInstance());
        } catch (InstantiationException ex) {
            ManejarExcepcion(ex);
        } catch (IllegalAccessException ex) {
            ManejarExcepcion(ex);
        }
        cargarListaConFiltros();
    }

    protected void cargarListaConFiltros() {
        try {
            LazyDataModel lazyLista = new LazyDataModel() {
                @Override
                public int count(Map filterBy) {
                    throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
                }

                @Override
                public List load(int first, int pageSize, Map sortBy, Map filterBy) {
                    String campoOrdenar = null;
                    boolean esAscendente = true;
                    List<ClaseVO> resultado = getServicio().buscarPorFiltros(getFiltro(), first, pageSize, campoOrdenar, esAscendente, principal.getUser().getIdCampo());
                    filasActuales = resultado.size();
                    return resultado;
                }
            };
            contarFilas(principal.getUser().getIdCampo());
            lazyLista.setRowCount(filasTotales);
            this.lista = lazyLista;
        } catch (Exception ex) {
            ManejarExcepcion(ex);
        }
    }

    protected void contarFilas(Integer campo) throws SIAException {
        filasTotales = getServicio().contarPorFiltros(getFiltro(), campo);
    }

    //Eventos
    public void buscar() {
        try {
            cargarListaConFiltros();
        } catch (Exception ex) {
            ManejarExcepcion(ex);
        }
    }

    public void reestablecer() {
        try {
            setFiltro(claseVO.newInstance());
            cargarListaConFiltros();
        } catch (InstantiationException ex) {
            ManejarExcepcion(ex);
        } catch (IllegalAccessException ex) {
            ManejarExcepcion(ex);
        }
    }

    public void agregarNuevo() {
        try {
            setElemento(claseVO.newInstance());
            esNuevoElemento = true;
        } catch (InstantiationException ex) {
            ManejarExcepcion(ex);
        } catch (IllegalAccessException ex) {
            ManejarExcepcion(ex);
        }
    }

    public void cargarElementParaEditar(TipoID id) {
        try {
            setElemento(getServicio().buscar(id));
            esNuevoElemento = false;
        } catch (Exception ex) {
            ManejarExcepcion(ex);
        }
    }

    public void guardarElemento() {
        try {
            if (esNuevoElemento) {
                getServicio().crear(getElemento(), getUserName(), principal.getUser().getIdCampo());
                addInfoMessage(obtenerCadenaDeRecurso(mensajeCrearKey()));
                notifyObservers("guardar");
            } else {
                getServicio().actualizar(getElemento(), getUserName(), principal.getUser().getIdCampo());
                addInfoMessage(obtenerCadenaDeRecurso(mensajeEditarKey()));
            }
            cargarListaConFiltros();
        } catch (Exception ex) {
            ManejarExcepcion(ex);
        }
    }

    public String eliminarElemento(TipoID id) {
        try {
            System.out.println("id: " + id);
            getServicio().eliminar(id, getUserName(), principal.getUser().getIdCampo());
            addInfoMessage(obtenerCadenaDeRecurso(mensajeEliminarKey()));
            cargarListaConFiltros();
        } catch (Exception ex) {
            ManejarExcepcion(ex);
        }
        return null;
    }

    //Metodos
    protected abstract LocalServiceInterface<ClaseVO, TipoID> getServicio();

    protected abstract String mensajeCrearKey();

    protected abstract String mensajeEditarKey();

    protected abstract String mensajeEliminarKey();

    //Propiedades
    public ClaseVO getFiltro() {
        return filtro;
    }

    public void setFiltro(ClaseVO filtro) {
        this.filtro = filtro;
    }

    public ClaseVO getElemento() {
        return elemento;
    }

    public void setElemento(ClaseVO elemento) {
        this.elemento = elemento;
    }

    public boolean getEsNuevoElemento() {
        return this.esNuevoElemento;
    }

    public DataModel<ClaseVO> getLista() {
        return lista;
    }

    public void setLista(DataModel<ClaseVO> lista) {
        this.lista = lista;
    }

    public int getFilasTotales() {
        return filasTotales;
    }

    public String getMensajePaginacion() {
        return MessageFormat.format(
                obtenerCadenaDeRecurso(MENSAJE_PAGINACION),
                filasTotales,
                filasActuales);
    }

    @Override
    public void addObserver(SaveObserver o) {
        observadores.add(o);
    }

    @Override
    public void notifyObservers(String event) {
        Iterator<SaveObserver> i = observadores.iterator();
        while (i.hasNext()) {
            i.next().update(this, event);
        }
    }
}
