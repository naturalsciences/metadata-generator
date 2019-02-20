/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.naturalsciences.bmdc.metadata.model;

import java.io.Serializable;
import java.util.Collection;

/**
 *
 * @author thomas
 */
public interface IDistribution extends Serializable {

    static final long serialVersionUID = 1L;

    /* Collection<IDistributionFormat> getDistributionFormats();

    void setDistributionFormats(Collection<IDistributionFormat> formats);*/
    Collection<IDistributionResource> getDistributionResources();

    void setDistributionResources(Collection<IDistributionResource> resources);

}
