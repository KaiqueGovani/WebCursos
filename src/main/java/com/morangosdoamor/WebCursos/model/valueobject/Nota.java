package com.morangosdoamor.WebCursos.model.valueobject;

import java.util.Objects;

import jakarta.persistence.Embeddable;

@Embeddable
public class Nota {

    private float value;

    public Nota(float value) {
        if (value < 0 || value > 10) {
            throw new IllegalArgumentException("Nota must be between 0 and 10");
        }
        this.value = value;
    }

    protected Nota() {
        // For JPA
    }

    public float getValue() {
        return value;
    }

    public boolean isAprovado() {
        return value >= 7.0f;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Nota nota = (Nota) o;
        return Float.compare(nota.value, value) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
