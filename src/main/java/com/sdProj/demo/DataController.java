package com.sdProj.demo;

import java.math.BigInteger;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import com.sdProj.data.Event;
import com.sdProj.data.Team;
import com.sdProj.data.Player;
import com.sdProj.data.User;
import com.sdProj.data.Game;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

@Controller
public class DataController {

    HttpResponse <JsonNode> response;
    HttpResponse <JsonNode> response2;
    HttpResponse <JsonNode> response3;

    @Autowired
    GameService gameService;

    @Autowired
    PlayerService playerService;

    @Autowired
    TeamService teamService;

    @Autowired
    EventService eventService;

    @Autowired
    UserService userService;
    
    @PostMapping("/login")
    public String login (@ModelAttribute User u) {
        System.out.println("login request: " + u);
        Optional<User> authenticated = userService.authenticate(u.getUsername(), u.getPassword());
        if(authenticated.isPresent()){
            int role = userService.getRole(u.getUsername());
            setrole(role);
            return "redirect:/home";
        }else{
            return "error_page";
        }
    }
    @GetMapping("/login")
    public String getLoginPage(Model model) {
        model.addAttribute("loginRequest", new User());
        return "login";
    }
    
    @GetMapping("/home")
    public String home() {
        return "home";
    }


    @GetMapping("/showPlayers")
    public String getPlayers(Model model) {
        model.addAttribute("players", this.playerService.getAllPlayers());
        return "showPlayers";
    }

    @GetMapping("/showTeams")
    public String getTeams(Model model) {
        model.addAttribute("teams", this.teamService.getAllTeams());
        return "showTeams";
    }

    @GetMapping("/")
    public String redirect() {
        return "redirect:/home";
    }

    @GetMapping("/createData")
    public String createData() {
        return "createData";
        
    }

	@PostMapping("/saveData")
	public String saveData(Model model) {
        // Host url
        String host = "https://v3.football.api-sports.io";
        //Type here your key
        String x_apisports_key = "3b93b6832c51c181af7574e12124c4f1";
        // Params for a request
        String s = "league=39&season=2021";
        
        // Request
        try {
            //Get teams and players from api
            response = Unirest.get(host + "/teams?" + s)
                .header("x-apisports-key", x_apisports_key)
                .asJson();
            //Get players from api
            response2 = Unirest.get(host+ "/players?" + s)
                .header("x-apisports-key", x_apisports_key)
                .asJson();

            //Get games from api
            response3 = Unirest.get(host+ "/fixtures?" + s +"&from=2021-08-01&to=2022-05-14")
                .header("x-apisports-key", x_apisports_key)
                .asJson();
        } catch (UnirestException e) {
            System.out.println("Error getting data from API");
        }

        //Parse returned data to team objects   
        JSONObject content = response.getBody().getObject();
        JSONArray r = content.getJSONArray("response");
        for (int i = 0; i < r.length(); i++) {
            //System.out.println(r.getJSONObject(i).getJSONObject("team").getString("name") + " " + r.getJSONObject(i).getJSONObject("team").getString("code") +"\n");
            Team a = new Team(r.getJSONObject(i).getJSONObject("team").getString("name"),r.getJSONObject(i).getJSONObject("team").getString("logo") ,r.getJSONObject(i).getJSONObject("venue").getString("name"));
            this.teamService.addTeam(a);   
        }

        //Parse returned data to player objects
        content = response2.getBody().getObject();
        r = content.getJSONArray("response");
        for (int i = 0; i < r.length(); i++) {
            Team t = teamService.getTeamByName(
                r.getJSONObject(i)
                .getJSONArray("statistics")
                .getJSONObject(0).getJSONObject("team")
                .getString("name")
            );
            if(t == null){
                continue;
            }
            content = r.getJSONObject(i).getJSONObject("player");
            java.sql.Date sqlDateOfBirth;
            try {
                sqlDateOfBirth = java.sql.Date.valueOf(content.getJSONObject("birth").getString("date"));
            } catch (Exception e) {
                //TODO: handle exception
                System.out.println("Player  " + content.getString("name") + " with no date of birth");
                sqlDateOfBirth = null;
            }
            Player a = new Player(
                content.getString("name"),
                content.getString("photo"),
                r.getJSONObject(i).getJSONArray("statistics").getJSONObject(0).getJSONObject("games").getString("position"),
                sqlDateOfBirth, 
                t
            );
            this.playerService.addPlayer(a);   
            //System.out.println(a.getName() + " " + a.getPosition() + " " + a.getTeam().getName() + "\n");
        }

        //Parse returned data to game objects
        content = response3.getBody().getObject();
        r = content.getJSONArray("response");
        for (int i = 0; i < r.length(); i++) {
            Team t1 = teamService.getTeamByName(
                r.getJSONObject(i).getJSONObject("teams").getJSONObject("home").getString("name")
            );
            Team t2 = teamService.getTeamByName(
                r.getJSONObject(i).getJSONObject("teams").getJSONObject("away").getString("name")
            );
            if(t1 == null || t2 == null){
                continue;
            }
            String winner;
            if(r.getJSONObject(i).getJSONObject("goals").getInt("home") < r.getJSONObject(i).getJSONObject("goals").getInt("away"))
                winner = t2.getName();
            else if(r.getJSONObject(i).getJSONObject("goals").getInt("home") > r.getJSONObject(i).getJSONObject("goals").getInt("away"))
                winner = t1.getName();
            else
                winner = "Draw";
             
            Instant instant = Instant.parse( r.getJSONObject(i).getJSONObject("fixture").getString("date"));
            java.sql.Timestamp sqlDate = java.sql.Timestamp.from(instant);
            Game a = new Game(
                r.getJSONObject(i).getJSONObject("fixture").getJSONObject("venue").getString("name"),
                sqlDate,
                winner,
                t1,
                t2,
                r.getJSONObject(i).getJSONObject("goals").getInt("home"),
                r.getJSONObject(i).getJSONObject("goals").getInt("away"));
            this.gameService.addGame(a);   
        }
		return "redirect:/home";
	}
    
