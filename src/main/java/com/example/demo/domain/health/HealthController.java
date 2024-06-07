package com.example.demo.domain.health;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping
@Slf4j
public class HealthController {

    /**
     * health check
     */
    @GetMapping
    public void healthCheck(@RequestHeader MultiValueMap<String, String> headers) {
        headers.forEach((key, value) -> log.info("Header: {}={}", key, value));
    }
}
