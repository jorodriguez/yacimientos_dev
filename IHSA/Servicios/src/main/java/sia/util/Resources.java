/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.util;

import javax.annotation.Resource;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.sql.DataSource;
import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultConfiguration;

/**
 *
 * @author mrojas
 */
@ApplicationScoped
public class Resources {

    @Resource(lookup = "jdbc/__siaPool")
    private DataSource dataSource;

    private static final SQLDialect DIALECT = SQLDialect.POSTGRES;

    @Produces
    public DSLContext ctxProducer() {
        final Configuration conf = new DefaultConfiguration().set(dataSource).set(DIALECT);
        return DSL.using(conf);
    }

}