  // modelo - eventos
    @GetMapping("/listEvents")
    public String listEvents(Model model) {
        model.addAttribute("events", this.eventService.getAllEvents());
        return "listEvents";
    }

    @GetMapping("/createEvent")
    public String createEvent(Model m) {
        int role = getrole();
        if(role > 0){
            m.addAttribute("event", new Event());
            m.addAttribute("allGames", this.gameService.getGames());
            m.addAttribute("allPlayers", this.playerService.getAllPlayers());
            return "editEvent";
        }else{
            return "nopermission";
        }
    }

    @GetMapping("/editEvent")
    public String editEvent(@RequestParam(name="id", required=true) int id, Model m) {
        Optional<Event> op = this.eventService.getEvent(id);
        if (op.isPresent()) {
            m.addAttribute("event", op.get());
            m.addAttribute("allGames", this.gameService.getGames());
            m.addAttribute("allPlayers", this.playerService.getAllPlayers());
            return "editEvent";
        }
        else {
            return "redirect:/listEvents";
        }
    }    

    @PostMapping("/saveEvent")
    public String saveEvent(@ModelAttribute Event event) {
        this.eventService.addEvent(event);
        return "redirect:/listEvents";
    }

    // Show all events
    @GetMapping("/showEvents")
    public String listAllEvents(Model model) {
        model.addAttribute("events", this.eventService.getAllEvents());
        for(Object o : this.eventService.getAllEvents()){
            System.out.println(o);
        }
        return "listEvents";
    }

    @GetMapping("/admEvents")
    public String listAdmEvents(Model m){
        int role = getrole();
        if(role == 2){
            m.addAttribute("admEvents", this.eventService.selectFalseEvents());
            return "adminEvents";
        }else{
            return "nopermission";
        }
    }

    @PostMapping("/saveAdmEvents")
    public String saveAdmEvents(@ModelAttribute Event e){
        this.eventService.validateEvents(e.getId());
        return "redirect:/home";
    }
/* 
    @GetMapping("/saveAdmEvents")
    public String saveAdmEvents(@RequestParam(name="id", required=true) int id,Model m){
        m.addAttribute("admEvents", this.eventService.validateEvents(id));
        return "saveAdminEvents";
    }
*/

