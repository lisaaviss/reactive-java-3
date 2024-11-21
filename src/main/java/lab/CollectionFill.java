package lab;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CollectionFill {
    static Random random = new Random();
    static ArrayList<Commit.Author> authors = Generator.generateAuthors();

    static ArrayList<Project> projects = Generator.generateProjects();
    public static List<Commit> collectionFill(int elementsNumber){
        List<Commit> commitList = new ArrayList<>();
        int i = 0;
        while (i < elementsNumber){
            Commit.Author author = getRandomAuthor();
            Project project = getRandomProject();
            Commit commit = Generator.generateCommit(i, author, project);
            commitList.add(commit);
            i++;
        }
        return commitList;
    }

    public static Commit.Author getRandomAuthor(){
        int authorsNum = authors.size();
        int randomInt = random.nextInt(authorsNum);
        return authors.get(randomInt);
    }
    public static Project getRandomProject(){
        int projectSize = projects.size();
        int randomInt = random.nextInt(projectSize);
        return projects.get(randomInt);
    }
}
