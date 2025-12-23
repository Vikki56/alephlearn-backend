// package com.example.demo.domain;

// import jakarta.persistence.*;

// @Entity
// @Table(name = "quiz_questions")
// public class QuizQuestion {

//     @Id
//     @GeneratedValue(strategy = GenerationType.IDENTITY)
//     private Long id;

//     // kis quiz ka question hai
//     @ManyToOne(fetch = FetchType.LAZY)
//     @JoinColumn(name = "quiz_id", nullable = false)
//     private Quiz quiz;

//     @Column(nullable = false, length = 2000)
//     private String text;

//     // "MCQ", "TF", "CODE" — frontend se jo aa raha hai
//     @Column(nullable = false, length = 50)
//     private String type;

//     // MCQ options (TF/CODE ke liye null ho sakte hain)
//     @Column(length = 1000)
//     private String option1;

//     @Column(length = 1000)
//     private String option2;

//     @Column(length = 1000)
//     private String option3;

//     @Column(length = 1000)
//     private String option4;

//     // MCQ / TF ke liye correct index (0-3),
//     // TF ke case me 0 = FALSE, 1 = TRUE jaisa mapping rakh sakte ho
//     private Integer correctIndex;

//     // Coding ke liye reference answer / expected output
//     @Column(length = 4000)
//     private String answerText;

//     // order: Q1, Q2, Q3 ...
//     @Column(nullable = false)
//     private int ordinalPosition;

//     // ---------- getters / setters ----------

//     public Long getId() {
//         return id;
//     }

//     public Quiz getQuiz() {
//         return quiz;
//     }

//     public void setQuiz(Quiz quiz) {
//         this.quiz = quiz;
//     }

//     public String getText() {
//         return text;
//     }

//     public void setText(String text) {
//         this.text = text;
//     }

//     public String getType() {
//         return type;
//     }

//     public void setType(String type) {
//         this.type = type;
//     }

//     public String getOption1() {
//         return option1;
//     }

//     public void setOption1(String option1) {
//         this.option1 = option1;
//     }

//     public String getOption2() {
//         return option2;
//     }

//     public void setOption2(String option2) {
//         this.option2 = option2;
//     }

//     public String getOption3() {
//         return option3;
//     }

//     public void setOption3(String option3) {
//         this.option3 = option3;
//     }

//     public String getOption4() {
//         return option4;
//     }

//     public void setOption4(String option4) {
//         this.option4 = option4;
//     }

//     public Integer getCorrectIndex() {
//         return correctIndex;
//     }

//     public void setCorrectIndex(Integer correctIndex) {
//         this.correctIndex = correctIndex;
//     }

//     public String getAnswerText() {
//         return answerText;
//     }

//     public void setAnswerText(String answerText) {
//         this.answerText = answerText;
//     }

//     public int getOrdinalPosition() {
//         return ordinalPosition;
//     }

//     public void setOrdinalPosition(int ordinalPosition) {
//         this.ordinalPosition = ordinalPosition;
//     }
// }