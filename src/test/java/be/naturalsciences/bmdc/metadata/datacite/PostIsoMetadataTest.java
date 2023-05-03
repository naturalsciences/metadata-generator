/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.naturalsciences.bmdc.metadata.datacite;

import be.naturalsciences.bmdc.metadata.iso.ISO19115DatasetBuilder;
import be.naturalsciences.bmdc.metadata.iso.ISO19115DatasetPrinter;
import be.naturalsciences.bmdc.metadata.datacite.mds.account.Account;
import be.naturalsciences.bmdc.metadata.datacite.mds.doi.GetDOI;
import be.naturalsciences.bmdc.metadata.datacite.mds.metadata.GetMetadata;
import be.naturalsciences.bmdc.metadata.datacite.http.client.HTTPResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.sis.metadata.InvalidMetadataException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author thomas
 */
public class PostIsoMetadataTest {

    private static Account getAccount() {
            InputStream input = null;
            Properties prop = new Properties();
            String account = null;
            String password = null;
            String prefix = null;
            try {
                input = ISO19115DatasetBuilder.class.getClassLoader().getResourceAsStream("test.properties");
                prop.load(input);
                account = prop.getProperty("datacite.account");
                password =  prop.getProperty("datacite.password");
                prefix = prop.getProperty("datacite.prefix");
            } catch (IOException ex) {
                ex.printStackTrace();
            } finally {
                if (input != null) {
                    try {
                        input.close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
            return new Account(account, password, prefix);
    }

    public static final Account TEST_ACCOUNT = getAccount();

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
    public void testExecute() throws Exception {
        System.out.println("execute");
        innerTestExecute(testFile1, "e2aaecaf-b555-405e-ae77-dfe1b07e0f17");
        innerTestExecute(testFile2, "bmdc.be:dataset:1022"); //will fail as there are no authors!
    }

    private void innerTestExecute(File testFile, String id) throws Exception {
        try {
            ISO19115toDataCitePublisher post = new ISO19115toDataCitePublisher(TEST_ACCOUNT);
            post.setIsoMetadata(testFile);
            HTTPResponse postResult = post.execute();
            System.out.println(post.getDCMetadata());
            System.out.println(postResult);
            String prefix= TEST_ACCOUNT.getPrefix();
            //TODO: Not testable FTTB, account suspended. assertTrue(postResult.toString().contains(String.format("OK (%s/%S)",prefix,id)));
            GetMetadata getMetadata = new GetMetadata(TEST_ACCOUNT, String.format("%s/%s",prefix,id));
            HTTPResponse getResult = getMetadata.execute();

            //TODO: Not testable FTTB, account suspended. assertTrue(getResult.toString().contains(String.format("<datacite:identifier identifierType=\"DOI\">%s/%s</datacite:identifier>",prefix,id)));
            post.updateOriginalMetadata();
            String isoMetadata = post.getISOMetadata();
            assertTrue(isoMetadata.contains(String.format("<gmx:Anchor xlink:href=\"https://doi.org/%s/%s\">%s/%s</gmx:Anchor>",prefix,id,prefix,id)));

            GetDOI getDoi = new GetDOI(TEST_ACCOUNT, String.format("%s/%s",prefix,id));
            HTTPResponse execute = getDoi.execute();
            //TODO: Not testable FTTB, account suspended. assertTrue(execute.toString().contains("http://metadata.naturalsciences.be/" + id));
        } catch (InvalidMetadataException ex) {
            Logger.getLogger(ISO19115DatasetPrinter.class.getName()).log(Level.INFO, ex.getMessage());
        }
    }
}
