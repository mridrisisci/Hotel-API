package app.dtos;

import app.entities.Hotel;
import app.entities.Room;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HotelDTO
{

    private Integer id;
    private String name;
    private String address;
    private Set<Room> rooms;

    public HotelDTO(String name, String address, Set<Room> rooms)
    {
        this.name = name;
        this.address = address;
        this.rooms = rooms;
    }

    public HotelDTO(Hotel hotel)
    {
        this.id = hotel.getId();
        this.name = hotel.getName();
        this.address = hotel.getAddress();
        this.rooms = hotel.getRooms();
    }
}
