package sia.servicios.oficio.impl;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.mail.MessagingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import sia.constantes.Constantes;
import sia.excepciones.ExistingItemException;
import sia.excepciones.InsufficientPermissionsException;
import sia.excepciones.InvalidBusinessOperationException;
import sia.excepciones.InvalidFileTypeException;
import sia.excepciones.InvalidStateException;
import sia.excepciones.InvalidValuesException;
import sia.excepciones.MissingRequiredValuesException;
import sia.excepciones.PromotionFailedException;
import sia.excepciones.SIAException;
import sia.excepciones.UnavailableItemException;
import sia.modelo.ApCampoGerencia;
import sia.modelo.CoPrivacidad;
import sia.modelo.Estatus;
import sia.modelo.OfOficio;
import sia.modelo.OfOficioAsociado;
import sia.modelo.OfOficioSiMovSiAdjunto;
import sia.modelo.OfOficioSiMovimiento;
import sia.modelo.OfOficioUsuario;
import sia.modelo.OfTipoOficio;
import sia.modelo.SiAdjunto;
import sia.modelo.SiMovimiento;
import sia.modelo.SiOperacion;
import sia.modelo.Usuario;
import sia.modelo.campo.usuario.puesto.vo.CompaniaBloqueGerenciaVo;
import sia.modelo.oficio.vo.AdjuntoOficioVo;
import sia.modelo.oficio.vo.InformacionMovimientoVo;
import sia.modelo.oficio.vo.InformacionOficioVo;
import sia.modelo.oficio.vo.MovimientoVo;
import sia.modelo.oficio.vo.OficioEntradaVo;
import sia.modelo.oficio.vo.OficioPromovibleVo;
import sia.modelo.oficio.vo.OficioSalidaVo;
import sia.modelo.oficio.vo.OficioVo;
import sia.modelo.oficio.vo.PermisosVo;
import sia.modelo.oficio.vo.PrivacidadOficio;
import sia.modelo.oficio.vo.Promovible;
import sia.modelo.oficio.vo.ResultadosConsultaVo;
import sia.modelo.sistema.AbstractFacade;
import sia.modelo.sistema.vo.CorreoVo;
import sia.modelo.usuario.vo.UsuarioRolVo;
import sia.modelo.usuario.vo.UsuarioVO;
import sia.modelo.vo.ApCampoGerenciaVo;
import sia.notificaciones.oficio.impl.NotificacionOficioImpl;
import sia.servicios.campo.nuevo.impl.ApCampoGerenciaImpl;
import sia.servicios.catalogos.impl.EstatusImpl;
import sia.servicios.catalogos.impl.UsuarioImpl;
import sia.servicios.oficio.interceptor.ValidacionEdicionArchivoAdjuntoInterceptor;
import sia.servicios.oficio.interceptor.ValidacionSeguimientoInterceptor;
import sia.servicios.sistema.impl.SiAdjuntoImpl;
import sia.servicios.sistema.impl.SiMovimientoImpl;
import sia.servicios.sistema.impl.SiUsuarioRolImpl;
import sia.util.UtilLog4j;
import sia.util.UtilSia;

/**
 * Implementa los métodos de negocio para el módulo de Control de Oficios.
 *
 * @author esapien
 */

@Stateless 
public class OfOficioImpl extends AbstractFacade<OfOficio> {

    // <editor-fold defaultstate="collapsed" desc="Atributos del bean">
    //private final static Logger logger = Logger.getLogger(OfOficioImpl.class.getName());
    @Inject
    private SiAdjuntoImpl siAdjuntoRemote;
    @Inject
    private ApCampoGerenciaImpl apCampoGerenciaServicioRemoto;
    @Inject
    private EstatusImpl estatusServicioRemoto;
    @Inject
    private SiMovimientoImpl movimientoServicioRemoto;
    @Inject
    private UsuarioImpl usuarioServicioRemoto;
    @Inject
    private SiUsuarioRolImpl usuarioRolServicioRemoto;
    @Inject
    private NotificacionOficioImpl notificacionOficioServicioRemoto;
    @Inject
    private OfOficioSiMovimientoImpl oficioMovimientoServicioRemoto;
    @Inject
    private OfOficioAsociadoImpl oficioAsociadoRemoto;
    @Inject
    private OfOficioUsuarioImpl oficioUsuarioRemoto;
    @Inject
    private OfOficioSiMovSiAdjuntoImpl oficioMovAdjuntoServicioRemoto;
    
    @Inject
    private OfOficioUsuarioImpl oficioUsuarioRemote;
    

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;
    

    private final String queryBase;

    private final String queryBaseGroupBy;

    //private final String queryOficiosAsociados;
    private final String queryMovimientos;

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Métodos del ciclo de vida del bean">
    /**
     * Constructor
     *
     */
    public OfOficioImpl() {

        super(OfOficio.class);

        // construir query base para consultas
        queryBase = "select "
                // tipo de oficio
                + " tof.id as ID_TIPO_OFICIO, \n"
                + " tof.nombre as TIPO_OFICIO, \n"
                // oficio
                + " of1.ID as ID_OFICIO, \n"
                + " of1.numero_oficio, \n"
                + " of1.fecha_oficio, \n"
                + " of1.fecha_genero, \n"
                + " of1.asunto as asunto_oficio, \n"
                + " of1.observaciones as observaciones_oficio, \n"
                // compañía
                + " c.rfc, \n"
                + " c.NOMBRE as COMPANIA,\n"
                + " c.SIGLAS as COMPANIA_SIGLAS,\n"
                // bloque
                + " ap_c.id as ID_BLOQUE, \n"
                + " ap_c.NOMBRE as BLOQUE, \n"
                // gerencia
                + " g.id as ID_GERENCIA, \n"
                + " g.nombre as GERENCIA, \n"
                // estatus
                + " est.id as ID_ESTATUS, \n"
                + " est.nombre as ESTATUS, \n"
                // usuario
                + " us1.id as USUARIO_GENERO_ID, \n"
                + " us1.nombre as USUARIO_GENERO, \n"
                + " of1.eliminado, \n"
                + " of1.urgente, \n"
                + " of1.seguimiento, \n"
                + " of1.co_privacidad, \n"
                + " (SELECT array_agg(usuario) FROM OF_OFICIO_USUARIO WHERE of_oficio = of1.id) \n"
                + "from OF_OFICIO of1 \n"
                + " inner join AP_CAMPO_GERENCIA ap_cg on (of1.AP_CAMPO_GERENCIA = ap_cg.ID) \n"
                + " inner join AP_CAMPO ap_c on (ap_cg.AP_CAMPO = ap_c.ID) \n"
                + " inner join GERENCIA g on (ap_cg.GERENCIA = g.ID) \n"
                + " inner join COMPANIA c on (ap_c.COMPANIA = c.RFC) \n"
                + " inner join OF_TIPO_OFICIO tof on (of1.OF_TIPO_OFICIO = tof.ID) \n"
                + " inner join ESTATUS est on (of1.ESTATUS = est.ID) \n"
                + " inner join USUARIO us1 on (of1.GENERO = us1.ID) \n"
                + "where 1=1 \n";

        //queryBase = sb.toString();

        queryBaseGroupBy = getQueryBaseGroupBy();

        /*
	 sb = new StringBuilder();
	 sb
	 .append(" SELECT ")

	 .append("    of2.ID AS ID_PADRE, ")
	 .append("    of2.NUMERO_OFICIO AS NUMERO_OFICIO_PADRE,  ")
	 .append("    of2.ELIMINADO AS ELIMINADO_PADRE,  ")
	 .append("    ap_c2.compania AS COMPANIA_ID_PADRE,  ")
	 .append("    ap_cg2.ap_campo AS CAMPO_ID_PADRE,  ")
	 .append("    ap_cg2.gerencia AS GERENCIA_ID_PADRE,  ")
	 .append("    of2.OF_TIPO_OFICIO AS ID_TIPO_OFICIO_PADRE, ")
	 .append("    of2.ESTATUS AS ESTATUS_PADRE, ")

	 .append("    of1.ID as ID_HIJO, ")
	 .append("    of1.NUMERO_OFICIO as NUMERO_OFICIO_HIJO, ")
	 .append("    of1.ELIMINADO AS ELIMINADO_HIJO,  ")
	 .append("    ap_c1.compania AS COMPANIA_ID_HIJO,  ")
	 .append("    ap_cg1.ap_campo AS CAMPO_ID_HIJO,  ")
	 .append("    ap_cg1.gerencia AS GERENCIA_ID_HIJO, ")
	 .append("    of1.OF_TIPO_OFICIO AS ID_TIPO_OFICIO_HIJO, ")
	 .append("    of1.ESTATUS AS ESTATUS_HIJO ")

	 .append("FROM ")

	 .append("    OF_OFICIO of1 ")
	 .append("    inner join OF_OFICIO of2 on (of1.OF_OFICIO = of2.ID) ")
	 .append("    inner join AP_CAMPO_GERENCIA ap_cg2 on (of2.AP_CAMPO_GERENCIA = ap_cg2.ID) ")
	 .append("    inner join AP_CAMPO ap_c2 on (ap_cg2.AP_CAMPO = ap_c2.ID)  ")
	 .append("    inner join AP_CAMPO_GERENCIA ap_cg1 on (of1.AP_CAMPO_GERENCIA = ap_cg1.ID) ")
	 .append("    inner join AP_CAMPO ap_c1 on (ap_cg1.AP_CAMPO = ap_c1.ID)  ")

	 .append("WHERE 1=1 ");

	 queryOficiosAsociados = sb.toString();*/
        queryMovimientos = "SELECT \n"
                + "  ofic_mov.ID as ID_OFICIO_MOVIMIENTO,  \n"
                + "  ofic.ID as ID_OFICIO,  \n"
                + "  ofic.NUMERO_OFICIO,  \n"
                + "  oper.ID as ID_OPERACION, \n"
                + "  oper.NOMBRE as OPERACION, \n"
                + "  mov.ID as MOVIMIENTO_ID,  \n"
                + "  mov.MOTIVO as MOTIVO,  \n"
                + "  adj.ID as ADJUNTO_ID, \n"
                + "  adj.NOMBRE as ADJUNTO_NOMBRE, \n"
                + "  adj.URL as ADJUNTO_URL, \n"
                + "  adj.TIPO_ARCHIVO as ADJUNTO_TIPO_ARCHIVO, \n"
                + "  adj.PESO as ADJUNTO_PESO, \n"
                + "  adj.UUID as ADJUNTO_UUID, \n"
                + "  adj.ELIMINADO as ADJUNTO_ELIMINADO, \n"
                + "  mov.GENERO as USUARIO_MOVIMIENTO,  \n"
                + "  usu.NOMBRE as USUARIO_NOMBRE,  \n"
                + "  mov.FECHA_GENERO as FECHA_MOVIMIENTO,  \n"
                + "  mov.HORA_GENERO as HORA_MOVIMIENTO \n"
                + "FROM SI_MOVIMIENTO mov \n"
                + "  inner join USUARIO usu on (mov.GENERO = usu.ID) \n"
                + "  inner join OF_OFICIO_SI_MOVIMIENTO ofic_mov on (ofic_mov.SI_MOVIMIENTO = mov.ID) \n"
                + "  inner join OF_OFICIO ofic on (ofic_mov.OF_OFICIO = ofic.ID) \n"
                + "  inner join ESTATUS est on (ofic.ESTATUS = est.ID) \n"
                + "  inner join SI_OPERACION oper on (mov.SI_OPERACION = oper.ID) \n"
                + "  left outer join OF_OFICIO_SI_MOV_SI_ADJUNTO ofic_mov_adj on (ofic_mov_adj.OF_OFICIO_SI_MOVIMIENTO = ofic_mov.ID) \n"
                + "  left outer join SI_ADJUNTO adj on (ofic_mov_adj.SI_ADJUNTO = adj.ID) \n"
                + "WHERE 1=1 \n";

        //queryMovimientos = sb.toString();

    }

    /**
     * Proceso de inicialización del bean.
     *
     */
    @PostConstruct
    private void inicio() {

//        this.rutaRaizAdjuntos = siParametroRemote.find(1).getUploadDirectory();

    }

