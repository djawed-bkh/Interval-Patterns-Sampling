
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Database {

    public List<List<Double>> database;

    public List<List<Double>> TransposedDatabase;
    public List<List<Integer>> binaryDatabase;

    ArrayList<Map<Integer, Double>> MappingDistinctValue;
    public ArrayList<ArrayList<Integer>> attributesFrontieres;


    public List<List<Double>> getDistinctValues() {
        return distinctValues;
    }


    public List<List<Double>> distinctValues;
    public int columnsNumberNumerical;
    public int objectNumber;

    public int columnsNumberBinary;


    public int getColumnsNumberNumerical() {
        return columnsNumberNumerical;
    }

    public int getObjectNumber() {
        return objectNumber;
    }

    public List<Double> getObject(int g) {
        return database.get(g);
    }

    public List<Integer> getTrasaction(int t) {
        return binaryDatabase.get(t);
    }


    public Database(List<List<Double>> database, List<List<Double>> distinctValues) {
        this.database = database;
        this.distinctValues = distinctValues;
    }

    public Database(List<List<Double>> database) {
        this.database = database;
    }


    public void readDB(String dbPath) {
        /*
         * Method that read the database from a text file and store it in a database object
         * Params: dbPath=> the path of the text file
         * */

        database = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(dbPath))) {
            String line;

            br.readLine(); // pour ignorer la premiere ligne (col names)

            while ((line = br.readLine()) != null) {
                String[] values = line.split("\t");
                List<Double> row = new ArrayList<>();
                for (String value : values) {
                    row.add(Double.parseDouble(value.trim()));
                }
                database.add(row);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.objectNumber = database.size();
        this.columnsNumberNumerical = database.get(0).size();
        this.TransposedDatabase = transposeDB(this.database, this.columnsNumberNumerical, this.objectNumber);


        this.setDistinctValues();
        this.binarizeDB();
        this.columnsNumberBinary = binaryDatabase.get(0).size();
    }

    public void setDistinctValues() {
        /*
         * Method that set all the distinc values in an arraylist for each attribute m
         * The distinct values are set in the vector with an increasing order
         * */

        distinctValues = new ArrayList<>();
        int numCols = database.get(0).size();
        for (int col = 0; col < numCols; col++) {
            Set<Double> distinctSet = new TreeSet<>();
            for (List<Double> row : database) {
                distinctSet.add(Double.valueOf(row.get(col)));
            }
            distinctValues.add(new ArrayList<>(distinctSet));
        }
    }

    public void NormalizeDatabase(){
        /*
        * This method normalizes the database as fellow:
        *
        * - Normalize negative values
        * - Removes Categorical Values
        *
        * */



    }




    public void binarizeDB() {
        /**
         *
         * Binarize numerical dataset throught Interordinal scaling
         *
         */
        List<List<Integer>> IS_data = new ArrayList<>();

        for (int i = 0; i < distinctValues.size(); i++) {   // pour chaque attribut
            for (int j = 0; j < distinctValues.get(i).size(); j++) {        // pour chaque valeur distincte
                List<Integer> smallerOrEqual = new ArrayList<>();
                List<Integer> higherOrEqual = new ArrayList<>();

                for (int y = 0; y < TransposedDatabase.get(i).size(); y++) {
                    if (TransposedDatabase.get(i).get(y) <= distinctValues.get(i).get(j)) {
                        smallerOrEqual.add(1);
                    } else {
                        smallerOrEqual.add(0);
                    }
                    if (TransposedDatabase.get(i).get(y) >= distinctValues.get(i).get(j)) {
                        higherOrEqual.add(1);
                    } else {
                        higherOrEqual.add(0);
                    }
                }

                IS_data.add(smallerOrEqual);
                IS_data.add(higherOrEqual);
            }
        }

        binaryDatabase = transposeDBBin(IS_data, IS_data.get(0).size(), IS_data.size());
        MapValuesWithBinaryAttributes();
    }

    public void MapValuesWithBinaryAttributes() {
        /*
        * Method that maps each item in the inter-ordinal scaled database with it corresponding numerical value
        * */
        ArrayList<Map<Integer, Double>> tempMap = new ArrayList<>();
        ArrayList<ArrayList<Integer>> tempList = new ArrayList<>();

        Integer currindex = 0;
        for (int m = 0; m < this.distinctValues.size(); m++) {

            tempList.add(new ArrayList<>());

            Map<Integer, Double> MapAttm = new HashMap<>();
            tempList.get(tempList.size() - 1).add(currindex);
            for (int d = 0; d < this.distinctValues.get(m).size(); d++) {
                MapAttm.put(currindex, this.distinctValues.get(m).get(d));
                currindex++;
                MapAttm.put(currindex, this.distinctValues.get(m).get(d));
                currindex++;
            }
            tempList.get(tempList.size() - 1).add(currindex - 1);
            tempMap.add(MapAttm);
        }
        this.MappingDistinctValue = tempMap;
        this.attributesFrontieres = tempList;

    }



    public List<List<Integer>> transposeDBBin(List<List<Integer>> db, int Ncol, int Nobj) {
        /*
         * Method that transpose the database for an easier handeling
         * Params: db=> the original database arraylist; Ncol=> The number of column in db; Nobj=> The number of objects in db
         * Return: a List<List<Integer>> containing the transposed db
         * */

        // Initialize the transposed list with the dimensions swapped
        List<List<Integer>> transposedDB = new ArrayList<>();

        for (int i = 0; i < Ncol; i++) {
            transposedDB.add(new ArrayList<>());
        }

        // Perform the transpose
        for (int i = 0; i < Nobj; i++) {
            for (int j = 0; j < Ncol; j++) {
                transposedDB.get(j).add(db.get(i).get(j));
            }
        }
        return transposedDB;
    }

    public List<List<Double>> transposeDB(List<List<Double>> db, int Ncol, int Nobj) {
        /*
        * Method that transpose the database for an easier handeling
        * Params: db=> the original database arraylist; Ncol=> The number of column in db; Nobj=> The number of objects in db
        * Return: a List<List<Integer>> containing the transposed db
        * */

        // Initialize the transposed list with the dimensions swapped
        List<List<Double>> transposedDB = new ArrayList<>();

        for (int i = 0; i < Ncol; i++) {
            transposedDB.add(new ArrayList<>());
        }

        // Perform the transpose
        for (int i = 0; i < Nobj; i++) {
            for (int j = 0; j < Ncol; j++) {
                transposedDB.get(j).add(db.get(i).get(j));
            }
        }
        return transposedDB;
    }

    public void showDB() {
        for (List<Double> row : database) {
            for (Double value : row) {
                System.out.print(value + " ");
            }
            System.out.println();
        }
    }


    public void showBinarizedDB() {
        for (List<Integer> row : binaryDatabase) {
            for (Integer value : row) {
                System.out.print(value + " ");
            }
            System.out.println();
        }
    }


    public void showTransposedDB() {
        for (List<Double> row : TransposedDatabase) {
            for (Double value : row) {
                System.out.print(value + " ");
            }
            System.out.println();
        }
    }


    public void showDistinctValues() {
        System.out.println("Distinct Values:");
        for (List<Double> distinctCol : this.distinctValues) {
            System.out.println(distinctCol);
        }
    }

    public Database randomizeNumericalDB() {
        /*
        * todo: improve this code to be faster by swapping loops
        * Method that randomizes the current numerical data though random permutations of values in each attribute
        *
        * Retrun the randomized Database
        * */

        List<List<Double>> randDB = new ArrayList<>();

        // Create a copy of the original database
        for (List<Double> row : this.database) {
            randDB.add(new ArrayList<>(row));
        }

        // Shuffle values in randDB
        for (int r = 0; r < this.getObjectNumber(); r++) {
            for (int c = 0; c < this.getColumnsNumberNumerical(); c++) {
                int s = (int) (this.getObjectNumber() * Math.random());
                if (s < this.getObjectNumber() && r < this.getObjectNumber()) {
                    double tmp = randDB.get(r).get(c);
                    randDB.get(r).set(c, randDB.get(s).get(c));
                    randDB.get(s).set(c, tmp);
                }
            }
        }
        return new Database(randDB);
    }

    public void getDBStatistics(){
        System.out.println("-- Nombre d'attributs |M|: "+ this.columnsNumberNumerical);
        System.out.println("-- Nombre d'objets |G|: "+ this.objectNumber);

        int distincValues=0;
        for(int i= 0; i <this.columnsNumberNumerical; i++){
            distincValues += distinctValues.get(i).size();
        }

        System.out.println("--Nombre de valeurs distinctes: "+ distincValues);


    }


    public void setDBInfos(){
        this.objectNumber = this.database.size();
        this.columnsNumberNumerical= this.database.get(0).size();
    }


}
