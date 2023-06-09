/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.ihsa.excepciones;

import java.util.ArrayList;
import java.util.List;

/**
 * Deberá usarse cuando exista un proceso que envíe una notificación y el email
 * de alguno de los destinarios no exista
 *
 * Esta excepción contiene una lista de Usuarios los cuales deberán ser los
 * Usuarios de los cuales no se encontró su email
 *
 * @author b75ckd35th
 */
public class EmailNotFoundException extends LectorException {

//    private List<Usuario> usuarios = new ArrayList<Usuario>();
    private List<String> usuarios = new ArrayList<String>();

    public EmailNotFoundException(List<String> usuarios) {
        this.usuarios = usuarios;
        super.setLiteral("sia.exception.emailNotFoundException");
    }

    public EmailNotFoundException(List<String> usuarios, String literal) {
        this.usuarios = usuarios;
        super.setLiteral(literal);
    }

    public EmailNotFoundException(String usuario) {
        this.usuarios.add(usuario);
    }

    public String getAllUsuariosWithoutEmail() {
        String uList = "";
        if (!this.usuarios.isEmpty()) {
            for (int i = 0; i < this.usuarios.size(); i++) {
                String u = this.usuarios.get(i);
                uList += u;
                if ((i + 1) < this.usuarios.size()) {
                    uList += ", ";
                }
            }
        }
        return uList;
    }

    public void addUsuario(String u) {
        this.usuarios.add(u);
    }

    /**
     * @return the usuarios
     */
    public List<String> getUsuarios() {
        return usuarios;
    }

    /**
     * @param usuarios the usuarios to set
     */
    public void setUsuarios(List<String> usuarios) {
        this.usuarios = usuarios;
    }
}
