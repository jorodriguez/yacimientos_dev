/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mx.ihsa.sistema.converters;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;
import javax.inject.Inject;
import javax.inject.Named;
import mx.ihsa.dominio.vo.CLocalidadVo;
import mx.ihsa.dominio.vo.ObjetivoVo;
import mx.ihsa.procesador.bean.ContactoView;
import mx.ihsa.servicios.sistema.impl.CatObjetivoImpl;

/**
 *
 * @author joel
 */
@Named
@FacesConverter(value = "objetivoConverter", managed = true)
public class ObjetivoConverter implements Converter {

    @Inject
    CatObjetivoImpl objetivoImpl;

    @Override
    public Object getAsObject(FacesContext fc, UIComponent uic, String value) {
        if (value != null && value.trim().length() > 0) {
            try {
                return objetivoImpl.find(Integer.valueOf(value));
            } catch (NumberFormatException e) {
                throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Conversion Error", "Objetivo no encontrado."));
            }
        } else {
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext fc, UIComponent uic, Object object) {
        if (object != null) {
            return String.valueOf(((ObjetivoVo) object).getId());
        } else {
            return null;
        }
    }

}
