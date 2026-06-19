package com.diogenes.wolfpack.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.diogenes.wolfpack.WolfPack;
import com.diogenes.wolfpack.assets.AssetLoader;
import com.diogenes.wolfpack.battle.BattleAction;
import com.diogenes.wolfpack.battle.BattleLogEntry;
import com.diogenes.wolfpack.battle.BattleManager;
import com.diogenes.wolfpack.battle.BattleState;
import com.diogenes.wolfpack.campaign.Campaign;
import com.diogenes.wolfpack.campaign.EncounterGenerator;
import com.diogenes.wolfpack.effects.StatusEffect;
import com.diogenes.wolfpack.entities.*;
import com.diogenes.wolfpack.skills.Skill;
import com.diogenes.wolfpack.skills.TargetingType;

import java.util.ArrayList;
import java.util.List;

public class BattleScreen implements Screen {

    final WolfPack game;
    private final Campaign campaign;
    private final EncounterGenerator encounterGenerator;
    private final AssetLoader assets;
    private final Viewport viewport;

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

    private static final int MAX_LOG_ENTRIES = 5;
    private final List<String> battleLog = new ArrayList<>();

    private static final float ENEMY_TURN_DELAY = 1.0f;
    private float enemyTurnTimer;

    private static final float HP_BAR_WIDTH = 110;
    private static final float HP_BAR_HEIGHT = 10;
    private static final float STATUS_ICON_SIZE = 16;

    private static final float BOTTOM_Y = 180;
    private static final float LOG_WIDTH_PERCENT = 0.40f;
    private static final float SKILL_WIDTH_PERCENT = 0.30f;
    private static final float DESC_WIDTH_PERCENT = 0.30f;

    public BattleScreen(final WolfPack game, final Campaign campaign, final AssetLoader assets) {
        this.game = game;
        this.campaign = campaign;
        this.assets = assets;
        this.encounterGenerator = new EncounterGenerator();

        viewport = new FitViewport(WolfPack.WORLD_WIDTH, WolfPack.WORLD_HEIGHT);
    }

    @Override
    public void show() {
        wolves = campaign.getWolves();
        enemies = encounterGenerator.generateEncounter(campaign.getCurrentDay());

        battleManager = new BattleManager(wolves, enemies);

        currentSkillIndex = 0;
        currentTargetIndex = 0;
        lastTurnStartProcessedFor = null;

        beginCurrentUnitTurn();
    }

