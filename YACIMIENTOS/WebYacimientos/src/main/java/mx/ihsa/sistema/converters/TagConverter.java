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
import mx.ihsa.dominio.vo.TagVo;
import mx.ihsa.servicios.sistema.impl.SiTagImpl;

/**
 *
 * @author joel
 */
@Named
@FacesConverter(value = "tagConverter", managed = true)
public class TagConverter implements Converter<TagVo> {

    @Inject
    SiTagImpl tagImpl;

    @Override
    public TagVo getAsObject(FacesContext fc, UIComponent uic, String value) {
        if (value != null && value.trim().length() > 0) {
            try {
                return tagImpl.buscarPorId(Integer.valueOf(value));
            } catch (NumberFormatException e) {
                throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Conversión Error", "Objetivo no encontrado."));
            }
        } else {
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext fc, UIComponent uic, TagVo object) {
        if (object != null) {
            return object.getNombre();
        } else {
            return null;
        }
    }

}
