/*
 * Copyright 2011 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.workbench.screens.guided.dtable.client.wizard.pages;

import org.drools.workbench.models.guided.dtable.shared.model.ConditionCol52;
import org.uberfire.client.mvp.UberView;

import java.util.List;

/**
 * View and Presenter definitions for the Column Expansion page
 */
public interface ColumnExpansionPageView
        extends
        UberView<ColumnExpansionPageView.Presenter>,
        RequiresValidator {

    interface Presenter {

        void setColumnsToExpand( List<ConditionCol52> columns );

        List<ConditionCol52> getColumnsToExpand();

    }

    void setAvailableColumns( List<ConditionCol52> columns );

    void setChosenColumns( List<ConditionCol52> columns );

    void setAreConditionsDefined( boolean areConditionsDefined );

}
