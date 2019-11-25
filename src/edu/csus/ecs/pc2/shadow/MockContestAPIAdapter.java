// Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.shadow;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;

/**
 * This class provides a "mock" implementation of connections to a Remote CCS CLICS Contest API.
 * It returns data as if it were connected to an actual remote CCS.
 * 
 * @author John Clevenger, PC2 Development Team, pc2@ecs.csus.edu
 *
 */
public class MockContestAPIAdapter implements IRemoteContestAPIAdapter {

    private URL remoteURL;
    private String login;
    private String password;

    /**
     * @param remoteURL
     * @param login
     * @param password
     */
    public MockContestAPIAdapter(URL remoteURL, String login, String password) {
        this.remoteURL = remoteURL;
        this.login = login;
        this.password = password;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getRemoteContestConfiguration() {
       String judgementTypes = "[{\r\n" + 
               "    \"id\": \"CE\",\r\n" + 
               "    \"name\": \"Compiler Error\",\r\n" + 
               "    \"penalty\": false,\r\n" + 
               "    \"solved\": false\r\n" + 
               "}, {\r\n" + 
               "    \"id\": \"AC\",\r\n" + 
               "    \"name\": \"Accepted\",\r\n" + 
               "    \"penalty\": false,\r\n" + 
               "    \"solved\": true\r\n" + 
               "}]" ;
       String languages = "[{\r\n" + 
               "    \"id\": \"java\",\r\n" + 
               "    \"name\": \"Java\"\r\n" + 
               "}, {\r\n" + 
               "    \"id\": \"cpp\",\r\n" + 
               "    \"name\": \"GNU C++\"\r\n" + 
               "}, {\r\n" + 
               "    \"id\": \"python2\",\r\n" + 
               "    \"name\": \"Python 2\"\r\n" + 
               "}]";
       String problems = "[{\"id\":\"asteroids\",\"label\":\"A\",\"name\":\"Asteroid Rangers\",\"ordinal\":1,\"color\":\"blue\",\"rgb\":\"#00f\",\"time_limit\":2,\"test_data_count\":10},\r\n" + 
               "  {\"id\":\"bottles\",\"label\":\"B\",\"name\":\"Curvy Little Bottles\",\"ordinal\":2,\"color\":\"gray\",\"rgb\":\"#808080\",\"time_limit\":3.5,\"test_data_count\":15}\r\n" + 
               " ]";
       String groups = "[{\"id\":\"asia-74324325532\",\"icpc_id\":\"7593\",\"name\":\"Asia\"}\r\n" + 
               " ]";
       String organizations = "[{\"id\":\"inst123\",\"icpc_id\":\"433\",\"name\":\"Shanghai Jiao Tong U.\",\"formal_name\":\"Shanghai Jiao Tong University\"},\r\n" + 
               "  {\"id\":\"inst105\",\"name\":\"Carnegie Mellon University\",\"country\":\"USA\",\r\n" + 
               "   \"logo\":[{\"href\":\"http://example.com/api/contests/wf14/organizations/inst105/logo/56px\",\"width\":56,\"height\":56},\r\n" + 
               "           {\"href\":\"http://example.com/api/contests/wf14/organizations/inst105/logo/160px\",\"width\":160,\"height\":160}]\r\n" + 
               "  }\r\n" + 
               " ]";
       String teams = "[{\"id\":\"11\",\"icpc_id\":\"201433\",\"name\":\"Shanghai Tigers\",\"organization_id\":\"inst123\",\"group_ids\":[\"asia-74324325532\"]},\r\n" + 
               "  {\"id\":\"123\",\"name\":\"CMU1\",\"organization_id\":\"inst105\",\"group_ids\":[\"8\",\"11\"]}\r\n" + 
               " ]";
       String contestState = "{\r\n" + 
               "   \"started\": \"2014-06-25T10:00:00+01\",\r\n" + 
               "   \"ended\": null,\r\n" + 
               "   \"frozen\": \"2014-06-25T14:00:00+01\",\r\n" + 
               "   \"thawed\": null,\r\n" + 
               "   \"finalized\": null,\r\n" + 
               "   \"end_of_updates\": null\r\n" + 
               " }";
       
       String retStr = getJSONConfigurationString(judgementTypes,languages,problems,groups,organizations,teams,contestState);
       
        return retStr;
    }


    @Override
    public InputStream getRemoteContestEventFeed() {
        // TODO Auto-generated method stub
        return null;
    }
    
    /** Returns a JSON string defining an array whose elements are made up of the specified CLICS Contest API endpoint values.
     * 
     * For example, the element in the array corresponding to the received value "problems" is a JSON string with the key "problems"
     * and a value defined by the received parameter "problems".
     * 
     * @param judgementTypes
     * @param languages
     * @param problems
     * @param groups
     * @param organizations
     * @param teams
     * @param contestState
     * @return
     */
    private String getJSONConfigurationString(String judgementTypes, String languages, String problems, String groups, String organizations, String teams, String contestState) {

        String retStr = "[";
        retStr += getArrayElement("judgementTypes",judgementTypes);
        retStr += ",\r\n";
        retStr += getArrayElement("languages",languages);
        retStr += ",\r\n";
        retStr += getArrayElement("problems",problems);
        retStr += ",\r\n";
        retStr += getArrayElement("groups",groups);
        retStr += ",\r\n";
        retStr += getArrayElement("organizations",organizations);
        retStr += ",\r\n";
        retStr += getArrayElement("teams",teams);
        retStr += ",\r\n";
        retStr += getArrayElement("contestState",contestState);
        retStr += "]";
        
        return retStr;
    }
    
    /**
     * Returns a JSON array element (surrounded by square brackets) consisting of the specified keyword
     * and with a value defined by the received value parameter.
     * 
     * @param retStr the String to which a JSON array element is to be added
     * @param keyword the JSON keyword for the element to be added
     * @param value a JSON string defining the value of the added element
     */
    private String getArrayElement(String keyword, String value) {

        String newStr = "[";
        newStr += "\"" + keyword + "\":" + "\"" + value + "\"";
        newStr += "]";
        
        return newStr ;
        
    }

  
    public InputStream readRemoteCCSEventFeed(File file) {
        PacedFileInputStream efEventStreamReader;
        try {
            int secondsPauseForEAchLine = 2;
            efEventStreamReader = new PacedFileInputStream(file,secondsPauseForEAchLine);
            return efEventStreamReader;
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public InputStream readRemoteCCSEventFeed(){
        
        /**
         * Test JSON event feed.   From CSUS Fall 2019 (real) contest.
         */
        String filename = "testdata/PacedFileInputStreamTest/csus-f2019.eventfeed.json";
        return readRemoteCCSEventFeed(new File(filename));
        
        // todo read or pass through from REST end point
//        throw new NotImplementedException();
    }
    
    public static void main (String [] args) {
        System.out.println ("MockContestAPIAdapter:");
        System.out.println ("   Mock contest configuration:");
        System.out.println (new MockContestAPIAdapter(null, null, null).getRemoteContestConfiguration());
    }

}
