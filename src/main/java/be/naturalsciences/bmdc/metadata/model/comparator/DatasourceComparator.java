/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.naturalsciences.bmdc.metadata.model.comparator;

import be.naturalsciences.bmdc.metadata.model.IDatasource;
import java.util.Comparator;

/**
 *
 * @author thomas
 */
public class DatasourceComparator implements Comparator<IDatasource> {

    @Override
    public int compare(IDatasource t, IDatasource t1) {
        return t.getTitle().compareTo(t1.getTitle());
    }

}
