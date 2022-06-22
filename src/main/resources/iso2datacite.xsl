<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<xsl:stylesheet version="2.0" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:gml="http://www.opengis.net/gml" xmlns:gmd="http://www.isotc211.org/2005/gmd" xmlns:gco="http://www.isotc211.org/2005/gco" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://datacite.org/schema/kernel-3 http://schema.datacite.org/meta/kernel-3/metadata.xsd" exclude-result-prefixes="xs xsl gml gmd gco">
  <xsl:template xmlns="http://datacite.org/schema/kernel-4" match="gmd:identificationInfo/gmd:MD_DataIdentification/gmd:citation/gmd:CI_Citation/gmd:citedResponsibleParty">
    <creator>
      <creatorName>
        <xsl:value-of select="gmd:CI_ResponsibleParty/gmd:organisationName/gco:CharacterString" />
      </creatorName>
    </creator>
  </xsl:template>
  <xsl:template  xmlns="http://datacite.org/schema/kernel-4" match="gmd:identificationInfo/gmd:MD_DataIdentification/gmd:citation/gmd:CI_Citation/gmd:title">
    <title>
      <xsl:value-of select="gco:CharacterString" />
    </title>
  </xsl:template>
  <xsl:template match="gmd:MD_Metadata">
    <resource xmlns="http://datacite.org/schema/kernel-4" xsi:schemaLocation="http://datacite.org/schema/kernel-4 http://schema.datacite.org/meta/kernel-4/metadata.xsd">
      <identifier identifierType="DOI">
        <xsl:value-of select="concat('10.5072/',gmd:fileIdentifier/gco:CharacterString)" />
      </identifier>
      <creators>
        <xsl:apply-templates select="gmd:identificationInfo/gmd:MD_DataIdentification/gmd:citation/gmd:CI_Citation/gmd:citedResponsibleParty" />
      </creators>
      <titles>
        <xsl:apply-templates select="gmd:identificationInfo/gmd:MD_DataIdentification/gmd:citation/gmd:CI_Citation/gmd:title" />
      </titles>
      <publisher>Royal Belgian Institute for Natural Sciences (RBINS)</publisher>
      <publicationYear>
        <xsl:value-of select="substring(gmd:identificationInfo/gmd:MD_DataIdentification/gmd:citation/gmd:CI_Citation/gmd:date/gmd:CI_Date/gmd:date/gco:Date, 1, 4)" />
      </publicationYear>
      <resourceType resourceTypeGeneral="Dataset">Dataset</resourceType>
    </resource>
  </xsl:template>
</xsl:stylesheet>
