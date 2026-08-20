CREATE DATABASE IF NOT EXISTS adivina_quien CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE adivina_quien;

DROP TABLE IF EXISTS adivina_quien.partidas;
DROP TABLE IF EXISTS adivina_quien.preguntas;
DROP TABLE IF EXISTS adivina_quien.personajes;

CREATE TABLE adivina_quien.personajes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    rutaImagen VARCHAR(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE adivina_quien.preguntas (
    id INT AUTO_INCREMENT PRIMARY KEY,
    texto VARCHAR(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE adivina_quien.partidas (
    id INT AUTO_INCREMENT PRIMARY KEY,
    jugador1 VARCHAR(100) NOT NULL,
    jugador2 VARCHAR(100) NOT NULL,
    ganador VARCHAR(100) NOT NULL,
    personaje_ganador VARCHAR(100) NOT NULL,
    fecha DATE NOT NULL,
    duracion TIME NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO adivina_quien.personajes (id, nombre, rutaImagen) VALUES
(1, 'Bowser', 'assets/iconos/Bowser.png'),
(2, 'Bowser Jr.', 'assets/iconos/BowserJr.png'),
(3, 'Captain Falcon', 'assets/iconos/CaptainFalcon.png'),
(4, 'Charizard', 'assets/iconos/Charizard.png'),
(5, 'Daisy', 'assets/iconos/Daisy.png'),
(6, 'Diddy Kong', 'assets/iconos/DiddyKong.png'),
(7, 'Donkey Kong', 'assets/iconos/DonkeyKong.png'),
(8, 'Dr. Mario', 'assets/iconos/DrMario.png'),
(9, 'Fox', 'assets/iconos/Fox.png'),
(10, 'Ganondorf', 'assets/iconos/Ganondorf.png'),
(11, 'Ice Climbers', 'assets/iconos/IceClimbers.png'),
(12, 'Isabelle', 'assets/iconos/Isabelle.png'),
(13, 'Jigglypuff', 'assets/iconos/Jigglypuff.png'),
(14, 'Ken', 'assets/iconos/Ken.png'),
(15, 'King Dedede', 'assets/iconos/KingDedede.png'),
(16, 'King K. Rool', 'assets/iconos/KingKRool.png'),
(17, 'Kirby', 'assets/iconos/Kirby.png'),
(18, 'Link', 'assets/iconos/Link.png'),
(19, 'Luigi', 'assets/iconos/Luigi.png'),
(20, 'Mario', 'assets/iconos/Mario.png'),
(21, 'Mega Man', 'assets/iconos/MegaMan.png'),
(22, 'Meta Knight', 'assets/iconos/MetaKnight.png'),
(23, 'Ness', 'assets/iconos/Ness.png'),
(24, 'Pac-Man', 'assets/iconos/Pac-Man.png'),
(25, 'Peach', 'assets/iconos/Peach.png'),
(26, 'Pikachu', 'assets/iconos/Pikachu.png'),
(27, 'Piranha Plant', 'assets/iconos/PiranhaPlant.png'),
(28, 'Pit', 'assets/iconos/PitIcon.png'),
(29, 'Ryu', 'assets/iconos/Ryu.png'),
(30, 'Samus', 'assets/iconos/Samus.png'),
(31, 'Snake', 'assets/iconos/Snake.png'),
(32, 'Sonic', 'assets/iconos/Sonic.png'),
(33, 'Sora', 'assets/iconos/Sora.png'),
(34, 'Steve', 'assets/iconos/Steve.png'),
(35, 'Terry', 'assets/iconos/Terry.png'),
(36, 'Toon Link', 'assets/iconos/ToonLink.png'),
(37, 'Wario', 'assets/iconos/Wario.png'),
(38, 'Yoshi', 'assets/iconos/Yoshi.png'),
(39, 'Young Link', 'assets/iconos/YoungLink.png'),
(40, 'Zelda', 'assets/iconos/Zelda.png');

INSERT INTO adivina_quien.preguntas (texto) VALUES
('Es de genero femenino?'),
('Es un humano?'),
('Es un animal o criatura?'),
('Pertenece al universo de Mario Bros?'),
('Pertenece al universo de Pokemon?'),
('Pertenece a The Legend of Zelda?'),
('Lleva sombrero, gorra o casco?'),
('Usa una espada o arma blanca?'),
('Tiene bigote?'),
('Lleva guantes?'),
('Tiene pelo o pelaje amarillo o dorado?'),
('Tiene pelaje o ropa de color verde?'),
('Tiene pelaje o ropa de color rojo?'),
('Tiene pelaje o ropa de color azul?'),
('Tiene pelaje o ropa de color rosa?'),
('Tiene alas o puede volar o levitar?'),
('Es un villano o antagonista?'),
('Lleva armadura metalica?'),
('Usa armas de fuego o proyectiles?'),
('Es de tamano pequeno o infantil?');