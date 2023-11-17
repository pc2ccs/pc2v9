// Copyright (C) 1989-2024 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.clics.API202306;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.security.Permission;
import edu.csus.ecs.pc2.core.util.JSONTool;
import edu.csus.ecs.pc2.services.core.JSONUtilities;

/**
 * CLICS Team object
 * 
 * @author John Buck
 *
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CLICSTeam {

    @JsonProperty
    private String id;

    @JsonProperty
    private String icpc_id;

    @JsonProperty
    private String name;

    @JsonProperty
    private String label;

    @JsonProperty
    private String display_name;

    @JsonProperty
    private String organization_id;

    @JsonProperty
    private String [] group_ids;

    @JsonProperty
    private boolean hidden;
    
    // The following properties are not maintained by the CCS and therefore not included (they are optional in the spec)
    // location, photo, video, backup, key_log, tool_data, desktop, webcam, audio
    
    /**
     * Fill in properties for team as per 2023-06 spec
     * 
     * @param model The contest
     * @param account team information
     */
    public CLICSTeam(IInternalContest model, Account account) {
        
        id = "" + account.getClientId().getClientNumber();
        
        if (JSONTool.notEmpty(account.getExternalId())) {
            icpc_id = account.getExternalId();
        }
        label = account.getLabel();
        name = account.getDisplayName();
        if (JSONTool.notEmpty(account.getInstitutionCode()) && !account.getInstitutionCode().equals("undefined")) {
            organization_id = JSONTool.getOrganizationId(account);
        }
        if (account.getGroupId() != null) {
            group_ids = new String[1];
            // FIXME eventually accounts should have more then 1 groupId, make sure add them
            group_ids[0] = JSONTool.getGroupId(model.getGroup(account.getGroupId()));
        }
        hidden = !account.isAllowed(Permission.Type.DISPLAY_ON_SCOREBOARD);
    }

    public String toJSON() {

        try {
            ObjectMapper mapper = JSONUtilities.getObjectMapper();
            return mapper.writeValueAsString(this);
        } catch (Exception e) {
            return "Error creating JSON for team info " + e.getMessage();
        }
    }
}
