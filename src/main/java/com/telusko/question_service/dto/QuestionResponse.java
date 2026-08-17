package com.telusko.question_service.dto;

import com.telusko.question_service.model.Question;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionResponse {

    private int count;
    private List<Question> questions;
}
