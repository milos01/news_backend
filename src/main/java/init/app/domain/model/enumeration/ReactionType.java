package init.app.domain.model.enumeration;

import java.util.stream.Stream;

public enum ReactionType {
    HEART, ANGRY, SAD, INTERESTING, LOL;

    public static Stream<ReactionType> stream() {
        return Stream.of(ReactionType.values());
    }
}
