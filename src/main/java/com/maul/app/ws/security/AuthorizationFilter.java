package com.maul.app.ws.security;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import com.maul.app.ws.io.entity.UserEntity;
import com.maul.app.ws.io.repositories.UserRepository;

import io.jsonwebtoken.Jwts;

public class AuthorizationFilter extends BasicAuthenticationFilter {
    private final UserRepository userRepository;

    public AuthorizationFilter(AuthenticationManager authManager, UserRepository userRepository) {
        super(authManager);
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain) // HttpServletRequest
                                                                                                        // make us able
                                                                                                        // to access the
                                                                                                        // request so we
                                                                                                        // can get
                                                                                                        // request
                                                                                                        // header later
                                                                                                        // to check the
                                                                                                        // token
            throws ServletException, java.io.IOException {
        String header = req.getHeader(SecurityConstants.HEADER_STRING);

        if (header == null || !header.startsWith(SecurityConstants.TOKEN_PREFIX)) {
            chain.doFilter(req, res); // continue process with another filter
            return;
        }

        UsernamePasswordAuthenticationToken authentication = getAuthentication(req);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(req, res);
    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
        String token = request.getHeader(SecurityConstants.HEADER_STRING);
        if (token != null) {
            token = token.replace(SecurityConstants.TOKEN_PREFIX, "");

            String user = Jwts.parser().setSigningKey(SecurityConstants.getTokenSecret()).parseClaimsJws(token)
                    .getBody().getSubject();

            if (user != null) {
                UserEntity usernEntity = userRepository.findByEmail(user);
                if (usernEntity == null)
                    return null;
                UserPrincipal userPrincipal = new UserPrincipal(usernEntity);
                return new UsernamePasswordAuthenticationToken(userPrincipal, null, userPrincipal.getAuthorities());
            }
            return null;
        }
        return null;
    }
}