package sia.constantes;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 *
 * @author sluis
 */
public class Constantes {

    /*
     * ======================== Constantes para los Booleanos y Estados
     * Generales - INICIO ================================
     */
    /**
     * True
     */
    public static final boolean BOOLEAN_TRUE = true;
    /**
     * False
     */
    public static final boolean BOOLEAN_FALSE = false;
    public static final String NULL = "NULL";
    public static final String NULL_minus = "null";
    public static final String VACIO = "";
    public static final String BLANCO = " ";
    public static final String GUION = "-";
    public static final String GUION_BAJO = "_";
    public static final String PUNTO = ".";
    public static final String COMA = ",";
    public static final String COMILLA_SIMPLE = "'";
    public static final String COMILLA_DOBLE = "\"";
    public static final String COMA_COMILLAS_SIMPLES = "','";
    public static final int CERO = 0;
    public static final int MENOS_UNO = -1;
    public static final String CORREO_TODO_IHSA = "Todoihsa@ihsa.mx";
    public static final String NEGRO = "Negro";
    public static final String HTML_NBSP = "&nbsp;";
    public static final String RUTA_PUBLICA_SEMAFORO_AMARILLO = Configurador.urlSia() + "resources/img/Amarillo.png";
    public static final String RUTA_PUBLICA_SEMAFORO_NEGRO = Configurador.urlSia() + "resources/img/Negro.png";
    public static final String RUTA_PUBLICA_SEMAFORO_ROJO = Configurador.urlSia() + "resources/img/Rojo.png";
    public static final String RUTA_PUBLICA_SEMAFORO_VERDE = Configurador.urlSia() + "resources/img/Verde.png";
    // caracteres especiales para depurar cadenas antes de su guardardo en base de datos
    public static final String CARACTER_WORD_COMILLA_DOBLE_ABRE = "“";
    public static final String CARACTER_WORD_COMILLA_DOBLE_CIERRA = "”";
    public static final String CARACTER_WORD_GUION = "–";
    // File sizes used to generate formatted label
    public static final long MEGABYTE_LENGTH_BYTES = 1048000l;
    public static final long KILOBYTE_LENGTH_BYTES = 1024l;
    /**
     * Valor : 1 Utilizar para uso general por ejemplo : if(fila ==
     * Constantes.UNO)..
     */
    public static final int UNO = 1;
    /**
     * Valor : 2 Utilizar para uso general por ejemplo : if(fila ==
     * Constantes.UNO)..
     */
    public static final int DOS = 2;
    /**
     * Es el estado normal de un objeto. Se usa normalmente al crearlos y
     * modificarlos. Ejemplo crear: Object o;
     * o.setEliminado(Constantes.NO_ELIMINADO); super.create(o)
     *
     * Ejemplo editar: Object o; o.setEliminado(Constantes.NO_ELIMINADO);
     * super.edit(o)
     */
    public static final boolean NO_ELIMINADO = BOOLEAN_FALSE;
    /**
     * Usado para cambiar el estado de un elmento u objeto a eliminado. Se usa
     * normalmente al eliminarlos Ejemplo eliminar: Object o;
     * o.setEliminado(Constantes.ELIMINADO); super.edit(o)
     */
    public static final boolean ELIMINADO = BOOLEAN_TRUE;
    /**
     * Usado para un objeto u elemento que ha sido modificado True
     */
    public static final boolean MODIFICADO = BOOLEAN_TRUE;
    /**
     * Usado para un objeto u elemento que no ha sido modificado nunca desde su
     * creacion False
     */
    public static final boolean NO_MODIFICADO = BOOLEAN_FALSE;
    /**
     * Usado para indicar estados Activos
     */
    public static final boolean STATUS_ACTIVO = BOOLEAN_TRUE;
    /**
     * Usado para indicar estados Inactivos
     */
    public static final boolean STATUS_INACTIVO = BOOLEAN_FALSE;
    /**
     * Usado para indicar un estado Finalizado
     */
    public static final String STATUS_FINALIZADO = "FINALIZADO";
    /**
     * Usado para indicar un estado Cancelado
     */
    public static final String STATUS_CANCELADO = "CANCELADO";
    /**
     * Estatus para Solicitud de Estancia
     */
    public static final int STATUS_SOLICITUD_ESTANCIA_ENVIADA = 10;
    /**
     * Constante para el modulo Requisicion
     */
    public static final String M_REQUISICION = "Requisiciones";
    public static final String M_ORDEN = "Orden C/S";
    public static final int MODULO_REQUISICION = 1;
    public static final int MODULO_COMPRA = 2;
    /**
     * Constante para el modulo Contratos
     */
    public static final int MODULO_CONTRATO = 6;
    public static final int MODULO_ADMINSIA = 24;
//    public static final int MODULO_CONTRATO_MODULO = 25;
    public static final int STATUS_INICIO = 300;
    public static final int STATUS_FIN = 305;
    /**
     * Constante para el modulo SGyL
     */
    public static final int MODULO_SGYL = 9;
    public static final int INDICE_CERO = 0;
    public static final int ESTATUS_SOLICITUD_ESTANCIA_PENDIENTE = 1;
    public static final int ESTATUS_SOLICITUD_ESTANCIA_TEMPORAL = 90;
    public static final int ESTATUS_SOLICITUD_ESTANCIA_SOLICITADA = 10;
    public static final int ESTATUS_SOLICITUD_ESTANCIA_ASIGNADA = 40;
    public static final int ESTATUS_SOLICITUD_ESTANCIA_CANCELADA = 50;
    //Motivo aereo
    public static final int MOTIVO_AEREO = 270;
    //---------------------------------------------------------------
    /**
     * Es el asunto del correo cuando se notifica que se tiene que dar Visto
     * bueno una solicitud
     */
    public static final String MENSAJE_ESTATUS_SOLICITUD_VIAJE_VISTO_BUENO = "Visto Bueno a la solicitud de viaje: ";
    /**
     * Es el asunto del correo cuando se notifica que se tiene que aprobar una
     * solicitud Valor : "Aprobar la solicitud de viaje:"
     */
    public static final String MENSAJE_ESTATUS_SOLICITUD_VIAJE_APROBAR = "Aprobar la solicitud de viaje: ";

    public static final String MENSAJE_ESTATUS_SOLICITUDES_VIAJE_APROBAR = "Aprobar las solicitudes de viaje: ";
    /**
     * Es el asunto del correo cuando se notifica que se tiene que justificar
     * una solicitud Valor : "Aprobar la justificación de la solicitud de
     * viaje:"
     */
    public static final String MENSAJE_ESTATUS_SOLICITUD_VIAJE_JUSTIFICAR = "Autorizar la solicitud de viaje: ";
    /**
     * Es el asunto del correo cuando se notifica que se tiene que Aprobar una
     * solicitud
     */
    public static final String MENSAJE_ESTATUS_SOLICITUD_VIAJE_AUTORIZAR = "Autorizar la solicitud de viaje: ";
    /**
     * Es el asunto del correo cuando se notifica que se tiene que crear un
     * viaje
     */
    public static final String MENSAJE_ESTATUS_SOLICITUD_VIAJE_PARA_CREAR_VIAJE_POR_ANALISTAS = "Crear Viaje para la solicitud: ";

