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
            List<String> inputArr = getInput();
            String command = inputArr.get(0);
            String data = inputArr.get(1);
            commandHandler(command, data);
        }
    }

    private List<String> getInput() {
        List <String> input_arr = new ArrayList<>();
        String s = input.nextLine();
        String[] commandArr = s.split(" ", 2);

        input_arr.add(commandArr[0]);
        input_arr.add(commandArr[1]);

        return input_arr;
    }

    private void commandHandler(String command,  String data) throws IOException {
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
        }
    }

    public static void printOutput(Output output) throws JsonProcessingException {
        String print = mapper.writeValueAsString(output);
        print = print.replace("\\", "");
        System.out.println(print);
    }
}
