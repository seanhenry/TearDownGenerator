package codes.seanhenry.analytics;

public class TrackerSpy implements Tracker {

    public int invocationCount = 0;
    public String invokedCategory;
    public String invokedAction;
    public String invokedValue;

    @Override
    public void track(String category, String action, String value) {
        invocationCount += 1;
        invokedCategory = category;
        invokedAction = action;
        invokedValue = value;
    }
}
