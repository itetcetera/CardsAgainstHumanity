package com.tj.cardsagainsthumanity.core.game.events.handler;

import com.tj.cardsagainsthumanity.core.game.events.types.GameEvent;

public interface GameStartedEventHandler {
    void onGameStarted(GameEvent game);
}
