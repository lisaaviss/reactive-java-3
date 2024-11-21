package lab;

import net.datafaker.Faker;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class Generator {
    static Faker faker = new Faker(new Locale("en"));
    static Random random = new Random();
    public static Commit generateCommit(int id, Commit.Author author, Project project){
        Commit commit = new Commit();

        String[] operationVerb = new String[]{ "Fix ", "Add ", "Remove ", "Update "};
        int randomInt = random.nextInt(4);
        String message = operationVerb[randomInt] + faker.hacker().adjective() +
                " " + faker.hacker().noun() + " in " + faker.hacker().noun(); // Генерация message commit

        Instant startDateTime = LocalDateTime.of(2022, 1, 1, 0,0,0)
                .atZone(ZoneId.systemDefault()).toInstant();
        Instant endDateTime = Instant.now();

        LocalDateTime creationTime = LocalDateTime.ofInstant(faker.timeAndDate().between(startDateTime, endDateTime),
                ZoneId.systemDefault()); // Генерация времени создания commit

        CommitStatus[] statuses = CommitStatus.values();
        randomInt = random.nextInt(3);
        CommitStatus status = statuses[randomInt]; // Генерация статуса commit




        List<String> changedFiles = new ArrayList<>(); // Генерация списка измененных файлов
        randomInt = random.nextInt(1, 6);
        for(int i=0; i<randomInt; i++){
            changedFiles.add(faker.file().fileName());
        }


        ArrayList<Branch> branchesList = project.getBranchList();
        randomInt = random.nextInt(branchesList.size());
        Branch branch = project.getBranchList().get(randomInt);


        commit.setId(id);
        commit.setMessage(message);
        commit.setCreationTime(creationTime);
        commit.setStatus(status);
        commit.setAuthor(author);
        commit.setChangedFiles(changedFiles);
        commit.setBranch(branch);

        return commit;
    }
    public static Project generateProject(){
        Project project = new Project();
        String projectName = faker.commerce().productName(); // Имя проекта
        String projectDescription = faker.company().bs(); // Описание проекта
        project.setName(projectName);
        project.setDescription(projectDescription);
        ArrayList<Branch> branches = new ArrayList<>();
        int randomInt = random.nextInt(1, 11);
        for (int i = 0; i<randomInt; i++){
            Branch branch = generateBranch();
            branches.add(branch);
        }
        project.setBranchList(branches);
        return project;
    }

    public static Branch generateBranch(){
        Branch branch = new Branch();
        String branchName = faker.commerce().productName(); // Имя ветки
        boolean isProtected = random.nextBoolean();
        branch.setName(branchName);
        branch.setProtected(isProtected);
        return branch;
    }

    public static ArrayList<Project> generateProjects(){
        ArrayList<Project> projects = new ArrayList<>();
        int randomInt = random.nextInt(20, 101);
        for (int i = 0; i<randomInt; i++){
            Project project = generateProject();
            projects.add(project);
        }
        return projects;
    }

    public static ArrayList<Commit.Author> generateAuthors(){
        ArrayList<Commit.Author> authors = new ArrayList<>();
        int randomInt = random.nextInt(10, 51);
        for(int i = 0; i< randomInt; i++){
            String name = faker.name().fullName(); // Генерация имени для Author commit
            String email = faker.internet().emailAddress(); // Генерация почты для Author commit
            Commit.Author author = new Commit.Author(name, email);
            authors.add(author);
        }
        return authors;
    }
}
