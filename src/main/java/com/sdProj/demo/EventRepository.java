package com.sdProj.demo;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import com.sdProj.data.Event;

public interface EventRepository extends CrudRepository<Event, Integer>   
{ 
    //Returns player_id and max goals in a single game
    @Query(value = "select player_id, max(count) from (select count(*),player_id,game_id from event where type like 'Goal' and valid = true group by game_id,player_id) as foo group by player_id", nativeQuery = true)
    public List<Object[]> maxGoalsInGame();

    //Returns player_id, total goals and average goals per game
    @Query(value = "select player_id, count(*),round (cast (CAST (count(*) AS FLOAT) / (select CAST (count(*) AS float) from game_teams where teams_name like min(player.team_name)) as numeric),2)from event inner join player on player.name = event.player_id where type like 'Goal' and valid = true group by player_id ", nativeQuery = true)
    public List<Object[]> goalsStatsPerPlayer();

    //Get id of players who received cards in games betweem two teams
    @Query(value = "select  player.team_name,count(*) from event join player on event.player_id = player.name where game_id in  (select games_id from game_teams where teams_name like %?1 and games_id in (select games_id from game_teams where teams_name like %?2)) and color like %?3 and valid is true group by player.team_name", nativeQuery = true)
    public List<List<Object>> getTeamCards(String teamName1, String teamName2, String cardColor);

    //Get events of game with id
    @Query(value = "select id,color,date_trunc('second', time),type,player_id from event where game_id = ?1 and valid is true", nativeQuery = true)
    public List<List<Object>> getEventsByGameId(int gameId);


    //Insert yellow card to a given player in a given game by a given user
    @Query(value = "update event set color='red' where count(select color from event where game_id=%?1 and player_id=%?2 and color='yellow')>=2", nativeQuery=true)
    public List<List<Object>> insertYellowCard(int gameId, String player);

    @Query(value="select * from event join game on event.game_id = game.id where (game.game_date BETWEEN NOW() - INTERVAL '2 HOURS' AND NOW()) AND valid = false order by event.time", nativeQuery = true)
    public List<Event> selectFalseEvents();
    
    @Modifying
    @org.springframework.transaction.annotation.Transactional
    @Query(value="update event set valid = true where id= ?1", nativeQuery = true)
    public void validateEvents(int id);
    

} 

//select player_id, count(*),CAST (count(*) AS FLOAT) / (select CAST (count(*) AS float) from game_teams where teams_name like min(player.team_name)) from event inner join player on player.name = event.player_id where type like 'Goal' group by player_id

//select player_id, count(*),CAST (count(*) AS FLOAT) / (select CAST (count(*) AS float) from game_teams where teams_name like 'Everton') from event where type like 'Goal' group by player_id