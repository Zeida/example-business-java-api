package com.example.business.api.unittest;

import com.example.business.api.dto.ItemDTO;
import com.example.business.api.dto.PriceReductionDTO;
import com.example.business.api.dto.UserDTO;
import com.example.business.api.model.Item;
import com.example.business.api.model.PriceReduction;
import com.example.business.api.model.User;
import com.example.business.api.repository.ItemRepository;
import com.example.business.api.repository.PriceReductionRepository;
import com.example.business.api.service.PriceReductionServiceImpl;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@RunWith(MockitoJUnitRunner.class)
public class PriceReductionServiceTest {
    @Spy
    @InjectMocks
    private PriceReductionServiceImpl priceReductionService;

    @Mock
    private PriceReductionRepository priceReductionRepository;

    @Mock
    private ItemRepository itemRepository;

    @Test
    public void findAllPriceReductionsWithOnePriceReduction() {
        Set<PriceReduction> priceReductions = new HashSet<>();
        Set<PriceReductionDTO> priceReductionDTOS = new HashSet<>();

        User user = new User();
        user.setId(1L);

        Item item = new Item();
        item.setId(1L);

        PriceReduction priceReduction = new PriceReduction(1L, 1L, 22.5,
                LocalDateTime.now(),LocalDateTime.now().plusMonths(1), item);

        priceReductions.add(priceReduction);

        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());

        ItemDTO itemDTO = new ItemDTO();
        itemDTO.setId(item.getId());

        PriceReductionDTO priceReductionDTO = new PriceReductionDTO();
        priceReductionDTO.setId(priceReduction.getId());
        priceReductionDTO.setCode(priceReduction.getCode());
        priceReductionDTO.setStartDate(priceReduction.getStartDate());
        priceReductionDTO.setEndDate(priceReduction.getEndDate());
        priceReductionDTO.setAmountDeducted(priceReduction.getAmountDeducted());
        priceReductionDTO.setItem(itemDTO);

        priceReductionDTOS.add(priceReductionDTO);

        Mockito.when(priceReductionRepository.findAll()).thenReturn(priceReductions);
        Mockito.doReturn(priceReductionDTOS).when(priceReductionService).convertIterable2DTO(priceReductions);

