/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.proveedor.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.LocalBean;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.constantes.Constantes;
import sia.modelo.Proveedor;
import sia.modelo.PvClasificacionArchivo;
import sia.modelo.PvDocumento;
import sia.modelo.SiAdjunto;
import sia.modelo.Usuario;
import sia.modelo.documento.vo.DocumentoVO;
import sia.modelo.proveedor.Vo.ProveedorDocumentoVO;
import sia.modelo.sistema.AbstractFacade;
import sia.servicios.catalogos.impl.UsuarioImpl;
import sia.servicios.sistema.impl.SiAdjuntoImpl;
import sia.util.UtilLog4j;

/**
 *
 * @author mluis
 */
@LocalBean 
public class PvClasificacionArchivoImpl extends AbstractFacade<PvClasificacionArchivo> {

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public PvClasificacionArchivoImpl() {
        super(PvClasificacionArchivo.class);
    }
    @Inject
    private PvDocumentoImpl pvDocumentoRemote;
    @Inject
    private ProveedorServicioImpl proveedorServicioRemoto;
    @Inject
    private SiAdjuntoImpl servicioSiAdjunto;
    @Inject
    private UsuarioImpl usuarioRemote;
    @Inject
    private SiAdjuntoImpl siAdjuntoRemote;

    
    public boolean guardarClasificacionArchivo(String sia, int idNumerico, SiAdjunto siAdjunto, int clasificacion) {
        boolean v;
        try {
            PvClasificacionArchivo pvClasificacionArchivo = new PvClasificacionArchivo();
            pvClasificacionArchivo.setGenero(this.usuarioRemote.find(sia));
            pvClasificacionArchivo.setObligatoria(Constantes.BOOLEAN_FALSE);
            pvClasificacionArchivo.setSiAdjunto(siAdjunto);
            pvClasificacionArchivo.setProveedor(this.proveedorServicioRemoto.find(idNumerico));
            pvClasificacionArchivo.setPvDocumento(this.pvDocumentoRemote.find(clasificacion));
            pvClasificacionArchivo.setFechaGenero(new Date());
            pvClasificacionArchivo.setHoraGenero(new Date());
            pvClasificacionArchivo.setEliminado(false);
            this.create(pvClasificacionArchivo);
            v = true;
        } catch (Exception e) {
            v = false;
        }
        return v;
    }

    
    public void eliminarArchivoModel(PvClasificacionArchivo pvClasificacionArchivo, String sesion) {
        pvClasificacionArchivo.setEliminado(true);
        edit(pvClasificacionArchivo);
        //
        servicioSiAdjunto.eliminarArchivo(pvClasificacionArchivo.getSiAdjunto().getId(), sesion);
    }

    
    public List<PvClasificacionArchivo> traerArchivos(int elemento) {
        try {
            return em.createQuery("SELECT f FROM PvClasificacionArchivo f "
                    + " WHERE f.proveedor.id = :id AND f.eliminado = :t").setParameter("t", false).setParameter("id", elemento).getResultList();
        } catch (Exception e) {
            return null;
        }
    }

    
    public List<ProveedorDocumentoVO> traerArchivoPorProveedorOid(int idProveedor, int idDocProv) {
        List<ProveedorDocumentoVO> lvo = null;
        try {
            String cad = " SELECT a.ID, a.PROVEEDOR, a.GENERO, a.PV_DOCUMENTO, a.FECHA_GENERO, a.HORA_GENERO, a.SI_ADJUNTO, a.ELIMINADO, d.NOMBRE, ";
            //                      0        1           2         3             4                  5             6            7            8 
            cad += " a.FECHA_ENTREGA, a.INICIO_VIGENCIA, a.FIN_VIGENCIA, a.VALIDO, ad.NOMBRE, ad.UUID, d.MULTI, a.obligatoria, ad.url ";
            //               9              10                11            12       13           14      15        16
            cad += " FROM PV_CLASIFICACION_ARCHIVO a ";
            cad += " left join PV_DOCUMENTO d on d.id = a.PV_DOCUMENTO and d.ELIMINADO = 'False' ";
            cad += " left join SI_ADJUNTO ad on ad.id = a.SI_ADJUNTO and ad.ELIMINADO = 'False' ";
            cad += " where a.ELIMINADO = '" + Constantes.NO_ELIMINADO + "' ";
            if (idProveedor > 0) {
                cad += " and a.PROVEEDOR = " + idProveedor;
            } else if (idDocProv > 0) {
                cad += " and a.ID = " + idDocProv;
            }

            cad += " order by a.PV_DOCUMENTO, a.id ASC";

            List<Object[]> lo = em.createNativeQuery(cad).getResultList();
            if (lo != null) {
                lvo = new ArrayList<>();
                for (Object[] lo1 : lo) {
                    lvo.add(castProvDocto(lo1));
                }
            }

        } catch (Exception e) {
            lvo = new ArrayList<>();
            UtilLog4j.log.fatal(e);
        }
        return lvo;
    }
    
    
    public List<ProveedorDocumentoVO> traerArchivoPorProveedorListid(int idProveedor, int idList, int docID) {
        List<ProveedorDocumentoVO> lvo = null;
        try {
            String cad = " SELECT a.ID, a.PROVEEDOR, a.GENERO, a.PV_DOCUMENTO, a.FECHA_GENERO, a.HORA_GENERO, a.SI_ADJUNTO, a.ELIMINADO, d.NOMBRE, ";
            //                      0        1           2         3             4                  5             6            7            8 
            cad += " a.FECHA_ENTREGA, a.INICIO_VIGENCIA, a.FIN_VIGENCIA, a.VALIDO, ad.NOMBRE, ad.UUID, d.MULTI, a.obligatoria, ad.url ";
            //               9              10                11            12       13           14      15        16
            cad += " FROM PV_CLASIFICACION_ARCHIVO a ";
            cad += " left join PV_DOCUMENTO d on d.id = a.PV_DOCUMENTO and d.ELIMINADO = false ";
            cad += " left join SI_LISTA_ELEMENTO le on le.ID = d.SI_LISTA_ELEMENTO and le.eliminado = false ";
            cad += " left join SI_ADJUNTO ad on ad.id = a.SI_ADJUNTO and ad.ELIMINADO = false ";
            cad += " where a.ELIMINADO = false ";
            
            if (idProveedor > 0) {
                cad += " and a.PROVEEDOR = " + idProveedor;
            } 
            
            if (idList > 0) {
                cad += " and le.si_lista = " + idList;
            }
            
            if (docID > 0) {
                cad += " and d.id = " + docID;
            }

            cad += " and ad.uuid is not null order by a.PV_DOCUMENTO, a.id ASC";

            List<Object[]> lo = em.createNativeQuery(cad).getResultList();
            if (lo != null) {
                lvo = new ArrayList<>();
                for (Object[] lo1 : lo) {
                    lvo.add(castProvDocto(lo1));
                }
            }

        } catch (Exception e) {
            lvo = new ArrayList<>();
            UtilLog4j.log.fatal(e);
        }
        return lvo;
    }

