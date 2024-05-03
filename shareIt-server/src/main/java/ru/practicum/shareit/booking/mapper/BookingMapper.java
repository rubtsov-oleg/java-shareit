package ru.practicum.shareit.booking.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.booking.dto.BookingOutDTO;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.dto.BookingDTO;

import java.util.List;

@Mapper(componentModel = "spring", uses = {BookerMapper.class, ItemMapper.class})
public interface BookingMapper {
    @Mapping(source = "bookerId", target = "booker")
    @Mapping(source = "itemId", target = "item")
    Booking toModel(BookingDTO bookingDTO);

    @Mapping(source = "booker.id", target = "bookerId")
    @Mapping(source = "item.id", target = "itemId")
    BookingDTO toDTO(Booking booking);

    BookingOutDTO toOutDTO(Booking booking);

    List<Booking> toListModels(List<BookingDTO> bookingDTOList);

    List<BookingDTO> toListDTO(List<Booking> bookingList);

    List<BookingOutDTO> toListOutDTO(List<Booking> bookingList);
}
