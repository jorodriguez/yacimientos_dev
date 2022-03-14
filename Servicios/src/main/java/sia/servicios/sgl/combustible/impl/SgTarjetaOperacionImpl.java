/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.sgl.combustible.impl;

import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.base.Strings;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.LocalBean;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TemporalType;
import javax.sql.DataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.exception.DataAccessException;
import sia.constantes.Constantes;
import sia.excepciones.SIAException;
import sia.modelo.Gerencia;
import sia.modelo.SgEstacion;
import sia.modelo.SgTarjetaBancaria;
import sia.modelo.SgTarjetaOperacion;
import sia.modelo.SgVehiculo;
import sia.modelo.Usuario;
import sia.modelo.combustible.vo.Consumo;
import sia.modelo.sgl.vo.ReporteVo;
import sia.modelo.sgl.vo.TarjetaOperacionVO;
import sia.modelo.sistema.AbstractFacade;
import sia.util.LecturaLibro;
import sia.util.UtilLog4j;
import org.apache.poi.ss.usermodel.IndexedColors;
import sia.servicios.catalogos.impl.GerenciaImpl;
import sia.servicios.catalogos.impl.UsuarioImpl;
import sia.servicios.sgl.impl.SgOficinaImpl;
import sia.servicios.sgl.vehiculo.impl.SgVehiculoImpl;
import sia.servicios.sistema.impl.SiManejoFechaImpl;
import sia.servicios.sistema.impl.SiParametroImpl;

/**
 *
 * @author ihsa
 */
@LocalBean 
public class SgTarjetaOperacionImpl extends AbstractFacade<SgTarjetaOperacion>{

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Inject
    DSLContext dbCtx;

    public SgTarjetaOperacionImpl() {
        super(SgTarjetaOperacion.class);
    }
    @Inject
    private UsuarioImpl usuarioRemote;
    @Inject
    private SgEstacionImpl sgEstacionLocal;
    @Inject
    private SgTarjetaBancariaImpl sgTarjetaBancariaLocal;
    @Inject
    private SgVehiculoImpl sgVehiculoRemote;
    @Inject
    private GerenciaImpl gerenciaRemote;
    @Inject
    private SiParametroImpl siParametroRemote;
    
    @Inject
    private SiManejoFechaImpl fechaLocal;
    
    @Inject
    private SgOficinaImpl oficinaRemote;

