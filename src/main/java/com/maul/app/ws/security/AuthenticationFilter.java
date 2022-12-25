package com.maul.app.ws.security;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maul.app.ws.SpringApplicationContext;
import com.maul.app.ws.exceptions.UserServiceException;
import com.maul.app.ws.io.entity.UserEntity;
import com.maul.app.ws.io.repositories.UserRepository;
import com.maul.app.ws.service.UserService;
import com.maul.app.ws.shared.dto.UserDto;
import com.maul.app.ws.ui.model.request.UserLoginRequestModel;
import com.maul.app.ws.ui.model.response.ErrorMessage;
import com.maul.app.ws.ui.model.response.ErrorMessages;
import com.maul.app.ws.ui.model.response.LoginRest;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private UserLoginRequestModel creds;

    public AuthenticationFilter(AuthenticationManager authenticationManager, UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse res)
            throws org.springframework.security.core.AuthenticationException {
        try {
            creds = new ObjectMapper().readValue(req.getInputStream(),
                    UserLoginRequestModel.class);

            return authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(creds.getEmail(), creds.getPassword(), new ArrayList<>()));
        } catch (java.io.IOException e) {
            throw new UserServiceException(ErrorMessages.TOKEN_EXPIRED.getErrorMessage());
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void successfulAuthentication(HttpServletRequest req, HttpServletResponse res, FilterChain chain,
            Authentication auth) throws IOException, ServletException {
        LoginRest returnValue = new LoginRest();

        String userName = ((UserPrincipal) auth.getPrincipal()).getUsername();
        Collection<String> roles = ((UserPrincipal) auth.getPrincipal())
                .getRoles();

        String token = Jwts.builder().setSubject(userName)
                .setExpiration(new Date(System.currentTimeMillis() + SecurityConstants.EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS512, SecurityConstants.getTokenSecret()).compact();

        UserService userService = (UserService) SpringApplicationContext.getBean("userServiceImpl");

        UserDto userDto = userService.getUser(userName);

        res.addHeader(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + token);
        res.addHeader("UserID", userDto.getUserId());

        returnValue.setToken(token);
        returnValue.setUserID(userDto.getUserId());
        returnValue.setRoles(roles);

        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");
        res.getWriter().write(new ObjectMapper().writeValueAsString(returnValue));

    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest req, HttpServletResponse res,
            AuthenticationException failed)
            throws org.springframework.security.core.AuthenticationException {
        var failReason = failed.getMessage().toString();
        var condition = failReason.equals("Bad credentials");
        Date date = new Date();
        String strDateFormat = "hh:mm:ss a";
        DateFormat dateFormat = new SimpleDateFormat(strDateFormat);
        String formattedDate = dateFormat.format(date);

        ErrorMessage errorMessages = new ErrorMessage();

        try {
            UserEntity userEntity = userRepository.findByEmail(creds.getEmail());

            if (condition) {
                errorMessages.setMessage("Pleace Check your username or password again");
                errorMessages.setStatus(400);
                errorMessages.setTimestamp(date);
            } else {
                if (userEntity == null) {
                    errorMessages.setMessage("User not Existed please register first");
                    errorMessages.setStatus(400);
                    errorMessages.setTimestamp(date);
                } else {
                    errorMessages.setMessage("Email Not Verified Yet");
                    errorMessages.setStatus(400);
                    errorMessages.setTimestamp(date);
                }
            }
            res.setContentType("application/json");
            res.setCharacterEncoding("UTF-8");
            res.setStatus(400);

            res.getWriter().write(new ObjectMapper().writeValueAsString(errorMessages));

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

}
