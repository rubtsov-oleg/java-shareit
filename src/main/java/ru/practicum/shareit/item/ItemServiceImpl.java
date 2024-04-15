package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDTO;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exceptions.BookinglValidationException;
import ru.practicum.shareit.exceptions.OwnerValidationException;
import ru.practicum.shareit.item.dto.CommentDTO;
import ru.practicum.shareit.item.dto.ItemDTO;
import ru.practicum.shareit.item.dto.ItemOutDTO;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final UserRepository userRepository;
    private final ItemMapper itemMapper;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;

    @Override
    @Transactional
    public ItemDTO saveItem(Integer userId, ItemDTO itemDTO) {
        itemDTO.setOwnerId(userId);
        return itemMapper.toDTO(itemRepository.save(itemMapper.toModel(itemDTO)));
    }

    @Override
    public ItemOutDTO findById(Integer userId, Integer id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Вещь " + id + " не найден"));
        ItemOutDTO itemOutDTO = itemMapper.toOutDTO(item);
        if (item.getOwner().getId().equals(userId)) {
            Optional<Booking> nextBooking = bookingRepository
                    .findFirstByItemAndStartAfterAndStatusNotOrderByStartAscIdAsc(
                            item, LocalDateTime.now(), BookingStatus.REJECTED);
            nextBooking.ifPresent(booking -> itemOutDTO.setNextBooking(bookingMapper.toDTO(booking)));

            Optional<Booking> lastBooking = bookingRepository
                    .findFirstByItemAndStartBeforeOrderByEndDescIdDesc(item, LocalDateTime.now());
            lastBooking.ifPresent(booking -> itemOutDTO.setLastBooking(bookingMapper.toDTO(booking)));
        }
        itemOutDTO.setComments(commentMapper.toListDTO(commentRepository.findAllByItemOrderByCreatedDesc(item)));
        return itemOutDTO;
    }

    @Override
    @Transactional
    public ItemDTO updateItem(Integer userId, Integer id, ItemDTO itemDTO) {
        ItemDTO existedItem = itemMapper.toDTO(itemRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Вещь " + id + " не найден")));
        if (!Objects.equals(existedItem.getOwnerId(), userId)) {
            throw new OwnerValidationException("Пользователь " + userId + " не может изменять вещь " + id);
        }
        if (itemDTO.getName() != null) {
            existedItem.setName(itemDTO.getName());
        }
        if (itemDTO.getDescription() != null) {
            existedItem.setDescription(itemDTO.getDescription());
        }
        if (itemDTO.getAvailable() != null) {
            existedItem.setAvailable(itemDTO.getAvailable());
        }
        return itemMapper.toDTO(itemRepository.save(itemMapper.toModel(existedItem)));
    }

    @Override
    public List<ItemOutDTO> findAllByOwner(Integer id) {
        User owner = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Пользователь " + id + " не найден"));
        return bookingEnrichment(itemRepository.findAllByOwnerOrderByIdAsc(owner));
    }

    @Override
    public List<ItemOutDTO> search(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        return bookingEnrichment(itemRepository.search(text));
    }

    public List<ItemOutDTO> bookingEnrichment(List<Item> itemList) {
        List<BookingDTO> nextBookingDTOList = bookingMapper
                .toListDTO(bookingRepository.findNextBookingsByItems(itemList));
        List<BookingDTO> lastBookingDTOList = bookingMapper
                .toListDTO(bookingRepository.findLastBookingsByItems(itemList));
        List<ItemOutDTO> output = new ArrayList<>();
        for (Item item : itemList) {
            ItemOutDTO itemOutDTO = itemMapper.toOutDTO(item);
            for (BookingDTO nextBookingDTO : nextBookingDTOList) {
                if (nextBookingDTO.getItemId().equals(item.getId())) {
                    itemOutDTO.setNextBooking(nextBookingDTO);
                    break;
                }
            }
            for (BookingDTO lastBookingDTO : lastBookingDTOList) {
                if (lastBookingDTO.getItemId().equals(item.getId())) {
                    itemOutDTO.setLastBooking(lastBookingDTO);
                    break;
                }
            }
            output.add(itemOutDTO);
        }
        return output;
    }

    @Override
    @Transactional
    public CommentDTO createComment(Integer userId, Integer itemId, CommentDTO commentDTO) {
        Item existedItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new NoSuchElementException("Вещь " + itemId + " не найдена"));
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("Пользователь " + userId + " не найден"));

        if (bookingRepository
                .findFirstByBookerAndItemAndStatusNotAndEndBeforeOrderByEndDesc(
                        author, existedItem, BookingStatus.REJECTED, LocalDateTime.now()).isEmpty()) {
            throw new BookinglValidationException("Пользователь не бронировал данную вещь");
        }
        Comment comment = new Comment();
        comment.setItem(existedItem);
        comment.setAuthor(author);
        comment.setText(commentDTO.getText());
        return commentMapper.toDTO(commentRepository.save(comment));
    }
}
