/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.sistema.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.constantes.Constantes;
import sia.modelo.SiCategoria;
import sia.modelo.Usuario;
import sia.modelo.sistema.AbstractFacade;
import sia.modelo.sistema.vo.CategoriaVo;

/**
 *
 * @author ihsa
 */
@Stateless 
public class SiCategoriaImpl extends AbstractFacade<SiCategoria>{

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SiCategoriaImpl() {
        super(SiCategoria.class);
    }

    
    public List<CategoriaVo> traerTodasCategoria() {
        String sb = "select c.id, c.nombre, c.codigo, c.descripcion from si_categoria c where c.eliminado = '" + Constantes.NO_ELIMINADO + "'";
        List<Object[]> lobj = em.createNativeQuery(sb).getResultList();
        List<CategoriaVo> listaCad = new ArrayList<CategoriaVo>();
        for (Object[] lobj1 : lobj) {
            CategoriaVo cv = new CategoriaVo();
            cv.setId((Integer) lobj1[0]);
            cv.setNombre((String) lobj1[1]);
            cv.setCodigo((String) lobj1[2]);
            cv.setDescripcion((String) lobj1[3]);
            cv.setSelected(false);
            listaCad.add(cv);
        }
        return listaCad;
    }

    
    public CategoriaVo buscarCategoriaPorId(int idCategoria) {
        String sb = "select c.id, c.nombre, c.codigo, c.descripcion from si_categoria c where c.id = " + idCategoria + " and  c.eliminado = '" + Constantes.NO_ELIMINADO + "'";
        Object[] lobj = (Object[]) em.createNativeQuery(sb).getSingleResult();
        CategoriaVo cv = new CategoriaVo();
        cv.setId((Integer) lobj[0]);
        cv.setNombre((String) lobj[1]);
        cv.setCodigo((String) lobj[2]);
        cv.setDescripcion((String) lobj[3]);
        return cv;
    }

    /**
     *
     * @param sesion
     * @param categoriaVo
     */
    
