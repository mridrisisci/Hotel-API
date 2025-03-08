package app.controllers;

import app.daos.GenericDAO;
import app.dtos.HotelDTO;
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
                for (Room room : hotelDTO.getRooms())
                { // iterates the hashset
                    //hotel.getRooms().add(room);
                    room.setHotel(hotel);
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
            ctx.status(404).json("could not persist object to db");
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
            ctx.status(404).json("hotels not found");
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
            ctx.status(404).json("could not find hotel");
        }
    }

    public void getRooms(Context ctx)
    {

    }

    public void update(Context ctx)
    {

    }

    public void delete(Context ctx)
    {

    }


}
