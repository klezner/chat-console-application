**Projekt 1a**

Założenia ogólne

1. Projekt realizowany samodzielnie przez wszystkich uczestników kursu
2. Nieprzekraczalny termin oddania projektu to 10.04.2022
3. Stworzony kod powinien być opublikowany na repozytorium git np. GitHub
4. Realizując projekt używamy wyłącznie standardowego **SDK Java 11** tzn. nie korzystamy z frameworków, ani
   zewnętrznych
   bibliotek
6. Ze względu na kolejność realizowanych zajęć testy jednostkowe nie są wymagane, ale mile widziane

Opis aplikacji
Stwórz czat tekstowy/aplikację klient-server wykorzystując Java Sockets. Aplikacja powinna umożliwiać:

- rozmowę wielu osób na kanale grupowym
- rozmowę 2 lub więcej osób na kanale prywatnym
- przesyłanie plików między osobami na danym kanale
- zapamiętywanie historii rozmów po stronie serwera w bazie opartej o plik płaski
- możliwość przeglądania historii rozmów z poziomu klienta (jeśli uczestniczył on w rozmowie/był na kanale)
  Obsługa aplikacji powinna odbywać się z terminala/linii komend (interfejs tekstowy) dla 1a oraz REST API w przypadku
  1b.

Uwaga! Należy zwrócić szczególną uwagę na aspekty związane z wielowątkowością - zapewnić zarówno bezpieczeństwo jak
wydajność całego rozwiązania.

> **Server -> User**
>* online - connected
>* offline - disconnected
>* channel join - channel create and subscribe or switch channel
>* channel leave - channel leave and unsubscribe
>* file upload - upload file to server
>* file download - download file from server
>* server logs
>* chat history - save to file
>* chat history - read from file
>
>**User -> Server**
>* login, logout - ok
>* me - ok
>* users, channel users - ok
>* channels - ok
>* history - ok
>* upload file, download file - ok
>
>**User -> User**
>* channel messages - ok
>* cache channel messages - ok
>* upload file to server and download file - ok

> **Available commands**
>* ***clientMessage***
>* **/help** - help
>* **/me** - about me (username, subscribed channels, active channel)
>* **/users** - get all users connected
>* **/channels** - get all active channels
>* **/history** - get my channel history
>* **/files** - get all server files
>* **/channel /join *channelName*** - create and join new channels or join channel if exists (still subscribed)
>* **/channel /leave** - leave channel (unsubscribe) and delete if there is no clients
>* **/channel /users** - get all channel users (subscribing channel)
>* **/upload *fileName*** - upload file to server
>* **/download *fileName*** - download file from server
>* **/quit** - disconnect server and close client