    /*Este asunto se muestra solo en solicitudes aereas para enviar correo para la agencia en viajes aereos*/
    public static final String MENSAJE_ESTATUS_SOLICITUD_AUTORIZADA = " Solicitud de viaje autorizada: ";
    //--------------------------------------------------------------
    public static final String SOLICITUD = "Solicitud";
    public static final String VIAJE = "Viaje";
    /**
     * Estatus 401 pendiente o en bandeja de solicitudes (por solicitar)
     */
    public static final int ESTATUS_SOLICITUD_VIAJE_CANCELADO = 400;
    public static final int ESTATUS_PENDIENTE = 401;
    public static final String ESTATUS_PENDIENTE_NOMBRE = "CREADA";
    /**
     * Estatus 410 Estatus para Solicitada
     */
    public static final int ESTATUS_SOLICITADA = 410;
    /**
     * Estatus 415 visto bueno
     */
    public static final int ESTATUS_VISTO_BUENO = 415;
    public static final int ESTATUS_APROBAR = 420;
    public static final int ESTATUS_SEGURIDAD = 430;
    public static final int ESTATUS_JUSTIFICAR = 435;
    public static final int ESTATUS_APROBAR_JUSTIFICACION = 437; //este no se utiliza
    public static final int ESTATUS_GERENTE_NO_APROBO = 425;
    public static final int ESTATUS_CON_CENTOPS = 438;
    /**
     * Estatus 440 Estatus para Autorizar
     */
    public static final int ESTATUS_AUTORIZAR = 440;
    /**
     * Estatus 450 - para que los analistas vean la solicitud
     */
    public static final int ESTATUS_PARA_HACER_VIAJE = 450;
    public static final int QUERY_AEREO_CAMBIO_ITINERARIO = 1;
    public static final int QUERY_TRAER_CAMBIO_ITINERARIO = 2;
    public static final int CONTAR_CAMBIOS_ITINERARIO = 4;
    public static final int ESTATUS_TERMINADA = 460;
    /**
     * Tipo de Elemento Ayuda (usado para diferenciar los archivos en la tabla
     * Adjunto)
     */
    public static final String TIPO_ARCHIVO_AYUDA = "ayuda";
    /**
     * Correo de ejemplo: ejemplo@ihsa.mx
     */
    public static final String CORREO_EJEMPLO = "ejemplo@ihsa.mx";
    /**
     * Id del Usuario que es Asistente de Dirección
     */
//    public static final String USUARIO_ASISTENTE_DIRECCION = "PMUNOZ";
    public static final String ROL_SGL_RESPONSABLE = "SGL_RESPONSABLE";
    public static final String ROL_SGL_ADMINISTRA = "SGL_ADMINISTRA";
    public static final String ROL_SGL_ANALISTA = "SGL_ANALISTA";
    public static final String ROL_SISTEMA_EMPLEADO = "SISTEMA_EMPLEADO";
    public static final String ROL_SISTEMA_GERENTE = "SISTEMA_GERENTE";
    public static final int ROL_ID_ASISTENTE_DIRECCION = 8;
    public static final int SGL_RESPONSABLE = 77;
    public static final int SGL_SEGURIDAD = 15;
    public static final int ROL_CENTRO_OPERACION = 16;
    public static final int ROL_JUSTIFICA_VIAJES = 64;
    public static final int ROL_ADMIN_VIAJES_AEREOS = 83;
    public static final String COD_ROL_NOTIFICA_SV_AEREAS = "NOT-SVA";
    /**
     * Valor : 9 Representa el id del rol de analistas
     */
    public static final int ROL_ID_SGL_CAPACITACION = 7;
    public static final int SGL_ANALISTA = 9;
    public static final int SGL_ADMINISTRA = 13;
    public static final int ROL_DIRECCION_GENERAL = 14;
    public static final int ROL_GERENTE = 12;
    public static final int ROL_EMPLEADO_GENERAL = 11;
    public static final int ROL_ID_ADMINISTRA_SGL = 13;
    public static final int ROL_ID_ANALISTA_SGL = 9;
    public static final int ROL_ID_ADM_ESTANCIA = 71;
    public static final String COD_ROL_ADMINISTRA_ESTANCIA = "ADM-EST";
    /**
     * *
     * CONSTANTES PARA CONTRATOS
     */
    public static final int ROL_ADMINISTRA_CONTRATO = 18;
    public static final int ROL_CONSULTA_CONTRATO = 19;
    public static final int ROL_REVISA_CONTRATO = 20;
    public static final int ROL_REGISTRA_PROVEEDOR = 73;
    public static final int ROL_VALIDA_DOCTOS = 84;
    /**
     * *
     * CONSTANTES PARA CONTRATOS
     */
    public static final int ROL_ASIGNA_REQUISICION = 21;
    public static final int ROL_COMPRADOR = 22;
    public static final int ROL_LOGISTICA_MATERIAL = 23;
    public static final int PLANTILLA_HTML_NOTIFICACION_SIA_LOGO_IZQUIERDA_ID = 1;
    public static final int PLANTILLA_HTML_FORMATO_REQUISICIONES_ID = 2;
    public static final int PLANTILLA_HTML_FORMATO_ORDENES_COMPRA_SERVICIO_ID = 3;
    public static final int PLANTILLA_HTML_FORMATO_CONTROL_DE_OFICIOS = 4;
    public static final int ROL_VISTO_BUENO_COSTO = 34;
    public static final int ROL_VISTO_BUENO_CONTABILIDAD = 35;
    public static final int ROL_SOCIO = 40;
    public static final String CODIGO_ROL_LICITACION = "49";
    //
    public static final int ROL_REPORTE_COMPRA = 48;
    public static final int ROL_ADMIN_SIA = 33;
    public static final String CODIGO_ROL_CANCELAR_OCS = "CANCOCS";
    public static final int ROL_REQUISICION_ESPERA = 1161;
    public static final int ROL_REQUISICION_ESPERA_ADM = 1162;

    /*
     * ======================== Constantes para los Booleanos y Estados
     * Generales - FIN ================================
     */
 /*
     * ======================== Constantes para Opción de Ayuda - INICIO
     * ================================
     */
    /**
     * Icono contraído (carpeta cerrada) en componente ice:tree
     */
    public static final String CONTRACTED_ICON = "./xmlhttp/css/xp/css-images/tree_folder_open.gif";
    /**
     * Icono expandido (carpeta abierta) en componente ice:tree
     */
    public static final String EXPANDED_ICON = "./xmlhttp/css/xp/css-images/tree_folder_close.gif";
    /**
     * Icono de hoja (archivo) en componente ice:tree
     */
    public static final String LEAF_ICON = "./xmlhttp/css/xp/css-images/tree_document.gif";
    /**
     * Icono de vídeo wmv
     */
    public static final String ICON_VIDEO_WMV = "./resources/imagenes/icon_video_wmv.png";
    /**
     * Icono de vídeo wmv para SiaWeb
     */
    public static final String ICON_VIDEO_WMV_SIAWEB = "./resources/SiaWeb/Imagenes/icon_video_wmv.png";
    /**
     * Icono de archivo pdf
     */
    public static final String ICON_PDF = "./resources/imagenes/icon_pdf.png";
    /**
     * Icono de archivo pdf para SiaWeb
     */
    public static final String ICON_PDF_SIAWEB = "./resources/SiaWeb/Imagenes/icon_pdf.png";
    /**
     * Extensión de archivo .pdf
     */
    public static final String ARCHIVO_EXTENSION_PDF = ".pdf";
    /**
     * Extensión de archivo .odt
     */
    public static final String ARCHIVO_EXTENSION_ODT = ".odt";
    /**
     * Extensión de archivo .odt
     */
    public static final String ARCHIVO_EXTENSION_TMP = ".tmp";
    /**
     * Extensión de archivo .wmv
     */
    public static final String ARCHIVO_EXTENSION_VIDEO_WINDOWS_MEDIA_PLAYER = ".wmv";
    /**
     * Content Type para PDF
     */
    public static final String CONTENT_TYPE_PDF = "application/pdf";
    /**
     * Content Type para ODT
     */
    public static final String CONTENT_TYPE_ODT = "application/vnd.oasis.opendocument.text";
    /**
     * Content Type para WMV
     */
    public static final String CONTENT_TYPE_WMV = "video/x-ms-wmv";
    public static final String PREFIJO_ARCHIVO_TEMPORAL_ICE_FACES = "ice_file_";
    /**
     * Nombre de módulo: Administración
     */
    public static final String NOMBRE_MODULO_ADMINISTRACION = "Administración";
    /**
     * NOmbre de módulo: SGyL (Servicios Generales y Logística)
     */
    public static final String NOMBRE_MODULO_SERVICIOS_GENERALES = "SGyL";
    /*
     * ======================== Constantes para Opción de Ayuda - FIN
     * ================================
     */
 /*
     * ======================== Constantes para Popups - INICIO
     * ================================
     */
    /**
     * Popup Crear
     */
    public static final String MOSTRAR_POPUP_CREAR = "crear";
    /**
     * Popup Actualizar
     */
    public static final String MOSTRAR_POPUP_ACTUALIZAR = "actualizar";
    /**
     * Popup Eliminar
     */
    public static final String MOSTRAR_POPUP_ELIMINAR = "eliminar";
    /*
     * ======================== Constantes para Popups - FIN
     * ================================
     */
 /*
     * ======================== Constantes para Conversaciones - INICIO
     * ================================
     */
    public static final String CONVERSACION_ARBOL_AYUDAS = "Arbol_Ayudas";
    /**
     * Conversación para Catálogo de Opciones
     */
    public static final String CONVERSACION_CATALOGO_OPCIONES = "Catalogo_Opciones";
    /**
     * Conversación para Catálogo de Módulos
     */
    public static final String CONVERSACION_CATALOGO_MODULOS = "Catalogo_Modulos";
    /**
     * Conversación para Catálogo de Ayudas
     */
    public static final String CONVERSACION_CATALOGO_AYUDAS = "Catalogo_Ayudas";
    /**
     * Conversación para Checklist de Staff
     */
    public static final String CONVERSACION_CHECKLIST_STAFF = "Checklist_Staff";
    /**
     * Conversación para Checklist de Oficina
     */
    public static final String CONVERSACION_CHECKLIST_OFICINA = "Checklist_Oficina";
    /**
     * Conversación para Checklist de Vehículo
     */
    public static final String CONVERSACION_CHECKLIST_VEHICULO = "Checklist_Vehiculo";
    /**
     * Conversación para Catálogo de Características
     */
    public static final String CONVERSACION_CATALOGO_CARACTERISTICAS = "Catalogo_Caracteristicas";
    /**
     * Conversación para Tipos
     */
    public static final String CONVERSACION_TIPOS = "Tipos";
    /**
     * Conversación para el Catálogo Sg_Aerolinea
     */
    public static final String CONVERSACION_CATALOGO_AEROLINEAS = "Catálogo_Aerolíneas";
    /**
     * Conversación para el Catálogo de Sg_Lugar
     */
    public static final String CONVERSACION_CATALOGO_SG_LUGAR = "Catálogo_Sg_Lugar";
    /**
     * Conversación para Staff
     */
    public static final String CONVERSACION_CATALOGO_STAFF = "Catalogo_Staff";
    /**
     * Conversación para Catálogo de Solicitudes de Estancia
     */
    public static final String CONVERSACION_CATALOGO_SOLICITUD_ESTANCIA = "Catalogo_Solicitud_Estancia";
    /**
     * Conversación para Catálogo de Modelos
     */
    public static final String CONVERSACION_CATALOGO_MODELO = "Catálogo_Modelo";
    /**
     * Conversación para Catálogo de Marcas
     */
    public static final String CONVERSACION_CATALOGO_MARCA = "Catálogo_Marca";
    /**
     * Conversación para Catálogo de Vehículo
     */
    public static final String CONVERSACION_CATALOGO_VEHICULO = "Catálogo_Vehículo";
    /**
     * Conversación para Registro de Huéspedes
     */
    public static final String CONVERSACION_REGISTRO_HUESPED = "Registro_Huésped";
    /**
     * Conversación para Cambiar de Habitación desde un Staff
     */
    public static final String CONVERSACION_CAMBIO_HABITACION_STAFF = "Cambio_Habitación_Staff";
    /**
     * Conversación para configurar los campos del Checklist de Vehículo
     */
    public static final String CONVERSACION_CONFIGURACION_CHECKLIST_VEHICULO = "Configuración_Checklist_Oficina";
    /**
     * Conversación para los Gastos de Viajes
     */
    public static final String CONVERSACION_GASTO_VIAJE = "Gasto_Viaje";
    /**
     * Conversación para las Solicitudes de Viaje
     */
    public static final String CONVERSACION_SOLICITUD_VIAJE = "Solicitud_Viaje";
    /**
     * Conversación para Baja de Vehículos
     */
    public static final String CONVERSACION_BAJA_VEHICULOS = "Baja_Vehículos";
    /**
     * Conversación para Cambio vehiculo de Oficina
     */
    public static final String CONVERSACION_CAMBIO_VEHICULO_OFICINA = "Cambio_Vehiculo_Oficina";
    /**
     * Conversación para el Catálogo de SiPais
     */
    public static final String CONVERSACION_CATALOGO_SI_PAIS = "Catálogo_SiPais";
    /**
     * Conversación para el Catálogo de SiEstado
     */
    public static final String CONVERSACION_CATALOGO_SI_ESTADO = "Catálogo_SiEstado";
    /**
     * Conversación para el Catálogo de SiCiudad
     */
    public static final String CONVERSACION_CATALOGO_SI_CIUDAD = "Catálogo_SiCiudad";
    public static final String CONVERSACION_BITACORA_VIAJE = "AdministrarBitacoraViaje";
    public static final String CONVERSACION_BUSCAR_SOLICITUD_VIAJE = "BuscarSolicitudViaje";
    public static final String CONVERSACION_CREAR_SOLICITUD_VIAJE = "CrearSolicitudViaje";
    public static final String CONVERSACION_MODIFICAR_SOLICITUD_VIAJE = "EditarSolicitudViaje";
    public static final String CONVERSACION_CATALOGO_RH_PUESTO = "Catálogo_RhPuesto";
    public static final String CONVERSACION_CATALOGO_AP_CAMPO_GERENCIA = "Catalogo_Ap_Campo_Gerencia";
    /*
     * ======================== Constantes para Conversaciones - FIN
     * ================================
     */
//    Características
    /**
     * Se usa si la Característica mostrará los subtítulos del Checklist
     */
    public static final boolean CARACTERISTICA_PRINCIPAL = BOOLEAN_TRUE;
    /**
     * Se usa si la Característica es normal y no es para guardar subtítulos de
     * Checklist
     */
    public static final boolean CARACTERISTICA_SECUNDARIA = BOOLEAN_FALSE;
//    Checklist
    /**
     * Checklist para Staff
     */
    public static final String CHECKLIST_STAFF = "Staff";
    /**
     * Checklist para Oficina
     */
    public static final String CHECKLIST_OFICINA = "Oficina";
    /**
     * Checklist para Vehículo
     */
    public static final String CHECKLIST_VEHICULO = "Vehiculo";
    /**
     * URL
     */
    public static final String URL = "/ServiciosGenerales/AbrirArchivo?a=";
    /**
     * URLs relativos para procesos de redireccionamiento.
     */
    public static final String URL_REL_SIA_SIGN_OUT = "/Sia/SingOut";
    public static final String URL_REL_SIA_PRINCIPAL = "/Sia";
    public static final String URL_REL_SIA_WEB = "/Compras";
    public static final String URL_REL_SERVICIOS_GENERALES = "/ServiciosGenerales";
    public static final String URL_REL_PROVEEDOR_INTERNO = "/ProveedorInterno";
    public static final String URL_REL_CONTROL_OFICIOS = "/ControlOficios";
    public static final String URL_REL_CONTRATO = "/Contratos";

