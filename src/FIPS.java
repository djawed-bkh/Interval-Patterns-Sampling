
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

public class FIPS {

    protected final Database dataset;
    ArrayList<BigDecimal> CumulativeObjectsweights = new ArrayList<>();
    BigDecimal ObjectstotalWeight = BigDecimal.ZERO;
    Random randGenerator;
    int seed;
    public FIPS(Database dataset, int seed) {
        this.dataset = dataset;
        this.seed = seed;
        this.randGenerator = new Random(this.seed);
        ProcessObjectsWeights();
    }


    public void ProcessObjectsWeights() {
        /*
        * Method that process the weight of each object of the database through to the NIP formula
        *
        * */

        for (int g = 0; g < this.dataset.getObjectNumber(); g++) {
            BigDecimal objectWeight = BigDecimal.ONE;
            for (int m = 0; m < this.dataset.getColumnsNumberNumerical(); m++) {

                double vgm = this.dataset.getObject(g).get(m);

                if (vgm == -1) {
                    System.out.println("WARNING: we should not be here");
                }
                BigDecimal vgmBigDecimal = BigDecimal.valueOf(vgm);
                BigDecimal distinctValuesSizeBigDecimal = BigDecimal.valueOf(dataset.getDistinctValues().get(m).size());


                BigDecimal vgmIndex = BigDecimal.valueOf(getIndexValue(vgm,m)); // recupere l'indice de vgm dans la liste de valeurs distinctes


                BigDecimal component = vgmIndex
                        .add(distinctValuesSizeBigDecimal.subtract(vgmIndex).subtract(BigDecimal.ONE))
                        .add(vgmIndex.multiply(distinctValuesSizeBigDecimal.subtract(vgmIndex).subtract(BigDecimal.ONE)))
                        .add(BigDecimal.ONE);
                objectWeight = objectWeight.multiply(component);
            }

            if (CumulativeObjectsweights.isEmpty()) {
                CumulativeObjectsweights.add(objectWeight);
            } else {
                BigDecimal lastCumulativeWeight = CumulativeObjectsweights.get(CumulativeObjectsweights.size() - 1);
                CumulativeObjectsweights.add(lastCumulativeWeight.add(objectWeight));
            }

            ObjectstotalWeight = ObjectstotalWeight.add(objectWeight);

        }
        if(Objects.equals(CumulativeObjectsweights.get(0), CumulativeObjectsweights.get(CumulativeObjectsweights.size() - 1))){
            System.out.println("WARINING: The CumulativeObjectsweights contains the same weights");
        }
    }

    public IP drawIP() {

        /* Method that draws an interval pattern proportional to fréquency
         *
         * Return: sampled IP
         */
        ArrayList<Double> resultingIP = new ArrayList<>();

        int drawnObject1 = DrawObject();           // draw object proportionally to w(g)
        for (int m = 0; m < dataset.getColumnsNumberNumerical(); m++) {
            double value = dataset.getObject(drawnObject1).get(m);   // ON RÉCUPÉRE La valeur vg,m
            int indexValue= getIndexValue(value, m); //todo: voir si ça fonctionne

            int indexlb = (int) (randGenerator.nextDouble() * indexValue);            //WARNING: works only for normalized data
            int indexub = (indexValue + (int) (randGenerator.nextDouble() * ((dataset.getDistinctValues().get(m).size()) - indexValue))); //WARNING: works only for normalized data
            resultingIP.add(dataset.distinctValues.get(m).get(indexlb));
            resultingIP.add(dataset.distinctValues.get(m).get(indexub));
        }
        return new IP(resultingIP, dataset, false);
    }

    public int getIndexValue(double value, int m){
        for(int i =0; i < dataset.getDistinctValues().get(m).size(); i++){
            if(dataset.getDistinctValues().get(m).get(i)== value){
                return i;
            }

        }
        return -1;
    }

    public int DrawObject() {
        /*
        * Method that draws an object index proportionally to it weight
        * Return: drawn object
        * */

        BigDecimal drawnobject = BigDecimal.valueOf(randGenerator.nextDouble()).multiply(ObjectstotalWeight);
        return FindIndex(0, dataset.getObjectNumber()-1, drawnobject);
    }

    private int FindIndex(int left, int right, BigDecimal drawnobject) {
        /*
        * Method that searching a cell in CumulativeObjectsweights that has a cumulative weight smaller than  drawnobject
        *
        * Return: The cell corresponding to the object respecting the condition
        * */

        int middle = (left + right) / 2;

        // Using BigDecimal comparison methods
        if (CumulativeObjectsweights.get(middle).compareTo(drawnobject) >= 0) {
            if (middle == 0 || CumulativeObjectsweights.get(middle - 1).compareTo(drawnobject) < 0) {
                return middle;
            } else {
                return FindIndex(left, middle, drawnobject);
            }
        } else {
            return FindIndex(middle + 1, right, drawnobject);
        }
    }


}









