package com.maul.app.ws.ui.controller;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.transaction.Transactional;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.maul.app.ws.exceptions.UserServiceException;
import com.maul.app.ws.service.AddressService;
import com.maul.app.ws.service.UserService;
import com.maul.app.ws.shared.dto.AddressDTO;
import com.maul.app.ws.shared.dto.PasswordResetRequestDTO;
import com.maul.app.ws.shared.dto.UserDto;
import com.maul.app.ws.ui.model.request.PasswordResetModel;
import com.maul.app.ws.ui.model.request.PasswordResetRequestModel;
import com.maul.app.ws.ui.model.request.UpdateUserEmailStatus;
import com.maul.app.ws.ui.model.request.UserDetailsRequestModel;
import com.maul.app.ws.ui.model.response.AddressesRest;
import com.maul.app.ws.ui.model.response.ErrorMessages;
import com.maul.app.ws.ui.model.response.OperationStatusModel;
import com.maul.app.ws.ui.model.response.RequestOperationName;
import com.maul.app.ws.ui.model.response.RequestOperationStatus;
import com.maul.app.ws.ui.model.response.ResetPasswordRequestRest;
import com.maul.app.ws.ui.model.response.UserRest;

@RestController
@RequestMapping("/users")
//@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    AddressService addressService;

    @GetMapping()
    public List<UserRest> getUsers(@RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "limit", defaultValue = "10") int limit) {

        List<UserRest> returnedValue = new ArrayList<>();

        List<UserDto> userDtos = userService.getUsers(page, limit);

        for (UserDto userDto : userDtos) {
            UserRest userModel = new UserRest();
            BeanUtils.copyProperties(userDto, userModel);
            returnedValue.add(userModel);
        }

        return returnedValue;
    }

    @GetMapping("/{id}")
    public UserRest getUser(@PathVariable String id) {
        UserRest returnValue = new UserRest();

        UserDto userDto = userService.getUserByUserId(id);
        BeanUtils.copyProperties(userDto, returnValue);

        return returnValue;
    }

    @PostMapping("/signUp")
    public UserRest createUser(@RequestBody UserDetailsRequestModel userDetails) throws Exception {
        UserRest returnValue = new UserRest();

        if (userDetails.getFirstName().isEmpty())
            throw new UserServiceException(ErrorMessages.MISSING_REQUIRED_FIELD.getErrorMessage());

        // Shallow Copy
//		UserDto userDto = new UserDto();
//		BeanUtils.copyProperties(userDetails, userDto);

        // Deep Copy (should use this if theres object in object
        ModelMapper modelMapper = new ModelMapper();
        UserDto userDto = modelMapper.map(userDetails, UserDto.class);

        UserDto createdUser = userService.createUser(userDto);
        returnValue = modelMapper.map(createdUser, UserRest.class);

        return returnValue;
    }

    @PutMapping(path = "/{id}")
    public UserRest updateUser(@PathVariable String id, @RequestBody UserDetailsRequestModel userDetails) {
        UserRest returnValue = new UserRest();

        UserDto userDto = new UserDto();
        BeanUtils.copyProperties(userDetails, userDto);

        UserDto updatedUser = userService.updateUser(id, userDto);
        BeanUtils.copyProperties(updatedUser, returnValue);

        return returnValue;
    }

    @Secured("ROLE_ADMIN")
    @DeleteMapping("/{id}")
    public OperationStatusModel deleteUser(@PathVariable String id) {
        OperationStatusModel returnValue = new OperationStatusModel();
        returnValue.setOperationName(RequestOperationName.DELETE.name());

        userService.deleteUser(id);

        returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());

        return returnValue;
    }

    @GetMapping(path = "/{id}/addresses")
    public CollectionModel<AddressesRest> getUserAddresses(@PathVariable String id) {
        List<AddressesRest> returnValue = new ArrayList<>();

        List<AddressDTO> addressDTO = addressService.getAddresses(id);

        ModelMapper modelMapper = new ModelMapper();

        if (addressDTO != null && !addressDTO.isEmpty()) {

            Type listType = new TypeToken<List<AddressesRest>>() {
            }.getType();
            returnValue = modelMapper.map(addressDTO, listType);

            for (AddressesRest addressRest : returnValue) {
                Link selfLink = WebMvcLinkBuilder
                        .linkTo(WebMvcLinkBuilder.methodOn(UserController.class)
                                .getUserAddress(id, addressRest.getAddressId()))
                        .withSelfRel();
                addressRest.add(selfLink);
            }
        }

        Link userLink = WebMvcLinkBuilder.linkTo(UserController.class).slash(id).withRel("user");

        Link selfLink = WebMvcLinkBuilder
                .linkTo(WebMvcLinkBuilder.methodOn(UserController.class).getUserAddresses(id))
                .withSelfRel();

        return CollectionModel.of(returnValue, userLink, selfLink);
    }

    @GetMapping(path = "/{userId}/addresses/{addressId}")
    public EntityModel<AddressesRest> getUserAddress(@PathVariable String userId, @PathVariable String addressId) {
        AddressDTO addressDTO = addressService.getAddress(addressId);

        ModelMapper modelMapper = new ModelMapper();
        AddressesRest returnValue = modelMapper.map(addressDTO, AddressesRest.class);

        Link userLink = WebMvcLinkBuilder.linkTo(UserController.class).slash(userId).withRel("user");
        Link userAddressesLink = WebMvcLinkBuilder
                .linkTo(WebMvcLinkBuilder.methodOn(UserController.class).getUserAddresses(userId))
                .withRel("addresses");
        Link selfLink = WebMvcLinkBuilder
                .linkTo(WebMvcLinkBuilder.methodOn(UserController.class).getUserAddress(userId, addressId))
                .withSelfRel();

        return EntityModel.of(returnValue, Arrays.asList(userLink, userAddressesLink, selfLink));
    }

    @PostMapping(path = "/passwordResetRequest")
    public ResetPasswordRequestRest requestReset(@RequestBody PasswordResetRequestModel passwordResetModel) {
        ResetPasswordRequestRest returnValue = new ResetPasswordRequestRest();

        PasswordResetRequestDTO passwordResetRequestDTO = userService
                .requestPasswordReset(passwordResetModel.getEmail());

        BeanUtils.copyProperties(passwordResetRequestDTO, returnValue);

        return returnValue;
    }

    @PostMapping(path = "/passwordReset")
    public OperationStatusModel resetPassword(@RequestBody PasswordResetModel passwordResetModel) {
        OperationStatusModel returnValue = new OperationStatusModel();

        boolean operationResult = userService.resetPassword(passwordResetModel.getToken(),
                passwordResetModel.getPassword());

        returnValue.setOperationName(RequestOperationName.PASSWORD_RESET.name());
        returnValue.setOperationResult(RequestOperationStatus.ERROR.name());

        if (operationResult) {
            returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());
        }

        return returnValue;
    }

    @PostMapping(path = "/confirmUser/{id}")
    public OperationStatusModel confirmUser(@PathVariable String id) {
        OperationStatusModel returnValue = new OperationStatusModel();

        String operationResult = userService.confirmUser(id);

        returnValue.setOperationName(RequestOperationName.CONFIRM_USER.name());
        returnValue.setOperationResult(RequestOperationStatus.ERROR.name());

        if (operationResult == "existed") {
            returnValue.setOperationResult(RequestOperationStatus.ALREADY_CONFIRMED.name());
        }

        if (operationResult == "true") {
            returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());
        }

        return returnValue;
    }

    @Transactional
    @PutMapping(path = "/updateUserEmailStatus")
    public OperationStatusModel updateUserEmailStatus(@RequestBody UpdateUserEmailStatus updateUserEmailStatus) {
        OperationStatusModel returnValue = new OperationStatusModel();

        String operationResult = userService.updateUserEmailStatus(updateUserEmailStatus.getUserId(),
                updateUserEmailStatus.getStatus());

        returnValue.setOperationName(RequestOperationName.CONFIRM_USER.name());
        returnValue.setOperationResult(RequestOperationStatus.ERROR.name());

        if (operationResult == "true") {
            returnValue.setOperationResult(RequestOperationStatus.UPDATED.name());
        }

        return returnValue;
    }

    @GetMapping(path = "/getConfirmedUser")
    public List<UserRest> getConfirmedUser(@RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "limit", defaultValue = "10") int limit) {

        List<UserRest> returnedValue = new ArrayList<>();

        List<UserDto> userDtos = userService.getConfirmedUsers(page, limit);

        for (UserDto userDto : userDtos) {
            UserRest userModel = new UserRest();
            BeanUtils.copyProperties(userDto, userModel);
            returnedValue.add(userModel);
        }

        return returnedValue;
    }

    @GetMapping(path = "/findUserFirstName")
    public List<UserRest> findUserFirstName(@RequestParam String firstName,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "limit", defaultValue = "10") int limit) {

        List<UserRest> returnedValue = new ArrayList<>();

        List<UserDto> userDtos = userService.findUserByFirstName(page, limit, firstName);

        for (UserDto userDto : userDtos) {
            UserRest userModel = new UserRest();
            List<AddressesRest> addressModel = new ArrayList<>();
            BeanUtils.copyProperties(userDto, userModel);
            for (AddressDTO addressDTO : userDto.getAddresses()) {
                AddressesRest addressRest = new AddressesRest();
                BeanUtils.copyProperties(addressDTO, addressRest);
                addressModel.add(addressRest);
            }
            userModel.setAddresses(addressModel);
            returnedValue.add(userModel);
        }

        return returnedValue;
    }

}