    private ProveedorDocumentoVO castProvDocto(Object[] lo1) {
        ProveedorDocumentoVO vo = new ProveedorDocumentoVO();
        vo.setId((Integer) lo1[0]);
        vo.setIdDocumento((Integer) lo1[3]);
        vo.setDocumento((String) lo1[8]);
        vo.getAdjuntoVO().setId(lo1[6] != null ? (Integer) lo1[6] : 0);
        vo.getAdjuntoVO().setNombre((String) lo1[13]);
        vo.getAdjuntoVO().setUuid((String) lo1[14]);
        vo.getAdjuntoVO().setUrl((String) lo1[17]);
        vo.setFechaEntrega((Date) lo1[9]);
        vo.setInicioVigencia((Date) lo1[10]);
        vo.setFinVigencia((Date) lo1[11]);
        vo.setValido((Boolean) lo1[12]);
        vo.setObligatoria(lo1[16] != null ? (Boolean) lo1[16] : Constantes.BOOLEAN_FALSE);
        vo.setMultiArchivo(lo1[15] != null ? (Boolean) lo1[15] : false);
        return vo;
    }

    
    public List<ProveedorDocumentoVO> traerArchivoPorProveedorYDoc(int idProveedor, int idDoc, boolean conArchivo) {
        List<ProveedorDocumentoVO> lvo = null;
        try {
            String cad = " SELECT a.ID, a.PROVEEDOR, a.GENERO, a.PV_DOCUMENTO, a.FECHA_GENERO, a.HORA_GENERO, a.SI_ADJUNTO, a.ELIMINADO, d.NOMBRE, ";
            //                      0        1           2         3             4                  5             6            7            8 
            cad += " a.FECHA_ENTREGA, a.INICIO_VIGENCIA, a.FIN_VIGENCIA, a.VALIDO, ad.NOMBRE, ad.UUID, d.MULTI ";
            //               9              10                11            12       13           14      15
            cad += " FROM PV_CLASIFICACION_ARCHIVO a ";
            cad += " left join PV_DOCUMENTO d on d.id = a.PV_DOCUMENTO and d.ELIMINADO = 'False' ";
            cad += " left join SI_ADJUNTO ad on ad.id = a.SI_ADJUNTO and ad.ELIMINADO = 'False' ";
            cad += " where a.ELIMINADO = '" + Constantes.NO_ELIMINADO + "' ";
            if (idProveedor > 0) {
                cad += " and a.PROVEEDOR = " + idProveedor;
            }

            if (idDoc > 0) {
                cad += " and a.PV_DOCUMENTO = " + idDoc;
            }

            if (conArchivo) {
                cad += " and a.SI_ADJUNTO is not null ";
            }

            cad += " order by a.PV_DOCUMENTO, a.id ASC";

            List<Object[]> lo = em.createNativeQuery(cad).getResultList();
            if (lo != null) {
                lvo = new ArrayList<ProveedorDocumentoVO>();
                for (Object[] lo1 : lo) {
                    ProveedorDocumentoVO vo = new ProveedorDocumentoVO();
                    vo.setId((Integer) lo1[0]);
                    vo.setIdDocumento((Integer) lo1[3]);
                    vo.setDocumento((String) lo1[8]);
                    vo.getAdjuntoVO().setId(lo1[6] != null ? (Integer) lo1[6] : 0);
                    vo.getAdjuntoVO().setNombre((String) lo1[13]);
                    vo.getAdjuntoVO().setUuid((String) lo1[14]);
                    vo.setFechaEntrega((Date) lo1[9]);
                    vo.setInicioVigencia((Date) lo1[10]);
                    vo.setFinVigencia((Date) lo1[11]);
                    vo.setValido((Boolean) lo1[12]);
                    vo.setMultiArchivo((String) lo1[15] != null ? (Boolean) lo1[15] : false);

                    lvo.add(vo);
                }
            }

        } catch (Exception e) {
            lvo = new ArrayList<ProveedorDocumentoVO>();
            UtilLog4j.log.fatal(e);
        }
        return lvo;
    }

    
    public boolean buscarTipoDocumento(PvDocumento pvDocumento) {
        boolean v;
        try {
            List<PvClasificacionArchivo> lista;
            lista = em.createQuery("SELECT f FROM PvClasificacionArchivo f WHERE f.pvDocumento.id = :id").setParameter("id", pvDocumento.getId()).getResultList();
            if (lista.size() > 0) {
                v = true;
            } else {
                v = false;
            }
        } catch (Exception e) {
            v = false;
        }

        return v;
    }

    
    public boolean guardar(String sesion, List<DocumentoVO> listaDocsProv, int provId) {
        boolean guardo = false;
        try {
            for (DocumentoVO pvDocto : listaDocsProv) {
                guardar(sesion, pvDocto, provId);
                guardo = true;
            }
        } catch (Exception e) {
            guardo = false;
            UtilLog4j.log.fatal(e);
        }
        return guardo;
    }

    
    public void guardar(String sesion, DocumentoVO vo, int provId) {
        try {
            PvClasificacionArchivo doc = new PvClasificacionArchivo();
            doc.setPvDocumento(new PvDocumento(vo.getId()));
            doc.setProveedor(new Proveedor(provId));
            doc.setObligatoria(vo.isObligatoria());
            doc.setValido(Constantes.BOOLEAN_FALSE);
            doc.setGenero(new Usuario(sesion));
            doc.setFechaGenero(new Date());
            doc.setHoraGenero(new Date());
            doc.setEliminado(Constantes.NO_ELIMINADO);
            create(doc);
        } catch (Exception e) {
            UtilLog4j.log.fatal(e);
        }
    }
    
    
    public void guardar(String sesion, DocumentoVO vo, int provId, int idAdjunto) {
        try {
            PvClasificacionArchivo doc = new PvClasificacionArchivo();
            doc.setPvDocumento(new PvDocumento(vo.getTipoDoc()));
            doc.setProveedor(new Proveedor(provId));
            doc.setObligatoria(vo.isObligatoria());
            doc.setValido(Constantes.BOOLEAN_FALSE);
            doc.setGenero(new Usuario(sesion));
            doc.setFechaGenero(new Date());
            doc.setHoraGenero(new Date());
            doc.setSiAdjunto(idAdjunto > 0 ? new SiAdjunto(idAdjunto) : null);
            doc.setEliminado(Constantes.NO_ELIMINADO);
            create(doc);
        } catch (Exception e) {
            UtilLog4j.log.fatal(e);
        }
    }

    
    public void quitarArchivoDocumento(String sesion, int idDocProv) {
        try {
            PvClasificacionArchivo doc = find(idDocProv);
            doc.setSiAdjunto(null);
            doc.setModifico(new Usuario(sesion));
            doc.setFechaModifico(new Date());
            doc.setHoraModifico(new Date());
            edit(doc);
            //
            siAdjuntoRemote.eliminarArchivo(doc.getSiAdjunto().getId(), sesion);
        } catch (Exception e) {
            UtilLog4j.log.fatal(e);
        }
    }

    
    public void agregarArchivo(String sesion, ProveedorDocumentoVO proveedorDocumentoVO, int idAdjunto) {
        try {
            PvClasificacionArchivo doc = buscarPorIdLazy(proveedorDocumentoVO.getId());
            doc.setSiAdjunto(idAdjunto > 0 ? new SiAdjunto(idAdjunto) : null);
            doc.setFechaEntrega(proveedorDocumentoVO.getFechaEntrega());
            doc.setInicioVigencia(proveedorDocumentoVO.getInicioVigencia());
            doc.setFinVigencia(proveedorDocumentoVO.getFinVigencia());
            doc.setModifico(new Usuario(sesion));
            doc.setFechaModifico(new Date());
            doc.setHoraModifico(new Date());
            edit(doc);
        } catch (Exception e) {
            UtilLog4j.log.fatal(e);
        }
    }

    
    public void eliminar(String sesion, int idDocAdjunto) {
        try {
            PvClasificacionArchivo doc = buscarPorIdLazy(idDocAdjunto);
            doc.setModifico(new Usuario(sesion));
            doc.setFechaModifico(new Date());
            doc.setHoraModifico(new Date());
            doc.setEliminado(Constantes.ELIMINADO);
            edit(doc);
            //
            siAdjuntoRemote.eliminarArchivo(doc.getSiAdjunto().getId(), sesion);
        } catch (Exception e) {
            UtilLog4j.log.fatal(e);
        }
    }

    
    public void guardar(String sesion, int idDocProv, int adjunto) {
        try {
            PvClasificacionArchivo doc = this.buscarPorIdLazy(idDocProv);
            doc.setSiAdjunto(new SiAdjunto(adjunto));
            doc.setModifico(new Usuario(sesion));
            doc.setFechaModifico(new Date());
            doc.setHoraModifico(new Date());
            doc.setEliminado(Constantes.NO_ELIMINADO);
            this.edit(doc);
        } catch (Exception e) {
            UtilLog4j.log.fatal(e);
        }
    }

    
    public List<ProveedorDocumentoVO> traerArchivoPorProveedor(int idProveedor) {
        List<ProveedorDocumentoVO> lvo = null;
        try {
            String cad = " SELECT a.ID, a.PROVEEDOR, a.GENERO, a.PV_DOCUMENTO, a.FECHA_GENERO, a.HORA_GENERO, a.SI_ADJUNTO, a.ELIMINADO, d.NOMBRE, "
                    + " a.FECHA_ENTREGA, a.INICIO_VIGENCIA, a.FIN_VIGENCIA, a.VALIDO, ad.NOMBRE, ad.UUID, d.MULTI, a.obligatoria, ad.url "
                    + " FROM PV_CLASIFICACION_ARCHIVO a "
                    + "     left join PV_DOCUMENTO d on d.id = a.PV_DOCUMENTO and d.ELIMINADO = 'False' "
                    + "     left join SI_ADJUNTO ad on ad.id = a.SI_ADJUNTO and ad.ELIMINADO = 'False' "
                    + " where a.ELIMINADO = false "
                    + " and a.PROVEEDOR = " + idProveedor
                    + " order by a.PV_DOCUMENTO, a.id ASC";

            List<Object[]> lo = em.createNativeQuery(cad).getResultList();
            if (lo != null) {
                lvo = new ArrayList<>();
                for (Object[] lo1 : lo) {
                    lvo.add(castProvDocto(lo1));
                }
            }

        } catch (Exception e) {
            lvo = new ArrayList<>();
            UtilLog4j.log.fatal(e);
        }
        return lvo;
    }

    
    public void eliminarArchivo(int idClasificacionArchivo, int idAdjunto, String sesion) {
        PvClasificacionArchivo ca = buscarPorIdLazy(idClasificacionArchivo);
        ca.setSiAdjunto(null);
        ca.setModifico(new Usuario(sesion));
        ca.setFechaModifico(new Date());
        ca.setHoraModifico(new Date());

        edit(ca);
        //
        servicioSiAdjunto.eliminarArchivo(idAdjunto, sesion);
    }
    
    private PvClasificacionArchivo buscarPorIdLazy(int idProveedorDocto) {
        try {
         return (PvClasificacionArchivo) em.createNamedQuery("PvClasificacionArchivo.buscarPorId").setParameter(1, idProveedorDocto).getSingleResult();
        } catch (Exception e) {
            UtilLog4j.log.error(e);
            return null;
        }
    }
}
