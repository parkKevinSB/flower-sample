package io.github.parkkevinsb.flower.sample.battle.api;

import io.github.parkkevinsb.bloom.EventBus;
import io.github.parkkevinsb.flower.sample.battle.domain.Battle;
import io.github.parkkevinsb.flower.sample.battle.domain.BattleStatus;
import io.github.parkkevinsb.flower.sample.battle.domain.BattleStore;
import io.github.parkkevinsb.flower.sample.battle.domain.MonsterType;
import io.github.parkkevinsb.flower.sample.battle.event.AnimationFinishedEvent;
import io.github.parkkevinsb.flower.sample.battle.event.PlayerAttackEvent;
import io.github.parkkevinsb.flower.sample.battle.event.PlayerFleeEvent;
import io.github.parkkevinsb.flower.sample.battle.workflow.factory.TurnBattleFlowFactory;
import io.github.parkkevinsb.flower.sample.battle.workflow.worker.TurnBattleWorker;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@RestController
public class BattleController {

    private final TurnBattleWorker worker;
    private final TurnBattleFlowFactory flowFactory;
    private final BattleStore store;
    private final EventBus events;

    public BattleController(
            TurnBattleWorker worker,
            TurnBattleFlowFactory flowFactory,
            BattleStore store,
            EventBus events
    ) {
        this.worker = worker;
        this.flowFactory = flowFactory;
        this.store = store;
        this.events = events;
    }

    @GetMapping("/game")
    public GameResponse game() {
        return GameResponse.of(store.snapshot());
    }

    @PostMapping("/battles")
    public ResponseEntity<BattleResponse> startRandom(@RequestParam(required = false) MonsterType monster) {
        String id = "B-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return start(id, monster);
    }

    @PostMapping("/battles/{battleId}")
    public ResponseEntity<BattleResponse> start(
            @PathVariable String battleId,
            @RequestParam(required = false) MonsterType monster
    ) {
        Battle battle = store.start(battleId, monster);
        worker.submit(flowFactory.create(battleId));
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(BattleResponse.of(battle));
    }

    @PostMapping("/battles/{battleId}/attack")
    public ResponseEntity<Void> attack(@PathVariable String battleId) {
        Battle battle = requireBattle(battleId);
        if (battle.getStatus() != BattleStatus.WAITING_PLAYER_ACTION) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "battle is not waiting for a player action");
        }
        events.publish(new PlayerAttackEvent(battleId));
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/battles/{battleId}/flee")
    public ResponseEntity<Void> flee(@PathVariable String battleId) {
        Battle battle = requireBattle(battleId);
        if (battle.getStatus() != BattleStatus.WAITING_PLAYER_ACTION) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "battle is not waiting for a player action");
        }
        events.publish(new PlayerFleeEvent(battleId));
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/battles/{battleId}/animation-finished")
    public ResponseEntity<Void> animationFinished(@PathVariable String battleId) {
        requireBattle(battleId);
        events.publish(new AnimationFinishedEvent(battleId));
        return ResponseEntity.accepted().build();
    }

    @GetMapping("/battles/{battleId}")
    public ResponseEntity<BattleResponse> get(@PathVariable String battleId) {
        Battle battle = store.find(battleId);
        if (battle == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(BattleResponse.of(battle));
    }

    @GetMapping("/battles")
    public List<BattleResponse> list() {
        return store.findAll().stream()
                .map(BattleResponse::of)
                .toList();
    }

    private Battle requireBattle(String battleId) {
        Battle battle = store.find(battleId);
        if (battle == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "unknown battle");
        }
        return battle;
    }
}
