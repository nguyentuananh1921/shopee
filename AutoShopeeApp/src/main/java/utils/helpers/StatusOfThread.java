package utils.helpers;

public class StatusOfThread {
    private boolean threadStatus;

    public StatusOfThread(boolean threadStatus) {
        this.threadStatus = threadStatus;
    }

    public boolean isThreadStatus() {
        return threadStatus;
    }

    public void setThreadStatus(boolean threadStatus) {
        this.threadStatus = threadStatus;
    }
}
