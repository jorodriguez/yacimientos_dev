package sia.servicios.campo.nuevo.impl;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import sia.constantes.Constantes;
import sia.excepciones.ExistingItemException;
import sia.excepciones.ItemUsedBySystemException;
import sia.modelo.RhPuesto;
import sia.modelo.Usuario;
import sia.modelo.puesto.vo.RhPuestoVo;
import sia.modelo.sistema.AbstractFacade;
import sia.util.UtilLog4j;

/**
 *
 * @author rluna
 */
@Stateless 
public class RhPuestoImpl extends AbstractFacade<RhPuesto> {

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public RhPuestoImpl() {
        super(RhPuesto.class);
    }

    //Variables    
    private RhPuesto rhPuesto;
    private String antesEvto;
    
    public void savePuesto(String idUsuarioGenero, String nombrePuesto, String descripcion) throws ExistingItemException {
        UtilLog4j.log.info(this, "RhPuestoImp.save()");
        RhPuestoVo existente = findByName(nombrePuesto, false);
        rhPuesto = new RhPuesto();
        if (existente == null) {
            rhPuesto.setNombre(nombrePuesto);
            rhPuesto.setDescripcion(descripcion);
            rhPuesto.setEliminado(Constantes.BOOLEAN_FALSE);
            rhPuesto.setFechaGenero(new Date());
            rhPuesto.setHoraGenero(new Date());
            rhPuesto.setGenero(new Usuario(idUsuarioGenero));
            UtilLog4j.log.info(this, "Valor de rhpuesto" + rhPuesto.toString());
            create(rhPuesto);
            UtilLog4j.log.info(this, "Puesto CREATED SUCCESSFULLY");
            // rhPuesto = new RhPuesto();
        } else {
            throw new ExistingItemException("rhPuesto.mensaje.error.siPuestoExistente", rhPuesto.getNombre(), rhPuesto);
        }
    }

    
    public void updatePuesto(String nombrePuesto, String idUsuarioGenero, String descripcion, int idPuesto) throws ExistingItemException {
        UtilLog4j.log.info(this, "Objetos Que llegan: " + nombrePuesto + idUsuarioGenero + descripcion + idPuesto);
        UtilLog4j.log.info(this, "RhPuestoImp.update()");
        //Recibir el puesto que se va a cambiar
        //rhPuesto= new RhPuesto();
        RhPuesto original = find(idPuesto);
        UtilLog4j.log.info(this, "Orignial" + original.toString());
        RhPuestoVo existente = findByName(nombrePuesto, false);
        this.antesEvto = original.toString();
//        RhPuestoVo existente = findByName(nombrePuesto,false);
//    this.antesEvto = find(rhPuesto.getId()).toString();
        //rhPuesto= new RhPuesto();
        if (existente == null) {
            original.setNombre(nombrePuesto);
            original.setDescripcion(descripcion);
            original.setModifico(new Usuario(idUsuarioGenero));
            original.setFechaModifico(new Date());
            original.setHoraModifico(new Date());
            UtilLog4j.log.info(this, "Valor de rhpuesto" + original.toString());
            edit(original);
            UtilLog4j.log.info(this, "Puesto UPDATED SUCCESSFULLY");
        } else {
            if (original.getNombre().equals(nombrePuesto)) {
                original.setDescripcion(descripcion);
                original.setModifico(new Usuario(idUsuarioGenero));
                original.setFechaModifico(new Date());
                original.setHoraModifico(new Date());
                edit(original);
                UtilLog4j.log.info(this, "Puesto UPDATED SUCCESSFULLY");
            } else {
                throw new ExistingItemException("rhPuesto.mensaje.error.siPuestoExistente", original.getNombre(), rhPuesto);
            }
        }
    }

    
    public void deletePuesto(int idPuesto, String idUsuarioGenero, String nombre) throws ItemUsedBySystemException {
        UtilLog4j.log.info(this, "Objetos Que llegan: " + idUsuarioGenero + idPuesto);
        UtilLog4j.log.info(this, "RhPuestoImp.delete()");
        RhPuesto original = find(idPuesto);
        UtilLog4j.log.info(this, "Orignial" + original.toString());
        this.antesEvto = original.toString();

        if (isUsed(idPuesto) == false) {

            original.setModifico(new Usuario(idUsuarioGenero));
            original.setFechaModifico(new Date());
            original.setHoraModifico(new Date());
            original.setEliminado(Constantes.ELIMINADO);
            UtilLog4j.log.info(this, "Valor de rhpuesto" + original.toString());
            edit(original);
            UtilLog4j.log.info(this, "Puesto DELETED SUCCESSFULLY");
        } else {
            throw new ItemUsedBySystemException(original.getNombre(), rhPuesto);
        }


//                    
    }

