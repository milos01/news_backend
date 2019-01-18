package init.app.configuration;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class MultipleDataSourcesConfiguration {

    @Primary
    @Bean(name = "newDb")
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource newDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "newJdbcTemplate")
    public JdbcTemplate newJdbcTemplate(@Qualifier("newDb") DataSource newMySql) {
        return new JdbcTemplate(newMySql);
    }

    @Bean(name = "oldDb")
    @ConfigurationProperties(prefix = "spring.old-datasource")
    public DataSource oldDataSource() {
        return  DataSourceBuilder.create().build();
    }

    @Bean(name = "oldJdbcTemplate")
    public JdbcTemplate oldJdbcTemplate(@Qualifier("oldDb") DataSource oldMySql) {
        return new JdbcTemplate(oldMySql);
    }

}
