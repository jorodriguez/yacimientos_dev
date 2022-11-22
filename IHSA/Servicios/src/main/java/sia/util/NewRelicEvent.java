/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.util;

import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author mrojas
 */
@Getter
@Setter
public class NewRelicEvent {
    private String system;
    private String className;
    private String method;
    private String eventName;
    private String data;
}
