package edu.csus.ecs.pc2.core.standings.json;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

/**
 * This class defines a Team in a way that is consistent with the CLICS Contest API "/teams" endpoint.
 * See https://ccs-specs.icpc.io/contest_api#teams for details.
 * 
 * @author John Clevenger, PC2 Development Team (pc2@ecs.csus.edu)
 *
 */
//The following annotation tells the Jackson ObjectMapper not to include "type information" when it serializes
//objects of this class (the default is to include the fully-qualified class name as an additional JSON element).
//(Note that exclusion of such type information means that generated JSON cannot subsequently be accurately DESERIALIZED...)
@JsonTypeInfo(use=Id.NONE)
public class Team {
    
    @JsonProperty
    private String id;

    @JsonProperty
    private String icpc_id;
    
    @JsonProperty
    private String name;
    
    @JsonProperty
    private String display_name;
    
    @JsonProperty
    private String organization_id;
    
    @JsonProperty
    private List<String> group_ids;

    public String getId() {
        return id;
    }

   //other CLICS attributes could be defined here; all remaining attributes are "Optional" in the CLICS specification.
    
    public void setId(String id) {
        this.id = id;
    }

    public String getIcpc_id() {
        return icpc_id;
    }

    public void setIcpc_id(String icpc_id) {
        this.icpc_id = icpc_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplay_name() {
        return display_name;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    public String getOrganization_id() {
        return organization_id;
    }

    public void setOrganization_id(String organization_id) {
        this.organization_id = organization_id;
    }

    public List<String> getGroup_ids() {
        return group_ids;
    }

    public void setGroup_ids(List<String> group_ids) {
        this.group_ids = group_ids;
    }

    
    

}
