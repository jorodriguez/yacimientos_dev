/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lector.servicios.sistema.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import lector.constantes.Constantes;
import lector.dominio.vo.SiRelRolOpcionVO;
import lector.modelo.SiOpcion;
import lector.modelo.SiRelRolOpcion;
import lector.modelo.SiRol;
import lector.modelo.Usuario;
import lector.servicios.sistema.vo.SiOpcionVo;
import lector.sistema.AbstractImpl;

/**
 *
 * @author nlopez
 */
@Stateless 
public class SiRelRolOpcionImpl extends AbstractImpl<SiRelRolOpcion>{

    //
    @Inject
    private SiOpcionImpl siOpcionRemote;
    @Inject
    private SiRolImpl siRolRemote;
    
    public SiRelRolOpcionImpl() {
	super(SiRelRolOpcion.class);
    }

    

    /**
     * Creo: NLopez
     *
     * @param opcion
     * @param rol
     * @return
     */
    
    public SiRelRolOpcionVO findRolOpcion(Integer opcion, Integer rol) {
	SiRelRolOpcionVO rolOpcionVO = new SiRelRolOpcionVO();

	StringBuilder q = new StringBuilder();
	q.append("SELECT ");
	q.append("r.ID, ");
	q.append("r.SI_ROL, ");
	q.append("r.SI_OPCION ");
	q.append("FROM ");
	q.append("SI_REL_ROL_OPCION r ");
	q.append("WHERE ");
	q.append(" r.SI_OPCION =").append(opcion);
	q.append(" AND r.SI_ROL= ").append(rol);

	List<Object[]> result = em.createNativeQuery(q.toString()).getResultList();

	for (Object[] objects : result) {
	    rolOpcionVO.setId((Integer) objects[0]);
	    rolOpcionVO.setRolId((Integer) objects[1]);
	    rolOpcionVO.setOpcionId((Integer) objects[2]);

	}

	return rolOpcionVO;
    }

    
    public void guardar(SiOpcionVo opcion, Integer rol) {
	SiRelRolOpcionVO relRolOpcion = findRolOpcion(opcion.getId(), rol);	

	if (relRolOpcion.getId() != null) {
	           SiRelRolOpcion rolOpcion = find(relRolOpcion.id);

	    rolOpcion.setEliminado(Constantes.BOOLEAN_FALSE);
	    rolOpcion.setFechaModifico(new Date());

	    edit(rolOpcion);

	} else {
	    SiOpcion o = siOpcionRemote.find(opcion.getId());
	           SiRol r = siRolRemote.find(rol);

	    SiRelRolOpcion rro = new SiRelRolOpcion();
	    rro.setSiRol(r);
	    rro.setSiOpcion(o);
	    rro.setEliminado(Constantes.BOOLEAN_FALSE);
	    rro.setFechaGenero(new Date());
	    rro.setGenero(new Usuario(Constantes.USUARIO_DEFAULT));
	    rro.setModifico(new Usuario(Constantes.USUARIO_DEFAULT));
	    create(rro);

	}
    }

    
    public List<SiOpcionVo> traerOpcionePorRol(int modulo, String roles, String sesion) {
        roles = (roles == null || roles.isEmpty() ? "0" : roles);
	String sb = "select o.id,  o.NOMBRE, o.PAGINA, \n"
		+ "  case when \n"
		+ "	  o.ESTATUS_CONTAR is not null then \n"
		+ "		case when o.ESTATUS_CONTAR = 35 then "
		+ "		    ( select count(se.ID) from SG_SOLICITUD_ESTANCIA se\n"
		+ "                            inner join GERENCIA g on se.GERENCIA = g.ID\n"
		+ "                            inner join AP_CAMPO_GERENCIA cg on cg.GERENCIA = g.ID  and cg.AP_CAMPO = 1\n"
		+ "                        where cg.RESPONSABLE = '" + sesion + "'\n"
		+ "                        and se.ESTATUS = o.ESTATUS_CONTAR\n"
		+ "                        and se.ELIMINADO = 'False')"
		+ "		when o.ESTATUS_CONTAR BETWEEN 415 and 420 then "
		+ "		    (SELECT count(ea.id) from SG_ESTATUS_APROBACION ea \n"
		+ "			    inner join SG_SOLICITUD_VIAJE sv on ea.SG_SOLICITUD_VIAJE = sv.ID \n"
		+ "		    WHERE ea.estatus in (415,420)"
                + "                 and sv.estatus in (415,420) "
		+ "		    and sv.FECHA_SALIDA >= current_date"
		+ "		    AND ea.usuario = '" + sesion + "'"
		+ "		    AND ea.realizado = 'False'"
		+ "		    AND ea.historial = 'False'"
		+ "		    AND ea.eliminado = 'False' )"
                + "            when o.ESTATUS_CONTAR BETWEEN 435 and 438 then "
		+ "		    (SELECT count(ea.id) from SG_ESTATUS_APROBACION ea \n"
		+ "			    inner join SG_SOLICITUD_VIAJE sv on ea.SG_SOLICITUD_VIAJE = sv.ID \n"
		+ "		    WHERE ea.estatus in (435,438)"
                + "                 and sv.estatus in (435,438) "
		+ "		    and sv.FECHA_SALIDA >= current_date"
		+ "		    AND ea.realizado = 'False'"
		+ "		    AND ea.historial = 'False'"
		+ "		    AND ea.eliminado = 'False' )"
                + "            when o.ESTATUS_CONTAR = 400 then \n" +
"				    (SELECT count(ea.id) from SG_ESTATUS_APROBACION ea \n" +
"					    inner join SG_SOLICITUD_VIAJE sv on ea.SG_SOLICITUD_VIAJE = sv.ID \n" +
"				    WHERE ea.estatus = 400\n" +
"                                 and sv.estatus = 400\n" +
"				    and sv.FECHA_SALIDA >= current_date				   \n" +
"				    AND ea.realizado = 'False'\n" +
"				    AND ea.historial = 'False'\n" +
"				    AND ea.eliminado = 'False' )"
		+ "		 end	"
		+ " when  o.ESTATUS_CONTAR is null then  0 end "
		+ ", o.icono from SI_REL_ROL_OPCION ro    "
		+ "	inner join SI_OPCION o on ro.SI_OPCION = o.ID "
		+ "	where o.SI_MODULO = " + modulo
		+ "	and ro.SI_ROL in (" + roles + ")"
		+ "	and o.SI_OPCION  is not null "
		+ "	and o.PAGINA  is not null "
		+ "	and ro.acceso_rapido = 'True' "
		+ "	and o.ELIMINADO = 'False' "
		+ "	group by o.id,  o.NOMBRE, o.PAGINA, o.ESTATUS_CONTAR, o.icono ";
//	System.out.println("asdad" + sb);
	List<Object[]> lo = em.createNativeQuery(sb).getResultList();
	List<SiOpcionVo> lop = new ArrayList<SiOpcionVo>();
	for (Object[] lo1 : lo) {
	    SiOpcionVo o = new SiOpcionVo();
	    o.setId((Integer) lo1[0]);
	    o.setNombre(((String) lo1[1]).replace(" ", "<br>"));
	    o.setPagina((String) lo1[2]);
	    o.setTotal((Long) lo1[3]);
	    o.setIcono((String) lo1[4]);
	    lop.add(o);
	}
	return lop;
    }
}
