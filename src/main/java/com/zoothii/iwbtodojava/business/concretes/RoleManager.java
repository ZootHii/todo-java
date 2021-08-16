package com.zoothii.iwbtodojava.business.concretes;

import com.zoothii.iwbtodojava.business.abstracts.RoleService;
import com.zoothii.iwbtodojava.core.data_access.RoleDao;
import com.zoothii.iwbtodojava.core.entities.Role;
import com.zoothii.iwbtodojava.core.utulities.constants.Messages;
import com.zoothii.iwbtodojava.core.utulities.results.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@CacheConfig(cacheNames = {"roles"})
public class RoleManager implements RoleService {

    private final RoleDao roleDao;

    public RoleManager(RoleDao roleDao) {
        this.roleDao = roleDao;
    }

    @Override
    @Cacheable(value = "ten-seconds-cache", key = "'roles-cache'") // ten seconds cache works EhCache
    public DataResult<List<Role>> getRoles() throws InterruptedException {
        Thread.sleep(1000);
        return new SuccessDataResult<>(roleDao.findAll(), Messages.successGetRoles);
    }

    @Override
    @PreAuthorize("hasAnyRole('BEN','ADMIN')")
    @CacheEvict(value = "ten-seconds-cache", key = "'roles-cache'", condition = "#result.success")
    // result.success condition works
    public Result createRole(Role role) {
        var resultRoleExists = checkIfRoleExistsByName(role.getName());
        if (resultRoleExists.isSuccess()) {
            return new ErrorResult(resultRoleExists.getMessage());
        }

        roleDao.save(role);
        return new SuccessResult("Role " + role.getName() + " is successfully created.");
    }

    @Override
    @PreAuthorize("hasAnyRole('BEN','ADMIN')")
    @CacheEvict(value = "ten-seconds-cache", key = "'roles-cache'", condition = "#result.success")
    public Result deleteRole(Long roleId) {
        var roleToDelete = roleDao.findById(roleId);
        if (roleToDelete.isEmpty()) {
            return new ErrorResult("roles is not exists");
        }

        roleDao.deleteById(roleId);
        return new SuccessResult("Role " + roleToDelete.get().getName() + " is successfully deleted.");
    }

    @Override
    public DataResult<Role> getRoleByName(String name) {
        var resultRoleExists = checkIfRoleExistsByName(name);
        if (!resultRoleExists.isSuccess()) {
            return new ErrorDataResult<>(resultRoleExists.getMessage());
        }

        return new SuccessDataResult<>(roleDao.getRoleByName(name), Messages.successGetRoleByName);
    }

    /*** Business Rules ***/
    @Override
    public Result checkIfRoleExistsByName(String role) {
        if (roleDao.getRoleByName(role) == null) {
            return new ErrorResult(Messages.errorCheckIfRoleExists(role));
        }
        return new SuccessResult(Messages.successCheckIfRoleExists(role));
    }

    @Override
    public Result createDefaultRoleIfNotExists(String defaultRole) {
        if (roleDao.getRoleByName(defaultRole) != null) {
            return new ErrorResult(Messages.errorCreateDefaultRoleIfNotExists(defaultRole));
        }

        roleDao.save(new Role(0L, defaultRole));
        return new SuccessResult(Messages.successCreateDefaultRoleIfNotExists(defaultRole));
    }
}
