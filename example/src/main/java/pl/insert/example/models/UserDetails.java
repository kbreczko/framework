package pl.insert.example.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class UserDetails {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    public UserDetails(String name) {
        this.name = name;
    }

    public UserDetails() {
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
}
