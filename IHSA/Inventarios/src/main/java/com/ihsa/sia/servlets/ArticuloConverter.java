/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ihsa.sia.servlets;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;
import javax.inject.Inject;
import javax.inject.Named;
import sia.excepciones.SIAException;
import sia.inventarios.service.ArticuloRemote;
import sia.modelo.vo.inventarios.ArticuloVO;

/**
 *
 * @author mluis
 */
@Named
@FacesConverter(value = "articuloConverter", managed = true)
public class ArticuloConverter implements Converter<ArticuloVO> {

    @Inject
    ArticuloRemote articuloImpl;

    @Override
    public ArticuloVO getAsObject(FacesContext context, UIComponent component, String value) {
        if (value != null && value.trim().length() > 0 && !value.trim().equals("null")) {
            try {
                return articuloImpl.buscar(Integer.valueOf(value));
            } catch (NumberFormatException e) {
                throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Conversion Error", "Artículo no encontrado."));
            } catch (SIAException ex) {
                Logger.getLogger(ArticuloConverter.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            return null;
        }
        return null;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, ArticuloVO value) {
        if (value != null) {
            return String.valueOf(value.getId());
        } else {
            return null;
        }
    }

}
