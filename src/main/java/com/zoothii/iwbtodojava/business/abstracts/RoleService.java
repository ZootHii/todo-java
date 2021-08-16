package com.zoothii.iwbtodojava.business.abstracts;

import com.zoothii.iwbtodojava.core.entities.Role;
import com.zoothii.iwbtodojava.core.utulities.results.DataResult;
import com.zoothii.iwbtodojava.core.utulities.results.Result;

import java.util.List;

public interface RoleService {

    DataResult<List<Role>> getRoles() throws InterruptedException;

    Result createRole(Role role);

    Result deleteRole(Long roleId);

    DataResult<Role> getRoleByName(String name);

    Result checkIfRoleExistsByName(String role);

    Result createDefaultRoleIfNotExists(String defaultRole);

}
