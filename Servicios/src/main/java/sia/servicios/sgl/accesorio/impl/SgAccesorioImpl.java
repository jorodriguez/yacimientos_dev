package sia.servicios.sgl.accesorio.impl;

/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.LocalBean;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.constantes.Constantes;
import sia.modelo.Proveedor;
import sia.modelo.SgAccesorio;
import sia.modelo.SgLinea;
import sia.modelo.SgMarca;
import sia.modelo.SgModelo;
import sia.modelo.SgOficina;
import sia.modelo.SgTipo;
import sia.modelo.SgTipoEspecifico;
import sia.modelo.SiCondicion;
import sia.modelo.Usuario;
import sia.modelo.sgl.accesorio.AccesorioVo;
import sia.modelo.sistema.AbstractFacade;
import sia.util.UtilLog4j;

/**
 *
 * @author mluis
 */
@LocalBean 
public class SgAccesorioImpl extends AbstractFacade<SgAccesorio> {

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
	return em;
    }

    public SgAccesorioImpl() {
	super(SgAccesorio.class);
    }
    
    
    public List<AccesorioVo> traerAccesorioPorMarca(int idMarca, int oficina) {
	List<AccesorioVo> lista = new ArrayList<AccesorioVo>();
	try {
	    clearQuery();
	    query.append(consulta());
	    query.append("  where a.SG_MARCA = ").append(idMarca);
	    query.append("  and a.SG_OFICINA = ").append(oficina);
	    query.append("  and a.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");
	    List<Object[]> lo = em.createNativeQuery(query.toString()).getResultList();
	    if (lo != null) {
		for (Object[] lo1 : lo) {
		    lista.add(castAccesorio(lo1));
		}
	    }
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, e);
	}
	return lista;
    }

    private AccesorioVo castAccesorio(Object[] obj) {
	AccesorioVo accesorioVo = new AccesorioVo();
	accesorioVo.setId((Integer) obj[0]);
	accesorioVo.setIdProveedor((Integer) obj[1]);
	accesorioVo.setProveedor((String) obj[2]);
	accesorioVo.setIdMarca((Integer) obj[3]);
	accesorioVo.setMarca((String) obj[4]);
	accesorioVo.setIdModelo((Integer) obj[5]);
	accesorioVo.setModelo((String) obj[6]);
	accesorioVo.getOficinaVO().setId((Integer) obj[7]);
	accesorioVo.getOficinaVO().setNombre((String) obj[8]);
	accesorioVo.setIdTipo((Integer) obj[9]);
	accesorioVo.setTipo((String) obj[10]);
	accesorioVo.setIdTipoEspecifico((Integer) obj[11]);
	accesorioVo.setTipoEspecifico((String) obj[12]);
	accesorioVo.setDescripcion((String) obj[13]);
	accesorioVo.setSerie((String) obj[14]);
	accesorioVo.setGarantia(((String) obj[15]).equals(Constantes.BOOLEAN_TRUE));
	accesorioVo.setFechaAdquisicion((Date) obj[16]);
	accesorioVo.setFechaVencimiento((Date) obj[17]);
	accesorioVo.setSistemaOperativo((String) obj[18]);
	accesorioVo.setDisponible(((String) obj[19]).equals(Constantes.BOOLEAN_TRUE));
	//
	accesorioVo.getLineaVo().setId(obj[20] != null ? (Integer) obj[20] : Constantes.CERO);
	accesorioVo.getLineaVo().setCuenta((String) obj[21]);
	accesorioVo.getLineaVo().setSubCuenta((String) obj[22]);
	accesorioVo.getLineaVo().setNumero((String) obj[23]);
	accesorioVo.getLineaVo().setEmei((String) obj[24]);
	accesorioVo.getLineaVo().setTipoLinea((String) obj[25]);
	accesorioVo.getLineaVo().setIdEstado(obj[26] != null ? (Integer) obj[26] : Constantes.CERO);
	accesorioVo.getLineaVo().setEstado((String) obj[27]);
	accesorioVo.setIdCondicion(obj[28] != null ? (Integer) obj[28] : Constantes.CERO);
	accesorioVo.setCondicion((String) obj[29]);
	accesorioVo.setIdAsiganarAccesorio(obj[30] != null ? (Integer) obj[30] : Constantes.CERO);
	accesorioVo.setIdUsuario(obj[31] != null ? (String) obj[31] : Constantes.VACIO);
	accesorioVo.setUsuario((String) obj[32]);
	accesorioVo.setFechaOperacion((Date) obj[33]);
//
	return accesorioVo;

    }

    
    public SgAccesorio guardarAccesorio(String usuario, int idMarca, int idModelo, int sgTipo, int idTipoEspecifico,
	    AccesorioVo accesorioVo, boolean garantia, int idCondicion, int pro,
	    int sgOficina) {
	SgAccesorio sgAccesorio = new SgAccesorio();
	try {

	    sgAccesorio.setProveedor(new Proveedor(pro));
	    sgAccesorio.setSgOficina(new SgOficina(sgOficina));
	    sgAccesorio.setSgMarca(new SgMarca(idMarca));
	    sgAccesorio.setSgModelo(new SgModelo(idModelo));
	    sgAccesorio.setSgTipo(new SgTipo(sgTipo));
	    sgAccesorio.setSgTipoEspecifico(new SgTipoEspecifico(idTipoEspecifico));
	    sgAccesorio.setSiCondicion(new SiCondicion(idCondicion));
	    sgAccesorio.setGarantia(garantia);
	    sgAccesorio.setDescripcion(accesorioVo.getDescripcion());
	    sgAccesorio.setSerie(accesorioVo.getSerie());
	    sgAccesorio.setFechaAdquisicion(accesorioVo.getFechaAdquisicion());
	    sgAccesorio.setFechaVencimiento(accesorioVo.getFechaVencimiento());
	    sgAccesorio.setSistemaOperativo(accesorioVo.getSistemaOperativo());
	    sgAccesorio.setGenero(new Usuario(usuario));
	    sgAccesorio.setHoraGenero(new Date());
	    sgAccesorio.setFechaGenero(new Date());
	    sgAccesorio.setEliminado(Constantes.NO_ELIMINADO);
	    sgAccesorio.setDisponible(Constantes.BOOLEAN_TRUE);
	    create(sgAccesorio);

	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, e);
	    sgAccesorio = null;
	}
	return sgAccesorio;
    }

    
    public void eliminarAccesorio(String usuario, int accesorio) {
	try {
	    SgAccesorio sgAccesorio = find(accesorio);
	    sgAccesorio.setEliminado(Constantes.ELIMINADO);
	    sgAccesorio.setModifico(new Usuario(usuario));
	    sgAccesorio.setFechaModifico(new Date());
	    sgAccesorio.setHoraModifico(new Date());
	    edit(sgAccesorio);
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, e);
	}
    }

    
    public void modificarAccesorio(String usuario, AccesorioVo accesorioVo) {
	try {
	    SgAccesorio sgAccesorio = find(accesorioVo.getId());
	    sgAccesorio.setSgOficina(new SgOficina(accesorioVo.getOficinaVO().getId()));
	    sgAccesorio.setSgMarca(new SgMarca(accesorioVo.getIdMarca()));
	    sgAccesorio.setSgModelo(new SgModelo(accesorioVo.getIdModelo()));
	    sgAccesorio.setSgTipo(new SgTipo(accesorioVo.getIdTipo()));
	    sgAccesorio.setSgTipoEspecifico(new SgTipoEspecifico(accesorioVo.getIdTipoEspecifico()));
	    sgAccesorio.setSiCondicion(new SiCondicion(accesorioVo.getIdCondicion()));
	    sgAccesorio.setGarantia(accesorioVo.isGarantia() ? true : false);
	    sgAccesorio.setDescripcion(accesorioVo.getDescripcion());
	    sgAccesorio.setSerie(accesorioVo.getSerie());
	    sgAccesorio.setFechaAdquisicion(accesorioVo.getFechaAdquisicion());
	    sgAccesorio.setFechaVencimiento(accesorioVo.getFechaVencimiento());
	    sgAccesorio.setSistemaOperativo(accesorioVo.getSistemaOperativo());
	    sgAccesorio.setModifico(new Usuario(usuario));
	    sgAccesorio.setFechaModifico(new Date());
	    sgAccesorio.setHoraModifico(new Date());
	    edit(sgAccesorio);	    
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, e);
	}
    }

    
    public void accesorioDisponible(String usuario, int accesorioVo, boolean disponible) {
	try {	    
	    SgAccesorio sgAccesorio = find(accesorioVo);
	    sgAccesorio.setModifico(new Usuario(usuario));
	    sgAccesorio.setFechaModifico(new Date());
	    sgAccesorio.setHoraModifico(new Date());
	    sgAccesorio.setDisponible(disponible);
	    edit(sgAccesorio);
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, e);
	}
    }

    
    public List<AccesorioVo> traerAccesorioPorTipo(int idTipo, int oficina) {
	List<AccesorioVo> lista = new ArrayList<AccesorioVo>();
	try {
	    clearQuery();
	    query.append(consulta());
	    query.append(" where 1=1");
	    //query.append("  where a.SG_TIPO = ").append(idTipo);
	    if (oficina > 0) {
		query.append("  and a.SG_OFICINA = ").append(oficina);
	    }
	    query.append("  and a.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");
	    query.append("  order by t.nombre, m.nombre asc");
	    List<Object[]> lo = em.createNativeQuery(query.toString()).getResultList();
	    if (lo != null) {
		for (Object[] lo1 : lo) {
		    lista.add(castAccesorio(lo1));
		}
	    }
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, e);
	}
	return lista;
    }

    
    public AccesorioVo buscarPorId(int id) {
	AccesorioVo lista = new AccesorioVo();
	try {
	    clearQuery();
	    query.append(consulta());
	    query.append("  where a.id = ").append(id);/*
	     query.append("  and acc.si_operacion = ").append(Constantes.ID_SI_OPERACION_ASIGNAR);
	     query.append("  and acc.pertenece = ").append(Constantes.CERO);
	     query.append("  and acc.recibido = '").append(Constantes.BOOLEAN_FALSE).append("'");*/

	    query.append("  and a.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");
	    //    System.out.println("Asignado a: " + query.toString());
	    Object[] lo = (Object[]) em.createNativeQuery(query.toString()).getSingleResult();
	    if (lo != null) {
		lista = castAccesorio(lo);
	    }
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, e);
	}
	return lista;
    }

    private String consulta() {
	StringBuilder sb = new StringBuilder();
//
	query.append("select a.ID, p.ID, p.NOMBRE, a.SG_MARCA, m.NOMBRE, a.SG_MODELO, mod.NOMBRE, o.ID, o.NOMBRE, t.ID, t.NOMBRE, te.ID, te.NOMBRE,");
	query.append("  a.DESCRIPCION,a.SERIE, a.GARANTIA, a.FECHA_ADQUISICION, a.FECHA_VENCIMIENTO, a.SISTEMA_OPERATIVO, a.DISPONIBLE, ");
	query.append("  l.ID, l.CUENTA, l.SUBCUENTA, l.NUMERO,l.EMEI, l.TIPO_LINEA, e.id, e.nombre, c.id, c.nombre, acc.id, u.id, u.nombre,acc.FECHA_OPERACION from SG_ACCESORIO a");
	query.append("	inner join PROVEEDOR p on a.PROVEEDOR = p.ID");
	query.append("	inner join SG_TIPO t on a.SG_TIPO = t.ID");
	query.append("	inner join SG_TIPO_ESPECIFICO te on a.TIPO_ESPECIFICO = te.ID");
	query.append("	inner join SG_OFICINA o on a.SG_OFICINA = o.ID");
	query.append("	inner join SG_MARCA m on a.SG_MARCA = m.ID");
	query.append("	inner join si_condicion c on a.si_condicion = c.id");
	query.append("	inner join SG_MODELO mod on a.SG_MODELO = mod.ID");
	query.append("	left join SG_LINEA l on a.sg_linea = l.ID");
	query.append("	left  join ESTATUS e on l.estado  = e.ID");
	query.append("	left  join sg_asignar_accesorio acc on acc.sg_accesorio  = a.ID and acc.terminada = 'False' and acc.pertenece = ").append(Constantes.CERO).append(" and acc.eliminado = 'False'");

	query.append("	left  join usuario u on acc.usuario  = u.ID");
	return sb.toString();
    }

    
    public void agregarLineaAccesorio(String sesion, int accesorio, int idLinea) {
	SgAccesorio sgAccesorio = find(accesorio);
	if (sgAccesorio != null) {
	    sgAccesorio.setSgLinea(new SgLinea(idLinea));
	    sgAccesorio.setModifico(new Usuario(sesion));
	    sgAccesorio.setFechaModifico(new Date());
	    sgAccesorio.setHoraModifico(new Date());
	    //
	    edit(sgAccesorio);
	}
    }
}
