package com.ludigi.webchecker.element;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "element")
class ElementData {
    @Id
    private UUID id;
    private String url;
    private String selector;
    @Column(name = "element_value")
    private String value;
    private LocalDateTime fetchTime;

    public ElementData() {
    }

    public ElementData(UUID id, String url, String selector, String value, LocalDateTime fetchTime) {
        this.id = id;
        this.url = url;
        this.selector = selector;
        this.value = value;
        this.fetchTime = fetchTime;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSelector() {
        return selector;
    }

    public void setSelector(String selector) {
        this.selector = selector;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public LocalDateTime getFetchTime() {
        return fetchTime;
    }

    public void setFetchTime(LocalDateTime fetchTime) {
        this.fetchTime = fetchTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ElementData that = (ElementData) o;
        return Objects.equals(id, that.id) && Objects.equals(url, that.url) && Objects.equals(selector, that.selector) && Objects.equals(value, that.value) && Objects.equals(fetchTime, that.fetchTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, url, selector, value, fetchTime);
    }
}
