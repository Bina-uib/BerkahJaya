<?php
require_once "../config/database.php";

$id = $_POST["id"] ?? "";
$nama = trim($_POST["nama_kategori"] ?? "");

if (!filter_var($id, FILTER_VALIDATE_INT) || $id < 1 || $nama === "") {
    http_response_code(400);
    echo json_encode([
        "success" => false,
        "message" => "Input kategori tidak valid"
    ]);
    exit;
}

$stmt = $conn->prepare(
    "UPDATE categories SET nama_kategori = ? WHERE id = ?"
);
$stmt->bind_param("si", $nama, $id);

if ($stmt->execute()) {
    echo json_encode([
        "success" => true,
        "message" => "Kategori berhasil diperbarui"
    ]);
} else {
    http_response_code(500);
    echo json_encode([
        "success" => false,
        "message" => "Gagal memperbarui kategori"
    ]);
}

$stmt->close();
?>