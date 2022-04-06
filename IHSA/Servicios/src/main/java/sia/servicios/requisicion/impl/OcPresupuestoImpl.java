/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.requisicion.impl;

import com.newrelic.api.agent.Trace;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import sia.modelo.OcPresupuesto;
import sia.modelo.presupuesto.vo.PresupuestoDetVO;
import sia.modelo.presupuesto.vo.PresupuestoVO;
import sia.modelo.sistema.AbstractFacade;
import sia.servicios.sistema.impl.SiParametroImpl;
import sia.util.UtilLog4j;

/**
 *
 * @author jcarranza
 */
@Stateless 
public class OcPresupuestoImpl extends AbstractFacade<OcPresupuesto> {

    private static final UtilLog4j LOGGER = UtilLog4j.log;

    @Inject
    private OcPresupuestoDetalleImpl ocPresupuestoDetalleRemote;
    @Inject
    private SiParametroImpl parametrosSistemaServicioRemoto;

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public OcPresupuestoImpl() {
        super(OcPresupuesto.class);
    }

    
    public List<SelectItem> getPresupuestoItems(int apCampoID, boolean all) {
        UtilLog4j.log.info(this, "#getPresupuestoItems ");
        ArrayList<SelectItem> lst = new ArrayList<SelectItem>();
        try {
            String query = "select a.id, a.nombre, a.codigo "
                    + " from oc_presupuesto a  "
                    + " where a.eliminado =  false "
                    + " and a.ap_campo = " + apCampoID;

            UtilLog4j.log.info(this, "query" + query);

            List<Object[]> lo = em.createNativeQuery(query).getResultList();
            SelectItem item = null;
            if (lo != null) {
                for (Object[] objects : lo) {
                    item = new SelectItem((Integer) objects[0], (String) objects[1]);
                    lst.add(item);
                }
            }
        } catch (Exception e) {
            UtilLog4j.log.error(this, "Error: al obtener los proyectos OT'S " + e.getMessage(), e);
            lst = new ArrayList<SelectItem>();
        }
        return lst;
    }

    
    public List<PresupuestoVO> getPresupuestos(int apCampoID, boolean all) {
        UtilLog4j.log.info(this, "#getPresupuestoItems ");
        ArrayList<PresupuestoVO> lst = new ArrayList<PresupuestoVO>();
        try {
            String query = "select a.id, a.nombre, a.codigo "
                    + " from oc_presupuesto a  "
                    + " where a.eliminado =  false "
                    + " and a.ap_campo = " + apCampoID;

            UtilLog4j.log.info(this, "query" + query);

            List<Object[]> lo = em.createNativeQuery(query).getResultList();
            PresupuestoVO item = null;
            if (lo != null) {
                for (Object[] objects : lo) {
                    item = new PresupuestoVO((Integer) objects[0], (String) objects[1], (String) objects[2]);
                    lst.add(item);
                }
            }
        } catch (Exception e) {
            UtilLog4j.log.error(this, "Error: al obtener los proyectos OT'S " + e.getMessage(), e);
            lst = new ArrayList<PresupuestoVO>();
        }
        return lst;
    }

    
    public PresupuestoVO getPresupuesto(int presupuestoID, boolean all) {
        UtilLog4j.log.info(this, "#getPresupuesto ");
        PresupuestoVO vo = null;
        try {
            String query = "select a.id, a.nombre, a.codigo "
                    + " from oc_presupuesto a  "
                    + " where a.eliminado =  false "
                    + " and a.id = " + presupuestoID;

            UtilLog4j.log.info(this, "query" + query);

            List<Object[]> lo = em.createNativeQuery(query).getResultList();
            if (lo != null) {
                for (Object[] objects : lo) {
                    vo = new PresupuestoVO((Integer) objects[0], (String) objects[1], (String) objects[2]);
                }
            }
        } catch (Exception e) {
            UtilLog4j.log.error(this, "Error: al obtener los proyectos OT'S " + e.getMessage(), e);
            vo = new PresupuestoVO();
        }
        return vo;
    }
    
