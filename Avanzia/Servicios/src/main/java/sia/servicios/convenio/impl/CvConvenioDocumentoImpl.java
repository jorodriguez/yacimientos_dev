/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.convenio.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.constantes.Constantes;
import sia.modelo.Convenio;
import sia.modelo.CvConvenioDocumento;
import sia.modelo.PvDocumento;
import sia.modelo.SiAdjunto;
import sia.modelo.Usuario;
import sia.modelo.contrato.vo.ContratoDocumentoVo;
import sia.modelo.contrato.vo.ContratoVO;
import sia.modelo.sgl.vo.Vo;
import sia.modelo.sistema.AbstractFacade;
import sia.util.UtilLog4j;

/**
 *
 * @author ihsa
 */
@Stateless 
public class CvConvenioDocumentoImpl extends AbstractFacade<CvConvenioDocumento> {

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
    @Inject
    private CvConvenioAdjuntoImpl cvConvenioAdjuntoLocal;

    public CvConvenioDocumentoImpl() {
        super(CvConvenioDocumento.class);
    }

    
    public void guardar(String sesion, List<Vo> listaConvenioDocumento, int idConvenio) {
        for (Vo pvDocto : listaConvenioDocumento) {
            guardar(sesion, pvDocto, idConvenio);
        }
    }

    
    public void guardar(String sesion, Vo vo, int idConvenio) {

        CvConvenioDocumento cvConvenioDocumento = new CvConvenioDocumento();
        cvConvenioDocumento.setConvenio(new Convenio(idConvenio));
        cvConvenioDocumento.setPvDocumento(new PvDocumento(vo.getId()));
        cvConvenioDocumento.setValido(Constantes.BOOLEAN_FALSE);
        cvConvenioDocumento.setGenero(new Usuario(sesion));
        cvConvenioDocumento.setFechaGenero(new Date());
        cvConvenioDocumento.setHoraGenero(new Date());
        cvConvenioDocumento.setEliminado(Constantes.NO_ELIMINADO);
        create(cvConvenioDocumento);
    }

    
    public void guardar(String sesion, Vo vo, int idConvenio, int idAdjunto) {

        CvConvenioDocumento cvConvenioDocumento = new CvConvenioDocumento();
        cvConvenioDocumento.setConvenio(new Convenio(idConvenio));
        cvConvenioDocumento.setPvDocumento(new PvDocumento(vo.getId()));
        cvConvenioDocumento.setSiAdjunto(idAdjunto > 0 ? new SiAdjunto(idAdjunto) : null);
        cvConvenioDocumento.setValido(Constantes.BOOLEAN_FALSE);
        cvConvenioDocumento.setGenero(new Usuario(sesion));
        cvConvenioDocumento.setFechaGenero(new Date());
        cvConvenioDocumento.setHoraGenero(new Date());
        cvConvenioDocumento.setEliminado(Constantes.NO_ELIMINADO);
        create(cvConvenioDocumento);
    }

    
    public List<ContratoDocumentoVo> traerDoctosPorConveni(int idConvenio, String tiposDocs, String excluirTipoDocs) {
        List<ContratoDocumentoVo> lvo = null;
        String sb = consulta();
        sb += "	where c.ID = " + idConvenio;
        sb += "	and cd.ELIMINADO = '" + Constantes.NO_ELIMINADO + "'";

        if (tiposDocs != null && !tiposDocs.isEmpty()) {
            sb += "	and cd.PV_DOCUMENTO in (" + tiposDocs + ") ";
        }

        if (excluirTipoDocs != null && !excluirTipoDocs.isEmpty()) {
            sb += "	and cd.PV_DOCUMENTO not in (" + excluirTipoDocs + ") ";
        }

        sb += "	order by pd.nombre";
        List<Object[]> lo = em.createNativeQuery(sb).getResultList();

        if (lo != null) {
            lvo = new ArrayList<ContratoDocumentoVo>();
            for (Object[] lo1 : lo) {
                lvo.add(castContratoConvenioVo(lo1));
            }
        }
        return lvo;
    }

