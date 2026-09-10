package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.practicum.shareit.AbstractIntegrationTest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.User;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Проверяет, что обращение к ленивым ассоциациям ({@code Item.owner}) после
 * возврата из репозитория не бросает {@link org.hibernate.LazyInitializationException}.
 */
class ItemRequestServiceIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private ItemRequestService itemRequestService;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Test
    void create_doesNotThrowLazyInitializationException() {
        User requestor = createUser("req-requestor-create@mail.ru");

        ItemRequestDto result = itemRequestService.create(
                requestor.getId(), new ItemRequestDto(null, "Нужна дрель", null, null));

        assertThat(result.getId()).isNotNull();
        assertThat(result.getDescription()).isEqualTo("Нужна дрель");
        assertThat(result.getItems()).isEmpty();
    }

    @Test
    void findById_withResponseItem_doesNotThrowLazyInitializationException() {
        User requestor = createUser("req-requestor-find@mail.ru");
        User owner = createUser("req-owner-find@mail.ru");
        ItemRequestDto request = itemRequestService.create(
                requestor.getId(), new ItemRequestDto(null, "Нужна дрель", null, null));
        Item item = new Item(null, "Дрель", "desc", true, owner);
        item.setRequest(itemRequestRepository.findById(request.getId()).orElseThrow());
        itemRepository.save(item);

        ItemRequestDto result = itemRequestService.findById(owner.getId(), request.getId());

        assertThat(result.getItems()).hasSize(1);
        assertThat(result.getItems().get(0).getName()).isEqualTo("Дрель");
        assertThat(result.getItems().get(0).getOwnerId()).isEqualTo(owner.getId());
    }

    @Test
    void findOwn_returnsOnlyOwnRequests() {
        User requestor = createUser("req-requestor-own@mail.ru");
        User other = createUser("req-other-own@mail.ru");
        itemRequestService.create(requestor.getId(), new ItemRequestDto(null, "Нужна дрель", null, null));
        itemRequestService.create(other.getId(), new ItemRequestDto(null, "Нужна лопата", null, null));

        List<ItemRequestDto> result = List.copyOf(itemRequestService.findOwn(requestor.getId()));

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getDescription()).isEqualTo("Нужна дрель");
    }

    @Test
    void findAllByOthers_excludesOwnRequests() {
        User requestor = createUser("req-requestor-others@mail.ru");
        User other = createUser("req-other-others@mail.ru");
        itemRequestService.create(requestor.getId(), new ItemRequestDto(null, "Нужна дрель-excl", null, null));
        itemRequestService.create(other.getId(), new ItemRequestDto(null, "Нужна лопата-excl", null, null));

        List<String> descriptions = itemRequestService.findAllByOthers(requestor.getId()).stream()
                .map(ItemRequestDto::getDescription)
                .collect(Collectors.toList());

        assertThat(descriptions).contains("Нужна лопата-excl");
        assertThat(descriptions).doesNotContain("Нужна дрель-excl");
    }
}
