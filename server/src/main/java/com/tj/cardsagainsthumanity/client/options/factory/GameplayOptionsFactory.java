package com.tj.cardsagainsthumanity.client.options.factory;

import com.tj.cardsagainsthumanity.client.model.GameState;
import com.tj.cardsagainsthumanity.client.options.OptionSet;
import com.tj.cardsagainsthumanity.client.options.sets.EmptyOptionSet;
import com.tj.cardsagainsthumanity.client.options.sets.gameplay.CzarOptionSet;
import com.tj.cardsagainsthumanity.client.options.sets.gameplay.PlayerOptionSet;
import com.tj.cardsagainsthumanity.models.gameplay.Game;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GameplayOptionsFactory {
    private final CzarOptionSet czarOptionSet;
    private final PlayerOptionSet playerOptionSet;

    @Autowired
    GameplayOptionsFactory(CzarOptionSet czarOptionSet, PlayerOptionSet playerOptionSet) {
        this.czarOptionSet = czarOptionSet;
        this.playerOptionSet = playerOptionSet;
    }

    public OptionSet getOptionsFromGameState(GameState state) {
        return state.getState().filter(gameState -> gameState == Game.GameState.STARTED)
                .map(gameState -> getOptionsForStartedGame(state))
                .orElseGet(() -> getOptionsForUnstartedGame(state));
    }

    private OptionSet getOptionsForStartedGame(GameState state) {
        return state.getCurrentRound().filter(round -> round.isCzarIsYou())
                .map(round -> getOptionSetForCzar(state))
                .orElseGet(() -> getOptionSetForPlayer(state));
    }

    private OptionSet getOptionSetForPlayer(GameState state) {
        return playerOptionSet;
    }

    private OptionSet getOptionSetForCzar(GameState state) {
        return czarOptionSet;
    }

    private OptionSet getOptionsForUnstartedGame(GameState state) {
        return new EmptyOptionSet("Waiting for game setup to finish on server.");
    }
}
