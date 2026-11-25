package org.example.backend.duck;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import static org.mockito.Mockito.mock;

class DuckServiceTest {
    private DuckService service;
    private DuckRepository duckRepository;

    @BeforeEach
    void setUp() throws Exception {
        duckRepository = mock(DuckRepository.class);
        service = new DuckService(duckRepository);
    }

    @Test
    void getDuck_returnsDuckFromRepository() {
        Duck expected = new Duck("Mallard", "duck.png", "A mallard", 7, 42);
        expected.setDuckId(12);
        when(duckRepository.findById(12)).thenReturn(java.util.Optional.of(expected));

        Duck actual = service.getDuck(12);

        assertEquals(expected, actual);
        verify(duckRepository).findById(12);
    }

    @Test
    void getDuck_throwsNotFoundWhenRepositoryHasNoDuck() {
        when(duckRepository.findById(12)).thenReturn(java.util.Optional.empty());

        EmptyResultDataAccessException error = assertThrows(
            EmptyResultDataAccessException.class,
            () -> service.getDuck(12)
        );

        assertEquals("Duck with id 12 does not exist", error.getMessage());
        verify(duckRepository).findById(12);
    }

    @Test
    void getAllDuckRanked_returnsDucksInRepositoryOrder() {
        List<Duck> expected = List.of(
            new Duck("Mallard", "mallard.png", "A mallard", 7, 42),
            new Duck("Pekin", "pekin.png", "A Pekin duck", 8, 35)
        );
        when(duckRepository.findAllByOrderByRatingScoreDesc()).thenReturn(expected);

        List<Duck> actual = service.getAllDuckRanked();

        assertEquals(expected, actual);
        verify(duckRepository).findAllByOrderByRatingScoreDesc();
    }

    @Test
    void createDuck_rejectsBlankNameWithoutCallingRepository() {
        PostDuckRequest request = new PostDuckRequest("  ", "duck.png", "A mallard", 1);

        IllegalArgumentException error = assertThrows(
            IllegalArgumentException.class,
            () -> service.createDuck(request)
        );

        assertEquals("Fields must not be blank", error.getMessage());
        verifyNoInteractions(duckRepository);
    }

    @Test
    void createDuck_rejectsBlankImageLinkWithoutCallingRepository() {
        PostDuckRequest request = new PostDuckRequest("Mallard", "\t", "A mallard", 1);

        IllegalArgumentException error = assertThrows(
            IllegalArgumentException.class,
            () -> service.createDuck(request)
        );

        assertEquals("Fields must not be blank", error.getMessage());
        verifyNoInteractions(duckRepository);
    }

    @Test
    void createDuck_rejectsBlankDescriptionWithoutCallingRepository() {
        PostDuckRequest request = new PostDuckRequest("Mallard", "duck.png", "", 1);

        IllegalArgumentException error = assertThrows(
            IllegalArgumentException.class,
            () -> service.createDuck(request)
        );

        assertEquals("Fields must not be blank", error.getMessage());
        verifyNoInteractions(duckRepository);
    }

    @Test
    void createDuck_savesDuckWithRequestValuesAndInitialRating() {
        PostDuckRequest request = new PostDuckRequest(
            "Mallard", "duck.png", "A friendly mallard", 1
        );
        Duck savedDuck = new Duck(
            "Mallard", "duck.png", "A friendly mallard", 1, 0
        );
        savedDuck.setDuckId(42);
        when(duckRepository.save(any(Duck.class))).thenReturn(savedDuck);

        service.createDuck(request);

        ArgumentCaptor<Duck> duckCaptor = ArgumentCaptor.forClass(Duck.class);
        verify(duckRepository).save(duckCaptor.capture());
        Duck duckToSave = duckCaptor.getValue();

        assertEquals("Mallard", duckToSave.getName());
        assertEquals("A friendly mallard", duckToSave.getDescription());
        assertEquals("duck.png", duckToSave.getImageLink());
        assertEquals(1, duckToSave.getCreatorId());
        assertEquals(0, duckToSave.getRatingScore());
    }

    @Test
    void createDuck_returnsResponseMappedFromSavedDuck() {
        PostDuckRequest request = new PostDuckRequest(
            "Mallard", "duck.png", "A friendly mallard", 1
        );
        Duck savedDuck = new Duck(
            "Mallard", "duck.png", "A friendly mallard", 1, 0
        );
        savedDuck.setDuckId(42);
        when(duckRepository.save(any(Duck.class))).thenReturn(savedDuck);

        PostDuckResponse actual = service.createDuck(request);

        PostDuckResponse expected = new PostDuckResponse(
            "Mallard", "duck.png", "A friendly mallard", 1, 42
        );
        assertEquals(expected, actual);
        verify(duckRepository).save(any(Duck.class));
    }

    @Test
    void createDuck_propagatesRepositoryFailure() {
        PostDuckRequest request = new PostDuckRequest(
            "Mallard", "duck.png", "A mallard", 1
        );
        DataAccessException repositoryError = new DataAccessException("save failed") {};
        when(duckRepository.save(any(Duck.class))).thenThrow(repositoryError);

        DataAccessException actual = assertThrows(
            DataAccessException.class,
            () -> service.createDuck(request)
        );

        assertEquals(repositoryError, actual);
        verify(duckRepository).save(any(Duck.class));
    }

}
