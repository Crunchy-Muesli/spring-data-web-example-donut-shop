package donut.shop.rest.service;

import donut.shop.entity.relational.Donut;
import donut.shop.entity.relational.Ingredient;
import donut.shop.entity.repository.relational.DonutRepository;
import donut.shop.entity.repository.relational.IngredientRepository;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
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

    public List<Ingredient> getIngredients(){
        return ingredientRepository.findAll();
    }


    public Donut newDonut(Donut donut) {
        donut.setPrice(checkAndRoundDonutPrice(donut.getPrice()));
        Set<Ingredient> realIngredients = getRealIngredients(donut.getIngredients());

        donut.setIngredients(realIngredients);
        return donutRepository.save(donut);
    }

    public Donut updateDonut(int donutId,Donut req) {
        Donut donut = donutRepository.findByDonutId(donutId)
                .orElseThrow(() -> new RuntimeException("Donut with name " + req.getName() + " not found"));

        donut.setDescription(req.getDescription());
        donut.setName(req.getName());
        donut.setPrice(checkAndRoundDonutPrice(req.getPrice()));
        donut.setIngredients(getRealIngredients(req.getIngredients()));
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

    private Set<Ingredient> getRealIngredients(Set<Ingredient> ingredients) {
        return ingredients.stream()
                .map(ingredient -> ingredientRepository.findByName(ingredient.getName())
                        .orElseThrow(() -> new RuntimeException("Ingredient with name " + ingredient.getName() + " not found")))
                .collect(Collectors.toSet());
    }

    private String checkAndRoundDonutPrice(String price) {

        //rounding value to 2 decimal digits
        Double priceDouble = Double.parseDouble(price);

        if (priceDouble > 10 || priceDouble < 0.50) throw new RuntimeException("How much do you think a donut costs Pick a price between 10 and 0.50... coins");
        String rounded = String.format("%.2f", priceDouble);

        return rounded;
    }

    public Set<Ingredient> deleteIngredients(List<Ingredient> ingredients) {
        Set<Ingredient> toBedeleted = getRealIngredients(ingredients.stream().collect(Collectors.toSet()));

        Set<Ingredient> usedIngredients = donutRepository.findAll().stream()
                .flatMap(donut -> donut.getIngredients().stream())
                .collect(Collectors.toSet());

        Set<Ingredient> undeletable = toBedeleted.stream()
                .filter(usedIngredients::contains)
                .collect(Collectors.toSet());

        if(!undeletable.isEmpty()) throw new RuntimeException("Cannot delete ingredients " + undeletable.stream().map(Ingredient::getName).collect(Collectors.joining(",","[","]")) + " since they are still in use");

        ingredientRepository.deleteInBatch(toBedeleted);

        return toBedeleted;
    }

}
