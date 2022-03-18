

package sia.servicios.sistema.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import sia.modelo.SiPermiso;
import sia.modelo.permiso.vo.PermisoVo;
import sia.modelo.rol.vo.RolVO;
import sia.modelo.sistema.AbstractFacade;

/**
 * EJB para la entidad SI_PERMISO.
 *
 * @author esapien
 */
@Stateless 
public class SiPermisoImpl extends AbstractFacade<SiPermiso> {
    
    private final static Logger logger = Logger.getLogger(SiPermisoImpl.class.getName());
    
    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;
    
    private final String queryBase;
    
    
    /**
     * Constructor
     * 
     */
    public SiPermisoImpl() {
        
        super(SiPermiso.class);
        
        StringBuilder sb = new StringBuilder();
        
        sb.append("select ");
        sb.append("usu_rol.USUARIO as usuario, ");
        sb.append("mod.id as modulo_id, ");
        sb.append("mod.nombre as modulo,  ");
        sb.append("rol.id as rol_id,  ");
        sb.append("rol.nombre as rol_nombre, ");
        sb.append("rol.codigo as rol_codigo, ");
        sb.append("per.id as permiso_id, ");
        sb.append("per.NOMBRE as permiso_nombre ");
        sb.append("from SI_REL_ROL_PERMISO rol_per ");
        sb.append("inner join si_permiso per on (rol_per.SI_PERMISO = per.ID and rol_per.ELIMINADO = 'False' and per.ELIMINADO = 'False') ");
        sb.append("inner join si_rol rol on (rol_per.SI_ROL = rol.ID and rol.ELIMINADO = 'False') ");
        sb.append("inner join SI_MODULO mod on (rol.si_modulo = mod.id and mod.ELIMINADO = 'False') ");
        sb.append("inner join SI_USUARIO_ROL usu_rol on (usu_rol.si_rol = rol.id and usu_rol.ELIMINADO = 'False') ");
        sb.append("where 1=1 ");
        
        queryBase = sb.toString();
        
        
    }
    
    
    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
    
    /**
     * 
     * @param usuarioId
     * @param moduloId
     * @param apCampo
     * @return La lista de roles con los permisos correspondientes. Si el usuario
     * no tiene ningún rol asignado, regresa una lista vacía (no nula).
     */
    
    public List<RolVO> fetchPermisosPorUsuarioModulo(String usuarioId, Integer moduloId, int apCampo) {
        
        StringBuilder sb = new StringBuilder();
        
        sb.append(queryBase);
        
        List params = new ArrayList();
        
        sb.append(" AND usu_rol.usuario = ? ");
        sb.append(" AND rol.SI_MODULO = ? ");
        sb.append(" AND usu_rol.AP_CAMPO= ? ");
        params.add(usuarioId);
        params.add(moduloId);
        params.add(apCampo);
        
        sb.append(" ORDER BY rol.id ASC, per.id ASC ");
        
        
        logger.log(Level.INFO, "query = {0}", sb.toString());
        
        Query q = em.createNativeQuery(sb.toString());
        
        
        // establecer condiciones de consulta
        int i = 0;
        
        for (Object param : params) {
            q.setParameter(++i, param);
        }
        
        List resultado = q.getResultList();
        
        logger.log(Level.INFO, "resultados = {0}", resultado.size());

        List<RolVO> roles = castVo(resultado);
        
        return roles;
        
    }
    
    
    /**
     * 
     * @param lista
     * @return 
     */
    private List<RolVO> castVo(List lista) {
        
        List<RolVO> roles = new ArrayList();
        
        RolVO rol = null;
        
        int rolId = -1;
        
        for (Iterator it = lista.iterator(); it.hasNext();) {
            
            Object[] obj = (Object[]) it.next();
            
            int auxRolId = (Integer)obj[3];
            
            // validar cambio de rol
            if (auxRolId != rolId) {
                rolId = auxRolId;
                
                rol = new RolVO();
                
                rol.setId(rolId);
                rol.setNombre(String.valueOf(obj[4]));
                rol.setCodigo(String.valueOf(obj[5]));
                rol.setPermisos(new ArrayList<PermisoVo>());
                
                // agregar a la lista de roles
                roles.add(rol);
                
            }
            
            // crear y agregar nuevo permiso
            PermisoVo permiso = new PermisoVo();
            
            permiso.setId((Integer)obj[6]);
            permiso.setNombre(String.valueOf(obj[7]));
            
            rol.getPermisos().add(permiso);
            
        }
        
        return roles;
        
    }
    
}
