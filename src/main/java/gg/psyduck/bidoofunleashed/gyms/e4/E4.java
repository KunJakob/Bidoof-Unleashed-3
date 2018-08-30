package gg.psyduck.bidoofunleashed.gyms.e4;

import gg.psyduck.bidoofunleashed.gyms.Gym;
import lombok.Getter;

@Getter
public class E4 extends Gym {

    private Type type;

    public E4(Gym.Builder builder, Type type) {
        super(builder);
        this.type = type;
    }

    public enum Type {
        E4, CHAMPION
    }

}
