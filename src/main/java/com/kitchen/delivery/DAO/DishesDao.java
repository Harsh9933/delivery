package com.kitchen.delivery.DAO;

import com.kitchen.delivery.Model.Dishes;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DishesDao extends JpaRepository<Dishes , Integer> {
}
