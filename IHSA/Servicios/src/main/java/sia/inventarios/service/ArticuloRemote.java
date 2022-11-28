package sia.inventarios.service;

import java.util.List;
import javax.ejb.Local;
import javax.faces.model.SelectItem;
import sia.excepciones.SIAException;
import sia.modelo.InvArticulo;
import sia.modelo.campo.vo.CampoVo;
import sia.modelo.sistema.vo.CategoriaVo;
import sia.modelo.vo.inventarios.ArticuloInventarioVO;
import sia.modelo.vo.inventarios.ArticuloVO;
import sia.modelo.vo.inventarios.InventarioVO;

/**
 * * * @author Aplimovil SA de CV
 */
@Local
public interface ArticuloRemote extends LocalServiceInterface<ArticuloVO, Integer> {

    public void create(InvArticulo articulo);

    public void edit(InvArticulo articulo);

    public void remove(InvArticulo articulo);

    public InvArticulo find(Object id);

    public List<InvArticulo> findAll();

    public List<InventarioVO> buscarInventarios(Integer articuloId, Integer campo);

    public List<ArticuloVO> obtenerArticulos(String codigo, int campoID, int categoriaID, String codigosCategorias);

    public List<ArticuloVO> obtenerArticulosUsuario(String codigo, int categoriaID, String codigosCategorias, String Usuario);

    public List<SelectItem> obtenerArticulosItems(String texto, String codigo, int campoID, int categoriaID, String codigosCategorias);

    public List<SelectItem> obtenerArticulosItemsUsuario(String texto, String codigo, int categoriaID, String codigosCategorias, String usuarioID);

    public ArticuloVO obtenerArticulos(int articuloID, int campoID);

    public ArticuloVO buscar(Integer id, Integer campo) throws SIAException;

    int guardarArticulo(ArticuloVO articulo, String sesion, List<CampoVo> campos, List<CategoriaVo> categorias, String numParte) throws SIAException;

    List<ArticuloVO> articulosFrecuentes(String usr, int campoID);

    void cambiarArticulo(String sesion, List<ArticuloVO> listaCambiarArticulos, List<CategoriaVo> listaCambiarSeleccionada);

    public List<ArticuloVO> articulosFrecuentesOrden(String usr, int campoID);

    String construirCodigo(List<CategoriaVo> categoriasSeleccionadas);

    ArticuloInventarioVO buscarArticuloConInventarios(String codigo, Integer campo);

    boolean existeArticuloConCodigo(String codigo, int campo);

    List<SelectItem> obtenerCategorias(int campoID);

    InvArticulo buscarPorNombre(String nombre, int unidadID);

    InvArticulo buscarPorCodigoInterno(String codigoInt, int unidadID);

    List<ArticuloVO> buscarArticuloSinCategoriaPorGenero(String sesion);

    void agregarCategoriaArticulo(String id, List<ArticuloVO> latemp, List<CategoriaVo> get);

    InvArticulo buscarPorCodigo(String codigo, int unidadID);

    List<ArticuloVO> buscarPorPalabras(String palabra, String nombrCampo);

    public ArticuloVO buscar(Integer id) throws SIAException;

    List<ArticuloVO> obtenerArticulosPorPalabra(String palabra, int campoID);
}
