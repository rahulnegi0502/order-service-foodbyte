package com.foodbyte.inventory.repository;

import com.foodbyte.inventory.entity.AdjustmentType;
import com.foodbyte.inventory.entity.StockAdjustment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface StockAdjustmentRepository
        extends JpaRepository<StockAdjustment, UUID> {

    // all adjustments for a warehouse stock record
    List<StockAdjustment> findByWarehouseStockId(UUID warehouseStockId);

    // find by orderId or restockId
    // used for order dispute resolution
    List<StockAdjustment> findByReferenceId(String referenceId);

    // filter by adjustment type
    // used for analytics — all ORDER type adjustments
    List<StockAdjustment> findByAdjustmentType(AdjustmentType type);

    // analytics — all adjustments for specific product
    // joins through warehouseStock → product
    @Query("SELECT sa FROM StockAdjustment sa " +
            "WHERE sa.warehouseStock.product.id = :productId")
    List<StockAdjustment> findByProductId(
            @Param("productId") UUID productId
    );

    // all adjustments for a warehouse
    // joins through warehouseStock → warehouse
    @Query("SELECT sa FROM StockAdjustment sa " +
            "WHERE sa.warehouseStock.warehouse.id = :warehouseId")
    List<StockAdjustment> findByWarehouseId(
            @Param("warehouseId") UUID warehouseId
    );

    // time range query — "show me today's adjustments"
    @Query("SELECT sa FROM StockAdjustment sa " +
            "WHERE sa.warehouseStock.warehouse.id = :warehouseId " +
            "AND sa.createdAt BETWEEN :from AND :to")
    List<StockAdjustment> findByWarehouseIdAndDateRange(
            @Param("warehouseId") UUID warehouseId,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to
    );

    // analytics — units sold for specific product
    @Query("SELECT SUM(ABS(sa.quantityChange)) FROM StockAdjustment sa " +
            "WHERE sa.warehouseStock.product.id = :productId " +
            "AND sa.adjustmentType = 'ORDER' " +
            "AND sa.createdAt BETWEEN :from AND :to")
    Integer getTotalUnitsSold(
            @Param("productId") UUID productId,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to
    );
}