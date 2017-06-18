package com.worthent.foundation.util.state.examples.xml;

import com.worthent.foundation.util.state.StateTableControl;
import com.worthent.foundation.util.state.etc.obj.ObjectConstructionController;
import com.worthent.foundation.util.state.etc.xml.SaxEventAdapter;
import com.worthent.foundation.util.state.etc.xml.XmlEvent;
import com.worthent.foundation.util.state.etc.xml.XmlObjectBuilderAdapter;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;
import java.nio.charset.Charset;

/**
 * Unit test for the SAX Event Adapter and the object builder.
 *
 * @author Erik K. Worth
 */
public class SaxEventAdapterTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(SaxEventAdapterTest.class);

    private static final Charset UTF_8 = Charset.forName("UTF-8");

    private static final String TEST_XML_PATH = "PurchaseOrder.xml";

    @Rule
    public TestWatcher watchman= new TestWatcher() {
        @Override
        public void starting(final Description description) {
            LOGGER.debug("Starting test {}", description.getMethodName());
        }
    };

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    /** The list of purchase orders received */
    private List<PurchaseOrderData> purchaseOrders;

    /** The state table control object */
    private StateTableControl<XmlEvent> stateTableControl;

    /** The class being tested here */
    private SaxEventAdapter saxEventAdapter;

    @Before
    public void setup() throws Exception {
        purchaseOrders = new LinkedList<>();
        stateTableControl = new XmlObjectBuilderAdapter(
                new ObjectConstructionController<>(PurchaseOrderData.class, purchaseOrders::add));
        saxEventAdapter = new SaxEventAdapter(stateTableControl);
    }

    @Test
    public void processSaxEventsAndVerify() throws Exception {
        final URL url = this.getClass().getClassLoader().getResource(TEST_XML_PATH);
        assertNotNull("Cannot load " + TEST_XML_PATH, url);
        try (final InputStream inputStream = url.openStream()) {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            saxParser.parse(inputStream, saxEventAdapter);
        }

        // Validate Purchase Order
        LOGGER.debug("Purchase Orders: {}", purchaseOrders);
        assertThat(purchaseOrders.size()).isEqualTo(1);
        final PurchaseOrderData purchaseOrderData = purchaseOrders.get(0);
        assertThat(purchaseOrderData.getPurchaseTimestamp()).isEqualTo(1234567890L);
        assertThat(purchaseOrderData.getAccountId()).isEqualTo("ABCDEFGHIJK");
        assertThat(purchaseOrderData.getTaxRate()).isEqualTo(8.25F);
        final List<PurchaseItemData> items = purchaseOrderData.getItems();
        final Iterator<PurchaseItemData> itemIterator = items.iterator();
        assertThat(itemIterator.hasNext()).isTrue();
        PurchaseItemData item = itemIterator.next();
        assertThat(item.getItemNumber()).isEqualTo(1);
        assertThat(item.getSku()).isEqualTo("YXY-123");
        assertThat(item.getQuantity()).isEqualTo(1);
        assertThat(item.getPrice()).isEqualTo(new BigDecimal("50.25"));
        assertThat(item.getCurrency()).isEqualTo("USD");
        item = itemIterator.next();
        assertThat(item.getItemNumber()).isEqualTo(2);
        assertThat(item.getSku()).isEqualTo("YXY-555");
        assertThat(item.getQuantity()).isEqualTo(2);
        assertThat(item.getPrice()).isEqualTo(new BigDecimal("70.00"));
        assertThat(item.getCurrency()).isEqualTo("USD");
    }

    @Test
    public void processXmlWithErrorAndVerify() throws Exception {
        thrown.expect(SAXException.class);
        final String badXml =
                "<PurchaseOrderData>\n" +
                "    <!-- This element should have a long integer value -->\n" +
                "    <PurchaseTimestamp>not-a-number</PurchaseTimestamp>\n" +
                "</PurchaseOrderData>\n";
        try (final InputStream inputStream = new ByteArrayInputStream(badXml.getBytes(UTF_8))) {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            saxParser.parse(inputStream, saxEventAdapter);
        }
    }
}
