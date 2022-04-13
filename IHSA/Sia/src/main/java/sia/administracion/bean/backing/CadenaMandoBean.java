/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.administracion.bean.backing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;

import javax.faces.event.ValueChangeEvent;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.PrimeFaces;
import org.primefaces.event.SelectEvent;
import sia.excepciones.SIAException;
import sia.modelo.Usuario;
import sia.modelo.cadena.aprobacion.vo.CadenaAprobacionVo;
import sia.servicios.requisicion.impl.CadenasMandoImpl;
import sia.sistema.bean.backing.Sesion;
import sia.sistema.bean.support.FacesUtils;
import sia.util.UtilLog4j;

/**
 *
 * @author mluis
 */
@Named
@ViewScoped
public class CadenaMandoBean implements Serializable {

    @Setter
    @Inject
    Sesion sesion;
    @Inject
    CadenasMandoImpl cadenasMandoServicioRemoto;
    @Getter
    @Setter
    List<CadenaAprobacionVo> listaCadena;
///Lsita de campos

    @Getter
    @Setter
    private List<Usuario> listaUsuarios;
    @Getter
    @Setter
    private String usuario;
    @Getter
    @Setter
    private String solicita;
    @Getter
    @Setter
    private String revisa;
    @Getter
    @Setter
    private String aprueba;
    @Getter
    @Setter
    private int idCampo;
    @Getter
    @Setter
    private String campo;
    @Getter
    @Setter
    private int idOrdena = 1;
//    private String agregar = "False";
    @Getter
    @Setter
    private CadenaAprobacionVo cadenaAprobacionVo;
    private boolean modalPop = false;
    private boolean modalModificarPop = false;
    private boolean revisaReq = false;
    private boolean apruebaReq = false;

    /**
     * Creates a new instance of CadenaMando
     */
    public CadenaMandoBean() {
    }

    @PostConstruct
    public void iniciar() {
        if (getIdCampo() <= 0) {
            setIdCampo(sesion.getUsuarioVo().getIdCampo());
        }

        //setListaCadena(cadenasMandoServicioRemoto.traerCadenaAprobacion(null, getIdCampo(), 1, false, false, false));

    }

    public void irCadenaMando() {
        setIdCampo(1);
        setIdOrdena(1);
        //    getTraerCadena();
    }

    public void cambiarValorCampo(ValueChangeEvent valueChangeEvent) {
        setIdCampo((Integer) valueChangeEvent.getNewValue());
        UtilLog4j.log.info(this, "campo: " + getIdCampo());
        setUsuario(null);
        setIdOrdena(1);
        llenarListaCadena();
    }

    /*
    public void cambiarValorOrdena(ValueChangeEvent valueChangeEvent) {
        setIdOrdena((Integer) valueChangeEvent.getNewValue());
        UtilLog4j.log.info(this, "Operacion: " + getIdOrdena());
        if (getListaCadena().getRowCount() == 0) {
            FacesUtils.addInfoMessage(FacesUtils.getKeyResourceBundle("sia.cadena.aprobacion.no.existe"));
        }
    }

    public void buscarCadena() {
        //    getTraerCadena();
        if (getListaCadena().getRowCount() < 1) {
            FacesUtils.addInfoMessage(FacesUtils.getKeyResourceBundle("sia.cadena.aprobacion.no.existe"));
        }
//        this.agregar = "False";
        setCadenaAprobacionVo(null);
    }
     */
    public void agregarCadenaMando() {
        //setSolicita(getU().getNombre());
        setUsuario(null);
        setRevisa(null);
        setAprueba(null);
        setCadenaAprobacionVo(null);
    }

