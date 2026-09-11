package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    private User requestor;
    private User owner;

    @BeforeEach
    void setUp() {
        requestor = new User(1L, "Ivan", "ivan@mail.ru");
        owner = new User(2L, "Petr", "petr@mail.ru");
    }

    @Test
    void create_whenRequestorExists_savesRequest() {
        ItemRequestDto inputDto = new ItemRequestDto(null, "Нужна дрель", null, null);
        when(userService.getUserById(1L)).thenReturn(requestor);
        when(itemRequestRepository.save(any(ItemRequest.class))).thenAnswer(invocation -> {
            ItemRequest request = invocation.getArgument(0);
            request.setId(5L);
            return request;
        });

        ItemRequestDto result = itemRequestService.create(1L, inputDto);

        assertThat(result.getId()).isEqualTo(5L);
        assertThat(result.getDescription()).isEqualTo("Нужна дрель");
        assertThat(result.getItems()).isEmpty();
    }

    @Test
    void create_whenRequestorMissing_propagatesNotFoundAndDoesNotSave() {
        ItemRequestDto inputDto = new ItemRequestDto(null, "Нужна дрель", null, null);
        when(userService.getUserById(99L)).thenThrow(new NotFoundException("Пользователь с id 99 не найден"));

        assertThatThrownBy(() -> itemRequestService.create(99L, inputDto)).isInstanceOf(NotFoundException.class);

        verify(itemRequestRepository, never()).save(any());
    }

    @Test
    void findOwn_returnsRequestsWithTheirItems() {
        ItemRequest request = itemRequestWithId(5L);
        Item item = new Item(10L, "Дрель", "desc", true, owner);
        item.setRequest(request);
        when(userService.getUserById(1L)).thenReturn(requestor);
        when(itemRequestRepository.findByRequestorIdOrderByCreatedDesc(1L)).thenReturn(List.of(request));
        when(itemRepository.findAllByRequestIdIn(List.of(5L))).thenReturn(List.of(item));

        List<ItemRequestDto> result = List.copyOf(itemRequestService.findOwn(1L));

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(5L);
        assertThat(result.get(0).getItems()).hasSize(1);
        assertThat(result.get(0).getItems().get(0).getName()).isEqualTo("Дрель");
    }

    @Test
    void findOwn_whenRequestorMissing_propagatesNotFound() {
        when(userService.getUserById(99L)).thenThrow(new NotFoundException("Пользователь с id 99 не найден"));

        assertThatThrownBy(() -> itemRequestService.findOwn(99L)).isInstanceOf(NotFoundException.class);
    }

    @Test
    void findAllByOthers_returnsOtherUsersRequests() {
        ItemRequest request = itemRequestWithId(5L);
        when(userService.getUserById(2L)).thenReturn(owner);
        when(itemRequestRepository.findByRequestorIdNotOrderByCreatedDesc(2L)).thenReturn(List.of(request));
        when(itemRepository.findAllByRequestIdIn(List.of(5L))).thenReturn(List.of());

        List<ItemRequestDto> result = List.copyOf(itemRequestService.findAllByOthers(2L));

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(5L);
        assertThat(result.get(0).getItems()).isEmpty();
    }

    @Test
    void findById_whenRequestExists_returnsRequestWithItems() {
        ItemRequest request = itemRequestWithId(5L);
        Item item = new Item(10L, "Дрель", "desc", true, owner);
        when(userService.getUserById(2L)).thenReturn(owner);
        when(itemRequestRepository.findById(5L)).thenReturn(Optional.of(request));
        when(itemRepository.findAllByRequestIdIn(List.of(5L))).thenReturn(List.of(item));

        ItemRequestDto result = itemRequestService.findById(2L, 5L);

        assertThat(result.getId()).isEqualTo(5L);
        assertThat(result.getItems()).hasSize(1);
    }

    @Test
    void findById_whenRequestMissing_throwsNotFound() {
        when(userService.getUserById(1L)).thenReturn(requestor);
        when(itemRequestRepository.findById(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemRequestService.findById(1L, 404L)).isInstanceOf(NotFoundException.class);

        verify(itemRepository, never()).findAllByRequestIdIn(any());
    }

    @Test
    void findById_whenUserMissing_propagatesNotFoundAndDoesNotLookUpRequest() {
        when(userService.getUserById(99L)).thenThrow(new NotFoundException("Пользователь с id 99 не найден"));

        assertThatThrownBy(() -> itemRequestService.findById(99L, 5L)).isInstanceOf(NotFoundException.class);

        verify(itemRequestRepository, never()).findById(anyLong());
    }

    private ItemRequest itemRequestWithId(Long id) {
        ItemRequest request = new ItemRequest();
        request.setId(id);
        request.setDescription("Нужна дрель");
        request.setRequestor(requestor);
        request.setCreated(LocalDateTime.now());
        return request;
    }
}
