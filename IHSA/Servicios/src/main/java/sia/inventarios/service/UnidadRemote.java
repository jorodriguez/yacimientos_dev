package sia.inventarios.service;
import javax.ejb.Local;
import sia.modelo.vo.inventarios.UnidadVO;

@Local
public interface UnidadRemote extends LocalServiceInterface<UnidadVO, Integer> {

}

