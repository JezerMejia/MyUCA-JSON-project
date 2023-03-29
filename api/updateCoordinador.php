<?php
include_once "conexion.php";

http_response_code(500);

function update_coordinador(
  mysqli $mysql,
  int $id,
  string $nombres,
  string $apellidos,
  string $fechaNac,
  string $titulo,
  string $email,
  string $facultad,
) {
  http_response_code(400);
  $query =
    "UPDATE coordinador SET nombres = ?, apellidos = ?, fechaNac = ?, titulo = ?, email = ?, facultad = ? WHERE idC = ?";

  $result = $mysql->execute_query($query, [
    $nombres,
    $apellidos,
    $fechaNac,
    $titulo,
    $email,
    $facultad,
    $id,
  ]);

  if ($result) {
    http_response_code(200);
    echo "Registro actualizado";
  } else {
    echo "Error al modificar el registro: {$mysql->error}";
  }
}

if ($_SERVER["REQUEST_METHOD"] == "POST") {
  $conn = Connection::get_instance();
  $mysql = $conn->connect();

  $id = $_POST["idC"];
  $nombres = $_POST["nombres"];
  $apellidos = $_POST["apellidos"];
  $fechaNac = $_POST["fechaNac"];
  $titulo = $_POST["titulo"];
  $email = $_POST["email"];
  $facultad = $_POST["facultad"];

  update_coordinador(
    $mysql,
    $id,
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
