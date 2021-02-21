package donut.shop.rest.service;

import donut.shop.entity.relational.Donut;
import donut.shop.entity.relational.Ingredient;
import donut.shop.entity.repository.relational.DonutRepository;
import donut.shop.entity.repository.relational.IngredientRepository;
import javassist.NotFoundException;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, classes = {
        AdminService.class,
        DonutRepository.class,
        IngredientRepository.class
})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AdminServiceTest {

    @MockBean
    private DonutRepository donutRepository;

    @MockBean
    private IngredientRepository ingredientRepository;

    @Autowired
    private AdminService adminService;

    @Test
    public void getIngredientsTest() {
        Ingredient ingredient = new Ingredient();
        ingredient.setIngredientId(1);
        ingredient.setName("cream");

        doReturn(Collections.singletonList(ingredient)).when(ingredientRepository).findAll();

        List<Ingredient> result = adminService.getIngredients();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(ingredient, result.get(0));
    }

    @Test
    public void newDonutTest() {
        Ingredient ingredient = new Ingredient();
        ingredient.setIngredientId(1);
        ingredient.setName("cream");

        Donut donut = new Donut();
        donut.setName("donut");
        donut.setPrice("2.555");
        donut.setIngredients(Set.of(ingredient));
        donut.setDescription("good donut");

        doReturn(Optional.of(ingredient)).when(ingredientRepository).findByName(any(String.class));
        doReturn(donut).when(donutRepository).save(any(Donut.class));

        Donut result = adminService.newDonut(donut);

        assertNotNull(result);
        assertEquals(donut, result);

        //Wrong price
        donut.setPrice("1000");
        assertThrows(RuntimeException.class, () -> adminService.newDonut(donut));

        //Ingredient not found
        donut.setPrice("2");
        doThrow(new RuntimeException()).when(ingredientRepository).findByName(any(String.class));
        assertThrows(RuntimeException.class, () -> adminService.newDonut(donut));
    }

    @Test
    public void updateDonutTest() {
        Ingredient ingredient = new Ingredient();
        ingredient.setIngredientId(1);
        ingredient.setName("cream");

        Donut donut = new Donut();
        donut.setPrice("3");
        donut.setIngredients(Set.of(ingredient));

        doReturn(Optional.of(donut)).when(donutRepository).findByName(any(String.class));
        doReturn(Optional.of(ingredient)).when(ingredientRepository).findByName(any(String.class));
        doReturn(donut).when(donutRepository).save(any(Donut.class));

        Donut result = adminService.updateDonut("name", donut);
        assertNotNull(result);
        assertEquals(donut, result);

        //Donut not found
        doThrow(new RuntimeException()).when(donutRepository).findByName(any(String.class));
        assertThrows(RuntimeException.class, () -> adminService.updateDonut("name", donut));

        //Ingredient not found
        doThrow(new RuntimeException()).when(ingredientRepository).findByName(any(String.class));
        assertThrows(RuntimeException.class, () -> adminService.updateDonut("name", donut));
    }

    @Test
    public void deleteDonutTest() throws NotFoundException {
        Donut donut = new Donut();

        doReturn(Optional.of(donut)).when(donutRepository).findByName(any(String.class));

        adminService.deleteDonut("name");

        //Donut not found
        doThrow(new RuntimeException()).when(donutRepository).findByName(any(String.class));
        assertThrows(RuntimeException.class, () -> adminService.deleteDonut("name"));
    }

    @Test
    public void addIngredientsTest() {
        Ingredient ingredient1 = new Ingredient();
        ingredient1.setName("one");
        Ingredient ingredient2 = new Ingredient();
        ingredient2.setName("two");

        doReturn(Arrays.asList(ingredient1, ingredient2)).when(ingredientRepository).saveAll(any(List.class));

        List<Ingredient> result = adminService.addIngredients(Arrays.asList(ingredient1, ingredient2));

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(ingredient1, result.get(0));
        assertEquals(ingredient2, result.get(1));
    }

    @Test
    public void deleteIngredientsTest() {
        Ingredient ingredient1 = new Ingredient();
        ingredient1.setName("one");

        Donut donut = new Donut();
        donut.setIngredients(Set.of(ingredient1));

        doReturn(Optional.of(ingredient1)).when(ingredientRepository).findByName(any(String.class));

        adminService.deleteIngredients(Arrays.asList("one"));

        //Ingredients are used
        doReturn(Arrays.asList(donut)).when(donutRepository).findAll();
        assertThrows(RuntimeException.class, () -> adminService.deleteIngredients(Arrays.asList("one")));
    }
}
