package com.click.myapplication.database;


public class Schema {
    public static final class Chat{
        public static final String NAME="Chat";

        public static final class cols{
            public static final String QUESTION="question";
            public static final String HAPPYANS="happyans";
            public static final String SADANS="sadans";
            public static final String ANGRYANS="angryans";
        }
    }

    public static final class ChatLearn{
        public static final String NAME="ChatLearn";

        public static final class cols{
            public static final String QUESTION="question";
            public static final String HAPPYANS="happyans";
            public static final String SADANS="sadans";
            public static final String ANGRYANS="angryans";
        }
    }

    public static final class QA{
        public static final String NAME="QA";

        public static final class cols{
            public static final String QUESTION="question";
            public static final String SOLUTION="solution";
        }
    }

    public static final class UnansweredQuestions{
        public static final String NAME="UnansweredQuestions";

        public static final class cols{
            public static final String QUESTION="question";
        }
    }
}
