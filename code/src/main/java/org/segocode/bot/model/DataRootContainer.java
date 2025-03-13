package org.segocode.bot.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DataRootContainer {
    private List<User> users = new ArrayList<>();

    /**
     * Finds a user by their ID
     *
     * @param id The ID of the user to find
     * @return An Optional containing the user if found, empty otherwise
     */
    public Optional<User> findUserById(String id) {
        return users.stream()
                .filter(user -> id.equals(user.getId()))
                .findFirst();
    }
}
