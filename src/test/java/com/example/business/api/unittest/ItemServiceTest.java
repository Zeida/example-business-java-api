package com.example.business.api.unittest;

import com.example.business.api.dto.ItemDTO;
import com.example.business.api.dto.UserDTO;
import com.example.business.api.model.Item;
import com.example.business.api.model.ItemStateEnum;
import com.example.business.api.model.User;
import com.example.business.api.repository.ItemRepository;
import com.example.business.api.repository.UserRepository;
import com.example.business.api.security.AuthenticationFacade;
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
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Collection;
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
    private UserRepository userRepository;

    @Mock
    private AuthenticationFacade authenticationFacade;

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
        Mockito.verify(itemService, Mockito.times(1)).getAllItems();
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
        Mockito.verify(itemService, Mockito.times(1)).getAllItems();
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
        Mockito.verify(itemService, Mockito.times(1)).getItemByCode(code);
    }

    @Test
    public void getItemWithNoExistingCode() {
        Long code = 2L;
        Item itemFromDB = new Item();
        itemFromDB.setId(1L);
        itemFromDB.setCode(1L);

        ItemDTO itemDTO = new ItemDTO();
        itemDTO.setId(itemFromDB.getId());
        itemDTO.setCode(itemFromDB.getCode());

        Mockito.when(itemRepository.findByCode(code)).thenReturn(Optional.empty());

        Exception exception = Assert.assertThrows(ResponseStatusException.class, () -> this.itemService.getItemByCode(code));

        String expectedMessage = String.format("%s \"The item '%s' does not exist\"",
                HttpStatus.NOT_FOUND.toString(), code);

        String actualMessage = exception.getMessage();

        Assert.assertEquals(expectedMessage, actualMessage);
        Mockito.verify(itemService, Mockito.times(1)).getItemByCode(code);
    }

    @Test
    public void addNewItem() {
        UserDTO user = new UserDTO();
        user.setUsername("user");
        user.setPassword("hashed-password-goes-here");
        user.setItems(new HashSet<>());

        ItemDTO itemToAdd = new ItemDTO();
        itemToAdd.setCode(1L);
        itemToAdd.setPrice(12.5);
        itemToAdd.setCreator(user);

        user.getItems().add(itemToAdd);

        User actualUser = new User(1L, "user", "hashed-password-goes-here", new HashSet<>());

        Item actualItem = new Item(1L, 1L, null, 12.5,
                ItemStateEnum.ACTIVE, null, null, LocalDateTime.now(), actualUser);

        actualUser.getItems().add(actualItem);

        Mockito.when(itemRepository.findByCode(1L)).thenReturn(Optional.empty());
        Mockito.when(userRepository.findByUsername("user")).thenReturn(Optional.of(actualUser));
        Mockito.when(authenticationFacade.getAuthentication()).thenReturn(new Authentication() {
            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                return null;
            }

            @Override
            public Object getCredentials() {
                return null;
            }

            @Override
            public Object getDetails() {
                return null;
            }

            @Override
            public Object getPrincipal() {
                return null;
            }

            @Override
            public boolean isAuthenticated() {
                return false;
            }

            @Override
            public void setAuthenticated(boolean b) throws IllegalArgumentException {

            }

            @Override
            public String getName() {
                return actualUser.getUsername();
            }
        });

        itemService.saveItem(itemToAdd);

        Mockito.verify(itemService, Mockito.times(1)).saveItem(itemToAdd);
    }

    @Test
    public void addNewItemButUserDoestNotExist() {
        UserDTO user = new UserDTO();
        user.setUsername("user");
        user.setPassword("hashed-password-goes-here");
        user.setItems(new HashSet<>());

        ItemDTO itemToAdd = new ItemDTO();
        itemToAdd.setCode(1L);
        itemToAdd.setPrice(12.5);
        itemToAdd.setCreator(user);

        Mockito.when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.empty());
        Mockito.when(authenticationFacade.getAuthentication()).thenReturn(new Authentication() {
            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                return null;
            }

            @Override
            public Object getCredentials() {
                return null;
            }

            @Override
            public Object getDetails() {
                return null;
            }

            @Override
            public Object getPrincipal() {
                return null;
            }

            @Override
            public boolean isAuthenticated() {
                return false;
            }

            @Override
            public void setAuthenticated(boolean b) throws IllegalArgumentException {

            }

            @Override
            public String getName() {
                return user.getUsername();
            }
        });

        user.getItems().add(itemToAdd);

        Exception exception = Assert.assertThrows(ResponseStatusException.class,
                () -> this.itemService.saveItem(itemToAdd));

        String expectedMessage = String.format("%s \"This action cannot be done with the current user\"",
                HttpStatus.FORBIDDEN.toString());

        String actualMessage = exception.getMessage();

        Assert.assertEquals(expectedMessage, actualMessage);

        Mockito.verify(itemService, Mockito.times(1)).saveItem(itemToAdd);
    }

    @Test
    public void updateItemWithExistingCodeAndNoIterablesAttributes() {
        ItemDTO itemToUpdate = new ItemDTO();
        itemToUpdate.setCode(1L);

        Item actualItem = new Item();
        actualItem.setCode(1L);

        Mockito.when(itemRepository.findByCode(1L)).thenReturn(Optional.of(actualItem));

        itemService.updateItemWithCode(itemToUpdate, 1L);

        Mockito.verify(itemService, Mockito.times(1)).updateItemWithCode(itemToUpdate, 1L);
    }

    @Test
    public void updateItemWithNoExistingCodeAndNoIterablesAttributes() {
        ItemDTO itemToUpdate = new ItemDTO();
        itemToUpdate.setCode(1L);

        Mockito.when(itemRepository.findByCode(1L)).thenReturn(Optional.empty());

        Exception exception = Assert.assertThrows(ResponseStatusException.class,
                () -> this.itemService.updateItemWithCode(itemToUpdate, 1L));

        String expectedMessage = String.format("%s \"The item '%s' does not exist\"",
                HttpStatus.NOT_FOUND.toString(), itemToUpdate.getCode());

        String actualMessage = exception.getMessage();

        Assert.assertEquals(expectedMessage, actualMessage);

        Mockito.verify(itemService, Mockito.times(1)).updateItemWithCode(itemToUpdate, 1L);
    }

    @Test
    public void deleteExistingItem() {
        ItemDTO itemToDelete = new ItemDTO();
        itemToDelete.setCode(1L);

        Item actualItem = new Item();
        actualItem.setCode(1L);

        Mockito.when(itemRepository.findByCode(1L)).thenReturn(Optional.of(actualItem));

        itemService.deleteItem(itemToDelete);

        Mockito.verify(itemService, Mockito.times(1)).deleteItem(itemToDelete);
    }

    @Test
    public void deleteNoExistingItem() {
        ItemDTO itemToDelete = new ItemDTO();
        itemToDelete.setCode(1L);

        Mockito.when(itemRepository.findByCode(1L)).thenReturn(Optional.empty());

        Exception exception = Assert.assertThrows(ResponseStatusException.class,
                () -> this.itemService.deleteItem(itemToDelete));

        String expectedMessage = String.format("%s \"The item '%s' does not exist\"",
                HttpStatus.NOT_FOUND.toString(), itemToDelete.getCode());

        String actualMessage = exception.getMessage();

        Assert.assertEquals(expectedMessage, actualMessage);

        Mockito.verify(itemService, Mockito.times(1)).deleteItem(itemToDelete);
    }
}
