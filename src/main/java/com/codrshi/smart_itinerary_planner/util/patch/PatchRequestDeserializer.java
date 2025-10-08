package com.codrshi.smart_itinerary_planner.util.patch;

import com.codrshi.smart_itinerary_planner.common.Constant;
import com.codrshi.smart_itinerary_planner.common.enums.PatchOperation;
import com.codrshi.smart_itinerary_planner.dto.IPatchDataDTO;
import com.codrshi.smart_itinerary_planner.dto.request.IPatchItineraryRequestDTO;
import com.codrshi.smart_itinerary_planner.dto.implementation.request.PatchItineraryRequestDTO;
import com.codrshi.smart_itinerary_planner.exception.BadRequestException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PatchRequestDeserializer extends JsonDeserializer<IPatchItineraryRequestDTO> {


    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public IPatchItineraryRequestDTO deserialize(JsonParser jsonParser, DeserializationContext context)
            throws IOException {

        JsonNode node = jsonParser.readValueAsTree();
        JsonNode patchOpNode = node.get(Constant.PATCH_OPERATION);
        JsonNode patchDataNode = node.get(Constant.PATCH_DATA);

        Class<? extends IPatchDataDTO> targetClass;
        List<IPatchDataDTO> patchDataList = new ArrayList<>();

        if(patchOpNode == null || patchOpNode.isNull() || patchOpNode.asText().isBlank()) {
            throw new BadRequestException(String.format(Constant.ERR_MSG_MISSING_FIELD, Constant.PATCH_OPERATION));
        }

        if(patchDataNode == null || patchDataNode.isNull()) {
            throw new BadRequestException(String.format(Constant.ERR_MSG_MISSING_FIELD, Constant.PATCH_DATA));
        }

        try {
            targetClass = PatchDataRegistry.getClass(patchOpNode.asText());
        }
        catch(IllegalArgumentException ex) {
            throw new BadRequestException(ex.getMessage());
        }

        try {
            for (JsonNode patchNode : patchDataNode) {
                patchDataList.add(objectMapper.treeToValue(patchNode, targetClass));
            }
        }
        catch (Exception ex) {
            throw new BadRequestException(Constant.ERR_MSG_INVALID_PATCH_DATA_STRUCTURE);
        }

        IPatchItineraryRequestDTO patchItineraryRequestDTO = new PatchItineraryRequestDTO();
        patchItineraryRequestDTO.setPatchOperation(PatchOperation.fromString(patchOpNode.asText()));
        patchItineraryRequestDTO.setPatchData(patchDataList);
        return patchItineraryRequestDTO;
    }
}
