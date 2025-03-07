package app.entities;

import app.dtos.RoomDTO;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.NaturalId;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "rooms")
public class Room
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer number;
    private Integer price;

    @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
    @ToString.Exclude
    @JoinColumn(name = "hotel_id")
    private Hotel hotel;

    public Room(RoomDTO roomDTO)
    {
        this.id = roomDTO.getId();
        this.number = roomDTO.getNumber();
        this.price = roomDTO.getPrice();
    }

}
