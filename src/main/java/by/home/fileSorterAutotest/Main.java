package by.home.fileSorterAutotest;

import by.home.fileSorterAutotest.config.AppConfig;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Main class
 */
public class Main {

    /**
     * Main method with which it all begin
     *
     * @param args - arguments which send by commend line at startup
     */
    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
    }
}
