/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.sistema.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import sia.constantes.Constantes;
import sia.excepciones.SIAException;
import sia.modelo.*;
import sia.modelo.sistema.AbstractFacade;
import sia.servicios.sistema.vo.SiAyudaVo;
import sia.util.UtilLog4j;

/**
 *
 * @author sluis
 */
@Stateless 
public class SiAyudaImpl extends AbstractFacade<SiAyuda>{
    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;
    @Inject
    private SiModuloImpl moduloService;
    @Inject
    private SiOpcionImpl opcionService;    
    @Inject
    private SiAdjuntoImpl adjuntoService;
    @Inject
    private SiAyudaAdjuntoImpl ayudaAdjuntoService;
    @Inject
    private SiParametroImpl parametroService;

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SiAyudaImpl() {
        super(SiAyuda.class);
    }

    
    public List<SiAyuda> getAyudasByModuloAndOpcion(String nombreModulo, Integer idOpcion, boolean status) {
        UtilLog4j.log.info(this,"SiAyudaImpls.getAyudasByModuloAndOpcion()");
        SiModulo modulo = moduloService.findModuloByName(nombreModulo, status);
        SiOpcion opcion = opcionService.find(idOpcion);
        
        UtilLog4j.log.info(this,"Filtrando por Módulo: " + modulo.getNombre() + " Opción: " + opcion.getNombre());
        
        List<SiAyuda> ayudasListTmp = null;
        
        if(modulo != null && opcion != null) {
            try {
                ayudasListTmp = em.createQuery("SELECT ayu FROM SiAyuda ayu WHERE ayu.modulo.id = :idModulo AND ayu.opcion.id = :idOpcion AND ayu.eliminado = :status")
                    .setParameter("idModulo", modulo.getId())
                    .setParameter("idOpcion", opcion.getId())
                    .setParameter("status", status).getResultList();
                
                if(ayudasListTmp != null) {
                    UtilLog4j.log.info(this,"Se encontraron " + ayudasListTmp.size() + " ayudas");
                    return ayudasListTmp;
                }
                else {
                    UtilLog4j.log.info(this,"No se encontraron ayudas");
                    return null;
                }
            } catch (Exception e) {
                UtilLog4j.log.info(this,e.getMessage());
                return null;
            }
        }
        else {
            UtilLog4j.log.info(this,"No se pudieron obtener las ayudas porque falta el Módulo o la Opción para realizar la búsqueda");
            return null;
        }
    }

    
    public void crearAyuda(String nombreAyuda, int idModulo, Integer idOpcion, String idUsuario, boolean status) throws Exception {
        UtilLog4j.log.info(this,"SiAyudaImpl.crearAyuda()");

        SiAyuda ayudaTmp = findAyudaByNameAndModuloAndOpcion(nombreAyuda, idModulo, idOpcion, status); //verificar que no existe la ayuda
        UtilLog4j.log.info(this,"ayudaTmp: " + ayudaTmp);
        if (ayudaTmp == null) {
            SiAyuda ayuda = new SiAyuda();
            ayuda.setNombre(nombreAyuda);
            ayuda.setEliminado(status);
            ayuda.setModulo(moduloService.find(idModulo));
            ayuda.setOpcion(opcionService.find(idOpcion));
            ayuda.setFechaGenero(new Date());
            ayuda.setHoraGenero(new Date());
            ayuda.setGenero(new Usuario(idUsuario));

            UtilLog4j.log.info(this,"DEBUG -- Ayuda antes de crear: " + "nombre: " + ayuda.getNombre() + " modulo: " + ayuda.getModulo().getNombre()
                    + " opcion: " + ayuda.getOpcion().getNombre() + " fechaGenero: " + ayuda.getFechaGenero() + " horaGenero: " + ayuda.getHoraGenero()
                    + " eliminado: " + ayuda.isEliminado() + " usuarioGenero: " + ayuda.getGenero());

            super.create(ayuda);
        }
        else {
            throw new Exception("Ya existe el nombre de esta ayuda");
        }
    }
    
    
    public void actualizarAyuda(SiAyuda ayuda, String nombreAyuda, int idModulo, Integer idOpcion, String idUsuario, boolean status) throws Exception {
        UtilLog4j.log.info(this,"SiAyudaImpls.actualizarAyuda()");

        if (!ayuda.getNombre().equals(nombreAyuda)) {
            if (findAyudaByNameAndModuloAndOpcion(nombreAyuda, idModulo, idOpcion, status) == null) {
                ayuda.setNombre(nombreAyuda);
                ayuda.setFechaGenero(new Date());
                ayuda.setHoraGenero(new Date());
                ayuda.setGenero(new Usuario(idUsuario));

                super.edit(ayuda);
            } else {
                throw new Exception("Ya existe el nombre de esta ayuda");
            }
        } else {
            ayuda.setNombre(nombreAyuda);
            ayuda.setFechaGenero(new Date());
            ayuda.setHoraGenero(new Date());
            ayuda.setGenero(new Usuario(idUsuario));

            super.edit(ayuda);
        }
    }
    
    
    public void deleteAyuda(SiAyuda ayuda, String idUsuario) throws SIAException, Exception {
        UtilLog4j.log.info(this,"SiAyudaImpl.deleteAyuda()");
        
        if (ayuda != null && idUsuario != null && !idUsuario.equals("")) {
            //Eliminar la Ayuda
            ayuda.setFechaGenero(new Date());
            ayuda.setHoraGenero(new Date());
            ayuda.setEliminado(Constantes.ELIMINADO);
            ayuda.setGenero(new Usuario(idUsuario));

            super.edit(ayuda);
            
            //Eliminar los Adjuntos
            List<SiAyudaAdjunto> ayudaAdjuntos = ayudaAdjuntoService.getAllAdjuntosByAyuda(ayuda);
            for(SiAyudaAdjunto saa : ayudaAdjuntos) {
                ayudaAdjuntoService.delete(saa, idUsuario);

                //Eliminar los archivos físicamente
                try {
                    File file = new File(parametroService.find(1).getUploadDirectory() + saa.getSiAdjunto().getUrl());
                    file.delete();
                }
                catch (Exception e) {
                    UtilLog4j.log.fatal(this,e.getMessage());
                    throw new SIAException(SiAyudaImpl.class.getName(), "deleteAyuda()",
                            "No se pudo eliminar el archivo físicamente. Porfavor contacta al Equipo del SIA para arreglar esto al correo soportesia@ihsa.mx");
                }
            }
        }
        else {
            throw new SIAException(SiAyudaImpl.class.getName(), "saveAyudaAdjunto()",
                    "Faltan parámetros para poder eliminar la Ayuda",
                    ("Parámetros: ayuda: " + (ayuda != null ? ayuda.getId() : null)
                    + " idUsuario" + idUsuario));
        }
    }    

    
    public SiAyuda findAyudaByNameAndModuloAndOpcion(String nombreAyuda, int idModulo, Integer idOpcion, boolean estado) {
        UtilLog4j.log.info(this,"SiAyudaImpl.findAyudaByNameAndModuloAndOpcion()");
        if(nombreAyuda != null && !nombreAyuda.equals("") && idModulo > 0 && !nombreAyuda.equals("") && idOpcion != null){
            try {
                return (SiAyuda)em.createQuery("SELECT ayu FROM SiAyuda ayu WHERE ayu.nombre = :nombreAyuda AND ayu.modulo.id = :id_Modulo AND ayu.opcion.id = :idOpcion AND ayu.eliminado = :status")
                    .setParameter("nombreAyuda", nombreAyuda)
                    .setParameter("id_Modulo", idModulo)
                    .setParameter("idOpcion", idOpcion)
                    .setParameter("status", estado).getSingleResult();
            } catch (Exception e) {
                UtilLog4j.log.fatal(this,e.getMessage());
                UtilLog4j.log.fatal(this,"No se obtuvo ninguna ayuda que coincida con los parametros");
                return null;
            }
        }
        else {
            UtilLog4j.log.info(this,"No se pudo obtener la Ayuda porque falta nombreAyuda, nombreModulo o nombreOpcion en los parametros");
            return null;
        }
    }

    
    public List<SiAyuda> findByEstado(boolean estado) {
        UtilLog4j.log.info(this,"SiAyudaImpl.findByEstado()");
        return em.createQuery("SELECT ayu FROM SiAyuda ayu WHERE ayu.eliminado = :estado").setParameter("estado", estado).getResultList();
    }

    
    public SiAyudaAdjunto saveAyudaAdjunto(SiAyuda ayuda, String fileName, String contentType, Long tamanioArchivo, String ruta, String idUsuario) throws SIAException, Exception {
        UtilLog4j.log.info(this,"SiAyudaImpl.saveAdjuntoAyuda()");

        SiAyudaAdjunto ayudaAdjunto = null;

        if (ayuda != null && fileName != null && !fileName.equals("") && contentType != null && !contentType.equals("") && tamanioArchivo != 0 && ruta != null && !ruta.equals("") && idUsuario != null && !idUsuario.equals("")) {

            //Guardar el Adjunto primero
            SiAdjunto adjunto = adjuntoService.save(fileName, ruta, contentType, tamanioArchivo, idUsuario);

            //Guardar la relación Adjunto-Ayuda
            ayudaAdjuntoService.save(ayuda, adjunto, idUsuario);
        }
        else {
            throw new SIAException(SiAyudaImpl.class.getName(), "saveAyudaAdjunto()",
                    "Faltan parámetros para poder crear el Adjunto de la Ayuda",
                    ("Parámetros: ayuda: " + (ayuda != null ? ayuda.getId() : null)
                    + " fileName" + fileName
                    + " contentType" + contentType
                    + " tamanioArchivo" + tamanioArchivo
                    + " ruta" + ruta
                    + " idUsuario" + idUsuario));
        }
        return ayudaAdjunto;
    }

    
    public void deleteAyudaAdjunto(SiAyudaAdjunto ayudaAdjunto, String idUsuario) throws SIAException, Exception {
        UtilLog4j.log.info(this,"SiAyudaImpl.deleteAdjuntoAyuda()");

        if (ayudaAdjunto != null) {
            //Eliminar relación Adjunto - Ayuda
            ayudaAdjuntoService.delete(ayudaAdjunto, idUsuario);
            
            //ELiminar el adjunto
            adjuntoService.delete(ayudaAdjunto.getSiAdjunto(), idUsuario);
            
        } else {
            throw new SIAException(SiAyudaImpl.class.getName(), "deleteAdjuntoAyuda()",
                    "Faltan parámetros para poder eliminar el adjunto",
                    ("Parámetros: ayudaAdjunto: " + (ayudaAdjunto != null ? ayudaAdjunto.getId() : null)));
        }
    }
    
    
    public List<SiAyudaVo> getAllSiAyuda(String orderByField, boolean sortAscending, boolean eliminado) {
        UtilLog4j.log.info(this,"SiAyudaImpl.getAllSiAyuda()");

        String q = "SELECT "
                + "a.ID, " //0
                + "a.NOMBRE, " //1
                + "a.MODULO, " //2
                + "a.OPCION " //3
                + "FROM "
                + "SI_AYUDA a "
                + "WHERE "
                + "a.ELIMINADO = '" + (eliminado ? Constantes.ELIMINADO : Constantes.NO_ELIMINADO) + "' ";

        if (orderByField != null && !orderByField.isEmpty()) {
            q += " ORDER BY a." + orderByField + " " + (sortAscending ? Constantes.ORDER_BY_ASC : Constantes.ORDER_BY_DESC);
        }

        Query query = em.createNativeQuery(q);

        UtilLog4j.log.info(this,"query: " + query.toString());

        List<Object[]> result = query.getResultList();
        List<SiAyudaVo> list = new ArrayList<SiAyudaVo>();

        SiAyudaVo vo = null;

        for (Object[] objects : result) {
            vo = new SiAyudaVo();
            vo.setId((Integer) objects[0]);
            vo.setNombre((String) objects[1]);
            vo.setIdSiModulo((Integer) objects[2]);
            vo.setIdSiOpcion((Integer) objects[3]);
            list.add(vo);
        }

        UtilLog4j.log.info(this,"Se encontraron " + (list != null ? list.size() : 0) + " SiAyuda");

        return (list != null ? list : Collections.EMPTY_LIST);
    }    

    
    public List<SiAyudaVo> getAllSiAyudaBySiModuloAnSiOpcion(int idSiModulo, int idSiOPcion, String orderByField, boolean sortAscending, boolean eliminado) {
        UtilLog4j.log.info(this,"SiAyudaImpl.getAllSiAyudaBySiModuloAnSiOpcion()");

        String q = "SELECT "
                + "a.ID, " //0
                + "a.NOMBRE, " //1
                + "FROM "
                + "SI_AYUDA a "
                + "WHERE "
                + "a.ELIMINADO = '" + (eliminado ? Constantes.ELIMINADO : Constantes.NO_ELIMINADO) + "' "
                + "AND a.SI_MODULO=" + idSiModulo + " "
                + "AND a.SI_OPCION=" + idSiOPcion;

        if (orderByField != null && !orderByField.isEmpty()) {
            q += " ORDER BY a." + orderByField + " " + (sortAscending ? Constantes.ORDER_BY_ASC : Constantes.ORDER_BY_DESC);
        }

        Query query = em.createNativeQuery(q);

        UtilLog4j.log.info(this,"query: " + query.toString());

        List<Object[]> result = query.getResultList();
        List<SiAyudaVo> list = new ArrayList<SiAyudaVo>();

        SiAyudaVo vo = null;

        for (Object[] objects : result) {
            vo = new SiAyudaVo();
            vo.setId((Integer) objects[0]);
            vo.setNombre((String) objects[1]);
            list.add(vo);
        }

        UtilLog4j.log.info(this,"Se encontraron " + (list != null ? list.size() : 0) + " SiAyuda");

        return (list != null ? list : Collections.EMPTY_LIST);
    }
}