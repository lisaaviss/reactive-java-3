package lab;

import java.time.LocalDateTime;
import java.util.List;

public class Commit {
    private int id;
    private String message;
    private LocalDateTime creationTime;
    private CommitStatus status;
    private Author author;
    private List<String> changedFiles;

    private Branch branch;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getCreationTime() {

        return creationTime;
    }

    public void setCreationTime(LocalDateTime creationTime) {
        this.creationTime = creationTime;
    }

    public CommitStatus getStatus() {
        return status;
    }

    public void setStatus(CommitStatus status) {
        this.status = status;
    }

    public Author getAuthor() {

        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public List<String> getChangedFiles(long delay) {
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return changedFiles;
    }

    public void setChangedFiles(List<String> changedFiles) {
        this.changedFiles = changedFiles;
    }

    public Branch getBranch() {
        return branch;
    }

    public void setBranch(Branch branch) {
        this.branch = branch;
    }

    record Author(String name, String email){

    }
}
