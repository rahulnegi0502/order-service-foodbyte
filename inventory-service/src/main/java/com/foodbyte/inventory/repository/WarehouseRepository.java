package com.foodbyte.inventory.repository;

import com.foodbyte.inventory.entity.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, UUID> {

    // find active warehouses in same city
    // then calculate distance in service layer
    List<Warehouse> findByCityAndIsActiveTrue(String city);

    // find by pincode — even faster than city
    List<Warehouse> findByPincodeAndIsActiveTrue(String pincode);

    // check duplicate warehouse name in same city
    boolean existsByNameAndCity(String name, String city);

    // get all active warehouses — admin dashboard
    List<Warehouse> findByIsActiveTrue();
}