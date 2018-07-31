# framework

##### Opis:
Implementacja prostegu frameworku do wstrzykiwania zależności i przykład użycia.

Framework obsługuje:
- [x] wstrzykiwanie zależności
- [x] dostarczanie źródła definicji beanów (metody oznaczone adnotacją `@Bean`)
- [x] wyszukiwanie komponentów oznaczonych adnotacją (`@Repository`, `@Service`, `@Component`) w celu stworzenia beana
- [x] pobieranie beanów za pomocą metody `getBean(Class<?> clazz)`
- [x] wszystkie utworzone beany posiadają zasięg `Singleton`
- [x] zarządzanie transakcjami (możliwe propagacje: `REQUIRED`, `REQUIRES_NEW`)

Ograniczenia:
- [x] klasa zawierająca źródło beanów oraz komponenty powinny posiadać konstruktor zero argumentowy
- [x] wstrzykiwanie zależności tylko przez ustawienie adnotacji `@Inject` nad polem

Tworzenie beana:
- Wyszukiwanie - jeśli bean został wcześniej stworzony to zwracana jest instancja utworzonego obiektu
- Instancjonowanie – znajduje definicję beanu i instancjonuje bean
- Wypełnianie właściwości – używając wstrzykiwania zależności, uzupełnia wszystkie właściwości określone za pomocą @Inject (w przypadku braku beanu zgłasza wyjątek)
- Opakowanie w proxy jeśli jest to potrzebne

Kolejność wyboru typu wstrzykiwania:  
- po typie konkretnej klasy

###### Adnotacje:

|Adnotacja            |Użycie  |Opis                         												
|---------------------|--------|---------------------------------------------------------------------------
|`@Inject`		      |Pole    |Wstrzykiwanie instancji klasy implementującej dany interfejs/klase 		
|`@Component`         |Klasa   |Bazowy stereotyp, oznacza, że na podstawie tej klasy będzie utworzony bean
|`@Repository `       |Klasa   |Wskazuje że klasa pozwala na dostęp do danych
|`@Service`           |Klasa   |Stereotyp który wskazuje, że ta klasa jest serwisem   
|`@Bean`              |Metoda  |Metoda opatrzona `@Bean` zwróci obiekt, który powinien być zarejestrowany jako `Bean` w kontekście aplikacji
|`@PersistenceContext`|Pole    |Wstrzyknięcie proxy, który zapewnia dostarczenie aktualnego `entity manager`
|`@Transactional`     |Metoda  |Metoda opatrzona `@Transactional` oznacza transakcje, którą zarządza menadżer transakcji
|`@ComponentScan`     |Klasa   |Adnotacja dostarcza ścieżke w której należy szukać komponenty		


