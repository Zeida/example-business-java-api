package com.example.business.api.controller;

import com.example.business.api.dto.ItemDTO;
import com.example.business.api.dto.PriceReductionDTO;
import com.example.business.api.dto.SupplierDTO;
import com.example.business.api.model.Item;
import com.example.business.api.model.PriceReduction;
import com.example.business.api.service.PriceReductionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
public class PriceReductionController {
    @Autowired
    private PriceReductionService priceReductionService;

    @GetMapping(path = "/price-reductions")
    @ResponseBody
    public Iterable<PriceReductionDTO> allPriceReductions() {
        return priceReductionService.getAllPriceReductions();
    }

    @PostMapping(path = "/price-reductions", consumes = "application/json")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void addPriceReduction(@RequestBody PriceReductionDTO priceReduction) {
        priceReductionService.savePriceReduction(priceReduction);
    }

    @GetMapping(path = "/price-reductions/{code}")
    @ResponseBody
    public PriceReductionDTO getPriceReductionFromCode(@PathVariable Long code) {
        return priceReductionService.getPriceReductionFromCode(code);
    }

    @PutMapping(path = "/price-reductions/{code}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void updatePriceReductionByCode(@PathVariable Long code, @RequestBody PriceReductionDTO priceReduction) {

    }
}
