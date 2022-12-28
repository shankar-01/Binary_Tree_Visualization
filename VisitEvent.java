import java.util.EventObject;
public class VisitEvent extends EventObject {

    /**
     * Constructs a prototypical Event.
     *
     * @param source the object on which the Event initially occurred
     * @throws IllegalArgumentException if source is null
     */
    //event method
    public VisitEvent(Object source) {
        super(source);

    }
}
//listener interface
