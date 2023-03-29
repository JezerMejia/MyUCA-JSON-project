<?php
include_once "conexion.php";

http_response_code(500);

function delete_coordinador(mysqli $mysql, int $id) {
  http_response_code(400);
  $query = "DELETE FROM coordinador WHERE idC = ?";

  $result = $mysql->execute_query($query, [$id]);

  if ($result) {
    http_response_code(200);
    echo "Registro eliminado";
  } else {
    echo "Error al eliminar el registro: {$mysql->error}";
  }
}

if ($_SERVER["REQUEST_METHOD"] == "DELETE") {
  $conn = Connection::get_instance();
  $mysql = $conn->connect();

  $data = file_get_contents("php://input");
  $request_vars = [];
  parse_str($data, $request_vars);

  $id = $_REQUEST["idC"];

  delete_coordinador($mysql, $id);

  $conn->disconnect();
} else {
  http_response_code(400);
  echo "No se realizó una solicitud DELETE";
}
