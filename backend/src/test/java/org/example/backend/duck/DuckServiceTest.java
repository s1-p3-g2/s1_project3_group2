package org.example.backend.duck;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.KeyHolder;

class DuckServiceTest {
    private JdbcTemplate jdbcTemplate;
    private DuckService service;

    @BeforeEach
    void setUp() throws Exception {
        jdbcTemplate = mock(JdbcTemplate.class);
        service = new DuckService();

        var field = DuckService.class.getDeclaredField("jdbcTemplate");
        field.setAccessible(true);
        field.set(service, jdbcTemplate);
    }

    @Test
    void createDuck_rejectsBlankName() {
        PostDuckRequest request = new PostDuckRequest("  ", "duck.png", "A mallard", 1);

        IllegalArgumentException error = assertThrows(
            IllegalArgumentException.class,
            () -> service.createDuck(request)
        );

        assertEquals("Fields must not be blank", error.getMessage());
        verifyNoInteractions(jdbcTemplate);
    }

    @Test
    void createDuck_rejectsBlankImageLink() {
        PostDuckRequest request = new PostDuckRequest("Mallard", "\t", "A mallard", 1);

        IllegalArgumentException error = assertThrows(
            IllegalArgumentException.class,
            () -> service.createDuck(request)
        );

        assertEquals("Fields must not be blank", error.getMessage());
        verifyNoInteractions(jdbcTemplate);
    }

    @Test
    void createDuck_rejectsBlankDescription() {
        PostDuckRequest request = new PostDuckRequest("Mallard", "duck.png", "",1);

        IllegalArgumentException error = assertThrows(
            IllegalArgumentException.class,
            () -> service.createDuck(request)
        );

        assertEquals("Fields must not be blank", error.getMessage());
        verifyNoInteractions(jdbcTemplate);
    }

    @Test
    void createDuck_returnsDuckAfterInsertAndReadBack() throws Exception {
        PostDuckRequest request = new PostDuckRequest("Mallard", "duck.png", "A friendly mallard",1);
        PostDuckResponse expected = new PostDuckResponse(
            "Mallard", "A friendly mallard", "duck.png", 7, 42
        );

        doAnswer(invocation -> {
            KeyHolder keyHolder = invocation.getArgument(1);
            keyHolder.getKeyList().add(Map.of("duck_id", 42));
            return 1;
        }).when(jdbcTemplate).update(any(PreparedStatementCreator.class), any(KeyHolder.class));
        when(jdbcTemplate.queryForObject(
            anyString(),
            org.mockito.ArgumentMatchers.<RowMapper<PostDuckResponse>>any(),
            eq(42)
        )).thenReturn(expected);

        PostDuckResponse actual = service.createDuck(request);

        assertEquals(expected, actual);
        verify(jdbcTemplate).queryForObject(
            eq("SELECT name, image_link, description, creator_id, duck_id " +
                "FROM ducks WHERE duck_id = ?"),
            org.mockito.ArgumentMatchers.<RowMapper<PostDuckResponse>>any(),
            eq(42)
        );
    }

    @Test
    void createDuck_bindsInsertValuesAndInitialRating() throws Exception {
        PostDuckRequest request = new PostDuckRequest("Mallard", "duck.png", "A mallard",1);
        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        when(connection.prepareStatement(anyString(), eq(java.sql.Statement.RETURN_GENERATED_KEYS)))
            .thenReturn(statement);

        doAnswer(invocation -> {
            KeyHolder keyHolder = invocation.getArgument(1);
            keyHolder.getKeyList().add(Map.of("duck_id", 42));
            return 1;
        }).when(jdbcTemplate).update(any(PreparedStatementCreator.class), any(KeyHolder.class));
        when(jdbcTemplate.queryForObject(anyString(),
            org.mockito.ArgumentMatchers.<RowMapper<PostDuckResponse>>any(), eq(42)))
            .thenReturn(new PostDuckResponse("Mallard", "A mallard", "duck.png", 7, 42));

        service.createDuck(request);

        var creator = org.mockito.ArgumentCaptor.forClass(PreparedStatementCreator.class);
        verify(jdbcTemplate).update(creator.capture(), any(KeyHolder.class));
        creator.getValue().createPreparedStatement(connection);

        verify(connection).prepareStatement(
            "INSERT INTO ducks (name, description, image_link, creator_id, rating_score) VALUES (?, ?, ?, ?, ?)",
            java.sql.Statement.RETURN_GENERATED_KEYS
        );
        verify(statement).setString(1, "Mallard");
        verify(statement).setString(2, "A mallard");
        verify(statement).setString(3, "duck.png");
        verify(statement).setInt(4, 1);
        verify(statement).setInt(5, 0);
    }

    @Test
    void createDuck_throwsWhenGeneratedKeyIsMissing() {
        PostDuckRequest request = new PostDuckRequest("Mallard", "duck.png", "A mallard",1);
        doAnswer(invocation -> 1)
            .when(jdbcTemplate).update(any(PreparedStatementCreator.class), any(KeyHolder.class));

        IllegalStateException error = assertThrows(
            IllegalStateException.class,
            () -> service.createDuck(request)
        );

        assertEquals("Could not retrieve generated duck_id", error.getMessage());
    }

    @Test
    void createDuck_propagatesDatabaseFailureDuringInsert() {
        PostDuckRequest request = new PostDuckRequest("Mallard", "duck.png", "A mallard",1);
        DataAccessException databaseError = new DataAccessException("insert failed") {};
        doAnswer(invocation -> { throw databaseError; })
            .when(jdbcTemplate).update(any(PreparedStatementCreator.class), any(KeyHolder.class));

        DataAccessException actual = assertThrows(
            DataAccessException.class,
            () -> service.createDuck(request)
        );

        assertEquals(databaseError, actual);
    }

    @Test
    void createDuck_propagatesReadBackFailure() {
        PostDuckRequest request = new PostDuckRequest("Mallard", "duck.png", "A mallard",1);
        doAnswer(invocation -> {
            KeyHolder keyHolder = invocation.getArgument(1);
            keyHolder.getKeyList().add(Map.of("duck_id", 42));
            return 1;
        }).when(jdbcTemplate).update(any(PreparedStatementCreator.class), any(KeyHolder.class));
        DataAccessException databaseError = new DataAccessException("select failed") {};
        when(jdbcTemplate.queryForObject(anyString(),
            org.mockito.ArgumentMatchers.<RowMapper<PostDuckResponse>>any(), eq(42)))
            .thenThrow(databaseError);

        assertEquals(databaseError, assertThrows(
            DataAccessException.class,
            () -> service.createDuck(request)
        ));
    }
}
