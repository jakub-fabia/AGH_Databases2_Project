package edu.agh.hotel.backend.dto.hotel;

import edu.agh.hotel.backend.domain.Hotel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface HotelMapper {

    @Mapping(target = "id",            ignore = true)
    @Mapping(target = "reviewSum",     constant = "0")
    @Mapping(target = "reviewCount",   constant = "0")
    Hotel toEntity(HotelCreateRequest dto);

    @Mapping(target = "id",          ignore = true)
    @Mapping(target = "reviewSum",   ignore = true)
    @Mapping(target = "reviewCount", ignore = true)
    void updateEntityFromDto(HotelUpdateRequest dto, @MappingTarget Hotel entity);
}