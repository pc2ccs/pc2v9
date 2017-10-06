package edu.csus.ecs.pc2.services.core;

import edu.csus.ecs.pc2.core.model.IInternalContest;

/**
 * Organization JSON.
 * TT
 * @author Douglas A. Lane, PC^2 Team, pc2@ecs.csus.edu
 */
public class OrganizationJSON {

    public String createJSON(IInternalContest contest) {
        
        //    Name    Type    Required?   Nullable?   @WF     Description
        //    id  ID  yes     no  provided by CCS     identifier of the organization
        //    icpc_id     string  no  yes     provided by CCS     external identifier from ICPC CMS
        //    name    string  yes     no  provided by CCS     display name of the organization
        //    formal_name     string  no  yes     provided by CCS     full organization name if too long for normal display purposes.
        //    country     string  no  yes     not used    ISO 3-letter code of the organization's country
        //    url     string  no  yes     provided by CDS     URL to organization's website
        //    twitter_hashtag     string  no  yes     provided by CDS     organization hashtag
        //    location    object  no  yes     provided by CDS     JSON object as specified below
        //    location.latitude   float   depends     no  provided by CDS     latitude. Required iff location is present.
        //    location.longitude  float   depends     no  provided by CDS     longitude. Required iff location is present. 

        return null; // TODO CLICS technical deficit code getOrganizationJSON
    }
}
