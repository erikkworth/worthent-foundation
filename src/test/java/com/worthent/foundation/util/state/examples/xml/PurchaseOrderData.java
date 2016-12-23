package com.worthent.foundation.util.state.examples.xml;

import com.worthent.foundation.util.state.AbstractStateTableData;
import com.worthent.foundation.util.state.etc.xml.XmlData;

import java.util.LinkedList;
import java.util.List;

/**
 * State Table Data Object representing a purchase order parsed from an XML document.
 */
public class PurchaseOrderData extends AbstractStateTableData {

    /** The number of milliseconds from epoch (1/1/1907 00:00:00) when the order was placed */
    private long purchaseTimestamp;

    /** The account Id for the user that made the purchase */
    private String accountId;

    /** The purchased items */
    private List<PurchaseItemData> items;

    /** The tax rate as a percentage */
    private float taxRate;

    public PurchaseOrderData() {
        items = new LinkedList<>();
    }

    public long getPurchaseTimestamp() {
        return purchaseTimestamp;
    }

    public void setPurchaseTimestamp(long purchaseTimestamp) {
        this.purchaseTimestamp = purchaseTimestamp;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public List<PurchaseItemData> getItems() {
        return items;
    }

    public void addItem(PurchaseItemData item) {
        this.items.add(item);
    }

    public float getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(float taxRate) {
        this.taxRate = taxRate;
    }
}
