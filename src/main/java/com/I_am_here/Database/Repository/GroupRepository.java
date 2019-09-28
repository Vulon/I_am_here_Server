package com.I_am_here.Database.Repository;

import com.I_am_here.Database.Entity.Party;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupRepository extends JpaRepository<Party, Integer> {

}
