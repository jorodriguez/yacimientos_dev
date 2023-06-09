/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package lector.sistema.bean.model;

import java.io.Serializable;
import java.util.List;
import javax.faces.model.DataModel;

/**
 *
 * @author hacosta
 */
public abstract class ListModel {
  private boolean sortAscending = true;
    private boolean checkAll = false;
    private boolean checkAllRows = false;
    private String sortColumn = getDefaultSortColumn();
    private String estatus = "Pendiente";
    private Serializable selected;
    // Atributos para las lineas
    private Serializable rowSelected;
    //-- Metodos ---
    public abstract DataModel getDataModel();
    public abstract String getDefaultSortColumn();
    public abstract void clearDataModel();
    public abstract void checkAll();
    public abstract void uncheckAll();
    public abstract void deleteChecked();
    public abstract boolean isNew(Serializable transfer);
    public abstract Serializable nuevo();
    public abstract void create(Serializable transfer);
    public abstract void update(Serializable transfer);
    //--- Metodos para las lineas
    public abstract DataModel getRowsDataModel();
    public abstract void clearRowsDataModel();
    public abstract void checkAllRows();
    public abstract void uncheckAllRows();
    public abstract void deleteRowsChecked();
    public abstract boolean isNewRow(Serializable transfer);
    public abstract Serializable newRow();
    public abstract void createRow(Serializable transfer);
    public abstract void updateRow(Serializable transfer);
    //--- Inicia clase
    public <T> List<T> getDataModelAsList() {
        return (List<T>) getDataModel().getWrappedData();
    }

    public <T> List<T> getRowsDataModelAsList(){
       return (List<T>) getRowsDataModel().getWrappedData();
    }

    /**
     * @return the sortColumn
     */
    public String getSortColumn() {
        return sortColumn;
    }

    /**
     * @param sortColumn the sortColumn to set
     */
    public void setSortColumn(String sortColumn) {
        if (!this.sortColumn.equals(sortColumn)) {
            clearDataModel();
            this.sortColumn = sortColumn;
        }
    }

    /**
     * @return the sortAscending
     */
    public boolean isSortAscending() {
        return sortAscending;
    }

    /**
     * @param sortAscending the sortAscending to set
     */
    public void setSortAscending(boolean sortAscending) {
        if (this.sortAscending != sortAscending) {
            clearDataModel();
        }
        this.sortAscending = sortAscending;
    }

    /**
     * @return the estatus
     */
    public String getEstatus() {
        return estatus;
    }

    /**
     * @param estatus the estatus to set
     */
    public void setEstatus(String estatus) {
        if (!this.estatus.equals(estatus)) {
            clearDataModel();
            this.estatus = estatus;
        }
    }

    public void saveSelected() {
        if (isNew(this.getSelected())) {
            create(this.getSelected());
        } else {
            update(this.getSelected());
        }
        clearDataModel();
    }

    public void saveRowSelected(){
        if (isNewRow(this.getRowSelected())) {
            createRow(this.getRowSelected());
        } else {
            updateRow(this.getRowSelected());
        }
        clearRowsDataModel();
    }

    //este metodo lo utilizo para pasar un nuevo registro
    public void setSelectedAsNew() {
        setSelected(nuevo());
    }

    public void setRowSelectedAsNew(){
        setRowSelected(newRow());
    }
    /**
     * @return the check
     */
    public boolean isCheckAll() {
        return checkAll;
    }

    /**
     * @param check the check to set
     */
    public void setCheckAll(boolean check) {
        this.checkAll = check;
    }


    /**
     * @return the selected
     */
    public Serializable getSelected() {
        return selected;
    }

    /**
     * @param selected the selected to set
     */
    public void setSelected(Serializable selected) {
        this.selected = selected;
    }

    /**
     * @return the rowSelected
     */
    public Serializable getRowSelected() {
        return rowSelected;
    }

    /**
     * @param rowSelected the rowSelected to set
     */
    public void setRowSelected(Serializable rowSelected) {
        this.rowSelected = rowSelected;
    }

    /**
     * @return the checkAllRows
     */
    public boolean isCheckAllRows() {
        return checkAllRows;
    }

    /**
     * @param checkAllRows the checkAllRows to set
     */
    public void setCheckAllRows(boolean checkAllRows) {
        this.checkAllRows = checkAllRows;
    }
}
