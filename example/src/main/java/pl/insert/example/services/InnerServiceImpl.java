package pl.insert.example.services;

import pl.insert.example.models.UserDetails;
import pl.insert.example.repositories.UserDetailsRepository;
import pl.insert.framework.di.annotations.Inject;
import pl.insert.framework.di.annotations.stereotypes.Service;
import pl.insert.framework.transactional.annotations.Transactional;
import pl.insert.framework.transactional.enums.Propagation;

@Service
public class InnerServiceImpl implements InnerService {

    @Inject
    private UserDetailsRepository userDetailsRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void testPropagation(UserDetails userDetails) {
        userDetailsRepository.save(userDetails);
        throw new RuntimeException("Rollback this transaction!");
    }
}
