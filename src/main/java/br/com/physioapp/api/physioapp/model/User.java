package br.com.physioapp.api.physioapp.model;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.management.relation.Role;

import org.hibernate.annotations.Check;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "users", uniqueConstraints = {
    @UniqueConstraint(name = "uk_users_email", columnNames = { "email" })
})
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "user_type", discriminatorType = DiscriminatorType.STRING)
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "user_type", visible = true)
@JsonSubTypes({
    @JsonSubTypes.Type(value = Patient.class, name = "PATIENT"),
    @JsonSubTypes.Type(value = Physiotherapist.class, name = "PHYSIO")
})
@Check(constraints = "user_type <> 'PHYSIO' OR crefito IS NOT NULL")
@Check(constraints = "user_type <> 'PATIENT' OR crefito IS NULL")
@Check(constraints = "user_type IN ('PATIENT', 'PHYSIO')")
public abstract class User {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @EqualsAndHashCode.Include
  private UUID id;

  @NotBlank
  @Column(nullable = false)
  private String fullname;

  @NotBlank
  @Column(nullable = false, unique = true, length = 255)
  private String email;

  @Column(nullable = false)
  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  @NotBlank
  @ToString.Exclude
  private String password;

  @Enumerated(EnumType.STRING)
  @Column(name = "user_type", insertable = false, updatable = false, nullable = false)
  private UserType type;

  @Column(nullable = false)
  private boolean isActive = true;

  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
  @Column(name = "role")
  @Enumerated(EnumType.STRING)
  private Set<Role> roles = new HashSet<>();

  @CreationTimestamp
  private LocalDateTime createdAt;

  @UpdateTimestamp
  private LocalDateTime updatedAt;

  @Version
  private Long version;
}
