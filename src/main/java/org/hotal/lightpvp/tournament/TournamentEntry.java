package org.hotal.lightpvp.tournament;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@AllArgsConstructor
@Getter
public class TournamentEntry {

    private UUID uuid;
    private String name;

}
