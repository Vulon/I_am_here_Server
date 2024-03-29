package com.I_am_here.Database.Repository;

import com.I_am_here.Database.Entity.Host;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface HostRepository extends JpaRepository<Host, Integer> {
    @Query
    Host findByUuidAndPassword(String UUID, String password);
    @Query
    Host getByUuidAndPassword(String UUID, String password);

    @Query
    Host getByUuid(String UUID);

    @Query
    Host getByHostId(Integer id);


}
