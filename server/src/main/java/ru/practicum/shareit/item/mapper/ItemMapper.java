package ru.practicum.shareit.item.mapper;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.List;

@Component
@AllArgsConstructor
public class ItemMapper {

    private final BookingMapper bookingMapper;

    public Item toItem(ItemDto itemDto) {
        return new Item(
                itemDto.getId(),
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getOwner(),
                itemDto.getAvailable(),
                itemDto.getRequest()
        );
    }

    public ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getOwner(),
                item.getAvailable(),
                item.getRequest() != null ? item.getRequest().getId() : null,
                item.getRequest(),
                null,
                null,
                new ArrayList<>()
        );
    }

    public ItemDto toItemDto(Item item, Booking last, Booking next, List<CommentDto> commentDtoList) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getOwner(),
                item.getAvailable(),
                item.getRequest() != null ? item.getRequest().getId() : null,
                item.getRequest(),
                bookingMapper.toBookingDto(last),
                bookingMapper.toBookingDto(next),
                commentDtoList
        );
    }

    public List<ItemDto> toItemsDto(List<Item> items) {
        List<ItemDto> itemsDto = new ArrayList<>();
        for (Item item : items) {
            itemsDto.add(toItemDto(item));
        }
        return itemsDto;
    }

    public List<Item> toItems(List<ItemDto> items) {
        List<Item> itemList = new ArrayList<>();
        if (items != null) {
            for (ItemDto itemDto : items) {
                itemList.add(toItem(itemDto));
            }
        }
        return itemList;
    }
}
