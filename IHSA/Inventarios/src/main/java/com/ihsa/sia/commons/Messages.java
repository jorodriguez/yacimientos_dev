package com.ihsa.sia.commons;

import java.io.Serializable;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 *
 * @author Aplimovil SA de CV
 */
public class Messages implements Serializable {

	private static final String BUNDLE_NAME = "com.ihsa.sia.lang.lang";
	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME, new Locale("es"));
	/**
	 * Separador de linea obtenido de los properties
	 */
	public static String SALTO_LINEA = System.getProperty("line.separator");


	/**
	 * Carga de valores iniciales para variables estaticas
	 */
	static {

	}

	/**
	 * Permite la obtención del valor de la clave del archivo de propiedades general
	 *
	 * @param key	- Clave del archivo de propiedades que se desea obtener
	 * @return	- Valor de la clave ingresadaS
	 * @throws {@link  MissingResourceException}
	 */
	public static String getString(String key) throws MissingResourceException {
		return RESOURCE_BUNDLE.getString(key);
	}

	/**
	 * Permite la obtención del valor de la clave del archivo de propiedades general en formato {@link  Integer}
	 *
	 * @param key	- Clave del archivo de propiedades que se desea obtener
	 * @return	- Valor de la clave ingresada
	 * @throws {@link  MissingResourceException}
	 */
	public static Integer getInteger(String key) throws MissingResourceException {
		try {
			return Integer.valueOf(RESOURCE_BUNDLE.getString(key));
		} catch (Exception e) {
			return null;
		}

	}

	/**
	 * Permite la obtención del valor de la clave del archivo de propiedades general en formato {@link  Long}
	 *
	 * @param key	- Clave del archivo de propiedades que se desea obtener
	 * @return	- Valor de la clave ingresada
	 * @throws {@link  MissingResourceException}
	 */
	public static Long getLong(String key) throws MissingResourceException {
		try {
			return Long.valueOf(RESOURCE_BUNDLE.getString(key));
		} catch (Exception e) {
		}
		return null;
	}

	/**
	 * Permite la obtención del valor de la clave del archivo de propiedades general en formato {@link  Double}
	 *
	 * @param key	- Clave del archivo de propiedades que se desea obtener
	 * @return	- Valor de la clave ingresada
	 * @throws {@link  MissingResourceException}
	 */
	public static Double getDouble(String key) throws MissingResourceException {
		try {
			return Double.valueOf(RESOURCE_BUNDLE.getString(key));
		} catch (Exception e) {
		}
		return null;
	}
}
