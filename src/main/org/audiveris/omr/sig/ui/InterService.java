//------------------------------------------------------------------------------------------------//
//                                                                                                //
//                                     I n t e r S e r v i c e                                    //
//                                                                                                //
//------------------------------------------------------------------------------------------------//
// <editor-fold defaultstate="collapsed" desc="hdr">
//
//  Copyright © Audiveris 2018. All rights reserved.
//
//  This program is free software: you can redistribute it and/or modify it under the terms of the
//  GNU Affero General Public License as published by the Free Software Foundation, either version
//  3 of the License, or (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
//  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
//  See the GNU Affero General Public License for more details.
//
//  You should have received a copy of the GNU Affero General Public License along with this
//  program.  If not, see <http://www.gnu.org/licenses/>.
//------------------------------------------------------------------------------------------------//
// </editor-fold>
package org.audiveris.omr.sig.ui;

import org.audiveris.omr.glyph.Glyph;
import org.audiveris.omr.sig.SIGraph;
import org.audiveris.omr.sig.inter.Inter;
import org.audiveris.omr.sig.inter.HeadInter;
import org.audiveris.omr.sig.inter.Inters;
import org.audiveris.omr.ui.ViewParameters;
import org.audiveris.omr.ui.selection.EntityListEvent;
import org.audiveris.omr.ui.selection.EntityService;
import org.audiveris.omr.ui.selection.IdEvent;
import org.audiveris.omr.ui.selection.LocationEvent;
import org.audiveris.omr.ui.selection.SelectionHint;
import static org.audiveris.omr.ui.selection.SelectionHint.ENTITY_INIT;
import org.audiveris.omr.ui.selection.SelectionService;
import org.audiveris.omr.util.EntityIndex;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Point;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Class {@code InterService} is an EntityService for inters.
 *
 * @author Hervé Bitteur
 */
public class InterService
        extends EntityService<Inter>
{

    private static final Logger logger = LoggerFactory.getLogger(InterService.class);

    /** Events that can be published on inter service. */
    private static final Class<?>[] eventsAllowed = new Class<?>[]{
        EntityListEvent.class,
        IdEvent.class};

    /**
     * Creates a new {@code InterService} object.
     *
     * @param index           underlying inter index (InterManager)
     * @param locationService related service for location info
     */
    public InterService (EntityIndex<Inter> index,
                         SelectionService locationService)
    {
        super(index, locationService, eventsAllowed);
    }

    //-----------------//
    // getMostRelevant //
    //-----------------//
    @Override
    protected Inter getMostRelevant (List<Inter> list,
                                     Point location)
    {
        switch (list.size()) {
        case 0:
            return null;

        case 1:
            return list.get(0);

        default:
            List<Inter> copy = new ArrayList<>(list);
            Collections.sort(copy, Inters.membersFirst);

            Inter selected = copy.get(0);

            if (selected instanceof HeadInter && selected.getMirror() != null) {
                HeadInter head = (HeadInter) selected;
                Line2D line = head.getMidLine();

                if (location != null && line.relativeCCW(location) > 0) {
                    selected = selected.getMirror();
                }
            }

            return selected;
        }
    }

    //-----------------------//
    // handleEntityListEvent //
    //-----------------------//
    /**
     * Interest in EntityList
     *
     * @param listEvent list of inters
     */
    @Override
    protected void handleEntityListEvent (EntityListEvent<Inter> listEvent)
    {
        final SelectionHint hint = listEvent.hint;

        if (hint == ENTITY_INIT) {
            final Inter inter = listEvent.getEntity();

            if (inter != null) {
                final SIGraph sig = inter.getSig();

                if (sig != null) {
                    // Publish underlying glyph, perhaps null
                    final Glyph glyph = inter.getGlyph();
                    sig.getSystem().getSheet().getGlyphIndex().publish(glyph);
                }
            }
        }

        // Publish selected inter last, so that display of its bounds remains visible
        super.handleEntityListEvent(listEvent);
    }

    //---------------------//
    // handleLocationEvent //
    //---------------------//
    /**
     * Interest in location &rArr; list
     *
     * @param locationEvent the location event
     */
    @Override
    protected void handleLocationEvent (LocationEvent locationEvent)
    {
        // Search only when in MODE_INTER or MODE_GLYPH
        if (ViewParameters.getInstance()
                .getSelectionMode() != ViewParameters.SelectionMode.MODE_SECTION) {
            super.handleLocationEvent(locationEvent);
        }
    }
}
