create table game
(
    id varchar(36) not null primary key,
    nextPlayer int null,
    gameOver boolean not null
);
create table pit
(
    gameId varchar(36) not null,
    pitId int not null,
    size int not null,
    primary key(gameId, pitId),
    foreign key (gameId) references game(id) on delete cascade
);
