package com.running.runapp.domain.ghostRun.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.running.runapp.global.error.BusinessException;
import com.running.runapp.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GoogleGeocodingService {

    private static final String GEOCODING_URL = "https://maps.googleapis.com/maps/api/geocode/json";
    private static final List<String> DONG_TYPE_PRIORITY = List.of(
            "sublocality_level_4",
            "sublocality_level_3",
            "sublocality_level_2",
            "sublocality_level_1",
            "administrative_area_level_4",
            "administrative_area_level_3",
            "neighborhood",
            "locality"
    );

    private final RestClient restClient = RestClient.create();

    @Value("${google.maps.api-key:}")
    private String apiKey;

    public String resolveDong(Double lat, Double lng) {
        if (!StringUtils.hasText(apiKey) || lat == null || lng == null) {
            log.warn("Google geocoding skipped: apiKeySet={}, lat={}, lng={}", StringUtils.hasText(apiKey), lat, lng);
            throw new BusinessException(ErrorCode.GEOCODING_FAILED);
        }

        URI uri = UriComponentsBuilder.fromUriString(GEOCODING_URL)
                .queryParam("latlng", lat + "," + lng)
                .queryParam("language", "ko")
                .queryParam("key", apiKey)
                .build()
                .toUri();

        JsonNode root = restClient.get()
                .uri(uri)
                .retrieve()
                .body(JsonNode.class);

        String status = root == null ? null : root.path("status").asText();
        if (root == null || !"OK".equals(status)) {
            log.warn(
                    "Google geocoding failed: status={}, errorMessage={}, lat={}, lng={}",
                    status,
                    root == null ? null : root.path("error_message").asText(null),
                    lat,
                    lng
            );
            throw new BusinessException(ErrorCode.GEOCODING_FAILED);
        }

        String legalDong = findDongLikeComponent(root);
        if (StringUtils.hasText(legalDong)) {
            return legalDong;
        }

        for (String type : DONG_TYPE_PRIORITY) {
            String dong = findAddressComponent(root, type);
            if (StringUtils.hasText(dong)) {
                String normalizedDong = normalizeDong(dong);
                if (StringUtils.hasText(normalizedDong)) {
                    return normalizedDong;
                }
            }
        }

        String formattedAddressDong = findDongInFormattedAddress(root);
        if (StringUtils.hasText(formattedAddressDong)) {
            return formattedAddressDong;
        }

        log.warn("Google geocoding could not resolve dong: lat={}, lng={}", lat, lng);
        throw new BusinessException(ErrorCode.GEOCODING_FAILED);
    }

    private String findDongLikeComponent(JsonNode root) {
        for (JsonNode result : root.path("results")) {
            for (JsonNode component : result.path("address_components")) {
                String normalizedDong = normalizeDong(component.path("long_name").asText());
                if (StringUtils.hasText(normalizedDong)) {
                    return normalizedDong;
                }
            }
        }
        return null;
    }

    private String findAddressComponent(JsonNode root, String targetType) {
        for (JsonNode result : root.path("results")) {
            for (JsonNode component : result.path("address_components")) {
                for (JsonNode type : component.path("types")) {
                    if (targetType.equals(type.asText())) {
                        return component.path("long_name").asText();
                    }
                }
            }
        }
        return null;
    }

    private String findDongInFormattedAddress(JsonNode root) {
        for (JsonNode result : root.path("results")) {
            String formattedAddress = result.path("formatted_address").asText();
            if (!StringUtils.hasText(formattedAddress)) {
                continue;
            }

            for (String token : formattedAddress.split("\\s+")) {
                String normalizedDong = normalizeDong(token);
                if (StringUtils.hasText(normalizedDong)) {
                    return normalizedDong;
                }
            }
        }
        return null;
    }

    private String normalizeDong(String addressComponent) {
        if (!StringUtils.hasText(addressComponent)) {
            return null;
        }

        String value = addressComponent.trim();
        if (!(value.endsWith("동") || value.endsWith("읍") || value.endsWith("면"))) {
            return null;
        }

        return value
                .replaceFirst("제\\d+동$", "동")
                .replaceFirst("\\d+동$", "동");
    }
}
