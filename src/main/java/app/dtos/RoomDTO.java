package app.dtos;

import app.entities.Room;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class RoomDTO
{
    private Integer id;
    private Integer number;
    private Integer price;

    public RoomDTO(Room room)
    {
        this.id = room.getId();
        this.number = room.getNumber();
        this.price = room.getPrice();
    }
}
