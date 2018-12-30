package cz.uhk.fim.runhk.model;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.List;

/**
 * Created by EvaKozakova on 02.04.2018.
 */
@IgnoreExtraProperties
public class Player {

    private String nickname;
    private String email;
    private String password;

    private int level;
    private int exps;

    private int age;
    private int weight;

    private List<Challenge> challengeList;
    private Challenge challengeToDo;

    private boolean isMale;

    public Player(String nickname, String email, String password, int level, int exps, List<Challenge> challengeList) {
        this.nickname = nickname;
        this.email = email;
        this.password = password;
        this.level = level;
        this.exps = exps;
        this.challengeList = challengeList;
    }

    public Player() {
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

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getExps() {
        return exps;
    }

    public void setExps(int exps) {
        this.exps = exps;
    }

    public List<Challenge> getChallengeList() {
        return challengeList;
    }

    public void setChallengeList(List<Challenge> challengeList) {
        this.challengeList = challengeList;
    }

    public Challenge getChallengeToDo() {
        return challengeToDo;
    }

    public void setChallengeToDo(Challenge challengeToDo) {
        this.challengeToDo = challengeToDo;
    }

    public boolean isMale() {
        return isMale;
    }

    public void setMale(boolean male) {
        isMale = male;
    }
}
