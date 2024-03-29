package hello.itemservice.domain.item;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Repository;

@Repository
public class ItemRepository {
    private static final Map<Long, Item> store = new ConcurrentHashMap<>();
    private static final AtomicLong sequence = new AtomicLong(0);

    public Item save(Item item) {
        item.setId(sequence.incrementAndGet());
        store.put(item.getId(), item);
        return item;
    }

    public Item findById(Long id) {
        return store.get(id);
    }

    public List<Item> findAll() {   //ArrayList로 한 번 감싸서 리턴할 경우 store에 변경이 생기지 않아서 좋은 방식이다.
        return new ArrayList<>(store.values());
    }

    public void update(Long id, Item updateParam) {
        Item item = store.get(id);
        item.setItemName(updateParam.getItemName());
        item.setPrice(updateParam.getPrice());
        item.setQuantity(updateParam.getQuantity());
    }

    public void clearStore() {
        store.clear();
    }
}
