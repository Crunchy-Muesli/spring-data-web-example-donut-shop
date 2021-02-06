package donut.shop.rest;

import donut.shop.entity.relational.Donut;
import donut.shop.entity.relational.Ingredient;
import donut.shop.rest.service.AdminService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/")
public class AdminRest {

    Logger logger = LoggerFactory.getLogger(AdminRest.class);

    private AdminService adminService;

    @Autowired
    public AdminRest(AdminService service) {
        this.adminService = service;
    }

    @PostMapping("new-donut")
    public ResponseEntity<Donut> newDonut(@RequestBody Donut req) {
        try {
            Donut res = adminService.newDonut(req);
            return ResponseEntity.ok(res);
        }
        catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PatchMapping("update-donut")
    public ResponseEntity<Donut> updateDonut(@RequestBody Donut req) {
        try {
            Donut res = adminService.updateDonut(req);
            return ResponseEntity.ok(res);
        }
        catch (Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @DeleteMapping("delete-donut")
    public ResponseEntity<Void> deleteDonut(@RequestBody Donut req) {
        try {
            adminService.deleteDonut(req);
            return ResponseEntity.ok(null);
        }
        catch (Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @PostMapping("add-ingredients")
    public ResponseEntity<List<Ingredient>> addIngredients(@RequestBody List<Ingredient> req) {
        try {
            List<Ingredient> res = adminService.addIngredients(req);
            return ResponseEntity.ok(res);
        }
        catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }


}
