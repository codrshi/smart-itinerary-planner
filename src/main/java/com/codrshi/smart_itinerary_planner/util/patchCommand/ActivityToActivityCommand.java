package com.codrshi.smart_itinerary_planner.util.patchCommand;

import com.codrshi.smart_itinerary_planner.common.Constant;

import java.util.Map;

public class ActivityToActivityCommand implements IPatchCommand {
    private Map<String, ActivityNode> activityAdjacencyMap;

    public ActivityToActivityCommand(Map<String, ActivityNode> activityAdjacencyMap) {
        this.activityAdjacencyMap = activityAdjacencyMap;
    }

    @Override
    public void execute(String sourceId, String targetId) {

        ActivityNode sourceNode = activityAdjacencyMap.get(sourceId);
        ActivityNode targetNode = activityAdjacencyMap.get(targetId);

        if(!activityAdjacencyMap.containsKey(sourceId) || !activityAdjacencyMap.containsKey(targetId) || sourceNode == null) {
            return;
        }

        if(targetNode == null) {
            activityAdjacencyMap.put(targetId, sourceNode);
        }
        else {
            ActivityNode tailNode = getTailNode(targetNode);
            tailNode.setNext(sourceNode);
        }

        activityAdjacencyMap.put(sourceId, null);
    }

    private ActivityNode getTailNode(ActivityNode node) {
        if(node == null) {
            return null;
        }

        ActivityNode tailNode = node;
        while(tailNode.getNext() != null) {
            tailNode = tailNode.getNext();
        }
        return tailNode;
    }
}
