javac CreateCompetition.java
javac SetUpCompetition.java

java CreateCompetition "Mock Mock Competition" | mysql -u root -p > out.txt
java SetUpCompetition < out.txt | mysql -u root -p

rm out.txt
rm *.class
