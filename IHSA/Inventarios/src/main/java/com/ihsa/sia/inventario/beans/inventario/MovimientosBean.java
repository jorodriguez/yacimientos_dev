package com.ihsa.sia.inventario.beans.inventario;

import com.ihsa.sia.inventario.beans.LocalAbstractBean;
import com.ihsa.sia.inventario.beans.RolesBean;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import sia.constantes.Constantes;
import static sia.constantes.Constantes.INV_TRANSACCION_STATUS_PREPARACION;
import static sia.constantes.Constantes.INV_TRANSACCION_STATUS_TRASPASO_PENDIENTE_REVISION;
import sia.excepciones.SIAException;
import sia.inventarios.service.AlmacenRemote;
import sia.inventarios.service.TransaccionRemote;
import sia.modelo.usuario.vo.UsuarioVO;
import sia.modelo.vo.inventarios.AlmacenVO;
import sia.modelo.vo.inventarios.TransaccionVO;
/**
 *
 * @author Aplimovil SA de CV
 */
@Named(value = "movimientos")
@ViewScoped
public class MovimientosBean extends LocalAbstractBean<TransaccionVO, Integer>
	implements Serializable {

    @Inject
    private TransaccionRemote servicio;
    @Inject
    private AlmacenRemote almacenServicio;

    private String motivoRechazo;

    @Inject
    RolesBean rolesBean;

    List<AlmacenVO> almacenes;

    public MovimientosBean() {
	super(TransaccionVO.class);
    }

    @Override
    @PostConstruct
    public void init() {
	mostrarMensajeEnSesion();
	super.init();
	almacenes = almacenServicio.buscarPorFiltros(new AlmacenVO(), principal.getUser().getIdCampo());
    }

    @Override
    public TransaccionRemote getServicio() {
	return servicio;
    }

    @Override
    protected String mensajeCrearKey() {
	return "";
    }

    @Override
    protected String mensajeEditarKey() {
	return "";
    }

    @Override
    protected String mensajeEliminarKey() {
	return "sia.inventarios.movimientos.eliminarMensaje";
    }

    public void procesar(Integer transaccionId) {
	try {
	    servicio.procesar(transaccionId, getUserName(), principal.getUser().getIdCampo());
	    addInfoMessage(obtenerCadenaDeRecurso("sia.inventarios.movimientos.procesarMensaje"));
	    cargarListaConFiltros();
	} catch (SIAException ex) {
	    ManejarExcepcion(ex);
	}
    }

    public void confirmar(Integer transaccionId) {
	try {
	    servicio.confirmar(transaccionId, getUserName(), principal.getUser().getIdCampo());
	    addInfoMessage(obtenerCadenaDeRecurso("sia.inventarios.movimientos.confirmarMensaje"));
	    cargarListaConFiltros();
	} catch (SIAException ex) {
	    ManejarExcepcion(ex);
	}
    }

    public void rechazar(Integer transaccionId) {
	try {
	    servicio.rechazar(transaccionId, motivoRechazo, getUserName());
	    motivoRechazo = "";
	    addInfoMessage(obtenerCadenaDeRecurso("sia.inventarios.movimientos.rechazarMensaje"));
	    cargarListaConFiltros();
	} catch (SIAException ex) {
	    ManejarExcepcion(ex);
	}
    }

    public boolean tienePermisoParaCambios(TransaccionVO vo) throws SIAException {
	UsuarioVO usuarioVO = rolesBean.getUsuario();

	if (rolesBean.esAdministradorDeInventarios()) {
	    return true;
	} else if (rolesBean.esResponsableDeAlmacen()) {
	    try {
		AlmacenVO almacenVO = almacenServicio.buscar(vo.getAlmacenId());

		if (almacenVO != null) {
		    String responsable1UsuarioId = almacenVO.getResponsable1UsuarioId();
		    String responsable2UsuarioId = almacenVO.getResponsable2UsuarioId();

		    return ((responsable1UsuarioId != null && responsable1UsuarioId.equals(usuarioVO.getId())) || (responsable2UsuarioId != null && responsable2UsuarioId.equals(usuarioVO.getId())));
		} else {
		    return false;
		}
	    } catch (NullPointerException ex) {
		return false;
	    } catch (SIAException ex) {
		throw new SIAException(ex.getMessage());
	    }
	} else if (rolesBean.esEmpleadoDeAlmacen()) {
	    return vo.getGeneroId().equals(usuarioVO.getId());
	} else {
	    return false;
	}
    }

    public boolean tienePermisoSobreTraspaso(TransaccionVO vo) throws SIAException {
	if (!vo.getTipoMovimiento().equals(Constantes.INV_MOVIMIENTO_TIPO_TRASPASO_SALIENTE)) {
	    return false;
	}

	if (rolesBean.esAdministradorDeInventarios()) {
	    return true;
	} else if (rolesBean.esResponsableDeAlmacen()) {
	    UsuarioVO usuarioVO = rolesBean.getUsuario();
	    AlmacenVO almacenDestinoVO = almacenServicio.buscar(vo.getTraspasoAlmacenDestinoId());

	    if (almacenDestinoVO != null) {
		String responsable1UsuarioId = almacenDestinoVO.getResponsable1UsuarioId();
		String responsable2UsuarioId = almacenDestinoVO.getResponsable2UsuarioId();

		return ((responsable1UsuarioId != null && responsable1UsuarioId.equals(usuarioVO.getId())) || (responsable2UsuarioId != null && responsable2UsuarioId.equals(usuarioVO.getId())));
	    } else {
		return false;
	    }
	} else {
	    return false;
	}
    }

    public boolean puedeEditar(TransaccionVO vo) throws SIAException {
	return (vo.getStatus().equals(INV_TRANSACCION_STATUS_PREPARACION) && tienePermisoParaCambios(vo));
    }

    public boolean puedeListarArticulos(TransaccionVO vo) throws SIAException {
	return !vo.getStatus().equals(INV_TRANSACCION_STATUS_PREPARACION);
    }

    public boolean puedeEliminar(TransaccionVO vo) throws SIAException {
	return (vo.getStatus().equals(INV_TRANSACCION_STATUS_PREPARACION) && tienePermisoParaCambios(vo));
    }

    public boolean puedeConfirmar(TransaccionVO vo) throws SIAException {
	return (vo.getStatus().equals(INV_TRANSACCION_STATUS_TRASPASO_PENDIENTE_REVISION) && tienePermisoSobreTraspaso(vo));
    }

    public boolean puedeRechazar(TransaccionVO vo) throws SIAException {
	return (vo.getStatus().equals(INV_TRANSACCION_STATUS_TRASPASO_PENDIENTE_REVISION) && tienePermisoSobreTraspaso(vo));
    }

    public boolean puedeProcesar(TransaccionVO vo) throws SIAException {
	return (vo.getStatus().equals(INV_TRANSACCION_STATUS_PREPARACION) && tienePermisoParaCambios(vo));
    }

    public String getMotivoRechazo() {
	return motivoRechazo;
    }

    public void setMotivoRechazo(String motivoRechazo) {
	this.motivoRechazo = motivoRechazo;
    }

    public List<AlmacenVO> getAlmacenes() {
	return almacenes;
    }

    private void mostrarMensajeEnSesion() {
	FacesContext context = FacesContext.getCurrentInstance();
	String mensaje = (String) context
		.getExternalContext().getSessionMap().get("mensaje");
	if (null != mensaje) {
	    context.getExternalContext().getSessionMap().remove("mensaje");
	    addInfoMessage(mensaje);
	}
    }
}
