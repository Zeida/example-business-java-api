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

import java.time.LocalDateTime;
import java.util.*;

@RunWith(MockitoJUnitRunner.class)
public class PriceReductionServiceTest {
    @Spy
    @InjectMocks
    private PriceReductionServiceImpl priceReductionService;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private PriceReductionRepository priceReductionRepository;

    @Mock
    private ItemRepository itemRepository;

    private static ItemDTO testItemDTO;
    private static Item testItem;

    private static PriceReductionDTO testPriceReductionDTO;
    private static PriceReduction testPriceReduction;

    private static final Long ID = 1L;
    private static final Long CODE = 1L;

    private static final Double PRICE = 12.5;
    private static final String DESC = "Description";

    private static final Double AMOUNT_DEDUCTED = 10.5;
    private static final LocalDateTime END_DATE = LocalDateTime.now().plusMonths(6);

    @BeforeClass
    public static void setupTest() {
        testItemDTO = new ItemDTO();
        testItemDTO.setId(ID);
        testItemDTO.setCode(CODE);
        testItemDTO.setPrice(PRICE);
        testItemDTO.setDescription(DESC);

        testItem = new Item();
        testItem.setId(ID);
        testItem.setCode(CODE);
        testItem.setPrice(PRICE);
        testItem.setDescription(DESC);

        List<PriceReduction> priceReductions = new ArrayList<>();
        List<PriceReductionDTO> priceReductionDTOS = new ArrayList<>();

        testPriceReductionDTO = new PriceReductionDTO();
        testPriceReductionDTO.setId(ID);
        testPriceReductionDTO.setCode(CODE);
        testPriceReductionDTO.setAmountDeducted(AMOUNT_DEDUCTED);
        testPriceReductionDTO.setEndDate(END_DATE);
        testPriceReductionDTO.setItem(testItemDTO);

        testPriceReduction = new PriceReduction();
        testPriceReduction.setId(ID);
        testPriceReduction.setCode(CODE);
        testPriceReduction.setAmountDeducted(AMOUNT_DEDUCTED);
        testPriceReduction.setEndDate(END_DATE);
        testPriceReduction.setItem(testItem);

        priceReductions.add(testPriceReduction);
        priceReductionDTOS.add(testPriceReductionDTO);

        testItem.setPriceReductions(priceReductions);
        testItemDTO.setPriceReductions(priceReductionDTOS);
    }

    @Test
    public void findAllPriceReductionsWithOnePriceReduction() {
        Set<PriceReduction> priceReductions = new HashSet<>();
        Set<PriceReductionDTO> priceReductionDTOS = new HashSet<>();

        priceReductions.add(testPriceReduction);
        priceReductionDTOS.add(testPriceReductionDTO);

        Mockito.when(priceReductionRepository.findAll()).thenReturn(priceReductions);
        Mockito.doReturn(priceReductionDTOS).when(priceReductionService).convertIterable2DTO(priceReductions);

        Iterable<PriceReductionDTO> allPriceReductions = priceReductionService.getAllPriceReductions();
        PriceReductionDTO actualPriceReduction = allPriceReductions.iterator().next();
        Assert.assertEquals(CODE, actualPriceReduction.getCode());
        Mockito.verify(priceReductionService, Mockito.times(1)).getAllPriceReductions();
    }

    @Test
    public void getPriceReductionByExistingCode() {
        Mockito.when(priceReductionRepository.findByCode(CODE)).thenReturn(Optional.of(testPriceReduction));
        Mockito.doReturn(testPriceReductionDTO).when(priceReductionService).convert2DTO(testPriceReduction);

        PriceReductionDTO actualPriceReduction = priceReductionService.getPriceReductionFromCode(CODE);

        Assert.assertEquals(CODE, actualPriceReduction.getCode());
        Mockito.verify(priceReductionService, Mockito.times(1)).getPriceReductionFromCode(CODE);
    }

