package com.hotel.userservice.UserService.controller;

import com.hotel.userservice.UserService.constants.Pages;
import com.hotel.userservice.UserService.constants.UserRole;
import com.hotel.userservice.UserService.dto.BookingDto;
import com.hotel.userservice.UserService.response.ResponseData;
import com.hotel.userservice.UserService.service.interfaces.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequestMapping(Pages.USERS_BASE)
@CrossOrigin(origins = "http://localhost:7070/")
@RestController
public class UserController {
    @Autowired
    private IUserService userService;


    @PostMapping(Pages.ADD_USER)
    public ResponseEntity<ResponseData> addUser (
            @RequestParam(value = "profileImage", required = false) MultipartFile profileImage,
            @RequestParam(value = "username", required = false) String username,
            @RequestParam(value = "password", required = false) String password,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "address", required = false) String address,
            @RequestParam(value = "city", required = false) String city,
            @RequestParam(value = "phone", required = false) String phone,
            @RequestParam(value = "role", required = false) UserRole role


    ){
        ResponseData responseData = userService.addUser(profileImage,username,password,city, address,
                phone, email, role);
        return ResponseEntity.status(responseData.getStatusCode()).body(responseData);
    }

    @GetMapping(Pages.ALL_USERS)
//    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ResponseData> getAllUsers() {
        ResponseData responseData = new ResponseData();
        responseData = userService.getAllUsers();
        return ResponseEntity.status(responseData.getStatusCode()).body(responseData);
    }

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
    @GetMapping(Pages.GET_USER_BY_ID)
    public ResponseEntity<ResponseData> getUserById(@PathVariable("userId") String userId) {
        ResponseData response = userService.getUserById(userId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @DeleteMapping(Pages.DELETE_USER)
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ResponseData> deleteUSer(@PathVariable("userId") String userId) {
        ResponseData response = userService.deleteUser(userId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping(Pages.GET_LOGGED_IN_USER_INFO)
    public ResponseEntity<ResponseData> getLoggedInUserProfile() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        ResponseData response = userService.getUserInfo(email);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }



    @PatchMapping(Pages.UPDATE_USER_DETAILS)
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
    public ResponseEntity<ResponseData> updateUser(@PathVariable("userId") Long userId,
                                                   @RequestParam(value = "profileImage", required = false) MultipartFile profileImage,
                                                   @RequestParam(value = "username", required = false) String username,
                                                   @RequestParam(value = "email", required = false) String email,
                                                   @RequestParam(value = "city", required = false) String city,
                                                   @RequestParam(value = "address", required = false) String address,
                                                   @RequestParam(value = "phone", required = false) String  phone,
                                                   @RequestParam(value = "role", required = false) UserRole role


    ) {
        ResponseData response = userService.updateUser(userId, profileImage, username,email, city, address,phone, role);
        System.out.println(response);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }


//    @GetMapping(Pages.GET_USER_BOOKINGS)
//    public ResponseEntity<ResponseData> getUserBookingHistory(@PathVariable("userId") String userId) {
//        ResponseData response = userService.getUserBookingHistory(userId);
//        return ResponseEntity.status(response.getStatusCode()).body(response);
//    }
//


}
