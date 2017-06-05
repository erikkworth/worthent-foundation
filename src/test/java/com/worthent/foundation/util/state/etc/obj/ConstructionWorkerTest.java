package com.worthent.foundation.util.state.etc.obj;

import com.google.common.collect.ImmutableMap;
import com.worthent.foundation.util.state.examples.xml.PurchaseItemData;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for the Construction Worker utility.
 *
 * @author Erik K. Worth
 */
public class ConstructionWorkerTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConstructionWorkerTest.class);

    private static final int TEST_ITEM_NUMBER = 1;
    private static final String TEST_SKU = "testSku";
    private static final int TEST_QUANTITY = 2;
    private static final BigDecimal TEST_PRICE = new BigDecimal("12.35");
    private static final String TEST_CURRENCY = "USD";
    private static final Map<String, Object> TEST_PURCHASE_ITEM_FIELDS = new ImmutableMap.Builder<String, Object>()
            .put("itemnumber", Integer.toString(TEST_ITEM_NUMBER))
            .put("sku", TEST_SKU)
            .put("quantity", Integer.toString(TEST_QUANTITY))
            .put("price", TEST_PRICE.toString())
            .put("currency", TEST_CURRENCY)
            .build();

    @Rule
    public TestWatcher watchman= new TestWatcher() {
        @Override
        public void starting(final Description description) {
            LOGGER.debug("Starting test {}", description.getMethodName());
        }
    };

    @Test
    public void constructObjectWithSimpleFields() throws Exception {
        final ConstructionWorker purchaseItemDataConstructionWorker =
                new ConstructionWorker(PurchaseItemData.class);
        final PurchaseItemData purchaseItemData = (PurchaseItemData) purchaseItemDataConstructionWorker.newObject(TEST_PURCHASE_ITEM_FIELDS);
        assertThat(purchaseItemData.getItemNumber()).isEqualTo(TEST_ITEM_NUMBER);
        assertThat(purchaseItemData.getSku()).isEqualTo(TEST_SKU);
        assertThat(purchaseItemData.getQuantity()).isEqualTo(TEST_QUANTITY);
        assertThat(purchaseItemData.getPrice()).isEqualTo(TEST_PRICE);
        assertThat(purchaseItemData.getCurrency()).isEqualTo(TEST_CURRENCY);
    }
}
