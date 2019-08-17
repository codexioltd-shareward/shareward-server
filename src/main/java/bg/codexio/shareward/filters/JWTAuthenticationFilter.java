package bg.codexio.shareward.filters;

import bg.codexio.shareward.constant.ConfigurationConstants;
import bg.codexio.shareward.entity.User;
import bg.codexio.shareward.model.user.UserLoginRequestModel;
import com.auth0.jwt.JWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;
import static com.auth0.jwt.impl.PublicClaims.CONTENT_TYPE;


public class JWTAuthenticationFilter
        extends UsernamePasswordAuthenticationFilter {

    private final ObjectMapper objectMapper;

    private final AuthenticationManager authenticationManager;

    private final String secretKey;

    private final UserDetailsService userDetailsService;

    public JWTAuthenticationFilter(ObjectMapper objectMapper,
                                   AuthenticationManager authenticationManager,
                                   String secretKey,
                                   UserDetailsService userDetailsService) {
        this.objectMapper = objectMapper;
        this.authenticationManager = authenticationManager;
        this.secretKey = secretKey;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public Authentication attemptAuthentication(
            HttpServletRequest req,
            HttpServletResponse res
    )
            throws AuthenticationException {
        try {
            var creds = this.objectMapper
                    .readValue(req.getInputStream(),
                            UserLoginRequestModel.class);

            var user = this.userDetailsService.loadUserByUsername(creds.getEmail());

            return authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            user,
                            creds.getPassword(),
                            new ArrayList<>()
                    )
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(
            HttpServletRequest req,
            HttpServletResponse res,
            FilterChain chain,
            Authentication auth
    ) throws IOException, ServletException {
        var userEntity = ((User) auth.getPrincipal());

        var token = JWT.create()
                .withSubject(userEntity.getEmail())
                .withExpiresAt(
                        new Date(System.currentTimeMillis() + ConfigurationConstants.EXPIRATION_TIME)
                )
                .sign(HMAC512(this.secretKey.getBytes()));

        res.addHeader(ConfigurationConstants.HEADER_AUTHORIZATION, ConfigurationConstants.TOKEN_PREFIX + token);

        res.setContentType(CONTENT_TYPE);
        res.setCharacterEncoding("UTF-8");
        res.getWriter().write(this.objectMapper.writeValueAsString(Map.of("userId", userEntity.getId())));
    }

}
