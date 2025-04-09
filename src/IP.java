

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

public class IP {

    protected ArrayList<Double> IP;
    protected ArrayList<Integer> coverage;
    protected long hash;
    protected final Database dataset;

    protected BigDecimal volume;
    protected boolean closed;


    public IP(ArrayList<Double> IP, Database dataset, boolean closed) {
        this.IP = IP;
        this.dataset = dataset;
        this.closed = closed;
        this.coverage = processCoverage(dataset);
        this.volume = processVolume();
    }

    public ArrayList<Integer> processCoverage(Database db) {
        /*
        * Method that process the coverage of the current IP
        * Return: Arraylist where each cell contains a covered object
        * */

        ArrayList<Integer> cov = new ArrayList<>();
        long val = 0;
        for (int g = 0; g < db.getObjectNumber(); g++) {
            boolean included = true;
            for (int m = 0; m < db.getColumnsNumberNumerical(); m++) {
                if (this.IP.get(m * 2) > db.getObject(g).get(m) || this.IP.get((m * 2) + 1) < db.getObject(g).get(m)) {
                    included = false;
                    break;
                }
            }
            if (included) {
                cov.add(g);
                val = (val + (long) (g + 1) * 13 * (g + 7));
            }
        }
        this.hash = val;
        return cov;
    }


    public BigDecimal processDensity(){
        BigDecimal coverageSize = new BigDecimal(this.coverage.size());
        BigDecimal volume = this.processVolume();
        return coverageSize.divide(volume, RoundingMode.HALF_UP);
    }


    public BigDecimal processVolume() {
        /*
        * Method that process the volume of the current IP
        * Retrun: double value
        *
        *  */
        BigDecimal volume = BigDecimal.ONE;
        for (int i = 0; i < this.getIP().size(); i += 2) {
            double diff = this.getIP().get(i + 1) - this.getIP().get(i);

            volume = volume.multiply(BigDecimal.valueOf(diff)) ;
        }

        return volume;
    }


    public ArrayList<Double> closureOperator(){
        ArrayList<Double> CIP= new ArrayList<>();
        ArrayList<Integer> coverage= processCoverage(this.dataset);
        for(int m =0; m < dataset.columnsNumberNumerical; m++ ){
            double lb = Double.MAX_VALUE;
            double ub = Double.MIN_VALUE;
            for(int g: coverage){
                if(dataset.getObject(g).get(m)< lb){
                    lb =dataset.getObject(g).get(m);
                }
                if(dataset.getObject(g).get(m)> ub){
                    ub =dataset.getObject(g).get(m);
                }
            }
            if(lb != Integer.MAX_VALUE && ub != Integer.MIN_VALUE){
                CIP.add(lb);
                CIP.add(ub);
            }else{
                System.out.println("EMPTY COVERAGE !");
            }
        }
                return CIP;
    }


    public void showIP() {
        System.out.println("------IP DESCRIPTION: ");
        for (int i = 0; i < IP.size() - 1; i += 2) {
            System.out.print("[" + IP.get(i) + ", " + IP.get(i + 1) + "]");

        }
        System.out.println("\n");
    }

    public void showCoverage() {
        System.out.println("***IP COVERAGE");
        if(this.coverage.isEmpty()){
            System.out.println("WARNING: EMPTY COVERAGE");
            return;
        }
        System.out.print("{");
        for (Integer g : this.coverage) {
            System.out.print(g);
            System.out.print(",");
        }
        System.out.print("}");
        System.out.println("\n");
    }


    public ArrayList<Double> getIP() {
        return IP;
    }

    public void setIP(ArrayList<Double> IP) {
        this.IP = IP;
    }

    public ArrayList<Integer> getCoverage() {
        return coverage;
    }
    public double getFrequency() {
        return (double) coverage.size() / dataset.objectNumber;
    }

    public long getHash() {
        return hash;
    }

    public BigDecimal getVolume() {
        return volume;
    }
}
