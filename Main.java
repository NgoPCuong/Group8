import java.util.Random;

public class Main {

    public static void main(String[] args){

        //Create a new ChocAn object to run
        ChocAn chocAnTerminal = new ChocAn();
        chocAnTerminal.runChocAn();
        
        //User testManager = new Manager(987654321);
        //testManager.run();

        //Test adding member and provider
        //Manager testManager = new Manager(987654321);
        //testManager.addMember();
        //
        //
        Provider p = new Provider(502891894);

        p.createBill();
        


        // Test For dataacess role
        //DataAccess dataAccess = new DataAccess();
        //System.out.println(dataAccess.directoryLookUp());
        //System.out.println(dataAccess.getMemberBill(587857871));
        //dataAccess.suspendMember(887320551);
        //dataAccess.unsuspendMember(887320551);


    }


}
