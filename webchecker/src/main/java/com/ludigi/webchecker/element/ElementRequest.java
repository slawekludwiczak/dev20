package com.ludigi.webchecker.element;

import java.util.Objects;

record ElementRequest(String url, String selector) {
    ElementRequest {
        Objects.requireNonNull(url);
        Objects.requireNonNull(selector);
    }
}
