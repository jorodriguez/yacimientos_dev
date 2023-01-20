/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lector.servicios.sistema.impl;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import lector.archivador.ProveedorAlmacenDocumentos;
import lector.constantes.Constantes;
import lector.dominio.vo.AdjuntoVO;
import lector.excepciones.SIAException;
import lector.modelo.SiAdjunto;
import lector.modelo.Usuario;
import lector.sistema.AbstractFacade;
import lector.util.UtilLog4j;

/**
 *
 * @author sluis
 */
@Stateless 
public class SiAdjuntoImpl extends AbstractFacade<SiAdjunto>{

    @PersistenceContext(unitName =  Constantes.PERSISTENCE_UNIT)
    private EntityManager em;
    
    @Inject
    private ProveedorAlmacenDocumentos almacenDocumentos;
    //
    String beforeEvent;

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SiAdjuntoImpl() {
        super(SiAdjunto.class);
    }

    
    public int saveSiAdjunto(String fileName, String contentType, String absolutePath, long size, String idUsuario) {
        UtilLog4j.log.info(this, "SiAdjuntoImpl.saveSiAdjunto()");

        SiAdjunto siAdjunto = new SiAdjunto();

        siAdjunto.setNombre(fileName);
        siAdjunto.setTipoArchivo(contentType);
        siAdjunto.setUrl(absolutePath);
        siAdjunto.setPeso(size);
        siAdjunto.setGenero(new Usuario(idUsuario));
        siAdjunto.setFechaGenero(new Date());
        siAdjunto.setHoraGenero(new Date());
        siAdjunto.setEliminado(Constantes.NO_ELIMINADO);
        UUID uuid = UUID.randomUUID();
        siAdjunto.setUuid(uuid.toString());
        create(siAdjunto);
        UtilLog4j.log.info(this, "SiAdjunto CREATED SUCCESSFULLY");

        return siAdjunto.getId();
    }

    
    public int saveSiAdjunto(AdjuntoVO vo, String idUsuario) {
        //String fileName, String contentType, String absolutePath, long size, String idUsuario) {
        UtilLog4j.log.info(this, "SiAdjuntoImpl.saveSiAdjunto()");
        int ret = 0;
        if (vo != null
                && vo.getUrl() != null && !vo.getUrl().isEmpty()
                && vo.getNombre() != null && !vo.getNombre().isEmpty()
                && vo.getTipoArchivo() != null && !vo.getTipoArchivo().isEmpty()
                && vo.getTamanio() > 0) {
            SiAdjunto siAdjunto = new SiAdjunto();

            siAdjunto.setNombre(vo.getNombre());
            siAdjunto.setTipoArchivo(vo.getTipoArchivo());
            siAdjunto.setUrl(vo.getUrl());
            siAdjunto.setPeso(vo.getTamanio());
            siAdjunto.setGenero(new Usuario(idUsuario));
            siAdjunto.setFechaGenero(new Date());
            siAdjunto.setHoraGenero(new Date());
            siAdjunto.setEliminado(Constantes.NO_ELIMINADO);
            UUID uuid = UUID.randomUUID();
            siAdjunto.setUuid(uuid.toString());
            create(siAdjunto);
            UtilLog4j.log.info(this, "SiAdjunto CREATED SUCCESSFULLY");
            ret = siAdjunto.getId();
        }

        return ret;
    }

    
    public void delete(int idSiAdjunto, String idUsuario) {
        UtilLog4j.log.info(this, "SiAdjuntoImpl.delete()");

        SiAdjunto siAdjunto = find(idSiAdjunto);
        this.beforeEvent = siAdjunto.toString();

        siAdjunto.setModifico(new Usuario(idUsuario));
        siAdjunto.setFechaModifico(new Date());
        siAdjunto.setHoraModifico(new Date());
        siAdjunto.setEliminado(Constantes.ELIMINADO);

        edit(siAdjunto);
        UtilLog4j.log.info(this, "SiAdjunto DELETED SUCCESSFULLY");
    }

    
    public SiAdjunto getPorElemento(Integer idElemento) {
        try {
            return (SiAdjunto) em.createQuery("SELECT s FROM SiAdjunto s WHERE s.idElemento = :idElemento").setParameter("idElemento", idElemento).getResultList().get(0);
        } catch (Exception e) {
            UtilLog4j.log.error(e);
            return null;
        }
    }

    
    public List<SiAdjunto> getListaPorElemento(Integer idElemento) {
        return em.createQuery("SELECT s FROM SiAdjunto s WHERE s.idElemento = :idElemento").setParameter("idElemento", idElemento).getResultList();
    }

    
    public boolean guardarArchivo(String genero, int elemento, String absolutePath, String fileName, String contentType, long size, int modulo, String tipo) {
        UUID uuid = UUID.randomUUID();
        return this.guardarArchivo(genero, elemento, absolutePath, fileName, contentType, size, modulo, tipo, uuid.toString());
    }

    
    public boolean guardarArchivo(String genero, int elemento, String absolutePath, String fileName, String contentType, long size, int modulo, String tipo, String uuid) {
        boolean v = false;
        try {
            SiAdjunto siAdjunto = new SiAdjunto();
            siAdjunto.setGenero(new Usuario(genero));
            siAdjunto.setIdElemento(elemento);
            siAdjunto.setUrl(absolutePath);
            siAdjunto.setNombre(fileName);
            siAdjunto.setTipoArchivo(contentType);
            siAdjunto.setPeso(size);
            siAdjunto.setFechaGenero(new Date());
            siAdjunto.setHoraGenero(new Date());
            siAdjunto.setEliminado(false);
            siAdjunto.setTipoElemento(tipo);
            siAdjunto.setUuid(uuid);
            this.create(siAdjunto);
            v = true;
        } catch (Exception e) {
            e.getStackTrace();
        }
        return v;
    }

    
    public List<SiAdjunto> traerArchivoPorElemento(int elemento) {
        return em.createQuery("SELECT f FROM SiAdjunto f WHERE f.idElemento = :ele ORDER BY f.id ASC").setParameter("ele", elemento).getResultList();
    }

    
    public List<SiAdjunto> traerArchivos(int modulo, int elemento, String tipoElemento) {
        return em.createQuery("SELECT f FROM SiAdjunto f WHERE f.siModulo.id = :modulo  AND f.idElemento = :elemento  AND f.tipoElemento = :tipoElemento AND f.eliminado = :false "
                + " ORDER BY  f.id ASC").setParameter("false", false).setParameter("modulo", modulo).setParameter("elemento", elemento).setParameter("tipoElemento", tipoElemento).getResultList();
    }

    
    public List<SiAdjunto> traerArchivoPorModulo(int modulo) {
        return em.createQuery("SELECT f FROM SiAdjunto f WHERE f.siModulo.id = :modulo").setParameter("modulo", modulo).getResultList();
    }

    
    public SiAdjunto guardarArchivoDevolverArchivo(String genero, Integer elemento, String absolutePath, String fileName, String contentType, long size, int modulo, String tipo) {
        UtilLog4j.log.info(this, "SiAdjuntoImpl.guardarArchivoDevolverArchivo()");

        SiAdjunto retVal = null;

        try {
            SiAdjunto siAdjunto = new SiAdjunto();
            siAdjunto.setGenero(new Usuario(genero));
            siAdjunto.setIdElemento(elemento);
            siAdjunto.setUrl(absolutePath);
            siAdjunto.setNombre(fileName);
            siAdjunto.setTipoArchivo(contentType);
            siAdjunto.setPeso(size);
            siAdjunto.setFechaGenero(new Date());
            siAdjunto.setHoraGenero(new Date());
            siAdjunto.setEliminado(false);
            siAdjunto.setTipoElemento(tipo);
            UUID uuid = UUID.randomUUID();
            siAdjunto.setUuid(uuid.toString());
            this.create(siAdjunto);
            retVal = siAdjunto;
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Error al guardar el archivo adjunto y devolverlo", e);
        }

        return retVal;
    }

    
    public SiAdjunto save(String fileName, String absolutePath, String contentType, String tipo, long size, String idUsuario) throws SIAException {
        UtilLog4j.log.info(this, "SiAdjuntoImpl.save(String fileName, String absolutePath, String contentType, long size, String idUsuario)");
        UUID uuid = UUID.randomUUID();
        return this.save(fileName, absolutePath, contentType, tipo, size, idUsuario, uuid.toString());
    }

    
    public SiAdjunto save(String fileName, String absolutePath, String contentType, long size, String idUsuario) throws SIAException {
        UtilLog4j.log.info(this, "SiAdjuntoImpl.save(String fileName, String absolutePath, String contentType, long size, String idUsuario)");
        UUID uuid = UUID.randomUUID();
        return this.save(fileName, absolutePath, contentType, size, idUsuario, uuid.toString());
    }

    
    public SiAdjunto save(String fileName, String absolutePath, String contentType, long size, String idUsuario, String uuid) throws SIAException {
        UtilLog4j.log.info(this, "SiAdjuntoImpl.save(String fileName, String absolutePath, String contentType, long size, String idUsuario, String uuid)");
        return this.save(fileName, absolutePath, contentType, null, size, idUsuario, uuid);
    }

    
    public SiAdjunto save(String fileName, String absolutePath, String contentType, String tipo, long size, String idUsuario, String uuid) throws SIAException {
        UtilLog4j.log.info(this, "SiAdjuntoImpl.save(String fileName, String absolutePath, String contentType, long size, String idUsuario, String uuid)");

        SiAdjunto adjunto = null;

        if (absolutePath != null && !absolutePath.equals("") && contentType != null && !contentType.equals("") && size != 0 && idUsuario != null && !idUsuario.equals("")) {
            adjunto = new SiAdjunto();

            adjunto.setNombre(fileName);
            adjunto.setUrl(absolutePath);
            adjunto.setTipoArchivo(contentType);
            adjunto.setPeso(size);
            adjunto.setFechaGenero(new Date());
            adjunto.setHoraGenero(new Date());
            adjunto.setEliminado(Constantes.NO_ELIMINADO);
            adjunto.setGenero(new Usuario(idUsuario));
            adjunto.setUuid(uuid);
            if (tipo != null && !tipo.isEmpty()) {
                adjunto.setTipoElemento(tipo);
            }

            super.create(adjunto);

        } else {
            throw new SIAException(SiAdjuntoImpl.class.getName(), "save()",
                    "Faltan parámetros para poder guardar el adjunto",
                    ("Parámetros: fileName: " + fileName
                    + "absolutePath" + absolutePath
                    + "contentType" + contentType
                    + "size" + size
                    + "idUsuario" + idUsuario));
        }

        UtilLog4j.log.info(this, "SiAdjunto CREATED SUCCESSFULLY");

        return adjunto;
    }

    
    public SiAdjunto update(SiAdjunto adjunto, String idUsuario) throws SIAException {
        UtilLog4j.log.info(this, "SiAdjuntoImpl.update()");

        String antesEvento = super.find(adjunto.getId()).toString();

        adjunto.setModifico(new Usuario(idUsuario));
        adjunto.setFechaModifico(new Date());
        adjunto.setHoraModifico(new Date());
        super.edit(adjunto);

        UtilLog4j.log.info(this, "SiAdjunto UPDATED SUCCESSFULLY");

        return adjunto;
    }

    
    public SiAdjunto delete(SiAdjunto adjunto, String idUsuario) throws SIAException {
        UtilLog4j.log.info(this, "SiAdjuntoImpl.delete()");

        if (adjunto != null) {
            String antesEvento = super.find(adjunto.getId()).toString();

            adjunto.setFechaGenero(new Date());
            adjunto.setHoraGenero(new Date());
            adjunto.setEliminado(Constantes.ELIMINADO);
            adjunto.setGenero(new Usuario(idUsuario));
            super.edit(adjunto);
        } else {
            throw new SIAException(SiAdjuntoImpl.class.getName(), "save()",
                    "Faltan parámetros para poder guardar el adjunto",
                    ("Parámetros: adjunto: " + (adjunto != null ? adjunto.getId() : null)
                    + "idUsuario" + idUsuario));
        }

        UtilLog4j.log.info(this, "SiAdjunto DELETED SUCCESSFULLY");

        return adjunto;
    }

    
    public void eliminarArchivo(SiAdjunto archivo, String idUsuario, boolean estado) {
        UtilLog4j.log.info(this, "SiAdjuntoImpl.eliminarArchivo()");
        if (archivo == null) {
            UtilLog4j.log.info(this, "El archivo adjunto que intentas eliminar no existe.");
        } else {
            archivo.setGenero(new Usuario(idUsuario));
            archivo.setEliminado(estado);
            archivo.setFechaGenero(new Date());
            archivo.setHoraGenero(new Date());

            super.edit(archivo);
        }
    }

    
    public boolean validarExistenciaArchivo(String nombre, String url) {
        UtilLog4j.log.info(this, "= Validad Existencia =");
        UtilLog4j.log.info(this, "Nombre a buscar : " + nombre);
        UtilLog4j.log.info(this, "URL a buscar : " + url);

        try {
            Integer encontradoCount = (Integer) em.createNativeQuery("SELECT count(id) "
                    + " FROM SI_ADJUNTO "
                    + " WHERE URL = '" + url + "' "
                    + " AND nombre = '" + nombre + "' "
                    + " AND eliminado = '" + Constantes.BOOLEAN_FALSE + "'").getSingleResult();
            if (encontradoCount > 0) {
                //encontrado en la DB
                return true;
            } else {
                return false;
            }

        } catch (Exception e) {
            UtilLog4j.log.info(this, "Error al buscar la existencia del archivo " + e.getMessage());
            return false;
        }
    }

    
    public SiAdjunto find(Integer id, String Uuid) {
        return this.obtenerAdjunto(id, Uuid, Constantes.BOOLEAN_FALSE);
    }

