package com.zipwhip.zuno.integrations;

import com.zipwhip.zuno.config.ZunoProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
@Slf4j
public class ZipwhipClient {

    private static final Pattern PHONE_NUMBER = Pattern.compile("\\+\\d{11}");

    private static final String LOGIN_URL = "https://api.zipwhip.com/user/login";
    private static final String MESSAGE_SEND_URL = "https://api.zipwhip.com/message/send";

    private final RestTemplate restTemplate;
    private final ZunoProperties zunoProperties;

    @Value("${zuno.zipwhip.session-id}")
    private String sessionId;

    public void sendText(String destination, String text) {
        if (shouldSend(destination)) {

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
            map.add("session", getSessionId());
            map.add("contacts", destination);
            map.add("body", text);

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

            restTemplate.postForEntity(
                    MESSAGE_SEND_URL, request , Map.class);
        } else {
            log.info("Not sending to destination [{}]", destination);
        }

        log.info("\n[{}]\n{}\n", destination, text);
    }

    private String getSessionId() {
        if (sessionId == null) {
            sessionId = login();
        }
        return sessionId;
    }

    private String login() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("username", zunoProperties.getZipwhip().getUsername());
        map.add("password", zunoProperties.getZipwhip().getPassword());

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(
                LOGIN_URL, request , Map.class);

        Map body = response.getBody();

        if (body.get("success").equals(true)) {
            return (String) body.get("response");
        }

        return null;
    }

    private boolean shouldSend(String destination) {
        return zunoProperties.getZipwhip().isEnabled()
                && isPhoneNumber(destination);
    }

    private boolean isPhoneNumber(String destination) {
        Matcher matcher = PHONE_NUMBER.matcher(destination);
        return matcher.matches();
    }

}
