package com.foodbyte.inventory.entity;

public enum AdjustmentType {
    ORDER,          // order placed — deduct stock
    RESTOCK,        // new stock arrived — increase
    DAMAGED,        // damaged goods — decrease
    CORRECTION,     // manual correction by admin
    RESERVATION,    // stock reserved for pending order
    RELEASE         // reservation released on cancellation
}