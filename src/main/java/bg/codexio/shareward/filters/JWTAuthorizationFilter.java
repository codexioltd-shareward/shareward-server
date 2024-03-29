package bg.codexio.shareward.filters;

import bg.codexio.shareward.constant.ConfigurationConstants;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

import static bg.codexio.shareward.constant.ConfigurationConstants.TOKEN_PREFIX;

public class JWTAuthorizationFilter extends BasicAuthenticationFilter {

    private final ObjectMapper objectMapper;

    private final String secretKey;

    private final UserDetailsService userDetailsService;

    public JWTAuthorizationFilter(ObjectMapper objectMapper,
                                  AuthenticationManager authenticationManager,
                                  String secretKey,
                                  UserDetailsService userDetailsService) {
        super(authenticationManager);
        this.objectMapper = objectMapper;
        this.secretKey = secretKey;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain) throws IOException, ServletException {
        String header = req.getHeader(ConfigurationConstants.HEADER_AUTHORIZATION);

        if (header == null || !header.startsWith(TOKEN_PREFIX)) {
            chain.doFilter(req, res);
            return;
        }

        UsernamePasswordAuthenticationToken authentication = getAuthentication(req);

        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(req, res);
    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
        String token = request.getHeader(ConfigurationConstants.HEADER_AUTHORIZATION);
        if (token != null) {
            // parse the token.
            String user = JWT.require(Algorithm.HMAC512(this.secretKey.getBytes()))
                    .build()
                    .verify(token.replace(TOKEN_PREFIX, ""))
                    .getSubject();

            if (user != null) {
                return new UsernamePasswordAuthenticationToken(this.userDetailsService.loadUserByUsername(user), null, new ArrayList<>());
            }
            return null;
        }
        return null;
    }
}
