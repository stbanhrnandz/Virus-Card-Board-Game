package interfaces;

import enums.TreatmentType;
import java.util.List;
import model.Player;

public interface SpecialTreatment extends Colorable {
    TreatmentType getType();
    void apply(Player currentPlayer, List<Player> players);
}