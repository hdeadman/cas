package org.apereo.cas.services;

import org.apereo.cas.authentication.CoreAuthenticationTestUtils;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.util.CollectionUtils;
import org.apereo.cas.util.MockWebServer;
import org.apereo.cas.util.serialization.JacksonObjectMapperFactory;
import org.apereo.cas.util.spring.ApplicationContextProvider;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.val;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * This is {@link ReturnRestfulAttributeReleasePolicyTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
@Tag("RestfulApi")
public class ReturnRestfulAttributeReleasePolicyTests {
    private static final File JSON_FILE = new File(FileUtils.getTempDirectoryPath(),
        "verifySerializeAReturnAllowedAttributeReleasePolicyToJson.json");

    private static final ObjectMapper MAPPER = JacksonObjectMapperFactory.builder()
        .defaultTypingEnabled(true).build().toObjectMapper();

    @BeforeAll
    public static void initialize() {
        val context = mock(ConfigurableApplicationContext.class);
        when(context.getBean(CasConfigurationProperties.class)).thenReturn(new CasConfigurationProperties());
        ApplicationContextProvider.holdApplicationContext(context);
    }

    @Test
    public void verifyJson() throws IOException {
        val policyWritten = new ReturnRestfulAttributeReleasePolicy("http://endpoint.example.org");
        MAPPER.writeValue(JSON_FILE, policyWritten);
        val policyRead = MAPPER.readValue(JSON_FILE, ReturnRestfulAttributeReleasePolicy.class);
        assertEquals(policyWritten, policyRead);
    }

    @Test
    public void verifyPolicy() throws IOException {
        val data = MAPPER.writeValueAsString(CollectionUtils.wrap("givenName", "CASUSER", "familyName", "CAS"));
        try (val webServer = new MockWebServer(9299,
            new ByteArrayResource(data.getBytes(StandardCharsets.UTF_8), "REST Output"), MediaType.APPLICATION_JSON_VALUE)) {
            webServer.start();

            val policyWritten = new ReturnRestfulAttributeReleasePolicy("http://localhost:9299");
            val attributes = policyWritten.getAttributes(CoreAuthenticationTestUtils.getPrincipal(),
                CoreAuthenticationTestUtils.getService(),
                CoreAuthenticationTestUtils.getRegisteredService());
            assertFalse(attributes.isEmpty());
        }
    }

    @Test
    public void verifyBadPolicy() {
        try (val webServer = new MockWebServer(9298,
            new ByteArrayResource("---".getBytes(StandardCharsets.UTF_8), "REST Output"),
            MediaType.APPLICATION_JSON_VALUE)) {
            webServer.start();

            val policyWritten = new ReturnRestfulAttributeReleasePolicy("http://localhost:9298");
            val attributes = policyWritten.getAttributes(CoreAuthenticationTestUtils.getPrincipal(),
                CoreAuthenticationTestUtils.getService(),
                CoreAuthenticationTestUtils.getRegisteredService());
            assertTrue(attributes.isEmpty());
        }
    }
}