    /**
     * Mensaje que avisa que no se tiene una oficna asignada
     */
    public static final String AVISO_NO_OFICINA = "Es necesario tener una Oficina asignada para poder acceder a esta opción del Sistema";
    /*
     * ======================== Constantes para Timers - INICIO
     * ================================
     */
    public static final String TIMER_AVISO_VENCIMIENTO_CONVENIOS = "TIMER_AVISO_VENCIMIENTO_CONVENIOS";
    //
    public static final int HORA_EJECUCION_TIMER_LICENCIA = 7;
    public static final String TIMER_AVISO_VENCIMIENTO_LICENCIAS = "TIMER_AVISO_VENCIMIENTO_LICENCIAS";
    public static final String TIMER_AVISO_VENCIMIENTO_LICENCIAS_SEMANAL = "TIMER_AVISO_VENCIMIENTO_LICENCIAS_SEMANAL";
    public static final String TIMER_AVISO_VENCIMIENTO_CURSO_MANEJO = "TIMER_AVISO_VENCIMIENTO_CURSO_MANEJO";
    public static final String TIMER_QUITA_VIGENCIA_CURSO_MANEJO = "TIMER_QUITA_VIGENCIA_CURSO_MANEJO";
    public static final String TIMER_QUITA_VIGENCIA_LICENCIA = "TIMER_QUITA_VIGENCIA_LICENCIA";
    public static final int QUINCE_DIAS_ANTICIPADOS = 15;
    //
    public static final String TIMER_AVISO_PAGOS = "TIMER_AVISO_PAGOS";
    public static final String TIMER_AVISO_SALIDA_ESTANCIA_STAFF = "TIMER_AVISO_SALIDA_ESTANCIA_STAFF";
    public static final String TIMER_VENCIMIENTO_PAGO_SERVICIO = "TIMER_VENCIMIENTO_PAGO_SERVICIO";
    public static final String TIMER_AVISO_MANTENIMIENTO = "TIMER_AVISO_MANTENIMIENTO";
    public static final String TIMER_SALIDA_AUTOMATICA = "TIMER_SALIDA_AUTOMATICA";
    //public static final String TIMER_SALIDA_VIAJES = "TIMER_SALIDA_VIAJES";
    public static final String TIMER_VIAJES_SEMAFORO = "TIMER_VIAJES_SEMAFORO";
    public static final String TIMER_LIMPIAR_VIAJESYSOLICITUDES = "TIMER_LIMPIAR_VIAJESYSOLICITUDES";
    public static final String TIMER_SV_CANCELAR = "TIMER_SV_CANCELAR";
    public static final String TIMER_REGRESO_AUTOMATICO_VIAJES_TERRESTRES_FUERA_OFICINA = "TIMER_REGRESO_AUTOMATICO_VIAJES_TERRESTRES_FUERA_OFICINA";
    public static final String TIMER_AVISO_MANTENIMIENTO_POR_PERIODO = "TIMER_AVISO_MANTENIMIENTO_POR_PERIODO";
    //
    public static final String HORA_EJECUCION_TIMER = "8";
    public static final String HORA_EJECUCION_TIMER_SEMAFORO = "5";
    public static final String HORA_EJECUCION_TIMER_LIMPIARSOLICITUDESYVIAJES = "3";
//    public static final String HORA_EJECUCION_TIMER_CANCEL_SV = "16";
//    public static final String HORA_EJECUCION_TIMER_CANCEL_SVV = "13";
//    public static final String HORA_EJECUCION_TIMER_PRUEBA = "16";
    //
    public static final int HORA_EJECUCION_TIMER_GESTION_RIESGO = 9;
    public static final String MINUTOS_EJECUCION_TIMER = "00";
    public static final String SEGUNDOS_EJECUCION_TIMER = "00";
    public static final int SESENTA_DIAS_ANTICIPADOS = 60;
    public static final int CUARENTAYCINCO_DIAS_ANTICIPADOS = 45;
    public static final int TREINTA_DIAS_ANTICIPADOS = 30;
    public static final int HORA_EJECUCION_TIMER_REPORTE_VIAJE = 19;
    public static final String TIMER_REPORTE_DIARIO_VIAJE = "REPORTE DIARIO VIAJES";
    public static final String TIMER_NOTIFICA_VIAJE_AEREO = "NOTIFICA SALIDA VIAJE AEREO";
    //Monto acumulado
    public static final int HORA_EJECUCION_TIMER_REPORTE_MONTO_ACUMULADO = 20;
    public static final String TIMER_REPORTE_MONTO_ACUMULADO = "REPORTE DIARIO OC/S";
    //Autoriza OC/S
    public static final int HORA_EJECUCION_TIMER_REPORTE_AUTORIZA_ORDENES = 20;
    public static final String TIMER_REPORTE_AUTORIZA_ORDENES = "REPORTE AUTORIZA OC/S";
    //
    public static final String TIMER_ESTADO_SEMAFORO = "VERIFICA ESTADO SEMAFORO";
    public static final int HORA_EJECUCION_TIMER_ESTADO_SEMAFORO = 17;
    //oc/s por autorizars
    public static final int HORA_EJECUCION_TIMER_REPORTE_ORDEN_AUTORIZAR = 20;
    public static final String TIMER_REPORTE_ORDEN_AUTORIZAR = "REPORTE DIARIO OC/S AUTORIZAR";
    /**
     * Reporte de compradores
     */
    public static final int HORA_EJECUCION_TIMER_REPORTE_COMPRADORES = 4;
    public static final String TIMER_REPORTE_COMPRADORES = "REPORTE DIARIO COMPRADORES";
    // Control de Oficios - Notificacion de oficios registrados
    public static final int HORA_EJECUCION_TIMER_CONTROL_OFICIOS_NOTIFICACION_ALTA_OFICIOS_7_PM = 19;
    public static final int HORA_EJECUCION_TIMER_CONTROL_OFICIOS_NOTIFICACION_ALTA_OFICIOS_3_PM = 15;
    public static final String TIMER_CONFIG_CONTROL_OFICIOS_NOTIFICACION_ALTA_OFICIOS = "TIMER_CONFIG_CONTROL_OFICIOS_NOTIFICACION_ALTA_OFICIOS";
    public static final String TIMER_CONFIG_REPORTE_COMPRADORES = "TIMER_CONFIG_REPORTE_COMPRADORES";
    public static final String TIMER_CONFIG_CONTROL_OFICIOS_NOTIFICACION_NO_PROMOVIDOS_OFICIOS = "TIMER_CONFIG_CONTROL_OFICIOS_NOTIFICACION_NO_PROMOVIDOS_OFICIOS";//jevazquez 23/feb/2015
    public static final String TIMER_CONFIG_REPORTE_SV_POR_APROBAR = "TIMER_CONFIG_REPORTE_SV_POR_APROBAR";
    public static final String TIMER_CONFIG_REPORTE_SV_POR_APROBAR_VIERNES = "TIMER_CONFIG_REPORTE_SV_POR_APROBAR_VIERNES";
    public static final String TIMER_CAMBIO_DE_APROBACION = "TIMER_CAMBIO_DE_APROBACION";
    public static final String TIMER_REPORTE_GERENTE_SG = "TIMER_REPORTE_GERENTE_SG";
    /*
     * ======================== Constantes para Timers - FIN
     * ================================
     */
    /**
     * Indica que un huésped está hospedado en un staff u hotel
     */
    public static final boolean HUESPED_HOSPEDADO = BOOLEAN_TRUE;
    /**
     * Indica que un huésped no está hospedado en un staff u hotel
     */
    public static final boolean HUESPED_NO_HOSPEDADO = BOOLEAN_FALSE;
    /**
     * Mensaje que se muestra si la fecha real de ingreso de un Huésped a una
     * habitación es mayor a la fecha de salida
     */
    public static final String MENSAJE_FECHA_REAL_INGRESO_HUESPED_INVALIDA = "La fecha real de ingreso debe ser menor que la fecha real de salida del Huésped";
    public static final String MENSAJE_DESCRIPCION_VIAJERO_SOLICITUD_ESTANCIA_POR_CAPACITACION = "El empleado tendrá una Capacitación";
    /**
     * Path para docroot para deployments con ruta raíz en domain1/
     */
    public static final String PATH_SERVIDOR_DOCROOT = "docroot/";
    /**
     * Path para docroot para deployments con ruta raíz en domain1/config/
     */
    public static final String PATH_SERVIDOR_DOCROOT_SUBDIR = "../docroot/";
    /**
     * Path para validar deployments con ruta raíz en domain1/config/
     */
    public static final String PATH_END_SUBDIR_CONFIG = "config";
    /**
     * Identificadores de la tabla SI_PARAMETRO
     */
    public static final int PARAMETRO_LOCAL_FILES_ID = 1;
    /*
     * ======================== Constantes para el módulo de Control de Oficios
     * - INICIO ================================
     */
    /**
     * La siguiente constante coincide con el ID en la tabla SI_MODULO.
     */
    public static final int OFICIOS_MODULO_ID = 10;
    /**
     * Las siguientes constantes coinciden con los codigos en la tabla SI_ROL.
     */
    public static final String OFICIOS_ROL_EMISOR_OFICIOS_SALIDA_CODIGO = "10-EOS";
    public static final String OFICIOS_ROL_EMISOR_OFICIOS_ENTRADA_CODIGO = "10-EOE";
    public static final String OFICIOS_ROL_RECEPTOR_REYNOSA_CODIGO = "10-RREY";
    public static final String OFICIOS_ROL_RECEPTOR_MONTERREY_CODIGO = "10-RMTY";
    public static final String OFICIOS_ROL_CONSULTA_OFICIOS = "10-COF";
    public static final String OFICIOS_ROL_EDITOR_MAESTRO_CODIGO = "10-EMTRO";
    public static final String OFICIOS_ROL_EDITOR_OFICIOS_SALIDA_CODIGO = "10-EDOFS";
    public static final String OFICIOS_ROL_EDITOR_ADJUNTO_SALIDA_CODIGO = "10-EDADS";
    public static final String OFICIOS_ROL_RECEPTOR_NOTIFICACION_URGENTE = "10-RNU";
    public static final String OFICIOS_ROL_RECEPTOR_NOTIFICACION_URGENTE_GENERAL = "10-RUG";

