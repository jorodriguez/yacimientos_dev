/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.sgl.sistema.servlets;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import sia.constantes.Constantes;
import sia.excepciones.SIAException;
import sia.modelo.Compania;
import sia.modelo.SgAsignarAccesorio;
import sia.modelo.SgAsignarVehiculo;
import sia.modelo.SgChecklistLlantas;
import sia.modelo.SgDetalleRutaTerrestre;
import sia.modelo.SgHuespedHotel;
import sia.modelo.SgVehiculo;
import sia.modelo.SgViaje;
import sia.modelo.SgViajeVehiculo;
import sia.modelo.Usuario;
import sia.modelo.sgl.hotel.vo.SgHuespedHotelServicioVo;
import sia.modelo.sgl.viaje.vo.ViajeroVO;
import sia.modelo.sgl.vo.CheckListDetalleVo;
import sia.modelo.sgl.vo.SgHuespedHotelVo;
import sia.modelo.sgl.vo.SgTarjetaBancariaVo;
import sia.servicios.campo.nuevo.impl.ApCampoUsuarioRhPuestoImpl;
import sia.servicios.catalogos.impl.CompaniaImpl;
import sia.servicios.catalogos.impl.GerenciaImpl;
import sia.servicios.catalogos.impl.UsuarioImpl;
import sia.servicios.sgl.accesorio.impl.SgAsignarAccesorioImpl;
import sia.servicios.sgl.impl.SgChecklistDetalleImpl;
import sia.servicios.sgl.impl.SgHuespedHotelImpl;
import sia.servicios.sgl.impl.SgHuespedHotelServicioImpl;
import sia.servicios.sgl.impl.SgVehiculoChecklistImpl;
import sia.servicios.sgl.vehiculo.impl.SgAsignarVehiculoImpl;
import sia.servicios.sgl.vehiculo.impl.SgChecklistLlantasImpl;
import sia.servicios.sgl.viaje.impl.SgDetalleRutaTerrestreImpl;
import sia.servicios.sgl.viaje.impl.SgSolicitudViajeImpl;
import sia.servicios.sgl.viaje.impl.SgViajeImpl;
import sia.servicios.sgl.viaje.impl.SgViajeVehiculoImpl;
import sia.servicios.sgl.viaje.impl.SgViajeroImpl;
import sia.servicios.sistema.impl.SiManejoFechaImpl;
import sia.util.UtilLog4j;

/**
 *
 * @author mluis
 */
@WebServlet(name = "GeneraCarta", urlPatterns = {"/GeneraCarta"})
public class GeneraCarta extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Inject
    private SgAsignarVehiculoImpl sgAsignarVehiculoImpl;
    @Inject
    private SgAsignarAccesorioImpl sgAsignarAccesorioImpl;
    @Inject
    private CompaniaImpl companiaImpl;
    @Inject
    private SgHuespedHotelImpl sgHuespedHotelImpl;
    @Inject
    private SgChecklistDetalleImpl sgChecklistDetalleImpl;
    @Inject
    private SgChecklistLlantasImpl sgChecklistLlantasImpl;
    @Inject
    private SgVehiculoChecklistImpl sgVehiculoChecklistImpl;
    @Inject
    private SgViajeImpl sgViajeImpl;
    @Inject
    private SgViajeVehiculoImpl sgViajeVehiculoImpl;
    @Inject
    private SgDetalleRutaTerrestreImpl sgDetalleRutaTerrestreImpl;
    @Inject
    private SgViajeroImpl sgViajeroImpl;
    @Inject
    private SgSolicitudViajeImpl sgSolicitudViajeImpl;
    @Inject
    private ApCampoUsuarioRhPuestoImpl apCampoUsuarioRhPuestoImpl;
    @Inject
    private GerenciaImpl gerenciaImpl;
    @Inject
    private SgHuespedHotelServicioImpl sgHuespedHotelServicioImpl;
    @Inject
    private UsuarioImpl usuarioImpl;
    @Inject
    private SiManejoFechaImpl siManejoFechaLocal;

    public void generaPDF(HttpServletRequest request, HttpServletResponse response)
	    throws ServletException, IOException, DocumentException {

	String a = request.getParameter("a");
	String b = request.getParameter("b");
	String acc = request.getParameter("ALZY");
	String c = request.getParameter("ALHL");

	response.setContentType("application/pdf"); // Code 1
	Document document = new Document(PageSize.LETTER, 30, 30, 30, 30);
	PdfWriter writer = PdfWriter.getInstance(document, response.getOutputStream()); // Code 2
	Rectangle rct = new Rectangle(36, 54, 559, 788);
	//Definimos un nombre y un tamaño para el PageBox los nombres posibles son: “crop”, “trim”, “art” and “bleed”.
	writer.setBoxSize("art", rct);
	HeaderFooter event = new HeaderFooter();
	event.setCompania(companiaImpl.find("IHI070320FI3"));
	writer.setPageEvent(event);
	document.open();
	agregarMetaDatos(document);
	document.open();
	agregarMetaDatos(document);
	try {
	    if (a != null) {
		SgAsignarVehiculo sgAsignarVehiculo = sgAsignarVehiculoImpl.find(Integer.parseInt(request.getParameter("a")));
		if (sgAsignarVehiculo.getSiOperacion().getId() == 1) {
		    generarCartaAsignaVehiculo(document, sgAsignarVehiculo);
		} else if (sgAsignarVehiculo.getSiOperacion().getId() == 2) {
		    generarCartaRecibeVehiculo(document, sgAsignarVehiculo);
		}

		document.close();
	    }
	    if (acc != null) {
		SgAsignarAccesorio sgAsignarAccesorio = sgAsignarAccesorioImpl.find(Integer.parseInt(acc));
		generarCartaAsignaAccesorio(document, sgAsignarAccesorio);
		document.close();
	    }
	    if (b != null) {
		String[] cadena = b.split(",");
		SgHuespedHotel sgHuespedHotel = sgHuespedHotelImpl.find(Integer.parseInt(cadena[0]));
		Usuario uVistoBueno = this.usuarioImpl.find(cadena[1]);
//                String voBoCarta = cadena[1];
		String voBoCarta = uVistoBueno.getNombre();
		UtilLog4j.log.info(this, "vo Bo Carta: " + voBoCarta);
		// compara si la carta sera la de datos bancarios o no
		if (Integer.valueOf(cadena[2]) == Constantes.CARTA_DATOS_BANCARIOS) {
		    generarCartaHuespedDatosBancarios(document, sgHuespedHotel, voBoCarta);
		} else {
		    generarCartaHuesped(document, sgHuespedHotel, voBoCarta);
		}

		document.close();
	    }
	    if (c != null) {
		UtilLog4j.log.info(this, "c:" + c);
		SgViaje sgViaje = sgViajeImpl.find(Integer.parseInt(c));
		generarDocumentoViaje(document, sgViaje);
		document.close();
	    }
	} catch (Exception e) {
	    UtilLog4j.log.info(this, "Dentro de la excepción de generar carta" + " ERROR :  :  :" + e.getMessage() + " Causa :  : :" + e.getCause().toString());
	}
    }

    private void printMessage(String message, HttpServletRequest request, HttpServletResponse response) {
	try {
	    PrintWriter output = response.getWriter();
	    output.println("<html>");
	    output.println("<head>");
	    output.println("<title>Sistema Integral de Administración</title>");
	    output.println("</head>");
	    output.println("<body >");
	    output.println("<h1 style=\"color: red; font-size: 12px;\">" + message + "</h1>");
	    output.println("</body>");
	    output.println("</html>");
	} catch (IOException ioe) {
	    System.out.print(ioe.getMessage());
	}
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
	    throws ServletException, IOException {
	try {
	    generaPDF(request, response);
	} catch (DocumentException ex) {
	    Logger.getLogger(GeneraCarta.class.getName()).log(Level.SEVERE, null, ex);
	}
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
	    throws ServletException, IOException {
	try {
	    generaPDF(request, response);
	} catch (DocumentException ex) {
	    Logger.getLogger(GeneraCarta.class.getName()).log(Level.SEVERE, null, ex);
	}
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
	return "Short description";
    }// </editor-fold>

    private void agregarMetaDatos(Document document) {
	document.addTitle("Documentos generados automaticos por el SIA");
	document.addSubject("Diferentes tipos");
	document.addAuthor("Marino Luis");
    }

    private void generarCartaAsignaVehiculo(Document document, SgAsignarVehiculo sgAsignarVehiculo) throws DocumentException {
	SimpleDateFormat sdf = new SimpleDateFormat("dd 'de' MMMMM 'de' yyyy", new Locale("es", "ES"));
	//Crea el encabezado
	document.add(encabezado());
	// Create a 2-column table.
	PdfPTable table = new PdfPTable(1);
	table.getDefaultCell().setBorder(0);
	PdfPCell pCell = new PdfPCell(new Paragraph(sgAsignarVehiculo.getSgVehiculo().getSgOficina().getSgDireccion().getSiCiudad().getNombre() + " " + sgAsignarVehiculo.getSgVehiculo().getSgOficina().getSgDireccion().getSiEstado().getNombre() + " a " + sdf.format(new Date()),
		new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.NORMAL)));
	pCell.setBorder(0);
	pCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
	pCell.setPaddingBottom(12);
	table.addCell(pCell);
	// Add the first row.
	pCell = new PdfPCell(new Paragraph("En el presente acto se hace entrega de la unidad:",
		new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.NORMAL)));
	pCell.setBorder(0);
	pCell.setPaddingBottom(12);
	table.addCell(pCell);
	pCell = new PdfPCell(new Paragraph());
	pCell.setBorder(0);
	pCell.setPaddingBottom(12);
	table.addCell(pCell);

	//Datos del vehiculo
	table.addCell(datosVehiculo(sgAsignarVehiculo.getSgVehiculo()));
	//fin de los datos del vehiculo
	pCell = new PdfPCell(new Paragraph("Recibe: ", new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD)));
	pCell.setBorder(0);
	pCell.setPaddingBottom(12);
	table.addCell(pCell);

	Paragraph p = new Paragraph("El usuario " + sgAsignarVehiculo.getUsuario().getNombre() + ", de ahora en adelante USUARIO, "
		+ "realice las funciones inherentes al cargo que ha sido encomendado por la Dirección General "
		+ "de Iberoamericana de Hidrocarburos S.A. de C.V. precedida por "
		+ "el Ing. Eduardo . En el entendido de que dicha unidad es propiedad de "
		+ "IBEROAMERICANA DE HIDROCARBUROS S. A. de C. V., y que a partir de la presente "
		+ "fecha, este vehículo queda bajo resguardo del USUARIO, "
		+ "quien acepta hacerse responsable directo e incondicional del vehiculo en cuestión; "
		+ "dando buen uso y trato de este, y sabedor es de que queda estrictamente prohibido darle uso diferente del "
		+ "que la presente acta puntualizada. Ninguna persona que no sea el USUARIO podrá conducir esta unidad, "
		+ "de no ser que exista un permiso por escrito de la Dirección de "
		+ "Iberoamericana de Hidrocarburos S.A. de C.V. Dicha unidad se otorga en "
		+ "calidad de medio de trasporte al servicio de las necesidades de la Oficina Central "
		+ "en México a la que es destinada.",
		new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.NORMAL));
	pCell = new PdfPCell(p);
	pCell.setBorder(0);
	pCell.setHorizontalAlignment(Element.ALIGN_JUSTIFIED);
	pCell.setPaddingBottom(12);
	table.addCell(pCell);

	p = new Paragraph("UBICACIÓN DESTINADO DEL VEHICULO: Monterrey, Nuevo. León"
		+ " ESTADO DE VEHICULO:	La unidad se encuentra en perfectas condiciones, tanto mecánicas como de hojalatería y pintura.",
		new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.NORMAL));

	pCell = new PdfPCell(p);
	pCell.setBorder(0);
	pCell.setHorizontalAlignment(Element.ALIGN_JUSTIFIED);
	pCell.setPaddingBottom(12);
	table.addCell(pCell);

	p = new Paragraph("EL USUARIO, abajo firmante, acepta de común acuerdo conjuntamente con la Dirección General de Iberoamericana de Hidrocarburos, "
		+ "los términos de esta Acta y la Responsabilidad a que se compromete por el tiempo en que esta unidad se encuentre "
		+ "bajo su resguardo personal. En caso de que la unidad sufra desperfectos, o robo "
		+ "parcial o total por causas imputables al USUARIO, este se compromete a resarcir a "
		+ "entera satisfacción de la Dirección General de Iberoamericana de "
		+ "Hidrocarburos S.A. de C.V. en moneda Nacional en curso corriente (Pesos Mexicanos) "
		+ "el importe necesario para cubrir dichos desperfectos o robo parcial o total que "
		+ "no cubra el correspondiente seguro.",
		new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.NORMAL));

	pCell = new PdfPCell(p);
	pCell.setBorder(0);
	pCell.setHorizontalAlignment(Element.ALIGN_JUSTIFIED);
	pCell.setPaddingBottom(12);
	table.addCell(pCell);
	// Add the table to the document.
	p = new Paragraph("Se hace entrega de la “Normativa para el Uso de Vehículos Propiedad de la Empresa” y de la circular "
		+ "“La Seguridad al Conducir Vehículos” cuyo cumplimiento tiene carácter obligatorio.",
		new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.NORMAL));

	pCell = new PdfPCell(p);
	pCell.setBorder(0);
	pCell.setHorizontalAlignment(Element.ALIGN_JUSTIFIED);
	pCell.setPaddingBottom(12);
	table.addCell(pCell);

	p = new Paragraph("En el presente acto se identifica al USUARIO, credencial de elector.",
		new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.NORMAL));

	pCell = new PdfPCell(p);
	pCell.setBorder(0);
	pCell.setHorizontalAlignment(Element.ALIGN_JUSTIFIED);
	pCell.setPaddingBottom(25);
	table.addCell(pCell);

	p = new Paragraph("RECIBI", new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.NORMAL));
	pCell = new PdfPCell(p);
	pCell.setBorder(0);
	pCell.setHorizontalAlignment(Element.ALIGN_CENTER);
	pCell.setPaddingBottom(35);
	table.addCell(pCell);

	////
	p = new Paragraph(sgAsignarVehiculo.getUsuario().getNombre(), new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.NORMAL));
	pCell = new PdfPCell(p);
	pCell.setBorder(0);
	pCell.setHorizontalAlignment(Element.ALIGN_CENTER);
	pCell.setPaddingTop(40);
	pCell.setPaddingBottom(40);
	table.addCell(pCell);
	//------------------------------------- ///contenido
	document.add(table);
