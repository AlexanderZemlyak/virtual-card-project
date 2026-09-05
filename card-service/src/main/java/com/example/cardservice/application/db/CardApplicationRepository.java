package com.example.cardservice.application.db;

import com.example.cardservice.application.db.entities.CardApplicationEntity;
import com.example.cardservice.application.enums.ApplicationStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CardApplicationRepository extends JpaRepository<CardApplicationEntity, UUID> {
    List<CardApplicationEntity> findAllByCustomerId(UUID customerId);
    List<CardApplicationEntity> findAllByStatus(ApplicationStatus status);
}
