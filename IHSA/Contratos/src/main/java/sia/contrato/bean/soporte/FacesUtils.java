/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.contrato.bean.soporte;

import java.io.File;
import java.util.Locale;
import javax.el.ELContext;
import javax.faces.FactoryFinder;
import javax.faces.application.Application;
import javax.faces.application.ApplicationFactory;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import sia.util.UtilLog4j;

/**
 *
 * @author ihsa
 */
/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
public class FacesUtils {

    public static FacesContext getContext() {
	return FacesContext.getCurrentInstance();
    }

    /* Request */
    public static HttpServletRequest getHttpRequest(final FacesContext fc) {
	return (HttpServletRequest) fc.getExternalContext().getRequest();
    }

    public static Object getRequestAttribute(final FacesContext fc, final String key) {
	return getHttpRequest(fc).getAttribute(key);
    }

    public static void setRequestAttribute(final FacesContext fc, final String key, Object value) {
	if (value != null) {
	    getHttpRequest(fc).setAttribute(key, value);
	} else {
	    getHttpRequest(fc).removeAttribute(key);
	}
    }

    public static String getRequestParam(final FacesContext context, final String param) {
	return context.getExternalContext().getRequestParameterMap().get(param);
    }


    /* Managed Beans */
    public static Object getManagedBean(final FacesContext fc, final String name) {
	if (fc == null) {
	    throw new NullPointerException("context must not be null");
	}
	if (name == null) {
	    throw new NullPointerException("name must not be null");
	}

	final ELContext elcontext = fc.getELContext();
	final Application application = fc.getApplication();

	return application.getELResolver().getValue(elcontext, null, name);
    }

    public static <T> T getManagedBean(final FacesContext fc, final Class<T> cls) {
	String name = cls.getName();
	int i = name.lastIndexOf('.') + 1;
	name = name.substring(i, i + 1).toLowerCase(Locale.ROOT) + name.substring(i + 1);
	@SuppressWarnings("unchecked")
	T bean = (T) getManagedBean(fc, name);
	if (bean == null) {
	    UtilLog4j.log.warn("Managed Bean {} ist not available.", name);
	}
	return bean;
    }

    /*
     @SuppressWarnings("unchecked")
     public static <T> T findBean(String beanName) {
     FacesContext context = FacesContext.getCurrentInstance();
     return (T) context.getApplication().evaluateExpressionGet(context, "#{" + beanName + "}", Object.class);
     }
     */
    /* file */
    public static String getRealPath(final FacesContext fc, String path) {
	return (((ServletContext) fc.getExternalContext().getContext())).getRealPath(path);
    }

    public static String getFilePath(final FacesContext fc, String path, String file) {
	String realPath = getRealPath(fc, path);
	return realPath.endsWith(File.separator) ? realPath + file : realPath + File.separator + file;
    }

    public static Object getManagedBean(String beanName) {
	return getValueBinding(getJsfEl(beanName)).getValue(FacesContext.getCurrentInstance());
    }

    private static ValueBinding getValueBinding(String el) {
	return getApplication().createValueBinding(el);
    }

    private static Application getApplication() {
	ApplicationFactory appFactory = (ApplicationFactory) FactoryFinder.getFactory(FactoryFinder.APPLICATION_FACTORY);
	return appFactory.getApplication();
    }

    private static String getJsfEl(String value) {
	return "#{" + value + "}";
    }

    public static void addInfoMessage(FacesContext fc, String clientId, String msg) {
	fc.addMessage(clientId, new FacesMessage(FacesMessage.SEVERITY_INFO, msg, msg));
    }

    public static void addInfoMessage(String msg) {
	addInfoMessage(FacesContext.getCurrentInstance(), null, msg);
    }

    public static void addErrorMessage(String clientId, String msg) {
	FacesContext.getCurrentInstance().addMessage(clientId, new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, msg));
    }

    public static void addErrorMessage(String msg) {
	addErrorMessage(FacesContext.getCurrentInstance(), null, msg);
    }

    public static void addErrorMessage(FacesContext fc, String cliente, String msg) {
	fc.addMessage(cliente, new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, msg));
    }

    public static String getRequestParam(String name) {
	return (String) FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get(name);
    }

}
