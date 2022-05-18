package com.sdProj.demo;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

import com.sdProj.data.Game;

public interface GameRepository extends CrudRepository<Game, Integer>   
{ 
    //Get id of games between two teams
    @Query(value = "select games_id from game_teams where teams_name like %?1 and games_id in (select games_id from game_teams where teams_name like %?2), nativeQuery = true)", nativeQuery = true)
    public List<Integer> getGamesIds(String teamName1, String teamName2);

    //Get goals and location of games between two teams
    @Query(value = "select home_goals,away_goals,location from game where id in (select games_id from game_teams where teams_name like %?1 and games_id in (select games_id from game_teams where teams_name like %?2)), nativeQuery = true)", nativeQuery = true)
    public List<Object> getGoalsAndLocation(String teamName1, String teamName2);

    //Get id of players who received cards in games betweem two teams
    @Query(value = "select player_id from event where game_id in  (select games_id from game_teams where teams_name like %?1 and games_id in (select games_id from game_teams where teams_name like %?2)) and color like %?3", nativeQuery = true)
    public List<Integer> getTeamCards(String teamName1, String teamName2, String cardColor);
} 