package com.zoothii.iwbtodojava.business.concretes;


import com.zoothii.iwbtodojava.business.abstracts.RoleService;
import com.zoothii.iwbtodojava.business.abstracts.TodoService;
import com.zoothii.iwbtodojava.business.abstracts.UserService;
import com.zoothii.iwbtodojava.core.data_access.UserDao;
import com.zoothii.iwbtodojava.core.entities.Role;
import com.zoothii.iwbtodojava.core.entities.User;
import com.zoothii.iwbtodojava.core.utulities.constants.Roles;
import com.zoothii.iwbtodojava.core.utulities.results.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@CacheConfig(cacheNames = {"users"})
public class UserManager implements UserService, UserDetailsService {

    private final UserDao userDao;
    private final RoleService roleService;
    private final TodoService todoService;

    public UserManager(UserDao userDao, RoleService roleService, TodoService todoService) {
        this.userDao = userDao;
        this.roleService = roleService;
        this.todoService = todoService;
    }

    @Override
    @Cacheable(value = "ten-seconds-cache", key = "'users-cache'")
    public DataResult<List<User>> getUsers() {
        return new SuccessDataResult<>(this.userDao.findAll());
    }

    @Override
    @CacheEvict(value = "ten-seconds-cache", key = "'users-cache'", condition = "#result.success")
    public Result createUser(User user) {
        userDao.save(user);
        return new SuccessResult("User created successfully.");
    }

    @Override
    @CacheEvict(value = "ten-seconds-cache", key = "'users-cache'", condition = "#result.success")
    @Transactional
    //@PreAuthorize("hasAnyRole('ADMIN,BEN')")
    public Result deleteUser(Long userId) {
        if (!userDao.existsById(userId)) {
            return new ErrorResult("user not found");
        }

        // delete deleted users todos
        todoService.deleteTodosByUserId(userId);

        userDao.deleteById(userId);
        return new SuccessResult("User is successfully deleted.");
    }

//    @Override
//    @CacheEvict(value = "ten-seconds-cache", key = "'users-cache'", condition = "#result.success")
//    @PreAuthorize("hasAnyRole('ADMIN,BEN')")
//    public Result setNewRolesToUser(String username, Set<Role> roles) {
//        // TODO change set<String> accordingly
//        for (Role role : roles) {
//            var resultCheckIfRoleExists = roleService.checkIfRoleExistsByName(role.getName());
//            if (!resultCheckIfRoleExists.isSuccess()) {
//                return new ErrorResult(resultCheckIfRoleExists.getMessage());
//            }
//        }
//
//        var user = this.userDao.getUserByUsername(username);
//        var userRoles = user.getRoles();
//        userRoles.addAll(roles);
//        user.setRoles(userRoles);
//        userDao.save(user);
//        return new SuccessResult("new roles set to user");
//    }
//
//    @Override
//    @CacheEvict(value = "ten-seconds-cache", key = "'users-cache'", condition = "#result.success")
//    @PreAuthorize("hasAnyRole('ADMIN,BEN')")
//    public Result setRolesToUser(String username, Set<Role> roles) {
//        var user = this.userDao.getUserByUsername(username);
//
//        Set<String> strRoles = new HashSet<>();
//
//        for (Role role : roles) {
//            var resultCheckIfRoleExists = roleService.checkIfRoleExistsByName(role.getName());
//            if (resultCheckIfRoleExists.isSuccess()) {
//                strRoles.add(role.getName());
//            } else {
//                return new ErrorResult(resultCheckIfRoleExists.getMessage());
//            }
//        }
//
//        if (!strRoles.contains(Roles.ROLE_USER)) {
//            var resultDefaultRole = roleService.getRoleByName(Roles.ROLE_USER);
//            roles.add(resultDefaultRole.getData());
//        }
//
//        user.setRoles(roles);
//        userDao.save(user);
//        return new SuccessResult("roles set to user");
//    }

    @Override
    public DataResult<User> getUserByUsername(String username) {
        Result resultUsernameExists = checkIfUsernameExists(username);
        if (!resultUsernameExists.isSuccess()) {
            return new ErrorDataResult<>(resultUsernameExists.getMessage());
        }

        User user = this.userDao.getUserByUsername(username);
        return new SuccessDataResult<>(user);
    }

    /*** Business Rules ***/
    @Override
    public Result checkIfUsernameExists(String username) {
        var user = this.userDao.getUserByUsername(username);
        if (user == null) {
            return new ErrorResult("Username is not exists.");
        }
        return new SuccessResult("Username is exists.");
    }

    @Override
    public Result checkIfEmailExists(String email) {
        User user = this.userDao.getUserByEmail(email);
        if (user == null) {
            return new ErrorResult("Email is not exists.");
        }
        return new SuccessResult("Email is exists.");
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userDao.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));

        return User.build(user);
    }
}
