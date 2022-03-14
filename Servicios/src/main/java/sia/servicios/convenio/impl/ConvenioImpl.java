/*
 * ConvenioFacade.java
 * Creada el 27/08/2009, 10:11:11 AM
 * Clase Java desarrollada por: Héctor Acosta Sierra para: MPG-IHSA
 *
 * Para información sobre el uso de esta clase, asi como bugs, actualizaciones o mejoras
 * enviar un mail a: hacosta@mpg-ihsa.com.mx o a: new_nick_name@hotmail.com
 */
package sia.servicios.convenio.impl;

import java.io.File;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.ejb.LocalBean;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import sia.constantes.Constantes;
import sia.inventarios.service.ArticuloImpl;
import sia.modelo.ApCampo;
import sia.modelo.Convenio;
import sia.modelo.CvClasificacion;
import sia.modelo.CvRelacionActividad;
import sia.modelo.CvTipo;
import sia.modelo.Estatus;
import sia.modelo.InvArticulo;
import sia.modelo.Moneda;
import sia.modelo.Proveedor;
import sia.modelo.ProveedorActividad;
import sia.modelo.RhDocumentosTipoContrato;
import sia.modelo.SiUnidad;
import sia.modelo.Usuario;
import sia.modelo.contrato.vo.ClasificacionVo;
import sia.modelo.contrato.vo.ContratoDocumentoVo;
import sia.modelo.contrato.vo.ContratoVO;
import sia.modelo.contrato.vo.ConvenioArticuloVo;
import sia.modelo.contrato.vo.FiltroVo;
import sia.modelo.contrato.vo.RhConvenioDocumentoVo;
import sia.modelo.gerencia.vo.GerenciaVo;
import sia.modelo.proveedor.Vo.ContactoProveedorVO;
import sia.modelo.proveedor.Vo.ProveedorVo;
import sia.modelo.sistema.AbstractFacade;
import sia.modelo.usuario.vo.UsuarioResponsableGerenciaVo;
import sia.modelo.usuario.vo.UsuarioRolVo;
import sia.modelo.vo.ApCampoGerenciaVo;
import sia.modelo.vo.ApCampoVo;
import sia.notificaciones.convenio.impl.NotificacionConvenioImpl;
import sia.servicios.campo.nuevo.impl.ApCampoGerenciaImpl;
import sia.servicios.campo.nuevo.impl.ApCampoImpl;
import sia.servicios.catalogos.impl.EstatusImpl;
import sia.servicios.catalogos.impl.MonedaImpl;
import sia.servicios.proveedor.impl.ProveedorActividadImpl;
import sia.servicios.proveedor.impl.ProveedorServicioImpl;
import sia.servicios.rh.impl.RhConvenioDocumentosImpl;
import sia.servicios.sistema.impl.FolioImpl;
import sia.servicios.sistema.impl.SiManejoFechaImpl;
import sia.servicios.sistema.impl.SiUnidadImpl;
import sia.servicios.sistema.impl.SiUsuarioRolImpl;
import sia.servicios.sistema.vo.CatalogoContratoVo;
import sia.util.LecturaLibro;
import sia.util.UtilLog4j;

/**
 *
 * @author Héctor Acosta Sierra
 * @version 1.0
 * @author-mail new_nick_name@hotmail.com @date 27/08/2009
 */
