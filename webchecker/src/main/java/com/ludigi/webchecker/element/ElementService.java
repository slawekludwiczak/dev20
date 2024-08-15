package com.ludigi.webchecker.element;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
class ElementService {
    private final ElementDataRepository elementDataRepository;
    private final Clock clock;
    private final IdGenerator idGenerator;

    ElementService(ElementDataRepository elementDataRepository, IdGenerator idGenerator, Clock clock) {
        this.elementDataRepository = elementDataRepository;
        this.clock = clock;
        this.idGenerator = idGenerator;
    }

    Optional<UUID> fetchElement(ElementRequest request) {
        Document document;
        try {
            document = Jsoup.connect(request.url()).get();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        Elements elements = document.select(request.selector());
        if (elements.isEmpty()) {
            return Optional.empty();
        }
        String elementValue = elements.text();
        UUID id = idGenerator.nextId();
        ElementData elementData = new ElementData(
                id,
                request.url(),
                request.selector(),
                elementValue,
                LocalDateTime.now(clock));
        elementDataRepository.save(elementData);
        return Optional.of(id);
    }

    Optional<ElementResponse> findElement(UUID id) {
        return elementDataRepository.findById(id)
                .map(ed -> new ElementResponse(
                        ed.getId(),
                        ed.getValue(),
                        ed.getUrl(),
                        ed.getSelector(),
                        ed.getFetchTime()
                ));
    }
}
