package com.ludigi.webchecker.element;

import java.time.LocalDateTime;
import java.util.UUID;

record ElementResponse(
        UUID id,
        String value,
        String url,
        String selector,
        LocalDateTime fetchTime
) { }
