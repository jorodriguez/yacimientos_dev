package lector.util;

import com.newrelic.api.agent.NewRelic;
import java.util.Calendar;

/**
 *
 * @author mrojas
 */
public class GenNrStats {

    private final static UtilLog4j LOGGER = UtilLog4j.log;
    
    /**
     * Sends data to the NewRelic monitoring agent.
     * (https://newrelic.com/docs/java/java-agent-api)
     *
     * @param type The metric type
     */
    public static void saveNrData(String type) {
        NewRelic.addCustomParameter("SIA_Type", type);
        NewRelic.incrementCounter("Custom/" + type);
    }
    
    /**
     * Saves an event to the application log as a JSON string
     * @param event The event to be saved
     */
    public static void logEvents(NewRelicEvent event) {
        
        StringBuilder sbLog = new StringBuilder();
        
        sbLog.append("{system : ").append(event.getSystem())
                .append(", class : ").append(event.getClassName())
                .append(", method : ").append(event.getMethod())
                .append(", event : ").append(event.getEventName())
                .append(", data : ").append(event.getData())
                .append(", timestamp : ").append(Calendar.getInstance().getTimeInMillis())
                .append('}');
        
        LOGGER.info(sbLog.toString());
    }
}
