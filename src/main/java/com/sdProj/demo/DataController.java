package com.sdProj.demo;

import java.lang.reflect.Field;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.sdProj.data.Event;
import com.sdProj.data.Team;
import com.sdProj.data.Player;
import com.sdProj.data.Professor;
import com.sdProj.data.User;
import com.sdProj.data.Game;
import com.sdProj.data.Student;
import com.sdProj.formdata.FormData;

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

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    StudentService studentService;

    @Autowired
    ProfService profService;

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

    @GetMapping("/login")
    public String login() {
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

         //Print returned data in console
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonParser jp = new JsonParser();
        JsonElement je = jp.parse(response3.getBody().toString());
        String prettyJsonString = gson.toJson(je);
        //System.out.println(prettyJsonString + '\n');
        je = jp.parse(response2.getBody().toString());
        prettyJsonString = gson.toJson(je);
        //System.out.println(prettyJsonString + '\n');
        //System.out.println(response.getStatus());
        //System.out.println(response.getHeaders().get("Content-Type"));

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
            //System.out.println("Team: " + t.getName());
            content = r.getJSONObject(i).getJSONObject("player");
            //System.out.println(t.getName()+'\n');
            //System.out.println(r.getJSONObject(i).getString("name") + " " + r.getJSONObject(i).getString("position") +"\n");
            //java.sql.Date sqlDateOfBirth = java.sql.Date.valueOf(content.getJSONObject("birth").getString("date"));
            java.sql.Date sqlDateOfBirth = java.sql.Date.valueOf("1995-12-25");
            Player a = new Player(content.getString("name"),content.getString("photo"),r.getJSONObject(i).getJSONArray("statistics").getJSONObject(0).getJSONObject("games").getString("position") ,sqlDateOfBirth, t);
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
            //System.out.println(t1.getName() + " - " + t2.getName());

            String winner;
            //System.out.println(t1.getName() + " vs " + t2.getName() + "\n");
            //System.out.println(r.getJSONObject(i).getString("date") + " " + r.getJSONObject(i).getString("status") +"\n");
            if(r.getJSONObject(i).getJSONObject("goals").getInt("home") < r.getJSONObject(i).getJSONObject("goals").getInt("away"))
                winner = t2.getName();
            else if(r.getJSONObject(i).getJSONObject("goals").getInt("home") > r.getJSONObject(i).getJSONObject("goals").getInt("away"))
                winner = t1.getName();
            else
                winner = "Draw";
             
            Instant instant = Instant.parse( r.getJSONObject(i).getJSONObject("fixture").getString("date"));
            java.sql.Timestamp sqlDate = java.sql.Timestamp.from(instant);
             //java.sql.Timestamp sqlDate = java.sql.Timestamp.valueOf(r.getJSONObject(i).getJSONObject("fixture").getString("date"));
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

    @GetMapping("/listStudents")
    public String listStudents(Model model) {
        model.addAttribute("students", this.studentService.getAllStudents());
        return "listStudents";
    }

    @GetMapping("/createStudent")
    public String createStudent(Model m) {
        m.addAttribute("student", new Student());
        m.addAttribute("allProfessors", this.profService.getAllProfessors());
        return "editStudent";
    }

    //Endpoint to show the victories of a team
    @GetMapping("/listTeamStats")
    public String listTeamVictories(Model model) {
        List<List<Integer>> teamResults = new ArrayList<>(); 
        ArrayList<String> teamNames = new ArrayList<>();
        for (Team t : teamService.getAllTeams()){
            teamNames.add(t.getName());
            teamResults.add(this.teamService.teamResults(t));
            //System.out.println(this.teamService.teamResults(t));
        }
        model.addAttribute("names", teamNames);
        model.addAttribute("results", teamResults);
        
        for(List<Integer> l : teamResults){
            System.out.println(l);
        }
        return "listTeamStats";
    }

    @GetMapping("/editStudent")
    public String editStudent(@RequestParam(name="id", required=true) int id, Model m) {
        Optional<Student> op = this.studentService.getStudent(id);
        if (op.isPresent()) {
            m.addAttribute("student", op.get());
            m.addAttribute("allProfessors", this.profService.getAllProfessors());
            return "editStudent";
        }
        else {
            return "redirect:/listStudents";
        }
    }    

    @GetMapping("/teamDuel/{id1}/{id2}")
    public String teamDuel(@PathVariable("id1") String id, @PathVariable("id2") String id2, Model m) {
        Team t1 = this.teamService.getTeamByName(id);
        Team t2 = this.teamService.getTeamByName(id2);
        if (t1 != null && t2 != null) {
            List<Object> o = this.gameService.getGoalsAndLocation(t1, t2);
            m.addAttribute("object", o);
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

    @PostMapping("/saveStudent")
    public String saveStudent(@ModelAttribute Student st) {
        this.studentService.addStudent(st);
        return "redirect:/listStudents";
    }

    @GetMapping("/queryStudents")
    public String queryStudent1(Model m) {
        m.addAttribute("person", new FormData());
        return "queryStudents";
    }

    /* Note the invocation of a service method that is served by a query in jpql */
    @GetMapping("/queryResults")
    public String queryResult1(@ModelAttribute FormData data, Model m) {
        List<Student> ls = this.studentService.findByNameEndsWith(data.getName());
        m.addAttribute("students", ls);
        return "listStudents";
    }

    @GetMapping("/listProfessors")
    public String listProfs(Model model) {
        model.addAttribute("professors", this.profService.getAllProfessors());
        return "listProfessors";
    }

    @GetMapping("/createProfessor")
    public String createProfessor(Model m) {
        m.addAttribute("professor", new Professor());
        return "editProfessor";
    }

    private String getEditProfessorForm(int id, String formName, Model m) {
        Optional<Professor> op = this.profService.getProfessor(id);
        if (op.isPresent()) {
            m.addAttribute("professor", op.get());
            return formName;
        }
        return "redirect:/listProfessors";
    }

    @GetMapping("/editProfessor")
    public String editProfessor(@RequestParam(name="id", required=true) int id, Model m) {
        return getEditProfessorForm(id, "editProfessor", m);
    }    

    @PostMapping("/saveProfessor")
    public String saveProfessor(@ModelAttribute Professor prof) {
        this.profService.addProfessor(prof);
        return "redirect:/listProfessors";
    }

    /* For the sake of illustrating the use of @Transactional */
    @GetMapping("/changeOffice")
    public String getOfficeForm(@RequestParam(name="id", required=true) int id, Model m) {
        return getEditProfessorForm(id, "editProfessorOffice", m);
    }

    @PostMapping("/sumbitOfficeChange")
    public String changeOffice(@ModelAttribute Professor prof) {
        this.profService.changeProfOffice(prof.getId(), prof.getOffice());
        return "redirect:/listProfessors";
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
*/