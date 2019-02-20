/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.naturalsciences.bmdc.metadata.model.comparator;

import java.util.Comparator;
import org.opengis.metadata.citation.Citation;

/**
 *
 * @author thomas
 */
public class CitationComparator implements Comparator<Citation> {

    @Override
    public int compare(Citation t, Citation t1) {
        Comparator<Citation> c = Comparator.comparing(Citation::getTitle, Comparator.nullsFirst(Comparator.naturalOrder())).thenComparing(Citation::getEdition, Comparator.nullsFirst(Comparator.naturalOrder())).thenComparing(Citation::getEditionDate, Comparator.nullsFirst(Comparator.naturalOrder()));
        return c.compare(t, t1);
    }

}
