package com.diogenes.wolfpack.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.ScreenUtils;
import com.diogenes.wolfpack.WolfPack;
import com.diogenes.wolfpack.assets.AssetLoader;
import com.diogenes.wolfpack.battle.BattleState;
import com.diogenes.wolfpack.campaign.Campaign;
import com.diogenes.wolfpack.campaign.CampActionType;
import com.diogenes.wolfpack.campaign.CampManager;
import com.diogenes.wolfpack.entities.Enemy;
import com.diogenes.wolfpack.entities.Wolf;

import java.util.ArrayList;
import java.util.List;

public class CampScreen implements Screen {

    final WolfPack game;
    private final Campaign campaign;
    private final CampManager campManager;
    private final List<Wolf> wolves;
    private final AssetLoader assets;

    private BattleState currentState;

    private List<CampActionType> availableCampActions;
    private int currentCampActionIndex;
    private CampActionType selectedCampAction;
    private int currentCampTargetIndex;
    private String campResultMessage;
    private final List<Enemy> battleEnemies;

    public CampScreen(final WolfPack game, final Campaign campaign, final List<Enemy> battleEnemies, final AssetLoader assets) {
        this.game = game;
        this.campaign = campaign;
        this.campManager = new CampManager();
        this.wolves = campaign.getWolves();
        this.battleEnemies = battleEnemies;
        this.assets = assets;
    }

    @Override
    public void show() {
        campManager.resolveDayEnd(battleEnemies, campaign);

        availableCampActions = new ArrayList<>();
        for (CampActionType type : CampActionType.values()) {
            if (type == CampActionType.SKIP || campaign.canAfford(type.getCost())) {
                availableCampActions.add(type);
            }
        }

        currentCampActionIndex = 0;
        currentCampTargetIndex = 0;
        campResultMessage = null;
        currentState = BattleState.CAMP_SELECT_ACTION;
    }

    @Override
    public void render(float delta) {
        handleInput();
        draw();
    }

    @Override
    public void resize(int i, int i1) {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
    }

    private void handleInput() {
        if (currentState == BattleState.CAMP_SELECT_ACTION) {

            if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
                if (currentCampActionIndex > 0) currentCampActionIndex--;
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
                if (currentCampActionIndex < availableCampActions.size() - 1) currentCampActionIndex++;
            }

            selectedCampAction = availableCampActions.get(currentCampActionIndex);

            if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
                if (selectedCampAction == CampActionType.SKIP) {
                    advanceToNextDay();
                } else {
                    currentCampTargetIndex = 0;
                    currentState = BattleState.CAMP_SELECT_TARGET;
                }
            }

        } else if (currentState == BattleState.CAMP_SELECT_TARGET) {

            if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
                if (currentCampTargetIndex > 0) currentCampTargetIndex--;
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
                if (currentCampTargetIndex < wolves.size() - 1) currentCampTargetIndex++;
            }

            if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
                Wolf target = wolves.get(currentCampTargetIndex);
                applyCampAction(selectedCampAction, target);
                advanceToNextDay();
            }
        }
    }

    private void applyCampAction(CampActionType type, Wolf target) {
        boolean success;
        switch (type) {
            case FEED:
                success = campManager.feed(target, campaign);
                break;
            case TRAIN_ATTACK:
                success = campManager.trainAttack(target, campaign);
                break;
            case TRAIN_DEFENSE:
                success = campManager.trainDefense(target, campaign);
                break;
            case TRAIN_MAX_HP:
                success = campManager.trainMaxHp(target, campaign);
                break;
            default:
                success = false;
        }

        campResultMessage = success
                ? (type.getLabel() + " aplicado em " + target.getName() + "!")
                : (target.getName() + " já treinou ou não foi possível aplicar a ação.");
    }

    private void advanceToNextDay() {
        campaign.advanceDay();
        game.setScreen(new BattleScreen(game, campaign, game.assets));
    }

    private void draw() {
        ScreenUtils.clear(Color.BLACK);
        game.batch.begin();

        int y = 460;

        game.font.draw(game.batch, "--- ACAMPAMENTO (Dia " + campaign.getCurrentDay() + ") ---", 10, y);
        y -= 20;
        game.font.draw(game.batch, "Comida disponível: " + campaign.getFood(), 10, y);
        y -= 30;

        if (campResultMessage != null) {
            game.font.draw(game.batch, campResultMessage, 10, y);
            y -= 30;
        }

        if (currentState == BattleState.CAMP_SELECT_ACTION) {
            game.font.draw(game.batch, "Escolha a Ação (Setas Esquerda/Direita, Espaço para confirmar):", 10, y);
            y -= 30;

            for (int i = 0; i < availableCampActions.size(); i++) {
                CampActionType type = availableCampActions.get(i);
                String cursor = (i == currentCampActionIndex) ? "> " : "  ";
                String costLabel = type.getCost() > 0 ? " (Custo: " + type.getCost() + ")" : "";
                game.font.draw(game.batch, cursor + type.getLabel() + costLabel, 10, y);
                y -= 20;
            }
        } else {
            game.font.draw(game.batch, "Escolha o Lobo (Setas Esquerda/Direita, Espaço para confirmar):", 10, y);
            y -= 30;

            for (int i = 0; i < wolves.size(); i++) {
                Wolf w = wolves.get(i);
                String cursor = (i == currentCampTargetIndex) ? "> " : "  ";
                game.font.draw(
                        game.batch,
                        cursor + String.format("%s: %d/%d HP%s", w.getName(), w.getHp(), w.getMaxHp(), w.hasTrained() ? " [Treinado]" : ""),
                        10,
                        y
                );
                y -= 20;
            }
        }

        game.batch.end();
    }
}
