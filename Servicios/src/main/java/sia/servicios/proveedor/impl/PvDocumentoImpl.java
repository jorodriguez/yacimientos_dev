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
import sia.modelo.PvDocumento;
import sia.modelo.Usuario;
import sia.modelo.documento.vo.DocumentoVO;
import sia.modelo.sgl.vo.Vo;
import sia.modelo.sistema.AbstractFacade;
import sia.servicios.sistema.impl.SiListaElementoImpl;

/**
 *
 * @author mluis
 */
@LocalBean 
public class PvDocumentoImpl extends AbstractFacade<PvDocumento>{

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    @Inject
    private SiListaElementoImpl siListaElementoRemote;

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public PvDocumentoImpl() {
        super(PvDocumento.class);
    }

    
    public List<PvDocumento> traerDocumentosActivos(String t) {
        try {
            return em.createQuery("SELECT d FROM PvDocumento d WHERE d.eliminado = :t ORDER BY d.nombre ASC").setParameter("t", t).getResultList();
        } catch (Exception e) {
            return null;
        }
    }

    
    public void guardarModificacionDocumento(PvDocumento pvDocumento, Usuario usuario) {
        pvDocumento.setGenero(usuario);
        pvDocumento.setNombre(pvDocumento.getNombre());
        pvDocumento.setFechaGenero(new Date());
        pvDocumento.setHoraGenero(new Date());
        this.edit(pvDocumento);
    }

    
    public void eliminarDocumento(PvDocumento pvDocumento, Usuario usuario) {
        pvDocumento.setEliminado(true);
        pvDocumento.setGenero(usuario);
        pvDocumento.setFechaGenero(new Date());
        pvDocumento.setHoraGenero(new Date());
        this.edit(pvDocumento);
    }

    
    public boolean crearDocumento(PvDocumento pvDocumento, Usuario usuario) {
        boolean v = false;
        try {
            PvDocumento pvDoc = this.buscarPorNombre(pvDocumento.getNombre(), true);
            if (pvDoc == null) {
                PvDocumento documento = new PvDocumento();
                documento.setNombre(pvDocumento.getNombre());
                documento.setDescripcion(pvDocumento.getDescripcion());
                documento.setFechaGenero(new Date());
                documento.setHoraGenero(new Date());
                documento.setGenero(usuario);
                documento.setEliminado(false);
                this.create(documento);
                v = true;
            } else {
                pvDoc.setNombre(pvDocumento.getNombre());
                pvDoc.setDescripcion(pvDocumento.getDescripcion());
                pvDoc.setFechaGenero(new Date());
                pvDoc.setHoraGenero(new Date());
                pvDoc.setGenero(usuario);
                pvDoc.setEliminado(false);
                this.edit(pvDoc);
                v = true;
            }

        } catch (Exception e) {
            v = false;
        }

        return v;
    }

    
    public PvDocumento buscarPorNombre(String nombre, boolean eliminado) {
        try {
            return (PvDocumento) em.createQuery("SELECT d FROM PvDocumento d WHERE d.nombre = :nom AND d.eliminado = :f").setParameter("f", eliminado).setParameter("nom", nombre).getSingleResult();
        } catch (Exception e) {
            return null;
        }

    }

    
    public List<DocumentoVO> traerDocumento(String tipos) {
        List<DocumentoVO> lvo = null;
        String cad = " SELECT d.id, d.nombre, d.descripcion, d.SI_LISTA_ELEMENTO, le.NOMBRE FROM Pv_Documento d left join SI_LISTA_ELEMENTO le on le.ID = d.SI_LISTA_ELEMENTO  and le.eliminado = false where d.eliminado = false ";
        if(tipos != null && !tipos.isEmpty()){
            cad += " and le.si_lista in ("+tipos+") ";
        }
        cad +=" order by d.SI_LISTA_ELEMENTO ASC ";
        
        List<Object[]> lo = em.createNativeQuery(cad).getResultList();
        if (lo != null) {
            lvo = new ArrayList<DocumentoVO>();
            for (Object[] lo1 : lo) {
                DocumentoVO vo = new DocumentoVO();
                vo.setId((Integer) lo1[0]);
                vo.setNombre((String) lo1[1]);
                vo.setDescripcion((String) lo1[2]);
                vo.setSelected(false);
                vo.setTipoDoc((Integer) lo1[3] != null ? (Integer) lo1[3] : 0);
                vo.setTipoDocTxt((String) lo1[4]);
                lvo.add(vo);
            }
        }
        return lvo;
    }

    
    public List<DocumentoVO> traerDocumentoFaltante(int idConvenio, int idTipo) {
        List<DocumentoVO> lvo = null;
        String cad = "SELECT d.id, d.nombre, d.descripcion FROM Pv_Documento d ";
        cad += " where d.eliminado = 'False' ";
        cad += " and d.ID not in (select cd.PV_DOCUMENTO from CV_CONVENIO_DOCUMENTO cd where cd.CONVENIO = ";
        cad += " " + idConvenio + "	and cd.ELIMINADO = '" + Constantes.NO_ELIMINADO + "')";
        if (idTipo > 0) {
            cad += " and d.SI_LISTA_ELEMENTO =  " + idTipo;
        }
        cad += " order by d.nombre ASC";
        List<Object[]> lo = em.createNativeQuery(cad).getResultList();
        if (lo != null) {
            lvo = new ArrayList<DocumentoVO>();
            for (Object[] lo1 : lo) {
                DocumentoVO vo = new DocumentoVO();
                vo.setId((Integer) lo1[0]);
                vo.setNombre((String) lo1[1]);
                vo.setDescripcion((String) lo1[2]);
                vo.setSelected(false);
                lvo.add(vo);
            }
        }
        return lvo;
    }

    
    public List<DocumentoVO> traerDocFaltanteProveedor(int idProveedor, int idTipo) {
        List<DocumentoVO> lvo = null;
        String cad = "SELECT d.id, d.nombre, d.descripcion, d.MULTI  FROM Pv_Documento d ";
        cad += " where d.eliminado = 'False' ";
        cad += " and (d.ID not in (select cd.PV_DOCUMENTO from PV_CLASIFICACION_ARCHIVO cd where cd.PROVEEDOR = ";
        cad += " " + idProveedor + "	and cd.ELIMINADO = '" + Constantes.NO_ELIMINADO + "') or d.MULTI = 'True' )";
        if (idTipo > 0) {
            cad += " and d.SI_LISTA_ELEMENTO =  " + idTipo;
        }
        cad += " order by d.nombre ASC";
        List<Object[]> lo = em.createNativeQuery(cad).getResultList();
        if (lo != null) {
            lvo = new ArrayList<DocumentoVO>();
            for (Object[] lo1 : lo) {
                DocumentoVO vo = new DocumentoVO();
                vo.setId((Integer) lo1[0]);
                vo.setNombre((String) lo1[1]);
                vo.setDescripcion((String) lo1[2]);
                vo.setSelected(false);
                vo.setMultiArchivo(lo1[3] != null ? (Boolean) lo1[3] : false);
                lvo.add(vo);
            }
        }
        return lvo;
    }

    
    public void activarDocumento(PvDocumento pvDocumento, Usuario usuario) {
        pvDocumento.setEliminado(false);
        pvDocumento.setFechaGenero(new Date());
        pvDocumento.setHoraGenero(new Date());
        pvDocumento.setGenero(usuario);
        this.edit(pvDocumento);
    }

