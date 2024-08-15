package com.ludigi.webchecker.element;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.SimpleFileServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.UncheckedIOException;
import java.net.InetSocketAddress;
import java.nio.file.Path;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ElementServiceTest {
    @Mock
    private ElementDataRepository elementDataRepository;
    @Mock
    private IdGenerator idGenerator;
    private final Clock fixedClock = Clock.fixed(Instant.now(), ZoneId.systemDefault());
    private ElementService elementService;
    private static HttpServer httpServer;

    @BeforeAll
    static void startTempServer() {
        var addr = new InetSocketAddress(8090);
        Path filesPath = Path.of(ElementServiceTest.class.getResource("").getPath());
        httpServer = SimpleFileServer.createFileServer(addr, filesPath, SimpleFileServer.OutputLevel.INFO);
        httpServer.start();
    }

    @BeforeEach
    void init() {
        elementService = new ElementService(elementDataRepository, idGenerator, fixedClock);
    }

    @Test
    void shouldSaveElementAndReturnIdWhenElementExists() {
        UUID id = UUID.randomUUID();
        String url = "http://localhost:8090/index.html";
        String selector = "div > p > a";
        Mockito.when(idGenerator.nextId()).thenReturn(id);
        ArgumentCaptor<ElementData> elementDataArgumentCaptor = ArgumentCaptor.forClass(ElementData.class);
        ElementRequest request = new ElementRequest(url, selector);

        Optional<UUID> resultId = elementService.fetchElement(request);

        Mockito.verify(elementDataRepository).save(elementDataArgumentCaptor.capture());
        ElementData savedElement = elementDataArgumentCaptor.getValue();
        ElementData expectedResult = new ElementData(id, url, selector, "More information...", LocalDateTime.now(fixedClock));
        assertEquals(id, resultId.get());
        assertEquals(expectedResult, savedElement);
    }

    @Test
    void shouldNotSaveElementAndReturnEmptyWhenElementNotExists() {
        ElementRequest request = new ElementRequest("http://localhost:8090/index.html", "h2 > p > a");
        Optional<UUID> uuid = elementService.fetchElement(request);
        Mockito.verifyNoInteractions(elementDataRepository);
        assertTrue(uuid.isEmpty());
    }

    @Test
    void shouldThrowUncheckedIOExceptionOnConnectionError() {
        ElementRequest request = new ElementRequest("http://wr-on-g-ho-st:13245/index.html", "h2 > p > a");
        assertThrowsExactly(UncheckedIOException.class, () -> elementService.fetchElement(request));
    }

    @Test
    void shouldReturnElementWhenExist() {
        UUID id = UUID.randomUUID();
        ElementData element = new ElementData(id, "http://example.com", "p", "some value", LocalDateTime.of(2024, 10, 11, 12, 55));
        Mockito.when(elementDataRepository.findById(id)).thenReturn(Optional.of(element));
        Optional<ElementResponse> foundElement = elementService.findElement(id);
        assertTrue(foundElement.isPresent());
        ElementResponse response = foundElement.get();
        assertEquals(element.getId(), response.id());
        assertEquals(element.getValue(), response.value());
        assertEquals(element.getUrl(), response.url());
        assertEquals(element.getSelector(), response.selector());
        assertEquals(element.getFetchTime(), response.fetchTime());
    }

    @Test
    void shouldReturnEmptyWhenNotExist() {
        UUID id = UUID.randomUUID();
        Mockito.when(elementDataRepository.findById(id)).thenReturn(Optional.empty());
        Optional<ElementResponse> element = elementService.findElement(id);
        assertTrue(element.isEmpty());
    }

    @AfterAll
    static void stopTempServer() {
        httpServer.stop(0);
    }
}