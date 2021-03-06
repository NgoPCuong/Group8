import java.util.Random;

/**
 * This class contains all the methods related to a ChocAn Manager
 */
public class Manager extends User {

    public Manager(int userID) {
        super(userID);
    }

    // Generates a random 9-digit provider/member
    private int randomDigitsID() {
        boolean isValidID;
        int newMemberID;
        /*
        * Instead of having the Manager enter a 9 digit number when creating a Provider
        * this will generate a random 9 digit number for the Provider that is in the range
        * 100000000 - 999999999.
        */
        do {
            Random rand = new Random();
            newMemberID = 100000000 + rand.nextInt(899999999);
            isValidID = isIDTaken(newMemberID);
        } while(!isValidID);
        return newMemberID;
    }

    /* Verifies whether the entered ID is already in use or not
     *      1. If the entered ID is taken, verify will not be set to "invalid". Return will be false
     *         to signify the ID is already in the database
     *      2. If the entered ID isn't in the database, verify will be set to "invalid". This means
     *         that the ID is not currently in use. idIDTaken returns true to signify it is not taken
     */
    protected boolean isIDTaken(int id) {
        String verify = DataAccess.userVerification(id);
        return verify.equals("invalid");
    }

    /** This function scans the database for a valid MEMBER (for suspension purposes)
     * This function searches the database for a member specifically. The reason for this
     * is... if we just use isIDTaken(id) for the scan it searches ALL people in the database
     * including managers/providers and we don't want to suspend or unsuspend them.
     **/
    protected boolean isValidMember(int memberID) {
        String verify = DataAccess.userVerification(memberID);
        if (verify.equals("member") || verify.equals("suspended"))
            return true;
        else return false;
    }

    // Prompts the manager for basic organization information before adding to the database
    private boolean addOrganization(String user) {
        // Declare organization info variables
        String tempName, tempAddress, tempCity, tempState;
        int newID, tempZip;

        // Loop while the information entered isn't correct
        do {
            tempName = readString(user + " name: ");          // Get the organization name
            tempAddress = readString(user + " address: ");    // Get the organization address
            tempCity = readString(user + " city: ");          // Get the organization city
            tempState = readString(user + " state: ");        // Get the organization state
            newID = randomDigitsID();                         // Generate an ID number for the organization
            tempZip = readInt(user + " zip code: ", "Please enter a numerical zip code."); // Get the organization zip code

            clearScreen();
            System.out.println(user + " name: " + tempName);
            System.out.println(user + " address: " + tempAddress);
            System.out.println(user + " city: " + tempCity);
            System.out.println(user + " state: " + tempState);
            System.out.println(user + " zip code: " + tempZip);
        } while (checkAnswer("Is this information correct?: ") != 1);

        boolean queryStatus = data.addOrganization(newID, tempName, tempAddress, tempCity, tempState, tempZip, user);
        return queryStatus;
    }

    /*
     * This function removes a specific organization from the database. The argument passed
     * in is either "member" or "provider" dependant on the menu the user chose to enter.
     */
    private boolean removeOrganization(String user) {
        int userToRemove;           // Holds the ID number for the provider or member to be removed
        boolean valid;              // Holds the bool value for if the ID is in use or not

        // Do the ID verification while the ID entered is not valid
        do {
            // Prompt the manager for the ID number for the organization they would like to remove
            userToRemove = readInt("Please enter the " + user + " ID for the " + user + " you would like to remove: ","Please enter a numerical Id" );
            // Check if the ID entered is a valid ID in the database
            valid = isIDTaken(userToRemove);
            // If the ID entered isn't in the database
            if (valid)
                errorMessage("Not a valid " + user + " ID. Please enter a valid "+ user + " ID.");
        } while (valid);

        // Holds the bool value from the deleteOrganization function to signify success or failure of removal
        if (user.equals("member"))
            data.deleteAllMemberReport(userToRemove);               // Remove member reports if ID is a member ID
        else
            data.deleteAllProviderReport(userToRemove);             // Remove provider reports if ID is a provider ID
        boolean delete = data.removeOrganization(userToRemove);     // Remove the actual member/provider
        return delete;
    }

    /*
     * This function updates a specific organizations information. The argument passed in
     * is either "member" or "provider" dependant on the menu that user chose to enter.
     */
    private boolean updateOrganization(String user) {
        // Prompt manager for ID of organization they would like to update
        int organizationID;
        boolean valid;
        do {
            organizationID = readInt("Enter the ID number for the " + user + " you would like to update: ", "Please enter a numerical Id");
            valid = isIDTaken(organizationID);
            if (valid) ///////What is going on here? 
                errorMessage("Not a valid " + user + " ID. Please try again.");
        } while (valid);

        String tempName, tempAddress, tempCity, tempState;
        int tempZip;
        do {
            
            tempName = readString(user + " name: ");          // Get the organization name
            tempAddress = readString(user + " address: ");    // Get the organization address
            tempCity = readString(user + " city: ");          // Get the organization city
            tempState = readString(user + " state: ");        // Get the organization state
            tempZip = readInt(user + " zip code: ", "Please enter a numerical zip code.");  // Get the organization zip code

            clearScreen();
            System.out.println(user + " name: " + tempName);
            System.out.println(user + " address: " + tempAddress);
            System.out.println(user + " city: " + tempCity);
            System.out.println(user + " state: " + tempState);
            System.out.println(user + " zip code: " + tempZip);
        } while(checkAnswer("Is this information correct?: ") != 1);

        boolean update = data.updateOrganization(organizationID, tempName, tempAddress, tempCity, tempState, tempZip, user);
        return update;
    }

