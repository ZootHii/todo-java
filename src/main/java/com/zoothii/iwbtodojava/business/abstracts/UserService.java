package com.zoothii.iwbtodojava.business.abstracts;

import com.zoothii.iwbtodojava.core.entities.Role;
import com.zoothii.iwbtodojava.core.entities.User;
import com.zoothii.iwbtodojava.core.utulities.results.DataResult;
import com.zoothii.iwbtodojava.core.utulities.results.Result;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.Set;

public interface UserService {
    Result createUser(User user);

    DataResult<List<User>> getUsers();

    DataResult<User> getUserByUsername(String username);

    //Result setRolesToUser(String username, Set<Role> roles);

    Result deleteUser(Long userId);

    //Result setNewRolesToUser(String username, Set<Role> roles);

    Result checkIfUsernameExists(String username);

    Result checkIfEmailExists(String email);

}