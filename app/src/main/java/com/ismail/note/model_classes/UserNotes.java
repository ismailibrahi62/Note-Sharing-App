package com.ismail.note.model_classes;

public class UserNotes {
    private String noteId;
    private String title;
    private String content;
    private String timestamp;

    public UserNotes() {
        // Default constructor required for Firebase
    }

    public UserNotes(String noteId, String title, String content, String timestamp) {
        this.noteId = noteId;
        this.title = title;
        this.content = content;
        this.timestamp = timestamp;
    }

    public String getNoteId() {
        return noteId;
    }

    public void setNoteId(String noteId) {
        this.noteId = noteId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