////        document.add(pie());//Pie pagina uno
	///--------------------------------------

	if (!sgChecklistDetalleImpl.getAllItemsChecklistList(sgAsignarVehiculo.getSgChecklist().getId(), Constantes.NO_ELIMINADO).isEmpty()) {//Si no esta vacia
	    ///-------------------------------------
	    UtilLog4j.log.info(this, "1");
	    document.newPage();
	    document.add(encabezado());//Pagina dos
	    //-**------------------
	    PdfPTable tk = new PdfPTable(1);
	    tk.setHorizontalAlignment(150);
	    p = new Paragraph("Kilometrage actual: " + String.valueOf(sgVehiculoChecklistImpl.buscarPorChecklist(sgAsignarVehiculo.getSgChecklist()).getSgKilometraje().getKilometraje() + " KM"), new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD));
	    UtilLog4j.log.info(this, "2");
	    pCell = new PdfPCell(p);
	    pCell.setBorder(0);
	    pCell.setHorizontalAlignment(Element.ALIGN_LEFT);
	    pCell.setPaddingBottom(20);
	    pCell.setPaddingTop(10);
	    tk.addCell(pCell);
	    document.add(tk);
	    //
	    PdfPTable tCheck = new PdfPTable(1);
	    p = new Paragraph("Checklist general", new Font(Font.FontFamily.TIMES_ROMAN, 13, Font.BOLD));
	    pCell = new PdfPCell(p);
	    pCell.setBorder(0);
	    pCell.setHorizontalAlignment(Element.ALIGN_CENTER);
	    pCell.setPaddingBottom(20);
	    pCell.setPaddingTop(20);
	    tCheck.addCell(pCell);
	    document.add(tCheck);
	    ////detalle checkas  ---------------------------

	    PdfPTable pdfPTable = tablaDetalleCheckList(sgAsignarVehiculo);
	    if (pdfPTable != null) {
		document.add(pdfPTable);
	    }
	}
	UtilLog4j.log.info(this, "3");
	if (sgChecklistLlantasImpl.buscarPorChecklist(sgAsignarVehiculo.getSgChecklist()) != null) {
	    UtilLog4j.log.info(this, "4");
	    //****----------------------------------
	    PdfPTable tLlanta = new PdfPTable(1);
	    p = new Paragraph("Checklist llantas", new Font(Font.FontFamily.TIMES_ROMAN, 13, Font.BOLD));
	    pCell = new PdfPCell(p);
	    pCell.setBorder(0);
	    pCell.setHorizontalAlignment(Element.ALIGN_CENTER);
	    pCell.setPaddingBottom(10);
	    pCell.setPaddingTop(30);
	    tLlanta.addCell(pCell);
	    document.add(tLlanta);
	    PdfPTable pdfPTable = tablaCheckListLlantas(sgAsignarVehiculo);
	    if (pdfPTable != null) {
		document.add(pdfPTable);
	    }
	    //PIE
	}
