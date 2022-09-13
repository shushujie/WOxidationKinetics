import java.util.List;

public class Oxide extends Materials{
//    double density;//concentration of Oxygen
//    double endPosition; //accurate position of its frontier
//    int sIter;//idx of the interface position
//    double Diffusivity;
//    String type;

    public Oxide(double density, int sIter, boolean lockFrontier) {
        this.density = density;
        this.endPosition = sIter * dx;
        this.lockFrontier = lockFrontier;

        //initialize diffusivity
        if (density == InterfaceConcentration1) {
            type = "WO3";
            Diffusivity = DiffInOxide3_0 * Math.exp(-MigrationEnergyInWO3 / (Boltzmanns * ConcentrationOfOxigen.temperature));

        }
        else if (density == InterfaceConcentration2) {
            type = "WO2_9";
            Diffusivity = DiffInOxide3_0 * Math.exp(-Em_wo2d9 / (Boltzmanns * ConcentrationOfOxigen.temperature));
        }
        else if (density == InterfaceConcentration3) {
            type = "WO2_72";
            Diffusivity = DiffInOxide3_0 * Math.exp(-Em_wo2d72 / (Boltzmanns * ConcentrationOfOxigen.temperature));
        }
        else if (density == InterfaceConcentration4) {
            type = "WO2";
            Diffusivity = Diffusivity_0WO2 * Math.exp(-MigrationEnergyInWO2 / (Boltzmanns * ConcentrationOfOxigen.temperature));
        }
    }

    public void set() {
        int diff = sIter - (int)(endPosition / dx);
        endPosition += diff * dx;
    }

    public void Pos2Iter() {
        sIter = (int)(endPosition / dx);
    }



    /*
    double DiffInOxide3_0 = 6.8e-6;
    double DiffInOxide3 = DiffInOxide3_0 * Math.exp(-Em_wo2d72 / (ProfileUtilities.Boltzmanns * temperature));
     */


}
