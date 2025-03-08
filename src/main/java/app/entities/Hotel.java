package app.entities;

import app.dtos.HotelDTO;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "hotels")
public class Hotel
{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String address;


    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @OneToMany(mappedBy = "hotel", orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonManagedReference
    private Set<Room> rooms = new HashSet<>();

    public Hotel(HotelDTO hotelDTO)
    {
        this.name = hotelDTO.getName();
        this.address = hotelDTO.getAddress();
        this.rooms = hotelDTO.getRooms().stream()
            .map(room -> new Room(room, this))
            .collect(Collectors.toSet());
    }

    public Hotel(Hotel hotel)
    {
        this.id = hotel.getId();
        this.name = hotel.getName();
        this.address = hotel.getAddress();
    }

    public void addRoom(Room room)
    {
        rooms.add(room);
    }
}
