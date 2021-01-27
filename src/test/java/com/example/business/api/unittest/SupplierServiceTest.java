package com.example.business.api.unittest;

import com.example.business.api.dto.ItemDTO;
import com.example.business.api.dto.SupplierDTO;
import com.example.business.api.dto.UserDTO;
import com.example.business.api.model.Item;
import com.example.business.api.model.ItemStateEnum;
import com.example.business.api.model.Supplier;
import com.example.business.api.model.User;
import com.example.business.api.repository.ItemRepository;
import com.example.business.api.repository.SupplierRepository;
import com.example.business.api.service.SupplierServiceImpl;
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
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@RunWith(MockitoJUnitRunner.class)
public class SupplierServiceTest {
    @Spy
    @InjectMocks
    private SupplierServiceImpl supplierService;

    @Mock
    private SupplierRepository supplierRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private ItemRepository itemRepository;

    @Test
    public void findAllSuppliersWithOneSupplier() {
        Set<Supplier> suppliers = new HashSet<>();
        Set<SupplierDTO> suppliersDTO = new HashSet<>();

        Set<Item> items = new HashSet<>();
        Set<ItemDTO> itemsDTO = new HashSet<>();

        User user = new User();
        user.setId(1L);

        Item item = new Item();
        item.setId(1L);

        items.add(item);

        Supplier supplier = new Supplier(1L, "Supplier Test", "USA", items);

        suppliers.add(supplier);

        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());

        ItemDTO itemDTO = new ItemDTO();
        itemDTO.setId(item.getId());

        itemsDTO.add(itemDTO);

        SupplierDTO supplierDTO = new SupplierDTO();
        supplierDTO.setId(1L);
        supplierDTO.setName("Supplier Test");
        supplierDTO.setCountry("USA");
        supplierDTO.setItems(itemsDTO);

        suppliersDTO.add(supplierDTO);

        Mockito.when(supplierRepository.findAll()).thenReturn(suppliers);
        Mockito.doReturn(suppliersDTO).when(supplierService).convertIterable2DTO(suppliers);