////        if (!sgChecklistDetalleImpl.getAllItemsChecklistList(sgAsignarVehiculo.getSgChecklist(), Constantes.NO_ELIMINADO).isEmpty()
////                || sgChecklistLlantasImpl.buscarPorChecklist(sgAsignarVehiculo.getSgChecklist()) != null) {
//////////            document.add(pie());
////        }
    }

    private void generarCartaRecibeVehiculo(Document document, SgAsignarVehiculo sgAsignarVehiculo) throws DocumentException {
	SimpleDateFormat sdf = new SimpleDateFormat("dd 'de' MMMMM 'de' yyyy", new Locale("es", "ES"));
	//Crea el encabezado
	document.add(encabezado());
	// Create a 2-column table.
	PdfPTable table = new PdfPTable(1);
	table.getDefaultCell().setBorder(0);
	PdfPCell pCell = new PdfPCell(new Paragraph(sgAsignarVehiculo.getSgVehiculo().getSgOficina().getSgDireccion().getSiCiudad().getNombre() + " " + sgAsignarVehiculo.getSgVehiculo().getSgOficina().getSgDireccion().getSiEstado().getNombre() + " a " + sdf.format(new Date()),
		new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.NORMAL)));
	pCell.setBorder(0);
	pCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
	pCell.setPaddingBottom(12);
	table.addCell(pCell);
	//REcepcion de vehiculo
	pCell = new PdfPCell(new Paragraph("Recepción de vehículo", new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD)));
	pCell.setBorder(0);
	pCell.setHorizontalAlignment(Element.ALIGN_CENTER);
	pCell.setPaddingBottom(12);
	table.addCell(pCell);

	//Datos del vehiculo
	table.addCell(datosVehiculo(sgAsignarVehiculo.getSgVehiculo()));
	document.add(table);
	if (!sgChecklistDetalleImpl.getAllItemsChecklistList(sgAsignarVehiculo.getSgChecklist().getId(), Constantes.NO_ELIMINADO).isEmpty()) {//Si no esta vacia
	    ///-------------------------------------
	    UtilLog4j.log.info(this, "1");
	    //-**------------------
	    PdfPTable tk = new PdfPTable(1);
	    tk.setHorizontalAlignment(150);
	    Paragraph p = new Paragraph("Kilometrage actual: " + String.valueOf(sgVehiculoChecklistImpl.buscarPorChecklist(sgAsignarVehiculo.getSgChecklist()).getSgKilometraje().getKilometraje() + " KM"), new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD));
	    UtilLog4j.log.info(this, "2");
	    pCell = new PdfPCell(p);
	    pCell.setBorder(0);
	    pCell.setHorizontalAlignment(Element.ALIGN_LEFT);
	    pCell.setPaddingBottom(20);
	    pCell.setPaddingTop(10);
	    tk.addCell(pCell);
	    document.add(tk);
	    //
	    PdfPTable tCheck = new PdfPTable(1);
	    p = new Paragraph("Checklist general", new Font(Font.FontFamily.TIMES_ROMAN, 13, Font.BOLD));
	    pCell = new PdfPCell(p);
	    pCell.setBorder(0);
	    pCell.setHorizontalAlignment(Element.ALIGN_CENTER);
	    pCell.setPaddingBottom(20);
	    pCell.setPaddingTop(20);
	    tCheck.addCell(pCell);
	    document.add(tCheck);
	    ////detalle checkas  ---------------------------

	    PdfPTable pdfPTable = tablaDetalleCheckList(sgAsignarVehiculo);
	    if (pdfPTable != null) {
		document.add(pdfPTable);
	    }
	}
	UtilLog4j.log.info(this, "3");
	if (sgChecklistLlantasImpl.buscarPorChecklist(sgAsignarVehiculo.getSgChecklist()) != null) {
	    UtilLog4j.log.info(this, "4");
	    //****----------------------------------
	    PdfPTable tLlanta = new PdfPTable(1);
	    Paragraph p = new Paragraph("Checklist llantas", new Font(Font.FontFamily.TIMES_ROMAN, 13, Font.BOLD));
	    pCell = new PdfPCell(p);
	    pCell.setBorder(0);
	    pCell.setHorizontalAlignment(Element.ALIGN_CENTER);
	    pCell.setPaddingBottom(10);
	    pCell.setPaddingTop(30);
	    tLlanta.addCell(pCell);
	    document.add(tLlanta);
	    PdfPTable pdfPTable = tablaCheckListLlantas(sgAsignarVehiculo);
	    if (pdfPTable != null) {
		document.add(pdfPTable);
	    }
	    //PIE
	}
    }

    private void generarCartaHuesped(Document document, SgHuespedHotel sgHuespedHotel, String voBoCarta) throws DocumentException {
	UtilLog4j.log.info(this, "generarCartaaHuesped");

	Compania compania = companiaImpl.find(Constantes.RFC_IHSA);

	List<SgHuespedHotelServicioVo> serviciosIncluidosTarifa = this.sgHuespedHotelServicioImpl.findAllBySgHuespedHotel(sgHuespedHotel.getId().intValue(), false);
	List<SgHuespedHotelServicioVo> serviciosFacturaEmpresa = this.sgHuespedHotelServicioImpl.findAllBySgHuespedHotel(sgHuespedHotel.getId().intValue(), true);
	List<SgHuespedHotelVo> huespedes = this.sgHuespedHotelImpl.findAllSgHuespedHotelByNumeroReservacion(sgHuespedHotel.getNumeroHabitacion());

	UtilLog4j.log.info(this, "serviciosIncluidosTarifa.size(): " + serviciosIncluidosTarifa.size());
	UtilLog4j.log.info(this, "serviciosFacturaEmpresa.size(): " + serviciosFacturaEmpresa.size());

	//Crea el encabezado
	document.add(encabezado());

//        String persona = sgHuespedHotel.getSgDetalleSolicitudEstancia().getUsuario() != null ? sgHuespedHotel.getSgDetalleSolicitudEstancia().getUsuario().getNombre() : sgHuespedHotel.getSgDetalleSolicitudEstancia().getSgInvitado().getNombre();
	// Create a 2-column table.
	// Create a 2-column table.
	PdfPTable table = new PdfPTable(1);
	PdfPCell pCell;

	table.setWidthPercentage(75);
	Paragraph p = new Paragraph(sgHuespedHotel.getSgHotelHabitacion().getSgHotel().getProveedor().getNombre(), new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD));
	pCell = new PdfPCell(p);
	pCell.setBorder(0);
	pCell.setPaddingTop(15);
	table.addCell(pCell);
	p = new Paragraph("At'n: .", new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD));
	pCell = new PdfPCell(p);
	pCell.setBorder(0);
	pCell.setHorizontalAlignment(Element.ALIGN_LEFT);
	table.addCell(pCell);
	//fin de los datos del vehiculo
	pCell = new PdfPCell(new Paragraph("Por medio del presente solicito la reservación de Hospedaje de la siguiente persona: ", new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.NORMAL)));
	pCell.setBorder(0);
	pCell.setPaddingTop(18);
	pCell.setPaddingBottom(12);
	table.addCell(pCell);
	document.add(table);

	//Headers Tabla Datos Huésped
	PdfPTable headerDatosHuesped = new PdfPTable(4);
	float[] medidaCeldasDatosHuesped = {1.65f, 1.25f, 1.25f, 1.25f};
	headerDatosHuesped.setWidths(medidaCeldasDatosHuesped);

	PdfPTable headerDatosHuesped1 = new PdfPTable(1);
	p = new Paragraph("Nombre y apellido", new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.BOLD));
	pCell = new PdfPCell(p);
	pCell.setBorder(1);
	pCell.setPaddingBottom(5);
	headerDatosHuesped1.addCell(pCell);
	pCell.setHorizontalAlignment(Element.ALIGN_CENTER);
	headerDatosHuesped.addCell(headerDatosHuesped1);

	PdfPTable headerDatosHuesped2 = new PdfPTable(1);
	p = new Paragraph("Fecha de entrada", new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.BOLD));
	pCell = new PdfPCell(p);
	pCell.setBorder(1);
	pCell.setPaddingBottom(5);
	headerDatosHuesped2.addCell(pCell);
	pCell.setHorizontalAlignment(Element.ALIGN_CENTER);
	headerDatosHuesped.addCell(headerDatosHuesped2);
	document.add(headerDatosHuesped);

	PdfPTable headerDatosHuesped3 = new PdfPTable(1);
	p = new Paragraph("Fecha de salida", new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.BOLD));
	pCell = new PdfPCell(p);
	pCell.setBorder(1);
	pCell.setPaddingBottom(5);
	headerDatosHuesped3.addCell(pCell);
	pCell.setHorizontalAlignment(Element.ALIGN_CENTER);
	headerDatosHuesped.addCell(headerDatosHuesped3);
	document.add(headerDatosHuesped);

	PdfPTable headerDatosHuesped4 = new PdfPTable(1);
	p = new Paragraph("Reservación", new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.BOLD));
	pCell = new PdfPCell(p);
	pCell.setBorder(1);
	pCell.setPaddingBottom(5);
	headerDatosHuesped4.addCell(pCell);
	pCell.setHorizontalAlignment(Element.ALIGN_CENTER);
	headerDatosHuesped.addCell(headerDatosHuesped4);
	document.add(headerDatosHuesped);

	document.add(tablaDatosHuesped(huespedes));

	p = new Paragraph("Cabe mencionar, que Iberoamericana de Hidrocarburos S.A. de C.V. tiene "
		+ " un convenio firmado con " + sgHuespedHotel.getSgHotelHabitacion().getSgHotel().getProveedor().getNombre() + ". La tarifa de este mes de  ____________ es de $ " + sgHuespedHotel.getSgHotelHabitacion().getPrecio().setScale(2, BigDecimal.ROUND_HALF_UP) + " + impuestos."
		+ " A continuación señalamos los gastos que se encuentran incluidos en la tarifa del hotel según el convenio establecido y los gastos que serán cubiertos por nuestra compañía:",
		new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.NORMAL));

	PdfPTable ta = new PdfPTable(1);
	pCell = new PdfPCell(p);
	pCell.setBorder(0);
	pCell.setHorizontalAlignment(Element.ALIGN_JUSTIFIED);
	pCell.setPaddingBottom(15);
	pCell.setPaddingTop(15);
	ta.addCell(pCell);
	document.add(ta);

	//Headers tabla consumo
	PdfPTable headerTablaConsumo = new PdfPTable(2);
	float[] medidaCeldas = {2.70f, 2.70f};
	headerTablaConsumo.setWidths(medidaCeldas);

	PdfPTable header1 = new PdfPTable(1);
	p = new Paragraph("Incluidos en tarifa", new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.BOLD));
	pCell = new PdfPCell(p);
	pCell.setBorder(1);
	pCell.setPaddingBottom(5);
	header1.addCell(pCell);
	pCell.setHorizontalAlignment(Element.ALIGN_CENTER);
	headerTablaConsumo.addCell(header1);

	PdfPTable header2 = new PdfPTable(1);
	p = new Paragraph("Autorizados para facturación adicional", new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.BOLD));
	pCell = new PdfPCell(p);
	pCell.setBorder(1);
	pCell.setPaddingBottom(5);
	header2.addCell(pCell);
	pCell.setHorizontalAlignment(Element.ALIGN_CENTER);
	headerTablaConsumo.addCell(header2);
	document.add(headerTablaConsumo);

	//Tabla de consumo
	document.add(tablaConsumo(serviciosIncluidosTarifa, serviciosFacturaEmpresa));

	PdfPTable tabla = new PdfPTable(1);
	p = new Paragraph("Agradecemos de antemano las atenciones que brinda a la presente: ",
		new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.NORMAL));
	pCell = new PdfPCell(p);
	pCell.setBorder(0);
	pCell.setHorizontalAlignment(Element.ALIGN_JUSTIFIED);
	pCell.setPaddingBottom(5);
	tabla.addCell(pCell);
//        document.add(tabla);
//////        //Datos generales
//////        PdfPTable tas = new PdfPTable(1);
	p = new Paragraph("Agradecemos facturar a nombre de: ",
		new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD));
	pCell = new PdfPCell(p);
	pCell.setBorder(0);
	pCell.setHorizontalAlignment(Element.ALIGN_JUSTIFIED);
	pCell.setPaddingBottom(0);
	tabla.addCell(pCell);
	p = new Paragraph("Empresa: " + compania.getNombre(),
		new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL));
	pCell = new PdfPCell(p);
	pCell.setBorder(0);
	pCell.setHorizontalAlignment(Element.ALIGN_JUSTIFIED);

	pCell.setPaddingBottom(2);
	tabla.addCell(pCell);
	p = new Paragraph("Dirección: " + compania.getDomicilioFiscal(),
		new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL));
	pCell = new PdfPCell(p);
	pCell.setBorder(0);
	pCell.setHorizontalAlignment(Element.ALIGN_JUSTIFIED);
	pCell.setPaddingBottom(2);
	tabla.addCell(pCell);
//	p = new Paragraph("Ciudad: Distrito Federal",
//		new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL));
//	pCell = new PdfPCell(p);
//	pCell.setBorder(0);
//	pCell.setHorizontalAlignment(Element.ALIGN_JUSTIFIED);
//	pCell.setPaddingBottom(2);
//	tabla.addCell(pCell);
	p = new Paragraph("RFC: " + compania.getRfc(),
		new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL));
	pCell = new PdfPCell(p);
	pCell.setBorder(0);
	pCell.setHorizontalAlignment(Element.ALIGN_JUSTIFIED);
	pCell.setPaddingBottom(12);
	tabla.addCell(pCell);
	///Entrega de facctura
	p = new Paragraph("Agradecemos entregar la factura : ",
		new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD));
	pCell = new PdfPCell(p);
	pCell.setBorder(0);
	pCell.setHorizontalAlignment(Element.ALIGN_JUSTIFIED);
	pCell.setPaddingBottom(2);
	pCell.setPaddingTop(10);
	tabla.addCell(pCell);
	p = new Paragraph("Empresa: " + compania.getNombre(),
		new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL));
	pCell = new PdfPCell(p);
	pCell.setBorder(0);
	pCell.setHorizontalAlignment(Element.ALIGN_JUSTIFIED);
	pCell.setPaddingBottom(2);
	tabla.addCell(pCell);
	p = new Paragraph("Dirección: " + compania.getCalle() + " " + compania.getColonia(),
		new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL));
	pCell = new PdfPCell(p);
	pCell.setBorder(0);
	pCell.setHorizontalAlignment(Element.ALIGN_JUSTIFIED);
	pCell.setPaddingBottom(2);
	tabla.addCell(pCell);
	p = new Paragraph("Ciudad: " + compania.getCiudad(),
		new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL));
	pCell = new PdfPCell(p);
	pCell.setBorder(0);
	pCell.setHorizontalAlignment(Element.ALIGN_JUSTIFIED);
	pCell.setPaddingBottom(2);
	tabla.addCell(pCell);

	// Add the table to the document.
	p = new Paragraph("Quedo a sus órdenes para cualquier aclaración.",
		new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.NORMAL));

	pCell = new PdfPCell(p);
	pCell.setBorder(0);
	pCell.setHorizontalAlignment(Element.ALIGN_JUSTIFIED);
	pCell.setPaddingBottom(12);
	pCell.setPaddingTop(10);
	tabla.addCell(pCell);

	p = new Paragraph("Atentamente", new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.NORMAL));
	pCell = new PdfPCell(p);
	pCell.setBorder(0);
	pCell.setHorizontalAlignment(Element.ALIGN_CENTER);
	pCell.setPaddingBottom(35);
	pCell.setPaddingTop(15);
	tabla.addCell(pCell);

	////  Firmas para la carta de huespedes
	tabla.addCell(firmasEstancia(sgHuespedHotel, voBoCarta)); //

	document.add(tabla);