    private final String JASPERFilePathConsumo = "Formatos/fuentesSGyL/";
    JasperPrint jasperPrint;

    
    public List<SgTarjetaOperacion> guardar(String sesion, File adjunto) {
        LecturaLibro lecturaLibro = new LecturaLibro();
        try {
            List<SgTarjetaOperacion> lista = new ArrayList<>();
            HSSFWorkbook archivo = lecturaLibro.loadFile(adjunto);
            // System.out.println("Archivo  :: " + adjunto.getName());
            HSSFSheet workSheet = lecturaLibro.loadSheet(archivo);
            SgTarjetaOperacion to = null;
            for (int i = 7; i <= workSheet.getLastRowNum(); i++) {
                int rowNum = i + 1;
                try {
                    Consumo consumo = readSheetData(workSheet, rowNum);
                    if (consumo != null) {
                        to = new SgTarjetaOperacion();
                        SgTarjetaBancaria t = sgTarjetaBancariaLocal.buscarPorNumero(consumo.getTarjeta());
                        if (t == null) {
                            t = sgTarjetaBancariaLocal.guardar(sesion, consumo.getTarjeta());
                        }
                        to.setSgTarjeta(t);

                        SgVehiculo sgVehiculo = sgVehiculoRemote.buscarPorUltimoNumeroPlaca(consumo.getPlaca());
                        if (sgVehiculo != null) {
                            to.setSgVehiculo(sgVehiculo);
                        }
                        to.setPlaca(consumo.getPlaca());
                        to.setOperacion(consumo.getIdMovimiento());
                        to.setCargo(consumo.getImporte().doubleValue());
                        to.setTipo(consumo.getTipo());

                        if (consumo.getUsuario().contains(" ")) {
                            String[] user = consumo.getUsuario().split(" ");
                            Usuario u = usuarioRemote.find(user[0]);
                            if (u != null) {
                                to.setUsuario(u);
                                String cad = "";
                                for (int j = 1; j < user.length; j++) {
                                    cad += " " + user[j];
                                }
                                to.setUsSinRegistro(cad);
                            } else {
                                to.setUsSinRegistro(consumo.getUsuario());
                            }
                        } else { //
                            to.setUsSinRegistro(consumo.getUsuario());
                        }
                        to.setConcepto(consumo.getConcepto());
                        to.setTipoCombustible(consumo.getTipoComb());
                        to.setPrecioUnitario(consumo.getPrecio().doubleValue());
                        to.setKilometroInicial(consumo.getKmIni());
                        to.setKilometroFinal(consumo.getKmFin());
                        to.setCantidad(consumo.getLitros().doubleValue());
                        to.setRendimiento(consumo.getRendimiento().doubleValue());
                        to.setIva(Double.valueOf(consumo.getIva().replace("%", "")));
                        SgEstacion e = sgEstacionLocal.buscarPorNumero(consumo.getEstacion());
                        if (e == null) {
                            e = sgEstacionLocal.guardarEstacion(sesion, consumo.getEstacion(), consumo.getRfcEstacion(), consumo.getEstado(), consumo.getCiudad(), consumo.getColonia(), consumo.getDomicilio());
                        }
                        to.setSgEstacion(e);
                        Calendar c = Calendar.getInstance();
                        c.set(consumo.getYear(), consumo.getMonth() - 1, consumo.getDay());
                        to.setFechaOperacion(c.getTime());
                        try {
                            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                            Date date = sdf.parse(consumo.getTime());
                            to.setHoraOperacion(date);
                        } catch (ParseException exc) {
                            UtilLog4j.log.warn(exc);
                        }
                        
                        if(consumo.getCodOficina() != null && !consumo.getCodOficina().isEmpty()){
                            if(consumo.getCodOficina().toUpperCase().equals( "MTY")){
                                to.setSgOficina(oficinaRemote.find(1));
                            } else if(consumo.getCodOficina().toUpperCase().equals( "SF")){
                                to.setSgOficina(oficinaRemote.find(3));
                            }else if(consumo.getCodOficina().toUpperCase().equals( "REY")){
                                to.setSgOficina(oficinaRemote.find(2));
                            }
                        }

                        to.setGenero(new Usuario(sesion));
                        to.setFechaGenero(new Date());
                        to.setHoraGenero(new Date());
                        to.setEliminado(Constantes.NO_ELIMINADO);
                        //
                        create(to);
                        //
                        lista.add(to);
                        // modificacion  a la lista de consumos
                    }
                } catch (Exception ex) {
                    Logger.getLogger(SgTarjetaOperacionImpl.class.getName()).log(Level.SEVERE, null, ex);
                }

            } // for
            //    crearEtiquetas(new Date(), directorio);
            return lista;
        } catch (Exception ex) {
            Logger.getLogger(SgTarjetaOperacionImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private String getRepositoryPath() {
        return siParametroRemote.find(Constantes.UNO).getUploadDirectory();
    }

    
    public byte[] crearEtiquetas(Date fechaParametro) throws FileNotFoundException, IOException {
        String sourceFileName = "maestro.jasper";

        Connection con = null;

        byte[] pdfFileContent = null;

        try {

            Map<String, Object> parameters = new HashMap<String, Object>();
            parameters.put("FECHA", fechaParametro);

            parameters.put("REPORT_TITLE", ("Consumo de combustible de " + Constantes.FMT_ddMMyyy.format(traerFechaMinimaCargaArchivo(fechaParametro)) + " al " + Constantes.FMT_ddMMyyy.format(traerFechaMaximaCargaArchivo(fechaParametro))));
            parameters.put("DIR_SUB_REPORTE", getRepositoryPath() + JASPERFilePathConsumo);

            con = getConexion();

            //
            doIt(sourceFileName, parameters, con);
            //
            pdfFileContent = getPdf();

        } catch (SIAException e) {
            UtilLog4j.log.fatal(e);
        } catch (Exception e) {
            UtilLog4j.log.fatal(e);
        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (Exception e) {
                    UtilLog4j.log.fatal(e);
                }
            }
        }

        return pdfFileContent;
    }

    private Consumo readSheetData(HSSFSheet workSheet, int rowNum) throws Exception {
        UtilLog4j.log.info("Leyendo datos ...");
        LecturaLibro lecturaLibro = new LecturaLibro();
        SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //		Cell dateCell = getCellFromReference(workSheet, "C5");
        // String title = lecturaLibro.getValFromReference(workSheet, "C5");

        String cellVal = lecturaLibro.getValFromReference(workSheet, "H" + rowNum);

        if (!Strings.isNullOrEmpty(cellVal) && "CON".equals(cellVal)) {
            // TODO, Agregar los registros a las tablas de consumo de combustible
            Consumo consumo = new Consumo();

            consumo.setTarjeta(lecturaLibro.getValFromReference(workSheet, "A" + rowNum));
            consumo.setPlaca(lecturaLibro.getValFromReference(workSheet, "B" + rowNum));
            consumo.setFecha(new Timestamp(DATE_FORMAT.parse(
                    lecturaLibro.getCellFromReference(workSheet, "C" + rowNum).getStringCellValue()
            ).getTime()));

            consumo.setIdMovimiento(lecturaLibro.getValFromReference(workSheet, "D" + rowNum));
            consumo.setImporte(new BigDecimal(lecturaLibro.getCellFromReference(workSheet, "E" + rowNum).getNumericCellValue()));
            consumo.setTipo(lecturaLibro.getCellFromReference(workSheet, "H" + rowNum).getStringCellValue());
            consumo.setUsuario(lecturaLibro.getValFromReference(workSheet, "I" + rowNum));
            consumo.setConcepto(lecturaLibro.getCellFromReference(workSheet, "J" + rowNum).getStringCellValue());
            consumo.setTipoComb(lecturaLibro.getCellFromReference(workSheet, "K" + rowNum).getStringCellValue());
            consumo.setPrecio(new BigDecimal(lecturaLibro.getCellFromReference(workSheet, "L" + rowNum).getNumericCellValue()));
            consumo.setKmIni((int) lecturaLibro.getCellFromReference(workSheet, "M" + rowNum).getNumericCellValue());
            consumo.setKmFin((int) lecturaLibro.getCellFromReference(workSheet, "N" + rowNum).getNumericCellValue());
            consumo.setLitros(new BigDecimal(lecturaLibro.getCellFromReference(workSheet, "O" + rowNum).getNumericCellValue()));
            consumo.setRendimiento(new BigDecimal(lecturaLibro.getCellFromReference(workSheet, "P" + rowNum).getNumericCellValue()));
            consumo.setIva(lecturaLibro.getCellFromReference(workSheet, "Q" + rowNum).getStringCellValue());
            consumo.setEstacion(lecturaLibro.getCellFromReference(workSheet, "R" + rowNum).getStringCellValue());
            consumo.setRfcEstacion(lecturaLibro.getCellFromReference(workSheet, "S" + rowNum).getStringCellValue());
            consumo.setEstado(lecturaLibro.getCellFromReference(workSheet, "T" + rowNum).getStringCellValue());
            consumo.setCiudad(lecturaLibro.getCellFromReference(workSheet, "U" + rowNum).getStringCellValue());
            consumo.setColonia(lecturaLibro.getCellFromReference(workSheet, "V" + rowNum).getStringCellValue());
            consumo.setDomicilio(lecturaLibro.getCellFromReference(workSheet, "W" + rowNum).getStringCellValue());

            consumo.setYear(Integer.valueOf(lecturaLibro.getValFromReference(workSheet, "X" + rowNum)));
            consumo.setMonth(Integer.valueOf(lecturaLibro.getValFromReference(workSheet, "Y" + rowNum)));
            consumo.setDay(Integer.valueOf(lecturaLibro.getValFromReference(workSheet, "Z" + rowNum)));
            consumo.setTime(lecturaLibro.getCellFromReference(workSheet, "AA" + rowNum).getStringCellValue());
            consumo.setCodOficina(lecturaLibro.getCellFromReference(workSheet, "AB"+ rowNum).getStringCellValue());
            return consumo;
        }
        return null;
    }

    
    public List<SgTarjetaOperacion> traerRegistrosPorSemana(Calendar cInicioSem, Calendar cFinSem) {
        return em.createNamedQuery("SgTarjetaOperacion.traerRegistrosEntreFechas")
                .setParameter(1, cInicioSem.getTime(), TemporalType.DATE)
                .setParameter(2, cFinSem.getTime(), TemporalType.DATE)
                .getResultList();
    }

    
    public void quitarRegistrosCargados(String sesion, List<SgTarjetaOperacion> lista) {
        try {
            //
            for (SgTarjetaOperacion to : lista) {
                to.setModifico(new Usuario(sesion));
                to.setFechaModifico(new Date());
                to.setHoraModifico(new Date());
                to.setEliminado(Constantes.ELIMINADO);
                //
                edit(to);
            }
        } catch (Exception ex) {
            Logger.getLogger(SgTarjetaOperacionImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void doIt(String reportName, Map<String, Object> parameters, Connection conn) throws Exception {
        checkNotNull(reportName, "Debe proporcionar un nombre de reporte.");
        //checkNotNull(reportDataSource, "Debe proporcionar una fuente de datos para el reporte.");
        checkNotNull(parameters, "Debe proporcionar los parámetros para el reporte.");

        jasperPrint = JasperFillManager.fillReport(getRepositoryPath() + JASPERFilePathConsumo + reportName, parameters, conn);

    }

    public byte[] getPdf() throws SIAException {
        byte[] retVal;

        try {
            retVal = JasperExportManager.exportReportToPdf(jasperPrint);
        } catch (JRException e) {
            throw new SIAException(e.getMessage());
        }

        return retVal;
    }

    private Connection getConexion() {
        Connection result = null;

        try {
            final Context initialContext = new InitialContext();

            final DataSource datasource = (DataSource) initialContext.lookup(Constantes.DATASOURCE_CONTEXT);
            if (datasource == null) {
                UtilLog4j.log.fatal(this, "Failed to lookup datasource.");
            } else {
                result = datasource.getConnection();
            }
        } catch (NamingException ex) {
            UtilLog4j.log.fatal(ex);
        } catch (SQLException exx) {
            UtilLog4j.log.fatal(exx);
        }

        return result;
    }

    /**
     *
     * @param fecha
     * @return
     */
    
    public Date traerFechaMinimaCargaArchivo(Date fecha) {
        return (Date) em.createNamedQuery("SgTarjetaOperacion.traerFechaMinimaCargaArchivo")
                .setParameter(1, fecha, TemporalType.DATE).getSingleResult();
    }

    /**
     *
     * @param fecha
     * @return
     */
    
    public Date traerFechaMaximaCargaArchivo(Date fecha) {
        return (Date) em.createNamedQuery("SgTarjetaOperacion.traerFechaMaximaCargaArchivo")
                .setParameter(1, fecha, TemporalType.DATE).getSingleResult();
    }

    
    public List<Date> traerFechaCargaArchivo(int total) {
        return em.createNamedQuery("SgTarjetaOperacion.traerFechaCargaArchivo").setMaxResults(total).getResultList();
    }

    
    public List<SgTarjetaOperacion> traerRegistrosPorFechaCarga(Date fechaCarga) {
        return em.createNamedQuery("SgTarjetaOperacion.traerPorFecha")
                .setParameter(1, fechaCarga, TemporalType.DATE)
                .getResultList();
    }

    
    public List<ReporteVo> traerTotales(int anio) {
        String sb = " select g.NOMBRE, sum(t.CARGO) from SG_TARJETA_OPERACION t\n"
                + "    inner join USUARIO u on t.USUARIO = u.ID\n"
                + "    inner join GERENCIA g on u.GERENCIA = g.ID"
                + " where t.ELIMINADO = 'False'"
                + (anio > 0 ? consultaFecha() : "")
                + " group by g.NOMBRE"
                + "	union"
                + " select 'Otros', sum(t.CARGO) from SG_TARJETA_OPERACION t\n"
                + " where t.ELIMINADO = 'False'\n"
                + " and t.USUARIO is null"
                + (anio > 0 ? consultaFecha() : "");
        Query q = em.createNativeQuery(sb);
        if (anio > 0) {
            q.setParameter(1, anio).setParameter(2, anio);
        }
        List<Object[]> lo = q.getResultList();
        //
        List<ReporteVo> lr = new ArrayList<ReporteVo>();
        if (lo != null) {
            for (Object[] lo1 : lo) {
                if (lo1[1] != null) {
                    ReporteVo r = new ReporteVo();
                    r.setNombre((String) lo1[0]);
                    r.setTotal((Double) lo1[1]);
                    lr.add(r);
                }
            }
        }

        return lr;
    }

    /**
     *
     * @param anio
     * @param gerencia
     * @return
     */
    
    public List<ReporteVo> traerPorGerencia(int anio, String gerencia) {
        Gerencia g = gerenciaRemote.findByNameAndCompania(gerencia, Constantes.RFC_IHSA, false);
        if (g != null) {
            return traerConsumoPorGerencia(anio, gerencia);
        } else {
            return traerConsumoUsuarioSinRegistro(anio);
        }
    }

    private List<ReporteVo> traerConsumoPorGerencia(int anio, String gerencia) {
        String sb = " select u.NOMBRE, sum(t.CARGO) from SG_TARJETA_OPERACION t\n"
                + "    inner join USUARIO u on t.USUARIO = u.ID \n"
                + "    inner join GERENCIA g on u.GERENCIA = g.ID"
                + " where t.ELIMINADO = 'False'"
                + (anio > 0 ? consultaFecha() : "")
                + " and g.NOMBRE = ? "
                + " group by u.nombre"
                + " order by u.nombre ";
        Query q = em.createNativeQuery(sb);
        if (anio > 0) {
            q.setParameter(1, anio);
            q.setParameter(2, gerencia);
        } else {
            q.setParameter(1, gerencia);
        }
        List<Object[]> lo = q.getResultList();
        //
        List<ReporteVo> lr = new ArrayList<ReporteVo>();
        for (Object[] lo1 : lo) {
            ReporteVo r = new ReporteVo();
            r.setNombre((String) lo1[0]);
            r.setTotal((Double) lo1[1]);
            lr.add(r);
        }
        return lr;
    }

    private List<ReporteVo> traerConsumoUsuarioSinRegistro(int anio) {
        String sb = " select t.US_SIN_REGISTRO, sum(t.CARGO) from SG_TARJETA_OPERACION t"
                + " where t.ELIMINADO = 'False'"
                + " and t.USUARIO is null"
                + (anio > 0 ? consultaFecha() : "")
                + " group by t.US_SIN_REGISTRO"
                + " order by t.US_SIN_REGISTRO asc ";
        Query q = em.createNativeQuery(sb);
        if (anio > 0) {
            q.setParameter(1, anio);
        }
        List<Object[]> lo = q.getResultList();
        //
        List<ReporteVo> lr = new ArrayList<ReporteVo>();
        for (Object[] lo1 : lo) {
            ReporteVo r = new ReporteVo();
            r.setNombre((String) lo1[0]);
            r.setTotal((Double) lo1[1]);
            lr.add(r);
        }
        return lr;
    }

    
    public List<ReporteVo> traerConsumoPorUsuario(int anio, String usuario) {
        Usuario u = usuarioRemote.buscarPorNombre(usuario);
        if (u != null) {
            return traerConsumoPorUsuarioRegistrado(anio, usuario);
        } else {
            return traerConsumoPorUsuarioSinRegistro(anio, usuario);
        }
    }

    private List<ReporteVo> traerConsumoPorUsuarioRegistrado(int anio, String usuario) {
        String sb = " select u.nombre, sum(t.CARGO), extract(month from t.FECHA_OPERACION) || '/' || extract(year from t.FECHA_OPERACION) from SG_TARJETA_OPERACION t"
                + "    inner join USUARIO u on t.USUARIO = u.ID\n"
                + " where t.ELIMINADO = 'False'"
                + " and t.USUARIO is null"
                + (anio > 0 ? consultaFecha() : "")
                + " and u.NOMBRE = ? "
                + " group by u.nombre, extract(month from t.FECHA_OPERACION) || '/' || extract(year from t.FECHA_OPERACION)";
        Query q = em.createNativeQuery(sb);
        if (anio > 0) {
            q.setParameter(1, anio);
            q.setParameter(2, usuario);
        } else {
            q.setParameter(1, usuario);
        }
        List<Object[]> lo = q.getResultList();
        //
        List<ReporteVo> lr = new ArrayList<ReporteVo>();
        for (Object[] lo1 : lo) {
            ReporteVo r = new ReporteVo();
            r.setNombre((String) lo1[0]);
            r.setTotal((Double) lo1[1]);
            r.setFechaCompuesta((String) lo1[2]);
            lr.add(r);
        }
        return lr;
    }

    private List<ReporteVo> traerConsumoPorUsuarioSinRegistro(int anio, String usuario) {

        String sb = " select t.US_SIN_REGISTRO, sum(t.CARGO), extract(month from t.FECHA_OPERACION) || '/' || extract(year from t.FECHA_OPERACION) from SG_TARJETA_OPERACION t"
                + " where t.ELIMINADO = 'False'"
                + " and t.USUARIO is null"
                + (anio > 0 ? consultaFecha() : "")
                + " and t.US_SIN_REGISTRO = ? "
                + " group by t.US_SIN_REGISTRO, extract(month from t.FECHA_OPERACION) || '/' || extract(year from t.FECHA_OPERACION)";
        Query q = em.createNativeQuery(sb);
        if (anio > 0) {
            q.setParameter(1, anio);
            q.setParameter(2, usuario);
        } else {
            q.setParameter(1, usuario);
        }
        List<Object[]> lo = q.getResultList();
        //
        List<ReporteVo> lr = new ArrayList<ReporteVo>();
        for (Object[] lo1 : lo) {
            ReporteVo r = new ReporteVo();
            r.setNombre((String) lo1[0]);
            r.setTotal((Double) lo1[1]);
            r.setFechaCompuesta((String) lo1[2]);
            lr.add(r);
        }
        return lr;
    }

    
    public List<ReporteVo> traerPorUsuarioMesAnio(String usuario, int anio, int mes) {
        Usuario u = usuarioRemote.buscarPorNombre(usuario);
        if (u != null) {
            return traerPorUsuarioSinRegistroMesAnio(usuario, anio, mes);
        } else {
            return traerPorUsuarioConRegistroMesAnio(usuario, anio, mes);
        }
    }

    private List<ReporteVo> traerPorUsuarioSinRegistroMesAnio(String usuario, int anio, int mes) {
        String sb = "select t.FECHA_OPERACION, sum(t.CARGO) from SG_TARJETA_OPERACION t"
                + " where t.ELIMINADO = 'False'"
                + " and extract(year from t.FECHA_OPERACION) = ?"
                + " and t.US_SIN_REGISTRO = ? "
                + " and extract(month from t.FECHA_OPERACION) = ?"
                + " group by t.FECHA_OPERACION";
        Query q = em.createNativeQuery(sb);
        q.setParameter(1, anio);
        q.setParameter(2, usuario);
        q.setParameter(3, mes);
        List<Object[]> lo = q.getResultList();
        //
        List<ReporteVo> lr = new ArrayList<ReporteVo>();
        for (Object[] lo1 : lo) {
            ReporteVo r = new ReporteVo();
            r.setFecha((Date) lo1[0]);
            r.setTotal((Double) lo1[1]);
            lr.add(r);
        }
        return lr;
    }

    private List<ReporteVo> traerPorUsuarioConRegistroMesAnio(String usuario, int anio, int mes) {
        String sb = "select t.FECHA_OPERACION, sum(t.CARGO) from SG_TARJETA_OPERACION t"
                + "	inner join  usuario u on t.usuario = u.id"
                + " where t.ELIMINADO = 'False'"
                + " and extract(year from t.FECHA_OPERACION) = ?"
                + " and u.nombre = ? "
                + " and extract(month from t.FECHA_OPERACION) = ?"
                + " group by t.FECHA_OPERACION";
        Query q = em.createNativeQuery(sb);
        q.setParameter(1, anio);
        q.setParameter(2, usuario);
        q.setParameter(3, mes);
        List<Object[]> lo = q.getResultList();
        //
        List<ReporteVo> lr = new ArrayList<ReporteVo>();
        for (Object[] lo1 : lo) {
            ReporteVo r = new ReporteVo();
            r.setFecha((Date) lo1[0]);
            r.setTotal((Double) lo1[1]);
            lr.add(r);
        }
        return lr;
    }

    private String consultaFecha() {
        return " and extract(year from t.FECHA_OPERACION) = ?";
    }

    
    public Date traerPrimerRegistro() {
        String q = "select min(fecha_genero) from SG_TARJETA_OPERACION";
        return (Date) em.createNativeQuery(q).getSingleResult();
    }

    
    public TarjetaOperacionVO regresarMaximosMensuales(int mes) {

        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c1.set(c1.get(Calendar.YEAR), mes, 1);
        c2.set(c2.get(Calendar.YEAR), mes, 1);

        int finMes = c1.getActualMaximum(Calendar.DAY_OF_MONTH);
        int iniMes = c1.getActualMinimum(Calendar.DAY_OF_MONTH);

        c1.set(c1.get(Calendar.YEAR), mes, iniMes);
        c2.set(c2.get(Calendar.YEAR), mes, finMes);

        Date di = c1.getTime();
        Date df = c2.getTime();

        TarjetaOperacionVO o = null;

        String sql = "SELECT sum(cantidad)as cantidad,SUM(cargo) as cargo,"
                + " sum (kilometro_final-(kilometro_inicial)) as km_Mensual,avg(precio_unitario) as precio_unitario, avg(rendimiento) as rendimiento"
                + " FROM sg_tarjeta_operacion "
                + " where fecha_genero BETWEEN '" + Constantes.FMT_yyyyMMdd.format(di)
                + "' and '" + Constantes.FMT_yyyyMMdd.format(df)
                + "' and eliminado = ?";
        try {
            Record rec = dbCtx.fetchOne(sql, Constantes.FALSE);
            
            if(rec != null) {
                o = rec.into(TarjetaOperacionVO.class);
            }

        } catch (DataAccessException e) {
            UtilLog4j.log.fatal(e);
        }
        
        return o;
    }

    
    public List<TarjetaOperacionVO> kmMensualByUser(int mes) {

        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c1.set(c1.get(Calendar.YEAR), mes, 1);
        c2.set(c2.get(Calendar.YEAR), mes, 1);

        int finMes = c1.getActualMaximum(Calendar.DAY_OF_MONTH);
        int iniMes = c1.getActualMinimum(Calendar.DAY_OF_MONTH);

        c1.set(c1.get(Calendar.YEAR), mes, iniMes);
        c2.set(c2.get(Calendar.YEAR), mes, finMes);

        Date di = c1.getTime();
        Date df = c2.getTime();

        List<TarjetaOperacionVO> listOp;
        List<Record> r;
        String sql = "SELECT coalesce(o.usuario,o.us_sin_registro) as usuario, sum(o.cantidad) as cantidad, sum(o.cargo) as cargo, \n"
                + " sum (o.kilometro_final-(o.kilometro_inicial)) as km_Mensual, o.placa, coalesce(g.nombre,'sin gerencia') as gerencia\n"
                + " FROM sg_tarjeta_operacion o \n"
                + " left join ap_campo_usuario_rh_puesto a on a.usuario = o.usuario and a.eliminado= ? \n"
                + " left join gerencia g on g.id = a.gerencia and a.eliminado = ? \n"
                + " where o.fecha_genero BETWEEN '" + Constantes.FMT_yyyyMMdd.format(di)
                + "' and '" + Constantes.FMT_yyyyMMdd.format(df)
                + "' and o.eliminado = ?  GROUP by o.usuario, o.us_sin_registro, o.placa,g.nombre ORDER by o.us_sin_registro";

        try {
            listOp = dbCtx.fetch(sql, Constantes.FALSE, Constantes.FALSE, Constantes.FALSE).into(TarjetaOperacionVO.class);

            return listOp;
        } catch (DataAccessException e) {
            UtilLog4j.log.fatal(e);
            return null;
        }

    }

    
    public File crearArchivo(File fileTem, int mes) {
        try {

            List<TarjetaOperacionVO> to = kmMensualByUser(mes);
            List<TarjetaOperacionVO> toByOf = regresarMaximosMensualesByOficina(mes);
            TarjetaOperacionVO tar = regresarMaximosMensuales(mes);

            List<SelectItem> ls = fechaLocal.meses();
            
            if (to != null && !to.isEmpty()) {
                String REPOSITORYPATH = siParametroRemote.find(1).getUploadDirectory();

                fileTem = new File(REPOSITORYPATH + "temporal.xlsx");

                //FileInputStream input_document = new FileInputStream(fileTem);
                Workbook workbook = new XSSFWorkbook();
                Sheet pagina = workbook.createSheet("Reporte");
                pagina.setDefaultColumnWidth(25);
                
                Font font1 = workbook.createFont();
                //font1.setFontName("Arial");
                font1.setBold(true);
                font1.setColor(IndexedColors.BLACK.getIndex());
                
                CellStyle stilo1= workbook.createCellStyle();
                stilo1.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                stilo1.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
                stilo1.setFont(font1);
//                stilo1.setAlignment(Short.valueOf(HorizontalAlignment.LEFT.toString()));
//                stilo1.setVerticalAlignment(Short.valueOf(VerticalAlignment.CENTER.toString()));

                String[] titulos = {"Gerencia", "Usuario", "Placa", "kilometraje"};

                Row filaTitulos = pagina.createRow(Constantes.CERO);
                
                Cell cellTitle = filaTitulos.createCell(0);
                cellTitle.setCellStyle(stilo1);
                cellTitle.setCellValue("Totales del Mes: "+ ls.get(mes++).getLabel());

                Row filaMAx = pagina.createRow(2);
                Cell cellFilMax = filaMAx.createCell(0);
                cellFilMax.setCellStyle(stilo1);
                cellFilMax.setCellValue("Lts de Combustible");
                cellFilMax = filaMAx.createCell(1);
                cellFilMax.setCellStyle(stilo1);
                cellFilMax.setCellValue("Tipo");
                cellFilMax = filaMAx.createCell(2);
                cellFilMax.setCellStyle(stilo1);
                cellFilMax.setCellValue("Importe");
                cellFilMax = filaMAx.createCell(3);
                cellFilMax.setCellStyle(stilo1);
                cellFilMax.setCellValue("Precio por Litro");
                cellFilMax = filaMAx.createCell(4);
                cellFilMax.setCellStyle(stilo1);
                cellFilMax.setCellValue("Kilometraje");
                cellFilMax = filaMAx.createCell(5);
                cellFilMax.setCellStyle(stilo1);
                cellFilMax.setCellValue("Rendimiento por Litro");

                Row filaCant = pagina.createRow(3);
                Cell cellFillCant = filaCant.createCell(0);
                cellFillCant.setCellValue(tar.getCantidad());
                cellFillCant = filaCant.createCell(1);
                cellFillCant.setCellValue("Gasolina");
                cellFillCant = filaCant.createCell(2);
                cellFillCant.setCellValue(tar.getCargo());
                cellFillCant = filaCant.createCell(3);
                cellFillCant.setCellValue(tar.getPrecioUnitario());
                cellFillCant = filaCant.createCell(4);
                cellFillCant.setCellValue(tar.getKmMensual());
                cellFillCant = filaCant.createCell(5);
                cellFillCant.setCellValue(tar.getRendimiento());
                
                Row filaOficinas = pagina.createRow(5);
                Cell cellFilOficinas = filaOficinas.createCell(0);
                cellFilOficinas.setCellStyle(stilo1);
                cellFilOficinas.setCellValue("Oficina");
                cellFilOficinas = filaOficinas.createCell(1);
                cellFilOficinas.setCellStyle(stilo1);
                cellFilOficinas.setCellValue("Lts de Combustible");
                cellFilOficinas = filaOficinas.createCell(2);
                cellFilOficinas.setCellStyle(stilo1);
                cellFilOficinas.setCellValue("Tipo");
                cellFilOficinas = filaOficinas.createCell(3);
                cellFilOficinas.setCellStyle(stilo1);
                cellFilOficinas.setCellValue("Importe");
                cellFilOficinas = filaOficinas.createCell(4);
                cellFilOficinas.setCellStyle(stilo1);
                cellFilOficinas.setCellValue("Precio por Litro");
                cellFilOficinas = filaOficinas.createCell(5);
                cellFilOficinas.setCellStyle(stilo1);
                cellFilOficinas.setCellValue("Kilometraje");
                cellFilOficinas = filaOficinas.createCell(6);
                cellFilOficinas.setCellStyle(stilo1);
                cellFilOficinas.setCellValue("Rendimiento por Litro");               
                
                int count = 6;
                
                for (TarjetaOperacionVO t : toByOf) {
                    Row filaDatos = pagina.createRow(count);
                    Cell celdaDatos = filaDatos.createCell(0);
                    celdaDatos.setCellValue(t.getNameOficina());
                    celdaDatos = filaDatos.createCell(1);
                    celdaDatos.setCellValue(t.getCantidad());
                    celdaDatos = filaDatos.createCell(2);
                    celdaDatos.setCellValue("Gasolina");
                    celdaDatos = filaDatos.createCell(3);
                    celdaDatos.setCellValue(t.getCargo());
                    celdaDatos = filaDatos.createCell(4);
                    celdaDatos.setCellValue(t.getPrecioUnitario());
                    celdaDatos = filaDatos.createCell(5);
                    celdaDatos.setCellValue(t.getKmMensual());
                    celdaDatos = filaDatos.createCell(6);
                    celdaDatos.setCellValue(t.getRendimiento());
                    count++;
                }
                count++;
                Row filaTabla = pagina.createRow(count);

                for (int i = 0; i < titulos.length; i++) {
                    Cell celdaTitulo = filaTabla.createCell(i);
                    celdaTitulo.setCellStyle(stilo1);
                    celdaTitulo.setCellValue(titulos[i]);
                }

                count++;

                for (TarjetaOperacionVO t : to) {
                    Row filaDatos = pagina.createRow(count);
                    Cell celdaDatos = filaDatos.createCell(0);
                    celdaDatos.setCellValue(t.getGerencia());
                    celdaDatos = filaDatos.createCell(1);
                    celdaDatos.setCellValue(t.getUsuario());
                    celdaDatos = filaDatos.createCell(2);
                    celdaDatos.setCellValue(t.getPlaca());
                    celdaDatos = filaDatos.createCell(3);
                    celdaDatos.setCellValue(t.getKmMensual());
                    count++;
                }
                

                FileOutputStream file = new FileOutputStream(fileTem);
                workbook.write(file);
                file.close();

            }

        } catch (IOException e) {
            UtilLog4j.log.fatal(e);
        }

        return fileTem;
    }
    
    
    public List<TarjetaOperacionVO> regresarMaximosMensualesByOficina(int mes) {

        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c1.set(c1.get(Calendar.YEAR), mes, 1);
        c2.set(c2.get(Calendar.YEAR), mes, 1);

        int finMes = c1.getActualMaximum(Calendar.DAY_OF_MONTH);
        int iniMes = c1.getActualMinimum(Calendar.DAY_OF_MONTH);

        c1.set(c1.get(Calendar.YEAR), mes, iniMes);
        c2.set(c2.get(Calendar.YEAR), mes, finMes);

        Date di = c1.getTime();
        Date df = c2.getTime();

        List<TarjetaOperacionVO> o = null;

        String sql = "SELECT sum(top.cantidad)as cantidad,SUM(top.cargo) as cargo,"
                + " sum (top.kilometro_final-(top.kilometro_inicial)) as km_Mensual,avg(top.precio_unitario) as precio_unitario,"
                + " avg(top.rendimiento) as rendimiento"
                + ", COALESCE(o.id,0) as id_Oficina, COALESCE(o.nombre,'Sin oficina') as name_Oficina"
                + " FROM sg_tarjeta_operacion top"
                + " left join sg_oficina o on o.id = top.sg_oficina"
                + " where top.fecha_genero BETWEEN '"+ Constantes.FMT_yyyyMMdd.format(di)
                + "' and '"+ Constantes.FMT_yyyyMMdd.format(df)
                + "' and top.eliminado = ?"
                + " GROUP by o.id, o.nombre";
        try {
            o = dbCtx.fetch(sql, Constantes.FALSE).into(TarjetaOperacionVO.class);
            

        } catch (DataAccessException e) {
            UtilLog4j.log.fatal(e);
        }
        
        return o;
    }
}
