package com.foodbyte.inventory.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "stock_adjustments",
        indexes = {
                @Index(name = "idx_adj_warehouse_stock_id",
                        columnList = "warehouse_stock_id"),
                @Index(name = "idx_adj_reference_id",
                        columnList = "reference_id"),
                @Index(name = "idx_adj_type",
                        columnList = "adjustment_type")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockAdjustment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_stock_id", nullable = false)
    private WarehouseStock warehouseStock;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AdjustmentType adjustmentType;

    @Column(nullable = false)
    private int quantityChange;

    @Column(nullable = false)
    private int previousQuantity;

    @Column(nullable = false)
    private int newQuantity;

    // orderId or restockId — what caused this change
    @Column(length = 100)
    private String referenceId;

    @Column(length = 255)
    private String reason;

    // userId from User Service — plain UUID, no FK
    private UUID createdBy;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
}