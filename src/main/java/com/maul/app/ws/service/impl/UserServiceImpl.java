package com.maul.app.ws.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import javax.management.RuntimeErrorException;
import javax.transaction.Transactional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.maul.app.ws.exceptions.UserServiceException;
import com.maul.app.ws.io.entity.AddressEntity;
import com.maul.app.ws.io.entity.PasswordResetTokenEntity;
import com.maul.app.ws.io.entity.RoleEntity;
import com.maul.app.ws.io.entity.UserEntity;
import com.maul.app.ws.io.repositories.AddressRepository;
import com.maul.app.ws.io.repositories.PasswordResetTokenRepository;
import com.maul.app.ws.io.repositories.RoleRepository;
import com.maul.app.ws.io.repositories.UserRepository;
import com.maul.app.ws.security.UserPrincipal;
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
    RoleRepository roleRepository;

    @Autowired
    AddressRepository addressRepository;

    @Autowired
    JavaMailSender javaMailSender;

    @Autowired
    Utils utils;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    PasswordResetTokenRepository passwordResetTokenRepository;

    // This address must be verified with Amazon SES.
    final String FROM = "vayneaurelius5@gmail.com";

    final String TO = "renaldolouis555@gmail.com";

    // The subject line for the email.
    final String SUBJECT = "One last step to complete your Registration Account";

    final String PASSWORD_RESET_SUBJECT = "Password reset request";

    // The HTML body for the email.
    final String HTMLBODY = "<h1>Please verify your email address</h1>"
            + "<p>Thank you for registering with our Website. To complete registration process and be able to log in,"
            + " click on the following link: "
            + "<a href='http://localhost:3000/verify?token=$tokenValue'>"
            + "Final step to complete your registration" + "</a><br/><br/>"
            + "Thank you! And we are waiting for you inside!";

    // The email body for recipients with non-HTML email clients.
    final String TEXTBODY = "Please verify your email address. "
            + "Thank you for registering with our Website. To complete registration process and be able to log in,"
            + " open then the following URL in your browser window: "
            + " http://localhost:3000/verify?token=$tokenValue"
            + " Thank you! And we are waiting for you inside!";

    @Override
    public UserDto createUser(UserDto user) {

        if (userRepository.findByEmail(user.getEmail()) != null)
            throw new RuntimeErrorException(null, "Record Already exist");

        // for setting the address when creating user
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
        userEntity.setEmailVerificationToken(utils.generateEmailVerificationToken(publicUserId));

        // Set roles
        Collection<RoleEntity> roleEntities = new HashSet<>();
        for (String role : user.getRoles()) {
            RoleEntity roleEntity = roleRepository.findByName(role);
            if (roleEntity != null) {
                roleEntities.add(roleEntity);
            }
        }

        userEntity.setRoles(roleEntities);

        UserEntity storedUserDetails = userRepository.save(userEntity);

//      BeanUtils.copyProperties(storedUserDetails, returnValue);
        UserDto returnValue = modelMapper.map(storedUserDetails, UserDto.class);

        // Send an email message to user to verify their email address
//        new SES().verifyEmail(returnValue);

        String htmlBodyWithToken = HTMLBODY.replace("$tokenValue", returnValue.getEmailVerificationToken());
        String textBodyWithToken = TEXTBODY.replace("$tokenValue", returnValue.getEmailVerificationToken());

        try {
            // Creating a simple mail message
            SimpleMailMessage mailMessage = new SimpleMailMessage();

            // Setting up necessary details
            mailMessage.setFrom(FROM);
            mailMessage.setTo(TO);
            mailMessage.setText(textBodyWithToken);
            mailMessage.setSubject(SUBJECT);

            // Sending the mail
//            javaMailSender.send(mailMessage);
            javaMailSender.send(mailMessage);
            System.out.println("Email Sent!");
        } catch (Exception e) {
            System.out.println(e);

        }

        return returnValue;
    }

    @Override
    public boolean reVerify(UserDto user) {

        boolean returnValue = true;
        if (userRepository.findByEmail(user.getEmail()) != null) {

            UserEntity userEntity = userRepository.findByEmail(user.getEmail());
            UserDto userDto = new UserDto();
            BeanUtils.copyProperties(userEntity, userDto);

            var newToken = utils.generateEmailVerificationToken(userDto.getUserId());

            userEntity.setEmailVerificationToken(newToken);

            userRepository.save(userEntity);

            String textBodyWithToken = TEXTBODY.replace("$tokenValue", newToken);

            try {
                // Creating a simple mail message
                SimpleMailMessage mailMessage = new SimpleMailMessage();

                // Setting up necessary details
                mailMessage.setFrom(FROM);
                mailMessage.setTo(TO);
                mailMessage.setText(textBodyWithToken);
                mailMessage.setSubject(SUBJECT);

                // Sending the mail
//                javaMailSender.send(mailMessage);
                javaMailSender.send(mailMessage);
                System.out.println("Email Sent!");
            } catch (Exception e) {
                returnValue = false;
                System.out.println(e);

            }
        }

        return returnValue;
    }

    // this loadUserByUsername is springboot method so will be called automatically
    // on login
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByEmail(email);

        if (userEntity == null)
            throw new UsernameNotFoundException(email);

        return new UserPrincipal(userEntity);

//        return new User(userEntity.getEmail(), userEntity.getEncryptedPassowrd(),
//                userEntity.getEmailVerificationStatus(), true, true, true, new ArrayList<>());
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
//        userRepository.updateUserEmailVerificationStatus(status, userId);
        userRepository.updateUserEntityEmailVerificationStatus(status, userId);

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
