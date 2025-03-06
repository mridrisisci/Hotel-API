package app.entities;

import app.dtos.RoomDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.NaturalId;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "rooms")
public class Room
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer hotelId;
    private Integer number;
    private Integer price;

    public Room(RoomDTO roomDTO)
    {
        this.id = roomDTO.getId();
        this.hotelId = roomDTO.getHotelId();
        this.number = roomDTO.getNumber();
        this.price = roomDTO.getPrice();
    }

    public Room(Integer hotelId, Integer number, Integer price)
    {
        this.hotelId = hotelId;
        this.number = number;
        this.price = price;
    }
}
