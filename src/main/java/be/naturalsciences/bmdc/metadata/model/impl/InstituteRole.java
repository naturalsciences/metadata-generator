/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.naturalsciences.bmdc.metadata.model.impl;

import be.naturalsciences.bmdc.metadata.model.IDataset;
import be.naturalsciences.bmdc.metadata.model.IInstituteRole;

public class InstituteRole implements IInstituteRole {

    IDataset.Role isoRole;
    String organisationName;
    String phone;
    String fax;
    String deliveryPoint;
    String city;
    String postalCode;
    String sdnCountryCode;
    String emailAddress;
    String website;

    public IDataset.Role getIsoRole() {
        return isoRole;
    }

    public void setIsoRole(IDataset.Role isoRole) {
        this.isoRole = isoRole;
    }

    public String getOrganisationName() {
        return organisationName;
    }

    public void setOrganisationName(String organisationName) {
        this.organisationName = organisationName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getDeliveryPoint() {
        return deliveryPoint;
    }

    public void setDeliveryPoint(String deliveryPoint) {
        this.deliveryPoint = deliveryPoint;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getSdnCountryCode() {
        return sdnCountryCode;
    }

    public void setSdnCountryCode(String sdnCountryCode) {
        this.sdnCountryCode = sdnCountryCode;
    }

    @Override
    public String getEmailAddress() {
        return emailAddress;
    }

    @Override
    public void setEmailAddress(String emailAddresss) {
        this.emailAddress = emailAddress;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public InstituteRole(IDataset.Role role, String organisationName, String phone, String fax, String deliveryPoint, String city, String postalCode, String sdnCountryCode, String emailAddress, String website) {
        this.isoRole = role;
        this.organisationName = organisationName;
        this.phone = phone;
        this.fax = fax;
        this.deliveryPoint = deliveryPoint;
        this.city = city;
        this.postalCode = postalCode;
        this.sdnCountryCode = sdnCountryCode;
        this.emailAddress = emailAddress;
        this.website = website;
    }
}
