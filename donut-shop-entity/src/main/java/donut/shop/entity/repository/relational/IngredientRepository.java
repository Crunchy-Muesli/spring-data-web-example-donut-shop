package donut.shop.entity.repository.relational;

import donut.shop.entity.relational.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IngredientRepository extends JpaRepository<Ingredient,String> {

}
