package com.sdProj.demo;

import java.util.List;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.SQLException;

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
        } catch (UnirestException e) {
            System.out.println("Error getting data from API");
        }

         //Print returned data in console
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonParser jp = new JsonParser();
        JsonElement je = jp.parse(response.getBody().toString());
        String prettyJsonString = gson.toJson(je);
        System.out.println(prettyJsonString + '\n');
        je = jp.parse(response2.getBody().toString());
        prettyJsonString = gson.toJson(je);
        System.out.println(prettyJsonString + '\n');
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
            content = r.getJSONObject(i).getJSONObject("player");
            System.out.println(t.getName()+'\n');
            //System.out.println(r.getJSONObject(i).getString("name") + " " + r.getJSONObject(i).getString("position") +"\n");
            //java.sql.Date sqlDateOfBirth = java.sql.Date.valueOf(content.getJSONObject("birth").getString("date"));
            java.sql.Date sqlDateOfBirth = java.sql.Date.valueOf("1995-12-25");
            Player a = new Player(content.getString("name"),content.getString("photo"),r.getJSONObject(i).getJSONArray("statistics").getJSONObject(0).getJSONObject("games").getString("position") ,sqlDateOfBirth, t);
            this.playerService.addPlayer(a);   
        }


        Professor[] myprofs = { 
            new Professor("José", "D3.1"), 
            new Professor("Paulo", "135"), 
            new Professor("Estrela", "180")
        };
        Student[] mystudents = { 
            new Student("Paula", "91999991", 21),
            new Student("Artur", "91999992", 21),
            new Student("Rui", "91999993", 19),
            new Student("Luísa", "91999994", 20),
            new Student("Alexandra", "91999995", 21),
            new Student("Carlos", "91999995", 22)
        };

        mystudents[0].addProf(myprofs[0]);
        mystudents[0].addProf(myprofs[1]);
        mystudents[1].addProf(myprofs[1]);
        mystudents[1].addProf(myprofs[2]);
        mystudents[2].addProf(myprofs[0]);
        mystudents[3].addProf(myprofs[2]);
        mystudents[4].addProf(myprofs[1]);
        mystudents[5].addProf(myprofs[0]);
        mystudents[5].addProf(myprofs[1]);
        mystudents[5].addProf(myprofs[2]);

        for (Student student : mystudents)
            this.studentService.addStudent(student);
    
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