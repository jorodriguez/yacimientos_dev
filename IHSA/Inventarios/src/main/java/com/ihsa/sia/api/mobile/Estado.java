package com.ihsa.sia.api.mobile;

/**
 * Estado de la respuesta del api para moviles <ok/error>
 * @author Aplimovil SA de CV
 */
public enum Estado {
    ok, error;

    @Override
    public String toString() {
        return this.name().toLowerCase();
    }
}
