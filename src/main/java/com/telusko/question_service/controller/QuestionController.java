package com.telusko.question_service.controller;

import com.telusko.question_service.dto.*;
import com.telusko.question_service.model.Question;
import com.telusko.question_service.model.QuizAnswer;
import com.telusko.question_service.service.QuestionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1")
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    @GetMapping("questions")
    public ResponseEntity<QuestionResponse> getQuestions(
            @RequestParam(required = false) String active,
            @RequestParam(required = false) String category) {

        List<Question> questions =
                questionService.getQuestions(active, category);

        QuestionResponse response =
                new QuestionResponse(questions.size(), questions);

        return ResponseEntity.ok(response);
    }

    @GetMapping("questions/category/{category}")
    public ResponseEntity<QuestionResponse> getQuestionsByCategory(@PathVariable("category") String category) {
        List<Question> questions = questionService.getQuestionsByCategory(category);
        QuestionResponse response =
                new QuestionResponse(questions.size(), questions);
        return ResponseEntity.ok(response);
    }

    @PostMapping("questions")
    public ResponseEntity<Question> createQuestion(
            @Valid @RequestBody QuestionRequest request) {

        Question question = questionService.createQuestion(request);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping("questions/{id}")
    public ResponseEntity<Question> updateQuestion(
            @PathVariable Integer id,
            @Valid @RequestBody QuestionRequest request) {

        Question question = questionService.updateQuestion(id, request);

        return ResponseEntity.ok(question);
    }

    // ACTIVATE / DEACTIVATE
    @PatchMapping("questions/{id}/status")
    public ResponseEntity<Question> updateQuestionStatus(
            @PathVariable Integer id,
            @Valid @RequestBody QuestionStatusRequest request) {

        Question question =
                questionService.updateQuestionStatus(
                        id,
                        request.getActive()
                );

        return ResponseEntity.ok(question);
    }

    @GetMapping("/quiz")
    public ResponseEntity<List<Integer>> getQuestionsForQuiz(@RequestParam String categoryName, @RequestParam Integer numQ) {
        return questionService.getQuestionsForQuiz(categoryName, numQ);
    }

    @PostMapping("questions/getQuestions")
    public ResponseEntity<List<QuestionWrapper>> getQuestionsFromId(@RequestBody List<Integer> questionsIds) {
        return questionService.getQuestionsForId(questionsIds);
    }

    @PostMapping("/quiz/getScore")
    public ResponseEntity<QuizResultResponse> getScore(@RequestBody List<QuizAnswer> responses) {
        return questionService.getScore(responses);
    }
}
