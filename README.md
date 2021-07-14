# Smarter Reader für RSS-Feeds
## ✨_Feedify_✨
![Feedify](RSS-Feed-Reader\src\main\resources\static\assets\images\readmeImages\logo.png)

1. [Einleitung und Motivation](#Einleitung-und-Motivation)
2. [Deskription](#Deskription)
3. [User Interface](#User-Interface)
4. [Use cases/ User Stories](#Use-Cases)
5. [Installation]
6. [Technologien](#Technologien)
6. [Maintainers]

# Einleitung und Motivation
Immer mehr Webseiten veröffentlichen Inhalte(Artikel) zum Lesen. Diese Inhalte sind öfter in einer 
Xml-Datei zusammengefasst, die die wichtigen Informationen zu den veröffentlichten Artikeln enthalten. 
Dieses etabliertes Dateiformat nennt man **RSS**(Really Simple Syndication) und die enthaltenen 
Artikel **feeds**. Die Motivation des Projektes ist es, die RSS-Feeds verschiedenen Webseiten zu 
abonnieren und verwalten zu können, aber aus einem Endpunkt.

# Deskription
Beim Projekt Smarter Reader für RSS-Feeds geht es darum, Rss-Feeds von verschiedenen Webseiten zu
abonnieren und zu verwalten. Mit unserer Webapplikation sollte der User in der Lage sein,
die abonnierten Feeds in Kategorien einzuordnen. Die dargestellten Feeds kann man auch sortieren 
je nach bestimmten Sortierkriterien. Die API sorgt dafür, dass die Feeds nach jeder Stunde aktualisiert 
werden, damit neue Feeds-Veröffentlichungen auch in der Applikation vorhanden sind. Es besteht auch die
Möglichkeit zur inhaltsbasierten Suche in den Feeds. Ein Nutzer kann ein Feed als wichtig kennzeichnen
und auch für ein späteres Lesen markieren.

# Use Cases
Ein nutzer kann:
1. Ein konto erstellen und verifizieren
2. Sein konto verwalten
3. RSS-Feed abonnieren
4. RSS-Feed in Kategorien einordnen
5. Rss-Feeds nach Datum sortieren
6. In den RSS-Feeds suchen
7. RSS-Feeds als Wichtig markieren
8. RSS-Feeds fürs spätere Lesen markieren
9. RSS-Feeds nach Relevanz sortieren
10. Vom Feeds aus in die Webseite geleitet werden

# User Interface
## User Interests
Beim Starten der Applikation wird nach der Interessen des Users gefragt.
Diese benutzen wir, um die Feeds nach der User-Interessen anzuzeigen. 
![](userInterest.png)

## Login 
Um sich in der Application einzuloggen, braucht der User ein Konto. Dies kann er eichfach erstellen,
idem er seinen Namen, Vornamen, Nachnamen, Usernamen, Passwort und seine E-Mail eingibt.
![](signIn.png)

Nachdem das Konto erstellen wurde, kann sich der User mit seinen Namen und Passwort einloggen.
![](login.png)

## kanal Abonnieren
Um einen Kanal zu abonnieren, gibt der User einfach die URL des RSS-FEEDS. Wenn diese eine valide RSS-Feeds 
URL ist, wählt der User eine Kategorie, in der er die Feeds einordnen möchte.
![](channel.png)

## Homepage
Auf der Homepage bekommt der User alle Feeds der abonnierten Kanälen und kann sie dann nach seinem wunsch 
sortieren oder darstellen.
![](homepage.png)


# Technologien
- Springboot
- Postgresql
- java
- HTML
- CSS
- Jquery