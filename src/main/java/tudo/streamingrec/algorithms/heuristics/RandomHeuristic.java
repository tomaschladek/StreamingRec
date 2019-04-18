package tudo.streamingrec.algorithms.heuristics;

import java.util.List;
import java.util.Random;
import java.util.Set;

public class RandomHeuristic implements IHeuristic {

    private Random randomGenerator = new Random();

    @Override
    public Long get(List<Long> items, Set<Long> forbidden) {
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
}