    //Endpoint to show the victories of a team
    @GetMapping("/listTeamStats")
    public String listTeamVictories(Model model) {
        List<List<Integer>> teamResults = new ArrayList<>(); 
        ArrayList<String> teamNames = new ArrayList<>();
        for (Team t : teamService.getAllTeams()){
            teamNames.add(t.getName());
            teamResults.add(this.teamService.teamResults(t));
            //System.out.println(this.teamService.teamResults[](t));
        }
        model.addAttribute("names", teamNames);
        model.addAttribute("results", teamResults);
        
        for(List<Integer> l : teamResults){
            System.out.println(l);
        }
        return "listTeamStats";
    }

    @GetMapping("/teamDuel/{id1}/{id2}")
    public String teamDuel(@PathVariable("id1") String id, @PathVariable("id2") String id2, Model m) {
        Team t = this.teamService.getTeamByName(id);
        Team s = this.teamService.getTeamByName(id2);
        if (t != null && s != null) {
            System.out.println(t.getName() + " vs " + s.getName());
            System.out.println(this.gameService.getGamesIds(t, s));
            System.out.println(this.gameService.getGoalsAndLocation(t, s));
            int[][] results = new int[2][4];
            //home_goals, away_goals, location
            for(List<Object> o : this.gameService.getGoalsAndLocation(t, s)){
                if(o.get(2).equals(t.getStadium())){
                    results[0][3] += (int) o.get(0);
                    results[1][3] += (int) o.get(1);
                    if( (int) o.get(0) > (int) o.get(1)){
                        results[0][0]++;    
                        results[1][1]++;
                    }
                    else if( (int) o.get(0) <(int) o.get(1)){
                        results[0][1]++;
                        results[1][0]++;
                    }
                    else{
                        results[0][2]++;
                        results[1][2]++;
                    }
                }
                else{
                    results[0][3] += (int) o.get(1);
                    results[1][3] += (int) o.get(0);
                    if((int) o.get(1) > (int) o.get(0)){
                        results[0][0]++;
                        results[1][1]++;
                    }
                    else if((int) o.get(1) < (int) o.get(0)){
                        results[0][1]++;
                        results[1][0]++;
                    }
                    else{
                        results[0][2]++;
                        results[1][2]++;
                    }
                }
            }
            List<String> teamNames = new ArrayList<>();
            teamNames.add(t.getName());
            teamNames.add(s.getName());
            int[] red = {0,0}; 
            int [] yellow = {0,0};
            List<List<Object>> yellowCards = this.eventService.getCountCards(t, s,"Yellow");
            for (List<Object> o : yellowCards){
                System.out.println("Yellow: " + o.get(0) + " " + o.get(1));
                if(o.get(0).equals(t.getName())){
                    yellow[0] += ((BigInteger) o.get(1)).intValue();
                }
                else if (o.get(0).equals(s.getName())){
                    yellow[1] += ((BigInteger) o.get(1)).intValue();
                }
            }
            List<List<Object>> redCards = this.eventService.getCountCards(t, s,"Red");
            for (List<Object> o : redCards){
                System.out.println("Red: " + o.get(0) + " " + o.get(1));
                if(o.get(0).equals(t.getName())){
                    red[0] += ((BigInteger) o.get(1)).intValue();
                }
                else if (o.get(0).equals(s.getName())){
                    red[1] += ((BigInteger) o.get(1)).intValue();
                }
            }
            
            System.out.println("Results[]:" + results[0][0] + " " + results[0][1] + " " + results[0][2] + " " + results[0][3] + " " + results[1][0] + " " + results[1][1] + " " + results[1][2] + " " + results[1][3]);
            m.addAttribute("yellowCards", yellow);
            String[] logo = {t.getLogo(), s.getLogo()};
            m.addAttribute("logo", logo);
            m.addAttribute("redCards", red);
            m.addAttribute("teamNames", teamNames);
            m.addAttribute("results", results);
            //System.out.println(yellowCards.get(0) + " " + yellowCards.get(1) + " " + redCards.get(0) + " " + redCards.get(1));
            return "compareTeams";
        }
        else {
            return "redirect:/showTeams";
        }
    }

