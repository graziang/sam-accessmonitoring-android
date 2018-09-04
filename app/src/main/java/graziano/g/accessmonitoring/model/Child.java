package graziano.g.accessmonitoring.model;

import java.util.HashSet;
import java.util.Set;


public class Child {

    private int id;
    private int index;
    private String name;
    private String description;
    private boolean active;
    private String password;
    private String familyName;
    private Family family;

    private Set<Session> sessions = new HashSet<>();

    public Child(){
        this.name = "";
        this.familyName = "";
        this.description = "";
        this.password = "";
        this.familyName = "";
        this.active = false;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

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

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public Family getFamily() {
        return family;
    }

    public void setFamily(Family family) {
        this.family = family;
    }

    public Set<Session> getSessions() {
        return sessions;
    }

    public void setSessions(Set<Session> sessions) {
        this.sessions = sessions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Child child = (Child) o;

        if (name != null ? !name.equals(child.name) : child.name != null) return false;
        if (password != null ? !password.equals(child.password) : child.password != null)
            return false;
        return familyName != null ? familyName.equals(child.familyName) : child.familyName == null;
    }

}
