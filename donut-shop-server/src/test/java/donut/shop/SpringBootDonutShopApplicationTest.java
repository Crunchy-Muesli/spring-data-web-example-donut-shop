package donut.shop;

import com.opentable.db.postgres.embedded.EmbeddedPostgres;
import donut.shop.entity.mongo.CustomerReview;
import donut.shop.entity.relational.Donut;
import donut.shop.entity.relational.Ingredient;
import donut.shop.entity.repository.mongo.OrderMongoRepository;
import donut.shop.entity.repository.mongo.ReviewMongoRepository;
import donut.shop.entity.repository.relational.DonutRepository;
import donut.shop.entity.repository.relational.IngredientRepository;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.Before;
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
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import javax.sql.DataSource;
import java.io.IOException;
import java.net.URI;
import java.util.*;
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

    private RestTemplate restTemplate;

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

    @Before
    public void restTemplateSetup() {
        //Using apache client because TestRestTemplate does not support PATCH or DELETE with body
        this.restTemplate = testRestTemplate.getRestTemplate();
        HttpClient httpClient = HttpClientBuilder.create().build();
        this.restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(httpClient));
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
        assertEquals(9, ingredients.size());

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

    @Test
    public void test4AdminUpdateDonutTest() throws IOException {
        // update-donut, with no authentication
        ResponseEntity<Void> upDonutErr =
                restTemplate.exchange(URI.create(baseUrl.concat(ADMIN_REST).concat(ADMIN_UPDATE_DONUT).replace("{donutName}", "donut")), HttpMethod.PATCH, new HttpEntity(new Donut(), new HttpHeaders()), Void.class);
        assertEquals(HttpStatus.UNAUTHORIZED, upDonutErr.getStatusCode());

        // update-donut, correct
        Ingredient ingredient = new Ingredient();
        ingredient.setName("chocolate dough");

        Donut upDonut = new Donut();
        upDonut.setIngredients(Set.of(ingredient));
        upDonut.setPrice("2.50");
        upDonut.setDescription("A donut");
        upDonut.setName("Basic donut");

        HttpHeaders reqHeaders = new HttpHeaders();
        reqHeaders.setBasicAuth("admin", "password");

        ResponseEntity<Donut> upDonutCorr =
                restTemplate.exchange(URI.create(baseUrl.concat(ADMIN_REST).concat(ADMIN_UPDATE_DONUT).replace("{donutName}", "Chocolate%20Bomb")), HttpMethod.PATCH, new HttpEntity(upDonut, reqHeaders), Donut.class);
        assertEquals(HttpStatus.OK, upDonutCorr.getStatusCode());

        Donut upDonutResponse = upDonutCorr.getBody();

        assertTrue(new ReflectionEquals(upDonut, "name").matches(upDonutResponse));

        List<Donut> allDonuts = donutRepository.findAll();
        assertEquals(3, allDonuts.size());

        // update-donut, error non existent ingredient
        Ingredient ingredientWrong = new Ingredient();
        ingredientWrong.setName("wrong");

        upDonut.setIngredients(Set.of(ingredientWrong));

        ResponseEntity<Donut> newDonErrIng =
                restTemplate.exchange(URI.create(baseUrl.concat(ADMIN_REST).concat(ADMIN_UPDATE_DONUT).replace("{donutName}", "Chocolate%20Bomb")), HttpMethod.PATCH, new HttpEntity(upDonut, reqHeaders), Donut.class);
        assertEquals(HttpStatus.NOT_FOUND, newDonErrIng.getStatusCode());
        assertTrue(newDonErrIng.getHeaders().get("Exception").toString().contains("Ingredient with name"));
        assertTrue(newDonErrIng.getHeaders().get("Exception").toString().contains("not found"));

        // update-donut, error name not found
        ResponseEntity<Donut> newDonErrName =
                restTemplate.exchange(URI.create(baseUrl.concat(ADMIN_REST).concat(ADMIN_UPDATE_DONUT).replace("{donutName}", "wrong")), HttpMethod.PATCH, new HttpEntity(upDonut, reqHeaders), Donut.class);
        assertEquals(HttpStatus.NOT_FOUND, newDonErrName.getStatusCode());
        assertTrue(newDonErrName.getHeaders().get("Exception").toString().contains("Donut with name"));
        assertTrue(newDonErrName.getHeaders().get("Exception").toString().contains("not found"));
    }

    @Test
    public void test5AdminDeleteDonutTest() {
        // delete-donut, with no authentication
        ResponseEntity<Void> delDonErr =
                restTemplate.exchange(URI.create(baseUrl.concat(ADMIN_REST).concat(ADMIN_DELETE_DONUT)),
                        HttpMethod.DELETE, new HttpEntity("name", new HttpHeaders()), Void.class);
        assertEquals(HttpStatus.UNAUTHORIZED, delDonErr.getStatusCode());

        // delete-donut, correct
        HttpHeaders reqHeaders = new HttpHeaders();
        reqHeaders.setBasicAuth("admin", "password");

        ResponseEntity<Void> delDonCorr =
                restTemplate.exchange(URI.create(baseUrl.concat(ADMIN_REST).concat(ADMIN_DELETE_DONUT)),
                        HttpMethod.DELETE, new HttpEntity("Chocolate Bomb", reqHeaders), Void.class);
        assertEquals(HttpStatus.OK, delDonCorr.getStatusCode());

        List<Donut> allDonuts = donutRepository.findAll();
        assertEquals(2, allDonuts.size());

        // delete-donut, error not found
        ResponseEntity<Void> delDonErrNotFound =
                restTemplate.exchange(URI.create(baseUrl.concat(ADMIN_REST).concat(ADMIN_DELETE_DONUT)),
                        HttpMethod.DELETE, new HttpEntity("Chocolate Bomb", reqHeaders), Void.class);
        assertEquals(HttpStatus.NOT_FOUND, delDonErrNotFound.getStatusCode());
        assertTrue(delDonErrNotFound.getHeaders().get("Exception").toString().contains("not found"));
    }

    @Test
    public void test6AdminAddIngredientTest() {
        // add-ingredients, with no authentication
        ResponseEntity<Void> addIngErr =
                testRestTemplate.postForEntity(URI.create(baseUrl.concat(ADMIN_REST).concat(ADMIN_ADD_INGREDIENTS)), new ArrayList<>(), Void.class);
        assertEquals(HttpStatus.UNAUTHORIZED, addIngErr.getStatusCode());

        // add-ingredients, correct
        Ingredient ingredient = new Ingredient();
        ingredient.setName("newing");

        ResponseEntity<Ingredient[]> addIngCorr =
                testRestTemplate.withBasicAuth("admin", "password").postForEntity(URI.create(baseUrl.concat(ADMIN_REST).concat(ADMIN_ADD_INGREDIENTS)), Collections.singletonList(ingredient), Ingredient[].class);
        assertEquals(HttpStatus.OK, addIngCorr.getStatusCode());

        List<Ingredient> ingredients = Arrays.asList(addIngCorr.getBody());
        assertEquals(1, ingredients.size());

        List<String> ingredientNames =
                ingredientRepository.findAll().stream().map(Ingredient::getName).collect(Collectors.toList());
        assertEquals(10, ingredientNames.size());
        assertTrue(ingredientNames.contains("newing"));
    }

    @Test
    public void test7AdminDeleteIngredientsTest() {
        // delete-ingredients, with no authentication
        ResponseEntity<Void> delIngErr =
                restTemplate.exchange(URI.create(baseUrl.concat(ADMIN_REST).concat(ADMIN_DELETE_INGREDIENTS)), HttpMethod.DELETE, new HttpEntity(new ArrayList<>(), new HttpHeaders()), Void.class);
        assertEquals(HttpStatus.UNAUTHORIZED, delIngErr.getStatusCode());

        // delete-ingredients, correct
        HttpHeaders reqHeaders = new HttpHeaders();
        reqHeaders.setBasicAuth("admin", "password");

        ResponseEntity<Void> delIngCorr =
                restTemplate.exchange(URI.create(baseUrl.concat(ADMIN_REST).concat(ADMIN_DELETE_INGREDIENTS)), HttpMethod.DELETE, new HttpEntity(Collections.singletonList("chocolate glaze"), reqHeaders), Void.class);
        assertEquals(HttpStatus.OK, delIngCorr.getStatusCode());

        List<Ingredient> allIngredients = ingredientRepository.findAll();
        assertEquals(8, allIngredients.size());

        // delete-ingredients, error not found
        ResponseEntity<Void> delIngErrNotFound =
                restTemplate.exchange(URI.create(baseUrl.concat(ADMIN_REST).concat(ADMIN_DELETE_INGREDIENTS)), HttpMethod.DELETE, new HttpEntity(Collections.singletonList("chocolate glaze"), reqHeaders), Void.class);
        assertEquals(HttpStatus.NOT_FOUND, delIngErrNotFound.getStatusCode());
        assertTrue(delIngErrNotFound.getHeaders().get("Exception").toString().contains("not found"));

        // delete-ingredients, error ingredients belongs to a donut
        ResponseEntity<Void> delIngErrDon =
                restTemplate.exchange(URI.create(baseUrl.concat(ADMIN_REST).concat(ADMIN_DELETE_INGREDIENTS)), HttpMethod.DELETE, new HttpEntity(Collections.singletonList("sugar glaze"), reqHeaders), Void.class);
        assertEquals(HttpStatus.NOT_FOUND, delIngErrDon.getStatusCode());
        assertTrue(delIngErrDon.getHeaders().get("Exception").toString().contains("Cannot delete " +
                "ingredients"));
    }
}
