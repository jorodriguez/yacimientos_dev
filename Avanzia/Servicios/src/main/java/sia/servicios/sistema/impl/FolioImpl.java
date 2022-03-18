/*
 * FolioImpl.java
 * Creado el 7/07/2009, 08:47:52 AM
 * EJB sin estado desarrollado por: Héctor Acosta Sierra para: MPG-IHSA
 *
 * Para información sobre el uso de este EJB sin estado (Stateless Session EJB), asi como bugs, actualizaciones o mejoras
 * enviar un mail a: hacosta@mpg-ihsa.com.mx o a: hacosta.0505@gmail.com
 */
package sia.servicios.sistema.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Formatter;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.modelo.Folio;

/**
 *
 * @author Héctor Acosta Sierra
 * @version 1.0
 * @author-mail hacosta.0505@gmail.com @date 7/07/2009
 */
@Stateless 
public class FolioImpl{

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    public void create(Folio folio) {
        em.persist(folio);

    }

    
    public void edit(Folio folio) {
        em.merge(folio);
    }

    
    public void remove(Folio folio) {
        em.remove(em.merge(folio));
    }

    
    public Folio find(Object id) {
        return em.find(Folio.class, id);
    }

    
    public List<Folio> findAll() {
        return em.createQuery("select object(o) from Folio as o").getResultList();
    }

    
    public int getFolio(Object nombreComprobante) {
        Folio folio = (Folio) em.createQuery("SELECT f FROM Folio f WHERE f.comprobante = :comprobante")
                .setParameter("comprobante", nombreComprobante)
                .getSingleResult();
        // aqui checar el año si es igual suma el consecutivo si es diferente inicializa el consecutivo a 0
        if (folio.getAno() != null) {
            String anoActual = getDigitosAño(new Date());
            if (anoActual.equals(folio.getAno())) {
            } else {
                folio.setAno(anoActual);
                folio.setValor(0);
            }
        }
        //--Obtengo el ultimo valor y le sumo uno
//        int nuevoValor = folio.getValor() + 1;
        //---Actualizo el nuevo valor
        folio.setValor(folio.getValor() + 1);
        edit(folio);
        //-----------------------------------------
        return folio.getValor();
    }

    private String getDigitosAño(Date fecha) {
        SimpleDateFormat SDF = new SimpleDateFormat("dd/MM/yyyy");
        String cadena = SDF.format(fecha);
        String resultado = "";
        for (int i = 0; i < cadena.length(); i++) {
            if (i > 7) {
                resultado = resultado + cadena.charAt(i);
            }
        }
        return resultado;
    }

//    public String getAno(Object nombreComprobante) {
//        Folio folio = (Folio) em.createQuery("SELECT f FROM Folio f WHERE f.comprobante = :comprobante").setParameter("comprobante", nombreComprobante).getSingleResult();
//        return folio.getAno();
//    }
    /**
     * 25/03/2013 Nuevo metodo para retornar el comprobante de un folio por
     * nombre de comprobante y su campo. (Actualmente esta usado para crear los
     * consecutivos para Requisiciones y Ordenes de compra..)
     *
     * @param nombreComprobante -- es el nombre del comprobante
     * @param idApCampo -- es el campo al cual pertenece
     * @return el consecutivo
     */
    
