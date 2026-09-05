package com.example.cardservice.application.helpers;

import com.example.cardservice.application.api.dto.CardApplicationResponse;
import com.example.cardservice.application.db.entities.CardApplicationEntity;
import com.example.cardservice.application.domain.CardApplication;
import org.springframework.stereotype.Component;

@Component
public class CardApplicationMapper {

    public CardApplication toDomain(CardApplicationEntity application) {
        return new CardApplication(
                application.getId(),
                application.getCustomerId(),
                application.getStatus(),
                application.getCreatedAt()
        );
    }

    public CardApplicationEntity toEntity(CardApplication application) {
        return new CardApplicationEntity(
                application.getId(),
                application.getCustomerId(),
                application.getStatus(),
                application.getCreatedAt()
        );
    }

    public CardApplicationResponse toDTO(CardApplication application) {
        return new CardApplicationResponse(
                application.getId(),
                application.getCustomerId(),
                application.getStatus()
        );
    }
}
