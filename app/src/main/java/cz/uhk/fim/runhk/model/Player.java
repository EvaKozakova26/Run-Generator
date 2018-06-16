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

    private List<Quest> questList;
    private Quest questToDo;

    public Player(String nickname, String email, String password, int level, int exps, List<Quest> questList) {
        this.nickname = nickname;
        this.email = email;
        this.password = password;
        this.level = level;
        this.exps = exps;
        this.questList = questList;
    }

    public Player() {
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

    public List<Quest> getQuestList() {
        return questList;
    }

    public void setQuestList(List<Quest> questList) {
        this.questList = questList;
    }

    public Quest getQuestToDo() {
        return questToDo;
    }

    public void setQuestToDo(Quest questToDo) {
        this.questToDo = questToDo;
    }
}
