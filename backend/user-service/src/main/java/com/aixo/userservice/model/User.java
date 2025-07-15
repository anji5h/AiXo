package com.aixo.userservice.model;

import com.aixo.userservice.enums.OAuthProvider;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "users")
@EntityListeners(AuditingEntityListener.class)
@Getter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID userId;

    @Setter
    @Column(unique = true, nullable = false)
    private String oAuthId;

    @Setter
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private OAuthProvider oAuthProvider = OAuthProvider.GOOGLE;

    @Setter
    @Column(nullable = false, length = 100)
    private String name;

    @Setter
    @Column(unique = true, nullable = false)
    private String email;

    @Setter
    @Column()
    private String imageURL;

    @CreatedDate
    @Column(nullable = false, updatable = false, columnDefinition = "TIMESTAMPTZ")
    private Instant createdAt;

    @LastModifiedDate
    @Column(nullable = false, columnDefinition = "TIMESTAMPTZ")
    private Instant updatedAt;
}