    @Override
    public void render(float delta) {
        handleInput();
        update(delta);
        draw();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {}

    private void update(float delta){
        if(checkBattleOver()) return;

        if (battleManager.getCurrentUnit() instanceof Wolf) {
            if (currentState != BattleState.SELECT_SKILL && currentState != BattleState.SELECT_TARGET) {
                currentState = BattleState.SELECT_SKILL;
            }
        } else {
            currentState = BattleState.ENEMY_TURN;
            enemyTurnTimer += delta;
            if(enemyTurnTimer >= ENEMY_TURN_DELAY){
                handleEnemyTurn();
                advanceToNextTurn();
                enemyTurnTimer = 0f;
            }
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
        if (battleManager.playerWon()) {
            if (campaign.isFinalDay()) {
                currentState = BattleState.VICTORY;
            } else {
                game.setScreen(new CampScreen(game, campaign, enemies, assets));
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

    private void handleEnemyTurn(){
        BattleAction action = ((Enemy) battleManager.getCurrentUnit()).chooseAction(battleManager.getWolves());

        // if action is null the enemy fled
        if(action == null) return;

        addToBattleLog(battleManager.executeAction(battleManager.getCurrentUnit(), action));
    }

    private void handleInput() {
        if (currentState == BattleState.SELECT_SKILL) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
                if (currentSkillIndex > 0) currentSkillIndex--;
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
                if (currentSkillIndex < (battleManager.getCurrentUnit().getSkills().size() - 1)) currentSkillIndex++;
            }

            selectedSkill = battleManager.getCurrentUnit().getSkills().get(currentSkillIndex);

            if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
                TargetingType targetingType = selectedSkill.getTargetingType();

                if (targetingType == TargetingType.ALL_ENEMIES
                    || targetingType == TargetingType.ALL_ALLIES
                    || targetingType == TargetingType.SELF) {
                    confirmAndExecute(null);
                } else {
                    List<? extends Unit> candidates = (targetingType == TargetingType.SINGLE_ALLY)
                        ? getActiveTargets(battleManager.getWolves())
                        : getActiveTargets(battleManager.getEnemies());

                    if (candidates.isEmpty()) return;

                    selectedTargetsList = candidates;
                    currentTargetIndex = 0;
                    currentState = BattleState.SELECT_TARGET;
                }
            }
        } else if (currentState == BattleState.SELECT_TARGET) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
                if (currentTargetIndex > 0) currentTargetIndex--;
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
                if (currentTargetIndex < (selectedTargetsList.size() - 1)) currentTargetIndex++;
            }
            selectedTarget = selectedTargetsList.get(currentTargetIndex);
            if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
                confirmAndExecute(selectedTarget);
            }
        }
    }

    // filter non-selectable units, dead ones, and for enemies also fled ones
    private List<? extends Unit> getActiveTargets(List<? extends Unit> units){
        List<Unit> active = new ArrayList<>();
        for (Unit u : units) {
            if (!u.isAlive()) continue;
            if (u instanceof Enemy && ((Enemy) u).hasFled()) continue;
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

    // ===================== DRAWING IMPLEMENTATION =====================

    private void draw() {
        viewport.apply();
        game.batch.setProjectionMatrix(viewport.getCamera().combined);
        ScreenUtils.clear(Color.BLACK);

        game.batch.begin();

        if (assets.winterBackground != null) {
            game.batch.draw(assets.winterBackground, 0, 0, WolfPack.WORLD_WIDTH, WolfPack.WORLD_HEIGHT);
        }

        // 2. Elementos Globais na faixa cinza superior
        drawGlobalTopInfo();

        // 3. Renderizar Arena Central (Sprites + Suas respectivas Barras de Vida Flutuantes)
        drawCombatArena();

        // 4. Console de controle inferior rebocado
        drawBottomSection();

        // Telas de Fim de Jogo
        if (currentState == BattleState.VICTORY) {
            game.font.draw(game.batch, "VITÓRIA! A Matilha sobreviveu ao Inverno.", 450, 400);
        } else if (currentState == BattleState.DEFEAT) {
            game.font.draw(game.batch, "DERROTA... A Neve cobriu seus rastros.", 450, 400);
        }

        game.batch.end();
    }

    private void drawGlobalTopInfo() {
        // Desenha uma faixa preta com 60% de opacidade no topo para dar contraste absoluto
        game.batch.setColor(new Color(0f, 0f, 0f, 0.6f));
        game.batch.draw(assets.whitePixel, 0, 665, WolfPack.WORLD_WIDTH, 55);
        game.batch.setColor(Color.WHITE); // Reseta a cor do batch

        // Texto com cores vivas e legíveis
        game.font.setColor(Color.YELLOW);
        game.font.draw(game.batch, "Dia " + campaign.getCurrentDay() + " / 7", 40, 695);

        game.font.setColor(Color.WHITE);
        game.font.draw(game.batch, "Turno Atual: ", 930, 695);
        game.font.draw(game.batch, battleManager.getCurrentUnit().getName(), 1050, 695);
        game.font.setColor(Color.WHITE);
    }

    private void drawCombatArena() {
        // --- LOOP DOS LOBOS (Lado Esquerdo - Posicionamento Dinâmico em Profundidade) ---
        List<Wolf> activeWolves = battleManager.getWolves();
        for (int i = 0; i < activeWolves.size(); i++) {
            Wolf wolf = activeWolves.get(i);

            // Renderização com base no índice para criar o escalonamento diagonal natural de matilha
            float x = (i == 1) ? 260 : 160; // Alpha (índice 1) avança na linha de frente
            float y = 460 - (i * 125);      // Distribuição vertical uniforme

            drawCharacterWithUi(wolf, x, y, false);
        }

        // --- LOOP DOS INIMIGOS (Lado Direito - Inversão de Espelho) ---
        List<Enemy> activeEnemies = battleManager.getEnemies();
        for (int i = 0; i < activeEnemies.size(); i++) {
            Enemy enemy = activeEnemies.get(i);

            float x = (activeEnemies.size() == 1) ? 900 : ((i == 1) ? 860 : 960);
            float y = 460 - (i * 125);

            if (!enemy.hasFled()) {
                drawCharacterWithUi(enemy, x, y, true);
            }
        }

        if (currentState == BattleState.SELECT_TARGET && selectedTarget != null) {
            float targetX = getUnitRenderX(selectedTarget);
            float targetY = getUnitRenderY(selectedTarget) + getSpriteHeight(selectedTarget) + 50;
            float arrowSize = 32;
            float centerX = targetX + (getSpriteWidth(selectedTarget) / 2) - (arrowSize / 2);

            game.batch.draw(assets.iconSelected, centerX, targetY, arrowSize, arrowSize);
            game.batch.setColor(Color.WHITE);
        }
    }

    private void drawCharacterWithUi(Unit unit, float x, float y, boolean isEnemy) {
        // 1. Desenhar Sprite (Se estiver vivo)
        if (unit.isAlive()) {
            TextureRegion region = isEnemy ? getEnemySprite((Enemy) unit) : getWolfSprite((Wolf) unit);
            if (region != null) {
                game.batch.draw(region, x, y, getSpriteWidth(unit), getSpriteHeight(unit));
            }
        } else {
            // Se morreu, não renderiza o sprite, deixa apenas o texto cinza transparente indicando a queda
            game.font.setColor(Color.GRAY);
            game.font.draw(game.batch, unit.getName() + " (K.O.)", x, y + 30);
            game.font.setColor(Color.WHITE);
            return;
        }

        // 2. Posicionamento das Barras de Vida Flutuantes (Diretamente acima do topo de cada sprite)
        float uiOriginX = x + (getSpriteWidth(unit) / 2) - (HP_BAR_WIDTH / 2);
        float uiOriginY = y + getSpriteHeight(unit) + 12;

        // Nome da Criatura
        Color nameColor = isEnemy ? Color.SCARLET : Color.valueOf("7cd97c");
        game.font.setColor(nameColor);
        game.font.draw(game.batch, unit.getName(), uiOriginX, uiOriginY + 22);

        // Fundo da Barra (Cinza Escuro)
        game.batch.setColor(Color.DARK_GRAY);
        game.batch.draw(assets.whitePixel, uiOriginX, uiOriginY, HP_BAR_WIDTH, HP_BAR_HEIGHT);

        // Preenchimento do HP
        float hpPercent = (float) unit.getHp() / unit.getMaxHp();
        Color barColor = hpPercent > 0.5f ? Color.GREEN : (hpPercent > 0.25f ? Color.YELLOW : Color.RED);
        game.batch.setColor(barColor);
        game.batch.draw(assets.whitePixel, uiOriginX + 1, uiOriginY + 1, Math.max(0, (HP_BAR_WIDTH - 2) * hpPercent), HP_BAR_HEIGHT - 2);
        game.batch.setColor(Color.WHITE);

        // Texto Numérico do HP (Ex: "24/30") colocado no centro da barra
        game.font.setColor(Color.WHITE);
        game.font.draw(game.batch, unit.getHp() + "/" + unit.getMaxHp(), uiOriginX + (HP_BAR_WIDTH / 2) - 18, uiOriginY + 9);

        // Ícones de Status acoplados logo abaixo da barra de vida correspondente
        drawStatusIcons(unit, uiOriginX, uiOriginY - 18);
    }

    // Métodos Auxiliares para cálculo de tamanhos nativos com upscale proporcional (3x)
    private float getSpriteWidth(Unit unit) {
        if (unit instanceof Boar) return 64 * 3;
        if (unit instanceof Fox) return 48 * 3;
        if (unit instanceof Deer) return 72 * 3;
        if (unit instanceof Bear) return 64 * 3;
        if (unit instanceof BossBear) return 76 * 3;
        return 32 * 3; // Lobos e coelhos usam padrão base 32
    }

    private float getSpriteHeight(Unit unit) {
        if (unit instanceof Boar) return 40 * 3;
        if (unit instanceof Fox) return 36 * 3;
        if (unit instanceof Deer) return 52 * 3;
        if (unit instanceof Bear) return 33 * 3;
        if (unit instanceof BossBear) return 64 * 3;
        if (unit instanceof Rabbit) return 26 * 3;
        return 32 * 3; // Lobos usam padrão quadrado 32
    }

    private float getUnitRenderX(Unit unit) {
        if (unit instanceof Wolf) {
            int idx = battleManager.getWolves().indexOf(unit);
            return (idx == 1) ? 260 : 160;
        } else {
            int idx = battleManager.getEnemies().indexOf(unit);
            return (battleManager.getEnemies().size() == 1) ? 900 : ((idx == 1) ? 860 : 960);
        }
    }

    private float getUnitRenderY(Unit unit) {
        int idx = (unit instanceof Wolf) ? battleManager.getWolves().indexOf(unit) : battleManager.getEnemies().indexOf(unit);
        return 460 - (idx * 125);
    }

    private TextureRegion getWolfSprite(Wolf wolf) {
        if (wolf instanceof Scout) return assets.wolfScout;
        if (wolf instanceof Healer) return assets.wolfHealer;
        if (wolf instanceof Alpha) return assets.wolfAlpha;
        return null;
    }

    private TextureRegion getEnemySprite(Enemy enemy) {
        if (enemy instanceof Boar) return assets.enemyBoar;
        if (enemy instanceof Fox) return assets.enemyFox;
        if (enemy instanceof Deer) return assets.enemyDeer;
        if (enemy instanceof Bear) return assets.enemyBear;
        if (enemy instanceof BossBear) return assets.enemyBossBear;
        if (enemy instanceof Rabbit) return assets.enemyRabbit;
        return null;
    }

    private void drawStatusIcons(Unit unit, float x, float y) {
        int index = 0;
        for (StatusEffect effect : unit.getStatusEffects()) {
            TextureRegion icon = getIconForStatus(effect);
            if (icon != null) {
                game.batch.draw(icon, x + (index * (STATUS_ICON_SIZE + 4)), y, STATUS_ICON_SIZE, STATUS_ICON_SIZE);
                index++;
            }
        }
    }

    private TextureRegion getIconForStatus(StatusEffect effect) {
        String name = effect.getName();
        if (name.equals("Sangramento") || name.equals("Bleed")) return assets.iconBleed;
        if (name.equals("Ataque+") || name.equals("Attack Up")) return assets.iconAttackUp;
        if (name.equals("Ataque-") || name.equals("Attack Down")) return assets.iconAttackDown;
        if (name.equals("Marcado") || name.equals("Marked")) return assets.iconMarked;
        return null;
    }

    private void drawBottomSection() {
        float bottomY = 0;
        float logWidth = WolfPack.WORLD_WIDTH * LOG_WIDTH_PERCENT;
        float skillWidth = WolfPack.WORLD_WIDTH * SKILL_WIDTH_PERCENT;
        float descWidth = WolfPack.WORLD_WIDTH * DESC_WIDTH_PERCENT;

        // Fundo do painel inferior consolidado
        game.batch.setColor(new Color(0.04f, 0.04f, 0.06f, 0.95f));
        game.batch.draw(assets.whitePixel, 0, bottomY, WolfPack.WORLD_WIDTH, BOTTOM_Y);
        game.batch.setColor(Color.WHITE);

        // Linhas Divisórias
        game.batch.setColor(Color.DARK_GRAY);
        game.batch.draw(assets.whitePixel, logWidth, bottomY, 2, BOTTOM_Y);
        game.batch.draw(assets.whitePixel, logWidth + skillWidth, bottomY, 2, BOTTOM_Y);
        game.batch.setColor(Color.WHITE);

        // 3 Sub-painéis internos
        drawBattleLog(logWidth, bottomY);
        drawSelectionPanel(skillWidth, bottomY, logWidth);
        drawDescriptionPanel(descWidth, bottomY, logWidth + skillWidth);
    }

    private void drawBattleLog(float width, float bottomY) {
        game.font.setColor(Color.GRAY);
        game.font.draw(game.batch, "--- REGISTRO DE COMBATE ---", 20, bottomY + BOTTOM_Y - 12);

        float y = bottomY + BOTTOM_Y - 35;
        for (String entry : battleLog) {
            game.font.setColor(Color.WHITE);
            game.font.draw(game.batch, entry, 20, y);
            y -= 24;
        }
    }

    private void drawSelectionPanel(float width, float bottomY, float offsetX) {
        float x = offsetX + 20;
        float y = bottomY + BOTTOM_Y - 12;

        if (currentState == BattleState.SELECT_SKILL) {
            drawSkillSelection(x, y, width);
        } else if (currentState == BattleState.SELECT_TARGET) {
            drawTargetSelection(x, y, width);
        } else if (currentState == BattleState.ENEMY_TURN) {
            game.font.setColor(Color.ORANGE);
            game.font.draw(game.batch, "Turno do adversário...", x, y - 25);
        }
        game.font.setColor(Color.WHITE);
    }

    private void drawSkillSelection(float x, float y, float width) {
        game.font.setColor(Color.LIGHT_GRAY);
        game.font.draw(game.batch, "HABILIDADE (<- ->, ESPAÇO):", x, y);
        y -= 30;

        List<Skill> skills = battleManager.getCurrentUnit().getSkills();
        for (int i = 0; i < skills.size(); i++) {
            String cursor = (i == currentSkillIndex) ? "> " : "  ";
            Color color = (i == currentSkillIndex) ? Color.YELLOW : Color.WHITE;
            game.font.setColor(color);
            game.font.draw(game.batch, cursor + skills.get(i).getName(), x, y);
            y -= 26;
        }
    }

    private void drawTargetSelection(float x, float y, float width) {
        game.font.setColor(Color.LIGHT_GRAY);
        game.font.draw(game.batch, "ESCOLHA O ALVO (<- ->, ESPAÇO):", x, y);
        y -= 30;

        for (int i = 0; i < selectedTargetsList.size(); i++) {
            String cursor = (i == currentTargetIndex) ? "> " : "  ";
            Color color = (i == currentTargetIndex) ? Color.YELLOW : Color.WHITE;
            game.font.setColor(color);
            game.font.draw(game.batch, cursor + selectedTargetsList.get(i).getName(), x, y);
            y -= 26;
        }
    }

    private void drawDescriptionPanel(float width, float bottomY, float offsetX) {
        float x = offsetX + 20;
        float y = bottomY + BOTTOM_Y - 12;

        game.font.setColor(Color.GRAY);
        game.font.draw(game.batch, "--- DESCRIÇÃO DA AÇÃO ---", x, y);
        y -= 30;

        if (selectedSkill != null) {
            game.font.setColor(Color.WHITE);
            String desc = selectedSkill.getDescription();

            if (desc.length() > 26) {
                String[] words = desc.split(" ");
                StringBuilder line = new StringBuilder();
                int lineCount = 0;
                for (String word : words) {
                    if (line.length() + word.length() + 1 > 26) {
                        game.font.draw(game.batch, line.toString(), x, y - (lineCount * 22));
                        line = new StringBuilder(word);
                        lineCount++;
                        if (lineCount > 3) break;
                    } else {
                        if (line.length() > 0) line.append(" ");
                        line.append(word);
                    }
                }
                if (line.length() > 0 && lineCount <= 3) {
                    game.font.draw(game.batch, line.toString(), x, y - (lineCount * 22));
                }
            } else {
                game.font.draw(game.batch, desc, x, y);
            }

            game.font.setColor(Color.GOLD);
            game.font.draw(game.batch, "Alvo: " + getTargetingDescription(selectedSkill.getTargetingType()), x, bottomY + 25);
        }
        game.font.setColor(Color.WHITE);
    }

    private String getTargetingDescription(TargetingType type) {
        switch (type) {
            case SINGLE_ENEMY: return "Inimigo único";
            case SINGLE_ALLY: return "Aliado único";
            case ALL_ENEMIES: return "Todos inimigos";
            case ALL_ALLIES: return "Todos aliados";
            case SELF: return "Próprio";
            default: return "—";
        }
    }
}
