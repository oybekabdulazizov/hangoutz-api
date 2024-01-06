package com.hangoutz.app.model;

import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Table(name = "token")
public class Token {


    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private String id;

    @Column(name = "token", unique = true)
    private String token;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private TokenType type;

    @ManyToOne(
            cascade = {
                    CascadeType.MERGE,
                    CascadeType.DETACH,
                    CascadeType.PERSIST,
                    CascadeType.REFRESH})
    @JoinColumn(name = "user_id")
    private User user;
}
