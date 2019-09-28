package com.I_am_here.Database.Repository;

import com.I_am_here.Database.Entity.Host;
import com.I_am_here.Database.Entity.Manager;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface HostRepository extends JpaRepository<Host, Integer> {
    @Query
    Host findByUUIDAndPassword(String UUID, String password);
    @Query
    Host getByUUIDAndPassword(String UUID, String password);
}
