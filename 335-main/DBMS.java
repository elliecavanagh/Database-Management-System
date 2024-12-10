import java.io.File;
import java.io.FileNotFoundException;
import java.security.InvalidKeyException;
import java.io.IOException;
import java.util.Scanner;
import java.util.ArrayList;
import java.io.FileWriter;

public class DBMS implements Process, Connection {

    private File databaseFile;
    public ArrayList<Record> data = new ArrayList<>();
    String[] fieldValues;

    public void connect(String filename) throws FileNotFoundException {
        databaseFile = new File(filename);
        if(!databaseFile.exists() || !databaseFile.canWrite()){
            throw new FileNotFoundException("File not found or unavailable.");
        } else {
            Scanner reader = new Scanner(databaseFile);
            System.out.println("File connected successfully.");
            // Grabbing Field Names
            final String firstLine = reader.nextLine().toLowerCase(); // holds line - id,name,number - keeping it from being case sensitive
            //System.out.println(firstLine);
            fieldValues = firstLine.split(","); // make sure this gets read into the arraylist

            // Grabbing Field Values and Assigning to Field Objects, Record Array and Data ArrayList
            while(reader.hasNextLine()){ // line by line
                final String line = reader.nextLine().toLowerCase(); // holds line - 12,pear,123 - keeping it from being case sensitive
                String[] values = line.split(",");
                Field[] y = new Field[fieldValues.length]; // new Field[]
                for(int i = 0; i < fieldValues.length; i++){
                    Field x = new Field(); // new Field obj
                    x.setName(fieldValues[i]); // adding Field obj name (field)
                    x.setValue(values[i]); // adding Field obj value - bugs out with empty text file or any blank lines
                    y[i] = (x); // add Fields to Field[]
                }
                Record rec = new Record(y); // new record object which holds Field[]
                data.add(rec);// add record array to data ArrayList
            }
            reader.close();
        }
    }

    public void disconnect(String filename) throws FileNotFoundException {
        File x = new File(filename); // setting string to a File makes it easier to compare and get consistent results
        if(databaseFile.getName().equals(x.getName())){ // checking if inputted file matches currently connected file
            if(!databaseFile.exists()){
                //File not found or not connected
                throw new FileNotFoundException("File not found.");
            } else {
                //Successful Disconnect
                data.clear();
                System.out.println("File successfully disconnected.");
            }
        } else {
            throw new FileNotFoundException("Error: File not connected.");
        }
    }


    public Record insert(Record rec) throws InvalidKeyException, IOException  {
        // checks if the dbFile has any connection
        if (databaseFile == null) {
            throw new IllegalStateException("No connection established. Call connect() first.");
        } else {
            Field[] fields = rec.getRec(); // pull Field[] from record
                for (Field field : fields) { // iterate through Field[]
                    String name = field.getName(); // grabbing primary key - should be first entry in the record
                    String val = field.getValue();
                    if (contains(name, val)) { // check if primary key already exists in the database
                        throw new InvalidKeyException("Record with primary key already exists."); // primary key already exists
                    } else { // primary key doesn't exist. add to database, write to file.
                        data.add(rec); // add to database
                        try {
                            FileWriter writer = new FileWriter(databaseFile, false); // Overwrite file
                            writer.write(String.join(",", fieldValues) + "\n"); // Write header

                            // Write new record to the file
                            for (Record remainingRecord : data) { // iterate through remaining Record objs in ArrayList
                                StringBuilder line = new StringBuilder(); // new StringBuilder
                                Field[] y = remainingRecord.getRec(); // new array to hold the arrays held by Record objs
                                for (Field value : y) { // iterate through remaining Field[] to rewrite in dbFile
                                    line.append(value.getValue()).append(",");
                                }
                                writer.write(line.substring(0, line.length() - 1) + "\n");
                            }
                            System.out.println("File updated successfully.");
                            writer.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                            throw new IllegalStateException("Failed to update the database file after deletion.");
                        }
                        break;
                    }
                }
        }
        return rec;
    }

    // Checks if a specific field contains the given value
    public boolean contains(String field, String value) {
        boolean result = false;
        if (databaseFile == null) {
            throw new IllegalStateException("No connection established. Call connect() first.");
        } else {
            for (int i = 0; i < data.size(); i++) {
                for (int j = 0; j < fieldValues.length; j++){
                    Record y = data.get(i);
                    Field[] x = y.getRec();
                    String name = x[j].getName();
                    String val = x[j].getValue();
                    if (name.equals(field) && val.equals(value)){
                        result = true;
                        break; // exit loop
                    } else {
                        result = false;
                    }
                }
                if(result){ // if result == true
                    break; // exit loop
                }
            }
        }
        return result;
    }