    /**
     *
     * @param id
     * @param Uuid
     * @return
     */
    
    public SiAdjunto obtenerAdjunto(Integer id, String Uuid) {

        return this.obtenerAdjunto(id, Uuid, false);

    }

    /**
     *
     * @param id
     * @param Uuid
     * @param eliminado
     * @return
     */
    private SiAdjunto obtenerAdjunto(Integer id, String Uuid, boolean eliminado) {
        SiAdjunto result;
        try {

            StringBuilder sb = new StringBuilder();

            sb.append("SELECT s FROM SiAdjunto s ");
            sb.append("WHERE 1=1 ");

            if (eliminado) {
                sb.append("and s.eliminado = :falso ");
            }

            sb.append("and s.uuid = :idLlave ");
            sb.append("and s.id = :IdAdjunto ");

            Query findQuery = em.createQuery(sb.toString());

            if (eliminado) {
                findQuery.setParameter("falso", eliminado);
            }
            findQuery.setParameter("idLlave", Uuid);
            findQuery.setParameter("IdAdjunto", id);
            result = (SiAdjunto) findQuery.getSingleResult();

        } catch (NoResultException e) {
            result = null;
        } catch (Exception e) {
            result = null;
        }
        return result;
    }

    
    public SiAdjunto find(Integer id, String Uuid, boolean isElement) {
        SiAdjunto result = null;
        try {
            if (isElement) {
                result = (SiAdjunto) em.createQuery("SELECT s FROM SiAdjunto s WHERE s.eliminado = :falso and s.uuid = :idLlave and s.idElemento = :IdElemento").setParameter("falso", Constantes.BOOLEAN_FALSE).setParameter("IdElemento", id).setParameter("idLlave", Uuid).getSingleResult();
            } else {
                result = (SiAdjunto) em.createQuery("SELECT s FROM SiAdjunto s WHERE s.eliminado = :falso and s.uuid = :idLlave and s.id = :IdAdjunto").setParameter("falso", Constantes.BOOLEAN_FALSE).setParameter("IdAdjunto", id).setParameter("idLlave", Uuid).getSingleResult();
            }

        } catch (NoResultException e) {
            result = null;
        } catch (Exception e) {
            result = null;
        }
        return result;
    }

    
    public List<SiAdjunto> findAllNotUUID() {
        return em.createQuery("SELECT s FROM SiAdjunto s WHERE s.uuid is null").getResultList();
    }

    
    public AdjuntoVO buscarArchivo(int id, String uuid) {
        AdjuntoVO retVal = null;

        String sql
                = "select a.id, a.nombre, a.url, a.tipo_archivo, a.peso, a.uuid from SI_ADJUNTO a"
                + " where a.ID = ? "
                + " and a.UUID = ? "
                + " and a.ELIMINADO =  ? ";

        Object[] objects
                = (Object[]) em.createNativeQuery(sql)
                        .setParameter(1, id)
                        .setParameter(2, uuid)
                        .setParameter(3, Constantes.NO_ELIMINADO)
                        .getSingleResult();

        if (objects != null) {
            retVal = castAdjunto(objects);
        }

        return retVal;
    }

    
    public AdjuntoVO buscarArchivoOficio(int id, String uuid, int idOficio) {
        try {
            clearQuery();
            query.append("select a.id, a.nombre, a.url, a.tipo_archivo, a.peso, a.uuid from SI_ADJUNTO a ");
            query.append("inner join OF_OFICIO_SI_MOV_SI_ADJUNTO mov_adj on(mov_adj.SI_ADJUNTO = a.ID) ");
            query.append("inner join OF_OFICIO_SI_MOVIMIENTO mov on(mov.ID = mov_adj.OF_OFICIO_SI_MOVIMIENTO) ");
            query.append("inner join OF_OFICIO of1 on ( of1.id = mov.OF_OFICIO) ");
            query.append(" where a.ID =").append(id);
            query.append(" and a.UUID = '").append(uuid).append("'");
            query.append(" and a.ELIMINADO = '").append(Constantes.NO_ELIMINADO).append("'");
            query.append(" and a.ELIMINADO = '").append(Constantes.NO_ELIMINADO).append("' ");
            // CO_PRIVACIDAD 5 = RESTRINGIDO
            query.append("and of1.CO_PRIVACIDAD <> 5");
            query.append(" and of1.ID = ").append(idOficio);
            Object[] objects = (Object[]) em.createNativeQuery(query.toString()).getSingleResult();
            if (objects != null) {
                return castAdjunto(objects);
            }
        } catch (NoResultException e) {
            UtilLog4j.log.error(this, e);
        }
        return null;
    }