    @GetMapping("/listPlayerGoalStats")
    public String listPlayerGoalStats(Model model) {
        model.addAttribute("stats", this.eventService.getGoalsStatsPerPlayer());
        model.addAttribute("maxGoals", this.eventService.getMaxGoalsInGame());
        return "listPlayerGoalStats";
    }

    
    @GetMapping("/createGame")
    public String createGame(Model m) {
        int role = getrole();
        if(role == 2){
            m.addAttribute("game", new Game());
            m.addAttribute("allTeams", this.teamService.getAllTeams());
            return "editGame";
        }else{
            return "nopermission";
        }
    }
    @GetMapping("/editGame")
    public String editGame(@RequestParam(name="id", required=true) int id, Model m) {
        Optional<Game> op = this.gameService.getGame(id);
        if (op.isPresent()) {
            m.addAttribute("game", op.get());
            return "editGame";
        }
        else {
            return "redirect:/home";
        }
    }    

    @PostMapping("/saveGame")
    public String saveGame(@ModelAttribute Game u) {
        this.gameService.addGame(u);
        return "redirect:/home";
    }

    private String getEditAdminForm(String id, String formName, Model m) {
        Optional<User> op = this.userService.getUser(id);
        if (op.isPresent()) {
            m.addAttribute("admin", op.get());
            return formName;
        }
        return "redirect:/home";
    }

    @GetMapping("/createAdmin")
    public String createAdmin(Model m) {
        m.addAttribute("user", new User());
        return "editAdmin";
    }
    @GetMapping("/editAdmin")
    public String editAdmin(@RequestParam(name="id", required=true) String id, Model m) {
        Optional<User> op = this.userService.getUser(id);
        if (op.isPresent()) {
            m.addAttribute("user", op.get());
            return "editAdmin";
        }
        else {
            return "redirect:/home";
        }
    }
    @PostMapping("/saveAdmin")
    public String saveAdmin(@ModelAttribute User u) {
        this.userService.addUser(u);
        this.userService.setRole(u.getUsername(), 2);
        return "redirect:/home";
    }    
    @GetMapping("/changeAdmin")
    public String getAdminForm(@RequestParam(name="id", required=true) String id, Model m) {
        return getEditAdminForm(id, "editAdmin", m);
    }

    private String getEditUserForm(String id, String formName, Model m) {
        Optional<User> op = this.userService.getUser(id);
        if (op.isPresent()) {
            m.addAttribute("user", op.get());
            return formName;
        }
        return "redirect:/home";
    }

    @GetMapping("/createUser")
    public String createUser(Model m) {
        int role = getrole();
        if(role == 2){
            m.addAttribute("user", new User());
            return "editUser";
        }else{
            return "nopermission";
        }
    }
    @GetMapping("/editUser")
    public String editUser(@RequestParam(name="id", required=true) String id, Model m) {
        Optional<User> op = this.userService.getUser(id);
        if (op.isPresent()) {
            m.addAttribute("user", op.get());
            return "editUser";
        }
        else {
            return "redirect:/home";
        }
    }    

    @PostMapping("/saveUser")
    public String saveUser(@ModelAttribute User u) {
        this.userService.addUser(u);
        this.userService.setRole(u.getUsername(), 1);
        return "redirect:/home";
    }
    @GetMapping("/changeUser")
    public String getUserForm(@RequestParam(name="id", required=true) String id, Model m) {
        return getEditUserForm(id, "editUser", m);
    }

    private String getEditPlayerForm(String id, String formName, Model m) {
        Optional<Player> op = this.playerService.getPlayer(id);
        if (op.isPresent()) {
            m.addAttribute("player", op.get());
            return formName;
        }
        return "redirect:/home";
    }

    @GetMapping("/createPlayer")
    public String createPlayer(Model m) {
        int role = getrole();
        if(role == 2){
            m.addAttribute("player", new Player());
            m.addAttribute("allTeams", this.teamService.getAllTeams());
            return "editPlayer";
        }else{
            return "nopermission";
        }
    }
    @GetMapping("/editPlayer")
    public String editPlayer(@RequestParam(name="id", required=true) String id, Model m) {
        Optional<Player> op = this.playerService.getPlayer(id);
        if (op.isPresent()) {
            m.addAttribute("player", op.get());
            return "editPlayer";
        }
        else {
            return "redirect:/home";
        }
    }    

