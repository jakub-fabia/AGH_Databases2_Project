package edu.agh.hotel.backend.dto.hotel;

import edu.agh.hotel.backend.domain.Hotel;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface HotelMapper {
    Hotel toEntity(HotelCreateRequest dto);

    void updateEntityFromDto(HotelUpdateRequest dto, @MappingTarget Hotel entity);
}