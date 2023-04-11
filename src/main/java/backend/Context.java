package backend;

public class Context {
    private int id;
    private String text;
    private int mamaID;
    private int start;
    private int end;
    private float cost;

    public Context(int id, int mamaID, String text) {
        this.id = id;
        this.mamaID=mamaID;
        this.text = text;
    }

    public void setStart(int start) {
        this.start = start;
    }
    
    public void setEnd(int end) {
        this.end = end;
    }

    public void setCost(float cost) {
        this.cost = cost;
    }
    
    public int getStart() {
        return start;
    }
    
    public int getEnd() {
    	return end;
    }
    
    public float getCost() {
    	return cost;
    }
    
    public int getID() {
    	return id;
    }
    
    public int getMamaID() {
    	return mamaID;
    }

    public String[] asArray() {
        String[] result = new String[6];
        result[0] = Integer.toString(id);
        result[1] = text;
        result[2] = Integer.toString(mamaID);
        result[3] = Integer.toString(start);
        result[4] = Integer.toString(end);
        result[5] = Float.toString(cost);

        return result;
    }
}