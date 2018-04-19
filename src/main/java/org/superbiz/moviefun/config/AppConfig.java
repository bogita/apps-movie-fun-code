package org.superbiz.moviefun.config;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionTemplate;
import org.superbiz.moviefun.DatabaseServiceCredentials;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.lang.management.PlatformManagedObject;

@Configuration
public class AppConfig {

    @Bean
    public DatabaseServiceCredentials serviceCredentials(@Value("${VCAP_SERVICES}") String vcapService){
        return new DatabaseServiceCredentials(vcapService);
    }

    @Bean
    public DataSource albumsDataSource(DatabaseServiceCredentials serviceCredentials){
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl(serviceCredentials.jdbcUrl("albums-mysql", "p-mysql"));
        return ds;
    }

    @Bean
    public DataSource moviesDataSource(DatabaseServiceCredentials serviceCredentials){
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl(serviceCredentials.jdbcUrl("movies-mysql", "p-mysql"));
        return ds;
    }

    @Bean
    public HibernateJpaVendorAdapter hibernateJpaVendorAdapter(){
        HibernateJpaVendorAdapter jpaVendorAdapter = new HibernateJpaVendorAdapter();
        jpaVendorAdapter.setDatabasePlatform("org.hibernate.dialect.MySQL5Dialect");
        jpaVendorAdapter.setDatabase(Database.MYSQL);
        jpaVendorAdapter.setGenerateDdl(true);
        return jpaVendorAdapter;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean moviesEntityManagerFactory(
            HibernateJpaVendorAdapter hibernateJpaVendorAdapter, DataSource moviesDataSource
    ){
        LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
        entityManagerFactoryBean.setJpaVendorAdapter(hibernateJpaVendorAdapter);
        entityManagerFactoryBean.setDataSource(moviesDataSource);
        entityManagerFactoryBean.setPackagesToScan("org.superbiz.moviefun.movies");
        entityManagerFactoryBean.setPersistenceUnitName("movies-unit");
        return entityManagerFactoryBean;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean albumsEntityManagerFactory(
            HibernateJpaVendorAdapter hibernateJpaVendorAdapter, DataSource albumsDataSource
    ){
        LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
        entityManagerFactoryBean.setJpaVendorAdapter(hibernateJpaVendorAdapter);
        entityManagerFactoryBean.setDataSource(albumsDataSource);
        entityManagerFactoryBean.setPackagesToScan("org.superbiz.moviefun.albums");
        entityManagerFactoryBean.setPersistenceUnitName("albums-unit");
        return entityManagerFactoryBean;
    }

    @Bean
    public PlatformTransactionManager moviesTransactionManager(EntityManagerFactory moviesEntityManagerFactory){
        return new JpaTransactionManager(moviesEntityManagerFactory);
    }

    @Bean
    public PlatformTransactionManager albumsTransactionManger(EntityManagerFactory albumsEntityManagerFactory){
        return new JpaTransactionManager(albumsEntityManagerFactory);
    }

    @Bean
    public TransactionTemplate albumsTransactionTemplate(PlatformTransactionManager albumsTransactionManger){
        return new TransactionTemplate(albumsTransactionManger);
    }

    @Bean
    public TransactionTemplate moviesTransactionTemplate(PlatformTransactionManager moviesTransactionManager){
        return new TransactionTemplate(moviesTransactionManager);
    }

}
