package sia.catalogos.bean.model;

import java.io.Serializable;
import java.util.List;
import javax.inject.Inject;


import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import sia.excepciones.ExistingItemException;
import sia.excepciones.ItemUsedBySystemException;
import sia.modelo.RhPuesto;
import sia.modelo.puesto.vo.RhPuestoVo;
import sia.servicios.campo.nuevo.impl.RhPuestoImpl;
import sia.sistema.bean.backing.Sesion;
import sia.util.UtilLog4j;

/**
 *
 * @author rluna
 */
@Named(value = "puestosBeanModel")
@ViewScoped
public class PuestosBeanModel implements Serializable {
    //Sistema

    @Inject
    private Sesion sesion;
    //Servicios
    @Inject
    private RhPuestoImpl rhPuestoImpl;
    //Entidad
    private RhPuesto rhPuesto;
    //Clases
    private RhPuestoVo rhPuestoVo;
    //Colecciones
    private DataModel dataModel;
    private List<SelectItem> selectItemList;
    //Primitivos
    private String nombrePuesto;
    private String descripcionPuesto;
    private String cadenaBuscar;
    private String opcionSeleccionada;
    private int idPuesto;
    

    public PuestosBeanModel() {
    }
/**
 * Inicio conversacion con el catalogo puestos
 */

    
    public void controlaPopUpFalso(String llave) {
        sesion.getControladorPopups().put(llave, Boolean.FALSE);
    }

    public void controlaPopUpTrue(String llave) {
        sesion.getControladorPopups().put(llave, Boolean.TRUE);
    }


    /**
     * Comienza RhPuesto
     */
    public RhPuesto getRhPuestoById(int idRhPuesto) {
        return this.rhPuestoImpl.find(idRhPuesto);
    }

    public void getAllRhPuesto() {
        if (this.dataModel == null) {
            this.dataModel = (new ListDataModel(this.getRhPuestoImpl().findAllRhPuesto("nombre", true, true)));
        }
    }

    public void reloadAllRhPuesto() {
        this.dataModel = (new ListDataModel(this.getRhPuestoImpl().findAllRhPuesto("nombre", true, false)));
    }

    public List<RhPuestoVo> getAllRhPuestoList() {
        return this.rhPuestoImpl.findAllRhPuesto("nombre", true, true);
    }

    public void saveRhPuesto() throws ExistingItemException {

        this.rhPuestoImpl.savePuesto(this.sesion.getUsuario().getId(), getNombrePuesto(), getDescripcionPuesto());
        reloadAllRhPuesto();

    }

    public void updateRhPuesto() throws ExistingItemException {
        UtilLog4j.log.info(this, "PuestosBeanModel.update()");
        this.rhPuestoImpl.updatePuesto(getNombrePuesto(), this.sesion.getUsuario().getId(), getDescripcionPuesto(), getRhPuestoVo().getId());
        buscarRhPuesto();
    }

    public void deleteRhPuesto() throws ItemUsedBySystemException {
        this.rhPuestoImpl.deletePuesto(rhPuestoVo.getId(), this.sesion.getUsuario().getId(), this.getNombrePuesto());
        reloadAllRhPuesto();

    }
    
    public void buscarRhPuesto(){
     this.dataModel = (new ListDataModel(this.rhPuestoImpl.getRhPuestoLike(cadenaBuscar)));
    }

    public DataModel getPuestosDataModel() {
        return this.getDataModel();
    }

    /**
     * @return the nombrePuesto
     */
    public String getNombrePuesto() {
        return nombrePuesto;
    }

    /**
     * @param nombrePuesto the nombrePuesto to set
     */
    public void setNombrePuesto(String nombrePuesto) {
        this.nombrePuesto = nombrePuesto;
    }

    /**
     * @return the descripcionPuesto
     */
    public String getDescripcionPuesto() {
        return descripcionPuesto;
    }

    /**
     * @param descripcionPuesto the descripcionPuesto to set
     */
    public void setDescripcionPuesto(String descripcionPuesto) {
        this.descripcionPuesto = descripcionPuesto;
    }

    /**
     * @return the idPuesto
     */
    public int getIdPuesto() {
        return idPuesto;
    }

    /**
     * @param idPuesto the idPuesto to set
     */
    public void setIdPuesto(int idPuesto) {
        this.idPuesto = idPuesto;
    }

    /**
     * @param sesion the sesion to set
     */
    public void setSesion(Sesion sesion) {
        this.sesion = sesion;
    }

    /**
     * @return the rhPuestoImpl
     */
    public RhPuestoImpl getRhPuestoImpl() {
        return rhPuestoImpl;
    }

    /**
     * @param rhPuestoImpl the rhPuestoImpl to set
     */
    public void setRhPuestoImpl(RhPuestoImpl rhPuestoImpl) {
        this.rhPuestoImpl = rhPuestoImpl;
    }

    /**
     * @return the rhPuesto
     */
    public RhPuesto getRhPuesto() {
        return rhPuesto;
    }

    /**
     * @param rhPuesto the rhPuesto to set
     */
    public void setRhPuesto(RhPuesto rhPuesto) {
        this.rhPuesto = rhPuesto;
    }

    /**
     * @return the dataModel
     */
    public DataModel getDataModel() {
        return dataModel;
    }

    /**
     * @param dataModel the dataModel to set
     */
    public void setDataModel(DataModel dataModel) {
        this.dataModel = dataModel;
    }

    /**
     * @return the selectItemList
     */
    public List<SelectItem> getSelectItemList() {
        return selectItemList;
    }

    /**
     * @param selectItemList the selectItemList to set
     */
    public void setSelectItemList(List<SelectItem> selectItemList) {
        this.selectItemList = selectItemList;
    }

    /**
     * @return the rhPuestoVo
     */
    public RhPuestoVo getRhPuestoVo() {
        return rhPuestoVo;
    }

    /**
     * @param rhPuestoVo the rhPuestoVo to set
     */
    public void setRhPuestoVo(RhPuestoVo rhPuestoVo) {
        this.rhPuestoVo = rhPuestoVo;
    }

    /**
     * @return the cadenaBuscar
     */
    public String getCadenaBuscar() {
        return cadenaBuscar;
    }

    /**
     * @param cadenaBuscar the cadenaBuscar to set
     */
    public void setCadenaBuscar(String cadenaBuscar) {
        this.cadenaBuscar = cadenaBuscar;
    }

    /**
     * @return the opcionSeleccionada
     */
    public String getOpcionSeleccionada() {
        return opcionSeleccionada;
    }

    /**
     * @param opcionSeleccionada the opcionSeleccionada to set
     */
    public void setOpcionSeleccionada(String opcionSeleccionada) {
        this.opcionSeleccionada = opcionSeleccionada;
    }


  
}
