# Metadata generator

Metadata-generator is a small Java library to create both original and INSPIRE-compliant ISO 19115:2003 metadata at RBINS. It leverages the org.apache.sis.metadata (http://sis.apache.org/) 
and org.opengis.metadata (http://docs.geotools.org/) packages.

Capacities:

- Output a 19115:2003 xml document provided a base Dataset and associated Datasource objects
- Crosstranslate terms in GEMET and the INSPIRE registry into NL, DE, FR and EN.
- Mint a doi for the dataset, and push updates in the metadata at DataCite and extend the ISO metadata result with this identifier.
