/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.comunicacion.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.ejb.LocalBean;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.constantes.Constantes;
import sia.modelo.SiCodificacion;
import sia.modelo.Usuario;
import sia.modelo.sistema.AbstractFacade;
import sia.util.UtilLog4j;

/**
 *
 * @author jorodriguez
 */
@LocalBean 
public class SiCodificacionImpl extends AbstractFacade<SiCodificacion> {

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SiCodificacionImpl() {
        super(SiCodificacion.class);
    }

    
    public boolean createCodificacion(SiCodificacion siCodificacion, Usuario usuario) {
        UtilLog4j.log.info(this,"createCodificacion en implementacion");
        try {
            siCodificacion.setValorActual(0);
            siCodificacion.setModificar(Constantes.BOOLEAN_TRUE);
            siCodificacion.setGenero(usuario);
            siCodificacion.setFechaGenero(new Date());
            siCodificacion.setHoraGenero(new Date());
            siCodificacion.setEliminado(Constantes.BOOLEAN_FALSE);
            create(siCodificacion);
            UtilLog4j.log.info(this,"creata ok");
            UtilLog4j.log.info(this,"Todo bien al guardar sgCodificacion");
            return true;
        } catch (Exception e) {
            UtilLog4j.log.fatal(this,"Excepcion en crear Codificacion " + e.getMessage());
            return false;
        }
    }

    
    public boolean updateCodificacion(SiCodificacion siCodificacion, Usuario usuarioModifico) {
        try {
            siCodificacion.setModifico(usuarioModifico);
            siCodificacion.setFechaModifico(new Date());
            siCodificacion.setHoraModifico(new Date());
            siCodificacion.setEliminado(Constantes.BOOLEAN_FALSE);
            edit(siCodificacion);
            UtilLog4j.log.info(this,"Todo bien al modificar sgCodificacion");
            return true;
        } catch (Exception e) {
            UtilLog4j.log.fatal(this,"Excepcion en modificar Codificacion" + e.getMessage());
            return false;
        }
    }

    
    public boolean deleteCodificacion(SiCodificacion siCodificacion, Usuario usuarioModifico) {
        try {
            siCodificacion.setEliminado(Constantes.BOOLEAN_TRUE);
            edit(siCodificacion);
            UtilLog4j.log.info(this,"Todo bien al eliminar sgCodificacion");
            return true;
        } catch (Exception e) {
            UtilLog4j.log.fatal(this,"Excepcion en eliminar Codificacion" + e.getMessage());
            return false;
        }
    }

    
    public List<SiCodificacion> findAllCodificacion(Usuario usuarioSesion) {
        try {
            return em.createQuery("SELECT c FROM SiCodificacion c "
                    + " WHERE c.genero =:usuarioSesion AND c.eliminado = :eli ORDER BY c.id ASC").setParameter("usuarioSesion", usuarioSesion).setParameter("eli", Constantes.BOOLEAN_FALSE).getResultList();
        } catch (Exception e) {
            UtilLog4j.log.fatal(this,"Excepcion en la consulta de codificaciones " + e.getMessage());
            return null;
        }
    }

    private String getDigitosAño(Date fecha) {
        SimpleDateFormat SDF = new SimpleDateFormat("dd/MM/yyyy");
        String Cadena = SDF.format(fecha);
        String Resultado = "";
        for (int i = 0; i < Cadena.length(); i++) {
            if (i > 7) {
                Resultado = Resultado + Cadena.charAt(i);
            }
        }
        return Resultado;
    }

    
    public String getCodigo(SiCodificacion siCodificacion, Usuario usuario) {
        UtilLog4j.log.info(this,"getCodigo para la codificacion " + siCodificacion.getLetras());
        String code = "";
        try {
           
//            if (siCodificacion.getValorActual() > siCodificacion.getInicio()) {
                UtilLog4j.log.info(this,"es mayor el valor actual que el valor de inicio");
                if (siCodificacion.getIntegraAno().equals(true)) {
                    UtilLog4j.log.info(this,"Si integra año");
                    siCodificacion.setValorActual(siCodificacion.getValorActual() + 1);
                    edit(siCodificacion);
                    String anoActual = getDigitosAño(new Date());
                    code = siCodificacion.getLetras() + anoActual +"-"+siCodificacion.getValorActual();
//                } else {
//                    UtilLog4j.log.info(this,"NO es mayor el valor actual que el valor de inicio");
//                    if (siCodificacion.getIntegraAno().equals(true)) {
//                        UtilLog4j.log.info(this,"Si integra año");
//                        siCodificacion.setValorActual(siCodificacion.getValorActual() + 1);
//                        edit(siCodificacion);
//                        String anoActual = getDigitosAño(new Date());
//                        code = siCodificacion.getLetras() + anoActual + siCodificacion.getValorActual();
//                    }
//                }
            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(this,"Excepcion en getCodigo() " + e.getMessage());

        }
        UtilLog4j.log.info(this,"Codificacion " + code);
        return code;
    }

    
    public void ponerUsada(SiCodificacion siCodificacion, Usuario usuario) {
       try {
            siCodificacion.setModifico(usuario);
            siCodificacion.setFechaModifico(new Date());
            siCodificacion.setHoraModifico(new Date());
            siCodificacion.setModificar(Constantes.BOOLEAN_FALSE);
            edit(siCodificacion);
            UtilLog4j.log.info(this,"Todo bien al Modificar sgCodificacion");
        } catch (Exception e) {
            UtilLog4j.log.fatal(this,"Excepcion en Modificar Codificacion" + e.getMessage());
        } 
    }
}
