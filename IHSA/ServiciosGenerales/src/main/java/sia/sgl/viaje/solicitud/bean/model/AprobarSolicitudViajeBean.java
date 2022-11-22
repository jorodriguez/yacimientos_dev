/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package sia.sgl.viaje.solicitud.bean.model;

import com.google.common.base.Joiner;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.PrimeFaces;
import sia.constantes.Constantes;
import sia.modelo.sgl.viaje.vo.EstatusAprobacionSolicitudVO;
import sia.modelo.sgl.viaje.vo.InvitadoVO;
import sia.modelo.sgl.viaje.vo.SolicitudViajeVO;
import sia.modelo.sgl.viaje.vo.ViajeroVO;
import sia.modelo.usuario.vo.UsuarioVO;
import sia.servicios.campo.nuevo.impl.ApCampoUsuarioRhPuestoImpl;
import sia.servicios.catalogos.impl.UsuarioImpl;
import sia.servicios.sgl.impl.SgEstatusAprobacionImpl;
import sia.servicios.sgl.impl.SgInvitadoImpl;
import sia.servicios.sgl.viaje.impl.SgSolicitudViajeImpl;
import sia.servicios.sgl.viaje.impl.SgViajeroImpl;
import sia.servicios.sistema.impl.SiManejoFechaImpl;
import sia.sgl.sistema.bean.backing.Sesion;
import sia.sgl.sistema.bean.support.FacesUtils;
import sia.util.UtilLog4j;

/**
 *
 * @author mluis
 */
@Named(value = "aprobarSolicitudViajeBean")
@ViewScoped
public class AprobarSolicitudViajeBean implements Serializable {

    /**
     * Creates a new instance of AprobarSolicitudViajeBean
     */
    public AprobarSolicitudViajeBean() {
    }
    @Inject
    Sesion sesion;

    @Inject
    SgSolicitudViajeImpl sgSolicitudViajeImpl;
    @Inject
    SiManejoFechaImpl siManejoFechaImpl;
    @Inject
    SgEstatusAprobacionImpl estatusAprobacionService;
    @Inject
    SgInvitadoImpl sgInvitadoImpl;
    @Inject
    ApCampoUsuarioRhPuestoImpl apCampoUsuarioRhPuestoImpl;
    @Inject
    UsuarioImpl usuarioImpl;
    @Inject
    SgViajeroImpl sgViajeroImpl;
    //
    @Getter
    @Setter
    private List<SolicitudViajeVO> listSolicitudesVo;
    @Getter
    @Setter
    private int countSVT = 0;
    @Getter
    private List<Integer> jus;
    @Getter
    @Setter
    private List<ViajeroVO> listViajeroBajarVO;
    @Getter
    @Setter
    private List<UsuarioVO> usuariosGerenciaVO;
    @Getter
    @Setter
    private SolicitudViajeVO solicitudViajeVO;
    @Getter
    @Setter
    private boolean selectTodo = false;
    @Getter
    @Setter
    String codigos = "";
    @Getter
    @Setter
    String motivo = "";
    @Getter
    @Setter
    String invitado = "";
    @Getter
    @Setter
    String idUsuario = "";

    @PostConstruct
    public void iniciar() {
        listSolicitudesVo = new ArrayList<>();
        listViajeroBajarVO = new ArrayList<>();
        jus = new ArrayList<>();
        mostrarSolicitudesByAprobar();
    }

