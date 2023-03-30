<?php
include_once "conexion.php";

http_response_code(500);

function parse_coordinador(array $data): string {
  $coordinador = "{";

  $coordinador .= "\"idC\": " . $data["idC"] . ",";
  $coordinador .= "\"nombres\": \"" . $data["nombres"] . "\",";
  $coordinador .= "\"apellidos\": \"" . $data["apellidos"] . "\",";
  $coordinador .= "\"fechaNac\": \"" . $data["fechaNac"] . "\",";
  $coordinador .= "\"titulo\": \"" . $data["titulo"] . "\",";
  $coordinador .= "\"email\": \"" . $data["email"] . "\",";
  $coordinador .= "\"facultad\": \"" . $data["facultad"] . "\"";

  $coordinador .= "}";
  return $coordinador;
}

function get_coordinador_by_id(mysqli $mysql, string $id): string {
  $result = $mysql->execute_query("SELECT * FROM coordinador WHERE idC = ?", [
    $id,
  ]);

  $coordinador = $result->fetch_assoc();
  $coordinador = parse_coordinador($coordinador);
  $coordinador = trim($coordinador);

  http_response_code(200);

  $result->close();
  return $coordinador;
}

function get_all_coordinadores(mysqli $mysql): string {
  http_response_code(400);
  $result = $mysql->query("SELECT * FROM coordinador;");

  $coordinadores = "";
  if ($mysql->affected_rows > 0) {
    $coordinadores = "{\"data\": [";
    while ($row = $result->fetch_assoc()) {
      $coordinadores = $coordinadores . parse_coordinador($row);
      $coordinadores = $coordinadores . ",";
    }
    $coordinadores = rtrim($coordinadores, ",");

    $coordinadores = trim($coordinadores);
    $coordinadores = $coordinadores . "]}";
  }

  http_response_code(200);

  $result->close();
  return $coordinadores;
}

if ($_SERVER["REQUEST_METHOD"] == "GET") {
  $conn = Connection::get_instance();
  $mysql = $conn->connect();

  if (isset($_GET["idC"])) {
    $id = $_GET["idC"];
    $estudiante = get_coordinador_by_id($mysql, $id);
    echo $estudiante;
    return;
  }

  $coordinadores = get_all_coordinadores($mysql);
  echo $coordinadores;

  $conn->disconnect();
} else {
  http_response_code(400);
  echo "No se realizó una solicitud GET";
}
