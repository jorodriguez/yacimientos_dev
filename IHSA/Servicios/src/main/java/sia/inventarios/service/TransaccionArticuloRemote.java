package sia.inventarios.service;

import javax.ejb.Local;
import sia.modelo.vo.inventarios.TransaccionArticuloVO;

/**
 * * * @author Aplimovil SA de CV
 */
@Local
public interface TransaccionArticuloRemote extends LocalServiceInterface<TransaccionArticuloVO, Integer> {
}
