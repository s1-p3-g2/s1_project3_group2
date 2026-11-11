package org.example.backend.service;

import java.sql.PreparedStatement;
import java.sql.Statement;

import org.example.backend.dto.PostDuckRequest;
import org.example.backend.dto.PostDuckResponse;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import jakarta.transaction.Transactional;

public class DuckService {
    private  JdbcTemplate jdbcTemplate;
    @Transactional 
    public PostDuckResponse createDuck(PostDuckRequest pdr){
        //Do the jbdc template stuff here then return a PostDuckResponse object or an error
        //Check if all the fields fit
        //name, image_link, description
        if(pdr.name().isBlank() || pdr.image_link().isBlank() || pdr.description().isBlank()){
            throw new IllegalArgumentException("Fields must not be blank");
        }

        //Can't check the creator_id yet since user stuff is someone else's slice


        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO ducks (name, description, image_link, score) VALUES (?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS
            );
            statement.setString(1, pdr.name());
            statement.setString(2, pdr.description());
            statement.setString(3, pdr.image_link());
            statement.setInt(4, 0);
            return statement;
        }, keyHolder);

        Number generatedId = keyHolder.getKey();
        if (generatedId == null) {
            throw new IllegalStateException("Could not retrieve generated duck_id");
        }

        return jdbcTemplate.queryForObject(
            "SELECT name, duck_id, description, image_link, score " +
                "FROM ducks WHERE duck_id = ?",
            (rs, rowNum) -> new PostDuckResponse(
                rs.getString("name"),
                rs.getString("description"),
                rs.getString("image_link"),
                rs.getInt("creator_id"),
                rs.getInt("duck_id")
            ),
            generatedId.intValue()
        );
    }
}
