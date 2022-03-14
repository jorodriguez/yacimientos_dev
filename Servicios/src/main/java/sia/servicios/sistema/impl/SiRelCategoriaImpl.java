/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.sistema.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.LocalBean;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.constantes.Constantes;
import sia.modelo.SiCategoria;
import sia.modelo.SiRelCategoria;
import sia.modelo.Usuario;
import sia.modelo.sistema.AbstractFacade;
import sia.modelo.sistema.vo.CategoriaVo;

/**
 *
 * @author ihsa
 */
@LocalBean 
public class SiRelCategoriaImpl extends AbstractFacade<SiRelCategoria>{

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
	return em;
    }

    public SiRelCategoriaImpl() {
	super(SiRelCategoria.class);
    }
    @Inject
    private SiCategoriaImpl siCategoriaLocal;

    
    public CategoriaVo traerCategoriaPorCategoria(int idCategoria, String categoriasSeleccionadas, int apCampoID) {
        CategoriaVo categoria = siCategoriaLocal.buscarCategoriaPorId(idCategoria);
        String sb = "";        
        if (categoriasSeleccionadas == null || categoriasSeleccionadas.isEmpty()) {
            sb = " select c.id, c.nombre, c.codigo, c.descripcion, rc.id  ";
            sb += " from si_rel_categoria rc 		     ";
            sb += " inner join si_categoria c on rc.si_categoria = c.id and c.ELIMINADO = 'False'  ";
            sb += " where rc.si_categoria_padre = " + idCategoria;
            sb += " and rc.eliminado = 'False'  ";
            sb += " group by c.id, c.nombre, c.codigo, c.descripcion, rc.id   ";
            sb += " order by c.nombre  ";
        } else {
            String categoriasSeleccionadasAux = categoriasSeleccionadas + "," + idCategoria + ",";
            sb = " SELECT c.ID, c.NOMBRE, c.CODIGO, c.DESCRIPCION, T1.SI_CATEGORIA ";
            sb += " FROM (	select rc.SI_CATEGORIA ";
            sb += "  		from SI_REL_CATEGORIA rc 		        ";
            sb += "  		where rc.SI_CATEGORIA_PADRE = " + idCategoria;
            sb += "  		and rc.ELIMINADO = 'False') AS T1 ";
            sb += " JOIN (select  ";            
            sb += " case when (CHAR_LENGTH (SUBSTRING(a.CATEGORIAS FROM (position (',"+idCategoria+",' in a.CATEGORIAS))))-"+String.valueOf(","+idCategoria+",").length()+") > 1 THEN ";
            sb += "   case when (SUBSTRING(a.CATEGORIAS FROM "+(categoriasSeleccionadasAux.length()+1)+" FOR ((CHAR_LENGTH (a.CATEGORIAS))-(position (',"+idCategoria+",' in a.CATEGORIAS)+CHAR_LENGTH (',"+idCategoria+",'))))  SIMILAR TO '%,%')  ";
            sb += "   then ";
   	    sb += "      CAST(SUBSTRING(a.CATEGORIAS FROM "+(categoriasSeleccionadasAux.length()+1)+" FOR position (',' in SUBSTRING(a.CATEGORIAS FROM "+(categoriasSeleccionadasAux.length()+1)+" FOR ((CHAR_LENGTH (a.CATEGORIAS))-(position (',"+idCategoria+",' in a.CATEGORIAS)+CHAR_LENGTH (',"+idCategoria+",')))))-1) AS INTEGER) ";
   	    sb += "   else ";
	    sb += "  	 CAST(SUBSTRING (a.CATEGORIAS FROM "+(categoriasSeleccionadasAux.length()+1)+" FOR ((CHAR_LENGTH (a.CATEGORIAS))-(position (',"+idCategoria+",' in a.CATEGORIAS)+CHAR_LENGTH (',"+idCategoria+",')))) AS INTEGER)  ";
            sb += "   end ";
	    sb += " else  0 ";
            //sb += " 	CAST(SUBSTRING (a.CATEGORIAS FROM "+(categoriasSeleccionadasAux.length() - String.valueOf(idCategoria).length())+" FOR ((CHAR_LENGTH (a.CATEGORIAS))-(position (',"+idCategoria+",' in a.CATEGORIAS)+CHAR_LENGTH (',"+idCategoria+",')))) AS INTEGER)  ";
	    sb += " end	as SI_CATEGORIATXT   	  ";            
            sb += "  	 from INV_ARTICULO a     		 ";
            sb += "  	 inner join INV_ARTICULO_CAMPO cc on cc.INV_ARTICULO = a.ID and cc.AP_CAMPO = " + apCampoID;
            sb += "  	 where a.ELIMINADO = 'False'     		 ";
            sb += "  	 and SUBSTRING (a.CATEGORIAS FROM 1 FOR ("+categoriasSeleccionadasAux.length()+")) = '"+categoriasSeleccionadasAux+"' ) AS T2 ON T1.SI_CATEGORIA = T2.SI_CATEGORIATXT ";            
            sb += " inner join SI_CATEGORIA c on T1.SI_CATEGORIA = c.ID and c.ELIMINADO = 'False'   ";
            sb += " group by c.ID, c.NOMBRE, c.CODIGO, c.DESCRIPCION, T1.SI_CATEGORIA ";
        }

        List<Object[]> lobj = em.createNativeQuery(sb).getResultList();
	// llenamos el obj categoria	
        // buscamos la lista de categorias hijas
        List<CategoriaVo> listaCat = new ArrayList<CategoriaVo>();
        for (Object[] lobj1 : lobj) {
            CategoriaVo cv = new CategoriaVo();
            cv.setId((Integer) lobj1[0]);
            cv.setNombre((String) lobj1[1]);
            cv.setCodigo((String) lobj1[2]);
            cv.setDescripcion((String) lobj1[3]);
            cv.setIdPadre((Integer) lobj1[4]);
            listaCat.add(cv);
        }
        categoria.setListaCategoria(listaCat);
        return categoria;
    }
    
    
    public boolean tieneProductos(String categoriasSeleccionadas, int apCampoID) {        
	String sb = " select count(a.id) ";
        sb += " from INV_ARTICULO a  ";
        sb += " inner join INV_ARTICULO_CAMPO c on c.INV_ARTICULO = a.id and c.AP_CAMPO = "+apCampoID;
        sb += " where a.ELIMINADO = 'False'  ";
        sb += " and SUBSTRING (a.CATEGORIAS FROM 1 FOR "+categoriasSeleccionadas.length()+") = '"+categoriasSeleccionadas+"' ";
//              and SUBSTRING (a.CATEGORIAS FROM 1 FOR 12) = '138,139,8459'
        return ((Integer) em.createNativeQuery(sb).getSingleResult()).intValue() > 0;
    }

    
    public void guardar(String sesion, List<CategoriaVo> listaCat, int idCatPadre) {
	//
	for (CategoriaVo listaCat1 : listaCat) {
	    // valida si existe la relacion
	    guardar(sesion, listaCat1, idCatPadre);
	}
    }

    private void guardar(String sesion, CategoriaVo categoriaVo, int idCatPadre) {

	SiRelCategoria siRelCategoria = new SiRelCategoria();
	siRelCategoria.setSiCategoriaPadre(new SiCategoria(idCatPadre));
	siRelCategoria.setSiCategoria(new SiCategoria(categoriaVo.getId()));
	siRelCategoria.setGenero(new Usuario(sesion));
	siRelCategoria.setFechaGenero(new Date());
	siRelCategoria.setHoraGenero(new Date());
	siRelCategoria.setEliminado(Constantes.NO_ELIMINADO);
	//
	create(siRelCategoria);
    }

    
    public void eliminarRelacion(String sesion, int idCatePadre) {
	SiRelCategoria siRelCategoria = find(idCatePadre);
	siRelCategoria.setModifico(new Usuario(sesion));
	siRelCategoria.setFechaModifico(new Date());
	siRelCategoria.setHoraModifico(new Date());
	siRelCategoria.setEliminado(Constantes.ELIMINADO);
	//
	edit(siRelCategoria);
    }
}
