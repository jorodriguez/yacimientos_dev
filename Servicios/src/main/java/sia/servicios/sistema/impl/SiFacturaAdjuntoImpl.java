/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.sistema.impl;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.archivador.AlmacenDocumentos;
import sia.constantes.Constantes;
import sia.excepciones.SIAException;
import sia.modelo.SiAdjunto;
import sia.modelo.SiFactura;
import sia.modelo.SiFacturaAdjunto;
import sia.modelo.Usuario;
import sia.modelo.sgl.vo.AdjuntoVO;
import sia.modelo.sistema.AbstractFacade;
import sia.modelo.sistema.vo.FacturaAdjuntoVo;
import sia.util.UtilLog4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipOutputStream;
import javax.ejb.LocalBean;
import javax.inject.Inject;
import sia.archivador.DocumentoAnexo;
import sia.constantes.Configurador;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.FileUtils;
import sia.archivador.ProveedorAlmacenDocumentos;
import sia.correo.impl.EnviarCorreoImpl;


/**
 *
 * @author mluis
 */
@LocalBean 
public class SiFacturaAdjuntoImpl extends AbstractFacade<SiFacturaAdjunto>{

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
    @Inject
    ProveedorAlmacenDocumentos proveedorAlmacenDocumentosRemote;
    @Inject
    private SiParametroImpl parametrosSistemaServicioRemoto;
    @Inject
    private EnviarCorreoImpl enviarCorreoRemote;

    private final static UtilLog4j LOGGER = UtilLog4j.log;

    public SiFacturaAdjuntoImpl() {
        super(SiFacturaAdjunto.class);
    }

    
    public void guardar(SiAdjunto siAdjunto, int idFactura, String tipo, String sesion) {
        SiFacturaAdjunto siFacturaAdjunto = new SiFacturaAdjunto();
        siFacturaAdjunto.setSiAdjunto(siAdjunto);
        siFacturaAdjunto.setSiFactura(new SiFactura(idFactura));
        siFacturaAdjunto.setTipoArchivo(tipo);
        siFacturaAdjunto.setGenero(new Usuario(sesion));
        siFacturaAdjunto.setFechaGenero(new Date());
        siFacturaAdjunto.setHoraGenero(new Date());
        siFacturaAdjunto.setEliminado(Constantes.NO_ELIMINADO);
        //
        create(siFacturaAdjunto);
    }

    
    public List<FacturaAdjuntoVo> traerSoporteFactura(int idFactura, boolean factura) {
        String tipos = null;
        return this.traerSoporteFactura(idFactura, factura, tipos);
    }

    
    public List<FacturaAdjuntoVo> traerSoporteFactura(int idFactura, boolean factura, String tipos) {
        String c = " select fa.id, fa.tipo_archivo, fa.si_factura, a.ID, a.NOMBRE,  a.UUID, a.url from si_factura_adjunto fa "
                + "     inner join si_adjunto a on fa.si_adjunto = a.id and a.eliminado = false "
                + " where fa.si_factura = " + idFactura + ""
                + " and fa.eliminado = false ";

        if (tipos != null && !tipos.isEmpty()) {
            c += " and fa.tipo_archivo in (" + tipos + ") ";
        }

        //
        List<Object[]> lo = em.createNativeQuery(c).getResultList();
        //
        List<FacturaAdjuntoVo> lfa = new ArrayList<>();
        for (Object[] objects : lo) {
            lfa.add(castFacturaAdjVo(objects, factura));
        }
        return lfa;
    }

