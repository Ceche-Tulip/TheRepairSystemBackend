package org.trs.therepairsystem.service;

import org.trs.therepairsystem.dto.response.AIAnswerResponse;

public interface AIService {
    AIAnswerResponse askAI(String question);
}
