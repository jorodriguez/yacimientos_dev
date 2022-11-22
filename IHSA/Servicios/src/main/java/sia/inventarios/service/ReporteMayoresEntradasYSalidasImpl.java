package sia.inventarios.service;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TemporalType;
import sia.constantes.Constantes;
import static sia.inventarios.service.Utilitarios.esNuloOVacio;
import sia.modelo.vo.inventarios.ReporteMayoresEntradasYSalidasVO;

/**
 *
 * @author Aplimovil SA de CV
 */
@Stateless
public class ReporteMayoresEntradasYSalidasImpl {

    private final static String QUERY = "SELECT articuloId, codigo, nombre, SUM(numeroUnidadesEntrantes)  AS totalUnidadesEntrantes, SUM(numeroUnidadesSalientes) AS totalUnidadesSalientes"
	    + " FROM ( "
	    + "     SELECT a.ID AS articuloId, a.CODIGO AS codigo, a.NOMBRE as nombre, "
	    + "     (CASE WHEN m.NUMERO_UNIDADES > 0 THEN m.NUMERO_UNIDADES ELSE 0 END) AS numeroUnidadesEntrantes,"
	    + "     (CASE WHEN m.NUMERO_UNIDADES < 0 THEN ABS(m.NUMERO_UNIDADES) ELSE 0 END) AS numeroUnidadesSalientes"
	    + "     FROM INV_INVENTARIO_MOVIMIENTO AS m"
	    + "     JOIN INV_INVENTARIO AS i ON m.INVENTARIO = i.ID"
	    + "     JOIN INV_ARTICULO AS a ON i.ARTICULO = a.ID"
	    + "     WHERE m.Eliminado = ?1 {0}"
	    + " ) AS t GROUP BY articuloId, codigo, nombre"
	    + " {1} ORDER BY totalUnidadesEntrantes limit 20 ";

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    public List<ReporteMayoresEntradasYSalidasVO> obtenerLista(ReporteMayoresEntradasYSalidasVO filtro) {
	String fechaCondicional = "";
	String numeroCondicional = "";
	List<Object> parametros = new ArrayList<Object>();
	parametros.add(Constantes.BOOLEAN_FALSE);

	if (!esNuloOVacio(filtro.getNumeroEntradas())) {
	    numeroCondicional += construirHaving(numeroCondicional, "numeroUnidadesEntrantes", parametros.size() + 1);
	    parametros.add(filtro.getNumeroEntradas());
	}
	if (!esNuloOVacio(filtro.getNumeroSalidas())) {
	    numeroCondicional += construirHaving(numeroCondicional, "numeroUnidadesSalientes", parametros.size() + 1);
	    parametros.add(filtro.getNumeroSalidas());
	}
	if (filtro.getFechaInicio() != null && filtro.getFechaFin() != null) {
	    fechaCondicional = " AND m.FECHA >= ?" + (parametros.size() + 1);
	    parametros.add(filtro.getFechaInicio());
	    fechaCondicional += " AND m.FECHA <= ?" + (parametros.size() + 1);
	    parametros.add(filtro.getFechaFin());
	}

	String consulta = MessageFormat.format(QUERY, fechaCondicional, numeroCondicional);
	Query query = em.createNativeQuery(consulta);

	//Fijar los parametros
	int contador = 1;

	for (Object o : parametros) {
	    if (o instanceof Date) {
		query.setParameter(contador++, (Date) o, TemporalType.DATE);
	    } else {
		query.setParameter(contador++, o);
	    }
	}

	List<Object[]> resultList = query.getResultList();
	List<ReporteMayoresEntradasYSalidasVO> lista = new ArrayList<ReporteMayoresEntradasYSalidasVO>();

	for (Object[] resultado : resultList) {
	    lista.add(new ReporteMayoresEntradasYSalidasVO(
		    Long.valueOf(0),//(Long) resultado[0],
		    (String) resultado[1],
		    (String) resultado[2],
		    ((BigDecimal) resultado[3]).doubleValue(),
		    ((BigDecimal) resultado[4]).doubleValue())
	    );
	}

	return lista;
    }

    public String construirHaving(String condicional, String field, int parametro) {
	String campo = String.format(" sum(%s) >= ?%d", field, parametro);
	return condicional.isEmpty() ? " HAVING " + campo : " AND " + campo;
    }
}
