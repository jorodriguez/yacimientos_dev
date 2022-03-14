/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.convenio.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.LocalBean;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import sia.constantes.Constantes;
import sia.excepciones.SIAException;
import sia.inventarios.service.ArticuloImpl;
import sia.inventarios.service.InvArticuloCampoImpl;
import sia.modelo.Convenio;
import sia.modelo.CvConvenioArticulo;
import sia.modelo.InvArticulo;
import sia.modelo.Usuario;
import sia.modelo.campo.vo.CampoVo;
import sia.modelo.contrato.vo.ConvenioArticuloVo;
import sia.modelo.sistema.AbstractFacade;
import sia.modelo.vo.inventarios.ArticuloVO;
import sia.util.UtilLog4j;

/**
 *
 * @author mluis
 */
@LocalBean 
public class CvConvenioArticuloImpl extends AbstractFacade<CvConvenioArticulo> {

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public CvConvenioArticuloImpl() {
        super(CvConvenioArticulo.class);
    }
    @Inject
    ArticuloImpl articuloRemote;
    @Inject
    InvArticuloCampoImpl invArticuloCampoRemote;

    
    public void guardar(String sesion, List<ConvenioArticuloVo> listaArticulo, int convenio, int campo) {
        List<CampoVo> lc = null;
        for (ConvenioArticuloVo convenioArticuloVo : listaArticulo) {
            lc = new ArrayList<CampoVo>();
            if (convenioArticuloVo.getIdConvenioArticulo() > 0) {
                modificar(convenioArticuloVo, sesion);
            } else {
                if (convenioArticuloVo.getIdArticulo() == 0) {
                    try {
                        lc.add(new CampoVo(campo));
                        convenioArticuloVo.setDescripcion(convenioArticuloVo.getNombre());
                        ArticuloVO articulo = new ArticuloVO();
                        articulo.setCodigo(articuloRemote.construirCodigo(null));
                        articulo.setNombre(convenioArticuloVo.getNombre());
                        articulo.setDescripcion(convenioArticuloVo.getNombre());
                        articulo.setUnidadId(convenioArticuloVo.getUnidadId());
                        articulo.setCampoId(campo);
                        articulo.setNumParte(Constantes.VACIO);
                        int art = articuloRemote.guardarArticulo(articulo, sesion, lc, null, articulo.getNumParte());
                        convenioArticuloVo.setIdArticulo(art);
                    } catch (SIAException ex) {
                        Logger.getLogger(CvConvenioArticuloImpl.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    try {
                        ArticuloVO ac = articuloRemote.buscar(convenioArticuloVo.getIdArticulo(), campo);
                        if (ac == null) {
                            invArticuloCampoRemote.guardar(sesion, convenioArticuloVo.getIdArticulo(), campo);
                        }
                    } catch (SIAException ex) {
                        Logger.getLogger(CvConvenioArticuloImpl.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                guardarArticulo(convenioArticuloVo, convenio, sesion);
            }
        }
    }

    private void modificar(ConvenioArticuloVo convenioArticuloVo, String sesion) {
        CvConvenioArticulo convenioArticulo = find(convenioArticuloVo.getIdConvenioArticulo());
        //
        convenioArticulo.setCantidad(convenioArticuloVo.getCantidad());
        convenioArticulo.setPrecioUnitario(convenioArticuloVo.getPrecioUnitario());
        convenioArticulo.setImporte((convenioArticuloVo.getCantidad() * convenioArticuloVo.getPrecioUnitario()));
        //
        convenioArticulo.setModifico(new Usuario(sesion));
        convenioArticulo.setFechaModifico(new Date());
        convenioArticulo.setHoraModifico(new Date());
        //
        edit(convenioArticulo);
    }

    private void guardarArticulo(ConvenioArticuloVo articuloVO, int convenio, String sesion) {
        CvConvenioArticulo convenioArticulo = new CvConvenioArticulo();
        //
        try {
            convenioArticulo.setConvenio(new Convenio(convenio));
            convenioArticulo.setInvArticulo(new InvArticulo(articuloVO.getIdArticulo()));
            //
            convenioArticulo.setItem(articuloVO.getItem());
            convenioArticulo.setCantidad(articuloVO.getCantidad());
            convenioArticulo.setPrecioUnitario(articuloVO.getPrecioUnitario());
            convenioArticulo.setImporte((articuloVO.getCantidad() * articuloVO.getPrecioUnitario()));
            convenioArticulo.setAlcance(articuloVO.getAlcance());
            //
            convenioArticulo.setGenero(new Usuario(sesion));
            convenioArticulo.setFechaGenero(new Date());
            convenioArticulo.setHoraGenero(new Date());
            convenioArticulo.setEliminado(Constantes.NO_ELIMINADO);
            //
            create(convenioArticulo);

        } catch (Exception e) {
            UtilLog4j.log.error(e);
        }
    }

    
    public void eliminar(ConvenioArticuloVo articuloVO, String sesion) {
        CvConvenioArticulo convenioArticulo = find(articuloVO.getIdConvenioArticulo());
        //
        convenioArticulo.setModifico(new Usuario(sesion));
        convenioArticulo.setFechaModifico(new Date());
        convenioArticulo.setHoraModifico(new Date());
        convenioArticulo.setEliminado(Constantes.ELIMINADO);
        //
        edit(convenioArticulo);

    }

    
    public List<ConvenioArticuloVo> traerConvenioArticulo(int idConvenio, int campo) {
        String c = consulta()
                + " where ca.CONVENIO = " + idConvenio
                + " and ac.AP_CAMPO = " + campo
                + " and ca.ELIMINADO = 'False'"
                + " GROUP by ca.id, a.CODIGO, a.CODIGO_EAN13, a.CODIGO_INT, a.NOMBRE, a.DESCRIPCION, u.NOMBRE, "
                + " ac.SAT_ARTICULO, ca.CANTIDAD, ca.PRECIO_UNITARIO, ca.IMPORTE,  ca.alcance, u.id, ca.item,"
                + " c.codigo, p.id, p.nombre, c.nombre, m.id, m.nombre, a.id,coalesce(c.id,0), c.codigo, coalesce(cc.id,0), cc.codigo ";
        Query q = em.createNativeQuery(c);
        List<Object[]> lo = q.getResultList();
        List<ConvenioArticuloVo> lca = new ArrayList<ConvenioArticuloVo>();
        for (Object[] objects : lo) {
            lca.add(castConvenioArticulo(objects));
        }
        return lca;
    }

    
    public ConvenioArticuloVo convenioArticulo(int idConvenio, int idArticulo, int campo) {
        try {
            String c = consulta()
                    + " where ca.CONVENIO = " + idConvenio
                    + " and ac.inv_articulo = " + idArticulo
                    + " and ac.AP_CAMPO = " + campo
                    + " and ca.ELIMINADO = 'False'";
            //
            return castConvenioArticulo((Object[]) em.createNativeQuery(c).getSingleResult());
        } catch (Exception e) {
            return null;
        }
    }

    private String consulta() {
        String c = " SELECT DISTINCT ca.id, a.CODIGO, a.CODIGO_EAN13, a.CODIGO_INT, a.NOMBRE, a.DESCRIPCION, u.NOMBRE, "
                + " ac.SAT_ARTICULO, ca.CANTIDAD, ca.PRECIO_UNITARIO, ca.IMPORTE,  ca.alcance, u.id, ca.item,"
                + " c.codigo, p.id, p.nombre, c.nombre, m.id, m.nombre, a.id, coalesce(c.id,0), c.codigo, coalesce(cc.id,0), cc.codigo "
                + " from CV_CONVENIO_ARTICULO ca \n"
                + " inner  join INV_ARTICULO_CAMPO ac on ac.INV_ARTICULO = ca.INV_ARTICULO \n"
                + " inner join convenio c on c.id = ca.convenio \n"                
                + " inner join INV_ARTICULO a on ca.INV_ARTICULO = a.id  \n"
                + " inner join SI_UNIDAD u on a.UNIDAD = u.id  \n"                
                + " inner join proveedor p on c.proveedor = p.id  \n"
                + " left  join moneda m on c.moneda = m.id  \n"
                + " left join convenio cc on cc.convenio = c.id  \n";        

        return c;
    }
    
    private String consultaContratoMarco() {
        String c = " SELECT DISTINCT ca.id, a.CODIGO, a.CODIGO_EAN13, a.CODIGO_INT, a.NOMBRE, a.DESCRIPCION, u.NOMBRE, "
                + " ac.SAT_ARTICULO, ca.CANTIDAD, ca.PRECIO_UNITARIO, ca.IMPORTE,  ca.alcance, u.id, ca.item,"
                + " c.codigo, p.id, p.nombre, c.nombre, m.id, m.nombre, a.id, coalesce(c.id,0), c.codigo, coalesce(c.id,0), c.codigo "
                + " from convenio c \n"                
                + " inner join CV_CONVENIO_ARTICULO ca on ca.convenio = c.id \n"
                + " inner join INV_ARTICULO a on ca.INV_ARTICULO = a.id  \n"
                + " inner join SI_UNIDAD u on a.UNIDAD = u.id  \n"
                + " left  join INV_ARTICULO_CAMPO ac on ac.INV_ARTICULO = a.ID 	 \n"
                + " inner join proveedor p on c.proveedor = p.id  \n"
                + " left  join moneda m on c.moneda = m.id  \n";

        return c;
    }

    private ConvenioArticuloVo castConvenioArticulo(Object[] objects) {
        ConvenioArticuloVo cav = new ConvenioArticuloVo();
        cav.setIdConvenioArticulo((Integer) objects[0]);
        cav.setCodigo((String) objects[1]);
        cav.setCodigoBarras((String) objects[2]);
        cav.setCodigoInt((String) objects[3]);
        cav.setNombre((String) objects[4]);
        cav.setDescripcion((String) objects[5]);
        cav.setUnidadNombre((String) objects[6]);
        cav.setCodigoSat((String) objects[7]);
        cav.setCantidad((Double) objects[8]);
        cav.setPrecioUnitario((Double) objects[9]);
        cav.setImporte((Double) objects[10]);
        cav.setAlcance((String) objects[11]);
        cav.setUnidadId((Integer) objects[12]);
        cav.setItem((String) objects[13]);
        cav.setConvenio((String) objects[14]);
        cav.setIdProveedor((Integer) objects[15]);
        cav.setProveedor((String) objects[16]);
        cav.setNombreConvenio((String) objects[17]);
        cav.setIdMoneda(objects[18] != null ? (Integer) objects[18] : Constantes.CERO);
        cav.setMoneda((String) objects[19]);
        cav.setIdArticulo((Integer) objects[20]);
        cav.setGuardado(Constantes.TRUE);
        cav.setRegistrado(Constantes.TRUE);
        cav.setIdConvenioMarco((Integer) objects[21]);
        cav.setIdConvenio((Integer) objects[23]);        
        return cav;
    }

    
    public List<ConvenioArticuloVo> buscarArticulosEnConvenio(String idsArticulos, int campo) {
        //
        String sb = consulta()
                + " where ca.INV_ARTICULO in (" + idsArticulos + ") \n"
                + " and c.AP_CAMPO = " + campo
                + " and ac.AP_CAMPO = " + campo
                + " and ac.ELIMINADO = 'False' \n"
                + " and c.estatus > " + Constantes.ESTADO_CONVENIO_REGISTRADO
                + " and ca.ELIMINADO = 'False' ";
        Query q = em.createNativeQuery(sb);
        List<Object[]> lo = q.getResultList();
        List<ConvenioArticuloVo> lca = new ArrayList<ConvenioArticuloVo>();
        for (Object[] objects : lo) {
            lca.add(castConvenioArticulo(objects));
        }
        return lca;
    }

    
    public List<ConvenioArticuloVo> traerCodigoConvenioArticulo(String convenio, int campo) {
        String c = consulta()
                + " where c.convenio = (select COALESCE(convenio, id,0) from convenio where codigo = '" + convenio + "')"
                + " and ac.AP_CAMPO = " + campo
                + " and ca.ELIMINADO = 'False'"
                + " union "
                + consultaContratoMarco()
                + " where c.id = (select COALESCE(convenio, id,0) from convenio where codigo = '" + convenio + "')"
                + " and ac.AP_CAMPO = " + campo
                + " and ca.ELIMINADO = 'False'";
        Query q = em.createNativeQuery(c);
        List<Object[]> lo = q.getResultList();
        List<ConvenioArticuloVo> lca = new ArrayList<ConvenioArticuloVo>();
        for (Object[] objects : lo) {
            lca.add(castConvenioArticulo(objects));
        }
        return lca;
    }

    
    public ConvenioArticuloVo codigoConvenioArticulo(String convenio, int idArticulo, int campo) {
        try {
            String c = consulta()
                    + " where c.codigo = '" + convenio + "'"
                    + " and ac.inv_articulo = " + idArticulo
                    + " and ac.AP_CAMPO = " + campo
                    + " and ca.ELIMINADO = 'False'";
            //
            return castConvenioArticulo((Object[]) em.createNativeQuery(c).getSingleResult());
        } catch (Exception e) {
            UtilLog4j.log.error(e);
            return null;
        }
    }

    
    public List<SelectItem> convenioPorArticulo(int idArticulo, int campo) {
        List<SelectItem> listaConArt = null;
        String c = "SELECT ca.CONVENIO, c.CODIGO, c.NOMBRE, a.id, a.NOMBRE, a.DESCRIPCION, ca.CANTIDAD, ca.IMPORTE, m.SIGLAS, m.id,"
                + " ca.id, p.id, p.nombre, c.fecha_vencimiento, ca.precio_unitario"
                + "  from CV_CONVENIO_ARTICULO ca"
                + "	inner join INV_ARTICULO a on ca.INV_ARTICULO = a.id"
                + "	inner join CONVENIO c on ca.CONVENIO = c.id"
                + "     inner join proveedor p on c.proveedor = p.id "
                + "	inner join MONEDA m on c.MONEDA = m.id"
                + " where a.id = " + idArticulo
                + " and c.AP_CAMPO= " + campo
                + " and ca.ELIMINADO = false";
        //+ " and c.ESTATUS = " + Constantes.ESTADO_CONVENIO_ACTIVO;

        List<Object[]> lista = em.createNativeQuery(c).getResultList();
        //
        if (lista != null && lista.size() > 0) {
            listaConArt = new ArrayList<>();
            for (Object[] objects : lista) {
                ConvenioArticuloVo cav = new ConvenioArticuloVo();
                cav.setIdConvenio((Integer) objects[0]);
                cav.setCodigo((String) objects[1]);
                cav.setNombreConvenio((String) objects[2]);
                cav.setIdArticulo((Integer) objects[3]);
                cav.setNombre((String) objects[4]);
                cav.setDescripcion((String) objects[5]);
                cav.setCantidad((Double) objects[6]);
                cav.setImporte((Double) objects[7]);
                cav.setMoneda((String) objects[8]);
                cav.setIdMoneda((Integer) objects[9]);
                cav.setId((Integer) objects[10]);
                cav.setIdProveedor((Integer) objects[11]);
                cav.setProveedor((String) objects[12]);
                cav.setFecha((Date) objects[13]);
                cav.setPrecioUnitario((Double) objects[14]);
                //

//                listaConArt.add(new SelectItem(cav, "Prov.: " +cav.getProveedor() + ", Contrato: " + cav.getCodigo()+ ", Precio: $" + cav.getPrecioUnitario() + ", venc: " + cav.getFecha()));
                listaConArt.add(new SelectItem(cav.getId(), "Prov.: " +cav.getProveedor() + ", Contrato: " + cav.getCodigo()+ ", Precio: $" + cav.getPrecioUnitario() + ", venc: " + cav.getFecha()));
            }
        }
        return listaConArt;
    }
    
    
    public List<ConvenioArticuloVo> convenioPorArticuloVOs(int idArticulo, int campo) {
        List<ConvenioArticuloVo> listaConArt = null;
        String c = "SELECT ca.CONVENIO, c.CODIGO, c.NOMBRE, a.id, a.NOMBRE, a.DESCRIPCION, ca.CANTIDAD, ca.IMPORTE, m.SIGLAS, m.id,"
                + " ca.id, p.id, p.nombre, c.fecha_vencimiento, ca.precio_unitario, coalesce(c.convenio,0)"
                + "  from CV_CONVENIO_ARTICULO ca"
                + "	inner join INV_ARTICULO a on ca.INV_ARTICULO = a.id"
                + "	inner join CONVENIO c on ca.CONVENIO = c.id"
                + "     inner join proveedor p on c.proveedor = p.id "
                + "	inner join MONEDA m on c.MONEDA = m.id"
                + " where a.id = " + idArticulo
                + " and c.AP_CAMPO= " + campo
                + " and ca.ELIMINADO = false";
                //+ " and c.ESTATUS = " + Constantes.ESTADO_CONVENIO_ACTIVO;
        
        List<Object[]> lista = em.createNativeQuery(c).getResultList();
        //
        if (lista != null && lista.size() > 0) {
            listaConArt = new ArrayList<>();
            for (Object[] objects : lista) {
                ConvenioArticuloVo cav = new ConvenioArticuloVo();
                cav.setIdConvenio((Integer) objects[0]);
                cav.setCodigo((String) objects[1]);
                cav.setNombreConvenio((String) objects[2]);
                cav.setIdArticulo((Integer) objects[3]);
                cav.setNombre((String) objects[4]);
                cav.setDescripcion((String) objects[5]);
                cav.setCantidad((Double) objects[6]);
                cav.setImporte((Double) objects[7]);
                cav.setMoneda((String) objects[8]);
                cav.setIdMoneda((Integer) objects[9]);
                cav.setId((Integer) objects[10]);
                cav.setIdProveedor((Integer) objects[11]);
                cav.setProveedor((String) objects[12]);
                cav.setFecha((Date) objects[13]);
                cav.setPrecioUnitario((Double) objects[14]);
                cav.setIdConvenioMarco((Integer) objects[15]);
                cav.setEtiquetaItem("Prov.: " +cav.getProveedor() + ", Contrato: " + cav.getCodigo()+ ", Precio: $" + cav.getPrecioUnitario() + ", venc: " + cav.getFecha());
                //
//                listaConArt.add(new SelectItem(cav, "Prov.: " +cav.getProveedor() + ", Contrato: " + cav.getCodigo()+ ", Precio: $" + cav.getPrecioUnitario() + ", venc: " + cav.getFecha()));
                listaConArt.add(cav);
            }
        }
        return listaConArt;
    }

    
    public ConvenioArticuloVo convenioPorArticuloConvenio(int convenioID, int idArticulo) {
        String c = "SELECT ca.CONVENIO, c.CODIGO, c.NOMBRE, a.id, a.NOMBRE, a.DESCRIPCION, ca.CANTIDAD, ca.IMPORTE, m.SIGLAS, m.id,"
                + " ca.id, ca.precio_unitario, p.id, p.NOMBRE from CV_CONVENIO_ARTICULO ca"
                + "	inner join INV_ARTICULO a on ca.INV_ARTICULO = a.id"
                + "	inner join CONVENIO c on ca.CONVENIO = c.id"
                + "     inner join PROVEEDOR p on c.PROVEEDOR = p.id"
                + "	inner join MONEDA m on c.MONEDA = m.id"
                + " where ca.id = " + convenioID 
                //+ " and a.id = " + idArticulo
                + " and ca.ELIMINADO = false";
        //+ " and c.ESTATUS = " + Constantes.ESTADO_CONVENIO_ACTIVO;
        Object[] objects = (Object[]) em.createNativeQuery(c).getSingleResult();
        //
        ConvenioArticuloVo cav = null;
        if (objects != null) {
            cav = new ConvenioArticuloVo();
            cav.setIdConvenio((Integer) objects[0]);
            cav.setCodigo((String) objects[1]);
            cav.setNombreConvenio((String) objects[2]);
            cav.setIdArticulo((Integer) objects[3]);
            cav.setNombre((String) objects[4]);
            cav.setDescripcion((String) objects[5]);
            cav.setCantidad((Double) objects[6]);
            cav.setImporte((Double) objects[7]);
            cav.setMoneda((String) objects[8]);
            cav.setIdMoneda((Integer) objects[9]);
            cav.setId((Integer) objects[10]);
            cav.setPrecioUnitario((Double) objects[11]);
            cav.setIdProveedor((Integer) objects[12]);
            cav.setProveedor((String) objects[13]);
            //
        }
        return cav;
    }

    
    public List<ConvenioArticuloVo> traerArticulosConvenioAnterior(int convenioMaestro, int convenioActual, int apCampo) {
        String sql = "SELECT id, ap_campo, codigo, nombre, fecha_genero,hora_genero from convenio where convenio is not null \n"
                + "and convenio = ?1 \n"
                + "and eliminado = false\n"
                + "and id < ?2 \n"
                + "order by id desc limit 1";
        //
        List<ConvenioArticuloVo> lista;
        try {
            Object[] obj = (Object[]) em.createNativeQuery(sql).setParameter(1, convenioMaestro).setParameter(2, convenioActual).getSingleResult();
            //
            lista = traerArticulosContratoAnteriorMenosLosActuales((Integer) obj[0], convenioActual);
        } catch (NoResultException e) {
            UtilLog4j.log.info("No encontró el convenio anterior y se usa el maestro");
            lista = traerArticulosContratoAnteriorMenosLosActuales(convenioMaestro, convenioActual);
        }

        return lista;
    }

    private List<ConvenioArticuloVo> traerArticulosContratoAnteriorMenosLosActuales(int convenioAnterior, int convenioActual) {
        String s = "SELECT a.id, a.CODIGO, a.CODIGO_EAN13, a.CODIGO_INT, a.NOMBRE, a.DESCRIPCION, u.id, u.NOMBRE, car.cantidad, car.precio_unitario from cv_convenio_articulo car \n"
                + "                	inner join INV_ARTICULO a on car.INV_ARTICULO = a.id \n"
                + "                	inner join SI_UNIDAD u on a.UNIDAD = u.id \n"
                + "                where\n"
                + "car.inv_articulo in  (\n"
                + "SELECT DISTINCT a.id \n"
                + "from CV_CONVENIO_ARTICULO ca \n"
                + "	inner join INV_ARTICULO a on ca.INV_ARTICULO = a.id \n"
                + " where ca.CONVENIO = ?1 and ca.ELIMINADO = 'False' \n"
                + " GROUP by a.id \n"
                + "EXCEPT\n"
                + "SELECT DISTINCT a.id \n"
                + "from CV_CONVENIO_ARTICULO ca \n"
                + "	inner join INV_ARTICULO a on ca.INV_ARTICULO = a.id \n"
                + "where ca.CONVENIO = ?2  and ca.ELIMINADO = 'False' \n"
                + "GROUP by a.id "
                + ")";
        //
        List<Object[]> objs = em.createNativeQuery(s).setParameter(1, convenioAnterior).setParameter(2, convenioActual).getResultList();
        List<ConvenioArticuloVo> lista = null;
        if (objs != null) {
            lista = new ArrayList<>();
            for (Object[] obj : objs) {
                ConvenioArticuloVo cav = new ConvenioArticuloVo();
                cav.setIdArticulo((Integer) obj[0]);
                cav.setCodigo((String) obj[1]);
                cav.setCodigoBarras((String) obj[2]);
                cav.setCodigoInt((String) obj[3]);
                cav.setNombre((String) obj[4]);
                cav.setDescripcion((String) obj[5]);
                cav.setUnidadId((Integer) obj[6]);
                cav.setUnidadNombre((String) obj[7]);
                cav.setCantidad((Double) obj[8]);
                cav.setPrecioUnitario((Double) obj[9]);                
                cav.setGuardado(Boolean.FALSE);
                cav.setRegistrado(Boolean.TRUE);
                lista.add(cav);
            }
        }
        return lista;
    }
}
