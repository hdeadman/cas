package org.apereo.cas.monitor;

import org.apereo.cas.monitor.config.LdapMonitorConfiguration;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertNotNull;


/**
 * Verify config class actually starts to test against autowiring error that was observed.
 * Minimum pool size of zero allows test to run without ldap server.
 * @author Hal Deadman
 * @since 6.1.0
 */
@SpringBootTest(classes = {
    LdapMonitorConfiguration.class
})
@TestPropertySource(properties = {
    "cas.monitor.ldap.min-pool-size=0"
})
@Tag("Ldap")
public class LdapMonitorConfigurationTests {

    @Autowired
    @Qualifier("pooledLdapConnectionFactoryHealthIndicator")
    private HealthIndicator monitor;

    @Test
    public void configLoaded() {
        assertNotNull(monitor);
    }

}