    @PostMapping("/savePlayer")
    public String savePlayer(@ModelAttribute Player t) {
        this.playerService.addPlayer(t);
        return "redirect:/home";
    }

    @GetMapping("/changePlayer")
    public String getPlayerForm(@RequestParam(name="id", required=true) String id, Model m) {
        return getEditPlayerForm(id, "editPlayer", m);
    }
    
    private String getEditTeamForm(String id, String formName, Model m) {
        Team op = this.teamService.getTeamByName(id);
        if (op != null) {
            m.addAttribute("team", op);
            return formName;
        }
        return "redirect:/home";
    }

    @GetMapping("/createTeam")
    public String createTeam(Model m) {
        int role = getrole();
        if(role == 2){
            m.addAttribute("team", new Team());
            return "editTeam";
        }else{
            return "nopermission";
        }
    }
    @GetMapping("/editTeam")
    public String editTeam(@RequestParam(name="id", required=true) String id, Model m) {
        Team op = this.teamService.getTeamByName(id);
        if (op != null) {
            m.addAttribute("team", op);
            return "editTeam";
        }
        else {
            return "redirect:/home";
        }
    }    

    @PostMapping("/saveTeam")
    public String saveTeam(@ModelAttribute Team t) {
        this.teamService.addTeam(t);
        return "redirect:/home";
    }

    @GetMapping("/changeTeam")
    public String getTeamForm(@RequestParam(name="id", required=true) String id, Model m) {
        return getEditTeamForm(id, "editTeam", m);
    }
    

    @GetMapping("/currentGames")
    public String currentGames(Model model) {
        List<List<Object>> currentGamessData = this.gameService.getCurrentGames();
        List<List<Object>> cleanedGamesData = new ArrayList();
        List<List<List<Object>>> cleanedGamesEvents = new ArrayList();
        //this.eventService.getEventsByGameId(id);
        for(int i = 0; i < currentGamessData.size(); i++){
            System.out.println(currentGamessData.get(i));
        }
        for(int i = 0; i < currentGamessData.size(); i++){
            List<Object> temp = new ArrayList<>();
            cleanedGamesEvents.add(this.eventService.getEventsByGameId((int) currentGamessData.get(i).get(2)));
            if(currentGamessData.get(i).get(0).equals(currentGamessData.get(i).get(6))){
                temp.add(currentGamessData.get(i).get(1));
                temp.add(currentGamessData.get(i+1).get(1));
            }
            else{
                temp.add(currentGamessData.get(i+1).get(1));
                temp.add(currentGamessData.get(i).get(1));
            }
            temp.add(new String(currentGamessData.get(i).get(3) + " - " + currentGamessData.get(i).get(4)));
            temp.add(currentGamessData.get(i).get(5));
            temp.add(currentGamessData.get(i).get(6));
            cleanedGamesData.add(temp);
            i++;
            System.out.println(temp.get(0) + " " + temp.get(1) + " " + temp.get(2) + " " + temp.get(3) + " " + temp.get(4)); 
        }
        model.addAttribute("games", cleanedGamesData);
        model.addAttribute("events", cleanedGamesEvents);
        System.out.println(cleanedGamesEvents);
        return "currentGames";
    }


    public void setrole( int c) {
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpSession session = attr.getRequest().getSession(true);
        session.setAttribute("role", c);

    }
	public int getrole() {
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpSession session = attr.getRequest().getSession(true);
        Integer role = (Integer) session.getAttribute("role");
        int c;
        if (role == null){
            c = 0;
            session.setAttribute("role", c);
        }else{
            c = role;
        }
		return c;
	}

}
    

