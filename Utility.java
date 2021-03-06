/**
 - * This file (Utility.java) includes the implementation		
 - * of the Utility class. This class constructs a Scanner		
 - * object, so other classes can derive from it to		
 - * avoid creating and recreating this object throughout		
 - * the program.  Furthermore, the Utility class will hold		
 - * any of the input validation functions that may be used		
 - * frequently throughout the program		
 - *		
 - * Created by MMC on 11/11/16.		
 - */

import java.util.Scanner;

public class Utility {
    // Fields
    protected Scanner input;    //Create new scanner object

    // Constructor
    public Utility(){
        // Declare an object of type Scanner
        input = new Scanner(System.in);
    }

    // Function to output prompt argument and prompt for string input
    public String readString(String prompt){
        String temp;
        System.out.print(prompt); //prompt the user 
        temp = input.nextLine();  //Read the string from buffer
        return temp;
    }

    /**
    *Does all the necessary work to prompt user for and return an int
    */
    public int readInt(String prompt,String errorMessage)
    {
        int tempInt;
        do {
            System.out.print(prompt);
            //If/Else block to verify input is an integer
            String testInput = input.next();
            if (testIntegerInput(testInput)) {
                tempInt = Integer.parseInt(testInput);
            } else {
                System.out.println(errorMessage);
                tempInt = -1;
            }
        } while (tempInt < 0);
        input.nextLine();
        return tempInt;
    }

    /* Performs a check on the user's input to see if
     * they answered some form of "yes."
     * INPUT: NONE
     * OUTPUT: 0 for no, 1 for yes, 2 for an invalid response
     */
    public int checkAnswer(String prompt) {
        System.out.print(prompt);    // Prompt the user
        String temp = input.next();  // Catches the user's input
        input.nextLine();            // Consumes newline character

        int responseFlag;
        if (temp.equalsIgnoreCase("no") || temp.equalsIgnoreCase("n")) {
            responseFlag = 0;
        }
        else if (temp.equalsIgnoreCase("yes") || temp.equalsIgnoreCase("y")) {
            responseFlag = 1;
        } else {
            System.out.println("Not a valid response.");
            responseFlag = 2;
        }
        return responseFlag;
    }

    /* Performs a check on the user's input to see if
     * they entered an integer when prompted to do so.
     * INPUT: String
     * OUTPUT: True or False
     */
   public boolean testIntegerInput(String userInput){
       boolean validInteger = false;
       try{
           Integer.parseInt(userInput);
           //If successful, input is an integer
            validInteger = true;
       } catch (NumberFormatException ex){
           System.out.println("Invalid Input. Not an numerical value.");
       }
       return validInteger;
   }
    
    /**
     * Clear the console screen
     */
    public void clearScreen(){
        for(int i = 0; i < 100; ++i )
            System.out.println("\n\n\n\n");
    }

    /**
     * Wait for the user to press a key to continue
     */
    public void waitForEnter(){
        input.nextLine();
    }

    public void errorMessage(String message){
                 System.out.println(" \033[0;31m "+ message +"\033[0m");
    }

    public void warningMessage(String message){
                 System.out.println(" \033[0;33m "+ message +"\033[0m");
    }

    public void successMessage(String message){
                 System.out.println(" \033[0;32m "+ message +"\033[0m");
    }

}
