package com.example.demo.domain.entity;

import com.example.demo.domain.Quiz;
import com.example.demo.domain.QuizQuestionType;
import jakarta.persistence.*;

@Entity
@Table(name = "quiz_questions")
public class QuizQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id", nullable = false)
    private Quiz quiz;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private QuizQuestionType type;

    @Column(nullable = false, length = 4000)
    private String text;

    @Column(length = 1000)
    private String option1;

    @Column(length = 1000)
    private String option2;

    @Column(length = 1000)
    private String option3;

    @Column(length = 1000)
    private String option4;

    private Integer correctIndex;

    private Boolean correctBool;

    @Column(length = 4000)
    private String codingAnswer;

    @Column(nullable = false)
    private int ordinalPosition;

    // ---------- getters / setters ----------

    public Long getId() {
        return id;
    }

    public Quiz getQuiz() {
        return quiz;
    }

    public void setQuiz(Quiz quiz) {
        this.quiz = quiz;
    }

    public QuizQuestionType getType() {
        return type;
    }

    public void setType(QuizQuestionType type) {
        this.type = type;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getOption1() {
        return option1;
    }

    public void setOption1(String option1) {
        this.option1 = option1;
    }

    public String getOption2() {
        return option2;
    }

    public void setOption2(String option2) {
        this.option2 = option2;
    }

    public String getOption3() {
        return option3;
    }

    public void setOption3(String option3) {
        this.option3 = option3;
    }

    public String getOption4() {
        return option4;
    }

    public void setOption4(String option4) {
        this.option4 = option4;
    }

    public Integer getCorrectIndex() {
        return correctIndex;
    }

    public void setCorrectIndex(Integer correctIndex) {
        this.correctIndex = correctIndex;
    }

    public Boolean getCorrectBool() {
        return correctBool;
    }

    public void setCorrectBool(Boolean correctBool) {
        this.correctBool = correctBool;
    }

    public String getCodingAnswer() {
        return codingAnswer;
    }

    public void setCodingAnswer(String codingAnswer) {
        this.codingAnswer = codingAnswer;
    }

    public int getOrdinalPosition() {
        return ordinalPosition;
    }

    public void setOrdinalPosition(int ordinalPosition) {
        this.ordinalPosition = ordinalPosition;
    }
}