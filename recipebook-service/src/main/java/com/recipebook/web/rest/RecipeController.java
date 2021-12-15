package com.recipebook.web.rest;

import com.recipebook.domain.values.*;
import com.recipebook.service.RecipeService;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author - AvanishKishorPandey
 */

@RestController
@RequestMapping(value = "/recipes", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
@Api(tags = "Recipes", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
public class RecipeController {
    @Autowired
    private RecipeService recipeService;

    @ApiOperation(value = "The API is used to get all the recipes for user.")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "page", dataType = "Integer", value = "Current page number", defaultValue = "0"),
            @ApiImplicitParam(paramType = "query", name = "size", dataType = "Integer", value = "Number of records per page", defaultValue = "10")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "The response for this API contains all of the user recipes with page details.", response = PageResponse.class),
            @ApiResponse(code = 401, message = "You did not supply a valid Authorization header. The header was omitted or your API key was not valid. The response will contain an Errors JSON Object with the specific errors.", response = APIError.class),
            @ApiResponse(code = 500, message = "There was an internal error. The response will contain an Errors JSON Object with the specific errors.", response = APIError.class)
    })
    @GetMapping
    public ResponseEntity<PageResponse<RecipeResponse>> getAllRecipe(@RequestParam(defaultValue = "0", required = false) int page, @RequestParam(defaultValue = "10", required = false) int size) {
        log.debug("REST request to GET_ALL recipe with page : {} & size : {}", page, size);
        PageResponse<RecipeResponse> responseData = recipeService.findAll(page, size);
        return ResponseEntity.ok().body(responseData);
    }

    @ApiOperation(value = "The API is used to create recipe.")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The response will contain the Recipe object that was created.", response = RecipeResponse.class),
            @ApiResponse(code = 400, message = "The request was invalid and/or malformed. The response will contain an Errors JSON Object with the specific errors.", response = APIValidationError.class),
            @ApiResponse(code = 401, message = "You did not supply a valid Authorization header. The header was omitted or your API key was not valid. The response will contain an Errors JSON Object with the specific errors.", response = APIError.class),
            @ApiResponse(code = 500, message = "There was an internal error. The response will contain an Errors JSON Object with the specific errors.", response = APIError.class)
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<RecipeResponse> createRecipe(@RequestBody @Valid RecipeRequest recipeRequest) throws URISyntaxException {
        log.debug("REST request to CREATE recipe with payload : {}", recipeRequest);
        RecipeResponse responseData = recipeService.createRecipe(recipeRequest);
        return ResponseEntity.created(new URI(String.format("/v1/recipes/%s", responseData.getUuid())))
                .body(responseData);
    }

    @ApiOperation(value = "The API is used to get existing recipe.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "The response will contain the Recipe object that was updated.", response = RecipeResponse.class),
            @ApiResponse(code = 401, message = "You did not supply a valid Authorization header. The header was omitted or your API key was not valid. The response will contain an Errors JSON Object with the specific errors.", response = APIError.class),
            @ApiResponse(code = 403, message = "The user is authorized to perform update action on this record.", response = APIError.class),
            @ApiResponse(code = 404, message = "The response will contain an Errors JSON Object with the specific errors.", response = APIError.class),
            @ApiResponse(code = 500, message = "There was an internal error. The response will contain an Errors JSON Object with the specific errors.", response = APIError.class)
    })
    @GetMapping("/{recipeUUID}")
    public ResponseEntity<RecipeResponse> getRecipe(@PathVariable String recipeUUID) {
        log.debug("REST request to GET recipe by GUID {}", recipeUUID);
        RecipeResponse responseData = recipeService.findByGuid(recipeUUID);
        return ResponseEntity.ok().body(responseData);
    }

    @ApiOperation(value = "The API is used to update existing recipe.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "The response will contain the Recipe object that was updated.", response = RecipeResponse.class),
            @ApiResponse(code = 400, message = "The request was invalid and/or malformed. The response will contain an Errors JSON Object with the specific errors.", response = APIValidationError.class),
            @ApiResponse(code = 401, message = "You did not supply a valid Authorization header. The header was omitted or your API key was not valid. The response will contain an Errors JSON Object with the specific errors.", response = APIError.class),
            @ApiResponse(code = 403, message = "The user is authorized to perform update action on this record.", response = APIError.class),
            @ApiResponse(code = 404, message = "The response will contain an Errors JSON Object with the specific errors.", response = APIError.class),
            @ApiResponse(code = 500, message = "There was an internal error. The response will contain an Errors JSON Object with the specific errors.", response = APIError.class)
    })
    @PutMapping("/{recipeUUID}")
    public ResponseEntity<RecipeResponse> updateRecipe(@PathVariable String recipeUUID, @RequestBody @Valid RecipeRequest recipeRequest) {
        log.debug("REST request to UPDATE recipe by GUID {} with payload : {}", recipeUUID, recipeRequest);
        recipeRequest.setGuid(recipeUUID);
        RecipeResponse responseData = recipeService.updateRecipe(recipeRequest);
        return ResponseEntity.ok().body(responseData);
    }

    @ApiOperation(value = "The API is used to delete existing recipe.")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "No content."),
            @ApiResponse(code = 401, message = "You did not supply a valid Authorization header. The header was omitted or your API key was not valid. The response will contain an Errors JSON Object with the specific errors.", response = APIError.class),
            @ApiResponse(code = 403, message = "The user is authorized to perform delete action on this record.", response = APIError.class),
            @ApiResponse(code = 404, message = "The response will contain an Errors JSON Object with the specific errors.", response = APIError.class),
            @ApiResponse(code = 500, message = "There was an internal error. The response will contain an Errors JSON Object with the specific errors.", response = APIError.class)
    })
    @DeleteMapping("/{recipeUUID}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteRecipe(@PathVariable String recipeUUID) {
        log.debug("REST request to DELETE recipe with GUID : {}", recipeUUID);
        this.recipeService.deleteRecipe(recipeUUID);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
