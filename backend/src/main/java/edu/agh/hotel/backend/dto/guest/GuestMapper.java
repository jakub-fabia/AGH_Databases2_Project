package edu.agh.hotel.backend.dto.guest;

import edu.agh.hotel.backend.domain.Guest;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface GuestMapper {
    Guest toEntity(GuestCreateRequest dto);

    void updateEntityFromDto(GuestUpdateRequest dto, @MappingTarget Guest entity);

    GuestSummary toSummary(Guest guest);
}