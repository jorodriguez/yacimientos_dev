/*
 * ProveedorFacade.java
 * Creada el 24/08/2009, 02:00:20 PM
 * Clase Java desarrollada por: Héctor Acosta Sierra para: MPG-IHSA
 *
 * Para información sobre el uso de esta clase, asi como bugs, actualizaciones o mejoras
 * enviar un mail a: hacosta@mpg-ihsa.com.mx o a: new_nick_name@hotmail.com
 */
package sia.servicios.proveedor.impl;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipOutputStream;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import sia.archivador.AlmacenDocumentos;
import sia.archivador.DocumentoAnexo;
import sia.archivador.ProveedorAlmacenDocumentos;
import sia.constantes.Constantes;
import sia.excepciones.SIAException;
import sia.modelo.ContactoProveedor;
import sia.modelo.Estatus;
import sia.modelo.OcTerminoPago;
import sia.modelo.Proveedor;
import sia.modelo.PvLogNotifica;
import sia.modelo.PvTipoPersona;
import sia.modelo.SiAdjunto;
import sia.modelo.Usuario;
import sia.modelo.campo.usuario.puesto.vo.CompaniaBloqueGerenciaVo;
import sia.modelo.proveedor.Vo.ContactoProveedorVO;
import sia.modelo.proveedor.Vo.CuentaBancoVO;
import sia.modelo.proveedor.Vo.ProveedorDocumentoVO;
import sia.modelo.proveedor.Vo.ProveedorVo;
import sia.modelo.sistema.AbstractFacade;
import sia.modelo.usuario.vo.UsuarioVO;
import sia.notificaciones.proveedor.impl.NotificacionProveedorImpl;
import sia.servicios.campo.nuevo.impl.ApCampoImpl;
import sia.servicios.campo.nuevo.impl.ApCampoUsuarioRhPuestoImpl;
import sia.servicios.catalogos.impl.ProveedorVetadoImpl;
import sia.servicios.catalogos.impl.UsuarioImpl;
import sia.servicios.sistema.impl.SiAdjuntoImpl;
import sia.servicios.sistema.impl.SiFacturaAdjuntoImpl;
import sia.servicios.sistema.impl.SiParametroImpl;
import sia.servicios.sistema.vo.ProveedorVetadoVO;
import sia.util.LecturaLibro;
import sia.util.ProveedorEnum;
import sia.util.UtilLog4j;

/**
 *
 * @author Mluis
 * @version 1.0
 */
@Stateless
public class ProveedorServicioImpl extends AbstractFacade<Proveedor> {

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ProveedorServicioImpl() {
        super(Proveedor.class);
    }
    @Inject
    private NotificacionProveedorImpl notificacionServicioRemoto;
    @Inject
    private PvLogNotificaImpl pvNotificaServicioRemoto;
    @Inject
    private SiAdjuntoImpl siAdjuntoServicioRemoto;
    @Inject
    private PvTipoPersonaImpl pvTipoPersonaServicioRemoto;
    @Inject
    private UsuarioImpl usuarioRemote;
    @Inject
    private ContactoProveedorImpl contactoProveedorRemote;
    @Inject
    private CuentaBancoProveedorImpl cuentaBancoProveedorRemote;
    @Inject
    private PvClasificacionArchivoImpl pvClasificacionArchivoRemote;
    @Inject
    private PvProveedorCompaniaImpl pvProveedorCompaniaRemote;
    @Inject
    private PvRegistroFiscalImpl pvRegistroFiscalRemote;
    @Inject
    private PvProveedorMovimientoImpl pvProveedorMovimientoRemote;
    @Inject
    private ProveedorVetadoImpl proveedorVetadoRemote;
    @Inject
    ApCampoUsuarioRhPuestoImpl apCampoUsuarioRhPuestoRemote;
    @Inject
    ApCampoImpl apCampoRemote;
    @Inject
    ProveedorAlmacenDocumentos proveedorAlmacenDocumentosRemote;
    @Inject
    private SiParametroImpl parametrosSistemaServicioRemoto;

    SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MMM-yyyy");
    SimpleDateFormat DATE_FORMAT_ANIO = new SimpleDateFormat("yyyy");

    public Proveedor getPorNombre(Object nombreProveedor, String rfcCompania) {
        try {
            return (Proveedor) em.createQuery("SELECT p.proveedor FROM PvProveedorCompania p WHERE p.proveedor.nombre = :nombre"
                    + " and p.compania.rfc = :rfcCom and p.eliminado = :elim").setParameter("rfcCom", rfcCompania).setParameter("elim", false).setParameter("nombre", nombreProveedor).getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public Proveedor traerPorRFC(String rfc, String rfcCompania) {
        try {
            if (!rfcCompania.isEmpty()) {
                return (Proveedor) em.createQuery("SELECT p.proveedor FROM PvProveedorCompania p WHERE p.proveedor.rfc = :rfc and p.compania.rfc = :rfcCom").setParameter("rfc", rfc).setParameter("rfcCom", rfcCompania).getSingleResult();
            } else {
                return (Proveedor) em.createQuery("SELECT p FROM Proveedor p WHERE p.rfc = :rfc ").setParameter("rfc", rfc).getSingleResult();
            }

        } catch (Exception e) {
            return null;
        }

    }

    public String encriptar(String text) throws NoSuchAlgorithmException {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] b = md.digest(text.getBytes());
            int size = b.length;
            StringBuilder h = new StringBuilder(size);

            for (int i = 0; i < size; i++) {
                int u = b[i] & 255;
                if (u < 16) {
                    h.append(Integer.toHexString(u));
                } else {
                    h.append(Integer.toHexString(u));
                }
            }
            //clave encriptada
            return h.toString();
        } catch (Exception e) {
            return text;
        }
    }