////////        document.add(pie());
    }

    /**
     * Metodo para generar la nueva carta de huespedes con datos bancarios
     * author : NLopez 11/10/2013
     *
     * @param document
     * @param sgHuespedHotel
     * @param voBoCarta
     * @throws DocumentException
     */
    private void generarCartaHuespedDatosBancarios(Document document, SgHuespedHotel sgHuespedHotel, String voBoCarta) throws DocumentException {
	UtilLog4j.log.info(this, "generarCartaHuespedDatosBancarios");
	Compania compania = companiaImpl.find(Constantes.RFC_IHSA);
	SgTarjetaBancariaVo creditCard = companiaImpl.getTarjetaBancaria(Constantes.RFC_IHSA);

	List<SgHuespedHotelServicioVo> serviciosIncluidosTarifa = this.sgHuespedHotelServicioImpl.findAllBySgHuespedHotel(sgHuespedHotel.getId().intValue(), false);
	List<SgHuespedHotelServicioVo> serviciosFacturaEmpresa = this.sgHuespedHotelServicioImpl.findAllBySgHuespedHotel(sgHuespedHotel.getId().intValue(), true);
	List<SgHuespedHotelVo> huespedes = this.sgHuespedHotelImpl.findAllSgHuespedHotelByNumeroReservacion(sgHuespedHotel.getNumeroHabitacion());

	UtilLog4j.log.info(this, "serviciosIncluidosTarifa.size(): " + serviciosIncluidosTarifa.size());
	UtilLog4j.log.info(this, "serviciosFacturaEmpresa.size(): " + serviciosFacturaEmpresa.size());

	//Crea el encabezado
	document.add(encabezado());

//        String persona = sgHuespedHotel.getSgDetalleSolicitudEstancia().getUsuario() != null ? sgHuespedHotel.getSgDetalleSolicitudEstancia().getUsuario().getNombre() : sgHuespedHotel.getSgDetalleSolicitudEstancia().getSgInvitado().getNombre();
	// Create a 2-column table.
	// Create a 2-column table.
	PdfPTable table = new PdfPTable(1);
	PdfPCell pCell;

	//table.setWidthPercentage(75);
	Paragraph p = new Paragraph(sgHuespedHotel.getSgHotelHabitacion().getSgHotel().getProveedor().getNombre(), new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD));
	pCell = new PdfPCell(p);
	pCell.setBorder(0);
	pCell.setPaddingTop(15);
	table.addCell(pCell);
	p = new Paragraph("Reservaciones/A quien Corresponda", new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.NORMAL));
	pCell = new PdfPCell(p);
	pCell.setBorder(0);
	pCell.setHorizontalAlignment(Element.ALIGN_JUSTIFIED);
	table.addCell(pCell);
	p = new Paragraph("Presente.-", new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.NORMAL));
	pCell = new PdfPCell(p);
	pCell.setBorder(0);
	pCell.setPaddingBottom(15);
	pCell.setHorizontalAlignment(Element.ALIGN_JUSTIFIED);
	table.addCell(pCell);
	//fin de los datos del vehiculo
	//////////////////////////////////////////////////////////////////////////////////

	StringBuilder main = new StringBuilder();
	main.append("Por medio de la presente se autoriza hacer los cargos correspondientes a la estancia de la reservación con clave de confirmación ");
	main.append(huespedes.get(0).getReservacion());
	main.append(" para que a la salida del huésped se cobre a la tarjeta que les proporciono aquì mismo.");

	pCell = new PdfPCell(new Paragraph(main.toString(), new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.NORMAL)));
	pCell.setBorder(0);
	pCell.setHorizontalAlignment(Element.ALIGN_JUSTIFIED);
	//pCell.setPaddingTop(18);
	pCell.setPaddingBottom(15);
	table.addCell(pCell);
	document.add(table);
        //////////////////////////////////////////////////////////////////////////////////

	// Reservacion
	PdfPTable reservacion = new PdfPTable(1);
	p = new Paragraph("RESERVACION A NOMBRE DE: ",
		new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.BOLD));
	pCell = new PdfPCell(p);
	pCell.setBorder(0);
	pCell.setHorizontalAlignment(Element.ALIGN_JUSTIFIED);
	pCell.setPaddingBottom(2);
	reservacion.addCell(pCell);
	int count = 1;
	StringBuilder hues = new StringBuilder();
	for (SgHuespedHotelVo huesped : huespedes) {
	    hues.append(huesped.getNombreHuesped());
	    if (count < huespedes.size()) {
		hues.append("/");
	    }
	    count++;
	}
	p = new Paragraph(huespedes.get(0).getNombreHuesped(),
		new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.BOLD));
	pCell = new PdfPCell(p);
	pCell.setBorder(0);
	pCell.setHorizontalAlignment(Element.ALIGN_JUSTIFIED);
	pCell.setPaddingBottom(2);
	reservacion.addCell(pCell);

	StringBuilder fecha = new StringBuilder();
	fecha.append("Fechas: Entrando el ");
	fecha.append(huespedes.get(0).getFechaIngreso());
	fecha.append(" y saliendo el ");
	fecha.append(huespedes.get(0).getFechaSalida());

	p = new Paragraph(fecha.toString(),
		new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.BOLD));
	pCell = new PdfPCell(p);
	pCell.setBorder(0);
	pCell.setHorizontalAlignment(Element.ALIGN_JUSTIFIED);
	pCell.setPaddingBottom(2);
	reservacion.addCell(pCell);

	p = new Paragraph("Tipo de habitación: " + sgHuespedHotel.getSgHotelHabitacion().getSgTipoEspecifico().getNombre(),
		new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.BOLD));
	pCell = new PdfPCell(p);
	pCell.setBorder(0);
	pCell.setHorizontalAlignment(Element.ALIGN_JUSTIFIED);
	pCell.setPaddingBottom(12);
	reservacion.addCell(pCell);
	document.add(reservacion);
	//////////////////////////////////////////////////////////////////////////////////

	PdfPTable cargos = new PdfPTable(1);
	p = new Paragraph("Cargos autorizados: ",
		new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.BOLD));
	pCell = new PdfPCell(p);
	pCell.setBorder(0);
	pCell.setHorizontalAlignment(Element.ALIGN_JUSTIFIED);
	pCell.setPaddingBottom(5);
	cargos.addCell(pCell);
	int dias = siManejoFechaLocal.dias(huespedes.get(0).getFechaSalida(), huespedes.get(0).getFechaIngreso());
	StringBuilder cargo = new StringBuilder();
	cargo.append("Pago de hospedaje por ");
	cargo.append(dias);
	if (siManejoFechaLocal.dias(huespedes.get(0).getFechaSalida(), huespedes.get(0).getFechaIngreso()) > 1) {
	    cargo.append(" noches ");
	} else {
	    cargo.append(" noche ");
	}
	cargo.append("de alojamiento, por ");
	cargo.append("habitación ");
	cargo.append(sgHuespedHotel.getSgHotelHabitacion().getSgTipoEspecifico().getNombre());
	cargo.append(" por un total de ");
	cargo.append("$ ");
	double tot = dias * sgHuespedHotel.getSgHotelHabitacion().getPrecio().doubleValue();
	cargo.append(tot);
	cargo.append(" más impuestos. ");
	if (serviciosIncluidosTarifa.size() > 0) {
	    cargo.append(" Incluye ");
	    for (SgHuespedHotelServicioVo servicio : serviciosIncluidosTarifa) {
		cargo.append(" ( ");
		cargo.append(servicio.getNombreSgTipoEspecifico());
		cargo.append(" ) ");
	    }
	}
	p = new Paragraph(cargo.toString(),
		new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.NORMAL));
	pCell = new PdfPCell(p);
	pCell.setBorder(0);
	pCell.setHorizontalAlignment(Element.ALIGN_JUSTIFIED);
	pCell.setPaddingBottom(15);
	cargos.addCell(pCell);
	document.add(cargos);

	//////////////////////////////////////////////////////////////////////////////////
	//// Datos de la tarjeta
	PdfPTable tarjeta = new PdfPTable(1);
	p = new Paragraph("Datos de la Tarjeta: ",
		new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.NORMAL));
	pCell = new PdfPCell(p);
	pCell.setBorder(0);
	pCell.setHorizontalAlignment(Element.ALIGN_JUSTIFIED);
	pCell.setPaddingBottom(10);
	tarjeta.addCell(pCell);

	p = new Paragraph(creditCard.getBeneficiario(),
		new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.NORMAL));
	pCell = new PdfPCell(p);
	pCell.setBorder(0);
	pCell.setHorizontalAlignment(Element.ALIGN_JUSTIFIED);
	pCell.setPaddingBottom(2);
	tarjeta.addCell(pCell);

	p = new Paragraph("No. de tarjeta: " + creditCard.getNumeroTarjeta(),
		new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.NORMAL));
	pCell = new PdfPCell(p);
	pCell.setBorder(0);
	pCell.setHorizontalAlignment(Element.ALIGN_JUSTIFIED);
	pCell.setPaddingBottom(2);
	tarjeta.addCell(pCell);

	p = new Paragraph("Fecha de Vencimiento: " + creditCard.getFechaVencimiento(),
		new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.NORMAL));
	pCell = new PdfPCell(p);
	pCell.setBorder(0);
	pCell.setHorizontalAlignment(Element.ALIGN_JUSTIFIED);
	pCell.setPaddingBottom(12);
	tarjeta.addCell(pCell);

	document.add(tarjeta);

	//////////////////////////////////////////////////////////////////////////////////
	PdfPTable tabla = new PdfPTable(1);
	p = new Paragraph("Datos para facturación: ",
		new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.NORMAL));
	pCell = new PdfPCell(p);
	pCell.setBorder(0);
	pCell.setHorizontalAlignment(Element.ALIGN_JUSTIFIED);
	pCell.setPaddingBottom(10);
	tabla.addCell(pCell);

	p = new Paragraph(compania.getNombre(),
		new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.NORMAL));
	pCell = new PdfPCell(p);
	pCell.setBorder(0);
	pCell.setHorizontalAlignment(Element.ALIGN_JUSTIFIED);
	pCell.setPaddingBottom(2);
	tabla.addCell(pCell);

	String[] domicilio = compania.getDomicilioFiscal().split(",");

	for (int i = 0; i < domicilio.length; i++) {
	    StringBuilder dom = new StringBuilder();
	    dom.append(domicilio[i].trim());
	    if (i == 1) {
		dom.append(", ");
		i++;
		dom.append(domicilio[i].trim());
		dom.append(", ");
		i++;
		dom.append(domicilio[i].trim());
	    }
	    p = new Paragraph(dom.toString(),
		    new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.NORMAL));
	    pCell = new PdfPCell(p);
	    pCell.setBorder(0);
	    pCell.setHorizontalAlignment(Element.ALIGN_JUSTIFIED);
	    pCell.setPaddingBottom(2);
	    tabla.addCell(pCell);
	}

	p = new Paragraph("Ciudad: Distrito Federal",
		new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.NORMAL));
	pCell = new PdfPCell(p);
	pCell.setBorder(0);
	pCell.setHorizontalAlignment(Element.ALIGN_JUSTIFIED);
	pCell.setPaddingBottom(2);
	tabla.addCell(pCell);
	p = new Paragraph("RFC: " + compania.getRfc(),
		new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.NORMAL));
	pCell = new PdfPCell(p);
	pCell.setBorder(0);
	pCell.setHorizontalAlignment(Element.ALIGN_JUSTIFIED);
	pCell.setPaddingBottom(2);
	tabla.addCell(pCell);
	///Entrega de facctura
	p = new Paragraph("Enviar factura a: coordinacionag@ihsa.mx",
		new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.NORMAL));
	pCell = new PdfPCell(p);
	pCell.setBorder(0);
	pCell.setHorizontalAlignment(Element.ALIGN_JUSTIFIED);
	pCell.setPaddingBottom(2);
	pCell.setPaddingTop(2);
	tabla.addCell(pCell);

	// Add the table to the document.
	p = new Paragraph("Atentamente", new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.NORMAL));
	pCell = new PdfPCell(p);
	pCell.setBorder(0);
	pCell.setHorizontalAlignment(Element.ALIGN_JUSTIFIED);
	pCell.setPaddingBottom(35);
	pCell.setPaddingTop(15);
	tabla.addCell(pCell);

	////  Firmas para la carta de huespedes
	tabla.addCell(firmasEstancia(sgHuespedHotel, voBoCarta)); //

	document.add(tabla);