        Iterable<SupplierDTO> allSuppliers = supplierService.getAllSuppliers();
        SupplierDTO actualSupplier = allSuppliers.iterator().next();
        Assert.assertEquals(new Long(1L), actualSupplier.getId());
        Assert.assertEquals(1, actualSupplier.getItems().size());
        Mockito.verify(supplierService, Mockito.times(1)).getAllSuppliers();
    }

    @Test
    public void getSupplierByExistingName() {
        String name = "Supplier Test";
        Item itemFromDB = new Item();
        itemFromDB.setId(1L);
        itemFromDB.setCode(1L);

        ItemDTO itemDTO = new ItemDTO();
        itemDTO.setId(itemFromDB.getId());
        itemDTO.setCode(itemFromDB.getCode());

        Set<Item> items = new HashSet<>();
        items.add(itemFromDB);

        Set<ItemDTO> itemsDTO = new HashSet<>();
        itemsDTO.add(itemDTO);

        Supplier supplierFromDB = new Supplier();
        supplierFromDB.setId(1L);
        supplierFromDB.setItems(items);
        supplierFromDB.setName(name);

        SupplierDTO supplierFromDBDTO = new SupplierDTO();
        supplierFromDBDTO.setId(1L);
        supplierFromDBDTO.setItems(itemsDTO);
        supplierFromDBDTO.setName(name);

        Mockito.when(supplierRepository.findByName(name)).thenReturn(Optional.of(supplierFromDB));
        Mockito.doReturn(supplierFromDBDTO).when(supplierService).convert2DTO(supplierFromDB);

        SupplierDTO actualSupplier = supplierService.getSupplierByName(name);

        Assert.assertEquals(name, actualSupplier.getName());
        Assert.assertEquals(1,  actualSupplier.getItems().size());
        Mockito.verify(supplierService, Mockito.times(1)).getSupplierByName(name);
    }

    @Test
    public void getSupplierByNoExistingName() {
        String name = "Supplier Test";
        Item itemFromDB = new Item();
        itemFromDB.setId(1L);
        itemFromDB.setCode(1L);

        ItemDTO itemDTO = new ItemDTO();
        itemDTO.setId(itemFromDB.getId());
        itemDTO.setCode(itemFromDB.getCode());

        Set<Item> items = new HashSet<>();
        items.add(itemFromDB);

        Set<ItemDTO> itemsDTO = new HashSet<>();
        itemsDTO.add(itemDTO);

        Supplier supplierFromDB = new Supplier();
        supplierFromDB.setId(2L);
        supplierFromDB.setItems(items);
        supplierFromDB.setName(name);

        SupplierDTO supplierFromDBDTO = new SupplierDTO();
        supplierFromDBDTO.setId(1L);
        supplierFromDBDTO.setItems(itemsDTO);
        supplierFromDBDTO.setName(name);

        Mockito.when(supplierRepository.findByName(name)).thenReturn(Optional.empty());

        Exception exception = Assert.assertThrows(ResponseStatusException.class, () -> this.supplierService.getSupplierByName(name));

        String expectedMessage = String.format("%s \"The supplier '%s' does not exist\"",
                HttpStatus.NOT_FOUND.toString(), name);

        String actualMessage = exception.getMessage();

        Assert.assertEquals(expectedMessage, actualMessage);
        Mockito.verify(supplierService, Mockito.times(1)).getSupplierByName(name);
    }

    @Test
    public void addNewSupplier() {
        UserDTO user = new UserDTO();
        user.setUsername("user");
        user.setPassword("hashed-password-goes-here");
        user.setItems(new HashSet<>());

        ItemDTO itemToAdd = new ItemDTO();
        itemToAdd.setCode(1L);
        itemToAdd.setPrice(12.5);
        itemToAdd.setCreator(user);

        user.getItems().add(itemToAdd);

        SupplierDTO supplierDTO = new SupplierDTO();
        supplierDTO.setName("Supplier Test");

        Set<ItemDTO> items = new HashSet<>();
        items.add(itemToAdd);

        supplierDTO.setItems(items);

        User actualUser = new User(1L, "user", "hashed-password-goes-here", new HashSet<>());

        Item actualItem = new Item(1L, 1L, null, 12.5,
                ItemStateEnum.ACTIVE, null, null, LocalDateTime.now(), actualUser);

        actualUser.getItems().add(actualItem);

        Supplier actualSupplier = new Supplier();
        supplierDTO.setName("Supplier Test");

        Set<Item> actualItems = new HashSet<>();
        actualItems.add(actualItem);

        actualSupplier.setItems(actualItems);

        Mockito.when(supplierRepository.findByName("Supplier Test")).thenReturn(Optional.empty());
        Mockito.doReturn(actualSupplier).when(supplierService).convert2Entity(supplierDTO);
        Mockito.when(itemRepository.findByCode(1L)).thenReturn(Optional.of(actualItem));

        supplierService.saveSupplier(supplierDTO);

        Mockito.verify(supplierService, Mockito.times(1)).saveSupplier(supplierDTO);
    }

    @Test
    public void updateExistingSupplier() {
        SupplierDTO supplierToUpdate = new SupplierDTO();
        supplierToUpdate.setName("Supplier Test");

        Supplier actualSupplier = new Supplier();
        actualSupplier.setName("Supplier Test");

        Mockito.when(supplierRepository.findByName("Supplier Test")).thenReturn(Optional.of(actualSupplier));

        supplierService.updateSupplierWithName(supplierToUpdate, "Supplier Test");

        Mockito.verify(supplierService, Mockito.times(1))
                .updateSupplierWithName(supplierToUpdate, "Supplier Test");
    }

    @Test
    public void updateNoExistingSupplier() {
        SupplierDTO supplierToUpdate = new SupplierDTO();
        supplierToUpdate.setName("Supplier Test");


        Mockito.when(supplierRepository.findByName("Supplier Test")).thenReturn(Optional.empty());

        Exception exception = Assert.assertThrows(ResponseStatusException.class,
                () -> this.supplierService.updateSupplierWithName(supplierToUpdate, "Supplier Test"));

        String expectedMessage = String.format("%s \"The supplier '%s' does not exist\"",
                HttpStatus.NOT_FOUND.toString(), supplierToUpdate.getName());

        String actualMessage = exception.getMessage();

        Assert.assertEquals(expectedMessage, actualMessage);

        Mockito.verify(supplierService, Mockito.times(1))
                .updateSupplierWithName(supplierToUpdate, "Supplier Test");
    }
}
