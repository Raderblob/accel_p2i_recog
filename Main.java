import gnu.io.SerialPort;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.Set;

public class Main {
    static int inc=0;
    static LinkedList<Gesture> history = new LinkedList<>();
    static SerialConnexion myConnexion;

    public static void main(String[] args) {

        myConnexion = new SerialConnexion();


        try {
            myConnexion.connect("COM6");
        } catch (Exception e) {
            e.printStackTrace();
        }

        SettingsChecker sClass = new SettingsChecker();
        Thread settingsThread = new Thread(sClass);
        settingsThread.start();

        Interface testUI = new Interface(history);
        testUI.setVisible(true);


        do {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(myConnexion.dataIncoming()){
                testUI.setState("Reading Mouvement");
                while (!myConnexion.dataEnding()){
                    //System.out.println("Reading");
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                testUI.setState("Computing Mouvement");
                System.out.println("end msg");
                //diff settings
                switch (testUI.isLearning){
                    case 'g':

                        history.add(new Gesture(myConnexion.rawDataArr(),testUI.dataName + ""));
                        for (Gesture dS : history) {
                            for(int i = 0;i< dS.mySets.size();i++){
                                System.out.println("Data set " + dS.mySets.get(i).myId + " " + dS.mySets.get(i).myData.size());
                            }
                        }
                        inc++;
                        break;
                    case 't':
                        DataSet dataTotest = myConnexion.rawData("testData");

                        String sel=getClosest(dataTotest);

                        System.out.println("matches with gesture " + sel);
                        break;
                    case 'r':
                        ArrayList<Data> rawD = myConnexion.rawDataArr();

                        String potRes = getClosest(new DataSet(rawD,"TestData"));

                        for (Gesture dS : history) {
                            if(dS.myName.equals(testUI.dataName)){
                                if(dS.myName.equals(potRes)){
                                    dS.reinforce(rawD);
                                    System.out.println("Reinforced");
                                }else{
                                    System.out.println("Missmatch");
                                }
                            }
                        }
                        break;
                }
                testUI.setState("New Mouvement read");
                testUI.showGestures(history);
            }




                /*System.out.println("Enter name");
                String reChoice = scanner.nextLine().trim();
                for(int i = 0;i<history.size();i++){
                    if(history.get(i).myName.equals(reChoice)){
                        System.out.println("reading " + history.get(i).myName);
                        myConnexion.SerialWrite("g");
                        scanner.nextLine().trim();
                        myConnexion.SerialWrite("s");
                        history.get(i).reinforce(myConnexion.rawDataArr());
                        System.out.println("@");
                    }
                }
                for (Gesture dS : history) {
                    for(int i = 0;i< dS.mySets.size();i++){
                        System.out.println("Data set " + dS.mySets.get(i).myId + " " + dS.mySets.get(i).myData.size());
                    }
                }*/

        }while(true);
     //  System.exit(0);
    }

    static private String getClosest(DataSet dataTotest){
        double minval = Double.MAX_VALUE;
        String sel="nothing";

        for(Gesture dS:history){
            double comp = dS.readDistance(dataTotest);
            System.out.println("Data set " + dS.myName + " "+ comp);
            if(minval> comp){
                minval=comp;
                sel = dS.myName;
            }
        }
        return sel;
    }

}
class SettingsChecker implements Runnable {
    public String mode;
    Scanner scanner;
    public SettingsChecker(){
        scanner = new Scanner(System.in);
        mode = "g";
    }
    @Override
    public void run() {
        String playerInput;
        do {
            playerInput = scanner.nextLine().trim();
            switch (playerInput){
                case"g":
                    mode = playerInput;
                    break;
                case"t":
                    mode=playerInput;
                    break;
                case"r":
                    mode=playerInput;
                    break;
            }
            System.out.println(mode);
        } while (true);
    }
}
