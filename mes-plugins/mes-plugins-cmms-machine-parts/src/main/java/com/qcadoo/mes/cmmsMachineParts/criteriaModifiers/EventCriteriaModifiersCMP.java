/**
 * ***************************************************************************
 * Copyright (c) 2010 Qcadoo Limited
 * Project: Qcadoo MES
 * Version: 1.4
 *
 * This file is part of Qcadoo.
 *
 * Qcadoo is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation; either version 3 of the License,
 * or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * ***************************************************************************
 */
package com.qcadoo.mes.cmmsMachineParts.criteriaModifiers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.qcadoo.mes.basic.ParameterService;
import com.qcadoo.mes.basic.constants.BasicConstants;
import com.qcadoo.mes.basic.constants.DivisionFields;
import com.qcadoo.mes.basic.constants.SubassemblyFields;
import com.qcadoo.mes.basic.constants.WorkstationFields;
import com.qcadoo.mes.cmmsMachineParts.constants.FaultTypeFields;
import com.qcadoo.mes.cmmsMachineParts.constants.MaintenanceEventFields;
import com.qcadoo.mes.cmmsMachineParts.constants.PlannedEventFields;
import com.qcadoo.mes.cmmsMachineParts.constants.PlannedEventType;
import com.qcadoo.mes.cmmsMachineParts.states.constants.MaintenanceEventState;
import com.qcadoo.mes.cmmsMachineParts.states.constants.MaintenanceEventStateChangeFields;
import com.qcadoo.mes.cmmsMachineParts.states.constants.PlannedEventState;
import com.qcadoo.mes.productionLines.constants.WorkstationFieldsPL;
import com.qcadoo.model.api.DataDefinition;
import com.qcadoo.model.api.DataDefinitionService;
import com.qcadoo.model.api.search.JoinType;
import com.qcadoo.model.api.search.SearchCriteriaBuilder;
import com.qcadoo.model.api.search.SearchCriterion;
import com.qcadoo.model.api.search.SearchRestrictions;
import com.qcadoo.view.api.components.lookup.FilterValueHolder;

@Service public class EventCriteriaModifiersCMP {

    private static final String L_OTHER = "Inne";

    public static final String EVENT_CONTEXT_FILTER_PARAMETER_FACTORY = "maintenanceEventContextFactory";

    public static final String EVENT_CONTEXT_FILTER_PARAMETER_DIVISION = "maintenanceEventContextDivision";

    @Autowired private ParameterService parameterService;

    @Autowired private DataDefinitionService dataDefinitionService;

    public void hideFailedStateChanges(final SearchCriteriaBuilder scb) {
        scb.add(SearchRestrictions.eq(MaintenanceEventStateChangeFields.STATUS, "03successful"));
    }

    public void filterRevokedAndPlannedEvents(final SearchCriteriaBuilder scb) {
        scb.add(SearchRestrictions.ne(MaintenanceEventFields.STATE, MaintenanceEventState.REVOKED.getStringValue()))
                .add(SearchRestrictions.ne(MaintenanceEventFields.STATE, MaintenanceEventState.PLANNED.getStringValue()));
    }

    public void filterCanceledEvents(final SearchCriteriaBuilder scb) {
        scb.add(SearchRestrictions.ne(PlannedEventFields.STATE, PlannedEventState.CANCELED.getStringValue()))
                .add(SearchRestrictions.ne(PlannedEventFields.TYPE, PlannedEventType.REVIEW.getStringValue()))
                .add(SearchRestrictions.ne(PlannedEventFields.TYPE, PlannedEventType.UDT_REVIEW.getStringValue()))
                .add(SearchRestrictions.ne(PlannedEventFields.TYPE, PlannedEventType.METER_READING.getStringValue()));
    }

    public void selectFactory(final SearchCriteriaBuilder scb, final FilterValueHolder filterValue) {
    }

    public void selectDivision(final SearchCriteriaBuilder scb, final FilterValueHolder filterValue) {
        if (filterValue.has(MaintenanceEventFields.FACTORY)) {
            DataDefinition factoryDataDefinition = dataDefinitionService
                    .get(BasicConstants.PLUGIN_IDENTIFIER, BasicConstants.MODEL_FACTORY);
            scb.add(SearchRestrictions.belongsTo(DivisionFields.FACTORY, factoryDataDefinition,
                    filterValue.getLong(MaintenanceEventFields.FACTORY)));
        }
    }