    /**
     * Las siguientes constantes coinciden con los valores en la tabla
     * SI_PERMISO.
     */
    // permisos básicos
    public static final String OFICIOS_PERMISO_INGRESAR_MODULO_OFICIO = "INGRESAR:MODULO:OFICIO";
    public static final String OFICIOS_PERMISO_CONSULTAR_OFICIO = "CONSULTAR:OFICIO";
    public static final String OFICIOS_PERMISO_VER_DETALLE_OFICIO = "VER:DETALLE:OFICIO";
    public static final String OFICIOS_PERMISO_VER_HISTORIAL_OFICIO = "VER:HISTORIAL:OFICIO";
    // edición
    public static final String OFICIOS_PERMISO_ALTA_OFICIO = "ALTA:OFICIO";
    public static final String OFICIOS_PERMISO_MODIFICAR_OFICIO = "MODIFICAR:OFICIO";
    public static final String OFICIOS_PERMISO_MODIFICAR_OFICIO_SALIDA = "MODIFICAR:OFICIO:SALIDA";
    public static final String OFICIOS_PERMISO_MODIFICAR_ADJUNTO_SALIDA = "MODIFICAR:ADJUNTO:SALIDA";
    public static final String OFICIOS_PERMISO_ANULAR_OFICIO = "ANULAR:OFICIO";
    public static final String OFICIOS_PERMISO_PROMOVER_ESTATUS_OFICIO = "PROMOVER:ESTATUS:OFICIO";
    public static final String OFICIOS_PERMISO_MODIFICAR_ARCHIVOADJUNTO_HISTORIAL_OFICIO = "MODIFICAR:ARCHIVOADJUNTO:HISTORIAL:OFICIO";
    // opcionales
    public static final String OFICIOS_PERMISO_VER_TODO_GERENCIAS = "VER:TODO:GERENCIAS";
    public static final String OFICIOS_PERMISO_VER_ARCHIVOADJUNTO_HISTORIAL_OFICIO = "VER:ARCHIVOADJUNTO:HISTORIAL:OFICIO";
    public static final String OFICIOS_PERMISO_DESCARGAR_ARCHIVOADJUNTO_HISTORIAL_OFICIO = "DESCARGAR:ARCHIVOADJUNTO:HISTORIAL:OFICIO";
    public static final String OFICIOS_PERMISO_RECIBIR_CORREO_ALTA_OFICIO = "RECIBIR:CORREO:ALTA:OFICIO";
    public static final String OFICIOS_PERMISO_GESTIONAR_SEGUIMIENTO_OFICIO = "GESTIONAR:SEGUIMIENTO:OFICIO";
    //permisos otro copiar en otro bloque   
    public static final String OFICIO_PERMISO_COPIAR_EN_OTRO_BLOQUE = "COPY:OFICIO";
    /**
     * Estatus de oficios de entrada y salida. Coinciden con los ID de la tabla
     * ESTATUS.
     *
     */
    public static final int OFICIOS_ESTATUS_ID_OFICIO_CREADO = 800;
    public static final int OFICIOS_ESTATUS_ID_ENVIADO_REYNOSA = 805;
    public static final int OFICIOS_ESTATUS_ID_ENVIADO_MONTERREY = 810;
    public static final int OFICIOS_ESTATUS_ID_RECIBIDO_PEMEX = 815;
    public static final int OFICIOS_ESTATUS_ID_OFICIO_TERMINADO = 820;
    public static final int OFICIOS_ESTATUS_ID_OFICIO_ANULADO = 825;
    /**
     * Operaciones correspondientes a los estatus de los oficios para el
     * registro de los movimientos de promoción. Coinciden con los ID de la
     * tabla SI_OPERACION.
     *
     */
    public static final int OFICIOS_OPERACION_ID_OFICIO_CREADO = 11;
    public static final int OFICIOS_OPERACION_ID_ENVIADO_REYNOSA = 12;
    public static final int OFICIOS_OPERACION_ID_ENVIADO_MONTERREY = 13;
    public static final int OFICIOS_OPERACION_ID_RECIBIDO_PEMEX = 14;
    public static final int OFICIOS_OPERACION_ID_OFICIO_TERMINADO = 15;
    public static final int OFICIOS_OPERACION_ID_OFICIO_ANULADO = 16;
    /**
     * Operaciones correspondientes a la activacion/desactivacion de seguimiento
     * de oficio para el registro de movimiento. Coinciden con los ID de la
     * tabla SI_OPERACION.
     *
     */
    public static final int OFICIOS_OPERACION_ID_SEGUIMIENTO_OFICIO_ON = 17;
    public static final int OFICIOS_OPERACION_ID_SEGUIMIENTO_OFICIO_OFF = 18;
    /**
     * Para registrar movimiento de actualizacion de archivo adjunto de
     * movimiento.
     */
    public static final int OFICIOS_OPERACION_ID_ACTUALIZACION_ARCHIVO_ADJUNTO_MOVIMIENTO = 19;
    /**
     * Constantes para consulta
     */
    public static final byte OFICIOS_CONSULTA_SEGUIMIENTO_SI = 1;
    public static final byte OFICIOS_CONSULTA_SEGUIMIENTO_NO = -1;
    public static final byte OFICIOS_CONSULTA_SEGUIMIENTO_TODOS = 0;
    /**
     * Constantes para el guardado de archivos
     *
     */
    public static final String OFICIOS_PATH_RELATIVO_OFICIOS = "controloficios/oficios";
    /**
     * Ruta para el guardado temporal de archivos PDF para el mostrado en la
     * utilería ViewerJS.
     *
     */
    public static final String OFICIOS_PATH_ARCHIVOS_TEMP = "oficiostemp/";
    /**
     * Prefijo para los archivos temporales para ser borrados al final del ciclo
     * de vida del bean de oficios.
     *
     */
    public static final String OFICIOS_PREFIJO_ARCHIVOS_TEMPORALES = "temp_";
    /**
     * Este valor está delimitado por la longitud máxima en base de datos.
     *
     */
    public static final int OFICIOS_ARCHIVO_ADJUNTO_NOMBRE_LONGITUD_MAXIMA = 200;
    /**
     * Prefijo para identificar los archivos adjuntos de un oficio.
     */
    public static final String OFICIOS_PREFIJO_ARCHIVO_OFICIO = "oficio_";
    /**
     * Valores para los correos.
     *
     */
    public static final String OFICIOS_CORREO_ASUNTO_IHSA_PEMEX = "Correspondencia IHSA PEMEX - ";
    public static final String OFICIOS_CORREO_ASUNTO_IHSA_CQ_PEMEX = "Correspondencia IHSA_CQ - ";
    /**
     * Valores para los correos. jevazquez 16/04/15 aprobado
     *
     */
    public static final String OFICIOS_CORREO_ASUNTO_IHSA_PEMEX_NO_PROMOVIDOS = "Correspondencia pendiente de promoveer IHSA PEMEX - ";
    /**
     *
     */
    public static final String OFICIOS_CORREO_ASUNTO_PROMOCION = "Promoción de Oficio - ";
    /**
     * //jevazquez 18/02/15
     */
    public static final String OFICIOS_CORREO_MODIFICA_OFICIO = "Modificación de Oficio - ";
    /**
     *
     */
    public static final String OFICIOS_VISTA_BANDEJA_ENTRADA = "bandejaEntrada.xhtml?faces-redirect=true;";
    public static final String OFICIOS_VISTA_ANULAR = "anular.xhtml?faces-redirect=true;";
    /**
     *
     */
    public static final String OFICIOS_VISTA_CONSULTAR = "consultar.xhtml?faces-redirect=true;";
    /**
     *
     */
    public static final String OFICIOS_VISTA_EDITAR = "editar";
    /**
     *
     */
    public static final String OFICIOS_VISTA_DETALLE = "detalle.xhtml?faces-redirect=true;";
    /**
     *
     */
    public static final String OFICIOS_VISTA_PROMOCION_ESTATUS = "promocionEstatus.xhtml?faces-redirect=true;";
    /**
     *
     */
    public static final String OFICIOS_VISTA_SEGUIMIENTO = "seguimiento.xhtml?faces-redirect=true;";
    /**
     * De Pemex a IHSA.
     */
    public static final int OFICIOS_TIPO_OFICIO_ENTRADA_ID = 1;
    public static final String OFICIOS_TIPO_OFICIO_ENTRADA_NOMBRE = "Entrada";
    /**
     * De IHSA a Pemex.
     */
    public static final int OFICIOS_TIPO_OFICIO_SALIDA_ID = 2;
    public static final String OFICIOS_TIPO_OFICIO_SALIDA_NOMBRE = "Salida";
    /**
     * Para indicar hacia (a) cuáles oficios un oficio está asociado.
     */
    public static final int OFICIOS_ASOCIACION_HACIA = 1;
    /**
     * Para indicar desde (por) cuáles oficios un oficio está asociado.
     */
    public static final int OFICIOS_ASOCIACION_DESDE = 2;
    /**
     * Cantidad máxima de registros que puede regresar una consulta, para
     * prevenir sobrecarga de información.
     *
     */
    public static final int OFICIOS_MAXIMO_RETORNO_CONSULTA_INICIAL = 100;
    /*
     * ======================== Constantes para Ordenamiento - INICIO
     * ================================
     */
    /**
     * Se usa para ordenar de forma ASCENDENTE los querys
     */
    public static final String ORDER_BY_ASC = "ASC";
    /**
     * Se usa para ordenar de forma DESCENDENTE los querys
     */
    public static final String ORDER_BY_DESC = "DESC";
    public static final String ORDENAR_POR_ID = "ID";
    /*
     * ======================== Constantes para Formatos de Fecha - INICIO
     * ================================
     */
    /**
     *
     */
    public static final String FECHA_DEFAULT_TARJETA_CREDITO = "00/00";
    /**
     * Formato para fecha - dd/MM/yyyy Ejemplo: 19/08/2012
     */
    public static final SimpleDateFormat FMT_ddMMyyy = new SimpleDateFormat("dd/MM/yyyy");

