

import java.math.BigDecimal;
import java.util.*;
public class HFIPS {
    /*
     * Volume times frequency based Interval Pattern Sampling
     * */

    protected final Database dataset;
    ArrayList<BigDecimal> CumulativeIHweights = new ArrayList<>();
    BigDecimal ObjectstotalIH = BigDecimal.ZERO;
    Random randGenerator;
    int seed;

    public HFIPS(Database dataset, int seed) {
        this.dataset = dataset;
        this.seed = seed;
        this.randGenerator = new Random(this.seed);
        ProcessObjectsWeights();
    }

    public IP drawIP() {

        /* Method that draws an interval pattern proportional to volume x frequency
         *
         * Return: sampled IP
         */
        ArrayList<Double> resultingIP = new ArrayList<>();

        //     STEP 1: draw an object proportionally to w(g)
        int drawnObject =DrawObject();// draw object proportionally to w(g)


        //     STEP 2: draw intervals bounds proportionally to w(a) and w(b)
        for (int m = 0; m < dataset.getColumnsNumberNumerical(); m++) {

            ArrayList<Double> Igm = processIgm(m, dataset.getObject(drawnObject).get(m));
            ArrayList<Double> Jgm = processJgm(m, dataset.getObject(drawnObject).get(m));

            /*           draw lower bound   LOWER BOUND   */
            ArrayList<BigDecimal> cumulativeLBWeights = cumulativeLowerBoundWeights(Igm, Jgm); // process the weight of each distinct value

            //todo: voir si on peut inverser la liste au lieu de faire un mapping (pour gagner en temps cpu)
            List<Map.Entry<Integer, BigDecimal>> indexedList = new ArrayList<>();
            for (int i = 0; i < cumulativeLBWeights.size(); i++) {
                indexedList.add(new AbstractMap.SimpleEntry<>(i, cumulativeLBWeights.get(i)));
            }
            indexedList.sort(Comparator.comparing(Map.Entry::getValue));

            int indexInSortedList = DrawlbIndex(Igm, indexedList);          // use a binary search to get the desired value

            double valueLB = Igm.get(indexedList.get(indexInSortedList).getKey());   // get the value corresponding to the key


                            /*    UPPER BOUND   */

            ArrayList<BigDecimal> cumulativeUBWeights = cumulativeUpperBoundWeights(Jgm, valueLB); // process the weight of each distinct value
            int indexInJgm = drawUbIndex(Jgm, cumulativeUBWeights);    // get the desired value with a binary search
            double valueUB = Jgm.get(indexInJgm);   // this is the upper bound

            resultingIP.add(valueLB);           //Add the sampled interval
            resultingIP.add(valueUB);
        }
        return new IP(resultingIP, dataset, false);
    }


    public int DrawlbIndex(ArrayList<Double> lbList, List<Map.Entry<Integer, BigDecimal>> SortedCumulativelbWeights) {
        /* * Method that draws a lower bound proportionally to it weight
         * Return: drawn lower bound
         **/

        if (lbList.size() > 1) {
            double randomValue = randGenerator.nextDouble();
            BigDecimal drawnlowerBound = BigDecimal.valueOf(randomValue)
                    .multiply(SortedCumulativelbWeights.get(SortedCumulativelbWeights.size() - 1).getValue());
            int result = FindLBBoundIndex(0, lbList.size() - 1, drawnlowerBound, SortedCumulativelbWeights);
            return result;

        } else if (lbList.size() == 1) { //si il existe qu'une seule valeur dans Igm
            return 0;

        } else {
            System.out.println("ERROR WE SHOULD NOT BE HERE");
            return -1;
        }
    }

    public int drawUbIndex(ArrayList<Double> UBList, ArrayList<BigDecimal> CumulativelbWeights) {
        /*
         *
         * */
        if (UBList.size() > 1) {  // here we do a binary search
            BigDecimal drawnUpperBound = BigDecimal.valueOf(randGenerator.nextDouble())
                    .multiply(CumulativelbWeights.get(CumulativelbWeights.size() - 1));
            return FindUBBoundIndex(0, UBList.size() - 1, drawnUpperBound, CumulativelbWeights);

        } else if (UBList.size() == 1) { // here we just take the only candidate
            return 0;
        } else {
            System.out.println("ERROR WE SHOULD NOT BE HERE");
            return -1;
        }

    }

