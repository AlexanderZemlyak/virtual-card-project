package com.example.card_service.application.helpers;

import com.example.card_service.application.api.dto.CardApplicationResponse;
import com.example.card_service.application.db.entities.CardApplicationEntity;
import com.example.card_service.application.domain.CardApplication;
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
