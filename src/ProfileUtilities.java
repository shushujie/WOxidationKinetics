import java.util.ArrayList;
import java.util.List;


public class ProfileUtilities {

    public static double dt = ConcentrationOfOxigen.dt;
    public static double dx = ConcentrationOfOxigen.dx;
    public static int depth = ConcentrationOfOxigen.depthIterations;

    //implement Initial condition:
    public static void InitialCondition(List<Double> Concentration, double dx, int depthIterations,
                                        double SurfaceOxygen,
                                        Oxide WO3, Oxide WO2_9, Oxide WO2_72, Oxide WO2
                                        ) {
        int sIter_1 = WO3.sIter;
        int sIter_2 = WO2_9.sIter;
        int sIter_3 = WO2_72.sIter;
        int sIter_4 = WO2.sIter;

//        Boundary condition: Surface concentration
        Concentration.add(SurfaceOxygen);
        for(int i = 1; i <=sIter_1; i++)
            Concentration.add(WO3.density);
        for(int i =sIter_1 + 1;  i <= sIter_2; i++)
            Concentration.add(WO2_9.density);
        for(int i =sIter_2 + 1;  i <= sIter_3; i++)
            Concentration.add(WO2_72.density);
        for(int i =sIter_3 + 1;  i <= sIter_4; i++)
            Concentration.add(WO2.density);

        for (int i = sIter_4 + 1; i <= depthIterations; i++) {
            Concentration.add(WO2.density);

        }
    }

    //getting getGradient of the previous time step(step [j - 1]):

    public static double getGradient(List<Double> PreviousConcentration, long j, int i, double dx, boolean Left)  {
        if(Left) {
            return (PreviousConcentration.get(i) - PreviousConcentration.get(i - 1)) / dx;
        }

        if (i + 1 >= depth) return 0;

        return (PreviousConcentration.get(i + 1) - PreviousConcentration.get(i)) / dx;
    }

    //add new C_O point to current list of C_O
    //if the function UpdateConcentration is applied for a segment of a pure phase, give interfaceConcentration a dummy value of 0
    public static double UpdateConcentration(List<Double> PreviousConcentration,List<Double> OxygenConcentration,
                                             int startDepth, int enddepth, int oldEndDepth,
                                             long timeIter, Materials leftMaterial, Materials rightMaterial,
                                             boolean IsAtInterface,
                                             double interfaceConcentration

                                            ) {
        double ChangeRate = 0;//(net flux maintained at the current grid point)
        double Diff_left = leftMaterial.Diffusivity;
        double Diff_right = rightMaterial.Diffusivity;

        for (int i = startDepth; i < oldEndDepth; i++) {
            double gradient_left = IsAtInterface ? getGradient( PreviousConcentration, timeIter,  i - 1, dx, true) : getGradient( PreviousConcentration, timeIter,  i, dx, true);
            double gradient_right = getGradient( PreviousConcentration, timeIter,  i, dx, false);

            ChangeRate = (Diff_right * gradient_right - Diff_left * gradient_left) / dx;

            //At an interface:
            if(IsAtInterface) {return ChangeRate * dt;}
//          if(!IsAtInterface)
            OxygenConcentration.add(PreviousConcentration.get(i) + ChangeRate * dt ); //at i * dx, timeIter(j)*dt,
        }

        for (int i = oldEndDepth; i <= enddepth; i++) {
            OxygenConcentration.add(interfaceConcentration);
        }
        //complete the last profile point which is the interface
        //when it is !IsAtInterface:
            //OxygenConcentration.add(enddepth, interfaceConcentration);

        //returns the change of concentration. dc/dt = d^2c/dx^2 -> the method returns dc = d^2c/dx^2 * dt
            return ChangeRate * dt;
    }


//, boolean isStefan
    public static double GetNewInterface(List<Double> OxygenConcentration, double flux, Oxide currOxide) {
        double v_s = flux / currOxide.density;
        double ds = v_s * dt;
        currOxide.endPosition += ds;

        currOxide.sIter = (int)(currOxide.endPosition / dx);
        return ds;
    }

    public static void pushRight(double ds, List<Oxide> oxides) {
        for (Oxide oxide : oxides) {
            oxide.endPosition += ds;
            oxide.Pos2Iter();
        }
    }
    //, boolean NotStefan
    public static int GetNewInterface( List<Double> OxygenConcentration, double amount, Oxide currOxide, boolean NotStefan) {
//keep consuming the current:
//        System.out.println("amount "+(amount));
        while (amount > 0) {
//            if(currOxide.type == "WO2") System.out.println("WO2");
            if (currOxide.sIter + 1>= ConcentrationOfOxigen.depthIterations) {
                return currOxide.sIter;
            }

            //[oxygen] of the next layer of the interface:
//            System.out.println("OxygenConcentration.size() "+OxygenConcentration.size());
            double cntOfLayerToFill = OxygenConcentration.get(currOxide.sIter + 1);
            double gap = currOxide.density - cntOfLayerToFill;
//            System.out.println("at "+(currOxide.sIter + 1)+" cntOfLayerToFill "+ ( cntOfLayerToFill) );

            //In the if condition, to time Area on both sides of the expression to make sense
            if(amount >= dx * gap) {

                amount -= dx * gap;
                OxygenConcentration.set(currOxide.sIter + 1, currOxide.density);
                currOxide.sIter++;
                currOxide.endPosition += dx;
                //System.out.println("New interface of s" + order + " is forming, Current sIter_" + order+": " + sIter);
            }
            else {
//                System.out.println("Type " + currOxide.type);

                currOxide.endPosition += (amount / currOxide.density);
                if(currOxide.endPosition >= currOxide.sIter * dx) {
                    currOxide.sIter += (int)((currOxide.endPosition - currOxide.sIter * dx) / dx);
                }
                OxygenConcentration.set(currOxide.sIter + 1, cntOfLayerToFill + amount / dx);

//                System.out.println("OxygenConcentration.get("+(currOxide.sIter + 1)+") "+ OxygenConcentration.get(currOxide.sIter + 1));
                break;
            }


        }
//        System.out.println("currOxide.endPosition "+ currOxide.endPosition);
        return currOxide.sIter;
    }



    public static double GetMassGain(
            int sIter_1,
            int sIter_2,
            int sIter_3,
            int sIter_4
    ) {
        //Calculate total oxygen amount: //in density:[g/(m^3)]
        double result = 0;
        //for each oxide: mass gain = len * [Oxy](:n[W]) * M_W[mole/(m^3)] * m_Oxy g/mole
        //in short, it is len *(g/m^3)
        result += sIter_1 * ConcentrationOfOxigen.dx * (Oxide.InterfaceConcentration1) * Oxide.rho_w_molar * Oxide.molarMass_oxy;
        result += (sIter_2 - sIter_1) * ConcentrationOfOxigen.dx * (Oxide.InterfaceConcentration2) * Oxide.rho_w_molar * Oxide.molarMass_oxy;
        result += (sIter_3 - sIter_2) * ConcentrationOfOxigen.dx * (Oxide.InterfaceConcentration3) * Oxide.rho_w_molar * Oxide.molarMass_oxy;
        result += (sIter_4 - sIter_3) * ConcentrationOfOxigen.dx * (Oxide.InterfaceConcentration4) * Oxide.rho_w_molar * Oxide.molarMass_oxy;
        //result -= (sIter_4) * ConcentrationOfOxigen.dx * (Oxide.InterfaceConcentration4) * Oxide.rho_w_molar * Oxide.molarMass_oxy;
        return result;
    }




}


