CREATE EXTENTION IF NOT EXISTS "pgcrypto";

CREATE TABLE usuario (
    id UUID get_random_uuid() PRIMARY KEY,
    nome VARCHAR(250) NOT NULL,
    cpf VARCHAR(11) NOT NULL,
    dataNascimento DATE NOT NULL
);