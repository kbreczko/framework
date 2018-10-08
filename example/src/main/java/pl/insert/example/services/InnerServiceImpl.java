package pl.insert.example.services;

import pl.insert.example.models.UserDetails;
import pl.insert.example.repositories.UserDetailsRepository;
import pl.insert.framework.annotations.Inject;
import pl.insert.framework.annotations.components.Service;
import pl.insert.framework.annotations.transactional.Transactional;
import pl.insert.framework.transactional.enums.Propagation;

@Service
public class InnerServiceImpl implements InnerService {

    @Inject
    private UserDetailsRepository userDetailsRepository;

    @Transactional(propagation = Propagation.REQUIRED.REQUIRES_NEW)
    @Override
    public void testPropagation(UserDetails userDetails) {
        userDetailsRepository.save(userDetails);
        throw new RuntimeException("Rollback this transaction!");
    }
}
