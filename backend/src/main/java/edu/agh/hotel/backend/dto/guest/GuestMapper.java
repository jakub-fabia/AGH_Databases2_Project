package edu.agh.hotel.backend.dto.guest;

import edu.agh.hotel.backend.domain.Guest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface GuestMapper {

    @Mapping(target = "id", ignore = true)
    Guest toEntity(GuestCreateRequest dto);

    @Mapping(target = "id", ignore = true)
    void updateEntityFromDto(GuestUpdateRequest dto, @MappingTarget Guest entity);
}