package edu.agh.hotel.backend.dto.booking;

import edu.agh.hotel.backend.domain.Booking;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface BookingMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "guest", ignore = true)
    @Mapping(target = "hotel", ignore = true)
    @Mapping(target = "bookingRooms", ignore = true)
    Booking toEntity(BookingCreateRequest dto);


    @Mapping(target = "bookingRooms", ignore = true)
    void updateEntityFromDto(BookingUpdateRequest dto, @MappingTarget Booking entity);
}








