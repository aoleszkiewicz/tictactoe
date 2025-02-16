::mo¿na wskazaæ prywatne JRE, np. z udostêpnionego pakietu
set JAVA_HOME=..\..\..\eclipse\plugins\org.eclipse.justj.openjdk.hotspot.jre.full.win32.x86_64_17.0.4.v20220903-1038\jre\bin
set path=%JAVA_HOME%
::mo¿na te¿ wskazaæ katalog z plikiem JAR, je¿eli plik BAT jest przechowywany w innym miejscu np.
::cd C:\eclipse-2018-09\workspace\DbApp\lib
java -jar hsqldb-2.6.0.jar