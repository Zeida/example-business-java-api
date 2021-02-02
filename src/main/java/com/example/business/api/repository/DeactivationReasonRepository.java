package com.example.business.api.repository;

import com.example.business.api.model.DeactivationReason;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeactivationReasonRepository extends CrudRepository<DeactivationReason, Long> {
}
