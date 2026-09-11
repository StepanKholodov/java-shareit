package ru.practicum.shareit.item.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

/**
 * JPA-репозиторий вещей.
 */
public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findAllByOwnerId(Long ownerId);

    List<Item> findAllByRequestIdIn(List<Long> requestIds);

    /**
     * @param text текст для поиска; спецсимволы {@code LIKE} ({@code %}, {@code _}, {@code \})
     *             должны быть уже экранированы вызывающим кодом (см. {@code ItemServiceImpl}),
     *             иначе они будут трактоваться как символы-маски, а не буквально
     * @return доступные вещи, в названии или описании которых встречается текст
     */
    @Query("select i from Item i "
            + "where i.available = true "
            + "and (lower(i.name) like lower(concat('%', :text, '%')) escape '\\' "
            + "or lower(i.description) like lower(concat('%', :text, '%')) escape '\\')")
    List<Item> search(@Param("text") String text);
}
