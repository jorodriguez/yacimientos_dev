/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sia.ihsa.contratos.servlet;

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
import sia.modelo.usuario.vo.UsuarioVO;
import sia.modelo.vo.inventarios.ArticuloVO;
import sia.servicios.catalogos.impl.UsuarioImpl;

/**
 *
 * @author mluis
 */
@Named
@FacesConverter(value = "usuarioConverter", managed = true)
public class UsuarioConverter implements Converter<UsuarioVO> {

    @Inject
    UsuarioImpl usuarioImpl;

    @Override
    public UsuarioVO getAsObject(FacesContext context, UIComponent component, String value) {
        if (value != null && value.trim().length() > 0) {
            try {
                return usuarioImpl.findById((value));
            } catch (NumberFormatException e) {
                throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Conversion Error", "Artículo no encontrado."));
            }
        } else {
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, UsuarioVO value) {
        if (value != null) {
            return String.valueOf(value.getId());
        } else {
            return null;
        }
    }

}