    @Test
    public void getPriceReductionByNoExistingCode() {
        Long code = 2L;

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
    public void addNewPriceReduction() {
        testItemDTO.setPriceReductions(null);
        testItem.setPriceReductions(null);

        Mockito.when(priceReductionRepository.findByCode(CODE)).thenReturn(Optional.empty());
        Mockito.when(itemRepository.findByCode(CODE)).thenReturn(Optional.of(testItem));

        priceReductionService.savePriceReduction(testPriceReductionDTO);

        Mockito.verify(priceReductionService,
                Mockito.times(1))
                .savePriceReduction(testPriceReductionDTO);

        testItemDTO.addPriceReduction(testPriceReductionDTO);
        testItem.addPriceReduction(testPriceReduction);
    }

    @Test
    public void addNewPriceReductionWithMissingAmountDeducted() {
        testPriceReductionDTO.setAmountDeducted(null);

        Exception exception = Assert.assertThrows(ResponseStatusException.class,
                () -> this.priceReductionService.savePriceReduction(testPriceReductionDTO));

        String expectedMessage = String.format("%s \"A price reduction must have the amount deducted > 0\"",
                HttpStatus.BAD_REQUEST.toString());

        String actualMessage = exception.getMessage();

        Assert.assertEquals(expectedMessage, actualMessage);

        Mockito.verify(priceReductionService,
                Mockito.times(1))
                .savePriceReduction(testPriceReductionDTO);

        testPriceReductionDTO.setAmountDeducted(AMOUNT_DEDUCTED);
    }

    @Test
    public void addNewPriceReductionWithMissingItem() {
        testPriceReductionDTO.setItem(null);

        Exception exception = Assert.assertThrows(ResponseStatusException.class,
                () -> this.priceReductionService.savePriceReduction(testPriceReductionDTO));

        String expectedMessage = String.format("%s \"A price reduction must me applied to an item.\"",
                HttpStatus.BAD_REQUEST.toString());

        String actualMessage = exception.getMessage();

        Assert.assertEquals(expectedMessage, actualMessage);

        Mockito.verify(priceReductionService,
                Mockito.times(1))
                .savePriceReduction(testPriceReductionDTO);

        testPriceReductionDTO.setItem(testItemDTO);
    }

    @Test
    public void addNewPriceReductionWithMissingEndDate() {
        testPriceReductionDTO.setEndDate(null);

        Exception exception = Assert.assertThrows(ResponseStatusException.class,
                () -> this.priceReductionService.savePriceReduction(testPriceReductionDTO));

        String expectedMessage = String.format("%s \"A price reduction must have an end date.\"",
                HttpStatus.BAD_REQUEST.toString());

        String actualMessage = exception.getMessage();

        Assert.assertEquals(expectedMessage, actualMessage);

        Mockito.verify(priceReductionService,
                Mockito.times(1))
                .savePriceReduction(testPriceReductionDTO);

        testPriceReductionDTO.setEndDate(END_DATE);
    }

    @Test
    public void addNewPriceReductionButStarDateIsAfterEndDate() {
        testPriceReductionDTO.setStartDate(END_DATE.plusMonths(1));

        Exception exception = Assert.assertThrows(ResponseStatusException.class,
                () -> this.priceReductionService.savePriceReduction(testPriceReductionDTO));

        String expectedMessage = String.format("%s \"End date should be a date after start date\"",
                HttpStatus.BAD_REQUEST.toString());

        String actualMessage = exception.getMessage();

        Assert.assertEquals(expectedMessage, actualMessage);

        Mockito.verify(priceReductionService,
                Mockito.times(1))
                .savePriceReduction(testPriceReductionDTO);

        testPriceReductionDTO.setStartDate(null);
    }

    @Test
    public void updateExistingPriceReduction() {
        testItemDTO.setPriceReductions(null);
        testItem.setPriceReductions(null);

        Mockito.when(priceReductionRepository.findByCode(CODE)).thenReturn(Optional.of(testPriceReduction));
        Mockito.when(itemRepository.findByCode(CODE)).thenReturn(Optional.of(testItem));

        priceReductionService.updatePriceReductionWithCode(testPriceReductionDTO, CODE);

        Mockito.verify(priceReductionService, Mockito.times(1))
                .updatePriceReductionWithCode(testPriceReductionDTO, CODE);

        testItemDTO.addPriceReduction(testPriceReductionDTO);
        testItem.addPriceReduction(testPriceReduction);
    }

    @Test
    public void updateNoExistingPriceReduction() {
        Long code = 2L;
        testPriceReductionDTO.setCode(code);

        Mockito.when(priceReductionRepository.findByCode(code)).thenReturn(Optional.empty());

        Exception exception = Assert.assertThrows(ResponseStatusException.class,
                () -> this.priceReductionService.updatePriceReductionWithCode(testPriceReductionDTO, code));

        String expectedMessage = String.format("%s \"The price reduction '%s' does not exist\"",
                HttpStatus.NOT_FOUND.toString(), testPriceReductionDTO.getCode());

        String actualMessage = exception.getMessage();

        Assert.assertEquals(expectedMessage, actualMessage);

        Mockito.verify(priceReductionService, Mockito.times(1))
                .updatePriceReductionWithCode(testPriceReductionDTO, code);

        testPriceReductionDTO.setCode(CODE);
    }
}
