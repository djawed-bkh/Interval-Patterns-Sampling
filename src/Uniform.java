

import java.util.*;

public class Uniform {

    protected final Database dataset;
    Random randGenerator;
    int seed;
    public Uniform(Database dataset, int seed) {
        this.dataset = dataset;
        this.seed = seed;
        this.randGenerator = new Random(this.seed);
    }

    public IP drawIP() {
        /*
        * Method that draws a random IP
        * */

        ArrayList<Double> ip = new ArrayList<>();
        ArrayList<Set<Double>> CurrDistincValues = null;
        ArrayList<Integer> attributeList = new ArrayList<>();
        ArrayList<Integer> selectedAttributesList = new ArrayList<>();

            for(int m=0; m < dataset.getColumnsNumberNumerical(); m++){     // list of attributes index for the random selection
                attributeList.add(m);
            }
            for(int m = 0; m < dataset.getColumnsNumberNumerical(); m++){ // create the list containing the final IP bounds
                ip.add(-1.0);
                ip.add(-1.0);
            }

            boolean firstInterval= true;
            while (!attributeList.isEmpty()){

                int drawnAttributeIndex = (int) (randGenerator.nextDouble() * (attributeList.size()));
                int drawnAttribute = attributeList.get(drawnAttributeIndex);
                selectedAttributesList.add(drawnAttribute);
                attributeList.remove(drawnAttributeIndex);

                if (firstInterval){
                    int IndexBound1 = (int) (randGenerator.nextDouble() * (dataset.distinctValues.get(drawnAttribute).size() ));
                    int IndexBound2 = (int) (randGenerator.nextDouble() * (dataset.distinctValues.get(drawnAttribute).size()));
                    ip.set(drawnAttribute * 2,Math.min(dataset.distinctValues.get(drawnAttribute).get(IndexBound1), dataset.distinctValues.get(drawnAttribute).get(IndexBound2)));
                    ip.set(drawnAttribute * 2 +1,Math.max(dataset.distinctValues.get(drawnAttribute).get(IndexBound1), dataset.distinctValues.get(drawnAttribute).get(IndexBound2)));
                    CurrDistincValues = processPossibleDistinctValues(dataset, ip, selectedAttributesList);

                    firstInterval = false;
                }else{
                    List<Double> myset = new ArrayList<>(CurrDistincValues.get(drawnAttribute));
                    int IndexBound1 = (int) (randGenerator.nextDouble()* (myset.size()));
                    int IndexBound2 = (int) (randGenerator.nextDouble() * (dataset.distinctValues.get(drawnAttribute).size()));

                    ip.set( drawnAttribute* 2,Math.min(myset.get(IndexBound1), dataset.distinctValues.get(drawnAttribute).get(IndexBound2)));
                    ip.set(drawnAttribute * 2 + 1 ,Math.max(myset.get(IndexBound1), dataset.distinctValues.get(drawnAttribute).get(IndexBound2)));
                    CurrDistincValues = processPossibleDistinctValues(dataset, ip, selectedAttributesList);

                }

            }
        return new IP(ip, dataset, false);
    }




    public IP drawClosedIP() {
        /*
         * Method that draws a random IP
         * */

        ArrayList<Double> ip = new ArrayList<>();
        ArrayList<Set<Double>> CurrDistincValues = null;
        ArrayList<Integer> attributeList = new ArrayList<>();
        ArrayList<Integer> selectedAttributesList = new ArrayList<>();

        for(int m=0; m < dataset.getColumnsNumberNumerical(); m++){     // list of attributes index for the random selection
            attributeList.add(m);
        }
        for(int m = 0; m < dataset.getColumnsNumberNumerical(); m++){ // create the list containing the final IP bounds
            ip.add(-1.0);
            ip.add(-1.0);
        }

        boolean firstInterval= true;
        while (!attributeList.isEmpty()){

            int drawnAttributeIndex = (int) (randGenerator.nextDouble() * (attributeList.size()));
            int drawnAttribute = attributeList.get(drawnAttributeIndex);
            selectedAttributesList.add(drawnAttribute);
            attributeList.remove(drawnAttributeIndex);

            if (firstInterval){
                int IndexBound1 = (int) (randGenerator.nextDouble() * (dataset.distinctValues.get(drawnAttribute).size() ));
                int IndexBound2 = (int) (randGenerator.nextDouble() * (dataset.distinctValues.get(drawnAttribute).size()));
                ip.set(drawnAttribute * 2,Math.min(dataset.distinctValues.get(drawnAttribute).get(IndexBound1), dataset.distinctValues.get(drawnAttribute).get(IndexBound2)));
                ip.set(drawnAttribute * 2 +1,Math.max(dataset.distinctValues.get(drawnAttribute).get(IndexBound1), dataset.distinctValues.get(drawnAttribute).get(IndexBound2)));
                CurrDistincValues = processPossibleDistinctValues(dataset, ip, selectedAttributesList);

                firstInterval = false;
            }else{
                List<Double> myset = new ArrayList<>(CurrDistincValues.get(drawnAttribute));
                int IndexBound1 = (int) (randGenerator.nextDouble() * (myset.size()));
                int IndexBound2 = (int) (randGenerator.nextDouble() * (dataset.distinctValues.get(drawnAttribute).size()));

                ip.set( drawnAttribute* 2,Math.min(myset.get(IndexBound1), dataset.distinctValues.get(drawnAttribute).get(IndexBound2)));
                ip.set(drawnAttribute * 2 + 1 ,Math.max(myset.get(IndexBound1), dataset.distinctValues.get(drawnAttribute).get(IndexBound2)));
                CurrDistincValues = processPossibleDistinctValues(dataset, ip, selectedAttributesList);

            }

        }

        IP myip= new IP(ip, dataset,false);

        return  new IP(myip.closureOperator(), dataset,true);
    }





    public ArrayList<Set<Double>> processPossibleDistinctValues(Database dataset, ArrayList<Double> ip, ArrayList<Integer> selectedAttributes) {
        /*
        * Method that retrieves the distinct values for each attribute that lead to IP having at least one object covered
        *
        * Params: dataset=> Numerical database; ip=> partial interval pattern
        *
        * Retrun: ArrayList<Set<Integer>> of the set of remaining distinct values for each attribute
        * */

        ArrayList<Set<Double>> remainingDistincValues = new ArrayList<>();
        for (int m = 0; m < dataset.getColumnsNumberNumerical(); m++) { // generate |M| empty sets
            remainingDistincValues.add(new HashSet<>());
        }

        if (ip.size() % 2 != 0) {
            System.out.println("Error: The partial IP is incomplete");  // we should never be here
        } else {
            for (int g = 0; g < dataset.getObjectNumber(); g++) {
                boolean included = true;
                for (Integer m: selectedAttributes) {

                    if (ip.get(m * 2) > dataset.getObject(g).get(m) || ip.get((m * 2) + 1) < dataset.getObject(g).get(m)) {
                        included = false;
                        break;
                    }
                }
                if (included) {
                    for (int m = 0; m < dataset.getColumnsNumberNumerical(); m++) {
                        remainingDistincValues.get(m).add(dataset.getObject(g).get(m));
                    }
                }
            }
        }
        return remainingDistincValues;
    }


}
