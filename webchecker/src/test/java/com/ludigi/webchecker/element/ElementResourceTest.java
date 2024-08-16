package com.ludigi.webchecker.element;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.SimpleFileServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ElementResourceTest {
    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private ElementDataRepository elementDataRepository;

    private static HttpServer httpServer;

    @BeforeAll
    static void startTempServer() {
        var addr = new InetSocketAddress(8090);
        Path filesPath = Path.of(ElementServiceTest.class.getResource("").getPath());
        httpServer = SimpleFileServer.createFileServer(addr, filesPath, SimpleFileServer.OutputLevel.INFO);
        httpServer.start();
    }

    @BeforeEach
    void clearDatabase() {
        elementDataRepository.deleteAll();
    }

    @Test
    void shouldSaveElementAndReturn201WhenElementForSelectorExists() {
        String correctUrl = "http://localhost:8090/index.html";
        String existingSelector = "div > p > a";
        ElementRequest request = new ElementRequest(correctUrl, existingSelector);
        URI location = restTemplate.postForLocation("/api/elements", request);
        String locationPath = location.getPath();
        String[] pathSplit = locationPath.split("/");
        String id = pathSplit[pathSplit.length - 1];
        assertNotNull(id);
        assertThat(elementDataRepository.findById(UUID.fromString(id))).isPresent();
    }

    @Test
    void shouldNotSaveAndReturn204WhenElementForSelectorNotExists() {
        String correctUrl = "http://localhost:8090/index.html";
        String wrongSelector = "h2 > p.notexist";
        ElementRequest request = new ElementRequest(correctUrl, wrongSelector);
        ResponseEntity<String> response = restTemplate.postForEntity("/api/elements", request, String.class);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertThat(elementDataRepository.findAll()).isEmpty();
    }

    @Test
    void shouldNotSaveAndReturn200WhenCommunicationFailure() {
        String wrongUrl = "http://wr-ong-host:1234/index.html";
        String anySelector = "h2 > p.notexist";
        ElementRequest request = new ElementRequest(wrongUrl, anySelector);
        ResponseEntity<String> response = restTemplate.postForEntity("/api/elements", request, String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertThat(elementDataRepository.findAll()).isEmpty();
    }

    @Test
    void shouldReturn200WithElementWhenExists() {
        UUID id = UUID.randomUUID();
        ElementData element = new ElementData(
                id,
                "http://example.com",
                "p",
                "foo",
                LocalDateTime.now()
        );
        elementDataRepository.save(element);
        ResponseEntity<ElementResponse> response = restTemplate
                .getForEntity("/api/elements/%s".formatted(id), ElementResponse.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(
                new ElementResponse(
                        element.getId(),
                        element.getValue(),
                        element.getUrl(),
                        element.getSelector(),
                        element.getFetchTime()
                ),
                response.getBody()
        );
    }

    @Test
    void shouldReturn404WhenElementNotExists() {
        UUID id = UUID.randomUUID();
        ResponseEntity<ElementResponse> response = restTemplate
                .getForEntity("/api/elements/%s".formatted(id), ElementResponse.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @AfterAll
    static void stopTempServer() {
        httpServer.stop(0);
    }
}