//        document.add(pie());
    }

    private PdfPTable firmasEstancia(SgHuespedHotel sgHuespedHotel, String voBoCarta) {
	PdfPTable tas = new PdfPTable(2);
	PdfPCell pCell;
	Paragraph p;
	if (sgHuespedHotel.getSgDetalleSolicitudEstancia().getSgInvitado() != null) {
	    tas.getDefaultCell().setBorder(0);
	    PdfPTable t1;
	    t1 = new PdfPTable(1);
	    t1.getDefaultCell().setBorder(0);
	    p = new Paragraph(gerenciaImpl.getResponsableByApCampoAndGerencia(1, sgHuespedHotel.getSgSolicitudEstancia().getGerencia().getId(), false).getNombre(), new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.NORMAL));
	    pCell = new PdfPCell(p);
	    pCell.setBorder(0);
	    pCell.setHorizontalAlignment(Element.ALIGN_CENTER);
	    pCell.setPaddingBottom(5);
	    t1.addCell(pCell);
	    p = new Paragraph(sgHuespedHotel.getSgSolicitudEstancia().getGerencia().getNombre(), new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.NORMAL));
	    pCell = new PdfPCell(p);
	    pCell.setBorder(0);
	    pCell.setHorizontalAlignment(Element.ALIGN_CENTER);
	    pCell.setPaddingBottom(10);
	    t1.addCell(pCell);
	    tas.addCell(t1);
	    //SGyL
	    t1 = new PdfPTable(1);
	    t1.getDefaultCell().setBorder(0);
	    p = new Paragraph(voBoCarta, new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.NORMAL));
	    pCell = new PdfPCell(p);
	    pCell.setBorder(0);
	    pCell.setHorizontalAlignment(Element.ALIGN_CENTER);
	    pCell.setPaddingBottom(5);
	    t1.addCell(pCell);
	    p = new Paragraph("Servicios Generales y Logística", new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.NORMAL));
	    pCell = new PdfPCell(p);
	    pCell.setBorder(0);
	    pCell.setHorizontalAlignment(Element.ALIGN_CENTER);
	    pCell.setPaddingBottom(10);
	    t1.addCell(pCell);
	    tas.addCell(t1); // agrega las dos columnas
	    return tas;
	} else if (sgHuespedHotel.getSgDetalleSolicitudEstancia().getUsuario() != null) {
	    tas.getDefaultCell().setBorder(0);
	    PdfPTable t1;
	    t1 = new PdfPTable(1);
	    t1.getDefaultCell().setBorder(0);
	    p = new Paragraph(gerenciaImpl.getResponsableByApCampoAndGerencia(1, sgHuespedHotel.getSgDetalleSolicitudEstancia().getUsuario().getGerencia().getId(), false).getNombre(), new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.NORMAL));
	    pCell = new PdfPCell(p);
	    pCell.setBorder(0);
	    pCell.setHorizontalAlignment(Element.ALIGN_CENTER);
	    pCell.setPaddingBottom(5);
	    t1.addCell(pCell);
	    p = new Paragraph(sgHuespedHotel.getSgDetalleSolicitudEstancia().getUsuario().getGerencia().getNombre(), new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.NORMAL));
	    pCell = new PdfPCell(p);
	    pCell.setBorder(0);
	    pCell.setHorizontalAlignment(Element.ALIGN_CENTER);
	    pCell.setPaddingBottom(10);
	    t1.addCell(pCell);
	    tas.addCell(t1);
	    //SGyL
	    t1 = new PdfPTable(1);
	    t1.getDefaultCell().setBorder(0);
	    p = new Paragraph(voBoCarta, new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.NORMAL));
	    pCell = new PdfPCell(p);
	    pCell.setBorder(0);
	    pCell.setHorizontalAlignment(Element.ALIGN_CENTER);
	    pCell.setPaddingBottom(5);
	    t1.addCell(pCell);
	    p = new Paragraph("Servicios Generales y Logística", new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.NORMAL));
	    pCell = new PdfPCell(p);
	    pCell.setBorder(0);
	    pCell.setHorizontalAlignment(Element.ALIGN_CENTER);
	    pCell.setPaddingBottom(10);
	    t1.addCell(pCell);
	    tas.addCell(t1); // agrega las dos columnas
	    return tas;
	}
	return null;
    }

    private void generarCartaAsignaAccesorio(Document document, SgAsignarAccesorio sgAsignarAccesorio) throws DocumentException {
	SimpleDateFormat sdf = new SimpleDateFormat("dd 'de' MMMMM 'de' yyyy", new Locale("es", "ES"));

//Crea el encabezado
	document.add(encabezado());
	// Create a 2-column table.
	PdfPTable table = new PdfPTable(1);
	PdfPCell pCell = new PdfPCell(new Paragraph(sgAsignarAccesorio.getSgAccesorio().getSgOficina().getSgDireccion().getSiCiudad().getNombre() + " " + sgAsignarAccesorio.getSgAccesorio().getSgOficina().getSgDireccion().getSiEstado().getNombre() + " a " + sdf.format(new Date()),
		new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.NORMAL)));
	pCell.setBorder(0);
	pCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
	pCell.setPaddingBottom(12);
	table.addCell(pCell);

	// Add the first row.
	pCell = new PdfPCell(new Paragraph("ASUNTO: entrega de " + sgAsignarAccesorio.getSgAccesorio().getSgTipoEspecifico().getNombre(),
		new Font(Font.FontFamily.TIMES_ROMAN, 15, Font.BOLD)));
	pCell.setBorder(0);
	pCell.setPaddingBottom(12);
	pCell.setPaddingTop(20);
	table.addCell(pCell);
	Paragraph p = new Paragraph("En el presente se hace entrega del equipo abajo descrito a " + sgAsignarAccesorio.getUsuario().getNombre() + " , el cual es para uso exclusivo, y "
		+ " destinado para el buen desarrollo de las funciones inherentes al cargo que ha sido encomendado, a partir de esta fecha "
		+ " el equipo queda bajo resguardo del usuario, quien acepta hacerse responsable, dando buen uso y trato de este, "
		+ " sabedor de que queda estrictamente prohibido darle uso diferente del que la presente puntualiza",
		new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.NORMAL));
	pCell = new PdfPCell(p);
	pCell.setBorder(0);
	pCell.setHorizontalAlignment(Element.ALIGN_JUSTIFIED);
	pCell.setPaddingBottom(12);
	table.addCell(pCell);
	document.add(table);
	//Datos del accesorio
	PdfPTable t = new PdfPTable(2);
	t.setWidthPercentage(50);

	p = new Paragraph("Descripción:", new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.NORMAL));
	pCell = new PdfPCell(p);
	pCell.setBorder(0);
	pCell.setHorizontalAlignment(Element.ALIGN_LEFT);
	t.addCell(pCell);
	p = new Paragraph("", new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.BOLD));
	pCell = new PdfPCell(p);
	pCell.setBorder(0);
	pCell.setHorizontalAlignment(Element.ALIGN_LEFT);
	t.addCell(pCell);
	p = new Paragraph("Tipo :", new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.BOLD));
	pCell = new PdfPCell(p);
	pCell.setBorder(0);
	pCell.setHorizontalAlignment(Element.ALIGN_LEFT);
	t.addCell(pCell);
	p = new Paragraph(sgAsignarAccesorio.getSgAccesorio().getSgTipoEspecifico().getNombre(), new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.BOLD));
	pCell = new PdfPCell(p);
	pCell.setBorder(0);
	pCell.setHorizontalAlignment(Element.ALIGN_LEFT);
	t.addCell(pCell);
	p = new Paragraph("Marca:", new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.BOLD));
	pCell = new PdfPCell(p);
	pCell.setBorder(0);
	pCell.setHorizontalAlignment(Element.ALIGN_LEFT);
	t.addCell(pCell);
	p = new Paragraph(sgAsignarAccesorio.getSgAccesorio().getSgMarca().getNombre(), new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.BOLD));
	pCell = new PdfPCell(p);
	pCell.setBorder(0);
	pCell.setHorizontalAlignment(Element.ALIGN_LEFT);
	t.addCell(pCell);
	p = new Paragraph("Modelo :", new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.BOLD));
	pCell = new PdfPCell(p);
	pCell.setBorder(0);
	pCell.setHorizontalAlignment(Element.ALIGN_LEFT);
	t.addCell(pCell);
	p = new Paragraph(sgAsignarAccesorio.getSgAccesorio().getSgModelo().getNombre(), new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.BOLD));
	pCell = new PdfPCell(p);
	pCell.setBorder(0);
	pCell.setHorizontalAlignment(Element.ALIGN_LEFT);
	t.addCell(pCell);
	p = new Paragraph("Serie:", new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.BOLD));
	pCell = new PdfPCell(p);
	pCell.setBorder(0);
	pCell.setHorizontalAlignment(Element.ALIGN_LEFT);
	t.addCell(pCell);
	p = new Paragraph(sgAsignarAccesorio.getSgAccesorio().getSerie(), new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.BOLD));
	pCell = new PdfPCell(p);
	pCell.setBorder(0);
	t.addCell(pCell);
	pCell.addElement(t);
	pCell.setPaddingBottom(30);
	pCell.setHorizontalAlignment(Element.ALIGN_LEFT);
	//fin de los datos del acceorio
	document.add(t);
	document.add(firmasAsignaAccesorio(sgAsignarAccesorio));
//////        document.add(pie());

    }

    private PdfPTable encabezado() {
	try {
	    // Create a 3-column table.
	    PdfPTable ta = new PdfPTable(3);
	    float[] medidaCeldas = {1.30f, 2.30f, 1.30f};
	    ta.setWidths(medidaCeldas);
	    ta.setWidthPercentage(100);
	    PdfPCell pCe = new PdfPCell(Image.getInstance(companiaImpl.find("IHI070320FI3").getLogo()));
	    pCe.setBorder(0);
	    pCe.setHorizontalAlignment(Element.ALIGN_CENTER);
	    pCe.setPaddingBottom(12);
	    ta.addCell(pCe);
	    pCe = new PdfPCell(new Paragraph("Iberoamericana de Hidrocarburos S.A de C.V.",
		    new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD)));
	    pCe.setHorizontalAlignment(Element.ALIGN_MIDDLE);
	    pCe.setPaddingBottom(12);
	    pCe.setBorder(0);
	    ta.addCell(pCe);
	    pCe = new PdfPCell(Image.getInstance(companiaImpl.find("IHI070320FI3").getLogoEsr()));
	    pCe.setHorizontalAlignment(Element.ALIGN_CENTER);
	    pCe.setPaddingBottom(5);
	    pCe.setBorder(0);
	    ta.addCell(pCe);
	    return ta;
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, "ocurrio un error en el encabezado : : : : " + e.getMessage());
	    return null;
	}
    }

