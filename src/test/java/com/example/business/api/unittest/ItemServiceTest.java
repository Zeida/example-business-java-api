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

import java.util.HashSet;
import java.util.Optional;
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
    public void findAllItemsWithOneItem() {
        Set<Item> items = new HashSet<>();
        Set<ItemDTO> itemsDTO = new HashSet<>();

        User user = new User();
        user.setId(1L);

        Item item = new Item();
        item.setId(1L);

        items.add(item);

        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());

        ItemDTO itemDTO = new ItemDTO();
        itemDTO.setId(item.getId());

        itemsDTO.add(itemDTO);

        Mockito.when(itemRepository.findAll()).thenReturn(items);
        Mockito.doReturn(itemsDTO).when(itemService).convertIterable2DTO(items);

        Iterable<ItemDTO> allItems = itemService.getAllItems();

        Assert.assertEquals(new Long(1L), allItems.iterator().next().getId());
    }

    @Test
    public void findAllItemsWith3Items() {
        Set<Item> items = new HashSet<>();
        Set<ItemDTO> itemsDTO = new HashSet<>();

        User user = new User();
        user.setId(1L);

        Item item1 = new Item();
        item1.setId(1L);

        Item item2 = new Item();
        item2.setId(2L);

        Item item3 = new Item();
        item3.setId(3L);

        items.add(item1);
        items.add(item2);
        items.add(item3);

        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());

        ItemDTO itemDTO1 = new ItemDTO();
        itemDTO1.setId(item1.getId());

        ItemDTO itemDTO2 = new ItemDTO();
        itemDTO2.setId(item2.getId());

        ItemDTO itemDTO3 = new ItemDTO();
        itemDTO3.setId(item3.getId());

        itemsDTO.add(itemDTO1);
        itemsDTO.add(itemDTO2);
        itemsDTO.add(itemDTO3);

        Mockito.when(itemRepository.findAll()).thenReturn(items);
        Mockito.doReturn(itemsDTO).when(itemService).convertIterable2DTO(items);

        Iterable<ItemDTO> allItems = itemService.getAllItems();

        allItems.forEach(givenItem -> Assert.assertTrue(itemsDTO.contains(givenItem)));
    }

    @Test
    public void getItemByExistingCode() {
        Long code = 1L;
        Item itemFromDB = new Item();
        itemFromDB.setId(1L);
        itemFromDB.setCode(code);

        ItemDTO itemDTO = new ItemDTO();
        itemDTO.setId(itemFromDB.getId());
        itemDTO.setCode(itemFromDB.getCode());

        Mockito.when(itemRepository.findByCode(code)).thenReturn(Optional.of(itemFromDB));
        Mockito.doReturn(itemDTO).when(itemService).convert2DTO(itemFromDB);

        Assert.assertEquals(code, itemService.getItemByCode(code).getCode());
    }
}
