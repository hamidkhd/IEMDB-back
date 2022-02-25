package ir.ac.ut.ie;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.*;

public class CommandHandler {
    private Scanner input;
    private MainSystem mainSystem;
    private static ObjectMapper mapper;

    public CommandHandler() {
        mainSystem = new MainSystem();
        input = new Scanner(System.in);
        mapper = new ObjectMapper();
    }

    public static void main(String[] args) throws IOException {
        CommandHandler commandHandler = new CommandHandler();
        commandHandler.main_loop();
    }

    private void main_loop() throws IOException {
        while (true) {
            try {
                List<String> inputArr = getInput();
                String command = inputArr.get(0);
                String data = "";
                if (inputArr.size() > 1)
                    data = inputArr.get(1);
                commandHandler(command, data);
            }
            catch (Exception exception) {}
        }
    }

    private List<String> getInput() {
        List <String> input_arr = new ArrayList<>();
        String s = input.nextLine();
        String[] commandArr = s.split(" ", 2);
        for (int i=0; i<commandArr.length; i++)
            input_arr.add(commandArr[i]);

        return input_arr;
    }

    public void commandHandler(String command,  String data) throws IOException {
        switch (command) {
            case "addActor":
                mainSystem.addActor(data);
                break;
            case "addMovie":
                mainSystem.addMovie(data);
                break;
            case "addUser":
                mainSystem.addUser(data);
                break;
            case "addComment":
                mainSystem.addComment(data);
                break;
            case "rateMovie":
                mainSystem.rateMovie(data);
                break;
            case "voteComment":
                mainSystem.voteComment(data);
                break;
            case "addToWatchList":
                mainSystem.watchListHandler(data, true);
                break;
            case "removeFromWatchList":
                mainSystem.watchListHandler(data, false);
                break;
            case "getMoviesList":
                mainSystem.getMoviesList(data);
                break;
            case "getMovieById":
                mainSystem.getMovieById(data);
                break;
            case "getMoviesByGenre":
                mainSystem.getMoviesByGenre(data);
                break;
            case "getWatchList":
                mainSystem.getWatchList(data);
                break;
            default:
                CommandHandler.printOutput(new Output(false, "InvalidCommand"));
                break;
        }
    }

    public static void printOutput(Output output) throws JsonProcessingException {
        String print = mapper.writeValueAsString(output);
        print = print.replace("\\", "");
        System.out.println(print);
    }
}
