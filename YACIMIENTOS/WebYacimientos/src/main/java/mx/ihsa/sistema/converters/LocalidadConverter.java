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
import javax.inject.Named;
import mx.ihsa.dominio.vo.CLocalidadVo;
import mx.ihsa.procesador.bean.ContactoView;

/**
 *
 * @author joel
 */
@Named
@FacesConverter(value = "localidadConverter", managed = true)
public class LocalidadConverter implements Converter<CLocalidadVo> {
    
    @Override
    public CLocalidadVo getAsObject(FacesContext context, UIComponent component, String value) {
        
        System.out.println(" ====> string value "+value);
        
        if (value != null && value.trim().length() > 0) {
            try {
                
                ContactoView view = (ContactoView) context.getExternalContext().getApplicationMap().get("contactoView");
                
                return view.getListaLocalidades().stream().filter(e->e.getNombre().equals(value)).findFirst().get();
                
                                
            }
            catch (NumberFormatException e) {
                
                throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Conversion Error", "Not a valid id localidad."));
            }
        }
        else {
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, CLocalidadVo value) {
        if (value != null) {
            return String.valueOf(value.getId());
        }
        else {
            return null;
        }
    }
}
