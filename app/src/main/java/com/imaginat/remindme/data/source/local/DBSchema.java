package com.imaginat.remindme.data.source.local;

/**
 * Created by nat on 8/8/16.
 */
public class DBSchema {
    //=========================================================================
    public static final class lists_table{
        public static final String NAME = "listOfLists";
        public static final String[] ALL_COLUMNS = new String[]{cols.LIST_ID,cols.LIST_TITLE,cols.LIST_ICON};//
        public static final String createCommand =
                "CREATE TABLE "+NAME+"("+
                        cols.LIST_ID+" INTEGER PRIMARY KEY, "+
                        cols.LIST_TITLE+" TEXT, "+
                        cols.LIST_ICON+" INTEGER DEFAULT 0 "+
                        ")";
        public static final class cols{
            public static final String LIST_ID="list_id";
            public static final String LIST_TITLE="list_title";
            public static final String LIST_ICON="list_icon";
        }
    }
    //=========================================================================
    public static final class reminders_table{
        public static final String NAME = "reminders";
        public static final String[] ALL_COLUMNS = new String[]{cols.REMINDER_ID,cols.LIST_ID,cols.REMINDER_TEXT,cols.IS_COMPLETED,cols.CALENDAR_EVENT_ID};
        public static final String createCommand=
                "CREATE TABLE "+NAME+"("+
                        cols.REMINDER_ID+" INTEGER PRIMARY KEY, "+
                        cols.LIST_ID+" INTEGER NOT NULL, "+
                        cols.REMINDER_TEXT+" TEXT NOT NULL, "+
                        cols.IS_COMPLETED+" INTEGER DEFAULT 0, "+
                        cols.CALENDAR_EVENT_ID+" INTEGER DEFAULT -1, "+
                        "FOREIGN KEY("+cols.LIST_ID+") REFERENCES "+lists_table.NAME+"("+lists_table.cols.LIST_ID+")"+
                        ")";

        public static final class cols{
            public static final String REMINDER_ID="reminder_id";
            public static final String LIST_ID="list_id";
            public static final String REMINDER_TEXT="reminderText";
            public static final String IS_COMPLETED="isCompleted";
            public static final String CALENDAR_EVENT_ID="calendarEventID";
        }
    }
}
