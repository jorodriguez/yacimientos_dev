package sia.inventarios.service;

import java.util.List;
import javax.ejb.Local;
import sia.excepciones.SIAException;

/**
 *
 * @author Aplimovil SA de CV
 * @param <ClaseVO>
 * @param <TipoID>
 *
 * Esta clase es necesaria para minimizar la repeticion de codigo en las
 * interfaces, asi como para hacer funcionar la clase LocalAbstractBean en el
 * componente WEB, que permite la reutilizacion de codigo de los views tipo
 * catalogo.
 */
@Local
public interface LocalServiceInterface<ClaseVO, TipoID> {

    public List<ClaseVO> buscarPorFiltros(ClaseVO filtro, Integer campo);

    public List<ClaseVO> buscarPorFiltros(ClaseVO filtro, Integer inicio, Integer tamanioPagina,
            String campoOrdenar, boolean esAscendente, Integer campo);

    public int contarPorFiltros(ClaseVO filtro, Integer campo) throws SIAException;

    public ClaseVO buscar(TipoID id) throws SIAException;

    public void crear(ClaseVO entityVO, String username, int campo) throws SIAException;

    public void actualizar(ClaseVO entityVO, String username, int campo) throws SIAException;

    public void eliminar(TipoID id, String username, Integer campo) throws SIAException;

}