    public static final SimpleDateFormat FMT_ddMMyyyh_mm_a = new SimpleDateFormat("dd/MM/yyyy h:mm a");
    /**
     * Formato para fecha - yyyy/MM/dd Ejemplo: 2012/08/19
     */
    public static final SimpleDateFormat FMT_yyyyMMdd = new SimpleDateFormat("yyyy/MM/dd");
    /**
     * Formato para fecha - yyyy-MM-dd Ejemplo: 2012-08-19
     */
    public static final SimpleDateFormat FMT_yyyy_MM_dd = new SimpleDateFormat("yyyy-MM-dd");
    /**
     * Formato para hora - HH:mm:ss Ejemplo: 12:05:23
     */
    public static final SimpleDateFormat FMT_HHmmss = new SimpleDateFormat("HH:mm:ss");
    public static final SimpleDateFormat FMT_yyyyMMddHHmmss = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss ");
    /**
     * *
     * Formato que se usa para los archivos de log
     */
    public static final SimpleDateFormat FMT_ddMMyyyyHHmmss = new SimpleDateFormat("dd.MM.yyyy hh-mm-ss");
    /**
     * Formato para hora - h:mm Ejemplo: 12:05 (Sin Segundos)
     */
    public static final SimpleDateFormat FMT_hmm_a = new SimpleDateFormat("HH:mm ");
    /**
     * Formato texto para hora - dd 'de' MMMMM 'de' yyyy Ejemplo:
     */
    public static final SimpleDateFormat FMT_TextDate = new SimpleDateFormat("dd 'de' MMMMM 'de' yyyy", new Locale("es", "ES"));
    /**
     * Formato texto para hora - EEEE 'de' MMMMM 'de' yyyy
     */
    public static final SimpleDateFormat FMT_TextDateLarge = new SimpleDateFormat("EEEE dd 'de' MMMMM 'de' yyyy", new Locale("es", "ES"));
    /*
     * ======================== Constantes para Formatos de Moneda - INICIO
     * ================================
     */
    /**
     * Formato general para Moneda ó Montos Ejemplo: 5968.90 <--Solo 2 decimales
     */
    public static final DecimalFormat formatoMoneda = new DecimalFormat("###,###,###.##");
    /*
     * ======================== Constantes para Formatos de Moneda - FIN
     * ================================
     */
 /*
     * Mensaje motivo automatico cuando se cancela una estancia autimaticamente,
     * por ahora se pone esto ya que no hay una opcion al cancelar por un un
     * usuario.
     */
 /*
     * ======================== Constantes Mensajes automaticos- INICIO
     * ================================
     */
    public static final String mensajeCancelacionAutomaticaUsuarioEstancia = "Se cancela el usuario por acuerdo de servicios generales...";
    public static final String mensajeCancelacionAutomaticaSolicitudEstancia = "Se cancela la solicitud por acuerdo de servicios generales...";
    public static final String mensajeCancelacionAutomaticaSolicitudViajesinMotivoRetraso = " debido a que no tiene una justificación de salida...";
    //Noticias en viajes
    //public static final String tituloAutomaticoNoticiaViajeHTML = "<p>Se ha creado el viaje <strong>  </strong> </p> ";
    /*
     * ======================== - FIN ================================
     */
 /*
     * ======================== Constantes Consecutivos- INICIO
     * ================================
     */
    public static final String CONSECUTIVO_VIAJE = "VIAJE_CONSECUTIVO";
    public static final String CONSECUTIVO_VIAJE_PRUEBA = "PRUEBA_VIAJE_CONSECUTIVO";
    /*
     * ======================== - FIN ================================
     */
 /*
     * ======================== Constantes Solicitudes de Viaje- INICIO
     * ================================
     */
    /**
     * Valor : "Solicitud de viaje:" Ocupada para enviar en los asuntos de
     * correos de solicitar
     *///Solicitud de Viaje :
    public static final String MENSAJE_ASUNTO_CORREO_SOLICITAR_VIAJE = "Has realizado la solicitud de viaje : ";
    public static final String MENSAJE_ASUNTO_CORREO_SOLICITAR_VIAJES = "Se Realizaron las solicitudes de viaje : ";
    /*
     * ======================== - FIN ================================
     */
 /*
     * ======================== Constantes Companias- INICIO
     * ================================
     */
    public static final String RFC_IHSA = "IHI070320FI3";
    public static final String RFC_OILSERV = "OIL140522MA0";
    public static final String RFC_PETROINT = "PET140522TK8";
    public static final String RFC_MPG = "MPG050310434";
    public static final String RFC_OIL_GAS = "GOG160621JA6";
    public static final String RFC_IHSA_CQ = "901066223-8";
    public static final String RFC_IHSA_MX_CQ = "IHC171108AX9";