    private AdjuntoVO castAdjunto(Object[] objects) {
        AdjuntoVO adjuntoVO = new AdjuntoVO();
        adjuntoVO.setId((Integer) objects[0]);
        adjuntoVO.setNombre((String) objects[1]);
        adjuntoVO.setUrl((String) objects[2]);
        adjuntoVO.setTipoArchivo((String) objects[3]);
        adjuntoVO.setPeso((String) objects[4]);
        adjuntoVO.setUuid((String) objects[5]);
        return adjuntoVO;
    }

    
    public void eliminarArchivo(int idAdjunto, String sesion) {
        UtilLog4j.log.info(this, "SiAdjuntoImpl.eliminarArchivo()");
        SiAdjunto archivo = find(idAdjunto);
        if (archivo != null) {
            try {
                if (archivo.getUrl() != null) {
                    almacenDocumentos.getAlmacenDocumentos().borrarDocumento(archivo.getUrl());
                }
                archivo.setModifico(new Usuario(sesion));
                archivo.setEliminado(Constantes.ELIMINADO);
                archivo.setFechaModifico(new Date());
                archivo.setHoraModifico(new Date());
                edit(archivo);
            } catch (SIAException ex) {
                Logger.getLogger(SiAdjuntoImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            UtilLog4j.log.info(this, "El archivo adjunto que intentas eliminar es null");
        }
    }

    
    public AdjuntoVO buscarArchivo(int idAdjunto) {
        clearQuery();
        query.append("select a.id, a.nombre, a.url, a.tipo_archivo, a.peso, a.uuid from SI_ADJUNTO a");
        query.append(" where a.ID =").append(idAdjunto);
        query.append(" and a.ELIMINADO = '").append(Constantes.NO_ELIMINADO).append("'");
        Object[] objects = (Object[]) em.createNativeQuery(query.toString()).getSingleResult();
        if (objects != null) {
            return castAdjunto(objects);
        }
        return null;
    }

}
