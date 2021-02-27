package donut.shop;

import com.opentable.db.postgres.embedded.EmbeddedPostgres;
import donut.shop.entity.mongo.CustomerReview;
import donut.shop.entity.relational.Donut;
import donut.shop.entity.relational.Ingredient;
import donut.shop.entity.repository.mongo.OrderMongoRepository;
import donut.shop.entity.repository.mongo.ReviewMongoRepository;
import donut.shop.entity.repository.relational.DonutRepository;
import donut.shop.entity.repository.relational.IngredientRepository;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mockito.internal.matchers.apachecommons.ReflectionEquals;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.LinkedMultiValueMap;

import javax.sql.DataSource;
import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static donut.shop.constant.DonutShopConstant.*;
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@TestPropertySource(locations = "classpath:test.properties")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ComponentScan
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes =
        {SpringBootDonutShopApplication.class, SpringBootDonutShopApplicationTest.Config.class})
@ActiveProfiles("test")
public class SpringBootDonutShopApplicationTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    @Qualifier("embedded")
    private DataSource dataSource;

    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    private DonutRepository donutRepository;

    @Autowired
    private IngredientRepository ingredientRepository;

    @Autowired
    private OrderMongoRepository orderRepository;

    @Autowired
    private ReviewMongoRepository reviewsRepository;

    private static final String baseUrl = "http://localhost:9080";

    @TestConfiguration
    static class Config {

        @Bean("embedded")
        public DataSource dataSource() throws IOException {
            return EmbeddedPostgres.start().getDatabase("postgres", "postgres");
        }
    }

    @Test
    public void test1CheckBasicTestConfig() {
        // Just testing if flyway + embedded postgres + embedded mongo are working properly
        assertTrue(ingredientRepository.findByName("sugar glaze").isPresent());
        assertFalse(ingredientRepository.findByName("wrong").isPresent());

        CustomerReview review = new CustomerReview();
        review.setReview("review");
        review.set_id("id");
        review.setOrderId("orderId");

        reviewsRepository.save(review);

        assertTrue(reviewsRepository.findById("id").isPresent());
        assertFalse(reviewsRepository.findById("wrong").isPresent());
    }

    @Test
    public void test2AdminGetIngredientTest() {
        // get-ingredients, with no authentication
        ResponseEntity<Void> getIngErr =
                testRestTemplate.getForEntity(URI.create(baseUrl.concat(ADMIN_REST).concat(ADMIN_GET_INGREDIENTS)), Void.class);
        assertEquals(HttpStatus.UNAUTHORIZED, getIngErr.getStatusCode());

        // get-ingredients, correct
        ResponseEntity<Ingredient[]> getIngCorr =
                testRestTemplate.withBasicAuth("admin", "password").getForEntity(URI.create(baseUrl.concat(ADMIN_REST).concat(ADMIN_GET_INGREDIENTS)), Ingredient[].class);
        assertEquals(HttpStatus.OK, getIngCorr.getStatusCode());
        List<Ingredient> ingredients = Arrays.asList(getIngCorr.getBody());
        assertEquals(8, ingredients.size());

        List<String> ingredientNames =
                ingredients.stream().map(Ingredient::getName).collect(Collectors.toList());
        assertTrue(ingredientNames.contains("sugar glaze"));
    }

    @Test
    public void test3AdminNewDonutTest() {
        // new-donut, with no authentication
        ResponseEntity<Void> newDonErr =
                testRestTemplate.getForEntity(URI.create(baseUrl.concat(ADMIN_REST).concat(ADMIN_NEW_DONUT)), Void.class);
        assertEquals(HttpStatus.UNAUTHORIZED, newDonErr.getStatusCode());

        // new-donut, correct
        Ingredient ingredient = new Ingredient();
        ingredient.setName("chocolate dough");

        Donut newDonut = new Donut();
        newDonut.setIngredients(Set.of(ingredient));
        newDonut.setPrice("2.50");
        newDonut.setDescription("A donut");
        newDonut.setName("Basic donut");

        ResponseEntity<Donut> newDonCorr =
                testRestTemplate.withBasicAuth("admin", "password").postForEntity(URI.create(baseUrl.concat(ADMIN_REST).concat(ADMIN_NEW_DONUT)), newDonut, Donut.class);
        assertEquals(HttpStatus.OK, newDonCorr.getStatusCode());

        Donut newDonutResponse = newDonCorr.getBody();
        newDonut.setDonutId(newDonutResponse.getDonutId());
        assertTrue(new ReflectionEquals(newDonut).matches(newDonutResponse));

        List<Donut> allDonuts = donutRepository.findAll();
        assertEquals(4, allDonuts.size());

        // new-donut, error duplicate
        ResponseEntity<Donut> newDonErrDup =
                testRestTemplate.withBasicAuth("admin", "password").postForEntity(URI.create(baseUrl.concat(ADMIN_REST).concat(ADMIN_NEW_DONUT)), newDonut, Donut.class);
        assertEquals(HttpStatus.BAD_REQUEST, newDonErrDup.getStatusCode());
        assertTrue(newDonErrDup.getHeaders().get("Exception").toString().contains(
                "ConstraintViolationException"));

        // new-donut, error non existent ingredient
        Ingredient ingredientWrong = new Ingredient();
        ingredientWrong.setName("wrong");

        newDonut.setIngredients(Set.of(ingredientWrong));

        ResponseEntity<Donut> newDonErrIng =
                testRestTemplate.withBasicAuth("admin", "password").postForEntity(URI.create(baseUrl.concat(ADMIN_REST).concat(ADMIN_NEW_DONUT)), newDonut, Donut.class);
        assertEquals(HttpStatus.BAD_REQUEST, newDonErrIng.getStatusCode());
        assertTrue(newDonErrIng.getHeaders().get("Exception").toString().contains("not found"));

        // new-donut, error price
        newDonut.setIngredients(Set.of(ingredient));
        newDonut.setPrice("3000.00");

        ResponseEntity<Donut> newDonErrPrice =
                testRestTemplate.withBasicAuth("admin", "password").postForEntity(URI.create(baseUrl.concat(ADMIN_REST).concat(ADMIN_NEW_DONUT)), newDonut, Donut.class);
        assertEquals(HttpStatus.BAD_REQUEST, newDonErrPrice.getStatusCode());
        assertTrue(newDonErrPrice.getHeaders().get("Exception").toString().contains("How much do you think"));
    }
}
