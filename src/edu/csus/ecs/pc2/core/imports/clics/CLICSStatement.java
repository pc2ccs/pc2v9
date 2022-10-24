// Copyright (C) 1989-2022 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core.imports.clics;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.csus.ecs.pc2.core.model.JSONObjectMapper;

/**
 * A CLICS problem statement element.
 * 
 * @author Douglas A. Lane <pc2@ecs.csus.edu>
 */
public class CLICSStatement {

    // ,"statement":[{"href":"contests/bapc2022/problems/kioskconstruction/statement","mime":"application/pdf","filename":"K.pdf"}],"test_data_count":39},"time":"2022-10-21T15:11:29.038+02:00"}

//    "statement": [
//      {
//        "href": "contests/bapc2022/problems/lowestlatency/statement",
//        "mime": "application/pdf",
//        "filename": "L.pdf"
//      }
//    ],
//    "test_data_count": 100

    @JsonProperty
    String href;

    @JsonProperty
    String mime;

    @JsonProperty
    String filename;

    public String getHref() {
        return href;
    }

    public String getMime() {
        return mime;
    }

    public String getFilename() {
        return filename;
    }

    public String toJSON() throws JsonProcessingException {
        ObjectMapper om = JSONObjectMapper.getObjectMapper();
        return om.writeValueAsString(this);
    }
}
