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

}
