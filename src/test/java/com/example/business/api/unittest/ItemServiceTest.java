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
import org.junit.Before;
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

    private ItemDTO testItemDTO;

    private Item testItem;
    private User testUser;

    private Authentication auth;

    private final Long CODE = 1L;
    private final Double PRICE = 12.5;
    private final String DESC = "Description";

    @Before
    public void setupTest() {
        testItemDTO = new ItemDTO();
        UserDTO testUserDTO = new UserDTO();

        Long ID = 1L;
        testItemDTO.setId(ID);
        testItemDTO.setCode(CODE);
        testItemDTO.setPrice(PRICE);
        testItemDTO.setDescription(DESC);

        testUserDTO.setUsername("user");
        testUserDTO.setPassword("hashed-password-goes-here");

        Set<ItemDTO> itemDTOS = new HashSet<>();
        itemDTOS.add(testItemDTO);

        testItemDTO.setCreator(testUserDTO);
        testUserDTO.setItems(itemDTOS);

        testItem = new Item();
        testUser = new User();

        testItem.setId(ID);
        testItem.setCode(CODE);
        testItem.setPrice(PRICE);
        testItem.setDescription(DESC);

        testUser.setUsername("user");
        testUser.setPassword("hashed-password-goes-here");

        Set<Item> items = new HashSet<>();
        items.add(testItem);

        testItem.setCreator(testUser);
        testUser.setItems(items);

        auth = new Authentication() {
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
                return testUser.getUsername();
            }
        };
    }

    @Test
    public void findAllItemsWithOneItem() {
        Set<Item> items = new HashSet<>();
        Set<ItemDTO> itemsDTO = new HashSet<>();

        itemsDTO.add(testItemDTO);
        items.add(testItem);

        Mockito.when(itemRepository.findAll()).thenReturn(items);
        Mockito.doReturn(itemsDTO).when(itemService).convertIterable2DTO(items);

        Iterable<ItemDTO> allItems = itemService.getAllItems();
        Assert.assertEquals(testItem.getCode(), allItems.iterator().next().getCode());
        Mockito.verify(itemService, Mockito.times(1)).getAllItems();
    }

    @Test
    public void getItemByExistingCode() {
        Long code = 1L;

        Mockito.when(itemRepository.findByCode(code)).thenReturn(Optional.of(testItem));
        Mockito.doReturn(testItemDTO).when(itemService).convert2DTO(testItem);

        Assert.assertEquals(code, itemService.getItemByCode(code).getCode());
        Mockito.verify(itemService, Mockito.times(1)).getItemByCode(code);
    }

    @Test
    public void getItemWithNoExistingCode() {
        Long code = 2L;

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
        Mockito.when(itemRepository.findByCode(1L)).thenReturn(Optional.empty());
        Mockito.when(userRepository.findByUsername("user")).thenReturn(Optional.of(testUser));
        Mockito.when(authenticationFacade.getAuthentication()).thenReturn(auth);

        itemService.saveItem(testItemDTO);

        Mockito.verify(itemService, Mockito.times(1)).saveItem(testItemDTO);
    }

    @Test
    public void addNewItemButDescriptionIsMissing() {
        testItemDTO.setDescription(null);

        Mockito.when(userRepository.findByUsername("user")).thenReturn(Optional.of(testUser));
        Mockito.when(authenticationFacade.getAuthentication()).thenReturn(auth);

        Exception exception = Assert.assertThrows(ResponseStatusException.class,
                () -> this.itemService.saveItem(testItemDTO));

        String expectedMessage = String.format("%s \"An item must have a non-empty description\"",
                HttpStatus.BAD_REQUEST.toString());

        String actualMessage = exception.getMessage();

        Assert.assertEquals(expectedMessage, actualMessage);

        Mockito.verify(itemService, Mockito.times(1)).saveItem(testItemDTO);

        testItemDTO.setDescription(DESC);
    }

    @Test
    public void addNewItemButPriceIsMissing() {
        testItemDTO.setPrice(null);

        Mockito.when(userRepository.findByUsername("user")).thenReturn(Optional.of(testUser));
        Mockito.when(authenticationFacade.getAuthentication()).thenReturn(auth);

        Exception exception = Assert.assertThrows(ResponseStatusException.class,
                () -> this.itemService.saveItem(testItemDTO));

        String expectedMessage = String.format("%s \"An item must have a valid price\"",
                HttpStatus.BAD_REQUEST.toString());

        String actualMessage = exception.getMessage();

        Assert.assertEquals(expectedMessage, actualMessage);

        Mockito.verify(itemService, Mockito.times(1)).saveItem(testItemDTO);

        testItemDTO.setPrice(PRICE);
    }

    @Test
    public void addNewItemButCodeIsMissing() {
        testItemDTO.setCode(null);

        Mockito.when(userRepository.findByUsername("user")).thenReturn(Optional.of(testUser));
        Mockito.when(authenticationFacade.getAuthentication()).thenReturn(auth);

        Exception exception = Assert.assertThrows(ResponseStatusException.class,
                () -> this.itemService.saveItem(testItemDTO));

        String expectedMessage = String.format("%s \"An item must have a valid code\"",
                HttpStatus.BAD_REQUEST.toString());

        String actualMessage = exception.getMessage();

        Assert.assertEquals(expectedMessage, actualMessage);

        Mockito.verify(itemService, Mockito.times(1)).saveItem(testItemDTO);

        testItemDTO.setCode(CODE);
    }

    @Test
    public void addNewItemButUserDoestNotExist() {
        Mockito.when(userRepository.findByUsername(testUser.getUsername())).thenReturn(Optional.empty());
        Mockito.when(authenticationFacade.getAuthentication()).thenReturn(auth);

        Exception exception = Assert.assertThrows(ResponseStatusException.class,
                () -> this.itemService.saveItem(testItemDTO));

        String expectedMessage = String.format("%s \"This action cannot be done with the current user\"",
                HttpStatus.FORBIDDEN.toString());

        String actualMessage = exception.getMessage();

        Assert.assertEquals(expectedMessage, actualMessage);

        Mockito.verify(itemService, Mockito.times(1)).saveItem(testItemDTO);
    }

    @Test
    public void updateItemWithExistingCode() {
        Long code = 1L;

        Mockito.when(itemRepository.findByCode(code)).thenReturn(Optional.of(testItem));

        itemService.updateItemWithCode(testItemDTO, code);

        Mockito.verify(itemService, Mockito.times(1)).updateItemWithCode(testItemDTO, code);
    }

    @Test
    public void updateItemWithNoExistingCode() {
        Long code = 2L;

        testItemDTO.setCode(code);

        Mockito.when(itemRepository.findByCode(code)).thenReturn(Optional.empty());

        Exception exception = Assert.assertThrows(ResponseStatusException.class,
                () -> this.itemService.updateItemWithCode(testItemDTO, code));

        String expectedMessage = String.format("%s \"The item '%s' does not exist\"",
                HttpStatus.NOT_FOUND.toString(), testItemDTO.getCode());

        String actualMessage = exception.getMessage();

        Assert.assertEquals(expectedMessage, actualMessage);

        Mockito.verify(itemService, Mockito.times(1)).updateItemWithCode(testItemDTO, code);

        testItemDTO.setCode(1L);
    }

    @Test
    public void updateItemWithEmptyDescription() {
        Long code = 1L;

        testItemDTO.setDescription("");

        Mockito.when(itemRepository.findByCode(code)).thenReturn(Optional.of(testItem));

        Exception exception = Assert.assertThrows(ResponseStatusException.class,
                () -> this.itemService.updateItemWithCode(testItemDTO, code));

        String expectedMessage = String.format("%s \"An item must have a non-empty description\"",
                HttpStatus.BAD_REQUEST.toString());

        String actualMessage = exception.getMessage();

        Assert.assertEquals(expectedMessage, actualMessage);

        Mockito.verify(itemService, Mockito.times(1)).updateItemWithCode(testItemDTO, code);

        testItemDTO.setDescription(DESC);
    }

    @Test
    public void updateItemWithInvalidPrice() {
        Long code = 1L;

        testItemDTO.setPrice(0.);

        Mockito.when(itemRepository.findByCode(code)).thenReturn(Optional.of(testItem));

        Exception exception = Assert.assertThrows(ResponseStatusException.class,
                () -> this.itemService.updateItemWithCode(testItemDTO, code));

        String expectedMessage = String.format("%s \"An item must have a valid price\"",
                HttpStatus.BAD_REQUEST.toString());

        String actualMessage = exception.getMessage();

        Assert.assertEquals(expectedMessage, actualMessage);

        Mockito.verify(itemService, Mockito.times(1)).updateItemWithCode(testItemDTO, code);

        testItemDTO.setPrice(PRICE);
    }

    @Test
    public void deleteExistingItem() {
        Long code = 1L;

        Mockito.when(itemRepository.findByCode(code)).thenReturn(Optional.of(testItem));

        itemService.deleteItem(testItemDTO);

        Mockito.verify(itemService, Mockito.times(1)).deleteItem(testItemDTO);
    }

    @Test
    public void deleteNoExistingItem() {
        Long code = 2L;

        testItemDTO.setCode(code);

        Mockito.when(itemRepository.findByCode(code)).thenReturn(Optional.empty());

        Exception exception = Assert.assertThrows(ResponseStatusException.class,
                () -> this.itemService.deleteItem(testItemDTO));

        String expectedMessage = String.format("%s \"The item '%s' does not exist\"",
                HttpStatus.NOT_FOUND.toString(), testItemDTO.getCode());

        String actualMessage = exception.getMessage();

        Assert.assertEquals(expectedMessage, actualMessage);

        Mockito.verify(itemService, Mockito.times(1)).deleteItem(testItemDTO);

        testItemDTO.setCode(CODE);
    }
}
