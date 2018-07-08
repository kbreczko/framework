# framework

##### Opis:
Implementacja prostegu frameworku do wstrzykiwania zależności i przykład użycia.

Framework obsługuje:
- [x] wstrzykiwanie zależności przez adnotacje `@Inject`
- [x] dostarczanie źródła definicji beanów (metody oznaczone adnotacją `@Bean`)
- [x] wyszukiwanie komponentów oznaczonych adnotacją (`@Repository`, `@Service`) w celu stworzenia beana
- [x] pobieranie beanów za pomocą metody `getBean(Class<?> clazz)`
- [x] wszystkie utworzone beany posiadają zasięg `Singleton`

Ograniczenia:
- [x] klasa zawierająca źródło beanów oraz komponenty powinny posiadać konstruktor 0 argumentowy

Tworzenie beana:
- Wyszukiwanie - jeśli bean został wcześniej stworzony to zwracana jest instancja utworzonego obiektu
- Instancjonowanie – znajduje definicję beanu i instancjonuje bean (@Bean, @Repository, @Service)
- Wypełnianie właściwości – używając wstrzykiwania zależności, uzupełnia wszystkie właściwości określone za pomocą @Inject (w przypadku braku zależności wstrzykuje `null`)



###### Adnotacje:

|Adnotacja       |Użycie  |Opis                         												
|----------------|--------|---------------------------------------------------------------------------
|`@Inject`		 |Pole    |Wstrzykiwanie instancji klasy implementującej dany interfejs/klase 		
|`@Repository `  |Klasa   |Framework wie, że z danej klasy musi utworzyć beana i wstrzyknąć zależności
|`@Service`      |Klasa   |Podobnie jak adnotacja `@Repository`   
|`@Bean`         |Metoda  |Metoda opatrzona `@Bean` zwróci obiekt, który powinien być zarejestrowany jako Bean w kontekście aplikacji
|`@Transactional`|Metoda  |Metoda oznacza transakcje, którą zarządza menadżer transakcji		


