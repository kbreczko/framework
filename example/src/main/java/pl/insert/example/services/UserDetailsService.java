package pl.insert.example.services;

import pl.insert.example.models.UserDetails;

public interface UserDetailsService {

    void save(UserDetails userDetails);

    UserDetails findById(long id);
}