    public String getFolio(Object nombreComprobante, Integer idApCampo) {
        Folio folio = (Folio) em.createQuery("SELECT f FROM Folio f WHERE f.comprobante = :comprobante AND f.apCampo.id = :idApCampo").setParameter("comprobante", nombreComprobante).setParameter("idApCampo", idApCampo).getSingleResult();
        if (folio.getAno() != null) {
            String anoActual = getDigitosAño(new Date());
            if (anoActual.equals(folio.getAno())) {
            } else {
                folio.setAno(anoActual);
                folio.setValor(0);
            }
        }
        //---Actualizo el nuevo valor
        folio.setValor(folio.getValor() + 1);
        edit(folio);
        //-----------------------------------------
        return ((folio.getPrefijo() != null ? folio.getPrefijo() : "") + folio.getAno() + "-" + Integer.toString(folio.getValor()));
    }

    
    public String traerAnioFolio(String nombreComprobante, int idApCampo) {
        Folio folio = (Folio) em.createQuery("SELECT f FROM Folio f WHERE f.comprobante = :comprobante AND f.apCampo.id = :idApCampo").setParameter("comprobante", nombreComprobante).setParameter("idApCampo", idApCampo).getSingleResult();
        if (folio.getAno() != null) {
            String anoActual = getDigitosAño(new Date());
            if (anoActual.equals(folio.getAno())) {
            } else {
                folio.setAno(anoActual);
                folio.setValor(0);
            }
        }
        //---Actualizo el nuevo valor
        folio.setValor(folio.getValor() + 1);
        edit(folio);
        //-----------------------------------------
        return (folio.getAno() + "-" + Integer.toString(folio.getValor()));
    }

    
    public String traerFolioAnio(String nombreComprobante, int idApCampo) {
        Folio folio = (Folio) em.createQuery("SELECT f FROM Folio f WHERE f.comprobante = :comprobante AND f.apCampo.id = :idApCampo").setParameter("comprobante", nombreComprobante).setParameter("idApCampo", idApCampo).getSingleResult();
        if (folio.getAno() != null) {
            String anoActual = getDigitosAño(new Date());
            if (anoActual.equals(folio.getAno())) {
            } else {
                folio.setAno(anoActual);
                folio.setValor(0);
            }
        }
        //---Actualizo el nuevo valor
        folio.setValor(folio.getValor() + 1);
        edit(folio);
        //-----------------------------------------
        String val = String.valueOf(folio.getValor());
        if (val.length() == 1) {
            val = "00" + val;
        } else if (val.length() == 2) {
            val = "0" + val;
        }
        return (val + "-" + (folio.getPrefijo().isEmpty() ? folio.getAno() : (folio.getAno() + "-" + folio.getPrefijo())));
    }

    
    public int traerFolioPorCampo(String nombreComprobante, int idApCampo) {
        Folio folio = (Folio) em.createQuery("SELECT f FROM Folio f WHERE f.comprobante = :comprobante AND f.apCampo.id = :idApCampo").setParameter("comprobante", nombreComprobante).setParameter("idApCampo", idApCampo).getSingleResult();
        //---Actualizo el nuevo valor
        folio.setValor(folio.getValor() + 1);
        edit(folio);
        return folio.getValor();
    }

    
    public int traerFolio(String nombreComprobante) {
        try {
            Folio folio = (Folio) em.createQuery("SELECT f FROM Folio f WHERE f.comprobante = :comprobante and f.eliminado = 'False'", Folio.class)
                    .setParameter("comprobante", nombreComprobante)
                    .getSingleResult();
            //---Actualizo el nuevo valor
            folio.setValor(folio.getValor() + 1);
            edit(folio);
            return folio.getValor();
        } catch (Exception e) {
            return 0;
        }
    }

    
    public String traerFolioMesAnio(Object nombreComprobante, Integer idApCampo) {
        Folio folio = (Folio) em.createQuery("SELECT f FROM Folio f WHERE f.comprobante = :comprobante AND f.apCampo.id = :idApCampo").setParameter("comprobante", nombreComprobante).setParameter("idApCampo", idApCampo).getSingleResult();
        if (folio.getAno() != null) {
            String anoActual = getDigitosAño(new Date());
            if (anoActual.equals(folio.getAno())) {
            } else {
                folio.setAno(anoActual);
                folio.setValor(0);
            }
        }
        //---Actualizo el nuevo valor
        folio.setValor(folio.getValor() + 1);
        edit(folio);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String mes = sdf.format(new Date()).substring(3, 5);
        //-----------------------------------------
        return ((folio.getPrefijo() != null ? folio.getPrefijo() : "") + mes + folio.getAno() + "-" + Integer.toString(folio.getValor()));
    }

    
    public String getFolioSinCampo(String comprobante, int cerosIzq) {
        Folio folio = (Folio) em.createQuery("SELECT f FROM Folio f WHERE f.comprobante = :comprobante and f.eliminado = false")
                .setParameter("comprobante", comprobante)
                .getSingleResult();
        if (folio.getAno() != null) {
            String anoActual = getDigitosAño(new Date());
            if (anoActual.equals(folio.getAno())) {
            } else {
                folio.setAno(anoActual);
                folio.setValor(0);
            }
        }
        //---Actualizo el nuevo valor
        folio.setValor(folio.getValor() + 1);
        edit(folio);
        //-----------------------------------------
        Formatter obj = new Formatter();
        return ((folio.getPrefijo() != null ? folio.getPrefijo() : "") + folio.getAno() + "-" + String.valueOf(obj.format("%0" + cerosIzq + "d", folio.getValor())));
    }

}
