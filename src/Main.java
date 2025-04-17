

import java.util.ArrayList;



//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {

    public static void main(String[] args) {


        // Initialize the database with empty lists
        Database db = new Database(new ArrayList<>(), new ArrayList<>());

        int seed = 28;

        String[] databases = {"AP", "balance-scale", "BK", "cancer", "CH", "diabetes", "glass", "heart", "Iris", "NT", "LW", "sonar", "yacht"};


        for (String database : databases) {

            db.readDB("../benchmark/" + database + ".dat");
            System.out.println("\n");
            System.out.println("Database: " + database);


            //db.showDB();     // print the database in the console
            //db.getDBStatistics();   // prints the database charachteristics
            //db.showDistinctValues();    // prints the distinct values present in each attribute of the database


            //----------------------------------------Testing FIPS ---------------------------//

            System.out.println("TESTING FIPS: ");
            FIPS fips = new FIPS(db, seed);

            for (int i = 0; i < 10; i++) {
                IP fipsIP = fips.drawIP();
                fipsIP.showIP();
                fipsIP.showCoverage();
            }


            //----------------------------------------Testing HFIPS ---------------------------//

            System.out.println("TESTING HFIPS: ");
            HFIPS hfips = new HFIPS(db, seed);

            for (int i = 0; i < 10; i++) {
                IP hfipsIP = hfips.drawIP();
                hfipsIP.showIP();
                hfipsIP.showCoverage();
            }

            //----------------------------------------Testing UNIFORM SAMPLING WITHOUT ENSURING NON EMPTY COVERAGE  ---------------------------//
            System.out.println("TESTING UNIFORM SAMPLING (WITHOUT COVERAGE CONTROL): ");

            TotalyRandomIPSampling totalyrandSampling = new TotalyRandomIPSampling(db);

            for (int i = 0; i < 10; i++) {
                IP totalyrandsampling = totalyrandSampling.drawIP();
                totalyrandsampling.showIP();
                totalyrandsampling.showCoverage();
            }


            //----------------------------------------Testing UNIFORM SAMPLING ENSURING NON EMPTY COVERAGE---------------------------//
            System.out.println("TESTING UNIFORM (ENSURING NON EMPTY COVERAGE: ");
            Uniform uniformSampling = new Uniform(db, seed);

            for (int i = 0; i < 10; i++) {
                IP uniformIP = uniformSampling.drawIP();
                uniformIP.showIP();
                uniformIP.showCoverage();
            }


            Evaluation e = new Evaluation();


            ////////////////////////////////////////////////////////////////////////////////////////////////////////////
            ////////////////////////// Uncomment the experiment you want to lunch//////////////////////////////////////
            ///////////////////////////////////////////////////////////////////////////////////////////////////////////

            //-----------------------------------------------------Density Evaluation-----------------//
            //System.out.println("-----------FIPS:");
            //e.DensityEvaluationFIPS(db,seed,1000);

            //System.out.println("-----------HIPS:");
            //e.DensityEvaluationHIPS(db,seed,1000);

            //System.out.println("-----------Uniform:");
            //e.DensityEvaluationUniform(db,seed,1000);


            //--------------------------Volume evaluation ----------------------------------------/µ

            //e.evolutionVolumeHIPS(seed,500,10,db,"results/Volume/HIPS/"+database+"_volume_Evolution_HIPS.csv");
            //  e.evolutionVolumeFIPS(seed,500,10,db,"results/Volume/FIPS/"+database+"_volume_Evolution_FIPS.csv");
            //e.evolutionVolumeRandIP(seed,500,10,db,"results/Volume/RANDOM/"+database+"_volume_Evolution_Random.csv");




            //--------------------------Volume times frequency evaluation ----------------------------------------/µ

            //e.evolutionVolumeTimesFrequencyHIPS(seed,500,10,db,"results/Volume/HIPS/"+database+"_volume_Evolution_HIPS.csv");
            //e.evolutionVolumeTimesFrequencyFIPS(seed,500,10,db,"results/Volume/FIPS/"+database+"_volume_Evolution_FIPS.csv");
            //e.evolutionVolumeUniform(seed,500,10,db,"results/Volume/RANDOM/"+database+"_volume_Evolution_Random.csv");

            //----------------------------------- Percentage of empty coverages for totaly uniform sampling
            // without coverage control ---------------------------------------------------------------------------//

            //System.out.println("Totaly random IP: Percentage of empty coverage "+e.EvaluationEmptyCoverage(10000,db));


            //------------------------------Frequency based Plausibility ---------------------------------------//

            //System.out.println("Plausibilité for Frequency FIPS: "+e.PlausibilityFrequencyIP(seed,10000,10,db));
            //System.out.println("Plausibilité for Frequency UNIFORM: "+e.PlausibilityFrequencyRandIP(seed,10000,10,db));
            // System.out.println("Plausibilité for Frequency HFIPS: "+e.PlausibilityFrequencyHIPS(seed,10000,10,db));


            //-------------------------------------------Diversity evaluation in  term of equivalence classes -----//

            // System.out.println("FIPS: Diversité soulet: "+e.eqClassDiversityIP(seed,100000, db));
            //System.out.println("HIPS: Diversité soulet: "+e.eqClassDiversityHIPS(seed,100000, db));
            //System.out.println("UNIFORM: Diversité soulet: "+e.eqClassDiversityRandomIP(seed,100000, db));

            //-------------------------------------Diversity evaluation objects overlapping -----//

            //e.jaccardCDFIP(seed,500,db,"results/JaccardCDF/FIPS/"+ database +"_JaccardCDF_FIPS.csv" );
            //e.jaccardCDFHIPS(seed,500,db,"results/JaccardCDF/HIPS/"+ database +"_JaccardCDF_HIPS.csv" );
            //e.jaccardCDFRandomIP(seed,500,db,"results/JaccardCDF/RandomIP/"+ database +"_JaccardCDF_Random_IP.csv" );


            //-----------------------------Frequency evaluation -------------------------------------//


            //e.evolutionFrequenceFIPS(seed,500,db,"results/newFrequency/FIPS/"+database+"_frequency_Evolution_FIPS.csv");
            //e.evolutionFrequenceRandomIP(seed,500,db,"results/newFrequency/RandomIP/"+database+"_frequency_Evolution_RandomIP.csv");
            //e.evolutionFrequenceHIPS(seed,500,db,"results/newFrequency/HIPS/"+database+"_frequency_Evolution_HIPS.csv");


            //-------------------------Temps cpu evaluation -------------------------------------------//

            //e.evolutionTempsCPUFIPS(seed,500, 10,db,"results/CpuEvolution/FIPS/"+ database +"_CPU_Evolution_FIPS.csv");
            // e.evolutionTempsCPUHIPS(seed,500, 10,db,"results/CpuEvolution/HIPS/"+ database +"_CPU_Evolution_HIPS.csv");
            //e.evolutionTempsCPUUniform(seed,500, 10,db,"results/CpuEvolution/RandomIP/"+ database +"_CPU_Evolution_Random_IP.csv");


        }


    }

}
