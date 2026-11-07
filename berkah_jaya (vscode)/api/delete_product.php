<?php
require_once "../config/database.php";

$id = $_POST["id"] ?? "";

if (!filter_var($id, FILTER_VALIDATE_INT) || $id < 1) {
    http_response_code(400);
    echo json_encode([
        "success" => false,
        "message" => "ID tidak valid"
    ]);
    exit;
}

$stmt = $conn->prepare("DELETE FROM products WHERE id=?");
$stmt->bind_param("i", $id);
$stmt->execute();

echo json_encode([
    "success" => true,
    "message" => $stmt->affected_rows > 0
        ? "Produk berhasil dihapus"
        : "Produk tidak ditemukan"
]);
?>
