package com.zoothii.iwbtodojava.core.data_access;

import com.zoothii.iwbtodojava.core.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleDao extends JpaRepository<Role, Long> {
    Role getRoleByName(String name);
}