    private ContratoDocumentoVo castContratoConvenioVo(Object[] obj) {
        ContratoDocumentoVo vo = new ContratoDocumentoVo();
        vo.setId((Integer) obj[0]);
        vo.setIdDocumento((Integer) obj[1]);
        vo.setDocumento((String) obj[2]);
        vo.getAdjuntoVO().setId(obj[3] != null ? (Integer) obj[3] : 0);
        vo.getAdjuntoVO().setNombre((String) obj[4]);
        vo.getAdjuntoVO().setUuid((String) obj[5]);
        vo.setFechaEntrega((Date) obj[6]);
        vo.setInicioVigencia((Date) obj[7]);
        vo.setFinVigencia((Date) obj[8]);
        vo.setValido((Boolean) obj[9]);
        return vo;
    }

    
    public void agregarArchivo(String sesion, ContratoDocumentoVo contratoDocumentoVo, int idAdjunto) {

        CvConvenioDocumento cvConvenioDocumento = find(contratoDocumentoVo.getId());
        cvConvenioDocumento.setSiAdjunto(idAdjunto > 0 ? new SiAdjunto(idAdjunto) : null);
        cvConvenioDocumento.setFechaEntrega(contratoDocumentoVo.getFechaEntrega());
        cvConvenioDocumento.setInicioVigencia(contratoDocumentoVo.getInicioVigencia());
        cvConvenioDocumento.setFinVigencia(contratoDocumentoVo.getFinVigencia());
        cvConvenioDocumento.setModifico(new Usuario(sesion));
        cvConvenioDocumento.setFechaModifico(new Date());
        cvConvenioDocumento.setHoraModifico(new Date());
        edit(cvConvenioDocumento);
    }

    
    public ContratoDocumentoVo buscarPorId(int id) {
        ContratoDocumentoVo lvo = null;
        String sb = consulta();
        sb += "	where cd.ID = " + id;
        try {
            Object[] lo = (Object[]) em.createNativeQuery(sb).getSingleResult();
            lvo = castContratoConvenioVo(lo);
        } catch (Exception e) {
            UtilLog4j.log.fatal(e);
        }
        return lvo;
    }

    private String consulta() {
        String sb = "select cd.ID, pd.id, pd.NOMBRE, a.ID, a.NOMBRE, a.UUID, cd.fecha_entrega, cd.inicio_vigencia, cd.fin_vigencia, cd.valido from CV_CONVENIO_DOCUMENTO cd";
        sb += "	    inner join CONVENIO c on cd.CONVENIO = c.ID";
        sb += "	    inner join PV_DOCUMENTO pd on cd.PV_DOCUMENTO = pd.ID";
        sb += "	    left join SI_ADJUNTO a on cd.SI_ADJUNTO = a.ID";
        return sb;
    }

    
    public void eliminar(String sesion, int idConvenioAdjunto) {
        CvConvenioDocumento cvConvenioDocumento = find(idConvenioAdjunto);
        cvConvenioDocumento.setModifico(new Usuario(sesion));
        cvConvenioDocumento.setFechaModifico(new Date());
        cvConvenioDocumento.setHoraModifico(new Date());
        cvConvenioDocumento.setEliminado(Constantes.ELIMINADO);
        edit(cvConvenioDocumento);
    }

    
    public void quitarArchivoDocumento(String sesion, int idConvenioDocto) {
        CvConvenioDocumento cvConvenioDocumento = find(idConvenioDocto);
        cvConvenioDocumento.setSiAdjunto(null);
        cvConvenioDocumento.setModifico(new Usuario(sesion));
        cvConvenioDocumento.setFechaModifico(new Date());
        cvConvenioDocumento.setHoraModifico(new Date());
        edit(cvConvenioDocumento);
    }

    
    public List<ContratoVO> traerContratoDocumentacionIncompleta() {
        String sb = consultaAdjunto();
        sb += "	where cd.ELIMINADO = '" + Constantes.NO_ELIMINADO + "'";
        sb += "	    and cd.SI_ADJUNTO is null";
        sb += "    group by ";
        sb += "    	c.id, c.CODIGO, c.NOMBRE, p.NOMBRE, c.FECHA_FIRMA, c.FECHA_INICIO, ";
        sb += "	c.FECHA_VENCIMIENTO, c.VIGENCIA, c.PORCENTAJE_DEDUCCION, c.MONTO,";
        sb += "	m.SIGLAS, ct.NOMBRE, cc.NOMBRE, g.NOMBRE, e.NOMBRE, m.id, ct.id, ";
        sb += "	cc.id, g.id, e.id, c.convenio, p.id";
        List<Object[]> lo = em.createNativeQuery(sb).getResultList();
        //
        List<ContratoVO> lista = null;
        if (lo != null) {
            lista = new ArrayList<ContratoVO>();
            for (Object[] obj : lo) {
                lista.add(castContratos(obj));
            }
        }
        return lista;
    }

    
    public int totalContratoDocumentacionIncompleta() {
        String sb = "select count(distinct(cd.CONVENIO)) from CV_CONVENIO_DOCUMENTO cd ";
        sb += "	where cd.ELIMINADO = '" + Constantes.NO_ELIMINADO + "'";
        sb += "	    and cd.SI_ADJUNTO is null";
        return ((Integer) em.createNativeQuery(sb).getSingleResult());
        //
    }

