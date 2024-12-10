public class Record {
    private Field[] fieldArr;

    public Record(Field[] fields) { // stores Field[]
        fieldArr = new Field[fields.length];
        for(int i = 0; i < fields.length; i++){ // iterate through array
            fieldArr[i] = fields[i]; // add input array obj to new array
        }
    }

    public Field[] getRec(){ // returns the Field[]
        return fieldArr;
    }
    // deleted Operation and AbstractOperation - not really sure what the point of them is
}
