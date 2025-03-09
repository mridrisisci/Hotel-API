package app.dtos;

import app.entities.Hotel;
import app.entities.Room;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HotelDTO
{

    private Integer id;
    private String name;
    private String address;
    private Set<RoomDTO> rooms;

    public HotelDTO(Hotel hotel)
    {
        this.name = hotel.getName();
        this.address = hotel.getAddress();
        this.rooms = hotel.getRooms().stream()
            .map(RoomDTO::new)
            .collect(Collectors.toSet());
    }

}
