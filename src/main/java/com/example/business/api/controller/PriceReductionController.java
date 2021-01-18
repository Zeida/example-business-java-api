package com.example.business.api.controller;

import com.example.business.api.dto.PriceReductionDTO;
import com.example.business.api.service.PriceReductionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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
    public PriceReductionDTO getPriceReductionFromCode(@PathVariable String code) {
        try {
            Long parsedCode = Long.parseLong(code);
            return priceReductionService.getPriceReductionFromCode(parsedCode);
        } catch(NumberFormatException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "The price reduction code provided must be numerical-only", e);
        }
    }

    @PutMapping(path = "/price-reductions/{code}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void updatePriceReductionByCode(@PathVariable Long code, @RequestBody PriceReductionDTO priceReduction) throws ChangeSetPersister.NotFoundException {
        priceReductionService.updatePriceReductionWithCode(priceReduction, code);
    }
}
