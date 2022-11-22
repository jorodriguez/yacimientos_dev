package com.ihsa.sia.commons;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

/**
 *
 * @author Aplimovil SA de CV
 */
public class ComponentFinder {
    
    public static UIComponent findComponent(final String id){
    FacesContext context = FacesContext.getCurrentInstance(); 
    /*UIViewRoot root = context.getViewRoot();
    final UIComponent[] found = new UIComponent[1];
    root.visitTree(new FullVisitContext(context), new VisitCallback() {     
        @Override
        public VisitResult visit(VisitContext context, UIComponent component) {
            if(component != null && id.equals(component.getId())){
                found[0] = component;
                return VisitResult.COMPLETE;
            }
            return VisitResult.ACCEPT;              
        }
    });
    return found[0];*/
    return context.getViewRoot().findComponent(id) ;
    }
}
