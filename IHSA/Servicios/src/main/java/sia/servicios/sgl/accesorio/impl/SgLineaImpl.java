/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.sgl.accesorio.impl;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.constantes.Constantes;
import sia.modelo.Estatus;
import sia.modelo.SgLinea;
import sia.modelo.Usuario;
import sia.modelo.sgl.accesorio.LineaVo;
import sia.modelo.sistema.AbstractFacade;
import sia.util.UtilLog4j;

/**
 *
 * @author ihsa
 */
@Stateless 
public class SgLineaImpl extends AbstractFacade<SgLinea> {

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
	return em;
    }

    public SgLineaImpl() {
	super(SgLinea.class);
    }

    
    public int guardar(String sesion, LineaVo lineaVo) {
	SgLinea sgLinea = new SgLinea();
	sgLinea.setCuenta(lineaVo.getCuenta());
	sgLinea.setSubcuenta(lineaVo.getSubCuenta());
	sgLinea.setTipoLinea(lineaVo.getTipoLinea());
	sgLinea.setNumero(lineaVo.getNumero());
	sgLinea.setEmei(lineaVo.getEmei());
	sgLinea.setEstatus(new Estatus(lineaVo.getIdEstado()));
	sgLinea.setGenero(new Usuario(sesion));
	sgLinea.setFechaGenero(new Date());
	sgLinea.setHoraGenero(new Date());
	sgLinea.setEliminado(Constantes.NO_ELIMINADO);

	create(sgLinea);

	return sgLinea.getId();
    }

    
    public void modificar(String sesion, LineaVo lineaVo) {
	SgLinea sgLinea = find(lineaVo.getId());
	sgLinea.setCuenta(lineaVo.getCuenta());
	sgLinea.setSubcuenta(lineaVo.getSubCuenta());
	sgLinea.setTipoLinea(lineaVo.getTipoLinea());
	sgLinea.setNumero(lineaVo.getNumero());
	sgLinea.setEmei(lineaVo.getEmei());
	sgLinea.setEstatus(new Estatus(lineaVo.getIdEstado()));
	sgLinea.setGenero(new Usuario(sesion));
	sgLinea.setFechaGenero(new Date());
	sgLinea.setHoraGenero(new Date());
	sgLinea.setEliminado(Constantes.NO_ELIMINADO);
//
	edit(sgLinea);
    }

    
    public String traerLineaJson() {
	Gson gson = null;

	try {
	    gson = new Gson();
	    StringBuilder sb = new StringBuilder();
	    sb.append("select l.id, l.numero from sg_linea l");
	    sb.append(" where l.eliminado ='").append(Constantes.NO_ELIMINADO).append("'");

	    List<Object[]> lista = em.createNativeQuery(sb.toString()).getResultList();
	    JsonArray a = new JsonArray();

	    if (lista != null) {
		for (Object[] o : lista) {
		    JsonObject ob = new JsonObject();
		    ob.addProperty("value", o[0] != null ? o[0].toString() : "-");
		    ob.addProperty("label", o[1] != null ? (String) o[1] : "-");
		    a.add(ob);
		}
	    }
	    return gson.toJson(a);

	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, "Excepcion las linea " + e.getMessage());
	    return null;
	}
    }

    
    public SgLinea buscarPorNumero(String numero) {
	UtilLog4j.log.info(this, "SgModeloImpl.findByName()");
	if (numero != null) {
	    try {
		//Armando query
		String sb = "SELECT l FROM SgLinea l WHERE l.numero = :numero and l.eliminado = 'False'";
		return (SgLinea) em.createQuery(sb).setParameter("numero", numero).getSingleResult();
	    } catch (Exception nre) {
		UtilLog4j.log.fatal(nre);
	    }
	}
	return null;
    }

}
