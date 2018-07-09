package pl.insert.example.repositories;

import pl.insert.example.models.UserDetails;

public interface UserDetailsRepository {
    void save(UserDetails userDetails);

    UserDetails findById(long id);

}
