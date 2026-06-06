package com.foodbyte.inventory.repository;

import com.foodbyte.inventory.entity.StockSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface StockSnapshotRepository
        extends JpaRepository<StockSnapshot, UUID> {

    // all snapshots for a warehouse+product combination
    List<StockSnapshot> findByWarehouseStockId(UUID warehouseStockId);

    // snapshots for a product across all warehouses
    @Query("SELECT ss FROM StockSnapshot ss " +
            "WHERE ss.warehouseStock.product.id = :productId " +
            "ORDER BY ss.snapshotTime DESC")
    List<StockSnapshot> findByProductId(
            @Param("productId") UUID productId
    );

    // snapshots for a warehouse across all products
    @Query("SELECT ss FROM StockSnapshot ss " +
            "WHERE ss.warehouseStock.warehouse.id = :warehouseId " +
            "ORDER BY ss.snapshotTime DESC")
    List<StockSnapshot> findByWarehouseId(
            @Param("warehouseId") UUID warehouseId
    );

    // find snapshot closest to a specific time
    // used for historical stock level queries
    @Query("SELECT ss FROM StockSnapshot ss " +
            "WHERE ss.warehouseStock.id = :warehouseStockId " +
            "AND ss.snapshotTime <= :time " +
            "ORDER BY ss.snapshotTime DESC")
    Optional<StockSnapshot> findLatestBeforeTime(
            @Param("warehouseStockId") UUID warehouseStockId,
            @Param("time") LocalDateTime time
    );

    // snapshots within date range
    @Query("SELECT ss FROM StockSnapshot ss " +
            "WHERE ss.warehouseStock.id = :warehouseStockId " +
            "AND ss.snapshotTime BETWEEN :from AND :to " +
            "ORDER BY ss.snapshotTime ASC")
    List<StockSnapshot> findByWarehouseStockIdAndTimeRange(
            @Param("warehouseStockId") UUID warehouseStockId,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to
    );

    // check if snapshot already exists for today
    // prevents duplicate snapshots
    boolean existsByWarehouseStockIdAndSnapshotTimeBetween(
            UUID warehouseStockId,
            LocalDateTime start,
            LocalDateTime end
    );
}