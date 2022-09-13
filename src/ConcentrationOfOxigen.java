//import java.util.*;


import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
//import java.lang.Math;

public class ConcentrationOfOxigen
{
    public static double temperature = 700 + 273;//unit(kelvin, K)
    //The four flags are used to control if the interface of s1(WO3/WO2.9),s2(WO2.9/WO2.72),s3(WO2.72/WO2),s4(WO2/W) move or fixed.
    //If a Lock is assigned a "true" value, the interface is fixed.
    public static boolean LockS = true, LockS2 = true, LockS3 = false, LockS4 = false;


    // Discrete parameters:
    public static long sampling = 100_000L;//only output when the count of interation is a multiple of the "sampling".
    public static int depthIterations = 4500;// the depth(/thickness) of the materail, expressed in a count of dx
    public static long timeIterations = 10L*sampling;//100L * sampling;//
    public static double dx = 1e-9;//in [m], around 1nm
    public static double dt = 1e-9;// in [second] //1e-3s
    //NOTE!: if you want to raise dx and dt to do rougher simulations, the factor raised for dt should be the square of that for dx
    //e.g. dx = 10e-9; dt = 100e-9;

    //print options:
    public static  boolean printProfile = true, printMassGain = true, printFitParas = false;


    public static void main(String args[]) throws IOException {

        /****** (1) Creat all oxides    ******/
        Oxide WO3 = new Oxide(Materials.InterfaceConcentration1, KineticModel.sIter0s[0], LockS);
        Oxide WO2_9 = new Oxide(Materials.InterfaceConcentration2, KineticModel.sIter0s[1], LockS2);
        Oxide WO2_72 = new Oxide(Materials.InterfaceConcentration3, KineticModel.sIter0s[2], LockS3);
        Oxide WO2 = new Oxide(Materials.InterfaceConcentration4, KineticModel.sIter0s[3], LockS4);

        /****** (2) Creat W   ******/
        Materials W = new Materials();
        W.Diffusivity = Materials.D0_InW * Math.exp(-Materials.Em_InW / (Materials.Boltzmanns * temperature));

        FileWriter testkp_Writer = new FileWriter("testLog");
        PrintWriter print_kp = new PrintWriter(testkp_Writer);
        FileWriter fittedkp_Writer = new FileWriter("fittedKp");
        PrintWriter print_fittedkp = new PrintWriter(fittedkp_Writer);

        /****** Run the Model again all Oxides ******/
        //NOTE: the functionn "KineticModel.KineticModel" returns the parabolic constant(kp, seen in the thesis of ShuHuang 2022).
        //The Kp value can be estimated (or ignored if you are interested in the process output more).
        double Kp = KineticModel.KineticModel(WO3, WO2_9, WO2_72, WO2, W);

//


        //read kp database
        ArrayList<Double> temperatures = new ArrayList<>();
        ArrayList<Double> targets = new ArrayList<>();
        IOUtilities.kpReader(temperatures, targets);
        double tolerance = 0.1; double target = targets.get(index);

        if (Kp < 1e-4) Kp = 0;//too small kp is meaningless

        //double baseUnitofMassGain =  (Oxide.InterfaceConcentration4) * Oxide.rho_w_molar * Oxide.molarMass_oxy;//g/m^3
        //System.out.println("baseUnitofMassGain " + baseUnitofMassGain);
        print_kp.close();
        print_fittedkp.close();

    }//main
}//class


