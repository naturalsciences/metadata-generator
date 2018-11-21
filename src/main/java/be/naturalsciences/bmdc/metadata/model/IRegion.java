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
public interface IRegion {

    public String getName();

    public void setName(String name);

    public String getSource();

    public void setSource(String source);

    public Long getMRGID();

    public void setMRGID(Long mrgid);
}
