/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.sgl.combustible.impl;

import static com.google.common.base.Preconditions.checkNotNull;
import java.io.File;
import java.util.Map;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import sia.excepciones.SIAException;

/**
 *
 * @author ihsa
 */
public class Reporte {

    private String reportName;
    private JasperPrint jasperPrint;
    private JRDataSource reportDataSource;
    private Map<String, Object> parameters;
    private String basePath;

    public Map<String, Object> getParameters() {
	return parameters;
    }

    public void setParameters(Map<String, Object> parameters) {
	this.parameters = parameters;
    }

    public String getReportName() {
	return reportName;
    }

    public void setReportName(String reportName) {
	this.reportName = reportName;
    }

    public JasperPrint getJasperPrint() {
	return jasperPrint;
    }

    public void setDataSource(JRDataSource reportDataSource) {
	this.reportDataSource = reportDataSource;
    }

    public String getBasePath() {
	return basePath;
    }

    public void setBasePath(String basePath) {
	this.basePath = basePath;
    }

    public void doIt() throws SIAException {

	checkNotNull(reportName, "Debe proporcionar un nombre de reporte.");
	checkNotNull(reportDataSource, "Debe proporcionar una fuente de datos para el reporte.");
	checkNotNull(parameters, "Debe proporcionar los parámetros para el reporte.");

	try {
	    jasperPrint
		    = JasperFillManager.fillReport(
			    basePath + File.separator + "reports" + File.separator + reportName,
			    parameters,
			    reportDataSource
		    );
	} catch (JRException e) {
	    throw new SIAException("" + e.getMessage());
	}
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

}
