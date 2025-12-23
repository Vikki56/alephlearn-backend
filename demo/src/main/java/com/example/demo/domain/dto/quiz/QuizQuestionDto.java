package com.example.demo.domain.dto.quiz;

import com.example.demo.domain.QuizQuestionType;
import java.util.List;

public class QuizQuestionDto {
    private Long id;
    private QuizQuestionType type;   // MULTIPLE_CHOICE / TRUE_FALSE / CODING
    private String text;
    private List<String> options;    // MCQ options (0–4)
    private Integer correctIndex;    // MCQ: 0-3
    private Boolean correctBool;     // TRUE_FALSE
    private String codingAnswer;     // CODING
    private Integer ordinalPosition; // 1,2,3,...

    // getters + setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public QuizQuestionType getType() { return type; }
    public void setType(QuizQuestionType type) { this.type = type; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public List<String> getOptions() { return options; }
    public void setOptions(List<String> options) { this.options = options; }

    public Integer getCorrectIndex() { return correctIndex; }
    public void setCorrectIndex(Integer correctIndex) { this.correctIndex = correctIndex; }

    public Boolean getCorrectBool() { return correctBool; }
    public void setCorrectBool(Boolean correctBool) { this.correctBool = correctBool; }

    public String getCodingAnswer() { return codingAnswer; }
    public void setCodingAnswer(String codingAnswer) { this.codingAnswer = codingAnswer; }

    public Integer getOrdinalPosition() { return ordinalPosition; }
    public void setOrdinalPosition(Integer ordinalPosition) { this.ordinalPosition = ordinalPosition; }
}