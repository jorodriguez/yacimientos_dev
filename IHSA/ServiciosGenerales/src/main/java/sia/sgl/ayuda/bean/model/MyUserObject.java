/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.sgl.ayuda.bean.model;


import javax.swing.tree.DefaultMutableTreeNode;

/**
 * The MyUserObject object is responsible for storing extra data
 * for a url.  The url along with text is bound to a ice:commanLink object which
 * will launch a new browser window pointed to the url.
 */
public class MyUserObject {

    // url to show when a node is clicked
    private String url;
    private Integer idArchivo;
    private String uuid;
    private boolean selected;

    public MyUserObject(DefaultMutableTreeNode wrapper) {
       // super(wrapper);
    }

    /**
     * Gets the url value of this IceUserObject.
     *
     * @return string representing a URL.
     */
    public String getUrl() {
        return url;
    }

    /**
     * Sets the URL.
     *
     * @param url a valid URL with protocol information such as
     *            http://icesoft.com
     */
    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    /**
     * @return the idArchivo
     */
    public Integer getIdArchivo() {
        return idArchivo;
    }

    /**
     * @param idArchivo the idArchivo to set
     */
    public void setIdArchivo(Integer idArchivo) {
        this.idArchivo = idArchivo;
    }

    /**
     * @return the uuid
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * @param uuid the uuid to set
     */
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

}