    public void mostrarSolicitudesByAprobar() {
        try {
            int e1 = 0;
            int e2 = 0;
            switch (sesion.getIdRol()) {
                case Constantes.ROL_GERENTE:
                    e1 = Constantes.ESTATUS_APROBAR;
                    e2 = Constantes.ESTATUS_VISTO_BUENO;
                    break;
                case Constantes.ROL_DIRECCION_GENERAL:
                    e1 = Constantes.CERO;
                    e2 = Constantes.ESTATUS_AUTORIZAR;
                    break;
                case Constantes.ROL_JUSTIFICA_VIAJES:
                    e1 = Constantes.ESTATUS_JUSTIFICAR;
                    e2 = Constantes.CERO;
                    break;
                case Constantes.SGL_RESPONSABLE:
                    e1 = Constantes.CERO;
                    e2 = Constantes.ESTATUS_APROBAR;
                    break;
                case Constantes.ROL_ADMIN_VIAJES_AEREOS:
                    e1 = Constantes.CERO;
                    e2 = Constantes.ESTATUS_APROBAR;
                    break;
                default:
                    e1 = Constantes.CERO;
                    e2 = Constantes.CERO;
                    break;
            }
            if (e1 != 0 && e2 != 0) {
                setListSolicitudesVo(
                        sgSolicitudViajeImpl.traerSolicitudesTerrestreByEstatus(
                                e1, Constantes.CERO, sesion.getUsuario().getId(), " AND s.fecha_salida >= CAST('NOW' AS DATE)"));
                getListSolicitudesVo().addAll(
                        sgSolicitudViajeImpl.traerSolicitudesAereasByEstatus(e2, sesion.getUsuario().getId(), sesion.getIdRol()));
                setCountSVT(getListSolicitudesVo().size());
            } else if (e2 == 0 && e1 == Constantes.ESTATUS_JUSTIFICAR) {
                setListSolicitudesVo(
                        sgSolicitudViajeImpl.traerSolicitudesTerrestreByEstatus(
                                e1, Constantes.CERO, null, " AND s.fecha_salida >= CAST('NOW' AS DATE)"));
                getListSolicitudesVo().addAll(
                        sgSolicitudViajeImpl.traerSolicitudesTerrestreByEstatus(
                                Constantes.ESTATUS_CON_CENTOPS, Constantes.CERO, null, " AND s.fecha_salida >= CAST('NOW' AS DATE)"));
                setCountSVT(getListSolicitudesVo().size());
            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(e);
        }
    }

    public void aprobar() {

        if (validaSeleccion()) {
            aprobarSV();
        } else {
            FacesUtils.addErrorMessage("Debe de seleccionar al menos una solictud");
        }
    }

    public boolean validaSeleccion() {
        boolean val = false;
        for (SolicitudViajeVO vo : getListSolicitudesVo()) {
            if (vo.isSelect()) {
                val = true;
                break;
            }
        }
        return val;

    }

    public void aprobarSV() {

        ArrayList<String> cod = new ArrayList<>();
        boolean aprobada = false;
        ArrayList<String> jusCod = new ArrayList<>();
        
        for (SolicitudViajeVO vo : listSolicitudesVo) {
            if (vo.isSelect()) {                
                if (siManejoFechaImpl.validaFechaSalidaViaje(vo.getFechaSalida(), vo.getHoraSalida())) {
                    if (vo.getViajeros().size() > 0) {
                        if (vo.getIdSgTipoEspecifico() == Constantes.TIPO_ESPECIFICO_SOLICITUD_TERRESTRE) {
                            if (vo.getIdEstatus() == Constantes.ESTATUS_APROBAR) {
                                if (siManejoFechaImpl.dateIsTomorrow(vo.getFechaSalida())
                                        && (siManejoFechaImpl.validaHoraMaximaAprobacion(sesion.getIdRol(), Constantes.HORA_MAXIMA_APROBACION))) {
                                    getJus().add(vo.getIdSolicitud());
                                    jusCod.add(vo.getCodigo());
                                } else if (siManejoFechaImpl.salidaProximoLunes(new Date(), vo.getFechaSalida(), vo.getIdEstatus())) {
                                    getJus().add(vo.getIdSolicitud());
                                    jusCod.add(vo.getCodigo());
                                } else {
                                    aprobada = estatusAprobacionService.aprobarSolicitud(
                                            estatusAprobacionService.buscarEstatusAprobacionPorIdSolicitudIdEstatus(
                                                    vo.getIdSolicitud(), vo.getIdEstatus()).getId(), sesion.getUsuario().getId());
                                }
                            } else {
                                aprobada = estatusAprobacionService.aprobarSolicitud(
                                        estatusAprobacionService.buscarEstatusAprobacionPorIdSolicitudIdEstatus(
                                                vo.getIdSolicitud(), vo.getIdEstatus()).getId(), sesion.getUsuario().getId());
                            }
                        }
                    } else {
                        FacesUtils.addErrorMessage("No es posible aprobar la solicitud" + vo.getCodigo() + " debido a que no cuenta con ningun viajero");
                    }

                } else {
                    FacesUtils.addInfoMessage("No se puede aprobar la solicitud: " + vo.getCodigo() + " debido a que su fecha de salida ya ha pasado.");
                }

                if (aprobada) {
                    cod.add(vo.getCodigo());
                }

            }
        }
        codigos += ", " + codigos;        
        //if (aprobada) {
        if(cod.size() > 0){
            FacesUtils.addInfoMessage("Se Aprobo la(s) Solicitud(es) siguiente(s): " +  cod.stream().collect(Collectors.joining(",")));
            mostrarSolicitudesByAprobar();
        } else {
            FacesUtils.addErrorMessage("ocurrio un error inesperado favor de comunicarse con el equipo del SIA al correo soportesia@ihsa.mx");
        }
        if (!getJus().isEmpty()) {
            codigos = ", " + codigos;
            PrimeFaces.current().executeScript(";$(modalJustificar).modal('show');");
        }
    }

    public void popMotivoCancelar() {
        if (validaSeleccion()) {
            PrimeFaces.current().executeScript(";$(modalCancelar).modal('show');");
        } else {
            FacesUtils.addErrorMessage("Debe de seleccionar al menos una Solicitud para poder Cancelar");
        }
    }

    public void cancelarSolictud() {
        setCodigos("");
        boolean cancelar = false;
        ArrayList<String> cod = new ArrayList<>();
        for (SolicitudViajeVO vo : listSolicitudesVo) {
            if (vo.isSelect()) {
                try {
                    cancelar = estatusAprobacionService.cancelarSolicitud(
                            estatusAprobacionService.buscarEstatusAprobacionPorIdSolicitudIdEstatus(vo.getIdSolicitud(), vo.getIdEstatus()).getId(),
                            getMotivo(), sesion.getUsuario().getId(), true, false, Constantes.FALSE);
                    if (cancelar) {
                        cod.add(vo.getCodigo());
                    }
                } catch (Exception ex) {
                    Logger.getLogger(SolicitudViajeBeanModel.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        setMotivo("");
        setCodigos(Joiner.on(",").join(cod));
        if (cancelar) {
            FacesUtils.addInfoMessage("Se cancela(n) la(s) Solicitud(es) siguiente(s): " + getCodigos());
            mostrarSolicitudesByAprobar();
        } else {
            FacesUtils.addErrorMessage("ocurrio un error inesperado favor de comunicarse con el equipo del SIA al correo soportesia@ihsa.mx");
        }
    }

    public boolean aprobarWhitJustificacion() {
        boolean regresa = false;
        EstatusAprobacionSolicitudVO e = new EstatusAprobacionSolicitudVO();
        if (getJus() != null && !getJus().isEmpty()) {
            if (getMotivo() != null && !getMotivo().isEmpty()) {
                for (int i : getJus()) {
                    e = estatusAprobacionService.buscarEstatusAprobacionPorIdSolicitudIdEstatus(i, Constantes.ESTATUS_APROBAR);
                    estatusAprobacionService.aprobarJustificandoSolicitud(e.getId(), i, getMotivo(), sesion.getUsuario().getId());
                    regresa = Constantes.TRUE;
                }
            }

        }
        PrimeFaces.current().executeScript(";$(modalJustificar).modal('hide');");
        return regresa;
    }

    public void cargarListaViajeros() {
        setSolicitudViajeVO(new SolicitudViajeVO());
        setListViajeroBajarVO(new ArrayList<>());
        String cod = "";
        int count = 0;
        for (SolicitudViajeVO vo : listSolicitudesVo) {
            if (vo.isSelect()) {
                cod = vo.getCodigo();
                setSolicitudViajeVO(vo);
                count++;
            }
        }
        if (count == 1) {
            usuariosActivos(getSolicitudViajeVO().getIdGerencia());
            //inicializarlistInvitados(false);
            PrimeFaces.current().executeScript(";$(modalAddViajeros).modal('show');");
        } else if (count > 1) {
            setSolicitudViajeVO(new SolicitudViajeVO());
            FacesUtils.addErrorMessage("Solo se debe de seleccionar una Solicitud para poder agregar viajeros");
        } else {
            setSolicitudViajeVO(new SolicitudViajeVO());
            FacesUtils.addErrorMessage("Debe de seleccionar una Solicitud para poder agregar viajeros");
        }
    }

    public void usuariosActivos(int idGerencia) {
        usuariosGerenciaVO = apCampoUsuarioRhPuestoImpl.traerUsurioGerenciaCampo(idGerencia, sesion.getUsuario().getApCampo().getId());
    }

    public List<String> invitadoListener(String cadena) {
        setInvitado("");
        List<String> nombres = new ArrayList<>();
        List<InvitadoVO> invs = traerInvitados(cadena);
        invs.stream().forEach(us -> {
            nombres.add(us.getNombre() + " // " + us.getEmpresa());
        });
        return nombres;
    }

    public List<InvitadoVO> traerInvitados(String cadena) {
        return sgInvitadoImpl.buscarInvitadoParteNombre(cadena);
    }

    public void addEmpleado() {
        boolean agregar = true;
        //valida que el viajero no se encuentre en la lista
        for (ViajeroVO vo : getSolicitudViajeVO().getViajeros()) {
            if (idUsuario.equals(vo.getIdUsuario())) {
                FacesUtils.addErrorMessage("No se puede agregar al mismo viajero mas de una vez");
                agregar = false;
                break;
            }
        }

        //Valida que no se encuentre en la lista de bajas
        if (agregar) {
            ViajeroVO v = null;
            UsuarioVO us = usuarioImpl.findById(idUsuario);
            v = new ViajeroVO();
            if (v != null) {
                v.setIdInvitado(0);
                v.setInvitado("");
                v.setEmpleado(Boolean.TRUE);
                v.setUsuario(us.getNombre());
                v.setIdUsuario(us.getId());
                v.setTipoViajero(Constantes.SG_TIPO_ESPECIFICO_EMPLEADO);
                v.setEsEmpleado(Constantes.TRUE);
                v.setCorreo("");
                v.setTelefono("");
            }
            v.setId(0);
            v.setCodigoSolicitudViaje(getSolicitudViajeVO().getCodigo());
            v.setFechaSalida(getSolicitudViajeVO().getFechaSalida());
            v.setHoraSalida(getSolicitudViajeVO().getHoraSalida());
            v.setFechaRegreso(getSolicitudViajeVO().getFechaRegreso());
            v.setHoraRegreso(getSolicitudViajeVO().getHoraRegreso());
            v.setEstancia(Constantes.BOOLEAN_FALSE);
            v.setEstanciaB(Constantes.FALSE);
            v.setRedondo(getSolicitudViajeVO().isRedondo() ? Constantes.BOOLEAN_TRUE : Constantes.BOOLEAN_FALSE);
            v.setDestino(getSolicitudViajeVO().getDestino());
            v.setIdSolicitudViaje(getSolicitudViajeVO().getIdSolicitud());
            v.setSgSolicitudEstancia(0);
            v.setIdViaje(0);
            v.setIdOrigen(getSolicitudViajeVO().getIdOficinaOrigen());
            v.setIdDestino(getSolicitudViajeVO().getIdOficinaDestino() != Constantes.CERO ? getSolicitudViajeVO().getIdOficinaDestino() : getSolicitudViajeVO().getIdSiCiudadDestino());

            getSolicitudViajeVO().getViajeros().add(v);
            //
            idUsuario = "";
        }
    }

    public void addInvitado() {

        InvitadoVO inv = null;
        boolean agregar = true;
        String[] cad = invitado.split("//");
        ViajeroVO v = null;
        inv = sgInvitadoImpl.buscarInvitado(cad[0].trim());
        //valida que el viajero no se encuentre en la lista
        for (ViajeroVO vo : getSolicitudViajeVO().getViajeros()) {
            if (inv.getIdInvitado() == vo.getIdInvitado()) {
                FacesUtils.addErrorMessage("No se puede agregar al mismo viajero mas de una vez");
                agregar = false;
                break;
            }
        }

        //Valida que no se encuentre en la lista de bajas
        if (agregar) {
            if (inv != null) {
                v = new ViajeroVO();
                v.setIdInvitado(inv.getIdInvitado());
                v.setInvitado(inv.getNombre());
                v.setUsuario("null");
                v.setIdUsuario("null");
                v.setTipoViajero(Constantes.SG_TIPO_ESPECIFICO_INVITADO);
                v.setEsEmpleado(Constantes.FALSE);
                v.setCorreo("");
                v.setTelefono("");
            }
            v.setId(0);
            v.setCodigoSolicitudViaje(getSolicitudViajeVO().getCodigo());
            v.setFechaSalida(getSolicitudViajeVO().getFechaSalida());
            v.setHoraSalida(getSolicitudViajeVO().getHoraSalida());
            v.setFechaRegreso(getSolicitudViajeVO().getFechaRegreso());
            v.setHoraRegreso(getSolicitudViajeVO().getHoraRegreso());
            v.setEstancia(Constantes.BOOLEAN_FALSE);
            v.setEstanciaB(Constantes.FALSE);
            v.setRedondo(getSolicitudViajeVO().isRedondo() ? Constantes.BOOLEAN_TRUE : Constantes.BOOLEAN_FALSE);
            v.setDestino(getSolicitudViajeVO().getDestino());
            v.setIdSolicitudViaje(getSolicitudViajeVO().getIdSolicitud());
            v.setSgSolicitudEstancia(0);
            v.setIdViaje(0);
            v.setIdOrigen(getSolicitudViajeVO().getIdOficinaOrigen());
            v.setIdDestino(getSolicitudViajeVO().getIdOficinaDestino() != Constantes.CERO ? getSolicitudViajeVO().getIdOficinaDestino() : getSolicitudViajeVO().getIdSiCiudadDestino());

            getSolicitudViajeVO().getViajeros().add(v);

        }
        invitado = "";
    }

    public void removeViajeros(int idViajero, String idUsuario, int idInvitado) {
        if (idViajero > 0) {
            for (ViajeroVO vo : getSolicitudViajeVO().getViajeros()) {
                if (idViajero == vo.getId()) {
                    getListViajeroBajarVO().add(vo);
                    getSolicitudViajeVO().getViajeros().remove(vo);
                    break;
                }
            }
        } else {
            for (ViajeroVO vo : getSolicitudViajeVO().getViajeros()) {
                if (idViajero == 0) {
                    if (vo.getIdInvitado() > 0 && idInvitado > 0) {
                        if (vo.getIdInvitado() == idInvitado) {
                            getSolicitudViajeVO().getViajeros().remove(vo);
                            break;
                        }
                    } else {
                        if (idUsuario.equals(vo.getIdUsuario())) {
                            getSolicitudViajeVO().getViajeros().remove(vo);
                            break;
                        }
                    }
                }
            }

        }

    }

    public void addEditOrRemoveViajeros() throws Exception {
        List<ViajeroVO> lista = getSolicitudViajeVO().getViajeros();
        String motivo = "Eliminado por " + sesion.getUsuario().getNombre();

        if (!lista.isEmpty()) {
            boolean estanciaActual = false;
            int idestancia = 0;
            for (ViajeroVO vo : lista) {
                estanciaActual = (vo.isEstancia());
                if (vo.getSgSolicitudEstancia() > 0) {
                    idestancia = vo.getSgSolicitudEstancia();
                }
                if (vo.getId() > 0) {
                    if (estanciaActual != vo.isEstanciaB()) {
                        sgViajeroImpl.update(vo.getId(), vo.getIdUsuario(), vo.getIdInvitado(), vo.getIdSolicitudViaje(), Constantes.CERO,
                                vo.getSgSolicitudEstancia(), vo.isEstanciaB(), sesion.getUsuario().getId());

                    }
                } else {
                    vo.setSgSolicitudEstancia(idestancia);
                    if (estanciaActual != vo.isEstanciaB()) {
                        vo.setEstancia(estanciaActual ? Constantes.BOOLEAN_TRUE : Constantes.BOOLEAN_FALSE);
                    }
                    sgViajeroImpl.guardarViajero(vo.getIdInvitado(), vo.getIdUsuario(), vo.getSgSolicitudEstancia(),
                            vo.getIdSolicitudViaje(), Constantes.CERO, "", sesion.getUsuario().getId(), vo.isEstancia(), vo.isRedondo());
                }
            }
            for (ViajeroVO vob : getListViajeroBajarVO()) {
                if (vob.getId() > 0) {
                    sgViajeroImpl.delete(vob.getId(), sesion.getUsuario().getId(), motivo);
                }
            }
        }
        PrimeFaces.current().executeScript(";$(modalAddViajeros).modal('hide');");
        mostrarSolicitudesByAprobar();
    }

    public void selecionarTodoTer() {
        listSolicitudesVo.stream().forEach(sv -> {
            sv.setSelect(selectTodo);
        });
    }
}
