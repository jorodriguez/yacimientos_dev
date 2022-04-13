/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.inventarios.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.constantes.Constantes;
import sia.modelo.ApCampo;
import sia.modelo.InvArticulo;
import sia.modelo.InvArticuloCampo;
import sia.modelo.Moneda;
import sia.modelo.SatArticulo;
import sia.modelo.Usuario;
import sia.modelo.campo.vo.CampoVo;
import sia.modelo.sistema.AbstractFacade;
import sia.modelo.sistema.vo.CategoriaVo;
import sia.modelo.vo.inventarios.ArticuloVO;
import sia.servicios.sistema.impl.SiCategoriaImpl;
import sia.util.UtilLog4j;

/**
 *
 * @author ihsa
 */
@Stateless 
public class InvArticuloCampoImpl extends AbstractFacade<InvArticuloCampo>  {

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
    @Inject
    SiCategoriaImpl  siCategoriaLocal;

    public InvArticuloCampoImpl() {
        super(InvArticuloCampo.class);
    }

    
    public List<CategoriaVo> traerCategoriaArticulo() {
        String sb = " select ID, NOMBRE, CODIGO ";
        sb += " from SI_CATEGORIA  ";
        sb += " where ELIMINADO = 'False' ";
        sb += " and ID in (select SI_CATEGORIA_PADRE from SI_REL_CATEGORIA where ELIMINADO = 'False' group by SI_CATEGORIA_PADRE) ";
        sb += " and ID not in (select SI_CATEGORIA from SI_REL_CATEGORIA where ELIMINADO = 'False' group by SI_CATEGORIA) ";
        sb += " order by NOMBRE         ";

        List<Object[]> lobj = em.createNativeQuery(sb).getResultList();
        //
        List<CategoriaVo> lista = new ArrayList<CategoriaVo>();
        for (Object[] lobj1 : lobj) {
            CategoriaVo cv = new CategoriaVo();
            cv.setId((Integer) lobj1[0]);
            cv.setNombre((String) lobj1[1]);
            cv.setCodigo((String) lobj1[2]);
            cv.setSelected(false);
            lista.add(cv);
        }
        return lista;
    }

    
    public List<CampoVo> traerCampoPorArticulo(int idArticulo) {
        List<CampoVo> lista = null;
        try {
            String sb = "	select  c.id, c.NOMBRE, i.id, i.SAT_ARTICULO, s.CODIGO, s.DESCRIPCION, ia.SI_PAIS ";
            sb += " from ap_campo c   ";
            sb += " inner join inv_articulo_campo i on c.ID = i.AP_CAMPO and i.eliminado = 'False' and i.inv_articulo = " + idArticulo;
            sb += " left join SAT_ARTICULO s on s.id = i.SAT_ARTICULO and s.ELIMINADO = 'False' ";
            sb += " left join COMPANIA ia on ia.rfc = c.COMPANIA ";
            sb += " where c.eliminado = 'False'   ";
            sb += " order by c.id asc ";
            List<Object[]> lobj = em.createNativeQuery(sb).getResultList();
            //
            lista = new ArrayList<CampoVo>();
            for (Object[] lobj1 : lobj) {
                CampoVo cv = new CampoVo();
                cv.setId((Integer) lobj1[0]);
                cv.setNombre((String) lobj1[1]);
                cv.setIdRelacion((Integer) lobj1[2]);
                cv.setSatArticuloID(lobj1[3] != null ? (Integer) lobj1[3] : 0);
                cv.setSatArticuloCode((String) lobj1[4]);
                cv.setSatArticuloDesc((String) lobj1[5]);
                cv.setIdCompaniaPais(lobj1[6] != null ? (Integer) lobj1[6] : 0);
                lista.add(cv);
            }
        } catch (Exception e) {
            lista = new ArrayList<CampoVo>();
            UtilLog4j.log.error(e);
        }
        return lista;
    }

    /**
     *
     * @param idArticulo
     * @return
     */
    
