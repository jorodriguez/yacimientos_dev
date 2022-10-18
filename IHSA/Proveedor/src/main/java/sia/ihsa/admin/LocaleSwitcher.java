/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.ihsa.admin;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.annotation.PostConstruct;
import javax.faces.application.Application;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.IteratorUtils;

/**
 *
 * @author mluis
 */
@ManagedBean
@SessionScoped
public class LocaleSwitcher implements Serializable {

    private static final long serialVersionUID = 84157941310458440L;

    /**
     * Creates a new instance of LocaleSwitcher
     */
    public LocaleSwitcher() {
    }
    private Locale locale;

    private List<Locale> supportedLocales;

    @PostConstruct
    protected void init() {
        //locale =  FacesContext.getCurrentInstance().getApplication().getDefaultLocale();  //
        locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
        this.supportedLocales = IteratorUtils.toList(FacesContext.getCurrentInstance().getApplication().getSupportedLocales());
    }

    public Locale getLocale() {
        if (locale == null) {
            locale = FacesContext.getCurrentInstance().getViewRoot()
                    .getLocale();
        }
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public void localeChangeListener(ValueChangeEvent changeEvent) {
        locale = new Locale(changeEvent.getNewValue().toString());
        //FacesContext.getCurrentInstance().getApplication().setDefaultLocale(locale);
        FacesContext.getCurrentInstance().getViewRoot().setLocale(locale);
    }

    public List<Locale> getSupportedLocales() {
        return supportedLocales;
    }

    public String getSelectedLocale() {
        return getLocale().toString();
    }

    public void setSelectedLocale(String localeString) {
        Application application = FacesContext.getCurrentInstance()
                .getApplication();
        Iterator supportdLocales = application.getSupportedLocales();
        while (supportdLocales.hasNext()) {
            Locale lcl = (Locale) supportdLocales.next();
            if (lcl.toString().equals(localeString)) {
                this.locale = lcl;
            }
        }
    }
}