    // Lets a manager suspend or unsuspend a member in the database
    private boolean changeMemberStanding() {
        // Verify whether the member exists in the database
        int member;
        do {
            // Prompt for the memberID
            member = readInt("Please enter the member's ID number: ","Please enter a numerical Id");
            // Prompt if the ID isn't in the database
            if (!isValidMember(member))
                errorMessage("The entered member ID was not found. Please enter a valid member ID.");
        } while (!isValidMember(member));

        // Loop to make sure choice is entered properly
        String choice; // Holds whether the manager wants to suspend or unsuspend the member
        do {
            // Prompt manager for suspension or unsuspension of a member
            choice = readString("Do you wish to suspend or unsuspend this member?: ");

            // Run data access function corresponding to managers choice
            if (choice.equalsIgnoreCase("suspend"))
                return data.suspendMember(member);
            else if (choice.equalsIgnoreCase("unsuspend"))
                return data.unsuspendMember(member);
            errorMessage("Please enter a valid choice!");
        } while (!choice.equalsIgnoreCase("suspend") || choice.equalsIgnoreCase("unsuspend"));
        return false; // Fail flag
    }

    // Displays all of the manager menu options to keep the run function shorter and sweeter
    private int menuPrompt() {
        int menuChoice = 0; // it will hold the value of the user input
        do {
            // Print menu options to the screen
            System.out.println("###########################################################");
            System.out.println("## Manager Menu Options:                                 ##");
            System.out.println("##\t (1) Edit Members                                    ##");
            System.out.println("##\t (2) Edit Providers                                  ##");
            System.out.println("##\t (3) Suspend/Unsuspend Member                        ##");
            System.out.println("##\t (4) Generate Weekly Member Emails                   ##");
            System.out.println("##\t (5) Generate Weekly Provider Emails                 ##");
            System.out.println("##\t (6) Quit                                            ##");
            System.out.println("###########################################################");
            menuChoice = readInt("Enter a menu choice (1-6): ","");
            if(menuChoice <= 0 || menuChoice > 6) {
                clearScreen();
                errorMessage("Please make a valid choice!");
            }
        } while (menuChoice <= 0 || menuChoice > 6);
        return menuChoice;
    }

    /**
     * This will function will display the submenu options for the manager to
     * take actions members and providers
     * @param user the user the manager will edit(Provider or Member)
     * @return the function will return the option chosen by the manger
     */
    private int editSubmenu(String user){
        int menuChoice = 0; // it will hold the value of the user input
        do {
            // Print menu options to the screen
            System.out.println("###########################################################");
            System.out.println(user +"s Edit Menu Options:");
            System.out.println("\t (1) Add "+ user);
            System.out.println("\t (2) Remove "+ user);
            System.out.println("\t (3) Update "+ user);
            System.out.println("\t (4) Return"); // We need such option
            System.out.println("###########################################################");
            menuChoice = readInt("Enter your choice (1-4): ","");
            if(menuChoice <= 0 || menuChoice > 4) {
                clearScreen();
                errorMessage("Please make a valid choice!");
            }
        } while (menuChoice <= 0 || menuChoice > 4);
        return menuChoice;
    }

    //This function will control actions in the submenu
    private void submenuRun(String user) {
        int menuChoice = 0;
        do {         // Loop to test input against valid choices
            menuChoice = editSubmenu(user); // Display the menu options to the screen and get the selected option
            clearScreen();

            // If cases handle running the appropriate method based on manager choice
            if (menuChoice == 1) {
                boolean add = addOrganization(user);
                if (add)
                    successMessage(user + " successfully added. Enter to continue.");
                else
                    errorMessage("There was a problem adding the " + user + ". Enter to continue.");
                waitForEnter();  //Wait for the user to press something then move on
                clearScreen();   //Clear the screen after enter is pressed
            }
            else if (menuChoice == 2) {
                boolean remove = removeOrganization(user);
                if (remove)
                    successMessage(user + " successfully removed. Enter to continue.");
                else
                    errorMessage("There was a problem removing the " + user + ". Enter to continue.");
                waitForEnter();
                clearScreen();
            }
            else if (menuChoice == 3){
                boolean update = updateOrganization(user);
                if (update)
                    successMessage(user + " successfully updated. Enter to continue.");
                else
                    errorMessage("There was a problem updating the " + user + ". Enter to continue.");
                waitForEnter();
                clearScreen();
            }
        } while (menuChoice != 4);
        return; // Return to the main menu
    }

    /*********  Driver  **********/
    // This function is used as the menu function when a manager is allocated
    public void run() {
        // Integer to hold manager's menu choice
        int menuChoice = 0;

        // Loop to test input against valid choices
        do {
            menuChoice = menuPrompt(); // Display the menu options to the screen and get the selected option
            clearScreen();

            //If cases handle running the appropriate method based on manager choice
            if (menuChoice == 1) {
                submenuRun("member");
            } else if (menuChoice == 2) {
                submenuRun("provider");
            } else if (menuChoice == 3){
                boolean change = changeMemberStanding();
                if (change)
                    successMessage("Member standing successfully changed. Enter to continue.");
                else
                    errorMessage("There was a problem changing the member's standing.");
                waitForEnter();
                clearScreen();
            } else if (menuChoice == 4) {
                boolean reportGen = data.exportMembersReportToFile();
                if (reportGen)
                    successMessage("Successfully generated reports");
                else
                    errorMessage("There was a problem generating reports.");
                waitForEnter();
                clearScreen();
            } else if (menuChoice == 5) {
                boolean reportGen = data.exportProviderServicesToFile();
                if (reportGen)
                    successMessage("Successfully generated reports");
                else
                    errorMessage("There was a problem generating reports");
                waitForEnter();
                clearScreen();
            }
        } while (menuChoice != 6);
    }
}
