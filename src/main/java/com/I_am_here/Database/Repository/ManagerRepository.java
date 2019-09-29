package com.I_am_here.Database.Repository;

import com.I_am_here.Database.Entity.Manager;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ManagerRepository extends JpaRepository<Manager, Integer> {

    @Query
    Manager findByUuidAndPassword(String UUID, String password);
    @Query
    Manager getByUuidAndPassword(String UUID, String password);
}