////////////    private PdfPTable pie() {
//////////        PdfPTable ta = new PdfPTable(1);
//////////        try {
//////////            // Create a 3-column table.
//////////            PdfPCell pCe = new PdfPCell(new Paragraph("Av. Batallón de San Patricio 111 | piso 29 Torre ING. Col. Valle Oriente C.P. 66269 San Pedro Garza García, N.L. Tel. 81 8363 8290 ",
//////////                    new Font(Font.FontFamily.TIMES_ROMAN, 7, Font.ITALIC)));
//////////            pCe.setHorizontalAlignment(Element.ALIGN_JUSTIFIED);
//////////            pCe.setPaddingTop(5);
//////////            pCe.setBorder(0);
//////////            ta.addCell(pCe);
//////////            return ta;
//////////        } catch (Exception e) {
//////////            System.out.print(e.getMessage());
//////////            return null;
//////////        }
//////////    }
    private PdfPTable firmasAsignaAccesorio(SgAsignarAccesorio sgAsignarAccesorio) {
	Paragraph p;
	PdfPCell pCell;
	PdfPTable tRe = new PdfPTable(2);
	PdfPTable f1 = new PdfPTable(1);
	PdfPCell pc = new PdfPCell();
	p = new Paragraph("A T E N T A  M E N T E", new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.NORMAL));
	pCell = new PdfPCell(p);
	pCell.setBorder(0);
	pCell.setHorizontalAlignment(Element.ALIGN_CENTER);
	pCell.setPaddingBottom(35);
	f1.addCell(pCell);

	p = new Paragraph("_____________________", new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.NORMAL));
	pCell = new PdfPCell(p);
	pCell.setBorder(0);
	pCell.setHorizontalAlignment(Element.ALIGN_CENTER);
	pCell.setPaddingTop(30);
	f1.addCell(pCell);
	p = new Paragraph("Cesar Buzon", new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.NORMAL));
	pCell = new PdfPCell(p);
	pCell.setBorder(0);
	pCell.setHorizontalAlignment(Element.ALIGN_CENTER);
	pCell.setPaddingBottom(2);
	f1.addCell(pCell);
	p = new Paragraph("Servicios Informáticos", new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.NORMAL));
	pCell = new PdfPCell(p);
	pCell.setBorder(0);
	pCell.setHorizontalAlignment(Element.ALIGN_CENTER);
	pCell.setPaddingBottom(2);
	f1.addCell(pCell);
	pc.addElement(f1);
	pc.setBorder(0);
	pc.setPaddingTop(40);
	tRe.addCell(pc);

	PdfPTable c2 = new PdfPTable(1);
	PdfPCell cell2 = new PdfPCell();
	p = new Paragraph("R E C I B I", new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.NORMAL));
	pCell = new PdfPCell(p);
	pCell.setBorder(0);
	pCell.setHorizontalAlignment(Element.ALIGN_CENTER);
	pCell.setPaddingBottom(35);
	c2.addCell(pCell);
	p = new Paragraph("_____________________", new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.NORMAL));
	pCell = new PdfPCell(p);
	pCell.setBorder(0);
	pCell.setHorizontalAlignment(Element.ALIGN_CENTER);
	pCell.setPaddingTop(30);
	c2.addCell(pCell);
	p = new Paragraph(sgAsignarAccesorio.getUsuario().getNombre(), new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.NORMAL));
	pCell = new PdfPCell(p);
	pCell.setBorder(0);
	pCell.setHorizontalAlignment(Element.ALIGN_CENTER);
	pCell.setPaddingBottom(2);
	c2.addCell(pCell);
	p = new Paragraph(this.apCampoUsuarioRhPuestoImpl.getPuestoPorUsurioCampo(sgAsignarAccesorio.getUsuario().getId(), sgAsignarAccesorio.getUsuario().getApCampo().getId().intValue()), new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.NORMAL));
	pCell = new PdfPCell(p);
	pCell.setBorder(0);
	pCell.setHorizontalAlignment(Element.ALIGN_CENTER);
	pCell.setPaddingBottom(2);
	c2.addCell(pCell);
	pc.addElement(c2);
	cell2.addElement(c2);
	cell2.setBorder(0);
	cell2.setPaddingTop(40);
	cell2.setPaddingBottom(50);
	tRe.addCell(cell2);
	return tRe;
    }

    private PdfPTable tablaConsumo(List serviciosIncluidoTarifa, List serviciosFacturaEmpresa) throws DocumentException {
	//Tabla de datos
	PdfPTable t = new PdfPTable(2);
	PdfPCell pCell;
	Paragraph p;
	float[] medidaCeldas = {2.70f, 2.70f};
	t.setWidths(medidaCeldas);

	//Columna 1
	PdfPTable columna1 = new PdfPTable(1);
	for (int i = 0; i < serviciosIncluidoTarifa.size(); i++) {
	    p = new Paragraph(((SgHuespedHotelServicioVo) serviciosIncluidoTarifa.get(i)).getNombreSgTipoEspecifico(), new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.NORMAL));
	    pCell = new PdfPCell(p);
	    pCell.setHorizontalAlignment(Element.ALIGN_LEFT);
	    pCell.setPaddingBottom(5);
	    pCell.setBorder(1);
	    columna1.addCell(pCell);
	}
	t.addCell(columna1);

	//Columna 2
	PdfPTable columna2 = new PdfPTable(1);
	for (int i = 0; i < serviciosFacturaEmpresa.size(); i++) {
	    p = new Paragraph(((SgHuespedHotelServicioVo) serviciosFacturaEmpresa.get(i)).getNombreSgTipoEspecifico(), new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.NORMAL));
	    pCell = new PdfPCell(p);
	    pCell.setBorder(1);
	    pCell.setHorizontalAlignment(Element.ALIGN_LEFT);
	    pCell.setPaddingBottom(5);
	    columna2.addCell(pCell);
	}
	t.addCell(columna2);

	return t;
    }

    private PdfPTable tablaDatosHuesped(List<SgHuespedHotelVo> huespedes) throws DocumentException {
	//Tabla de datos
	PdfPTable t = new PdfPTable(4);
	PdfPCell pCell;
	Paragraph p;
	float[] medidaCeldas = {1.65f, 1.25f, 1.25f, 1.25f};
	t.setWidths(medidaCeldas);

	//Columna 1
	PdfPTable columna1 = new PdfPTable(1);
	for (int i = 0; i < huespedes.size(); i++) {
	    p = new Paragraph(((SgHuespedHotelVo) huespedes.get(i)).getNombreHuesped(), new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.NORMAL));
	    pCell = new PdfPCell(p);
	    pCell.setHorizontalAlignment(Element.ALIGN_LEFT);
	    pCell.setPaddingBottom(5);
	    pCell.setBorder(1);
	    columna1.addCell(pCell);
	}
	t.addCell(columna1);

	//Columna 2
	PdfPTable columna2 = new PdfPTable(1);
	for (int i = 0; i < huespedes.size(); i++) {
	    p = new Paragraph((Constantes.FMT_ddMMyyy.format(((SgHuespedHotelVo) huespedes.get(i)).getFechaIngreso())), new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.NORMAL));
	    pCell = new PdfPCell(p);
	    pCell.setBorder(1);
	    pCell.setHorizontalAlignment(Element.ALIGN_LEFT);
	    pCell.setPaddingBottom(5);
	    columna2.addCell(pCell);
	}
	t.addCell(columna2);

	//Columna 3
	PdfPTable columna3 = new PdfPTable(1);
	for (int i = 0; i < huespedes.size(); i++) {
	    p = new Paragraph((Constantes.FMT_ddMMyyy.format(((SgHuespedHotelVo) huespedes.get(i)).getFechaSalida())), new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.NORMAL));
	    pCell = new PdfPCell(p);
	    pCell.setBorder(1);
	    pCell.setHorizontalAlignment(Element.ALIGN_LEFT);
	    pCell.setPaddingBottom(5);
	    columna3.addCell(pCell);
	}
	t.addCell(columna3);

	//Columna 3
	PdfPTable columna4 = new PdfPTable(1);
	for (int i = 0; i < huespedes.size(); i++) {
	    p = new Paragraph(((SgHuespedHotelVo) huespedes.get(i)).getReservacion(), new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.NORMAL));
	    pCell = new PdfPCell(p);
	    pCell.setBorder(1);
	    pCell.setHorizontalAlignment(Element.ALIGN_LEFT);
	    pCell.setPaddingBottom(5);
	    columna4.addCell(pCell);
	}
	t.addCell(columna4);

	return t;
    }

    private PdfPTable tablaDetalleCheckList(SgAsignarVehiculo sgAsignarVehiculo) {
	try {

	    PdfPTable t = new PdfPTable(3);
	    t.setWidthPercentage(100);

	    PdfPCell pCell;
//Titulo para las paginas
	    Paragraph p = new Paragraph("Característica", new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.BOLD));
	    pCell = new PdfPCell(p);
	    pCell.setBorder(1);
	    pCell.setBackgroundColor(BaseColor.GRAY);
	    pCell.setHorizontalAlignment(Element.ALIGN_LEFT);
	    t.addCell(pCell);
	    p = new Paragraph("Estado ", new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.BOLD));
	    pCell = new PdfPCell(p);
	    pCell.setBorder(1);
	    pCell.setBackgroundColor(BaseColor.GRAY);
	    pCell.setHorizontalAlignment(Element.ALIGN_LEFT);
	    t.addCell(pCell);
	    p = new Paragraph("Observación", new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.BOLD));
	    pCell = new PdfPCell(p);
	    pCell.setBorder(1);
	    pCell.setBackgroundColor(BaseColor.GRAY);
	    pCell.setHorizontalAlignment(Element.ALIGN_LEFT);
	    t.addCell(pCell);
	    for (CheckListDetalleVo sgChecklistDetalle : sgChecklistDetalleImpl.getAllItemsChecklistList(sgAsignarVehiculo.getSgChecklist().getId(), Constantes.NO_ELIMINADO)) {
		//Datos del vehiculo
		p = new Paragraph(sgChecklistDetalle.getCaracteristicaVo().getNombre(), new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.NORMAL));
		pCell = new PdfPCell(p);
		pCell.setBorder(1);
		pCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		t.addCell(pCell);
		p = new Paragraph(sgChecklistDetalle.isEstado() ? "Bien" : "Mal", new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.NORMAL));
		pCell = new PdfPCell(p);
		pCell.setBorder(1);
		pCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		t.addCell(pCell);
		p = new Paragraph(sgChecklistDetalle.getObservacion(), new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.NORMAL));
		pCell = new PdfPCell(p);
		pCell.setBorder(1);
		pCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		t.addCell(pCell);
	    }
	    return t;
	    //LLantas
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, "ocurrio un error en el checkList : : : : " + e.getMessage());
	    return null;
	}
    }

    private PdfPTable tablaCheckListLlantas(SgAsignarVehiculo sgAsignarVehiculo) {
	try {

	    PdfPTable t = new PdfPTable(6);
	    t.setWidthPercentage(100);
	    PdfPCell pCell;
//Titulo para las paginas
	    Paragraph p;
	    SgChecklistLlantas sgChecklistLlantas = sgChecklistLlantasImpl.buscarPorChecklist(sgAsignarVehiculo.getSgChecklist());
	    //
	    PdfPTable di = new PdfPTable(1);
	    p = new Paragraph("Delantera Izq.", new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.NORMAL));
	    pCell = new PdfPCell(p);
	    pCell.setHorizontalAlignment(Element.ALIGN_CENTER);
	    pCell.setPaddingBottom(12);
	    pCell.setBorder(0);
	    di.addCell(pCell);
	    p = new Paragraph(sgChecklistLlantas.getDelanteraIzquierda(), new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.NORMAL));
	    pCell = new PdfPCell(p);
	    pCell.setBorder(1);
	    pCell.setHorizontalAlignment(Element.ALIGN_CENTER);
	    pCell.setPaddingBottom(12);
	    di.addCell(pCell);
	    t.addCell(di);
	    //
	    PdfPTable dd = new PdfPTable(1);
	    p = new Paragraph("Delantera derecha", new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.NORMAL));
	    pCell = new PdfPCell(p);
	    pCell.setHorizontalAlignment(Element.ALIGN_CENTER);
	    pCell.setPaddingBottom(12);
	    pCell.setBorder(0);
	    dd.addCell(pCell);
	    p = new Paragraph(sgChecklistLlantas.getDelanteraDerecha(), new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.NORMAL));
	    pCell = new PdfPCell(p);
	    pCell.setBorder(1);
	    pCell.setHorizontalAlignment(Element.ALIGN_CENTER);
	    pCell.setPaddingBottom(12);
	    dd.addCell(pCell);
	    t.addCell(dd);
	    //
	    PdfPTable ti = new PdfPTable(1);
	    p = new Paragraph("Tracera izquierda", new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.NORMAL));
	    pCell = new PdfPCell(p);
	    pCell.setHorizontalAlignment(Element.ALIGN_CENTER);
	    pCell.setPaddingBottom(12);
	    pCell.setBorder(0);
	    ti.addCell(pCell);
	    p = new Paragraph(sgChecklistLlantas.getTraseraIzquierda(), new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.NORMAL));
	    pCell = new PdfPCell(p);
	    pCell.setBorder(1);
	    pCell.setHorizontalAlignment(Element.ALIGN_CENTER);
	    pCell.setPaddingBottom(12);
	    ti.addCell(pCell);
	    t.addCell(ti);
	    //
	    PdfPTable td = new PdfPTable(1);
	    p = new Paragraph("Tracera derecha", new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.NORMAL));
	    pCell = new PdfPCell(p);
	    pCell.setHorizontalAlignment(Element.ALIGN_CENTER);
	    pCell.setPaddingBottom(12);
	    pCell.setBorder(0);
	    td.addCell(pCell);
	    p = new Paragraph(sgChecklistLlantas.getTraseraDerecha(), new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.NORMAL));
	    pCell = new PdfPCell(p);
	    pCell.setBorder(1);
	    pCell.setHorizontalAlignment(Element.ALIGN_CENTER);
	    pCell.setPaddingBottom(12);
	    td.addCell(pCell);
	    t.addCell(td);
	    //
	    PdfPTable r = new PdfPTable(1);
	    p = new Paragraph("Refacción", new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.NORMAL));
	    pCell = new PdfPCell(p);
	    pCell.setHorizontalAlignment(Element.ALIGN_CENTER);
	    pCell.setPaddingBottom(12);
	    pCell.setBorder(0);
	    r.addCell(pCell);
	    p = new Paragraph(sgChecklistLlantas.getRefaccion(), new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.NORMAL));
	    pCell = new PdfPCell(p);
	    pCell.setBorder(1);
	    pCell.setHorizontalAlignment(Element.ALIGN_CENTER);
	    pCell.setPaddingBottom(12);
	    r.addCell(pCell);
	    t.addCell(r);
	    //
	    PdfPTable estado = new PdfPTable(1);
	    p = new Paragraph("Estado", new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.NORMAL));
	    pCell = new PdfPCell(p);
	    pCell.setHorizontalAlignment(Element.ALIGN_CENTER);
	    pCell.setPaddingBottom(12);
	    pCell.setBorder(0);
	    estado.addCell(pCell);
	    p = new Paragraph(sgChecklistLlantas.isBuenEstado()? "Bien" : "Mal", new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.NORMAL));
	    pCell = new PdfPCell(p);
	    pCell.setBorder(1);
	    pCell.setHorizontalAlignment(Element.ALIGN_CENTER);
	    pCell.setPaddingBottom(12);
	    estado.addCell(pCell);
	    t.setSpacingAfter(50);
	    t.addCell(estado);
	    return t;
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, "Ocurrio un error en check list de llantas  : : : : : " + e.getMessage());
	    return null;
	}
    }

    private void generarDocumentoViaje(Document document, SgViaje sgViaje) throws DocumentException, SIAException {
	try {
	    SimpleDateFormat sdf = new SimpleDateFormat("dd 'de' MMMMM 'de' yyyy", new Locale("es", "ES"));

	    //Crea el encabezado
	    document.add(encabezado());
	    PdfPCell pCell;
	    PdfPTable table;
	    Paragraph p = new Paragraph("Logística para el viaje programado el día " + sdf.format(sgViaje.getFechaProgramada()).concat("."),
		    new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.NORMAL));
	    pCell = new PdfPCell(p);
	    pCell.setBorder(0);
	    pCell.setHorizontalAlignment(Element.ALIGN_JUSTIFIED);
	    pCell.setPaddingBottom(12);
	    table = new PdfPTable(1);
	    table.addCell(pCell);
	    document.add(table);

	    //Datos del viaje
	    table = new PdfPTable(1);
	    table.getDefaultCell().setBorder(0);
	    table.addCell(agregarLineasEnBlanco()); //Linea en blanco
	    p = new Paragraph("Datos del viaje", new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.BOLD));
	    pCell = new PdfPCell(p);
	    pCell.setBorder(0);
	    pCell.setHorizontalAlignment(Element.ALIGN_LEFT);
	    table.addCell(pCell);

	    PdfPTable t = new PdfPTable(2);
	    float[] medidaCeldaViaje = {1.2f, 3.2f};
	    t.setWidths(medidaCeldaViaje);
	    t.getDefaultCell().setBorder(0);
	    t.getDefaultCell().setPaddingLeft(0.5f);
	    p = new Paragraph("Fecha salida:", new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.NORMAL));
	    pCell = new PdfPCell(p);
	    pCell.setBorder(0);
	    t.addCell(pCell);

	    p = new Paragraph(Constantes.FMT_ddMMyyy.format(sgViaje.getFechaProgramada()), new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.NORMAL));
	    pCell = new PdfPCell(p);
	    pCell.setBorder(0);
	    pCell.setHorizontalAlignment(Element.ALIGN_LEFT);
	    t.addCell(pCell);
	    //
	    p = new Paragraph("Hora salida:", new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.NORMAL));
	    pCell = new PdfPCell(p);
	    pCell.setBorder(0);
	    pCell.setHorizontalAlignment(Element.ALIGN_LEFT);
	    t.addCell(pCell);
	    p = new Paragraph(Constantes.FMT_hmm_a.format(sgViaje.getHoraProgramada()), new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.NORMAL));
	    pCell = new PdfPCell(p);
	    pCell.setBorder(0);
	    pCell.setHorizontalAlignment(Element.ALIGN_LEFT);
	    t.addCell(pCell);
	    //
	    if (sgViaje.getSgViaje() == null) { //Mostrar solo si es un viaje de ida
		p = new Paragraph("Fecha regreso:", new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.NORMAL));
		pCell = new PdfPCell(p);
		pCell.setBorder(0);
		pCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		t.addCell(pCell);
		p = new Paragraph(sgViaje.getFechaRegreso() != null ? Constantes.FMT_ddMMyyy.format(sgViaje.getFechaRegreso()) : "-", new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.NORMAL));
		pCell = new PdfPCell(p);
		pCell.setBorder(0);
		pCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		t.addCell(pCell);
		//
		p = new Paragraph("Hora de regreso :", new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.NORMAL));
		pCell = new PdfPCell(p);
		pCell.setBorder(0);
		pCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		t.addCell(pCell);
		p = new Paragraph(sgViaje.getHoraRegreso() != null ? Constantes.FMT_hmm_a.format(sgViaje.getHoraRegreso()) : "-", new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.NORMAL));
		pCell = new PdfPCell(p);
		pCell.setBorder(0);
		pCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		t.addCell(pCell);
	    }
	    table.addCell(t);
	    //
	    document.add(table);

	    //Ruta
	    PdfPTable tRuta = new PdfPTable(1);
	    if (sgViaje.getSgRutaTerrestre() != null) {

		tRuta.getDefaultCell().setBorder(0);
		tRuta.setSpacingAfter(2.0f);
		tRuta.addCell(agregarLineasEnBlanco()); //Linea en blanco
		PdfPTable ta;
		//Ruta
		p = new Paragraph("Ruta", new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.BOLD));
		pCell = new PdfPCell(p);
		pCell.setBorder(0);
		pCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		tRuta.addCell(pCell);
		List<SgDetalleRutaTerrestre> ldr = sgDetalleRutaTerrestreImpl.getDetailByRuote(sgViaje.getSgRutaTerrestre().getId(), Constantes.NO_ELIMINADO);
		UtilLog4j.log.info(this, "lista detalle ruta: " + ldr.size());
		//
		PdfPTable tabla3 = new PdfPTable(ldr.size() + 1);
		tabla3.getDefaultCell().setBorder(0);
		ta = new PdfPTable(1);
		ta.getDefaultCell().setBorder(0);
		p = new Paragraph("Origen", new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.BOLD));
		pCell = new PdfPCell(p);
		pCell.setBorder(0);
		ta.addCell(pCell);
		//
		p = new Paragraph(sgViaje.getSgOficina().getNombre(), new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.NORMAL));
		pCell = new PdfPCell(p);
		pCell.setBorder(0);
		ta.addCell(pCell);
