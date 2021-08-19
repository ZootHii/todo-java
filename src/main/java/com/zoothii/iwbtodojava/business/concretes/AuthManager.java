package com.zoothii.iwbtodojava.business.concretes;

import com.zoothii.iwbtodojava.business.abstracts.AuthService;
import com.zoothii.iwbtodojava.business.abstracts.RoleService;
import com.zoothii.iwbtodojava.business.abstracts.UserService;
import com.zoothii.iwbtodojava.core.entities.Role;
import com.zoothii.iwbtodojava.core.entities.User;
import com.zoothii.iwbtodojava.core.utulities.constants.Messages;
import com.zoothii.iwbtodojava.core.utulities.constants.Roles;
import com.zoothii.iwbtodojava.core.utulities.results.*;
import com.zoothii.iwbtodojava.core.utulities.security.token.AccessToken;
import com.zoothii.iwbtodojava.core.utulities.security.token.jwt.JwtUtils;
import com.zoothii.iwbtodojava.entities.concretes.payload.request.LoginRequest;
import com.zoothii.iwbtodojava.entities.concretes.payload.request.RegisterRequest;
import com.zoothii.iwbtodojava.entities.concretes.payload.response.AuthResponse;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthManager implements AuthService {

    final private AuthenticationManager authenticationManager;
    final private UserService userService;
    final private RoleService roleService;
    final private PasswordEncoder passwordEncoder;
    final private JwtUtils jwtUtils;

    @Lazy
    public AuthManager(AuthenticationManager authenticationManager, UserService userService, RoleService roleService, PasswordEncoder passwordEncoder, JwtUtils jwtUtils) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
    }

    @Override
    @Transactional
    public DataResult<AuthResponse> register(RegisterRequest registerRequest) {

        var resultUsernameExists = userService.checkIfUsernameExists(registerRequest.getUsername());
        if (resultUsernameExists.isSuccess()) {
            return new ErrorDataResult<>(resultUsernameExists.getMessage());
        }
        var resultEmailExists = userService.checkIfEmailExists(registerRequest.getEmail());
        if (resultEmailExists.isSuccess()) {
            return new ErrorDataResult<>(resultEmailExists.getMessage());
        }

        var requestedRolesString = registerRequest.getRoles();
        var resultSetRequestedRolesStringToRole = setRequestedRolesStringToRole(requestedRolesString);
        if (!resultSetRequestedRolesStringToRole.isSuccess()) {
            return new ErrorDataResult<>(resultSetRequestedRolesStringToRole.getMessage());
        }

        // Create new user's account and hash password set requested roles and save
        var rolesToSet = resultSetRequestedRolesStringToRole.getData();
        var usernameToSet = registerRequest.getUsername();
        var emailToSet = registerRequest.getEmail();
        var passwordToSet = passwordEncoder.encode(registerRequest.getPassword());
        User user = new User(usernameToSet, emailToSet, passwordToSet, rolesToSet);

        userService.createUser(user);
        return new SuccessDataResult<>(generateJwtResponseUsernamePassword(registerRequest.getUsername(), registerRequest.getPassword()), Messages.successRegister);
    }

    private void addDefaultRole(Set<Role> roles) {
        roleService.createDefaultRoleIfNotExists(Roles.ROLE_USER);
        roles.add(roleService.getRoleByName(Roles.ROLE_USER).getData());
    }

    @Override
    @Transactional
    public DataResult<AuthResponse> login(LoginRequest loginRequest) {
        // check username exist from service
        var resultUserNameExists = userService.checkIfUsernameExists(loginRequest.getUsername());
        if (!resultUserNameExists.isSuccess()) {
            return new ErrorDataResult<>(resultUserNameExists.getMessage());
        }

        // check password true for the user
        var resultDataUser = userService.getUserByUsername(loginRequest.getUsername());
        var resultPassword = checkIfPasswordCorrect(loginRequest.getPassword(), resultDataUser.getData().getPassword());
        if (!resultPassword.isSuccess()) {
            return new ErrorDataResult<>(resultPassword.getMessage());
        }

        return new SuccessDataResult<>(generateJwtResponseUsernamePassword(loginRequest.getUsername(), loginRequest.getPassword()), Messages.successLogin);
    }

    @Override
    public Result checkIfPasswordCorrect(String requestedPassword, String encryptedPassword) {
        if (!passwordEncoder.matches(requestedPassword, encryptedPassword)) {
            return new ErrorResult(Messages.errorPassword);
        }

        return new SuccessResult(Messages.successPassword);
    }

    @Override
    public DataResult<User> getAuthenticatedUserDetails() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User userDetails = (User) authentication.getPrincipal();
            return new SuccessDataResult<>(userDetails, Messages.successGetAuthenticatedUserDetails);
        } catch (ClassCastException classCastException) {
            return new ErrorDataResult<>(Messages.errorGetAuthenticatedUserDetails);
        }
    }

    // set requested roles checking from database for security
    private DataResult<Set<Role>> setRequestedRolesStringToRole(Set<String> requestedRolesString) {
        Set<Role> requestedRoles = new HashSet<>();

        if (requestedRolesString == null) {
            addDefaultRole(requestedRoles);
        } else {
            // if default user role not requested add
            if (!requestedRolesString.contains(Roles.ROLE_USER)) {
                addDefaultRole(requestedRoles);
            }

            for (String role : requestedRolesString) {

                // if admin requested and not exists create
                if (Objects.equals(role, Roles.ROLE_ADMIN)) {
                    roleService.createDefaultRoleIfNotExists(role);
                }

                var resultRoleExistsByName = roleService.checkIfRoleExistsByName(role);
                if (!resultRoleExistsByName.isSuccess()) {
                    return new ErrorDataResult<>(resultRoleExistsByName.getMessage());
                }
                var roleToAdd = roleService.getRoleByName(role).getData();
                requestedRoles.add(roleToAdd);
            }
        }

        return new SuccessDataResult<>(requestedRoles, Messages.successSetRequestedRolesStringToRole);
    }

    // authenticate user and return jwt as response
    private AuthResponse generateJwtResponseUsernamePassword(String username, String password) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        AccessToken accessToken = jwtUtils.createAccessToken(SecurityContextHolder.getContext().getAuthentication());

        User userDetails = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());

        return new AuthResponse(accessToken, userDetails.getId(), userDetails.getUsername(), userDetails.getEmail(), roles);
    }
}