package net.almaak.tweets.app.conf;

/**
 * Created by leiferksn on 9/15/16.
 */

public enum AllowedOperation {
    DELETE("d", "Deletes a list of tweets.");

    private final String operation;
    private final String operationDesc;

    AllowedOperation(String aOperation, String aOperationDesc) {
        operation = aOperation;
        operationDesc = aOperationDesc;
    }

    @Override
    public String toString() {
        return "( " + operation + " : " + operationDesc + " )";
    }

    public static AllowedOperation getOperationByString(final String opString){
        for(AllowedOperation op : AllowedOperation.values()) {
            if (op.operation.equalsIgnoreCase(opString)) {
                return op;
            }
        }
        return null;
    }

    public static String listValues(){
        StringBuffer buf = new StringBuffer();
        for(AllowedOperation op : AllowedOperation.values()){
            buf.append(op.toString());
            buf.append(",");
        }
        String values = buf.toString();
        values = values.substring(0, values.length() - 1);
        return values;
    }

}
