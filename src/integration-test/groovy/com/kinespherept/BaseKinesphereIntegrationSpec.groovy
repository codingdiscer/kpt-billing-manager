package com.kinespherept

import com.kinespherept.dao.KinesphereDao
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.datasource.DriverManagerDataSource
import org.testcontainers.containers.GenericContainer
import spock.lang.Shared
import spock.lang.Specification

/**
 * A base Specification class that knows how to look for a local instance or spin up a testcontainer of postgres,
 * to be used for integration and functional tests.
 */
@Slf4j
class BaseKinesphereIntegrationSpec extends Specification {

//    // certificates - contains all common CA certs, plus the target ca certs
//    static File TRUSTSTORE_PATH = new File(Resources.getResource('truststore.jks').toURI())

    // docker kinesphere-db image and postgres details
    static String DB_IMAGE = 'local/kpt-visitstatus-manager-db'
    static String DB_URL_TEMPLATE = 'jdbc:postgresql://%s:%d/postgres'
    static String DB_DRIVER = 'org.postgresql.Driver'
    static String DB_LOCAL_USERNAME = 'postgres'
    static String DB_LOCAL_PASSWORD = 'postgres'
    static String DB_LOCAL_HOSTNAME = '192.168.99.100'
    //static String DB_LOCAL_HOSTNAME = 'localhost'
    static int DB_PORT = 5432
    static long DB_INIT_WAIT_TIME = 3000
    static final String VALIDATION_QUERY = "SELECT 1"

    // supporting objects
    @Shared GenericContainer postgres   // spin up a postgres instance if we can't find one at localhost
    @Shared DriverManagerDataSource dataSource  // keep a reference to the datasource that was actually used


    /**
     * Spin up a connection to the integration data source
     */
    def setupSpec() {

        // create the connection to the db
        dataSource = connectToDb()

        if(!dataSource) {
            // ruh-roh - we need a data source to continue, so just toss this
            throw new Exception("Unable to connect to a database instance - this test will stop here")
        }

        // set the system properties for Spring Data to connect with
        System.properties['spring.datasource.url'] = dataSource.getUrl()
        System.properties['spring.datasource.username'] = dataSource.getUsername()
        System.properties['spring.datasource.password'] = dataSource.getPassword()
        System.properties['spring.datasource.driver-class-name'] = DB_DRIVER
        System.properties['spring.jpa.database-platform'] = 'org.hibernate.dialect.PostgreSQLDialect'
    }

    /**
     * clean up after ourselves
     */
    def cleanupSpec() {
        postgres?.close()
        dataSource?.connection?.close()
    }

    /**
     * Prepares a default truststore with ca certs.
     * Useful when an org has a CA
     */
//    def static prepareCaCerts() {
//        System.properties['javax.net.ssl.trustStore'] = TRUSTSTORE_PATH.absolutePath
//    }

    /**
     * Jumps through the necessary hoops to connect to a db instance
     */
    DriverManagerDataSource connectToDb() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource()

        try {
            // first try on localhost...
            dataSource.setDriverClassName(DB_DRIVER)
            dataSource.setUrl(String.format(DB_URL_TEMPLATE, DB_LOCAL_HOSTNAME, DB_PORT))
            dataSource.setUsername(DB_LOCAL_USERNAME)
            dataSource.setPassword(DB_LOCAL_PASSWORD)

            // execute a validation query - this will either succeed quietly or throw an exception
            new JdbcTemplate(dataSource).queryForRowSet(VALIDATION_QUERY)
        } catch(RuntimeException e) {
            if(e.message.contains('Connection to localhost:5432 refused.')) {
                // spin up the docker image
                postgres = new GenericContainer(DB_IMAGE).withExposedPorts(DB_PORT)
                postgres.start()
                // this delay is required to allow enough time for the docker image to fully spin up.
                Thread.sleep(DB_INIT_WAIT_TIME)

                dataSource.setUrl(String.format(DB_URL_TEMPLATE, postgres.getContainerIpAddress(), postgres.getMappedPort(DB_PORT)))

                try {
                    // execute a validation query - this will either succeed quietly or throw an exception
                    new JdbcTemplate(dataSource).queryForRowSet(VALIDATION_QUERY)
                } catch(Exception e2) {
                    //log.error "Unable to connect to postgres db on localhost:5432 or via testcontainers"
                    // doh!  this won't work - return a null datasource to flag the error
                    dataSource = null
                }
            } else {
                //log.error("Connection to database failed with exception", e)

                // doh!  this won't work - return a null datasource to flag the error
                dataSource = null
            }

        }
        return dataSource;
    }

    /**
     * Convenience method to truncate a table
     */
    def truncateTable(String tableName) {
        new JdbcTemplate(dataSource).execute("truncate ${tableName}")
    }






    @Configuration
    static class BaseKinesphereIntegrationSpecConfig {
        @Autowired Environment env

        @Bean KinesphereDao kinesphereDao() {

            //log.info "kinesphereDao(), env=${env}"
            println "kinesphereDao(), env=${env}"
            new KinesphereDao()
//            new RepoConfig(
//                    repoOrgAndName: env.getProperty('kafka.operations.repository.repoOrgAndName'),
//                    username: env.getProperty('kafka.operations.repository.username'),
//                    authToken:  env.getProperty('kafka.operations.repository.authToken')
//            )
        }

//        @Bean KinesphereDao gitKinesphereDao() {
//            new KinesphereDao(repoConfig())
//        }

    }


//    @Configuration
//    static class ConfigurationItemsDaoConfig {
//        @Autowired Environment env
//
//        @Bean ConfigurationItemsProperties configurationItemsConfig() {
//            log.info "configurationItemsConfig(), env=${env}"
//            new ConfigurationItemsProperties(
//                    apiKey: env.getProperty('kafka.operations.configuration_items.api_key'),
//                    authToken:  env.getProperty('kafka.operations.configuration_items.auth_token')
//            )
//        }
//    }


}
