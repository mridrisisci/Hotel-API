package app.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class RoomDTO
{
    private Integer id;
    private Integer hotelId;
    private Integer number;
    private Integer price;

    public RoomDTO(Integer hotelId, Integer number, Integer price)
    {
        this.hotelId = hotelId;
        this.number = number;
        this.price = price;
    }


}
