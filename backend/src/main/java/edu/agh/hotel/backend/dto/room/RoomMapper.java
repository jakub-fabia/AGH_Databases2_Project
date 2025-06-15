package edu.agh.hotel.backend.dto.room;

import edu.agh.hotel.backend.domain.Room;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface RoomMapper {
    Room toEntity(RoomCreateRequest dto);

    void updateEntityFromDto(RoomUpdateRequest dto, @MappingTarget Room entity);
}
