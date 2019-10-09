package com.I_am_here.Database.Repository;

import com.I_am_here.Database.Entity.Manager;
import com.I_am_here.Database.Entity.Party;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;
import java.util.Set;

public interface PartyRepository extends JpaRepository<Party, Integer> {

    @Query
    Party getByNameAndManager(String name, Manager manager);

    @Query
    Set<Party> getAllByBroadcastWord(String broadcast_word);

    @Query
    Party findByParty(Integer party_id);
}
