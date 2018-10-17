package pl.insert.example.services;

import pl.insert.example.models.UserDetails;
import pl.insert.example.repositories.UserDetailsRepository;
import pl.insert.framework.di.annotations.Inject;
import pl.insert.framework.di.annotations.stereotypes.Service;
import pl.insert.framework.transactional.annotations.Transactional;


@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Inject
    private UserDetailsRepository userDetailsRepository;

    @Inject
    private InnerService innerService;

    @Transactional
    public void save(UserDetails userDetails) {
        userDetailsRepository.save(userDetails);

        testPropagation(new UserDetails("bug"));
        userDetailsRepository.save(new UserDetails("afterException"));
        //throw new RuntimeException("Rollback this transaction!");
    }

    private void testPropagation(UserDetails userDetails) {
        try {
            innerService.testPropagation(userDetails);
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }

    @Transactional
    public UserDetails findById(long id) {
        return userDetailsRepository.findById(id);
    }
}