/*
select home_goals,away_goals,location, game_date from game WHERE   game_date >= NOW() - '2 hour'::INTERVAL
select * from event where game_id = 600
select teams_name from game_teams where games_id = 600

select  games_id ,teams_name from game_teams where games_id in (select id from game where winner != null) group by games_id,teams_name
select * from game_teams
select * from users
/*sempre que um evento 'goal' é validado, deve-se incrementar os golos na tabela jogo

select * from event where game_id = 1 and valid = true order by time
select count(*) from event where game_id = 1 and type like 'Goal'
insert into users values('m','miguel','1232',223232,'ADMIN');
insert into event values(1,'Yellow',current_time,'Card',true,1,'Marc Navarro','m');
insert into event values(2,'Yellow',current_time,'Card',false,1,'Marc Navarro','m');
insert into event values(3,'Red',current_time,'Card',true,1,'Marc Navarro','m');
insert into event values(4,'',current_time,'Goal',true,1,'Marc Navarro','m');
insert into event values(5,'',current_time,'End',true,1,'','m');

insert into game values (601,2,localtimestamp(0),0,'Stanford Bridge','TBD')


Falta autenticaçao dos utilizadores para a edição de eventos e visualização de todos os eventos. Username sairá do form mas será preciso ser autenticado antes de se inserir novo evento.
Como fazer em relação aos jogos interrompidos: criar uma nva coluna associada ao jogo a dizer se esta interrompido ou não e inserir evento da mesma forma de sempre. Contudo apenas eventos de "resume" serão permitidos.






select * from game where id = 360
select * from event join game on event.game_id = game.id where game.game_date BETWEEN NOW() - INTERVAL '2 HOURS' AND NOW() order by event.time

select event.id, event.color, event.time, event.type, event.valid, event.game_id, event.player_id, event.user_id from event join game on event.game_id = game.id where event.valid = false and game.game_date BETWEEN NOW() - INTERVAL '2 HOURS' AND NOW() order by event.time

insert into users values ('amanda','amanda','123',34913,'ADMIN');
insert into event values (6,'Yellow',current_timestamp,'Card',true,132,'J. Danns','amanda');
select * from users

select * from player
select player.team_name,count(*) from event join player on event.player_id = player.name where game_id in  (select games_id from game_teams where teams_name like 'Liverpool' and games_id in (select games_id from game_teams where teams_name like 'Everton')) and color like 'Red' and valid is true group by player.team_name

select * from game where location like 'Goodison Park' and winner like 'Liverpool'

insert into event values (7,'Yellow',current_timestamp,'Card',true,132,'G. Sigurðsson','amanda');
insert into event values (8,'Yellow',current_timestamp,'Card',true,132,'J. Danns','amanda');
insert into event values (9,'Yellow',current_timestamp,'Card',true,132,'J. Danns','amanda');
insert into event values (14,'Red',current_timestamp,'Card',true,335,'Marc Navarro','amanda');
insert into event values (16,'Red',current_timestamp,'Card',true,335,'J. Danns','amanda');


select * from game
insert into users values ('amanda','amanda','123',34913,'ADMIN');
insert into game values (690,0,current_timestamp,3,'Anfield','TBD');
insert into game_teams values (690,'Everton');
insert into game_teams values (690,'Liverpool');
insert into event values (8,'',current_timestamp,'Goal',true,690,'J. Danns','amanda');

insert into game values (692,0,current_timestamp,3,'Anfield','TBD');
insert into game_teams values (692,'Manchester City');
insert into game_teams values (692,'Watford');
insert into event values (11,'',current_timestamp,'Goal',true,692,'Kamil Conteh','amanda');
insert into event values (13,'Red',current_timestamp,'Card',true,692,'Kamil Conteh','amanda');


delete from event
select * from event where game_id = 690
select id,color,time,type,player_id from event

select stadium,name,id,home_goals,away_goals, cast(date_trunc('second', current_timestamp-game_date) as time) as time,location from game join game_teams on game.id = game_teams.games_id join team on teams_name = name where game_date BETWEEN NOW() - INTERVAL '2 HOURS' AND NOW() order by id

CAST('yourString' AS varchar(50)) as anyColumnName

select * from game_teams where teams_name like 'Leicester'

insert into event values (14,'Red',current_timestamp,'Card',true,335,'Marc Navarro','amanda');
insert into event values (18,'Red',current_timestamp,'Card',true,360,'J. Danns','amanda');


select event.id, event.color, event.time, event.type, event.valid, event.game_id, event.player_id, event.user_id from event join game on event.game_id = game.id where game.game_date BETWEEN NOW() - INTERVAL '2 HOURS' AND NOW() order by event.time
*/