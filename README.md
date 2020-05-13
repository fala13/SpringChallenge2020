CLI referee for https://www.codingame.com/contests/spring-challenge-2020/ aimed to work with Brutaltester (https://github.com/dreignier/cg-brutaltester)

# Compiled
Get it here: https://github.com/fala13/SpringChallenge2020/releases

# To compile
mvn package

# Example use
java --add-opens java.base/java.lang=ALL-UNNAMED -jar ~/codingame/pacs-runner/SpringChallenge2020/target/spring-2020-1.0-SNAPSHOT.jar -p1 pacsBin1 -p2 pacsBin2 -l gamelog1.json

Where "pacsBin1", "pacsBin2" are the commands to run your player programs.

With cg-brutaltester:
time java -jar cg-brutaltester-1.0.0-SNAPSHOT.jar -r "java --add-opens java.base/java.lang=ALL-UNNAMED -jar spring-2020-1.0-SNAPSHOT.jar" -p1 "pacsBin1" -p2 "pacsBin2" -t 4 -n 10 -l "./logs/" -v

# Ramblings
Took me few hours to get through java issues this time. I have no clue about
maven and java and but surprisingly no one else shares local runners and I
don't like to commit to contest without testing framework ;)
