package com.worthent.foundation.util.state.examples.xml;

import com.worthent.foundation.util.state.AbstractStateTableData;
import com.worthent.foundation.util.state.etc.obj.ObjectConstructor;
import com.worthent.foundation.util.state.etc.obj.ObjectField;
import com.worthent.foundation.util.state.etc.xml.XmlData;

import java.util.LinkedList;
import java.util.List;

/**
 * State Table Data Object representing a purchase order parsed from an XML document.
 */
public class PurchaseOrderData extends AbstractStateTableData {

    /** The number of milliseconds from epoch (1/1/1907 00:00:00) when the order was placed */
    private final long purchaseTimestamp;

    /** The account Id for the user that made the purchase */
    private final String accountId;

    /** The purchased items */
    private final List<PurchaseItemData> items;

    /** The tax rate as a percentage */
    private final float taxRate;

    @ObjectConstructor
    public PurchaseOrderData(
            @ObjectField("PurchaseTimestamp") final long purchaseTimestamp,
            @ObjectField("AccountId") final String accountId,
            @ObjectField(value = "Items", elementType = PurchaseItemData.class) final List<PurchaseItemData> items,
            @ObjectField("TaxRate") final float taxRate) {
        this.purchaseTimestamp = purchaseTimestamp;
        this.accountId = accountId;
        this.items = items;
        this.taxRate = taxRate;
    }

    @Override
    public String toString() {
        return "PurchaseOrderData{" +
                "purchaseTimestamp=" + purchaseTimestamp +
                ", accountId='" + accountId + '\'' +
                ", items=" + items +
                ", taxRate=" + taxRate +
                '}';
    }

    public long getPurchaseTimestamp() {
        return purchaseTimestamp;
    }

    public String getAccountId() {
        return accountId;
    }

    public List<PurchaseItemData> getItems() {
        return items;
    }

    public float getTaxRate() {
        return taxRate;
    }

}
