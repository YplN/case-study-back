
INSERT INTO SURVEY (SURVEY_TITLE, SURVEY_DESC)
VALUES 
    ('Teamwork Satisfaction Survey', 'This survey aims to assess team members'' satisfaction levels regarding teamwork dynamics within the organization.'),
    ('Team Dynamics Assessment', 'The Team Dynamics Assessment survey evaluates the overall functioning and effectiveness of the team. It examines aspects such as cohesion, goal alignment, decision-making processes, adaptability to change, and accountability.'),
    ('Team Performance Evaluation', 'The Team Performance Evaluation survey gauges the performance and productivity levels of the team. It assesses factors such as productivity, work quality, meeting deadlines, and innovation.');


INSERT INTO QUESTION(QUESTION_TEXT, SURVEY_ID) 
VALUES 
    ('How satisfied are you with the level of collaboration within your team?', 1),
    ('How effectively do team members communicate with each other?', 1),
    ('Rate the level of trust among team members.', 1),
    ('How well does the team handle conflicts and disagreements?', 1),
    ('To what extent do team members support each other''s ideas and initiatives?', 1),
    ('Rate the overall cohesion of your team.', 2),
    ('How well does the team understand and align with its goals and objectives?', 2),
    ('Rate the effectiveness of team meetings in addressing issues and making decisions.', 2),
    ('How well does the team adapt to changes and challenges?', 2),
    ('Rate the level of accountability among team members.', 2),
    ('How would you rate the productivity of your team?', 3),
    ('Rate the quality of work produced by the team.', 3),
    ('How well does the team meet deadlines and deliverables?', 3),
    ('Rate the level of innovation and creativity within the team.', 3),
    ('How satisfied are you with the overall performance of your team?', 3);


SET @User1UUID = RANDOM_UUID();
SET @User2UUID = RANDOM_UUID();
SET @User3UUID = RANDOM_UUID();


INSERT INTO ANSWER(ANSWER_RATING, QUESTION_ID, USER_UUID) 
    VALUES 
    -- User1
        (4, 1, @User1UUID),
        (4, 3, @User1UUID),
        (2, 4, @User1UUID),
        (5, 5, @User1UUID),
        (4, 6, @User1UUID),
        (3, 7, @User1UUID),
        (5, 8, @User1UUID),
        (1, 9, @User1UUID),
        (4, 10, @User1UUID),
        (2, 11, @User1UUID),
        (5, 12, @User1UUID),
        (3, 13, @User1UUID),
        (4, 14, @User1UUID),
    -- User2
        (4, 1, @User2UUID),
        (1, 3, @User2UUID),
        (4, 4, @User2UUID),
        (3, 5, @User2UUID),
        (2, 6, @User2UUID),
        (5, 7, @User2UUID),
        (4, 8, @User2UUID),
        (2, 9, @User2UUID),
        (3, 10, @User2UUID),
        (5, 11, @User2UUID),
        (1, 12, @User2UUID),
        (4, 13, @User2UUID),
        (3, 14, @User2UUID),
    --User3
        (3, 3, @User3UUID),
        (5, 4, @User3UUID),
        (1, 5, @User3UUID),
        (4, 6, @User3UUID),
        (2, 7, @User3UUID),
        (3, 8, @User3UUID),
        (5, 9, @User3UUID),
        (4, 10, @User3UUID),
        (1, 11, @User3UUID),
        (3, 12, @User3UUID),
        (5, 13, @User3UUID),
        (2, 14, @User3UUID);