    /*
     * ======================== - FIN ================================
     *
     */
    // copiados en correo
    public static final String RESPONSABLE_TI = "cbuzon@ihsa.mx";
    /*
     * ======================== Constantes para el manejo de id de eventos de
     * log - INICIO ================================
     */
    public static final int ID_SI_EVENTO_CREAR = 1;
    public static final int ID_SI_EVENTO_MODIFICAR = 2;
    public static final int ID_SI_EVENTO_ELIMINAR = 3;
    public static final String USUARIO_PRUEBA = "PRUEBA";
    /*
     * Representa el ID del usuario SIA en la base de datos Ocupado: En envio de
     * correo de solicitudes de viajes
     */
    public static final String USUARIO_SIA = "SIA";
    public static final String USUARIO_MLUIS = "MLUIS";
    public static final String LINK_SIA = "<A HREF='" + Configurador.urlSia() + "Sia' TARGET='_new'>SIA</A>";
    public static final String SV_TERRESTRE = "Terrestre";
    /*
     * ======================== Constantes para comparar Terrestre,Ciduad,Aereo
     * - INICIO ================================
     */
    /**
     * Valor : 0 Representa el parametro para recoger todas las solicitudes de
     * viajes, por oficina ciudad aereas lugar
     *
     */
    public static final int TODAS_SOLICITUDES_VIAJE = 0;
    public static final int SOLICITUDES_TERRESTRE_OFICINA = 1;
    public static final int SOLICITUDES_TERRESTRE_CIUDAD = 2;
    public static final int SOLICITUDES_TERRESTRES = 5;
    /**
     * Valor : 5 Representa el numero del index del tab de las solicitudes de
     * viaje a lugares cercanos.
     *
     */
    public static final int SOLICITUDES_TERRESTRE_LUGAR = 3;
    /**
     * Valor : 3 Representa el numero del index del tab de las solicitudes tipo
     * áereas.
     */
    public static final int SOLICITUDES_AEREA = 3;
    public static final int SOLICITUDES_TIPO_AEREA = 30;
    public static final int SOLICITUDES_TERRESTRE = 2;
    public static final int TAB_SOLICITUDES_POR_JUSTIFICAR = 4;
    public static final int TAB_SOLICITUDES_A_LUGAR = 3;
    /**
     * Valor : 2 Es el id del tipo especifico (Terrestre)
     */
    public static final int TIPO_ESPECIFICO_SOLICITUD_TERRESTRE = 2;
    /**
     * Valor : 3 Es el id del tipo especifico (Áerea)
     */
    public static final int TIPO_ESPECIFICO_SOLICITUD_AEREA = 3;
    public static final int SG_TIPO_ESPECIFICO_SOLICITUD_VIAJE_AEREA = 3;
    public static final int SG_TIPO_ESPECIFICO_SOLICITUD_VIAJE_TERRESTRE = 2;
    public static final String redondo = "redondo";
    /**
     * Tipos de Solicitudes de Viaje
     */
    public static final int SG_TIPO_SOLICITUD_VIAJE_TERRESTRE_GERENTE = 1;
    public static final int SG_TIPO_SOLICITUD_VIAJE_TERRESTRE_EMPLEADO = 2;
    public static final int SG_TIPO_SOLICITUD_VIAJE_VACACIONES = 3;
    public static final int SG_TIPO_SOLICITUD_VIAJE_CAMBIO_DE_GUARDIA = 4;
    public static final int SG_TIPO_SOLICITUD_VIAJE_EMPRESA = 5;
    public static final int SG_TIPO_SOLICITUD_VIAJE_CAPACITACION = 6;
    public static final int SG_TIPO_SOLICITUD_VIAJE_MOTIVOS_LABORALES = 7;
    public static final int RUTA_TIPO_OFICINA = 21;
    public static final int RUTA_TIPO_CIUDAD = 22;
    public static final int RUTA_TIPO_LUGAR = 23;

    public static final int RUTA_MTY_SF = 1;
    public static final int RUTA_SF_MTY = 7;
    public static final int RUTA_MTY_REY = 6;
    public static final int RUTA_REY_MTY = 3;
    public static final int RUTA_SF_REY = 5;
    public static final int RUTA_REY_SF = 4;

    /*
     * ======================== Constantes para la carta de hospedaje - INICIO
     * ================================
     */
    public static final int CARTA_DATOS_BANCARIOS = 1;
    public static final int CARTA_SIN_DATOS_BANCARIOS = 0;
    public static final String solicitudTerrestre = "TERRESTRE";
    public static final String solicitudAereo = "AEREO";
    public static final String modificar = "MODIFICAR";
    public static final String insertar = "INSERTAR";
    public static final String sencillo = "sencillo";
    public static final String OPCION_OTRO = "otro";
    public static final boolean TRUE = true;
    public static final boolean FALSE = false;
    /**
     * Constantes ocupadas en las opciones de consulta para traer itinerarios *
     */
    public static final boolean CARGAR_ESCALAS = true;
    public static final boolean NO_CARGAR_ESCALAS = false;
    /**
     * constantes para definir si una cadena es aprobada y historial *
     */
    public static final boolean ESTATUS_NO_APROBADO = false;
    public static final boolean ESTATUS_APROBADO = true;
    public static final boolean ESTATUS_NO_HISTORIAL = false;
    public static final boolean ESTATUS_HISTORIAL = true;
    public static final String VIAJERO_EMPLEADO = "E";
    public static final String VIAJERO_INVITADO = "I";
    public static final int SG_TIPO_ESPECIFICO_EMPLEADO = 19;
    public static final int SG_TIPO_ESPECIFICO_INVITADO = 20;
    public static final String RANGO_A_F = "AF";
    public static final int VALOR_ASCII_F = 70;
    public static final String RANGO_G_M = "GM";
    public static final int VALOR_ASCII_M = 77;
    public static final String RANGO_N_R = "NR";
    public static final int VALOR_ASCII_R = 82;
    public static final String RANGO_S_Z = "SZ";
    public static final int VALOR_ASCII_Z = 90;
    /**
     * Gerencias OC/S
     */
    public static final int GERENCIA_ID_CAPACITACION = 55;
    public static final int GERENCIA_ID_COMPRAS = 23;
    public static final int GERENCIA_ID_ADMINISTRACION = 32;
    public static final int GERENCIA_ID_DIRECCION_TECNICA = 62;
    public static final int GERENCIA_ID_DIRECCION_FINANZAS = 43;
    public static final int GERENCIA_ID_DIRECCION_GENERAL = 11;
    public static final int GERENCIA_ID_SERVICIOS_GENERALES = 33;
    public static final double PORCENTAJA_10 = 1.10;
    //
    public static final int GERENCIA_ID_RR_HH = 51;
    public static final int GERENCIA_ID_SGL = 33;
    /**
     * f:params - popupAgregarViajeros.xhtml
     */
    public static final String param_ID_USUARIO = "idUsuario";
    public static final String param_ID_INVITADO = "idInvitado";
    public static final String param_ADD_OR_REMOVE = "addorremove";
    public static final String param_ADD = "add";
    public static final String param_REMOVE = "remove";
    public static final String TAB_SOLICITA_OFICINA = "0";
    public static final String TAB_SOLICITA_CIUDAD = "1";
    public static final int ID_OFICINA_TORRE_MARTEL = 1;
    public static final int ID_OFICINA_REY_PRINCIPAL = 2;
    public static final int ID_OFICINA_REY_INFRA = 4;
    public static final int ID_OFICINA_MONCLOVA = 15;
    public static final int ID_OFICINA_SAN_FERNANDO = 3;

    public static final int ID_TIPO_GENERAL_DIRECCION_GENERAL = 20;
    public static final int ID_TIPO_CAMBIO_ESTADO_SEMAFORO = 29;
    /**
     * Valor : Estancia prolongada
     */
    public static final String ASUNTO_NOTIFICACION_ESTANCIA_PROLONGADA = "Estancia prolongada";
    public static final int DIAS_PROLONGACION_ESTANCIA = 1;
    public static final int AP_CAMPO_DEFAULT = 1;
    public static final int AP_CAMPO_PIRINEOS = 3;
    public static final int AP_CAMPO_NEJO = 1;
    public static final int AP_CAMPO_SAN_ANDRES = 5;
    public static final int AP_CAMPO_TIERRA_BLANCA = 4;
    //Motivos SiMovimiento
    public static final String MOTIVO_PROLONGACION_ESTANCIA = "Se prolonga la estancia porque el semáforo ha sido puesto en color negro y el huésped no debe salir hasta que cambie este";
    //Operaciones
    public static final int ID_SI_OPERACION_ASIGNAR = 1;
    public static final int ID_SI_OPERACION_RECIBIR = 2;
    public static final int ID_SI_OPERACION_CANCELAR = 3;
    public static final int ID_SI_OPERACION_DEVOLVER = 4;
    public static final int ID_SI_RECHAZAR_REPSE = 98;
    public static final int ID_SI_OPERACION_OFICINA_ORIGEN = 5;
    public static final int ID_SI_OPERACION_OFICINA_DESTINO = 6;
    public static final int ID_SI_OPERACION_PROLONGAR_ESTANCIA = 9;
    public static final int ID_SI_OPERACION_ASIGNAR_VEHICULO = 10;
    public static final int ID_GERENCIA_IHSA = 11;
    public static final int ID_SI_OPERACION_ESPERA = 94;
    public static final int ID_SI_OPERACION_ESPERAMSG = 95;
    public static final int ID_SI_OPERACION_ESPERAADJ = 96;
    public static final int ID_SI_OPERACION_ACTIVAR_REQ = 97;

