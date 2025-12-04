package dev.J;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;

@Embeddable
public record Address( String streetNumber, String streetName, String city, String postalCode) {
    public static final String state = "CA";
}