    //Lista para devolver todo los puestos u objetos de la base de datos usando una clase virtual que regresa 
    //La cual regresa el string de en una lista de arreglos que cada arreglo es un campo de la db fijarse bien en los 
    //parametros de entrada para los demas programadores
    public List<RhPuestoVo> findAllRhPuesto(String orderByField, boolean sortAscending, boolean eliminado) {
        List<Object[]> list;
        Query q = em.createNativeQuery("SELECT a.ID, "//0
                + " a.NOMBRE,"//1
                + " a.DESCRIPCION" //2
                + " FROM RH_PUESTO a "
                + " WHERE a.ELIMINADO='" + Constantes.NO_ELIMINADO + "'"
                + " ORDER BY a." + orderByField + " " + (sortAscending ? Constantes.ORDER_BY_ASC : Constantes.ORDER_BY_DESC));
        list = q.getResultList();
        UtilLog4j.log.info(this, "query: " + q.toString());
        List<RhPuestoVo> voList = new ArrayList<RhPuestoVo>();
        for (Object[] objeto : list) {
            voList.add(castPuesto(objeto));
        }
        UtilLog4j.log.info(this, "Se encontraron " + (list != null ? list.size() : 0) + " RhPuestos");
        return voList;
    }

    
    public List<RhPuestoVo> getRhPuestoLike(String cadena) {
        List<Object[]> list;
        Query q = em.createNativeQuery("SELECT a.ID, "//0
                + " a.NOMBRE,"//1
                + " a.DESCRIPCION" //2
                + " FROM RH_PUESTO a "
                + " WHERE a.ELIMINADO='" + Constantes.NO_ELIMINADO + "'"
                + " AND upper(a.nombre) LIKE '" + cadena.toUpperCase() + "%'");
        list = q.getResultList();
        UtilLog4j.log.info(this, "query: " + q.toString());
        List<RhPuestoVo> voList = new ArrayList<RhPuestoVo>();

        for (Object[] objeto : list) {
            voList.add(castPuesto(objeto));

        }
        UtilLog4j.log.info(this, "Se encontraron " + (list != null ? list.size() : 0) + " RhPuestos");
        return voList;
    }
//metodo para buscar por nombre hay que usar una clase vo 

    
    public RhPuestoVo findByName(String nombre, boolean eliminado) {
        UtilLog4j.log.info(this, "RhPuestoImp.findByName()");
        try {
            //primero hay qe guardar todos los objetos que te retorna el query en un tipo de dato

            Object[] objetos;
            RhPuestoVo rhPuestoVo;//para el valor de retorno
            Query q = em.createNativeQuery("SELECT a.NOMBRE, a.ID, a.DESCRIPCION"
                    + " FROM RH_PUESTO a"
                    + " WHERE a.NOMBRE='" + nombre + "'"
                    + " AND a.ELIMINADO='" + Constantes.NO_ELIMINADO + "'");
            //guardar los objetos del query en el arreglo
            objetos = (Object[]) q.getSingleResult();
            //validacion de lo que trae de la db
            rhPuestoVo = new RhPuestoVo();
            rhPuestoVo.setNombre((String) objetos[0]);
            rhPuestoVo.setId((Integer) objetos[1]);
            rhPuestoVo.setDescripcion((String) objetos[2]);
            return rhPuestoVo;
        } catch (Exception e) {
            return null;
        }
    }

    
    public boolean isUsed(int id) {
        UtilLog4j.log.info(this, "RhPuesto.isUsed()");
        int cont = 0;
        List<Object> list = Collections.EMPTY_LIST;
        Query q = em.createNativeQuery("SELECT a.RH_PUESTO"
                + " FROM AP_CAMPO_USUARIO_RH_PUESTO a"
                + " WHERE a.RH_PUESTO="
                + id + " AND a.ELIMINADO='"
                + Constantes.NO_ELIMINADO + "'");
        list = q.getResultList();
        if (list != null && !list.isEmpty()) {
            UtilLog4j.log.info(this, "RHPuesto " + id + " usado en Ap_Campo_USuario_Rh_Puesto");
            cont++;
            list.clear();
        }

        return (cont == 0 ? false : true);
    }

    /**
     * @return the antesEvto
     */
    public String getAntesEvto() {
        return antesEvto;
    }

    /**
     * @param antesEvto the antesEvto to set
     */
    public void setAntesEvto(String antesEvto) {
        this.antesEvto = antesEvto;
    }

    private RhPuestoVo castPuesto(Object[] objeto) {
        RhPuestoVo vo = new RhPuestoVo();
        vo.setId((Integer) objeto[0]);
        vo.setNombre(String.valueOf(objeto[1]));
        if (objeto[2] == null) {
            vo.setDescripcion("");
        } else {
            vo.setDescripcion(String.valueOf(objeto[2]));
        }
        return vo;
    }

    
    public String traerPuestoActivoJson() {
        Gson gson = null;
        try {
            gson = new Gson();
            StringBuilder sb = new StringBuilder();
            sb.append("select p.id, p.nombre");
            sb.append(" from rh_puesto p");
            sb.append(" where p.eliminado ='").append(Constantes.NO_ELIMINADO).append("'");


            List<Object[]> lista = em.createNativeQuery(sb.toString()).getResultList();
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
            UtilLog4j.log.info(this, "Excepcion los puesto " + e.getMessage());
            return null;
        }
    }
    
      
    public RhPuestoVo findById(int idPuesto, boolean eliminado) {
        UtilLog4j.log.info(this, "RhPuestoImp.findById()");
        try {
            //primero hay qe guardar todos los objetos que te retorna el query en un tipo de dato

            Object[] objetos;
            RhPuestoVo rhPuestoVo;//para el valor de retorno
            clearQuery();
            query.append("SELECT a.NOMBRE, a.ID, a.DESCRIPCION FROM RH_PUESTO a");
            query.append(" WHERE a.id = ").append(idPuesto);
                    query.append(" AND a.ELIMINADO='").append(Constantes.NO_ELIMINADO).append("'");
            //guardar los objetos del query en el arreglo
            objetos = (Object[])em.createNativeQuery(query.toString()).getSingleResult();
            //validacion de lo que trae de la db
            rhPuestoVo = new RhPuestoVo();
            rhPuestoVo.setNombre((String) objetos[0]);
            rhPuestoVo.setId((Integer) objetos[1]);
            rhPuestoVo.setDescripcion((String) objetos[2]);
            return rhPuestoVo;
        } catch (Exception e) {
            return null;
        }
    }
}
