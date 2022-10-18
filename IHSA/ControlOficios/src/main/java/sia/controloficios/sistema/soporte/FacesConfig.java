/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.controloficios.sistema.soporte;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.enterprise.util.Nonbinding;
import javax.inject.Qualifier;

@Qualifier
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface FacesConfig  {
     public static enum Version {

         /**
          * <p class="changed_added_2_3">This value indicates CDI should be used
          * for EL resolution as well as enabling JSF CDI injection, as specified
          * in Section 5.6.3 "CDI for EL Resolution" and Section 5.9 "CDI Integration".</p>
          */
         JSF_2_3

     }

     /**
      * <p class="changed_added_2_3">The value of this attribute indicates that
      * features corresponding to this version must be enabled for this application.</p>
      * @return the spec version for which the features must be enabled.
      */
     @Nonbinding Version version() default Version.JSF_2_3;
    
}
