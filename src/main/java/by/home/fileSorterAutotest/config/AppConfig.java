package by.home.fileSorterAutotest.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

/**
 * Class configure Spring Beans, include runner, properties, applicationContextProvider
 */
@Configuration
@ComponentScan("by.home.fileSorterAutotest.service")
@PropertySource("classpath:database.properties")
@Import({DataConfig.class})
public class AppConfig {

}
