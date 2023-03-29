<?php
include_once "conexion.php";

http_response_code(500);

function insert_coordinador(
  mysqli $mysql,
  string $nombres,
  string $apellidos,
  string $fechaNac,
  string $titulo,
  string $email,
  string $facultad,
) {
  http_response_code(400);
  $query =
    "INSERT INTO coordinador (nombres, apellidos, fechaNac, titulo, email, facultad) VALUES(?, ?, ?, ?, ?, ?);";

  $result = $mysql->execute_query($query, [
    $nombres,
    $apellidos,
    $fechaNac,
    $titulo,
    $email,
    $facultad,
  ]);

  if ($result) {
    http_response_code(200);
    echo "Registro guardado";
  } else {
    echo "Error al guardar el registro: {$mysql->error}";
  }
}

if ($_SERVER["REQUEST_METHOD"] == "POST") {
  $conn = Connection::get_instance();
  $mysql = $conn->connect();

  $nombres = $_POST["nombres"];
  $apellidos = $_POST["apellidos"];
  $fechaNac = $_POST["fechaNac"];
  $titulo = $_POST["titulo"];
  $email = $_POST["email"];
  $facultad = $_POST["facultad"];

  insert_coordinador(
    $mysql,
    $nombres,
    $apellidos,
    $fechaNac,
    $titulo,
    $email,
    $facultad,
  );

  $conn->disconnect();
} else {
  http_response_code(400);
  echo "No se realizó una solicitud POST";
}
