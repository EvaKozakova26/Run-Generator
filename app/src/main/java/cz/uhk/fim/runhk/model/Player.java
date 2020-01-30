package cz.uhk.fim.runhk.model;

import java.util.List;

/**
 * Created by EvaKozakova on 02.04.2018.
 */
public class Player {


    private String nickname;
    private String email;
    private String password;
    private int level;
    private int exps;
    private int age;
    private int weight;
    private List<Run> runList;
    private Run runToDo;

    private boolean isMale;

    public Player(String nickname, String email, String password, int level, int exps, List<Run> runList) {
        this.nickname = nickname;
        this.email = email;
        this.password = password;
        this.level = level;
        this.exps = exps;
        this.runList = runList;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public String getNickname() {
        return nickname;
    }

    public int getLevel() {
        return level;
    }

    public int getExps() {
        return exps;
    }

    public void setRunToDo(Run runToDo) {
        this.runToDo = runToDo;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void setExps(int exps) {
        this.exps = exps;
    }

    public List<Run> getRunList() {
        return runList;
    }

    public void setRunList(List<Run> runList) {
        this.runList = runList;
    }

    public Run getRunToDo() {
        return runToDo;
    }

    public boolean isMale() {
        return isMale;
    }

    public void setMale(boolean male) {
        isMale = male;
    }
}

