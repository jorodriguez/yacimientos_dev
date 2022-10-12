package sia.inventarios.service;

import java.util.List;
import javax.ejb.Local;
import sia.excepciones.SIAException;
import sia.modelo.InvAlmacen;
import sia.modelo.vo.inventarios.AlmacenVO;
import sia.modelo.vo.inventarios.InventarioVO;

/**
 * * * @author Aplimovil SA de CV
 */
@Local
public interface AlmacenRemote extends LocalServiceInterface<AlmacenVO, Integer> {

    public List<InventarioVO> buscarInventariosPorArticulo(Integer almacenId, String keywords);

    public InventarioVO buscarInventario(Integer almacenId, Integer articuloId, Integer campo);

    public InventarioVO obtenerInventario(Integer almacenId, Integer articuloId, String username, Integer campo) throws SIAException;

    List<AlmacenVO> almacenesPorCampo(int idCampo);

    public InvAlmacen find(Object id);
    
    
}
