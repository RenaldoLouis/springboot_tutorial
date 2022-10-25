package com.maul.app.ws.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.management.RuntimeErrorException;
import javax.transaction.Transactional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.maul.app.ws.exceptions.UserServiceException;
import com.maul.app.ws.io.entity.AddressEntity;
import com.maul.app.ws.io.entity.PasswordResetTokenEntity;
import com.maul.app.ws.io.entity.UserEntity;
import com.maul.app.ws.io.repositories.AddressRepository;
import com.maul.app.ws.io.repositories.PasswordResetTokenRepository;
import com.maul.app.ws.io.repositories.UserRepository;
import com.maul.app.ws.service.UserService;
import com.maul.app.ws.shared.Utils;
import com.maul.app.ws.shared.dto.AddressDTO;
import com.maul.app.ws.shared.dto.PasswordResetRequestDTO;
import com.maul.app.ws.shared.dto.UserDto;
import com.maul.app.ws.ui.model.response.ErrorMessages;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    AddressRepository addressRepository;

    @Autowired
    Utils utils;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    PasswordResetTokenRepository passwordResetTokenRepository;

    @Override
    public UserDto createUser(UserDto user) {

        if (userRepository.findByEmail(user.getEmail()) != null)
            throw new RuntimeErrorException(null, "Record Already exist");

        for (int i = 0; i < user.getAddresses().size(); i++) {
            AddressDTO address = user.getAddresses().get(i);
            address.setUserDetails(user);
            address.setAddressId(utils.generateAddressId(30));
            user.getAddresses().set(i, address);

        }

//		BeanUtils.copyProperties(user, userEntity);
        ModelMapper modelMapper = new ModelMapper();
        UserEntity userEntity = modelMapper.map(user, UserEntity.class);

        String publicUserId = utils.generateUserId(30);
        userEntity.setUserId(publicUserId);
        userEntity.setEncryptedPassowrd(bCryptPasswordEncoder.encode(user.getPassword()));

        UserEntity storedUserDetails = userRepository.save(userEntity);

//      BeanUtils.copyProperties(storedUserDetails, returnValue);
        UserDto returnValue = modelMapper.map(storedUserDetails, UserDto.class);

        return returnValue;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByEmail(email);

        if (userEntity == null)
            throw new UsernameNotFoundException(email);

        return new User(userEntity.getEmail(), userEntity.getEncryptedPassowrd(), new ArrayList<>());
    }

    @Override
    public UserDto getUser(String email) {
        UserEntity userEntity = userRepository.findByEmail(email);
        if (userEntity == null)
            throw new UsernameNotFoundException(email);
        UserDto returnValue = new UserDto();
        BeanUtils.copyProperties(userEntity, returnValue);
        return returnValue;
    }

    @Override
    public UserDto getUserByUserId(String userId) {
        UserDto returnValue = new UserDto();

//        UserEntity userEntity = userRepository.findByUserId(userId);
        UserEntity userEntity = userRepository.findUserEntityByUserId(userId);

        if (userEntity == null)
            throw new UsernameNotFoundException("User With ID : " + userId + " Not Found");

        BeanUtils.copyProperties(userEntity, returnValue);
        return returnValue;
    }

    @Override
    public String confirmUser(String userId) {
        String returnValue = "true";
        UserEntity userEntity = userRepository.findByUserId(userId);

        if (userEntity == null) {
            throw new UsernameNotFoundException("User With ID : " + userId + " Not Found");
        }

        if (userEntity.getEmailVerificationStatus() == true) {
            returnValue = "existed";
        }

        if (userEntity != null) {
            userEntity.setEmailVerificationStatus(true);
            userRepository.save(userEntity);
        }
        return returnValue;
    }

    @Transactional
    @Override
    public String updateUserEmailStatus(String userId, Boolean status) {
        String returnValue = "true";
        userRepository.updateUserEmailVerificationStatus(status, userId);

        return returnValue;
    }

    @Override
    public UserDto updateUser(String userId, UserDto user) {
        UserDto returnValue = new UserDto();
        UserEntity userEntity = userRepository.findByUserId(userId);

        if (userEntity == null)
            throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());

        userEntity.setFirstName(user.getFirstName());
        userEntity.setLastName(user.getLastName());

        UserEntity updatedUserDetails = userRepository.save(userEntity);

        BeanUtils.copyProperties(updatedUserDetails, returnValue);

        return returnValue;
    }

    @Override
    public void deleteUser(String userId) {
        UserEntity userEntity = userRepository.findByUserId(userId);

        if (userEntity == null)
            throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());

        userRepository.delete(userEntity);

    }

    @Override
    public List<UserDto> getUsers(int page, int limit) {
        List<UserDto> returnedValue = new ArrayList<>();

        if (page > 0)
            page -= 1;

        org.springframework.data.domain.Pageable pageableRequest = PageRequest.of(page, limit);

        Page<UserEntity> usersPage = userRepository.findAll(pageableRequest);

        List<UserEntity> users = usersPage.getContent();

        for (UserEntity userEntity : users) {
            UserDto userDto = new UserDto();
            BeanUtils.copyProperties(userEntity, userDto);
            returnedValue.add(userDto);
        }

        return returnedValue;
    }

    @Override
    public List<UserDto> findUserByFirstName(int page, int limit, String firstName) {
        List<UserDto> returnedValue = new ArrayList<>();

        if (page > 0)
            page -= 1;

        org.springframework.data.domain.Pageable pageableRequest = PageRequest.of(page, limit);

        Page<UserEntity> usersPage = userRepository.findUserByFirstName(pageableRequest, firstName.toLowerCase());

        List<UserEntity> users = usersPage.getContent();

        for (UserEntity userEntity : users) {
            UserDto userDto = new UserDto();
            List<AddressDTO> addressDTO = new ArrayList<>();
            BeanUtils.copyProperties(userEntity, userDto);
            List<AddressEntity> addresses = addressRepository.findAllByUserDetails(userEntity);
            for (AddressEntity addressEntity : addresses) {
                AddressDTO addressModel = new AddressDTO();
                BeanUtils.copyProperties(addressEntity, addressModel);
                addressDTO.add(addressModel);
            }
            userDto.setAddresses(addressDTO);
            returnedValue.add(userDto);
        }

        return returnedValue;
    }

    @Override
    public boolean verifyEmailToken(String token) {
        boolean returnValue = false;

        // Find user by token
        UserEntity userEntity = userRepository.findUserByEmailVerificationToken(token);

        if (userEntity != null) {
            boolean hasTokenExpired = Utils.hasTokenExpired(token);
            if (!hasTokenExpired) {
                userEntity.setEmailVerificationToken(null);
                userEntity.setEmailVerificationStatus(Boolean.TRUE);
                userRepository.save(userEntity);
                returnValue = true;
            }
        }
        return returnValue;
    }

    @Override
    public PasswordResetRequestDTO requestPasswordReset(String email) {
        PasswordResetRequestDTO returnValue = new PasswordResetRequestDTO();

        UserEntity userEntity = userRepository.findByEmail(email);

        if (userEntity == null) {
            throw new UserServiceException(ErrorMessages.EMAIL_ADDRESS_NOT_FOUND.getErrorMessage());
        }

        String token = utils.generatePasswordResetToken(userEntity.getUserId());

        PasswordResetTokenEntity passwordResetTokenEntity = new PasswordResetTokenEntity();
        passwordResetTokenEntity.setToken(token);
        passwordResetTokenEntity.setUserDetails(userEntity);
        passwordResetTokenRepository.save(passwordResetTokenEntity);

        BeanUtils.copyProperties(passwordResetTokenEntity, returnValue);

//        returnValue = new AmazonSES().sendPasswordResetRequest(userEntity.getFirstName(), userEntity.getEmail(), token);

        return returnValue;
    }

    @Override
    public boolean resetPassword(String token, String password) {
        boolean returnValue = false;

        if (Utils.hasTokenExpired(token)) {
            throw new UserServiceException(ErrorMessages.TOKEN_EXPIRED.getErrorMessage());
        }

        PasswordResetTokenEntity passwordResetTokenEntity = passwordResetTokenRepository.findByToken(token);

        if (passwordResetTokenEntity == null) {
            throw new UserServiceException(ErrorMessages.TOKEN_NOT_FOUND.getErrorMessage());
        }

        // Prepare new password
        String encodedPassword = bCryptPasswordEncoder.encode(password);

        // Update User password in database
        UserEntity userEntity = passwordResetTokenEntity.getUserDetails();
        userEntity.setEncryptedPassowrd(encodedPassword);
        UserEntity savedUserEntity = userRepository.save(userEntity);

        // Verify if password was saved succesfully
        if (savedUserEntity != null && savedUserEntity.getEncryptedPassowrd().equalsIgnoreCase(encodedPassword)) {
            returnValue = true;
        }

        // Remove password REeset Token from database
        passwordResetTokenRepository.delete(passwordResetTokenEntity);

        return returnValue;
    }

    @Override
    public List<UserDto> getConfirmedUsers(int page, int limit) {
        List<UserDto> returnedValue = new ArrayList<>();

        if (page > 0)
            page -= 1;

        org.springframework.data.domain.Pageable pageableRequest = PageRequest.of(page, limit);

        Page<UserEntity> usersPage = userRepository.findAllUsersWithConfirmedEmailAddress(pageableRequest);

        List<UserEntity> users = usersPage.getContent();

        for (UserEntity userEntity : users) {
            UserDto userDto = new UserDto();
            BeanUtils.copyProperties(userEntity, userDto);
            returnedValue.add(userDto);
        }

        return returnedValue;
    }

}
