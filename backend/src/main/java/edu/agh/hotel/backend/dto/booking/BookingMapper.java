package edu.agh.hotel.backend.dto.booking;

import edu.agh.hotel.backend.domain.Booking;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface BookingMapper {
    Booking toEntity(BookingCreateRequest dto);

    void updateEntityFromDto(BookingUpdateRequest dto, @MappingTarget Booking entity);
}








