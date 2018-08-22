package gg.psyduck.bidoofunleashed.gyms;

import com.pixelmonmod.pixelmon.entities.pixelmon.EntityPixelmon;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;

@RequiredArgsConstructor
@Getter
public class TeamStorage {

    private String gymName;

    @Setter
    private List<EntityPixelmon> team;

}
