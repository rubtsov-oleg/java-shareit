package ru.practicum.shareit.request;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.user.User;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Integer> {

    List<ItemRequest> findAllByRequestorOrderByCreatedDesc(User requestor);

    Page<ItemRequest> findAllByRequestorNotOrderByCreatedDesc(User requestor, Pageable pageable);

}
