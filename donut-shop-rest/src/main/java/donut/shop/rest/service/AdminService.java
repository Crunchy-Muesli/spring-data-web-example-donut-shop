package donut.shop.rest.service;

import donut.shop.entity.dto.DonutDto;
import donut.shop.entity.dto.IngredientDto;
import donut.shop.entity.relational.Donut;
import donut.shop.entity.relational.Ingredient;
import donut.shop.entity.repository.relational.DonutRepository;
import donut.shop.entity.repository.relational.IngredientRepository;
import javassist.NotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class AdminService {

    private DonutRepository donutRepository;
    private IngredientRepository ingredientRepository;
    private ModelMapper modelMapper;

    @Autowired
    public AdminService(DonutRepository donutRepository, IngredientRepository ingredientRepository, ModelMapper modelMapper){
        this.donutRepository = donutRepository;
        this.ingredientRepository = ingredientRepository;
        this.modelMapper = modelMapper;
    }

    public Donut newDonut(DonutDto req) {
        Donut donut = modelMapper.map(req,Donut.class);
        return donutRepository.save(donut);
    }

    public Donut updateDonut(DonutDto req) throws NotFoundException {
        Donut donut = donutRepository.findByName(req.getName())
                .orElseThrow(() -> new NotFoundException("Donut with name " + req.getName() + " not found"));

        donut.setDescription(req.getDescription());
        donut.setPrice(req.getPrice());
        donut.setIngredients(Arrays.asList(modelMapper.map(req.getIngredients(), Ingredient[].class)));

        return donut;
    }

    public void deleteDonut(DonutDto req) throws NotFoundException {
        Donut donut = donutRepository.findByName(req.getName())
                .orElseThrow(() -> new NotFoundException("Donut with name " + req.getName() + " not found"));

        donutRepository.delete(donut);
    }

    public List<Ingredient> addIngredients(List<IngredientDto> req){

        List<Ingredient> ingredients = Arrays.asList(modelMapper.map(req,Ingredient[].class));
        return ingredientRepository.saveAll(ingredients);
    }

}
