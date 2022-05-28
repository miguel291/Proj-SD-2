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

    //Get id of players who received cards in games betweem two teams
    @Query(value = "select count(*) from event join player on event.player_id = player.name where game_id in  (select games_id from game_teams where teams_name like %?1 and games_id in (select games_id from game_teams where teams_name like %?2)) and color like %?3 and valid is true group by player.team_name", nativeQuery = true)
    public List<Integer> getTeamCards(String teamName1, String teamName2, String cardColor);

    //Get events of game with id    
    @Query(value = "select * from event where game_id = ?1 and valid is true", nativeQuery = true)
    public List<List<Object>> getEventsByGameId(int gameId);


    //Insert yellow card to a given player in a given game by a given user
    //@Query(value = "insert into event(color,time,type,valid,game_id,player_id,user_id) values((case when count(select color from event where game_id=%?1 and player_id=%?2 and color='yellow')>=2 then 'red' else 'yellow')),current_timestamp,%?1,%?2,%?3,%?4,%?5) ", nativeQuery=true)
    //public Event insertYellowCard(String type, boolean valid, int gameId, String playerId, String user);
} 

//select player_id, count(*),CAST (count(*) AS FLOAT) / (select CAST (count(*) AS float) from game_teams where teams_name like min(player.team_name)) from event inner join player on player.name = event.player_id where type like 'Goal' group by player_id

//select player_id, count(*),CAST (count(*) AS FLOAT) / (select CAST (count(*) AS float) from game_teams where teams_name like 'Everton') from event where type like 'Goal' group by player_id