package donut.shop.rest.service;

import donut.shop.entity.relational.Donut;
import donut.shop.entity.relational.Ingredient;
import donut.shop.entity.repository.relational.DonutRepository;
import donut.shop.entity.repository.relational.IngredientRepository;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AdminService {

    private final DonutRepository donutRepository;
    private final IngredientRepository ingredientRepository;

    @Autowired
    public AdminService(DonutRepository donutRepository, IngredientRepository ingredientRepository) {
        this.donutRepository = donutRepository;
        this.ingredientRepository = ingredientRepository;
    }

    public List<Ingredient> getIngredients() {
        return ingredientRepository.findAll();
    }


    public Donut newDonut(Donut donut) {
        donut.setPrice(checkAndRoundDonutPrice(donut.getPrice()));
        Set<Ingredient> realIngredients =
                getRealIngredients(donut.getIngredients()
                        .stream()
                        .map(Ingredient::getName)
                        .collect(Collectors.toSet()));

        donut.setIngredients(realIngredients);
        return donutRepository.save(donut);
    }

    public Donut updateDonut(String name, Donut req) {
        Donut donut = donutRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Donut with name " + name + " not found"));

        donut.setDescription(req.getDescription());
        donut.setPrice(checkAndRoundDonutPrice(req.getPrice()));
        donut.setIngredients(getRealIngredients(req.getIngredients()
                .stream()
                .map(Ingredient::getName)
                .collect(Collectors.toSet())));

        return donutRepository.save(donut);
    }

    public void deleteDonut(String donutName) throws NotFoundException {
        Donut donut = donutRepository.findByName(donutName)
                .orElseThrow(() -> new NotFoundException("Donut with name " + donutName + " not found"));

        donutRepository.delete(donut);
    }

    public List<Ingredient> addIngredients(List<Ingredient> ingredients) {
        return ingredientRepository.saveAll(ingredients);
    }

    public void deleteIngredients(List<String> ingredients) {
        Set<Ingredient> toBeDeleted = getRealIngredients(new HashSet<>(ingredients));

        Set<Ingredient> usedIngredients = donutRepository.findAll().stream()
                .flatMap(donut -> donut.getIngredients().stream())
                .collect(Collectors.toSet());

        Set<Ingredient> undeletable = toBeDeleted.stream()
                .filter(usedIngredients::contains)
                .collect(Collectors.toSet());

        if (!undeletable.isEmpty())
            throw new RuntimeException("Cannot delete ingredients " + undeletable.stream()
                    .map(Ingredient::getName)
                    .collect(Collectors.joining(",", "[", "]"))
                    + " since they are still in use");

        ingredientRepository.deleteInBatch(toBeDeleted);

    }

    private Set<Ingredient> getRealIngredients(Set<String> ingredients) {
        return ingredients.stream()
                .map(ingredient -> ingredientRepository.findByName(ingredient)
                        .orElseThrow(() -> new RuntimeException("Ingredient with name " + ingredient + " " +
                                "not found")))
                .collect(Collectors.toSet());
    }

    private String checkAndRoundDonutPrice(String price) {

        //rounding value to 2 decimal digits
        Double priceDouble = Double.parseDouble(price);

        if (priceDouble > 10 || priceDouble < 0.50)
            throw new RuntimeException("How much do you think a donut costs? Pick a price between 10 and 0" +
                    ".50... coins");

        return String.format("%.2f", priceDouble);
    }


}
