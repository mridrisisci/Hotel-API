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
                    //hotel.getRooms().add(room);
                    Room room = new Room(roomDTO, hotel);
                    genericDAO.create(room);
                }
                //HotelContainerDTO[] hotelS = objectMapper.readValue(new File("src/hotels.json"), HotelContainerDTO[].class);
                //hotels.forEach(System.out::println);
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
            logger.info("testing123", ctx.path());
            ctx.json(genericDAO.findAll(Hotel.class));
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
