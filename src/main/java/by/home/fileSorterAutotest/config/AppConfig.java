package by.home.fileSorterAutotest.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Class configure Spring Beans, include runner, properties, applicationContextProvider
 */
@Configuration
@ComponentScan("by.home.fileSorterAutotest.service")
@Import({DataConfig.class})
public class AppConfig {

}
