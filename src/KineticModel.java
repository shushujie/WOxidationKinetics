import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.nio.file.*;

public class KineticModel {

    public static double KineticModel(
            Oxide WO3,
            Oxide WO2_9,
            Oxide WO2_72,
            Oxide WO2,
            Materials W
    ) throws IOException {

        //Sometimes to distinguish from verison to version of your code, you may change the testID into e.g. "shu04012022"
        String testID = "0401_22";

        FileWriter fileWriter = new FileWriter("profile" + testID + ".txt");
        PrintWriter printWriter = new PrintWriter(fileWriter);
        FileWriter mGain_Writer = new FileWriter("massGain"+testID+".txt");
        PrintWriter print_mGain = new PrintWriter(mGain_Writer);
        FileWriter CMFit_Writer = new FileWriter("CommonsMathFit"+testID+".txt");
        PrintWriter Print_CMFit = new PrintWriter(CMFit_Writer);

        FileWriter Ss_Writer = new FileWriter("InterfacePositions"+testID+".txt");
        PrintWriter print_Ss = new PrintWriter(Ss_Writer);


/*
////// Initial condition implement:
*/
        //Initialize interfaces at time = 0
        ArrayList<Double> OxygenConcentration = new ArrayList<>();//[Oxy] profile at a specific time; it's a temporary data container
        ArrayList<Double> Time = new ArrayList<>();
        ArrayList<Double> sqrtTime = new ArrayList<>();
        ArrayList<Double> MassGains = new ArrayList<>();

        WO3.sIter = sIter0s[0];
        WO3.set();
        WO2_9.sIter = sIter0s[1];
        WO2_9.set();
        WO2_72.sIter = sIter0s[2];
        WO2_72.set();
        WO2.sIter = sIter0s[3];
        WO2.set();


//        System.out.println("\n"+ WO3.sIter+", "+WO2_9.sIter+", "+WO2_72.sIter+", "+WO2.sIter);

        /****** Initialize Profile of Oxygen ******/
        ProfileUtilities.InitialCondition(OxygenConcentration, dx, depthIterations, SurfaceOxygen,
                WO3, WO2_9, WO2_72, WO2);

        //Add initial condition to  list of  profiles(time)
        if (ConcentrationOfOxigen.printProfile){
            DataPrinter.WriteData(OxygenConcentration, printWriter, 0, true);
        }


        DataPrinter.WriteInterfacePosition(print_Ss, WO3, WO2_9, WO2_72, WO2);
        double massGain = ProfileUtilities.GetMassGain(WO3.sIter, WO2_9.sIter, WO2_72.sIter, WO2.sIter);
/*        if (ConcentrationOfOxigen.printMassGain){
        print_mGain.printf("%d ", 0); print_mGain.printf("%e\n", massGain);}*/
        //Time.add(0.0);
        //sqrtTime.add(Math.sqrt(Time.get(0)));



        /****** Revolution of Profile of Oxygen ******/

        ArrayList<Integer> sIterArray = new ArrayList<Integer>();
        sIterArray.add(WO3.sIter);
        sIterArray.add(WO2_9.sIter);
        sIterArray.add(WO2_72.sIter);
        sIterArray.add(WO2.sIter);

        //As values at time j = 0 already initialized, kick off time evolution from j = 1:
        /***  To Update profile for j dt steps***/
        for (long j = 1; j <= timeIterations; j++) {//j for time loop
            //record the profile at previous time step
            ArrayList<Double> PreviousConcentration = new ArrayList<>(OxygenConcentration);
            OxygenConcentration = new ArrayList<>();//it functions as a profile container for each time iterator;

            /*** Entrance to Update profile ***/
            boolean  IsFinish = SolveDiffusionODE.SolveODE(
                    PreviousConcentration,
                    OxygenConcentration,
                    sIterArray,
                    j,
                    WO3, WO2_9, WO2_72, WO2,
                    W
            );

            if (!IsFinish) {
                break;
            }

            if(j%sampling == 0) {



                if (ConcentrationOfOxigen.printProfile){
                    DataPrinter.WriteData(OxygenConcentration, printWriter, j, true);
                }

                //TODO:
                DataPrinter.WriteInterfacePosition(print_Ss, WO3, WO2_9, WO2_72, WO2);
                massGain = ProfileUtilities.GetMassGain(WO3.sIter, WO2_9.sIter, WO2_72.sIter, WO2.sIter);

                double time = j * dt;
                if (ConcentrationOfOxigen.printMassGain) {
                    print_mGain.printf("%e ", time);
                    print_mGain.printf("%e\n", massGain);
                }

                Time.add(time);
                sqrtTime.add(Math.sqrt(time));
                MassGains.add(massGain);

            }

        }//time iteration;


//https://www.demo2s.com/java/apache-commons-abstractcurvefitter-fit-collection-weightedobservedpoin.html
//        FittingCurve Curvefitter = new FittingCurve();
//        double[] coeffs = Curvefitter.fit(sqrtTime, MassGains);
//        double kp = coeffs[1]*coeffs[1];
//        //System.out.println(testID + " has kp of: " + kp );
//
//        for(int i = 0; i < coeffs.length; i++) {
//            //System.out.println(i + "'s oder " + coeffs[i]);
//            Print_CMFit.println(coeffs[i]);
//        }
        printWriter.close();
        print_mGain.close();
        Print_CMFit.close();
        print_Ss.close();
        printWriter.close();




        return 0;


    }//main

    public static double dx = ConcentrationOfOxigen.dx;
    public static double dt = ConcentrationOfOxigen.dt;
    public static int depthIterations = ConcentrationOfOxigen.depthIterations;
    public static long timeIterations = ConcentrationOfOxigen.timeIterations;
    public static long sampling = ConcentrationOfOxigen.sampling;


    public static double SurfaceOxygen = Oxide.SurfaceOxygen;
    public static double InterfaceConcentration1 = Oxide.InterfaceConcentration1;//3.0;
    public static double InterfaceConcentration2 = Oxide.InterfaceConcentration2;//2.9;
    public static double InterfaceConcentration3 = Oxide.InterfaceConcentration3;//2.722;
    public static double InterfaceConcentration4 = Oxide.InterfaceConcentration4;//2.0;

    public static int[] sIter0s = new int[]{2, 4, 6, 8};

}
