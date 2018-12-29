package com.kinespherept.config

import groovy.util.logging.Slf4j
import org.hibernate.jpa.HibernatePersistenceProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.datasource.DriverManagerDataSource
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.annotation.EnableTransactionManagement

import javax.annotation.PostConstruct
import javax.persistence.EntityManagerFactory
import javax.sql.DataSource

@Configuration
@EnableJpaRepositories('com.kinespherept.dao')
@EnableTransactionManagement
@Slf4j
class KinesphereDaoConfig {


    @Autowired Environment environment



    @PostConstruct
    void init() {
        log.info "inside KinesphereDaoConfig.init() :: environment=${environment}"
    }


    @Bean
    JdbcTemplate jdbcTemplate(DataSource dataSource) { return new JdbcTemplate(dataSource)}

    @Bean
    DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource()
        dataSource.setDriverClassName(environment.getProperty('spring.datasource.driver-class-name'))
        dataSource.setUrl(environment.getProperty('spring.datasource.url'))
        dataSource.setUsername(environment.getProperty('spring.datasource.username'))
        dataSource.setPassword(environment.getProperty('spring.datasource.password'))
        dataSource
    }

    @Bean
    LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean()
        emf.setDataSource(dataSource())
        emf.setPersistenceProviderClass(HibernatePersistenceProvider)
        emf.setPackagesToScan('com.kinespherept')
        emf
    }

    @Bean
    PlatformTransactionManager transactionManager(EntityManagerFactory emf) {
        JpaTransactionManager transactionManager = new JpaTransactionManager()
        transactionManager.setEntityManagerFactory(emf)
        transactionManager.setDataSource(dataSource())
        transactionManager
    }

}
