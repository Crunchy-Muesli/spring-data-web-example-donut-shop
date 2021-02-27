package donut.shop;

import java.util.Properties;

public class DefaultProperties {
    private DefaultProperties() {
    }

    public static Properties getDefaultProperties() {
        Properties p = new Properties();
        p.setProperty("server.address", "0.0.0.0");
        p.setProperty("server.port", "8080");
        p.setProperty("server.scheme", "http");
        p.setProperty("server.servlet.context-path", "/donut-shop");
        p.setProperty("spring.output.ansi.enabled", "ALWAYS");
        return p;
    }

}
