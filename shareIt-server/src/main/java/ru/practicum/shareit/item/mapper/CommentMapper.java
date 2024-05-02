package ru.practicum.shareit.item.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.dto.CommentDTO;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CommentMapper {
    @Mapping(source = "author.name", target = "authorName")
    CommentDTO toDTO(Comment comment);

    List<CommentDTO> toListDTO(List<Comment> commentList);

    Comment toModel(CommentDTO commentDTO);
}
