# KVVArtet-09-2017

## Состав

- Иващенко Вениамин [@WorldVirus](https://github.com/WorldVirus) фронтенд
- Орлов Владислав [@torrentino555](https://github.com/torrentino555) фронтенд/графика
- Парпибаев Артур [@zonder129](https://github.com/zonder129) API/DB/авторизация на бекенде, логика одиночной игры на фронтенде
- Черкасов Кирилл [@KCherkasov](https://github.com/KCherkasov) логика игры на бекенде/ресурсы

## Frontend: https://github.com/frontend-park-mail-ru/2017_2_KVVArtet

## Production: https://landsanddungeons.ru.com/

## Жанр: action-RPG

## Описание

Dungeon crawler. Город с вендорами выступает в качестве лобби, где игроки могут объединиться в группу для прохождения инстансов. Инстансы имеют различную сложность, что отражается в силе обитающих там монстров и наградах за прохождение. Геймплей походовый (в духе серии Shadowrun и ее MMO-версии Boston Lockdown).

Сингл-плеер представляет из себя оффлайновый hotseat-режим для двух игроков, один из которых играет за наших героев, второй за случайных монстров.

Мультиплеерный режим в разработке.

Контент (инстансы, квесты, монстры и экипировка) процедурно генерируемы.

## API

Документация на API в файле [swagger.yml](swagger.yml).

Для просмотра скопируйте его в [онлайн-редактор Swagger](http://editor.swagger.io/#).

## Howto run backend

sudo apt-get install postgresql

psql -U postgres

//create password

CREATE DATABASE landd

CREATE USER landd WITH ENCRYPTED PASSWORD 'landd'

GRANT ALL TO landd ON landd

quit

git clone https://github.com/zonder129/KVVArtet-09-2017/

cd KVVArtet-09-2017/

sudo apt-get install maven

mvn compile

mvn install

java -jar /target/KVVArtet-1.0-SNAPSHOT.jar
