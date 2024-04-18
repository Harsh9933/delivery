package com.kitchen.delivery.Controller;


import com.kitchen.delivery.DAO.DishesDao;
import com.kitchen.delivery.Model.Dishes;
import com.kitchen.delivery.Service.DishesService;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.http.HttpResponse;
import java.util.List;

@RestController
public class DishController {


    @Autowired
    DishesService dishesService;

    @GetMapping("/allDishes")
    public List<Dishes> getDishes(){
        return dishesService.getDishes();
    }
    @PostMapping("/dishes")
    public Dishes addDishes(@ModelAttribute Dishes dishes , @RequestParam(value = "file") MultipartFile file){
        return dishesService.addDishes(dishes , file);
    }

    @PutMapping("/dishes")
    public Dishes updateDishes(@RequestBody Dishes dishes){
        return dishesService.updateDishes(dishes);
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<HttpStatus> deleteDish(@PathVariable int id){
        try{
            dishesService.deleteDish(id);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        catch (Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
