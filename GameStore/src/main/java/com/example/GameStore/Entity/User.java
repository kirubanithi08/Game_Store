package com.example.GameStore.Entity;

import jakarta.annotation.Nonnull;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue
    Long id;
    @Nonnull
    String username;
    @Nonnull
    String password;
}
