package com.sdProj.demo;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

import com.sdProj.data.Game;

public interface GameRepository extends CrudRepository<Game, Integer>   
{ 
    //Get id of games between two teams
    @Query(value = "select games_id from game_teams where teams_name like %?1 and games_id in (select games_id from game_teams where teams_name like %?2)", nativeQuery = true)
    public List<Integer> getGamesIds(String teamName1, String teamName2);

    //Get goals and location of games between two teams
    @Query(value = "select home_goals,away_goals,location from game where id in (select games_id from game_teams where teams_name like %?1 and games_id in (select games_id from game_teams where teams_name like %?2))", nativeQuery = true)
    public List<List<Object>> getGoalsAndLocation(String teamName1, String teamName2);

    //Get id of players who received cards in games betweem two teams
    @Query(value = "select count(*) from event join player on event.player_id = player.name where game_id in  (select games_id from game_teams where teams_name like %?1 and games_id in (select games_id from game_teams where teams_name like %?2)) and color like %?3 and valid is true group by player.team_name", nativeQuery = true)
    public List<Integer> getTeamCards(String teamName1, String teamName2, String cardColor);
} 
//select count(*) from event join player on event.player_id = player.name where game_id in  (select games_id from game_teams where teams_name like %?1 and games_id in (select games_id from game_teams where teams_name like %?2)) and color like %?3 and valid is true group by player.team_name
// select player_id from event where game_id in  (select games_id from game_teams where teams_name like %?1 and games_id in (select games_id from game_teams where teams_name like %?2)) and color like %?3

/*
select id,game_teams.teams_name,home_goals,away_goals,game_date::timestamp(0),location from game join game_teams on game.id = game_teams.games_id where game_date>= NOW() - '2 hour'::INTERVAL
select * from event where game_id = 690


select id,game_teams.teams_name,home_goals,away_goals,game_date::timestamp(0),location from game join game_teams on game.id = game_teams.games_id where game_date>= NOW() - '2 hour'::INTERVAL
select * from event where game_id = 690
select * from game
select * from game_teams
insert into game values (690,0,current_timestamp,3,'Anfield','TBD')
insert into game_teams values (690,'Everton');
insert into game_teams values (690,'Liverpool')
*/