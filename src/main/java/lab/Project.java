package lab;

import java.util.ArrayList;

public class Project {
    private String name;
    private String description;

    private ArrayList<Branch> branchList;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<Branch> getBranchList() {
        return branchList;
    }

    public void setBranchList(ArrayList<Branch> branchList) {
        this.branchList = branchList;
    }
}
