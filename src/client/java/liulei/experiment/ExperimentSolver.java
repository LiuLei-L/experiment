package liulei.experiment;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.OptionalInt;
import java.util.Queue;
import java.util.Set;

/**
 * Solves the three Experimentation Table games (Chronomatron, Ultrasequencer,
 * Superpairs).
 */
final class ExperimentSolver {
    enum Mode { NONE, CHRONOMATRON, ULTRASEQUENCER, SUPERPAIRS }

    record Cell(int slot, String itemId, int count, boolean foil, String name, boolean empty) {
    }

    private static final int SERUMS_CHRONO_ROUND_CAP = 9;
    private static final int SERUMS_ULTRA_ROUND_CAP = 6;

    private Mode mode = Mode.NONE;
    private final List<Integer> chronomatron = new ArrayList<>();
    private boolean chronomatronRevealLatched;
    private int chronomatronClickIndex;
    private int chronomatronRound;
    private final Map<Integer, Integer> ultrasequencer = new HashMap<>();
    private int ultrasequencerClickIndex;
    private boolean ultrasequencerReady;
    private int ultrasequencerRound;
    private final Map<String, Integer> seenPairs = new HashMap<>();
    private final Queue<Integer> pairClicks = new ArrayDeque<>();
    private final Set<Integer> queuedPairSlots = new HashSet<>();
    private boolean serumsCompleted;

    Mode select(String title) {
        Mode selected;
        if (title.contains("Chronomatron") && !title.contains("Sta")) selected = Mode.CHRONOMATRON;
        else if (title.contains("Ultrasequencer") && !title.contains("Sta")) selected = Mode.ULTRASEQUENCER;
        else if (title.startsWith("Superpairs (")) selected = Mode.SUPERPAIRS;
        else selected = Mode.NONE;
        if (selected != mode) reset(selected);
        return mode;
    }

    OptionalInt nextClick(List<Cell> cells, long nowMillis, long lastClickMillis, long delayMillis) {
        if (nowMillis - lastClickMillis < delayMillis) return OptionalInt.empty();
        if (serumsCompleted) return OptionalInt.empty();
        return switch (mode) {
            case CHRONOMATRON -> solveChronomatron(cells);
            case ULTRASEQUENCER -> solveUltrasequencer(cells);
            case SUPERPAIRS -> solveSuperpairs(cells);
            case NONE -> OptionalInt.empty();
        };
    }

    boolean serumsCompleted() {
        return serumsCompleted;
    }

    int rememberedCount() {
        return switch (mode) {
            case CHRONOMATRON -> chronomatron.size();
            case ULTRASEQUENCER -> ultrasequencer.size();
            case SUPERPAIRS -> queuedPairSlots.size();
            case NONE -> 0;
        };
    }

    private OptionalInt solveChronomatron(List<Cell> cells) {
        Cell control = cell(cells, 49);
        if (control == null) return OptionalInt.empty();
        if (control.itemId().equals("minecraft:glowstone")) {
            chronomatronRevealLatched = false;
            chronomatronClickIndex = 0;
            return OptionalInt.empty();
        }
        if (!control.itemId().equals("minecraft:clock")) return OptionalInt.empty();
        Cell foil = cells.stream().filter(cell -> cell.slot() >= 10 && cell.slot() <= 43 && cell.foil()).findFirst().orElse(null);
        if (!chronomatronRevealLatched && foil != null) {
            chronomatron.add(foil.slot());
            chronomatronRound = chronomatron.size();
            chronomatronRevealLatched = true;
            chronomatronClickIndex = 0;
        }
        if (chronomatronRevealLatched && chronomatronClickIndex < chronomatron.size()) {
            int slot = chronomatron.get(chronomatronClickIndex++);
            // 点完当前轮最后一个后检查是否达到 Serums 上限
            if (chronomatronClickIndex >= chronomatron.size()
                    && ExperimentConfig.serums()
                    && chronomatronRound >= SERUMS_CHRONO_ROUND_CAP) {
                serumsCompleted = true;
            }
            return OptionalInt.of(slot);
        }
        return OptionalInt.empty();
    }

    private OptionalInt solveUltrasequencer(List<Cell> cells) {
        Cell control = cell(cells, 49);
        if (control == null) return OptionalInt.empty();
        if (control.itemId().equals("minecraft:clock")) {
            ultrasequencerReady = false;
            if (ultrasequencer.containsKey(ultrasequencerClickIndex)) {
                int slot = ultrasequencer.get(ultrasequencerClickIndex++);
                // 点完当前轮最后一个后检查是否达到 Serums 上限
                if (!ultrasequencer.containsKey(ultrasequencerClickIndex)
                        && ExperimentConfig.serums()
                        && ultrasequencerRound >= SERUMS_ULTRA_ROUND_CAP) {
                    serumsCompleted = true;
                }
                return OptionalInt.of(slot);
            }
            return OptionalInt.empty();
        }
        if (control.itemId().equals("minecraft:glowstone") && !ultrasequencerReady) {
            ultrasequencerRound++;
            ultrasequencer.clear();
            cells.stream()
                .filter(cell -> cell.slot() >= 9 && cell.slot() <= 44)
                .filter(ExperimentSolver::isSequenceItem)
                .sorted(Comparator.comparingInt(Cell::count))
                .forEach(cell -> ultrasequencer.put(cell.count() - 1, cell.slot()));
            ultrasequencerClickIndex = 0;
            ultrasequencerReady = true;
        }
        return OptionalInt.empty();
    }

    private OptionalInt solveSuperpairs(List<Cell> cells) {
        for (Cell cell : cells) {
            if (cell.slot() < 9 || cell.slot() > 44 || !isRevealedPair(cell) || queuedPairSlots.contains(cell.slot())) continue;
            String key = cell.itemId() + "|" + cell.name();
            Integer first = seenPairs.putIfAbsent(key, cell.slot());
            if (first != null && first != cell.slot() && !queuedPairSlots.contains(first)) {
                pairClicks.add(first);
                pairClicks.add(cell.slot());
                queuedPairSlots.add(first);
                queuedPairSlots.add(cell.slot());
                seenPairs.remove(key);
            }
        }
        Integer next = pairClicks.poll();
        return next == null ? OptionalInt.empty() : OptionalInt.of(next);
    }

    private void reset(Mode selected) {
        mode = selected;
        chronomatron.clear();
        chronomatronRevealLatched = false;
        chronomatronClickIndex = 0;
        chronomatronRound = 0;
        ultrasequencer.clear();
        ultrasequencerClickIndex = 0;
        ultrasequencerReady = false;
        ultrasequencerRound = 0;
        seenPairs.clear();
        pairClicks.clear();
        queuedPairSlots.clear();
        serumsCompleted = false;
    }

    private static Cell cell(List<Cell> cells, int slot) {
        return cells.stream().filter(cell -> cell.slot() == slot).findFirst().orElse(null);
    }

    private static boolean isSequenceItem(Cell cell) {
        return cell.itemId().contains("_dye")
            || cell.itemId().equals("minecraft:lapis_lazuli")
            || cell.itemId().equals("minecraft:bone_meal");
    }

    private static boolean isRevealedPair(Cell cell) {
        if (cell.empty()) return false;
        String id = cell.itemId();
        return !id.contains("stained_glass")
            && !id.equals("minecraft:clock")
            && !id.equals("minecraft:glowstone")
            && !id.equals("minecraft:black_dye")
            && !cell.name().isBlank();
    }
}
