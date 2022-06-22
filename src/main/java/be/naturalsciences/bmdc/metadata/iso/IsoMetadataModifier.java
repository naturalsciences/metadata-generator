/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.naturalsciences.bmdc.metadata.iso;

import org.apache.sis.metadata.InvalidMetadataException;
import org.w3c.dom.Document;

/**
 *
 * @author thomas
 */
public interface IsoMetadataModifier {

    public Document getIsoMetadata();

    public void setIsoMetadata(Document isoDocument) throws InvalidMetadataException;

    public String getIdentifierSnippet();

    public void updateOriginalMetadata();

}
