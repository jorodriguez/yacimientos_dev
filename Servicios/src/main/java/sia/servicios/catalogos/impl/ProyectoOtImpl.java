/*
 * ProyectoOtImpl.java
 * Creado el 7/07/2009, 08:47:52 AM
 * EJB sin estado desarrollado por: Héctor Acosta Sierra para: MPG-IHSA
 *
 * Para información sobre el uso de este EJB sin estado (Stateless Session EJB), asi como bugs, actualizaciones o mejoras
 * enviar un mail a: hacosta@mpg-ihsa.com.mx o a: hacosta.0505@gmail.com
 */
package sia.servicios.catalogos.impl;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.LocalBean;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.constantes.Constantes;
import sia.modelo.ApCampo;
import sia.modelo.ProyectoOt;
import sia.modelo.Usuario;
import sia.modelo.proyectoOT.vo.ProyectoOtVo;
import sia.modelo.sgl.vo.OrdenVO;
import sia.modelo.sistema.AbstractFacade;
import sia.servicios.campo.nuevo.impl.ApCampoImpl;
import sia.servicios.orden.impl.OrdenImpl;
import sia.servicios.requisicion.impl.RelGerenciaProyectoImpl;
import sia.util.UtilLog4j;

/**
 *
 * @version 1.0
 */
@LocalBean 
public class ProyectoOtImpl extends AbstractFacade<ProyectoOt>{
    
    @Inject
    private RelGerenciaProyectoImpl relGerenciaProyectoServicioImpl;
    @Inject
    private ApCampoImpl apCampoRemote;
    @Inject
    private OrdenImpl ordenRemote;

    // 
    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;
    
    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
    
