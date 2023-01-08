# GPX2KML

Конвертер для преобразования [GPX](https://ru.wikipedia.org/wiki/GPX)-файлов, выгружаемых через сервис игры [geocaching.su](https://geocaching.su), 
формат [KML](https://ru.wikipedia.org/wiki/KML), поддерживаемый такими мобильными приложениями как [Maps.me](https://ru.maps.me) 
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

## Сборка исполняемого JAR (TODO)

## Запуск конвертации
[Скачать последний релиз](/webres/gpx2kml-1.0-SNAPSHOT.jar) или собрать исполняемый jar из исходного кода самостоятельно

Для запуска вам понадобится [Java не ниже 11](https://www.java.com/ru/download)

Перед запуском необходимо выгрузить интересующие вас тайники на [карте](https://geocaching.su/map) в формате GPX.
Для этого нужно переместиться на карте в интересующую вас область с тайниками, в панели фильтров
кликнуть иконку <img src="https://geocaching.su/images/icons/points_ico.gif"> (вы должны быть авторизованы на сайте).
Далее выбрать опцию "GPX для Garmin Colorado & Oregon (gpx)" и кликнуть "Загрузить файл".

Запуск конвертации:

    java -jar gpx2kml.jar путь_до_файла_gpx

При успешном выполнении в текущей директории будет создан файл с расширением kml

Для получения информации по параметрам запуска:

    java -jar gpx2kml.jar -help