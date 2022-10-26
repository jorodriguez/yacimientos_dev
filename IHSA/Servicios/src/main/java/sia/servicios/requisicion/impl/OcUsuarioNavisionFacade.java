/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sia.servicios.requisicion.impl;

import java.time.LocalDate;
import java.util.Date;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import javax.inject.Inject;
import sia.modelo.OcUsuarioNavision;
import sia.modelo.Usuario;
import sia.servicios.sistema.impl.FolioImpl;

/**
 *
 * @author efectiva
 */
@Stateless
public class OcUsuarioNavisionFacade extends AbstractFacade<OcUsuarioNavision> {

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public OcUsuarioNavisionFacade() {
        super(OcUsuarioNavision.class);
    }

    public List<String> traerUsuarios(String nombre) {
        return em.createNativeQuery("select NOMBRE from OC_USUARIO_NAVISION"
                + " where upper(NOMBRE) like '%" + nombre.toUpperCase() + "%'"
                + " and ELIMINADO = false").getResultList();
    }

    public OcUsuarioNavision buscarPorNombre(String usuarioBeneficiado) {
        try {
            return (OcUsuarioNavision) em.createNativeQuery("select *  from OC_USUARIO_NAVISION "
                    + " where upper(NOMBRE) = '" + usuarioBeneficiado.toUpperCase() + "'"
                    + " and ELIMINADO = false ").getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public void agregarUsuario(String sesion, String usurioBeneficiado) {
        if (buscarPorNombre(usurioBeneficiado) == null) {
            String[] cad = usurioBeneficiado.split(" ");
            String c = "";
            if (cad[0].length() > 10) {
                c = cad[0].substring(0, 10);
            } else {
                c = cad[0];
            }
            OcUsuarioNavision un = new OcUsuarioNavision();
            un.setRfc(c.toUpperCase() + LocalDate.now().getYear());
            un.setNombre(usurioBeneficiado);
            un.setNombre(usurioBeneficiado);
            un.setNavision(Boolean.FALSE);
            un.setGenero(new Usuario(sesion));
            un.setFechaGenero(new Date());
            un.setHoraGenero(new Date());
            un.setEliminado(Boolean.FALSE);
            //
            create(un);

        }
    }

}