//        pCell.addElement(ta);
		//
		p = new Paragraph(sgViaje.getSgOficina().getSgDireccion().getSiCiudad().getNombre(), new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.NORMAL));
		pCell = new PdfPCell(p);
		pCell.setBorder(0);
		ta.addCell(pCell);
		//Origen
		tabla3.addCell(ta);
		for (SgDetalleRutaTerrestre sgDet : ldr) {
		    t = new PdfPTable(1);
		    t.getDefaultCell().setBorder(0);
		    //
		    if (sgDet.isDestino()) {
//                ta = new PdfPTable(1);
			p = new Paragraph("Destino", new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.BOLD));
			pCell = new PdfPCell(p);
			pCell.setBorder(0);
			pCell.setHorizontalAlignment(Element.ALIGN_LEFT);
			t.addCell(pCell);
		    } else {
//                ta = new PdfPTable(1);
			p = new Paragraph("De paso", new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.NORMAL));
			pCell = new PdfPCell(p);
			pCell.setBorder(0);
			pCell.setHorizontalAlignment(Element.ALIGN_LEFT);
			t.addCell(pCell);
		    }
		    p = new Paragraph(sgDet.getSgOficina().getNombre(), new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.NORMAL));
		    pCell = new PdfPCell(p);
		    pCell.setBorder(0);
		    pCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		    t.addCell(pCell);
		    //
		    p = new Paragraph(sgDet.getSgOficina().getSgDireccion().getSiCiudad().getNombre(), new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.NORMAL));
		    pCell = new PdfPCell(p);
		    pCell.setBorder(0);
		    pCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		    t.addCell(pCell);
		    tabla3.getDefaultCell().setPaddingBottom(2.0f);
		    tabla3.addCell(t);
		}
		tRuta.addCell(tabla3);
		document.add(tRuta);

	    } else if (sgViaje.getSgViajeCiudad() != null) {
		tRuta = new PdfPTable(2);
		//Headers para el Origen y el Destino
		float[] medidaCeldas = {2.70f, 2.70f};
		tRuta.setWidths(medidaCeldas);

		PdfPTable header1 = new PdfPTable(1);
		p = new Paragraph("Origen", new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.BOLD));
		pCell = new PdfPCell(p);
		pCell.setBorder(0);
		pCell.setPaddingBottom(5);
		header1.addCell(pCell);
		pCell.setHorizontalAlignment(Element.ALIGN_CENTER);
		tRuta.addCell(header1);

		PdfPTable header2 = new PdfPTable(1);
		p = new Paragraph("Destino", new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.BOLD));
		pCell = new PdfPCell(p);
		pCell.setBorder(0);
		pCell.setPaddingBottom(5);
		header2.addCell(pCell);
		pCell.setHorizontalAlignment(Element.ALIGN_CENTER);
		tRuta.addCell(header2);
		document.add(tRuta);

		//Tabla de datos Origen y destino
		PdfPTable todc = new PdfPTable(2);
		PdfPCell pCellodc;
		Paragraph podc;
		float[] medidaCeldasodc = {2.70f, 2.70f};
		t.setWidths(medidaCeldasodc);

		//Columna 1
		PdfPTable columna1odc = new PdfPTable(1);
		podc = new Paragraph(sgViaje.getSgViaje() == null ? sgViaje.getSgOficina().getNombre() : sgViaje.getSgViajeCiudad().getSiCiudad().getNombre(), new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.NORMAL));
		pCellodc = new PdfPCell(podc);
		pCellodc.setHorizontalAlignment(Element.ALIGN_LEFT);
		pCellodc.setPaddingBottom(5);
		pCellodc.setBorder(0);
		columna1odc.addCell(pCellodc);
		todc.addCell(columna1odc);

		//Columna 2
		PdfPTable columna2odc = new PdfPTable(1);
		podc = new Paragraph(sgViaje.getSgViaje() == null ? sgViaje.getSgViajeCiudad().getSiCiudad().getNombre() : sgViaje.getSgOficina().getNombre(), new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.NORMAL));
		pCellodc = new PdfPCell(podc);
		pCellodc.setBorder(0);
		pCellodc.setHorizontalAlignment(Element.ALIGN_LEFT);
		pCellodc.setPaddingBottom(5);
		columna2odc.addCell(pCellodc);
		todc.addCell(columna2odc);

		document.add(todc);
	    }

	    //Viajeros
	    PdfPTable tViajero = new PdfPTable(1);
	    tViajero.getDefaultCell().setBorder(0);
	    tViajero.setSpacingAfter(2.0f);
	    tViajero.addCell(agregarLineasEnBlanco()); //Linea en blanco
	    p = new Paragraph("Viajeros", new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.BOLD));
	    pCell = new PdfPCell(p);
	    pCell.setBorder(0);

	    pCell.setHorizontalAlignment(Element.ALIGN_LEFT);
	    tViajero.addCell(pCell);
	    List<ViajeroVO> lv = sgViajeroImpl.getTravellersByTravel(sgViaje.getId(), null);
	    UtilLog4j.log.info(this, "Lista viajeros: " + lv.size());
	    float[] medidaCeldasViajero = {2.0f, 5.25f};
	    //Encabeazado
	    t = new PdfPTable(2);
	    t.getDefaultCell().setBorder(0);
	    t.setWidths(medidaCeldasViajero);
	    t.getDefaultCell().setPaddingLeft(0.5f);
	    p = new Paragraph("Nombre", new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.BOLD));
	    pCell = new PdfPCell(p);
	    pCell.setBorder(0);
	    pCell.setBackgroundColor(BaseColor.GRAY);
	    t.addCell(pCell);

	    p = new Paragraph("Motivo de viaje", new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.BOLD));
	    pCell = new PdfPCell(p);
	    pCell.setBorder(0);
	    pCell.setBackgroundColor(BaseColor.GRAY);
	    t.addCell(pCell);
	    tViajero.addCell(t);
	    for (ViajeroVO viajero : lv) {
		t = new PdfPTable(2);
		t.getDefaultCell().setBorder(0);
		t.setWidths(medidaCeldasViajero);
		t.getDefaultCell().setPaddingLeft(0.5f);
		p = new Paragraph(!viajero.getUsuario().equals("null") ? viajero.getUsuario() : viajero.getInvitado(), new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.NORMAL));
		pCell = new PdfPCell(p);
		pCell.setBorder(0);
		t.addCell(pCell);

		p = new Paragraph(sgSolicitudViajeImpl.buscarPorCodigo(viajero.getCodigoSolicitudViaje(), Constantes.NO_ELIMINADO)
                        .get(Constantes.CERO).getMotivo(), new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.NORMAL));
		pCell = new PdfPCell(p);
		pCell.setBorder(0);
		t.addCell(pCell);
		tViajero.addCell(t);
	    }
	    tViajero.getDefaultCell().setPaddingBottom(2.0f);
	    document.add(tViajero);
	    //DAtos del vehiculo
	    SgViajeVehiculo sgViajeVehiculo = sgViajeVehiculoImpl.getVehicleByTravel(sgViaje.getId());
	    if (sgViajeVehiculo != null) {
		document.add(datosVehiculo(sgViajeVehiculo.getSgVehiculo()));
	    }

