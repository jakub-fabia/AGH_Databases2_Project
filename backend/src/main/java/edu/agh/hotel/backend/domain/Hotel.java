package edu.agh.hotel.backend.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.time.LocalTime;
import java.util.Objects;

@Entity
@Table(name = "hotel")
public class Hotel {

    /* ---------- Primary key ---------- */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hotel_id")
    private Integer id;

    /* ---------- Required columns ---------- */

    @NotBlank
    @Size(max = 255)
    private String name;

    @NotBlank
    @Size(max = 30)
    private String country;

    @NotBlank
    @Size(max = 30)
    private String city;

    @NotBlank
    @Column(columnDefinition = "text")
    private String address;

    @NotBlank
    @Size(max = 20)
    private String phone;

    @Email
    @NotBlank
    @Size(max = 255)
    private String email;

    /* ---------- Optional / numeric ---------- */

    @Min(1) @Max(5)
    private Short stars;                       // smallint → Short (nullable)

    @Column(name = "review_sum", nullable = false)
    private int reviewSum = 0;                 // default 0 in DB and Java

    @Column(name = "review_count", nullable = false)
    private int reviewCount = 0;

    /* ---------- Times ---------- */

    @Column(name = "checkin_time", nullable = false)
    private LocalTime checkinTime;

    @Column(name = "checkout_time", nullable = false)
    private LocalTime checkoutTime;

    /* ---------- Constructors ---------- */

    protected Hotel() { }                      // JPA needs a no-arg ctor

    public Hotel(String name, String country, String city, String address,
                 String phone, String email, Short stars,
                 LocalTime checkinTime, LocalTime checkoutTime) {

        this.name = name;
        this.country = country;
        this.city = city;
        this.address = address;
        this.phone = phone;
        this.email = email;
        this.stars = stars;
        this.checkinTime = checkinTime;
        this.checkoutTime = checkoutTime;
    }

    /* ---------- Getters & setters  (or use Lombok @Getter/@Setter) ---------- */

    public Integer getId()                      { return id; }
    public String getName()                  { return name; }
    public void   setName(String name)       { this.name = name; }

    public String getCountry()               { return country; }
    public void   setCountry(String country) { this.country = country; }

    public String getCity()                  { return city; }
    public void   setCity(String city)       { this.city = city; }

    public String getAddress()               { return address; }
    public void   setAddress(String address) { this.address = address; }

    public String getPhone()                 { return phone; }
    public void   setPhone(String phone)     { this.phone = phone; }

    public String getEmail()                 { return email; }
    public void   setEmail(String email)     { this.email = email; }

    public Short  getStars()                 { return stars; }
    public void   setStars(Short stars)      { this.stars = stars; }

    public int    getReviewSum()             { return reviewSum; }
    public void   setReviewSum(int reviewSum){ this.reviewSum = reviewSum; }

    public int    getReviewCount()           { return reviewCount; }
    public void   setReviewCount(int reviewCount) { this.reviewCount = reviewCount; }

    public LocalTime getCheckinTime()        { return checkinTime; }
    public void      setCheckinTime(LocalTime t){ this.checkinTime = t; }

    public LocalTime getCheckoutTime()       { return checkoutTime; }
    public void      setCheckoutTime(LocalTime t){ this.checkoutTime = t; }

    /* ---------- Convenience (derived) ---------- */

    @Transient
    public double getAverageRating() {
        return reviewCount == 0 ? 0.0
                : (double) reviewSum / reviewCount;
    }

    /* ---------- equals / hashCode on id ---------- */

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Hotel other)) return false;
        return id != null && id.equals(other.id);
    }
    @Override public int hashCode() { return Objects.hashCode(id); }
}