    /*
     * Valor : "Empleado" Se ocupa para los cuerpos de correos de viajeros de
     * solicitudes de viaje
     */
    public static final String CONCEPTO_EMPLEADO = "Empleado";
    /*
     * Valor : "Invitado" Se ocupa para los cuerpos de correos de viajeros de
     * solicitudes de viaje
     */
    public static final String CONCEPTO_INVITADO = "Invitado";
    public static final String PREFIJO_SOLICITUD_ESTANCIA_PRUEBA = "PSE";
    public static final String PREFIJO_SOLICITUD_ESTANCIA_REAL = "SE";
    public static final String FOLIO_SOLICITUD_ESTANCIA_PRUEBA = "PRUEBA_SOLICITUD_ESTANCIA_CONSECUTIVO";
    public static final String FOLIO_SOLICITUD_ESTANCIA_REAL = "SOLICITUD_ESTANCIA_CONSECUTIVO";
    public static final int EMPLEADO = 19;
    public static final int INVITADO = 20;
    public static final int TIPO_GENERAL_DETALLE_SOLICITUD = 6;
    //
    public static final String VEHICULO_EMPRESA = "s";
    public static final int QUEDADO_OFICINA_DESTINO = 6;
    public static final int VIAJERO_ESCALA = 9;
    public static final int ESTATUS_VIAJE_POR_SALIR = 501;
    public static final int ESTATUS_VIAJE_CREADO = 505;
    public static final int ESTATUS_VIAJE_PROCESO = 510;
    public static final int ESTATUS_VIAJE_EN_DESTINO = 518;
    public static final int ESTATUS_VIAJE_FINALIZAR = 520;
    public static final int ESTATUS_VIAJE_CANCELADO = 500;
    /**
     * Motivo de Cancelacion letra E
     */
    public static final String MOTIVO_CANCELACION_EMPLEADO = "E";
    /**
     * SECCION PARA AUTORIZAR VIAJES
     */
    public static final int NINGUNA_OFICINA = 0;
    public static final int VIAJE_ESPERA_AUTORIZACION = 515;
    /**
     * * **********************
     */
    public static final int OPCION_VIAJE_AEREO = 7;
    public static final String CANCELA_DIRECCION_GENERAL = "Cancela dirección general";
    public static final int NOTIFICA_DIRECCION_GENERAL = 7;
    public static final int NOTIFICA_GESTION_RIESGOS = 5;
    public static final int DIAS_PROLONGADOS_ESTANCIA = 1;
    public static final boolean VIENE_SERVICIOS_GENERALES = true;
    public static final int ID_COLOR_SEMAFORO_VERDE = 1;
    public static final int ID_COLOR_SEMAFORO_AMARILLO = 2;
    public static final int ID_COLOR_SEMAFORO_ROJO = 3;
    public static final int ID_COLOR_SEMAFORO_NEGRO = 4;
    /*
     * @MLUIS 13/11/2013
     */
    public static final String LETRA_S = "S";
    public static final String LETRA_N = "N";
    public static final String OPCION_PROPIA = "propia";
    /*
     * @MLUIS 26/11/2013
     */
    public static final int SEMAFORO_NEGRO_TIPO_ESPECIFICO = 26;
    public static final int SEMAFORO_HORARIO_TIPO_ESPECIFICO = 27;
    public static final int DIA_ANTERIOR_TIPO_ESPECIFICO = 28;
    public static final int FIN_SEMANA_TIPO_ESPECIFICO = 29;
    public static final int DIA_FESTIVO = 30;
    public static final int PROXIMO_LUNES = 31;
    /**
     * Valor : 19 representa el tipo especifico para usuario
     */
    public static final int ID_TIPO_ESPECIFICO_USUARIO = 19;
    /**
     * Valor : 19 representa el tipo especifico para usuario
     */
    public static final int ID_TIPO_ESPECIFICO_INVITADO = 20;
    /**
     * Dias de la semana
     */
    public static final int NUMERO_DIA_DOMINGO = 1;
    public static final int NUMERO_DIA_LUNES = 2;
    public static final int NUMERO_DIA_VIERNES = 6;
    public static final int NUMERO_DIA_SABADO = 7;
    //
    public static final int PRIMERA_VEZ_VIAJE = 1;
    public static final int QUEDADO_ORIGEN = 5;
    /**
     * Estatus que representa el id del motivo de una solicitud de viaje Aereo
     * por politica una solicitud de viaje aereo no tiene un Motivo. Valor : 270
     */
    public static final int ID_MOTIVO_VIAJE_AEREO_DEFAULT = 270;
    public static final int ID_TIPO_ESPECIFICO_REINICIO_KILOMETRAJE = 7;
    /*
     * ======================== Constantes ORDENES
     * ================================
     */
    public static final int ORDENES_CANCELADAS = 100;
    public static final int ORDENES_SIN_SOLICITAR = 101;
    public static final int ORDENES_SIN_APROBAR = 110;
    public static final int ORDENES_SIN_AUTORIZAR_MPG = 120;
    public static final int ORDENES_SIN_AUTORIZAR_IHSA = 130;  /// para campo autoriza direccion finanzas MPG
    public static final int ORDENES_SIN_AUTORIZAR_COMPRAS = 140;
    public static final int ORDENES_SIN_AUTORIZAR_LICITACION = 151;
    /*
     * ======================== - FIN ================================
     */

 /*
     * ======================== Constantes REQUISICIONES
     * ================================
     */
    public static final int REQUISICION_ASIGNADA = 40;
    public static final int REQUISICION_PENDIENTE = 1;
    public static final int REQUISICION_SOLICITADA = 10;
    public static final int REQUISICION_REVISADA = 15;
    public static final int REQUISICION_APROBADA = 20;
    public static final int REQUISICION_AUTORIZADA = 30;
    public static final int REQUISICION_VISTO_BUENO = 35;
    public static final int REQUISICION_FINALIZADA = 60;
    public static final int REQUISICION_VISTO_BUENO_C = 25;
    public static final int REQUISICION_CANCELADA = 50;
    public static final int REQUISICION_EN_ESPERA = 45;
    /*
     * ======================== - FIN ================================
     */
 /*
     * ======================== Constantes MONTO MAXIMO DE COMPRA
     * ================================
     */
    public static final int MONTO_MAXIMO_REVISA_USD = 5000;
    public static final int MONTO_MAXIMO_AUTORIZA_USD = 20000;
    /*
     * ======================== - FIN ================================
     */

 /*
     * ======================== Constantes
     * ESTATUS================================
     */
    public static final int ESTATUS_CANCELADA = 100;
    public static final int ESTATUS_PENDIENTE_R = 101;
    public static final int ESTATUS_SOLICITADA_R = 110;
    public static final int ESTATUS_VISTO_BUENO_R = 120;
    public static final int ESTATUS_REVISA_SOCIO = 125;
    public static final int ESTATUS_REVISADA = 130;
    public static final int ESTATUS_POR_APROBAR_SOCIO = 135;
    public static final int ESTATUS_APROBADA = 140;
    public static final int ESTATUS_AUTORIZADA = 150;
    public static final int ESTATUS_ENVIADA_PROVEEDOR = 160;
    public static final int ESTATUS_ORDEN_RECIBIDA_PARCIAL = 163;
    public static final int ESTATUS_ORDEN_RECIBIDA = 165;
    public static final int ESTATUS_ORDEN_RECEPCION_FACTURA = 170;
    public static final int ESTATUS_ASIGNADA = 40;
    public static final int ESTATUS_ACEPTAR_GERENTE_REQ = 33;
    public static final int ESTATUS_VISTO_BUENO_REQ = 35;
    /*
     * ======================== - FIN ======4==========================
     */
 /*
     * ======================== - INICIO ================================
     */
    public static final int HORA_MAXIMA_APROBACION = 17;
    /*
     * ======================== - FIN ================================
     */
    public static final boolean EXCLUIR_PERIODO_PRUEBA = true;
    //
    public static final int ID_TIPO_USUARIO_RIVASA = 26;
    //
    public static final int DIAS_ANTICIPADOS = 5;