    public ArrayList<BigDecimal> cumulativeUpperBoundWeights(ArrayList<Double> Jgm, double selectedLowerBound) {
            /*
            * process the weight of each value in Jgm that is candidate for being an upper bound
            *
            * */
        ArrayList<BigDecimal> cumulativeUBWeights = new ArrayList<>();
        for (double b : Jgm) {
            BigDecimal weighta = BigDecimal.valueOf(b - selectedLowerBound);
            if (cumulativeUBWeights.isEmpty()) {
                cumulativeUBWeights.add(weighta);
            } else {
                BigDecimal previousweights = cumulativeUBWeights.get(cumulativeUBWeights.size() - 1);
                cumulativeUBWeights.add(previousweights.add(weighta));
            }
        }
        return cumulativeUBWeights;
    }

    public ArrayList<BigDecimal> cumulativeLowerBoundWeights(ArrayList<Double> Igm, ArrayList<Double> Jgm) {
        /*
        * Calcule le poid de chaque borne inférieure candidate (dans Igm)
        *
        *
        * */
        ArrayList<BigDecimal> cumulativelbWeights = new ArrayList<>();
        for (double a : Igm) {
            BigDecimal weighta = BigDecimal.ZERO;
            BigDecimal multiplication = BigDecimal.valueOf(Jgm.size() * a);
            for (double x : Jgm) {
                weighta = weighta.add(BigDecimal.valueOf(x).subtract(multiplication));
            }
            if (cumulativelbWeights.isEmpty()) {
                cumulativelbWeights.add(weighta);
            } else {
                BigDecimal previousweights = cumulativelbWeights.get(cumulativelbWeights.size() - 1);
                cumulativelbWeights.add(previousweights.add(weighta));
            }

        }
        return cumulativelbWeights;
    }


    private int FindLBBoundIndex(int left, int right, BigDecimal drawnLB, List<Map.Entry<Integer, BigDecimal>> sortedCumulativelbWeights) {
        /**  Method that search a cell in cumulative object weights that has a cumulative weight smaller than  drawnobject
         *
         * Return: The cell corresponding to the object respecting the condition
         *
         **/
        if (left >= right) {
            return left;
        }

        int middle = (left + right) / 2;


        if (sortedCumulativelbWeights.get(middle).getValue().compareTo(drawnLB) >= 0) {    // si value(middle) > value drawnLB
            if (middle == 0 || (sortedCumulativelbWeights.get(middle - 1).getValue().compareTo(drawnLB) < 0)) {
                return middle;
            } else {
                return FindLBBoundIndex(left, middle - 1, drawnLB, sortedCumulativelbWeights);
            }
        } else {

            return FindLBBoundIndex(middle + 1, right, drawnLB, sortedCumulativelbWeights);
        }
    }


    private int FindUBBoundIndex(int left, int right, BigDecimal drawnUB, ArrayList<BigDecimal> sortedCumulativelbWeights) {

        /**  Method that search a cell in cumulative object weights that has a cumulative weight smaller than  drawnobject
         *
         * Return: The cell corresponding to the object respecting the condition
         **/
        if (left >= right) {
            return left;
        }

        int middle = (left + right) / 2;

        // Using BigDecimal comparison methods
        if (sortedCumulativelbWeights.get(middle).compareTo(drawnUB) >= 0) {    // si sortedCumulativelbWeights.get(middle) >= drawnUB
            if (middle == 0 || (sortedCumulativelbWeights.get(middle - 1).compareTo(drawnUB) < 0)) {
                return middle;
            } else {
                return FindUBBoundIndex(left, middle - 1, drawnUB, sortedCumulativelbWeights);
            }
        } else {
            return FindUBBoundIndex(middle + 1, right, drawnUB, sortedCumulativelbWeights);
        }
    }


    public HashMap<Double, BigDecimal> upperBoundWeights(int m, double vgm, double selected_a) {
        HashMap<Double, BigDecimal> cumulativeUbWeights = new HashMap<>();
        //1- process Igm AND Jgm
        ArrayList<Double> Jgm = processJgm(m, vgm);
        for (double b : Jgm) {
            BigDecimal weightb = BigDecimal.valueOf(b - selected_a);
            if (cumulativeUbWeights.isEmpty()) {
                cumulativeUbWeights.put(b, weightb);
            } else {
                BigDecimal previousweights = cumulativeUbWeights.get(cumulativeUbWeights.size() - 1);
                cumulativeUbWeights.put(b, previousweights.add(weightb));
            }
        }
        return cumulativeUbWeights;
    }


