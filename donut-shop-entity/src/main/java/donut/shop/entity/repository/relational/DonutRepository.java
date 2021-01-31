package donut.shop.entity.repository.relational;

import donut.shop.entity.relational.Donut;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DonutRepository extends JpaRepository<Donut,String> {

    Optional<Donut> findByName(String name);

}
