package com.hangoutz.app.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

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

    @OneToMany(
            mappedBy = "host",
            cascade = {
                    CascadeType.REMOVE,
                    CascadeType.MERGE,
                    CascadeType.DETACH,
                    CascadeType.PERSIST,
                    CascadeType.REFRESH})
    private Set<Event> hostingEvents;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private String id;

    @Column(name = "name")
    private String name;

    @Column(name = "lastname")
    private String lastname;

    @Column(name = "date_of_birth")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime dateOfBirth;

    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private Role role;


    public void hostEvent(Event event) {
        if (hostingEvents == null) {
            hostingEvents = new HashSet<>();
        }
        hostingEvents.add(event);
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
                ", email='" + email + '\'' +
                ", accountNonExpired='" + isAccountNonExpired() + '\'' +
                ", accountNonLocked='" + isAccountNonLocked() + '\'' +
                ", credentialsNonExpired='" + isCredentialsNonExpired() + '\'' +
                ", enabled='" + isEnabled() + '\'' +
                ", role=" + role +
                '}';
    }
}
