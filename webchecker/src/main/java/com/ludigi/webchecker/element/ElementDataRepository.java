package com.ludigi.webchecker.element;

import org.springframework.data.repository.CrudRepository;
import java.util.UUID;

interface ElementDataRepository extends CrudRepository<ElementData, UUID> {
}