    /**
     * Genera claúsula GROUP BY para el query base de SQL de oficios. Los campos
     * corresponden a los campos de la consulta para realizar efectivamente el
     * agrupamiento para los usuariosRey con acceso a oficio restringido.
     *
     *
     */
    private String getQueryBaseGroupBy() {

        return " GROUP BY \n"
                + " tof.id, \n"
                + " tof.nombre, \n"
                // oficio
                + " of1.ID, \n"
                + " of1.numero_oficio, \n"
                + " of1.fecha_oficio, \n"
                + " of1.fecha_genero, \n"
                + " of1.asunto, \n"
                + " of1.observaciones, \n"
                // compañía
                + " c.rfc, \n"
                + " c.NOMBRE,\n"
                + " c.SIGLAS,\n"
                // bloque
                + " ap_c.id, \n"
                + " ap_c.NOMBRE, \n"
                // gerencia
                + " g.id, \n"
                + " g.nombre, \n"
                // estatus
                + " est.id, \n"
                + " est.nombre, \n"
                // usuario
                + " us1.id, \n"
                + " us1.nombre, \n"
                + " of1.eliminado, \n"
                + " of1.urgente, \n"
                + " of1.seguimiento, \n"
                + " of1.co_privacidad";

    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Métodos de negocio">
    /**
     * Envía correo de notificación de los nuevos oficios registrados.
     *
     */
    
    public void enviarNotificacionAltaOficios() throws MessagingException {

        getLogger().info(this, "@enviarNotificacionAltaOficios");

        List<List<UsuarioVO>> usuariosByCampo = 
                this.usuarioServicioRemoto.obtenerUsuariosPorModuloPermiso(
                        Constantes.OFICIOS_MODULO_ID,
                        Constantes.OFICIOS_PERMISO_RECIBIR_CORREO_ALTA_OFICIO);
        //List<UsuarioVO> usuarios = new ArrayList<>();
        List<String> para = new ArrayList<>();
        final Joiner j = Joiner.on(",").skipNulls();
        
        int campoActual = 0;
        //String nombreCampoActual= "";
        CorreoVo correo = new CorreoVo();
        correo.setCc(Constantes.VACIO);
        correo.setCco(Constantes.VACIO);

        // obtener oficios no notificados con archivo adjunto para informe de avance

        for (List<UsuarioVO> listVo : usuariosByCampo) {
            String correoPara = Constantes.VACIO;
            //List<UsuarioVO> usuarios = listVo;
            campoActual = listVo.get(0).getIdCampo();
            // nombreCampoActual = listVo.get(0).getCampo();

            for (UsuarioVO vo : listVo) {
                para.add(vo.getMail());
            }

            correoPara = j.join(para);
            correo.setPara(correoPara);
            
            getLogger().info(this, correoPara + " " + para.size() + " cantidad de usuarios en la lista de correos");
            
            para = new ArrayList<>();
            
            if (campoActual < 4 && campoActual > 0) {
                correo.setAsunto(Constantes.OFICIOS_CORREO_ASUNTO_IHSA_PEMEX
                        + UtilSia.getFechaActual_ddMMyyy());
            } else if ((campoActual < 9 && campoActual > 6) || (campoActual < 15 && campoActual > 11)) {
                correo.setAsunto(Constantes.OFICIOS_CORREO_ASUNTO_IHSA_CQ_PEMEX
                        + UtilSia.getFechaActual_ddMMyyy());
            }

            List<OficioPromovibleVo>oficios = this.buscarOficiosNoNotificados(campoActual);
            notificacionOficioServicioRemoto.enviarNotificacionAltaOficios(correo, oficios);
            // actualizar registros como notificados
            this.actualizarOficiosNotificados(oficios);
        }

    }

    //jevazquez 23/feb/2015 aprobado
    
    public void enviarNotificacionNoPromovidoOficios() throws MessagingException {

        getLogger().info(this, "@enviarNotificacionNoPromovidoOficios");

        final List<UsuarioRolVo> usuariosMty = 
                usuarioRolServicioRemoto.traerRolPorCodigo(
                        Constantes.OFICIOS_ROL_RECEPTOR_MONTERREY_CODIGO, 
                        Constantes.AP_CAMPO_DEFAULT, 
                        Constantes.MODULO_CONTROL_OFICIO
                );

        getLogger().info(this, "Usuarios encontrados = " + usuariosMty.size());

        final Iterator<UsuarioRolVo> itMty = usuariosMty.iterator();
        final List<UsuarioRolVo> usuariosRey = 
                usuarioRolServicioRemoto.traerRolPorCodigo(
                        Constantes.OFICIOS_ROL_RECEPTOR_REYNOSA_CODIGO, 
                        Constantes.AP_CAMPO_DEFAULT, 
                        Constantes.MODULO_CONTROL_OFICIO
                );

        getLogger().info(this, "Usuarios encontrados = " + usuariosRey.size());

        Iterator<UsuarioRolVo> itRey = usuariosRey.iterator();

        StringBuilder correos = new StringBuilder();

        while (itMty.hasNext()) {

            UsuarioRolVo vo = itMty.next();

            String correo = vo.getCorreo();

            correos.append(correo).append(Constantes.COMA);

        }
        
        while (itRey.hasNext()) {

            UsuarioRolVo vo = itRey.next();

            String correo = vo.getCorreo();

            correos.append(correo).append(Constantes.COMA);

        }

        // remover ultima coma
        correos.setLength(correos.length() - 1);

        getLogger().info(this, "Correos = " + correos.toString());

        CorreoVo correo = new CorreoVo();

        correo.setPara(correos.toString());
        correo.setCc(Constantes.VACIO);
        correo.setCco(Constantes.VACIO);
        correo.setAsunto(Constantes.OFICIOS_CORREO_ASUNTO_IHSA_PEMEX_NO_PROMOVIDOS
                + UtilSia.getFechaActual_ddMMyyy());

        // obtener oficios no notificados con archivo adjunto para informe de avance
        List<OficioPromovibleVo> oficios = this.buscarOficiosNoNotificadosSemana();

        notificacionOficioServicioRemoto.enviarNotificacionNoPromovidas(correo, oficios);

        getLogger().info(this, "OK correo");

        // actualizar registros como notificados
        //    this.actualizarOficiosNotificados(oficios);
    }

    /**
     *
     * @param oficios
     */
    private void actualizarOficiosNotificados(List<OficioPromovibleVo> oficios) {

        Date fecha = new Date();

        for (OficioVo vo : oficios) {
            // obtener entidad desde base de datos
            OfOficio entidad = this.find(vo.getOficioId());

            entidad.setFechaNotificoAlta(fecha);
            entidad.setHoraNotificoAlta(fecha);

            this.edit(entidad);
        }

    }

    /**
     * Valida los valores requeridos en el proceso de anulacion de un oficio.
     *
     * @param vo
     */
    private void validarAnulacionRequeridos(String motivo) throws MissingRequiredValuesException {

        StringBuilder sb = new StringBuilder();

        if (motivo == null || motivo.trim().length() == 0) {
            sb.append("Motivo de anulación");
        }

        if (sb.toString().length() > 0) {

            MissingRequiredValuesException ex = new MissingRequiredValuesException();
            ex.setValoresFaltantes(sb.toString());

            throw ex;

        }

    }

    /**
     * Valida si el oficio proporcionado se encuentra asociado por otro oficio.
     *
     * @param oficioId
     * @throws InvalidBusinessOperationException
     */
    private void validarAnulacionAsociados(Integer oficioId)
            throws InvalidBusinessOperationException {

        boolean isAsociado = oficioAsociadoRemoto.isAsociado(oficioId);

        if (isAsociado) {
            throw new InvalidBusinessOperationException();
        }
    }

    /**
     * Actualiza el estatus de un oficio a Anulado y registra la operación en la
     * bitácora.
     *
     * @param vo
     * @param idUsuario
     * @return
     */
    private OfOficio registrarAnulacionOficio(OficioPromovibleVo vo, String motivo, String idUsuario)
            throws MissingRequiredValuesException, InvalidBusinessOperationException {

        // validar que el oficio no se encuentre asociado
        validarAnulacionAsociados(vo.getOficioId());

        // validar datos requeridos para anulacion
        validarAnulacionRequeridos(motivo);

        // obtener entidad desde base de datos
        OfOficio entidad = this.find(vo.getOficioId());

        // modificar numero de oficio para permitir reutilizacion
        // del numero de oficio
        String nuevoNumeroOficio = entidad.getNumeroOficio()
                + Constantes.GUION
                + entidad.getId();

        vo.anular();

        // obtener id de estatus anulado de esta implementación
        Estatus estatus = new Estatus(vo.getEstatusId());

        Usuario usuario = new Usuario(idUsuario);

        Date fechaAnulacion = new Date();

        entidad.setNumeroOficio(nuevoNumeroOficio);
        entidad.setEstatus(estatus);
        entidad.setModifico(usuario);
        entidad.setFechaModifico(fechaAnulacion);
        entidad.setHoraModifico(fechaAnulacion);
        entidad.setEliminado(Constantes.BOOLEAN_TRUE);

        // actualizar estatus en base de datos
        this.edit(entidad);

        return entidad;

    }

    /**
     * Actualiza como eliminados a los archivos adjuntos de un oficio en el
     * proceso de anulación de un oficio.
     *
     * @param oficioId
     * @param usuarioId
     */
    private void registrarAnulacionAdjuntos(Integer oficioId, String usuarioId) {

        List<MovimientoVo> movimientos = this.obtenerMovimientos(oficioId);

        for (MovimientoVo movimiento : movimientos) {

            Integer adjuntoId = movimiento.getAdjunto().getId();

            if (adjuntoId != null && adjuntoId > 0) {

                siAdjuntoRemote.delete(adjuntoId, usuarioId);

            }

        }

    }

    /**
     * Realiza la anulación de un registro de oficio.
     *
     * @param vo
     * @param idUsuario
     */
    
    public void anularOficio(OficioPromovibleVo vo, String motivo, String idUsuario)
            throws MissingRequiredValuesException,
            InvalidBusinessOperationException {

        getLogger().info(this, "@anularOficio = " + vo);

        // registrar anulación de oficio
        OfOficio entidad = registrarAnulacionOficio(vo, motivo, idUsuario);

        // registrar movimiento
        InformacionMovimientoVo movimientoVo = new InformacionMovimientoVo();
        movimientoVo.setOficio(entidad);
        // obtener el ID de operación correspondiente a esta implementación de oficio
        movimientoVo.setOperacionId(vo.getOperacionId(entidad.getEstatus().getId()));
        movimientoVo.setMotivoMovimiento("Anulación de oficio: " + motivo);
        movimientoVo.setUsuarioId(idUsuario);

        registrarMovimiento(movimientoVo);

        // registrar anulacion de archivos adjuntos
        registrarAnulacionAdjuntos(entidad.getId(), idUsuario);

        // anular oficios asociados
        oficioAsociadoRemoto.borrarLogicoOficiosAsociados(vo.getOficioId(), idUsuario);

        // anular usuariosRey con acceso
        oficioUsuarioRemote.borrarLogicoUsuariosOficioRestringido(vo.getOficioId(), idUsuario);

    }

    /**
     *
     * @param vo
     * @param motivo
     * @param idUsuario
     */
    @Interceptors(ValidacionSeguimientoInterceptor.class)    
    public void activarSeguimiento(OficioVo vo, String motivo, String idUsuario) {

        OfOficio entidad = this.find(vo.getOficioId());

        entidad.setSeguimiento(Constantes.BOOLEAN_TRUE);

        this.edit(entidad);

        // registrar movimiento
        InformacionMovimientoVo movimientoVo = new InformacionMovimientoVo();
        movimientoVo.setOficio(entidad);
        movimientoVo.setOperacionId(Constantes.OFICIOS_OPERACION_ID_SEGUIMIENTO_OFICIO_ON);
        movimientoVo.setMotivoMovimiento("Activación de seguimiento: " + motivo);
        movimientoVo.setUsuarioId(idUsuario);

        registrarMovimiento(movimientoVo);

    }

    /**
     *
     * @param vo
     * @param motivo
     * @param idUsuario
     */
    @Interceptors(ValidacionSeguimientoInterceptor.class)
    
    public void desactivarSeguimiento(OficioVo vo, String motivo, String idUsuario) {

        OfOficio entidad = this.find(vo.getOficioId());

        entidad.setSeguimiento(Constantes.BOOLEAN_FALSE);

        this.edit(entidad);

        // registrar movimiento
        InformacionMovimientoVo movimientoVo = new InformacionMovimientoVo();
        movimientoVo.setOficio(entidad);
        movimientoVo.setOperacionId(Constantes.OFICIOS_OPERACION_ID_SEGUIMIENTO_OFICIO_OFF);
        movimientoVo.setMotivoMovimiento("Desactivación de seguimiento: " + motivo);
        movimientoVo.setUsuarioId(idUsuario);

        registrarMovimiento(movimientoVo);

    }

    /**
     * Promueve el estatus de un oficio en la base de datos.
     *
     * @param vo
     * @param idUsuario
     * @throws MissingRequiredValuesException
     */
    private OfOficio promoverActualizarEstatusOficio(OficioPromovibleVo vo, String idUsuario)
            throws
            InvalidStateException,
            PromotionFailedException,
            MissingRequiredValuesException {

        // obtener entidad desde base de datos
        OfOficio entidad = this.find(vo.getOficioId());

        // validar estatus
        int estatusId = entidad.getEstatus().getId();

        // verificar consistencia
        if (vo.getEstatusId() != estatusId) {
            throw new InvalidStateException("El estatus del oficio ha cambiado.");
        }

        // promover al siguiente estatus
        getLogger().info(this, "estatus antes de promocion = " + vo.getEstatusId());

        vo.promover();

        getLogger().info(this, "estatus despues de promocion = " + vo.getEstatusId());

        Estatus nuevoEstatus = estatusServicioRemoto.find(vo.getEstatusId());

        getLogger().info(this, "nuevo estatus = " + nuevoEstatus);

        entidad.setEstatus(nuevoEstatus);

        // datos de modificación
        Usuario usuario = new Usuario(idUsuario);
        entidad.setModifico(usuario);
        entidad.setFechaModifico(new Date());
        entidad.setHoraModifico(new Date());

        // actualizar estatus en la BD y registrar bitácora
        actualizarOficio(entidad);

        return entidad;

    }

    /**
     *
     * Promueve el oficio al estatus siguiente, según corresponda a su estatus y
     * tipo de oficio actual.
     *
     * Adicionalmente envía correo de notificación a los roles correspondientes.
     *
     * @param vo
     * @param usuario
     * @throws MissingRequiredValuesException
     * @throws InvalidStateException
     * @throws PromotionFailedException
     * @throws SIAException
     */
    
    public void promoverEstatusOficio(OficioPromovibleVo vo, Usuario usuario)
            throws MissingRequiredValuesException,
            InvalidStateException,
            PromotionFailedException,
            SIAException,
            MessagingException {

        getLogger().info(this, "@promoverEstatusOficio = " + vo);

        String idUsuario = usuario.getId();

        // para guardar achivo adjunto de promoción
        boolean requiereArchivoPromocion = vo.requiereArchivoAdjuntoPromocion();

        // promover estatus del oficio y actualizar en la base de datos
        OfOficio entidad = promoverActualizarEstatusOficio(vo, idUsuario);

        getLogger().info(this, "entidad null = " + (entidad == null));
        getLogger().info(this, "entidad.estatus null = " + (entidad.getEstatus() == null));
        getLogger().info(this, "entidad.estatus.id = " + entidad.getEstatus().getId());

        // registrar movimiento
        InformacionMovimientoVo movimientoVo = new InformacionMovimientoVo();
        movimientoVo.setOficio(entidad);
        // obtener el ID de operación correspondiente a esta implementación de oficio
        movimientoVo.setOperacionId(vo.getOperacionId(entidad.getEstatus().getId()));
        movimientoVo.setMotivoMovimiento(vo.getMotivoMovimiento(entidad.getEstatus().getId()));
        movimientoVo.setUsuarioId(idUsuario);

        OfOficioSiMovimiento oficioMovimiento
                = registrarMovimiento(movimientoVo);
        List<OfOficioSiMovimiento> listMov = new ArrayList<OfOficioSiMovimiento>();
        listMov.add(oficioMovimiento);

        // guardar archivo adjunto en caso que aplique
        // si se promovió a Recibido Pemex (solo oficios de tipo Salida),
        // guardar archivo requerido
        if (requiereArchivoPromocion) {

            // archivo adjunto
            // guardar registro de archivo adjunto
            AdjuntoOficioVo archivo = vo.getArchivoPromocion();

            // registrar archivo adjunto
            registrarAdjunto(archivo, listMov, idUsuario);

        }

        // validar si hay rol de usuario a notificar
        String codigo = vo.rolCodigoNotificarPromocionEstatusActual();

        getLogger().info(this, "oficio urgente = " + vo.isUrgente());

        // notificar solo oficios urgentes
        if (vo.isUrgente() && codigo != null) {
            notificarPromocionOficio(codigo, vo, usuario.getEmail());
        }

    }

    /**
     *
     * Envía correo de notificación de promoción de oficio a los usuariosRey con
     * el rol indicado, con copia al usuario que realiza la promoción.
     *
     * @param codigo Codigo de rol en la base de datos. Valor no nulo
     * @param vo Información del oficio a notificar
     * @param correoPromotor Correo del usuario que realiza la promoción del
     * oficio, para ser copiado en el correo (CC).
     */
    private void notificarPromocionOficio(
            String codigo,
            OficioPromovibleVo vo,
            String correoPromotor) throws MessagingException {

        getLogger().info(this, "@notificarPromocionOficio - rolId = " + codigo);

        Set<String> mailsPara = new HashSet<String>();
        Set<String> mailsCc = new HashSet<String>();
        Set<String> mailsCco = new HashSet<String>();

        // obtener todos los usuarios con estos roles y que tengan acceso al bloque
        // del oficio entre los bloques a los que está asignado
        List<UsuarioVO> usuarios
                = usuarioServicioRemoto.getUsuariosPorRolBloque(codigo, vo.getBloqueId());

        getLogger().info(this,
                "@notificarPromocionOficio - rolId = " + codigo
                + ", cant. usuarios = " + usuarios.size());

        for (UsuarioVO usuarioVo : usuarios) {

            getLogger().info(this,
                    "@notificarPromocionOficio - "
                    + "Agregando destinatario: rolId = " + codigo + ", "
                    + "usuarioId = " + usuarioVo.getId() + ", "
                    + "mail = " + usuarioVo.getMail());

            mailsPara.add(usuarioVo.getMail().trim());

        }

        // enviar correo general de notificación de promoción de oficio
        // CC al promotor del oficio
        mailsCc.add(correoPromotor);

        CorreoVo parametrosCorreo = new CorreoVo();

        parametrosCorreo.setSetPara(mailsPara);
        parametrosCorreo.setSetCc(mailsCc);
        parametrosCorreo.setSetCco(mailsCco);

        parametrosCorreo.setAsunto(Constantes.OFICIOS_CORREO_ASUNTO_PROMOCION + UtilSia.getFechaActual_ddMMyyy());

        // enviar correo de notificación
        notificacionOficioServicioRemoto.notificarPromocionOficio(parametrosCorreo, vo);

    }

    /*
     * jevazquez 18/02/15 aprobado
     */
    private void notificarModificacionOficio(OficioVo vo) throws MessagingException {

        //  getLogger().info(this, "@notificarPromocionOficio - rolId = " + );
        Set<String> mailsPara = new HashSet<>();
        Set<String> mailsCc = new HashSet<>();
        Set<String> mailsCco = new HashSet<>();

        // obtener todos los usuariosRey con estos roles y que tengan acceso al bloque
        // del oficio entre los bloques a los que está asignado

        List<UsuarioVO> usuariosRey = usuarioServicioRemoto.getUsuariosPorRolBloque(
                Constantes.OFICIOS_ROL_RECEPTOR_REYNOSA_CODIGO, vo.getBloqueId());

        for (UsuarioVO usuarioVo : usuariosRey) {
            mailsPara.add(usuarioVo.getMail().trim());
        }
        
        List<UsuarioVO> usuariosMty = usuarioServicioRemoto.getUsuariosPorRolBloque(
                Constantes.OFICIOS_ROL_RECEPTOR_MONTERREY_CODIGO, vo.getBloqueId());

        for (UsuarioVO usuarioVo : usuariosMty) {
            mailsPara.add(usuarioVo.getMail().trim());
        }

        //la constante 10-RUG es para todas las gerencias y el rol 10-RNU por gerencia especifica
        List<UsuarioVO> usuariosUrgente = usuarioServicioRemoto.getUsuariosPorRolBloque(
                Constantes.OFICIOS_ROL_RECEPTOR_NOTIFICACION_URGENTE, vo.getBloqueId());

        for (UsuarioVO usuarioVo : usuariosUrgente) {
            mailsPara.add(usuarioVo.getMail().trim());
        }

        List<UsuarioVO> usuariosUrgenteGeneral = usuarioServicioRemoto.getUsuariosPorRolBloque(
                Constantes.OFICIOS_ROL_RECEPTOR_NOTIFICACION_URGENTE_GENERAL, vo.getBloqueId());

        for (UsuarioVO usuarioVo : usuariosUrgenteGeneral) {
            mailsPara.add(usuarioVo.getMail().trim());
        }

        CorreoVo parametrosCorreo = new CorreoVo();

        parametrosCorreo.setSetPara(mailsPara);
        parametrosCorreo.setSetCc(mailsCc);
        parametrosCorreo.setSetCco(mailsCco);
        parametrosCorreo.setAsunto(Constantes.OFICIOS_CORREO_MODIFICA_OFICIO + UtilSia.getFechaActual_ddMMyyy());
        notificacionOficioServicioRemoto.notificarModificaOficio(parametrosCorreo, vo);
    }

    /**
     * Crea un nuevo registro de oficio en la base de datos y un registro de
     * evento correspondiente en la bitácora.
     *
     * @param informacionOficioVo
     * @return La entidad de oficio creada.
     */
    private OfOficio crearOficio(InformacionOficioVo informacionOficioVo) {

        // preparar entidad para su guardado
        OfOficio oficio = generarEntidadOficio(informacionOficioVo);

        // guardar registro de oficio
        this.create(oficio);

        getLogger().info(this, "@crearOficio - oficio ID = " + oficio.getId());

        return oficio;

    }

    /**
     *
     * Agrega un nuevo registro de oficio a la base de datos.
     *
     * @param informacionOficioVo
     * @param copyBloques
     * @return ID entero del registro agregado.
     * @throws MissingRequiredValuesException
     * @throws ExistingItemException
     * @throws UnavailableItemException
     * @throws InvalidFileTypeException
     * @throws InvalidValuesException
     * @throws SIAException
     */
    
    public List<List<Object>> agregarOficio(final InformacionOficioVo informacionOficioVo, final List<SelectItem> copyBloques)
            throws
            MissingRequiredValuesException,
            ExistingItemException,
            UnavailableItemException,
            InvalidFileTypeException,
            InvalidValuesException,
            SIAException {

        OficioPromovibleVo vo = null;
        
        
        final List<OfOficioSiMovimiento> lisMov = new ArrayList<>();
        OfOficioSiMovimiento oficioMovimiento = null;
        OfOficio oficio = new OfOficio();
        
        //List<Integer> idsOficio = new ArrayList<Integer>();
        final List<List<Object>> newListOf = new ArrayList<>();
        List<Object> actual = new ArrayList<>();
        
        if (informacionOficioVo != null && informacionOficioVo.getOficioVo() != null) {
            String idUsuario = informacionOficioVo.getIdUsuario();
            
            // realizar validaciones necesarias
            validarCamposObligatorios(informacionOficioVo.getOficioVo(), true);

            validarRegistroDuplicado(informacionOficioVo.getOficioVo().getOficioNumero(), informacionOficioVo.getOficioVo().getBloqueId());
            validarAsociadosMismoBloque(informacionOficioVo.getOficioVo(), idUsuario);
            validarUsuariosOficioRestringidoMismoBloque(informacionOficioVo.getOficioVo());

            // agregar nuevo registro de oficio
            oficio = crearOficio(informacionOficioVo);
            //idsOficio.add(oficio.getId());
            informacionOficioVo.getOficioVo().setOficioId(oficio.getId());
            // agregar oficios a los que está asociado
            agregarOficiosAsociados(oficio, informacionOficioVo.getOficioVo(), idUsuario);
            // agregar usuariosRey a los que está restringido, si aplica
            agregarUsuariosOficioRestringido(oficio, informacionOficioVo.getOficioVo(), idUsuario);
            // registrar movimiento del oficio
            Integer estatusId = oficio.getEstatus().getId();
            InformacionMovimientoVo movimientoVo = new InformacionMovimientoVo();
            movimientoVo.setOficio(oficio);
            movimientoVo.setOperacionId(informacionOficioVo.getOficioVo().getOperacionId(estatusId));
            movimientoVo.setUsuarioId(idUsuario);
            movimientoVo.setMotivoMovimiento(informacionOficioVo.getOficioVo().getMotivoMovimiento(estatusId));

            oficioMovimiento = registrarMovimiento(movimientoVo);
            lisMov.add(oficioMovimiento);
            actual.add(oficio.getId());
            actual.add(informacionOficioVo.getOficioVo().getBloqueId());
            actual.add(informacionOficioVo.getOficioVo().getBloqueNombre());
            actual.add(informacionOficioVo.getOficioVo().getEstatusId());
            actual.add(informacionOficioVo.getOficioVo().getEstatusNombre());
            newListOf.add(actual);

            vo = informacionOficioVo.getOficioVo();

            //aqui se deben de crear las copias
            if (!copyBloques.isEmpty()) {
                for (final SelectItem item : copyBloques) {
                    actual = new ArrayList<Object>();

                    idUsuario = informacionOficioVo.getIdUsuario();
                    getLogger().info(this, "@agregarOficio = " + vo);
                    vo.setBloqueId((Integer) item.getValue());
                    vo.setBloqueNombre(item.getLabel());
                    InformacionOficioVo of = new InformacionOficioVo(vo, idUsuario);

                    // realizar validaciones necesarias
                    validarCamposObligatorios(vo, true);

                    validarRegistroDuplicado(vo.getOficioNumero(), vo.getBloqueId());
                    validarAsociadosMismoBloque(vo, idUsuario);
                    validarUsuariosOficioRestringidoMismoBloque(vo);

                    // agregar nuevo registro de oficio
                    oficio = crearOficio(of);
                    //idsOficio.add(oficio.getId());
                    vo.setOficioId(oficio.getId());
                    // agregar oficios a los que está asociado
                    agregarOficiosAsociados(oficio, vo, idUsuario);
                    // agregar usuariosRey a los que está restringido, si aplica
                    agregarUsuariosOficioRestringido(oficio, vo, idUsuario);
                    // registrar movimiento del oficio
                    estatusId = oficio.getEstatus().getId();
                    movimientoVo = new InformacionMovimientoVo();
                    movimientoVo.setOficio(oficio);
                    movimientoVo.setOperacionId(vo.getOperacionId(estatusId));
                    movimientoVo.setUsuarioId(idUsuario);
                    movimientoVo.setMotivoMovimiento(vo.getMotivoMovimiento(estatusId));

                    oficioMovimiento = registrarMovimiento(movimientoVo);
                    lisMov.add(oficioMovimiento);
                    actual.add(vo.getOficioId());
                    actual.add(vo.getBloqueId());
                    actual.add(vo.getBloqueNombre());
                    actual.add(vo.getEstatusId());
                    actual.add(vo.getEstatusNombre());
                    newListOf.add(actual);
                }
            }

            if (!lisMov.isEmpty()) {
            // registrar archivo adjunto
            SiAdjunto adjunto = registrarAdjunto(vo.getArchivoAdjunto(), lisMov, idUsuario);
        }
            
        }//regresar lista de vo seria mejor opcion

        return newListOf;

    }

    /**
     *
     * @param oficio
     * @param vo
     * @param idUsuario
     */
    private void agregarUsuariosOficioRestringido(OfOficio oficio, OficioPromovibleVo vo, String idUsuario) {

        if (vo.getAcceso().isRestringido()) {

            List<String> usuarioIds = vo.getRestringidoAUsuariosIds();

            for (String usuarioAccesoId : usuarioIds) {

                // crear entidad para el registro
                OfOficioUsuario entidad = new OfOficioUsuario();

                Date fechaGenero = new Date();

                entidad.setOfOficio(oficio);
                entidad.setUsuario(new Usuario(usuarioAccesoId));
                entidad.setGenero(new Usuario(idUsuario));
                entidad.setFechaGenero(fechaGenero);
                entidad.setHoraGenero(fechaGenero);
                entidad.setEliminado(Constantes.BOOLEAN_FALSE);

                oficioUsuarioRemoto.create(entidad);
            }
        }
    }

    /**
     *
     * @param oficio
     * @param oficioVo
     * @param usuarioId
     */
    private void agregarOficiosAsociados(OfOficio oficio, OficioPromovibleVo oficioVo, String usuarioId) {

        List<OficioVo> oficios = oficioVo.getAsociadoHaciaOficios();

        for (OficioVo vo : oficios) {

            OfOficio asociadoA = new OfOficio();
            asociadoA.setId(vo.getOficioId());

            // crear entidad para el registro
            OfOficioAsociado asociado = new OfOficioAsociado();

            Date fechaGenero = new Date();
            Usuario usuario = new Usuario(usuarioId);

            asociado.setOfOficio(oficio);
            asociado.setOfOficioAsociadoA(asociadoA);
            asociado.setGenero(usuario);
            asociado.setFechaGenero(fechaGenero);
            asociado.setHoraGenero(fechaGenero);
            asociado.setEliminado(Constantes.BOOLEAN_FALSE);

            oficioAsociadoRemoto.create(asociado);
        }
    }

    /**
     * Valida si el oficio proporcionado ya se encuentra asociado a otro oficio.
     *
     * @param oficioId
     */
    /*private void validarOficioAsociadoOcupado(Integer oficioId) throws UnavailableItemException {

     // validar si este oficio ya se encuentra asociado por otro oficio
     OficioVo vo = this.obtenerOficioHijo(oficioId);

     getLogger().info(this, "@validarOficioAsociado - Oficio asociado = " + vo);

     if (vo != null) {
     throw new UnavailableItemException();
     }

     }*/
    /**
     * Registra un movimiento de oficio.
     *
     * @param oficio
     * @param usuarioId
     *
     */
    private OfOficioSiMovimiento registrarMovimiento(InformacionMovimientoVo movimientoVo) {

        // la operación dependerá del estatus actual
        OfOficio oficio = movimientoVo.getOficio();
        int operacionId = movimientoVo.getOperacionId();
        String motivoMovimiento = movimientoVo.getMotivoMovimiento();
        String usuarioId = movimientoVo.getUsuarioId();

        // este metodo ya registra la operación en bitácora en la tabla SI_LOG
        SiMovimiento movimiento = movimientoServicioRemoto.save(motivoMovimiento, operacionId, usuarioId);

        // generar relación entre oficio y movimiento
        OfOficioSiMovimiento oficioMovimiento
                = oficioMovimientoServicioRemoto
                        .agregarOficioMovimiento(oficio, movimiento, usuarioId);

        getLogger().info(this, "@registrarMovimiento - oficioMovimiento ID = " + oficioMovimiento.getId());

        return oficioMovimiento;

    }

    /**
     *
     * @param vo
     */
    private void depurarCaracteres(OficioVo vo) {

        vo.setOficioNumero(UtilSia.depurarCaracteresBaseDatos(vo.getOficioNumero()));
        vo.setOficioAsunto(UtilSia.depurarCaracteresBaseDatos(vo.getOficioAsunto()));
        vo.setObservaciones(UtilSia.depurarCaracteresBaseDatos(vo.getObservaciones()));

    }

    /**
     *
     * Modifica un registro de oficio existente en la base de datos.
     *
     * @param vo
     * @param idUsuario
     * @throws MissingRequiredValuesException
     * @throws ExistingItemException
     * @throws UnavailableItemException
     * @throws InvalidFileTypeException
     * @throws InvalidValuesException
     * @throws SIAException
     */
    
    public void modificarOficio(OficioPromovibleVo vo, String idUsuario)
            throws
            MissingRequiredValuesException,
            ExistingItemException,
            UnavailableItemException,
            InvalidFileTypeException,
            InvalidValuesException,
            SIAException {

        getLogger().info(this, "@modificarOficio = " + vo);

        validarCamposObligatorios(vo, false);

        validarAsociadosMismoBloque(vo, idUsuario);

        validarUsuariosOficioRestringidoMismoBloque(vo);

        // depurar caracteres especiales antes de guardar
        depurarCaracteres(vo);

        // obtener entidad desde base de datos
        OfOficio entidad = this.find(vo.getOficioId());

        // datos de compañía, bloque y gerencia
        ApCampoGerenciaVo campoGerenciaVo
                = apCampoGerenciaServicioRemoto.findByCampoGerencia(vo.getBloqueId(), vo.getGerenciaId(), false);

        entidad.setApCampoGerencia(apCampoGerenciaServicioRemoto.find(campoGerenciaVo.getId()));

        // numero de oficio
        // si se modificó el numero de oficio, validar duplicidad
        if (!entidad.getNumeroOficio().trim().equalsIgnoreCase(vo.getOficioNumero().trim())) {
            validarRegistroDuplicado(vo.getOficioNumero(), vo.getBloqueId());
            entidad.setNumeroOficio(vo.getOficioNumero());
        }

        // tipo de oficio
        OfTipoOficio tipoOficio = new OfTipoOficio();
        tipoOficio.setId(vo.getTipoOficioId());

        entidad.setOfTipoOficio(tipoOficio);
        entidad.setAsunto(vo.getOficioAsunto());
        entidad.setFechaOficio(vo.getOficioFecha());
        entidad.setObservaciones(vo.getObservaciones());
        entidad.setUrgente(vo.isUrgente());
        entidad.setCoPrivacidad(new CoPrivacidad(vo.getAcceso().getId()));

        Date fechaModifico = new Date();

        // datos de modificación
        Usuario usuario = new Usuario(idUsuario);
        entidad.setModifico(usuario);
        entidad.setFechaModifico(fechaModifico);
        entidad.setHoraModifico(fechaModifico);

        actualizarOficio(entidad);

        // actualizar oficios asociados
        actualizarOficiosAsociados(entidad, vo, idUsuario);

        // actualizar usuariosRey con acceso a oficio restringido
        actualizarUsuariosOficioRestringido(entidad, vo, idUsuario);

    }

    /**
     *
     * @param entidad
     * @param vo
     * @param idUsuario
     */
    private void actualizarUsuariosOficioRestringido(OfOficio entidad, OficioPromovibleVo vo, String idUsuario) {

        // borrar lógico registros actuales
        oficioUsuarioRemoto.borrarLogicoUsuariosOficioRestringido(vo.getOficioId(), idUsuario);

        // agregar nuevos registros
        agregarUsuariosOficioRestringido(entidad, vo, idUsuario);

    }

    /**
     *
     * @param entidad
     * @param vo
     * @param idUsuario
     */
    private void actualizarOficiosAsociados(OfOficio entidad, OficioPromovibleVo vo, String idUsuario) {

        // hacer el borrado lógico de los oficios asociados actuales
        oficioAsociadoRemoto.borrarLogicoOficiosAsociados(vo.getOficioId(), idUsuario);

        // agregar oficios asociados recibidos
        agregarOficiosAsociados(entidad, vo, idUsuario);
    }

    /**
     * La lista de usuariosRey con acceso a un oficio restringido deben tener
     * acceso al bloque del oficio.
     *
     * @param vo
     */
    private void validarUsuariosOficioRestringidoMismoBloque(OficioPromovibleVo vo)
            throws InvalidValuesException {

        getLogger().info(this, "@validarUsuariosOficioRestringidoMismoBloque");

        if (vo.isRestringido()) {

            List<String> usuarioIds = vo.getRestringidoAUsuariosIds();

            int oficioBloqueId = vo.getBloqueId();

            for (String usuarioAccesoId : usuarioIds) {

                List<UsuarioVO> usuariosAcceso
                        = usuarioServicioRemoto.getUsuariosPorRolBloque(
                                Constantes.OFICIOS_ROL_CONSULTA_OFICIOS,
                                oficioBloqueId,
                                usuarioAccesoId,
                                null,
                                null);

                if (UtilSia.isNullOrEmpty(usuariosAcceso)) {

                    throw new InvalidValuesException("Uno o más usuarios seleccionados no tienen acceso al bloque del oficio.");
                }
            }
        }
    }

    /**
     *
     *
     * @param vo
     * @param idUsuario
     * @throws InvalidValuesException
     */
    private void validarAsociadosMismoBloque(
            OficioVo vo, String idUsuario)
            throws InvalidValuesException {

        // validar que todos los oficios asociados sean del mismo bloque que
        // el oficio a registrar
        for (Integer oficioId : vo.getAsociadoHaciaOficiosListaIds()) {

            OficioVo asociadoVo = buscarOficioVoPorId(oficioId, idUsuario);

            boolean mismoBloque = vo.getBloqueId().equals(asociadoVo.getBloqueId());

            if (!mismoBloque) {

                throw new InvalidValuesException("Los oficios a asociar debe pertenecer al mismo bloque.");

            }
        }
    }

    /**
     *
     * @param entidad
     * @param entidadAnterior
     */
    private void actualizarOficio(OfOficio entidad) {

        // guardar registro de oficio
        this.edit(entidad);
    }

    /**
     * Obtiene el registro de archivo adjunto correspondiente al movimiento con
     * el estatus actual del oficio.
     *
     * @param vo
     * @return
     */
    /*
     /* No aplica por cambio de edicion de archivos adjuntos al 22/jul/14
     *
     * private SiAdjunto obtenerAdjuntoActual(OficioVo vo) {

     SiAdjunto adjuntoActual = null;

     // obtener historial de movimientos

     List<MovimientoVo> movimientos = obtenerMovimientos(vo.getOficioId());

     // el archivo adjunto de este oficio será el registrado
     // en el movimiento con el estatus actual

     for (MovimientoVo movimiento : movimientos) {

     int operacionId = movimiento.getOperacionId();

     int movimientoEstatusId = ((Promovible)vo).getEstatusId(operacionId);

     if (vo.getEstatusId() == movimientoEstatusId
     && movimiento.getAdjunto() != null) {

     adjuntoActual = siAdjuntoRemote.find(movimiento.getAdjunto().getId());

     break;
     }

     }

     return adjuntoActual;

     }*/
    /**
     * Regresa el vo de archivo adjunto correspondiente al ID proporcionado.
     *
     * @param adjuntoId
     * @return
     */
    
    public AdjuntoOficioVo obtenerArchivoAdjunto(int adjuntoId) {

        SiAdjunto adjunto = siAdjuntoRemote.find(adjuntoId);

        AdjuntoOficioVo vo = castAdjuntoVo(adjunto);

        return vo;

    }

    /**
     *
     * @param vo
     * @param movimientoVo
     * @param motivo
     * @param idUsuario
     * @throws SIAException
     */
    @Interceptors(ValidacionEdicionArchivoAdjuntoInterceptor.class)
    
    public void actualizarAdjunto(OficioVo vo, MovimientoVo movimientoVo, String motivo, String idUsuario)
            throws SIAException, MessagingException {

        // obtener operacion del movimiento a reemplazar
        SiMovimiento movimiento = movimientoServicioRemoto.find(movimientoVo.getId());

        int operacionId = movimiento.getSiOperacion().getId();

        // actualizar movimiento con operacion de remplazo
        SiOperacion operacion = new SiOperacion(Constantes.OFICIOS_OPERACION_ID_ACTUALIZACION_ARCHIVO_ADJUNTO_MOVIMIENTO);
        movimiento.setSiOperacion(operacion);
        movimientoServicioRemoto.edit(movimiento);

        // registrar movimiento con la operacion a la que se remplaza el archivo
        OfOficio entidad = this.find(vo.getOficioId());

        InformacionMovimientoVo infoMovimientoVo = new InformacionMovimientoVo();
        infoMovimientoVo.setOficio(entidad);
        infoMovimientoVo.setOperacionId(operacionId);
        infoMovimientoVo.setMotivoMovimiento("Sustitución de archivo adjunto: " + motivo);
        infoMovimientoVo.setUsuarioId(idUsuario);

        OfOficioSiMovimiento oficioMovimiento = registrarMovimiento(infoMovimientoVo);
        List<OfOficioSiMovimiento> listMov = new ArrayList<OfOficioSiMovimiento>();
        listMov.add(oficioMovimiento);

        // obtener adjunto actual para su actualizacion
        AdjuntoOficioVo adjuntoVo = movimientoVo.getAdjunto();

        SiAdjunto adjuntoActual = siAdjuntoRemote.find(adjuntoVo.getId());

        // desactivar adjunto actual
        adjuntoActual.setEliminado(Constantes.BOOLEAN_TRUE);
        siAdjuntoRemote.update(adjuntoActual, idUsuario);

        // registrar nuevo adjunto
        registrarAdjunto(adjuntoVo, listMov, idUsuario);

        //remplazarArchivo(adjuntoActual, adjuntoVo, idUsuario);
        //jevazquez 18/02/15
        if (vo.isUrgente()) {

            notificarModificacionOficio(vo);
        }

    }

    /**
     * Renombra un archivo temporal a su nombre final para prepararlo para su
     * registro en la base de datos.
     *
     * @param adjunto
     * @param idUsuario
     */
    private void renombrarArchivo(AdjuntoOficioVo adjunto, String idUsuario) {

//	File archivoTemporal = adjunto.getArchivoSubido();
//
//	GestorArchivos gestorArchivos = new GestorArchivos(this.rutaRaizAdjuntos, idUsuario);
//
//	File archivoFinal = gestorArchivos.restaurarArchivoOficio(archivoTemporal, adjunto.getTipoArchivo());
        adjunto.setArchivoSubido(adjunto.getArchivoSubido());
    }

    
    /**
     * Valida si ya existe un registro con el número de oficio proporcionado.
     *
     * @param numeroOficio
     * @throws ExistingItemException En caso de existir un registro de oficio
     * con este número de oficio.
     */
    private void validarRegistroDuplicado(String numeroOficio, int idbloque) throws ExistingItemException {

        if (obtenerOficioVo(numeroOficio, idbloque) != null) {
            throw new ExistingItemException();
        }
    }

    /**
     *
     * Genera una nueva entidad de OfOficio con la información proporcionada
     * desde la vista para su guardado en la base de datos.
     *
     * @param vo
     * @param idUsuario
     * @return
     */
    private OfOficio generarEntidadOficio(InformacionOficioVo informacionOficioVo) {

        OficioPromovibleVo vo = informacionOficioVo.getOficioVo();
        String idUsuario = informacionOficioVo.getIdUsuario();

        OfOficio oficio = new OfOficio();

        // ApCampoGerencia
        ApCampoGerenciaVo campoGerenciaVo
                = apCampoGerenciaServicioRemoto
                        .findByCampoGerencia(vo.getBloqueId(), vo.getGerenciaId(), false);

        ApCampoGerencia campoGerencia = new ApCampoGerencia();

        campoGerencia.setId(campoGerenciaVo.getId());

        // OfTipoOficio
        OfTipoOficio tipoOficio = new OfTipoOficio();
        tipoOficio.setId(vo.getTipoOficioId());

        // Estatus inicial
        Estatus estatus = new Estatus();
        estatus.setId(Constantes.OFICIOS_ESTATUS_ID_OFICIO_CREADO);

        // Usuario
        Usuario usuario = new Usuario(idUsuario);

        Date fechaGenero = new Date();

        // preparar bean
        oficio.setApCampoGerencia(campoGerencia);
        oficio.setOfTipoOficio(tipoOficio);
        oficio.setNumeroOficio(vo.getOficioNumero());
        oficio.setAsunto(vo.getOficioAsunto());
        oficio.setFechaOficio(vo.getOficioFecha());
        oficio.setObservaciones(vo.getObservaciones());
        oficio.setEliminado(Constantes.BOOLEAN_FALSE);
        oficio.setGenero(usuario);
        oficio.setFechaGenero(fechaGenero);
        oficio.setHoraGenero(fechaGenero);
        oficio.setEstatus(estatus);
        oficio.setCoPrivacidad(new CoPrivacidad(vo.getAcceso().getId()));
        oficio.setUrgente(vo.isUrgente());

        return oficio;
    }

    /**
     *
     * Registra un archivo adjunto correspondiente a un movimiento de oficio.
     *
     *
     * @param adjuntoOficio
     * @param oficioMovimiento
     * @param idUsuario
     * @return
     * @throws SIAException
     * @throws Exception
     */
    private SiAdjunto registrarAdjunto(AdjuntoOficioVo adjuntoOficio,
            List<OfOficioSiMovimiento> oficioMovimiento, String idUsuario) throws SIAException {

        // guardar registro de archivo adjunto
        SiAdjunto adjunto = crearArchivoAdjunto(adjuntoOficio, idUsuario);

        // registrar relación entre un movimiento de oficio y un archivo adjunto
        if (!oficioMovimiento.isEmpty()) {
            for (OfOficioSiMovimiento om : oficioMovimiento) {
                OfOficioSiMovSiAdjunto oficioMovAdjunto
                        = oficioMovAdjuntoServicioRemoto
                                .agregarOficioMovAdjunto(om, adjunto, idUsuario);

                getLogger().info(this, "@registrarAdjunto - oficioMovAdjunto ID = " + oficioMovAdjunto.getId());
            }
        }

        return adjunto;

    }

    /**
     *
     * Crea un nuevo registro de archivo adjunto de oficio en la base de datos.
     * El método de servicio invocado registra el evento en la tabla SI_LOG.
     *
     * @param vo
     * @param idUsuario
     * @return
     * @throws SIAException
     * @throws Exception
     */
    private SiAdjunto crearArchivoAdjunto(AdjuntoOficioVo adjunto, String idUsuario)
            throws InvalidValuesException, SIAException {

        getLogger().info(this, "@crearArchivoAdjunto");

        // validar nombre de archivo
        String nombreArchivo = Strings.nullToEmpty(adjunto.getNombre());

        if (nombreArchivo.length() > Constantes.OFICIOS_ARCHIVO_ADJUNTO_NOMBRE_LONGITUD_MAXIMA) {

            throw new InvalidValuesException(
                    "El nombre del archivo excede la longitud máxima permitida ("
                    + Constantes.OFICIOS_ARCHIVO_ADJUNTO_NOMBRE_LONGITUD_MAXIMA + ").");
        }

        // renombrar archivo en disco para quitar prefijo de archivo temporal
        //renombrarArchivo(adjunto, idUsuario);
        // parametros para guardado
        String rutaArchivo = adjunto.getRutaArchivo();
        String tipoArchivo = adjunto.getTipoArchivo();
        Long tamanoArchivo = adjunto.getTamanoArchivo();
        adjunto.getUrl();

        getLogger().info(this, "archivoAdjuntoVo = " + adjunto.toString());
        getLogger().info(this,
                "nombreArchivo = " + nombreArchivo + " (" + UtilSia.stringLength(nombreArchivo) + "), "
                + "rutaArchivo = " + rutaArchivo + " (" + UtilSia.stringLength(rutaArchivo) + "), "
                + "tipoArchivo = " + tipoArchivo + ", "
                + "tamanoArchivo = " + tamanoArchivo);

        // guardar registro en la base de datos
        // este metodo de servicio ya registra el evento en bitácora en
        // la tabla SI_LOG
        SiAdjunto siAdjunto = siAdjuntoRemote
                .save(nombreArchivo, adjunto.getUrl(), tipoArchivo, tamanoArchivo, idUsuario);

        getLogger().info(this, "guardado en SI_ADJUNTO - ID = " + siAdjunto.getId());

        return siAdjunto;
    }

    /**
     *
     * Valida campos obligatorios de un oficio en proceso de guardado.
     *
     * @param vo Contiene la información del oficio a guardar.
     * @param validarAdjunto Para indicar si se debe validar archivo adjunto.
     * Solo aplica para altas.
     * @throws MissingRequiredValuesException En caso de faltar uno o más campos
     * obligatorios.
     */
    private void validarCamposObligatorios(OficioPromovibleVo vo, boolean validarAdjunto)
            throws MissingRequiredValuesException {

        // validar campos obligatorios
        final StringBuilder mensaje = new StringBuilder(150);

        if (vo.getOficioNumero().isEmpty()) {
            mensaje.append("Nº Oficio, ");
        }
        if (vo.getOficioAsunto().isEmpty()) {
            mensaje.append("Asunto, ");
        }
        if (vo.getOficioFecha() == null) {
            mensaje.append("Fecha de Oficio, ");
        }

        if (vo.getCompaniaId() <= 0) {
            mensaje.append("Compañía, ");
        }
        if (vo.getBloqueId() <= 0) {
            mensaje.append("Bloque, ");
        }
        if (vo.getGerenciaId() <= 0) {
            mensaje.append("Gerencia, ");
        }

        if (validarAdjunto && UtilSia.isNullOrBlank(vo.getArchivoAdjunto().getNombre())) {
            mensaje.append("Archivo Adjunto, ");
        }

        if (vo.getAcceso().isRestringido() && vo.getRestringidoAUsuarios().isEmpty()) {
            mensaje.append("Usuarios con Acceso, ");
        }

        if (mensaje.length() > 0) {

            mensaje.delete(mensaje.length() - 3, mensaje.length() - 1);
            //mensaje.setLength(mensaje.length() - 2);

            final MissingRequiredValuesException ex = new MissingRequiredValuesException();
            ex.setValoresFaltantes(mensaje.toString());

            throw ex;

        }
    }

    
    /**
     *
     *
     * @param ids
     * @return
     */
    
    public List<OficioPromovibleVo> buscarOficiosPorId(List<Integer> ids) {

        List<OficioPromovibleVo> resultado;

        if (ids != null && !ids.isEmpty()) {

            String cadenaIds = UtilSia.toCommaSeparatedString(ids, false);

            // preparar query en función de los parámetros recibidos
            StringBuilder sb = new StringBuilder();

            sb.append(queryBase)
                    .append(" and of1.ID in (")
                    .append(cadenaIds)
                    .append(") ")
                    .append(queryBaseGroupBy);

            Query nativeQuery = em.createNativeQuery(sb.toString());

            resultado = castVo(nativeQuery.getResultList());

        } else {

            // regresar lista vacía
            resultado = new ArrayList<OficioPromovibleVo>();

        }

        return resultado;

    }

    /**
     *
     * @param oficioId
     * @param usuarioId
     * @param validarRestringido
     * @return
     */
    
    public OficioPromovibleVo buscarOficioVoPorId(
            Integer oficioId,
            String usuarioId,
            boolean validarRestringido) throws InsufficientPermissionsException {

        OficioPromovibleVo vo = buscarOficioVoPorId(oficioId, usuarioId);

        if (validarRestringido && vo.isRestringido()) {

            // validar si el usuario tiene acceso a este oficio
            if (!vo.tieneAccesoOficioRestringido(usuarioId)) {
                throw new InsufficientPermissionsException("El usuario no tiene acceso a este oficio restringido.");
            }
        }

        return vo;
    }

    /**
     * Regresa el objeto OficioVo correspondiente al ID proporcionado.
     *
     * @param oficioId
     * @return
     */
    private OficioPromovibleVo buscarOficioVoPorId(Integer oficioId, String usuarioId) {

        getLogger().info(this, "@buscarOficioVoPorId");

        OficioPromovibleVo resultadoVo = obtenerOficioVo(oficioId);

        // obtener cadena de oficios asociados - removido 15/oct/14
        //resultadoVo.setOficiosAsociados(obtenerOficiosAsociados(resultadoVo));
        //printAsociados(resultadoVo.getOficiosAsociados());
        // obtener oficios hacia los que este oficio está asociado
        resultadoVo.setAsociadoHaciaOficios(
                oficioAsociadoRemoto.buscarOficiosAsociados(oficioId,
                        Constantes.OFICIOS_ASOCIACION_HACIA));

        // obtener oficios por los que este oficio está asociado
        resultadoVo.setAsociadoDesdeOficios(
                oficioAsociadoRemoto.buscarOficiosAsociados(oficioId,
                        Constantes.OFICIOS_ASOCIACION_DESDE));

        // obtener lista de usuariosRey con acceso al oficio restringido
        resultadoVo.setRestringidoAUsuarios(
                obtenerUsuariosOficioRestringido(resultadoVo));

        // obtener historial de movimientos
        resultadoVo.setMovimientos(obtenerMovimientos(oficioId));

        // el archivo adjunto de este oficio será el registrado
        // en el movimiento con el estatus correspondiente
        for (MovimientoVo movimiento : resultadoVo.getMovimientos()) {

            int operacionId = movimiento.getOperacionId();

            Integer movimientoEstatusId = ((Promovible) resultadoVo).getEstatusId(operacionId);

            // se valida contra nulo para validar que es una operacion relacionada
            // con la promocion de estatus de oficio. Si es una operacion no relacionada
            // (ej. activacion o desactivacion de seguimiento) el valor será nulo
            if (UtilSia.greaterThanZero(movimientoEstatusId)) {
                getLogger().info(this,
                        "vo estatus id = " + resultadoVo.getEstatusId() + ", "
                        + "mov estatus id = " + movimientoEstatusId + ", "
                        + "adjunto = " + movimiento.getAdjunto());

                if (resultadoVo.getEstatusId().intValue() == movimientoEstatusId.intValue()) {

                    resultadoVo.setArchivoAdjunto(movimiento.getAdjunto());
                    break;

                }
            }

        }

        // copiar archivos adjuntos de los movimientos a ruta para
        // acceso por el visor de archivos
        /*
	 * ESAPIEN-29/ene/15 - Opción de visor deshabilitado a la fecha. No
	 * se requiere copia temporal.
	 *
         */
        //copiarArchivosAdjuntos(resultadoVo.getMovimientos(), usuarioId);
        return resultadoVo;

    }

    /**
     * Obtiene la lista de usuariosRey registrados con acceso a un oficio
     * restringido.
     *
     * @param oficioId
     * @return
     */
    private List<UsuarioVO> obtenerUsuariosOficioRestringido(OficioVo vo) {

        List<UsuarioVO> usuarios;

        if (vo.isRestringido()) {

            List<String> usuarioIds = oficioUsuarioRemoto.buscarUsuariosOficioRestringidoIds(vo.getOficioId());

            usuarios = usuarioServicioRemoto.findUsuariosById(usuarioIds);
        } else {
            usuarios = new ArrayList<UsuarioVO>();
        }

        return usuarios;

    }

    /**
     * Obtiene la lista de movimientos de un oficio determinado.
     *
     * @param oficioId
     * @return
     */
    private List<MovimientoVo> obtenerMovimientos(final Integer oficioId) {

        final StringBuilder sql = new StringBuilder(100);

        // se agrega desc para los casos en donde la hora y fecha sea igual
        // (ej. en opción Guardar y Promover)
        sql.append(queryMovimientos)
                .append("and ofic.id = ? \n " 
                + "order by mov.FECHA_GENERO DESC, mov.HORA_GENERO DESC, mov.ID DESC ");

        final Query queryMov = em.createNativeQuery(sql.toString());
        queryMov.setParameter(1, oficioId);

        final List resultado = queryMov.getResultList();

        getLogger().debug(this, "@obtenerMovimientos - ID = {0}, query = {1}", new Object[]{oficioId, sql});

        return castMovimientosVo(resultado);

    }

    /**
     * Regresa el oficio correspondiente al ID proporcionado.
     *
     * @param id
     * @return
     */
    private OficioPromovibleVo obtenerOficioVo(final Integer id) {

        // obtener información de oficio
        final StringBuilder sql = new StringBuilder();

        sql.append(queryBase)
                .append("and of1.ID = ? ");

        //sb.append(queryBaseGroupBy);

        final Query qryOficio = em.createNativeQuery(sql.toString());
        qryOficio.setParameter(1, id);

        final List resultado = qryOficio.getResultList();

        OficioPromovibleVo resultadoVo = (OficioPromovibleVo) castVo(resultado).get(0);

        return resultadoVo;
    }

    /**
     *
     * Regresa el oficio correspondiente al número de oficio proporcionado.
     *
     * @param numeroOficio
     * @return El objeto con la información del registro o nulo en caso de no
     * encontrarlo.
     */
    private OficioVo obtenerOficioVo(String numeroOficio, int idbloque) {

        // obtener información de oficio
        final StringBuilder sql = new StringBuilder(100);

        sql.append(queryBase)
                .append(" and of1.NUMERO_OFICIO COLLATE \"es_ES\" = ? and  ap_c.id= ? ")
                .append(queryBaseGroupBy);

        final Query q = em.createNativeQuery(sql.toString());

        q.setParameter(1, numeroOficio.trim());
        q.setParameter(2, idbloque);

        final List resultados = q.getResultList();

        OficioVo resultadoVo = null;

        if (!resultados.isEmpty()) {
            resultadoVo = (OficioVo) castVo(resultados).get(0);
        }

        return resultadoVo;
    }

    /**
     * Regresa la lista de los oficios asociados en la que se encuentra el
     * oficio proporcionado, en orden secuencial.
     *
     * @param id
     * @return
     */
    /*private List<OficioVo> obtenerOficiosAsociados(OficioVo oficioVo) {

     getLogger().info(this, "@obtenerOficiosAsociados");

     int id = oficioVo.getOficioId();

     getLogger().info(this, "vo id inicial = " + id);

     // obtener los oficios anteriores (padres)

     List<OficioVo> oficiosAnteriores = new ArrayList();

     OficioVo vo;

     while ((vo = obtenerOficioPadre(id)) != null) {
     oficiosAnteriores.add(vo);
     id = vo.getOficioId();

     getLogger().info(this, "vo id padre = " + id);
     }

     // obtener los oficios posteriores (hijos)

     List<OficioVo> oficiosPosteriores = new ArrayList();

     id = oficioVo.getOficioId();

     while ((vo = obtenerOficioHijo(id)) != null) {
     oficiosPosteriores.add(vo);
     id = vo.getOficioId();

     getLogger().info(this, "vo id hijo = " + id);
     }


     getLogger().info(this, "Construir lista completa");

     // construir la lista completa

     List<OficioVo> oficiosAsociados = new ArrayList();

     // agregar oficios anteriores

     ListIterator<OficioVo> listItAnteriores = oficiosAnteriores.listIterator(oficiosAnteriores.size());

     while (listItAnteriores.hasPrevious()) {
     oficiosAsociados.add((OficioVo)listItAnteriores.previous());
     }

     // agregar oficio actual

     oficiosAsociados.add(oficioVo);

     // agregar oficios posteriores

     Iterator<OficioVo> it = oficiosPosteriores.iterator();

     while (it.hasNext()) {
     oficiosAsociados.add(it.next());
     }

     return oficiosAsociados;

     }*/
    /**
     * Regresa el oficio padre asociado en caso de haber, o nulo en caso
     * contrario.
     *
     * @param id
     * @return
     */
    /*private OficioVo obtenerOficioPadre(Integer id) {

     StringBuilder sql = new StringBuilder();
     sql.append(queryOficiosAsociados).append(" and of1.id = ? ");

     Query q = em.createNativeQuery(sql.toString());

     q.setParameter(1, id);

     List resultado = q.getResultList();

     OficioVo vo = null;

     Iterator it = resultado.iterator();

     if (it.hasNext()) {

     Object[] obj = (Object[]) it.next();

     vo = instanciarOficioVo((Integer) obj[6]);

     vo.setOficioId((Integer) obj[0]);
     vo.setOficioNumero(String.valueOf(obj[1]));
     vo.setEliminado(String.valueOf(obj[2]));
     vo.setCompaniaRfc(String.valueOf(obj[3]));
     vo.setBloqueId((Integer) obj[4]);
     vo.setGerenciaId((Integer) obj[5]);
     vo.setEstatusId((Integer) obj[7]);

     }

     return vo;
     }*/
    /**
     * Regresa el oficio hijo asociado en caso de haber, o nulo en caso
     * contrario.
     *
     * @param id
     * @return
     */
    /*private OficioVo obtenerOficioHijo(Integer id) {

     StringBuilder sql = new StringBuilder();
     sql
     .append(queryOficiosAsociados)
     .append(" and of1.OF_OFICIO = ? ");

     Query q = em.createNativeQuery(sql.toString());

     q.setParameter(1, id);

     List resultado = q.getResultList();

     OficioVo vo = null;

     Iterator it = resultado.iterator();

     if (it.hasNext()) {

     Object[] obj = (Object[]) it.next();

     vo = instanciarOficioVo((Integer) obj[14]);

     vo.setOficioId((Integer) obj[8]);
     vo.setOficioNumero(String.valueOf(obj[9]));
     vo.setEliminado(String.valueOf(obj[10]));
     vo.setCompaniaRfc(String.valueOf(obj[11]));
     vo.setBloqueId((Integer) obj[12]);
     vo.setGerenciaId((Integer) obj[13]);
     vo.setEstatusId((Integer) obj[15]);

     }

     return vo;
     }*/
    /**
     * Consulta los registros de oficio que no han sido notificados por email.
     *
     * @return
     */
    private List<OficioPromovibleVo> buscarOficiosNoNotificados(final int idCampo) {

        getLogger().info(this, "@buscarOficiosNoNotificados");

        final StringBuilder sql = new StringBuilder(200);

        sql.append(queryBase)// obtener oficios publicos, no restringidos, no anulados
        .append(" and of1.CO_PRIVACIDAD in ( ?,?)  "
                + " and of1.FECHA_NOTIFICO_ALTA is null "
                + " and of1.ELIMINADO = ? "
                + " and ap_c.id = ?")
        .append(queryBaseGroupBy)
        /*
	 * se agreaga nuevo order by por nombre de compañia, bloque, gerencia,
	 * oficio jevazquez 17/02/15
         */
        .append(" order by c.NOMBRE asc, ap_c.NOMBRE asc, g.NOMBRE asc, of1.numero_oficio asc ");

        Query qryNoNotif = em.createNativeQuery(sql.toString());
        qryNoNotif.setParameter(1, PrivacidadOficio.ID_PUBLICO);
        qryNoNotif.setParameter(2, PrivacidadOficio.ID_GERENCIA);
        qryNoNotif.setParameter(3, Constantes.BOOLEAN_FALSE);
        qryNoNotif.setParameter(4, idCampo);

        final List<Object[]> resultadoConsulta = qryNoNotif.getResultList();

        final List<OficioPromovibleVo> oficios = castVo(resultadoConsulta);

        final List<OficioPromovibleVo> resultado = new ArrayList<>();

        // agregar movimientos de oficio
        for (OficioPromovibleVo vo : oficios) {

            vo.setMovimientos(obtenerMovimientos(vo.getOficioId()));

            if (vo.contieneArchivoInformeAvance()) {
                resultado.add(vo);
            }

        }

        return resultado;

    }

    //jevazquez 23/feb/2015 aprobado
    private List<OficioPromovibleVo> buscarOficiosNoNotificadosSemana() {

        getLogger().info(this, "@buscarOficiosNoNotificados");

        final StringBuilder sql = new StringBuilder(300);

        sql.append(queryBase)
        // obtener oficios publicos, no restringidos, no anulados
        .append(" and of1.CO_PRIVACIDAD in ( ?,?) "
                + " and of1.FECHA_NOTIFICO_ALTA is null "
                + " and of1.ELIMINADO = ? ");
        
        Date fechaPrevia = new Date();
        fechaPrevia.setDate(fechaPrevia.getDate() - 7);
        
        sql.append(" and of1.FECHA_MODIFICO < CAST ('")
                .append(Constantes.FMT_yyyyMMdd.format(fechaPrevia))
                .append("' as date)")
                .append(queryBaseGroupBy)
                .append(" ORDER BY trim(upper (c.NOMBRE)) asc, trim(upper(ap_c.NOMBRE)) asc, trim(upper(g.NOMBRE)) asc, trim(upper(of1.numero_oficio)) asc");

        final Query qryNoNotif = em.createNativeQuery(sql.toString());
        qryNoNotif.setParameter(1, PrivacidadOficio.ID_PUBLICO);
        qryNoNotif.setParameter(2, PrivacidadOficio.ID_GERENCIA);
        qryNoNotif.setParameter(3, Constantes.BOOLEAN_FALSE);
        
        final List<Object[]> resultadoConsulta = qryNoNotif.getResultList();

        final List<OficioPromovibleVo> oficios = castVo(resultadoConsulta);

        final List<OficioPromovibleVo> resultado = new ArrayList<>();

        // agregar movimientos de oficio
        for (final OficioPromovibleVo vo : oficios) {

            vo.setMovimientos(obtenerMovimientos(vo.getOficioId()));
            if (vo.getEstatusId() != Constantes.OFICIOS_ESTATUS_ID_OFICIO_TERMINADO 
                    && vo.getEstatusId() != Constantes.OFICIOS_ESTATUS_ID_OFICIO_ANULADO) {
                resultado.add(vo);
            }
        }

        return resultado;

    }

    /**
     *
     * Regresa las condiciones de consulta para obtener los tipos y estatus de
     * oficios de acuerdo a los permisos.
     *
     * @param permisosVo
     * @param sql
     * @param params
     */
    private void condicionesBandeja(PermisosVo permisosVo, StringBuilder sql, List<String> params) {

        String condicionOficio = " (of1.OF_TIPO_OFICIO = ?::integer and of1.ESTATUS = ?::integer) ";

        StringBuilder sb = new StringBuilder();
        if (permisosVo.isRolEmisorOficiosEntrada() && permisosVo.isRolEmisorOficiosSalida()) {
            if (permisosVo.isRolReceptorMonterrey() && permisosVo.isRolReceptorReynosa()) {
                condicionOficio = "(of1.ESTATUS <> ?::integer)";
                sb.append(condicionOficio);

                params.add(String.valueOf(Constantes.OFICIOS_ESTATUS_ID_OFICIO_ANULADO));
                sb.append(" and ");

                sb.append(condicionOficio);

                params.add(String.valueOf(Constantes.OFICIOS_ESTATUS_ID_OFICIO_TERMINADO));
            }
        } else {
            // los permisos emisores son exclusivos
            if (permisosVo.isRolEmisorOficiosSalida()) {

                sb.append(condicionOficio);

                params.add(String.valueOf(Constantes.OFICIOS_TIPO_OFICIO_SALIDA_ID));
                params.add(String.valueOf(Constantes.OFICIOS_ESTATUS_ID_OFICIO_CREADO));

            } else if (permisosVo.isRolEmisorOficiosEntrada()) {

                sb.append(condicionOficio);

                params.add(String.valueOf(Constantes.OFICIOS_TIPO_OFICIO_ENTRADA_ID));
                params.add(String.valueOf(Constantes.OFICIOS_ESTATUS_ID_OFICIO_CREADO));

            }

            // los permisos receptores son exclusivos
            if (permisosVo.isRolReceptorMonterrey()) {

                if (sb.length() > 0) {
                    sb.append(" or ");
                }

                sb.append(condicionOficio);
                sb.append(" or ");
                sb.append(condicionOficio);

                params.add(String.valueOf(Constantes.OFICIOS_TIPO_OFICIO_SALIDA_ID));
                params.add(String.valueOf(Constantes.OFICIOS_ESTATUS_ID_RECIBIDO_PEMEX));

                params.add(String.valueOf(Constantes.OFICIOS_TIPO_OFICIO_ENTRADA_ID));
                params.add(String.valueOf(Constantes.OFICIOS_ESTATUS_ID_ENVIADO_MONTERREY));

            } else if (permisosVo.isRolReceptorReynosa()) {

                if (sb.length() > 0) {
                    sb.append(" or ");
                }

                sb.append(condicionOficio);

                params.add(String.valueOf(Constantes.OFICIOS_TIPO_OFICIO_SALIDA_ID));
                params.add(String.valueOf(Constantes.OFICIOS_ESTATUS_ID_ENVIADO_REYNOSA));

            }
        }
        sql.append(sb);

    }

    /**
     *
     * Obtiene los oficios correspondientes a la bandeja de entrada de acuerdo a
     * los permisos del usuario.
     *
     * @param oficioVo
     * @param permisosVo
     * @param bloques
     * @return
     */
    
    public List<OficioPromovibleVo> buscarOficiosBandejaEntrada(
            OficioVo oficioVo,
            PermisosVo permisosVo,
            List<CompaniaBloqueGerenciaVo> bloques, int bloqueActivo) {

        getLogger().info(this, "@buscarOficiosBandejaEntrada");

        // preparar query en función de los parámetros recibidos
        final StringBuilder sql = new StringBuilder(300);

        // para los parámetros
        final List<String> params = new ArrayList<>();

        sql.append(queryBase)
                .append(" and ap_c.id = ?::integer");
        params.add(String.valueOf(bloqueActivo));

        sql.append(" and (");

        condicionesBandeja(permisosVo, sql, params);

        sql.append(")");

        // filtros de la forma
        if (oficioVo.getOficioNumero() != null && oficioVo.getOficioNumero().trim().length() > 0) {
            sql.append(" and of1.numero_oficio collate \"es_ES\" like ? "); // num oficio
            params.add("%" + oficioVo.getOficioNumero().trim() + "%");
        }
        if (oficioVo.getOficioAsunto() != null && oficioVo.getOficioAsunto().trim().length() > 0) {
            sql.append(" and of1.asunto collate \"es_ES\" like ? "); // asunto
            params.add("%" + oficioVo.getOficioAsunto().trim() + "%");
        }

        // filtrar por los campos a los que el usuario tiene acceso
//        if (!permisosVo.isVerTodoGerencias()) {
//
//            //agregarFiltroBloques(bloques, sb, params);
//        }

        sql.append(queryBaseGroupBy)
                .append(" ORDER BY of1.fecha_genero desc,trim(upper (c.NOMBRE)) asc, trim(upper(ap_c.NOMBRE)) asc, trim(upper(g.NOMBRE)) asc, trim(upper(of1.numero_oficio)) asc ");

        final Query nativeQuery = em.createNativeQuery(sql.toString());

        // establecer parámetros de busqueda
        int i = 0;

        for (String param : params) {
            nativeQuery.setParameter(++i, param);
        }

        final List<Object[]> resultado = nativeQuery.getResultList();

        return castVo(resultado);

    }

    /**
     *
     * @param vo
     * @param filtrarRestringidos Si es true, se excluyen los oficios
     * restringidos a los que el usuario no tiene acceso.
     * @param usuarioId
     * @return
     */
    
    public final ResultadosConsultaVo buscarOficios(
            final OficioVo vo,
            final boolean filtrarRestringidos,
            final String usuarioId) {

        
        //FIXME : revisar la lógica para filtrar desde la consulta y no tener que iterar en los resultados.
        
        List<OficioPromovibleVo> resultados = buscarOficios(vo);

        // validar si se deben remover los oficios restringidos
        if (filtrarRestringidos) {

            List<OficioPromovibleVo> remover = new ArrayList<>();

            for (OficioPromovibleVo oficio : resultados) {

                getLogger().debug(this, "@buscarOficios-filtrarRestringidos -- num ofic = '" + oficio.getOficioNumero() + "', "
                        + "restringido = '" + oficio.isRestringido() + "', "
                        + "tiene acceso = '" + oficio.tieneAccesoOficioRestringido(usuarioId) + "'");

                if (oficio.isRestringido() && !oficio.tieneAccesoOficioRestringido(usuarioId)) {
                    remover.add(oficio);
                }
            }

            resultados.removeAll(remover);
        }

        ResultadosConsultaVo resultadosVo = new ResultadosConsultaVo();

        //FIXME : poner el límite en la consulta a la base de datos
        
        // verificar máximo de 100 registros
        if (resultados.size() > vo.getMaxOficios()) {

            resultadosVo.setCantidadOriginal(resultados.size());
            resultadosVo.setCantidadMaximaExcedida(true);

            // remover los elementos excedentes
            resultados.subList(vo.getMaxOficios(), resultados.size()).clear();

        }

        resultadosVo.setResultados(resultados);

        return resultadosVo;

    }

    /**
     * Realiza una búsqueda de registros de oficios con los parámetros de
     * consulta especificados.
     *
     * @param vo Objeto contenedor con los parámetros de consulta.
     * @return Lista con los resultados. En caso de no encontrar registros,
     * retorna lista vacía (nunca nulo).
     */
    private List<OficioPromovibleVo> buscarOficios(OficioVo vo) {

        getLogger().info(this, "@buscarOficios - params = {0}", new Object[]{vo});
        
        System.out.println("@buscar oficios "+vo.toString());

        // preparar query en función de los parámetros recibidos
        final StringBuilder sql = new StringBuilder();

        sql.append(queryBase);

        final List<Object> params = new ArrayList<>();

        if (!UtilSia.isNullOrBlank(vo.getCompaniaRfc()) && UtilSia.greaterThanZero(vo.getBloqueId())
                && UtilSia.greaterThanZero(vo.getGerenciaId())) {
            sql.append("and ( (of1.co_privacidad <= 4 "
                    + " and c.rfc COLLATE \"es_ES\" = ? "); // compañia
            params.add(vo.getCompaniaRfc().trim());
            
            sql.append("and ap_c.id = ? "); // bloque
            params.add(vo.getBloqueId());
            
            sql.append("and g.id = ? )"); // gerencia
            params.add(vo.getGerenciaId());
            
            // mostrar los oficios publicos
            if (vo.getMostrarPublicos() != Constantes.CERO) {
                sql.append(" or(of1.co_privacidad = 1")
                        .append(" and c.rfc COLLATE \"es_ES\" = ? "); // compañia
                params.add(vo.getCompaniaRfc().trim());
                
                sql.append("and ap_c.id = ? )"); // bloque
                params.add(vo.getBloqueId());
            }

            sql.append(" ) ");
        } else if (!UtilSia.isNullOrBlank(vo.getCompaniaRfc()) && UtilSia.greaterThanZero(vo.getBloqueId())) { //busqueda pro compañia y bloque
            sql.append("and ( of1.co_privacidad <= 4 and ")
                    .append(" c.rfc COLLATE \"es_ES\" = ? "); // compañia
            params.add(vo.getCompaniaRfc().trim());
            
            sql.append("and ap_c.id = ? "); // bloque
            params.add(vo.getBloqueId());
            
            sql.append("  ) ");
        } else if (!UtilSia.isNullOrBlank(vo.getCompaniaRfc())) { //AQUI ENTRA A BUSCAR POR COMPAÑIA
            sql.append("and ( of1.co_privacidad <= 4 AND  ")
                    .append(" c.rfc COLLATE \"es_ES\" = ? "); // compañia
            
            params.add(vo.getCompaniaRfc().trim());
            sql.append(" )");
        }
        
        if (UtilSia.greaterThanZero(vo.getEstatusId())) {
            sql.append("and est.id = ? "); // estatus
            params.add(vo.getEstatusId());
        }
        
        if (!UtilSia.isNullOrBlank(vo.getOficioNumero())) {
            sql.append("and UPPER(of1.numero_oficio) COLLATE \"es_ES\" like UPPER(?) "); // num oficio
            params.add("%" + vo.getOficioNumero().trim() + "%");
        }
        
        if (!UtilSia.isNullOrBlank(vo.getOficioAsunto())) {
            sql.append("and UPPER(of1.asunto) COLLATE \"es_ES\" like UPPER(?) "); // asunto
            params.add("%" + vo.getOficioAsunto().trim() + "%");
        }
        
        if (!UtilSia.isNullOrBlank(vo.getObservaciones())) {
            sql.append("and UPPER(of1.observaciones) COLLATE \"es_ES\" like UPPER(?) "); // descr oficio
            params.add("%" + vo.getObservaciones().trim() + "%");
        }
        
        if (UtilSia.greaterThanZero(vo.getTipoOficioId())) {
            sql.append("and tof.id = ? "); // tipo oficio
            params.add(vo.getTipoOficioId());
        }

        // aplicar fechas de oficio
        if (vo.getOficioFechaDesde() != null && vo.getOficioFechaHasta() != null) {
            sql.append("and of1.fecha_oficio between ? and ? ");
            params.add(vo.getOficioFechaDesde());
            params.add(vo.getOficioFechaHasta());
        } else if (vo.getOficioFechaDesde() != null && vo.getOficioFechaHasta() == null) {
            sql.append("and of1.fecha_oficio >= ? ");
            params.add(vo.getOficioFechaDesde());
        } else if (vo.getOficioFechaDesde() == null && vo.getOficioFechaHasta() != null) {
            sql.append("and of1.fecha_oficio <= ? ");
            params.add(vo.getOficioFechaHasta());
        }

        // aplicar fechas de alta
        if (vo.getFechaAltaDesde() != null && vo.getFechaAltaHasta() != null) {
            sql.append("and of1.fecha_genero between ? and ? ");
            params.add(vo.getFechaAltaDesde());
            params.add(vo.getFechaAltaHasta());
        } else if (vo.getFechaAltaDesde() != null && vo.getFechaAltaHasta() == null) {
            sql.append("and of1.fecha_genero >= ? ");
            params.add(vo.getFechaAltaDesde());
        } else if (vo.getFechaAltaDesde() == null && vo.getFechaAltaHasta() != null) {
            sql.append("and of1.fecha_genero <= ? ");
            params.add(vo.getFechaAltaHasta());
        }

        // para consulta de oficios para asociación
        if (UtilSia.greaterThanZero(vo.getOficioIdExcluir())) {
            sql.append("and of1.id <> ? ");
            params.add(vo.getOficioIdExcluir());
        }

        // requiere seguimiento
        if (vo.getSeguimientoConsulta() != Constantes.OFICIOS_CONSULTA_SEGUIMIENTO_TODOS) {
            sql.append("and of1.seguimiento = ? ");

            switch (vo.getSeguimientoConsulta()) {
                case Constantes.OFICIOS_CONSULTA_SEGUIMIENTO_SI:
                    params.add(Constantes.BOOLEAN_TRUE);
                    break;
                case Constantes.OFICIOS_CONSULTA_SEGUIMIENTO_NO:
                    params.add(Constantes.BOOLEAN_FALSE);
                    break;
            }
        }

        //sb.append(queryBaseGroupBy);

        //sb.append(" ORDER BY of1.FECHA_OFICIO DESC "); se cambia a ordenar como en bandeja de entrada jevazquez
        sql.append(
                " ORDER BY c.NOMBRE asc, ap_c.NOMBRE asc, g.NOMBRE asc, of1.numero_oficio asc \n"
                +"LIMIT "
        ).append(vo.getMaxOficios());

        getLogger().info(this, "query = {0}", new Object[]{sql});

        System.out.println(" ====== em "+ (em == null));
        System.out.println("  "+ sql != null ? sql.toString():" ES NULLL");
        
        final Query qryProm = em.createNativeQuery(sql.toString());

        // establecer condiciones de consulta
        int i = 0;

        for (final Object param : params) {
            qryProm.setParameter(++i, param);
        }

        final List<Object[]> resultado = qryProm.getResultList();

        getLogger().debug(this, "resultados = " + resultado.size());

        return castVo(resultado);

    }

    /**
     *
     * Realiza la búsqueda de oficios para asociar. Los oficios deben estar en
     * estatus Terminado.
     *
     * @param vo Parámetros de consulta del oficio
     * @param bloqueId ID del bloque del oficio a asociar
     * @return
     */
    
    public List<OficioPromovibleVo> buscarOficiosAsociacion(
            OficioVo vo,
            int bloqueId) {

        // preparar query en función de los parámetros recibidos
        StringBuilder sb = new StringBuilder();

        sb.append(queryBase);

        List params = new ArrayList();

        // busqueda por numero de oficio
        if (vo.getOficioNumero() != null && vo.getOficioNumero().trim().length() > 0) {
            sb.append("and of1.numero_oficio COLLATE \"es_ES\" like ? ");
            params.add("%" + vo.getOficioNumero().trim() + "%");
        }

        // para consulta de oficios para asociación
        if (vo.getOficioIdExcluir() != null && vo.getOficioIdExcluir() > 0) {
            sb.append("and of1.id <> ? ");
            params.add(vo.getOficioIdExcluir());
        }

        // excluir los oficios ya asociados
        if (vo.getAsociadoHaciaOficios().size() > 0) {

            sb.append("and of1.id not in (")
                    .append(UtilSia.toCommaSeparatedString(vo.getAsociadoHaciaOficiosListaIds(), false))
                    .append(") ");
        }

        // estatus debe estar terminado
        sb.append("and est.id = ? "); // estatus
        params.add(Constantes.OFICIOS_ESTATUS_ID_OFICIO_TERMINADO);

        /*if (!puedeVerTodasGerencias) {

	 agregarFiltroBloques(bloques, sb, params);
	 }*/
        // bloque ID
        sb.append("and ap_c.id = ? ");
        params.add(bloqueId);

        sb.append(queryBaseGroupBy)
                .append(" ORDER BY of1.FECHA_OFICIO DESC ");

        getLogger().info(this, "query = " + sb.toString());

        Query q = em.createNativeQuery(sb.toString());

        // establecer condiciones de consulta
        int i = 0;

        for (Object param : params) {
            q.setParameter(++i, param);
        }

        List<Object[]> resultado = q.getResultList();

        getLogger().info(this, "resultados = " + resultado.size());

        return castVo(resultado);

    }

    /**
     * Realiza una búsqueda de usuariosRey con acceso al bloque proporcionado
     * para consulta de oficios con acceso restringido.
     *
     * @param nombre Valor de consulta para nombre del usuario
     * @param bloqueId ID del bloque del oficio a dar acceso restringido
     * @return La lista de usuariosRey con permiso mínimo de consulta para
     * acceder al usuario restringido
     */
    
    public List<UsuarioVO> buscarUsuariosAccesoOficioRestringido(String nombre, OficioVo oficioVo) {

        getLogger().info(this, "@buscarUsuariosAccesoOficioRestringido - bloqueId = " + oficioVo.getBloqueId());

        // los usuariosRey deberán tener acceso mínimo de consulta y tener acceso
        // al bloque del oficio para que puedan buscar y acceder al detalle del
        // oficio restringido
        // excluir los usuariosRey ya seleccionados
        List<UsuarioVO> usuariosAcceso
                = usuarioServicioRemoto.getUsuariosPorRolBloque(
                        Constantes.OFICIOS_ROL_CONSULTA_OFICIOS,
                        oficioVo.getBloqueId(),
                        null,
                        nombre,
                        oficioVo.getRestringidoAUsuariosIds());

        return usuariosAcceso;
    }

    /**
     *
     * @param bloques
     * @param sb
     * @param params
     */
    private void agregarFiltroBloques(
            final List<CompaniaBloqueGerenciaVo> bloques,
            final StringBuilder sb,
            final List params) {

        if (bloques != null && !bloques.isEmpty()) {

            // bloque ID
            sb.append("and ap_c.id in (");

            for (CompaniaBloqueGerenciaVo bloqueVo : bloques) {
                sb.append("?, ");
                params.add(String.valueOf(bloqueVo.getBloqueId()));
            }

            // remover ultimos caracteres
            sb.setLength(sb.length() - 2);

            sb.append(") ");

        }
    }

    /**
     * Regresa todos los registros.
     *
     * @return
     */
    /*
     public List<OficioPromovibleVo> findAllOficios() {

     StringBuilder sb = new StringBuilder();
     sb.append(queryBase).append(" ORDER BY of1.FECHA_OFICIO DESC ");

     return executeQuery(sb.toString());

     }*/
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Métodos de utilería">
    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    /**
     *
     * @return
     */
    private UtilLog4j getLogger() {
        return UtilLog4j.log;
    }

    
    /**
     * Prepara el value object con el resultado de la consulta.
     *
     * @param lista
     * @return
     */
    private List<OficioPromovibleVo> castVo(List<Object[]> lista) {

        List<OficioPromovibleVo> result = new ArrayList();

        for (Object[] obj : lista) {

            OficioPromovibleVo vo = castVo(obj);

            result.add(vo);

        }

        return result;

    }

    /**
     * Genera una instancia del tipo de oficio solicitado.
     *
     * @param tipoOficio
     * @return
     */
    private OficioPromovibleVo instanciarOficioVo(int tipoOficio) {

        OficioPromovibleVo vo = null;

        if (tipoOficio == Constantes.OFICIOS_TIPO_OFICIO_SALIDA_ID) {
            vo = new OficioSalidaVo();
        } else if (tipoOficio == Constantes.OFICIOS_TIPO_OFICIO_ENTRADA_ID) {
            vo = new OficioEntradaVo();
        }

        return vo;

    }

    /**
     * Mapea los campos resultado de una consulta de una entidad a un objeto
     * OficioVo.
     *
     * @param obj
     * @return
     */
    private OficioPromovibleVo castVo(Object[] obj) {

        int i = 0;

        int tipoOficioId = (Integer) obj[i++];

        OficioPromovibleVo vo = instanciarOficioVo(tipoOficioId);

        // tipo de oficio
        vo.setTipoOficioId(tipoOficioId);
        vo.setTipoOficioNombre(String.valueOf(obj[i++]));

        // información del oficio
        vo.setOficioId((Integer) obj[i++]);
        vo.setOficioNumero(String.valueOf(obj[i++]));
        vo.setOficioFecha((java.util.Date) obj[i++]);
        vo.setFechaGenero((java.util.Date) obj[i++]);
        vo.setOficioAsunto(String.valueOf(obj[i++]));
        vo.setObservaciones(String.valueOf(obj[i++]));

        // información de oficio asociado
        /*Object asociadoId = obj[i++];
	 Object asociadoNumero = obj[i++];*/
        // compañía
        vo.setCompaniaRfc(String.valueOf(obj[i++]));
        vo.setCompaniaNombre(String.valueOf(obj[i++]));
        vo.setCompaniaSiglas(String.valueOf(obj[i++]));

        // bloque
        vo.setBloqueId((Integer) obj[i++]);
        vo.setBloqueNombre(String.valueOf(obj[i++]));

        // gerencia
        vo.setGerenciaId((Integer) obj[i++]);
        vo.setGerenciaNombre(String.valueOf(obj[i++]));

        // estatus
        vo.setEstatusId((Integer) obj[i++]);
        vo.setEstatusNombre(String.valueOf(obj[i++]));

        // usuario
        vo.setGenero(String.valueOf(obj[i++]));
        vo.setUsuarioGeneroNombre(String.valueOf(obj[i++]));

        // archivo adjunto
        // Los archivos adjuntos se relacionan con los movimeintos
        // de este oficio
        AdjuntoOficioVo adj = new AdjuntoOficioVo();

        vo.setArchivoAdjunto(adj);
        vo.setEliminado((Boolean) obj[i++]);
        vo.setUrgente((Boolean) obj[i++]);
        vo.setRequiereSeguimiento((Boolean) obj[i++]);
        vo.setAcceso(PrivacidadOficio.getPrivacidadOficio((Integer) obj[i++]));
        vo.setRestringidoAUsuariosIds(String.valueOf(obj[i++]));

        return vo;

    }

    /**
     *
     * @param lista
     * @return
     */
    private List<MovimientoVo> castMovimientosVo(List lista) {

        List<MovimientoVo> result = new ArrayList();

        for (Iterator it = lista.iterator(); it.hasNext();) {

            Object[] obj = (Object[]) it.next();

            MovimientoVo vo = castMovimientoVo(obj);

            result.add(vo);

        }

        return result;

    }

    /**
     * Mapea el resultado de una consulta en un objeto de MovimientoVo.
     *
     * @param obj
     * @return
     */
    private MovimientoVo castMovimientoVo(Object[] obj) {

        MovimientoVo vo = new MovimientoVo();

        int i = 0;

        vo.setOficioMovimientoId((Integer) obj[i++]);

        vo.setOficioId((Integer) obj[i++]);
        vo.setOficioNumero(String.valueOf(obj[i++]));

        vo.setOperacionId((Integer) obj[i++]);
        vo.setOperacion(String.valueOf(obj[i++]));

        vo.setId((Integer) obj[i++]);
        vo.setMotivo(String.valueOf(obj[i++]));

        // archivo adjunto
        AdjuntoOficioVo adj = new AdjuntoOficioVo();

        adj.setId((Integer) obj[i++]);
        adj.setNombre(String.valueOf(obj[i++]));
        adj.setUrl(String.valueOf(obj[i++]));
        adj.setTipoArchivo(String.valueOf(obj[i++]));
        adj.setPeso(String.valueOf(obj[i++]));
        adj.setUuid(String.valueOf(obj[i++]));
        
        final Object eliminado = obj[i++];
        adj.setEliminado(eliminado == null ? Boolean.FALSE : (Boolean)eliminado);

        vo.setAdjunto(adj);

        vo.setGenero(String.valueOf(obj[i++]));
        vo.setNombre(String.valueOf(obj[i++]));
        vo.setFechaGenero((java.util.Date) obj[i++]);
        vo.setHoraGenero((java.util.Date) obj[i++]);

        return vo;

    }

    /**
     * Mapeo de entidad de archivo adjunto a VO.
     *
     *
     * @param adjunto
     * @return
     */
    private AdjuntoOficioVo castAdjuntoVo(SiAdjunto adjunto) {

        // archivo adjunto
        AdjuntoOficioVo resultado = new AdjuntoOficioVo();

        resultado.setId(adjunto.getId());
        resultado.setNombre(adjunto.getNombre());
        resultado.setUrl(adjunto.getUrl());
        resultado.setTipoArchivo(adjunto.getTipoArchivo());
        resultado.setPeso(adjunto.getPeso());
        resultado.setUuid(adjunto.getUuid());

        return resultado;

    }

    // </editor-fold>
}