    public void guardar(String sesion, CategoriaVo categoriaVo) {
        SiCategoria sc = new SiCategoria();
        sc.setNombre(categoriaVo.getNombre());
        sc.setCodigo(categoriaVo.getCodigo());
        sc.setDescripcion(categoriaVo.getDescripcion());
        sc.setGenero(new Usuario(sesion));
        sc.setFechaGenero(new Date());
        sc.setHoraGenero(new Date());
        sc.setEliminado(Constantes.NO_ELIMINADO);
        //
        create(sc);
    }

    
    public void modificar(String sesion, CategoriaVo categoriaVo) {
        SiCategoria sc = find(categoriaVo.getId());
        sc.setNombre(categoriaVo.getNombre());
        sc.setCodigo(categoriaVo.getCodigo());
        sc.setDescripcion(categoriaVo.getDescripcion());
        sc.setModifico(new Usuario(sesion));
        sc.setFechaModifico(new Date());
        sc.setHoraModifico(new Date());
        //
        edit(sc);
    }

    
    public void eliminar(String sesion, CategoriaVo categoriaVo) {
        SiCategoria sc = find(categoriaVo.getId());
        sc.setModifico(new Usuario(sesion));
        sc.setFechaModifico(new Date());
        sc.setHoraModifico(new Date());
        sc.setEliminado(Constantes.ELIMINADO);
        //
        edit(sc);
    }

    
    public List<CategoriaVo> traerCategoriaMenosPrincipales() {
        String sb = "select c.ID, c.NOMBRE, c.CODIGO, c.DESCRIPCION  from si_categoria c";
        sb += " where c.id not in (select  ia.SI_CATEGORIA from INV_ARTICULO ia  ";
        sb += "                    where ia.SI_CATEGORIA is not null";
        sb += "                  )";
        sb += "and c.eliminado = '" + Constantes.NO_ELIMINADO + "'";
        List<Object[]> lobj = em.createNativeQuery(sb).getResultList();
        List<CategoriaVo> listaCad = new ArrayList<CategoriaVo>();
        for (Object[] lobj1 : lobj) {
            CategoriaVo cv = new CategoriaVo();
            cv.setId((Integer) lobj1[0]);
            cv.setNombre((String) lobj1[1]);
            cv.setCodigo((String) lobj1[2]);
            cv.setDescripcion((String) lobj1[3]);
            cv.setSelected(false);
            listaCad.add(cv);
        }
        return listaCad;
    }

    
    public List<CategoriaVo> traerCategoriMenosPrincipalMenosSubcategorias(int idCategoria) {
        String sb = " select c.ID, c.NOMBRE, c.CODIGO, c.DESCRIPCION ";
        sb += "	from si_categoria c  ";
        sb += "	left join ( ";
        sb += "	select a.SI_CATEGORIA_PADRE as xx ";
        sb += "	from SI_REL_CATEGORIA a ";
        sb += "	left join (select SI_CATEGORIA as xx from SI_REL_CATEGORIA where ELIMINADO = '" + Constantes.NO_ELIMINADO + "' group by SI_CATEGORIA) as t1 on t1.xx = a.SI_CATEGORIA_PADRE ";
        sb += "	where a.ELIMINADO = '" + Constantes.NO_ELIMINADO + "'";
        sb += "	and t1.xx is null ";
        sb += "	group by a.SI_CATEGORIA_PADRE ";
        sb += "	union ";
        sb += "	select si_categoria as xx ";
        sb += "	from SI_REL_CATEGORIA  ";
        sb += "	where eliminado = '" + Constantes.NO_ELIMINADO + "'";
        sb += "	and si_categoria_padre = " + idCategoria;
        sb += "	) as NotCats on NotCats.xx = c.id ";
        sb += "	where NotCats.xx is null ";
        sb += "	and c.eliminado = '" + Constantes.NO_ELIMINADO + "'";

        List<Object[]> lobj = em.createNativeQuery(sb).getResultList();
        List<CategoriaVo> listaCad = new ArrayList<CategoriaVo>();
        for (Object[] lobj1 : lobj) {
            CategoriaVo cv = new CategoriaVo();
            cv.setId((Integer) lobj1[0]);
            cv.setNombre((String) lobj1[1]);
            cv.setCodigo((String) lobj1[2]);
            cv.setDescripcion((String) lobj1[3]);
            cv.setSelected(false);
            listaCad.add(cv);
        }
        return listaCad;
    }

    /**
     *
     * @return
     */
    
    public List<CategoriaVo> traerCategoriaPrincipales() {
        String sb = " select ID, NOMBRE, CODIGO ";
        sb += " from SI_CATEGORIA  ";
        sb += " where ELIMINADO = 'False' ";
        sb += " and ID in (select SI_CATEGORIA_PADRE from SI_REL_CATEGORIA where ELIMINADO = 'False' group by SI_CATEGORIA_PADRE) ";
        sb += " and ID not in (select SI_CATEGORIA from SI_REL_CATEGORIA where ELIMINADO = 'False' group by SI_CATEGORIA) ";
        sb += " order by NOMBRE         ";

        //
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

    //TODO:Remover no estoy seguro del servicio
    
    public List<CategoriaVo> traerSubCategorias(int categoriaPadreId) {
        List<Object[]> resultado = em.createNativeQuery("SELECT id, nombre, codigo  FROM SI_CATEGORIA WHERE ID IN (SELECT SI_CATEGORIA FROM SI_REL_CATEGORIA WHERE SI_CATEGORIA_PADRE = ?1) AND ELIMINADO = ?2")
                .setParameter(1, categoriaPadreId)
                .setParameter(2, Constantes.BOOLEAN_FALSE)
                .getResultList();
        return mapearVo(resultado, categoriaPadreId);
    }

    private List<CategoriaVo> mapearVo(List<Object[]> resultado, int  categoriaPadre) {
        List<CategoriaVo> lista = new ArrayList<CategoriaVo>(resultado.size());
        for (Object[] objeto : resultado) {
            CategoriaVo vo = new CategoriaVo();
            vo.setId((Integer) objeto[0]);
            vo.setNombre((String) objeto[1]);
            vo.setCodigo((String) objeto[2]);
            vo.setIdPadre(categoriaPadre);
            lista.add(vo);
        }
        return lista;
    }

}