    public void selectWorkstation(final SearchCriteriaBuilder scb, final FilterValueHolder filterValue) {
        if (filterValue.has(MaintenanceEventFields.DIVISION)) {
            Long divisionId = filterValue.getLong(MaintenanceEventFields.DIVISION);
            scb.createAlias(WorkstationFields.DIVISION, WorkstationFields.DIVISION, JoinType.INNER)
                    .add(SearchRestrictions.eq(WorkstationFields.DIVISION + ".id", divisionId));
        }
        if (filterValue.has(MaintenanceEventFields.PRODUCTION_LINE)) {
            Long productionLineId = filterValue.getLong(MaintenanceEventFields.PRODUCTION_LINE);
            scb.createAlias(WorkstationFieldsPL.PRODUCTION_LINE, WorkstationFieldsPL.PRODUCTION_LINE, JoinType.INNER)
                    .add(SearchRestrictions.eq(WorkstationFieldsPL.PRODUCTION_LINE + ".id", productionLineId));
        }
    }

    public void selectSubassembly(final SearchCriteriaBuilder scb, final FilterValueHolder filterValue) {
        if (filterValue.has(MaintenanceEventFields.WORKSTATION)) {
            DataDefinition workstationDataDefinition = dataDefinitionService
                    .get(BasicConstants.PLUGIN_IDENTIFIER, BasicConstants.MODEL_WORKSTATION);
            scb.add(SearchRestrictions.belongsTo(SubassemblyFields.WORKSTATION, workstationDataDefinition,
                    filterValue.getLong(MaintenanceEventFields.WORKSTATION)));
        }
    }

    public void selectFaultType(final SearchCriteriaBuilder scb, final FilterValueHolder filterValue) {
        if (filterValue.has(MaintenanceEventFields.SUBASSEMBLY)) {
            addSubassemblyCriteria(scb, filterValue);

        } else if (filterValue.has(MaintenanceEventFields.WORKSTATION)) {
            addWorkstationCriteria(scb, filterValue);
        }

    }

    private void addSubassemblyCriteria(final SearchCriteriaBuilder scb, final FilterValueHolder filterValue) {
        Long subassemblyId = filterValue.getLong(MaintenanceEventFields.SUBASSEMBLY);
        addCriteriaForElementAndWorkstationType(scb, filterValue, subassemblyId, FaultTypeFields.SUBASSEMBLIES);
    }

    private void addWorkstationCriteria(final SearchCriteriaBuilder scb, final FilterValueHolder filterValue) {
        Long workstationId = filterValue.getLong(MaintenanceEventFields.WORKSTATION);
        addCriteriaForElementAndWorkstationType(scb, filterValue, workstationId, FaultTypeFields.WORKSTATIONS);
    }

    private void addCriteriaForElementAndWorkstationType(final SearchCriteriaBuilder scb, final FilterValueHolder filterValue,
            Long elementId, String alias) {
        SearchCriterion criterion;
        if (filterValue.has(WorkstationFields.WORKSTATION_TYPE)) {
            Long workstationTypeId = filterValue.getLong(WorkstationFields.WORKSTATION_TYPE);
            criterion = SearchRestrictions.or(SearchRestrictions.eq(FaultTypeFields.WORKSTATION_TYPES + ".id", workstationTypeId),
                    SearchRestrictions.eq(alias + ".id", elementId));
        } else {
            criterion = SearchRestrictions.eq(alias + ".id", elementId);
        }
        scb.createAlias(FaultTypeFields.WORKSTATION_TYPES, FaultTypeFields.WORKSTATION_TYPES, JoinType.LEFT)
                .createAlias(alias, alias, JoinType.LEFT)
                .add(SearchRestrictions.or(criterion, SearchRestrictions.eq(FaultTypeFields.NAME, L_OTHER)));
    }

    public void showEventsFromContext(final SearchCriteriaBuilder scb, final FilterValueHolder filterValue) {
        if (filterValue.has(EVENT_CONTEXT_FILTER_PARAMETER_FACTORY)) {
            scb.add(SearchRestrictions
                    .eq(MaintenanceEventFields.FACTORY + "_id", filterValue.getInteger(EVENT_CONTEXT_FILTER_PARAMETER_FACTORY)));
        }

        if (filterValue.has(EVENT_CONTEXT_FILTER_PARAMETER_DIVISION)) {
            scb.add(SearchRestrictions
                    .eq(MaintenanceEventFields.DIVISION + "_id", filterValue.getInteger(EVENT_CONTEXT_FILTER_PARAMETER_DIVISION)));
        }

    }

    public void showPlannedEventsFromContext(final SearchCriteriaBuilder scb, final FilterValueHolder filterValue) {
        if (filterValue.has(EVENT_CONTEXT_FILTER_PARAMETER_FACTORY)) {
            scb.add(SearchRestrictions
                    .eq(MaintenanceEventFields.FACTORY + "_id", filterValue.getInteger(EVENT_CONTEXT_FILTER_PARAMETER_FACTORY)));
        }

        if (filterValue.has(EVENT_CONTEXT_FILTER_PARAMETER_DIVISION)) {
            scb.add(SearchRestrictions
                    .eq(MaintenanceEventFields.DIVISION + "_id", filterValue.getInteger(EVENT_CONTEXT_FILTER_PARAMETER_DIVISION)));
        }
    }

}
