package CyStaff.app.ImageData;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;

@Api(value = "ImageDataController", description = "REST APIs related to ImageData Entity")
@RestController
@RequestMapping("/image")
public class ImageDataController {
    @Autowired
    private ImageDataService imageDataService;

    @ApiOperation(value = "Creates a new ImageData entry in the database.")
    @ApiResponse(code = 200, message = "New ImageData entry created.")
    @PostMapping
    public ResponseEntity<?> uploadImage(
            @ApiParam(name = "image", value = "Image File")
            @RequestParam("image") MultipartFile file) throws IOException{
        ImageUploadResponse response = imageDataService.uploadImage(file);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @ApiOperation(value = "Gets the ImageInfo in the database by name.")
    @ApiResponse(code = 200, message = "Returns ImageInfo.")
    @GetMapping("/info/{name}")
    public ResponseEntity<?> getImageInfoByName(
            @ApiParam(name = "name", value = "Name of Image")
            @PathVariable("name") String name){
        ImageData image = imageDataService.getInfoByImageName(name);

        return ResponseEntity.status(HttpStatus.OK)
                .body(image);
    }

    @ApiOperation(value = "Gets the Image in the database by name.")
    @ApiResponse(code = 200, message = "Returns Image.")
    @GetMapping("/{name}")
    public ResponseEntity<?> getImageByName(
            @ApiParam(name = "name", value = "Name of Image")
            @PathVariable("name") String name){
        byte [] image = imageDataService.getImage(name);

        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.valueOf("image/png"))
                .body(image);
    }

}
