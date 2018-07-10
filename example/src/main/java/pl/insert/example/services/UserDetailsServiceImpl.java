package pl.insert.example.services;

import pl.insert.example.models.UserDetails;
import pl.insert.example.repositories.UserDetailsRepository;
import pl.insert.framework.annotations.Inject;
import pl.insert.framework.annotations.components.Service;
import pl.insert.framework.annotations.transactional.Transactional;
import pl.insert.framework.transactional.TransactionalPropagation;


@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Inject
    private UserDetailsRepository userDetailsRepository;

    @Inject
    private InnerService innerService;

    @Transactional(propagation = TransactionalPropagation.REQUIRED)
    public void save(UserDetails userDetails) {
        userDetailsRepository.save(userDetails);
        UserDetails newUserDetails = new UserDetails("bug");
        try {
            innerService.testPropagation(newUserDetails);
        } catch (RuntimeException e) {
            e.printStackTrace();
        }

        newUserDetails = new UserDetails("afterException");
        userDetailsRepository.save(newUserDetails);
    }

    @Transactional(propagation = TransactionalPropagation.REQUIRED)
    public UserDetails findById(long id) {
        return userDetailsRepository.findById(id);
    }
}
