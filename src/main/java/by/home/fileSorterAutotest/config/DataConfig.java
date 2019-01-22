package by.home.fileSorterAutotest.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * Class to config database jpa services
 */
@Configuration
@EnableTransactionManagement
@PropertySource("classpath:database.properties")
@EnableJpaRepositories("by.home.fileSorterAutotest.repository")
public class DataConfig {

    @Value("${jdbc.driverClassName}")
    private String driverClassName;

    @Value("${jdbc.url}")
    private String url;

    @Value("${jdbc.username}")
    private String username;

    @Value("${jdbc.password}")
    private String password;

    @Value("${db.hibernate.dialect}")
    private String hibernateDialect;

    @Value("${db.hibernate.show_sql}")
    private Boolean hibernateShowSql;

    @Value("${db.hibernate.hbm2ddl.auto}")
    private String hibernateHBM2DD1auto;

    @Value("${db.hibernate.schema}")
    private String hibernateSchema;

    /**
     * Been of entityManagerFactoryBean, which allows configuration a JPA EntityManagerFactory
     * in a Spring application context
     *
     * @return entityManagerFactoryBean
     */
    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
        entityManagerFactoryBean.setDataSource(dataSource());
        entityManagerFactoryBean.setPackagesToScan("by.home.fileSorterAutotest.entity");
        entityManagerFactoryBean.setJpaVendorAdapter(jpaVendorAdapter());
        entityManagerFactoryBean.setJpaProperties(jpaProperties());
        return entityManagerFactoryBean;
    }

    /**
     * Been of DriverManagerDataSource which is configuring
     * the JDBC DriverManager via bean properties, and returning
     * a new Connection from every getConnection call.
     *
     * @return dataSource
     */
    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(driverClassName);
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        return dataSource;
    }

    /**
     * Define properties for JPA of hibernate config
     *
     * @return jpa properties
     */
    private Properties jpaProperties() {
        Properties properties = new Properties();
        properties.put("db.hibernate.dialect", hibernateDialect);
        properties.put("db.hibernate.show_sql", hibernateShowSql);
        properties.put("db.hibernate.hbm2ddl.auto", hibernateHBM2DD1auto);
        properties.put("db.hibernate.schema", hibernateSchema);
        return properties;
    }

    /**
     * Been of HibernateJpaVendorAdapter exposes Hibernate's persistence provider
     * and EntityManager extension interface, and adapts AbstractJpaVendorAdapter's
     * common configuration settings.
     *
     * @return HibernateJpaVendorAdapter
     */
    @Bean
    public JpaVendorAdapter jpaVendorAdapter() {
        return new HibernateJpaVendorAdapter();
    }

    /**
     * Transaction manager is need for use our JPA EntityManagerFactory for transactional data access.
     *
     * @return transactionManager
     */
    @Bean
    public JpaTransactionManager transactionManager() {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory().getObject());
        return transactionManager;
    }
}
