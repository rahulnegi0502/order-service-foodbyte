package com.foodbyte.inventory.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "warehouse_stock",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {"warehouse_id", "product_id"},
                        name = "uk_warehouse_stock"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WarehouseStock {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    @Builder.Default
    private int quantity = 0;

    @Column(nullable = false)
    @Builder.Default
    private int reservedQuantity = 0;

    @Column(nullable = false)
    @Builder.Default
    private int lowStockThreshold = 10;

    @Column(nullable = false)
    @Builder.Default
    private boolean isAvailable = true;

    private LocalDateTime lastRestockedAt;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // ── Calculated field — never stored ──────

    public int getAvailableQuantity() {
        return this.quantity - this.reservedQuantity;
    }

    // ── Business logic helpers ────────────────

    public boolean hasEnoughStock(int requestedQuantity) {
        return getAvailableQuantity() >= requestedQuantity;
    }

    public boolean isLowStock() {
        return getAvailableQuantity() <= lowStockThreshold;
    }
}