    public ArrayList<Double> processIgm(int m, double vgm) {
        ArrayList<Double> Igm = new ArrayList<>();

        for (int v = 0; v < dataset.getDistinctValues().get(m).size(); v++) {
            if (dataset.getDistinctValues().get(m).get(v) <= vgm) {
                Igm.add(dataset.getDistinctValues().get(m).get(v));
            }
        }
        return Igm;
    }


    public ArrayList<Double> processJgm(int m, double vgm) {
        ArrayList<Double> Jgm = new ArrayList<>();
        for (int v = 0; v < dataset.getDistinctValues().get(m).size(); v++) {
            if (dataset.getDistinctValues().get(m).get(v) >= vgm) {
                Jgm.add(dataset.getDistinctValues().get(m).get(v));
            }
        }
        return Jgm;
    }

    ;

    public int DrawObject() {
        /*
         * Method that draws an object index proportionally to it weight
         * Return: drawn object
         * */
        BigDecimal drawnobject = BigDecimal.valueOf(randGenerator.nextDouble()).multiply(ObjectstotalIH);
        return FindObjectIndex(0, dataset.getObjectNumber() - 1, drawnobject);
    }


    private int FindObjectIndex(int left, int right, BigDecimal drawnobject) {
        /*
         * Method that searching a cell in CumulativeObjectsweights that has a cumulative weight smaller than  drawnobject
         *
         * Return: The cell corresponding to the object respecting the condition
         * */

        if(left>= right){
            return left;
        }
        int middle = (left + right) / 2;

        if (CumulativeIHweights.get(middle).compareTo(drawnobject) >= 0) {  //si middle est superieur a drawn object
            if (middle == 0 || CumulativeIHweights.get(middle - 1).compareTo(drawnobject) < 0) { // si on est au premier elt ou si l'elt en dessous est plus petit
                return middle;
            } else {
                return FindObjectIndex(left, middle, drawnobject);
            }
        } else {
            return FindObjectIndex(middle + 1, right, drawnobject);
        }
    }


    public void ProcessObjectsWeights() {
        /*
         * Method that process the weight of each object of the database through to the NIP formula
         *
         * */


        for (int g = 0; g < this.dataset.getObjectNumber(); g++) {
            BigDecimal IH = BigDecimal.ONE;
            ArrayList<BigDecimal> TIA = new ArrayList<>();

            for (int m = 0; m < this.dataset.getColumnsNumberNumerical(); m++) {
                double vgm = this.dataset.getObject(g).get(m); //ici on récupére la valeur vg,m
                if (vgm == -1) {
                    System.out.println("Warning: voir les conséquences");
                }
                BigDecimal vgmBigDecimal = BigDecimal.valueOf(vgm);
                Set<Double> Igm = new HashSet<>();
                Set<Double> Jgm = new HashSet<>();
                double sumIGM = 0;
                double sumJGM = 0;
                for (int v = 0; v < dataset.getDistinctValues().get(m).size(); v++) {
                    if (dataset.getDistinctValues().get(m).get(v) <= vgm) {
                        Igm.add(dataset.getDistinctValues().get(m).get(v));
                        sumIGM += dataset.getDistinctValues().get(m).get(v);
                    }
                    if (dataset.getDistinctValues().get(m).get(v) >= vgm) {
                        Jgm.add(dataset.getDistinctValues().get(m).get(v));
                        sumJGM += dataset.getDistinctValues().get(m).get(v);
                    }
                }


                BigDecimal processTIA = BigDecimal.valueOf(Igm.size() * sumJGM - Jgm.size() * sumIGM);
                TIA.add(processTIA);

            }

            for (BigDecimal v : TIA) {
                IH = IH.multiply(v);
            }

            if (CumulativeIHweights.isEmpty()) {
                CumulativeIHweights.add(IH);
            } else {
                BigDecimal lastCumulativeWeight = CumulativeIHweights.get(CumulativeIHweights.size() - 1);
                CumulativeIHweights.add(lastCumulativeWeight.add(IH));
            }

            ObjectstotalIH = ObjectstotalIH.add(IH);

        }
        if (Objects.equals(CumulativeIHweights.get(0), CumulativeIHweights.get(CumulativeIHweights.size() - 1))) {
            System.out.println("WARINING: The CumulativeObjectsweights contains the same weights");
        }
    }


}