    private ContratoVO castContratos(Object[] obj) {
        ContratoVO contratoVO = new ContratoVO();
        contratoVO.setId((Integer) obj[0]);
        contratoVO.setNumero((String) obj[1]);
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
        //
        contratoVO.setListaArchivoConvenio(cvConvenioAdjuntoLocal.traerPorConvenio(contratoVO.getId()));
        return contratoVO;
    }

    private String consultaAdjunto() {
        String sb = "select c.id, c.CODIGO, c.NOMBRE, p.NOMBRE, c.FECHA_FIRMA, c.FECHA_INICIO, c.FECHA_VENCIMIENTO, c.VIGENCIA, ";
        sb += "  c.PORCENTAJE_DEDUCCION, c.MONTO, m.SIGLAS, ct.NOMBRE, cc.NOMBRE, g.NOMBRE, e.NOMBRE, ";
        sb += "  m.id, ct.id, cc.id, g.id, e.id, c.convenio, p.id ";
        sb += "  from CV_CONVENIO_DOCUMENTO cd";
        sb += "		inner join convenio c on cd.convenio = c.ID";
        sb += "		inner join PROVEEDOR p on c.PROVEEDOR = p.ID";
        sb += "		left join MONEDA m on c.MONEDA= m.ID";
        sb += "		inner join CV_TIPO ct on c.CV_TIPO = ct.ID";
        sb += "		left join CV_CLASIFICACION cc on c.CV_CLASIFICACION = cc.id ";
        sb += "		left join GERENCIA g on c.GERENCIA = g.ID";
        sb += "		inner join ESTATUS e on c.ESTATUS = e.ID    ";

        return sb;
    }

    
    public void guardarConvenioRelacionado(String sesion, List<ContratoDocumentoVo> documentos, int idConvenio) {
        for (ContratoDocumentoVo pvDocto : documentos) {
            CvConvenioDocumento cvConvenioDocumento = new CvConvenioDocumento();
            cvConvenioDocumento.setConvenio(new Convenio(idConvenio));
            cvConvenioDocumento.setFechaEntrega(pvDocto.getFechaEntrega());
            cvConvenioDocumento.setPvDocumento(new PvDocumento(pvDocto.getIdDocumento()));
            cvConvenioDocumento.setSiAdjunto(pvDocto.getAdjuntoVO().getId() > 0 ? new SiAdjunto(pvDocto.getAdjuntoVO().getId()) : null);
            cvConvenioDocumento.setInicioVigencia(pvDocto.getInicioVigencia());
            cvConvenioDocumento.setFinVigencia(pvDocto.getFinVigencia());
            cvConvenioDocumento.setValido(pvDocto.isValido());
            cvConvenioDocumento.setGenero(new Usuario(sesion));
            cvConvenioDocumento.setFechaGenero(new Date());
            cvConvenioDocumento.setHoraGenero(new Date());
            cvConvenioDocumento.setEliminado(Constantes.NO_ELIMINADO);
            create(cvConvenioDocumento);
        }
    }

    
    public List<ContratoDocumentoVo> traerDocumentosPorConvenio(int idConvenio) {
        List<ContratoDocumentoVo> lvo = null;
        String sb = consulta()
                + "	where c.ID = " + idConvenio
                + "	and cd.ELIMINADO = false "
                + "order by pd.nombre";
        List<Object[]> lo = em.createNativeQuery(sb).getResultList();

        if (lo != null) {
            lvo = new ArrayList<ContratoDocumentoVo>();
            for (Object[] lo1 : lo) {
                lvo.add(castContratoConvenioVo(lo1));
            }
        }
        return lvo;
    }

}
