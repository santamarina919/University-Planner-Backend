package dev.J.Entities;

import jakarta.persistence.Embeddable;

@Embeddable
public record Address( String streetNumber, String streetName, String city, String postalCode) {
    public static final String state = "CA";
}
