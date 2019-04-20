package tudo.streamingrec.util;

import tudo.streamingrec.AlgorithmWrapper;
import tudo.streamingrec.data.Event;
import tudo.streamingrec.data.session.SessionExtractor;

public interface IWorkPackageFactory{
    AlgorithmWrapper.WorkPackage getWorkPackage(Event event, SessionExtractor sessionExtractorforEvaluation);
}
