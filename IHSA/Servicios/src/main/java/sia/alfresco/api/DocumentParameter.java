/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.alfresco.api;

import java.util.Map;
import lombok.Builder;
import lombok.Getter;
import org.apache.chemistry.opencmis.client.api.Folder;

/**
 *
 * @author mrojas
 */
@Builder
@Getter
public class DocumentParameter {
    private Folder parentFolder;
    private byte[] fileContents;
    private String fileName;
    private String fileType;
    private Map<String, Object> props;

}
