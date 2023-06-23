/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.ihsa.util;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Stateless;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Named;
import javax.sql.DataSource;
import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultConfiguration;

/**
 *
 * @author 
 */


//@ApplicationScoped
@Stateless
@Startup
public class Resources {
    
    @Resource(lookup = "jdbc/__yacimientosPool")
    private DataSource dataSource;
    
    private static final SQLDialect DIALECT = SQLDialect.POSTGRES;   
    
    private DSLContext dsl;
        
    @PostConstruct
    public void ini (){
        System.out.println("===========================================================================================================");
        final Configuration conf = new DefaultConfiguration().set(dataSource).set(DIALECT);
        dsl = DSL.using(conf);
    }
         
    public Resources() {}
    
    public DSLContext getDsl(){
        return this.dsl;
    }
    

    /*@Produces
    public DSLContext ctxProducer() {
        System.out.println("===========================================================================================================");
        final Configuration conf = new DefaultConfiguration().set(dataSource).set(DIALECT);
        return DSL.using(conf);
    }*/
   
    

}
