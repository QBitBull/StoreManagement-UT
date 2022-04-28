package com.sfinance.SFBackend.Service.ServiceImplementation;

import com.sfinance.SFBackend.Entity.TypeAccount.Role;
import com.sfinance.SFBackend.Entity.User;
import com.sfinance.SFBackend.Exceptions.UserExceptions.EmailExistException;
import com.sfinance.SFBackend.Exceptions.UserExceptions.UserNotFoundException;
import com.sfinance.SFBackend.Exceptions.UserExceptions.UsernameExistException;
import com.sfinance.SFBackend.Repository.UserRepositoryInterface;
import com.sfinance.SFBackend.Security.BruteForceAttack.LoginAttemptService;
import com.sfinance.SFBackend.Security.Registration.AuthUser;
import com.sfinance.SFBackend.Service.UserService;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.transaction.Transactional;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;

import static com.sfinance.SFBackend.Constants.FileConstant.*;
import static com.sfinance.SFBackend.Constants.UserConstants.*;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

@Service
@Transactional
@Qualifier("UserDetailsService")

public class UserServiceImplementation implements UserService, UserDetailsService {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    private final UserRepositoryInterface userRepositoryInterface;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final LoginAttemptService loginAttemptService;

    @Autowired
    public UserServiceImplementation(UserRepositoryInterface userRepositoryInterface, BCryptPasswordEncoder bCryptPasswordEncoder, LoginAttemptService loginAttemptService) {
        this.userRepositoryInterface = userRepositoryInterface;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.loginAttemptService = loginAttemptService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepositoryInterface.findUserByUsername(username);
        if (user == null){
            LOGGER.error(NO_USER_FOUND_BY_USERNAME + username);
            throw new UsernameNotFoundException(NO_USER_FOUND_BY_USERNAME + username);
        }else {
            validateLoginAttempt(user);
            user.setLastLoginDateDisplay(user.getLastLoginDate());
            user.setLastLoginDate(new Date());
            userRepositoryInterface.save(user);
            AuthUser authUser = new AuthUser(user);
            LOGGER.info(FOUND_USER_BY_USERNAME + username);
            return authUser;
        }
    }

    @Override
    public User register(String firstName, String lastName, String username, String email) throws UserNotFoundException, EmailExistException, UsernameExistException {
        validateNewUsernameAndEmail(StringUtils.EMPTY,username,email);
        User user = new User();
        user.setUserID(generateUserId());
        String password = generatePassword();
        String encodedPassword = encodePassword(password);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setUsername(username);
        user.setEmail(email);
        user.setJoinDate(new Date());
        user.setPassword(encodedPassword);
        user.setActive(true);
        user.setNotLocked(true);
        user.setRole(Role.ROLE_USER.name());
        user.setAuthorities(Role.ROLE_USER.getAuthorities());
        user.setProfileImageURL(getTemporaryProfileImageURL(username));
        userRepositoryInterface.save(user);
        LOGGER.info("New user password: " + password);
        return user;
    }

    @Override
    public List<User> getUsers() {
        return userRepositoryInterface.findAll();
    }

    @Override
    public User findUserByUsername(String username) {
        return userRepositoryInterface.findUserByUsername(username);
    }

    @Override
    public User findUserByEmail(String email) {
        return userRepositoryInterface.findUserByEmail(email);
    }

    @Override
    public User addNewUser(String firstName, String lastName, String username, String email, String role, boolean isNotLocked, boolean isActive, MultipartFile profileImage) throws UserNotFoundException, EmailExistException, UsernameExistException, IOException {
        validateNewUsernameAndEmail(StringUtils.EMPTY,username,email);
        User user = new User();

        String password = generatePassword();
        String encodedPassword = encodePassword(password);

        user.setUserID(generateUserId());
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setUsername(username);
        user.setEmail(email);
        user.setJoinDate(new Date());
        user.setPassword(encodedPassword);
        user.setNotLocked(isNotLocked);
        user.setActive(isActive);
        user.setRole(getRoleEnumName(role).name());
        user.setAuthorities(getRoleEnumName(role).getAuthorities());
        user.setProfileImageURL(getTemporaryProfileImageURL(username));

        userRepositoryInterface.save(user);
        saveProfileImage(user, profileImage);
        LOGGER.info("New user password: " + password);
        return user;
    }

