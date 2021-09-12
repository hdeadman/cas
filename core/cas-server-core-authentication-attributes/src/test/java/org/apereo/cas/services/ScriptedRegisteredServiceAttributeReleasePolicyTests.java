package org.apereo.cas.services;

import org.apereo.cas.CoreAttributesTestUtils;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.util.spring.ApplicationContextProvider;

import lombok.val;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * This is {@link ScriptedRegisteredServiceAttributeReleasePolicyTests}.
 *
 * @author Misagh Moayyed
 * @since 5.1.0
 * @deprecated Since 6.2
 */
@Tag("Groovy")
@Deprecated(since = "6.2.0")
public class ScriptedRegisteredServiceAttributeReleasePolicyTests {

    @BeforeAll
    public static void initialize() {
        val context = mock(ConfigurableApplicationContext.class);
        when(context.getBean(CasConfigurationProperties.class)).thenReturn(new CasConfigurationProperties());
        ApplicationContextProvider.holdApplicationContext(context);
    }

    @Test
    public void verifyInlineScript() {
        val p = new ScriptedRegisteredServiceAttributeReleasePolicy();
        p.setScriptFile("groovy { return attributes }");
        val principal = CoreAttributesTestUtils.getPrincipal("cas",
            Collections.singletonMap("attribute", List.of("value")));
        val attrs = p.getAttributes(principal,
            CoreAttributesTestUtils.getService(),
            CoreAttributesTestUtils.getRegisteredService());
        assertEquals(attrs.size(), principal.getAttributes().size());
    }
}
