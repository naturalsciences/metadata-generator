/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.naturalsciences.bmdc.metadata.datacite;

import be.naturalsciences.bmdc.metadata.iso.ISO19115DatasetPrinter;
import demo.datacite.mds.account.Account;
import demo.datacite.mds.doi.GetDOI;
import demo.datacite.mds.metadata.GetMetadata;
import demo.http.client.HTTPResponse;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.sis.metadata.InvalidMetadataException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;

/**
 *
 * @author thomas
 */
public class PostIsoMetadataTest {

    public static final Account TEST_RBINS = new Account("DELFT.RBINS", "RBINS1846", "10.5072");

    ClassLoader classLoader;
    File testFile1;
    File testFile2;

    public PostIsoMetadataTest() {
        classLoader = getClass().getClassLoader();
        testFile1 = new File(classLoader.getResource("fisheries_zones.xml").getFile());
        testFile2 = new File(classLoader.getResource("ds_1022.xml").getFile());
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of execute method, of class ISO19115toDataCitePublisher.
     */
    @Test
    @Ignore
    public void testExecute() throws Exception {
        System.out.println("execute");
        innerTestExecute(testFile1, "e2aaecaf-b555-405e-ae77-dfe1b07e0f17");
        innerTestExecute(testFile2, "bmdc.be:dataset:1022"); //will fail as there are no authors!
    }

    private void innerTestExecute(File testFile, String id) throws Exception {
        try {
            ISO19115toDataCitePublisher post = new ISO19115toDataCitePublisher(TEST_RBINS);
            post.setIsoMetadata(testFile);
            HTTPResponse postResult = post.execute();
            System.out.println(post.getDCMetadata());
            System.out.println(postResult);
            assertTrue(postResult.toString().contains("OK (10.5072/" + id.toUpperCase() + ")"));
            GetMetadata getMetadata = new GetMetadata(TEST_RBINS, "10.5072/" + id);
            HTTPResponse getResult = getMetadata.execute();

            assertTrue(getResult.toString().contains("<datacite:identifier identifierType=\"DOI\">10.5072/" + id + "</datacite:identifier>"));
            post.updateOriginalMetadata();
            String isoMetadata = post.getISOMetadata();
            assertTrue(isoMetadata.contains("<gmx:Anchor xlink:href=\"https://doi.org/10.5072/" + id + "\">10.5072/" + id + "</gmx:Anchor>"));

            GetDOI getDoi = new GetDOI(TEST_RBINS, "10.5072/" + id);
            HTTPResponse execute = getDoi.execute();
            assertTrue(execute.toString().contains("http://metadata.naturalsciences.be/" + id));
        } catch (InvalidMetadataException ex) {
            Logger.getLogger(ISO19115DatasetPrinter.class.getName()).log(Level.INFO, ex.getMessage());
        }

    }

}
