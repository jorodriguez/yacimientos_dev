/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lector.sistema.servlet.support;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;
import javax.inject.Inject;
import javax.inject.Named;
import lector.dominio.modelo.usuario.vo.UsuarioVO;
import lector.servicios.catalogos.impl.UsuarioImpl;

/**
 *
 */
//@Named
//@FacesConverter(value = "usuarioConverter", managed = true)
public class UsuarioConverter{// implements Converter<UsuarioVO> {

    @Inject
    private UsuarioImpl usuarioImpl;

  /*  @Override
    public UsuarioVO getAsObject(FacesContext context, UIComponent component, String value) {
        if (value != null) {
            try {
                return usuarioImpl.findById(Integer.parseInt(value));
            } catch (NumberFormatException e) {
                throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Conversion Error", "Usuario no encontrado."));
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
    }*/

}