    /**
     *
     * @param sesion
     * @param vo
     */
    
    public void guardar(String sesion, String nombre, String descripcion, int tipo) {
        PvDocumento pvDocumento = new PvDocumento();
        pvDocumento.setNombre(nombre);
        pvDocumento.setDescripcion(descripcion);
        pvDocumento.setGenero(new Usuario(sesion));
        pvDocumento.setFechaGenero(new Date());
        pvDocumento.setHoraGenero(new Date());
        pvDocumento.setEliminado(Constantes.NO_ELIMINADO);
        pvDocumento.setSiListaElemento(siListaElementoRemote.find(tipo));
        create(pvDocumento);
    }

    /**
     *
     * @param sesion
     * @param vo
     */
    
    public void modificar(String sesion, int idTipo, String nombre, String descripcion, int tipo) {
        PvDocumento pvDocumento = find(idTipo);
        pvDocumento.setNombre(nombre);
        pvDocumento.setDescripcion(descripcion);
        pvDocumento.setModifico(new Usuario(sesion));
        pvDocumento.setFechaModifico(new Date());
        pvDocumento.setHoraModifico(new Date());
        pvDocumento.setSiListaElemento(siListaElementoRemote.find(tipo));
        edit(pvDocumento);
    }

    /**
     *
     * @param sesion
     * @param idTipo
     */
    
    public void eliminar(String sesion, int idTipo) {
        PvDocumento pvDocumento = find(idTipo);
        pvDocumento.setModifico(new Usuario(sesion));
        pvDocumento.setFechaModifico(new Date());
        pvDocumento.setHoraModifico(new Date());
        pvDocumento.setEliminado(Constantes.ELIMINADO);
        edit(pvDocumento);
    }

    
    public boolean isUsado(int id) {
        List<Object[]> lo = em.createNativeQuery("select * from CV_CONVENIO_DOCUMENTO cd where cd.PV_DOCUMENTO = " + id + " and cd.eliminado = 'False'").getResultList();
        return !lo.isEmpty();
    }

    
    public Vo buscarPorId(int id) {
        Vo lvo = null;
        String cad = "SELECT d.id, d.nombre, d.descripcion FROM Pv_Documento d where d.id = " + id + " and  d.eliminado = 'False' ";
        Object[] lo = (Object[]) em.createNativeQuery(cad).getSingleResult();
        if (lo != null) {
            lvo = new Vo();
            lvo.setId((Integer) lo[0]);
            lvo.setNombre((String) lo[1]);
            lvo.setDescripcion((String) lo[2]);
        }
        return lvo;
    }

    /**
     *
     * @param idTipo
     * @return
     */
    
    public List<DocumentoVO> traerDocumentoPorTipo(int idTipo) {
        List<DocumentoVO> lvo = null;
        String cad = "SELECT d.id, d.nombre, d.descripcion, d.MULTI  FROM Pv_Documento d ";
        cad += " where d.eliminado = 'False' ";
        cad += " and d.SI_LISTA_ELEMENTO =  " + idTipo;
        cad += " order by d.nombre ASC";
        List<Object[]> lo = em.createNativeQuery(cad).getResultList();
        if (lo != null) {
            lvo = new ArrayList<DocumentoVO>();
            for (Object[] lo1 : lo) {
                DocumentoVO vo = new DocumentoVO();
                vo.setId((Integer) lo1[0]);
                vo.setNombre((String) lo1[1]);
                vo.setDescripcion((String) lo1[2]);
                vo.setSelected(false);
                vo.setMultiArchivo(lo1[3] != null ? (Boolean) lo1[3] : false);
                lvo.add(vo);
            }
        }
        return lvo;
    }
}
