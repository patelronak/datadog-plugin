package org.datadog.jenkins.plugins.datadog.listeners;

import jenkins.security.SecurityListener;
import net.sf.json.JSONArray;
import org.acegisecurity.userdetails.UserDetails;
import org.datadog.jenkins.plugins.datadog.DatadogClient;
import org.datadog.jenkins.plugins.datadog.DatadogEvent;
import org.datadog.jenkins.plugins.datadog.DatadogUtilities;
import org.datadog.jenkins.plugins.datadog.events.ConfigChangedEventImpl;
import org.datadog.jenkins.plugins.datadog.events.UserAuthenticationEventImpl;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

public class DatadogSecurityListener extends SecurityListener {

    private static final Logger logger = Logger.getLogger(DatadogSecurityListener.class.getName());

    @Override
    protected void authenticated(@Nonnull UserDetails details) {
        try {
            final boolean emitSystemEvents = DatadogUtilities.getDatadogGlobalDescriptor().isEmitSecurityEvents();
            if (!emitSystemEvents) {
                return;
            }
            logger.fine("Start DatadogSecurityListener#authenticated");

            // Get Datadog Client Instance
            DatadogClient client = DatadogUtilities.getDatadogClient();

            // Get the list of global tags to apply
            Map<String, Set<String>> tags = DatadogUtilities.getDatadogGlobalDescriptor().getGlobalTags();

            // Send event
            DatadogEvent event = new UserAuthenticationEventImpl(details.getUsername(),
                    UserAuthenticationEventImpl.LOGIN, tags);
            client.sendEvent(event.createPayload());

            // Submit counter
            String hostname = DatadogUtilities.getHostname("null");
            client.incrementCounter("jenkins.user.authenticated", hostname, tags);

            logger.fine("End DatadogSecurityListener#authenticated");
        } catch (Exception e) {
            logger.warning("Unexpected exception occurred - " + e.getMessage());
        }
    }

    @Override
    protected void failedToAuthenticate(@Nonnull String username) {
        try {
            final boolean emitSystemEvents = DatadogUtilities.getDatadogGlobalDescriptor().isEmitSecurityEvents();
            if (!emitSystemEvents) {
                return;
            }
            logger.fine("Start DatadogSecurityListener#failedToAuthenticate");

            // Get Datadog Client Instance
            DatadogClient client = DatadogUtilities.getDatadogClient();

            // Get the list of global tags to apply
            Map<String, Set<String>> tags = DatadogUtilities.getDatadogGlobalDescriptor().getGlobalTags();

            // Send event
            DatadogEvent event = new UserAuthenticationEventImpl(username, UserAuthenticationEventImpl.ACCESS_DENIED, tags);
            client.sendEvent(event.createPayload());

            // Submit counter
            String hostname = DatadogUtilities.getHostname("null");
            client.incrementCounter("jenkins.user.access_denied", hostname, tags);

            logger.fine("End DatadogSecurityListener#failedToAuthenticate");
        } catch (Exception e) {
            logger.warning("Unexpected exception occurred - " + e.getMessage());
        }
    }

    @Override
    protected void loggedIn(@Nonnull String username) {
        //Covered by Authenticated
    }

    @Override
    protected void failedToLogIn(@Nonnull String username) {
        //Covered by failedToAuthenticate
    }

    @Override
    protected void loggedOut(@Nonnull String username) {
        try {
            final boolean emitSystemEvents = DatadogUtilities.getDatadogGlobalDescriptor().isEmitSecurityEvents();
            if (!emitSystemEvents) {
                return;
            }
            logger.fine("Start DatadogSecurityListener#loggedOut");

            // Get Datadog Client Instance
            DatadogClient client = DatadogUtilities.getDatadogClient();

            // Get the list of global tags to apply
            Map<String, Set<String>> tags = DatadogUtilities.getDatadogGlobalDescriptor().getGlobalTags();

            // Send event
            DatadogEvent event = new UserAuthenticationEventImpl(username, UserAuthenticationEventImpl.LOGOUT, tags);
            client.sendEvent(event.createPayload());

            // Submit counter
            String hostname = DatadogUtilities.getHostname("null");
            client.incrementCounter("jenkins.user.logout", hostname, tags);

            logger.fine("End DatadogSecurityListener#loggedOut");
        } catch (Exception e) {
            logger.warning("Unexpected exception occurred - " + e.getMessage());
        }
    }
}
