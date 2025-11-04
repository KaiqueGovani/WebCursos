package com.morangosdoamor.WebCursos.domain.valueobject;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Email {

    @jakarta.validation.constraints.Email
    @NotBlank
    @Column(name = "email", nullable = false, unique = true, length = 255)
    private String value;

    @Override
    public String toString() {
        return value;
    }
}
