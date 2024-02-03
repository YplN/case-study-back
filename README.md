# AgileValue Coding Challenge - Backend

This project is the backend part of an application designed to handle surveys. The application exposes several RESTful API endpoints to insert new submission to the database, and get the list of all the submissions.

## Project setup

```sh
./gradlew bootRun
```

**Note**: The application will start on port 8080 by default.

using your preferred client, such as Postman you can now send REST requests.

## API Endpoints

| Methods | Endpoints                   | Actions                                                                        | Status Codes                   | CROSS     |
| ------- | --------------------------- | ------------------------------------------------------------------------------ | ------------------------------ | --------- |
| GET     | /api/answer                 | retrieve the list of all the answers                                           | OK/NO CONTENT                  | X         |
| DELETE  | /api/answer/:id             | delete a answer by :id                                                         | NO CONTENT                     | X         |
| GET     | /api/answer/:id             | retrieve a answer by :id                                                       | OK/NO CONTENT                  | X         |
| PUT     | /api/answer/:id             | update a answer by :id                                                         | NO CONTENT/BAD REQUEST         | X         |
|         |                             |                                                                                |                                | X         |
| GET     | /api/question               | retrieve the list of all the questions                                         | OK/NO CONTENT                  | X         |
| GET     | /api/question/:id           | retrieve a question by :id                                                     | OK/NO CONTENT                  | X         |
| DELETE  | /api/question/:id           | delete a question by :id                                                       | NO CONTENT                     | X         |
| PUT     | /api/question/:id           | update a question by :id                                                       | NO CONTENT                     | X         |
| GET     | /api/question/json          | retrieve the list of all the questions                                         | OK                             | X         |
| GET     | /api/question/:id/answer    | retrieve all answers for question with :id                                     | OK/NO CONTENT                  | X         |
| POST    | /api/question/:id/answer    | create a new answer for question with :id                                      | CREATED/NO CONTENT/BAD REQUEST | X         |
| DELETE  | /api/question/:id/answer    | delete all answers for question with :id                                       | NO CONTENT                     | X         |
|         |                             |                                                                                |                                | X         |
| GET     | /api/surveys                | retrieve the list of all the surveys                                           | OK                             | X         |
| GET     | /api/surveys/json           | retrieve the list of all the surveys in json                                   | OK                             | X         |
| POST    | /api/surveys                | create a new survey                                                            | CREATED                        | X         |
| GET     | /api/surveys/:id            | retrieve all the question of a survey by :id                                   | OK/NO CONTENT                  | X         |
| DELETE  | /api/surveys/:id            | delete a survey by :id                                                         | NO CONTENT/BAD REQUEST         | X         |
|         |                             |                                                                                |                                | X         |
| GET     | /api/surveys/:id/answer     | retrieve all answers for survey with :id                                       | OK/NO CONTENT/BAD REQUEST      | X         |
| GET     | /api/surveys/:id/question   | retrieve all questions for survey with :id                                     | OK/NO CONTENT                  | X         |
| DELETE  | /api/surveys/:id/question   | delete all questions for survey with :id                                       | NO CONTENT/BAD REQUEST         | X         |
| POST    | /api/surveys/:id/question   | create a new question for survey with :id                                      | CREATED/NO CONTENT/BAD REQUEST | X         |
| GET     | /api/surveys/:id/results    | retrieves results for survey :id sorted by user                                | OK/NO CONTENT/BAD REQUEST      | X         |
| GET     | /api/surveys/results/full   | retrieves results for survey :id                                               | OK/NO CONTENT/BAD REQUEST      | X         |
| POST    | /api/surveys/:id/submit     | add a new submission to the survey :id                                         | OK/BAD REQUEST/INTERNAL ERROR  | port 5173 |
| GET     | /api/surveys/:id/user/:uuid | retrieve the survey :id if user :uuid never answered it                        | OK/NO CONTENT/BAD REQUEST      | port 5173 |
| GET     | /api/surveys/user/:uuid     | retrieve the list of all surveys sorted depending if :uuid answered them or no | OK                             | port 5173 |

