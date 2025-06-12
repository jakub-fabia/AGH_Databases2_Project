package edu.agh.hotel.backend.dto.room;

import edu.agh.hotel.backend.domain.Room;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface RoomMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "hotel", ignore = true)      // set in service by looking up hotelId
    @Mapping(target = "roomType", ignore = true)   // set in service by looking up roomTypeId
    Room toEntity(RoomCreateRequest dto);

    @Mapping(target = "hotel", ignore = true)
    @Mapping(target = "roomType", ignore = true)
    void updateEntityFromDto(RoomUpdateRequest dto, @MappingTarget Room entity);
}
