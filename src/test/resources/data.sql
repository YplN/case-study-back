DELETE FROM ANSWER;
DELETE FROM QUESTION;
DELETE FROM SURVEY;

ALTER TABLE ANSWER ALTER COLUMN answer_id RESTART WITH 1;
ALTER TABLE QUESTION ALTER COLUMN question_id RESTART WITH 1;
ALTER TABLE SURVEY ALTER COLUMN survey_id RESTART WITH 1;


-- SURVEYS --------------------------------
INSERT INTO SURVEY (SURVEY_TITLE, SURVEY_DESC)
VALUES 
    ('Example Survey 1', 'Description of Example Survey 1'),
    ('Example Survey 2', 'Description of Example Survey 2'),
    ('Example Survey 3', 'Description of Example Survey 3');


-- QUESTIONS --------------------------------
INSERT INTO QUESTION(QUESTION_TEXT, SURVEY_ID) 
VALUES 
-- Survey 1
    ('Question 1.1', 1), -- ID 1
    ('Question 1.2', 1),
    ('Question 1.3', 1),
-- Survey 2
    ('Question 2.1', 2),
    ('Question 2.2', 2), -- ID 5
    ('Question 2.3', 2),
    ('Question 2.4', 2),
    ('Question 2.5', 2),
-- Survey 3
    ('Question 3.1', 3),
    ('Question 3.2', 3); -- ID 10


-- ANSWERS --------------------------------
SET @User1UUID = '00000000-0000-0000-0000-000000000001';
SET @User2UUID = '00000000-0000-0000-0000-000000000002';
SET @User3UUID = '00000000-0000-0000-0000-000000000003';

INSERT INTO ANSWER(ANSWER_RATING, QUESTION_ID, USER_UUID) 
    VALUES  
    -- User1
        -- Survey 1
        (4, 1, @User1UUID), -- ID 1
        (4, 3, @User1UUID),
        -- Survey 2
        (2, 4, @User1UUID),
        (5, 5, @User1UUID),
        (4, 6, @User1UUID), -- ID 5
        (5, 8, @User1UUID),
        -- Survey 3
        (1, 9, @User1UUID),
        (4, 10, @User1UUID),
    -- User2
        -- Survey 1
        (4, 1, @User2UUID),
        (1, 3, @User2UUID), -- ID 10
        -- Survey 1
        (4, 4, @User2UUID),
        (3, 5, @User2UUID),
        (2, 6, @User2UUID),
        (5, 7, @User2UUID),
        (4, 8, @User2UUID), -- ID 15
        -- Survey 3
    --User3
        -- Survey 1
        (3, 3, @User3UUID),
        -- Survey 2
        (5, 4, @User3UUID),
        (1, 5, @User3UUID),
        (4, 6, @User3UUID),
        (2, 7, @User3UUID), -- ID 20
        (3, 8, @User3UUID),
        -- Survey 3
        (5, 9, @User3UUID),
        (5, 10, @User3UUID);
