package donut.shop.rest;

import donut.shop.entity.relational.Donut;
import donut.shop.entity.relational.Ingredient;
import donut.shop.rest.service.AdminService;
import javassist.NotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, classes = {
        AdminRest.class
})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class AdminRestTest {

    @MockBean
    private AdminService adminService;

    @Autowired
    private AdminRest adminRest;

    @Test
    public void getIngredientsTest() {
        Ingredient ingredient = new Ingredient();
        ingredient.setIngredientId(1);
        ingredient.setName("cream");

        doReturn(Collections.singletonList(ingredient)).when(adminService).getIngredients();

        ResponseEntity<List<Ingredient>> result = adminRest.getIngredients();

        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(1, result.getBody().size());
        assertEquals(ingredient, result.getBody().get(0));

        doThrow(new RuntimeException()).when(adminService).getIngredients();

        ResponseEntity<List<Ingredient>> errorResult = adminRest.getIngredients();

        assertNotNull(errorResult);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, errorResult.getStatusCode());
        assertNull(errorResult.getBody());
    }

    @Test
    public void newDonutTest() {
        Donut donut = new Donut();
        donut.setDonutId(1);
        donut.setName("name");

        doReturn(donut).when(adminService).newDonut(any(Donut.class));

        ResponseEntity<Donut> result = adminRest.newDonut(new Donut());

        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(donut, result.getBody());

        doThrow(new RuntimeException()).when(adminService).newDonut(any(Donut.class));

        ResponseEntity<Donut> errorResult = adminRest.newDonut(new Donut());

        assertNotNull(errorResult);
        assertEquals(HttpStatus.BAD_REQUEST, errorResult.getStatusCode());
        assertNull(errorResult.getBody());
    }

    @Test
    public void updateDonutTest() {
        Donut donut = new Donut();
        donut.setDonutId(1);
        donut.setName("name");

        doReturn(donut).when(adminService).updateDonut(any(String.class), any(Donut.class));

        ResponseEntity<Donut> result = adminRest.updateDonut("name", new Donut());

        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(donut, result.getBody());

        doThrow(new RuntimeException()).when(adminService).updateDonut(any(String.class), any(Donut.class));

        ResponseEntity<Donut> errorResult = adminRest.updateDonut("name", new Donut());

        assertNotNull(errorResult);
        assertEquals(HttpStatus.NOT_FOUND, errorResult.getStatusCode());
        assertNull(errorResult.getBody());
    }

    @Test
    public void deleteDonutTest() throws NotFoundException {

        ResponseEntity<Void> result = adminRest.deleteDonut("donut");

        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNull(result.getBody());

        doThrow(new RuntimeException()).when(adminService).deleteDonut(any(String.class));

        ResponseEntity<Void> errorResult = adminRest.deleteDonut("donut");

        assertNotNull(errorResult);
        assertEquals(HttpStatus.NOT_FOUND, errorResult.getStatusCode());
        assertNull(errorResult.getBody());
    }

    @Test
    public void addIngredientsTest() {
        Ingredient ingredient1 = new Ingredient();
        ingredient1.setName("one");
        Ingredient ingredient2 = new Ingredient();
        ingredient2.setName("two");

        doReturn(Arrays.asList(ingredient1, ingredient2)).when(adminService).addIngredients(any(List.class));

        ResponseEntity<List<Ingredient>> result = adminRest.addIngredients(Arrays.asList(ingredient1,
                ingredient2));

        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(2, result.getBody().size());
        assertEquals(ingredient1, result.getBody().get(0));
        assertEquals(ingredient2, result.getBody().get(1));

        doThrow(new RuntimeException()).when(adminService).addIngredients(any(List.class));

        ResponseEntity<List<Ingredient>> errorResult = adminRest.addIngredients(Arrays.asList(ingredient1,
                ingredient2));

        assertNotNull(errorResult);
        assertEquals(HttpStatus.BAD_REQUEST, errorResult.getStatusCode());
        assertNull(errorResult.getBody());
    }

    @Test
    public void deleteIngredientsTest() {

        ResponseEntity<Void> result = adminRest.deleteIngredients(Arrays.asList("one", "two"));

        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNull(result.getBody());

        doThrow(new RuntimeException()).when(adminService).deleteIngredients(any(List.class));

        ResponseEntity<Void> errorResult = adminRest.deleteIngredients(Arrays.asList("one", "two"));

        assertNotNull(errorResult);
        assertEquals(HttpStatus.NOT_FOUND, errorResult.getStatusCode());
        assertNull(errorResult.getBody());
    }


}
