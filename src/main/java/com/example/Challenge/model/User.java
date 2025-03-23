package com.example.Challenge.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String telegramId;

    @Column(nullable = false, unique = true)
    private String username;

    private String firstName;

    private String lastName;

    @Column(nullable = false)
    private String role;

    private String password;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_permissions", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "permission")
    private Set<String> permissions = new HashSet<>();

    // Quiz funksionalligi uchun qo'shimcha
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<UserTest> userTests = new ArrayList<>();

    public void addPermission(String permission) {
        this.permissions.add(permission);
    }

    public void removePermission(String permission) {
        this.permissions.remove(permission);
    }

    public boolean hasPermission(String permission) {
        return this.permissions.contains(permission);
    }

    // Admin ekanligini tekshirish uchun qo'shimcha metod
    public boolean isAdmin() {
        // Rolni tekshirish
        return "ADMIN".equals(this.role) || "SUPERADMIN".equals(this.role);
    }
}