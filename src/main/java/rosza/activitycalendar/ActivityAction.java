/**
 * Activity action class
 * 
 * @author Szalay Roland
 * 
 */
package rosza.activitycalendar;

public class ActivityAction {
  private static String   activityCommand;
  private static Activity activityValue;

  // Create new ActivityAction instance
  public ActivityAction(String command, Activity activity) {
    activityCommand = command;
    activityValue = activity;
  }

  // Getter methods
  public String getActionCommand() {
    return activityCommand;
  }

  public Activity getActivity() {
    return activityValue;
  }

  // Setter methods
  public void setActionCommand(String command) {
    activityCommand = command;
  }

  public void setActivity(Activity activity) {
    activityValue = activity;
  }
}
