package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;

/**
 * REST-контроллер операций над вещами: добавление, редактирование, просмотр и поиск.
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private static final String OWNER_HEADER = "X-Sharer-User-Id";

    private final ItemService itemService;

    /**
     * Добавляет новую вещь. Владельцем становится пользователь из заголовка
     * {@code X-Sharer-User-Id}.
     *
     * @param ownerId id владельца (из заголовка {@value #OWNER_HEADER})
     * @param itemDto данные вещи (название, описание, доступность)
     * @return созданная вещь с присвоенным id
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto create(@RequestHeader(OWNER_HEADER) Long ownerId, @Valid @RequestBody ItemDto itemDto) {
        return itemService.create(ownerId, itemDto);
    }

    /**
     * Частично обновляет название, описание и/или доступность вещи.
     * Редактировать вещь может только её владелец.
     *
     * @param ownerId id пользователя, выполняющего запрос (из заголовка {@value #OWNER_HEADER})
     * @param itemId  id обновляемой вещи
     * @param itemDto новые значения полей
     * @return обновлённая вещь
     */
    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader(OWNER_HEADER) Long ownerId,
                           @PathVariable Long itemId,
                           @RequestBody ItemDto itemDto) {
        return itemService.update(ownerId, itemId, itemDto);
    }

    /**
     * Возвращает вещь по id. Доступно любому пользователю.
     *
     * @param itemId id вещи
     * @return найденная вещь
     */
    @GetMapping("/{itemId}")
    public ItemDto findById(@PathVariable Long itemId) {
        return itemService.findById(itemId);
    }

    /**
     * Возвращает список вещей текущего владельца.
     *
     * @param ownerId id владельца (из заголовка {@value #OWNER_HEADER})
     * @return вещи владельца
     */
    @GetMapping
    public Collection<ItemDto> findAllByOwner(@RequestHeader(OWNER_HEADER) Long ownerId) {
        return itemService.findAllByOwner(ownerId);
    }

    /**
     * Ищет доступные для аренды вещи по тексту в названии или описании.
     *
     * @param text текст для поиска
     * @return найденные доступные вещи
     */
    @GetMapping("/search")
    public Collection<ItemDto> search(@RequestParam String text) {
        return itemService.search(text);
    }
}
