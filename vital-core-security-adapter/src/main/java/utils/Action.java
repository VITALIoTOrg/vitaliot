package utils;


public class Action {

    private boolean state;
    private String action;
    
    public Action(String action, boolean state) {
        this.action = action;
        this.state = state;
    }

    public boolean getState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
    
}
