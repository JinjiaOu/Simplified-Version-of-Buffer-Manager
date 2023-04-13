import java.util.Scanner;

public class Main {

    enum Command {
        GET,
        SET,
        PIN,
        UNPIN,
        HELP,
        QUIT
    }

    public static void main(String[] args) {
        BufferPool pool = new BufferPool(Integer.parseInt(args[0]));
        System.out.println("Please input a command (GET, SET, PIN, UNPIN, HELP, QUIT)");

        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("> ");
            String input = scanner.nextLine().trim();

            // read command and arguments
            Command command;
            String[] argsArray;
            int spaceIndex = input.indexOf(' ');
            if (spaceIndex == -1) {
                command = Command.valueOf(input.toUpperCase());
                argsArray = new String[0];
            } else {
                command = Command.valueOf(input.substring(0, spaceIndex).toUpperCase());
                argsArray = input.substring(spaceIndex + 1).split("\\s+");
            }

            // handle command
            switch (command) {
                case GET:
                    if (argsArray.length == 1) {
                        int recordID = Integer.parseInt(argsArray[0]);
                        pool.get(recordID);
                    } else {
                        System.out.println("Invalid arguments for GET command");
                    }
                    break;
                case SET:
                    if (argsArray.length == 2) {
                        int recordID = Integer.parseInt(argsArray[0]);
                        char[] record = argsArray[1].toCharArray();
                        pool.set(recordID, record);
                    } else {
                        System.out.println("Invalid arguments for SET command");
                    }
                    break;
                case PIN:
                    if (argsArray.length == 1) {
                        int blockID = Integer.parseInt(argsArray[0]);
                        pool.pin(blockID);
                    } else {
                        System.out.println("Invalid arguments for PIN command");
                    }
                    break;
                case UNPIN:
                    if (argsArray.length == 1) {
                        int blockID = Integer.parseInt(argsArray[0]);
                        pool.unpin(blockID);
                    } else {
                        System.out.println("Invalid arguments for UNPIN command");
                    }
                    break;
                case HELP:
                    System.out.println("List of commands:");
                    System.out.println("GET <recordID>");
                    System.out.println("SET <recordID> <40 byte string>");
                    System.out.println("PIN <blockID>");
                    System.out.println("UNPIN <blockID>");
                    System.out.println("QUIT");
                    break;
                case QUIT:
                    System.exit(0);
                    break;
                default:
                    System.out.println("Command not recognized. Type HELP for a list of commands.");
            }
        }
    }
}