    public ProyectoOtImpl() {
        super(ProyectoOt.class);
    }
    
    
    public List<ProyectoOt> getPorCompania(Object rfc) {
        return em.createQuery("select p from ProyectoOt as p WHERE p.compania.rfc = :rfc AND p.visible = :visible ORDER BY p.nombre ASC").setParameter("rfc", rfc).setParameter("visible", true).getResultList();
    }
    
    
    public ProyectoOt buscarPorNombre(Object nombreProyectoOt, Object nombreCompañia) {
        ProyectoOt proyectoOt;
        List<ProyectoOt> proyectosOt = em.createQuery("SELECT p FROM ProyectoOt p WHERE p.nombre = :nombre AND p.compania.nombre = :compania").setParameter("nombre", nombreProyectoOt).setParameter("compania", nombreCompañia).getResultList();
        if (proyectosOt.isEmpty()) {
            proyectoOt = new ProyectoOt();
            UtilLog4j.log.info(this, "Encontrado ");
        } else {
            proyectoOt = proyectosOt.get(0);
            UtilLog4j.log.info(this, "NO Encontrado ");
        }
        return proyectoOt;
    }
    
    
    public List<ProyectoOtVo> getProyectoPorGerencia(int idGerencia, Object nombreCompania, Integer idApCampo) {
        return this.relGerenciaProyectoServicioImpl.getProyectoPorGerencia(idGerencia, nombreCompania, idApCampo);
    }
    
    
    public List<ProyectoOtVo> getListaProyectosOtPorCampo(Integer idApCampo, String compania, String tipo, boolean isYacimiento) {
        UtilLog4j.log.info(this, "getListaProyectosOtPorCampo " + idApCampo);
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(" Select pot.id, pot.CUENTA_CONTABLE, pot.NOMBRE,  pot.ABIERTO,  pot.eliminado, pot.oc_yacimiento, pot.oc_subcampo, y.nombre, s.nombre ")
                    .append("   from PROYECTO_OT pot")
                    .append("   inner join ap_campo c on c.id = pot.AP_CAMPO ")
                    .append("   left join oc_yacimiento y on y.id = pot.oc_yacimiento ")
                    .append("   left join oc_subcampo s on s.id = pot.oc_subcampo ")
                    .append("   WHERE pot.AP_CAMPO = ").append(idApCampo).append(" AND pot.ELIMINADO = 'False'")
                    .append("   and pot.compania = '").append(compania).append("'")
                    .append("   and pot.abierto = 'True' ");
            
            if (tipo != null && !tipo.isEmpty()) {
                sb.append("and c.tipo = '").append(tipo).append("' ");
            }
            
            if (isYacimiento){
                sb.append(" and pot.oc_yacimiento is not null and pot.oc_subcampo is not null ");
            }
            
            sb.append(" ORDER BY pot.fecha_genero desc");
            UtilLog4j.log.info(this, "Q para proy OT: " + sb.toString());
            List<ProyectoOtVo> le = new ArrayList<>();
            List<Object[]> lo = em.createNativeQuery(sb.toString()).getResultList();
            
            for (Object[] objects : lo) {
                ProyectoOtVo pVo = new ProyectoOtVo();
                pVo.setId((Integer) objects[0]);
                pVo.setCuentaContable((String) objects[1]);
                pVo.setNombre((String) objects[2]);
                pVo.setAbierto((Boolean) objects[3]);
                pVo.setEliminado((Boolean) objects[4]);
                pVo.setSelected(Constantes.FALSE);
                pVo.setIdYacimiento(objects[5] != null ? (Integer)objects[5] : 0);
                pVo.setIdSubCampo(objects[6] != null ? (Integer)objects[6] : 0);                      
                pVo.setYacimientoNombre((String) objects[7]);
                pVo.setSubcampoNombre((String) objects[8]);
                le.add(pVo);
            }
            UtilLog4j.log.info(this, "SIZE " + le.size());
            return le;
        } catch (Exception e) {
            UtilLog4j.log.info(this, "Excepcion al traer la lista de proyectos ots - " + e.getMessage());
            return null;
        }
    }
    
    
    public List<ProyectoOtVo> getPorCuentaContable(String cuenta, Integer idApCampo) {
        UtilLog4j.log.info(this, "getPorCuentaContable " + idApCampo);
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("SELECT ID,  NOMBRE,  ELIMINADO,  COMPANIA,   CUENTA_CONTABLE,   ABIERTO  , elinado");
            sb.append(" FROM PROYECTO_OT");
            sb.append(" WHERE CUENTA_CONTABLE = '").append(cuenta).append("' AND AP_CAMPO = ").append(idApCampo).append(" AND ELIMINADO = 'False'");
            sb.append(" ORDER BY ID ASC");
            
            List<ProyectoOtVo> le = new ArrayList<ProyectoOtVo>();
            List<Object[]> lo = em.createNativeQuery(sb.toString()).getResultList();
            
            for (Object[] objects : lo) {
                ProyectoOtVo pVo = new ProyectoOtVo();
                pVo.setId((Integer) objects[0]);
                pVo.setNombre((String) objects[1]);
                pVo.setEliminado((Boolean) objects[2]);
                pVo.setRfcCompania((String) objects[3]);
                pVo.setCuentaContable((String) objects[4]);
                pVo.setAbierto((Boolean) objects[5]);
                le.add(pVo);
            }
            return le;
        } catch (Exception e) {
            UtilLog4j.log.info(this, "Error: al obtener los proyectos OT'S  por cuenta" + e.getMessage());
            return null;
        }
    }
    
    
    public ProyectoOt guardarProyectoOT(String nombreProyectoOT, String cuantaContable, int idApCampo, String idUsuario) {
        ProyectoOt proyectoOt = new ProyectoOt();
        ApCampo apCampo = apCampoRemote.find(idApCampo);
        proyectoOt.setCompania(apCampo.getCompania());
        proyectoOt.setApCampo(apCampo);
        proyectoOt.setNombre(nombreProyectoOT);
        proyectoOt.setCuentaContable(cuantaContable);
        proyectoOt.setAbierto(Constantes.BOOLEAN_TRUE);
        proyectoOt.setVisible(Constantes.BOOLEAN_TRUE);
        proyectoOt.setEliminado(Constantes.BOOLEAN_FALSE);
        proyectoOt.setHoraGenero(new Date());
        proyectoOt.setFechaGenero(new Date());
        proyectoOt.setGenero(new Usuario(idUsuario));
        //
        create(proyectoOt);
        //
        return proyectoOt;
    }
    
    private String getDigitosAño(Date fecha) {
        SimpleDateFormat SDF = new SimpleDateFormat("dd/MM/yyyy");
        String Cadena = SDF.format(fecha);
        String Resultado = "";
        Resultado = Cadena.substring(8, 10);
        UtilLog4j.log.info(this, "Resultado: " + Resultado);
        return Resultado;
    }
    
    
    public ProyectoOt traerProyectoOTPorCuentaContable(String cuentaContable, String compania) {
        try {
            return (ProyectoOt) em.createQuery("SELECT p FROM ProyectoOt p where p.compania.rfc = :rfc AND p.cuentaContable = :cuenta").setParameter("rfc", compania).setParameter("cuenta", cuentaContable).getSingleResult();
        } catch (Exception e) {
            UtilLog4j.log.info(this, "Error: " + e.getMessage());
            return null;
        }
    }
    
    
    public void modificarProyectoOt(int idProyectoOt, String idUsuario) {
        try {
            ProyectoOt proyectoOt = find(idProyectoOt);
            proyectoOt.setModifico(new Usuario(idUsuario));
            proyectoOt.setFechaModifico(new Date());
            proyectoOt.setHoraModifico(new Date());
            this.edit(proyectoOt);
        } catch (Exception e) {
            UtilLog4j.log.info(this, "Excepcion al modificar el proyecto ot " + e.getMessage());
        }
    }
    
    
    public boolean eliminarProyectoOt(List<ProyectoOtVo> lista, String idUsuario) {
        try {
            for (ProyectoOtVo proyectoOtVo : lista) {
                ProyectoOt proyectoOt = find(proyectoOtVo.getId());
                proyectoOt.setAbierto(Constantes.BOOLEAN_TRUE);
                proyectoOt.setEliminado(Constantes.BOOLEAN_TRUE);
                proyectoOt.setModifico(new Usuario(idUsuario));
                proyectoOt.setFechaModifico(new Date());
                proyectoOt.setHoraModifico(new Date());
                //el metodo de modificar ya tiene los parametros necesarios para modificar
                this.modificarProyectoOt(proyectoOt.getId(), idUsuario);
            }
            return true;
        } catch (Exception e) {
            UtilLog4j.log.info(this, "Excepcion al eliminar el proyecto ot " + e.getMessage());
            return false;
        }
    }

    /*
     * Sirve para cerrar un proyecto OT, (No se elimina solo se pone cerrado)
     */
    
    public boolean cerrarProyectoOt(List<ProyectoOtVo> lista, String idUsuario) {
        try {
            for (ProyectoOtVo proyectoOtVo : lista) {
                ProyectoOt proyectoOt = find(proyectoOtVo.getId());
                proyectoOt.setAbierto(Constantes.BOOLEAN_FALSE);
                proyectoOt.setEliminado(Constantes.BOOLEAN_TRUE);
                proyectoOt.setModifico(new Usuario(idUsuario));
                proyectoOt.setFechaModifico(new Date());
                proyectoOt.setHoraModifico(new Date());
                this.edit(proyectoOt);
            }
            return true;
        } catch (Exception e) {
            UtilLog4j.log.info(this, "Excepcion al cerrar el proyecto OT " + e.getMessage());
            return false;
        }
    }
    
    
    public boolean abrirProyectoOt(int idProyectoOt, String idUsuario) {
        try {
            ProyectoOt proyectoOt = find(idProyectoOt);
            proyectoOt.setAbierto(Constantes.BOOLEAN_TRUE);
            proyectoOt.setModifico(new Usuario(idUsuario));
            proyectoOt.setFechaModifico(new Date());
            proyectoOt.setHoraModifico(new Date());
            this.edit(proyectoOt);
            return true;
        } catch (Exception e) {
            UtilLog4j.log.info(this, "Excepcion al Abrir el proyecto OT " + e.getMessage());
            return false;
        }
    }
    
    
    public boolean buscarExistenciaRelacionConGerenciaYTipoObra(Integer idProyectoOt) {
        UtilLog4j.log.info(this, "buscarExistenciaRelacionConGerenciaYTipoObra " + idProyectoOt);
        String q = "";
        try {
            q = "SELECT count(*)"
                    + " FROM REL_PROYECTO_TIPO_OBRA pto"
                    + " where pto.PROYECTO_OT = " + idProyectoOt + " AND pto.ELIMINADO='" + Constantes.BOOLEAN_FALSE + "'";
            Integer countPT = (Integer) em.createNativeQuery(q).getSingleResult();
            if (countPT > 0) {
                return true;
            } else {
                //buscar en REL_GERENCIA_PROYECTO
                q = "";
                q += "SELECT count(*)"
                        + " FROM REL_GERENCIA_PROYECTO gp "
                        + " where gp.PROYECTO_OT = " + idProyectoOt + " AND gp.ELIMINADO='" + Constantes.BOOLEAN_FALSE + "'";;
                Integer countGP = (Integer) em.createNativeQuery(q).getSingleResult();
                if (countGP > 0) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            UtilLog4j.log.info(this, "Error: al obtener los proyectos OT'S  por cuenta" + e.getMessage());
            return false;
        }
    }
    
    
    public List<OrdenVO> getDetalleProyectoOt(Integer idProyectoOt) {
        return ordenRemote.traerOrdenDeProyectoOt(idProyectoOt);
    }

    /**
     * @param rfcCompania
     * @return
     * @author: icristobal Recupera un String con la lista de proveedores en
     * formato Gson
     */
    
    public String traerProyectoOTJson(String rfcCompania) {
        Gson gson = null;
        try {
            gson = new Gson();
            List<Object[]> lista = null;
            StringBuilder sb = new StringBuilder();
            
            sb.append("select p.id, p.nombre")
                    .append(" from Proyecto_ot p ")
                    .append(" where p.compania = '").append(rfcCompania).append("'")
                    .append("   and pot.abierto = 'True' ")
                    .append(" and p.eliminado ='False' order by p.nombre asc");
            lista = em.createNativeQuery(sb.toString()).getResultList();
            JsonArray a = new JsonArray();
            
            for (Object[] o : lista) {
                if (o != null) {
                    JsonObject ob = new JsonObject();
                    ob.addProperty("value", o[0] != null ? o[0].toString() : "-");
                    ob.addProperty("label", o[1] != null ? (String) o[1] : "-");
                    a.add(ob);
                }
            }
            return gson.toJson(a);
            
        } catch (Exception e) {
            UtilLog4j.log.info(this, "Excepcion " + e.getMessage());
            return null;
        }
    }
    
    
    public List<ProyectoOtVo> traerProyectoOTPorCadena(String cadena, int idCampo) {
        UtilLog4j.log.info(this, "getListaProyectosOtPorCampo " + idCampo);
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(" Select pot.id, pot.CUENTA_CONTABLE, pot.NOMBRE,  pot.ABIERTO,  pot.eliminado ")
                    .append(" from PROYECTO_OT pot")
                    .append(" WHERE pot.AP_CAMPO = ").append(idCampo).append(" and upper(pot.nombre) like upper('% ").append(cadena).append("%')")
                    .append("   and pot.abierto = 'True' ")
                    .append(" AND pot.ELIMINADO = 'False' ORDER BY pot.nombre ASC");
            UtilLog4j.log.info(this, "Q para proy OT: " + sb.toString());
            List<ProyectoOtVo> le = new ArrayList<ProyectoOtVo>();
            List<Object[]> lo = em.createNativeQuery(sb.toString()).getResultList();
            
            for (Object[] objects : lo) {
                ProyectoOtVo pVo = new ProyectoOtVo();
                pVo.setId((Integer) objects[0]);
                pVo.setCuentaContable((String) objects[1]);
                pVo.setNombre((String) objects[2]);
                pVo.setAbierto((Boolean) objects[3]);
                pVo.setEliminado((Boolean) objects[4]);
                le.add(pVo);
            }
            UtilLog4j.log.info(this, "SIZE " + le.size());
            return le;
        } catch (Exception e) {
            UtilLog4j.log.info(this, "Excepcion al traer la lista de proyectos ots - " + e.getMessage());
            return null;
        }
    }
    
    
    public ProyectoOt traerProyectoOTPorCuentaContableCampo(String cuentaContable, int campo) {
        try {
            return (ProyectoOt) em.createQuery("SELECT p FROM ProyectoOt p where p.cuentaContable = :cuenta AND p.apCampo.id = :campo", ProyectoOt.class)
                    .setParameter("cuenta", cuentaContable)
                    .setParameter("campo", campo).getSingleResult();
        } catch (Exception e) {
            UtilLog4j.log.info(this, "Error: " + e.getMessage());
            return null;
        }
    }
    
    
    public void activarProyectoOt(ProyectoOt proyectoOt, String sesion) {
        proyectoOt.setAbierto(Constantes.BOOLEAN_TRUE);
        proyectoOt.setVisible(Constantes.BOOLEAN_TRUE);
        proyectoOt.setModifico(new Usuario(sesion));
        proyectoOt.setHoraModifico(new Date());
        proyectoOt.setFechaModifico(new Date());
        proyectoOt.setEliminado(Constantes.BOOLEAN_FALSE);
        //
        edit(proyectoOt);
        //
    }
    
    
    public ProyectoOtVo getProyectoOtVO(Integer idProy) {
        UtilLog4j.log.info(this, "getProyectoOtVO " + idProy);
        ProyectoOtVo pVo = null;
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(" Select pot.id, pot.CUENTA_CONTABLE, pot.NOMBRE,  pot.ABIERTO,  pot.eliminado, pot.oc_yacimiento, pot.oc_subcampo ")
                    .append("   from PROYECTO_OT pot")
                    .append("   WHERE pot.id = ").append(idProy)
                    .append(" ORDER BY pot.fecha_genero desc");
            UtilLog4j.log.info(this, "Q para proy OT: " + sb.toString());
            
            List<Object[]> lo = em.createNativeQuery(sb.toString()).getResultList();
            
            if (lo != null && lo.size() > 0) {
                Object[] objects = lo.get(0);
                pVo = new ProyectoOtVo();
                pVo.setId((Integer) objects[0]);
                pVo.setCuentaContable((String) objects[1]);
                pVo.setNombre((String) objects[2]);
                pVo.setAbierto((Boolean) objects[3]);
                pVo.setEliminado((Boolean) objects[4]);                
                pVo.setIdYacimiento(objects[5] != null ? (Integer) objects[5] : 0);
                pVo.setIdSubCampo(objects[6] != null ? (Integer) objects[6] : 0);
            }
        } catch (Exception e) {
            UtilLog4j.log.info(this, "Excepcion al traer la lista de proyectos ots - " + e.getMessage());
            pVo = null;
        }
        return pVo;
    }
    
    
    public int validarOtExiste(String cuentaContable, String nombre, int apCampo) {
        UtilLog4j.log.info(this, "#validarActividadExiste ");
        int existe = 0;
        try {
            String query = "select ID, cuenta_contable, NOMBRE from proyecto_ot where ELIMINADO = false "
                    + " and cuenta_contable = '" + cuentaContable + "' "
                    + " and upper(nombre) = upper('" + nombre + "') "
                    + " and ap_campo = " + apCampo + " limit 1";
            
            UtilLog4j.log.info(this, "query" + query);
            
            Object[] lo = (Object[]) em.createNativeQuery(query).getSingleResult();
            
            if (lo != null) {
                existe = (Integer)lo[0];
            }
        } catch (Exception e) {
            UtilLog4j.log.error(this, "Error: al validar la existencia de un codigo de subtarea " + e.getMessage(), e);
            existe = 0;
        }
        return existe;
    }
}
