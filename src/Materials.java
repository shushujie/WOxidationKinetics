import java.security.PublicKey;

public class Materials {
    public double density;
    public double endPosition;
    public int sIter;//idx of the interface position
    public double Diffusivity;
    public String type;
    public boolean lockFrontier;
    public Materials() {
        //default value of material frontier is at the end of the whole depth
        density = 1;
        sIter = ConcentrationOfOxigen.depthIterations - 1;
        endPosition = sIter * dx;
        type = "W";
    }


    public static double Boltzmanns = 8.617e-5; //eV/K;
    public static double Diffusivity_0InW = 1e-6;//m^2/s The Diffusivity_0InW of Oxygen in Tungsten
    public static double Diffusivity_0InWO3 = 6.83e-6;//m^2/s The Diffusivity_0InW of Oxygen in Tungsten//https://doi.org/10.1016/0010-938X(80)90092-X
    public static double SurfaceOxygen = 3.5;//should be roughly 44.6 mole/(m^3) in air;//1000/22.4;
    public static double InterfaceConcentration1 = 3.0;
    public static double InterfaceConcentration2 = 2.9;
    public static double InterfaceConcentration3 = 2.722;
    public static double InterfaceConcentration4 = 2.0;

    //Oxy In WO3, WO2
    static double DiffInOxide3_0 = 6.83e-6;//m^2/s //(check also)8.3e-6; //The Diffusivity_0InW of Oxygen in Tungsten trioxide//https://doi.org/10.1016/0010-938X(80)90092-X
    static double MigrationEnergyInWO3 = 1.296153; //https://doi.org/10.1016/0010-938X(80)90092-X

    static double Diffusivity_0WO2 = 3.087e-5; //1.8523718e-5 * 5 / 3;//m^2/s
    static double MigrationEnergyInWO2 =4.4;//3.51;//4.4;//0.6;//1.86; //1.65+1.86;//by my dft calculation: 1.86eV is Em

    //Oxy In WO2.9, WO2.72
    static double Em_wo2d9 = 1.4;//eV
    static double Em_wo2d72 = 1.22;//eV



    //Oxy In tungsten
    static double D0_InW = 1e-6;//1.3e-4;//1e-6;//1.3 cm2/s
    static double Em_InW = 0.15;//1.04964;//2.7;//eV         //https://www.nature.com/articles/199337a0.pdf :D0_InW = 1e-6; Em_InW = 2.7;
    //https://www.nature.com/articles/2001310a0.pdf :D0_InW = 1.3e-4;Em_InW = 1.04964;
//0.15;//from Marc

    static double dx = ConcentrationOfOxigen.dx;
    static double dt = ConcentrationOfOxigen.dt;


    //mass gain related:
    public static double molarMass_w = 183.84;////g/mole
    public static double molarMass_oxy = 16;////g/mole
    public static double rho_w_mass = 19.3;//////19.3g/cc//19.3/183.84 * (1e6) mole/(m^3) //19.3/183.84 mole/cc
    public static double rho_w_molar = rho_w_mass / molarMass_w * 1e6;//[mole/(m^3)]//19.3/183.84 mole/cc * 1e6 ~= 1.05e5 mole/(m^3)

}
