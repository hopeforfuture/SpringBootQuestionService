package com.telusko.question_service.service;

import com.telusko.question_service.dao.QuestionDao;
import com.telusko.question_service.dto.QuestionRequest;
import com.telusko.question_service.dto.QuestionWrapper;
import com.telusko.question_service.dto.QuizResultResponse;
import com.telusko.question_service.model.Question;
import com.telusko.question_service.model.QuizAnswer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class QuestionService {

    @Autowired
    private QuestionDao questionDao;

    public List<Question> getAllQuestions() {
        return questionDao.findAll();
    }

    public List<Question> getQuestions(
            String active,
            String category) {

        // ------------------------------------------------
        // 1. No category provided
        // ------------------------------------------------
        if (category == null || category.isBlank()) {

            // No active parameter
            // => return active questions
            if (active == null || active.isBlank()) {
                return questionDao.findByActive(true);
            }

            // active=all
            if ("all".equalsIgnoreCase(active)) {
                return questionDao.findAll();
            }

            // active=true / false
            Boolean activeStatus = parseActiveStatus(active);

            return questionDao.findByActive(activeStatus);
        }


        // ------------------------------------------------
        // 2. Category provided
        // ------------------------------------------------

        // category + active not provided
        // => active questions in that category
        if (active == null || active.isBlank()) {
            return questionDao.findByCategoryIgnoreCaseAndActive(
                    category,
                    true
            );
        }

        // category + active=all
        if ("all".equalsIgnoreCase(active)) {
            return questionDao.findByCategoryIgnoreCase(category);
        }

        // category + active=true/false
        Boolean activeStatus = parseActiveStatus(active);

        return questionDao.findByCategoryIgnoreCaseAndActive(
                category,
                activeStatus
        );
    }

    public List<Question> getQuestionsByCategory(String category) {

        System.out.println("Category: " + category);
        return questionDao.findByCategoryIgnoreCase(category);
    }

    public Question createQuestion(QuestionRequest request) {

        Question question = new Question();

        question.setQuestionTitle(request.getQuestionTitle());
        question.setOption1(request.getOption1());
        question.setOption2(request.getOption2());
        question.setOption3(request.getOption3());
        question.setOption4(request.getOption4());
        question.setRightAnswer(request.getRightAnswer());
        question.setDifficultylevel(request.getDifficultylevel());
        question.setCategory(request.getCategory());

        return questionDao.save(question);
    }

    public Question updateQuestion(Integer id, QuestionRequest request) {

        // 1. Find existing question
        Question question = questionDao.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Question not found with id: " + id)
                );

        // 2. Update fields
        question.setQuestionTitle(request.getQuestionTitle());
        question.setOption1(request.getOption1());
        question.setOption2(request.getOption2());
        question.setOption3(request.getOption3());
        question.setOption4(request.getOption4());
        question.setRightAnswer(request.getRightAnswer());
        question.setDifficultylevel(request.getDifficultylevel());
        question.setCategory(request.getCategory());

        // 3. Save updated entity
        return questionDao.save(question);
    }

    // ACTIVATE / DEACTIVATE
    public Question updateQuestionStatus(
            Integer id,
            Boolean active) {

        Question question = questionDao.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Question not found with id: " + id
                        )
                );

        question.setActive(active);

        return questionDao.save(question);
    }

    private Boolean parseActiveStatus(String active) {

        if ("true".equalsIgnoreCase(active)) {
            return true;
        }

        if ("false".equalsIgnoreCase(active)) {
            return false;
        }

        throw new IllegalArgumentException(
                "Invalid active value. Use true, false, or all."
        );
    }

    public ResponseEntity<List<Integer>> getQuestionsForQuiz(String categoryName, Integer numQ) {
        List<Integer> questions = questionDao.findRandomQuestionsByCategory(categoryName, numQ);
        return new ResponseEntity<>(questions, HttpStatus.OK);
    }

    public ResponseEntity<List<QuestionWrapper>> getQuestionsForId(List<Integer> questionsIds) {
        List<QuestionWrapper> questionWrappers = new ArrayList<>();
        List<Question> questions = new ArrayList<>();

        for(Integer id : questionsIds) {
            questions.add(questionDao.findById(id).get());
        }

        for(Question question : questions) {
            QuestionWrapper questionWrapper = new QuestionWrapper();
            questionWrapper.setId(question.getId());
            questionWrapper.setQuestionTitle(question.getQuestionTitle());
            questionWrapper.setOption1(question.getOption1());
            questionWrapper.setOption2(question.getOption2());
            questionWrapper.setOption3(question.getOption3());
            questionWrapper.setOption4(question.getOption4());
            questionWrappers.add(questionWrapper);
        }

        return new ResponseEntity<>(questionWrappers, HttpStatus.OK);
    }

    public ResponseEntity<QuizResultResponse> getScore(
            List<QuizAnswer> quizAnswers) {
        int right = 0;

        for (QuizAnswer quizAnswer : quizAnswers) {
            Question question = questionDao.findById(quizAnswer.getId()).get();
            if (quizAnswer.getResponse()
                    .equals(question.getRightAnswer())) {
                right++;
            }
        }

        QuizResultResponse response =
                new QuizResultResponse(
                        "Quiz submitted successfully",
                        right
                );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
