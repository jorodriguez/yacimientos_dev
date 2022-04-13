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
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.constantes.Constantes;
import sia.modelo.InvCelda;
import sia.modelo.InvInventario;
import sia.modelo.InvInventarioCelda;
import sia.modelo.Usuario;
import sia.modelo.sistema.AbstractFacade;
import sia.modelo.vo.inventarios.CeldaVo;

/**
 *
 * @author mluis
 */
@Stateless 
public class InvInventarioCeldaImpl extends AbstractFacade<InvInventarioCelda>  {

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public InvInventarioCeldaImpl() {
        super(InvInventarioCelda.class);
    }

    
    public void guardar(int idInventario, int idCelda, String sesion) {
        InvInventarioCelda iic = new InvInventarioCelda();
        iic.setInvInventario(new InvInventario(idInventario));
        iic.setInvCelda(new InvCelda(idCelda));
        iic.setGenero(new Usuario(sesion));
        iic.setFechaGenero(new Date());
        iic.setHoraGenero(new Date());
        iic.setEliminado(Constantes.FALSE);
        //
        create(iic);
    }

    /**
     *
     * @param idInventario
     * @param sesion
     */
    
    public void eliminar(int idInventario, String sesion) {
        List<InvInventarioCelda> lista = ubicaciones(idInventario);
        for (InvInventarioCelda iic : lista) {
            iic.setModifico(new Usuario(sesion));
            iic.setFechaModifico(new Date());
            iic.setHoraModifico(new Date());
            iic.setEliminado(Constantes.TRUE);
            //
            edit(iic);
        }
    }

    
    public List<InvInventarioCelda> ubicaciones(int idInventario) {
        String c = "select * from inv_inventario_celda ic where ic.inv_inventario = ? and ic.eliminado = false";
        return (List<InvInventarioCelda>) em.createNativeQuery(c, InvInventarioCelda.class).setParameter(1, idInventario).getResultList();
    }

    
    public String ubicacion(int idInventario) {
        String c = "SELECT coalesce(string_agg(r.codigo || p.codigo || c.codigo, ','), '') as celda  from inv_inventario_celda ic \n"
                + "	inner join inv_celda c on ic.inv_celda = c.id \n"
                + "	inner join inv_rack r on c.inv_rack = r.id \n"
                + "	inner join inv_piso p on c.inv_piso = p.id \n"
                + "where ic.inv_inventario = ? \n"
                + "and ic.eliminado = false";
        //
        return (String) em.createNativeQuery(c).setParameter(1, idInventario).getSingleResult();
    }

    
    public String ubicacionPorArticulo(int idArticulo) {
        String c = "SELECT coalesce(string_agg(r.codigo || p.codigo || c.codigo, ','), '') as celda from inv_inventario_celda ic \n"
                + "	inner join inv_inventario i on ic.inv_inventario = i.id\n"
                + "	inner join inv_celda c on ic.inv_celda = c.id\n"
                + "	inner join inv_rack r on c.inv_rack = r.id\n"
                + "	inner join inv_piso p on c.inv_piso = p.id\n"
                + " where i.articulo = ? "
                + " AND i.eliminado = false "
                + " and ic.eliminado = false ";
        //
        return (String) em.createNativeQuery(c).setParameter(1, idArticulo).getSingleResult();
    }

    
    public List<CeldaVo> celdas(int idInventario) {
        String c = "SELECT c.id,  r.codigo || p.codigo || c.codigo as celda  from inv_inventario_celda ic \n"
                + "	inner join inv_celda c on ic.inv_celda = c.id \n"
                + "	inner join inv_rack r on c.inv_rack = r.id \n"
                + "	inner join inv_piso p on c.inv_piso = p.id \n"
                + " where ic.inv_inventario = ? \n"
                + " and ic.eliminado = false";
        //
        List<Object[]> lo = em.createNativeQuery(c).setParameter(1, idInventario).getResultList();
        List<CeldaVo> lista = new ArrayList<>();
        for (Object[] objects : lo) {
            lista.add(castCelda(objects));
        }
        return lista;
    }

    private CeldaVo castCelda(Object[] obj) {
        CeldaVo c = new CeldaVo();
        c.setId((Integer) obj[0]);
        c.setCelda((String) obj[1]);
        return c;

    }
}
