package com.recipebook.web.rest;

import com.recipebook.domain.values.*;
import com.recipebook.service.RecipeUserDetailsService;
import com.recipebook.service.RefreshTokenService;
import com.recipebook.service.TokenProvider;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * @author - AvanishKishorPandey
 */

@RestController
@Api(tags = "Authentication", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
public class AuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenProvider tokenProvider;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private RecipeUserDetailsService userDetailsService;

    @ApiOperation(value = "The signin API is used to authenticate a user in application. The issuer of the One Time Password will dictate if a JWT or a Refresh Token may be issued in the API response.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "The authentication was successful. The response will contain the User object that was authenticated along with accessToken & refreshToken.", response = AuthResponse.class),
            @ApiResponse(code = 500, message = "There was an internal error. The response will contain an Errors JSON Object with the specific errors.", response = APIError.class),
            @ApiResponse(code = 400, message = "The request was invalid and/or malformed. The response will contain an Errors JSON Object with the specific errors.", response = APIValidationError.class),
            @ApiResponse(code = 404, message = "The user was not found or the password was incorrect. The response will contain an Errors JSON Object with the specific errors.", response = APIError.class)
    })
    @PostMapping(value = "/signin", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AuthResponse> authenticate(@Valid @RequestBody final AuthRequest loginRequest) {
        log.debug("Request POST/login' calling authenticate() with payload {}", loginRequest);
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        final String accessToken = tokenProvider.createToken(userPrincipal);
        final String refreshToken = refreshTokenService.createRefreshToken(userPrincipal.getId());
        return ResponseEntity.ok(AuthResponse.builder()
                        .withAccessToken(accessToken)
                        .withRefreshToken(refreshToken)
                        .withUserResponse(UserResponse.builder()
                                .id(userPrincipal.getUsername())
                                .firstName(userPrincipal.getFirstName())
                                .lastName(userPrincipal.getLastName())
                                .build())
                .build());
    }

    @ApiOperation(value = "The refresh API is used obtain a new accessToken.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = " The response will contain the User object that was created.", response = UserResponse.class),
            @ApiResponse(code = 500, message = "There was an internal error. The response will contain an Errors JSON Object with the specific errors.", response = APIError.class),
            @ApiResponse(code = 400, message = "The request was invalid and/or malformed. The response will contain an Errors JSON Object with the specific errors.", response = APIValidationError.class),
            @ApiResponse(code = 403, message = "The response will contain the actions that prevented creating new accessToken.", response = APIError.class)
    })
    @PostMapping(value = "/refresh", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TokenRefreshResponse> refreshToken(@Valid @RequestBody final TokenRefreshRequest request) {
        log.debug("Request POST/refresh' calling refreshToken() with token {}", request.getRefreshToken());
        UserPrincipal userPrincipal = refreshTokenService.refreshToken(request);
        final String accessToken = tokenProvider.createToken(userPrincipal);
        return ResponseEntity.ok(TokenRefreshResponse.builder().withAccessToken(accessToken).build());
    }


    @ApiOperation(value = "The signup API is used register a user in application.")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = " The response will contain the User object that was created.", response = UserResponse.class),
            @ApiResponse(code = 500, message = "There was an internal error. The response will contain an Errors JSON Object with the specific errors.", response = APIError.class),
            @ApiResponse(code = 400, message = "The request was invalid and/or malformed. The response will contain an Errors JSON Object with the specific errors.", response = APIValidationError.class),
            @ApiResponse(code = 409, message = "The response will contain the actions that prevented creating new user.", response = APIError.class)
    })
    @PostMapping(value = "/signup", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<UserResponse> registerUser(@Valid @RequestBody final SignupRequest signupRequest) {
        log.debug("Request POST/signup' calling authenticate() with payload {}", signupRequest);
        UserResponse response = userDetailsService.registerUser(signupRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
