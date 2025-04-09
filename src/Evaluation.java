import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;


// here you can find all the function corresponding to the experimental protocol in the paper
public class Evaluation {

    public void meanFrequencyAndVolumeHIPS(Database db, int seed, int k){
        HFIPS HFIPS = new HFIPS(db,seed);
        List<Double> patternsFreq = new ArrayList<>();
        List<BigDecimal> patternsVolume = new ArrayList<>();
        // draw the patterns
        for (int i = 0; i< k; i++){
            IP pattern = HFIPS.drawIP();
            patternsFreq.add(pattern.getFrequency());
            patternsVolume.add(pattern.getVolume());
        }

        calculateStatsForDouble(patternsFreq);
        calculateStatsForBigDecimal(patternsVolume);
    }


    public void meanFrequencyAndVolumeFIPS(Database db, int seed, int k){
        FIPS fips= new FIPS(db,seed);
        List<Double> patternsFreq = new ArrayList<>();
        List<BigDecimal> patternsVolume = new ArrayList<>();
        // draw the patterns
        for (int i = 0; i< k; i++){
            IP pattern = fips.drawIP();
            patternsFreq.add(pattern.getFrequency());
            patternsVolume.add(pattern.getVolume());
        }

        calculateStatsForDouble(patternsFreq);
        calculateStatsForBigDecimal(patternsVolume);


    }


    public static void calculateStatsForDouble(List<Double> data) {
        if (data.isEmpty()) {
            System.out.println("Liste vide");
            return;
        }

        // Moyenne
        OptionalDouble avg = data.stream().mapToDouble(Double::doubleValue).average();
        double mean = avg.orElse(0.0);

        // Écart-type
        double variance = data.stream()
                .mapToDouble(d -> Math.pow(d - mean, 2))
                .average()
                .orElse(0.0);

        double stddev = Math.sqrt(variance);

        System.out.println("Moyenne Fréquence : " + mean + ", Écart-type : " + stddev);
    }

    public static void calculateStatsForBigDecimal(List<BigDecimal> data) {
        if (data.isEmpty()) {
            System.out.println("Liste vide");
            return;
        }

        MathContext mc = new MathContext(10, RoundingMode.HALF_UP);

        // Moyenne
        BigDecimal sum = BigDecimal.ZERO;
        for (BigDecimal num : data) {
            sum = sum.add(num);
        }
        BigDecimal mean = sum.divide(new BigDecimal(data.size()), mc);

        // Variance et écart-type
        BigDecimal varianceSum = BigDecimal.ZERO;
        for (BigDecimal num : data) {
            BigDecimal diff = num.subtract(mean, mc);
            varianceSum = varianceSum.add(diff.pow(2, mc));
        }

        BigDecimal variance = varianceSum.divide(new BigDecimal(data.size()), mc);
        BigDecimal stddev = variance.sqrt(mc);

        System.out.println(" Moyenne Volume : " + mean + ", Écart-type : " + stddev);
    }



    public void expeVolumesurFrequence(Database db,int seed, int nbmotifs, String outputpathprefix, String outputpathsuffix){
        //1- tirer 500 motifs avec fips et hips
        // methodes
        FIPS fips = new FIPS(db, seed);
        HFIPS hfips = new HFIPS(db, seed);
        Uniform uniform = new Uniform(db, seed);

        // conserver les motifs ici
        ArrayList<IP> fipsIP = new ArrayList<>();
        ArrayList<IP> hipsIP = new ArrayList<>();
        ArrayList<IP> UniformIP = new ArrayList<>();

        //frequency
        ArrayList<Double> fipsFrequency = new ArrayList<>();
        ArrayList<Double> hipsIPFrequency = new ArrayList<>();
        ArrayList<Double> uniformIPFrequency = new ArrayList<>();

        for (int i =0; i < nbmotifs; i++){
            IP ipfips =fips.drawIP();
            IP iphips = hfips.drawIP();
            IP ipUniform =uniform.drawIP();

            // add the patterns:
            fipsIP.add(ipfips);
            hipsIP.add(iphips);
            UniformIP.add(ipUniform);
        }
        fipsIP.sort(Comparator.comparingDouble(IP::getFrequency));
        hipsIP.sort(Comparator.comparingDouble(IP::getFrequency));
        UniformIP.sort(Comparator.comparingDouble(IP::getFrequency));


        System.out.println("Moyennes et écarts types par groupe de 50 pour FIPS :");
        calculateAndPrintAveragesAndStdDev(fipsIP, 50,outputpathprefix+"FIPS/volumeAndFrequency_FIPS_"+outputpathsuffix+".csv");

        System.out.println("\nMoyennes et écarts types par groupe de 50 pour HIPS :");
        calculateAndPrintAveragesAndStdDev(hipsIP, 50,outputpathprefix+"HIPS/volumeAndFrequency_HIPS_"+outputpathsuffix+".csv");

        System.out.println("\nMoyennes et écarts types par groupe de 50 pour Uniform :");
        calculateAndPrintAveragesAndStdDev(UniformIP, 50,outputpathprefix+"UNIFORM/volumeAndFrequency_UNIFORM_"+outputpathsuffix+".csv");


    }



    public void expeDensity(Database db,int seed, int nbmotifs, String outputpathprefix, String outputpathsuffix){
        //1- tirer 500 motifs avec fips et hips
        // methodes
        FIPS fips = new FIPS(db, seed);
        HFIPS hfips = new HFIPS(db, seed);
        Uniform uniform = new Uniform(db, seed);

        // conserver les motifs ici
        ArrayList<IP> fipsIP = new ArrayList<>();
        ArrayList<IP> hipsIP = new ArrayList<>();
        ArrayList<IP> UniformIP = new ArrayList<>();


        for (int i =0; i < nbmotifs; i++){
            IP ipfips =fips.drawIP();
            IP iphips = hfips.drawIP();
            IP ipUniform =uniform.drawIP();

            // add the patterns:
            fipsIP.add(ipfips);
            hipsIP.add(iphips);
            UniformIP.add(ipUniform);
        }
        fipsIP.sort(Comparator.comparingDouble(IP::getFrequency));
        hipsIP.sort(Comparator.comparingDouble(IP::getFrequency));
        UniformIP.sort(Comparator.comparingDouble(IP::getFrequency));


        System.out.println("Moyennes et écarts types par groupe de 50 pour FIPS :");
        calculateAndPrintDensityAveragesAndStdDev(fipsIP, 50,outputpathprefix+"FIPS/Density/Density_FIPS_"+outputpathsuffix+".csv");

        System.out.println("\nMoyennes et écarts types par groupe de 50 pour HIPS :");
        calculateAndPrintDensityAveragesAndStdDev(hipsIP, 50,outputpathprefix+"HIPS/Density/Density_HIPS_"+outputpathsuffix+".csv");

        System.out.println("\nMoyennes et écarts types par groupe de 50 pour Uniform :");
        calculateAndPrintDensityAveragesAndStdDev(UniformIP, 50,outputpathprefix+"UNIFORM/Density/Density_UNIFORM_"+outputpathsuffix+".csv");


    }




