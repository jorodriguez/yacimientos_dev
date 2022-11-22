/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.inventarios.service;

import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.modelo.InvCelda;
import sia.modelo.InvRack;
import sia.modelo.sistema.AbstractFacade;
import sia.modelo.vo.inventarios.CeldaVo;

/**
 *
 * @author mluis
 */
@Stateless 
public class InvCeldaImpl extends AbstractFacade<InvCelda> {

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public InvCeldaImpl() {
        super(InvCelda.class);
    }

    
    public CeldaVo ubicacion(int id) {
        String c = " select c.id, r.codigo || p.codigo || c.codigo from inv_celda c "
                + "     inner join inv_rack r on c.inv_rack = r.id "
                + "     inner join inv_piso p on c.inv_piso = p.id "
                + "  where c.id = ? and c.eliminado = false ";
        Object[] o = (Object[]) em.createNativeQuery(c).setParameter(1, id).getSingleResult();
        //
        return cast(o);
    }

    
    public List<InvRack> racksPorAlmacen(int idAlmacen) {
        String c = " select DISTINCT r.* from inv_celda c \n"
                + "             inner join inv_rack r on c.inv_rack = r.id \n"
                + "         where c.inv_almacen = ? and c.eliminado = false "
                + " order by r.id	 ";
        //
        return em.createNativeQuery(c, InvRack.class).setParameter(1, idAlmacen).getResultList();
    }

    
    public List<CeldaVo> pisoPorRack(int idRack) {
        String c = " select DISTINCT p.id, p.codigo from inv_celda c \n"
                + "             inner join inv_piso p on c.inv_piso = p.id \n"
                + "         where c.inv_rack = ? and c.eliminado = false "
                + " order by p.codigo	 ";
        //
        List<Object[]> o = em.createNativeQuery(c).setParameter(1, idRack).getResultList();
        //
        List<CeldaVo> lista = new ArrayList<>();
        for (Object[] objects : o) {
            lista.add(cast(objects));
        }
        return lista;
    }

    
    public List<CeldaVo> celdaPorRackPiso(int idRack, int idPiso) {
        String c = " select DISTINCT c.id, c.codigo from inv_celda c \n"
                + "  where c.inv_rack = ? and c.inv_piso = ? and c.eliminado = false 	"
                + "  order by c.id ";
        //
        List<Object[]> o = em.createNativeQuery(c)
                .setParameter(1, idRack)
                .setParameter(2, idPiso).getResultList();
        //
        List<CeldaVo> lista = new ArrayList<>();
        for (Object[] objects : o) {
            lista.add(cast(objects));
        }
        return lista;
    }

    private CeldaVo cast(Object[] obj) {
        CeldaVo c = new CeldaVo();
        c.setId((Integer) obj[0]);
        c.setCelda((String) obj[1]);
        //
        return c;
    }

    
    public List<CeldaVo> celdasPorAlmacen(String almacen) {
        String c = " select c.id, r.codigo || '-' || p.codigo || '-' || c.codigo as celda  \n"
                + " from inv_celda c \n"
                + "	inner join inv_rack r on c.inv_rack = r.id \n"
                + "	inner join inv_piso p on c.inv_piso = p.id 	\n"
                + "	inner join inv_almacen ia  on c.inv_almacen  = ia.id \n"
                + " where ia.nombre  = '" + almacen + "' \n"
                + " and c.eliminado = false";

        List<Object[]> o = em.createNativeQuery(c).getResultList();
        //
        List<CeldaVo> lista = new ArrayList<>();
        for (Object[] objects : o) {
            lista.add(cast(objects));
        }
        return lista;
    }

}
