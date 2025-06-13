package app;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class AnalysisRecord implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final String timestamp;
    private final String name;
    final List<MetricsData> metrics;
    private final boolean isProject;

    public AnalysisRecord(String name, List<MetricsData> metrics, boolean isProject) {
        this.timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date());
        this.name = name;
        this.metrics = metrics;
        this.isProject = isProject;
    }
    
    public String getDisplayName() {
        return (isProject ? "[Project] " : "[File] ") + name + " (" + timestamp + ")";
    }
    
    public List<MetricsData> getMetrics (){
    	return metrics;
    }
    
    
}
