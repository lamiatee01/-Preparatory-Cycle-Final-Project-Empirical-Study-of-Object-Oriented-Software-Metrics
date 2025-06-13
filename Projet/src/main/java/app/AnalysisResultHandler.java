package app;

import java.awt.Color;
import java.util.List;

public interface AnalysisResultHandler {
	void handleResults(List<MetricsData> results,String path);
    void handleError(Exception e);
    void updateStatus(String message, Color color);
}
