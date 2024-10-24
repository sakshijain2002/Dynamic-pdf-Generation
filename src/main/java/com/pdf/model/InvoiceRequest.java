package com.pdf.model;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceRequest {
    private String seller;
    private String sellerGstin;
    private String sellerAddress;
    private String buyer;
    private String buyerGstin;
    private String buyerAddress;
    private List<Item> items;

    // Getters and Setters

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Item {
        private String name;
        private String quantity;
        private double rate;
        private double amount;

        // Getters and Setters
    }
}