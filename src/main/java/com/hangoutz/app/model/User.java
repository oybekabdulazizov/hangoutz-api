package com.hangoutz.app.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Table(name = "user")
public class User implements UserDetails {

    @ManyToMany(
            cascade = {
                    CascadeType.MERGE,
                    CascadeType.DETACH,
                    CascadeType.PERSIST,
                    CascadeType.REFRESH})
    @JoinTable(
            name = "event_attendee",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "event_id")
    )
    private Set<Event> attendingEvents = new HashSet<>();

    @OneToMany(
            mappedBy = "host",
            cascade = {
                    CascadeType.REMOVE,
                    CascadeType.MERGE,
                    CascadeType.DETACH,
                    CascadeType.PERSIST,
                    CascadeType.REFRESH})
    private Set<Event> hostingEvents = new HashSet<>();

    @OneToMany(
            mappedBy = "user",
            cascade = {
                    CascadeType.REMOVE,
                    CascadeType.MERGE,
                    CascadeType.DETACH,
                    CascadeType.PERSIST,
                    CascadeType.REFRESH})
    private Set<Token> tokens = new HashSet<>();

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private String id;

    @Column(name = "name")
    private String name;

    @Column(name = "lastname")
    private String lastname;

    @Column(name = "date_of_birth")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDate dateOfBirth;

    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = "created_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDateTime createdAt;

    @Column(name = "last_modified_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDateTime lastModifiedAt;


    public void attendEvent(Event event) {
        if (event != null)
            attendingEvents.add(event);
    }

    public void cancelAttendanceToEvent(Event event) {
        if (event != null)
            attendingEvents.remove(event);
    }

    public void hostEvent(Event event) {
        if (event != null)
            hostingEvents.add(event);
    }

    public void addToken(Token token) {
        if (token != null)
            tokens.add(token);
    }


    @JsonManagedReference
    public Set<Event> getHostingEvents() {
        return hostingEvents;
    }

    @JsonManagedReference
    public void setHostingEvents(Set<Event> hostingEvents) {
        this.hostingEvents = hostingEvents;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }


    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", lastname='" + lastname + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                ", createdAt=" + createdAt +
                ", lastModifiedAt=" + lastModifiedAt +
                ", email='" + email + '\'' +
                ", accountNonExpired='" + isAccountNonExpired() + '\'' +
                ", accountNonLocked='" + isAccountNonLocked() + '\'' +
                ", credentialsNonExpired='" + isCredentialsNonExpired() + '\'' +
                ", enabled='" + isEnabled() + '\'' +
                ", role=" + role +
                '}';
    }
}
