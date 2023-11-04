package com.driver.controllers;

import com.driver.model.Booking;
import com.driver.model.Facility;
import com.driver.model.Hotel;
import com.driver.model.User;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class HotelRepository {
    HashMap<String,Hotel> hr=new HashMap<>();
    HashMap<Integer,User> ur=new HashMap<>();
    HashMap<String,Booking> br=new HashMap<>();
    HashMap<Integer,List<Booking>> ub = new HashMap<>();


    public String addHotel(Hotel hotel) {
        //You need to add an hotel to the database
        //incase the hotelName is null or the hotel Object is null return an empty a FAILURE
        //Incase somebody is trying to add the duplicate hotelName return FAILURE
        //in all other cases return SUCCESS after successfully adding the hotel to the hotelDb.
        if(hotel==null || hotel.getHotelName()==null ||hotel.getHotelName().length()==0 || hr.containsKey(hotel.getHotelName()))
            return "FAILURE";
        hr.put(hotel.getHotelName(),hotel);
        return "SUCCESS";
    }

    public Integer addUser(User user) {
        //You need to add a User Object to the database
        //Assume that user will always be a valid user and return the aadharCardNo of the user
        ur.put(user.getaadharCardNo(),user);
        return user.getaadharCardNo();
    }

    public String getHotelWithMostFacilities() {
        //Out of all the hotels we have added so far, we need to find the hotelName with most no of facilities
        //Incase there is a tie return the lexicographically smaller hotelName
        //Incase there is not even a single hotel with atleast 1 facility return "" (empty string)
        String bestHotel = "";
        int facilities = 0;
        for(String hotel:hr.keySet()){
            int curr = hr.get(hotel).getFacilities().size();
            if(curr==0)continue;
            if(curr==facilities && bestHotel.compareTo(hotel)>1){
                bestHotel = hotel;
            }
            if(curr>facilities){
                facilities = curr;
                bestHotel = hotel;
            }
        }
        return bestHotel;
    }

    public int bookARoom(Booking booking) {
        //The booking object coming from postman will have all the attributes except bookingId and amountToBePaid;
        //Have bookingId as a random UUID generated String
        //save the booking Entity and keep the bookingId as a primary key
        //Calculate the total amount paid by the person based on no. of rooms booked and price of the room per night.
        //If there arent enough rooms available in the hotel that we are trying to book return -1
        //in other case return total amount paid
        UUID uuid = UUID.randomUUID();
        booking.setBookingId(uuid.toString());
        br.put(uuid.toString(),booking);
        if(hr.get(booking.getHotelName()).getAvailableRooms()<booking.getNoOfRooms())
            return -1;
        booking.setAmountToBePaid(booking.getNoOfRooms()*hr.get(booking.getHotelName()).getPricePerNight());
        List<Booking> temp = ub.getOrDefault(booking.getBookingAadharCard(),new ArrayList<>());
        temp.add(booking);
        ub.put(booking.getBookingAadharCard(),temp);
        return booking.getAmountToBePaid();

    }

    public int getBookings(Integer aadharCard) {
        //In this function return the bookings done by a person
        if(!ub.containsKey(aadharCard))return 0;
        return ub.get(aadharCard).size();
    }

    public Hotel updateFacilities(List<Facility> newFacilities, String hotelName) {
        //We are having a new facilites that a hotel is planning to bring.
        //If the hotel is already having that facility ignore that facility otherwise add that facility in the hotelDb
        //return the final updated List of facilities and also update that in your hotelDb
        //Note that newFacilities can also have duplicate facilities possible
        Hotel hotel = hr.get(hotelName);
        List<Facility> c = hotel.getFacilities();
        HashSet<Facility> t = new HashSet<>(c);

        for (Facility facility:newFacilities){
            if(!t.contains(facility)){
                t.add(facility);
                c.add(facility);
            }
        }
        return hotel;
    }
}
