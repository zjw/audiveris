//----------------------------------------------------------------------------//
//                                                                            //
//                              R u n B o a r d                               //
//                                                                            //
//----------------------------------------------------------------------------//
// <editor-fold defaultstate="collapsed" desc="hdr">                          //
//  Copyright (C) Herve Bitteur 2000-2010. All rights reserved.               //
//  This software is released under the GNU General Public License.           //
//  Goto http://kenai.com/projects/audiveris to report bugs or suggestions.   //
//----------------------------------------------------------------------------//
// </editor-fold>
package omr.run;

import omr.lag.Lag;

import omr.log.Logger;

import omr.selection.MouseMovement;
import omr.selection.RunEvent;
import omr.selection.SelectionService;
import omr.selection.UserEvent;

import omr.ui.Board;
import omr.ui.field.LIntegerField;
import omr.ui.util.Panel;

import omr.util.Implement;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import org.bushe.swing.event.EventSubscriber;

/**
 * Class <code>RunBoard</code> is dedicated to display of Run information.
 *
 * @author Hervé Bitteur
 */
public class RunBoard
    extends Board
{
    //~ Static fields/initializers ---------------------------------------------

    /** Usual logger utility */
    private static final Logger logger = Logger.getLogger(RunBoard.class);

    /** Events this entity is interested in */
    private static final Class[] eventClasses = new Class[] { RunEvent.class };

    //~ Instance fields --------------------------------------------------------

    /** Field for run length */
    private final LIntegerField rLength = new LIntegerField(
        false,
        "Length",
        "Length of run in pixels");

    /** Field for run level */
    private final LIntegerField rLevel = new LIntegerField(
        false,
        "Level",
        "Average pixel level on this run");

    /** Field for run start */
    private final LIntegerField rStart = new LIntegerField(
        false,
        "Start",
        "Pixel coordinate at start of run");

    //~ Constructors -----------------------------------------------------------

    //----------//
    // RunBoard //
    //----------//
    /**
     * Create a Run Board on a lag, initially collapsed
     * @param unitName name of the owning unit
     * @param lag the related lag
     */
    public RunBoard (String unitName,
                     Lag    lag)
    {
        this(unitName, lag, false);
    }

    //----------//
    // RunBoard //
    //----------//
    /**
     * Create a Run Board on a lag
     * @param unitName name of the owning unit
     * @param lag the related lag
     * @param expanded true for expanded, false for collapsed
     */
    public RunBoard (String  unitName,
                     Lag     lag,
                     boolean expanded)
    {
        this(unitName, lag.getRunService(), expanded);
    }

    //----------//
    // RunBoard //
    //----------//
    /**
     * Create a Run Board
     * @param unitName name of the owning unit
     * @param selectionService the service handling run selections
     * @param expanded true for expanded, false for collapsed
     */
    public RunBoard (String           unitName,
                     SelectionService selectionService,
                     boolean          expanded)
    {
        super(
            unitName + "-RunBoard",
            "Run",
            selectionService,
            eventClasses,
            expanded);
        defineLayout();
    }

    //~ Methods ----------------------------------------------------------------

    //---------//
    // onEvent //
    //---------//
    /**
     * Call-back triggered when Run Selection has been modified
     *
     * @param event the notified event
     */
    @Implement(EventSubscriber.class)
    public void onEvent (UserEvent event)
    {
        try {
            // Ignore RELEASING
            if (event.movement == MouseMovement.RELEASING) {
                return;
            }

            if (logger.isFineEnabled()) {
                logger.fine("RunBoard: " + event);
            }

            if (event instanceof RunEvent) {
                final RunEvent runEvent = (RunEvent) event;
                final Run      run = runEvent.run;

                if (run != null) {
                    rStart.setValue(run.getStart());
                    rLength.setValue(run.getLength());
                    rLevel.setValue(run.getLevel());
                } else {
                    emptyFields(getBody());
                }
            }
        } catch (Exception ex) {
            logger.warning(getClass().getName() + " onEvent error", ex);
        }
    }

    //--------------//
    // defineLayout //
    //--------------//
    private void defineLayout ()
    {
        FormLayout   layout = Panel.makeFormLayout(1, 3);
        PanelBuilder builder = new PanelBuilder(layout, getBody());
        builder.setDefaultDialogBorder();

        CellConstraints cst = new CellConstraints();
        int             r = 1; // --------------------------------

        builder.add(rStart.getLabel(), cst.xy(1, r));
        builder.add(rStart.getField(), cst.xy(3, r));

        builder.add(rLength.getLabel(), cst.xy(5, r));
        builder.add(rLength.getField(), cst.xy(7, r));

        builder.add(rLevel.getLabel(), cst.xy(9, r));
        builder.add(rLevel.getField(), cst.xy(11, r));
    }
}
