# GPX2KML

## Changelog
### 2.0
- исправлено отображение символа для типа записи "комментарий" в блокноте тайника
- добавлен выбор способа вывода результата конвертации:
  - KMZ - заархивированный KML, значительно уменьшает размер выходного файла (по умолчанию)
  - KML - KML файл
  - STDOUT - вывод в стандартный поток вывода (консоль)
- добавлен выбор типа генерируемых ссылок для координат (см. параметр -glt):
  - **GE0** - Organic Maps, Maps.me (по умолчанию)
  - **OM** - Organic Maps
  - **OMAPS** - omaps.app
  - **YM** - Яндекс Карты
  - **GM** - Google Maps
- Перед описанием тайника выводятся ссылки для открытия точки тайника в других приложениях для навигации
- если имя выходного файла не задано, используется то же имя, что и у исходного файла, с расширением .kml или .kmz (в зависимости от типа вывода результата)
### 1.1
- обновил README и JAR актуального релиза
### 1.0
- отображение ссылки на страницу тайника на сайте geocaching.su
- отображение атрибутов тайника
- отображение записей блокнота тайника
- замена изображений на ссылки в описании тайника
- гиперссылка на фотоальбом тайника
- замена координат WGS84 на гиперссылки, открываемые в Organic Maps (в описании и записях блокнота)
- возможность указания имени коллекции точек в генерируемом KML

## Краткое описание

Конвертер для преобразования [GPX](https://ru.wikipedia.org/wiki/GPX)-файлов, выгружаемых через сервис игры [geocaching.su](https://geocaching.su), 
в формат [KML](https://ru.wikipedia.org/wiki/KML), поддерживаемый такими мобильными приложениями как [Maps.me](https://ru.maps.me) 
или [Organic Maps](https://organicmaps.app)

- Удобное именование выгруженных точек (тайников)
- Краткая информация о тайнике в начале описания
- Замена фотографий тайника и местности на гиперссылки
- Отображение записей блокнота тайника
- Преобразование координат WGS84, встреченных в описании и записях блокнота, в гиперссылки вида om://_5XngVwo9D (поддерживаются всё теми же Maps.me и Organic Maps)

| Коллекция выгруженных точек                                                                | Точка тайника на карте                                                                                             |
|---------------------------------------------------------------------------------------------------|----------------------------------------------------------------------------------------------|
| ![Коллекция выгруженных точек](/webres/Screenshot_2023-01-08-20-53-43-239_app.organicmaps.jpg)    | ![Точка тайника на карте](/webres/Screenshot_2023-01-08-20-56-59-204_app.organicmaps.jpg)    |
| Ссылки на фотографии в описании                                                             | Записи в блокноте тайника                                                                                            |
| ![Ссылки на фотографии в описании](/webres/Screenshot_2023-01-08-20-54-38-197_app.organicmaps.jpg) | ![Записи в блокноте тайника](/webres/Screenshot_2023-01-08-20-56-30-197_app.organicmaps.jpg) |

## Сборка исполняемого JAR
Для самостоятельной сборки исполняемого jar-файла вам понадобится [Java Development Kit не ниже 8-й версии](https://www.oracle.com/cis/java/technologies/downloads/)

Windows:

    .\mvnw.cmd clean package

*nix:

    ./mvnw clean package

После успешного выполнения сборки в директории _target_ появится файл **gpx2kml-*.jar**

## Запуск конвертации
[Скачать последний релиз](https://github.com/golubevda/gpx2kml/raw/main/webres/gpx2kml-1.1.jar) или собрать исполняемый jar из исходного кода самостоятельно

Для запуска вам понадобится [Java не ниже 8](https://www.java.com/ru/download)

Перед запуском необходимо выгрузить интересующие вас тайники на [карте](https://geocaching.su/map) в формате GPX.
Для этого нужно переместиться на карте в интересующую вас область с тайниками, в панели фильтров
кликнуть иконку <img src="https://geocaching.su/images/icons/points_ico.gif"> (вы должны быть авторизованы на сайте).
Далее выбрать опцию "GPX для Garmin Colorado & Oregon (gpx)" и кликнуть "Загрузить файл".

Запуск конвертации:

    java -jar gpx2kml.jar путь_до_файла_gpx

При успешном выполнении в текущей директории будет создан файл с расширением kml

Для получения информации по параметрам запуска:

    java -jar gpx2kml.jar -help

## Импорт закладок в Maps.me/Organic Maps
Открыть полученный KML-файл на смартфоне с помощью одной из вышеперечисленных программ.
Перейти на вкладку "Метки" и убедиться, что коллекция меток успешно импортирована.

## Решение проблем
### В интерпретаторе командной строки Windows некорректно отображаются кириллические символы
Проблема возникает из-за того, что по умолчанию интерпретатор командной строки Windows (cmd.exe) использует кодовую 
страницу 866 для отображения кириллических символов. Т.к. программа выводит текст в кодировке UTF-8, необходимо перед 
запуском конвертации переключить кодовую страницу на 65001, выполнив следующую команду:

    chcp 65001

