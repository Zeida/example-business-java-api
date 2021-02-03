package com.example.business.api.controller;

import com.example.business.api.dto.DeactivationReasonDTO;
import com.example.business.api.service.DeactivationReasonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.WebAsyncTask;

@RestController
public class DeactivationReasonController {
    @Autowired
    private DeactivationReasonService deactivationReasonService;

    @GetMapping(path = "/deactivations")
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public WebAsyncTask<Iterable<DeactivationReasonDTO>> allDeactivations() {
        return new WebAsyncTask<>(() -> deactivationReasonService.findAllDeactivationReasons());
    }
}
