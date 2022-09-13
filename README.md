# WOxidationKinetics
## This repository is created for maintaining code of Marian's Group, UCLA.
### by Shu Huang, 09/2022


The package is aimed to simulate the process of tungsten's oxidation ,
while five different phases are involved with temperature range of (600 - 1300 degree C);

1. The entrance class is ConcentrationOfOxigen.java
2. A schematic hierarchy:

        ConcentrationOfOxigen
             ↓
        KineticModel
             ↓
        SolveDiffusionODE
             ↓
        ProfileUtilities




*More:*
 1. Simulations with time range large than 10^4*dt are recommended to perform on clusters. Local machines are also ok for similar or less time steps.

