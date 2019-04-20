package tudo.streamingrec.algorithms.heuristics;

import java.util.List;
import java.util.Random;
import java.util.Set;

public class RandomHeuristic implements IHeuristic {

    private Random randomGenerator;

    public RandomHeuristic() {
        this(new Random());
    }

    public RandomHeuristic(Random randomGenerator) {
        this.randomGenerator = randomGenerator;
    }

    @Override
    public Long get(List<Long> items, Set<Long> forbidden) {
        if (items.size() == 0) return null;

        int max = Math.max(items.size(),50);
        for (int index = 0; index < max; index++)
        {
            int randomIndex = randomGenerator.nextInt(items.size());
            long item = items.get(randomIndex);
            if (!forbidden.contains(item)){
                return item;
            }
        }
        return null;
    }

    @Override
    public void trainAdd(long userId, long itemId) {

    }

    @Override
    public void trainRemove(long userId, long itemId) {

    }

    @Override
    public IHeuristic copy() {
        return new RandomHeuristic(randomGenerator);
    }
}
