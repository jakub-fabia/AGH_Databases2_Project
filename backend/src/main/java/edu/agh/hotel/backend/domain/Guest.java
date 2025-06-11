package edu.agh.hotel.backend.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "guest")
public class Guest {

    /* ---------- Primary key ---------- */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "guest_id")
    private Integer id;

    /* ---------- Required fields ---------- */

    @NotBlank
    @Size(max = 50)
    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @NotBlank
    @Size(max = 50)
    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    @NotNull
    @Past
    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    @NotBlank
    @Size(max = 30)
    @Column(nullable = false, length = 30)
    private String country;

    @NotBlank
    @Size(max = 30)
    @Column(nullable = false, length = 30)
    private String city;

    @NotBlank
    @Column(nullable = false, columnDefinition = "text")
    private String address;

    /* ---------- Optional fields ---------- */

    @Size(max = 20)
    @Column(length = 20)
    private String phone;

    @Email
    @Size(max = 255)
    @Column(length = 255)
    private String email;

    /* ---------- Constructors ---------- */

    protected Guest() { }

    public Guest(String firstName, String lastName, LocalDate dateOfBirth,
                 String country, String city, String address,
                 String phone, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.country = country;
        this.city = city;
        this.address = address;
        this.phone = phone;
        this.email = email;
    }

    /* ---------- Getters & Setters ---------- */

    public Integer getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    /* ---------- Convenience ---------- */

    @Transient
    public String getFullName() {
        return firstName + " " + lastName;
    }

    /* ---------- equals & hashCode on id ---------- */

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Guest other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}