    @Trace
    private void copiarArchivo(String pathOrigen, String pathDestino) throws Exception {
        File copied = new File(pathDestino);
        File original = new File(pathOrigen);
        try (InputStream in = new BufferedInputStream(new FileInputStream(original));
                OutputStream out = new BufferedOutputStream(new FileOutputStream(copied))) {
            byte[] buffer = new byte[1024];
            int lengthRead;
            while ((lengthRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, lengthRead);
                out.flush();
            }
        } catch (Exception ex) {
            LOGGER.error(this, "", ex);
            throw new Exception(ex.getMessage());
        }
    }
    
    
    @Trace(dispatcher = true)
    public File generarExcel(OcPresupuesto presupuesto, String pathOrigen, String pathDestino) throws Exception {
        File fileTemp = null;
        copiarArchivo(pathOrigen, pathDestino);
        fileTemp = new File(pathDestino);
        if (fileTemp.exists() && presupuesto != null && presupuesto.getId() > 0) {
            try (InputStream inputDocument = new FileInputStream(fileTemp);) {            
                OPCPackage pkg = OPCPackage.open(inputDocument);
                XSSFWorkbook wb = new XSSFWorkbook(pkg);
                wb.setSheetName(0, presupuesto.getNombre());
                XSSFSheet myWorksheet = wb.getSheetAt(0);
                cargarExcel(myWorksheet, wb, presupuesto);
                
                try (OutputStream outputFile = new FileOutputStream(fileTemp);) {
                    wb.write(outputFile);
                    outputFile.close();
                    pkg.close();
                } catch (Exception e) {
                    LOGGER.info(this, e.getMessage(), e);
                }
            } catch (Exception e) {
                LOGGER.info(this, e.getMessage(), e);
            }
        }
        return fileTemp;
    }
        
    
    @Trace(dispatcher = true)
    public File generarExcel(OcPresupuesto presupuesto, File fileTemp) throws Exception {

        try {
            if (presupuesto != null && presupuesto.getId() > 0) {
                String REPOSITORYPATH = parametrosSistemaServicioRemoto.find(1).getUploadDirectory();

                fileTemp = new File(REPOSITORYPATH + "temporal.xlsx");
                Workbook workbook = new XSSFWorkbook();
                Sheet pagina = workbook.createSheet(presupuesto.getNombre());

                cargarExcel(pagina, workbook, presupuesto);
                FileOutputStream file = new FileOutputStream(fileTemp);
                workbook.write(file);
                file.close();
            }
        } catch (Exception e) {
            LOGGER.info(this, e.getMessage(), e);
        }
        return fileTemp;
    }

    @Trace
    private void cargarExcel(Sheet pagina, Workbook wb, OcPresupuesto presupuesto) throws Exception {
        boolean completo = true;
        int xx = 0;
        int yy = 4;
        for (SelectItem anio : ocPresupuestoDetalleRemote.getAniosItems(presupuesto.getId(), false)) {
            for (SelectItem mes : ocPresupuestoDetalleRemote.getMesesItems(presupuesto.getId(), (int) anio.getValue(), false)) {
                for (PresupuestoDetVO linea : ocPresupuestoDetalleRemote.getPresupuestoDet(presupuesto.getId(), presupuesto.getApCampo().getId(),(int) anio.getValue(), (int) mes.getValue(), completo, false, false, true)) {
                    if (completo) {
                        excelCargarLineaCompleta(pagina, wb, linea, xx, yy, ((int) mes.getValue()+(((int) anio.getValue() -1) * 12)));
                    } else {
                        excelCargarLinea(pagina, wb, linea, xx, yy, ((int) mes.getValue()+(((int) anio.getValue() -1) * 12)));
                    }
                    yy++;
                }
                yy = 4;
                if (completo) {
                    completo = false;
                    xx += 21;
                } else {
                    xx += 10;
                }
            }
        }
        borrarEncabezado(pagina, xx, yy, 610);
    }
    
    private void borrarEncabezado(Sheet pagina,int x, int y, int max){
        for(int i = 0; i < y; i++){
            Row fila = pagina.getRow(i);
            for(int o = x; o < max; o++){
                Cell cell = fila.createCell(o);
            }        
        }
    
    }

