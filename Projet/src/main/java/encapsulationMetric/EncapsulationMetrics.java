package encapsulationMetric;

public class EncapsulationMetrics {
	private String className;
    private int total, pub, pri, pro, def;
    private double lcom5;

    public EncapsulationMetrics(String className, int total, int pub, int pri, int pro, int def ,double lcom5) {
        this.className = className;
        this.total = total;
        this.pub = pub;
        this.pri = pri;
        this.pro = pro;
        this.def = def;
        this.lcom5 = lcom5;
    }
    
    public int getTotal() {
    	return total;
    }
    
    
    public int getPublic() {
    	return pub;
    }
    public int getPrivate() {
    	return pri;
    }
    public int getProtected() {
    	return pro;
    }
    public int getDefault() {
    	return def;
    }
    public double getLCOM5() {
    	return lcom5;
    }
    

    private double getPublicRate() {
        return total == 0 ? 0 : (pub * 100.0) / total;
    }

    private double getPrivateRate() {
        return total == 0 ? 0 : (pri * 100.0) / total;
    }

    private double getProtectedRate() {
        return total == 0 ? 0 : (pro * 100.0) / total;
    }

    private double getPackagePrivateRate() {
        return total == 0 ? 0 : (def * 100.0) / total;
    }

    public String toText() {
        return String.format(
            "🎀 Class: %s\n" +
            "✨ Total Attributes: %d\n" +
            "🔓 Public: %d (%.1f%%)\n" +
            "🔐 Private: %d (%.1f%%)\n" +
            "🛡️ Protected: %d (%.1f%%)\n" +
            "📦 Package-Private: %d (%.1f%%)\n" +
            "📊 LCOM5: %.3f",
            className, total,
            pub, getPublicRate(),
            pri, getPrivateRate(),
            pro, getProtectedRate(),
            def, getPackagePrivateRate(),
            lcom5
        );
    }
}

