/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.naturalsciences.bmdc.metadata.model.comparator;

import java.util.Comparator;
import org.opengis.metadata.extent.GeographicDescription;

/**
 *
 * @author thomas
 */
public class GeographicDescriptionComparator implements Comparator<GeographicDescription> {

    @Override
    public int compare(GeographicDescription g1, GeographicDescription g2) {

        /*  IdentifierComparator ic = new IdentifierComparator();
        ic.compare(g1.getGeographicIdentifier(), g2.getGeographicIdentifier());*/
        Comparator<GeographicDescription> cg = Comparator.comparing(d -> d.getGeographicIdentifier().getCode());
        cg.thenComparing(d -> d.getGeographicIdentifier().getAuthority().getTitle()).thenComparing(d -> d.getGeographicIdentifier().getAuthority().getEdition()).thenComparing(d -> d.getGeographicIdentifier().getAuthority().getEditionDate());
        return cg.compare(g1, g2);
    }

}
