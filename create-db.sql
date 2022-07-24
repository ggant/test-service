CREATE TABLE [TEST_USER](
    [userId] [varchar](8) not null constraint TEST_USER_ID_PK primary key,
    [timezone] [varchar](50) NULL,
    [cc] [varchar](2) NULL,
    [counter] [int] NULL
    );

insert into [dbo].[TEST_USER] (userId, timezone, cc, counter) values ('123', 'Europe/Ljubljana', 'SI', 1)
