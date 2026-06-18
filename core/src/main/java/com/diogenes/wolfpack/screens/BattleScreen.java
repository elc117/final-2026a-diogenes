package com.diogenes.wolfpack.screens;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;

import com.badlogic.gdx.utils.ScreenUtils;
import com.diogenes.wolfpack.WolfPack;
import com.diogenes.wolfpack.battle.BattleAction;
import com.diogenes.wolfpack.battle.BattleLogEntry;
import com.diogenes.wolfpack.battle.BattleManager;
import com.diogenes.wolfpack.battle.BattleState;
import com.diogenes.wolfpack.campaign.Campaign;
import com.diogenes.wolfpack.campaign.CampActionType;
import com.diogenes.wolfpack.campaign.CampManager;
import com.diogenes.wolfpack.campaign.EncounterGenerator;
import com.diogenes.wolfpack.entities.*;
import com.diogenes.wolfpack.skills.Skill;
import com.diogenes.wolfpack.skills.TargetingType;

import java.util.ArrayList;
import java.util.List;


// width: 640 height:480
public class BattleScreen implements Screen {

    final WolfPack game;
    private final Campaign campaign;
    private final EncounterGenerator encounterGenerator;
    private final CampManager campManager;

    private BattleManager battleManager;
    private List<Wolf> wolves;
    private List<Enemy> enemies;
    private BattleState currentState;

    private Skill selectedSkill;
    private Unit selectedTarget;
    private int currentSkillIndex;
    private int currentTargetIndex;
    private List<? extends Unit> selectedTargetsList;
    private Unit lastTurnStartProcessedFor;

    // TODO: implement camp screen
    private List<CampActionType> availableCampActions;
    private int currentCampActionIndex;
    private CampActionType selectedCampAction;
    private int currentCampTargetIndex;
    private String campResultMessage;

    private static final int MAX_LOG_ENTRIES = 5; // Maybe test with more?
    private final List<String> battleLog = new ArrayList<>();

    public BattleScreen(final WolfPack game, final Campaign campaign) {
        this.game = game;
        this.campaign = campaign;
        this.encounterGenerator = new EncounterGenerator();
        this.campManager = new CampManager();
    }

    @Override
    public void show() {
        wolves = campaign.getWolves();
        enemies = encounterGenerator.generateEncounter(campaign.getCurrentDay());

        battleManager = new BattleManager(wolves, enemies);

        currentSkillIndex = 0;
        currentTargetIndex = 0;
        lastTurnStartProcessedFor = null;
        campResultMessage = null;

        beginCurrentUnitTurn();
    }

