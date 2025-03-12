package app.controllers;

import app.daos.GenericDAO;
import app.dtos.ErrorMessage;
import app.dtos.HotelDTO;
import app.dtos.RoomDTO;
import app.entities.Hotel;
import app.entities.Room;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.http.Context;
import jakarta.persistence.EntityManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class HotelController
{
    private GenericDAO genericDAO;
    private Logger logger = LoggerFactory.getLogger(HotelController.class);
    private ObjectMapper objectMapper = new ObjectMapper();
    private static ArrayList<HotelDTO> hotels = new ArrayList<>();

    public HotelController(EntityManagerFactory emf) { genericDAO = GenericDAO.getInstance(emf); }


    public void populateDB()
    {
        try {
            JsonNode node = objectMapper.readTree(new File("src/hotels.json")).get("hotels");
            Set<HotelDTO> hotels = objectMapper.convertValue(node, new TypeReference<Set<HotelDTO>>() {});
            for (HotelDTO hotelDTO : hotels)
            {
                Hotel hotel = new Hotel(hotelDTO);
                genericDAO.create(hotel);
                for (RoomDTO roomDTO : hotelDTO.getRooms())
                { // iterates the hashset
                    Room room = new Room(roomDTO, hotel);
                    genericDAO.create(room);
                }
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void create(Context ctx)
    {
        try
        {
            // deserialize the incoming request
            logger.info("creating hotel");
            HotelDTO incomingHotel = ctx.bodyAsClass(HotelDTO.class);
            logger.info("Deseralized HTTP request ", incomingHotel);

            // convert to entity
            Hotel newHotel = new Hotel(incomingHotel);
            logger.info("Converted DTO to entity ", newHotel);

            // persist new object
            Hotel createdHotel =  genericDAO.create(newHotel);
            logger.info("Created hotel: ", createdHotel);
            ctx.status(200).json("Object has been persisted to DB");
            ctx.json(new HotelDTO(createdHotel));
        } catch (Exception e)
        {
            logger.error("unable to persist hotel to db", e);
            ErrorMessage error = new ErrorMessage("unable to persist the hotel to db");
            ctx.status(404).json(e);
        }
    }

    public void getHotels(Context ctx)
    {
        try
        {
            // fetch entities
            List<Hotel> hotels = genericDAO.findAll(Hotel.class);
            // convert to DTOs
            List<HotelDTO> hotelDTOS = hotels.stream()
                .map(HotelDTO::new)
                .collect(Collectors.toList());
            ctx.status(200).json(hotelDTOS);
            logger.info("Hotels have been feetched");
        } catch (Exception e)
        {
            logger.info("unable to fetch hotels");
            ErrorMessage error = new ErrorMessage("unable to fetch hotels");
            ctx.status(404).json(error);
        }
    }

    public void getById(Context ctx)
    {
        try
        {
            Long id = Long.parseLong(ctx.pathParam("id"));
            Hotel foundHotel = new Hotel(genericDAO.read(Hotel.class, id));

            if (foundHotel == null)
            {
                ctx.status(404).json("Could not find hotel");
                return;
            }

            HotelDTO hotelDTO = new HotelDTO(foundHotel);
            ctx.json(hotelDTO);
        } catch (Exception e)
        {
            logger.error("unable to find the hotel", e);
            ErrorMessage error = new ErrorMessage("unable to find hotel");
            ctx.status(404).json(error);
        }
    }

    public void getRooms(Context ctx)
    {
        try
        {
            Long hotelId = Long.parseLong(ctx.pathParam("id"));

            Hotel hotel = genericDAO.read(Hotel.class, hotelId);
            if (hotel == null)
            {
                ctx.status(404).json(new ErrorMessage("Could not find hotel"));
                return;
            }

            List<RoomDTO> roomDTOS = hotel.getRooms().stream()
                    .map(RoomDTO::new)
                        .collect(Collectors.toList());
            ctx.json(roomDTOS);
        } catch (Exception e)
        {
            logger.error("Error displaying the hotels", e);
            ErrorMessage error = new ErrorMessage("error getting hotels");
            ctx.status(404).json(error);
        }

    }

    public void update(Context ctx)
    {

        try
        {
            int id = Integer.parseInt(ctx.pathParam("id"));

            // grab http request
            HotelDTO incomingHotel = ctx.bodyAsClass(HotelDTO.class);

            // fetch existing hotel
            Hotel existingHotel = genericDAO.read(Hotel.class, (long) id);

            if(existingHotel == null)
            {
                ctx.status(404).json("could not fetch hotel");
                return;
            }
            // update params
            existingHotel.setId(incomingHotel.getId());
            existingHotel.setName(incomingHotel.getName());
            existingHotel.setAddress(incomingHotel.getAddress());
            existingHotel.setRooms(incomingHotel.getRooms().stream()
                .map(room -> new Room(room, existingHotel))
                .collect(Collectors.toSet()));

            genericDAO.update(existingHotel);
            ctx.status(200).json(new HotelDTO(existingHotel));
        } catch (Exception e)
        {
            logger.error("error updating hotel", e);
            ErrorMessage error = new ErrorMessage("error updating hotel");
            ctx.status(400).json(error);
        }

    }

    public void delete(Context ctx)
    {

        try
        {
            int id = Integer.parseInt(ctx.pathParam("id"));

            // fetch hotel
            Hotel fetchHotel = genericDAO.read(Hotel.class, (long) id);
            if (!fetchHotel.getId().equals(id))
            {
                ctx.status(404).json("Could not find and/or delete hotel");
                return;
            }
            genericDAO.delete(Hotel.class, (long) id);
            // convert to dto
            HotelDTO deletedHotel = new HotelDTO(fetchHotel);

            logger.info("deleted hotel", deletedHotel);
            ctx.status(200).json("hotel deleted");
            ctx.json(deletedHotel);

        } catch (Exception e)
        {
            logger.error("error deleting hotel", e);
            ErrorMessage error = new ErrorMessage("error deleting hotel");
            ctx.status(400).json(error);
        }


    }


}