//COnductor
	    PdfPTable tableCon = new PdfPTable(1);
	    tableCon.getDefaultCell().setBorder(0);
	    tableCon.addCell(agregarLineasEnBlanco()); //Linea en blanco
	    tRuta.setSpacingAfter(2.0f);
	    p = new Paragraph("Responsable", new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.BOLD));
	    pCell = new PdfPCell(p);
	    pCell.setBorder(0);
	    tableCon.addCell(pCell);
//
	    t = new PdfPTable(2);
	    t.getDefaultCell().setPaddingLeft(0.5f);
	    p = new Paragraph("Nombre", new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.BOLD));
	    pCell = new PdfPCell(p);
	    pCell.setBorder(0);
	    pCell.setBackgroundColor(BaseColor.GRAY);
	    pCell.setPaddingLeft(0.5f);
	    t.addCell(pCell);

	    p = new Paragraph("Teléfono", new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.BOLD));
	    pCell = new PdfPCell(p);
	    pCell.setBackgroundColor(BaseColor.GRAY);
	    pCell.setBorder(0);
	    t.addCell(pCell);
	    //Fin encabezado
	    t.getDefaultCell().setPaddingLeft(0.5f);
	    p = new Paragraph(sgViaje.getResponsable().getNombre(), new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.NORMAL));
	    pCell = new PdfPCell(p);
	    pCell.setBorder(0);
	    pCell.setPaddingLeft(0.5f);
	    t.addCell(pCell);

	    p = new Paragraph(sgViaje.getResponsable().getCelular(), new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.NORMAL));
	    pCell = new PdfPCell(p);
	    pCell.setBorder(0);
	    t.addCell(pCell);
	    tableCon.addCell(t);
	    document.add(tableCon);
	    //Firmas

	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, "Ocurrio un error al crear el documento de viaje : : : : " + e.getMessage());
	}
    }

    private PdfPTable datosVehiculo(SgVehiculo sgVehiculo) {
	float[] medidaCeldas = {0.45f, 2.25f};

	PdfPCell pCell;
	pCell = new PdfPCell();
	pCell.setBorder(0);
//        pCell.setHorizontalAlignment(Element.ALIGN_LEFT);
	pCell.setPaddingBottom(12);
	PdfPTable table = new PdfPTable(1);
	table.getDefaultCell().setBorder(0);
	//Datos del vehiculo

	Paragraph p = new Paragraph("Datos del vehículo", new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.BOLD));
	pCell = new PdfPCell(p);
	pCell.setBorder(0);
	pCell.setHorizontalAlignment(Element.ALIGN_LEFT);
	table.addCell(pCell);
	PdfPTable t = new PdfPTable(2);
	t.getDefaultCell().setBorder(0);
	t.getDefaultCell().setPaddingLeft(0.5f);
	try {
	    t.setWidths(medidaCeldas);
	} catch (DocumentException ex) {
	    UtilLog4j.log.fatal(this, "Ocurrio un error en las medidas de ls celdas " + ex.getMessage());
	}
//        t.setWidthPercentage(50);
	p = new Paragraph("Modelo :", new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.NORMAL));
	pCell = new PdfPCell(p);
	pCell.setBorder(0);
	pCell.setHorizontalAlignment(Element.ALIGN_LEFT);
	t.addCell(pCell);
	p = new Paragraph(sgVehiculo.getSgModelo().getNombre(), new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.NORMAL));
	pCell = new PdfPCell(p);
	pCell.setBorder(0);
	pCell.setHorizontalAlignment(Element.ALIGN_LEFT);
	t.addCell(pCell);
	//
	p = new Paragraph("Marca:", new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.NORMAL));
	pCell = new PdfPCell(p);
	pCell.setBorder(0);
	pCell.setHorizontalAlignment(Element.ALIGN_LEFT);
	t.addCell(pCell);
	p = new Paragraph(sgVehiculo.getSgMarca().getNombre(), new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.NORMAL));
	pCell = new PdfPCell(p);
	pCell.setBorder(0);
	t.addCell(pCell);
	///

	p = new Paragraph("Placa :", new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.NORMAL));
	pCell = new PdfPCell(p);
	pCell.setBorder(0);
	pCell.setHorizontalAlignment(Element.ALIGN_LEFT);
	t.addCell(pCell);
	p = new Paragraph(sgVehiculo.getNumeroPlaca(), new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.NORMAL));
	pCell = new PdfPCell(p);
	pCell.setBorder(0);
	pCell.setHorizontalAlignment(Element.ALIGN_LEFT);
	t.addCell(pCell);
	//
	p = new Paragraph("Color:", new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.NORMAL));
	pCell = new PdfPCell(p);
	pCell.setBorder(0);
	pCell.setHorizontalAlignment(Element.ALIGN_LEFT);
	t.addCell(pCell);
	p = new Paragraph(sgVehiculo.getSgColor().getNombre(), new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.NORMAL));
	pCell = new PdfPCell(p);
	pCell.setBorder(0);
	pCell.setHorizontalAlignment(Element.ALIGN_LEFT);
	t.addCell(pCell);
	table.addCell(t);
	return table;
    }

    static class HeaderFooter extends PdfPageEventHelper {

	Compania compania;

	public void setCompania(Compania compania) {
	    this.compania = compania;
	}

	@Override
	public void onEndPage(PdfWriter writer, Document document) {
	    Rectangle rect = writer.getBoxSize("art");
	    //Pie
	    ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_CENTER,
		    new Phrase(compania.getCalle() + " Col. " + compania.getColonia() + " C.P. " + compania.getCp() + " " + compania.getCiudad() + " "
			    + compania.getEstado() + " Tel. " + compania.getTelefono(), new Font(Font.FontFamily.TIMES_ROMAN, 7, Font.ITALIC)),
		    (rect.getLeft() + rect.getRight()) / 2, rect.getBottom() - 18, 0);
	}
    }

    private PdfPCell agregarLineasEnBlanco() {
	PdfPCell pCell;
	pCell = new PdfPCell(new Paragraph());
	pCell.setBorder(0);
	return pCell;
    }
}