        Iterable<PriceReductionDTO> allPriceReductions = priceReductionService.getAllPriceReductions();
        PriceReductionDTO actualPriceReduction = allPriceReductions.iterator().next();
        Assert.assertEquals(new Long(1L), actualPriceReduction.getId());
        Mockito.verify(priceReductionService, Mockito.times(1)).getAllPriceReductions();
    }

    @Test
    public void getPriceReductionByExistingCode() {
        Long code = 1L;
        PriceReduction priceReductionFromDB = new PriceReduction();
        priceReductionFromDB.setId(1L);
        priceReductionFromDB.setCode(1L);

        PriceReductionDTO priceReductionDTO = new PriceReductionDTO();
        priceReductionDTO.setId(priceReductionFromDB.getId());
        priceReductionDTO.setCode(priceReductionFromDB.getCode());

        Mockito.when(priceReductionRepository.findByCode(code)).thenReturn(Optional.of(priceReductionFromDB));
        Mockito.doReturn(priceReductionDTO).when(priceReductionService).convert2DTO(priceReductionFromDB);

        PriceReductionDTO actualPriceReduction = priceReductionService.getPriceReductionFromCode(code);

        Assert.assertEquals(code, actualPriceReduction.getCode());
        Mockito.verify(priceReductionService, Mockito.times(1)).getPriceReductionFromCode(code);
    }

    @Test
    public void getPriceReductionByNoExistingCode() {
        Long code = 1L;
        PriceReduction priceReductionFromDB = new PriceReduction();
        priceReductionFromDB.setId(1L);
        priceReductionFromDB.setCode(1L);

        PriceReductionDTO priceReductionDTO = new PriceReductionDTO();
        priceReductionDTO.setId(priceReductionFromDB.getId());
        priceReductionDTO.setCode(priceReductionFromDB.getCode());

        Mockito.when(priceReductionRepository.findByCode(code)).thenReturn(Optional.empty());

        Exception exception = Assert.assertThrows(ResponseStatusException.class,
                () -> this.priceReductionService.getPriceReductionFromCode(code));

        String expectedMessage = String.format("%s \"The price reduction '%s' does not exist\"",
                HttpStatus.NOT_FOUND.toString(), code);

        String actualMessage = exception.getMessage();

        Assert.assertEquals(expectedMessage, actualMessage);
        Mockito.verify(priceReductionService, Mockito.times(1)).getPriceReductionFromCode(code);
    }

    @Test
    public void addNewSupplier() {
        Long code = 1L;

        Item item = new Item();
        ItemDTO itemDTO = new ItemDTO();
        item.setCode(code);
        itemDTO.setCode(code);

        PriceReduction priceReduction = new PriceReduction();
        priceReduction.setId(1L);
        priceReduction.setCode(1L);
        priceReduction.setAmountDeducted(1.0);
        priceReduction.setItem(item);
        priceReduction.setEndDate(LocalDateTime.now().plusMonths(5));

        PriceReductionDTO priceReductionDTO = new PriceReductionDTO();
        priceReductionDTO.setCode(priceReduction.getCode());
        priceReductionDTO.setAmountDeducted(priceReduction.getAmountDeducted());
        priceReductionDTO.setItem(itemDTO);
        priceReductionDTO.setEndDate(priceReduction.getEndDate());

        Mockito.when(priceReductionRepository.findByCode(code)).thenReturn(Optional.empty());
        Mockito.doReturn(priceReduction).when(priceReductionService).convert2Entity(priceReductionDTO);
        Mockito.when(itemRepository.findByCode(code)).thenReturn(Optional.of(item));

        priceReductionService.savePriceReduction(priceReductionDTO);

        Mockito.verify(priceReductionService,
                Mockito.times(1))
                .savePriceReduction(priceReductionDTO);
    }

    @Test
    public void updateExistingPriceReduction() {
        Long code = 1L;

        Item item = new Item();
        ItemDTO itemDTO = new ItemDTO();
        item.setCode(code);
        itemDTO.setCode(code);

        PriceReduction priceReduction = new PriceReduction();
        priceReduction.setId(1L);
        priceReduction.setCode(1L);
        priceReduction.setAmountDeducted(1.0);
        priceReduction.setItem(item);
        priceReduction.setEndDate(LocalDateTime.now().plusMonths(5));

        PriceReductionDTO priceReductionDTO = new PriceReductionDTO();
        priceReductionDTO.setCode(priceReduction.getCode());
        priceReductionDTO.setAmountDeducted(priceReduction.getAmountDeducted());
        priceReductionDTO.setItem(itemDTO);
        priceReductionDTO.setEndDate(priceReduction.getEndDate());

        Mockito.when(priceReductionRepository.findByCode(code)).thenReturn(Optional.of(priceReduction));
        Mockito.when(itemRepository.findByCode(code)).thenReturn(Optional.of(item));

        priceReductionService.updatePriceReductionWithCode(priceReductionDTO, code);

        Mockito.verify(priceReductionService, Mockito.times(1))
                .updatePriceReductionWithCode(priceReductionDTO, code);
    }

    @Test
    public void updateNoExistingSupplier() {
        Long code = 1L;

        Item item = new Item();
        ItemDTO itemDTO = new ItemDTO();
        item.setCode(code);
        itemDTO.setCode(code);

        PriceReduction priceReduction = new PriceReduction();
        priceReduction.setId(1L);
        priceReduction.setCode(1L);
        priceReduction.setAmountDeducted(1.0);
        priceReduction.setItem(item);
        priceReduction.setEndDate(LocalDateTime.now().plusMonths(5));

        PriceReductionDTO priceReductionDTO = new PriceReductionDTO();
        priceReductionDTO.setCode(priceReduction.getCode());
        priceReductionDTO.setAmountDeducted(priceReduction.getAmountDeducted());
        priceReductionDTO.setItem(itemDTO);
        priceReductionDTO.setEndDate(priceReduction.getEndDate());

        Mockito.when(priceReductionRepository.findByCode(1L)).thenReturn(Optional.empty());

        Exception exception = Assert.assertThrows(ResponseStatusException.class,
                () -> this.priceReductionService.updatePriceReductionWithCode(priceReductionDTO, 1L));

        String expectedMessage = String.format("%s \"The price reduction '%s' does not exist\"",
                HttpStatus.NOT_FOUND.toString(), priceReductionDTO.getCode());

        String actualMessage = exception.getMessage();

        Assert.assertEquals(expectedMessage, actualMessage);

        Mockito.verify(priceReductionService, Mockito.times(1))
                .updatePriceReductionWithCode(priceReductionDTO, 1L);
    }
}
