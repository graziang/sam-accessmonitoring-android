package graziano.g.accessmonitoring.model;

import java.util.HashSet;
import java.util.Set;

public class Family {

    private Long id;
    private String name;
    private String description;
    private String password;
    private String childrenPassword;
    private boolean active;
    private Set<Child> children = new HashSet<>();

    public Family() {
        this.name = "";
        this.description = "";
        this.password = "";
        this.childrenPassword = "";
        this.active = false;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getChildrenPassword() {
        return childrenPassword;
    }

    public void setChildrenPassword(String childrenPassword) {
        this.childrenPassword = childrenPassword;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Set<Child> getChildren() {
        return children;
    }

    public void setChildren(Set<Child> children) {
        this.children = children;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Family family = (Family) o;

        if (id != null ? !id.equals(family.id) : family.id != null) return false;
        if (name != null ? !name.equals(family.name) : family.name != null) return false;
        if (description != null ? !description.equals(family.description) : family.description != null)
            return false;
        if (password != null ? !password.equals(family.password) : family.password != null)
            return false;
        if (childrenPassword != null ? !childrenPassword.equals(family.childrenPassword) : family.childrenPassword != null)
            return false;
        return true;
    }
}