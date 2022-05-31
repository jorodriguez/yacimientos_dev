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
import sia.inventarios.service.InventarioImpl;
import sia.modelo.vo.inventarios.InventarioVO;

/**
 *
 * @author mluis
 */
@Named
@FacesConverter(value = "inventarioConverter", managed = true)
public class InventarioConverter implements Converter<InventarioVO> {

    @Inject
    InventarioImpl articuloImpl;

    @Override
    public InventarioVO getAsObject(FacesContext context, UIComponent component, String value) {
        if (value != null && value.trim().length() > 0) {
            try {
                return articuloImpl.buscar(Integer.parseInt(value));
            } catch (NumberFormatException e) {
                throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Conversion Error", "Inventario no encontrado."));
            } catch (SIAException ex) {
                Logger.getLogger(InventarioConverter.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            return null;
        }
        return null;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, InventarioVO value) {
        if (value != null) {
            return String.valueOf(value.getId());
        } else {
            return null;
        }
    }

}