    public static final int DIAS_REPORTE_COMPRADORES = 15;
    //
    public static final String PALABRA_SI = "Si";
    public static final String FILTRO = "filtro";
    //
    public static final String[] MESES = {"Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};
    public static final String DATASOURCE_CONTEXT = "jdbc/__siaPool";
    ////////////////
    public static final String ABREVIATURA_GERENCIA_COMPLEMENTO = ";SEC";
    public static final String ESTATUS_COMPROBANTE_ORDEN = "ODC";
    public static final String ESTATUS_COMPROBANTE_REQ = "REQ";
    public static final String ESTATUS_COMPROBANTE_LIN = "LIN";
    public static final String OCFLUJO_ACTION_APROBAR = "AP";
    public static final String ESTATUS_COMPROBANTE_CONV = "CON";
//    public static final String OCFLUJO_ACTION_APROBAR_SOCIO = "AS";
    public static final String OCFLUJO_ACTION_REVISAR = "RE";
    //
    public static final String ENVIADA_PROVEEDOR = "ENV";
    public static final String OCS_PROCESO = "PROCESO";
    public static final String ROL_INTEGRA_SIA_NAV = "OcSiaNav";
    public static final String CODIGO_ROL_SOCIO = "40";
    public static final String TITULO_SOL_VIAJE_NOTIFICACION = "Aprobar solicitud de viaje";
    public static final String TITULO_SOL_ESTANCIA_NOTIFICACION = "Aprobar estancia";

//    public enum TipoRequision {
//	AF, PS, AI
//    }
    public static final String PALABRA_TODO = "TODO";
    public static final String UPDATE_OPERATION = "Actualizar";
    public static final String CREATE_OPERATION = "Crear";
    //
    public static final String ROL_DESARROLLO_SISTEMA = "47";
    public static final String ROL_ADMIN_TI = "ADMTI";
    public static final String OPERACION_ERROR = "Error";
    public static final int TIPO_PAGO_STAFF = 3;
    public static final int TIPO_PAGO_OFICINA = 2;
    public static final int TIPO_PAGO_VEHICULO = 1;
    public static final int TIPO_VEHICULO_MANTENIMIENTO = 14;
    //
    public static final String CODIGO_ROL_CONS_REQ = "C_REQ";
    public static final String CODIGO_ROL_CONS_OCS = "C_ORD";
    public static final String CODIGO_ROL_VER_CURSO_MANEJO = "CUR_MAN";

    /*
     * ======================== Constantes INVENTARIOS ================================
     */
    public static final Integer INV_TRANSACCION_STATUS_PREPARACION = 1;
    public static final Integer INV_TRANSACCION_STATUS_APLICADA = 2;
    public static final Integer INV_TRANSACCION_STATUS_RECHAZADA = 3;
    public static final Integer INV_TRANSACCION_STATUS_TRASPASO_PENDIENTE_REVISION = 4;

    public static final Integer INV_MOVIMIENTO_TIPO_ENTRADA = 1;
    public static final Integer INV_MOVIMIENTO_TIPO_SALIDA = 2;
    public static final Integer INV_MOVIMIENTO_TIPO_TRASPASO_ENTRANTE = 3;
    public static final Integer INV_MOVIMIENTO_TIPO_TRASPASO_SALIENTE = 4;
    public static final Integer INV_MOVIMIENTO_TIPO_MERMA = 5;
    public static final Integer INV_MOVIMIENTO_TIPO_PERDIDA = 6;

    public static final int MODULO_INVENTARIOS = 11;

    /*
     * ======================== - FIN ================================
     */

 /*
     * ======================== - FIN ================================
     */
    //
    public static final String TIMER_REPORTE_ORDENES_SIN_AUTORIZAR = "Ordenes de compra sin autorizar";

    public static final String RUTA_INCIDENCIA_VEHICULO = "SGyL/Incidencia/Vehiculo/";

    public static final int SOLICITUD_ESTANCIA_PENDIENTE = 1;
    public static final int TIPO_ESTANCIA = 6;

    public static final String RUTA_LOCAL_FILES = "/local/files/";
    public static final String RUTA_INCIDENCIA_FACTURA = "SGyL/Incidencia/Factura/";
    public static final int MODULO_GR = 34;
    public static final String SIA = "SIA";
    public static final String CONVENIO = "Convenio";
    public static final String OCS_SIN_CONTRATO = "OCS_SIN_CONTRATO";
    public static final int OCS_CATEGORIA_TABLA = 5;
    public static final int OCS_CATEGORIA_OCSPDF = 4;
    public static final String CODIGO_ROL_CONS_REPORTE_COMPRA = "48";
    public static final String OK = "OK";
    public static final int OCS_CATEGORIA_REPSE = 6;

    public static final int EMPRESA_IHSA = 2;
    //MODULOS
    public static final int MODULO_ADMIN_SIA = 24;
    public static final int MODULO_RH_ADMIN = 23;
    public static final int MODULO_CONTROL_OFICIO = 10;

    public static final int GR_TIPO_ARCHIVO_Mapas = 1;
    public static final int GR_TIPO_ARCHIVO_Recomendaciones = 2;
    public static final int GR_TIPO_ARCHIVO_Sitios = 3;
    public static final int GR_TIPO_ARCHIVO_Situacion = 4;
    public static final int GR_TIPO_ARCHIVO_Mensaje = 5;

    //
    public static final int TOTAL_FILAS_RECUPERADAS = 10;

    public static final int ID_SI_OPERACION_AGREGAR_VIAJERO = 20;
    public static final int ID_SI_OPERACION_VIAJERO_NO_VIAJO = 21;
    public static final int ID_SI_OPERACION_INTERCAMBIO_VIAJERO = 22;
    public static final int ID_SI_OPERACION_FINALIZAR_VIAJES_FT = 26;

    public static final int ORIGEN = 1;
    public static final int PUNTO_SEGURIDAD = 2;
    public static final int DESTINO = 3;
    public static final int ID_SI_OP_LLEGO_VIAJE = 23;
    public static final int ID_SI_OPERACION_CAMBIO_VIAJERO_VIAJE = 24;
    //////////////////////////////////////////////////////////////////////////////////////////
    public static final int ULTIMOS_CONVENIOS = 10;
    //////////////////////////////////////convenios
    public static final String CONVENIOS_POR_VENCER = "CONVENIOS_POR_VENCER";
    public static final String CONVENIOS_VENCIDOS = "CONVENIOS_VENCIDOS";
    public static final int ESTADO_CONVENIO_REGISTRADO = 301;
    public static final int ESTADO_CONVENIO_A_FIRMAS = 305;
    public static final int ESTADO_CONVENIO_ACTIVO = 310;
    public static final int ESTADO_CONVENIO_VENCIDO = 320;
    public static final int ESTADO_CONVENIO_CANCELADO = 300;
    public static final int ESTADO_CONVENIO_EXHORTO = 350;
    public static final int ESTADO_CONVENIO_PROCESO_FINIQUITO = 360;
    public static final int ESTADO_CONVENIO_PROCESO_FINIQUITO_FINALIZADO = 370;
    public static final int ESTADO_CONVENIO_FINIQUITO = 380;

    public static final int CONTACTO_REP_LEGAL = 1;
    public static final int CONTACTO_REP_TECNICO = 2;
    public static final int CONTACTO_REP_COMPRAS = 3;

    public static final int DOCUMENTO_TIPO_PROVEEDOR = 6;
    public static final int DOCUMENTO_TIPO_CONTRATO = 7;
    public static final int DOCUMENTO_TIPO_EVALUACION = 45;
    public static final String COD_CONVENIO = "18";
    public static final String COD_EDITAR_CONVENIO_FORMALIZADO = "CONTREF";
    public static final String CODIGO_ROL_ELIMINAR_PARTIDAS = "ELIMIPAR";
    public static final String COD_ROL_COMPRADOR = "22";
    public static final String COD_ROL_EVALUADOR = "CONTEVA";
    public static final String COD_ROL_CONS_ADMIN_CONV = "CVDIR";
    public static final int GERENCIA_JURIDICO = 65;
    public static final int ID_SI_OPERACION_RETOMAR_VIAJE = 25;
    public static final String CODIGO_ROL_NOTI_NOT = "NOTI_NOT";
    public static final String CODIGO_ESTATUS_VEHICULO = "VHE";
    public static final int ESTADO_VEHICULO_ACTIVO = 610;
    //
    public static final int PAIS_MEXICO = 1;

    public static final String USD_SIGLAS = "USD";

    //
    public static final String AUTH_KEY_FCM = "AAAAeA4fNto:APA91bF4-vC1w60wal_PlEokk_CNLR3BX1X7nby6SNtT4GYe5hMKIpMWlaw5acAoeeA9fBjfB5CY3V9HAAVygUWdc67aMIcvPg65TXytDhyMUVX4KrtYS0cC7O7TWtp9FutIABwvkjFW";
    public static final String ESTANCIA_TOKEN = "estancia";
    public static final String VIAJE_TOKEN = "viaje";
    public static final String ORDEN_TOKEN = "orden";

    //constante para lista de solicitudes de viaje
    public static final String LISTASV = "LISTA SOLICITUDES DE VIAJE";
    public static final String CODIGO_ROL_VALPRO = "VALPRO";
    public static final int ID_60_DIAS = 10;

    public static final int MESES_PREVIOS = 2;
    public static final String COD_ROL_CON_NAC = "RECONA";
    public static final String FAC_CLAVE_NO_VALIDA = "01010101";

    public static final int ESTATUS_FACTURA_PROC_VALIDACION = 730;
    public static final int TIPO_CLASIFICACION_CONVENIO_SERVICIO = 1;
    public static final int TIPO_CLASIFICACION_CONVENIO_SUMINISTRO = 2;
    public static final int TIPO_CLASIFICACION_CONVENIO_OBRA = 3;

    public static final String FOLIO_VALE_SALIDA = "FOLIO_SALIDA";
    public static final String ROL_SOL_MAT = "SOL_MAT";
    public static final String ROL_AUTORIZA_MAT = "AUT_MAT";
    public static final String ROL_ENTREGA_MAT = "ENT_MAT";
    public static final String ROL_SUP_ENTREGA_MAT = "SUP_MAT";

    public static final String COD_ROL_SOPORTE_TECNICO = "SOP_TEC";
    public static final String COD_ROL_NOTI_SOPORTE_TECNICO = "TIC_NIV";
    public static final String PRIMER_NIVEL = "1N";
    public static final String SEGUNDO_NIVEL = "2N";
    public static final String TIPO_ARCHIVO_REQ_ESPERA = "ESPERA";

    //used on status code http
    public static final int RESPONSE_OK = 200;
    public static final int SERVER_ERROR = 501;
    public static final int SERVER_UNAVAILABLE = 503;
    public static final int UNAUTHORIZED = 401;

    public static final int LISTA_TIPO_IDENTIFICACION = 2;
    public static final int LISTA_TIPO_DOCUMENTO = 3;
    public static final int LISTA_TIPO_EVALUACION = 7;
    public static final int LISTA_TIPO_PORTAL = 8;

    public static final int DOCUMENTO_TIPO_SERV_ESP = 51;
    public static final int DOCUMENTO_TIPO_ACT_PREP = 50;
    public static final int DOCUMENTO_TIPO_EST_SOC_VIG = 49;

    //
    public static final String FORMA_SOL_FIN = "F01";
    public static final String FORMA_ACT_ENTREGA = "F02";
    public static final String FORMA_EDO_CUENTA = "F03";
    public static final String FORMA_CONC_CONT = "F04";
    public static final String FORMA_VAL_CUMP = "F05";
    
    //
    public static final String COD_ROL_ORDEN_REPSE = "REPSE";
    public static final int ID_OPERACION_REC_CARTA_INTENCION = 99;
    
    public static final boolean AUTORIZADO = true;
    public static final boolean NO_SELECCION = false;
    public static final String COD_ROL_REV_FACT = "HISFAC";

    /**
     * Formato para fecha - yyyy-MM-dd Ejemplo: 2012-08-19
     */
    public static final SimpleDateFormat FMT_dd_MM_yyyy = new SimpleDateFormat("dd-MM-yyyy");    
}
