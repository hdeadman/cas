package org.apereo.cas.web;

import org.apereo.cas.configuration.model.support.captcha.GoogleRecaptchaProperties;
import org.apereo.cas.util.CollectionUtils;
import org.apereo.cas.util.LoggingUtils;
import org.apereo.cas.util.http.HttpExecutionRequest;
import org.apereo.cas.util.http.HttpUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.hc.core5.http.HttpEntityContainer;
import org.apache.hc.core5.http.HttpResponse;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import java.nio.charset.StandardCharsets;

/**
 * This is {@link BaseCaptchaValidator}.
 *
 * @author Misagh Moayyed
 * @since 6.2.0
 */
@RequiredArgsConstructor
@Slf4j
public abstract class BaseCaptchaValidator implements CaptchaValidator {
    private static final ObjectReader READER = new ObjectMapper().findAndRegisterModules().reader();

    @Getter
    private final GoogleRecaptchaProperties recaptchaProperties;

    @Override
    public boolean validate(final String recaptchaResponse, final String userAgent) {
        HttpResponse response = null;
        try {
            val exec = HttpExecutionRequest.builder()
                .method(HttpMethod.POST)
                .url(recaptchaProperties.getVerifyUrl())
                .headers(CollectionUtils.wrap(
                    "User-Agent", userAgent,
                    "Content-Type", MediaType.APPLICATION_FORM_URLENCODED_VALUE,
                    "Accept-Language", "en-US,en;q=0.5"))
                .entity("secret=%s&response=%s".formatted(recaptchaProperties.getSecret(), recaptchaResponse))
                .build();

            response = HttpUtils.execute(exec);
            if (response != null && response.getCode() == HttpStatus.OK.value()) {
                try (val content = ((HttpEntityContainer) response).getEntity().getContent()) {
                    val result = IOUtils.toString(content, StandardCharsets.UTF_8);
                    if (StringUtils.isBlank(result)) {
                        throw new IllegalArgumentException("Unable to parse empty entity response from " + recaptchaProperties.getVerifyUrl());
                    }
                    LOGGER.debug("Recaptcha verification response received: [{}]", result);
                    val node = READER.readTree(result);
                    if (node.has("score") && node.get("score").doubleValue() <= recaptchaProperties.getScore()) {
                        LOGGER.warn("Recaptcha score received is less than the threshold score defined for CAS");
                        return false;
                    }
                    if (node.has("success") && node.get("success").booleanValue()) {
                        LOGGER.trace("Recaptcha has successfully verified the request");
                        return true;
                    }
                }
            }
        } catch (final Exception e) {
            LoggingUtils.error(LOGGER, e);
        } finally {
            HttpUtils.close(response);
        }
        return false;
    }
}