### Some details for the main endpoints

#### 1. POST `/api/surveys/:id/submit`

Send a POST request with a Submission. This will add all the answers to the submission to the database.
A submission corresponds to the UUID of the user who submitted, and a list of {questionId: number, rating:number|null}, where questionId is the Id of the question in the database, and rating is the rating given by the user to the corresponding question.

For example, if we have submitted the submission to the database:

```json
{
	"userUuid": "00000000-0000-0000-0000-000000000001",
	"submissions": [
		{
			"questionId": 1,
			"rating": 5
		},
		{
			"questionId": 2,
			"rating": 4
		},
		{
			"questionId": 3,
			"rating": null
		}
	]
}
```

will add the ratings 5 and 4 to the questions 1 and 2, respectively to the database.

**Note** If the user with the same UUID already answered these questions, the submission will be rejected.

#### 2. GET `/api/surveys/user/:uuid`

Given the UUID :uuid, it will return a map corresponding to the list of the surveys answered and not answered by this UUID.

For example, if you get the following response:

```json
{
  "answered":[
    {
      "id": 3,
      ...rest data of the survey...
    }
  ],
  "notAnswered":[
    {
      "id": 1,
      ...rest data of the survey...
    },
    {
      "id": 2,
      ...rest data of the survey...
    }
  ]
}

```

means the this UUID answered the survey with id 3, but not the surveys with id 1 and 2.

#### 3. GET `/api/surveys/:id/results`

This endpoint will return the full results for the survey with the given id. The answers will be merged by UUIDs.

For example, you can have the following result:

```json
[
    {
        "userUuid": "00000000-0000-0000-0000-000000000001",
        "userAnswers": [
            {
                "idQuestion": 1,
                "idAnswer": 14,
                "textQuestion": "How satisfied are you with the level of collaboration within your team?",
                "answerRating": 4
            },
            ...
            {
                "idQuestion": 5,
                "idAnswer": 17,
                "textQuestion": "To what extent do team members support each other's ideas and initiatives?",
                "answerRating": 3
            }
        ]
    },
    {
        "userUuid": "00000000-0000-0000-0000-000000000002",
        "userAnswers": [
            {
                "idQuestion": 3,
                "idAnswer": 27,
                "textQuestion": "Rate the level of trust among team members.",
                "answerRating": 3
            },
          ...
            {
                "idQuestion": 5,
                "idAnswer": 29,
                "textQuestion": "To what extent do team members support each other's ideas and initiatives?",
                "answerRating": 1
            }
        ]
    },
    {
        "userUuid": "00000000-0000-0000-0000-000000000003",
        "userAnswers": [
            {
                "idQuestion": 1,
                "idAnswer": 1,
                "textQuestion": "How satisfied are you with the level of collaboration within your team?",
                "answerRating": 4
            },
           ...
            {
                "idQuestion": 5,
                "idAnswer": 4,
                "textQuestion": "To what extent do team members support each other's ideas and initiatives?",
                "answerRating": 5
            }
        ]
    }
]
```

#### 4. GET `/api/surveys/:id/results/full`

This is another endpoint to get the results of a survey. The only difference with this endpoint is that the results are sorted in a different order. It follows the following structure:

```json
{
    "id": id_of_the_survey,
    "title": title_of_the_survey,
    "desc": description_of_the_survey,
    "questions": [
        {
            "id": id_of_the_question,
            "text": text_of_the_question,
            "answers": [
                {
                    "id": id_of_the_answer,
                    "rating": rating_of_the_answer,
                    "userUuid": uuid_of_the_answer
                },
                ... other answers
            ]
        },
       ... other questions
    ]
}

```

This format is more appropriate to aggregate the data from the different answers.

## Test scripts

To run the java tests, type the command

```sh
./gradlew test
```

it should run the following tests:

