package com.worthent.foundation.util.state.examples.xml;

import com.worthent.foundation.util.state.StateTableControl;
import com.worthent.foundation.util.state.etc.obj.ObjectConstructionController;
import com.worthent.foundation.util.state.etc.xml.SaxEventAdapter;
import com.worthent.foundation.util.state.etc.xml.XmlEvent;
import com.worthent.foundation.util.state.etc.xml.XmlObjectBuilderAdapter;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.InputStream;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertNotNull;

/**
 * Unit test for the SAX Event Adapter and the object builder.
 *
 * @author Erik K. Worth
 */
public class SaxEventAdapterTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(SaxEventAdapterTest.class);

    private static final String TEST_XML_PATH = "PurchaseOrder.xml";

    @Rule
    public TestWatcher watchman= new TestWatcher() {
        @Override
        public void starting(final Description description) {
            LOGGER.debug("Starting test {}", description.getMethodName());
        }
    };

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
        LOGGER.debug("Purchase Orders: {}", purchaseOrders);
    }
}
