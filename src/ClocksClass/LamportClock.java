package ClocksClass;

/**
 * Created by Marchu on 2/1/17.
 */
public class LamportClock {

    private int c;

    public LamportClock(){
        c = 1;
    }
    public int getValue(){
        return c;
    }
    public void tick(){
        c = c + 1;
    }
    public void sendAction(){
        c = c + 1;
    }
    public void receiveAction(int src, int sentValue){
        c = Math.max(c, sentValue) + 1;
    }

}
