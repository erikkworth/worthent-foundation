package com.worthent.foundation.util.state.examples.xml;

import com.worthent.foundation.util.state.etc.obj.ObjectConstructor;
import com.worthent.foundation.util.state.etc.obj.ObjectField;

import java.math.BigDecimal;

/**
 * This data transfer object represents a purchase item in a purchase order.
 *
 * @author Erik K. Worth
 */
public class PurchaseItemData {

    /** Identifies the purchase item in the purchase order */
    private final int itemNumber;

    /** The identifier for the item purchased */
    private final String sku;

    /** The quantity ordered */
    private final int quantity;

    /** The price per item */
    private final BigDecimal price;

    /** The currency identifier */
    private final String currency;

    @ObjectConstructor
    public PurchaseItemData(
            @ObjectField("itemNumber") final int itemNumber,
            @ObjectField("sku") final String sku,
            @ObjectField("quantity") final int quantity,
            @ObjectField("price") final BigDecimal price,
            @ObjectField("currency") final String currency) {
        this.itemNumber = itemNumber;
        this.sku = sku;
        this.quantity = quantity;
        this.price = price;
        this.currency = currency;
    }

    @Override
    public String toString() {
        return "PurchaseItemData{" +
                "itemNumber=" + itemNumber +
                ", sku='" + sku + '\'' +
                ", quantity=" + quantity +
                ", price=" + price +
                ", currency='" + currency + '\'' +
                '}';
    }

    public int getItemNumber() {
        return itemNumber;
    }

    public String getSku() {
        return sku;
    }

    public int getQuantity() {
        return quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public String getCurrency() {
        return currency;
    }

}
