package donut.shop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringBootDonutShopApplication {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(SpringBootDonutShopApplication.class);
        app.setDefaultProperties(DefaultProperties.getDefaultProperties());
        app.run(args);
    }
}
