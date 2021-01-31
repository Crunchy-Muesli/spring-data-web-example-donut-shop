package donut.shop;

import java.util.Properties;

public class DefaultProperties {
    private DefaultProperties() {
    }

    public static Properties getDefaultProperties() {
        Properties p = new Properties();
        p.setProperty("server.address", "0.0.0.0");
        p.setProperty("server.port", "9889");
        p.setProperty("server.scheme", "http");
        p.setProperty("spring.output.ansi.enabled", "ALWAYS");
        return p;
    }

}
