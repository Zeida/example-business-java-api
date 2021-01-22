package com.example.business.api.controller;

import com.example.business.api.dto.PriceReductionDTO;
import com.example.business.api.service.PriceReductionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
public class PriceReductionController {
    @Autowired
    private PriceReductionService priceReductionService;

    @GetMapping(path = "/price-reductions")
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_USER') OR hasRole('ROLE_ADMIN')")
    public Iterable<PriceReductionDTO> allPriceReductions() {
        return priceReductionService.getAllPriceReductions();
    }

    @PostMapping(path = "/price-reductions", consumes = "application/json")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('USER') OR hasRole('ADMIN')")
    public void addPriceReduction(@RequestBody PriceReductionDTO priceReduction) {
        priceReductionService.savePriceReduction(priceReduction);
    }

    @GetMapping(path = "/price-reductions/{code}")
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_USER') OR hasRole('ROLE_ADMIN')")
    public PriceReductionDTO getPriceReductionFromCode(@PathVariable Long code) {
        return priceReductionService.getPriceReductionFromCode(code);
    }

    @PutMapping(path = "/price-reductions/{code}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ROLE_USER') OR hasRole('ROLE_ADMIN')")
    public void updatePriceReductionByCode(@PathVariable Long code, @RequestBody PriceReductionDTO priceReduction) {
        priceReductionService.updatePriceReductionWithCode(priceReduction, code);
    }
}
