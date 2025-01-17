package com.hotel.roomservice.RoomService.service.impl;


import com.hotel.roomservice.RoomService.dto.RoomDto;
import com.hotel.roomservice.RoomService.exception.BaseException;
import com.hotel.roomservice.RoomService.mapper.RoomMapper;
import com.hotel.roomservice.RoomService.model.Room;
import com.hotel.roomservice.RoomService.repository.RoomRepository;
import com.hotel.roomservice.RoomService.response.ResponseData;
import com.hotel.roomservice.RoomService.service.AwsS3Service;
import com.hotel.roomservice.RoomService.service.interfaces.IRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class RoomServiceImpl implements IRoomService {

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private AwsS3Service awsS3Service;

    @Override
    public ResponseData addNewRoom(MultipartFile image,String name, String type, Double price, String description) {
        ResponseData response = new ResponseData();

        System.out.println(response + "roooomm");

        try {
            String imageUrl = awsS3Service.saveImageToS3(image);

            Room room = new Room();
            room.setImage(imageUrl);
            room.setType(type);
            room.setPrice(price);
            room.setName(name);
            room.set_booked(false);
            room.setDescription(description);

            Room savedRoom = roomRepository.save(room);

            RoomDto roomDTO = RoomMapper.mapRoomEntityToRoomDTO(savedRoom);

            response.setStatusCode(200);
            response.setMessage("successful");
            response.setRoom(roomDTO);


        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error saving a room " + e.getMessage());
        }
        return response;
    }

    @Override
    public List<String> getAllRoomTypes() {
        return roomRepository.findDistinctRoomTypes();
    }

    @Override
    public ResponseData getAllRooms() {
        ResponseData response = new ResponseData();
        System.out.println(response + "roooomm");

        try {
            List<Room> room = roomRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
            List<RoomDto> roomDtoList = RoomMapper.mapRoomListEntityToRoomListDTO(room);
            response.setStatusCode(200);
            response.setMessage("successful");
            response.setRoomList(roomDtoList);

        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error saving a room " + e.getMessage());
        }
        return response;
    }

    @Override
    public ResponseData deleteRoom(Long roomId) {
        ResponseData response = new ResponseData();

        try {
            roomRepository.findById(roomId).orElseThrow(() -> new BaseException("Room Not Found"));
            roomRepository.deleteById(roomId);
            response.setStatusCode(200);
            response.setMessage("successful");

        } catch (BaseException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error saving a room " + e.getMessage());
        }
        return response;
    }

    @Override
    public ResponseData updateRoom(Long roomId, String description, String roomType, String name,Double roomPrice, MultipartFile photo) {
        ResponseData response = new ResponseData();

        try {
            String imageUrl = null;
            if (photo != null && !photo.isEmpty()) {
                imageUrl = awsS3Service.saveImageToS3(photo);
            }
            Room room = roomRepository.findById(roomId).orElseThrow(() -> new BaseException("Room Not Found"));
            if (roomType != null) room.setType(roomType);
            if (roomPrice != null) room.setPrice(roomPrice);
            if (description != null) room.setDescription(description);
            if (imageUrl != null) room.setImage(imageUrl);
            if (name != null) room.setName(name);


            Room updatedRoom = roomRepository.save(room);
            RoomDto roomDTO = RoomMapper.mapRoomEntityToRoomDTO(updatedRoom);

            response.setStatusCode(200);
            response.setMessage("successful");
            response.setRoom(roomDTO);

        } catch (BaseException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error saving a room " + e.getMessage());
        }
        return response;
    }

    @Override
    public ResponseData getRoomById(Long roomId) {
        ResponseData response = new ResponseData();

        try {
            Room room = roomRepository.findById(roomId).orElseThrow(() -> new BaseException("Room Not Found"));
            RoomDto roomDTO = RoomMapper.mapRoomEntityToRoomDtoPlusBookings(room);
            response.setStatusCode(200);
            response.setMessage("successful");
            response.setRoom(roomDTO);

        } catch (BaseException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error saving a room " + e.getMessage());
        }
        return response;
    }

    @Override
    public ResponseData updateBookingStatus(Long roomId, boolean isBooked) {
        ResponseData response = new ResponseData();
        Room room = roomRepository.findById(roomId).orElseThrow(() -> new RuntimeException("Room not found"));
        room.set_booked(isBooked);
        Room updatedRoom = roomRepository.save(room);
        RoomDto roomDTO = RoomMapper.mapRoomEntityToRoomDTO(updatedRoom);

        response.setStatusCode(200);
        response.setMessage("successful");
        response.setRoom(roomDTO);
        return response;
    }

}
