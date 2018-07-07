package pl.insert.repositories;

import pl.insert.models.UserDetails;

public interface UserDetailsRepository {
    void save(UserDetails userDetails);

    UserDetails findById(long id);

}
