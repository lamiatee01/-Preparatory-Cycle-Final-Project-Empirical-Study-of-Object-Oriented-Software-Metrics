Defects4J Metrics Analyzer
This Java application analyzes Defects4J projects and generates CSV files containing software metrics. It includes logic to classify and evaluate Java exceptions.

Requirements
To run this application, ensure the following are installed and configured:

Java 11 or higher

Apache Maven, added to your system’s PATH variable

Usage
Clone or download the project.

Ensure all requirements are met.

Run the main class in the defectsUtils package to generate the CSV files.

Note about exception Classification
Only JDK exceptions are classified because they follow a consistent and well-documented hierarchy:

Exception (excluding RuntimeException) → Checked

RuntimeException → Runtime

Error → Error

Custom exceptions are excluded, as their intent and behavior are not always clear or consistent from static analysis alone.