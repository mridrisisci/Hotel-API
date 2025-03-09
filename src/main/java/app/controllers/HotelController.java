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
            HotelDTO hotelDTO = ctx.bodyAsClass(HotelDTO.class);
            Hotel hotel = new Hotel(hotelDTO);
            ctx.json(genericDAO.create(hotelDTO));
        } catch (Exception e)
        {
            logger.error("unable to persist hotel to db", e);
            ErrorMessage error = new ErrorMessage("unable to persist the hotel to db");
            ctx.status(404).json(e);
        }
    }

    public void getAll(Context ctx)
    {
        try
        {
            logger.info("fetching hotels...", ctx.path());
            List<Hotel> hotels = genericDAO.findAll(Hotel.class);
            /*
            List<HotelDTO> hotelDTOs = hotels.stream()
                .map(HotelDTO::new)
                .toList();*/
        } catch (Exception e)
        {
            logger.error("unable to retrieve all hotels", e);
            ErrorMessage error = new ErrorMessage("unable to find all the hotels");
            ctx.status(404).json(e);
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
                        .toList();
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

    }

    public void delete(Context ctx)
    {

    }


}
