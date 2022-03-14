

package sia.servicios.oficio.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.LocalBean;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import sia.constantes.Constantes;
import sia.modelo.OfOficio;
import sia.modelo.OfOficioUsuario;
import sia.modelo.Usuario;
import sia.modelo.sistema.AbstractFacade;
import sia.util.UtilLog4j;

/**
 *
 * @author esapien
 */
@LocalBean 
public class OfOficioUsuarioImpl extends AbstractFacade<OfOficioUsuario> {
    
    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;
    
    private final String queryBase;

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    /**
     * 
     */
    public OfOficioUsuarioImpl() {
        super(OfOficioUsuario.class);
        
        StringBuilder sb = new StringBuilder();
        
        sb.append("SELECT ");
        sb.append(" ID, ");
        sb.append(" OF_OFICIO, ");
        sb.append(" USUARIO, ");
        sb.append(" GENERO, ");
        sb.append(" FECHA_GENERO, ");
        sb.append(" HORA_GENERO, ");
        sb.append(" MODIFICO, ");
        sb.append(" FECHA_MODIFICO, ");
        sb.append(" HORA_MODIFICO, ");
        sb.append(" ELIMINADO ");
        sb.append("FROM OF_OFICIO_USUARIO ");
        sb.append("WHERE 1=1 ");
        
        queryBase = sb.toString();
        
    }
    
    /**
     * 
     * @param oficioId
     * @return 
     */
    
    public List<String> buscarUsuariosOficioRestringidoIds(int oficioId) {
        
        List<OfOficioUsuario> oficioUsuarios = buscarOfOficioUsuariosActivos(oficioId);
        
        List<String> resultado = new ArrayList();
        
        for (OfOficioUsuario oficioUsuario : oficioUsuarios) {
            
            resultado.add(oficioUsuario.getUsuario().getId());
            
        }
        
        return resultado;
        
    }
    
    
    
    /**
     * Realiza el borrado lógico de los registros de usuarios con acceso
     * al oficio restringido.
     * 
     * @param oficioId
     * @param usuarioId
     * @return 
     */
    
    public int borrarLogicoUsuariosOficioRestringido(Integer oficioId, String usuarioId) {
        
        List<OfOficioUsuario> oficioUsuarios = buscarOfOficioUsuariosActivos(oficioId);
        
        int cantidad = oficioUsuarios.size();
        
        // actualizar a eliminado
        Date fechaModifico = new Date();
        
        for (OfOficioUsuario entidad : oficioUsuarios) {
            
            entidad.setModifico(new Usuario(usuarioId));
            entidad.setFechaModifico(fechaModifico);
            entidad.setHoraModifico(fechaModifico);
            entidad.setEliminado(Constantes.BOOLEAN_TRUE);
            
            this.edit(entidad);
        }
        
        return cantidad;
    }
    
    
    
    
    /**
     * 
     * @param oficioId
     * @return 
     */
    private List<OfOficioUsuario> buscarOfOficioUsuariosActivos(Integer oficioId) {
        
        StringBuilder sb = new StringBuilder();
        
        sb.append(queryBase);

        sb.append("AND OF_OFICIO = ? ");
        sb.append("AND ELIMINADO = 'False' ");
        
        String sql = sb.toString();
        
        getLogger().info(this, "sql = " + sql + ", oficioId = " + oficioId);
        
        Query q = em.createNativeQuery(sql);
        
        q.setParameter(1, oficioId);
        
        List<Object[]> resultadoSql = q.getResultList();
        
        List<OfOficioUsuario> resultado = new ArrayList<OfOficioUsuario>();
        
        for (Object[] obj : resultadoSql) {
            
            OfOficioUsuario entidad = castToEntity(obj);
            
            resultado.add(entidad);
        }
        
        return resultado;
    }
    
    
    /**
     * 
     * @param obj
     * @return 
     */
    private OfOficioUsuario castToEntity(Object[] obj) {
        
        OfOficioUsuario entidad = new OfOficioUsuario();
        
        int i = 0;
        
        entidad.setId((Integer)obj[i++]);
        entidad.setOfOficio(new OfOficio((Integer)obj[i++]));
        entidad.setUsuario(new Usuario((String)obj[i++]));
        
        entidad.setGenero(new Usuario((String)obj[i++]));
        entidad.setFechaGenero((Date)obj[i++]);
        entidad.setHoraGenero((Date)obj[i++]);
        
        i++;
        Usuario usuario = obj[i] == null ? null : new Usuario((String)obj[i]);
        i++;
        Date fechaModifico = obj[i] == null ? null : (Date)obj[i];
        
        entidad.setModifico(usuario);
        entidad.setFechaModifico(fechaModifico);
        entidad.setHoraModifico(fechaModifico);
        
        entidad.setEliminado((Boolean)obj[i++]);
        
        return entidad;
        
        
    }
    
    
    /**
     * 
     * @return 
     */
    private UtilLog4j getLogger() {
        return UtilLog4j.log;
    }


    
    
}