    @Override
    public void render(float delta) {

        handleInput();
        update();

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

    private void update(){
        // camp things are handled in handleInput
        if(currentState == BattleState.CAMP_SELECT_ACTION || currentState == BattleState.CAMP_SELECT_TARGET){
            return;
        }

        if(checkBattleOver()) return;

        if (battleManager.getCurrentUnit() instanceof Wolf) {

            if (currentState != BattleState.SELECT_SKILL && currentState != BattleState.SELECT_TARGET) {
                currentState = BattleState.SELECT_SKILL;
            }

        } else {
            currentState = BattleState.ENEMY_TURN;
            handleEnemyTurn();
            advanceToNextTurn();
        }
    }

    private void beginCurrentUnitTurn(){
        Unit currentUnit = battleManager.getCurrentUnit();

        if(currentUnit != lastTurnStartProcessedFor){
            battleManager.processTurnStart(currentUnit);
            lastTurnStartProcessedFor = currentUnit;
        }

        currentState = (currentUnit instanceof Wolf) ? BattleState.SELECT_SKILL : BattleState.ENEMY_TURN;
    }

    private void advanceToNextTurn(){
        battleManager.nextTurn();
        beginCurrentUnitTurn();
    }

    private boolean checkBattleOver(){
        if(battleManager.playerLost()) {
            currentState = BattleState.DEFEAT;
            return true;
        }
        if(battleManager.playerWon()) {
            if(campaign.isFinalDay()){
                currentState = BattleState.VICTORY;
            } else {
                campManager.resolveDayEnd(enemies, campaign);
                beginCampPhase();
            }
            return true;
        }

        return false;
    }

    private void addToBattleLog(List<BattleLogEntry> entries){
        for(BattleLogEntry entry : entries){
            battleLog.add(0, entry.format());
        }
        while(battleLog.size() > MAX_LOG_ENTRIES){
            battleLog.remove(battleLog.size() - 1);
        }
    }

    // need to add a waiting time, or "space to continue"
    private void handleEnemyTurn(){
        BattleAction action = ((Enemy) battleManager.getCurrentUnit()).chooseAction(battleManager.getWolves());

        // if action is null the enemy fled
        if(action == null) return;


        addToBattleLog(battleManager.executeAction(battleManager.getCurrentUnit(), action));
    }

    private void beginCampPhase(){
        availableCampActions = new ArrayList<>();
        for(CampActionType type : CampActionType.values()){
            if(type == CampActionType.SKIP || campaign.canAfford(type.getCost())){
                availableCampActions.add(type);
            }
        }

        currentCampActionIndex = 0;
        currentCampTargetIndex = 0;
        currentState = BattleState.CAMP_SELECT_ACTION;
    }

    private void handleCampInput(){
        if(currentState == BattleState.CAMP_SELECT_ACTION){

            if(Gdx.input.isKeyJustPressed(Input.Keys.LEFT)){
                if(currentCampActionIndex > 0) currentCampActionIndex--;
            }
            if(Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)){
                if(currentCampActionIndex < availableCampActions.size() - 1) currentCampActionIndex++;
            }

            selectedCampAction = availableCampActions.get(currentCampActionIndex);

            if(Gdx.input.isKeyJustPressed(Input.Keys.SPACE)){
                if(selectedCampAction == CampActionType.SKIP){
                    advanceToNextDay();
                } else {
                    currentCampTargetIndex = 0;
                    currentState = BattleState.CAMP_SELECT_TARGET;
                }
            }

        } else if(currentState == BattleState.CAMP_SELECT_TARGET){

            if(Gdx.input.isKeyJustPressed(Input.Keys.LEFT)){
                if(currentCampTargetIndex > 0) currentCampTargetIndex--;
            }
            if(Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)){
                if(currentCampTargetIndex < wolves.size() - 1) currentCampTargetIndex++;
            }

            if(Gdx.input.isKeyJustPressed(Input.Keys.SPACE)){
                Wolf target = wolves.get(currentCampTargetIndex);
                applyCampAction(selectedCampAction, target);
                advanceToNextDay();
            }
        }
    }

    private void applyCampAction(CampActionType type, Wolf target){
        boolean success;
        switch(type){
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

    private void advanceToNextDay(){
        campaign.advanceDay();
        game.setScreen(new BattleScreen(game, campaign));
    }

    private void handleInput() {
        if(currentState == BattleState.CAMP_SELECT_ACTION || currentState == BattleState.CAMP_SELECT_TARGET){
            handleCampInput();
            return;
        }

        if(currentState == BattleState.SELECT_SKILL){

            if(Gdx.input.isKeyJustPressed(Input.Keys.LEFT)){
                if(currentSkillIndex > 0){
                    currentSkillIndex--;
                }
            }
            if(Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)){
                if(currentSkillIndex < (battleManager.getCurrentUnit().getSkills().size()-1)){
                    currentSkillIndex++;
                }
            }

            selectedSkill = battleManager.getCurrentUnit().getSkills().get(currentSkillIndex);

            if(Gdx.input.isKeyJustPressed(Input.Keys.SPACE)){
                TargetingType targetingType = selectedSkill.getTargetingType();

                if(targetingType == TargetingType.ALL_ENEMIES
                    || targetingType == TargetingType.ALL_ALLIES
                    || targetingType == TargetingType.SELF){
                    // pass null target to battlemanager as he has logic to handle it
                    confirmAndExecute(null);
                } else {
                    List<? extends Unit> candidates = (targetingType == TargetingType.SINGLE_ALLY)
                        ? getActiveTargets(battleManager.getWolves())
                        : getActiveTargets(battleManager.getEnemies());

                    if(candidates.isEmpty()){
                        // this should never hit because checkbattleover catches first
                        return;
                    }

                    selectedTargetsList = candidates;
                    currentTargetIndex = 0;
                    currentState = BattleState.SELECT_TARGET;
                }
            }
        }

        else if(currentState == BattleState.SELECT_TARGET){

            if(Gdx.input.isKeyJustPressed(Input.Keys.LEFT)){
                if(currentTargetIndex > 0){
                    currentTargetIndex--;
                }
            }
            if(Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)){
                if(currentTargetIndex < (selectedTargetsList.size()-1)){
                    currentTargetIndex++;
                }
            }
            selectedTarget = selectedTargetsList.get(currentTargetIndex);
            if(Gdx.input.isKeyJustPressed(Input.Keys.SPACE)){
                confirmAndExecute(selectedTarget);
            }

        }

    }

    // filter non-selectable units, dead ones, and for enemies also fled ones
    private List<? extends Unit> getActiveTargets(List<? extends Unit> units){
        List<Unit> active = new ArrayList<>();
        for(Unit u : units){
            if(!u.isAlive()) continue;
            if(u instanceof Enemy && ((Enemy) u).hasFled()) continue;
            active.add(u);
        }
        return active;
    }

    // execute the selected skill and advances turn
    private void confirmAndExecute(Unit target){
        addToBattleLog(battleManager.executeAction(battleManager.getCurrentUnit(), new BattleAction(selectedSkill, target)));
        currentSkillIndex = 0;
        currentTargetIndex = 0;
        advanceToNextTurn();
    }

    private void draw() {
        ScreenUtils.clear(Color.BLACK);
        game.batch.begin();

        if(currentState == BattleState.CAMP_SELECT_ACTION || currentState == BattleState.CAMP_SELECT_TARGET){
            drawCampPhase();
            game.batch.end();
            return;
        }

        // always draw base infos
        drawTurnInfo();
        drawWolves();
        drawEnemies();
        drawBattleLog();

        // draw contextual ui based on the state
        if (currentState == BattleState.SELECT_SKILL) {
            drawSkillSelection();
        } else if (currentState == BattleState.SELECT_TARGET) {
            drawTargetSelection();
        } else if (currentState == BattleState.VICTORY) {
            game.font.draw(game.batch, "VITORIA! A Matilha sobrevive.", 240, 240);
        } else if (currentState == BattleState.DEFEAT) {
            game.font.draw(game.batch, "DERROTA... A Matilha cai.", 240, 240);
        }

        game.batch.end();
    }

    private void drawTurnInfo() {
        game.font.draw(
            game.batch,
            "Dia " + campaign.getCurrentDay() + " | Turno Atual: " + battleManager.getCurrentUnit().getName(),
            10,
            470
        );
    }

    private void drawWolves() {
        int y = 430;
        game.font.draw(game.batch, "--- LOBOS ---", 10, y);
        y -= 20;

        for (Wolf w : battleManager.getWolves()) {
            game.font.draw(
                game.batch,
                String.format("%s: %d/%d HP", w.getName(), w.getHp(), w.getMaxHp()),
                10,
                y
            );
            y -= 20;
        }
    }

    private void drawEnemies() {
        int y = 430;
        int x = 400;

        game.font.draw(game.batch, "--- INIMIGOS ---", x, y);
        y -= 20;

        for (Enemy e : battleManager.getEnemies()) {
            game.font.draw(
                game.batch,
                String.format("%s: %d/%d HP", e.getName(), e.getHp(), e.getMaxHp()),
                x,
                y
            );
            y -= 20;
        }
    }

    private void drawBattleLog() {
        int y = 90;
        game.font.draw(game.batch, "--- REGISTRO ---", 10, y);
        y -= 20;

        for (String entry : battleLog) {
            game.font.draw(game.batch, entry, 10, y);
            y -= 20;
        }
    }

    private void drawSkillSelection() {
        int y = 200;
        game.font.draw(game.batch, "Escolha a Habilidade (Setas Esquerda/Direita, Espaço para confirmar):", 10, y);
        y -= 30;

        List<Skill> skills = battleManager.getCurrentUnit().getSkills();
        for (int i = 0; i < skills.size(); i++) {
            String cursor = (i == currentSkillIndex) ? "> " : "  ";
            game.font.draw(
                game.batch,
                cursor + skills.get(i).getName(),
                10,
                y
            );
            y -= 20;
        }
    }

    private void drawTargetSelection() {
        int y = 200;
        game.font.draw(game.batch, "Escolha o Alvo (Setas Esquerda/Direita, Espaço para confirmar):", 10, y);
        y -= 30;

        for (int i = 0; i < selectedTargetsList.size(); i++) {
            String cursor = (i == currentTargetIndex) ? "> " : "  ";
            game.font.draw(
                game.batch,
                cursor + selectedTargetsList.get(i).getName(),
                10,
                y
            );
            y -= 20;
        }
    }

    private void drawCampPhase() {
        int y = 460;

        game.font.draw(game.batch, "ACAMPAMENTO (Dia " + campaign.getCurrentDay() + ")", 10, y);
        y -= 20;
        game.font.draw(game.batch, "Comida disponível: " + campaign.getFood(), 10, y);
        y -= 30;

        if(campResultMessage != null){
            game.font.draw(game.batch, campResultMessage, 10, y);
            y -= 30;
        }

        if(currentState == BattleState.CAMP_SELECT_ACTION){
            game.font.draw(game.batch, "Escolha a Ação (Setas Esquerda/Direita, Espaço para confirmar):", 10, y);
            y -= 30;

            for(int i = 0; i < availableCampActions.size(); i++){
                CampActionType type = availableCampActions.get(i);
                String cursor = (i == currentCampActionIndex) ? "> " : "  ";
                String costLabel = type.getCost() > 0 ? " (Custo: " + type.getCost() + ")" : "";
                game.font.draw(game.batch, cursor + type.getLabel() + costLabel, 10, y);
                y -= 20;
            }
        } else {
            game.font.draw(game.batch, "Escolha o Lobo (Setas Esquerda/Direita, Espaço para confirmar):", 10, y);
            y -= 30;

            for(int i = 0; i < wolves.size(); i++){
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
    }


}
