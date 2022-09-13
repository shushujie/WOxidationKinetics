import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SolveDiffusionODE {
    static boolean SolveODE(ArrayList<Double> PreviousConcentration, ArrayList<Double> OxygenConcentration, ArrayList<Integer> sIterArray,
                            long j,
                            Oxide WO3, Oxide WO2_9, Oxide WO2_72, Oxide WO2,
                            Materials W

    ) throws IOException {

        double SurfaceOxygen = Oxide.SurfaceOxygen;
        double InterfaceConcentration1 = Oxide.InterfaceConcentration1;//3.0;
        double InterfaceConcentration2 = Oxide.InterfaceConcentration2;//2.9;
        double InterfaceConcentration3 = Oxide.InterfaceConcentration3;//2.722;
        double InterfaceConcentration4 = Oxide.InterfaceConcentration4;//2.0;
        boolean LockS = ConcentrationOfOxigen.LockS, LockS2 = ConcentrationOfOxigen.LockS2, LockS3 = ConcentrationOfOxigen.LockS3, LockS4 = ConcentrationOfOxigen.LockS4;//true;



        //boundary condition; at depth i = 0; for the loop to add the follow positions;
        OxygenConcentration.add( SurfaceOxygen );//OxygenConcentration is the concentration profile at time j


        int depthIterations = ConcentrationOfOxigen.depthIterations;

        if (!IsValid(WO3.sIter, WO2_9.sIter, WO2_72.sIter, WO2.sIter)) {
            return false;
        }

        double dx = ConcentrationOfOxigen.dx;
        double dt = ConcentrationOfOxigen.dt;





        /****** Handle the interfaces: ******/
// 2.At the WO3/WO2.9 interface, calculate how much the interface is going to move forward
        double Ats_1 = ProfileUtilities.UpdateConcentration(PreviousConcentration,OxygenConcentration,
                WO3.sIter,  WO3.sIter + 1, WO3.sIter + 1, j,
                WO3, WO2_9, true, InterfaceConcentration1);//returns the concentration change at s_1
        Ats_1 = (Ats_1 > 0 && (!LockS)) ? Ats_1 : 0;//stabilize the profile if the net accumulation of oxygen at the interface is negative, instead of change the interface's position
        if (!LockS) {
            double ds_1 = ProfileUtilities.GetNewInterface(PreviousConcentration, Ats_1, WO3);
            ProfileUtilities.pushRight(ds_1, Arrays.asList(WO2_9, WO2_72, WO2));
        }


// 4.At the the WO2.9/WO2.72 interface, calculate how much the interface is going to move forward.
        if (!IsValid(WO3.sIter, WO2_9.sIter, WO2_72.sIter, WO2.sIter)) return false;

        double Ats_2 = ProfileUtilities.UpdateConcentration(PreviousConcentration,OxygenConcentration,
                WO2_9.sIter,  WO2_9.sIter + 1, WO2_9.sIter + 1, j,
                WO2_9, WO2_72, true, InterfaceConcentration2);
        Ats_2 = (Ats_2 > 0 && (!LockS2)) ? Ats_2 : 0;//stabilize the profile if the net accumulation of oxygen at the interface is negative, instead of change the interface's position
        if (!LockS2) {
            double ds_2 = ProfileUtilities.GetNewInterface(PreviousConcentration, Ats_2, WO2_9);
            ProfileUtilities.pushRight(ds_2, Arrays.asList(WO2_72, WO2));
        }

//6. At the the WO2.72/WO2 interface
        if (!IsValid(WO3.sIter, WO2_9.sIter, WO2_72.sIter, WO2.sIter)) return false;

        double Ats_3 = ProfileUtilities.UpdateConcentration(PreviousConcentration,OxygenConcentration,
                WO2_72.sIter,  WO2_72.sIter + 1, WO2_72.sIter + 1, j,
                WO2_72, WO2, true, InterfaceConcentration3);

        Ats_3 = (Ats_3 > 0 && (!LockS3)) ? Ats_3 : 0;
        if (!LockS3) {
            double ds_3 = ProfileUtilities.GetNewInterface(PreviousConcentration, Ats_3, WO2_72);
            ProfileUtilities.pushRight(ds_3, Arrays.asList(WO2));
        }



//8. At the WO2/W(near metal) interface, calculate how much the interface is going to move forward.
        if (!IsValid(WO3.sIter, WO2_9.sIter, WO2_72.sIter, WO2.sIter)) {
//            System.out.print("No valid At the 4th interface");
            return false;
        }

        //change of concentration:
        double Ats_4 = ProfileUtilities.UpdateConcentration(PreviousConcentration, OxygenConcentration, WO2.sIter, WO2.sIter + 1,WO2.sIter + 1, j,
                    WO2, W, true, InterfaceConcentration4);


        Ats_4 = (Ats_4 > 0 && (!LockS4)) ? Ats_4 : 0;//stabilize the profile if the net accumulation of oxygen at the interface is negative, instead of change the interface's position
//        if(j % 1000 == 0) System.out.println("Ats_4 "+Ats_4);
        ProfileUtilities.GetNewInterface( PreviousConcentration, Ats_4, WO2);

        if(WO2.sIter >= depthIterations - 2) { WO2.sIter = depthIterations - 2; }//System.out.print("s4>end "); }





        /****** Profile consider no interface motion ******/
        //1.Topmost Oxide part profile //[start_depth, end_depth): with endDepth excluded
        ProfileUtilities.UpdateConcentration(PreviousConcentration, OxygenConcentration,
                1,  WO3.sIter, sIterArray.get(0), j,  WO3, WO3, false, InterfaceConcentration1);

        //3.between 1st and 2nd interfaces
        ProfileUtilities.UpdateConcentration(PreviousConcentration, OxygenConcentration,
                WO3.sIter + 1,  WO2_9.sIter, sIterArray.get(1), j,  WO2_9, WO2_9, false, InterfaceConcentration2);

        //5.between 2nd and 3rd interfaces
        ProfileUtilities.UpdateConcentration(PreviousConcentration, OxygenConcentration,
                WO2_9.sIter + 1,  WO2_72.sIter, sIterArray.get(2), j,  WO2_72, WO2_72, false, InterfaceConcentration3);

        //7.between 3rd and 4th interfaces
        ProfileUtilities.UpdateConcentration(PreviousConcentration, OxygenConcentration,
                WO2_72.sIter + 1,  WO2.sIter, sIterArray.get(3), j,  WO2, WO2, false, InterfaceConcentration4);

        //9.Metal part profile
        ProfileUtilities.UpdateConcentration(PreviousConcentration, OxygenConcentration,
                WO2.sIter + 1,  depthIterations - 1, depthIterations - 1, j,  W, W, false,0);//intfaceCon is useless here
        //BC(boundary condition):This is the 0 flux boundary condition at the far end;
        // get value at ("depth of film thickness" - dx):
        double valueAtL_dx = OxygenConcentration.get(depthIterations - 2);//
        // at every time j,get the left value before the last point(far end boundary point)
        OxygenConcentration.set(depthIterations - 1, valueAtL_dx );//finish the last position of the Oxy profile at time j and at (depthIterations - 1) * dx;

        sIterArray.set(0, WO3.sIter);
        sIterArray.set(1, WO2_9.sIter);
        sIterArray.set(2, WO2_72.sIter);
        sIterArray.set(3, WO2.sIter);

        return true;

    }

    public static boolean IsValid(int sIter_1, int sIter_2, int sIter_3, int sIter_4) {
        if(     sIter_1 + 1 >= ConcentrationOfOxigen.depthIterations ||
                sIter_2 + 1>= ConcentrationOfOxigen.depthIterations ||
                sIter_3 + 1>= ConcentrationOfOxigen.depthIterations ||
                sIter_4 > ConcentrationOfOxigen.depthIterations
        ) {
//          System.out.println("sIter_1 "+ sIter_1+", sIter_2 "+sIter_2+", sIter_3 "+ sIter_3 +", sIter_4 "+ sIter_4 );
            return false;
        }
        return true;
    }

    public static double dx = ConcentrationOfOxigen.dx;
    public static double dt = ConcentrationOfOxigen.dt;
}
