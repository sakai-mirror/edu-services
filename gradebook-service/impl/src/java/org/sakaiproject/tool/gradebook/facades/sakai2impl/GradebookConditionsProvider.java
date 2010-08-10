package org.sakaiproject.tool.gradebook.facades.sakai2impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sakaiproject.conditions.api.ConditionProvider;
import org.sakaiproject.conditions.api.ConditionService;
import org.sakaiproject.service.gradebook.shared.Assignment;
import org.sakaiproject.service.gradebook.shared.GradebookService;

public class GradebookConditionsProvider implements ConditionProvider {
	
	private Map<String, String> eventLookup = new HashMap<String, String>();
	
	private ConditionService conditionService;
	public void setConditionService(ConditionService conditionService) {
		this.conditionService = conditionService;
	}
	
	private GradebookService gbs;
	public void setGradebookService(GradebookService gradebookService) {
		this.gbs = gradebookService;
	}
	
	public void init() {
		eventLookup.put("gradebook.updateItemScore", "org.sakaiproject.conditions.impl.AssignmentGrading");
		eventLookup.put("gradebook.updateAssignment", "org.sakaiproject.conditions.impl.AssignmentUpdate");
		conditionService.registerConditionProvider(this);
	}

	public Map<String, String> getEntitiesForContext(String gradebookUid) {
		Map<String, String> rv = new HashMap<String, String>();
		List<Assignment> assignments = gbs.getAssignments(gradebookUid);
		for (Assignment asn : assignments) {
			String assignmentName = asn.getName();
			String assignmentPoints = asn.getPoints().toString();
			boolean isReleasedToStudents = asn.isReleased();
			boolean isUsedInGradeCalculation = asn.isCounted();
			Date dueDate = asn.getDueDate();
			long dueDateMillis = 0;
			if (dueDate != null) dueDateMillis = dueDate.getTime();
			// event resource of the form: /gradebook/[gradebook id]/[assignment name]/[points possible]/[due date millis]/[is released]/[is included in course grade]/[has authz]
			rv.put("/gradebook/"+ gradebookUid + "/" + assignmentName + "/" + assignmentPoints + "/" + dueDateMillis + "/" + isReleasedToStudents + "/" + isUsedInGradeCalculation , assignmentName + " (" + assignmentPoints + " points)");
		}
		return rv;
	}

	public String getId() {
		return "gradebook";
	}

	public Map<String, String> getEventToDomainClassMapping() {
		return eventLookup;
	}

}
