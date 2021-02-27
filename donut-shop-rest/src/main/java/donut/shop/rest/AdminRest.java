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

import static donut.shop.constant.DonutShopConstant.*;

@RestController
@RequestMapping(ADMIN_REST)
public class AdminRest {

    Logger logger = LoggerFactory.getLogger(AdminRest.class);

    private AdminService adminService;

    @Autowired
    public AdminRest(AdminService service) {
        this.adminService = service;
    }

    @GetMapping(ADMIN_GET_INGREDIENTS)
    public ResponseEntity<List<Ingredient>> getIngredients() {
        try {
            List<Ingredient> res = adminService.getIngredients();
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .header(EXCEPTION, e.getMessage())
                    .body(null);
        }
    }

    @PostMapping(ADMIN_NEW_DONUT)
    public ResponseEntity<Donut> newDonut(@RequestBody Donut req) {
        try {
            Donut res = adminService.newDonut(req);
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .header(EXCEPTION, e.getMessage())
                    .body(null);
        }
    }

    @PatchMapping(ADMIN_UPDATE_DONUT)
    public ResponseEntity<Donut> updateDonut(@PathVariable("donutName") String donutName,
                                             @RequestBody Donut req) {
        try {
            Donut res = adminService.updateDonut(donutName, req);
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .header(EXCEPTION, e.getMessage())
                    .body(null);
        }
    }

    @DeleteMapping(ADMIN_DELETE_DONUT)
    public ResponseEntity<Void> deleteDonut(@RequestBody String req) {
        try {
            adminService.deleteDonut(req);
            return ResponseEntity.ok(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .header(EXCEPTION, e.getMessage())
                    .body(null);
        }
    }

    @PostMapping(ADMIN_ADD_INGREDIENTS)
    public ResponseEntity<List<Ingredient>> addIngredients(@RequestBody List<Ingredient> req) {
        try {
            List<Ingredient> res = adminService.addIngredients(req);
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .header(EXCEPTION, e.getMessage())
                    .body(null);
        }
    }

    @DeleteMapping(ADMIN_DELETE_INGREDIENTS)
    public ResponseEntity<Void> deleteIngredients(@RequestBody List<String> req) {
        try {
            adminService.deleteIngredients(req);
            return ResponseEntity.ok(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .header(EXCEPTION, e.getMessage())
                    .body(null);
        }
    }


}
