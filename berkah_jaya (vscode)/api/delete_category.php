<?php
require_once "../config/database.php";

$id = $_POST["id"] ?? "";

if (!filter_var($id, FILTER_VALIDATE_INT) || $id < 1) {
    http_response_code(400);
    echo json_encode([
        "success" => false,
        "message" => "ID kategori tidak valid"
    ]);
    exit;
}

$stmt = $conn->prepare(
    "DELETE FROM categories WHERE id = ?"
);

$stmt->bind_param("i", $id);

if ($stmt->execute()) {
    if ($stmt->affected_rows > 0) {
        echo json_encode([
            "success" => true,
            "message" => "Kategori berhasil dihapus"
        ]);
    } else {
        echo json_encode([
            "success" => false,
            "message" => "Kategori tidak ditemukan"
        ]);
    }
} else {
    http_response_code(500);
    echo json_encode([
        "success" => false,
        "message" => "Gagal menghapus kategori"
    ]);
}

$stmt->close();
?>
