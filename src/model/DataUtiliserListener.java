package model;

import java.util.EventListener;

//observer interface
public interface DataUtiliserListener extends EventListener
{
    /**
     *  Sent when markets end
     */
    //public void drawingInvalidated(DrawingChangeEvent e);

    /**
     * Sent when game time updates, if this one then we modify the game time
     */
    
    /**
     * sent when game events occur, if this one then we modify our event list
     */
    
    /**
     * sent when market data is ready to be collected, if this one then we modify our graphs
     */
    
    
    /**
     *  Sent when the drawing wants to be refreshed
     */
}
