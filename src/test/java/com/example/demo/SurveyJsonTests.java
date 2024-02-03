// package com.example.demo;

// import com.example.demo.model.Question;
// import com.example.demo.model.Survey;
// import org.assertj.core.util.Arrays;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.json.JsonTest;
// import org.springframework.boot.test.json.JacksonTester;

// import java.io.IOException;
// import java.util.ArrayList;
// import java.util.List;

// import static org.assertj.core.api.Assertions.assertThat;

// @JsonTest
// public class SurveyJsonTests {

// @Autowired
// private JacksonTester<Survey> json;

// @Autowired
// private JacksonTester<Survey[]> jsonList;

// private Survey[] surveys;

// @BeforeEach
// void setUp() {

// // List<Question> questionsSurvey1 = new ArrayList<>();
// // questionsSurvey1.add(new Question("Question 1 for survey 1?"));
// // questionsSurvey1.add(new Question("Question 2 for survey 1?"));

// // List<Question> questionsSurvey2 = new ArrayList<>();
// // questionsSurvey2.add(new Question("Question 1 for survey 2?"));
// // questionsSurvey2.add(new Question("Question 2 for survey 2?"));

// // surveys = Arrays.array(
// // new Survey("First survey",
// // "Description for the first survey",
// // questionsSurvey1),
// // new Survey("Second survey",
// // "Description for the second survey",
// // questionsSurvey2));
// }

// @Test
// void firstTest() {
// assertThat(1).isEqualTo(1);
// }

// // @Test
// // void surveySerializationTest() throws IOException {
// // Survey survey = surveys[0];
// // assertThat(json.write(survey)).isStrictlyEqualToJson("single.json");
// // assertThat(json.write(survey)).hasJsonPathNumberValue("@.id");
// // assertThat(json.write(survey)).extractingJsonPathNumberValue("@.id")
// // .isEqualTo(99);
// // assertThat(json.write(survey)).hasJsonPathNumberValue("@.amount");
// // assertThat(json.write(survey)).extractingJsonPathNumberValue("@.amount")
// // .isEqualTo(123.45);
// // }

// // @Test
// // void surveyDeserializationTest() throws IOException {
// // String expected = """
// // {
// // "id": 1,
// // "title": "First survey",
// // "desc": "Description for the first survey",
// // "questions": [
// // {
// // "text": "Question 1 for survey 1??",
// // "id": 1
// // },
// // {
// // "text": "Question 2 for survey 1?",
// // "id": 2
// // }
// // ]
// // }
// // """;
// // assertThat(json.parse(expected))
// // .isEqualTo(surveys[0]);
// // assertThat(json.parseObject(expected).getId()).isEqualTo(1L);
// // assertThat(json.parseObject(expected).getTitle()).isEqualTo("First
// survey");
// // assertThat(json.parseObject(expected).getDesc()).isEqualTo("Description
// for
// // the first survey");
// // }
// //
// // @Test
// // void surveyListSerializationTest() throws IOException {
// // assertThat(jsonList.write(surveys)).isStrictlyEqualToJson("list.json");
// // }
// //
// // @Test
// // void surveyListDeserializationTest() throws IOException {
// // String expected = """
// // [
// // {"id": 99, "amount": 123.45 , "owner": "sarah1"},
// // {"id": 100, "amount": 1.00 , "owner": "sarah1"},
// // {"id": 101, "amount": 150.00, "owner": "sarah1"}
// //
// // ]
// // """;
// // assertThat(jsonList.parse(expected)).isEqualTo(surveys);
// // }

// }
