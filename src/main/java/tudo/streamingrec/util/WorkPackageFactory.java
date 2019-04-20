package tudo.streamingrec.util;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import tudo.streamingrec.AlgorithmWrapper;
import tudo.streamingrec.data.ClickData;
import tudo.streamingrec.data.Event;
import tudo.streamingrec.data.Item;
import tudo.streamingrec.data.Transaction;
import tudo.streamingrec.data.session.SessionExtractor;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class WorkPackageFactory implements IWorkPackageFactory{

    private final SessionExtractor sessionExtractor;
    private final Map<Long, List<Transaction>> userHistory;

    public WorkPackageFactory() {
        this.sessionExtractor = new SessionExtractor();
        this.userHistory = new Long2ObjectOpenHashMap<>();
    }

    /**
     * create a {@link AlgorithmWrapper.WorkPackage} from an event (click or new item)
     * @param event -
     * @param sessionExtractorforEvaluation -
     * @return the work package
     */
    public AlgorithmWrapper.WorkPackage getWorkPackage(Event event, SessionExtractor sessionExtractorforEvaluation) {
        if (event instanceof Item) {
            //in case of an item, just wrap it
            AlgorithmWrapper.WorkPackageArticle wpA = new AlgorithmWrapper.WorkPackageArticle();
            wpA.articleEvent = (Item) event;
            return wpA;
        } else {
            //in case of a click, wrap the click
            //+ find the appropriate session, all previous clicks in other sessions of this user,
            //and the the ground truth related to this click
            AlgorithmWrapper.WorkPackageClick wpC = new AlgorithmWrapper.WorkPackageClick();
            Transaction currentTransaction = (Transaction) event;
            wpC.clickData = new ClickData();
            wpC.clickData.click = currentTransaction;
            // extract the current user session (for removal of
            // duplicate/unnecessary recommendations later)
            List<Transaction> currenctUserSession = Collections
                    .unmodifiableList(new ObjectArrayList<>(sessionExtractor.addClick(currentTransaction)));
            wpC.clickData.session = currenctUserSession;
            //extract the user history
            List<Transaction> history = userHistory.get(currentTransaction.userId);
            if (history == null) {
                history = new ObjectArrayList<>();
                userHistory.put(currentTransaction.userId, history);
            }
            if (currentTransaction.userId != 0)
            {
                history.add(currentTransaction);//add the current click to the user history
            }

            //make it an unmodifiable list
            wpC.clickData.wholeUserHistory = Collections.unmodifiableList(new ObjectArrayList<>(history));
            if (sessionExtractorforEvaluation != null) {
                if (currentTransaction.userId != 0) {
                    // from the session, extract the list of unique item ids
                    LongOpenHashSet uniqueItemIDSoFar = new LongOpenHashSet();
                    for (Transaction t : currenctUserSession) {
                        uniqueItemIDSoFar.add(t.item.id);
                    }
                    //check with the user history to remove unwanted "reminders"
                    List<Transaction> wholeCurrentUserSession = sessionExtractorforEvaluation
                            .getSession(currentTransaction);
                    //extact the ground truth
                    LongOpenHashSet groundTruth = new LongOpenHashSet();
                    for (Transaction t : wholeCurrentUserSession) {
                        groundTruth.add(t.item.id);
                    }
                    // all transactions from the list that have already happened +
                    // transactions for items that have already been clicked (no
                    // reminders)
                    groundTruth.removeAll(uniqueItemIDSoFar);
                    wpC.groundTruth = groundTruth;
                }
                else{
                    wpC.groundTruth = new LongOpenHashSet();
                    wpC.groundTruth.add(currentTransaction.item.id);
                }
            }
            return wpC;
        }
    }
}
