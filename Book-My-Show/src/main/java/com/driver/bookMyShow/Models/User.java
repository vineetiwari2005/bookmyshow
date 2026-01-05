package com.driver.bookMyShow.Models;

import com.driver.bookMyShow.Enums.Gender;
import com.driver.bookMyShow.Enums.UserRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "USERS")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;

    private Integer age;

    private String address;

    @Enumerated(value = EnumType.STRING)
    private Gender gender;

    private String mobileNo;

    @Column(unique = true)
    private String emailId;

    // New authentication fields (nullable to preserve existing data)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private UserRole role = UserRole.USER; // Default role for backward compatibility

    private Boolean isActive = true;

    // Wallet balance for promo codes and refunds
    private Double walletBalance = 0.0;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Ticket> ticketList = new ArrayList<>();
}
