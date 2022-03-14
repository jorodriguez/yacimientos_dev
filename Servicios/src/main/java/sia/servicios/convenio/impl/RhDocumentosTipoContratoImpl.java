/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.convenio.impl;

import java.util.Date;
import java.util.List;
import javax.ejb.LocalBean;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.modelo.CvClasificacion;
import sia.modelo.RhDocumentos;
import sia.modelo.RhDocumentosTipoContrato;
import sia.modelo.Usuario;
import sia.modelo.sistema.AbstractFacade;

/**
 *
 * @author mluis
 */
@LocalBean 
public class RhDocumentosTipoContratoImpl extends AbstractFacade<RhDocumentosTipoContrato> {

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public RhDocumentosTipoContratoImpl() {
        super(RhDocumentosTipoContrato.class);
    }
    
    
    public void guardar(String sesion, int idDocumento, int idClasConv){
        RhDocumentosTipoContrato rdtc = new RhDocumentosTipoContrato();
        rdtc.setCvClasificacion(new CvClasificacion(idClasConv));
        rdtc.setRhDocumentos(new  RhDocumentos(idDocumento));
        rdtc.setGenero(new Usuario(sesion));
        rdtc.setFechaGenero(new Date());
        rdtc.setHoraGenero(new Date());
        rdtc.setEliminado(Boolean.FALSE);
        //
        create(rdtc);        
    }
    
    
    public List<RhDocumentosTipoContrato> traerPorClasificacion(int idClasConv){
        String c = "select * from rh_documentos_tipo_contrato dtc where dtc.cv_clasificacion = " + idClasConv + " and dtc.eliminado = false";
        //
        return em.createNativeQuery(c, RhDocumentosTipoContrato.class).getResultList();
    }
    
}
