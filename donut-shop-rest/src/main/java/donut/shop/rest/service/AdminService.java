package donut.shop.rest.service;

import donut.shop.entity.relational.Donut;
import donut.shop.entity.relational.Ingredient;
import donut.shop.entity.repository.relational.DonutRepository;
import donut.shop.entity.repository.relational.IngredientRepository;
import javassist.NotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AdminService {

    private DonutRepository donutRepository;
    private IngredientRepository ingredientRepository;

    @Autowired
    public AdminService(DonutRepository donutRepository, IngredientRepository ingredientRepository) {
        this.donutRepository = donutRepository;
        this.ingredientRepository = ingredientRepository;
    }

    public Donut newDonut(Donut donut) {

        if(countDecimal(donut.getPrice())>2) throw new RuntimeException("Must be a real price");
        if(countIntegers(donut.getPrice())>1) throw new RuntimeException("How much do you think a donut costs?!");

        Set<Ingredient> realIngredients = getRealIngredients(donut.getIngredients());

        donut.setIngredients(realIngredients);
        return donutRepository.save(donut);
    }

    public Donut updateDonut(Donut req){
        Donut donut = donutRepository.findByName(req.getName())
                .orElseThrow(() -> new RuntimeException("Donut with name " + req.getName() + " not found"));

        donut.setDescription(req.getDescription());
        donut.setPrice(req.getPrice());
        donut.setIngredients(getRealIngredients(req.getIngredients()));

        return donut;
    }

    public void deleteDonut(Donut req) throws NotFoundException {
        Donut donut = donutRepository.findByName(req.getName())
                .orElseThrow(() -> new NotFoundException("Donut with name " + req.getName() + " not found"));

        donutRepository.delete(donut);
    }

    public List<Ingredient> addIngredients(List<Ingredient> ingredients) {
        return ingredientRepository.saveAll(ingredients);
    }

    private Set<Ingredient> getRealIngredients(Set<Ingredient> ingredients) {
        return ingredients.stream()
                .map(ingredient -> ingredientRepository.findByName(ingredient.getName())
                        .orElseThrow(() -> new RuntimeException("Ingredient with name " + ingredient.getName() + " not found")))
                .collect(Collectors.toSet());
    }

    private Integer countDecimal(BigDecimal number){
        return Math.max(0, number.stripTrailingZeros().scale());
    }

    private Integer countIntegers(BigDecimal number){
        number = number.stripTrailingZeros();
        return number.precision() - number.scale();
    }


}
