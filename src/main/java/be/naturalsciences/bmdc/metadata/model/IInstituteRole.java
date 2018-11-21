/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.naturalsciences.bmdc.metadata.model;

/**
 *
 * @author thomas
 */
public interface IInstituteRole {

    public IDataset.Role getIsoRole();

    public void setIsoRole(IDataset.Role isoRole);

    public String getOrganisationName();

    public void setOrganisationName(String organisationName);

    public String getPhone();

    public void setPhone(String phone);

    public String getFax();

    public void setFax(String fax);

    public String getDeliveryPoint();

    public void setDeliveryPoint(String deliveryPoint);

    public String getCity();

    public void setCity(String city);

    public String getPostalCode();

    public void setPostalCode(String postalCode);

    public String getSdnCountryCode();

    public void setSdnCountryCode(String sdnCountryCode);

    public String getEmailAddress();

    public void setEmailAddress(String emailAddresss);

    public String getWebsite();

    public void setWebsite(String website);

}
