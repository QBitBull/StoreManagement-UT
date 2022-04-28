package com.sfinance.SFBackend.Controller;

import com.sfinance.SFBackend.Constants.SecurityConstants;
import com.sfinance.SFBackend.Entity.User;
import com.sfinance.SFBackend.Exceptions.ExceptionHandling;
import com.sfinance.SFBackend.Exceptions.UserExceptions.EmailExistException;
import com.sfinance.SFBackend.Exceptions.UserExceptions.UserNotFoundException;
import com.sfinance.SFBackend.Exceptions.UserExceptions.UsernameExistException;
import com.sfinance.SFBackend.Security.JWT.CustomHttp.HttpResponse;
import com.sfinance.SFBackend.Security.JWT.JWTTokenProvider;
import com.sfinance.SFBackend.Security.Registration.AuthUser;
import com.sfinance.SFBackend.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static com.sfinance.SFBackend.Constants.FileConstant.*;
import static com.sfinance.SFBackend.Constants.UserConstants.THE_PASSWORD_HAS_BEEN_RESETED;
import static com.sfinance.SFBackend.Constants.UserConstants.USER_DELETED_SUCCESSFULLY;
import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;

@RestController
@RequestMapping(value = "/user")
public class UserController extends ExceptionHandling {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JWTTokenProvider jwtTokenProvider;

    @Autowired
    public UserController(UserService userService, AuthenticationManager authenticationManager, JWTTokenProvider jwtTokenProvider) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody User user) throws UserNotFoundException, EmailExistException, UsernameExistException {
        User newUser = userService.register(user.getFirstName(),user.getLastName(),user.getUsername(),user.getEmail());
        return new ResponseEntity<>(newUser, HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<User> login(@RequestBody User user){
        authenticate(user.getUsername(), user.getPassword());
        User loginUser =  userService.findUserByUsername(user.getUsername());
        AuthUser authUser = new AuthUser(loginUser);
        HttpHeaders jwtHeader = getJWTHeader(authUser);
        return new ResponseEntity<>(loginUser, jwtHeader, HttpStatus.OK);
    }

    @PostMapping("/add")
    @PreAuthorize("hasAnyAuthority('user:create')")
    public ResponseEntity<User> addNewUser(@RequestParam("firstName")String firstName,
                                           @RequestParam("lastName") String lastName,
                                           @RequestParam("username") String username,
                                           @RequestParam("email") String email,
                                           @RequestParam("role") String role,
                                           @RequestParam("isActive") String isActive,
                                           @RequestParam("isNotLocked") String isNotLocked,
                                           @RequestParam(value = "profileImage", required = false)MultipartFile profileImage) throws UserNotFoundException, EmailExistException, IOException, UsernameExistException {
        User newUser = userService.addNewUser(firstName, lastName, username,
                email, role, Boolean.parseBoolean(isNotLocked), Boolean.parseBoolean(isActive), profileImage);
        return new ResponseEntity<>(newUser, HttpStatus.OK);
    }

    @PostMapping("/update")
    @PreAuthorize("hasAnyAuthority('user:update')")
    public ResponseEntity<User> updateUser(@RequestParam("currentUserName")String currentUserName,
                                           @RequestParam("firstName")String firstName,
                                           @RequestParam("lastName") String lastName,
                                           @RequestParam("username") String username,
                                           @RequestParam("email") String email,
                                           @RequestParam("role") String role,
                                           @RequestParam("isActive") String isActive,
                                           @RequestParam("isNotLocked") String isNotLocked,
                                           @RequestParam(value = "profileImage", required = false)MultipartFile profileImage) throws UserNotFoundException, EmailExistException, IOException, UsernameExistException {
        User updateUser = userService.updateUser(currentUserName,firstName, lastName, username,
                email, role, Boolean.parseBoolean(isNotLocked), Boolean.parseBoolean(isActive), profileImage);
        return new ResponseEntity<>(updateUser, HttpStatus.OK);
    }

    @GetMapping("/find/{username}")
    @PreAuthorize("hasAnyAuthority('user:read')")
    public ResponseEntity<User> getUser(@PathVariable String username){
        User user = userService.findUserByUsername(username);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping("/list")
    @PreAuthorize("hasAnyAuthority('user:read')")
    public ResponseEntity<List<User>> getAllUsers(){
        List<User> users = userService.getUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping("/resetPassword/{username}")
    public ResponseEntity<HttpResponse> resetPassword(@PathVariable String username){
        userService.resetPassword(username);
        return response(HttpStatus.OK, THE_PASSWORD_HAS_BEEN_RESETED);
    }

    @DeleteMapping("/delete/{username}")
    @PreAuthorize("hasAnyAuthority('user:delete')")
    public ResponseEntity<HttpResponse> deleteUser(@PathVariable String username){
        userService.deleteUser(username);
        return response(HttpStatus.NO_CONTENT, USER_DELETED_SUCCESSFULLY);
    }

    @PostMapping("/updateProfileImage")
    public ResponseEntity<User> updateProfileImage(@RequestParam("username") String username,
                                                   @RequestParam(value = "profileImage")MultipartFile profileImage) throws UserNotFoundException, EmailExistException, IOException, UsernameExistException {
        User user = userService.updateProfileImage(username,profileImage);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping(path = "/image/{username}/{fileName}", produces = IMAGE_JPEG_VALUE)
    public byte[] getProfileImage(@PathVariable("username") String username, @PathVariable("fileName") String fileName) throws IOException {
        return Files.readAllBytes(Paths.get(USER_FOLDER + username + FORWARD_SLASH + fileName));
    }

    @GetMapping(path = "/image/profile/{username}", produces = IMAGE_JPEG_VALUE)
    public byte[] getTempProfileImage(@PathVariable("username") String username) throws IOException {
        URL url = new URL(TEMP_PROFILE_IMAGE_BASE_URL + username);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try(InputStream inputStream = url.openStream()){
            int bytesRead;
            byte[] chunks = new byte[1024];
            while ((bytesRead = inputStream.read(chunks))>0){
                byteArrayOutputStream.write(chunks,0,bytesRead);
            }
        }
        return byteArrayOutputStream.toByteArray();
    }



    private ResponseEntity<HttpResponse> response(HttpStatus status, String message) {
        HttpResponse body = new HttpResponse(status.value(), status, status.getReasonPhrase().toUpperCase(), message.toUpperCase());
        return new ResponseEntity<>(body,status);
    }

    private HttpHeaders getJWTHeader(AuthUser authUser) {
        HttpHeaders httpHeaders =  new HttpHeaders();
        httpHeaders.add(SecurityConstants.JWT_TOKEN_HEADER,jwtTokenProvider.generateJwtToken(authUser));
        return httpHeaders;
    }

    private void authenticate(String username, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username,password));
    }
}
