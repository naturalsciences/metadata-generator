/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.naturalsciences.bmdc.iso;

import be.naturalsciences.bmdc.metadata.atom.AtomFeedDatasetBuilder;
import be.naturalsciences.bmdc.metadata.model.IDataset;
import be.naturalsciences.bmdc.metadata.model.impl.Dataset;
import be.naturalsciences.bmdc.metadata.model.impl.InstituteRole;
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
public class AtomFeedCreatorTest {

    public AtomFeedCreatorTest() {
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
     * Test of createAtom method, of class AtomFeedDatasetBuilder.
     */
    @Test
    public void testCreateAtom() throws Exception {
        System.out.println("createAtom");
        Dataset dataset = MetadataBuilderTest.createFakeDataset1();
        AtomFeedDatasetBuilder instance = new AtomFeedDatasetBuilder(dataset, "http://astacus.bmdc.be:8080/datasets/atom/service.xml", "http://metadata.naturalsciences.be/" + dataset.getIdentifier(), "http://astacus.bmdc.be:8080/datasets/atom/" + dataset.getIdentifier());
        InstituteRole BMDC_AUTHOR = new InstituteRole(IDataset.Role.AUTHOR, "BMDC", "02/773.21.44", null, "Gulledelle 100", "Sint-Lambrechts-Woluwe", "1200", "BE", "bmdc@naturalsciences.be", "http://bmdc.naturalsciences.be");

        String result = instance.createXMLString(BMDC_AUTHOR);
        System.out.println(result);
        assertTrue(result.contains("xml version=\"1.0\""));
    }

}