    public void registroCadenaMando() {
        boolean v;
        try {
            v = cadenasMandoServicioRemoto.registroCadenaMando(getIdCampo(), getUsuario(), getRevisa(), getAprueba(), sesion.getUsuarioVo().getId());
            if (v) {
                setUsuario(null);
                setRevisa(null);
                setAprueba(null);
//                        setModalPop(false);
                llenarListaCadena();
                PrimeFaces.current().executeScript(";dialogoOK('dialogoAgregarCad');");
                //                    getTraerCadena();
            } else {
                FacesUtils.addInfoMessage(new SIAException().getMessage());
            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Excepcion: " + e.getMessage());
        }
    }

    public boolean completarModificacion() {
        UtilLog4j.log.info(this, "Solicita: " + getUsuario());
        UtilLog4j.log.info(this, "REvisa: " + getRevisa());
        UtilLog4j.log.info(this, "Aprueba: " + getAprueba());
        return this.cadenasMandoServicioRemoto.completarModificacion(getCadenaAprobacionVo().getId(), getRevisa(), getAprueba(), sesion.getUsuarioVo().getId());
    }

    public void cancelarRegistroCadenaMando() {
        setRevisa(null);
        setAprueba(null);
    }

    public void modificarCadena(int idCad) {
        //setCadenaAprobacionVo((CadenaAprobacionVo) getListaCadena().getRowData());
        setCadenaAprobacionVo(cadenasMandoServicioRemoto.traerPorId(idCad));
        UtilLog4j.log.info(this, "Ca: " + getCadenaAprobacionVo().getSolicita() + getRevisa());
        setRevisa("");
        setAprueba("");
        //setModalModificarPop(true);
    }

    public void cancelarModificacion() {
        setCadenaAprobacionVo(null);
        setRevisa(null);
        setAprueba(null);
        PrimeFaces.current().executeScript(";dialogoOK('dialogoModificarCad');");
        //      setModalModificarPop(false);
    }

    public void eliminarCadena(int idCad) {
        setCadenaAprobacionVo(new CadenaAprobacionVo());
        getCadenaAprobacionVo().setId(idCad);
//        setCadenaAprobacionVo((CadenaAprobacionVo) getListaCadena().getRowData());
        cadenasMandoServicioRemoto.eliminar(getCadenaAprobacionVo().getId(), sesion.getUsuarioVo().getId());
        llenarListaCadena();
        setCadenaAprobacionVo(null);
//        this.agregar = "False";
    }

    public void eliminaVariasCadenas() {
        List<CadenaAprobacionVo> listCadena = getListaCadena();

        if (listCadena != null) {
            for (CadenaAprobacionVo vo : listCadena) {
                if (vo.isSelected()) {
                    System.out.println(vo.getAprueba() + " " + vo.getRevisa());
                }
            }
        }
    }

    public List<String> usuarioListener(String texto) {
        String queryLowerCase = texto.toLowerCase();
        List<String> usuarioFiltrados = new ArrayList<>();
        List<Usuario> usuarios = listaUsuarios;
        for (Usuario users : usuarios) {
            usuarioFiltrados.add(users.getNombre());
        }

        return usuarioFiltrados.stream().filter(t -> t.toLowerCase().startsWith(queryLowerCase)).collect(Collectors.toList());
    }

    public void onItemSelect(SelectEvent<String> event) {
        setUsuario(event.getObject());
    }
//    public List<SelectItem> regresaUsuarioActivo(String cadenaDigitada) {
//        List<SelectItem> list = new ArrayList<SelectItem>();
//        for (Usuario p : this.soporteProveedor.getUsuario()) {
//            if (p.getNombre() != null) {
//                String cadenaPersona = p.getNombre().toLowerCase();
//                cadenaDigitada = cadenaDigitada.toLowerCase();
//                if (cadenaPersona.indexOf(cadenaDigitada) >= 0) {
//                    SelectItem item = new SelectItem(p, p.getNombre());
//                    list.add(item);
//                }
//            }
//        }
//        return list;
//    }

    public List<String> usuarioListenerAgregarSolicita(String texto) {
        String queryLowerCase = texto.toLowerCase();
        List<String> usuarioFiltrados = new ArrayList<>();
        List<Usuario> usuarios = listaUsuarios;
        for (Usuario users : usuarios) {
            usuarioFiltrados.add(users.getNombre());
        }

        return usuarioFiltrados.stream().filter(t -> t.toLowerCase().startsWith(queryLowerCase)).collect(Collectors.toList());

    }

    public void onItemSelectSolicita(SelectEvent<String> event) {
        setSolicita(event.getObject());
    }

    public List<String> usuarioListenerAgregarRevisa(String texto) {
        String queryLowerCase = texto.toLowerCase();
        List<String> usuarioFiltrados = new ArrayList<>();
        for (Usuario users : listaUsuarios) {
            usuarioFiltrados.add(users.getNombre());
        }

        return usuarioFiltrados.stream().filter(t -> t.toLowerCase().startsWith(queryLowerCase)).collect(Collectors.toList());

    }

    public void onItemSelectRevisa(SelectEvent<String> event) {
        setRevisa(event.getObject());
    }

    public List<String> usuarioListenerAgregarAprueba(String texto) {

        String queryLowerCase = texto.toLowerCase();
        List<String> usuarioFiltrados = new ArrayList<>();
        List<Usuario> usuarios = listaUsuarios;
        for (Usuario users : usuarios) {
            usuarioFiltrados.add(users.getNombre());
        }

        return usuarioFiltrados.stream().filter(t -> t.toLowerCase().startsWith(queryLowerCase)).collect(Collectors.toList());
    }

    public void onItemSelectAprueba(SelectEvent<String> event) {
        setRevisa(event.getObject());
    }
    //Modificaciones

    public List<String> usuarioListenerRevisaMod(String texto) {

        String queryLowerCase = texto.toLowerCase();
        List<String> usuarioFiltrados = new ArrayList<>();
        List<Usuario> usuarios = listaUsuarios;
        for (Usuario users : usuarios) {
            usuarioFiltrados.add(users.getNombre());
        }

        return usuarioFiltrados.stream().filter(t -> t.toLowerCase().startsWith(queryLowerCase)).collect(Collectors.toList());

    }

    public List<String> usuarioListenerApruebaMod(String texto) {

        String queryLowerCase = texto.toLowerCase();
        List<String> usuarioFiltrados = new ArrayList<>();
        List<Usuario> usuarios = listaUsuarios;
        for (Usuario users : usuarios) {
            usuarioFiltrados.add(users.getNombre());
        }

        return usuarioFiltrados.stream().filter(t -> t.toLowerCase().startsWith(queryLowerCase)).collect(Collectors.toList());

    }

    //Fin
    public void llenarListaCadena() {
        setListaCadena(this.cadenasMandoServicioRemoto.traerCadenaAprobacion(null, getIdCampo(), getIdOrdena(), false, false, false));
    }
}
