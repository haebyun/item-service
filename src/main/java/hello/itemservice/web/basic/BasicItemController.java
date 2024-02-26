package hello.itemservice.web.basic;

import hello.itemservice.domain.item.Item;
import hello.itemservice.domain.item.ItemRepository;
import jakarta.annotation.PostConstruct;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/basic/items")
@RequiredArgsConstructor    //final이 달려있는 클래스들 생성해줌
public class BasicItemController {

    private final ItemRepository itemRepository;
    @GetMapping
    public String items(Model model) {
        List<Item> items = itemRepository.findAll();
        model.addAttribute("items", items);
        return "basic/items";
    }

    @GetMapping("/{itemId}")
    public String item(@PathVariable("itemId") Long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "basic/item";
    }

    @GetMapping("/add")
    public String addForm() {
        return "basic/addForm";
    }

    //    @PostMapping("/add")
    public String addItemV1(@RequestParam String itemName,
                            @RequestParam int price,
                            @RequestParam Integer quantity,
                            Model model) {
        Item item = new Item();
        item.setItemName(itemName);
        item.setPrice(price);
        item.setQuantity(quantity);

        itemRepository.save(item);
        model.addAttribute("item", item);
        return "basic/item";
    }

    // @PostMapping("/add")
    public String addItemV2(@ModelAttribute("item") Item item, Model model) {
        itemRepository.save(item);
        model.addAttribute("item", item);
        return "basic/item";
    }

    //@PostMapping("/add")
    public String addItemV3(@ModelAttribute("item") Item item) {
        itemRepository.save(item);
        return "basic/item";
    }

    //@PostMapping("/add")
    // 이러한 방식은 매우 심각한 문제가 발생할 수 있다.
    // 그 이유는 데이터를 입력하고 저장을 선택하면 POST /add + 데이터를 서버로 전송하는데
    // 이 상태에서 새로고침을 하면 계속 POST /add + 데이터를 서버로 다시 전송하게 되기 때문이다.
    // 새로 고침은 마지막에 서버에 전송한 데이터를 다시 전송하는 것이 이 문제의 원인이다.
    public String addItemV4(Item item) {
        itemRepository.save(item);
        return "basic/item";
    }

    // 이러한 문제를 해결하기 위해서는 데이터를 저장하고 GET /items/{id}로 리다이렉트를 하면 해결이 된다.
    // 왜냐하면 마지막에 GET으로 바꿔주었기 때문에 새로 고침을 하여도 POST가 아닌 GET이 실행되기 때문이다.
    // 하지만 이 방법도 리다이렉트 URL에 변수를 더해서 사용하였기 때문에 URL 인코딩이 되지 않아서 위험하다.
    public String addItemV5(Item item) {
        itemRepository.save(item);
        return "redirect:/basic/items/"+item.getId();
    }

    @PostMapping("/add")
    public String addItemV6(Item item, RedirectAttributes redirectAttributes) {
        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/basic/items/{itemId}";
    }

    @GetMapping("/{itemId}/edit")
    public String editForm(@PathVariable("itemId") Long id, Model model) {
        model.addAttribute("item", itemRepository.findById(id));
        return "basic/editForm";
    }

    @PostMapping("/{itemId}/edit")
    public String editItem(@PathVariable("itemId") Long itemId, @ModelAttribute("item") Item item) {
        itemRepository.update(itemId, item);
        return "redirect:/basic/items/{itemId}";
    }

    @PostConstruct
    public void init() {
        itemRepository.save(new Item("testA", 10000, 10));
        itemRepository.save(new Item("testB", 20000, 20));
    }
}
