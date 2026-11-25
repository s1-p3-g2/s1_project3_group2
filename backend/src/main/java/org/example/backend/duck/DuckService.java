package org.example.backend.duck;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DuckService {
    private final DuckRepository duckRepository;

    /**
   * Duck service constructor, auto called by Spring Boot on initialization.
   *
   * @param duckRepository The database access object that the service uses to interface with the
   *                         database ducks table
   */
    public DuckService(
        DuckRepository duckRepository) {
        this.duckRepository = duckRepository;
    }

    @Transactional 
    public PostDuckResponse createDuck(PostDuckRequest pdr){
        //Do the jbdc template stuff here then return a PostDuckResponse object or an error
        //Check if all the fields fit
        //name, image_link, description
        if(pdr.name().isBlank() || pdr.image_link().isBlank() || pdr.description().isBlank()){
            throw new IllegalArgumentException("Fields must not be blank");
        }

        //replace with repository
        Duck response = duckRepository.save(new Duck(pdr.name(), pdr.image_link(),  pdr.description(), pdr.creator_id(),0));


        return new PostDuckResponse(response.getName(), response.getImageLink(), response.getDescription(), response.getCreatorId(), response.getDuckId());
    }

    @Transactional(readOnly = true)
    public Duck getDuck(Integer duckId) {
        return duckRepository.findById(duckId)
            .orElseThrow(() ->
                new EmptyResultDataAccessException(
                    "Duck with id " + duckId + " does not exist", 1)
            );
    }

    @Transactional(readOnly = true)
    public List<Duck> getAllDuckRanked(){
        return duckRepository.findAllByOrderByRatingScoreDesc();
    }
}
