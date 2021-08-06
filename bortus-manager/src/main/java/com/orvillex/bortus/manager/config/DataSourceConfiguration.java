package com.orvillex.bortus.manager.config;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import com.google.common.base.Preconditions;

import org.apache.shardingsphere.spring.boot.datasource.DataSourcePropertiesSetterHolder;
import org.apache.shardingsphere.spring.boot.util.DataSourceUtil;
import org.apache.shardingsphere.spring.boot.util.PropertyUtil;
import org.apache.shardingsphere.underlying.common.config.inline.InlineExpressionParser;
import org.apache.shardingsphere.underlying.common.exception.ShardingSphereException;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.SharedEntityManagerCreator;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableConfigurationProperties(JpaProperties.class)
public class DataSourceConfiguration {
	private final JpaProperties jpaProperties;
	private final Environment environment;

	public DataSourceConfiguration(JpaProperties jpaProperties, Environment environment) {
		this.jpaProperties = jpaProperties;
		this.environment = environment;
	}

	@Primary
	@Bean
	public DataSource dataSource() {
		String prefix = "spring.shardingsphere.datasource.";
		String each = this.getDataSourceNames(prefix).get(0);
		try {
			return this.getDataSource(prefix, each);
		} catch (final ReflectiveOperationException ex) {
			throw new ShardingSphereException("Can't find datasource type!", ex);
		}
	}

	@Primary
	@Bean
	public EntityManagerFactory entityManagerFactory() {
		HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
		vendorAdapter.setDatabase(Database.MYSQL);
		LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
		factory.setJpaVendorAdapter(vendorAdapter);
		factory.setPersistenceUnitName("default");
		factory.setPackagesToScan("com.orvillex.bortus.*");
		factory.setDataSource(this.dataSource());
		factory.setJpaPropertyMap(this.jpaProperties.getProperties());
		factory.afterPropertiesSet();
		return factory.getObject();
	}

	@Bean
	@Primary
	public EntityManager entityManager(EntityManagerFactory entityManagerFactory) {
		return SharedEntityManagerCreator.createSharedEntityManager(entityManagerFactory);
	}

	@Primary
	@Bean
	public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
		JpaTransactionManager txManager = new JpaTransactionManager();
		txManager.setEntityManagerFactory(entityManagerFactory);
		return txManager;
	}

	private List<String> getDataSourceNames(final String prefix) {
		StandardEnvironment standardEnv = (StandardEnvironment) this.environment;
		standardEnv.setIgnoreUnresolvableNestedPlaceholders(true);
		return null == standardEnv.getProperty(prefix + "name")
				? new InlineExpressionParser(standardEnv.getProperty(prefix + "names")).splitAndEvaluate()
				: Collections.singletonList(standardEnv.getProperty(prefix + "name"));
	}

	@SuppressWarnings("unchecked")
	private DataSource getDataSource(final String prefix, final String dataSourceName) throws ReflectiveOperationException {
		Map dataSourceProps = PropertyUtil.handle(this.environment, prefix + dataSourceName.trim(), Map.class);
		Preconditions.checkState(!dataSourceProps.isEmpty(), "Wrong datasource properties!");
		DataSource result = DataSourceUtil.getDataSource(dataSourceProps.get("type").toString(), dataSourceProps);
		DataSourcePropertiesSetterHolder.getDataSourcePropertiesSetterByType(dataSourceProps.get("type").toString())
				.ifPresent(dataSourcePropertiesSetter -> dataSourcePropertiesSetter.propertiesSet(this.environment, prefix, dataSourceName, result));
		return result;
	}
}
