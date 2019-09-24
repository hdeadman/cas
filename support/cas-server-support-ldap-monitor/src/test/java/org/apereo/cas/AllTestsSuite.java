
package org.apereo.cas;

import org.apereo.cas.monitor.LdapMonitorConfigurationTests;
import org.apereo.cas.monitor.PooledConnectionFactoryHealthIndicatorTests;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.runner.RunWith;

/**
 * This is {@link AllTestsSuite}.
 *
 * @author Hal Deadman
 * @since 6.1.0
 */
@SelectClasses({
    LdapMonitorConfigurationTests.class,
    PooledConnectionFactoryHealthIndicatorTests.class
})
@RunWith(JUnitPlatform.class)
public class AllTestsSuite {
}
