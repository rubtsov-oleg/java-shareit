package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.*;

@Repository(value = "itemMemory")
public class InMemoryItemStorage implements ItemDAO {
    private Integer id = 1;
    private final Map<Integer, Item> items = new HashMap<>();

    @Override
    public Item save(Item item) {
        item.setId(createIdItem());
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item update(Item item) {
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Optional<Item> findById(Integer id) {
        return Optional.ofNullable(items.get(id));
    }

    @Override
    public List<Item> findAllByOwner(Integer id) {
        ArrayList<Item> finds = new ArrayList<>();
        for (Item item : items.values()) {
            if (item.getOwner().equals(id)) {
                finds.add(item);
            }
        }
        return finds;
    }

    @Override
    public List<Item> search(String text) {
        ArrayList<Item> finds = new ArrayList<>();
        if (text.isBlank()) {
            return finds;
        }
        for (Item item : items.values()) {
            if (item.getName().toLowerCase().contains(text.toLowerCase()) ||
                    item.getDescription().toLowerCase().contains(text.toLowerCase()) &&
                            item.getAvailable().equals(true)) {
                finds.add(item);
            }
        }
        return finds;
    }

    private Integer createIdItem() {
        return id++;
    }
}
