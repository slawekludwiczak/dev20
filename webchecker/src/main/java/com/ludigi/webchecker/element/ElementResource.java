package com.ludigi.webchecker.element;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.UncheckedIOException;
import java.util.UUID;

@RestController
class ElementResource {
    private final ElementService elementService;

    public ElementResource(ElementService elementService) {
        this.elementService = elementService;
    }

    @PostMapping("/api/elements")
    ResponseEntity<?> fetchElement(@RequestBody ElementRequest request) {
        return elementService.fetchElement(request)
                .map(id -> ServletUriComponentsBuilder
                        .fromCurrentRequest()
                        .path("/{id}")
                        .buildAndExpand(id)
                        .toUri())
                .map(location -> ResponseEntity.created(location).build())
                .orElse(ResponseEntity.noContent().build());
    }

    @GetMapping("/api/elements/{id}")
    ResponseEntity<ElementResponse> findElement(@PathVariable("id") UUID id) {
        return elementService.findElement(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @ExceptionHandler(UncheckedIOException.class)
    ResponseEntity<ProblemDetail> handleIOException(UncheckedIOException exception) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.OK);
        problemDetail.setDetail(exception.getMessage());
        return ResponseEntity.ok()
                .body(problemDetail);
    }
}
