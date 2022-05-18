package com.sdProj.demo;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

import com.sdProj.data.Event;

public interface EventRepository extends CrudRepository<Event, Integer>   
{ 
    //Returns player_id and max goals in a single game
    @Query(value = "select player_id, max(count) from (select count(*),player_id,game_id from event where type like 'Goal' group by game_id,player_id) as foo group by player_id", nativeQuery = true)
    public List<Object[]> maxGoalsInGame();

    //Returns player_id, total goals and average goals per game
    @Query(value = "select player_id, count(*),round (cast (CAST (count(*) AS FLOAT) / (select CAST (count(*) AS float) from game_teams where teams_name like min(player.team_name)) as numeric),2)from event inner join player on player.name = event.player_id where type like 'Goal' group by player_id ", nativeQuery = true)
    public List<Object[]> goalsStatsPerPlayer();
} 

//select player_id, count(*),CAST (count(*) AS FLOAT) / (select CAST (count(*) AS float) from game_teams where teams_name like min(player.team_name)) from event inner join player on player.name = event.player_id where type like 'Goal' group by player_id

//select player_id, count(*),CAST (count(*) AS FLOAT) / (select CAST (count(*) AS float) from game_teams where teams_name like 'Everton') from event where type like 'Goal' group by player_id