    public void calculateAndPrintDensityAveragesAndStdDev(ArrayList<IP> ipList, int groupSize, String filePath) {
        DecimalFormat sciFormat = new DecimalFormat("0.###E0");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            // Écriture de l'en-tête CSV
            writer.write("Groupe\tMoyenne_Densité\tÉcart_Type_Densité\n");

            int groupNumber = 1;
            for (int i = 0; i < ipList.size(); i += groupSize) {
                int end = Math.min(i + groupSize, ipList.size());
                List<IP> group = ipList.subList(i, end);


                BigDecimal averageDensity = calculateAverageDensity(group);
                BigDecimal stdDevDensityBD = BigDecimal.valueOf(calculateStandardDeviationDensity(group, averageDensity));


                String avgDensitySci = sciFormat.format(averageDensity);
                String stdDevDensitySci = sciFormat.format(stdDevDensityBD);


                // Écriture des valeurs dans le fichier CSV
                writer.write(String.format("%d\t%s\t%s\n",
                        groupNumber, avgDensitySci, stdDevDensitySci));

                groupNumber++;
            }

            System.out.println("Résultats écrits dans " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




    public void calculateAndPrintAveragesAndStdDev(ArrayList<IP> ipList, int groupSize, String filePath) {
        DecimalFormat sciFormat = new DecimalFormat("0.###E0");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            // Écriture de l'en-tête CSV
            writer.write("Groupe\tMoyenne_Fréquence\tÉcart_Type_Fréquence\tMoyenne_Volume\tÉcart_Type_Volume\n");

            int groupNumber = 1;
            for (int i = 0; i < ipList.size(); i += groupSize) {
                int end = Math.min(i + groupSize, ipList.size());
                List<IP> group = ipList.subList(i, end);

                double averageFrequency = calculateAverageFrequency(group);
                //BigDecimal averageVolume = calculateAverageVolume(group);
                double stdDevFrequency = calculateStandardDeviationFrequency(group, averageFrequency);

                //double stdDevVolume = calculateStandardDeviationVolume(group, averageVolume);

                BigDecimal averageVolume = calculateAverageVolume(group);
                BigDecimal stdDevVolumeBD = BigDecimal.valueOf(calculateStandardDeviationVolume(group, averageVolume));

                BigDecimal averageDensity = calculateAverageDensity(group);


                String avgVolumeSci = sciFormat.format(averageVolume);
                String stdDevVolumeSci = sciFormat.format(stdDevVolumeBD);

                // Écriture des valeurs dans le fichier CSV
                writer.write(String.format("%d\t%.5f\t%.5f\t%s\t%s\n",
                        groupNumber, averageFrequency, stdDevFrequency,
                        avgVolumeSci, stdDevVolumeSci));

                groupNumber++;
            }

            System.out.println("Résultats écrits dans " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // Méthode pour calculer la moyenne des fréquences dans un groupe
    public  BigDecimal calculateAverageVolume(List<IP> group) {
        BigDecimal sum = BigDecimal.ZERO;
        for (IP ip : group) {
            sum = sum.add(ip.getVolume());
        }
        return sum.divide(BigDecimal.valueOf(group.size()), RoundingMode.HALF_UP);
    }

    public BigDecimal calculateAverageDensity(List<IP> group) {
        BigDecimal sum = BigDecimal.ZERO;
        int scale = 10;

        for (IP ip : group) {
            BigDecimal volume = ip.getVolume();

            // Vérification de volume nul ou zéro avant la division
            if (volume == null || volume.compareTo(BigDecimal.ZERO) == 0) {
                BigDecimal tempDensity = BigDecimal.ZERO;
                sum = sum.add(tempDensity);
            }else{
                // Calcul de la densité
                BigDecimal tempDensity = BigDecimal.valueOf(ip.getCoverage().size())
                        .divide(volume, scale, RoundingMode.HALF_UP);
                sum = sum.add(tempDensity);
            }
        }

        // Si tous les éléments étaient ignorés (volume nul ou zéro), retourne BigDecimal.ZERO
        if (sum.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        return sum.divide(BigDecimal.valueOf(group.size()), scale, RoundingMode.HALF_UP);
    }


    public  double calculateAverageFrequency(List<IP> group) {
        double sum = 0;
        for (IP ip : group) {
            sum += ip.getFrequency();
        }
        return sum / group.size();
    }

    // Méthode pour calculer l'écart type des fréquences dans un groupe
    private static double calculateStandardDeviationFrequency(List<IP> group, double average) {
        double sumSquaredDifferences = 0;
        for (IP ip : group) {
            sumSquaredDifferences += Math.pow(ip.getFrequency() - average, 2);
        }
        double variance = sumSquaredDifferences / group.size();
        return Math.sqrt(variance);
    }

    public  double calculateStandardDeviationVolume(List<IP> group, BigDecimal average) {
        BigDecimal sumSquaredDifferences = BigDecimal.ZERO;
        for (IP ip : group) {
            BigDecimal difference = ip.getVolume().subtract(average);
            sumSquaredDifferences = sumSquaredDifferences.add(difference.multiply(difference));
        }
        BigDecimal variance = sumSquaredDifferences.divide(BigDecimal.valueOf(group.size()), RoundingMode.HALF_UP);
        return Math.sqrt(variance.doubleValue());
    }


    public double calculateStandardDeviationDensity(List<IP> group, BigDecimal average) {
        if (group == null || group.isEmpty()) {
            throw new IllegalArgumentException("Group cannot be null or empty");
        }

        BigDecimal sumSquaredDifferences = BigDecimal.ZERO;
        int scale = 10;

        // Calcul des différences au carré pour chaque élément
        for (IP ip : group) {
            BigDecimal volume = ip.getVolume();

            // Vérification que le volume n'est pas nul ou égal à zéro
            if (volume == null || volume.compareTo(BigDecimal.ZERO) == 0) {
                // Si le volume est nul ou zéro, ignorer cet élément ou le traiter différemment
                BigDecimal tempDensity = BigDecimal.ZERO;
                // Calcul de la différence entre la densité et la moyenne
                BigDecimal difference = tempDensity.subtract(average);
                // Ajout du carré de la différence à la somme
                sumSquaredDifferences = sumSquaredDifferences.add(difference.multiply(difference));

            }else{
                BigDecimal tempDensity = BigDecimal.valueOf(ip.getCoverage().size())
                        .divide(volume, scale, RoundingMode.HALF_UP);

                // Calcul de la différence entre la densité et la moyenne
                BigDecimal difference = tempDensity.subtract(average);
                // Ajout du carré de la différence à la somme
                sumSquaredDifferences = sumSquaredDifferences.add(difference.multiply(difference));
            }

        }

        // Si aucun élément valide n'a été trouvé (tous ont été ignorés), retourner 0
        if (sumSquaredDifferences.compareTo(BigDecimal.ZERO) == 0) {
            return 0.0;
        }
        // Calcul de la variance
        BigDecimal variance = sumSquaredDifferences.divide(BigDecimal.valueOf(group.size()), RoundingMode.HALF_UP);

        // Retourner l'écart type sous forme de double
        return Math.sqrt(variance.doubleValue());
    }


    public void expeTableau_comparatif_frequence(Database db, int seed, int nbmotifs){

        //1- tirer 500 motifs avec fips et hips
            ArrayList<IP> fipsIP = new ArrayList<>();
            ArrayList<IP> hipsIP = new ArrayList<>();
            ArrayList<IP> UniformIP = new ArrayList<>();
            FIPS fips = new FIPS(db, seed);
            HFIPS hfips = new HFIPS(db, seed);
            Uniform uniform = new Uniform(db, seed);

        //frequency
        ArrayList<Double> fipsFrequency = new ArrayList<>();
        ArrayList<Double> hipsIPFrequency = new ArrayList<>();
        ArrayList<Double> uniformIPFrequency = new ArrayList<>();

            for (int i =0; i < nbmotifs; i++){
            IP ipfips =fips.drawIP();
            IP iphips = hfips.drawIP();
            IP ipUniform =uniform.drawIP();

            // add the patterns:
            fipsIP.add(ipfips);
            hipsIP.add(iphips);
            UniformIP.add(ipUniform);
            // add the frequencies of the patterns
                fipsFrequency.add(ipfips.getFrequency());
                hipsIPFrequency.add(iphips.getFrequency());
                uniformIPFrequency.add(ipUniform.getFrequency());
            }

        //2- Trier les valeurs par ordre croissant pour toutes les listes
        Collections.sort(fipsFrequency);
        Collections.sort(hipsIPFrequency);
        Collections.sort(uniformIPFrequency);

        // Trier les volumes
        List<double[]> IntervalsThresholds = Arrays.asList(
                new double[]{0.00, 0.10},
                new double[]{0.11, 0.20},
                new double[]{0.21, 0.30},
                new double[]{0.31, 0.40},
                new double[]{0.41, 0.50},
                new double[]{0.51, 0.60},
                new double[]{0.61, 0.70},
                new double[]{0.71, 0.80},
                new double[]{0.81, 0.90},
                new double[]{0.91, 1.00}
        );

        System.out.printf("%-15s %-10s %-10s %-10s%n", "Interval", "FIPS", "HIPS", "Uniform");
        System.out.println("--------------------------------------------------");

        for (double[] interval : IntervalsThresholds) {
            int fipsCount = countInRange(fipsFrequency, interval[0], interval[1]);
            int hipsCount = countInRange(hipsIPFrequency, interval[0], interval[1]);
            int uniformCount = countInRange(uniformIPFrequency, interval[0], interval[1]);

            System.out.printf("[%.2f - %.2f]  %-10d %-10d %-10d%n", interval[0], interval[1], fipsCount, hipsCount, uniformCount);
        }
        // 3- Diviser la gaussienne par 10 et voir combien de motifs on à dans chaque pool


        // 4- pour les pool de motifs de chaque approche faire:

        //4-1 Calculer la moyenne et l'écart type des fréquences


        //4-2 Calculer la moyenne et l'écart type des volumes
    }

    private int countInRange(List<Double> values, double min, double max) {
        int count = 0;
        for (double value : values) {
            if (value >= min && value <= max) {
                count++;
            }
        }
        return count;
    }
    public static void writeCSVvol(String fileName, Map<BigDecimal, Integer> data) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for (Map.Entry<BigDecimal, Integer> entry : data.entrySet()) {
                writer.write(entry.getKey() + "," + entry.getValue());
                writer.newLine(); // Move to next line
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void writeCSVfreq(String fileName, Map<Double, Integer> data) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for (Map.Entry<Double, Integer> entry : data.entrySet()) {
                writer.write(entry.getKey() + "," + entry.getValue());
                writer.newLine(); // Move to next line
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




    public void volumeAndFrequencyUniform(int seed, int k, int rep, Database db){
        Uniform fips = new Uniform(db, seed);
        double moyfrequence = 0.0;
        BigDecimal moyvolume = BigDecimal.ZERO;

        for(int i=0; i <rep; i++ ){
            double Tempmoyfrequence = 0.0;
            BigDecimal Tempmoyvolume = BigDecimal.ZERO;

            for(int j=0; j <k; j++ ){
                IP ip = fips.drawIP();
                Tempmoyfrequence += ip.getFrequency();
                Tempmoyvolume = Tempmoyvolume.add(ip.processVolume());
            }
            moyfrequence += Tempmoyfrequence;
            moyvolume = moyvolume.add(Tempmoyvolume);

        }
        moyfrequence /= (rep * k);
        moyvolume = moyvolume.divide(BigDecimal.valueOf(rep * k), RoundingMode.HALF_UP);
        DecimalFormat sciFormat = new DecimalFormat("0.###E0");
        System.out.println("***UNIFORM***");
        System.out.println("La fréquence moyenne est de  "+ moyfrequence );
        System.out.println("Le volume moyen est de "+sciFormat.format(moyvolume));

    }


    public void volumeAndFrequencyHIPS(int k, int rep,int seed, Database db){
        HFIPS fips = new HFIPS(db,seed);
        double moyfrequence = 0.0;
        BigDecimal moyvolume = BigDecimal.ZERO;

        for(int i=0; i <rep; i++ ){
            double Tempmoyfrequence = 0.0;
            BigDecimal Tempmoyvolume = BigDecimal.ZERO;

            for(int j=0; j <k; j++ ){
                IP ip = fips.drawIP();
                Tempmoyfrequence += ip.getFrequency();
                Tempmoyvolume = Tempmoyvolume.add(ip.processVolume());
            }
            moyfrequence += Tempmoyfrequence;
            moyvolume = moyvolume.add(Tempmoyvolume);

        }

        moyfrequence /= (rep * k);
        moyvolume = moyvolume.divide(BigDecimal.valueOf(rep * k), RoundingMode.HALF_UP);
        DecimalFormat sciFormat = new DecimalFormat("0.###E0");
        System.out.println("***HIPS***");
        System.out.println("La fréquence moyenne est de  "+ moyfrequence );
        System.out.println("Le volume moyen est de "+sciFormat.format(moyvolume));



    }


    public void volumeAndFrequencyFIPS(int seed,int k, int rep, Database db){
        FIPS fips = new FIPS(db, seed);
        double moyfrequence = 0.0;
        BigDecimal moyvolume = BigDecimal.ZERO;

        for(int i=0; i <rep; i++ ){
            double Tempmoyfrequence = 0.0;
            BigDecimal Tempmoyvolume = BigDecimal.ZERO;

            for(int j=0; j <k; j++ ){
                IP ip = fips.drawIP();
                Tempmoyfrequence += ip.getFrequency();
                Tempmoyvolume = Tempmoyvolume.add(ip.processVolume());
            }
            moyfrequence += Tempmoyfrequence;
            moyvolume = moyvolume.add(Tempmoyvolume);

        }
        moyfrequence /= (rep * k);
        moyvolume = moyvolume.divide(BigDecimal.valueOf(rep * k), RoundingMode.HALF_UP);
        DecimalFormat sciFormat = new DecimalFormat("0.###E0");
        System.out.println("***FIPS***");
        System.out.println("La fréquence moyenne est de  "+ moyfrequence );
        System.out.println("Le volume moyen est de "+sciFormat.format(moyvolume));
    }

    public void DensityEvaluationFIPS(Database db, int seed, int NBPatterns) {

        /* * Méthode qui calcule la densité de NBPatterns motifs issus de FIPS
         *
         * */

        FIPS fips = new FIPS(db, seed);
        ArrayList<BigDecimal> densites = new ArrayList<>();
        BigDecimal densiteSomme = BigDecimal.ZERO;
        int scale = 10; // Échelle de précision pour éviter l'arrondi à zéro

        // Calcul de la densité moyenne
        for (int i = 0; i < NBPatterns; i++) {
            IP ip = fips.drawIP();
            BigDecimal coverageSize = BigDecimal.valueOf(ip.getCoverage().size());
            BigDecimal volume = ip.getVolume(); // Assurer la précision

            if (volume.compareTo(BigDecimal.ZERO) == 0) {
                BigDecimal tempDensite = BigDecimal.ZERO;

                densites.add(tempDensite);
                densiteSomme = densiteSomme.add(tempDensite);
                continue; // Éviter la division par zéro
            }else {
                BigDecimal tempDensite = coverageSize.divide(volume, scale, RoundingMode.HALF_UP);

                densites.add(tempDensite);
                densiteSomme = densiteSomme.add(tempDensite);
            }


        }

        BigDecimal densiteMoyenne = densiteSomme.divide(BigDecimal.valueOf(NBPatterns), scale, RoundingMode.HALF_UP);

        // Calcul de l'écart-type
        BigDecimal varianceSomme = BigDecimal.ZERO;
        for (BigDecimal densite : densites) {
            BigDecimal ecart = densite.subtract(densiteMoyenne);
            varianceSomme = varianceSomme.add(ecart.multiply(ecart)); // (xi - mean)²
        }

        BigDecimal variance = varianceSomme.divide(BigDecimal.valueOf(NBPatterns), scale, RoundingMode.HALF_UP);
        BigDecimal ecartType = new BigDecimal(Math.sqrt(variance.doubleValue())); // Racine carrée

        System.out.println("Densité moyenne: " + densiteMoyenne + " Écart-type: " + ecartType);
    }


    public void DensityEvaluationHIPS(Database db, int seed, int NBPatterns) {
        /* * Méthode qui calcule la densité de NBPatterns motifs issus de HIPS */

        HFIPS hfips = new HFIPS(db, seed);
        ArrayList<BigDecimal> densites = new ArrayList<>();
        BigDecimal densiteSomme = BigDecimal.ZERO;
        int scale = 10; // Précision des calculs

        // Calcul de la densité moyenne
        for (int i = 0; i < NBPatterns; i++) {
            IP ip = hfips.drawIP();
            BigDecimal coverageSize = BigDecimal.valueOf(ip.getCoverage().size());
            BigDecimal volume = ip.getVolume();

            if (volume.compareTo(BigDecimal.ZERO) == 0) {
                BigDecimal tempDensite = BigDecimal.ZERO;

                densites.add(tempDensite);
                densiteSomme = densiteSomme.add(tempDensite);
                continue;
            }else {
                BigDecimal tempDensite = coverageSize.divide(volume, scale, RoundingMode.HALF_UP);
                densites.add(tempDensite);
                densiteSomme = densiteSomme.add(tempDensite);
            }


        }

        BigDecimal densiteMoyenne = densiteSomme.divide(BigDecimal.valueOf(densites.size()), scale, RoundingMode.HALF_UP);

        // Calcul de l'écart-type
        BigDecimal varianceSomme = BigDecimal.ZERO;
        for (BigDecimal densite : densites) {
            BigDecimal ecart = densite.subtract(densiteMoyenne);
            varianceSomme = varianceSomme.add(ecart.multiply(ecart)); // (xi - mean)²
        }

        BigDecimal variance = varianceSomme.divide(BigDecimal.valueOf(densites.size()), scale, RoundingMode.HALF_UP);
        BigDecimal ecartType = new BigDecimal(Math.sqrt(variance.doubleValue())); // Racine carrée

        System.out.println("Densité moyenne: " + densiteMoyenne + " Écart-type: " + ecartType);
    }

    public void DensityEvaluationUniform(Database db, int seed, int NBPatterns) {
        /* * Méthode qui calcule la densité de NBPatterns motifs issus de l'échantillonnage uniforme */

        Uniform uniform = new Uniform(db, seed);
        ArrayList<BigDecimal> densites = new ArrayList<>();
        BigDecimal densiteSomme = BigDecimal.ZERO;
        int scale = 10; // Précision des calculs

        // Calcul de la densité moyenne
        for (int i = 0; i < NBPatterns; i++) {
            IP ip = uniform.drawIP();
            BigDecimal coverageSize = BigDecimal.valueOf(ip.getCoverage().size());
            BigDecimal volume = ip.getVolume();

            if (volume.compareTo(BigDecimal.ZERO) == 0) {
                BigDecimal tempDensite = BigDecimal.ZERO;
                densites.add(tempDensite);
                densiteSomme = densiteSomme.add(tempDensite);
                continue; // Éviter la division par zéro
            }else{
                BigDecimal tempDensite = coverageSize.divide(volume, scale, RoundingMode.HALF_UP);
                densites.add(tempDensite);
                densiteSomme = densiteSomme.add(tempDensite);
            }


        }

        BigDecimal densiteMoyenne = densiteSomme.divide(BigDecimal.valueOf(densites.size()), scale, RoundingMode.HALF_UP);

        // Calcul de l'écart-type
        BigDecimal varianceSomme = BigDecimal.ZERO;
        for (BigDecimal densite : densites) {
            BigDecimal ecart = densite.subtract(densiteMoyenne);
            varianceSomme = varianceSomme.add(ecart.multiply(ecart)); // (xi - mean)²
        }

        BigDecimal variance = varianceSomme.divide(BigDecimal.valueOf(densites.size()), scale, RoundingMode.HALF_UP);
        BigDecimal ecartType = new BigDecimal(Math.sqrt(variance.doubleValue())); // Racine carrée

        System.out.println("Densité moyenne: " + densiteMoyenne + " Écart-type: " + ecartType);
    }








    public void writeResults(String outputfile, ArrayList<HashMap<String, Object>> resultsList) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputfile))) {
            // Write the CSV header
            writer.write("constraintNumber,query,resultNbDraws,resultCPUTime,resultFrequency");
            writer.newLine();

            // Iterate through the results and write each as a CSV row
            for (HashMap<String, Object> result : resultsList) {
                String constraintNumber = result.get("constraintNumber").toString();
                String query = "\"" + result.get("query").toString() + "\""; // Surround the query with quotes
                String resultNbDraws = result.get("resultNbDraws").toString();
                String resultCPUTime = result.get("resultCPUTime").toString();
                String resultfrequency = result.get("resultFrequency").toString();

                // Write the row
                writer.write(constraintNumber + "," + query + "," + resultNbDraws + "," + resultCPUTime+ ","+resultfrequency);
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }












    public boolean checkInclusionConstraint(int mprim, double vprim, IP ip) {
        if (ip.getIP().get(mprim * 2) <= vprim && ip.getIP().get(mprim * 2 + 1) >= vprim) {
            return true;
        } else {
            return false;
        }
    }


    public boolean checkExclusionConstraint(int mprim, double vprim, IP ip) {
        if (ip.getIP().get(mprim * 2) > vprim || ip.getIP().get(mprim * 2 + 1) < vprim) {
            return true;
        } else {
            return false;
        }
    }


    public boolean checkSupConstraint(int mprim, double vprim, IP ip) {
        if (ip.getIP().get(mprim * 2) > vprim) {
            return true;
        } else {
            return false;
        }
    }

    public boolean checkInfConstraint(int mprim, double vprim, IP ip) {
        if (ip.getIP().get(mprim * 2 + 1) < vprim) {
            return true;
        } else {
            return false;
        }
    }


    public boolean checkSupEqConstraint(int mprim, double vprim, IP ip) {
        if (ip.getIP().get(mprim * 2) >= vprim) {
            return true;
        } else {
            return false;
        }
    }

    public boolean checkInfEqConstraint(int mprim, double vprim, IP ip) {
        if (ip.getIP().get(mprim * 2 + 1) <= vprim) {
            return true;
        } else {
            return false;
        }
    }


    public double EvaluationEmptyCoverage(int k, Database db) {

        TotalyRandomIPSampling trips = new TotalyRandomIPSampling(db);
        int numberEmptyCoverage = 0;
        for (int i = 0; i < k; i++) {
            IP rip = trips.drawIP();
            if (rip.coverage.isEmpty()) {
                numberEmptyCoverage += 1;
            }
        }

        return (double) numberEmptyCoverage / k;

    }


    public void constrainedPlausibilityHIPS(int seed, int n, int k, Database realDb, float minfreq, float maxfreq) {
        Database[] randDatasets;
        double avgPrecision = 0;
        double avgDifference = 0;
        double avgFrequency = 0;
        //---------------------------------------------------------- randomizing the initial dataset
        randDatasets = new Database[k];
        for (int i = 0; i < k; i++) {
            randDatasets[i] = realDb.randomizeNumericalDB();
            randDatasets[i].setDBInfos();
        }

        ArrayList<IP> patternPool = new ArrayList<>();
        int i = 0;

        // Track the start time
        long startTime = System.currentTimeMillis();
        long timeout = 300000; // 5 minutes in milliseconds

        while (patternPool.size() < n) {
            // Check if the elapsed time has exceeded the timeout
            if (System.currentTimeMillis() - startTime > timeout) {
                System.out.println("Timeout occurred. Exiting the process.");
                System.out.println("Number of Iterations before timeout: " + i);
                return; // Exit the method if the timeout is reached
            }

            IP ip = new HFIPS(realDb, seed).drawIP();
            float freq = (float) ip.getCoverage().size() / realDb.objectNumber;
            if (freq > minfreq && freq < maxfreq) {
                patternPool.add(ip);
                avgFrequency += freq;
            }
            i++;
        }

        for (IP pattern : patternPool) {
            ArrayList<Integer> cov = pattern.getCoverage();
            double frequency = cov.size();

            for (int j = 0; j < k; j++) {
                double freq = pattern.processCoverage(randDatasets[j]).size();
                double diff = frequency - freq;
                if (diff < 0) {
                    diff = 0;
                }
                if (frequency != Double.POSITIVE_INFINITY && diff != Double.POSITIVE_INFINITY) {
                    avgDifference += diff; // add the support difference to avgDifference
                    avgPrecision += frequency;
                }
            }
        }

        avgPrecision = avgDifference / avgPrecision;
        System.out.println("Plausibility HIPS: " + avgPrecision + " Number of Iterations: " + i + " Mean frequency: " + avgFrequency / n);
    }


    public void constrainedPlausibilityIP(int seed,int n, int k, Database realDb, float minfreq, float maxfreq) {
        Database[] randDatasets;
        double avgPrecision = 0;
        double avgDifference = 0;
        double avgFrequency = 0;
        //---------------------------------------------------------- randomizing the initial dataset
        randDatasets = new Database[k];
        for (int i = 0; i < k; i++) {
            randDatasets[i] = realDb.randomizeNumericalDB();
            randDatasets[i].setDBInfos();
        }

        ArrayList<IP> patternPool = new ArrayList<>();
        int i = 0;

        // Track the start time
        long startTime = System.currentTimeMillis();
        long timeout = 300000; // 5 minutes in milliseconds

        while (patternPool.size() < n) {
            // Check if the elapsed time has exceeded the timeout
            if (System.currentTimeMillis() - startTime > timeout) {
                System.out.println("Timeout occurred. Exiting the process.");
                System.out.println("Number of Iterations before timeout: " + i);
                return; // Exit the method if the timeout is reached
            }

            IP ip = new FIPS(realDb, seed).drawIP();
            float freq = (float) ip.getCoverage().size() / realDb.objectNumber;
            if (freq > minfreq && freq < maxfreq) {
                patternPool.add(ip);
                avgFrequency += freq;
            }
            i++;
        }

        for (IP pattern : patternPool) {
            ArrayList<Integer> cov = pattern.getCoverage();
            double frequency = cov.size();

            for (int j = 0; j < k; j++) {
                double freq = pattern.processCoverage(randDatasets[j]).size();
                double diff = frequency - freq;
                if (diff < 0) {
                    diff = 0;
                }
                if (frequency != Double.POSITIVE_INFINITY && diff != Double.POSITIVE_INFINITY) {
                    avgDifference += diff; // add the support difference to avgDifference
                    avgPrecision += frequency;
                }
            }
        }

        avgPrecision = avgDifference / avgPrecision;
        System.out.println("Plausibility FIPS: " + avgPrecision + " Number of Iterations: " + i + " Mean frequency: " + avgFrequency / n);
    }


    public void constrainedPlausibilityRandomIP(int seed,int n, int k, Database realDb, float minfreq, float maxfreq) {
        Database[] randDatasets;
        double avgPrecision = 0;
        double avgDifference = 0;
        double avgFrequency = 0;

        //---------------------------------------------------------- randomizing the initial dataset
        randDatasets = new Database[k];
        for (int i = 0; i < k; i++) {
            randDatasets[i] = realDb.randomizeNumericalDB();
            randDatasets[i].setDBInfos();
        }

        ArrayList<IP> patternPool = new ArrayList<>();
        int i = 0;

        // Track the start time
        long startTime = System.currentTimeMillis();
        long timeout = 300000; // 5 minutes in milliseconds

        while (patternPool.size() < n) {
            // Check if the elapsed time has exceeded the timeout
            if (System.currentTimeMillis() - startTime > timeout) {
                System.out.println("Timeout occurred. Exiting the process.");
                System.out.println("Number of Iterations before timeout: " + i);
                return; // Exit the method if the timeout is reached
            }

            IP ip = new Uniform(realDb, seed).drawIP();
            float freq = (float) ip.getCoverage().size() / realDb.objectNumber;
            if (freq > minfreq && freq < maxfreq) {
                patternPool.add(ip);
                avgFrequency += freq;
            }
            i++;
        }

        for (IP pattern : patternPool) {
            ArrayList<Integer> cov = pattern.getCoverage();
            double frequency = cov.size();

            for (int j = 0; j < k; j++) {
                double freq = pattern.processCoverage(randDatasets[j]).size();
                double diff = frequency - freq;
                if (diff < 0) {
                    diff = 0;
                }
                if (frequency != Double.POSITIVE_INFINITY && diff != Double.POSITIVE_INFINITY) {
                    avgDifference += diff; // add the support difference to avgDifference
                    avgPrecision += frequency;
                }
            }
        }

        avgPrecision = avgDifference / avgPrecision;

        System.out.println("Plausibility Random IP: " + avgPrecision + " Number of Iterations: " + i + " Mean frequency: " + avgFrequency / n);
    }




 /*   public double PlausibilityDensityRandomIP(int n, int k, Database realDb) {
        *//*
         * Method that process the plausibility with the density metric
         *
         * n: is the number of IP
         * k: is the number of randomized databases
         * *//*
        Database[] randDatasets;
        double avgPrecision = 0;
        double avgDifference = 0;

        //---------------------------------------------------------- randomizing the initial dataset
        randDatasets = new Database[k];
        for (int i = 0; i < k; i++) {
            randDatasets[i] = realDb.randomizeNumericalDB();
            randDatasets[i].setDBInfos();
        }
        for (int i = 0; i < n; i++) {
            IP ip = new RandomIPSampling(realDb).drawIP();
            ArrayList<Integer> cov = ip.getCoverage();
            double currVolume = ip.getVolume();
            double density = cov.size() / currVolume;

            for (int j = 0; j < k; j++) {
                double denss = ip.processCoverage(randDatasets[j]).size() / currVolume;
                double diff = density - denss;
                if (diff < 0) {
                    diff = 0;
                }
                if (density != Double.POSITIVE_INFINITY && diff != Double.POSITIVE_INFINITY) {
                    avgDifference += diff;                // ajouter la difference de support dans avgDifference
                    avgPrecision += density;
                }
            }
        }

        avgPrecision = avgDifference / avgPrecision;
        return avgPrecision;
    }*/

   /* public double PlausibilityDensityIP(int n, int k, Database realDb) {
        *//*
         * Plausibility for density
         * n is the number of IP
         * k is the number of randomized databases
         * *//*

        Database[] randDatasets;
        double avgPrecision = 0;
        double avgDifference = 0;

        //---------------------------------------------------------- randomizing the initial dataset
        randDatasets = new Database[k];
        for (int i = 0; i < k; i++) {
            randDatasets[i] = realDb.randomizeNumericalDB();
            randDatasets[i].setDBInfos();
        }
        for (int i = 0; i < n; i++) {
            IP ip = new FIPS(realDb).drawIP();
            ArrayList<Integer> cov = ip.getCoverage();
            double currVolume = ip.getVolume();
            double density = cov.size() / currVolume;

            for (int j = 0; j < k; j++) {
                double denss = ip.processCoverage(randDatasets[j]).size() / currVolume;
                double diff = density - denss;
                if (diff < 0) {
                    diff = 0;
                }
                if (density != Double.POSITIVE_INFINITY && diff != Double.POSITIVE_INFINITY) {
                    avgDifference += diff;                // ajouter la difference de support dans avgDifference
                    avgPrecision += density;
                }
            }
        }

        avgPrecision = avgDifference / avgPrecision;
        return avgPrecision;
    }*/


    public double PlausibilityFrequencyHIPS(int seed,int n, int k, Database realDb) {
        /*
         * Plausibility for Frequency
         * n is the number of IP
         * k is the number of randomized databases
         * */

        Database[] randDatasets;
        double avgPrecision = 0;
        double avgDifference = 0;

        //---------------------------------------------------------- randomizing the initial dataset
        randDatasets = new Database[k];
        for (int i = 0; i < k; i++) {
            randDatasets[i] = realDb.randomizeNumericalDB();
            randDatasets[i].setDBInfos();
        }
        for (int i = 0; i < n; i++) {
            IP ip = new FIPS(realDb, seed).drawIP();
            ArrayList<Integer> cov = ip.getCoverage();
            double frequency = cov.size();

            for (int j = 0; j < k; j++) {
                double freq = ip.processCoverage(randDatasets[j]).size();
                double diff = frequency - freq;
                if (diff < 0) {
                    diff = 0;
                }
                if (frequency != Double.POSITIVE_INFINITY && diff != Double.POSITIVE_INFINITY) {
                    avgDifference += diff;                // ajouter la difference de support dans avgDifference
                    avgPrecision += frequency;
                }
            }
        }

        avgPrecision = avgDifference / avgPrecision;
        return avgPrecision;
    }

    public double PlausibilityFrequencyIP(int seed,int n, int k, Database realDb) {
        /*
         * Plausibility for Frequency
         * n is the number of IP
         * k is the number of randomized databases
         * */

        Database[] randDatasets;
        double avgPrecision = 0;
        double avgDifference = 0;

        //---------------------------------------------------------- randomizing the initial dataset
        randDatasets = new Database[k];
        for (int i = 0; i < k; i++) {
            randDatasets[i] = realDb.randomizeNumericalDB();
            randDatasets[i].setDBInfos();
        }
        for (int i = 0; i < n; i++) {
            IP ip = new FIPS(realDb, seed).drawIP();
            ArrayList<Integer> cov = ip.getCoverage();
            double frequency = cov.size();

            for (int j = 0; j < k; j++) {
                double freq = ip.processCoverage(randDatasets[j]).size();
                double diff = frequency - freq;
                if (diff < 0) {
                    diff = 0;
                }
                if (frequency != Double.POSITIVE_INFINITY && diff != Double.POSITIVE_INFINITY) {
                    avgDifference += diff;                // ajouter la difference de support dans avgDifference
                    avgPrecision += frequency;
                }
            }
        }

        avgPrecision = avgDifference / avgPrecision;
        return avgPrecision;
    }


    public double PlausibilityFrequencyRandIP(int seed,int n, int k, Database realDb) {
        /*
         * Plausibility for Frequency
         * n is the number of IP
         * k is the number of randomized databases
         * */

        Database[] randDatasets;
        double avgPrecision = 0;
        double avgDifference = 0;

        //---------------------------------------------------------- randomizing the initial dataset
        randDatasets = new Database[k];
        for (int i = 0; i < k; i++) {
            randDatasets[i] = realDb.randomizeNumericalDB();
            randDatasets[i].setDBInfos();
        }
        for (int i = 0; i < n; i++) {
            IP ip = new Uniform(realDb, seed).drawIP();
            ArrayList<Integer> cov = ip.getCoverage();
            double frequency = cov.size();

            for (int j = 0; j < k; j++) {
                double freq = ip.processCoverage(randDatasets[j]).size();
                double diff = frequency - freq;
                if (diff < 0) {
                    diff = 0;
                }
                if (frequency != Double.POSITIVE_INFINITY && diff != Double.POSITIVE_INFINITY) {
                    avgDifference += diff;                // ajouter la difference de support dans avgDifference
                    avgPrecision += frequency;
                }
            }
        }

        avgPrecision = avgDifference / avgPrecision;
        return avgPrecision;
    }


    public void TempsCPUIP(int seed,int k, Database db) {
        /*
         * Method that process the duration in cpu time for a given number of FIPS patterns
         * Params: K=> number of patterns; db=> given database
         * */
        FIPS sampling = new FIPS(db,seed);
        long startTime = System.nanoTime();
        for (int i = 0; i < k; i++) {
            IP ip = sampling.drawIP();
        }
        long endTime = System.nanoTime();
        long elapsedTime = endTime - startTime;
        double elapsedTimeInMs = elapsedTime / 1_000_000_000.0;         // conversion to milliseconds

        System.out.println(" " + elapsedTimeInMs + " milliseconds");
    }

    public void TempsCPURandomIP(int seed,int k, Database db) {
        /*
         * Method that process the duration in cpu time for a given number of randomly drawn patterns
         * Params: K=> number of patterns; db=> given database
         * */
        Uniform sampling = new Uniform(db, seed);
        long startTime = System.nanoTime();
        for (int i = 0; i < k; i++) {
            IP ip = sampling.drawIP();
        }
        long endTime = System.nanoTime();
        long elapsedTime = endTime - startTime;
        double elapsedTimeInMs = elapsedTime / 1_000_000_000.0;         // conversion to milliseconds

        System.out.println(" " + elapsedTimeInMs + " milliseconds");
    }

    public void TempsCPUTotalyRandomIP(int k, Database db) {
        /*
         * Method that process the duration in cpu time for a given number of randomly drawn patterns that does not ensure
         * non empty coverage
         * Params: K=> number of patterns; db=> given database
         * */
        TotalyRandomIPSampling sampling = new TotalyRandomIPSampling(db);
        long startTime = System.nanoTime();
        for (int i = 0; i < k; i++) {
            IP ip = sampling.drawIP();
        }
        long endTime = System.nanoTime();
        long elapsedTime = endTime - startTime;
        double elapsedTimeInMs = elapsedTime / 1_000_000_000.0;         // conversion to milliseconds

        System.out.println(" " + elapsedTimeInMs + " milliseconds");
    }



    public void evolutionTempsCPURandomIP(int seed,int k, Database db, String output) {
        /*
         * Method that process the cpu time evolution of sampling k patterns with random method
         *
         * */
        System.out.println("Evolution IP CPU TIME, database: " + db);

        Uniform sampling = new Uniform(db, seed);

        try (PrintWriter writer = new PrintWriter(new FileWriter(output, true))) {
            writer.println("Iteration,IP_CPU_Time(ms)");

            long startTime = System.nanoTime();
            writer.println(0 + "," + 0.00); // Initial state, no time elapsed yet

            for (int i = 0; i < k; i++) {
                long iterationStartTime = System.nanoTime();  // Start time of the iteration

                IP ip = sampling.drawIP();

                long iterationEndTime = System.nanoTime();  // End time of the iteration
                long iterationElapsedTime = iterationEndTime - iterationStartTime;  // Elapsed time for this iteration
                double iterationElapsedTimeInMs = iterationElapsedTime / 1_000_000.0;  // Convert to milliseconds

                // Write to CSV file
                writer.println((i + 1) + "," + iterationElapsedTimeInMs);
            }

            long endTime = System.nanoTime();
            long totalElapsedTime = endTime - startTime;
            double totalElapsedTimeInMs = totalElapsedTime / 1_000_000.0;  // Convert total elapsed time to milliseconds
            writer.println((k + 1) + "," + totalElapsedTimeInMs);  // Total time for all iterations
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void evolutionTempsCPUTotalyRandomIP(int k, Database db, String output) {
        /*
         * Method that process the cpu time evolution of sampling k patterns with random method
         *
         * */
        System.out.println("Evolution IP CPU TIME, database: " + db);

        TotalyRandomIPSampling sampling = new TotalyRandomIPSampling(db);

        try (PrintWriter writer = new PrintWriter(new FileWriter(output, true))) {
            writer.println("Iteration,IP_CPU_Time(ms)");

            long startTime = System.nanoTime();
            writer.println(0 + "," + 0.00); // Initial state, no time elapsed yet

            for (int i = 0; i < k; i++) {
                long iterationStartTime = System.nanoTime();  // Start time of the iteration

                IP ip = sampling.drawIP();

                long iterationEndTime = System.nanoTime();  // End time of the iteration
                long iterationElapsedTime = iterationEndTime - iterationStartTime;  // Elapsed time for this iteration
                double iterationElapsedTimeInMs = iterationElapsedTime / 1_000_000.0;  // Convert to milliseconds

                // Write to CSV file
                writer.println((i + 1) + "," + iterationElapsedTimeInMs);
            }

            long endTime = System.nanoTime();
            long totalElapsedTime = endTime - startTime;
            double totalElapsedTimeInMs = totalElapsedTime / 1_000_000.0;  // Convert total elapsed time to milliseconds
            writer.println((k + 1) + "," + totalElapsedTimeInMs);  // Total time for all iterations
        } catch (IOException e) {
            e.printStackTrace();
        }
    }





    public void evolutionTempsCPUUniform(int seed, int k, int rep, Database db, String output) {
        /*
         * Method that processes the CPU time evolution of sampling k patterns with the Uniform method
         */
        System.out.println("Evolution IP CPU TIME, database: " + db);

        Uniform sampling = new Uniform(db, seed);

        try (PrintWriter writer = new PrintWriter(new FileWriter(output, true))) {
            writer.println("Iteration,IP_CPU_Time(ms)");

            ArrayList<Double> cpuTimeperIteration = new ArrayList<>(Collections.nCopies(k, 0.0));  // Initialize list with k elements, all set to 0.0
            long startTime = System.nanoTime();
            writer.println(0 + "," + 0.00); // Initial state, no time elapsed yet

            for (int j = 0; j < rep; j++) {
                for (int i = 0; i < k; i++) {
                    long iterationStartTime = System.nanoTime();  // Start time of the iteration

                    IP ip = sampling.drawIP();

                    long iterationEndTime = System.nanoTime();  // End time of the iteration
                    long iterationElapsedTime = iterationEndTime - iterationStartTime;  // Elapsed time for this iteration
                    double iterationElapsedTimeInMs = iterationElapsedTime / 1_000_000.0;  // Convert to milliseconds

                    // Add the elapsed time for this iteration to the corresponding element in the list
                    cpuTimeperIteration.set(i, cpuTimeperIteration.get(i) + iterationElapsedTimeInMs);
                }
            }

            // Write the average CPU time for each iteration to the CSV file
            for (int i = 0; i < k; i++) {
                double avgTimeForIteration = cpuTimeperIteration.get(i) / rep;  // Calculate average time for the iteration
                writer.println((i + 1) + "," + avgTimeForIteration);
            }

            long endTime = System.nanoTime();
            long totalElapsedTime = endTime - startTime;
            double totalElapsedTimeInMs = totalElapsedTime / 1_000_000.0;  // Convert total elapsed time to milliseconds
            writer.println((k + 1) + "," + totalElapsedTimeInMs);  // Total time for all iterations
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void evolutionTempsCPUHIPS(int seed,int k, int rep, Database db, String output) {
        /*
         * Method that processes the CPU time evolution of sampling k patterns with HIPS method
         */
        System.out.println("Evolution IP CPU TIME, database: " + db);

        HFIPS sampling = new HFIPS(db, seed);

        try (PrintWriter writer = new PrintWriter(new FileWriter(output, true))) {
            writer.println("Iteration,IP_CPU_Time(ms)");

            ArrayList<Double> cpuTimeperIteration = new ArrayList<>(Collections.nCopies(k, 0.0));  // Initialize list with k elements, all set to 0.0
            long startTime = System.nanoTime();
            writer.println(0 + "," + 0.00); // Initial state, no time elapsed yet

            for (int j = 0; j < rep; j++) {
                for (int i = 0; i < k; i++) {
                    long iterationStartTime = System.nanoTime();  // Start time of the iteration

                    IP ip = sampling.drawIP();

                    long iterationEndTime = System.nanoTime();  // End time of the iteration
                    long iterationElapsedTime = iterationEndTime - iterationStartTime;  // Elapsed time for this iteration
                    double iterationElapsedTimeInMs = iterationElapsedTime / 1_000_000.0;  // Convert to milliseconds

                    // Add the elapsed time for this iteration to the corresponding element in the list
                    cpuTimeperIteration.set(i, cpuTimeperIteration.get(i) + iterationElapsedTimeInMs);
                }
            }

            // Write the average CPU time for each iteration to the CSV file
            for (int i = 0; i < k; i++) {
                double avgTimeForIteration = cpuTimeperIteration.get(i) / rep;  // Calculate average time for the iteration
                writer.println((i + 1) + "," + avgTimeForIteration);
            }

            long endTime = System.nanoTime();
            long totalElapsedTime = endTime - startTime;
            double totalElapsedTimeInMs = totalElapsedTime / 1_000_000.0;  // Convert total elapsed time to milliseconds
            writer.println((k + 1) + "," + totalElapsedTimeInMs);  // Total time for all iterations
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public void evolutionTempsCPUFIPS(int seed, int k, int rep, Database db, String output) {
        /*
         * Method that processes the CPU time evolution of sampling k patterns with FIPS method
         */
        System.out.println("Evolution IP CPU TIME, database: " + db);

        FIPS sampling = new FIPS(db, seed);

        try (PrintWriter writer = new PrintWriter(new FileWriter(output, true))) {
            writer.println("Iteration,IP_CPU_Time(ms)");

            ArrayList<Double> cpuTimeperIteration = new ArrayList<>(Collections.nCopies(k, 0.0));  // Initialize list with k elements, all set to 0.0
            long startTime = System.nanoTime();
            writer.println(0 + "," + 0.00); // Initial state, no time elapsed yet

            for (int j = 0; j < rep; j++) {
                for (int i = 0; i < k; i++) {
                    long iterationStartTime = System.nanoTime();  // Start time of the iteration

                    IP ip = sampling.drawIP();

                    long iterationEndTime = System.nanoTime();  // End time of the iteration
                    long iterationElapsedTime = iterationEndTime - iterationStartTime;  // Elapsed time for this iteration
                    double iterationElapsedTimeInMs = iterationElapsedTime / 1_000_000.0;  // Convert to milliseconds

                    // Add the elapsed time for this iteration to the corresponding element in the list
                    cpuTimeperIteration.set(i, cpuTimeperIteration.get(i) + iterationElapsedTimeInMs);
                }
            }

            // Write the average CPU time for each iteration to the CSV file
            for (int i = 0; i < k; i++) {
                double avgTimeForIteration = cpuTimeperIteration.get(i) / rep;  // Calculate average time for the iteration
                writer.println((i + 1) + "," + avgTimeForIteration);
            }

            long endTime = System.nanoTime();
            long totalElapsedTime = endTime - startTime;
            double totalElapsedTimeInMs = totalElapsedTime / 1_000_000.0;  // Convert total elapsed time to milliseconds
            writer.println((k + 1) + "," + totalElapsedTimeInMs);  // Total time for all iterations
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public void evolutionVolumeRandIP(int seed,int k,int rep ,Database db, String output) {
        /*
         * Method that retrieves the frequency of k sampled interval patterns with the FIPS method
         * */

        List<BigDecimal> volume = new ArrayList<>();  // Store volume
        Uniform sampling = new Uniform(db, seed);
        for(int i =0; i < rep; i++){
            if(volume.isEmpty()){
                for (int j = 0; j < k; j++) {
                    IP ip = sampling.drawIP();
                    volume.add(ip.getVolume());  // Store the volume
                    // Sort the list by frequency in increasing order
                    volume.sort(Collections.reverseOrder());
                }
            }else {
                List<BigDecimal> tempvolume = new ArrayList<>();  // Store volume
                for (int j = 0; j < k; j++) {
                    IP ip = sampling.drawIP();
                    tempvolume.add(ip.getVolume());  // Store the volume
                    // Sort the list by frequency in increasing order
                    tempvolume.sort(Collections.reverseOrder());
                }
                for(int j = 0; j < volume.size(); j++){
                volume.set(j, volume.get(j).add(tempvolume.get(j)));
                }

            }
        }

        volume.replaceAll(bigDecimal -> bigDecimal.divide(BigDecimal.valueOf(k),  new MathContext(10, RoundingMode.HALF_UP)));

        // Prepare CSV data
        StringBuilder csvData = new StringBuilder();
        csvData.append("Volume\n");  // Header for CSV

        for (BigDecimal frequency : volume) {
            String scientificNotation = frequency.toEngineeringString();  // Transform to scientific notation
            csvData.append(scientificNotation).append("\n");
        }

        // Write the CSV data to a file
        try (FileWriter writer = new FileWriter(output)) {
            writer.write(csvData.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Evaluation of Volume evolution done successfully");

    }


    public void evolutionVolumeHIPS(int seed,int k,int rep, Database db, String output) {
        /*
         * Method that retrieves the frequency of k sampled interval patterns with the FIPS method
         * */

        List<BigDecimal> volume = new ArrayList<>();  // Store volume
        HFIPS sampling = new HFIPS(db, seed);
        for(int i =0; i < rep; i++){
            if(volume.isEmpty()){
                for (int j = 0; j < k; j++) {
                    IP ip = sampling.drawIP();
                    volume.add(ip.getVolume());  // Store the volume
                    // Sort the list by frequency in increasing order
                    volume.sort(Collections.reverseOrder());
                }
            }else {
                List<BigDecimal> tempvolume = new ArrayList<>();  // Store volume
                for (int j = 0; j < k; j++) {
                    IP ip = sampling.drawIP();
                    tempvolume.add(ip.getVolume());  // Store the volume
                    // Sort the list by frequency in increasing order
                    tempvolume.sort(Collections.reverseOrder());
                }
                for(int j = 0; j < volume.size(); j++){
                    volume.set(j, volume.get(j).add(tempvolume.get(j)));
                }

            }
        }
        volume.replaceAll(bigDecimal -> bigDecimal.divide(BigDecimal.valueOf(k), new MathContext(10, RoundingMode.HALF_UP)));
        // Prepare CSV data
        StringBuilder csvData = new StringBuilder();
        csvData.append("Volume\n");  // Header for CSV

        for (BigDecimal frequency : volume) {
            String scientificNotation = frequency.toEngineeringString();  // Transform to scientific notation
            csvData.append(scientificNotation).append("\n");
        }
        // Write the CSV data to a file
        try (FileWriter writer = new FileWriter(output)) {
            writer.write(csvData.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Evaluation of Volume evolution done successfully");

    }

    public void evolutionVolumeFIPS(int seed, int k,int rep, Database db, String output) {
        /*
         * Method that retrieves the frequency of k sampled interval patterns with the FIPS method
         * */

        List<BigDecimal> volume = new ArrayList<>();  // Store volume
        FIPS sampling = new FIPS(db, seed);
        for(int i =0; i < rep; i++){
            if(volume.isEmpty()){
                for (int j = 0; j < k; j++) {
                    IP ip = sampling.drawIP();
                    volume.add(ip.getVolume());  // Store the volume
                    // Sort the list by frequency in increasing order
                    volume.sort(Collections.reverseOrder());
                }
            }else {
                List<BigDecimal> tempvolume = new ArrayList<>();  // Store volume
                for (int j = 0; j < k; j++) {
                    IP ip = sampling.drawIP();
                    tempvolume.add(ip.getVolume());  // Store the volume
                    // Sort the list by frequency in increasing order
                    tempvolume.sort(Collections.reverseOrder());
                }
                for(int j = 0; j < volume.size(); j++){
                    volume.set(j, volume.get(j).add(tempvolume.get(j)));
                }

            }
        }
        volume.replaceAll(bigDecimal -> bigDecimal.divide(BigDecimal.valueOf(k),  new MathContext(10, RoundingMode.HALF_UP)));

        // Prepare CSV data
        StringBuilder csvData = new StringBuilder();
        csvData.append("Volume\n");  // Header for CSV

        for (BigDecimal frequency : volume) {
            String scientificNotation = frequency.toEngineeringString();  // Transform to scientific notation
            csvData.append(scientificNotation).append("\n");
        }

        // Write the CSV data to a file
        try (FileWriter writer = new FileWriter(output)) {
            writer.write(csvData.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Evaluation of Volume evolution done successfully");

    }


    public void evolutionFrequenceRandomIP(int seed,int k, Database db, String output) {
        /*
         * Method that retrives the frequency of k randomly sampled interval patterns
         * */
        List<Double> freqList = new ArrayList<>();  // Store frequency
        Uniform sampling = new Uniform(db, seed);
                for (int j = 0; j < k; j++) {
                    IP ip = sampling.drawIP();
                    double coverageSize = (double) ip.getCoverage().size() / db.getObjectNumber();
                    if (coverageSize == 0){
                        System.out.println("ERROR: coverage is equal to zero !!! ");
                    }
                    freqList.add(coverageSize);  // Store the frequency
                }
        freqList.sort(Collections.reverseOrder());
        // Prepare CSV data
        StringBuilder csvData = new StringBuilder();
        csvData.append("CoverageSize\n");  // Header for CSV

        for (Double frequency : freqList) {
            csvData.append(frequency).append("\n");
        }

        // Write the CSV data to a file
        try (FileWriter writer = new FileWriter(output)) {
            writer.write(csvData.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Evaluation of frequency evolution done successfully");

    }



/*
    public void evolutionFrequenceRandomIP(int k,int rep, Database db, String output) {
        */
/*
         * Method that retrives the frequency of k randomly sampled interval patterns
         * *//*

        List<Double> freqList = new ArrayList<>();  // Store frequency
        RandomIPSampling sampling = new RandomIPSampling(db);
        for(int i =0; i < rep; i++){
            if(freqList.isEmpty()){
                for (int j = 0; j < k; j++) {
                    IP ip = sampling.drawIP();
                    double coverageSize = (double) ip.getCoverage().size() / db.getObjectNumber();
                    if (coverageSize == 0){
                        System.out.println("ERROR: coverage is equal to zero !!! ");
                    }
                    freqList.add(coverageSize);  // Store the frequency
                    freqList.sort(Collections.reverseOrder());
                }
            }else {
                List<Double> tempfreq = new ArrayList<>();  // Store freq
                for (int j = 0; j < k; j++) {
                    IP ip = sampling.drawIP();
                    double coverageSize = (double) ip.getCoverage().size() / db.getObjectNumber();
                    tempfreq.add(coverageSize);  // Store the freq
                    // Sort the list by frequency in increasing order
                    tempfreq.sort(Collections.reverseOrder());
                }
                for(int j = 0; j < freqList.size(); j++){
                    freqList.set(j, freqList.get(j)+tempfreq.get(j));
                }
            }
        }
        freqList.replaceAll(value -> value / k);
        // Prepare CSV data
        StringBuilder csvData = new StringBuilder();
        csvData.append("CoverageSize\n");  // Header for CSV

        for (Double frequency : freqList) {
            csvData.append(frequency).append("\n");
        }

        // Write the CSV data to a file
        try (FileWriter writer = new FileWriter(output)) {
            writer.write(csvData.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Evaluation of frequency evolution done successfully");

    }
*/


    public void evolutionFrequenceHIPS(int seed,int k, Database db, String output) {
        /*
         * Method that retrives the frequency of k randomly sampled interval patterns
         * */

        List<Double> freqList = new ArrayList<>();  // Store frequency
        HFIPS sampling = new HFIPS(db, seed);

                for (int j = 0; j < k; j++) {
                    IP ip = sampling.drawIP();
                    double coverageSize = (double) ip.getCoverage().size() / db.getObjectNumber();
                    if (coverageSize == 0){
                        System.out.println("ERROR: coverage is equal to zero !!! ");
                    }
                    freqList.add(coverageSize);  // Store the frequency
                }
        freqList.sort(Collections.reverseOrder());

        // Prepare CSV data
        StringBuilder csvData = new StringBuilder();
        csvData.append("CoverageSize\n");  // Header for CSV

        for (Double frequency : freqList) {
            csvData.append(frequency).append("\n");
        }

        // Write the CSV data to a file
        try (FileWriter writer = new FileWriter(output)) {
            writer.write(csvData.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Evaluation of frequency evolution done successfully");

    }

/*    public void evolutionFrequenceRandomHIPS(int k,int rep, Database db, String output) {
        *//*
         * Method that retrives the frequency of k randomly sampled interval patterns
         * *//*

        List<Double> freqList = new ArrayList<>();  // Store frequency
        HIPS sampling = new HIPS(db);
        for(int i =0; i < rep; i++){
            if(freqList.isEmpty()){
                for (int j = 0; j < k; j++) {
                    IP ip = sampling.drawIP();
                    double coverageSize = (double) ip.getCoverage().size() / db.getObjectNumber();
                    if (coverageSize == 0){
                        System.out.println("ERROR: coverage is equal to zero !!! ");
                    }
                    freqList.add(coverageSize);  // Store the frequency
                    freqList.sort(Collections.reverseOrder());
                }
            }else {
                List<Double> tempfreq = new ArrayList<>();  // Store freq
                for (int j = 0; j < k; j++) {
                    IP ip = sampling.drawIP();
                    double coverageSize = (double) ip.getCoverage().size() / db.getObjectNumber();
                    tempfreq.add(coverageSize);  // Store the freq
                    // Sort the list by frequency in increasing order
                    tempfreq.sort(Collections.reverseOrder());
                }
                for(int j = 0; j < freqList.size(); j++){
                    freqList.set(j, freqList.get(j)+tempfreq.get(j));
                }
            }
        }
        freqList.replaceAll(value -> value / k);
        // Prepare CSV data
        StringBuilder csvData = new StringBuilder();
        csvData.append("CoverageSize\n");  // Header for CSV

        for (Double frequency : freqList) {
            csvData.append(frequency).append("\n");
        }

        // Write the CSV data to a file
        try (FileWriter writer = new FileWriter(output)) {
            writer.write(csvData.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Evaluation of frequency evolution done successfully");

    }*/


    public void evolutionFrequenceFIPS(int seed,int k ,Database db, String output) {
        /*
         * Method that retrieves the frequency of k sampled interval patterns with the FIPS method
         * */

        List<Double> freqList = new ArrayList<>();  // Store frequency
        FIPS sampling = new FIPS(db, seed);
                for (int j = 0; j < k; j++) {
                    IP ip = sampling.drawIP();
                    double coverageSize = (double) ip.getCoverage().size() / db.getObjectNumber();
                    if (coverageSize == 0){
                        System.out.println("ERROR: coverage is equal to zero !!! ");
                    }
                    freqList.add(coverageSize);  // Store the frequency
                }
        freqList.sort(Collections.reverseOrder());


        // Prepare CSV data
        StringBuilder csvData = new StringBuilder();
        csvData.append("CoverageSize\n");  // Header for CSV

        for (Double frequency : freqList) {
            csvData.append(frequency).append("\n");
        }

        // Write the CSV data to a file
        try (FileWriter writer = new FileWriter(output)) {
            writer.write(csvData.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Evaluation of frequency evolution done successfully");
    }


   /* public void evolutionFrequenceIP(int k,int rep ,Database db, String output) {
        *//*
         * Method that retrieves the frequency of k sampled interval patterns with the FIPS method
         * *//*

        List<Double> freqList = new ArrayList<>();  // Store frequency
        FIPS sampling = new FIPS(db);
        for(int i =0; i < rep; i++){
            if(freqList.isEmpty()){
                for (int j = 0; j < k; j++) {
                    IP ip = sampling.drawIP();
                    double coverageSize = (double) ip.getCoverage().size() / db.getObjectNumber();
                    if (coverageSize == 0){
                        System.out.println("ERROR: coverage is equal to zero !!! ");
                    }
                    freqList.add(coverageSize);  // Store the frequency
                    freqList.sort(Collections.reverseOrder());
                }
            }else {
                List<Double> tempfreq = new ArrayList<>();  // Store freq
                for (int j = 0; j < k; j++) {
                    IP ip = sampling.drawIP();
                    double coverageSize = (double) ip.getCoverage().size() / db.getObjectNumber();
                    tempfreq.add(coverageSize);  // Store the volume
                    // Sort the list by frequency in increasing order
                    tempfreq.sort(Collections.reverseOrder());
                }
                for(int j = 0; j < freqList.size(); j++){
                    freqList.set(j, freqList.get(j)+tempfreq.get(j));
                }
            }
        }
        freqList.replaceAll(value -> value / k);

        // Prepare CSV data
        StringBuilder csvData = new StringBuilder();
        csvData.append("CoverageSize\n");  // Header for CSV

        for (Double frequency : freqList) {
            csvData.append(frequency).append("\n");
        }

        // Write the CSV data to a file
        try (FileWriter writer = new FileWriter(output)) {
            writer.write(csvData.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Evaluation of frequency evolution done successfully");
    }*/


    public double eqClassDiversityIP(int seed,int k, Database db) {

        /*
         * Diversity metric calculated for a pool of patterns (sampled with FIPS)
         *  with the formula in the paper of giacometti and al. SDM 2018
         * */

        Set<Long> eqClass = new HashSet<>();
        FIPS sampling = new FIPS(db, seed);

        for (int i = 0; i < k; i++) {
            IP ip = sampling.drawIP();
            eqClass.add(ip.getHash());
        }
        return (double) eqClass.size() / k;
    }

    public double eqClassDiversityHIPS(int seed,int k, Database db) {

        /*
         * Diversity metric calculated for a pool of patterns (sampled with FIPS)
         *  with the formula in the paper of giacometti and al. SDM 2018
         * */

        Set<Long> eqClass = new HashSet<>();
        HFIPS sampling = new HFIPS(db, seed);

        for (int i = 0; i < k; i++) {
            IP ip = sampling.drawIP();
            eqClass.add(ip.getHash());
        }
        return (double) eqClass.size() / k;
    }


    public double eqClassDiversityRandomIP(int seed, int k, Database db) {
        /*
         * Diversity metric calculated for a pool of patterns (sampled randomly)
         * with the formula in the paper of giacometti and al. SDM 2018
         * */

        Set<Long> eqClass = new HashSet<>();
        Uniform sampling = new Uniform(db, seed);

        for (int i = 0; i < k; i++) {
            IP ip = sampling.drawIP();
            eqClass.add(ip.getHash());
        }
        return (double) eqClass.size() / k;
    }


    public void jaccardCDFHIPS(int seed,int k, Database db, String outputFilePath) {
        /*
         * Jaccard cumulative distribution function of k patterns (randomly sampled) with jaccard thresholds ={20,40,60,80,100}
         * */

        ArrayList<IP> patterns = new ArrayList<>();
        double[] thresholds = {0.2, 0.4, 0.6, 0.8, 1};
        HashMap<Double, Double> results = new HashMap<>();

        // 1- Draw k patterns
        HFIPS ips = new HFIPS(db, seed);
        for (int i = 0; i < k; i++) {
            patterns.add(ips.drawIP());
        }
        // 2- Calculate the CDF
        for (double thresh : thresholds) {
            int count = 0;
            for (int i = 0; i < patterns.size(); i++) { // iterate over each unique pair of patterns
                for (int j = i + 1; j < patterns.size(); j++) {
                    double jacc = jaccard(patterns.get(i), patterns.get(j));
                    if (jacc <= thresh) {
                        count++;
                    }
                }
            }
            double cdfValue = (double) count * 2 / (k * (k - 1));
            results.put(thresh, cdfValue);
        }

        // 3- Write the results to a CSV file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath))) {
            // Write the header
            writer.write("Jaccard threshold,CDF Value");
            writer.newLine();

            // Write each threshold and its corresponding CDF value
            for (double thresh : thresholds) {
                writer.write(thresh + "," + results.get(thresh));
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public void jaccardCDFRandomIP(int seed,int k, Database db, String outputFilePath) {
        /*
         * Jaccard cumulative distribution function of k patterns (randomly sampled) with jaccard thresholds ={20,40,60,80,100}
         * */

        ArrayList<IP> patterns = new ArrayList<>();
        double[] thresholds = {0.2, 0.4, 0.6, 0.8, 1};
        HashMap<Double, Double> results = new HashMap<>();

        // 1- Draw k patterns
        Uniform ips = new Uniform(db, seed);
        for (int i = 0; i < k; i++) {
            patterns.add(ips.drawIP());
        }
        // 2- Calculate the CDF
        for (double thresh : thresholds) {
            int count = 0;
            for (int i = 0; i < patterns.size(); i++) { // iterate over each unique pair of patterns
                for (int j = i + 1; j < patterns.size(); j++) {
                    double jacc = jaccard(patterns.get(i), patterns.get(j));
                    if (jacc <= thresh) {
                        count++;
                    }
                }
            }
            double cdfValue = (double) count * 2 / (k * (k - 1));
            results.put(thresh, cdfValue);
        }

        // 3- Write the results to a CSV file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath))) {
            // Write the header
            writer.write("Jaccard threshold,CDF Value");
            writer.newLine();

            // Write each threshold and its corresponding CDF value
            for (double thresh : thresholds) {
                writer.write(thresh + "," + results.get(thresh));
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void jaccardCDFIP(int seed,int k, Database db, String outputFilePath) {
        /*
         * Jaccard cumulative distribution function of k patterns (sampled with FIPS) with jaccard thresholds ={20,40,60,80,100}
         * */
        ArrayList<IP> patterns = new ArrayList<>();
        double[] thresholds = {0.2, 0.4, 0.6, 0.8, 1};
        HashMap<Double, Double> results = new HashMap<>();

        // 1- Draw k patterns
        FIPS ips = new FIPS(db, seed);
        for (int i = 0; i < k; i++) {
            patterns.add(ips.drawIP());
        }
        // 2- Calculate the CDF
        for (double thresh : thresholds) {
            int count = 0;
            for (int i = 0; i < patterns.size(); i++) { // iterate over each unique pair of patterns
                for (int j = i + 1; j < patterns.size(); j++) {
                    double jacc = jaccard(patterns.get(i), patterns.get(j));
                    if (jacc <= thresh) {
                        count++;
                    }
                }
            }
            double cdfValue = (double) count * 2 / (k * (k - 1));
            results.put(thresh, cdfValue);
        }

        // 3- Write the results to a CSV file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath))) {
            // Write the header
            writer.write("Jaccard threshold,CDF Value");
            writer.newLine();

            // Write each threshold and its corresponding CDF value
            for (double thresh : thresholds) {
                writer.write(thresh + "," + results.get(thresh));
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void jaccardDiversityIP(int seed, int k, Database db, String outputFilePath) {

        ArrayList<IP> IPList = new ArrayList<>();
        ArrayList<Double> MeanJaccardScore = new ArrayList<>();

        FIPS sampling = new FIPS(db, seed);

        // To store k values and corresponding Jaccard scores
        StringBuilder csvData = new StringBuilder();
        csvData.append("K, MeanJaccardScore\n");  // Header for CSV

        for (int i = 0; i < k; i++) {
            IP currip = sampling.drawIP();
            if (IPList.isEmpty()) {
                IPList.add(currip);
            } else {
                double localmean = 0.0;
                for (IP ip : IPList) {
                    localmean += jaccard(currip, ip);
                }
                double meanJaccard = localmean / IPList.size();
                MeanJaccardScore.add(meanJaccard);
                IPList.add(currip);

                // Print and append to CSV data
                //System.out.println("k = " + i + ": " + meanJaccard + ",");
                csvData.append(i).append(", ").append(meanJaccard).append("\n");
            }
        }

        // Write the CSV data to a file
        try (FileWriter writer = new FileWriter(outputFilePath)) {
            writer.write(csvData.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public double jaccard(IP a, IP b) {
        return (double) intersection(a.getCoverage(), b.getCoverage()).size() / (double) union(a.getCoverage(), b.getCoverage()).size();
    }

    public static ArrayList<Integer> union(ArrayList<Integer> list1, ArrayList<Integer> list2) {
        Set<Integer> set = new HashSet<>(list1);
        set.addAll(list2);  // Add all elements from list2 to the set
        return new ArrayList<>(set);  // Convert the set back to a list
    }

    public static ArrayList<Integer> intersection(ArrayList<Integer> list1, ArrayList<Integer> list2) {
        Set<Integer> set1 = new HashSet<>(list1);
        Set<Integer> set2 = new HashSet<>(list2);
        set1.retainAll(set2);  // Retain only elements that are present in both sets
        return new ArrayList<>(set1);  // Convert the set back to a list
    }


 /*   public double jaccard(Itemset a, Itemset b) {
        return (double) intersection(a.getCoverage(), b.getCoverage()).size() / (double) union(a.getCoverage(), b.getCoverage()).size();
    }*/



}