    private FacturaAdjuntoVo castFacturaAdjVo(Object[] obj, boolean factura) {
        FacturaAdjuntoVo fav = new FacturaAdjuntoVo();
        fav.setId((Integer) obj[0]);
        String nom = (String) obj[1];
        if (!factura) {
            fav.setTipo(nom.replace("Factura", "Nota de crédito"));
        } else {
            fav.setTipo(nom);
        }

        fav.setIdFactura((Integer) obj[2]);
        fav.setAdjuntoVo(new AdjuntoVO());
        fav.getAdjuntoVo().setId((Integer) obj[3]);
        fav.getAdjuntoVo().setNombre((String) obj[4]);
        fav.getAdjuntoVo().setUuid((String) obj[5]);
        fav.getAdjuntoVo().setUrl((String) obj[6]);
        return fav;
    }

    private FacturaAdjuntoVo castFacturaAdjVoUrl(Object[] obj, boolean factura) {
        FacturaAdjuntoVo fav = new FacturaAdjuntoVo();
        fav.setId((Integer) obj[0]);
        String nom = (String) obj[1];
        if (!factura) {
            fav.setTipo(nom.replace("Factura", "Nota de crédito"));
        } else {
            fav.setTipo(nom);
        }

        fav.setIdFactura((Integer) obj[2]);
        fav.setAdjuntoVo(new AdjuntoVO());
        fav.getAdjuntoVo().setId((Integer) obj[3]);
        fav.getAdjuntoVo().setNombre((String) obj[4]);
        fav.getAdjuntoVo().setUuid((String) obj[5]);
        fav.getAdjuntoVo().setUrl((String) obj[6]);
        fav.getAdjuntoVo().setUrlZip((String) obj[7]);
        return fav;
    }

    
    public void eliminar(int idFacturaAdjunto, String sesion) {
        try {
            SiFacturaAdjunto siFacturaAdjunto = find(idFacturaAdjunto);
            //
            AlmacenDocumentos almacenDocumentos = proveedorAlmacenDocumentosRemote.getAlmacenDocumentos();
            almacenDocumentos.borrarDocumento(siFacturaAdjunto.getSiAdjunto().getUrl());

            //
            siFacturaAdjunto.setModifico(new Usuario(sesion));
            siFacturaAdjunto.setFechaModifico(new Date());
            siFacturaAdjunto.setHoraModifico(new Date());
            siFacturaAdjunto.setEliminado(Constantes.ELIMINADO);
            //
            edit(siFacturaAdjunto);
        } catch (SIAException ex) {
            UtilLog4j.log.error(ex);
            Logger.getLogger(SiFacturaAdjuntoImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    
    public List<FacturaAdjuntoVo> traerArchivosFactura(int idFactura, String uuid) {
        String c = " select "
                + " fa.id,fa.tipo_archivo,fa.si_factura,a.ID,a.NOMBRE,a.UUID,  "
                + " a.url,  "
                + " 'Proveedor/'||p.rfc||'/F'||f.folio||'_" + uuid + "/'||a.NOMBRE as urlZip "
                + " from si_factura f "
                + " inner join si_factura_adjunto fa on fa.si_factura = f.id "
                + " inner join proveedor p on p.id = f.proveedor "
                + " inner join si_adjunto a on fa.si_adjunto = a.id "
                + " where fa.si_factura = " + idFactura
                + " and fa.eliminado = false "
                + " union "
                + " select  "
                + " e.id, ad.tipo_archivo, a.id, ad.id, ad.nombre, ad.uuid, ad.url,  "
                + " 'Proveedor/'||p.rfc||'/F'||a.folio||'_" + uuid + "/'||ad.NOMBRE as urlZip "
                + " from si_factura a "
                + " inner join proveedor p on p.id = a.proveedor "
                + " inner join orden o on o.id = a.orden and o.eliminado = false "
                + " inner join oc_orden_ets e on e.orden = o.id and e.eliminado = false "
                + " inner join si_adjunto ad on ad.id = e.si_adjunto and ad.eliminado = false "
                + " where a.id = " + idFactura
                + " and a.eliminado = false "
                + " union "
                + " select  "
                + " 0, ad.tipo_archivo, a.id, ad.id, ad.nombre, ad.uuid,ad.url,  "
                + " 'Proveedor/'||p.rfc||'/F'||a.folio||'_" + uuid + "/'||ad.NOMBRE as urlZip "
                + " from si_factura a "
                + " inner join proveedor p on p.id = a.proveedor "
                + " inner join si_adjunto ad on ad.id = a.si_adjunto and ad.eliminado = false "
                + " where a.id = " + idFactura
                + " and a.eliminado = false "
                + " union "
                + " SELECT "
                + " fa.id, "
                + " fa.tipo_archivo, "
                + " fa.si_factura, "
                + " a.ID, "
                + " a.NOMBRE, "
                + " a.UUID, "
                + " a.url, "
                + " 'Proveedor/'||p.rfc||'/F'||ff.folio||'_" + uuid + "/'||a.NOMBRE as urlZip "
                + " FROM si_factura f "
                + " inner join si_factura_adjunto fa on fa.si_factura = f.id "
                + " inner join proveedor p on p.id = f.proveedor "
                + " inner join si_adjunto a on fa.si_adjunto = a.id "
                + " inner join si_factura ff on ff.id = f.si_factura "
                + " WHERE f.si_factura = " + idFactura
                + " AND f.eliminado = false ";

        List<Object[]> lo = em.createNativeQuery(c).getResultList();
        //
        List<FacturaAdjuntoVo> lfa = new ArrayList<>();
        for (Object[] objects : lo) {
            lfa.add(castFacturaAdjVoUrl(objects, true));
        }
        return lfa;
    }

    
    public File crearZipFile(int idFactura, List<FacturaAdjuntoVo> archivos) {
        //String zipFile = "/home/jcarranza/factura" + idFactura + ".zip";
        File fileTempExcel = null;
        try {
            AlmacenDocumentos almacenDocumentos = proveedorAlmacenDocumentosRemote.getAlmacenDocumentos();
            String REPOSITORYPATH = this.parametrosSistemaServicioRemoto.find(1).getUploadDirectory();
            String PLANTILLAPATH = "Factura/Temporal";
            String URL_Temporal = new StringBuilder().append(REPOSITORYPATH).append(PLANTILLAPATH).append(File.separator).toString();
            fileTempExcel = File.createTempFile("factura" + idFactura, ".zip", new File(URL_Temporal));

            // create byte buffer
            byte[] buffer = new byte[1024];

            FileOutputStream fos = new FileOutputStream(fileTempExcel);

            ZipOutputStream zos = new ZipOutputStream(fos);

            for (FacturaAdjuntoVo archivo : archivos) {
                try {
                    DocumentoAnexo documento = almacenDocumentos.cargarDocumento(archivo.getAdjuntoVo().getUrl());
                    //File srcFile = new File(srcFiles[i]);
                    InputStream fis = new ByteArrayInputStream(documento.getContenido());
                    //FileInputStream fis = new FileInputStream(srcFile);
                    // begin writing a new ZIP entry, positions the stream to the start of the entry data
                    zos.putNextEntry(new ZipEntry(archivo.getAdjuntoVo().getUrlZip()));
                    int length;
                    while ((length = fis.read(buffer)) > 0) {
                        zos.write(buffer, 0, length);
                    }
                    zos.closeEntry();
                    // close the InputStream
                    fis.close();
                } catch (SIAException ex) {
                    zos.putNextEntry(new ZipEntry(archivo.getAdjuntoVo().getUrlZip() + "_ERROR"));
                    zos.closeEntry();
                    Logger.getLogger(SiFacturaAdjuntoImpl.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ZipException ex) {
                    Logger.getLogger(SiFacturaAdjuntoImpl.class.getName()).log(Level.SEVERE, null, ex);
                } catch (Exception ex) {
                    Logger.getLogger(SiFacturaAdjuntoImpl.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            // close the ZipOutputStream
            zos.close();

        } catch (IOException ioe) {
            System.out.println("Error creating zip file: " + ioe);
        }
        return fileTempExcel;
    }

    
    public List<FacturaAdjuntoVo> traerArchivosFacturaByIds(String idsFactura, String uuid) {
        String c = " select "
                + " fa.id,fa.tipo_archivo,fa.si_factura,a.ID,a.NOMBRE,a.UUID,  "
                + " a.url,  "
                + " 'Proveedor/'||p.rfc||'/F'||f.folio||'_" + uuid + "/'||a.NOMBRE as urlZip "
                + " from si_factura f "
                + " inner join si_factura_adjunto fa on fa.si_factura = f.id "
                + " inner join proveedor p on p.id = f.proveedor "
                + " inner join si_adjunto a on fa.si_adjunto = a.id "
                + " where fa.si_factura in (" + idsFactura + ") "
                + " and fa.eliminado = false "
                + " union "
                + " select  "
                + " e.id, ad.tipo_archivo, a.id, ad.id, ad.nombre, ad.uuid, ad.url,  "
                + " 'Proveedor/'||p.rfc||'/F'||a.folio||'_" + uuid + "/'||ad.NOMBRE as urlZip "
                + " from si_factura a "
                + " inner join proveedor p on p.id = a.proveedor "
                + " inner join orden o on o.id = a.orden and o.eliminado = false "
                + " inner join oc_orden_ets e on e.orden = o.id and e.eliminado = false "
                + " inner join si_adjunto ad on ad.id = e.si_adjunto and ad.eliminado = false "
                + " inner join oc_categoria_ets ce on ce.id = e.oc_categoria_ets "
                + " where a.id in (" + idsFactura + ") "
                + " and a.eliminado = false "
                + " union "
                + " select  "
                + " 0, ad.tipo_archivo, a.id, ad.id, ad.nombre, ad.uuid,ad.url,  "
                + " 'Proveedor/'||p.rfc||'/F'||a.folio||'_" + uuid + "/'||ad.NOMBRE as urlZip "
                + " from si_factura a "
                + " inner join proveedor p on p.id = a.proveedor "
                + " inner join si_adjunto ad on ad.id = a.si_adjunto and ad.eliminado = false "
                + " where a.id in (" + idsFactura + ") "
                + " and a.eliminado = false "
                + " union "
                + " SELECT "
                + " fa.id, "
                + " fa.tipo_archivo, "
                + " fa.si_factura, "
                + " a.ID, "
                + " a.NOMBRE, "
                + " a.UUID, "
                + " a.url, "
                + " 'Proveedor/'||p.rfc||'/F'||ff.folio||'_" + uuid + "/'||a.NOMBRE as urlZip "
                + " FROM si_factura f "
                + " inner join si_factura_adjunto fa on fa.si_factura = f.id "
                + " inner join proveedor p on p.id = f.proveedor "
                + " inner join si_adjunto a on fa.si_adjunto = a.id "
                + " inner join si_factura ff on ff.id = f.si_factura "
                + " WHERE f.si_factura in (" + idsFactura + ") "
                + " AND f.eliminado = false ";

        List<Object[]> lo = em.createNativeQuery(c).getResultList();
        //
        List<FacturaAdjuntoVo> lfa = new ArrayList<>();
        for (Object[] objects : lo) {
            lfa.add(castFacturaAdjVoUrl(objects, true));
        }
        return lfa;
    }

    private boolean isContainBOM(File fileTemp) throws IOException {

        if (!fileTemp.exists()) {
            throw new IllegalArgumentException("Path: " + fileTemp.getPath() + " does not exists!");
        }

        boolean result = false;

        byte[] bom = new byte[3];
        try (InputStream is = new FileInputStream(fileTemp)) {

            // read 3 bytes of a file.
            is.read(bom);

            // BOM encoded as ef bb bf
            String content = new String(Hex.encodeHex(bom));
            if ("efbbbf".equalsIgnoreCase(content)) {
                result = true;
            }

        }

        return result;
    }
    
    private void removeBom(File fileTemp) throws IOException {

      if (isContainBOM(fileTemp)) {

          byte[] bytes = FileUtils.readFileToByteArray(fileTemp);

          ByteBuffer bb = ByteBuffer.wrap(bytes);

          System.out.println("Found BOM!");

          byte[] bom = new byte[3];
          // get the first 3 bytes
          bb.get(bom, 0, bom.length);

          // remaining
          byte[] contentAfterFirst3Bytes = new byte[bytes.length - 3];
          bb.get(contentAfterFirst3Bytes, 0, contentAfterFirst3Bytes.length);

          System.out.println("Remove the first 3 bytes, and overwrite the file!");

          // override the same path
//          Files.write(path, contentAfterFirst3Bytes);
          FileUtils.writeByteArrayToFile(fileTemp, contentAfterFirst3Bytes);

      } else {
          System.out.println("This file doesn't contains UTF-8 BOM!");
      }

  }

    
    public File crearFile(FacturaAdjuntoVo archivo) {
        File fileTemp = null;
        try {
            if (archivo != null && archivo.getAdjuntoVo().getUrl() != null && !archivo.getAdjuntoVo().getUrl().isEmpty()) {
                AlmacenDocumentos almacenDocumentos = proveedorAlmacenDocumentosRemote.getAlmacenDocumentos();
                String REPOSITORYPATH = this.parametrosSistemaServicioRemoto.find(1).getUploadDirectory();
                String PLANTILLAPATH = "Factura/Temporal";
                String URL_Temporal = new StringBuilder().append(REPOSITORYPATH).append(PLANTILLAPATH).append(File.separator).toString();
                fileTemp = new File(URL_Temporal + archivo.getAdjuntoVo().getNombre());

                FileOutputStream fos = new FileOutputStream(fileTemp);

                if (archivo.getAdjuntoVo() != null
                        && archivo.getAdjuntoVo().getUrl() != null && !archivo.getAdjuntoVo().getUrl().isEmpty()) {
                    try {
                        DocumentoAnexo documento = almacenDocumentos.cargarDocumento(archivo.getAdjuntoVo().getUrl());
                        fos.write(documento.getContenido());

                    } catch (SIAException ex) {
                        Logger.getLogger(SiFacturaAdjuntoImpl.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (Exception ex) {
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

    
    public boolean notificacionArchivosFactura(int idFactura, String cc, String cco, String tipos) {

        File facturaPDF = null;
        File facturaXML = null;

        boolean ret = false;
        String para = "";

        try {

            List<FacturaAdjuntoVo> lfa = this.traerSoporteFactura(idFactura, true, tipos);

            for (FacturaAdjuntoVo vo : lfa) {
                if ("XML (Factura)".equalsIgnoreCase(vo.getTipo()) || "XML (Nota Credito)".equalsIgnoreCase(vo.getTipo())) {
                    facturaXML = this.crearFile(vo);
                    this.removeBom(facturaXML);

                }

                if ("PDF (Factura)".equalsIgnoreCase(vo.getTipo()) || "PDF (Nota Credito)".equalsIgnoreCase(vo.getTipo())) {
                    facturaPDF = this.crearFile(vo);                    
                }

            }

            para = Configurador.emailFacturaAvanzia();            
            ret = enviarCorreoRemote.enviarCorreoAvz(para, cc, cco,
                    "",
                    new StringBuilder(""),
                    null,
                    null,
                    facturaXML != null ? facturaXML : null,
                    facturaPDF != null ? facturaPDF : null);

        } catch (Exception e) {
            LOGGER.error("Error al enviar archivos del portal de proveedores : ", e);
        } finally {
            if (facturaPDF != null && facturaPDF.exists()) {
                facturaPDF.delete();
            }
            if (facturaXML != null && facturaXML.exists()) {
                facturaXML.delete();
            }

        }
        return ret;
    }
}
