package ru.practicum.shareit.item.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.util.EntityUtils;

/**
 * Вещь, которой пользователь (владелец) готов поделиться.
 */
@Entity
@Table(name = "items")
@Getter
@Setter
@NoArgsConstructor
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(name = "is_available", nullable = false)
    private Boolean available;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    /**
     * Запрос, в ответ на который была добавлена эта вещь. {@code null}, если вещь
     * добавлена не по запросу. Не персистится — полноценно заработает в спринте
     * add-item-requests, когда появится таблица requests.
     */
    @Transient
    private ItemRequest request;

    public Item(Long id, String name, String description, Boolean available, User owner) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
        this.owner = owner;
    }

    /**
     * @param userId id проверяемого пользователя
     * @return {@code true}, если пользователь с этим id — владелец вещи
     */
    public boolean isOwnedBy(Long userId) {
        return owner.getId().equals(userId);
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || EntityUtils.getEffectiveClass(this) != EntityUtils.getEffectiveClass(o)) {
            return false;
        }
        Item other = (Item) o;
        return getId() != null && getId().equals(other.getId());
    }

    @Override
    public final int hashCode() {
        return EntityUtils.getEffectiveClass(this).hashCode();
    }
}
