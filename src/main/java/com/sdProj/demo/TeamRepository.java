package com.sdProj.demo;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import com.sdProj.data.Team;

public interface TeamRepository extends CrudRepository<Team, String>   {
    @Query("select s from Team s where s.name like %?1")
    public Team findByName(String name); 

    //@Query("select count(id) from Game g where g.id in (select name from Team g where g.name like ?1) and winner = ?1")
    @Query(value = "select count(id) from game where id in (select games_id from game_teams where teams_name like %?1) and winner like %?1", nativeQuery = true)
    public int getWins(String name); 

    //Victories, Defeats, Played Matches
    @Query(value = "select stat from( select 1 OrderNo,count(id) as stat from game where id in (select games_id from game_teams where teams_name like %?1) and winner like %?1 UNION select 2 OrderNo,count(id) as stat from game where id in (select games_id from game_teams where teams_name like %?1) and winner not like %?1 and winner not like 'Draw' and winner not like 'TBD' UNION select 3 OrderNo,count(id) as stat from game where id in (select games_id from game_teams where teams_name like %?1) ) as foo order by OrderNo", nativeQuery = true)
    public List<Integer> getTeamResults(String name);

} 