- ANSWERS
  - shouldNotUpdateAnExistingAnswerIfItIsNotTheSameUserUuid()
  - shouldNotReturnAnAnswerThatDoesNotExist()
  - shouldNotCreateAnAnswerWithNoUserUuid()
  - shouldDeleteAnExistingAnswer()
  - shouldUpdateAnExistingAnswer()
  - shouldNotDeleteAnAnswerThatDoesNotExist()
  - shouldAcceptToCreateAnAnswerWithNullAsRating()
  - shouldDeleteAllAnswersOfAnExistingQuestion()
  - shouldNotChangeNumberOfAnswersWhenUpdatingAnExistingAnswer()
  - shouldProperlyFindAnExistingAnswer()
  - shouldNotUpdateAnAnswerThatDoesNotExist()
  - shouldCreateANewAnswer()
  - shouldNotReturnAnswersOfAQuestionThatDoesNotExist()
  - shouldReturnAllAnswers()
  - shouldReturnAllAnswersOfAnExistingSurvey()
- QUESTIONS
  - shouldReturnAllQuestions()
  - shouldReturnAllQuestionsOfAnExistingSurvey()
  - shouldNotReturnQuestionsOfASurveyThatDoesNotExist()
  - shouldNotUpdateAQuestionThatDoesNotExist()
  - shouldCreateANewQuestion()
  - shouldProperlyFindAnExistingQuestion()
  - shouldDeleteAllQuestionsOfAnExistingSurvey()
  - shouldUpdateAnExistingQuestion()
  - shouldReturnNotFoundWhenDeletingQuestionsOfASurveyThatDoesNotExist()
  - shouldNotUpdateAQuestionWithNoId()
  - shouldNotDeleteAQuestionWithNoId()
  - shouldRejectToCreateAQuestionWithEmptyText()
  - shouldNotDeleteAQuestionThatDoesNotExist()
  - shouldDeleteAnExistingQuestion()
  - shouldNotReturnAQuestionThatDoesNotExist()
- SURVEY
  - shouldCreateANewSurvey()
  - shouldNotDeleteASurveyWithNoId()
  - shouldDeleteAnExistingSurvey()
  - shouldNotReturnPrivateKeyOfANewSurvey()
  - shouldNotReturnASurveyThatDoesNotExist()
  - shouldNotDeleteASurveyThatDoesNotExist()
  - shouldDeleteAllAnswersWhenDeletingAnExistingSurvey()
  - shouldNotReturnPrivateIdOfAnExistingSurvey()
  - shouldReturnAllSurveys()
  - shouldDeleteAllQuestionsWhenDeletingAnExistingSurvey()
  - shouldProperlyFindAnExistingSurvey()
- SUBMISSION
  - shouldReturnTheListOfSurveysAnsweredWithTheSameUserUuid()
  - shouldNotProcessAUserSubmissionIfOneOfTheQuestionsDoNotBelongToTheSurvey()
  - shouldProcessANewUserSubmissionEvenIfAllQuestionsAreNotAnswered()
  - shouldProcessANewUserSubmissionEvenIfOneRatingIsNull()
  - shouldNotProcessAUserSubmissionIfTheUserAlreadyReplied()
  - shouldProcessANewUserSubmission()
  - shouldReturnTheSurveysNotAnsweredByUserUuid()
  - shouldNotReturnTheSurveysUserUuidIfSurveyIdDoesNotExist()

## Notes and limitations

### Survey ids

It is possible to access to the answers for a survey by using its id. Right now, the id of the survey is "public" since it appears directly in the url of the frontend page.
However, it would be possible to be able to access to the results of the survey only with a private id, different from the one appearing in the url for the user.

### UUID

So far, the UUIDs sent for submissions are computed locally and stored in the user's local storage. This approach aligns with several principles:

- **No Account Connection Requirement**: Users can answer surveys without needing to create an account or log in.
- **Prevention of Duplicate Responses**: Each user "cannot" submit multiple responses to a survey (discussed further below).
- **User Anonymity**: The user's anonymity is maintained in the database.

However, it's crucial to recognize the limitations of this method. This approach lacks resilience against malicious users, as local storage can be deleted through various means: directly from the browser, using private navigation, using a different browser, or switching to another device. Specifically, ensuring adherence to the three principles mentioned earlier becomes challenging when considering the latter scenario.
