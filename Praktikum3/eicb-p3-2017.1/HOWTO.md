# Anleitung für das dritte Praktikum
**Einführung in den Compilerbau, Wintersemester 2017/18**

## Voraussetzungen

* Eine Java-1.8-kompatible virtuelle Maschine.

## Einrichtung

Für die **Ersteinrichtung** benötigen Sie eine Internetverbindung.

Dieses Projekt verwendet [Gradle 3.1](https://docs.gradle.org/3.1/userguide/userguide.html) als Buildwerkzeug. Falls Gradle nicht auf Ihrem System verfügbar ist, können Sie die "Gradle Wrapper" genannten Skripte `gradlew` (Linux und macOS) bzw. `gradlew.bat` (Windows) anstelle des hier in der Anleitung verwendeten `gradle`-Befehls verwenden.

Führen Sie bitte folgendes Kommando aus:

	$ gradle mavlc interpreter
	
Falls Sie den Gradle Wrapper benutzen wollen, würden Sie stattdessen folgende Kommandos verwenden:

	$ ./gradlew mavlc interpreter # Linux und macOS
	$ gradlew.bat mavlc interpreter

Dies lädt die vom Projekt benötigten Bibliotheken herunter und erstellt im Verzeichnis `build/` die Startskripte `mavlc` (für Linux und macOS) und `mavlc.bat` (für Windows) für den von Ihnen zu entwickelnden MAVL-Compiler, sowie entsprechende Skripte für den MTAM-Interpreter (s.u.).

Wenn Sie Eclipse zur Entwicklung verwenden möchten, können Sie mittels

	$ gradle eclipse

ein Eclipse-Projekt erzeugen, welches Sie anschließend in einen beliebigen Eclipse-Workspace importieren können.

## Entwickeln und Testen

Während der Entwicklung können Sie die Übersetzung der Quellen mit

	$ gradle classes

starten. Dies übersetzt nur die geänderten Klassen. Wenn Sie einen komplette Neuübersetzung anstoßen möchten, verwenden Sie:

	$ gradle clean

Um den Compiler auszuführen, nutzen Sie das zu Ihrem System passende Startskript (hier gezeigt für Linux/macOS, unter Windows verwenden Sie stattdessen `build\mavlc.bat`):

	$ build/mavlc helloworld.mavl

Dies erzeugt die Datei Datei `a.tam`, die den erzeugten Binärcode erhält. Die Datei können Sie mithilfe des MTAM-Interpreters ausführen (s.u.). Um eine andere Ausgabedatei anzugeben, verwenden Sie die Option `-o`:

	$ build/mavlc -o helloworld.tam helloworld.mavl

Sie können den AST auch in eine graphische Darstellung im [Graphviz DOT-Format](http://graphviz.org) exportieren:

	$ build/mavlc -dot context helloworld.mavl

Wenn Sie die Graphviz-Werkzeuge auf Ihrem System installiert haben, können Sie beispielsweise aus der DOT-Datei ein PDF erzeugen:

	$ dot -Tpdf -o helloworld.pdf helloworld_decorated_ast.dot

Dieses Projekt enthält die öffentliche Testfälle der Praktikumsaufgaben, die Sie mittels

	$ gradle test

ausführen können. Das Kommando gibt nur eine Zusammenfassung auf die Konsole aus; den detaillierten Testreport finden Sie in der Datei `build/reports/tests/test/index.html`.

### Interpreter ###

Um den von Ihrem Compiler erzeugten Binärcode auf der MTAM auszuführen, nutzen Sie das zu Ihrem System passende Startskript (hier gezeigt für Linux/macOS, unter Windows verwenden Sie stattdessen `build\interpreter.bat`):

	$ build/interpreter -i helloworld.tam

Zum Debuggen Ihres erzeugten Binärcodes verfügt der Interpreter über zusätzliche Debug-Optionen, deren Verwendung und Bedeutung Sie sich mithilfe der `-h`-Option anzeigen lassen können:

	$ build/interpreter -h
	

## Abgabe

Mit

	$ gradle prepareSubmission

erstellen Sie ein Archiv, welches Sie anschließend über den Moodle-Kurs abgeben können.

## Bekannte Probleme

* Unter Windows funktioniert das Startskript `mavlc.bat` nicht, wenn der Projektpfad nicht-ASCII-Zeichen (also insbesondere Umlaute) enthält.
