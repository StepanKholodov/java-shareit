package ru.practicum.shareit.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.util.EntityUtils;

/**
 * Пользователь сервиса ShareIt.
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Override
    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || EntityUtils.getEffectiveClass(this) != EntityUtils.getEffectiveClass(o)) {
            return false;
        }
        User other = (User) o;
        return getId() != null && getId().equals(other.getId());
    }

    @Override
    public final int hashCode() {
        return EntityUtils.getEffectiveClass(this).hashCode();
    }
}
