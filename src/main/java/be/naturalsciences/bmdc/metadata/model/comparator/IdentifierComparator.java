/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.naturalsciences.bmdc.metadata.model.comparator;

import java.util.Comparator;
import org.opengis.metadata.Identifier;

/**
 *
 * @author thomas
 */
public class IdentifierComparator implements Comparator<Identifier> {

    public Comparator<Identifier> ci;

    public IdentifierComparator() {
        ci = Comparator.comparing(i -> i.getCode(), Comparator.reverseOrder());
        ci = ci.thenComparing(i -> i.getAuthority().getTitle()).thenComparing(i -> i.getAuthority().getEdition()).thenComparing(i -> i.getAuthority().getEditionDate());
    }

    @Override
    public int compare(Identifier g1, Identifier g2) {
        return ci.compare(g1, g2);
    }

}
