package pl.insert.services;

import pl.insert.models.UserDetails;

public interface UserDetailsService {

    void save(UserDetails userDetails);

    UserDetails findById(long id);
}