    public List<CampoVo> traerNoCampoArticulo(int idArticulo) {
        List<CampoVo> lista = null;
        try {
            String sb = " select  c.id, c.NOMBRE  ";
            sb += " from ap_campo c   ";
            sb += " left join inv_articulo_campo i on c.ID = i.AP_CAMPO and i.eliminado = 'False' and i.inv_articulo = " + idArticulo;
            sb += " where c.eliminado = 'False'  ";
            sb += " and i.ID is null ";
            sb += " order by c.id asc ";

            List<Object[]> lobj = em.createNativeQuery(sb).getResultList();
            //
            lista = new ArrayList<CampoVo>();
            for (Object[] lobj1 : lobj) {
                CampoVo cv = new CampoVo();
                cv.setId((Integer) lobj1[0]);
                cv.setNombre((String) lobj1[1]);
                cv.setSelected(false);
                lista.add(cv);
            }
        } catch (Exception e) {
            lista = new ArrayList<CampoVo>();
            UtilLog4j.log.error(e);
        }
        return lista;
    }

    
    public void guardarArticuloCampo(String sesion, int idArticulo, List<CampoVo> ltempCampo) {
        for (CampoVo campo : ltempCampo) {
            guardar(sesion, idArticulo, campo.getId());
        }
    }

    
    public void guardar(String sesion, int idArticulo, int idCampo) {
        InvArticuloCampo invArticuloCampo = new InvArticuloCampo();
        invArticuloCampo.setApCampo(new ApCampo(idCampo));
        invArticuloCampo.setInvArticulo(new InvArticulo(idArticulo));
        invArticuloCampo.setModifico(new Usuario(sesion));
        invArticuloCampo.setFechaModifico(new Date());
        invArticuloCampo.setHoraModifico(new Date());
        invArticuloCampo.setEliminado(Constantes.NO_ELIMINADO);
        //
        create(invArticuloCampo);
    }

    
    public void eliminar(String sesion, int idInvCampo) {
        InvArticuloCampo invArticuloCampo = find(idInvCampo);
        invArticuloCampo.setModifico(new Usuario(sesion));
        invArticuloCampo.setFechaModifico(new Date());
        invArticuloCampo.setHoraModifico(new Date());
        invArticuloCampo.setEliminado(Constantes.ELIMINADO);
        //
        edit(invArticuloCampo);
    }

    
    public void articulos(int campo) {
        String c = "SELECT --(SELECT nombre from  SI_CATEGORIA where id =  SUBSTRING(a.categorias from 1 for 3)),\n"
                + "	a.NOMBRE, \n"
                + "	a.CATEGORIAS from INV_ARTICULO_CAMPO ac	\n"
                + " inner join INV_ARTICULO a on ac.INV_ARTICULO = a.ID and a.eliminado  = 'False'\n"
                + " where ac.AP_CAMPO = " + campo
                + " and ac.eliminado  ='False'";
        List<Object[]> lo = em.createNativeQuery(c).getResultList();
        String art = "";
        for (Object[] objects : lo) {
            String[] cad = ((String) objects[1]).split(",");
            for (String string : cad) {
                CategoriaVo cat = siCategoriaLocal.buscarCategoriaPorId(Integer.parseInt(string));
                art += cat.getNombre() + "||";
            }
            art += (String) objects[0];
            //
            art = "";
        }
    }

    
    public void gardarArticuloSat(int invArtCampoID, int satArticuloID, String usuarioID) {
        InvArticuloCampo ent = this.find(invArtCampoID);
        ent.setModifico(new Usuario(usuarioID));
        ent.setFechaModifico(new Date());
        ent.setHoraModifico(new Date());
        ent.setSatArticulo(new SatArticulo(satArticuloID));

        this.edit(ent);
    }

    
    public void agregarPrecioArticulo(int satArticuloID, int idCampo, double precio, int monedaId, String usuarioID) {
        InvArticuloCampo articuloCampo = buscarPorArticuloCampo(satArticuloID, idCampo);
        if (articuloCampo != null) {
            articuloCampo.setPrecio(precio);
            articuloCampo.setMoneda(new Moneda(monedaId));
            articuloCampo.setModifico(new Usuario(usuarioID));
            articuloCampo.setFechaModifico(new Date());
            articuloCampo.setHoraModifico(new Date());
            //
            edit(articuloCampo);
        }
    }

    
    public InvArticuloCampo buscarPorArticuloCampo(int idArticulo, int idCampo) {
        try {

            String sb = " select  * from inv_articulo_campo ia  \n"
                    + "where ia.inv_articulo  = " + idArticulo + " and  ia.ap_campo = " + idCampo + " and ia.eliminado = false";
            return (InvArticuloCampo) em.createNativeQuery(sb, InvArticuloCampo.class).getSingleResult();
        } catch (Exception e) {
            UtilLog4j.log.error(e);
            return null;
        }
    }

    
    public void guardarArticulo(String sesion, ArticuloVO articuloVO, int idCampo) {
        InvArticuloCampo articuloCampo = new InvArticuloCampo();
        articuloCampo.setApCampo(new ApCampo(idCampo));
        articuloCampo.setInvArticulo(new InvArticulo(articuloVO.getId()));
        articuloCampo.setPrecio(articuloVO.getPrecio());
        articuloCampo.setMoneda(new Moneda(articuloVO.getIdMoneda()));
        articuloCampo.setGenero(new Usuario(sesion));
        articuloCampo.setFechaGenero(new Date());
        articuloCampo.setHoraGenero(new Date());
        articuloCampo.setEliminado(Constantes.BOOLEAN_FALSE);

        //
        create(articuloCampo);
    }
}