    public Proveedor traerProveedorVisible(String rfc) {
        try {
            return (Proveedor) em.createQuery("SELECT p FROM Proveedor p WHERE p.rfc = :rfc AND p.visible = :true").setParameter("rfc", rfc).setParameter("true", true).getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public boolean activarProveedor(String rfc, String correo, String pass, String rfcCompania) {
        boolean v = false;
        v = this.notificacionServicioRemoto.notificacionActivarProveedor(rfc, correo, pass);
        if (v) {
            try {
                Proveedor proveedor = this.traerPorRFC(rfc, rfcCompania);
                proveedor.setClave(this.encriptar(pass));
                proveedor.setVisible(true);
                proveedor.setCorreo(correo);
                this.edit(proveedor);
            } catch (NoSuchAlgorithmException ex) {
                Logger.getLogger(ProveedorServicioImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return v;
    }

    public boolean eliminarProveedor(int idProv) {
        boolean v = false;
        Proveedor proveedor = this.find(idProv);
        if (proveedor != null) {
            proveedor.setClave(null);
            proveedor.setVisible(false);
            this.edit(proveedor);
            v = true;
        }
        return v;
    }

    public boolean cambiarPass(int idP, String pass) {
        boolean v = false;
        try {
            Proveedor proveedor = find(idP);
            if (proveedor != null) {
                proveedor.setSesion(Constantes.BOOLEAN_TRUE);
                proveedor.setModifico(new Usuario(proveedor.getRfc()));
                proveedor.setFechaModifico(new Date());
                proveedor.setHoraModifico(new Date());
                proveedor.setClave(this.encriptar(pass));
                edit(proveedor);
                v = true;
            }
            return v;
        } catch (NoSuchAlgorithmException e) {
            UtilLog4j.log.error(e);
            return v;
        }

    }

    public boolean notificaCambioPassword(int idP) {
        boolean v = false;
        try {
            Proveedor proveedor = find(idP);
            if (proveedor != null) {
                ProveedorVo proveedorVo = new ProveedorVo();
                proveedorVo.setRfc(proveedor.getRfc());
                proveedorVo.setNombre(proveedor.getNombre());
                //
                String pass = String.valueOf(generaClave());
                notificacionServicioRemoto.notificaCambioContraseña(correosProveedor(idP), pass, proveedorVo);
                //
                proveedor.setSesion(Constantes.BOOLEAN_TRUE);
                proveedor.setModifico(new Usuario(proveedor.getRfc()));
                proveedor.setFechaModifico(new Date());
                proveedor.setHoraModifico(new Date());
                proveedor.setClave(this.encriptar(pass));
                edit(proveedor);
                v = true;
            }
            return v;
        } catch (NoSuchAlgorithmException e) {
            UtilLog4j.log.error(e);
            return v;
        }

    }

    private int generaClave() {
        int valorEntero = (int) Math.floor(Math.random() * (1000 - 10000 + 1) + 10000);
        return valorEntero;
    }

    public List<Proveedor> traerProveedorActivo(String rfcCompania, int status) {
        return em.createQuery("SELECT p.proveedor FROM PvProveedorCompania p WHERE p.proveedor.visible = :true and p.compania.rfc = :rfcCom and p.eliminado = :elim "
                + " and p.proveedor.estatus.id = :estatus ORDER BY p.proveedor.nombre ASC")
                .setParameter("rfcCom", rfcCompania)
                .setParameter("true", true)
                .setParameter("estatus", status)
                .setParameter("elim", false)
                .setMaxResults(30).getResultList();
    }

    public List<String> traerNombreProveedorQueryNativo(String rfcCompania, int status) {
        return em.createNativeQuery("SELECT p.nombre FROM pv_proveedor_compania pc"
                + "     inner join proveedor p on pc.proveedor = p.id "
                + " WHERE p.visible = 'True'"
                + " and pc.compania = '" + rfcCompania + "' "
                + " and p.status = " + status
                + " and pc.eliminado = false ORDER BY p.nombre ASC").getResultList();
    }

    public List<String> traerNombreLikeProveedorQueryNativo(String cadena, String rfcCompania, int status) {
        return em.createNativeQuery("SELECT p.nombre FROM pv_proveedor_compania pc"
                + "     inner join proveedor p on pc.proveedor = p.id"
                + " WHERE p.visible = 'True'"
                + " and p.status = " + status
                + " AND  upper(p.nombre) LIKE '" + cadena.toUpperCase() + "%'"
                + " and pc.compania = '" + rfcCompania + "' "
                + " and pc.eliminado = false ORDER BY p.nombre ASC").getResultList();
    }

    public boolean desActivarProveedor(String rfc, String rfcCompania) {
        boolean v = false;
        Proveedor proveedor = this.traerPorRFC(rfc, rfcCompania);
        proveedor.setVisible(false);
        proveedor.setClave(null);
        this.edit(proveedor);
        v = true;
        return v;
    }

    public boolean notificarTodos(String user, SiAdjunto proCircular, int puesto) {
        boolean v = false;

        List<ContactoProveedor> listaContactos = this.contactoProveedorRemote.traerPorPuesto(puesto);
        if (listaContactos.size() > 0) {
            for (ContactoProveedor contactoProveedor : listaContactos) {
                try {
                    v = notificacionServicioRemoto.notificarTodosProveedores(contactoProveedor.getCorreo(), contactoProveedor.getProveedor().getRfc(), contactoProveedor.getProveedor().getClave(), proCircular.getId());

                    if (v) {
                        PvLogNotifica pvNotifica = new PvLogNotifica();
                        pvNotifica.setContactoProveedor(contactoProveedor);
                        pvNotifica.setFechaGenero(new Date());
                        pvNotifica.setHoraGenero(new Date());
                        pvNotifica.setEntregada(true);
                        pvNotifica.setSiAdjunto(proCircular);
                        this.pvNotificaServicioRemoto.create(pvNotifica);
                    } else {
                        PvLogNotifica pvNotifica = new PvLogNotifica();
                        pvNotifica.setContactoProveedor(contactoProveedor);
                        pvNotifica.setFechaGenero(new Date());
                        pvNotifica.setHoraGenero(new Date());
                        pvNotifica.setSiAdjunto(proCircular);
                        pvNotifica.setEntregada(false);
                        this.pvNotificaServicioRemoto.create(pvNotifica);
                    }
                } catch (Exception e) {
                    //Guarda las no enviadas
                    PvLogNotifica pvNotifica = new PvLogNotifica();
                    pvNotifica.setContactoProveedor(contactoProveedor);
                    pvNotifica.setSiAdjunto(proCircular);
                    pvNotifica.setFechaGenero(new Date());
                    pvNotifica.setHoraGenero(new Date());
                    pvNotifica.setEntregada(false);
                    this.pvNotificaServicioRemoto.create(pvNotifica);
                }
            }
        }
        if (v) {
            proCircular.setEliminado(true);
            this.siAdjuntoServicioRemoto.edit(proCircular);
        }
        return v;
    }

    public void agregarTipoPersona(Proveedor proveedor, int persona) {
        proveedor.setPvTipoPersona(this.pvTipoPersonaServicioRemoto.find(persona));
        this.edit(proveedor);
    }

    /**
     * @param status
     * @author: icristobal Recupera un String con la lista de proveedores en
     * formato Gson
     * @param rfcCompania
     */
    public String getProveedorJson(String rfcCompania, int status) {
        Gson gson = null;
        try {
            gson = new Gson();
            List<Object[]> lista = null;
            String sb = "select p.id, p.nombre, calle, p.colonia, p.ciudad, p.estado, p.pais, p.telefono, p.fax, pc.referencia, p.rfc from PROVEEDOR p"
                    + "         inner join  Pv_Proveedor_Compania pc  on pc.proveedor = p.id "
                    + "     where pc.compania = '" + rfcCompania + "'"
                    + "     and p.status = " + status + "and p.eliminado =false "
                    + "     and pc.eliminado = false order by p.nombre asc";
            lista = em.createNativeQuery(sb).getResultList();
            JsonArray a = new JsonArray();

            for (Object[] o : lista) {
                if (o != null) {
                    String nombre = o[1] != null ? (String) o[1] : "-";
                    String rfc = o[10] != null ? (String) o[10] : "-";
                    JsonObject ob = new JsonObject();
                    ob.addProperty("value", o[0] != null ? o[0].toString() : "-");
                    ob.addProperty("label", rfc + ", " + nombre);
                    ob.addProperty("calle", o[2] != null ? (String) o[2] : "-");
                    ob.addProperty("colonia", o[3] != null ? (String) o[3] : "-");
                    ob.addProperty("ciudad", o[4] != null ? (String) o[4] : "-");
                    ob.addProperty("estado", o[5] != null ? (String) o[5] : "-");
                    ob.addProperty("pais", o[6] != null ? (String) o[6] : "-");
                    ob.addProperty("telefono", o[7] != null ? (String) o[7] : "-");
                    ob.addProperty("fax", o[8] != null ? (String) o[8] : "-");
                    ob.addProperty("referencia", o[9] != null ? (String) o[9] : "-");
                    ob.addProperty("rfc", o[10] != null ? (String) o[10] : "-");
                    ob.addProperty("nombre", nombre);
                    a.add(ob);
                }
            }
            return gson.toJson(a);
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Ocurrio un error al traer el proveedor por compania. " + e.getMessage());
            //
            return null;
        }
    }

    /**
     * @author: icristobal Recupera un String con la lista de proveedores en
     * formato Gson
     * @param idProveedor
     * @param rfcCompania
     * @return
     */
    public String traerDatosProveedor(int idProveedor, String rfcCompania) {
        Gson gson = null;
        try {
            gson = new Gson();
            StringBuilder sb = new StringBuilder();
            sb.append("select p.id, p.nombre, calle, p.colonia, p.ciudad, p.estado, p.pais, p.telefono, p.fax, ");
            sb.append(" (select pc.referencia from Pv_Proveedor_Compania pc where pc.proveedor = p.id and pc.eliminado = false");
            sb.append(" and pc.compania = '").append(rfcCompania).append("')");
            sb.append(", p.rfc from PROVEEDOR p");
            sb.append(" where p.id = ").append(idProveedor).append(" and p.eliminado =false");

            Object[] o = (Object[]) em.createNativeQuery(sb.toString()).getSingleResult();
            JsonObject ob = new JsonObject();

            if (o != null) {
                String nombre = o[1] != null ? (String) o[1] : "-";
                ob.addProperty("value", o[0] != null ? o[0].toString() : "-");
                ob.addProperty("label", o[1] != null ? (String) o[1] : "-");
                ob.addProperty("calle", o[2] != null ? (String) o[2] : "-");
                ob.addProperty("colonia", o[3] != null ? (String) o[3] : "-");
                ob.addProperty("ciudad", o[4] != null ? (String) o[4] : "-");
                ob.addProperty("estado", o[5] != null ? (String) o[5] : "-");
                ob.addProperty("pais", o[6] != null ? (String) o[6] : "-");
                ob.addProperty("telefono", o[7] != null ? (String) o[7] : "-");
                ob.addProperty("fax", o[8] != null ? (String) o[8] : "-");
                ob.addProperty("referencia", o[9] != null ? (String) o[9] : "-");
                ob.addProperty("rfc", o[10] != null ? (String) o[10] : "-");
                ob.addProperty("nombre", nombre);

            }
            return gson.toJson(ob);

        } catch (Exception e) {
            UtilLog4j.log.error(e);
            return null;
        }
    }

    /**
     * MLUIS Regresa lista de proveedores formato Gson
     *
     * @param status
     */
    public String traerProveedorPorCompaniaSesionJson(String listaRfcCompania, int status) {
        Gson gson = null;
        try {
            gson = new Gson();
            List<Object[]> lista = null;
            StringBuilder sb = new StringBuilder();
            sb.append("select distinct(p.NOMBRE), p.id, p.calle, p.colonia, p.ciudad, p.estado, p.pais, p.telefono, p.fax, p.rfc");
            sb.append(" from PROVEEDOR p ");
            sb.append(" inner join PV_PROVEEDOR_COMPANIA pc  on  pc.proveedor = p.id");
            sb.append(" where pc.compania in (").append(listaRfcCompania).append(")");
            if (status > 0) {
                sb.append(" and p.status = ").append(status);
            }
            sb.append(" and p.NOMBRE is not null");
            sb.append(" and p.ELIMINADO = '").append(Constantes.NO_ELIMINADO).append("'");
            sb.append(" order by p.NOMBRE asc");
            //
            lista = em.createNativeQuery(sb.toString()).getResultList();
            JsonArray a = new JsonArray();
            if (lista != null) {
                for (Object[] o : lista) {
                    a.add(castProveedorCompania(o));
                }
            }
            return gson.toJson(a);
        } catch (Exception e) {
            UtilLog4j.log.error(e);
            return null;
        }
    }

    private String consulta() {
        StringBuilder sb = new StringBuilder();
        sb.append(" select p.id, p.nombre, p.calle, p.colonia, p.ciudad, p.estado, p.pais, p.telefono, p.fax, p.correo, p.rfc, p.numero, p.codigo_postal, ");
        //                   0       1         2          3         4         5        6        7          8     9        10       11        12
        sb.append(" p.NUMERO_INTERIOR, p.GIRO,p.CURP,p.IMSSPATRONAL,p.IDCIF,p.NACIONAL, p.PV_TIPO_PERSONA,");
        //               13                 14   15         16       17         18          19
        sb.append(" reg.ID,reg.NOACTA,reg.NOMBRENOT,reg.NONOTARIA,reg.NOBOLETA,reg.SEDE,reg.EMISION,reg.INSCRIPCION,");
        //            20        21            22         23        24            25          26            27           
        sb.append(" per.nombre, es.id, es.nombre, p.oc_termino_pago, p.carta_contenido_nacional, p.sesion, p.repse");
        //           28          29        30           31                    32                    33  
        sb.append(" from PROVEEDOR p ");
        sb.append(" LEFT join PV_REGISTROFISCAL reg on reg.PROVEEDOR = p.ID and reg.ELIMINADO = false ");
        sb.append(" left join PV_TIPO_PERSONA per on per.ID = p.PV_TIPO_PERSONA ");
        sb.append(" inner join estatus es on p.status = es.id ");
        return sb.toString();
    }

    public ProveedorVo traerProveedor(int idProveedor, String compania) {

        StringBuilder sb = new StringBuilder();
        sb.append(consulta());
        sb.append(" where p.id  = ").append(idProveedor);
        Object[] objects = (Object[]) em.createNativeQuery(sb.toString()).getSingleResult();
        return castProveedor(objects, compania);
    }

    private ProveedorVo castProveedor(Object[] objects, String compania) {
        ProveedorVo proveedorVo = new ProveedorVo();
        proveedorVo.setIdProveedor((Integer) objects[0]);

        proveedorVo.setNombre((String) objects[1]);
        proveedorVo.setGiro((String) objects[14]);
        proveedorVo.setNacional((Boolean) objects[18]);
        proveedorVo.setPersona((Integer) objects[19] != null ? (Integer) objects[19] : 0);
        proveedorVo.setPersonaTxt((String) objects[28]);

        proveedorVo.setCalle((String) objects[2]);
        proveedorVo.setColonia((String) objects[3]);
        proveedorVo.setCiudad((String) objects[4]);
        proveedorVo.setEstado((String) objects[5]);
        proveedorVo.setPais((String) objects[6]);
        proveedorVo.setNumero((String) objects[11]);
        proveedorVo.setCodigoPostal((String) objects[12]);
        proveedorVo.setNumeroInt((String) objects[13]);

        proveedorVo.setRfc((String) objects[10]);
        proveedorVo.setImssPatronal((String) objects[16]);
        proveedorVo.setCurp((String) objects[15]);
        proveedorVo.setIdCIF((String) objects[17]);
        proveedorVo.setNoNotaria((String) objects[23]);
        proveedorVo.setNoBoleta((String) objects[24]);
        proveedorVo.setSede((String) objects[25]);
        proveedorVo.setPvRegistroFiscal((Integer) objects[20] != null ? (Integer) objects[20] : 0);
        proveedorVo.setNoActa((String) objects[21]);
        proveedorVo.setNombreNot((String) objects[22]);
        proveedorVo.setEmision(objects[26] != null ? (Date) objects[26] : null);
        proveedorVo.setInscripcion(objects[27] != null ? (Date) objects[27] : null);
        proveedorVo.setStatus(objects[29] != null ? (Integer) objects[29] : Constantes.CERO);
        proveedorVo.setNombreStatus((String) objects[30]);
        proveedorVo.setIdPago(objects[31] != null ? (Integer) objects[31] : Constantes.CERO);
        proveedorVo.setCarta((Boolean) objects[32]);
        proveedorVo.setPrimerSesion((Boolean) objects[33]);
        proveedorVo.setRepse((Boolean) objects[34]);

        if (proveedorVo.getIdProveedor() > 0) {
            proveedorVo.setLstRL(contactoProveedorRemote.traerContactoPorProveedor(proveedorVo.getIdProveedor(), Constantes.CONTACTO_REP_LEGAL));
            proveedorVo.setLstRT(contactoProveedorRemote.traerContactoPorProveedor(proveedorVo.getIdProveedor(), Constantes.CONTACTO_REP_TECNICO));
            proveedorVo.setContactos(contactoProveedorRemote.traerContactoPorProveedor(proveedorVo.getIdProveedor(), Constantes.CONTACTO_REP_COMPRAS));
            //proveedorVo.getContactos().addAll(contactoProveedorRemote.traerContactoPorProveedor(proveedorVo.getIdProveedor(), 0));
            proveedorVo.setTodoContactos(contactoProveedorRemote.traerTodosContactoPorProveedor(proveedorVo.getIdProveedor()));
            if (!compania.isEmpty()) {
                proveedorVo.setCuentas(cuentaBancoProveedorRemote.traerCuentas(proveedorVo.getIdProveedor(), compania));
            }
            proveedorVo.setLstDocsProveedor(pvClasificacionArchivoRemote.traerArchivoPorProveedorOid(proveedorVo.getIdProveedor(), 0));
        }

        return proveedorVo;
    }

    public void modificarDatos(ProveedorVo proveedorVo, String id) {
        try {
            Proveedor proveedor = find(proveedorVo.getIdProveedor());
            //
            proveedor.setRfc(proveedorVo.getRfc());
            proveedor.setNombre(proveedorVo.getNombre());
            proveedor.setPais(proveedorVo.getPais());
            proveedor.setEstado(proveedorVo.getEstado());
            proveedor.setCiudad(proveedorVo.getCiudad());
            proveedor.setColonia(proveedorVo.getColonia());
            proveedor.setCalle(proveedorVo.getCalle());
            proveedor.setNumero(proveedorVo.getNumero());
            proveedor.setCodigoPostal(proveedorVo.getCodigoPostal());
            proveedor.setCartaContenidoNacional(proveedorVo.isCarta());
            proveedor.setModifico(new Usuario(id));
            proveedor.setFechaModifico(new Date());
            proveedor.setHoraModifico(new Date());
            edit(proveedor);
        } catch (Exception ex) {
            //out.println("asdadasd: mod provee " + ex);
            Logger.getLogger(ProveedorServicioImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String traerJsonProveedorPorCompania(String rfcCompania, int status) {
        Gson gson = null;
        try {
            gson = new Gson();
            List<Object[]> lista = null;
            StringBuilder sb = new StringBuilder();
            sb.append("select distinct(p.NOMBRE), p.id, p.calle, p.colonia, p.ciudad, p.estado, p.pais, p.telefono, p.fax, p.rfc");
            sb.append(" from PROVEEDOR p ");
            sb.append(" inner join PV_PROVEEDOR_COMPANIA pc  on  pc.proveedor = p.id");
            sb.append(" where pc.compania = '").append(rfcCompania).append("'");
            sb.append(" and p.status = ").append(status);
            sb.append(" and p.NOMBRE is not null");
            sb.append(" and p.ELIMINADO = '").append(Constantes.NO_ELIMINADO).append("'");
            sb.append(" order by p.NOMBRE asc");
            lista = em.createNativeQuery(sb.toString()).getResultList();
            JsonArray a = new JsonArray();

            if (lista != null) {
                for (Object[] o : lista) {
                    a.add(castProveedorCompania(o));
                }
            }
            return gson.toJson(a);
        } catch (Exception e) {
            UtilLog4j.log.error(e);
            return null;
        }
    }

    private JsonObject castProveedorCompania(Object[] o) {
        JsonObject ob = new JsonObject();
        ob.addProperty("label", o[0] != null ? o[0].toString() : "-");
        ob.addProperty("value", o[1] != null ? (String) o[1].toString() : "-");
        ob.addProperty("calle", o[2] != null ? (String) o[2] : "-");
        ob.addProperty("colonia", o[3] != null ? (String) o[3] : "-");
        ob.addProperty("ciudad", o[4] != null ? (String) o[4] : "-");
        ob.addProperty("estado", o[5] != null ? (String) o[5] : "-");
        ob.addProperty("pais", o[6] != null ? (String) o[6] : "-");
        ob.addProperty("telefono", o[7] != null ? (String) o[7] : "-");
        ob.addProperty("fax", o[8] != null ? (String) o[8] : "-");
        ob.addProperty("rfc", o[9] != null ? (String) o[9] : "-");
        return ob;

    }

    public Proveedor traerProveedorPorRfc(String rfc) {
        try {
            return (Proveedor) em.createQuery("SELECT p FROM Proveedor p WHERE p.rfc = ?1").setParameter(1, rfc).getSingleResult();
        } catch (Exception e) {
            UtilLog4j.log.info(e);
            return null;
        }
    }

    public boolean validaProveedorSAT(String rfc, String idCif) {
        try {
            return contenidoHTML(rfc, idCif);
        } catch (IOException ex) {
            Logger.getLogger(ProveedorServicioImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public boolean contenidoHTML(String rfc, String idCif) throws IOException {
        URL url = new URL("https://siat.sat.gob.mx/app/qr/faces/pages/mobile/validadorqr.jsf?D1=10&D2=1&D3=" + idCif + "_" + rfc);
        URLConnection uc = url.openConnection();
        uc.connect();
        boolean encontrado = false;
        //Creamos el objeto con el que vamos a leer
        BufferedReader in = new BufferedReader(new InputStreamReader(uc.getInputStream()));
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            //contenido += inputLine + "\n";
            if (inputLine.contains("tiene asociada la siguiente información")) {
                encontrado = true;
                break;
            }
        }
        in.close();
        return encontrado;
    }

    public int cargarDatosProveedor(String sesion, File file, String rfcEmpresa) {
        try {
            LecturaLibro lecturaLibro = new LecturaLibro();
            XSSFWorkbook archivo = lecturaLibro.loadFileXLSX(file);
            XSSFSheet workSheet = lecturaLibro.loadSheet(archivo);
            int proveedor = 0;
            try {
                ProveedorVo proveedorVo = readSheetData(workSheet);
                proveedor = cargarDatos(proveedorVo, sesion, rfcEmpresa);
            } catch (Exception e) {
                UtilLog4j.log.error(e);
            }
            return proveedor;
        } catch (Exception ex) {
            UtilLog4j.log.error(ex);
        }
        return Constantes.CERO;
    }

    private ProveedorVo readSheetData(XSSFSheet workSheet) throws Exception {
        UtilLog4j.log.info("Leyendo datos ...");
        LecturaLibro lecturaLibro = new LecturaLibro();
        ProveedorVo proveedorVo = new ProveedorVo();
        try {
            proveedorVo.setPersonaTxt(lecturaLibro.getValFromReference(workSheet, "C3"));
            proveedorVo.setNombre(lecturaLibro.getValFromReference(workSheet, "B4"));
            proveedorVo.setNacional(lecturaLibro.getValFromReference(workSheet, "J4").contains("Nacional"));
            proveedorVo.setGiro(lecturaLibro.getValFromReference(workSheet, "B5"));
            proveedorVo.setCalle(lecturaLibro.getValFromReference(workSheet, "B7"));
            proveedorVo.setNumero(lecturaLibro.getValFromReference(workSheet, "K7"));
            proveedorVo.setNumeroInt(lecturaLibro.getValFromReference(workSheet, "M7"));
            proveedorVo.setColonia(lecturaLibro.getValFromReference(workSheet, "B9"));
            proveedorVo.setEstado(lecturaLibro.getValFromReference(workSheet, "H9"));
            proveedorVo.setCodigoPostal(lecturaLibro.getValFromReference(workSheet, "B10"));
            proveedorVo.setCiudad(lecturaLibro.getValFromReference(workSheet, "H10"));
            proveedorVo.setRfc(lecturaLibro.getValFromReference(workSheet, "B12"));
            proveedorVo.setImssPatronal(lecturaLibro.getValFromReference(workSheet, "G12"));
            proveedorVo.setCurp(lecturaLibro.getValFromReference(workSheet, "K12"));
            proveedorVo.setIdCIF(lecturaLibro.getValFromReference(workSheet, "B13"));
            // Datos del registro fiscal
            proveedorVo.setNoNotaria(lecturaLibro.getValFromReference(workSheet, "F13"));
            proveedorVo.setNoBoleta(lecturaLibro.getValFromReference(workSheet, "J13"));
            proveedorVo.setNoActa(lecturaLibro.getValFromReference(workSheet, "B14"));
            proveedorVo.setSede(lecturaLibro.getValFromReference(workSheet, "J14"));
            proveedorVo.setNombreNot(lecturaLibro.getValFromReference(workSheet, "B15"));
            String fEmision = lecturaLibro.getValFromReference(workSheet, "H15");
            if (!fEmision.isEmpty()) {
                proveedorVo.setEmision(DATE_FORMAT.parse(fEmision));
            }
            String fInsc = lecturaLibro.getValFromReference(workSheet, "L15");
            if (!fInsc.isEmpty()) {
                proveedorVo.setInscripcion(DATE_FORMAT.parse(fInsc));
            }
            // Datos bancarios
            CuentaBancoVO cuentaBancoVO = new CuentaBancoVO();
            cuentaBancoVO.setBanco(lecturaLibro.getValFromReference(workSheet, "B17"));
            cuentaBancoVO.setCuenta(lecturaLibro.getValFromReference(workSheet, "H17"));
            cuentaBancoVO.setMoneda(lecturaLibro.getValFromReference(workSheet, "B19"));
            cuentaBancoVO.setClabe(lecturaLibro.getValFromReference(workSheet, "H19"));
            // banco extrangero
            CuentaBancoVO cuentaBancoExtVO = new CuentaBancoVO();
            cuentaBancoExtVO.setBanco(lecturaLibro.getValFromReference(workSheet, "B21"));
            cuentaBancoExtVO.setCuenta(lecturaLibro.getValFromReference(workSheet, "H21"));
            cuentaBancoExtVO.setSwift(lecturaLibro.getValFromReference(workSheet, "B23"));
            cuentaBancoExtVO.setAba(lecturaLibro.getValFromReference(workSheet, "H23"));
            cuentaBancoVO.setClabe(Constantes.VACIO);
            List<CuentaBancoVO> listaCuenta = new ArrayList<>();
            proveedorVo.setCuentas(new ArrayList<CuentaBancoVO>());
            listaCuenta.add(cuentaBancoVO);
            listaCuenta.add(cuentaBancoExtVO);
            proveedorVo.setCuentas(listaCuenta);
            // Representante legal
            ContactoProveedorVO representanteLegal = new ContactoProveedorVO();
            representanteLegal.setNombre(lecturaLibro.getValFromReference(workSheet, "B26"));
            representanteLegal.setPuesto(lecturaLibro.getValFromReference(workSheet, "I26"));
            representanteLegal.setTelefono(lecturaLibro.getValFromReference(workSheet, "B27"));
            representanteLegal.setCelular(lecturaLibro.getValFromReference(workSheet, "I27"));
            representanteLegal.setCorreo(lecturaLibro.getValFromReference(workSheet, "B28"));
            representanteLegal.setRfc(lecturaLibro.getValFromReference(workSheet, "G28"));
            representanteLegal.setCurp(lecturaLibro.getValFromReference(workSheet, "K28"));
            representanteLegal.setPoder(lecturaLibro.getValFromReference(workSheet, "B29"));
            representanteLegal.setNotaria(lecturaLibro.getValFromReference(workSheet, "J29"));
            representanteLegal.setTipoTxt(lecturaLibro.getValFromReference(workSheet, "C31"));
            representanteLegal.setNombreNotario(lecturaLibro.getValFromReference(workSheet, "J31"));
            representanteLegal.setReferencia(lecturaLibro.getValFromReference(workSheet, "B32"));
            fEmision = lecturaLibro.getValFromReference(workSheet, "J30");
            if (!fEmision.isEmpty()) {
                representanteLegal.setEmision(DATE_FORMAT.parse(fEmision));
            }
            String f = lecturaLibro.getValFromReference(workSheet, "J32");
            if (!f.isEmpty()) {
                representanteLegal.setIdVigencia(f.contains("/") ? DATE_FORMAT.parse(f) : DATE_FORMAT_ANIO.parse(f));
            }
            representanteLegal.setTipoID(Constantes.CONTACTO_REP_LEGAL);
            // Representante tecnico
            ContactoProveedorVO representanteTecnico = new ContactoProveedorVO();
            representanteTecnico.setNombre(lecturaLibro.getValFromReference(workSheet, "B35"));
            representanteTecnico.setPuesto(lecturaLibro.getValFromReference(workSheet, "H35"));
            representanteTecnico.setTelefono(lecturaLibro.getValFromReference(workSheet, "L35"));
            representanteTecnico.setCorreo(lecturaLibro.getValFromReference(workSheet, "B36"));
            representanteTecnico.setRfc(lecturaLibro.getValFromReference(workSheet, "H36"));
            representanteTecnico.setCelular(lecturaLibro.getValFromReference(workSheet, "L36"));
            representanteTecnico.setTipoTxt(lecturaLibro.getValFromReference(workSheet, "C37"));
            representanteTecnico.setCurp(lecturaLibro.getValFromReference(workSheet, "H37"));
            f = lecturaLibro.getValFromReference(workSheet, "C38");
            if (!f.isEmpty()) {
                representanteTecnico.setIdVigencia(f.contains("/") ? DATE_FORMAT.parse(f) : DATE_FORMAT_ANIO.parse(f));
            }
            representanteTecnico.setReferencia(lecturaLibro.getValFromReference(workSheet, "I38"));
            representanteTecnico.setTipoID(Constantes.CONTACTO_REP_TECNICO);
            // Contacto ventas
            ContactoProveedorVO representanteVenta = new ContactoProveedorVO();
            representanteVenta.setNombre(lecturaLibro.getValFromReference(workSheet, "B41"));
            representanteVenta.setPuesto(lecturaLibro.getValFromReference(workSheet, "H41"));
            representanteVenta.setTelefono(lecturaLibro.getValFromReference(workSheet, "L41"));
            representanteVenta.setCorreo(lecturaLibro.getValFromReference(workSheet, "B42"));
            representanteVenta.setRfc(lecturaLibro.getValFromReference(workSheet, "H42"));
            representanteVenta.setCelular(lecturaLibro.getValFromReference(workSheet, "L42"));
            representanteVenta.setTipoTxt(lecturaLibro.getValFromReference(workSheet, "C43"));
            representanteVenta.setCurp(lecturaLibro.getValFromReference(workSheet, "H43"));
            f = lecturaLibro.getValFromReference(workSheet, "C44");
            if (!f.isEmpty()) {
                representanteVenta.setIdVigencia(f.contains("/") ? DATE_FORMAT.parse(f) : DATE_FORMAT_ANIO.parse(f));
            }
            representanteVenta.setReferencia(lecturaLibro.getValFromReference(workSheet, "I44"));
            representanteVenta.setTipoID(Constantes.CONTACTO_REP_COMPRAS);
            proveedorVo.setContactos(new ArrayList<ContactoProveedorVO>());
            List<ContactoProveedorVO> lista = new ArrayList<>();
            lista.add(representanteLegal);
            lista.add(representanteTecnico);
            lista.add(representanteVenta);
            proveedorVo.setContactos(lista);
            //
        } catch (ParseException e) {
            //
            UtilLog4j.log.error(e);
        }
        return proveedorVo;
    }

    private int cargarDatos(ProveedorVo proveedorVo, String sesion, String rfcEmporesa) {
        boolean proveedorVetado = false;
        ProveedorVetadoVO vo = proveedorVetadoRemote.findbyRfc(proveedorVo.getRfc());
        if (vo.getRfc() != null) {
            proveedorVetado = true;
            UtilLog4j.log.info("El proveedor " + proveedorVo.getRfc() + "Esta vetado.");
            return Constantes.MENOS_UNO;
        }

        if (!proveedorVetado) {
            Proveedor proveedor;
            try {
                proveedor = traerProveedorPorRfc(proveedorVo.getRfc());
                if (proveedor == null) {
                    proveedor = new Proveedor();
                    proveedor.setCartaContenidoNacional(Boolean.FALSE);
                    proveedor.setRfc(proveedorVo.getRfc());
                    proveedor.setFechaCreacion(new Date());
                    proveedor.setGenero(new Usuario(sesion));
                    proveedor.setFechaGenero(new Date());
                    proveedor.setHoraGenero(new Date());
                    proveedor.setEstatus(new Estatus(ProveedorEnum.REGISTRADO.getId()));
                    proveedor.setEliminado(Constantes.NO_ELIMINADO);
                } else {
                    proveedor.setModifico(new Usuario(sesion));
                    proveedor.setFechaModifico(new Date());
                    proveedor.setHoraModifico(new Date());
                }
                if (proveedor.isEliminado()) {
                    proveedor.setEstatus(new Estatus(ProveedorEnum.REGISTRADO.getId()));
                }
                //
                proveedor.setPvTipoPersona(proveedorVo.getPersonaTxt().contains("MORAL") ? new PvTipoPersona(Constantes.UNO) : new PvTipoPersona(Constantes.DOS));
                proveedor.setNombre(proveedorVo.getNombre());
                proveedor.setNacional(proveedorVo.isNacional());
                proveedor.setGiro(proveedorVo.getGiro());
                proveedor.setEstado(proveedorVo.getEstado());
                proveedor.setCiudad(proveedorVo.getCiudad());
                proveedor.setColonia(proveedorVo.getColonia());
                proveedor.setCalle(proveedorVo.getCalle());
                proveedor.setNumero(proveedorVo.getNumero().replaceAll("\\.0", ""));
                proveedor.setNumeroInterior(proveedorVo.getNumeroInt());
                proveedor.setCodigoPostal(proveedorVo.getCodigoPostal().replaceAll("\\.0", ""));

                proveedor.setImsspatronal(proveedorVo.getImssPatronal());
                proveedor.setCurp(proveedorVo.getCurp());
                proveedor.setIdCif(proveedorVo.getIdCIF());
                //
                proveedor.setPais(proveedor.isNacional() ? "MÉXICO" : "");
                proveedor.setEliminado(Constantes.NO_ELIMINADO);

                edit(proveedor);
                // Registro notarial
                pvRegistroFiscalRemote.guardar(proveedor.getId(), proveedorVo, sesion);
                // Registro cuentas
                cuentaBancoProveedorRemote.guardar(proveedor.getId(), proveedorVo.getCuentas(), sesion, rfcEmporesa);
                // Registro contactos
                contactoProveedorRemote.guardar(proveedor.getId(), proveedorVo.getContactos(), sesion);
                // proveedor compania
                pvProveedorCompaniaRemote.guardarRelacionProveedor(proveedor.getId(), rfcEmporesa, "00", sesion);
                // guardar el proveeedor como usuario
                guardarComoUsuario(proveedor, sesion);
                return proveedor.getId();
            } catch (Exception ex) {
                UtilLog4j.log.error(ex);
            }
        }

        return Constantes.CERO;
    }

    private void guardarComoUsuario(Proveedor proveedor, String sesion) {
        try {
            Usuario u = usuarioRemote.buscarPorId(proveedor.getRfc());
            if (u == null) {
                UsuarioVO usuarioVO = new UsuarioVO();
                usuarioVO.setId(proveedor.getRfc());
                usuarioVO.setNombre(proveedor.getNombre());
                usuarioVO.setClave(encriptar("1234"));
                usuarioVO.setTelefono(proveedor.getTelefono());
                usuarioVO.setFechaNacimiento(new Date());
                usuarioVO.setInterno(Constantes.BOOLEAN_FALSE);
                usuarioVO.setIdCampo(Constantes.AP_CAMPO_DEFAULT);
                //
                usuarioRemote.guardarNuevoUsuario(sesion, usuarioVO, Constantes.CERO);
            }
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(ProveedorServicioImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public List<ProveedorVo> traerProveedorEstatus(String sesion, int status, int maximoRegistros) {

        String sb = " select DISTINCT p.id, p.nombre, p.calle, p.colonia, p.ciudad, p.estado, p.pais, p.rfc, p.numero, p.codigo_postal, \n"
                + " p.NUMERO_INTERIOR, p.GIRO, p.CURP, p.IMSSPATRONAL, p.IDCIF, p.NACIONAL, p.PV_TIPO_PERSONA, p.carta_contenido_nacional \n"
                + " from PROVEEDOR p  \n"
                + "     inner join PV_PROVEEDOR_COMPANIA pc  on  pc.proveedor = p.id \n"
                + " where p.status = ?1 "
                + " and pc.COMPANIA in  (SELECT ca.COMPANIA from AP_CAMPO_USUARIO_RH_PUESTO  ca \n"
                + "                         inner join AP_CAMPO a on 	ca.AP_CAMPO = a.id \n"
                + "                 	where ca.usuario = ?2 and ca.ELIMINADO = false ) \n"
                + "                     and p.ELIMINADO = false  order by  p.NOMBRE asc";
        if (maximoRegistros > 0) {
            sb += " limit " + maximoRegistros;
        }

        List<Object[]> lo = em.createNativeQuery(sb).setParameter(1, status).setParameter(2, sesion).getResultList();
        List<ProveedorVo> lp = new ArrayList<>();
        for (Object[] objects : lo) {
            lp.add(castProveedor(objects));
        }
        return lp;
    }

    private ProveedorVo castProveedor(Object[] objects) {
        ProveedorVo proveedorVo = new ProveedorVo();
        proveedorVo.setIdProveedor((Integer) objects[0]);
        proveedorVo.setNombre((String) objects[1]);
        proveedorVo.setCalle((String) objects[2]);
        proveedorVo.setColonia((String) objects[3]);
        proveedorVo.setCiudad((String) objects[4]);
        proveedorVo.setEstado((String) objects[5]);
        proveedorVo.setPais((String) objects[6]);
        proveedorVo.setRfc((String) objects[7]);
        proveedorVo.setNumero((String) objects[8]);
        proveedorVo.setCodigoPostal((String) objects[9]);
        proveedorVo.setNumeroInt((String) objects[10]);
        proveedorVo.setGiro((String) objects[11]);
        proveedorVo.setCurp((String) objects[12]);
        proveedorVo.setImssPatronal((String) objects[13]);
        proveedorVo.setIdCIF((String) objects[14]);
        proveedorVo.setNacional((Boolean) objects[15]);
        proveedorVo.setPersona((Integer) objects[16] != null ? (Integer) objects[16] : Constantes.CERO);
        proveedorVo.setCarta((Boolean) objects[17]);
        //
        proveedorVo.setLstDocsProveedor(pvClasificacionArchivoRemote.traerArchivoPorProveedorOid(proveedorVo.getIdProveedor(), proveedorVo.getIdProveedor()));
        //
        proveedorVo.setEditar(Constantes.BOOLEAN_FALSE);
        return proveedorVo;
    }

    public void procesarProveedor(UsuarioVO sesion, ProveedorVo proveedorVo) {
        Proveedor p = find(proveedorVo.getIdProveedor());
        p.setCartaContenidoNacional(proveedorVo.isCarta());
        p.setOcTerminoPago(new OcTerminoPago(proveedorVo.getIdPago()));
        p.setEstatus(new Estatus(ProveedorEnum.EN_PROCESO.getId()));
        p.setModifico(new Usuario(sesion.getId()));
        p.setFechaModifico(new Date());
        p.setHoraModifico(new Date());
        edit(p);
        // notificar
        notificacionServicioRemoto.notificacionProveedorProceso(proveedorVo, sesion);
    }

    public void activarProveedor(String sesion, ProveedorVo proveedorVo, int campo) {
        Proveedor p = find(proveedorVo.getIdProveedor());
        p.setEstatus(new Estatus(ProveedorEnum.ACTIVO.getId()));
        p.setModifico(new Usuario(sesion));
        p.setFechaModifico(new Date());
        p.setHoraModifico(new Date());
        edit(p);
        // notificar
        notificacionServicioRemoto.notificacionAltaProveedor(proveedorVo, p.getGenero() != null ? p.getGenero().getEmail() : p.getModifico().getEmail(), campo);
    }

    public void devolverProveedor(Usuario usuario, ProveedorVo proveedorVo, String motivo) {
        Proveedor p = find(proveedorVo.getIdProveedor());
        p.setEstatus(new Estatus(ProveedorEnum.REGISTRADO.getId()));
        p.setModifico(usuario);
        p.setFechaModifico(new Date());
        p.setHoraModifico(new Date());
        edit(p);
        //
        pvProveedorMovimientoRemote.guardar(usuario.getId(), p.getId(), motivo);
        // notificar
        notificacionServicioRemoto.notificacionDevolucionProveedor(proveedorVo, motivo, p.getGenero() != null ? p.getGenero().getEmail() : p.getModifico().getEmail(), usuario.getEmail());
    }

    public ProveedorVo traerProveedorPorRfc(String rfc, String clave, int idProve, String compania) {
        ProveedorVo provPortalVO = null;
        try {
            String q = "";
            try {
                if (idProve < 1) {
                    q = consulta()
                            + " where p.rfc = '" + rfc + "'"
                            + " and p.clave = '" + encriptar(clave) + "'"
                            + " and p.status = " + ProveedorEnum.ACTIVO.getId();
                }
                if (idProve > 0) {
                    q = consulta()
                            + " where p.id = " + idProve;
                }
            } catch (NoSuchAlgorithmException ex) {
                Logger.getLogger(ProveedorServicioImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
            Object[] obj = (Object[]) em.createNativeQuery(q).getSingleResult();
            provPortalVO = castProveedor(obj, compania);
            if (provPortalVO != null && provPortalVO.getIdProveedor() > 0) {
                List<ProveedorDocumentoVO> lvoSE = pvClasificacionArchivoRemote.traerArchivoPorProveedorListid(provPortalVO.getIdProveedor(), Constantes.LISTA_TIPO_PORTAL, Constantes.DOCUMENTO_TIPO_SERV_ESP);
                if (lvoSE != null && lvoSE.size() > 0) {
                    provPortalVO.setPortalServEsp(lvoSE.get(0));
                }
                List<ProveedorDocumentoVO> lvoAP = pvClasificacionArchivoRemote.traerArchivoPorProveedorListid(provPortalVO.getIdProveedor(), Constantes.LISTA_TIPO_PORTAL, Constantes.DOCUMENTO_TIPO_ACT_PREP);
                if (lvoAP != null && lvoAP.size() > 0) {
                    provPortalVO.setPortalActPrep(lvoAP.get(0));
                }
                List<ProveedorDocumentoVO> lvoESV = pvClasificacionArchivoRemote.traerArchivoPorProveedorListid(provPortalVO.getIdProveedor(), Constantes.LISTA_TIPO_PORTAL, Constantes.DOCUMENTO_TIPO_EST_SOC_VIG);
                if (lvoESV != null && lvoESV.size() > 0) {
                    provPortalVO.setPortalEstSocVig(lvoESV.get(0));
                }
            }

        } catch (Exception e) {
            UtilLog4j.log.info(e, "No se encontró el proveedor . . . ");
            provPortalVO = null;
        }
        return provPortalVO;
    }

    public void guardarProveedorDesdeArchivo(File leerArchivoPrecio, String sesion) {
        try {
            LecturaLibro lecturaLibro = new LecturaLibro();
            XSSFWorkbook archivo = lecturaLibro.loadFileXLSX(leerArchivoPrecio);
            XSSFSheet workSheet = lecturaLibro.loadSheet(archivo, Constantes.UNO);
            // proveedor
            for (int i = 1; i <= workSheet.getLastRowNum(); i++) {
                int fila = i + 1;
                ProveedorVo ca = readSheetDataSave(workSheet, fila);
                if (ca != null && !ca.getRfc().isEmpty()) {
                    Proveedor proveedor = traerProveedorPorRfc(ca.getRfc());
                    if (proveedor == null) {
                        proveedor = new Proveedor();
                        proveedor.setRfc(ca.getRfc());
                        proveedor.setNombre(ca.getGiro());
                        proveedor.setNombreCorto(ca.getNombre());
                        proveedor.setIdCif(ca.getIdCIF());
                        proveedor.setCalle(ca.getCalle());
                        proveedor.setNumero(ca.getNumero());
                        proveedor.setColonia(ca.getColonia());
                        proveedor.setCodigoPostal(ca.getCodigoPostal());
                        proveedor.setEstado(ca.getEstado());
                        proveedor.setPais(ca.getPais());
                        proveedor.setNacional(Constantes.BOOLEAN_FALSE);
                        proveedor.setEstatus(new Estatus(ProveedorEnum.ACTIVO.getId()));
                        proveedor.setGenero(new Usuario(sesion));
                        proveedor.setFechaCreacion(new Date());
                        proveedor.setFechaGenero(new Date());
                        proveedor.setHoraGenero(new Date());
                        proveedor.setEliminado(Constantes.NO_ELIMINADO);
                        //
                        create(proveedor);
                        //
                        List<CompaniaBloqueGerenciaVo> li = apCampoUsuarioRhPuestoRemote.traerCompaniasBloquesGerencias(sesion);
                        for (CompaniaBloqueGerenciaVo companiaBloqueGerenciaVo : li) {
                            pvProveedorCompaniaRemote.guardarRelacionProveedor(proveedor.getId(), companiaBloqueGerenciaVo.getCompaniaRfc(), "000", sesion);
                        }
                    }
                }
            }
            // contacto
            XSSFSheet workSheetContacto = lecturaLibro.loadSheet(archivo, Constantes.CERO);
            for (int j = 3; j <= workSheetContacto.getLastRowNum(); j++) {
                int fila = j + 1;
                ContactoProveedorVO cpvo = readSheetDataContacto(workSheetContacto, fila);
                Proveedor p = traerProveedorPorRfc(cpvo.getRfc());
                if (p != null) {
                    contactoProveedorRemote.guardarContacto(p.getId(), cpvo.getNombre(), cpvo.getTelefono(), cpvo.getCorreo(), 3, sesion);
                }

            }
        } catch (Exception ex) {
            UtilLog4j.log.error(ex);
        }
    }

    private ContactoProveedorVO readSheetDataContacto(XSSFSheet workSheet, int fila) {
        ContactoProveedorVO cpvo = new ContactoProveedorVO();
        LecturaLibro lecturaLibro = new LecturaLibro();
        cpvo.setNombre(lecturaLibro.getValFromReference(workSheet, "C" + fila));
        cpvo.setRfc(lecturaLibro.getValFromReference(workSheet, "D" + fila));
        cpvo.setPuesto(lecturaLibro.getValFromReference(workSheet, "E" + fila));
        cpvo.setTelefono(lecturaLibro.getValFromReference(workSheet, "F" + fila));
        cpvo.setCorreo(lecturaLibro.getValFromReference(workSheet, "H" + fila));
        return cpvo;
    }

    private ProveedorVo readSheetDataSave(XSSFSheet workSheet, int fila) {
        UtilLog4j.log.info("Leyendo datos ...");
        ProveedorVo proveedor = new ProveedorVo();
        LecturaLibro lecturaLibro = new LecturaLibro();
        try {
            proveedor.setRfc(lecturaLibro.getValFromReference(workSheet, "B" + fila));
            proveedor.setNombre(lecturaLibro.getValFromReference(workSheet, "C" + fila));
            proveedor.setGiro(lecturaLibro.getValFromReference(workSheet, "D" + fila));
            proveedor.setIdCIF(lecturaLibro.getValFromReference(workSheet, "E" + fila));
            proveedor.setCalle(lecturaLibro.getValFromReference(workSheet, "F" + fila));
            proveedor.setNumero(lecturaLibro.getValFromReference(workSheet, "G" + fila));
            proveedor.setColonia(lecturaLibro.getValFromReference(workSheet, "H" + fila));
            proveedor.setCodigoPostal(lecturaLibro.getValFromReference(workSheet, "I" + fila));
            proveedor.setEstado(lecturaLibro.getValFromReference(workSheet, "J" + fila));
            proveedor.setPais(lecturaLibro.getValFromReference(workSheet, "K" + fila));
//
            return proveedor;
        } catch (Exception e) {
            UtilLog4j.log.error(e);
            return null;

        }
    }

    public List<ProveedorVo> traerProveedorPorParteNombre(String nombre, String sesion, int status) {
        String sb = " select DISTINCT p.id, p.nombre, p.calle, p.colonia, p.ciudad, p.estado, p.pais, p.rfc, p.numero, p.codigo_postal, \n"
                + " p.NUMERO_INTERIOR, p.GIRO, p.CURP, p.IMSSPATRONAL, p.IDCIF, p.NACIONAL, p.PV_TIPO_PERSONA, p.carta_contenido_nacional\n"
                + " from PROVEEDOR p  \n"
                + "     inner join PV_PROVEEDOR_COMPANIA pc  on  pc.proveedor = p.id \n"
                + " where p.status = ?1 "
                + " and upper(p.nombre) like UPPER('%" + nombre + "%')"
                + " and pc.COMPANIA in  (SELECT ca.COMPANIA from AP_CAMPO_USUARIO_RH_PUESTO  ca \n"
                + "                         inner join AP_CAMPO a on 	ca.AP_CAMPO = a.id \n"
                + "                 	where ca.usuario = ?2 and ca.ELIMINADO = false ) \n"
                + "                     and p.ELIMINADO = false  order by  p.NOMBRE asc";

        List<Object[]> lo = em.createNativeQuery(sb).setParameter(1, status).setParameter(2, sesion).getResultList();
        List<ProveedorVo> lp = new ArrayList<>();
        for (Object[] objects : lo) {
            lp.add(castProveedor(objects));
        }
        return lp;
    }

    public String correosProveedor(int idProveedor) {
        String c = " select  COALESCE(array_to_string(array_agg(DISTINCT cp.correo), ', '), 'siaihsa@gmail.com') from contacto_proveedor cp"
                + "  where cp.proveedor = " + idProveedor
                + "  and cp.eliminado = false ";
        return (String) em.createNativeQuery(c).getSingleResult();
    }

    public String correosProveedorOrden(int idOrden) {
        String c = " select  COALESCE(array_to_string(array_agg(DISTINCT cp.correo), ', '), 'siaihsa@gmail.com') from contactos_orden co "
                + "  inner join contacto_proveedor cp on cp.id = co.contacto_proveedor and cp.eliminado = false "
                + "  where co.orden = " + idOrden
                + "  and co.eliminado = false ";
        return (String) em.createNativeQuery(c).getSingleResult();
    }

    public boolean eliminarArchivosPortal(int idProveedor, int idList, String userID, String archivos) {
        boolean eliminar = false;
        try {
            if (idProveedor > 0 && idList > 0) {
                List<ProveedorDocumentoVO> lvoSE = pvClasificacionArchivoRemote.traerArchivoPorProveedorListid(idProveedor, idList, Constantes.CERO);
                for (ProveedorDocumentoVO vvoo : lvoSE) {
                    if (archivos.contains(vvoo.getAdjuntoVO().getUuid())) {
                        siAdjuntoServicioRemoto.eliminarArchivo(vvoo.getAdjuntoVO().getId(), userID);
                        pvClasificacionArchivoRemote.quitarArchivoDocumento(userID, vvoo.getId());
                    }
                }
            }
            eliminar = true;
        } catch (Exception e) {
            UtilLog4j.log.info(e, "No se pudo eliminar los archivos . . . ");
            eliminar = false;
        }
        return eliminar;
    }

    public File crearZipFile(String rfc, List<ProveedorDocumentoVO> archivos) {
        File fileTempExcel = null;
        try {
            AlmacenDocumentos almacenDocumentos = proveedorAlmacenDocumentosRemote.getAlmacenDocumentos();
            String REPOSITORYPATH = this.parametrosSistemaServicioRemoto.find(1).getUploadDirectory();
            String PLANTILLAPATH = "Factura/Temporal";
            String URL_Temporal = new StringBuilder().append(REPOSITORYPATH).append(PLANTILLAPATH).append(File.separator).toString();
            fileTempExcel = File.createTempFile(rfc + "_ArchivosPortal_", ".zip", new File(URL_Temporal));

            // create byte buffer
            byte[] buffer = new byte[1024];

            FileOutputStream fos = new FileOutputStream(fileTempExcel);

            ZipOutputStream zos = new ZipOutputStream(fos);

            for (ProveedorDocumentoVO archivo : archivos) {
                if (archivo != null && archivo.getAdjuntoVO() != null
                        && archivo.getAdjuntoVO().getUrl() != null && !archivo.getAdjuntoVO().getUrl().isEmpty()) {
                    try {
                        DocumentoAnexo documento = almacenDocumentos.cargarDocumento(archivo.getAdjuntoVO().getUrl());
                        InputStream fis = new ByteArrayInputStream(documento.getContenido());
                        zos.putNextEntry(new ZipEntry(archivo.getAdjuntoVO().getNombre()));
                        int length;
                        while ((length = fis.read(buffer)) > 0) {
                            zos.write(buffer, 0, length);
                        }
                        zos.closeEntry();
                        // close the InputStream
                        fis.close();
                    } catch (SIAException ex) {
                        zos.putNextEntry(new ZipEntry("_ERROR"));
                        zos.closeEntry();
                        Logger.getLogger(SiFacturaAdjuntoImpl.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (ZipException ex) {
                        Logger.getLogger(SiFacturaAdjuntoImpl.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (Exception ex) {
                        Logger.getLogger(SiFacturaAdjuntoImpl.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
            // close the ZipOutputStream
            zos.close();

        } catch (IOException ioe) {
            System.out.println("Error creating zip file: " + ioe);
        }
        return fileTempExcel;
    }

    public File crearFile(String rfc, ProveedorDocumentoVO archivo) {
        File fileTemp = null;
        try {
            if (archivo != null && archivo.getAdjuntoVO().getUrl() != null && !archivo.getAdjuntoVO().getUrl().isEmpty()) {
                AlmacenDocumentos almacenDocumentos = proveedorAlmacenDocumentosRemote.getAlmacenDocumentos();
                String REPOSITORYPATH = this.parametrosSistemaServicioRemoto.find(1).getUploadDirectory();
                String PLANTILLAPATH = "Factura/Temporal";
                String URL_Temporal = new StringBuilder().append(REPOSITORYPATH).append(PLANTILLAPATH).append(File.separator).toString();
                fileTemp = new File(URL_Temporal + archivo.getAdjuntoVO().getNombre());

                FileOutputStream fos = new FileOutputStream(fileTemp);

                if (archivo.getAdjuntoVO() != null
                        && archivo.getAdjuntoVO().getUrl() != null && !archivo.getAdjuntoVO().getUrl().isEmpty()) {
                    try {
                        DocumentoAnexo documento = almacenDocumentos.cargarDocumento(archivo.getAdjuntoVO().getUrl());
                        fos.write(documento.getContenido());

                    } catch (SIAException | IOException ex) {
                        Logger.getLogger(SiFacturaAdjuntoImpl.class.getName()).log(Level.SEVERE, null, ex);
                    } finally {
                        fos.close();
                    }
                }
            }
        } catch (IOException ioe) {
            System.out.println("Error creating zip file: " + ioe);
        }
        return fileTemp;
    }

    public ProveedorVo traerProveedorPorRFC(String rfc) {
        StringBuilder sb = new StringBuilder();
        sb.append(consulta());
        sb.append(" where p.rfc  = '")
                .append(rfc).append("'")
                .append(" and p.eliminado = false ");
        Object[] objects = (Object[]) em.createNativeQuery(sb.toString()).getSingleResult();
        return castProveedor(objects, "");
    }

    public ProveedorVo traerProveedorPorNombre(String nombre) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(consulta());
            sb.append(" where p.nombre  = '")
                    .append(nombre).append("'")
                    .append(" and p.eliminado = false ");
            Object[] objects = (Object[]) em.createNativeQuery(sb.toString()).getSingleResult();
            return castProveedor(objects, "");
        } catch (Exception e) {
            return null;
        }
    }

    public List<String> traerRfcNombreLikeProveedorQueryNativo(String cadena, String rfcCompania, int status) {
        return em.createNativeQuery("SELECT p.rfc || ' / ' || p.nombre FROM pv_proveedor_compania pc"
                + "     inner join proveedor p on pc.proveedor = p.id"
                + " WHERE p.status = " + status
                + " AND  upper(p.nombre) LIKE '" + cadena.toUpperCase() + "%'"
                + " and pc.compania = '" + rfcCompania + "' "
                + " and pc.eliminado = false ORDER BY p.nombre ASC").getResultList();
    }

}