    @Trace
    private int excelCargarLineaCompleta(Sheet pagina, Workbook wb, PresupuestoDetVO det, int x, int y, int mes) throws Exception {
        Row fila = pagina.createRow(y);
        Row filaTitulo = pagina.getRow(0);
        setValueExcel(fila, det.getIdPresupuesto(), 0, x++, y);
        setValueExcel(fila, det.getActPetroleraCodigo(), 0, x++, y);
        setValueExcel(fila, det.getActPetroleraNombre(), 0, x++, y);
        setValueExcel(fila, det.getUnidadCostoCodigo(), 0, x++, y);
        setValueExcel(fila, det.getUnidadCostoNombre(), 0, x++, y);
        setValueExcel(fila, det.getTareaCodigo(), 0, x++, y);
        setValueExcel(fila, det.getTareaNombre(), 0, x++, y);
        setValueExcel(fila, det.getSubTareaCodigo(), 0, x++, y);
        setValueExcel(fila, det.getSubTareaNombre(), 0, x++, y);
        setValueExcel(fila, det.getOtsCCTexto(), 0, x++, y); //setValueExcel(fila, det.getProyectoOtCodigo(), 0, x++, y);
        setValueExcel(fila, det.getOtsTexto(), 0, x++, y); //setValueExcel(fila, det.getProyectoOtNombre(), 0, x++, y);
        
        int celTitulo = x++;
        setValueExcel(fila, det.getManoObraCn().doubleValue(), 0, celTitulo, y);
        setValueExcel(filaTitulo, "Mes "+mes, 0, celTitulo, 0);
        
        setValueExcel(fila, det.getManoObraEx().doubleValue(), 0, x++, y);
        setValueExcel(fila, det.getBienasCn().doubleValue(), 0, x++, y);
        setValueExcel(fila, det.getBienesEx().doubleValue(), 0, x++, y);
        setValueExcel(fila, det.getServiciosCn().doubleValue(), 0, x++, y);
        setValueExcel(fila, det.getServiciosEx().doubleValue(), 0, x++, y);
        setValueExcel(fila, det.getCapacitacionCn().doubleValue(), 0, x++, y);
        setValueExcel(fila, det.getCapacitacionEx().doubleValue(), 0, x++, y);
        setValueExcel(fila, det.getTransferenciaTec().doubleValue(), 0, x++, y);
        setValueExcel(fila, det.getInfraestructura().doubleValue(), 0, x++, y);
        return x;
    }

    @Trace
    private int excelCargarLinea(Sheet pagina, Workbook wb, PresupuestoDetVO det, int x, int y, int mes) throws Exception {
        Row fila = pagina.getRow(y);        
        Row filaTitulo = pagina.getRow(0);
        int celTitulo = x++;
        setValueExcel(fila, det.getManoObraCn().doubleValue(), 0, celTitulo, y);
        setValueExcel(filaTitulo, "Mes "+mes, 0, celTitulo, 0);
        
        setValueExcel(fila, det.getManoObraEx().doubleValue(), 0, x++, y);
        setValueExcel(fila, det.getBienasCn().doubleValue(), 0, x++, y);
        setValueExcel(fila, det.getBienesEx().doubleValue(), 0, x++, y);
        setValueExcel(fila, det.getServiciosCn().doubleValue(), 0, x++, y);
        setValueExcel(fila, det.getServiciosEx().doubleValue(), 0, x++, y);
        setValueExcel(fila, det.getCapacitacionCn().doubleValue(), 0, x++, y);
        setValueExcel(fila, det.getCapacitacionEx().doubleValue(), 0, x++, y);
        setValueExcel(fila, det.getTransferenciaTec().doubleValue(), 0, x++, y);
        setValueExcel(fila, det.getInfraestructura().doubleValue(), 0, x++, y);
        return x;
    }

    private void setValueExcel(Row row, Object valor, int hoja, int x, int y) throws Exception {
        try {
            Cell cell = row.createCell(x);
//            Cell cell = my_worksheet.getRow(x).getCell(y);
            if (cell == null) {
//                cell = my_worksheet.getRow(x).createCell(y);
                cell = row.createCell(x);
            }
            if (valor instanceof String) {
                cell.setCellValue(String.valueOf(valor));
            }
            if (valor instanceof Double) {
                Double valorAux = (Double) valor;
                cell.setCellValue(valorAux.doubleValue());
            }
            if (valor instanceof Integer) {
                Integer valorAux = (Integer) valor;
                cell.setCellValue(valorAux);
            }
        } catch (Exception e) {
            LOGGER.fatal(this, e.getMessage());
        }
    }

}
