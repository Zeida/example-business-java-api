package com.example.business.api.controller;

import com.example.business.api.dto.PriceReductionDTO;
import com.example.business.api.service.PriceReductionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.WebAsyncTask;

@RestController
public class PriceReductionController {
    @Autowired
    private PriceReductionService priceReductionService;

    @GetMapping(path = "/price-reductions")
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_USER') OR hasRole('ROLE_ADMIN')")
    public WebAsyncTask<Iterable<PriceReductionDTO>> allPriceReductions() {
        return new WebAsyncTask<>(() -> priceReductionService.getAllPriceReductions());
    }

    @PostMapping(path = "/price-reductions", consumes = "application/json")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('USER') OR hasRole('ADMIN')")
    public WebAsyncTask<Void> addPriceReduction(@RequestBody PriceReductionDTO priceReduction) {
        return new WebAsyncTask<>(() -> priceReductionService.savePriceReduction(priceReduction));
    }

    @GetMapping(path = "/price-reductions/{code}")
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_USER') OR hasRole('ROLE_ADMIN')")
    public WebAsyncTask<PriceReductionDTO> getPriceReductionFromCode(@PathVariable Long code) {
        return new WebAsyncTask<>(() -> priceReductionService.getPriceReductionFromCode(code));
    }

    @PutMapping(path = "/price-reductions/{code}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ROLE_USER') OR hasRole('ROLE_ADMIN')")
    public WebAsyncTask<Void> updatePriceReductionByCode(@PathVariable Long code, @RequestBody PriceReductionDTO priceReduction) {
        return new WebAsyncTask<>(() -> priceReductionService.updatePriceReductionWithCode(priceReduction, code));
    }
}