@LocalBean 
public class ConvenioImpl extends AbstractFacade<Convenio> {

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ConvenioImpl() {
        super(Convenio.class);
    }
//GENERA CONTRATO
    @Inject
    private ProveedorServicioImpl proveedorServicioRemoto;
    @Inject
    private EstatusImpl estatusServicioRemoto;
    @Inject
    private MonedaImpl monedaServicioRemoto;
    @Inject
    private FolioImpl folioRemote;
    @Inject
    private CvRelacionActividadImpl cvRelacionActividadServicioRemoto;
    @Inject
    private ClasificacionServicioImpl clasificacionServicioServicioRemoto;
    @Inject
    private SiManejoFechaImpl siManejoFechaImpl;
    @Inject
    private ProveedorActividadImpl proveedorActividadRemote;
    @Inject
    private CvConvenioAdjuntoImpl cvConvenioAdjuntoImpl;
    @Inject
    private CvConvenioDocumentoImpl cvConvenioDocumentoImpl;
    @Inject
    private CvConvenioHitoImpl cvConvenioHitoImpl;
    @Inject
    private CvConvenioCondicionPagoImpl cvConvenioCondicionPagoImpl;
    @Inject
    private CvClasificacionImpl cvClasificacionImpl;
    @Inject
    private SiUsuarioRolImpl siUsuarioRolRemote;
    @Inject
    private NotificacionConvenioImpl notificacionConvenioImpl;
    @Inject
    private ApCampoGerenciaImpl apCampoGerenciaRemote;
    @Inject
    private CvConvenioGerenciaImpl cvConvenioGerenciaImpl;
    @Inject
    private ApCampoImpl apCampoRemote;
    @Inject
    private ArticuloImpl invArticuloRemote;
    @Inject
    private SiUnidadImpl unidadRemote;
    @Inject
    RhDocumentosTipoContratoImpl rhDocumentosTipoContratoImpl;
    @Inject
    RhConvenioDocumentosImpl rhConvenioDocumentosImpl;

    
    public void create(Convenio convenio) {
        em.persist(convenio);
    }

    
    public void edit(Convenio convenio) {
        em.merge(convenio);
    }

    
    public void remove(Convenio convenio) {
        em.remove(em.merge(convenio));
    }

    
    public Convenio find(Object id) {
        return em.find(Convenio.class, id);
    }

    
    public List<Convenio> findAll() {
        return em.createQuery("select object(o) from Convenio as o").getResultList();
    }

    
    public List getConveniosPorLetra(Object letra) {
        return em.createQuery("SELECT c FROM Convenio c WHERE c.nombre LIKE :nombre ORDER BY c.nombre ASC").setParameter("nombre", letra.toString().toUpperCase() + "%").getResultList();
    }

    
    public List<Convenio> getConveniosPorProveedor(Object nombreProveedor) {
        return em.createQuery("SELECT c FROM Convenio c WHERE c.proveedor.nombre = :proveedor ORDER BY c.nombre ASC").setParameter("proveedor", nombreProveedor).getResultList();
    }

    
    public boolean guardarContrato(String sesion, String id, int proveedor, int estatus, String moneda, String compania, String url, String nombre,
            double monto, String vigencia, Date fechaInicio, Date fechaVencimiento, int tipo, int actividad, int subActividad) {
        boolean v = false;
        int vi = siManejoFechaImpl.diferenciaDias(siManejoFechaImpl.converterDateToCalendar(fechaInicio, false), siManejoFechaImpl.converterDateToCalendar(fechaVencimiento, false));
        Convenio convenio = new Convenio();
        convenio.setCodigo(id.toUpperCase());
        convenio.setProveedor(this.proveedorServicioRemoto.find(proveedor));
        convenio.setEstatus(this.estatusServicioRemoto.find(estatus));
        convenio.setMoneda(this.monedaServicioRemoto.buscarPorNombre(moneda, convenio.getApCampo().getCompania().getRfc()));
        convenio.setCvTipo(new CvTipo(tipo));
        convenio.setNombre(nombre);
        convenio.setMonto(monto);
        convenio.setVigencia(vi + " días");
        //convenio.setVigencia(vigencia + "días");
        convenio.setFechaInicio(fechaInicio);
        convenio.setFechaVencimiento(fechaVencimiento);
        convenio.setGenero(new Usuario(sesion));
        convenio.setFechaGenero(new Date());
        convenio.setHoraGenero(new Date());
        convenio.setEliminado(Constantes.NO_ELIMINADO);
        //
        this.create(convenio);
        //Generamos el nuevo registro en la tabla Relacion_actividad
        CvRelacionActividad cvRelacionActividad = new CvRelacionActividad();
        //
        cvRelacionActividad.setConvenio(convenio);
        cvRelacionActividad.setClasificacion(this.clasificacionServicioServicioRemoto.find(subActividad));
        this.cvRelacionActividadServicioRemoto.create(cvRelacionActividad);
        //
        ProveedorActividad proveedorActividad = proveedorActividadRemote.buscarPorProveedorActividad(proveedor, actividad);
        if (proveedorActividad == null) {
            proveedorActividadRemote.agregarActividad(proveedor, actividad, sesion);
        }
        v = true;
        return v;
    }

    
    public int guardar(String sesion, ContratoVO contratoVO, List<Integer> listaClasificacion) {
        Convenio convenio = new Convenio();
        try {
            //convenio.setCodigo(contratoVO.getNumero().toUpperCase());
            convenio.setProveedor(new Proveedor(contratoVO.getProveedor()));
            convenio.setEstatus(new Estatus(Constantes.ESTADO_CONVENIO_REGISTRADO));
            convenio.setCvTipo(new CvTipo(contratoVO.getIdTipo()));
            convenio.setCvClasificacion(new CvClasificacion(listaClasificacion.get(listaClasificacion.size() - 1)));
            //
            convenio.setApCampo(new ApCampo(contratoVO.getIdCampo()));
            convenio.setNombre(contratoVO.getNombre());
            //
            convenio.setConvenio(contratoVO.getIdContratoRelacionado() > 0 ? new Convenio(contratoVO.getIdContratoRelacionado()) : null);
            if (convenio.getConvenio() == null) {
                convenio.setCodigo(generaCodigo(contratoVO.getProveedor(), listaClasificacion.get(Constantes.CERO), convenio.getApCampo().getId()));
            } else {
                convenio.setCodigo(sumarVersion(find(contratoVO.getIdContratoRelacionado()).getCodigo()));
            }
            //
            convenio.setGenero(new Usuario(sesion));
            convenio.setFechaGenero(new Date());
            convenio.setHoraGenero(new Date());
            convenio.setEliminado(Constantes.NO_ELIMINADO);
            //
            create(convenio);
            // agregar las gerencias al contrato
            for (GerenciaVo listaGerencia : contratoVO.getListaGerencia()) {
                cvConvenioGerenciaImpl.guardar(sesion, convenio.getId(), listaGerencia.getId());
            }
            //
            if (convenio.getConvenio() == null) {
                // documentos de RH para el contratoas
                List<RhDocumentosTipoContrato> doctos = rhDocumentosTipoContratoImpl.traerPorClasificacion(listaClasificacion.get(Constantes.CERO));
                if (doctos != null) {
                    for (RhDocumentosTipoContrato docto : doctos) {
                        RhConvenioDocumentoVo rhcdVo = new RhConvenioDocumentoVo();
                        rhcdVo.setIdConvenio(convenio.getId());
                        rhcdVo.setIdDocumento(docto.getRhDocumentos().getId());
                        rhcdVo.setIdAdjunto(Constantes.CERO);
                        //
                        rhConvenioDocumentosImpl.guardar(sesion, rhcdVo);
                    }
                }
            }

            //
            if (convenio.getConvenio() != null) {
                Convenio c = find(contratoVO.getIdContratoRelacionado());
                convenio.setMonto(0.0);//c.getMonto());
                convenio.setMoneda(c.getMoneda());
                convenio.setPorcentajeDeduccion(c.getPorcentajeDeduccion());

                // Hitos de pago
                List<CatalogoContratoVo> hitos = cvConvenioHitoImpl.traerHitosPorConvenio(contratoVO.getIdContratoRelacionado());
                if (hitos != null && !hitos.isEmpty()) {
                    for (CatalogoContratoVo hito : hitos) {
                        cvConvenioHitoImpl.guardar(sesion, convenio.getId(), hito.getIdTabla(), hito.getIdRelacion());
                    }
                }
                //Condiciones de pago
                List<CatalogoContratoVo> condiciones = cvConvenioCondicionPagoImpl.traerCondicionesPago(contratoVO.getIdContratoRelacionado());
                if (condiciones != null && !condiciones.isEmpty()) {
                    for (CatalogoContratoVo condicione : condiciones) {
                        cvConvenioCondicionPagoImpl.guardar(sesion, convenio.getId(), condicione.getIdTabla());
                    }
                }
                // Documentacion del convenio
                List<ContratoDocumentoVo> documentos = cvConvenioDocumentoImpl.traerDoctosPorConveni(contratoVO.getIdContratoRelacionado(), null, null);
                if (documentos != null && !documentos.isEmpty()) {
                    cvConvenioDocumentoImpl.guardarConvenioRelacionado(sesion, documentos, convenio.getId());
                }

                edit(convenio);
            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(e);
        }
        return convenio.getId();
    }

    private String generaCodigo(int proveedor, int clasificacion, int campo) {
        Proveedor p = proveedorServicioRemoto.find(proveedor);
        String folio = folioRemote.traerFolioAnio(Constantes.CONVENIO.toUpperCase(), campo);
        ClasificacionVo vo = cvClasificacionImpl.buscarPorId(clasificacion);
        String clasi = (vo.getNombre().substring(0, 2)).toUpperCase();
        //
        String codigo = folio + "-" + p.getRfc() + "-" + clasi + "-" + "00";
        return codigo;
    }

    private String sumarVersion(String codigo) {
        String[] cad = codigo.split("-");
        String codBase = "";
        int num = 0;
        switch (cad.length) {
            case 5: {
                // El codigo tiene el nuevo formato
                codBase = cad[0] + "-" + cad[1] + "-" + cad[2] + "-" + cad[3];
                List<ContratoVO> lc = traerConveniosPorParteDeCodigo(codBase);
                if (lc != null && !lc.isEmpty()) {
                    ContratoVO c = lc.get(lc.size() - 1);
                    String[] cod = c.getNumero().split("-");
                    num = Integer.parseInt(cod[4]) + 1;
                }
                break;
            }
            case 6: {
                codBase = cad[0] + "-" + cad[1] + "-" + cad[2] + "-" + cad[3] + "-" + cad[4];
                List<ContratoVO> lc = traerConveniosPorParteDeCodigo(codBase);
                if (lc != null && !lc.isEmpty()) {
                    ContratoVO c = lc.get(lc.size() - 1);
                    String[] cod = c.getNumero().split("-");
                    num = Integer.parseInt(cod[5]) + 1;
                }
                break;
            }
            default:
                //El codigo no tiene el nuevo formato
                String codFinal = (codigo.substring(codigo.length() - 2, codigo.length()));
                codBase = codigo.substring(0, codigo.length() - 3);
                if (codFinal.equals("00")) { // Aun así el código termina en 00
                    String codInicio = codigo.substring(0, codigo.length() - 3);
                    List<ContratoVO> lc = traerConveniosPorParteDeCodigo(codInicio);
                    if (lc != null && !lc.isEmpty()) {
                        ContratoVO c = lc.get(lc.size() - 1);
                        String[] cod = c.getNumero().split("-");
                        num = Integer.parseInt(cod[cod.length - 1]) + 1;
                    }
                } else { // El codigo del contrato de referencia no termina en 00
                    List<ContratoVO> lc = traerConveniosPorParteDeCodigo(codigo);
                    if (lc != null && lc.size() > 1) {
                        ContratoVO c = lc.get(lc.size() - 1);
                        String[] cod = c.getNumero().split("-");
                        num = Integer.parseInt(cod[cod.length - 1]) + 1;
                    } else {
                        codBase = codigo;
                        num = 1;
                    }
                }
                break;
        }
        return codBase + "-" + (num < 10 ? "0" + num : "" + num);
    }

    
    public List<Convenio> traerConvenioSinArchivo(String user) {
        return em.createQuery("SELECT c FROM Convenio c WHERE c.id NOT IN (SELECT s.idElemento FROM SiAdjunto s WHERE s.siModulo.id = :seis) and c.eliminado = 'False'  ORDER BY c.nombre ASC").setParameter("seis", 6).getResultList();
    }

    
    public boolean agregarArchivoConvenio(String idConvenio, String archivo) {
        boolean v = false;
        Convenio convenio = this.find(idConvenio);
        this.edit(convenio);
        v = true;
        return v;
    }

    
    public List<Convenio> traerConvenioConArchivo(String idConvenio) {
        return em.createQuery("SELECT c FROM Convenio c WHERE c.id = :id").setParameter("id", idConvenio).getResultList();
    }

    
    public void actualizarURLConvenio(String idConvenio) {
        try {
            Convenio convenio = this.find(idConvenio);
            this.edit(convenio);
        } catch (Exception e) {
            e.getMessage();
        }
    }

    
    public List<Convenio> getConveniosVigentePorProveedor(String proveedor, int actividad) {
        return em.createQuery("SELECT c FROM Convenio c WHERE c.proveedor.nombre = :pro AND c.estatus.id = :es AND "
                + " c.cvTipo.id = :uno").setParameter("es", 310).setParameter("pro", proveedor).setParameter("uno", 1).getResultList();
    }

    
    public List<Convenio> traerAcuerdosporProveedor(String proveedor) {
        return em.createQuery("SELECT c FROM Convenio c WHERE c.proveedor.nombre = :pro AND c.estatus.id = :es AND "
                + " c.cvTipo.id = :dos").setParameter("pro", proveedor).setParameter("es", 310).setParameter("dos", 2).getResultList();
    }

    
    public List<Convenio> traerServiciosPorProveedor(String proveedor) {
        return em.createQuery("SELECT c FROM Convenio c WHERE c.proveedor.nombre = :pro AND c.estatus.id = :id AND "
                + " c.cvTipo.id = :tres").setParameter("pro", proveedor).setParameter("id", 310).setParameter("tres", 3).getResultList();
    }

    
    public void cancelarConvenio(Convenio convenioAcutal) {
        convenioAcutal.setEstatus(this.estatusServicioRemoto.find(300));
        this.edit(convenioAcutal);
    }

    
    public Convenio buscarContratoPorNumero(String noCo) {
        try {
            return (Convenio) em.createQuery("SELECT c FROM Convenio c WHERE upper(c.codigo) = :noC").setParameter("noC", noCo.toUpperCase()).getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    
    public List<ContratoVO> getContratoPorFecha(Date fechaInicio, Date fechaFin, int campo) {
        List<ContratoVO> le = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            String q = "select c.id,c.codigo, c.nombre,p.id, p.nombre,  c.monto,m.siglas, c.fecha_inicio, c.fecha_vencimiento, t.nombre   "
                    + " from convenio c, proveedor p, moneda m, cv_tipo t "
                    + " where c.proveedor = p.id"
                    + "	and c.ap_campo = " + campo
                    + "  and c.moneda = m.id "
                    + "  and c.cv_tipo = t.id "
                    + "  and c.fecha_vencimiento between cast('" + sdf.format(fechaInicio) + "' as date) and cast('" + sdf.format(fechaFin) + "' as date) "
                    + "  and c.eliminado = 'False' "
                    + " order by c.fecha_vencimiento desc";
            List<Object[]> lo = em.createNativeQuery(q).getResultList();
            //
            for (Object[] objects : lo) {
                le.add(castContratoVo(objects));
            }
        } catch (Exception e) {
            UtilLog4j.log.error(e);
        }
        return le;
    }

    private ContratoVO castContratoVo(Object[] obj) {
        ContratoVO c = new ContratoVO();
        c.setId((Integer) obj[0]);
        c.setNumero((String) obj[1]);
        c.setNombre((String) obj[2]);
        c.setProveedor((Integer) obj[3]);
        c.setNombreProveedor((String) obj[4]);
        c.setMonto((Double) obj[5]);
        c.setMoneda((String) obj[6]);
        c.setFechaInicio((Date) obj[7]);
        c.setFechaVencimiento((Date) obj[8]);
        return c;
    }

    
    public List<ContratoVO> getListConvenioVigente(int idProveedor, Date fechaSolicito) {
        List<ContratoVO> lv = new ArrayList<>();
        limpiarCuerpoQuery();
        query.append("SELECT a.ID, a.CODIGO, a.NOMBRE as convenio, p.id, p.nombre, a.MONTO, m.siglas, ").append(" a.FECHA_INICIO, a.FECHA_VENCIMIENTO").append(" FROM CONVENIO a, proveedor p, moneda m where a.FECHA_VENCIMIENTO >= cast('").append(Constantes.FMT_yyyyMMdd.format(fechaSolicito)).append(" ' as date)").append(" and a.PROVEEDOR = ").append(idProveedor).append(" and a.PROVEEDOR = p.ID and a.MONEDA = m.id and a.eliminado = 'False'");
        //
        List<Object[]> lo = em.createNativeQuery(query.toString()).getResultList();
        for (Object[] objects : lo) {
            lv.add(castContratoVo(objects));
        }
        return lv;
    }

    
    public List<ContratoVO> getListConvenioVigente(int idProveedor, int campo, String codigoConvenioMarco) {
        List<ContratoVO> lv = new ArrayList<>();
        limpiarCuerpoQuery();
        query.append(" SELECT a.ID, a.CODIGO, a.NOMBRE as convenio, p.id, p.nombre, a.MONTO, m.siglas, ").append(" a.FECHA_INICIO, a.FECHA_VENCIMIENTO")
                .append(" FROM proveedor p ")
                .append(" inner join CONVENIO a on a.proveedor = p.id ")
                .append(" inner join moneda m on m.id = a.moneda  ")
                .append(" where a.PROVEEDOR = ").append(idProveedor)
                .append(" and a.ap_campo = ").append(campo)
                .append(" and a.estatus > ").append(Constantes.ESTADO_CONVENIO_REGISTRADO)
                .append(" and a.eliminado = 'False' ");

        if (codigoConvenioMarco != null && !codigoConvenioMarco.isEmpty()) {
            query.append(" and a.convenio = (select COALESCE(convenio, id,0) from convenio where codigo = '").append(codigoConvenioMarco).append("') ")
                    .append(" union  ")
                    .append(" SELECT ")
                    .append(" a.ID, ")
                    .append(" a.CODIGO, ")
                    .append(" a.NOMBRE as convenio, ")
                    .append(" p.id, ")
                    .append(" p.nombre, ")
                    .append(" a.MONTO, ")
                    .append(" m.siglas, ")
                    .append(" a.FECHA_INICIO, ")
                    .append(" a.FECHA_VENCIMIENTO ")
                    .append(" FROM proveedor p ")
                    .append(" inner join CONVENIO a on a.proveedor = p.id ")
                    .append(" inner join moneda m on m.id = a.moneda ")
                    .append(" where a.id = (select COALESCE(convenio, id,0) from convenio where codigo = '").append(codigoConvenioMarco).append("') ");

        }

        List<Object[]> lo = em.createNativeQuery(query.toString()).getResultList();
        for (Object[] objects : lo) {
            lv.add(castContratoVo(objects));
        }
        return lv;

    }

    private void limpiarCuerpoQuery() {
        query.delete(0, query.length());
    }

    
    public List<ContratoVO> listaContratos(int idProveedor, boolean vigente, int idActividad, int campo) {
        limpiarCuerpoQuery();
        List<ContratoVO> lc = null;
        try {
            query.append("select c.ID, c.CODIGO, c.NOMBRE, ct.id, ct.NOMBRE,c.VIGENCIA, p.id, p.nombre, c.MONTO, m.SIGLAS, c.FECHA_INICIO, c.FECHA_VENCIMIENTO ");
            query.append(" from CV_RELACION_ACTIVIDAD cr, convenio c, PROVEEDOR p, MONEDA m, CV_TIPO ct");
            query.append(" where cr.CONVENIO = c.ID and c.PROVEEDOR = p.id and c.MONEDA = m.id and c.CV_TIPO = ct.ID");
            query.append("	and c.ap_campo = ").append(campo);
            query.append(" and p.id = ").append(idProveedor);
            if (vigente) {
                query.append(" and c.FECHA_VENCIMIENTO >= cast('NOW' as date)");
            } else {
                query.append(" and c.FECHA_VENCIMIENTO < cast('NOW' as date)");
            }
            if (idActividad != -1 && idActividad != 0) {
                query.append(" and p.id in (select pa.proveedor from PROVEEDOR_ACTIVIDAD pa where pa.ACTIVIDAD = ").append(idActividad).append(")");
            }
            query.append("	and c.eliminado = 'False'");
            List<Object[]> lo = em.createNativeQuery(query.toString()).getResultList();
            if (lo != null) {
                lc = new ArrayList<>();
                for (Object[] objects : lo) {
                    lc.add(castContrato(objects));
                }
            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Ocurrio un error el recuperar los convenios vigentes. " + e);
        }
        return lc;
    }

    private ContratoVO castContrato(Object[] obj) {
        ContratoVO c = new ContratoVO();
        c.setId((Integer) obj[0]);
        c.setNumero((String) obj[1]);
        c.setNombre((String) obj[2]);
        c.setIdTipo((Integer) obj[3]);
        c.setTipo((String) obj[4]);
        c.setVigencia((String) obj[5]);
        c.setProveedor((Integer) obj[6]);
        c.setNombreProveedor((String) obj[7]);
        c.setMonto((Double) obj[8]);
        c.setMoneda((String) obj[9]);
        c.setFechaInicio((Date) obj[10]);
        c.setFechaVencimiento((Date) obj[11]);

        return c;
    }

    
    public void actualizarFecha(String sesion, int idConvenio, Date fechaInicio, Date fechaVencimiento, double monto) {
        int vi = siManejoFechaImpl.diferenciaDias(siManejoFechaImpl.converterDateToCalendar(fechaInicio, false), siManejoFechaImpl.converterDateToCalendar(fechaVencimiento, false));
        Convenio convenio = find(idConvenio);
        //convenio.setVigencia(vigencia + "días");
        convenio.setFechaInicio(fechaInicio);
        convenio.setFechaVencimiento(fechaVencimiento);
        convenio.setVigencia(vi + " días");
        convenio.setMonto(monto);
        convenio.setModifico(new Usuario(sesion));
        convenio.setFechaModifico(new Date());
        convenio.setHoraModifico(new Date());
        edit(convenio);
        //log

    }

    
    public void eliminarContrato(String sesion, int idConvenio) {
        Convenio convenio = find(idConvenio);
        convenio.setCodigo(convenio.getCodigo() + idConvenio);
        convenio.setEliminado(Constantes.ELIMINADO);
        convenio.setModifico(new Usuario(sesion));
        convenio.setFechaModifico(new Date());
        convenio.setHoraModifico(new Date());
        edit(convenio);
        //log
    }

    
    public List<ContratoVO> listaContratosVigentes(int proveedor, int modulo, String tipoElemento, String fecha, int campo) {
        StringBuilder sb = new StringBuilder();
        sb.append(consultaVigente(modulo, tipoElemento));
        sb.append(" where c.FECHA_VENCIMIENTO >= cast('").append(fecha).append("' as date)");
        sb.append(" and c.ap_campo = ").append(campo);
        sb.append(" and c.PROVEEDOR = ").append(proveedor);
        sb.append(" and a.ELIMINADO ='").append(Constantes.NO_ELIMINADO).append("'");
        sb.append(" and (c.ELIMINADO = '").append(Constantes.NO_ELIMINADO).append("'").append(" or c.ELIMINADO is null)");
        sb.append(" order by c.nombre asc");
        List<Object[]> objects = em.createNativeQuery(sb.toString()).getResultList();
        List<ContratoVO> lista = null;
        if (objects != null) {
            lista = new ArrayList<>();
            for (Object[] object : objects) {
                lista.add(castConvenioOrdenVo(object));
            }
        }

        return lista;
    }

    private ContratoVO castConvenioOrdenVo(Object[] objects) {
        ContratoVO contratoVO = new ContratoVO();/*
	 contratoVO.setNumero(((String) objects[0]).replace(" ", ""));
	 contratoVO.setNumero(contratoVO.getNumero().replace("/", "-"));
	 contratoVO.setNumero(contratoVO.getNumero().replace(".", "-"));
	 contratoVO.setNumero(contratoVO.getNumero().replace("  ", "-"));*/

        contratoVO.setNumero((String) objects[0]);

        contratoVO.setId((Integer) objects[1]);
        contratoVO.setNombre((String) objects[2]);
        contratoVO.setFechaVencimiento((Date) objects[3]);
        contratoVO.getAdjuntoVO().setId((Integer) objects[4]);
        contratoVO.getAdjuntoVO().setNombre((String) objects[5]);
        contratoVO.getAdjuntoVO().setUuid((String) objects[6]);
        contratoVO.setTipo((String) objects[7]);
        return contratoVO;
    }

    
    public ContratoVO buscarContrato(String codigo, int modulo, String tipoElemento) {
        try {
            ContratoVO contratoVO = new ContratoVO();
            StringBuilder sb = new StringBuilder();
            sb.append(consultaVigente(modulo, tipoElemento));
            sb.append(" where replace(c.CODIGO, ' ', '') = '").append(codigo.replace(" ", "")).append("'");
            sb.append(" and a.ELIMINADO = '").append(Constantes.NO_ELIMINADO).append("'");
            // System.out.println("q: " + sb.toString());
            Object[] objects = (Object[]) em.createNativeQuery(sb.toString()).getSingleResult();
            return castConvenioOrdenVo(objects);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     *
     * @param codigo
     * @param modulo
     * @param tipoElemento
     * @return
     */
    
    public List<ContratoVO> listaArchivoContrato(String codigo, int modulo, String tipoElemento) {
        List<ContratoVO> lista = null;
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(consultaVigente(modulo, tipoElemento));
            sb.append(" where replace(c.CODIGO, ' ', '') = '").append(codigo.replace(" ", "")).append("'");
            sb.append(" and a.ELIMINADO = '").append(Constantes.NO_ELIMINADO).append("'");
            sb.append(" order by a.fecha_genero desc");
            //   System.out.println("q: " + sb.toString());
            List<Object[]> objects = em.createNativeQuery(sb.toString()).getResultList();
            if (objects != null) {
                lista = new ArrayList<>();
                for (Object[] object : objects) {
                    lista.add(castConvenioOrdenVo(object));
                }
            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
        }
        return lista;
    }

    private String consultaVigente(int modulo, String tipoElemento) {
        StringBuilder sb = new StringBuilder();
        sb.append("select c.CODIGO, c.ID, c.NOMBRE, c.FECHA_VENCIMIENTO, a.ID, a.NOMBRE,  a.UUID, tc.nombre from CONVENIO c ");
        sb.append("	inner join SI_ADJUNTO a on a.ID_ELEMENTO = c.ID and a.SI_MODULO = ").append(modulo).append(" and a.TIPO_ELEMENTO = '").append(tipoElemento).append("'");
        sb.append("	inner join cv_tipo tc on c.cv_tipo = tc.id ");
        return sb.toString();
    }

    
    public List<ContratoVO> traerConvenios(int totalContratos, int campo) {
        String sb = "select  c.id, c.CODIGO, c.NOMBRE, p.NOMBRE, c.FECHA_FIRMA, c.FECHA_INICIO, c.FECHA_VENCIMIENTO, c.VIGENCIA, ";
        sb += "  c.PORCENTAJE_DEDUCCION, c.MONTO, m.SIGLAS, ct.NOMBRE, cc.NOMBRE,  e.NOMBRE ,";
        sb += "  m.id, ct.id, cc.id, e.id, c.convenio, p.id, c.ap_campo, 0, ap.compania ";
        sb += "  from CONVENIO c";
        sb += "		inner join PROVEEDOR p on c.PROVEEDOR = p.ID";
        sb += "		inner join AP_CAMPO ap on ap.id = c.ap_campo";
        sb += "		left join MONEDA m on c.MONEDA= m.ID";
        sb += "		inner join CV_TIPO ct on c.CV_TIPO = ct.ID";
        sb += "		left join CV_CLASIFICACION cc on c.CV_CLASIFICACION = cc.id ";
        sb += "		inner join ESTATUS e on c.ESTATUS = e.ID    ";
        sb += "	where c.ap_campo = " + campo;
        sb += "	and c.eliminado = 'False'";
        sb += "	order by c.id desc limit " + totalContratos + " ";
        List<Object[]> lo = em.createNativeQuery(sb).getResultList();
        List<ContratoVO> lc = null;
        if (lo != null) {
            lc = new ArrayList<>();
            for (Object[] ocObjects : lo) {
                lc.add(castContratos(ocObjects));
            }
        }
        return lc;
    }

    
    public List<ContratoVO> traerConveniosPorVencer(Date fecha, int dias, int campo) {
        String sb = consulta();
        sb += "  where c.FECHA_VENCIMIENTO between cast('" + siManejoFechaImpl.convertirFechaStringyyyyMMdd(fecha) + "' as date) and cast( '" + siManejoFechaImpl.convertirFechaStringyyyyMMdd(siManejoFechaImpl.fechaSumarDias(fecha, dias)) + "' as date)";
        sb += "	and c.ap_campo = " + campo;
        sb += "	and c.eliminado = 'False'";
        sb += "	 order by c.fecha_vencimiento desc";
        List<Object[]> lo = em.createNativeQuery(sb).getResultList();
        List<ContratoVO> lc = null;
        if (lo != null) {
            lc = new ArrayList<>();
            for (Object[] ocObjects : lo) {
                lc.add(castContratos(ocObjects));
            }
        }
        return lc;
    }

    
    public long totalConveniosPorVencer(Date fecha, int dias, int campo) {
        String sb = consultaTotal();
        sb += "  where c.FECHA_VENCIMIENTO between cast('" + siManejoFechaImpl.convertirFechaStringyyyyMMdd(fecha) + "' as date) and cast( '" + siManejoFechaImpl.convertirFechaStringyyyyMMdd(siManejoFechaImpl.fechaSumarDias(fecha, dias)) + "' as date)";
        sb += "	and c.ap_campo = " + campo;
        sb += "	and c.eliminado = 'False'";
        return ((Long) em.createNativeQuery(sb).getSingleResult());
    }

    
    public List<ContratoVO> traerConveniosPorProveedor(int idProveedor, int campo) {
        String sb = consulta();
        sb += "	where p.id = " + idProveedor;
        sb += "	and c.ap_campo = " + campo;
        sb += "	and c.eliminado = 'False'";
        sb += "	order by c.id desc";
        //
        List<Object[]> lo = em.createNativeQuery(sb).getResultList();
        List<ContratoVO> lc = null;
        if (lo != null) {
            lc = new ArrayList<>();
            for (Object[] ocObjects : lo) {
                lc.add(castContratos(ocObjects));
            }
        }
        return lc;
    }

    
    public List<ContratoVO> traerConvenioMaestroPorProveedor(int idProveedor, int campo) {
        String sb = consulta();
        sb += "	where p.id = " + idProveedor;
        sb += "	and  c.convenio is null";
        sb += "	and c.ap_campo = " + campo;
        sb += "	and c.eliminado = 'False'";
        sb += "	order by c.codigo asc";
        //System.out.println(" q :::  " + sb);
        List<Object[]> lo = em.createNativeQuery(sb).getResultList();
        List<ContratoVO> lc = null;
        if (lo != null) {
            lc = new ArrayList<>();
            for (Object[] ocObjects : lo) {
                lc.add(castContratos(ocObjects));
            }
        }
        return lc;
    }

    
    public ContratoVO buscarPorId(int icContrato, int apCampo, String usuario, boolean validarFormalizado) {
        String sb = consulta();
        sb += "	where c.id = " + icContrato;

        Object[] lo = (Object[]) em.createNativeQuery(sb).getSingleResult();
        ContratoVO lc = null;
        if (lo != null) {
            lc = castContratos(lo);
            if (lc != null && validarFormalizado && !lc.isEditar()) {
                lc.setEditar(siUsuarioRolRemote.buscarRolPorUsuarioModulo(usuario, Constantes.MODULO_CONTRATO, Constantes.COD_EDITAR_CONVENIO_FORMALIZADO, apCampo));
            }
        }
        return lc;
    }

    private String consulta() {
        String sb = "select c.id, c.CODIGO, c.NOMBRE, p.NOMBRE, c.FECHA_FIRMA, c.FECHA_INICIO, c.FECHA_VENCIMIENTO, c.VIGENCIA, ";
        sb += "  c.PORCENTAJE_DEDUCCION, c.MONTO, m.SIGLAS, ct.NOMBRE, cc.NOMBRE,  e.NOMBRE, ";
        sb += "  m.id, ct.id, cc.id, e.id, c.convenio, p.id, c.ap_campo,"
                + " case when c.CONVENIO is not null then	 	\n"
                + "	 	(SELECT sum(cv.MONTO) + (SELECT MONTO from CONVENIO where id = c.CONVENIO) from CONVENIO cv where cv.CONVENIO = c.CONVENIO and cv.eliminado = 'False')\n"
                + "	 else\n"
                + "	 	c.MONTO  +\n"
                + "	 	(SELECT sum(cv.MONTO) from CONVENIO cv where cv.CONVENIO = c.id  and cv.eliminado = 'False')   \n"
                + "	 end "
                + " , ap.compania"
                + " , (select count(ccf.id) from cv_convenio_formas ccf where ccf.convenio = c.id and ccf.si_adjunto is not null and ccf.eliminado = false)\n"
                + " || '/' || (select count(f.id) from cv_formas f where f.eliminado = false) as archivos"
                + "  from CONVENIO c"
                + "		inner join PROVEEDOR p on c.PROVEEDOR = p.id"
                + "		inner join AP_CAMPO ap on ap.id = c.ap_campo"
                + "		left join MONEDA m on c.MONEDA= m.ID"
                + "		inner join CV_TIPO ct on c.CV_TIPO = ct.ID"
                + "		left join CV_CLASIFICACION cc on c.CV_CLASIFICACION = cc.id "
                + "		inner join ESTATUS e on c.ESTATUS = e.ID    ";
        return sb;
    }

    private String consultaContactos() {
        String sb = "select "
                + " c.id,c.codigo,c.nombre,c.convpadre,c.estatus,c.moneda,c.estatusn,c.fechainicio,c.fechavencimiento,c.monto,c.idgerencia,c.gerencian,c.rfcp,c.nombrep,c.direccion,c.contacto,c.correoc,c.celular,c.tipo,c.campo "
                + " from contactos_proveedores_con_convenio c ";
        return sb;
    }

    private String consultaTotal() {
        String sb = "select count(c.id)  from CONVENIO c where c.eliminado = 'False' ";
        return sb;
    }

    private ContratoVO castContratos(Object[] obj) {
        ContratoVO contratoVO = new ContratoVO();
        contratoVO.setId((Integer) obj[0]);
        contratoVO.setNumero(((String) obj[1]));
        contratoVO.setNombre((String) obj[2]);
        contratoVO.setNombreProveedor((String) obj[3]);
        contratoVO.setFechaFirma((Date) obj[4]);
        contratoVO.setFechaInicio((Date) obj[5]);
        contratoVO.setFechaVencimiento((Date) obj[6]);
        contratoVO.setVigencia((String) obj[7]);
        contratoVO.setPorcentajeDeduccion(obj[8] != null ? (Double) obj[8] : 0.0);
        contratoVO.setMonto(obj[9] != null ? (Double) obj[9] : 0.0);
        contratoVO.setMoneda((String) obj[10]);
        contratoVO.setTipo((String) obj[11]);
        contratoVO.setClasificacion((String) obj[12]);
        contratoVO.setEstado((String) obj[13]);
        contratoVO.setIdMoneda(obj[14] != null ? (Integer) obj[14] : 0);
        contratoVO.setIdTipo(obj[15] != null ? (Integer) obj[15] : 0);
        contratoVO.setIdClasificacion(obj[16] != null ? (Integer) obj[16] : 0);
        contratoVO.setIdEstado(obj[17] != null ? (Integer) obj[17] : 0);
        contratoVO.setIdContratoRelacionado(obj[18] != null ? (Integer) obj[18] : 0);
        contratoVO.setProveedor(obj[19] != null ? (Integer) obj[19] : 0);
        contratoVO.setIdCampo(obj[20] != null ? (Integer) obj[20] : 0);
        contratoVO.setEditar(contratoVO.getIdEstado() == Constantes.ESTADO_CONVENIO_REGISTRADO);
        contratoVO.setFormalizado(contratoVO.getIdEstado() == Constantes.ESTADO_CONVENIO_ACTIVO);

        double total = obj[21] != null ? (Double) obj[21] : 0;
        contratoVO.setTotalContratoConModificatorios(total);

        contratoVO.setCompania((String) obj[22]);
        contratoVO.setTotalFormas((String) obj[23]);

        String[] cadena = new String[5];
        if (contratoVO.getNumero().contains(".") || contratoVO.getNumero().contains(" ")
                || contratoVO.getNumero().contains("/") || contratoVO.getNumero().contains(",")
                || contratoVO.getNumero().contains("ª")) {
            cadena[0] = contratoVO.getNumero().substring(1, 3);
        } else {
            cadena[0] = contratoVO.getNumero();
        }
        contratoVO.setNombreTab(cadena[0]);
        //contratoVO.setListaArchivoConvenio(cvConvenioAdjuntoImpl.traerPorConvenio(contratoVO.getId()));
        return contratoVO;
    }

    private ContratoVO castContactos(Object[] obj, boolean conConts, boolean conRL, boolean conRT) {
        ContratoVO contratoVO = new ContratoVO();
        contratoVO.setId((Integer) obj[0]);
        contratoVO.setNumero(((String) obj[1]));
        contratoVO.setNombre((String) obj[2]);
        contratoVO.setNombreProveedor((String) obj[13]);
        contratoVO.getProveedorVo().setNombre((String) obj[13]);
        contratoVO.getProveedorVo().setRfc((String) obj[12]);
        contratoVO.getProveedorVo().setDireccion((String) obj[14]);
        contratoVO.setFechaInicio((Date) obj[7]);
        contratoVO.setFechaVencimiento((Date) obj[8]);
        contratoVO.setMonto(obj[9] != null ? (Double) obj[9] : 0.0);
        contratoVO.setGerencia((String) obj[11]);
        contratoVO.setIdGerencia(obj[10] != null ? (Integer) obj[10] : 0);
        contratoVO.setTipo((String) obj[18]);
        contratoVO.setEstado((String) obj[6]);
        contratoVO.setIdMoneda(obj[5] != null ? (Integer) obj[5] : 0);
        contratoVO.setIdEstado(obj[4] != null ? (Integer) obj[4] : 0);
        contratoVO.setIdContratoRelacionado(obj[3] != null ? (Integer) obj[3] : 0);
        contratoVO.setIdCampo(obj[19] != null ? (Integer) obj[19] : 0);

        if ("CO".equals(contratoVO.getTipo()) && conConts) {
            if (contratoVO.getProveedorVo() != null && contratoVO.getProveedorVo().getContactos() == null) {
                contratoVO.getProveedorVo().setContactos(new ArrayList<>());
            }
            contratoVO.getProveedorVo().getContactos().add(contratoVO.getProveedorVo().crearContacto((String) obj[15], (String) obj[16], (String) obj[17], "Contacto"));
        } else if ("RL".equals(contratoVO.getTipo()) && conRL) {
            if (contratoVO.getProveedorVo() != null && contratoVO.getProveedorVo().getContactos() == null) {
                contratoVO.getProveedorVo().setContactos(new ArrayList<>());
            }
            contratoVO.getProveedorVo().getContactos().add(contratoVO.getProveedorVo().crearContacto((String) obj[15], (String) obj[16], (String) obj[17], "Rep. Legal"));
        } else if ("RT".equals(contratoVO.getTipo()) && conRT) {
            if (contratoVO.getProveedorVo() != null && contratoVO.getProveedorVo().getContactos() == null) {
                contratoVO.getProveedorVo().setContactos(new ArrayList<>());
            }
            contratoVO.getProveedorVo().getContactos().add(contratoVO.getProveedorVo().crearContacto((String) obj[15], (String) obj[16], (String) obj[17], "Rep. Técnico"));
        } else {
            contratoVO = null;
        }

        return contratoVO;
    }

    
    public boolean actualizarDatosGenerales(String sesion, ContratoVO contratoVO, List<GerenciaVo> lg) {
        boolean v = true;
        try {
            Convenio convenio = find(contratoVO.getId());
            convenio.setNombre(contratoVO.getNombre());
            //convenio.setGerencia(new Gerencia(contratoVO.getIdGerencia()));
            //convenio.setCvClasificacion(new CvClasificacion(contratoVO.getIdClasificacion()));
            convenio.setCvTipo(new CvTipo(contratoVO.getIdTipo()));
            convenio.setModifico(new Usuario(sesion));
            convenio.setFechaModifico(new Date());
            convenio.setHoraModifico(new Date());

            if (convenio != null && Constantes.ESTADO_CONVENIO_ACTIVO == convenio.getEstatus().getId()
                    && convenio.getCodigo() != null
                    && contratoVO.getNumero() != null
                    && !convenio.getCodigo().equals(contratoVO.getNumero())) {
                convenio.setCodigo(contratoVO.getNumero());
            }

            edit(convenio);
            //
            for (GerenciaVo lg1 : lg) {
                cvConvenioGerenciaImpl.guardar(sesion, convenio.getId(), lg1.getId());
            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(e);
            v = false;
        }
        return v;
    }

    /**
     *
     * @param sesion
     * @param contratoVO // estado, firma, inicio, fin, monto moneda
     * @return
     */
    
    public boolean actualizar(String sesion, ContratoVO contratoVO) {
        boolean v = true;
        try {
            Convenio convenio = find(contratoVO.getId());
            convenio.setPorcentajeDeduccion(contratoVO.getPorcentajeDeduccion());
            //convenio.setEstatus(new Estatus(contratoVO.getIdEstado()));
            convenio.setFechaFirma(contratoVO.getFechaFirma());
            convenio.setFechaInicio(contratoVO.getFechaInicio());
            convenio.setFechaVencimiento(contratoVO.getFechaVencimiento());
            if (contratoVO.getFechaInicio() != null && contratoVO.getFechaVencimiento() != null) {
                convenio.setVigencia(siManejoFechaImpl.dias(contratoVO.getFechaVencimiento(), contratoVO.getFechaInicio()) + " días");
            }
            convenio.setMonto(contratoVO.getMonto());
            convenio.setMoneda(new Moneda(contratoVO.getIdMoneda()));
            convenio.setModifico(new Usuario(sesion));
            convenio.setFechaModifico(new Date());
            convenio.setHoraModifico(new Date());
            //
            if (convenio.getConvenio() != null) {
                Convenio c = find(convenio.getConvenio().getId());
                c.setEstatus(new Estatus(Constantes.ESTADO_CONVENIO_ACTIVO));
                c.setFechaVencimiento(convenio.getFechaVencimiento());
                c.setVigencia(siManejoFechaImpl.dias(convenio.getFechaVencimiento(), contratoVO.getFechaInicio()) + " días");
                c.setModifico(new Usuario(sesion));
                c.setFechaModifico(new Date());
                c.setHoraModifico(new Date());
                edit(c);
            }
            //
            edit(convenio);
        } catch (Exception e) {
            UtilLog4j.log.fatal(e);
            v = false;
        }
        return v;
    }

    
    public List<ContratoVO> contratosRelacionados(int idContratoPadre, int idContrato) {
        String sd = "	select  id, CODIGO, Objetivo, NOMBRE, FECHA_FIRMA, FECHA_INICIO, FECHA_VENCIMIENTO, VIGENCIA, ";
        sd += "	    PORCENTAJE_DEDUCCION, MONTO, SIGLAS, Tipo, Clasificacion,  Estatus, ";
        sd += "	    idMONEDA, idTipo, idClasificacion, idESTATUS, CONVENIO, idPROVEEDOR, idCampo";
        sd += "     , totalCons ";
        sd += "     , compania, archivos ";
        sd += " from ( \n";
        sd += "	    ";
        sd += "	    select  c.id, c.CODIGO, c.NOMBRE as Objetivo, p.NOMBRE, c.FECHA_FIRMA, c.FECHA_INICIO, c.FECHA_VENCIMIENTO, c.VIGENCIA, \n ";
        sd += "		    c.PORCENTAJE_DEDUCCION, c.MONTO, m.SIGLAS, ct.NOMBRE as Tipo,\n ";
        sd += "		    cc.NOMBRE as Clasificacion, e.NOMBRE as Estatus, \n ";
        sd += "		    m.id as idMoneda, ct.id as idTipo, cc.id as idClasificacion,  e.id as idESTATUS, \n ";
        sd += "		    c.CONVENIO as CONVENIO,  p.ID as idPROVEEDOR, c.ap_campo as idCampo \n ";
        sd += "          , case when c.CONVENIO is not null then	 	\n"
                + "	 	(SELECT sum(cv.MONTO) + (SELECT MONTO from CONVENIO where id = c.CONVENIO) from CONVENIO cv where cv.CONVENIO = c.CONVENIO)\n"
                + "	 else\n"
                + "	 	c.MONTO  +\n"
                + "	 	(SELECT sum(cv.MONTO) from CONVENIO cv where cv.CONVENIO = c.id)   \n"
                + "	 end as totalCons "
                + "      , ap.compania"
                + " , (select count(ccf.id) from cv_convenio_formas ccf where ccf.convenio = c.id and ccf.si_adjunto is not null and ccf.eliminado = false)\n"
                + " || '/' || (select count(f.id) from cv_formas f where f.eliminado = false) as archivos";
        sd += "	    from CONVENIO c\n ";
        sd += "		    inner join PROVEEDOR p on c.PROVEEDOR = p.ID\n ";
        sd += " 	    inner join AP_CAMPO ap on ap.id = c.ap_campo\n ";
        sd += "		    left join MONEDA m on c.MONEDA= m.ID\n ";
        sd += "		    inner join CV_TIPO ct on c.CV_TIPO = ct.ID\n ";
        sd += "		    left join CV_CLASIFICACION cc on c.CV_CLASIFICACION = cc.id \n ";
        sd += "		    inner join ESTATUS e on c.ESTATUS = e.ID   \n ";
        sd += "	    where  c.CONVENIO = " + idContratoPadre;
        sd += "	    and c.eliminado = 'False'";
        sd += "	    and c.eliminado = 'False'";
        sd += "	    union\n ";
        sd += "	    select  c.id, c.CODIGO, c.NOMBRE as Objetivo, p.NOMBRE, c.FECHA_FIRMA, c.FECHA_INICIO, c.FECHA_VENCIMIENTO, c.VIGENCIA, \n ";
        sd += "		    c.PORCENTAJE_DEDUCCION, c.MONTO, m.SIGLAS, ct.NOMBRE as Tipo, cc.NOMBRE as Clasificacion, e.NOMBRE as Estatus, \n ";
        sd += "		    m.id as idMoneda, ct.id as idTipo, cc.id as idClasificacion,  e.id as idESTATUS, c.CONVENIO as CONVENIO,  p.ID as idPROVEEDOR , c.ap_campo as idCampo\n ";
        sd += "      , case when c.CONVENIO is not null then	 	\n"
                + "	 	(SELECT sum(cv.MONTO) + (SELECT MONTO from CONVENIO where id = c.CONVENIO) from CONVENIO cv where cv.CONVENIO = c.CONVENIO)\n"
                + "	 else\n"
                + "	 	c.MONTO  +\n"
                + "	 	(SELECT sum(cv.MONTO) from CONVENIO cv where cv.CONVENIO = c.id)   \n"
                + "	 end as totalCons "
                + "      , ap.compania"
                + " , (select count(ccf.id) from cv_convenio_formas ccf where ccf.convenio = c.id and ccf.si_adjunto is not null and ccf.eliminado = false)\n"
                + " || '/' || (select count(f.id) from cv_formas f where f.eliminado = false) as archivos";;
        sd += "	    from CONVENIO c\n ";
        sd += "		    inner join PROVEEDOR p on c.PROVEEDOR = p.ID\n ";
        sd += " 	    inner join AP_CAMPO ap on ap.id = c.ap_campo\n ";
        sd += "		    left join MONEDA m on c.MONEDA= m.ID\n ";
        sd += "		    inner join CV_TIPO ct on c.CV_TIPO = ct.ID\n ";
        sd += "		    left join CV_CLASIFICACION cc on c.CV_CLASIFICACION = cc.id \n ";
        sd += "		    inner join ESTATUS e on c.ESTATUS = e.ID   \n ";
        sd += "	    where  c.ID = " + idContrato;
        sd += "	    and c.eliminado = 'False'";
        sd += "	    union \n";
        sd += "	    select  c.id, c.CODIGO, c.NOMBRE as Objetivo, p.NOMBRE, c.FECHA_FIRMA, c.FECHA_INICIO, c.FECHA_VENCIMIENTO, c.VIGENCIA, \n";
        sd += "		    c.PORCENTAJE_DEDUCCION, c.MONTO, m.SIGLAS, ct.NOMBRE as Tipo, cc.NOMBRE as Clasificacion, e.NOMBRE as Estatus,    \n";
        sd += "		    m.id as idMoneda, ct.id as idTipo, cc.id as idClasificacion, e.id as idESTATUS, c.CONVENIO as CONVENIO,  p.ID as idPROVEEDOR, c.ap_campo as idCampo \n";
        sd += "         ,case when c.CONVENIO is not null then	 	\n"
                + "	 	(SELECT sum(cv.MONTO) + (SELECT MONTO from CONVENIO where id = c.CONVENIO) from CONVENIO cv where cv.CONVENIO = c.CONVENIO)\n"
                + "	 else\n"
                + "	 	c.MONTO  +\n"
                + "	 	(SELECT sum(cv.MONTO) from CONVENIO cv where cv.CONVENIO = c.id)   \n"
                + "	 end as totalCons "
                + "      , ap.compania "
                + " , (select count(ccf.id) from cv_convenio_formas ccf where ccf.convenio = c.id and ccf.si_adjunto is not null and ccf.eliminado = false)\n"
                + " || '/' || (select count(f.id) from cv_formas f where f.eliminado = false) as archivos";
        sd += "	    from CONVENIO c \n";
        sd += "		    inner join PROVEEDOR p on c.PROVEEDOR = p.ID \n";
        sd += " 	    inner join AP_CAMPO ap on ap.id = c.ap_campo\n ";
        sd += "		    left join MONEDA m on c.MONEDA= m.ID \n";
        sd += "		    inner join CV_TIPO ct on c.CV_TIPO = ct.ID \n";
        sd += "		    left join CV_CLASIFICACION cc on c.CV_CLASIFICACION = cc.id \n";
        sd += "		    inner join ESTATUS e on c.ESTATUS = e.ID   \n";
        sd += "	    where  c.CONVENIO = " + idContrato;
        sd += "	    and c.eliminado = 'False'";
        sd += "	    union\n";
        sd += "	    select  v.id, v.CODIGO, v.NOMBRE as Objetivo, p.NOMBRE, v.FECHA_FIRMA, v.FECHA_INICIO, v.FECHA_VENCIMIENTO, v.VIGENCIA, \n";
        sd += "		    v.PORCENTAJE_DEDUCCION, v.MONTO, m.SIGLAS, ct.NOMBRE as Tipo, cc.NOMBRE as Clasificacion, e.NOMBRE as Estatus, \n";
        sd += "		    m.id as idMoneda, ct.id as idTipo, cc.id as idClasificacion,  e.id as idESTATUS, v.CONVENIO as CONVENIO,  p.ID as idPROVEEDOR, c.ap_campo as idCampo \n";
        sd += "           , case when c.CONVENIO is not null then	 	\n"
                + "	 	(SELECT sum(cv.MONTO) + (SELECT MONTO from CONVENIO where id = c.CONVENIO) from CONVENIO cv where cv.CONVENIO = c.CONVENIO)\n"
                + "	 else\n"
                + "	 	c.MONTO  +\n"
                + "	 	(SELECT sum(cv.MONTO) from CONVENIO cv where cv.CONVENIO = c.id)   \n"
                + "	 end as totalCons "
                + "      , ap.compania "
                + " , (select count(ccf.id) from cv_convenio_formas ccf where ccf.convenio = c.id and ccf.si_adjunto is not null and ccf.eliminado = false)\n"
                + " || '/' || (select count(f.id) from cv_formas f where f.eliminado = false) as archivos";
        sd += "	    from CONVENIO c \n";
        sd += "		    inner join CONVENIO v on v.id = c.CONVENIO and v.ELIMINADO = 'False' \n";
        sd += "		    inner join PROVEEDOR p on v.PROVEEDOR = p.ID \n";
        sd += " 	    inner join AP_CAMPO ap on ap.id = c.ap_campo\n ";
        sd += "		    left join MONEDA m on v.MONEDA= m.ID \n";
        sd += "		    inner join CV_TIPO ct on v.CV_TIPO = ct.ID \n";
        sd += "		    left join CV_CLASIFICACION cc on v.CV_CLASIFICACION = cc.id \n";
        sd += "		    inner join ESTATUS e on v.ESTATUS = e.ID \n";
        sd += "	    where  c.ID = " + idContrato;
        sd += "	    and c.eliminado = 'False'";
        sd += "	    \n) ";
        sd += "	    as convenios  order by  id ";
        List<Object[]> lo = em.createNativeQuery(sd).getResultList();
        List<ContratoVO> lista = null;
        if (lo != null) {
            lista = new ArrayList<>();
            for (Object[] obj : lo) {
                ContratoVO contratoVO = castContratos(obj);
                contratoVO.setListaArchivoConvenio(cvConvenioAdjuntoImpl.traerPorConvenio(contratoVO.getId()));
                lista.add(contratoVO);
            }
        }
        return lista;
    }

    
    public List<ContratoVO> traerConveniosPorProveedorGerencia(int idProveedor, int gerencia) {
        String sb = consulta();
        sb += "	where p.id = " + idProveedor;
        sb += "	and  " + gerencia + " in (select gerencia from cv_convenio_gerencia where eliminado = 'False'";
        sb += "	and c.eliminado = 'False'";
        sb += "	order by c.id desc";
//	System.out.println(" q :::  " + sb);
        List<Object[]> lo = em.createNativeQuery(sb).getResultList();
        List<ContratoVO> lc = null;
        if (lo != null) {
            lc = new ArrayList<>();
            for (Object[] ocObjects : lo) {
                lc.add(castContratos(ocObjects));
            }
        }
        return lc;
    }

    
    public List<ContratoVO> traerConveniosPorProveedorPermisos(int idProveedor, String usuario, int rol,
            double importe, int moneda, Date fecha, int idOperador, int campo, int maximoRegistros, int estado) {
        String sb = consulta();
        String op = "=";
        sb += "	where ";
        if (idProveedor > 0) {
            sb += " p.id  = " + idProveedor + " and ";
        }
        sb += permisos(usuario, campo);

        if (idOperador > 0) {
            switch (idOperador) {
                case 1:
                    op = "=";
                    break;
                case 2:
                    op = "<";
                    break;
                case 3:
                    op = ">";
                    break;
            }
            sb += " and  c.monto " + op + importe;
        }

        if (moneda > 0) {
            sb += " and  c.moneda = " + moneda;
        }

        if (fecha != null) {
            sb += " and c.fecha_vencimiento = between " + siManejoFechaImpl.convertirFechaStringyyyyMMdd(siManejoFechaImpl.fechaRestarDias(fecha, 10)) + " and " + siManejoFechaImpl.convertirFechaStringyyyyMMdd(siManejoFechaImpl.fechaSumarMes(fecha, 10));
        }
        if (estado > Constantes.CERO) {
            sb += " and  c.estatus = " + estado;
        }
        sb += "	and c.ap_campo = " + campo;
        sb += "	and c.eliminado = 'False'";
        sb += "	order by c.id desc ";

        if (maximoRegistros > 0) {
            sb += " limit " + maximoRegistros + " ";
        }

//	System.out.println(" q :::  " + sb);
        List<Object[]> lo = em.createNativeQuery(sb).getResultList();
        List<ContratoVO> lc = null;
        if (lo != null) {
            lc = new ArrayList<>();
            for (Object[] ocObjects : lo) {
                lc.add(castContratos(ocObjects));
            }
        }
        return lc;
    }

    private String permisos(String usuario, int campo) {
        String sb = "";
        if (siUsuarioRolRemote.buscarRolPorUsuarioModulo(usuario, Constantes.MODULO_CONTRATO, Constantes.COD_CONVENIO, campo)) {
            sb += " 1 = 1";
        } else {
            if (siUsuarioRolRemote.buscarRolPorUsuarioModulo(usuario, Constantes.MODULO_CONTRATO, Constantes.COD_ROL_CONS_ADMIN_CONV, campo)
                    || siUsuarioRolRemote.buscarRolPorUsuarioModulo(usuario, Constantes.MODULO_COMPRA, Constantes.COD_ROL_COMPRADOR, campo)) {
                sb += " 1 = 1";
            } else {
                List<ApCampoGerenciaVo> lg = apCampoGerenciaRemote.buscarCampoGerencia(usuario, campo);
                if (lg != null && !lg.isEmpty()) {
                    String idGer = "";
                    for (ApCampoGerenciaVo lg1 : lg) {
                        if (idGer.isEmpty()) {
                            idGer = "" + lg1.getIdGerencia();
                        } else {
                            idGer += "," + lg1.getIdGerencia();
                        }
                    }
                    sb += " c.id in (select convenio from cv_convenio_gerencia where gerencia in (" + idGer + ") and  eliminado = 'False' and c.ap_campo = " + campo + ")";
                    sb += " or c.id in (select convenio from cv_convenio_usuario where eliminado = 'False' and usuario = '" + usuario + "')";
                } else {
                    sb += " 1 = 1";
                    sb += " and  c.id in (select convenio from cv_convenio_usuario where eliminado = 'False' and usuario = '" + usuario + "')";
                }
            }
            sb += " and c.eliminado = 'False'";
            sb += " and c.estatus > " + Constantes.ESTADO_CONVENIO_REGISTRADO;
            // consulta la tabla nueva de usuario contratos
        }
        return sb;
    }

    private String permisosContactos(String usuario, int campo) {
        String sb = "";
        if (siUsuarioRolRemote.buscarRolPorUsuarioModulo(usuario, Constantes.MODULO_CONTRATO, Constantes.COD_CONVENIO, campo)) {
            sb += " 1 = 1";
        } else {
            if (siUsuarioRolRemote.buscarRolPorUsuarioModulo(usuario, Constantes.MODULO_CONTRATO, Constantes.COD_ROL_CONS_ADMIN_CONV, campo)
                    || siUsuarioRolRemote.buscarRolPorUsuarioModulo(usuario, Constantes.MODULO_COMPRA, Constantes.COD_ROL_COMPRADOR, campo)) {
                sb += " 1 = 1";
            } else {
                List<ApCampoGerenciaVo> lg = apCampoGerenciaRemote.buscarCampoGerencia(usuario, campo);
                if (lg != null && !lg.isEmpty()) {
                    String idGer = "";
                    for (ApCampoGerenciaVo lg1 : lg) {
                        if (idGer.isEmpty()) {
                            idGer = "" + lg1.getIdGerencia();
                        } else {
                            idGer += "," + lg1.getIdGerencia();
                        }
                    }
                    sb += " c.id in (select convenio from cv_convenio_gerencia where gerencia in (" + idGer + ") and  eliminado = 'False' and c.ap_campo = " + campo + ")";
                    sb += " or c.id in (select convenio from cv_convenio_usuario where eliminado = 'False' and usuario = '" + usuario + "')";
                } else {
                    sb += " 1 = 1";
                    sb += " and  c.id in (select convenio from cv_convenio_usuario where eliminado = 'False' and usuario = '" + usuario + "')";
                }
            }
            //sb += " and c.eliminado = 'False'";
            sb += " and c.estatus > " + Constantes.ESTADO_CONVENIO_REGISTRADO;
            // consulta la tabla nueva de usuario contratos
        }
        return sb;
    }

    
    public List<ContratoVO> traerConveniosPorParteDeCodigo(String codigo) {
        String sb = consulta();
        sb += "	where c.codigo like  '" + codigo + "%'";
        sb += "	and c.eliminado = 'False'";
        sb += "	order by c.id asc";
//	System.out.println(" q :::  " + sb);
        List<Object[]> lo = em.createNativeQuery(sb).getResultList();
        List<ContratoVO> lc = null;
        if (lo != null) {
            lc = new ArrayList<>();
            for (Object[] ocObjects : lo) {
                lc.add(castContratos(ocObjects));
            }
        }
        return lc;
    }

    
    public ContratoVO traerConveniosPorCodigo(String codigo) {
        String sb = consulta();
        try {
            sb += "	where  c.eliminado = false and replace(c.CODIGO, ' ', '') = '" + codigo.replace(" ", "") + "'";
            //System.out.println(" q :::  " + sb);
            Object[] lo = (Object[]) em.createNativeQuery(sb).getSingleResult();

            return castContratos(lo);
        } catch (Exception e) {
            UtilLog4j.log.error(e);
            return null;
        }
    }

    
    public List<ContratoVO> buscarConvenioPorUsuario(String usuario, int campo) {
        String sb = consulta();
        List<ApCampoGerenciaVo> lg = apCampoGerenciaRemote.buscarCampoGerencia(usuario, campo);
        if (lg != null && !lg.isEmpty()) {
            String idGer = "";
            for (ApCampoGerenciaVo lg1 : lg) {
                if (idGer.isEmpty()) {
                    idGer = "" + lg1.getIdGerencia();
                } else {
                    idGer += "," + lg1.getIdGerencia();
                }

            }
            sb += " c.id in (select convenio from cv_convenio_gerencia where gerencia in (" + idGer + ") and  eliminado = 'False')";
        } else {
            sb += " 1 = 1";
        }
        sb += "	and c.ap_campo = " + campo;
        sb += "	and c.eliminado = 'False'";
        sb += "	order by c.id desc";
//	System.out.println(" q :::  " + sb);
        List<Object[]> lo = em.createNativeQuery(sb).getResultList();
        List<ContratoVO> lc = null;
        if (lo != null) {
            lc = new ArrayList<>();
            for (Object[] ocObjects : lo) {
                lc.add(castContratos(ocObjects));
            }
        }
        return lc;
    }

    
    public List<ProveedorVo> traerProveedoresPorUsuario(String usuario, int rol, int campo) {
        String sb = "select distinct(p.ID), p.RFC, p.NOMBRE from PROVEEDOR p ";
        sb += "	    inner join CONVENIO c on c.PROVEEDOR = p.ID";
        List<ApCampoGerenciaVo> lg = apCampoGerenciaRemote.buscarCampoGerencia(usuario, campo);
        if (lg != null && !lg.isEmpty()) {
            String idGer = "";
            for (ApCampoGerenciaVo lg1 : lg) {
                if (idGer.isEmpty()) {
                    idGer = "" + lg1.getIdGerencia();
                } else {
                    idGer += "," + lg1.getIdGerencia();
                }

            }
            sb += " c.id in (select convenio from cv_convenio_gerencia where gerencia in (" + idGer + ") and  eliminado = 'False')";
        } else {
            sb += " 1 = 1";
        }

        sb += "	  and c.ap_campo = " + campo;
        sb += "	  and c.eliminado = 'False'";
        sb += "	  order by p.nombre asc ";
        // q:
        //System.out.println("proveedore por usuario :: : " + sb);
        List<Object[]> lo = em.createNativeQuery(sb).getResultList();
        List<ProveedorVo> lp = new ArrayList<ProveedorVo>();
        for (Object[] lo1 : lo) {
            ProveedorVo proveedorVo = new ProveedorVo();
            proveedorVo.setIdProveedor((Integer) lo1[0]);
            proveedorVo.setRfc((String) lo1[1]);
            proveedorVo.setNombre((String) lo1[2]);
            lp.add(proveedorVo);
        }
        return lp;
    }

    /**
     *
     * @param dias
     * @param campo
     * @return
     */
    
    public List<ContratoVO> buscarConvenioNotificarDias(int dias, int campo) {
        String sb = "select c.id , c.CODIGO, c.NOMBRE, p.nombre, c.MONTO, m.siglas, c.fecha_vencimiento from convenio c ";
        sb += "	    inner join PROVEEDOR p on c.proveedor = p.ID ";
        sb += "	    inner join moneda m on c.MONEDA = m.id";
        sb += "	 where c.FECHA_VENCIMIENTO between cast('now' as date) and dateadd(" + dias + " day to  cast('now' as date))"
                + " and c.ap_campo = " + campo;
        sb += "	 and c.eliminado = 'False'";
        sb += "	 order by c.fecha_vencimiento asc";
        //
        List<Object[]> lo = em.createNativeQuery(sb).getResultList();
        List<ContratoVO> lc = new ArrayList<>();
        for (Object[] lo1 : lo) {
            ContratoVO cvo = new ContratoVO();
            cvo.setId((Integer) lo1[0]);
            cvo.setNumero((String) lo1[1]);
            cvo.setNombre((String) lo1[2]);
            cvo.setNombreProveedor((String) lo1[3]);
            cvo.setMonto(lo1[4] != null ? (Double) lo1[4] : 0.0);
            cvo.setMoneda((String) lo1[5]);
            cvo.setFechaVencimiento((Date) lo1[6]);
            lc.add(cvo);
        }
        return lc;
    }

    
    public void notificarConvenioPorVencer() {
        List<ApCampoVo> listCampo = apCampoRemote.traerApCampo();

        if (listCampo != null && !listCampo.isEmpty()) {

            for (ApCampoVo campoVo : listCampo) {

                List<ContratoVO> lista
                        = buscarConvenioNotificarDias(
                                Constantes.TREINTA_DIAS_ANTICIPADOS,
                                campoVo.getId()
                        );

                if (lista != null && !lista.isEmpty()) {

                    notificacionConvenioImpl.notificacionConvenioPorVencer(
                            correoPorRol(Constantes.ROL_ADMINISTRA_CONTRATO, campoVo.getId()),
                            correoPorGerencia(Constantes.GERENCIA_ID_COMPRAS, campoVo.getId()),
                            "",
                            "Convenios por vencer " + siManejoFechaImpl.convertirFechaStringddMMyyyy(new Date()),
                            lista
                    );
                }
            }
        }
    }

    private String correoPorRol(int rol, int campo) {
        String correo = "";
        for (UsuarioRolVo urvo : siUsuarioRolRemote.traerUsuarioPorRolModulo(rol, Constantes.MODULO_CONTRATO, campo)) {
            if (correo.isEmpty()) {
                correo = urvo.getCorreo();
            } else {
                correo += "," + urvo.getCorreo();
            }
        }
        return correo;
    }

    private String correoPorGerencia(int gerencia, int campo) {
        String correo = "";
        UsuarioResponsableGerenciaVo cgvo = apCampoGerenciaRemote.buscarResponsablePorGerencia(gerencia, campo);
        if (cgvo != null) {
            correo = cgvo.getEmailUsuario();
        }
        return correo;
    }

    
    public List<ContratoVO> buscarConvenioFechaVencimiento(Date fechaVencimiento, int idCampo) {
        String sb = "select c.id , c.CODIGO, c.NOMBRE, p.nombre, c.MONTO, m.siglas, c.fecha_vencimiento from convenio c ";
        sb += "	    inner join PROVEEDOR p on c.proveedor = p.ID ";
        sb += "	    inner join moneda m on c.MONEDA = m.id";
        sb += "	 where c.FECHA_VENCIMIENTO = cast('" + siManejoFechaImpl.convertirFechaStringyyyyMMdd(fechaVencimiento) + "' as date)";
        sb += "  and c.ap_campo = " + idCampo;
        sb += "	 and c.eliminado = 'False'";
        sb += "	 order by c.fecha_vencimiento asc";
        //
        List<Object[]> lo = em.createNativeQuery(sb).getResultList();
        List<ContratoVO> lc = new ArrayList<>();
        for (Object[] lo1 : lo) {
            ContratoVO cvo = new ContratoVO();
            cvo.setId((Integer) lo1[0]);
            cvo.setNumero((String) lo1[1]);
            cvo.setNombre((String) lo1[2]);
            cvo.setNombreProveedor((String) lo1[3]);
            cvo.setMonto(lo1[4] != null ? (Double) lo1[4] : 0.0);
            cvo.setMoneda((String) lo1[5]);
            cvo.setFechaVencimiento((Date) lo1[6]);
            lc.add(cvo);
        }
        return lc;
    }

    
    public void notificarVencimiento(int campo) {
        List<ContratoVO> lc = buscarConvenioFechaVencimiento(new Date(), campo);
        if (lc != null && !lc.isEmpty()) {
            notificacionConvenioImpl.notificacionConvenioVencidos(
                    correoPorRol(Constantes.ROL_ADMINISTRA_CONTRATO, campo),
                    correoPorGerencia(Constantes.GERENCIA_ID_COMPRAS, campo),
                    "",
                    "Contratos vencidos - " + siManejoFechaImpl.convertirFechaStringddMMyyyy(new Date()),
                    lc
            );
            //
            for (ContratoVO lc1 : lc) {
                Convenio convenio = find(lc1.getId());
                convenio.setEstatus(new Estatus(Constantes.ESTADO_CONVENIO_VENCIDO));
                convenio.setModifico(new Usuario(Constantes.USUARIO_SIA));
                convenio.setFechaModifico(new Date());
                convenio.setHoraModifico(new Date());
                edit(convenio);
            }
        }

    }

    
    public List<ContratoVO> traerConveniosPorGerencia(int gerencia, int campo) {
        String sb = consulta();
        sb += "where " + gerencia + " in (select gerencia from cv_convenio_gerencia where eliminado = 'False')";
        sb += "	and c.ap_campo = " + campo;
        sb += "	and c.eliminado = 'False'";
        sb += "	order by c.id desc";
        //System.out.println(" q :::  " + sb);
        List<Object[]> lo = em.createNativeQuery(sb).getResultList();
        List<ContratoVO> lc = null;
        if (lo != null) {
            lc = new ArrayList<>();
            for (Object[] ocObjects : lo) {
                lc.add(castContratos(ocObjects));
            }
        }
        return lc;
    }

    
    public List<ContratoVO> traerContratosBusqueda(List<FiltroVo> filtroVos, String usuario, int rol, int campo) {
        String sb = consulta();
        sb += "	where ";
        sb += parametros(filtroVos, campo);
        sb += " and ";
        sb += permisos(usuario, campo);
        sb += " and c.eliminado = 'False'";
        //.out.println(" q :::  " + sb);
        List<Object[]> lo = em.createNativeQuery(sb).getResultList();
        List<ContratoVO> lc = null;
        if (lo != null) {
            lc = new ArrayList<>();
            for (Object[] ocObjects : lo) {
                lc.add(castContratos(ocObjects));
            }
        }
        return lc;

    }

    
    public List<ContratoVO> traerContratosBusquedaContactos(List<FiltroVo> filtroVos, String usuario, int rol, int campo,
             boolean conConts, boolean conRL, boolean conRT) {
        String sb = consultaContactos();
        sb += "	where ";
        sb += parametrosContactos(filtroVos, campo);
        sb += " and ";
        sb += permisosContactos(usuario, campo);
        //sb += " and c.eliminado = 'False'";
        //.out.println(" q :::  " + sb);
        List<Object[]> lo = em.createNativeQuery(sb).getResultList();
        List<ContratoVO> lc = null;
        if (lo != null) {
            lc = new ArrayList<>();
            for (Object[] ocObjects : lo) {
                ContratoVO vo = castContactos(ocObjects, conConts, conRL, conRT);
                if (vo != null) {
                    lc.add(vo);
                }
            }
        }
        return lc;

    }

    private String parametros(List<FiltroVo> filtros, int campo) {
        String q = "";
        for (FiltroVo filtro : filtros) {
            if (filtro.getId() == 0) {
                if (filtro.getCampoSeleccionado() != null && !filtro.getCampoSeleccionado().isEmpty() && filtro.getCampoSeleccionado().equals("Proveedor")) {
                    q = "  upper(p.NOMBRE COLLATE \"es_ES\") like upper('%" + filtro.getAlcance().trim() + "%')";
                } else if (filtro.getCampoSeleccionado().equals("Estado")) {
                    q = " c.ESTATUS  = " + filtro.getIdEstado();
                } else if (filtro.getCampoSeleccionado().equals("Gerencia")) {
                    q = " c.id  in (select convenio from cv_convenio_gerencia where gerencia = " + filtro.getIdGerencia() + ")";
                } else if (filtro.getCampoSeleccionado().equals("Monto")) {
                    q = " c.monto   " + castOperadorRelacional(filtro.getOperadorRelacionalSeleccionado()) + Integer.parseInt(filtro.getAlcance()) + "";
                    if (filtro.getIdMoneda() > 0) {
                        q += " and c.MONEDA   = " + filtro.getIdMoneda() + "";
                    }

                } else if (filtro.getCampoSeleccionado().equals("Vencimiento")) {
                    if (filtro.getOperadorRelacionalSeleccionado().trim().equals("Entre")) {
                        q += " c.fecha_vencimiento " + castOperadorRelacional(filtro.getOperadorRelacionalSeleccionado()) + " cast('" + siManejoFechaImpl.convertirFechaStringyyyyMMdd(filtro.getFechaInicio()) + "' as date) and cast('" + siManejoFechaImpl.convertirFechaStringyyyyMMdd(filtro.getFechaFin()) + "' as date)";
                    } else {
                        q += " c.fecha_vencimiento " + castOperadorRelacional(filtro.getOperadorRelacionalSeleccionado()) + " cast('" + siManejoFechaImpl.convertirFechaStringyyyyMMdd(filtro.getFecha()) + "' as date)";

                    }
                } else if (filtro.getCampoSeleccionado().equals("Número")) {
                    q = " upper(c.codigo COLLATE \"es_ES\") like upper('%" + filtro.getAlcance().trim() + "%')";
                } else if (filtro.getCampoSeleccionado().equals("Nombre")) {
                    q = " upper(c.NOMBRE COLLATE \"es_ES\") like upper('%" + filtro.getAlcance().trim() + "%')";
                }
            } else if (filtro.getCampoSeleccionado() != null && filtro.getId() > 0 && !filtro.getCampoSeleccionado().isEmpty()) {
                if (filtro.getCampoSeleccionado().equals("Proveedor")) {
                    if (filtro.getOperadorLogicoSeleccionado().trim().equals("O")) {
                        q += " or upper(p.NOMBRE COLLATE \"es_ES\") like upper('%" + filtro.getAlcance().trim() + "%')";
                    } else {
                        q += " and upper(p.NOMBRE COLLATE \"es_ES\") like upper('%" + filtro.getAlcance().trim() + "%')";
                    }
                } else if (filtro.getCampoSeleccionado().equals("Estado")) {
                    if (filtro.getOperadorLogicoSeleccionado().trim().equals("O")) {
                        q += " or c.ESTATUS  = " + filtro.getIdEstado();
                    } else {
                        q += " and c.ESTATUS  = " + filtro.getIdEstado();
                    }
                } else if (filtro.getCampoSeleccionado().equals("Gerencia")) {
                    if (filtro.getOperadorLogicoSeleccionado().trim().equals("O")) {
                        q += " or c.convenio  in (select convenio from cv_convenio_gerencia where gerencia = " + filtro.getIdGerencia() + ")";
                    } else {
                        q += " and c.convenio  in (select convenio from cv_convenio_gerencia where gerencia = " + filtro.getIdGerencia() + ")";
                    }
                } else if (filtro.getCampoSeleccionado().equals("Monto")) {
                    if (filtro.getOperadorLogicoSeleccionado().trim().equals("O")) {
                        q += " or c.monto   " + castOperadorRelacional(filtro.getOperadorRelacionalSeleccionado()) + filtro.getAlcance() + "";
                        if (filtro.getIdMoneda() > 0) {
                            q += " and c.MONEDA   = " + filtro.getIdMoneda() + "";
                        }
                    } else {
                        q += " and c.monto   " + castOperadorRelacional(filtro.getOperadorRelacionalSeleccionado()) + filtro.getAlcance() + "";
                        if (filtro.getIdMoneda() > 0) {
                            q += " and c.MONEDA   = " + filtro.getIdMoneda() + "";
                        }

                    }
                } else if (filtro.getCampoSeleccionado().equals("Vencimiento")) {
                    if (filtro.getOperadorRelacionalSeleccionado().trim().equals("Entre")) {
                        if (filtro.getOperadorLogicoSeleccionado().trim().equals("O")) {
                            q += " or c.fecha_vencimiento " + castOperadorRelacional(filtro.getOperadorRelacionalSeleccionado()) + "cast('" + siManejoFechaImpl.convertirFechaStringyyyyMMdd(filtro.getFechaInicio()) + "' as date) and cast('" + siManejoFechaImpl.convertirFechaStringyyyyMMdd(filtro.getFechaFin()) + "' as date)";
                        } else {
                            q += " and c.fecha_vencimiento " + castOperadorRelacional(filtro.getOperadorRelacionalSeleccionado()) + "cast('" + siManejoFechaImpl.convertirFechaStringyyyyMMdd(filtro.getFechaInicio()) + "' as date) and cast('" + siManejoFechaImpl.convertirFechaStringyyyyMMdd(filtro.getFechaFin()) + "' as date)";
                        }
                    } else {
                        if (filtro.getOperadorLogicoSeleccionado().trim().equals("O")) {
                            q += " or c.fecha_vencimiento " + castOperadorRelacional(filtro.getOperadorRelacionalSeleccionado()) + "cast('" + siManejoFechaImpl.convertirFechaStringyyyyMMdd(filtro.getFecha()) + "' as date)";
                        } else {
                            q += " and c.fecha_vencimiento " + castOperadorRelacional(filtro.getOperadorRelacionalSeleccionado()) + "cast('" + siManejoFechaImpl.convertirFechaStringyyyyMMdd(filtro.getFecha()) + "' as date)";
                        }
                    }
                } else if (filtro.getCampoSeleccionado().equals("Número")) {
                    if (filtro.getOperadorLogicoSeleccionado().trim().equals("O")) {
                        q += " or upper(c.codigo COLLATE \"es_ES\") like upper('%" + filtro.getAlcance().trim() + "%')";
                    } else {
                        q += " and upper(c.codigo COLLATE \"es_ES\") like upper('%" + filtro.getAlcance().trim() + "%')";
                    }
                } else if (filtro.getCampoSeleccionado().equals("Nombre")) {
                    if (filtro.getOperadorLogicoSeleccionado().trim().equals("O")) {
                        q += " or upper(c.NOMBRE COLLATE \"es_ES\") like upper('%" + filtro.getAlcance().trim() + "%')";
                    } else {
                        q += " and upper(c.NOMBRE COLLATE \"es_ES\") like upper('%" + filtro.getAlcance().trim() + "%')";
                    }
                }
            }
        }
        q += " and c.ap_campo = " + campo;
        return q;
    }

    private String parametrosContactos(List<FiltroVo> filtros, int campo) {
        String q = "";
        for (FiltroVo filtro : filtros) {
            if (filtro.getId() == 0) {
                if (filtro.getCampoSeleccionado() != null && !filtro.getCampoSeleccionado().isEmpty() && filtro.getCampoSeleccionado().equals("Proveedor")) {
                    q = "  upper(c.nombrep COLLATE \"es_ES\") like upper('%" + filtro.getAlcance().trim() + "%')";
                } else if (filtro.getCampoSeleccionado().equals("Estado")) {
                    q = " c.estatus  = " + filtro.getIdEstado();
                } else if (filtro.getCampoSeleccionado().equals("Gerencia")) {
                    q = " c.Id  in (select convenio from cv_convenio_gerencia where gerencia = " + filtro.getIdGerencia() + ")";
                } else if (filtro.getCampoSeleccionado().equals("Monto")) {
                    q = " c.monto   " + castOperadorRelacional(filtro.getOperadorRelacionalSeleccionado()) + Integer.parseInt(filtro.getAlcance()) + "";
                    if (filtro.getIdMoneda() > 0) {
                        q += " and c.moneda   = " + filtro.getIdMoneda() + "";
                    }

                } else if (filtro.getCampoSeleccionado().equals("Vencimiento")) {
                    if (filtro.getOperadorRelacionalSeleccionado().trim().equals("Entre")) {
                        q += " c.fechavencimiento " + castOperadorRelacional(filtro.getOperadorRelacionalSeleccionado()) + " cast('" + siManejoFechaImpl.convertirFechaStringyyyyMMdd(filtro.getFechaInicio()) + "' as date) and cast('" + siManejoFechaImpl.convertirFechaStringyyyyMMdd(filtro.getFechaFin()) + "' as date)";
                    } else {
                        q += " c.fechavencimiento " + castOperadorRelacional(filtro.getOperadorRelacionalSeleccionado()) + " cast('" + siManejoFechaImpl.convertirFechaStringyyyyMMdd(filtro.getFecha()) + "' as date)";

                    }
                } else if (filtro.getCampoSeleccionado().equals("Número")) {
                    q = " upper(c.codigo COLLATE \"es_ES\") like upper('%" + filtro.getAlcance().trim() + "%')";
                } else if (filtro.getCampoSeleccionado().equals("Nombre")) {
                    q = " upper(c.nombre COLLATE \"es_ES\") like upper('%" + filtro.getAlcance().trim() + "%')";
                }
            } else if (filtro.getCampoSeleccionado() != null && filtro.getId() > 0 && !filtro.getCampoSeleccionado().isEmpty()) {
                if (filtro.getCampoSeleccionado().equals("Proveedor")) {
                    if (filtro.getOperadorLogicoSeleccionado().trim().equals("O")) {
                        q += " or upper(c.nombrep COLLATE \"es_ES\") like upper('%" + filtro.getAlcance().trim() + "%')";
                    } else {
                        q += " and upper(c.nombrep COLLATE \"es_ES\") like upper('%" + filtro.getAlcance().trim() + "%')";
                    }
                } else if (filtro.getCampoSeleccionado().equals("Estado")) {
                    if (filtro.getOperadorLogicoSeleccionado().trim().equals("O")) {
                        q += " or c.estatus  = " + filtro.getIdEstado();
                    } else {
                        q += " and c.estatus  = " + filtro.getIdEstado();
                    }
                } else if (filtro.getCampoSeleccionado().equals("Gerencia")) {
                    if (filtro.getOperadorLogicoSeleccionado().trim().equals("O")) {
                        q += " or c.convpadre  in (select convenio from cv_convenio_gerencia where gerencia = " + filtro.getIdGerencia() + ")";
                    } else {
                        q += " and c.convpadre  in (select convenio from cv_convenio_gerencia where gerencia = " + filtro.getIdGerencia() + ")";
                    }
                } else if (filtro.getCampoSeleccionado().equals("Monto")) {
                    if (filtro.getOperadorLogicoSeleccionado().trim().equals("O")) {
                        q += " or c.monto   " + castOperadorRelacional(filtro.getOperadorRelacionalSeleccionado()) + filtro.getAlcance() + "";
                        if (filtro.getIdMoneda() > 0) {
                            q += " and c.moneda   = " + filtro.getIdMoneda() + "";
                        }
                    } else {
                        q += " and c.monto   " + castOperadorRelacional(filtro.getOperadorRelacionalSeleccionado()) + filtro.getAlcance() + "";
                        if (filtro.getIdMoneda() > 0) {
                            q += " and c.moneda   = " + filtro.getIdMoneda() + "";
                        }

                    }
                } else if (filtro.getCampoSeleccionado().equals("Vencimiento")) {
                    if (filtro.getOperadorRelacionalSeleccionado().trim().equals("Entre")) {
                        if (filtro.getOperadorLogicoSeleccionado().trim().equals("O")) {
                            q += " or c.fechavencimiento " + castOperadorRelacional(filtro.getOperadorRelacionalSeleccionado()) + "cast('" + siManejoFechaImpl.convertirFechaStringyyyyMMdd(filtro.getFechaInicio()) + "' as date) and cast('" + siManejoFechaImpl.convertirFechaStringyyyyMMdd(filtro.getFechaFin()) + "' as date)";
                        } else {
                            q += " and c.fechavencimiento " + castOperadorRelacional(filtro.getOperadorRelacionalSeleccionado()) + "cast('" + siManejoFechaImpl.convertirFechaStringyyyyMMdd(filtro.getFechaInicio()) + "' as date) and cast('" + siManejoFechaImpl.convertirFechaStringyyyyMMdd(filtro.getFechaFin()) + "' as date)";
                        }
                    } else {
                        if (filtro.getOperadorLogicoSeleccionado().trim().equals("O")) {
                            q += " or c.fechavencimiento " + castOperadorRelacional(filtro.getOperadorRelacionalSeleccionado()) + "cast('" + siManejoFechaImpl.convertirFechaStringyyyyMMdd(filtro.getFecha()) + "' as date)";
                        } else {
                            q += " and c.fechavencimiento " + castOperadorRelacional(filtro.getOperadorRelacionalSeleccionado()) + "cast('" + siManejoFechaImpl.convertirFechaStringyyyyMMdd(filtro.getFecha()) + "' as date)";
                        }
                    }
                } else if (filtro.getCampoSeleccionado().equals("Número")) {
                    if (filtro.getOperadorLogicoSeleccionado().trim().equals("O")) {
                        q += " or upper(c.codigo COLLATE \"es_ES\") like upper('%" + filtro.getAlcance().trim() + "%')";
                    } else {
                        q += " and upper(c.codigo COLLATE \"es_ES\") like upper('%" + filtro.getAlcance().trim() + "%')";
                    }
                } else if (filtro.getCampoSeleccionado().equals("Nombre")) {
                    if (filtro.getOperadorLogicoSeleccionado().trim().equals("O")) {
                        q += " or upper(c.nombre COLLATE \"es_ES\") like upper('%" + filtro.getAlcance().trim() + "%')";
                    } else {
                        q += " and upper(c.nombre COLLATE \"es_ES\") like upper('%" + filtro.getAlcance().trim() + "%')";
                    }
                }
            }
        }
        q += " and c.campo = " + campo;
        return q;
    }

    private String castOperadorRelacional(String operador) {
        if (operador.equals("Igual a ")) {
            return " = ";
        } else if (operador.equals("Menor a ")) {
            return " < ";
        } else if (operador.equals("Menor igual a ")) {
            return " <= ";
        } else if (operador.equals("Mayor  a ")) {
            return " > ";
        } else if (operador.equals("Mayor igual a ")) {
            return " >= ";
        } else {
            return " between ";
        }
    }

    
    public List<ContratoVO> consultaOCSPorConvenio(String codigo, int estado) {
        String sb = "select c.CODIGO, count(o.id), extract(month from o.FECHA)::bigint,";
        sb += "	    extract(year from o.FECHA)::bigint, c.FECHA_INICIO, c.FECHA_VENCIMIENTO, c.MONTO,"
                + " round(sum(o.subtotal)::numeric,2) from convenio c ";
        sb += "		    inner join orden o on o.CONTRATO  = c.CODIGO";
        sb += "		    inner join AUTORIZACIONES_ORDEN ao on ao.ORDEN = o.ID";
        sb += "	    where c.CODIGO ='" + codigo + "'";
        sb += "	    and ao.ESTATUS > " + estado;
        sb += "	    group by c.CODIGO, extract(month from o.FECHA)::bigint, ";
        sb += "	    extract(year from o.FECHA)::bigint, c.FECHA_INICIO, c.FECHA_VENCIMIENTO, c.MONTO";
        sb += "	    order by extract(year from o.FECHA)::bigint, extract(month from o.FECHA)::bigint";
        List<Object[]> lo = em.createNativeQuery(sb).getResultList();
        //
        List<ContratoVO> lc = new ArrayList<>();
        for (Object[] lo1 : lo) {
            ContratoVO contratoVO = new ContratoVO();
            contratoVO.setNombre((String) lo1[0]);
            contratoVO.setTotalOCS((Long) lo1[1]);
            contratoVO.setMes((Long) lo1[2]);
            contratoVO.setAnio((Long) lo1[3]);
            contratoVO.setFechaInicio((Date) lo1[4]);
            contratoVO.setFechaVencimiento((Date) lo1[5]);
            contratoVO.setMonto((Double) lo1[6]);
            contratoVO.setTotalMes(((BigDecimal) lo1[7]).doubleValue());
            contratoVO.setAcumulado(contratoVO.getTotalMes() + contratoVO.getAcumulado());
            lc.add(contratoVO);
        }
        return lc;
    }

    
    public List<ContratoVO> consultaOCSPorConvenioDet(String codigo, int estado) {
        String sb = "select c.CODIGO, count(o.id), extract(month from o.FECHA)::bigint, ";
        sb += "	    extract(year from o.FECHA)::bigint, c.FECHA_INICIO, c.FECHA_VENCIMIENTO, c.MONTO, "
                + " round(sum(o.subtotal)::numeric,2) ";
        sb += "		    from convenio c ";
        sb += "		    left join orden_detalle d on d.eliminado = false and d.convenio_codigo =  c.codigo ";
        sb += "		    left join orden o on o.id = d.orden and o.eliminado = false ";
        sb += "		    left join AUTORIZACIONES_ORDEN ao on ao.ORDEN = o.ID ";
        sb += "	    where c.CODIGO ='" + codigo + "'";
        sb += "	    and ao.ESTATUS > " + estado;
        sb += "	    group by c.CODIGO, extract(month from o.FECHA)::bigint, ";
        sb += "	    extract(year from o.FECHA)::bigint, c.FECHA_INICIO, c.FECHA_VENCIMIENTO, c.MONTO ";
        sb += "	    order by extract(year from o.FECHA)::bigint, extract(month from o.FECHA)::bigint ";
        List<Object[]> lo = em.createNativeQuery(sb).getResultList();
        //
        List<ContratoVO> lc = new ArrayList<>();
        for (Object[] lo1 : lo) {
            ContratoVO contratoVO = new ContratoVO();
            contratoVO.setNombre((String) lo1[0]);
            contratoVO.setTotalOCS((Long) lo1[1]);
            contratoVO.setMes((Long) lo1[2]);
            contratoVO.setAnio((Long) lo1[3]);
            contratoVO.setFechaInicio((Date) lo1[4]);
            contratoVO.setFechaVencimiento((Date) lo1[5]);
            contratoVO.setMonto((Double) lo1[6]);
            contratoVO.setTotalMes(((BigDecimal) lo1[7]).doubleValue());
            contratoVO.setAcumulado(contratoVO.getTotalMes() + contratoVO.getAcumulado());
            lc.add(contratoVO);
        }
        return lc;
    }

    /**
     *
     * @param sesion
     * @param idContrato
     * @param nuevoEstado
     * @throws java.lang.Exception
     */
    
    public void promoverEstadoConvenio(String sesion, int idContrato, int nuevoEstado,
            Map<String, List<ContactoProveedorVO>> listaCorreo) throws Exception {
        String asunto;
        try {
            // Notificar
            ContactoProveedorVO cpvo = correoPara(listaCorreo.get("para"));
            ContratoVO contratoVO = buscarPorId(idContrato, 0, "", false);
            switch (nuevoEstado) {
                case Constantes.ESTADO_CONVENIO_ACTIVO:
                    asunto = "Contrato Formalizado";
                    asunto += " - " + Constantes.FMT_ddMMyyy.format(new Date());
                    notificacionConvenioImpl.notificacionConvenioFormalizado(cpvo.getCorreo(), correoCopia(listaCorreo.get("copia")), asunto, contratoVO, apCampoRemote.find(contratoVO.getIdCampo()).getCompania().getNombre(), cpvo.getNombre(), cpvo.getPuesto());
                    break;
                case Constantes.ESTADO_CONVENIO_A_FIRMAS:
                    asunto = "Contrato en firmas";
                    asunto += " - " + Constantes.FMT_ddMMyyy.format(new Date());
                    notificacionConvenioImpl.notificacionConvenioPromovido(correoPorRol(Constantes.ROL_VALIDA_DOCTOS, contratoVO.getIdCampo()), "", asunto, contratoVO);
                    break;
                case Constantes.ESTADO_CONVENIO_FINIQUITO:
                    asunto = "Contrato Finiquitado";
                    asunto += " - " + Constantes.FMT_ddMMyyy.format(new Date());
                    notificacionConvenioImpl.notificacionConvenioFiniquitado(cpvo.getCorreo(), correoCopia(listaCorreo.get("copia")), asunto, contratoVO, apCampoRemote.find(contratoVO.getIdCampo()).getCompania().getNombre(), cpvo.getNombre(), cpvo.getPuesto());
                    break;
                default:
                    break;
            }
            Convenio convenio = find(idContrato);
            convenio.setEstatus(new Estatus(nuevoEstado));
            convenio.setModifico(new Usuario(sesion));
            convenio.setFechaModifico(new Date());
            convenio.setHoraModifico(new Date());
            edit(convenio);
//
        } catch (Exception e) {
            throw e;
        }

    }

    private ContactoProveedorVO correoPara(List<ContactoProveedorVO> listaCorreo) {
        for (ContactoProveedorVO contactoPara : listaCorreo) {
            return contactoPara;
        }
        return null;
    }

    private String correoCopia(List<ContactoProveedorVO> listaCorreo) {
        String correo = "";
        for (ContactoProveedorVO listaCorreo1 : listaCorreo) {
            if (correo.isEmpty()) {
                correo = listaCorreo1.getCorreo();
            } else {
                correo += "," + listaCorreo1.getCorreo();
            }
        }
        return correo;
    }

    
    public List<ContratoVO> contratosPorVencerPorMontos(int campo, int moneda, Date fecha, double porcentje) {
        String sb = "select count(o.ID), c.CODIGO, c.NOMBRE, c.MONTO, p.NOMBRE, c.FECHA_INICIO, c.FECHA_VENCIMIENTO ";
        sb += " ,round(sum(o.SUBTOTAL)::numeric,2) ";
        sb += " from convenio c ";
        sb += "	    inner join orden o on o.CONTRATO = c.CODIGO ";
        sb += "	    left join AUTORIZACIONES_ORDEN ao on ao.ORDEN = o.ID";
        sb += "	    inner join PROVEEDOR p on c.PROVEEDOR = p.ID";
        sb += "	 where c.FECHA_VENCIMIENTO >= cast('" + siManejoFechaImpl.convertirFechaStringyyyyMMdd(fecha) + "' as date ";
        sb += "	 and ao.SOLICITO <> 'PRUEBA'";
        sb += "	 and o.AP_CAMPO = " + campo;
        sb += "	 and o.MONEDA = " + moneda;
        sb += "	 and and c.MONEDA = " + moneda;
        sb += "	 and and ao.ESTATUS >= " + Constantes.ORDENES_SIN_APROBAR;
        sb += "	 group by c.CODIGO, c.NOMBRE, c.MONTO ,p.NOMBRE, c.FECHA_INICIO, c.FECHA_VENCIMIENTO ";
        sb += " round(sum(o.SUBTOTAL)::numeric,2) > (c.MONTO - (c.MONTO * 0." + ((porcentje >= 10.0) ? porcentje : "0.0" + porcentje) + "))";
        sb += "	 order by sum(o.SUBTOTAL) ";
//
        List<Object[]> lo = em.createNativeQuery(sb).getResultList();
        List<ContratoVO> lc = null;
        if (lo != null) {
            lc = new ArrayList<>();
            for (Object[] lo1 : lo) {
                ContratoVO c = new ContratoVO();
                c.setTotalOCS((Long) lo1[0]);
                c.setNumero((String) lo1[1]);
                c.setNombre((String) lo1[2]);
                c.setMonto((Integer) lo1[3]);
                c.setNombreProveedor((String) lo1[4]);
                c.setFechaInicio((Date) lo1[5]);
                c.setFechaVencimiento((Date) lo1[6]);
                c.setSubTotalOcs((Double) lo1[7]);
                lc.add(c);
            }
        }
        return lc;
    }

    
    public List<ContratoVO> traerConveniosPorUsuarioGerencia(String idUsuario, int gerencia, int campo) {
        String sb = consulta();
        sb += "where " + gerencia + " in (select gerencia from cv_convenio_gerencia where eliminado = 'False')";
        sb += "	and c.id in (select convenio from cv_convenio_usuario where usuario = '" + idUsuario + "' and eliminado = 'False')";
        sb += "	and c.ap_campo = " + campo;
        sb += "	order by c.id desc";
        List<Object[]> lo = em.createNativeQuery(sb).getResultList();
        List<ContratoVO> lc = null;
        if (lo != null) {
            lc = new ArrayList<>();
            for (Object[] ocObjects : lo) {
                lc.add(castContratos(ocObjects));
            }
        }
        return lc;
    }

    
    public List<ConvenioArticuloVo> cargarArchivoPrecio(File archivoPrecio) {
        List<ConvenioArticuloVo> lartInt = new ArrayList<ConvenioArticuloVo>();
        try {
            ConvenioArticuloVo caInt = null;
            //
            LecturaLibro lecturaLibro = new LecturaLibro();
            XSSFWorkbook archivo = lecturaLibro.loadFileXLSX(archivoPrecio);
            XSSFSheet workSheet = lecturaLibro.loadSheet(archivo);
            for (int i = 6; i <= workSheet.getLastRowNum(); i++) {
                int rowNum = i + 1;
                ConvenioArticuloVo ca = readSheetData(workSheet, rowNum);
                if (ca != null) {
                    if (ca.getCantidad() == 0) {
                        caInt = ca;
                        lartInt.add(ca);
                    } else {
                        if (caInt != null) {
                            ca.setConvenioArticuloCabecera(caInt.getNombre());
                        }
                        lartInt.add(ca);
                    }
                }
            }
        } catch (Exception ex) {
            UtilLog4j.log.error(ex);
        }
        return lartInt;
    }

    private ConvenioArticuloVo readSheetData(XSSFSheet workSheet, int fila) throws Exception {
        UtilLog4j.log.info("Leyendo datos ...");
        InvArticulo articulo = null;
        LecturaLibro lecturaLibro = new LecturaLibro();
        ConvenioArticuloVo art = new ConvenioArticuloVo();
        try {
            String cantidad = lecturaLibro.getValFromReference(workSheet, "D" + fila);
            art.setItem(lecturaLibro.getValFromReference(workSheet, "A" + fila));
            art.setNombre(lecturaLibro.getValFromReference(workSheet, "B" + fila));
            art.setUnidadNombre(lecturaLibro.getValFromReference(workSheet, "C" + fila));
            if (!art.getUnidadNombre().isEmpty()) {
                SiUnidad unidad = unidadRemote.buscarPorNombre(art.getUnidadNombre());
                if (unidad != null) {
                    art.setUnidadId(unidad.getId());
                }
            } else {
                art.setUnidadId(Constantes.CERO);
            }
            art.setAlcance(lecturaLibro.getValFromReference(workSheet, "G" + fila));
            art.setArticuloCodigoInterno(lecturaLibro.getValFromReference(workSheet, "H" + fila));
            if (!art.getUnidadNombre().isEmpty() || !art.getArticuloCodigoInterno().isEmpty()) {
                if (art.getArticuloCodigoInterno() != null && !art.getArticuloCodigoInterno().isEmpty()) {
                    articulo = invArticuloRemote.buscarPorCodigo(art.getArticuloCodigoInterno().replace("'", ""), art.getUnidadId());
                }
                if (articulo == null) {
                    articulo = invArticuloRemote.buscarPorNombre(art.getNombre().replace("'", ""), art.getUnidadId());
                }
                if (articulo != null) {
                    art.setIdArticulo(articulo.getId());
                    art.setNombre(articulo.getNombre());
                    art.setId(articulo.getId());
                    art.setRegistrado(Boolean.TRUE);
                    art.setUnidadId(articulo.getUnidad().getId());
                } else {
                    art.setId(Constantes.CERO);
//                    art.setUnidadId(Constantes.CERO);
                }
                if (!cantidad.isEmpty()) {
                    //

                    art.setCantidad(Double.valueOf(lecturaLibro.getValFromReference(workSheet, "D" + fila)));
                    String pu = lecturaLibro.getValFromReference(workSheet, "E" + fila);
                    if (!pu.isEmpty()) {
                        art.setPrecioUnitario(Double.valueOf(lecturaLibro.getValFromReference(workSheet, "E" + fila)));
                    } else {
                        art.setPrecioUnitario(0);
                    }
                    art.setImporte(art.getCantidad() * art.getPrecioUnitario());
                }
            } else {
                art = null;
            }

        } catch (NumberFormatException e) {
            UtilLog4j.log.error(e);
        }
        return art;
    }

    public void terminarContrato(String sesion, ContratoVO contratoVo, String motivo) {
        Convenio convenio = find(contratoVo.getId());
        convenio.setEstatus(new Estatus(Constantes.ESTADO_CONVENIO_FINIQUITO));
        convenio.setModifico(new Usuario(sesion));
        convenio.setFechaModifico(new Date());
        convenio.setHoraModifico(new Date());
        //
        edit(convenio);
    }

    
    public boolean finalizarConvenio(String sesion, ContratoVO contratoVO, String mensaje) {
        boolean v = true;
        try {
            Convenio convenio = find(contratoVO.getId());
            convenio.setEstatus(new Estatus(Constantes.ESTADO_CONVENIO_FINIQUITO));
            convenio.setMensajeFinalizo(mensaje);
            convenio.setFinalizo(new Usuario(sesion));
            convenio.setFechaFinalizo(new Date());
            convenio.setHoraFinalizo(new Date());
            //
            convenio.setModifico(new Usuario(sesion));
            convenio.setFechaModifico(new Date());
            convenio.setHoraModifico(new Date());
            //
            edit(convenio);
        } catch (Exception e) {
            UtilLog4j.log.fatal(e);
            v = false;
        }
        return v;
    }

    
    public List<ContratoVO> traerConvenioMaestroPorProveedorStatus(int idProveedor, int statusId) {
        String sb = consulta();
        sb += "	where p.id = " + idProveedor;
        sb += "	and  c.convenio is null";
        sb += "	and c.estatus = " + statusId;
        sb += "	and c.eliminado = false";
        sb += "	order by c.codigo asc";
        //
        List<Object[]> lo = em.createNativeQuery(sb).getResultList();
        List<ContratoVO> lc = null;
        if (lo != null) {
            lc = new ArrayList<>();
            for (Object[] ocObjects : lo) {
                lc.add(castContratos(ocObjects));
            }
        }
        return lc;
    }

    
    public List<ContratoVO> traerConvenioPorStatusCampo(int statusId, int campoId) {
        String sb = consulta();
        sb += "	where c.estatus = " + statusId
                + "	and  c.convenio is null"
                + "	and  c.ap_campo = " + campoId
                + "	and c.eliminado = false"
                + "	order by c.codigo asc";
        //
        List<Object[]> lo = em.createNativeQuery(sb).getResultList();
        List<ContratoVO> lc = null;
        if (lo != null) {
            lc = new ArrayList<>();
            for (Object[] ocObjects : lo) {
                lc.add(castContratos(ocObjects));
            }
        }
        return lc;
    }

    
    public boolean actualizarStatus(String sesion, ContratoVO contratoVo, int statusId) {
        boolean v = true;
        try {
            Convenio convenio = find(contratoVo.getId());
            convenio.setEstatus(new Estatus(statusId));
            convenio.setModifico(new Usuario(sesion));
            convenio.setFechaModifico(new Date());
            convenio.setHoraModifico(new Date());
            edit(convenio);
            //
        } catch (Exception e) {
            UtilLog4j.log.fatal(e);
            v = false;
        }
        return v;
    }

    
    public int guardarContratoFiniquito(String sesion, ContratoVO contratoVo) {
        Convenio convenio = new Convenio();
        try {
            convenio.setProveedor(new Proveedor(contratoVo.getProveedor()));
            convenio.setEstatus(new Estatus(Constantes.ESTADO_CONVENIO_REGISTRADO));
            convenio.setCvTipo(new CvTipo(contratoVo.getIdTipo()));
            if (contratoVo.getIdClasificacion() > 0) {
                convenio.setCvClasificacion(new CvClasificacion(contratoVo.getIdClasificacion()));
            }
            convenio.setApCampo(new ApCampo(contratoVo.getIdCampo()));
            convenio.setNombre(contratoVo.getNombre());
            convenio.setConvenio(new Convenio(contratoVo.getIdContratoRelacionado()));
            //
            convenio.setCodigo(sumarVersion(find(contratoVo.getIdContratoRelacionado()).getCodigo()));
            //
            convenio.setGenero(new Usuario(sesion));
            convenio.setFechaGenero(new Date());
            convenio.setHoraGenero(new Date());
            convenio.setEliminado(Constantes.NO_ELIMINADO);
            //
            create(convenio);
            //
            contratoVo.setId(contratoVo.getIdContratoRelacionado());
            actualizarStatus(sesion, contratoVo, Constantes.ESTADO_CONVENIO_PROCESO_FINIQUITO_FINALIZADO);            //
        } catch (Exception e) {
            UtilLog4j.log.fatal(e);
        }
        return convenio.getId();
    }

    
    public List<ContratoVO> reporteContratoFiniquito(int campoId) {
        String c = "select  p.nombre as contratista, p.nombre_corto as nombre_corto, c.codigo as numero, c.nombre as contrato , "
                + "  c.fecha_vencimiento , e.nombre as status, u.nombre as analista\n"
                + ",(select cce.codigo_exhorto from cv_convenio_exhorto cce where cce.convenio = c.id order by cce.id asc limit 1)\n"
                + ",(select cf2.porcentaje from cv_convenio_formas ccf2 \n"
                + "	inner join cv_formas cf2 on ccf2.cv_formas = cf2.id\n"
                + "  where ccf2.convenio   = c.id \n"
                + "  and ccf2.validado = true and ccf2.si_adjunto is not null\n"
                + "  and cf2.codigo = 'F01'  order by ccf2.id desc limit 1) as SOLICITUD_FIN\n"
                + "  ,(select cf2.porcentaje from cv_convenio_formas ccf2 \n"
                + "	inner join cv_formas cf2 on ccf2.cv_formas = cf2.id\n"
                + "  where ccf2.convenio   = c.id \n"
                + "  and ccf2.validado = true and ccf2.si_adjunto is not null\n"
                + "  and cf2.codigo = 'F02'  order by ccf2.id desc limit 1) as ACTA_ENTREGA\n"
                + "    ,(select cf2.porcentaje from cv_convenio_formas ccf2 \n"
                + "	inner join cv_formas cf2 on ccf2.cv_formas = cf2.id\n"
                + "  where ccf2.convenio   = c.id \n"
                + "  and ccf2.validado = true and ccf2.si_adjunto is not null\n"
                + "  and cf2.codigo = 'F03'  order by ccf2.id desc limit 1) as EDO_CUENTA\n"
                + " ,(select cf2.porcentaje from cv_convenio_formas ccf2 \n"
                + "	inner join cv_formas cf2 on ccf2.cv_formas = cf2.id\n"
                + "  where ccf2.convenio   = c.id \n"
                + "  and ccf2.validado = true and ccf2.si_adjunto is not null\n"
                + "  and cf2.codigo = 'F04'  order by ccf2.id desc limit 1) as VALID_CONT\n"
                + "  ,(select cf2.porcentaje from cv_convenio_formas ccf2 \n"
                + "	inner join cv_formas cf2 on ccf2.cv_formas = cf2.id\n"
                + "  where ccf2.convenio   = c.id \n"
                + "  and ccf2.validado = true and ccf2.si_adjunto is not null\n"
                + "  and cf2.codigo = 'F05'  order by ccf2.id desc limit 1) as VALID_RH \n"
                + "  , cam.nombre, cam.id "
                + " from convenio c \n"
                + "     inner join ap_campo cam on c.ap_campo = cam.id"
                + "	inner join proveedor p on c.proveedor  = p.id \n"
                + "	inner join estatus e on c.estatus  = e.id \n"
                + "	inner join usuario u on c.genero  = u.id \n"
                + " where c.estatus in (" + Constantes.ESTADO_CONVENIO_VENCIDO + ", " + Constantes.ESTADO_CONVENIO_PROCESO_FINIQUITO + " , " + Constantes.ESTADO_CONVENIO_ACTIVO + ")\n"
                + " and c.convenio is null  "
                + " and c.id in (select cce2.convenio from cv_convenio_exhorto cce2 where cce2.eliminado = false )";
        if (campoId > 0) {
            c += " and c.ap_campo = " + campoId;
        }
        c += " and c.eliminado  = false ";

        List<ContratoVO> contratosReporte = new ArrayList<ContratoVO>();
        List<Object[]> objs = em.createNativeQuery(c).getResultList();
        for (Object[] obj : objs) {
            ContratoVO conVo = new ContratoVO();
            conVo.setId(0);
            conVo.setProveedorVo(new ProveedorVo());
            conVo.getProveedorVo().setNombre((String) obj[0]);
            conVo.getProveedorVo().setNombreCorto((String) obj[1]);
            conVo.setCodigo((String) obj[2]);
            conVo.setNombre((String) obj[3]);
            conVo.setFechaVencimiento((Date) obj[4]);
            conVo.setEstado((String) obj[5]);
            conVo.setAnalista((String) obj[6]);
            conVo.setCodigoExhorto((String) obj[7]);
            conVo.setSolFinAvance(obj[8] != null ? (Integer) obj[8] : 0);
            conVo.setActaEntAvance(obj[9] != null ? (Integer) obj[9] : 0);
            conVo.setEdoCuentaAvance(obj[10] != null ? (Integer) obj[10] : 0);
            conVo.setValidContAvance(obj[11] != null ? (Integer) obj[11] : 0);
            conVo.setValidRhAvance(obj[12] != null ? (Integer) obj[12] : 0);
            conVo.setCampo((String) obj[13]);
            conVo.setIdCampo((Integer) obj[14]);
            //
            int total = conVo.getSolFinAvance() + conVo.getActaEntAvance() + conVo.getEdoCuentaAvance() + conVo.getValidContAvance() + conVo.getValidRhAvance();
            conVo.setTotalFormas(total + "");
            contratosReporte.add(conVo);
        }

        return contratosReporte;
    }

    
    public List<ProveedorVo> traerProveedoresConContratoActivo() {
        String c = "select distinct(p.ID), p.RFC, p.NOMBRE from PROVEEDOR p \n"
                + "	    inner join CONVENIO c on c.PROVEEDOR = p.ID\n"
                + " where c.estatus  in(" + Constantes.ESTADO_CONVENIO_ACTIVO + "," + Constantes.ESTADO_CONVENIO_EXHORTO + "," + Constantes.ESTADO_CONVENIO_PROCESO_FINIQUITO + " )"
                + " and c.convenio is null "
                + " and c.eliminado = false"
                + " and p.eliminado = false";

        List<Object[]> lo = em.createNativeQuery(c).getResultList();
        List<ProveedorVo> lp = new ArrayList<ProveedorVo>();
        for (Object[] lo1 : lo) {
            ProveedorVo proveedorVo = new ProveedorVo();
            proveedorVo.setIdProveedor((Integer) lo1[0]);
            proveedorVo.setRfc((String) lo1[1]);
            proveedorVo.setNombre((String) lo1[2]);
            lp.add(proveedorVo);
        }
        return lp;
    }
}
