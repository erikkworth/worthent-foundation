package com.worthent.foundation.util.state.examples.xml;

import java.math.BigDecimal;

/**
 * This data transfer object represents a purchase item in a purchase order.
 *
 * @author Erik K. Worth
 */
public class PurchaseItemData {

    /** Identifies the purchase item in the purchase order */
    private int itemNumber;

    /** The identifier for the item purchased */
    private String sku;

    /** The quantity ordered */
    private int quantity;

    /** The price per item */
    private BigDecimal price;

    /** The currency identifier */
    private String currency;

    public int getItemNumber() {
        return itemNumber;
    }

    public void setItemNumber(int itemNumber) {
        this.itemNumber = itemNumber;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
