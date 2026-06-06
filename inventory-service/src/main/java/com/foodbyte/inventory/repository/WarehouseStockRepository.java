package com.foodbyte.inventory.repository;

import com.foodbyte.inventory.entity.WarehouseStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WarehouseStockRepository
        extends JpaRepository<WarehouseStock, UUID> {

    // get specific product stock in specific warehouse
    // used when order placed — check availability
    Optional<WarehouseStock> findByWarehouseIdAndProductId(
            UUID warehouseId, UUID productId
    );

    // all available products in a warehouse
    // used for product listing page
    List<WarehouseStock> findByWarehouseIdAndIsAvailableTrue(
            UUID warehouseId
    );

    // all stock records for a product across all warehouses
    // used for admin — where is this product stocked?
    List<WarehouseStock> findByProductId(UUID productId);

    // all low stock items across all warehouses
    // used for restock alerts
    @Query("SELECT ws FROM WarehouseStock ws " +
            "WHERE (ws.quantity - ws.reservedQuantity) " +
            "<= ws.lowStockThreshold " +
            "AND ws.isAvailable = true")
    List<WarehouseStock> findAllLowStockItems();

    // all stock for a warehouse — admin dashboard
    List<WarehouseStock> findByWarehouseId(UUID warehouseId);
}