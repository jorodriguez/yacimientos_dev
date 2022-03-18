/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.sgl.incidencia.vehiculo.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.constantes.Constantes;
import sia.modelo.SgVehiculo;
import sia.modelo.SiIncidencia;
import sia.modelo.SiIncidenciaVehiculo;
import sia.modelo.Usuario;
import sia.modelo.sgl.vehiculo.vo.VehiculoIncidenciaVo;
import sia.modelo.sistema.AbstractFacade;
import sia.util.UtilLog4j;

/**
 *
 * @author ihsa
 */
@Stateless 
public class SiIncidenciaVehiculoImpl extends AbstractFacade<SiIncidenciaVehiculo> {

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
	return em;
    }

    public SiIncidenciaVehiculoImpl() {
	super(SiIncidenciaVehiculo.class);
    }
    @Inject
    private SiIncidenciaImpl siIncidenciaLocal;

    
    public void guardar(int vehiculo, String titulo, String descripcion, int prioridad, int gerencia,
	    int estado, String palabaClave, String sesion) throws Exception {
	try {
	    int idIncidencia = siIncidenciaLocal.guardar(titulo, descripcion, prioridad, gerencia, estado, palabaClave, sesion, Constantes.CERO);
	    SiIncidenciaVehiculo siIncidenciaVehiculo = new SiIncidenciaVehiculo();
	    siIncidenciaVehiculo.setSiIncidencia(new SiIncidencia(idIncidencia));
	    siIncidenciaVehiculo.setSgVehiculo(new SgVehiculo(vehiculo));
	    siIncidenciaVehiculo.setGenero(new Usuario(sesion));
	    siIncidenciaVehiculo.setFechaGenero(new Date());
	    siIncidenciaVehiculo.setHoraGenero(new Date());
	    siIncidenciaVehiculo.setEliminado(Constantes.NO_ELIMINADO);
	    create(siIncidenciaVehiculo);

	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, "Ocurrio un error al guardar . . . ." + e.getMessage());
	    throw e;
	}
    }

    
    public List<VehiculoIncidenciaVo> traerIncidenciaVehiculo(String sesion, int estado) {
	clearQuery();
	query.append("select mar.nombre, m.nombre, v.numero_placa, i.titulo, i.descripcion, g.nombre, p.nombre, i.fecha_genero, i.id, v.id    from si_Incidencia_vehiculo iv");
	query.append("      inner join sg_vehiculo v on iv.sg_vehiculo = v.id ");
	query.append("      inner join sg_modelo m on v.sg_modelo = m.id ");
	query.append("      inner join sg_marca mar on v.sg_marca = mar.id ");
	query.append("      inner join si_incidencia i on iv.si_incidencia = i.id ");
	query.append("      inner join gerencia g on i.gerencia = g.id ");
	query.append("      inner join prioridad p on i.prioridad = p.id ");
//
	query.append("  where iv.genero = '").append(sesion).append("'");
	query.append("  and i.estado = ").append(estado);
	query.append("  order by i.fecha_genero desc");
//
	List<Object[]> lo = em.createNativeQuery(query.toString()).getResultList();
	List<VehiculoIncidenciaVo> lv = null;
	if (lo != null) {
	    lv = new ArrayList<VehiculoIncidenciaVo>();
	    for (Object[] objects : lo) {
		lv.add(castIncidenciaVehiculo(objects));
	    }
	}
	return lv;
    }

    private VehiculoIncidenciaVo castIncidenciaVehiculo(Object[] obj) {
	VehiculoIncidenciaVo viv = new VehiculoIncidenciaVo();
	viv.getVehiculoVO().setMarca((String) obj[0]);
	viv.getVehiculoVO().setModelo((String) obj[1]);
	viv.getVehiculoVO().setNumeroPlaca((String) obj[2]);
	viv.getIncidenciaVo().setTitulo((String) obj[3]);
	viv.getIncidenciaVo().setDescripcion((String) obj[4]);
	viv.getIncidenciaVo().setGerencia((String) obj[5]);
	viv.getIncidenciaVo().setPrioridad((String) obj[6]);
	viv.getIncidenciaVo().setFechaGenero((Date) obj[7]);
	viv.getIncidenciaVo().setIdIncidencia((Integer) obj[8]);
	viv.getVehiculoVO().setId((Integer) obj[9]);
	return viv;
    }
}