    @Override
    public User updateUser(String currentUsername, String newFirstName, String newLastName, String newUsername, String newEmail, String role, boolean isNotLocked, boolean isActive, MultipartFile profileImage) throws UserNotFoundException, EmailExistException, UsernameExistException, IOException {

        User currentUser = validateNewUsernameAndEmail(currentUsername,newUsername,newEmail);
        assert currentUser != null;
        currentUser.setFirstName(newFirstName);
        currentUser.setLastName(newLastName);
        currentUser.setUsername(newUsername);
        currentUser.setEmail(newEmail);
        currentUser.setNotLocked(isNotLocked);
        currentUser.setActive(isActive);
        currentUser.setRole(getRoleEnumName(role).name());
        currentUser.setAuthorities(getRoleEnumName(role).getAuthorities());
        userRepositoryInterface.save(currentUser);
        saveProfileImage(currentUser, profileImage);

        return currentUser;
    }

    @Override
    public void deleteUser(String username) {
        userRepositoryInterface.deleteUserByUsername(username);
    }

    @Override
    public void resetPassword(String username) {
        User user = userRepositoryInterface.findUserByUsername(username);
        if (user == null){
            throw new UsernameNotFoundException(NO_USER_FOUND_BY_USERNAME);
        }else{
            String password = generatePassword();
            user.setPassword(encodePassword(password));
            userRepositoryInterface.save(user);
            LOGGER.info("New user password: " + password);
        }
    }

    @Override
    public User updateProfileImage(String username, MultipartFile profileImage) throws UserNotFoundException, EmailExistException, UsernameExistException, IOException {
        User user = validateNewUsernameAndEmail(username, null, null);
        saveProfileImage(user, profileImage);
        return user;
    }

    private String getTemporaryProfileImageURL(String username) {
        return ServletUriComponentsBuilder.fromCurrentContextPath().path(DEFAULT_USER_IMAGE_PATH + username).toUriString();
    }

    private String encodePassword(String password) {
        return bCryptPasswordEncoder.encode(password);
    }

    private String generatePassword() {
        return RandomStringUtils.randomAlphanumeric(10);
    }

    private String generateUserId() {
        return RandomStringUtils.randomNumeric(10);
    }

    private User validateNewUsernameAndEmail(String currentUsername, String newUsername, String newEmail) throws UsernameExistException, UserNotFoundException, EmailExistException {

        User userByNewUsername = findUserByUsername(newUsername);
        User userByNewEmail = findUserByEmail(newEmail);

        if(StringUtils.isNotBlank(currentUsername)){
            User currentUser = findUserByUsername(currentUsername);
            if(currentUser == null){
                throw new UserNotFoundException(NO_USER_FOUND_BY_USERNAME + " " + currentUsername);
            }
            if(userByNewUsername != null && !currentUser.getId().equals(userByNewUsername.getId())){
                throw  new UsernameExistException(USERNAME_ALREADY_EXISTS);
            }
            if(userByNewEmail != null && !currentUser.getId().equals(userByNewEmail.getId())){
                throw  new EmailExistException(EMAIL_ALREADY_EXISTS);
            }
            return currentUser;
        }else{
            if(userByNewUsername != null){
                throw  new UsernameExistException(USERNAME_ALREADY_EXISTS);
            }
            if(userByNewEmail != null){
                throw  new EmailExistException(EMAIL_ALREADY_EXISTS);
            }
            return null;
        }
    }

    private void validateLoginAttempt(User user) {
        if(user.isNotLocked()){
            user.setNotLocked(!loginAttemptService.hasExceededMaxAttempt(user.getUsername()));
        }else{
            loginAttemptService.evictUserFromLoginAttemptCache(user.getUsername());
        }
    }

    private void saveProfileImage(User user, MultipartFile profileImage) throws IOException {
        if(profileImage != null){
            Path userFolder = Paths.get(USER_FOLDER + user.getUsername()).toAbsolutePath().normalize();
            if(!Files.exists(userFolder)){
                Files.createDirectories(userFolder);
                LOGGER.info(DIRECTORY_CREATED + userFolder);
            }
            Files.deleteIfExists(Paths.get(userFolder + user.getUsername() + DOT + JPG_EXTENSION));
            Files.copy(profileImage.getInputStream(),userFolder.resolve(user.getUsername()+ DOT + JPG_EXTENSION), REPLACE_EXISTING);
            user.setProfileImageURL(setProfileImageURL(user.getUsername()));
            userRepositoryInterface.save(user);
            LOGGER.info(FILE_SAVED_IN_FILE_SYSTEM + profileImage.getOriginalFilename());
        }
    }

    private String setProfileImageURL(String username) {
        return ServletUriComponentsBuilder.fromCurrentContextPath().path(USER_IMAGE_PATH + username + FORWARD_SLASH + username + DOT + JPG_EXTENSION).toUriString();
    }

    private Role getRoleEnumName(String role) {
        return Role.valueOf(role.toUpperCase());
    }
}