    //Inputting two Field objects - one that you're looking for (search) and one you're updating the record with
    public Record[] update(Field search, Field modify) throws IOException {
        if (databaseFile == null){
            throw new IllegalArgumentException("No connection established. Call connect() first.");
        }
        ArrayList<Record> updatedRecords = new ArrayList<>(); // ArrayList to hold updated records

        // Input Field info
        String searchName = search.getName();
        String searchVal = search.getValue();
        String modifyName = modify.getName();
        String newVal = modify.getValue();

        if (contains(searchName, searchVal)) { // if the database contains a matching name/value pair
            for (Record record : data) { // iterating through database / arraylist
                Field[] fields = record.getRec(); // new Field[] to hold [] from Record
                boolean recordUpdated = false;
                for (Field item : fields) { // looking for search values
                    if (item.getName().equals(searchName) && (item.getValue().equals(searchVal))) {// if found
                        for (Field field : fields) { // iterates through record items to find field to update
                            if (field.getName().equals(modifyName)) { // if field name equals the modify field
                                field.setValue(newVal); // set new value
                                recordUpdated = true;
                            }
                        }
                        if (recordUpdated) {
                            updatedRecords.add(record); // adding updated record to the arraylist
                        }
                        break;
                    }
                }
            }
            try {
                FileWriter writer = new FileWriter(databaseFile, false); // Overwrite file
                writer.write(String.join(",", fieldValues) + "\n"); // Write header

                // Update record(s) in the file
                for (Record updatedRecord : data) {
                    StringBuilder line = new StringBuilder();
                    Field[] fields = updatedRecord.getRec();
                    for (Field field : fields) {
                        line.append(field.getValue()).append(",");
                    }
                    writer.write(line.substring(0, line.length() - 1) + "\n");
                }
                writer.close();
                System.out.println("File updated successfully.");
            } catch (IOException e) {
                e.printStackTrace();
                throw new IllegalStateException("Failed to update the database file after deletion.");
            }
        } else {
            throw new IOException("No records found.");
        }
        Record[] rec = updatedRecords.toArray(new Record[0]);
        return rec;
    }


    public Record[] select(Field search) throws FileNotFoundException {

        if (databaseFile == null) { // Ensure there is an active database connection
            throw new IllegalStateException("No connection established. Call connect() first.");
        }

        ArrayList<Record> matchingRecords = new ArrayList<>();
        String name = search.getName();
        String value = search.getValue();

        if (contains(name, value)) {
            for (Record record : data) {
                Field[] field = record.getRec();
                for (Field fields : field) {
                    // Check if the field matches the search criteria
                    if (fields.getName().equals(name) && fields.getValue().equals(value)) {
                        matchingRecords.add(record); // Add the matching record to the list
                        break;
                    }
                }
            }
        } else { // Handle case if no matching records are found
            throw new FileNotFoundException("No matching records. :( ");
        }
        return matchingRecords.toArray(new Record[0]);
    }

    public Record[] delete(Field search) throws FileNotFoundException { // only deleting one record

        if (databaseFile == null) {
            throw new IllegalStateException("No connection established. Call connect() first.");
        }

        ArrayList<Record> deletedRecords = new ArrayList<>();
        String name = search.getName();
        String value = search.getValue();

        if(contains(name, value)) {
            for (Record record : data) { // iterating through arraylist
                Field[] fields = record.getRec(); // array holding one line of data
                for (Field field : fields) { // iterating through each object in array
                    if (field.getName().equals(name) && field.getValue().equals(value)) { // if the field and value matches the inputted one
                        deletedRecords.add(record); // add record to a deleted records arraylist
                    }
                }
            }
            for (Record delete : deletedRecords){
                data.remove(delete); //remove record from data
            }
            // Update the database file to reflect deletions
            try {
                FileWriter writer = new FileWriter(databaseFile, false); // Overwrite file
                writer.write(String.join(",", fieldValues) + "\n"); // Write header

                // Write remaining records to the file
                for (Record remainingRecord : data) {
                    StringBuilder line = new StringBuilder();
                    Field[] fields = remainingRecord.getRec();
                    for (Field field : fields) {
                        line.append(field.getValue()).append(",");
                    }
                    writer.write(line.substring(0, line.length() - 1) + "\n");
                }
                writer.close();
                System.out.println("File updated successfully after deletion.");
            } catch (IOException e) {
                e.printStackTrace();
                throw new IllegalStateException("Failed to update the database file after deletion.");
            }
        } else { // Handle case if no matching records are found
            throw new FileNotFoundException("No records found matching the search criteria for deletion.");
        }
        return deletedRecords.toArray(new Record[0]);
    }
}


