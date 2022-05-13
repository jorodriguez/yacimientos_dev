package sia.inventarios.service;

import java.util.List;
import javax.ejb.Local;
import sia.excepciones.SIAException;
import sia.modelo.vo.inventarios.ArticuloCompraVO;
import sia.modelo.vo.inventarios.TransaccionArticuloVO;
import sia.modelo.vo.inventarios.TransaccionVO;

/**
 *
 * @author Aplimovil SA de CV
 */

@Local
public interface TransaccionRemote extends LocalServiceInterface<TransaccionVO, Integer> {

    public List<TransaccionArticuloVO> obtenerListaArticulos(Integer transaccionId, Integer campo) throws SIAException;

    public List<TransaccionVO> buscarPorStatus(Integer status, int campoID) throws SIAException;

    public void procesar(Integer transaccionId, String username, Integer campo) throws SIAException;

    public void confirmar(Integer transaccionId, String username, Integer campo) throws SIAException;

    public void rechazar(Integer transaccionId, String motivoRechazo, String username) throws SIAException;

    public boolean validarFolioOrdenDeCompra(String folio) throws SIAException;

    public void crear(TransaccionVO transaccionVO, List<TransaccionArticuloVO> articulosVO, String username, int campo) throws SIAException;

    public void crearYProcesar(TransaccionVO transaccionVO, List<TransaccionArticuloVO> articulosVO, String username, int campo) throws SIAException;

    public void actualizar(TransaccionVO transaccionVO, List<TransaccionArticuloVO> articulosVO, String username, int campo) throws SIAException;

    public List<TransaccionVO> rastrearArticulo(String filtro);

    public List<ArticuloCompraVO> listarArticulosPorFolioOrdenDeCompra(String folio);
    
    List<TransaccionArticuloVO> traerPorTrasaccionId(int idTransaccion, int idCampo);
    
    void crearConciliar(TransaccionVO transaccionVO, List<TransaccionArticuloVO> transaccionArticulosVO, String username, int campo) throws SIAException;
    
}
