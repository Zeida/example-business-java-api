package com.example.business.api.unittest;

import com.example.business.api.dto.SupplierDTO;
import com.example.business.api.model.Supplier;
import com.example.business.api.repository.SupplierRepository;
import com.example.business.api.service.SupplierServiceImpl;
import org.junit.Assert;
import org.junit.BeforeClass;
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

    private static SupplierDTO testSupplierDTO;
    private static Supplier testSupplier;

    private static final Long ID = 1L;
    private static final String NAME = "Supplier S.L";

    @BeforeClass
    public static void setupTest() {
        testSupplierDTO = new SupplierDTO();
        testSupplierDTO.setId(ID);
        testSupplierDTO.setName(NAME);

        testSupplier = new Supplier();
        testSupplier.setId(ID);
        testSupplier.setName(NAME);
    }

    @Test
    public void findAllSuppliers() {
        Set<Supplier> suppliers = new HashSet<>();
        Set<SupplierDTO> suppliersDTO = new HashSet<>();

        suppliers.add(testSupplier);
        suppliersDTO.add(testSupplierDTO);

        Mockito.when(supplierRepository.findAll()).thenReturn(suppliers);
        Mockito.doReturn(suppliersDTO).when(supplierService).convertIterable2DTO(suppliers);

        Iterable<SupplierDTO> allSuppliers = supplierService.getAllSuppliers();
        SupplierDTO actualSupplier = allSuppliers.iterator().next();
        Assert.assertEquals(NAME, actualSupplier.getName());
        Mockito.verify(supplierService, Mockito.times(1)).getAllSuppliers();
    }

    @Test
    public void getSupplierByExistingName() {
        Mockito.when(supplierRepository.findByName(NAME)).thenReturn(Optional.of(testSupplier));
        Mockito.doReturn(testSupplierDTO).when(supplierService).convert2DTO(testSupplier);

        SupplierDTO actualSupplier = supplierService.getSupplierByName(NAME);

        Assert.assertEquals(NAME, actualSupplier.getName());
        Mockito.verify(supplierService, Mockito.times(1)).getSupplierByName(NAME);
    }

    @Test
    public void getSupplierByNoExistingName() {
        Mockito.when(supplierRepository.findByName(NAME)).thenReturn(Optional.empty());

        Exception exception = Assert.assertThrows(ResponseStatusException.class,
                () -> this.supplierService.getSupplierByName(NAME));

        String expectedMessage = String.format("%s \"The supplier '%s' does not exist\"",
                HttpStatus.NOT_FOUND.toString(), NAME);

        String actualMessage = exception.getMessage();

        Assert.assertEquals(expectedMessage, actualMessage);
        Mockito.verify(supplierService, Mockito.times(1)).getSupplierByName(NAME);
    }

    @Test
    public void addNewSupplier() {
        Mockito.when(supplierRepository.findByName(NAME)).thenReturn(Optional.empty());

        supplierService.saveSupplier(testSupplierDTO);

        Mockito.verify(supplierService, Mockito.times(1)).saveSupplier(testSupplierDTO);
    }

    @Test
    public void addNewSupplierWithMissingName() {
        testSupplierDTO.setName(null);

        Exception exception = Assert.assertThrows(ResponseStatusException.class,
                () -> this.supplierService.saveSupplier(testSupplierDTO));

        String expectedMessage = String.format("%s \"A supplier must have a non-empty name\"",
                HttpStatus.BAD_REQUEST.toString());

        String actualMessage = exception.getMessage();

        Assert.assertEquals(expectedMessage, actualMessage);

        Mockito.verify(supplierService, Mockito.times(1)).saveSupplier(testSupplierDTO);

        testSupplierDTO.setName(NAME);
    }

    @Test
    public void updateExistingSupplier() {
        Mockito.when(supplierRepository.findByName(NAME)).thenReturn(Optional.of(testSupplier));

        supplierService.updateSupplierWithName(testSupplierDTO, NAME);

        Mockito.verify(supplierService, Mockito.times(1))
                .updateSupplierWithName(testSupplierDTO, NAME);
    }

    @Test
    public void updateNoExistingSupplier() {
        Mockito.when(supplierRepository.findByName(NAME)).thenReturn(Optional.empty());

        Exception exception = Assert.assertThrows(ResponseStatusException.class,
                () -> this.supplierService.updateSupplierWithName(testSupplierDTO, NAME));

        String expectedMessage = String.format("%s \"The supplier '%s' does not exist\"",
                HttpStatus.NOT_FOUND.toString(), testSupplierDTO.getName());

        String actualMessage = exception.getMessage();

        Assert.assertEquals(expectedMessage, actualMessage);

        Mockito.verify(supplierService, Mockito.times(1))
                .updateSupplierWithName(testSupplierDTO, NAME);
    }
}
