package DitOomr;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;

import defectsUtils.Utility;

import com.github.javaparser.resolution.declarations.ResolvedReferenceTypeDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.github.javaparser.resolution.types.ResolvedReferenceType;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.type.Type;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class MethodCollector {
	
	private static int DIT;
	private static int totalmethods;
	private static int explictmethods;
	private static int ormethods;
	private static int olmethods;
	private static float orratio;
	private static float olration;
	private static int orwithnoano;
	private static int mtconstmethods;
	private static int excesiveol;
	private static int ambgmethods;
	private static float olandor;
	private static float orwithsuper;
	
	
	public MethodCollector(File file,HashSet<File> jarfilesinm2) throws IOException {
		compareMethods(file,jarfilesinm2);
		analyzeAdvancedOverrideMetrics(file,jarfilesinm2);
	}
	
	public int getDIT() {
        return DIT;
    }
	
	public int getTotalMethods() {
		return totalmethods;
	}

   

    public int getExplictmethods() {
        return explictmethods;
    }

    

    public int getOrmethods() {
        return ormethods;
    }

   
    public int getOlmethods() {
        return olmethods;
    }

    

    public float getOrratio() {
        return orratio;
    }


    public float getOlration() {
        return olration;
    }


    public int getOrwithnoano() {
        return orwithnoano;
    }

    

    public int getMtconstmethods() {
        return mtconstmethods;
    }

    

    public int getExcesiveol() {
        return excesiveol;
    }

    

    public int getAmbgmethods() {
        return ambgmethods;
    }

  

    public float getOlandor() {
        return olandor;
    }

   

    public float getOrwithsuper() {
        return orwithsuper;
    }

   
    
	

    public static Set<String> collectInheritedMethods(ResolvedReferenceTypeDeclaration resolvedClass) {
        Set<String> inheritedMethods = new HashSet<>();

        // On ne filtre plus la classe Object, on prend toutes les méthodes des ancêtres
        for (ResolvedReferenceType ancestor : resolvedClass.getAllAncestors()) {
            try {
                Optional<ResolvedReferenceTypeDeclaration> ancestorTypeOpt = ancestor.getTypeDeclaration();
                if (ancestorTypeOpt.isPresent()) {
                    ResolvedReferenceTypeDeclaration ancestorType = ancestorTypeOpt.get();
                    for (ResolvedMethodDeclaration method : ancestorType.getDeclaredMethods()) {
                        inheritedMethods.add(method.getSignature().toString());
                        
                        // Ajout important: stockons également le nom de la méthode sans signature complète
                        // pour détecter les redéfinitions même quand les signatures ne correspondent pas exactement
                        inheritedMethods.add(method.getName());
                    }
                }
            } catch (Exception e) {
                System.err.println("Erreur en traitant l'ancêtre : " + ancestor.getQualifiedName());
            }
        }

        return inheritedMethods;
    }
    
    /**
     * Collecte les signatures et les noms de méthodes héritées sous forme de structure avancée
     * pour faciliter la détection des redéfinitions
     * @param resolvedClass La classe à analyser
     * @return Map avec les noms de méthodes et leurs signatures par classe ancêtre
     */
    public static Map<String, List<String>> collectInheritedMethodsAdvanced(ResolvedReferenceTypeDeclaration resolvedClass) {
        Map<String, List<String>> inheritedMethodMap = new HashMap<>();
        
        
        // Parcourir tous les ancêtres
        for (ResolvedReferenceType ancestor : resolvedClass.getAllAncestors()) {
            try {
                Optional<ResolvedReferenceTypeDeclaration> ancestorTypeOpt = ancestor.getTypeDeclaration();
                if (ancestorTypeOpt.isPresent()) {
                    ResolvedReferenceTypeDeclaration ancestorType = ancestorTypeOpt.get();
                    
                    // Pour chaque méthode déclarée dans l'ancêtre
                    for (ResolvedMethodDeclaration method : ancestorType.getDeclaredMethods()) {
                        String methodName = method.getName();
                        String methodSignature = method.getSignature().toString();
                        
                        // Stocker le nom de la méthode comme clé et ajouter la signature à la liste
                        inheritedMethodMap.computeIfAbsent(methodName, k -> new ArrayList<>())
                                          .add(methodSignature);
                    }
                }
            } catch (Exception e) {
                System.err.println("Erreur en traitant l'ancêtre : " + ancestor.getQualifiedName());
            }
        }
        
        return inheritedMethodMap;
    }
  
    
 
    
 
    


   
    



    /**
     * Vérifie si une méthode est une redéfinition d'une méthode héritée
     * Cette fonction utilise plusieurs techniques pour détecter les redéfinitions
     */
    public static boolean isMethodOverriding1(MethodDeclaration methodDecl, Map<String, List<String>> inheritedMethodMap) {
        String methodName = methodDecl.getNameAsString();
        
        // Vérification 1 : La méthode a l'annotation @Override
        if (!methodDecl.getAnnotationByName("Override").isEmpty()) {
            return true;
        }
        
        // Vérification 2 : Le nom est dans la map des méthodes héritées
        if (!inheritedMethodMap.containsKey(methodName)) {
            return false; // Nom non trouvé, ce n'est pas une redéfinition
        }
        
        // Vérification 3 : Comparaison des signatures
        List<String> inheritedSignatures = inheritedMethodMap.get(methodName);
        
        // Si aucune signature n'est disponible pour ce nom, pas de redéfinition possible
        if (inheritedSignatures == null || inheritedSignatures.isEmpty()) {
            return false;
        }
        
        // Obtenir le nombre et les types de paramètres de la méthode déclarée
        int declaredParamCount = methodDecl.getParameters().size();
        List<Type> declaredParamTypes = new ArrayList<>();
        methodDecl.getParameters().forEach(p -> declaredParamTypes.add(p.getType()));
        
        // Pour chaque signature héritée avec le même nom
        for (String inheritedSignature : inheritedSignatures) {
            try {
                // Extraction des paramètres de la signature héritée
                int start = inheritedSignature.indexOf('(');
                int end = inheritedSignature.indexOf(')');
                
                if (start < 0 || end < 0) continue;
                
                String paramsInherited = inheritedSignature.substring(start + 1, end);
                int inheritedParamCount = paramsInherited.isEmpty() ? 0 : paramsInherited.split(",").length;
                
                // Si le nombre de paramètres est différent, ce n'est pas cette méthode qui est redéfinie
                if (declaredParamCount != inheritedParamCount) {
                    continue;
                }
                
                // Vérification supplémentaire pour les méthodes sans paramètres
                if (declaredParamCount == 0 && inheritedParamCount == 0) {
                    return true; // Redéfinition trouvée pour méthode sans paramètres
                }
                
                // Pour les méthodes avec paramètres, vérification avancée
                if (areParameterTypesCompatible(declaredParamTypes, paramsInherited)) {
                    return true; // Redéfinition trouvée
                }
            } catch (Exception e) {
                System.err.println("Erreur lors de la vérification de redéfinition pour " + methodName + ": " + e.getMessage());
            }
        }
        
        // Dernière tentative : vérification basée uniquement sur le nom et le nombre de paramètres
        // C'est moins précis mais permet de détecter les redéfinitions quand la résolution des types échoue
        return methodDecl.getNameAsString().equals("equals") && declaredParamCount == 1 ||
               methodDecl.getNameAsString().equals("hashCode") && declaredParamCount == 0 ||
               methodDecl.getNameAsString().equals("toString") && declaredParamCount == 0;
    }
    
    /**
     * Vérifie si les types de paramètres d'une méthode sont compatibles avec ceux d'une signature héritée
     */
    public static boolean areParameterTypesCompatible(List<Type> declaredTypes, String inheritedParamsStr) {
        // Si l'un est vide et pas l'autre, ils ne sont pas compatibles
        if ((declaredTypes.isEmpty() && !inheritedParamsStr.isEmpty()) || 
            (!declaredTypes.isEmpty() && inheritedParamsStr.isEmpty())) {
            return false;
        }
        
        // Si les deux sont vides, ils sont compatibles
        if (declaredTypes.isEmpty() && inheritedParamsStr.isEmpty()) {
            return true;
        }
        
        // Diviser la chaîne de paramètres hérités en types individuels
        String[] inheritedTypeNames = inheritedParamsStr.split(",");
        
        // Si le nombre de paramètres est différent, ils ne sont pas compatibles
        if (declaredTypes.size() != inheritedTypeNames.length) {
            return false;
        }
        
        // Vérification simplifiée: compare juste les noms courts des types
        for (int i = 0; i < declaredTypes.size(); i++) {
            String declaredTypeName = declaredTypes.get(i).toString();
            String inheritedTypeName = inheritedTypeNames[i].trim();
            
            // Extraction du nom court (sans package) pour faciliter la comparaison
            String declaredShortName = extractShortTypeName(declaredTypeName);
            String inheritedShortName = extractShortTypeName(inheritedTypeName);
            
            // Si les noms courts ne correspondent pas
            if (!areShortTypeNamesCompatible(declaredShortName, inheritedShortName)) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Extrait le nom court d'un type (sans le package)
     */
    private static String extractShortTypeName(String fullTypeName) {
        // Gérer le cas des génériques en les ignorant simplement pour cette analyse
        if (fullTypeName.contains("<")) {
            fullTypeName = fullTypeName.substring(0, fullTypeName.indexOf('<'));
        }
        
        // Extraire le nom court à partir du nom complet
        int lastDot = fullTypeName.lastIndexOf('.');
        return lastDot >= 0 ? fullTypeName.substring(lastDot + 1) : fullTypeName;
    }
    
    /**
     * Vérifie si deux noms courts de types sont compatibles pour une redéfinition
     */
    private static boolean areShortTypeNamesCompatible(String type1, String type2) {
        // Cas d'égalité directe
        if (type1.equals(type2)) {
            return true;
        }
        
        // Cas des primitifs vs wrappers
        if ((type1.equals("int") && type2.equals("Integer")) || 
            (type1.equals("Integer") && type2.equals("int"))) {
            return true;
        }
        
        if ((type1.equals("long") && type2.equals("Long")) || 
            (type1.equals("Long") && type2.equals("long"))) {
            return true;
        }
        
        if ((type1.equals("boolean") && type2.equals("Boolean")) || 
            (type1.equals("Boolean") && type2.equals("boolean"))) {
            return true;
        }
        
        if ((type1.equals("char") && type2.equals("Character")) || 
            (type1.equals("Character") && type2.equals("char"))) {
            return true;
        }
        
        if ((type1.equals("double") && type2.equals("Double")) || 
            (type1.equals("Double") && type2.equals("double"))) {
            return true;
        }
        
        if ((type1.equals("float") && type2.equals("Float")) || 
            (type1.equals("Float") && type2.equals("float"))) {
            return true;
        }
        
        if ((type1.equals("byte") && type2.equals("Byte")) || 
            (type1.equals("Byte") && type2.equals("byte"))) {
            return true;
        }
        
        if ((type1.equals("short") && type2.equals("Short")) || 
            (type1.equals("Short") && type2.equals("short"))) {
            return true;
        }
        
        // Pour Object et ses sous-classes dans la redéfinition equals()
        if (type1.equals("Object") && type2.equals("Object") || 
            type1.equals("Object") && !type2.equals("Object") || 
            !type1.equals("Object") && type2.equals("Object")) {
            return true;
        }
        
        return false;
    }

    public static void compareMethods(File file,HashSet<File> jarfilesinm2) throws IOException {
        // Construction automatique du TypeSolver adapté au fichier analysé
        CombinedTypeSolver typeSolver = Utility.getCombinedTypeSolver(file.getAbsolutePath(),jarfilesinm2);
        
       
        CompilationUnit cu = Utility.getCompilationUnit(file.getAbsolutePath(), typeSolver);
       

            if (cu.getTypes().isEmpty()) {
                System.out.println("Aucune classe ou interface trouvée dans le fichier.");
                return;
            }


            cu.getTypes().forEach(typeDecl -> {
                try {
                    ResolvedReferenceTypeDeclaration resolvedClass = typeDecl.resolve();


                    // Collection avancée des méthodes héritées avec leur nom et signatures
                    Map<String, List<String>> inheritedMethodMap = collectInheritedMethodsAdvanced(resolvedClass);
                    
                    // Collection traditionnelle pour compatibilité avec le reste du code
                    Set<String> inherited = collectInheritedMethods(resolvedClass);
                    
                    // Collection séparée pour les noms de méthodes héritées (sans signatures)
                    Set<String> inheritedNames = new HashSet<>();
                    for (String method : inherited) {
                        if (!method.contains("(")) {
                            // Si c'est juste un nom (sans parenthèses), ajoutez-le directement
                            inheritedNames.add(method);
                        } else {
                            // Extrait juste le nom de la méthode à partir de la signature
                            inheritedNames.add(method.substring(0, method.indexOf('(')));
                        }
                    }
                    
                    Set<String> declared = new HashSet<>();
                    for (ResolvedMethodDeclaration method : resolvedClass.getDeclaredMethods()) {
                        declared.add(method.getSignature().toString());
                    }
                   

                    int overrideCount = 0;
                    int totalMethods = 0;
                    float surchargeRatio = 0;
                    float redefRatio = 0;
                    int compteurOverrideSansAnnotation = 0;
                    int compteurOverrideVide = 0;
                    int surchargeExcessive = 0;
                    int surchargeAmbigue = 0;

                    // Détection des redéfinitions (override) - Version améliorée
                    Set<String> overridden = new HashSet<>();
                    
                    // Récupérer toutes les méthodes déclarées dans le code source
                    List<MethodDeclaration> declaredMethods = cu.findAll(MethodDeclaration.class);
                    
                    for (MethodDeclaration methodDecl : declaredMethods) {
                        try {
                            // Nouvelle implémentation plus robuste pour détecter les redéfinitions
                            boolean isOverride = isMethodOverriding1(methodDecl, inheritedMethodMap);
                            
                            // Vérifier si la méthode a l'annotation @Override explicite
                            boolean hasOverrideAnnotation = !methodDecl.getAnnotationByName("Override").isEmpty();
                            
                            // Récupérer le nom de la méthode
                            String methodName = methodDecl.getNameAsString();
                            
                            // Si c'est une redéfinition, on l'ajoute au compte
                            if (isOverride) {
                                overrideCount++;
                                
                                String resolvedSignature = "";
                                
                                try {
                                    // Essayer d'obtenir la signature complète
                                    ResolvedMethodDeclaration resolvedMethod = methodDecl.resolve();
                                    resolvedSignature = resolvedMethod.getSignature();
                                } catch (Exception e) {
                                    // Si ça échoue, utiliser une version simple de la signature
                                    resolvedSignature = methodName + "(" + methodDecl.getParameters().size() + " params)";
                                }
                                
                                overridden.add(resolvedSignature);
                                
                                // Vérification de l'annotation @Override
                                if (!hasOverrideAnnotation) {
                                	compteurOverrideSansAnnotation++;
                                    
                                }
                                
                                // Vérification si le corps est vide
                                if (!methodDecl.getBody().isPresent() || methodDecl.getBody().get().getStatements().isEmpty()) {
                                    compteurOverrideVide++;
                                   
                                }
                            }
                        } catch (Exception e) {
                            System.err.println("Erreur lors de l'analyse de la méthode : " + e.getMessage());
                        }
                    }

                    // Détection des vraies surcharges (overload)
                    Map<String, Set<String>> methodsByName = new HashMap<>();
                    for (ResolvedMethodDeclaration method : resolvedClass.getDeclaredMethods()) {
                        String name = method.getName();
                        String signature = method.getSignature().toString();
                        
                        // On n'inclut que les méthodes qui ne redéfinissent pas
                        if (!overridden.contains(signature)) {
                            methodsByName.computeIfAbsent(name, k -> new HashSet<>()).add(signature);
                        }
                    }
                    
                    int overloadCount = 0;
                    //Set<String> nomsGeneriques = Set.of("handle", "process", "doSomething", "execute", "perform", "run", "get", "set");

                    for (Map.Entry<String, Set<String>> entry : methodsByName.entrySet()) {
                        //String methodName = entry.getKey();
                        Set<String> overloads = entry.getValue();

                        if (overloads.size() > 1) {
                        	overloadCount += (overloads.size() - 1);

                            

                            // Vérifie surcharges ambiguës
                            for (String sig1 : overloads) {
                                for (String sig2 : overloads) {
                                    if (!sig1.equals(sig2) && sontSurchargesAmbigues(sig1, sig2)) {
                                        surchargeAmbigue++;
                                        // On ne compte chaque paire qu'une fois
                                        break;
                                    }
                                }
                            }
                            
                        }
                    }

                    

                    int dit = resolvedClass.getAllAncestors().size();
                    

                 // À:
                 // Ne compter que les signatures de méthodes héritées (pas les noms simples)
                 int inheritedMethodsCount = 0;
                 for (String method : inherited) {
                     if (method.contains("(")) { // Ne compte que les signatures complètes
                         inheritedMethodsCount++;
                     }
                 }
                 totalMethods = declared.size() + (inheritedMethodsCount - overrideCount);
                 surchargeRatio = totalMethods > 0 ? (float) overloadCount / totalMethods : 0;
                 redefRatio = totalMethods > 0 ? (float) overrideCount / totalMethods : 0;
                    
                    
                    
         
                    
                    DIT = dit;
                    totalmethods = totalMethods;
                    explictmethods = declared.size();
                    ormethods = overrideCount;
                    olmethods = overloadCount;
                    orratio = redefRatio;
                    olration = surchargeRatio;
                    orwithnoano = compteurOverrideSansAnnotation;
                    mtconstmethods = compteurOverrideVide;
                    excesiveol = surchargeExcessive;
                    ambgmethods = surchargeAmbigue;
                    
                    
                    
                } catch (Exception e) {
                    System.err.println("Erreur de résolution de classe : " + e.getMessage());
                    e.printStackTrace();
                }
            });
        
    }
    
    /**
     * Vérifie si les paramètres d'une méthode déclarée sont compatibles avec une signature héritée
     * @param methodDecl La déclaration de méthode à vérifier
     * @param inheritedSignature La signature de la méthode héritée
     * @return true si les paramètres sont compatibles pour une redéfinition
     */
    public static boolean haveCompatibleParameters(MethodDeclaration methodDecl, String inheritedSignature) {
        // Extraction des paramètres de la signature héritée
        int start = inheritedSignature.indexOf('(');
        int end = inheritedSignature.indexOf(')');
        
        if (start < 0 || end < 0) {
            return false;
        }
        
        String paramsInherited = inheritedSignature.substring(start + 1, end);
        
        // Compte les paramètres de la méthode héritée
        int inheritedParamCount = paramsInherited.isEmpty() ? 0 : paramsInherited.split(",").length;
        
        // Compte les paramètres de la méthode déclarée
        int declaredParamCount = methodDecl.getParameters().size();
        
        // Pour une redéfinition, le nombre de paramètres doit être le même
        return inheritedParamCount == declaredParamCount;
    }
    
    /**
     * Détermine si deux signatures de méthodes présentent une ambiguïté potentielle lors de la surcharge
     * @param sig1 Première signature de méthode
     * @param sig2 Deuxième signature de méthode
     * @return true si les signatures sont potentiellement ambiguës
     */
    public static boolean sontSurchargesAmbigues(String sig1, String sig2) {
        // Extraction du nom et des paramètres
        String name1 = sig1.substring(0, sig1.indexOf('('));
        String name2 = sig2.substring(0, sig2.indexOf('('));
        
        // Les noms doivent être identiques pour une surcharge
        if (!name1.equals(name2)) {
            return false;
        }
        
        // Extraction des paramètres (entre parenthèses)
        String params1 = sig1.substring(sig1.indexOf('(') + 1, sig1.indexOf(')'));
        String params2 = sig2.substring(sig2.indexOf('(') + 1, sig2.indexOf(')'));
        
        // Diviser les paramètres en liste
        String[] paramList1 = params1.isEmpty() ? new String[0] : params1.split(",");
        String[] paramList2 = params2.isEmpty() ? new String[0] : params2.split(",");
        
        // Les méthodes doivent avoir le même nombre de paramètres pour être ambiguës
        if (paramList1.length != paramList2.length) {
            return false;
        }
        
        // Vérifier si les types de paramètres sont similaires mais pas identiques
        boolean differencesTrouvees = false;
        boolean auMoinsUneAmbiguite = false;
        
        for (int i = 0; i < paramList1.length; i++) {
            String param1 = paramList1[i].trim();
            String param2 = paramList2[i].trim();
            
            // Si les paramètres sont identiques, pas d'ambiguïté à ce niveau
            if (param1.equals(param2)) {
                continue;
            }
            
            differencesTrouvees = true;
            
            // Vérifier les cas d'ambiguïté courante
            if (estPaireAmbigue(param1, param2)) {
                auMoinsUneAmbiguite = true;
            }
        }
        
        // Il faut au moins une différence et au moins une ambiguïté pour que ce soit ambigu
        return differencesTrouvees && auMoinsUneAmbiguite;
    }
    
    /**
     * Vérifie si deux types de paramètres peuvent créer une ambiguïté lors de la surcharge
     * @param type1 Premier type
     * @param type2 Deuxième type
     * @return true si les types peuvent créer une ambiguïté
     */
    private static boolean estPaireAmbigue(String type1, String type2) {
        // Cas d'ambiguïté courants en Java
        
        // 1. Types primitifs et leurs wrappers
        if (type1.equals("int") && type2.equals("java.lang.Integer")) return true;
        if (type1.equals("java.lang.Integer") && type2.equals("int")) return true;
        
        if (type1.equals("long") && type2.equals("java.lang.Long")) return true;
        if (type1.equals("java.lang.Long") && type2.equals("long")) return true;
        
        if (type1.equals("double") && type2.equals("java.lang.Double")) return true;
        if (type1.equals("java.lang.Double") && type2.equals("double")) return true;
        
        if (type1.equals("float") && type2.equals("java.lang.Float")) return true;
        if (type1.equals("java.lang.Float") && type2.equals("float")) return true;
        
        if (type1.equals("boolean") && type2.equals("java.lang.Boolean")) return true;
        if (type1.equals("java.lang.Boolean") && type2.equals("boolean")) return true;
        
        if (type1.equals("char") && type2.equals("java.lang.Character")) return true;
        if (type1.equals("java.lang.Character") && type2.equals("char")) return true;
        
        if (type1.equals("byte") && type2.equals("java.lang.Byte")) return true;
        if (type1.equals("java.lang.Byte") && type2.equals("byte")) return true;
        
        if (type1.equals("short") && type2.equals("java.lang.Short")) return true;
        if (type1.equals("java.lang.Short") && type2.equals("short")) return true;
        
        // 2. Types numériques primitifs proches
        if (type1.equals("int") && type2.equals("long")) return true;
        if (type1.equals("long") && type2.equals("int")) return true;
        
        if (type1.equals("float") && type2.equals("double")) return true;
        if (type1.equals("double") && type2.equals("float")) return true;
        
        if (type1.equals("byte") && type2.equals("short")) return true;
        if (type1.equals("short") && type2.equals("byte")) return true;
        
        if (type1.equals("char") && type2.equals("int")) return true;
        if (type1.equals("int") && type2.equals("char")) return true;
        
        // 3. Object et String (cas courant d'ambiguïté)
        if (type1.equals("java.lang.Object") && type2.equals("java.lang.String")) return true;
        if (type1.equals("java.lang.String") && type2.equals("java.lang.Object")) return true;
        
        // 4. Types primitifs vs Object
        if (type1.equals("java.lang.Object") && estTypePrimitif(type2)) return true;
        if (estTypePrimitif(type1) && type2.equals("java.lang.Object")) return true;
        
        // 5. Tableaux de types similaires
        if (type1.endsWith("[]") && type2.endsWith("[]")) {
            String baseType1 = type1.substring(0, type1.length() - 2);
            String baseType2 = type2.substring(0, type2.length() - 2);
            return estPaireAmbigue(baseType1, baseType2);
        }
        
        return false;
    }
    
    /**
     * Vérifie si un type est un type primitif Java
     * @param type Le type à vérifier
     * @return true si c'est un type primitif
     */
    private static boolean estTypePrimitif(String type) {
        return type.equals("int") || type.equals("long") || type.equals("double") || 
               type.equals("float") || type.equals("boolean") || type.equals("char") || 
               type.equals("byte") || type.equals("short");
    }


 // Ajout pour détecter l'utilisation de super dans les méthodes redéfinies
    public static boolean detectsSuperCall(MethodDeclaration method) {
        if (!method.getBody().isPresent()) {
            return false;
        }
        
        // Recherche des appels à super.methodeActuelle() dans le corps de la méthode
        String methodName = method.getNameAsString();
        return method.getBody().get().findAll(com.github.javaparser.ast.expr.MethodCallExpr.class).stream()
            .anyMatch(call -> call.getScope().isPresent() 
                     && call.getScope().get().toString().equals("super") 
                     && call.getNameAsString().equals(methodName));
    }

    /**
     * Calcule le taux d'extension cohérente (méthodes redéfinies qui appellent super)
     * @param methodDeclarations Liste des méthodes à analyser
     * @param inheritedMethodMap Map des méthodes héritées pour déterminer les redéfinitions
     * @return Pourcentage de méthodes redéfinies qui appellent super
     */
    public static float calculateCoherentExtensionRate(List<MethodDeclaration> methodDeclarations, Map<String, List<String>> inheritedMethodMap) {
        int overrideMethods = 0;
        int superCallMethods = 0;
        
        for (MethodDeclaration method : methodDeclarations) {
            if (isMethodOverriding1(method, inheritedMethodMap)) {
                overrideMethods++;
                
                if (detectsSuperCall(method)) {
                    superCallMethods++;
                }
            }
        }
        
        return overrideMethods > 0 ? (float) superCallMethods / overrideMethods : 0;
    }

  

    /**
     * Calcule un score de risque de confusion override/overload
     * @return Pourcentage de méthodes présentant une confusion override/overload
     */
    public static float calculateOverrideOverloadConfusionRate(List<MethodDeclaration> methods, Map<String, List<String>> inheritedMethodMap) {
        Map<String, Integer> methodNameCount = new HashMap<>();
        Set<String> overriddenMethods = new HashSet<>();
        Set<String> confusionMethods = new HashSet<>();
        
        // Identifier les méthodes redéfinies et compter les occurrences par nom
        for (MethodDeclaration method : methods) {
            String methodName = method.getNameAsString();
            methodNameCount.put(methodName, methodNameCount.getOrDefault(methodName, 0) + 1);
            
            if (isMethodOverriding1(method, inheritedMethodMap)) {
                overriddenMethods.add(methodName);
            }
        }
        
        // Détecter les méthodes avec confusion
        for (String methodName : overriddenMethods) {
            if (methodNameCount.getOrDefault(methodName, 0) > 1) {
                confusionMethods.add(methodName);
            }
        }
        
        // Calculer le taux de confusion
        return overriddenMethods.isEmpty() ? 0 : (float) confusionMethods.size() / overriddenMethods.size();
    }

 

    // Méthode principale pour calculer le taux d'extension cohérente et détecter les confusions override/overload
    public static void analyzeAdvancedOverrideMetrics(File file,HashSet<File> jarfilesinm2) throws IOException {
        // Récupérer toutes les déclarations de méthodes
    	//CombinedTypeSolver typeSolver = buildTypeSolver(file);
        
        //JavaSymbolSolver symbolSolver = new JavaSymbolSolver(typeSolver);
        
        
    	CombinedTypeSolver typeSolver = Utility.getCombinedTypeSolver(file.getAbsolutePath(),jarfilesinm2);
        CompilationUnit cu = Utility.getCompilationUnit(file.toPath().toString(), typeSolver);
    	
        List<MethodDeclaration> methodDeclarations = cu.findAll(MethodDeclaration.class);
        

        
        // Pour chaque type déclaré dans le fichier
        cu.getTypes().forEach(typeDecl -> {
            try {
                // Résoudre la classe
                ResolvedReferenceTypeDeclaration resolvedClass = typeDecl.resolve();
                
                // Collecter les méthodes héritées
                Map<String, List<String>> inheritedMethodMap = collectInheritedMethodsAdvanced(resolvedClass);
                
                // 1. Calculer le taux d'extension cohérente
                float coherentExtensionRate = calculateCoherentExtensionRate(methodDeclarations, inheritedMethodMap);
                orwithsuper = coherentExtensionRate;
               
                
                // 2. Détecter les confusions override/overload
                //detectOverrideOverloadConfusion(methodDeclarations, inheritedMethodMap);
                float confusionRate = calculateOverrideOverloadConfusionRate(methodDeclarations, inheritedMethodMap);
                olandor = confusionRate;
           
                
            } catch (Exception e) {
                System.err.println("Erreur lors de l'analyse avancée des métriques: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }


    
    public static void main(String[] args) {
       

        try {
        	String path = "C:\\Users\\aicha\\Videos\\projet_pluri\\Chart\\Bugs\\buggy_1\\source\\org\\jfree\\chart\\renderer\\category\\AbstractCategoryItemRenderer.java";
            File javaFile = new File(path);
            HashSet<File> jarfilesinm2 = Utility.getJarsFromM2(path);
            // Analyse directe sans avoir besoin de spécifier la racine source
            MethodCollector mc = new MethodCollector(javaFile,jarfilesinm2);
            System.out.println("total: " + mc.getTotalMethods());
            System.out.println("DIT: " + mc.getDIT());
            System.out.println("Explict Methods: " + mc.getExplictmethods());
            System.out.println("OR Methods: " + mc.getOrmethods());
            System.out.println("OL Methods: " + mc.getOlmethods());
            System.out.println("OR Ratio: " + mc.getOrratio());
            System.out.println("OL Ration: " + mc.getOlration());
            System.out.println("OR with No Annotation: " + mc.getOrwithnoano());
            System.out.println("MT Const Methods: " + mc.getMtconstmethods());
            System.out.println("Excessive OL: " + mc.getExcesiveol());
            System.out.println("Ambiguous Methods: " + mc.getAmbgmethods());
            System.out.println("OL and OR: " + mc.getOlandor());
            System.out.println("OR with Super: " + mc.getOrwithsuper());
        } catch (IOException e) {
            System.err.println("Erreur lors de l'analyse : ");
            e.printStackTrace();
        }
    }
}