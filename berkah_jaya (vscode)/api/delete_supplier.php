<?php
require_once "../config/database.php";

$id = $_POST["id"] ?? "";

if (!filter_var($id, FILTER_VALIDATE_INT) || $id < 1) {
    http_response_code(400);
    echo json_encode([
        "success" => false,
        "message" => "ID supplier tidak valid"
    ]);
    exit;
}

$stmt = $conn->prepare(
    "DELETE FROM suppliers WHERE id = ?"
);

$stmt->bind_param("i", $id);

if ($stmt->execute()) {
    if ($stmt->affected_rows > 0) {
        echo json_encode([
            "success" => true,
            "message" => "Supplier berhasil dihapus"
        ]);
    } else {
        echo json_encode([
            "success" => false,
            "message" => "Supplier tidak ditemukan"
        ]);
    }
} else {
    http_response_code(500);
    echo json_encode([
        "success" => false,
        "message" => "Gagal menghapus supplier"
    ]);
}

$stmt->close();
?>
