package app;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;


import javax.swing.SwingWorker;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;

import CBO.CBOMetric;
import DitOomr.MethodCollector;
import NOCMetric.ChildrenNB;
import defectsUtils.Utility;
import encapsulationMetric.EncapsulationConsoleApp;
import encapsulationMetric.EncapsulationMetrics;
import exceptionsm.ExceptionsMetric;
import importConflicts.ImportMetric;
import javalyzerx.JavalyzerxMetric;


public class FileAnalyzer {

	private final AnalysisResultHandler resultHandler;

    public FileAnalyzer(AnalysisResultHandler resultHandler) {
        this.resultHandler = resultHandler;
    }

    public void analyzeProject(File projectDir) {
        new SwingWorker<List<MetricsData>, Void>() {
            @Override
            protected List<MetricsData> doInBackground() throws Exception {
                List<MetricsData> results = new ArrayList<>();
                HashSet<File> jarfilesinm2 = Utility.getJarsFromM2(projectDir.getAbsolutePath());
                Files.walk(projectDir.toPath())
                    .filter(path -> path.toString().endsWith(".java"))
                    .forEach(path -> {
                        MetricsData metrics = analyzeJavaFile(path.toFile(),jarfilesinm2);
                        if (metrics != null) results.add(metrics);
                    });
                return results;
            }

            @Override
            protected void done() {
                try {
                    resultHandler.handleResults(get(),projectDir.getAbsolutePath());
                } catch (Exception e) {
                    resultHandler.handleError(e);
                }
            }
        }.execute();
    }
    
    
    public void analyzeFile(File file) {
        new SwingWorker<MetricsData, Void>() {
            @Override
            protected MetricsData doInBackground() throws Exception {
            	HashSet<File> jarfilesinm2 = Utility.getJarsFromM2(file.getAbsolutePath());
                return analyzeJavaFile(file,jarfilesinm2);
            }

            @Override
            protected void done() {
                try {
                    MetricsData result = get();
                    if (result != null) {
                        resultHandler.handleResults(Collections.singletonList(result),file.getAbsolutePath());
                    } else {
                        resultHandler.handleError(new Exception("Analysis returned null"));
                    }
                } catch (Exception e) {
                    resultHandler.handleError(e);
                }
            }
        }.execute();
    }
    
    
    private MetricsData analyzeJavaFile(File file,HashSet<File> jarfilesinm2)  {
        
        try {
            
            System.out.println(file.toPath().toString());
            String className = Utility.getClassName(file.toPath().toString()) + ".java";
            System.out.println(className);
            
            CombinedTypeSolver combinedsolver = Utility.getCombinedTypeSolver(file.toPath().toString(),jarfilesinm2);
			CompilationUnit cu = Utility.getCompilationUnit(file.toPath().toString(),combinedsolver);
			ImportMetric imp = new ImportMetric(cu,file.toPath().toString());
			EncapsulationMetrics encap = EncapsulationConsoleApp.analyzeEncapsulation(file);
			JavalyzerxMetric jxm = new JavalyzerxMetric(cu,file.toPath().toString());
			CBOMetric cbom = new CBOMetric(cu,combinedsolver);
			ChildrenNB nocm = new ChildrenNB(file.toPath().toString());
			ExceptionsMetric ex = new ExceptionsMetric(file.toPath().toString());
			MethodCollector mc = new MethodCollector(file,jarfilesinm2);
          
                
            MetricsData metrics = new MetricsData();
            metrics.setClassName(className);
            
            metrics.setTotalImports(imp.getTotalImports());
            metrics.setUsedImports(imp.getUsedImports());
            metrics.setUnusedImports(imp.getUnusedImports());
            metrics.setDublicateimports(imp.getDublicateimports());
            metrics.setUnjudgedImports(imp.getUnjudgedImports());
            metrics.setImportConflicts(imp.getImportConflicts());
            metrics.setWildCardImports(imp.getWildCardImports());
       

            // 🔗 CBO
            metrics.setCBO(cbom.getCBO());

            // 🔐 Encapsulation
            metrics.setTotalEnc(encap.getTotal());
            metrics.setPublic(encap.getPublic());
            metrics.setPrivate(encap.getPrivate());
            metrics.setProtected(encap.getProtected());
            metrics.setDefault(encap.getDefault());
            metrics.setLCOM5(encap.getLCOM5());

            // 📊 Javalyzerx
            metrics.setTotalLines(jxm.getTotalLines());
            metrics.setLinesWithComments(jxm.getLinesWithComments());
            metrics.setClassDeclaration(jxm.getClassDeclaration());
            metrics.setNestedClasses(jxm.getNestedClasses());
            metrics.setInterfaceDeclaration(jxm.getInterfaceDeclaration());
            metrics.setImplementedInterfaces(jxm.getImplementedInterfaces());
            metrics.setAbstractClasses(jxm.getAbstractClasses());
            metrics.setAbstractMethods(jxm.getAbstractMethods());

            // NOC
            metrics.setNOC(nocm.getNOC());

            // Exceptions
            metrics.setTotal(ex.getTotal());
            metrics.setJdkExceptions(ex.getJdkExceptions());
            metrics.setCustomExceptions(ex.getCustomExceptions());
            metrics.setJdkCheckedExceptions(ex.getJdkCheckedExceptions());
            metrics.setJdkRuntimeExceptions(ex.getCustomExceptions());
            metrics.setJdkErrors(ex.getJdkErrors());

            //OOMR
            metrics.setDIT(mc.getDIT());
            metrics.setTotalMethods(mc.getTotalMethods());
            metrics.setExplictmethods(mc.getExplictmethods());
            metrics.setOrmethods(mc.getOrmethods());
            metrics.setOlmethods(mc.getOlmethods());
            metrics.setOrratio(mc.getOrratio());
            metrics.setOlration(mc.getOlration());
            metrics.setOrwithnoano(mc.getOrwithnoano());
            metrics.setMtconstmethods(mc.getMtconstmethods());
            metrics.setExcesiveol(mc.getExcesiveol());
            metrics.setAmbgmethods(mc.getAmbgmethods());
            metrics.setOlandor(mc.getOlandor());
            
           
            
            return metrics;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
