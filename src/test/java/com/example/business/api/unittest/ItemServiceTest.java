package com.example.business.api.unittest;

import com.example.business.api.dto.ItemDTO;
import com.example.business.api.dto.UserDTO;
import com.example.business.api.model.Item;
import com.example.business.api.model.ItemStateEnum;
import com.example.business.api.model.User;
import com.example.business.api.repository.ItemRepository;
import com.example.business.api.repository.PriceReductionRepository;
import com.example.business.api.repository.SupplierRepository;
import com.example.business.api.service.ItemServiceImpl;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@RunWith(MockitoJUnitRunner.class)
public class ItemServiceTest {
    @Spy
    @InjectMocks
    private ItemServiceImpl itemService;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private SupplierRepository supplierRepository;

    @Mock
    private PriceReductionRepository priceReductionRepository;

    @Test
    public void findAllItems() {
        Set<Item> items = new HashSet<>();
        Set<ItemDTO> itemsDTO = new HashSet<>();

        User user = new User();
        user.setId(1L);
        user.setUsername("username");
        user.setPassword("password");
        user.setItems(items);

        Item item = new Item();
        item.setId(1L);
        item.setCode(1234567890L);
        item.setDescription("Description");
        item.setPrice(12.32);
        item.setState(ItemStateEnum.ACTIVE);
        item.setCreationDate(LocalDateTime.now());
        item.setCreator(user);

        items.add(item);

        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setUsername(user.getUsername());
        userDTO.setPassword(user.getPassword());
        userDTO.setItems(itemsDTO);

        ItemDTO itemDTO = new ItemDTO();
        itemDTO.setId(1L);

        itemsDTO.add(itemDTO);

        Mockito.when(itemRepository.findAll()).thenReturn(items);
        Mockito.doReturn(itemsDTO).when(itemService).convertIterable2DTO(items);

        Iterable<ItemDTO> allItems = itemService.getAllItems();

        Long id = 1L;

        Assert.assertEquals(id, allItems.iterator().next().getId());
    }
}
